package org.abratuhi.mavendepbuilder;

import lombok.Getter;
import org.abratuhi.mavendepbuilder.gml.GMLLayout;
import org.abratuhi.mavendepbuilder.graphml.GraphMLLayout;
import org.abratuhi.mavendepbuilder.model.JavaClass;
import org.abratuhi.mavendepbuilder.model.JavaPackage;
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
 * @author Alexei Bratuhin
 */
public class MavenDependencyBuilder {

	private static final Logger LOGGER = Logger.getLogger(MavenDependencyBuilder.class);

	public void buildDependencies(Set<Project> projects, File toFile, final String format) throws IOException {
		switch (format) {
		case "gml": {new GMLLayout().buildDependencies(projects, toFile); break; }
		case "graphml": {new GraphMLLayout().buildDependencies(projects, toFile); break;}
		default: throw new IllegalArgumentException("Unsupported format: " + format);
		}
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

							JavaPackage pakkage = project.getPackageByName(clazz.getPakkage()).orElse(new JavaPackage(clazz.getPakkage()));
							pakkage.getClasses().add(clazz);
							project.getPackages().add(pakkage);
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
				clazz.setPakkage(sPackage);
				clazz.setName(sPackage + "." + sName);
			}

			if (line.startsWith("import")) {
				String sClass = line.replaceAll("import", "").replaceAll(";", "").trim();
				clazz.getImports().add(sClass);
			}
		});

		return clazz;
	}

	public static void main (String [] args) throws ParseException, IOException {
		Options options = new Options();
		options.addOption("i", "inputDirectory", true, "Input directory to parse for maven projects");
		options.addOption("o", "outputFile", true, "Output file in gml format");
		options.addOption("f", "format", true, "gml/graphml");

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);

		String in = null;
		String out = null;
		String format = null;
		if (cmd.hasOption("i")) {
			in = cmd.getOptionValue("i");
		}
		if (cmd.hasOption("o")) {
			out = cmd.getOptionValue("o");
		}
		if (cmd.hasOption("f")) {
			format = cmd.getOptionValue("f");
		}

		if (StringUtils.isEmpty(in) || StringUtils.isEmpty(out)) {
			new HelpFormatter().printHelp("java -jar <this_lib>", options);
			return;
		}

		MavenDependencyBuilder mavenDependencyBuilder = new MavenDependencyBuilder();
		Set<Project> projects = mavenDependencyBuilder.visitDirectory(new File(in));
		mavenDependencyBuilder.buildDependencies(projects, new File(out), format);
	}
}
