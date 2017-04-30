package org.abratuhi.mavendepbuilder.layout.dot;

import org.abratuhi.mavendepbuilder.graph.Graph;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.abratuhi.mavendepbuilder.layout.ILayout;
import org.abratuhi.mavendepbuilder.layout.LayoutUtil;
import org.abratuhi.mavendepbuilder.options.LayoutOptions;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Alexei Bratuhin
 */
public class DotLayout implements ILayout {
	@Override public <S extends Graphable, T> void doLayout(Graph<S, T> graph, File toFile, LayoutOptions layoutOptions)
			throws IOException {
		StringBuffer sb = new StringBuffer();

		sb.append("digraph graph {");

		graph.getNodes().stream().forEach(node ->
			sb.append(node.getObject().getId() + " [label = \"" + LayoutUtil.getNodeLabel(node, layoutOptions.getNodeLayout()) + "\" ]; \n")
		);

		graph.getNodes().stream().map(node -> node.out()).flatMap(s -> s.stream()).forEach(edge ->
				sb.append(edge.getFrom().getObject().getId() + " -> " + edge.getTo().getObject().getId() + " [label = \"" + LayoutUtil.getEdgeLabel(edge, layoutOptions.getEdgeLayout()) + "\"]; \n")
		);

		sb.append("}");

		// write result to file
		FileUtils.writeStringToFile(toFile, sb.toString());
	}
}
