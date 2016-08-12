package org.abratuhi.mavendepbuilder;

import lombok.Getter;
import org.abratuhi.mavendepbuilder.model.JavaClass;
import org.abratuhi.mavendepbuilder.model.Project;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by bratuhia on 04.08.2016.
 */
public class MavenDependencyBuilder {

	private final static Logger LOGGER = Logger.getLogger(MavenDependencyBuilder.class);

	@Getter final Map<String, Project> classProjectMap = new HashMap<>();
	@Getter final Map<Project, Map<Project, Set<String>>> fromToDepclassesMap = new HashMap<>();

	public void buildDependencies(Set<Project> projects) throws IOException {
		buildDependencies(projects, new File("dependencies.gml"));
	}

	public void buildDependencies(Set<Project> projects, File toFile) throws IOException {
		// set numerical ids
		Iterator<Project> iterator = projects.iterator();
		for (int i = 1; iterator.hasNext(); i++) {
			Project project = iterator.next();
			project.setId(i);
		}

		// fill map className -> project
		projects.parallelStream()
				.forEach(project -> project.getClasses()
						.forEach(javaClass -> classProjectMap.put(javaClass.getName(), project)));

		// fill multimap <fromProject, toProject> -> importedClases
		projects.stream()
				.forEach(from -> {
					fromToDepclassesMap.put(from, new HashMap<>());
					from.getClasses()
						.forEach(javaClass -> javaClass.getImports()
								.forEach(importClass -> {
									Project to = classProjectMap.get(importClass);
									if (to != null) {
										if (!fromToDepclassesMap.get(from).containsKey(to)) {
											fromToDepclassesMap.get(from).put(to, new TreeSet<>());
										}
										fromToDepclassesMap.get(from).get(to).add(importClass);
									}
								}));
				});

		// build directed graph in gml notation (using wikipedia example as reference)
		StringBuilder sb = new StringBuilder();
		sb.append("graph [ \n");
		sb.append("directed 1 \n");
		projects.forEach(project -> {
			sb.append("node [ \n");
			sb.append("id " + project.getId() + " \n");
			sb.append("label \"" + project.getName() + "\" \n");
			sb.append("] \n");
		});
		fromToDepclassesMap.keySet().forEach(from -> {
			fromToDepclassesMap.get(from).keySet().forEach(to -> {
				if (! from.getName().equals(to.getName())) {
					sb.append("edge [ \n");
					sb.append("source " + from.getId() + " \n");
					sb.append("target " + to.getId() + " \n");
					sb.append("label \"");
						fromToDepclassesMap.get(from).get(to).forEach(classImport -> {
							sb.append(classImport + " ");
						});
					sb.append("\" \n");
					sb.append("] \n");
				}
			});
		});
		sb.append("] \n");

		// write result to file
		FileUtils.writeStringToFile(toFile, sb.toString());
	}

	/**
	 * Visit directory (recursively), use directory name as new project, iff it has pom.xml and a src directory containing at least one .java file.
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	public Set<Project> visitDirectory(File dir) throws IOException {
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
							JavaClass clazz = visitJavaClass(path.toFile());
							project.getClasses().add(clazz);
						} catch (IOException e) {
							LOGGER.error(e);
						}
					});
		}

		if (StringUtils.isNotEmpty(project.getName())) {
			result.add(project);
		}

		Files.list(dir.toPath())
				.forEach(path -> {
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
	public JavaClass visitJavaClass(File javaClass) throws IOException {
		if (!javaClass.exists() || !javaClass.isFile()) {
			return null;
		}

		final JavaClass clazz = new JavaClass();

		String[] javaClassLines = FileUtils.readFileToString(javaClass).split("\n");
		Arrays.stream(javaClassLines).forEach(line -> {
			if (line.startsWith("package")) {
				String sPackage = line.replaceAll("package", "").replaceAll(";", "").trim();
				String sName = javaClass.getName().replaceAll("\\.java", "");
				clazz.setName(sPackage + "." + sName);
			}

			if (line.startsWith("import")) {
				String sClass = line.replaceAll("import", "").replaceAll(";", "").trim();
				clazz.getImports().add(sClass);
			}
		});

		return clazz;
	}

	public int getNumberOfDependenciesBetweenProjects() {
		return fromToDepclassesMap.values().stream()
				.map(map -> map.values())
				.flatMap(set -> set.stream())
				.mapToInt(set -> set.size())
				.sum();
	}


	public static void main (String [] args) throws ParseException, IOException {
		Options options = new Options();
		options.addOption("i", "inputDirectory", true, "Input directory to parse for maven projects");
		options.addOption("o", "outputFile", true, "Output file in gml format");

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);

		String in = null;
		String out = null;
		if (cmd.hasOption("i")) {
			in = cmd.getOptionValue("i");
		}
		if (cmd.hasOption("o")) {
			out = cmd.getOptionValue("o");
		}

		if (StringUtils.isEmpty(in) || StringUtils.isEmpty(out)) {
			new HelpFormatter().printHelp("java -jar <this_lib>", options);
			return;
		}

		MavenDependencyBuilder mavenDependencyBuilder = new MavenDependencyBuilder();
		Set<Project> projects = mavenDependencyBuilder.visitDirectory(new File(in));
		mavenDependencyBuilder.buildDependencies(projects, new File(out));
	}
}
