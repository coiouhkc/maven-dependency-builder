package org.abratuhi.mavendepbuilder.graph;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexei Bratuhin
 */
public class FashTest {

	private Fash fash = new Fash();

	@Test
	public void testEmpty() {
		Graph<String, String> g = new Graph<>();
		List<Edge<String, String>> actual = fash.proceed(g);
		assertNotNull(actual);
		assertTrue("Feedback arc set of an empty graph must be empty", actual.isEmpty());
	}

	@Test
	public void testOneNode() {
		Graph<String, String> g = new Graph<>(new ArrayList<>(Collections.singletonList(new Node<String, String>(null, new ArrayList<>()))));
		List<Edge<String, String>> actual = fash.proceed(g);
		assertNotNull(actual);
		assertTrue("Feedback arc set of an empty graph must be empty", actual.isEmpty());
	}

	@Test
	public void testOneNodeLoop() {
		Node<String, String> n1 = new Node<>(null, new ArrayList<>());
		Edge<String, String> e1 = new Edge<>(null, n1, n1, 1);
		n1.setEdges(Collections.singletonList(e1));

		Graph<String, String> g = new Graph<>(new ArrayList<>(Collections.singletonList(n1)));
		List<Edge<String, String>> actual = fash.proceed(g);
		assertNotNull(actual);
		assertEquals("Feedback arc set of an one node one arc loop must contain that arc", 1, actual.size());
	}

	@Test
	public void testTwoNodeLoop() {
		Node<String, String> n1 = new Node<>(null, new ArrayList<>());
		Node<String, String> n2 = new Node<>(null, new ArrayList<>());
		Edge<String, String> e1 = new Edge<>(null, n1, n2, 1);
		Edge<String, String> e2 = new Edge<>(null, n2, n1, 2);
		n1.setEdges(new ArrayList<>(Arrays.asList(e1, e2)));
		n2.setEdges(new ArrayList<>(Arrays.asList(e1, e2)));

		Graph<String, String> g = new Graph<>(new ArrayList<>(Arrays.asList(n1, n2)));
		List<Edge<String, String>> actual = fash.proceed(g);
		assertNotNull(actual);
		assertEquals("Feedback arc set of an two node loop must contain one arc", 1, actual.size());
		assertEquals("Feedback arc set must contain the arc with the least weight", e1, actual.get(0));
	}

	@Test
	public void testFourNodeWithLoops() {
		Node<String, String> n1 = new Node<>(null, new ArrayList<>());
		Node<String, String> n2 = new Node<>(null, new ArrayList<>());
		Node<String, String> n3 = new Node<>(null, new ArrayList<>());
		Node<String, String> n4 = new Node<>(null, new ArrayList<>());
		Edge<String, String> e1 = new Edge<>(null, n1, n2, 2);
		Edge<String, String> e2 = new Edge<>(null, n2, n3, 2);
		Edge<String, String> e3 = new Edge<>(null, n3, n4, 2);
		Edge<String, String> e4 = new Edge<>(null, n4, n1, 2);
		Edge<String, String> e5 = new Edge<>(null, n1, n3, 2);
		Edge<String, String> e6 = new Edge<>(null, n2, n4, 2);
		n1.setEdges(new ArrayList<>(Arrays.asList(e1, e4, e5)));
		n2.setEdges(new ArrayList<>(Arrays.asList(e1, e2, e6)));
		n3.setEdges(new ArrayList<>(Arrays.asList(e2, e3, e5)));
		n4.setEdges(new ArrayList<>(Arrays.asList(e3, e4, e6)));

		Graph<String, String> g = new Graph<>(new ArrayList<>(Arrays.asList(n1, n2, n3, n4)));
		List<Edge<String, String>> actual = fash.proceed(g);
		assertNotNull(actual);
		assertEquals("Feedback arc set of this graph must contain two arcs", 2, actual.size());
		assertTrue("Feedback arc set of this graph must contain the arc e2 (n2 -> n3)", actual.contains(e2));
		assertTrue("Feedback arc set of this graph must contain the arc e4 (n4 -> n1)", actual.contains(e4));
	}

}
