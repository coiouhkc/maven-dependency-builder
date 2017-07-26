package org.abratuhi.mavendepbuilder.layout.gml;

import org.abratuhi.mavendepbuilder.graph.Edge;
import org.abratuhi.mavendepbuilder.options.LayoutOptions;
import org.abratuhi.mavendepbuilder.graph.Graph;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.abratuhi.mavendepbuilder.layout.ILayout;
import org.abratuhi.mavendepbuilder.layout.LayoutUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @see <a href="http://www.fim.uni-passau.de/fileadmin/files/lehrstuhl/brandenburg/projekte/gml/gml-technical-report.pdf">http://www.fim.uni-passau.de/fileadmin/files/lehrstuhl/brandenburg/projekte/gml/gml-technical-report.pdf</a>
 * @author Alexei Bratuhin
 */
public class GMLLayout implements ILayout {

	public <S extends Graphable, T> void doLayout(Graph<S, T> graph, List<Edge> violations, File toFile, LayoutOptions layoutOptions) throws IOException {
		// build directed graph in gml notation (using wikipedia example as reference)
		StringBuilder sb = new StringBuilder();
		sb.append("graph [ \n");
		sb.append("directed 1 \n");
		graph.getNodes().forEach(node -> {
			sb.append("node [ \n");
			sb.append("id " + node.getObject().getId() + " \n");
			sb.append("label \"" + LayoutUtil.getNodeLabel(node, layoutOptions.getNodeLayout()) + "\" \n");
			sb.append("] \n");
		});

		graph.edges().forEach(edge -> {
			sb.append("edge [ \n");
			sb.append("source " + edge.getFrom().getObject().getId() + " \n");
			sb.append("target " + edge.getTo().getObject().getId() + " \n");
			sb.append("label \"");
			sb.append(LayoutUtil.getEdgeLabel(edge, layoutOptions.getEdgeLayout()) + " ");
			sb.append("\" \n");
			sb.append("] \n");
		});
		sb.append("] \n");

		// write result to file
		FileUtils.writeStringToFile(toFile, sb.toString());
	}


}
