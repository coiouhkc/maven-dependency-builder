package org.abratuhi.mavendepbuilder.layout.dot;

import org.abratuhi.mavendepbuilder.graph.Graph;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.abratuhi.mavendepbuilder.layout.ILayout;
import org.abratuhi.mavendepbuilder.layout.LayoutUtil;
import org.abratuhi.mavendepbuilder.options.LayoutOptions;
import org.apache.commons.io.FileUtils;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.IOException;

/**
 * @author Alexei Bratuhin
 */
public class DotLayout implements ILayout {
	@Override public <S extends Graphable, T> void doLayout(DefaultDirectedGraph<S, DefaultEdge> graph, File toFile, LayoutOptions layoutOptions)
			throws IOException {
		StringBuffer sb = new StringBuffer();

		sb.append("digraph graph {");

		graph.vertexSet().stream().forEach(node ->
			sb.append(node.getLabel() + " [label = \"" + LayoutUtil.getNodeLabel(node.getLabel(), layoutOptions.getNodeLayout()) + "\" ]; \n")
		);

		graph.edgeSet().forEach(edge ->
				sb.append(graph.getEdgeSource(edge) + " -> " + graph.getEdgeTarget(edge) + " [label = \"" + LayoutUtil.getEdgeLabel("", layoutOptions.getEdgeLayout()) + "\"]; \n")
		);

		sb.append("}");

		// write result to file
		FileUtils.writeStringToFile(toFile, sb.toString());
	}
}
