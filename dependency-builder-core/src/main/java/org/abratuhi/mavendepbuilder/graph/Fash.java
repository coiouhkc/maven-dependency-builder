package org.abratuhi.mavendepbuilder.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <p>Note: FAS ~ Feedback Arc Set</p>
 * @see <a href="http://ajc.maths.uq.edu.au/pdf/12/ajc-v12-p15.pdf">http://ajc.maths.uq.edu.au/pdf/12/ajc-v12-p15.pdf</a>.
 * @author Alexei Bratuhin
 *
 *
 * TODO: implement strong components step for posible speed-up.
 */
public class Fash implements IFasAlgorithm {
	public <S, T> List<Edge<S, T>> proceed(Graph<S, T> graph) {
		List<Edge<S, T>> result = new ArrayList<>();
		Graph<S, T> graph1 = graph.copy();
		// main algorithm loop
		while (!graph1.isEmpty()) {
			// source&sink removal loop
			// while no cycles detected (at least one sink or source found) - remove thos
			while (!(graph1.sources().isEmpty() && graph1.sinks().isEmpty())) {
				// step 1
				graph1.removeAll(graph1.sources());
				// step 2
				graph1.removeAll(graph1.sinks());
			}
			// source&sink removal loop - end

			// if cycle found -> break cycle
			if (! graph1.isEmpty()) {
				// break cycle by removing the feedback (incoming) edges for a node with highest differential
				// step 3
				Node<S, T> node = graph1.getNodes().stream().max(Comparator.comparingInt(Node::outInWightDiff)).orElse(null);
				if (node != null) {
					result.addAll(node.in());
					// remove the newly created source
					// step 4
					graph1.remove(node);
				}
			}
		}
		// main algorithm loop - end
		return result;
	}
}
