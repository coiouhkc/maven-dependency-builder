package org.abratuhi.mavendepbuilder.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @see {@url http://ajc.maths.uq.edu.au/pdf/12/ajc-v12-p15.pdf}.
 * @author Alexei Bratuhin
 *
 *
 * TODO: implement strong components step for posible speed-up.
 */
public class AlgorithmFash {
	public <S, T> List<Edge<S, T>> proceed(Graph<S, T> graph) {
		List<Edge<S, T>> result = new ArrayList<>();
		Graph<S, T> graph1 = graph; // TODO: do not mutate initial graph
		while (!graph1.isEmpty()) {
			graph1 = graph1.removeAll(graph1.sources());
			graph1 = graph1.removeAll(graph1.sinks());
			if (! graph1.isEmpty()) {
				Node<S, T> node = graph1.getNodes().stream().max(Comparator.comparingInt(Node::outInWightDiff)).orElse(null);
				if (node != null) {
					result.addAll(node.in());
					graph1.remove(node);
				}
			}
		}
		return result;
	}
}
