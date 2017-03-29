package org.abratuhi.mavendepbuilder.graph;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexei Bratuhin
 */
public class GraphTest {
	@Test
	public void testSinksInTwoNodeLoop() {
		Node<String, String> n1 = new Node<>(null, new ArrayList<>());
		Node<String, String> n2 = new Node<>(null, new ArrayList<>());
		Edge<String, String> e1 = new Edge<>(null, n1, n2, 1);
		Edge<String, String> e2 = new Edge<>(null, n2, n1, 1);
		n1.setEdges(Arrays.asList(e1, e2));
		n2.setEdges(Arrays.asList(e1, e2));
		Graph<String, String> g = new Graph<>(Arrays.asList(n1, n2));
		assertTrue("Two node loop has no sinks", CollectionUtils.isEmpty(g.sinks()));
	}
	@Test
	public void testSourcesInTwoNodeLoop() {
		Node<String, String> n1 = new Node<>(null, new ArrayList<>());
		Node<String, String> n2 = new Node<>(null, new ArrayList<>());
		Edge<String, String> e1 = new Edge<>(null, n1, n2, 1);
		Edge<String, String> e2 = new Edge<>(null, n2, n1, 1);
		n1.setEdges(Arrays.asList(e1, e2));
		n2.setEdges(Arrays.asList(e1, e2));
		Graph<String, String> g = new Graph<>(Arrays.asList(n1, n2));
		assertTrue("Two node loop has no sources", CollectionUtils.isEmpty(g.sources()));
	}
	@Test
	public void testRemoveAllEmptyList() {
		Node<String, String> n1 = new Node<>(null, new ArrayList<>());
		Node<String, String> n2 = new Node<>(null, new ArrayList<>());
		Edge<String, String> e1 = new Edge<>(null, n1, n2, 1);
		Edge<String, String> e2 = new Edge<>(null, n2, n1, 1);
		n1.setEdges(Arrays.asList(e1, e2));
		n2.setEdges(Arrays.asList(e1, e2));
		Graph<String, String> g = new Graph<>(Arrays.asList(n1, n2));
		assertEquals("RemoveAll emptyList may not affect anything", 2, g.getNodes().size());
	}
}
