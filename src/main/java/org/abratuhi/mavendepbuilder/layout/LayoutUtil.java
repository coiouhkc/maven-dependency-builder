package org.abratuhi.mavendepbuilder.layout;

import org.abratuhi.mavendepbuilder.options.LayoutOptions;
import org.abratuhi.mavendepbuilder.graph.Edge;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.abratuhi.mavendepbuilder.graph.Node;

/**
 * @author Alexei Bratuhin
 */
public class LayoutUtil {
	public static <S extends Graphable, T> String getNodeLabel(Node<S, T> node, LayoutOptions.NodeLayout nodeLayout) {
		switch(nodeLayout){
		case NONE: return "";
		case TEXT: return node.getObject().getLabel();
		default: return null;
		}
	}

	public static <S extends Graphable, T> String getEdgeLabel(Edge<S, T> edge, LayoutOptions.EdgeLayout edgeLayout) {
		switch(edgeLayout){
		case NONE: return "";
		case WEIGHT: return  edge.getWeight().toString();
		case TEXT: return edge.getObject().toString();
		default: return null;
		}
	}
}
