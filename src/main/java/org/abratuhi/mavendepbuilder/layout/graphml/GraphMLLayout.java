package org.abratuhi.mavendepbuilder.layout.graphml;

import org.abratuhi.mavendepbuilder.LayoutOptions;
import org.abratuhi.mavendepbuilder.graph.Graph;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.abratuhi.mavendepbuilder.layout.ILayout;
import org.abratuhi.mavendepbuilder.layout.LayoutUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Alexei Bratuhin
 */
public class GraphMLLayout implements ILayout{

	public <S extends Graphable, T> void doLayout(Graph<S, T> graph, File toFile, LayoutOptions layoutOptions) throws IOException {
		// build directed graph in graphml notation with yEd flavour
		StringBuffer sb = new StringBuffer();
		sb.append(""
				+ "<graphml\n" + " xmlns=\"http://graphml.graphdrawing.org/xmlns\"\n"
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
				+ " xmlns:y=\"http://www.yworks.com/xml/graphml\"\n"
				+ " xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd\">"
				+ "  <key for=\"node\" id=\"d0\" yfiles.type=\"nodegraphics\"/>"
				+ "  <key for=\"edge\" id=\"d10\" yfiles.type=\"edgegraphics\"/>"
				+ "  <graph id=\"G\" edgedefault=\"directed\">\n" );
		graph.getNodes().stream().forEach(node -> {
			sb.append("<node id=\"" + node.getObject().getId() + "\">\n");
			sb.append(""
					+ "<data key=\"d0\">\n"
					+ "        <y:ShapeNode>\n"
					+ "          <y:NodeLabel>\"" + LayoutUtil.getNodeLabel(node, layoutOptions.getNodeLayout()) + "\"</y:NodeLabel>\n"
					+ "        </y:ShapeNode>\n"
					+ "      </data>\n");
			sb.append("</node>\n");
		});


		graph.edges().forEach(edge -> {
			sb.append("<edge id=\"" + edge.getFrom().getObject().getId() + "_" + edge.getTo().getObject().getId() + "\" source=\"" + edge.getFrom().getObject().getId() + "\" target=\"" + edge.getTo().getObject().getId() + "\">\n");
			sb.append("<data key=\"d10\">\n"
					+ "        <y:PolyLineEdge>\n"
					+ "          <y:Path sx=\"0.0\" sy=\"0.0\" tx=\"0.0\" ty=\"0.0\"/>\n"
					+ "          <y:LineStyle color=\"#000000\" type=\"line\" width=\"1.0\"/>\n"
					+ "          <y:Arrows source=\"none\" target=\"standard\"/>\n"
					+ "          <y:EdgeLabel>" + LayoutUtil.getEdgeLabel(edge, layoutOptions.getEdgeLayout()) + "</y:EdgeLabel>\n"
					+ "          <y:BendStyle smoothed=\"false\"/>\n"
					+ "        </y:PolyLineEdge>\n" + "      </data>");
			sb.append("</edge>");
		});



		sb.append("</graph>\n"
				+ "</graphml>"
				+ "");

		// write result to file
		FileUtils.writeStringToFile(toFile, sb.toString());
	}
}
