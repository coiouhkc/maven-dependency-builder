package org.abratuhi.mavendepbuilder;

import org.abratuhi.mavendepbuilder.graph.Edge;
import org.abratuhi.mavendepbuilder.graph.Fash;
import org.abratuhi.mavendepbuilder.graph.Graph;
import org.abratuhi.mavendepbuilder.model.Project;
import org.abratuhi.mavendepbuilder.options.LayoutOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Alexei Bratuhin
 */
public class MavenDependencyBuilderCli {

	private static final Logger LOGGER = Logger.getLogger(MavenDependencyBuilderCli.class);

	public static void main (String [] args) throws ParseException, IOException {
		Options options = new Options();
		options.addOption("i", "input-directory", true, "Input directory to parse for maven projects");
		options.addOption("o", "output-file", true, "Output file");
		options.addOption("f", "format", true, "Output file format (gml/graphml/dot/csv)");
		options.addOption("t", "dependency-type", true, "Dependency type (project/package)");
		options.addOption("c", "check-for-violation", false, "Whether to check for violations");
		options.addOption("n", "node-layout", true, "Node layout type (none/text)");
		options.addOption("e", "edge-layout", true, "Edge layout type (none/weight/text)");

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);

		String in = null;
		String out = null;
		LayoutOptions.FormatLayout formatLayoutType = LayoutOptions.FormatLayout.GML;
		LayoutOptions.DependencyType dependencyType = LayoutOptions.DependencyType.PACKAGE;
		LayoutOptions.NodeLayout nodeLayoutType = LayoutOptions.NodeLayout.TEXT;
		LayoutOptions.EdgeLayout edgeLayoutType = LayoutOptions.EdgeLayout.WEIGHT;
		boolean checkForViolations = false;
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

		if (StringUtils.isEmpty(in) || StringUtils.isEmpty(out)) {
			new HelpFormatter().printHelp("java -jar <this_lib>", options);
			return;
		}

		MavenDependencyBuilder mdb = new MavenDependencyBuilder();

		Set<Project> projects = mdb.visit(new File(in));

		Graph dependencyGraph = mdb.buildDependencyGraph(projects, dependencyType);
		
		List<Edge> violations = new ArrayList<>();
		if (checkForViolations) {
			violations = new Fash().proceed(dependencyGraph);
		}

		mdb.layout(dependencyGraph, violations, new File(out), new LayoutOptions(formatLayoutType, nodeLayoutType, edgeLayoutType));
	}
}
