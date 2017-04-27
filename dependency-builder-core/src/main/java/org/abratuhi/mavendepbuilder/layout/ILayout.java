package org.abratuhi.mavendepbuilder.layout;

import org.abratuhi.mavendepbuilder.options.LayoutOptions;
import org.abratuhi.mavendepbuilder.graph.Graph;
import org.abratuhi.mavendepbuilder.graph.Graphable;

import java.io.File;
import java.io.IOException;

/**
 * @author Alexei Bratuhin
 */
public interface ILayout {
	<S extends Graphable, T> void doLayout(Graph<S, T> graph, File toFile, LayoutOptions layoutOptions) throws
			IOException;
}
