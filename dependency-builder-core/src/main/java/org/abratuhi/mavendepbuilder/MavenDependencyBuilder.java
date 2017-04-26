package org.abratuhi.mavendepbuilder;

import org.abratuhi.mavendepbuilder.graph.Graph;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.abratuhi.mavendepbuilder.graph.Node;
import org.abratuhi.mavendepbuilder.layout.gml.GMLLayout;
import org.abratuhi.mavendepbuilder.layout.graphml.GraphMLLayout;
import org.abratuhi.mavendepbuilder.model.JavaClass;
import org.abratuhi.mavendepbuilder.model.JavaPackage;
import org.abratuhi.mavendepbuilder.model.Project;
import org.abratuhi.mavendepbuilder.options.LayoutOptions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Alexei Bratuhin
 */
public class MavenDependencyBuilder {

	private static final Logger LOGGER = Logger.getLogger(MavenDependencyBuilder.class);

	public <S extends Graphable,T> void layout(Graph<S, T> dependencyGraph, File toFile, LayoutOptions layoutOptions) throws IOException {
		switch (layoutOptions.getFormatLayout()) {
		case GML: {new GMLLayout().doLayout(dependencyGraph, toFile, layoutOptions); break; }
		case GRAPHML: {new GraphMLLayout().doLayout(dependencyGraph, toFile, layoutOptions); break; }
		default: throw new IllegalArgumentException("Unsupported format: " + layoutOptions.getFormatLayout());
		}
	}

	private Project get(Set<Project> projects, String className) {
		return projects.stream()
				.filter(project ->
						project.getClasses().stream()
								.map(JavaClass::getName)
								.collect(Collectors.toList())
								.contains(className))
				.findFirst()
				.orElse(null);
	}

	private JavaPackage get(List<JavaPackage> packages, String className) {
		return packages.stream()
				.filter(project ->
						project.getClasses().stream()
								.map(JavaClass::getName)
								.collect(Collectors.toList())
								.contains(className))
				.findFirst()
				.orElse(null);
	}

	/* default */ <S extends Graphable,T> Graph<S, T> buildDependencyGraph(Set<Project> projects, LayoutOptions.DependencyType dependencyType) {
		switch (dependencyType) {
		case PROJECT: return (Graph<S, T>) buildProjectDependencyGraph(projects);
		case PACKAGE: return (Graph<S, T>) buildPackageDependencyGraph(projects);
		default: throw new IllegalArgumentException("Unsupported dependency type: " + dependencyType);
		}
	}

	/* default */ Graph<Project, String> buildProjectDependencyGraph(Set<Project> projects) {
		Map<Project, Map<Project, Set<String>>> fromToClasses = new HashMap<>();
		projects.stream().forEach(from ->
				from.getClasses().stream().map(JavaClass::getImports).flatMap(Collection::stream).forEach(importedJavaClass -> {
						Project to = get(projects, importedJavaClass);
						if (to != null) {
							fromToClasses.putIfAbsent(from, new HashMap<>());
							fromToClasses.get(from).putIfAbsent(to, new HashSet<>());
							fromToClasses.get(from).get(to).add(importedJavaClass);
						}
				})
		);

		Graph<Project, String> result = new Graph<>();
		projects.stream().forEach(project -> result.getNodes().add(new Node<>(project, new ArrayList<>())));
		fromToClasses.entrySet().stream().forEach(fromEntry ->
				fromEntry.getValue().entrySet().stream().forEach(toEntry -> {
					Node<Project, String> from = result.getByNodeObject(fromEntry.getKey());
					Node<Project, String> to = result.getByNodeObject(toEntry.getKey());
					if (!from.equals(to)) {
						result.addEdge(StringUtils
								.join(toEntry.getValue().stream().collect(Collectors.toList()),
										","), from, to, toEntry.getValue().size());
					}
				})
		);
		return result;
	}

