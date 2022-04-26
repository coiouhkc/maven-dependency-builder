package org.abratuhi.mavendepbuilder.layout;

import org.abratuhi.mavendepbuilder.options.LayoutOptions;
import org.abratuhi.mavendepbuilder.graph.Graph;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.IOException;

/**
 * @author Alexei Bratuhin
 */
public interface ILayout {
	<S extends Graphable, T> void doLayout(DefaultDirectedGraph<S, DefaultEdge> graph, File toFile, LayoutOptions layoutOptions) throws
			IOException;
}
