package org.abratuhi.mavendepbuilder;

import org.abratuhi.mavendepbuilder.graph.Edge;
import org.abratuhi.mavendepbuilder.graph.Graph;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.abratuhi.mavendepbuilder.graph.IFasAlgorithm;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alexei Bratuhin
 */
public class DependencyTestUtil {

	/*default*/ static <S extends Graphable, T> void testViolations(IFasAlgorithm algorithm, Graph<S, T> graph) {
		testViolations(algorithm, graph, 0);
	}

	/*default*/ static <S extends Graphable, T> void testViolations(IFasAlgorithm algorithm, Graph<S, T> graph, int expectedViolations) {
		List<Edge<S, T>> violations = algorithm.proceed(graph);
		if (violations.size() > 0) {
			String allEdges = edgesToString(graph.edges());
			String allViolations = edgesToString(violations);
			assertEquals(expectedViolations, violations.size(), "All:\n" + allEdges + "\n" + "Violations:\n" + allViolations + "\n");
		}
	}

	/*default*/ static <S extends Graphable, T> String edgesToString(List<Edge<S, T>> violations) {
		return StringUtils
				.join(violations.stream().map(edge -> edge.getFrom().getObject().getLabel() + " -> " + edge.getTo().getObject().getLabel() + " | " + edge.getObject().toString()).collect(
						Collectors.toList()), "\n");
	}
}
