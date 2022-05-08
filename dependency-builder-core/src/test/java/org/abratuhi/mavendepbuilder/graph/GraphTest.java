package org.abratuhi.mavendepbuilder.graph;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Alexei Bratuhin
 */
public class GraphTest {
	@Test
	void testSinksInTwoNodeLoop() {
		Node<String, String> n1 = new Node<>(null, new ArrayList<>());
		Node<String, String> n2 = new Node<>(null, new ArrayList<>());
		Edge<String, String> e1 = new Edge<>(null, n1, n2, 1);
		Edge<String, String> e2 = new Edge<>(null, n2, n1, 1);
		n1.setEdges(Arrays.asList(e1, e2));
		n2.setEdges(Arrays.asList(e1, e2));
		Graph<String, String> g = new Graph<>(Arrays.asList(n1, n2));
		assertTrue(CollectionUtils.isEmpty(g.sinks()), "Two node loop has no sinks");
	}
	@Test
	void testSourcesInTwoNodeLoop() {
		Node<String, String> n1 = new Node<>(null, new ArrayList<>());
		Node<String, String> n2 = new Node<>(null, new ArrayList<>());
		Edge<String, String> e1 = new Edge<>(null, n1, n2, 1);
		Edge<String, String> e2 = new Edge<>(null, n2, n1, 1);
		n1.setEdges(Arrays.asList(e1, e2));
		n2.setEdges(Arrays.asList(e1, e2));
		Graph<String, String> g = new Graph<>(Arrays.asList(n1, n2));
		assertTrue(CollectionUtils.isEmpty(g.sources()), "Two node loop has no sources");
	}
	@Test
	void testRemoveAllEmptyList() {
		Node<String, String> n1 = new Node<>(null, new ArrayList<>());
		Node<String, String> n2 = new Node<>(null, new ArrayList<>());
		Edge<String, String> e1 = new Edge<>(null, n1, n2, 1);
		Edge<String, String> e2 = new Edge<>(null, n2, n1, 1);
		n1.setEdges(Arrays.asList(e1, e2));
		n2.setEdges(Arrays.asList(e1, e2));
		Graph<String, String> g = new Graph<>(Arrays.asList(n1, n2));
		g.removeAll(new ArrayList<>());
		assertEquals(2, g.getNodes().size(), "RemoveAll emptyList may not affect anything");
	}
	@Test
	void testRemoveOne() {
		Node<String, String> n1 = new Node<>("1", new ArrayList<>());
		Node<String, String> n2 = new Node<>("2", new ArrayList<>());
		Node<String, String> n3 = new Node<>("3", new ArrayList<>());
		Graph<String, String> g = new Graph<>(new ArrayList<>(Arrays.asList(n1, n2, n3)));
		g.addEdge("1 -> 2", n1, n2, 1);
		g.addEdge("2 -> 1", n2, n1, 1);
		g.addEdge("3 -> 1", n3, n1, 1);
		g.remove(n1);
		assertEquals(2, g.getNodes().size(), "Remove should remove node");
		assertEquals(n2.getEdges().size(), 0, "Remove node should imply removing all from/to edges");
		assertEquals(0, n3.getEdges().size(), "Remove node should imply removing all from/to edges");
	}
}
