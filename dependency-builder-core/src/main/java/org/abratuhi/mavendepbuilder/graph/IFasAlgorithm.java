package org.abratuhi.mavendepbuilder.graph;

import java.util.List;

/**
 * @author Alexei Bratuhin
 */
public interface IFasAlgorithm {
	<S, T> List<Edge<S, T>> proceed(Graph<S, T> graph);
}
