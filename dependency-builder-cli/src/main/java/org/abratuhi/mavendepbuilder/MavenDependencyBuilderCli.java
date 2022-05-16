package org.abratuhi.mavendepbuilder;

import org.abratuhi.mavendepbuilder.graph.DependencyEdge;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.abratuhi.mavendepbuilder.graph.JFashT;
import org.abratuhi.mavendepbuilder.model.Project;
import org.abratuhi.mavendepbuilder.options.LayoutOptions;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alexei Bratuhin
 */
public class MavenDependencyBuilderCli {

  private static final Logger LOGGER = Logger.getLogger(MavenDependencyBuilderCli.class);

  public static void main(String[] args) throws ParseException, IOException {
    Options options = new Options();
    options.addOption("i", "input-directory", true, "Input directory to parse for maven projects");
    options.addOption("o", "output-file", true, "Output file");
		options.addOption("f", "format", true, "Output file format (gml/graphml/dot/csv)");
    options.addOption("t", "dependency-type", true, "Dependency type (project/package)");
    options.addOption("c", "check-for-violation", false, "Whether to check for violations");
    options.addOption("n", "node-layout", true, "Node layout type (none/text)");
    options.addOption("e", "edge-layout", true, "Edge layout type (none/weight/text)");
		options.addOption(null, "exclude", true, "Directories to exclude from parsing, comma-separated pattern-based, e.g. node_modules,target,out*");

    CommandLineParser parser = new PosixParser();
    CommandLine cmd = parser.parse(options, args);

    String in = null;
    String out = null;
    LayoutOptions.FormatLayout formatLayoutType = LayoutOptions.FormatLayout.GML;
    LayoutOptions.DependencyType dependencyType = LayoutOptions.DependencyType.PACKAGE;
    LayoutOptions.NodeLayout nodeLayoutType = LayoutOptions.NodeLayout.TEXT;
    LayoutOptions.EdgeLayout edgeLayoutType = LayoutOptions.EdgeLayout.WEIGHT;
    boolean checkForViolations = false;
		Set<String> excludePatterns = new HashSet<>();
    if (cmd.hasOption("i")) {
      in = cmd.getOptionValue("i");
    }
    if (cmd.hasOption("o")) {
      out = cmd.getOptionValue("o");
    }
    if (cmd.hasOption("f")) {
      formatLayoutType = LayoutOptions.FormatLayout.fromString(cmd.getOptionValue("f"));
    }
    if (cmd.hasOption("t")) {
      dependencyType = LayoutOptions.DependencyType.fromString(cmd.getOptionValue("t"));
    }
    if (cmd.hasOption("c")) {
      checkForViolations = true;
    }
    if (cmd.hasOption("n")) {
      nodeLayoutType = LayoutOptions.NodeLayout.fromString(cmd.getOptionValue("n"));
    }
    if (cmd.hasOption("e")) {
      edgeLayoutType = LayoutOptions.EdgeLayout.fromString(cmd.getOptionValue("e"));
    }
		if (cmd.hasOption("exclude")) {
			excludePatterns = Arrays.stream(cmd.getOptionValue("exclude").split(",")).collect(Collectors.toSet());
		}

    if (StringUtils.isEmpty(in) || StringUtils.isEmpty(out)) {
      new HelpFormatter().printHelp("java -jar <this_lib>", options);
      return;
    }

		MavenDependencyBuilder mdb = new MavenDependencyBuilder(excludePatterns);

    Set<Project> projects = mdb.visit(new File(in));

    DefaultDirectedWeightedGraph<? extends Graphable, DependencyEdge> dependencyGraph = mdb.buildDependencyGraph(projects, dependencyType);

		if (checkForViolations) {
			List<DependencyEdge> violations = JFashT.proceed(dependencyGraph);
			violations.forEach(violation -> LOGGER.warn(violation.toString()));
		}

    mdb.layout(
        dependencyGraph,
        Collections.emptyList(),
        new File(out),
        new LayoutOptions(formatLayoutType, nodeLayoutType, edgeLayoutType)
    );

  }
}