	/* default */ Graph<JavaPackage, String> buildPackageDependencyGraph(Set<Project> projects) {
		List<JavaPackage> packages = projects.stream().map(Project::getPackages).flatMap(Collection::stream).collect(
				Collectors.toList());
		Map<JavaPackage, Map<JavaPackage, Set<String>>> fromToClasses = new HashMap<>();
		packages.stream().forEach(from ->
				from.getClasses().stream().map(JavaClass::getImports).flatMap(Collection::stream).forEach(importedJavaClass -> {
					JavaPackage to = get(packages, importedJavaClass);
					if (to != null) {
						fromToClasses.putIfAbsent(from, new HashMap<>());
						fromToClasses.get(from).putIfAbsent(to, new HashSet<>());
						fromToClasses.get(from).get(to).add(importedJavaClass);
					}
				})
		);

		Graph<JavaPackage, String> result = new Graph<>();
		packages.stream().forEach(javaPackage -> result.getNodes().add(new Node<>(javaPackage, new ArrayList<>())));
		fromToClasses.entrySet().stream().forEach(fromEntry ->
				fromEntry.getValue().entrySet().stream().forEach(toEntry -> {
					Node<JavaPackage, String> from = result.getByNodeObject(fromEntry.getKey());
					Node<JavaPackage, String> to = result.getByNodeObject(toEntry.getKey());
					if (!from.equals(to)) {
						result.addEdge(StringUtils
								.join(toEntry.getValue().stream().collect(Collectors.toList()),
										","), from, to, toEntry.getValue().size());
					}
				})
		);
		return result;
	}

	/**
	 * Parse directory recursively, searching for projects and source classes.
	 * @param dir	initial traversal directory
	 * @return	set of found maven projects
	 * @throws IOException
	 */
	public Set<Project> visit(File dir) throws IOException {
		Set<Project> result = visitDirectory(dir);
		AtomicInteger idIndex = new AtomicInteger(0);
		result.stream().forEach(project -> project.setId(idIndex.getAndIncrement()));
		result.stream().map(Project::getPackages).flatMap(Collection::stream).forEach(javaPackage -> javaPackage.setId(idIndex.getAndIncrement()));
		result.stream().map(Project::getClasses).flatMap(Collection::stream).forEach(javaClass -> javaClass.setId(idIndex.getAndIncrement()));
		return result;
	}

	/**
	 * Visit directory (recursively), use directory name as new project, iff it has pom.xml and a src directory containing at least one .java file.
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	/* default */ Set<Project> visitDirectory(File dir) throws IOException {
		final Set<Project> result = new HashSet<>();
		if (!dir.isDirectory()) {
			return result;
		}

		final Project project = new Project();

		if (new File(dir, "pom.xml").exists()) {
			project.setName(dir.getName());
		}

		if (new File(dir, "src").exists()) {
			Files.walk(new File(dir, "src").toPath())
					.filter(path -> path.getFileName().toString().endsWith(".java"))
					.forEach(path -> {
						try {
							JavaClass javaClass = visitJavaClass(path.toFile());
							JavaPackage javaPackage = project.getOrAdd(javaClass.getPakkage());
							javaPackage.getClasses().add(javaClass);
						} catch (IOException e) {
							LOGGER.error(e);
						}
					});
		}

		if (StringUtils.isNotEmpty(project.getName())) {
			result.add(project);
		}

		Files.list(dir.toPath()).forEach(path -> {
			try {
				result.addAll(visitDirectory(path.toFile()));
			} catch (IOException e) {
				LOGGER.error(e);
			}
		});

		return result;
	}

	/**
	 * Visit java class, skip all lines not starting with <code>package</code> or <code>import</code>, interpret those lines as className and importedClasses correspondingly.
	 * @param javaClass
	 * @return
	 * @throws IOException
	 */
	/* default */ JavaClass visitJavaClass(File javaClass) throws IOException {
		if (!javaClass.exists() || !javaClass.isFile()) {
			return null;
		}

		final JavaClass clazz = new JavaClass();

		String[] javaClassLines = FileUtils.readFileToString(javaClass).split("\n");
		Arrays.stream(javaClassLines).forEach(line -> {
			if (line.startsWith("package")) {
				String sPackage = line.replaceAll("package", "").replaceAll(";", "").trim();
				String sName = javaClass.getName().replaceAll("\\.java", "");
				clazz.setPakkage(sPackage);
				clazz.setName(sPackage + "." + sName);
			}

			if (line.startsWith("import")) {
				String sClass = line.replaceAll("import", "").replaceAll("static", "").replaceAll(";", "").trim();
				clazz.getImports().add(sClass);
			}
		});

		return clazz;
	}
}
