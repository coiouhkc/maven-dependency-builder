package org.abratuhi.mavendepbuilder.graph;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Alexei Bratuhin
 */
public class FashTest {

	private final Fash fash = new Fash();

	@Test
	void testEmpty() {
		Graph<String, String> g = new Graph<>();
		List<Edge<String, String>> actual = fash.proceed(g);
		assertNotNull(actual);
		assertTrue(actual.isEmpty(), "Feedback arc set of an empty graph must be empty");
	}

	@Test
	void testOneNode() {
		Graph<String, String> g = new Graph<>(new ArrayList<>(Collections.singletonList(new Node<>(null, new ArrayList<>()))));
		List<Edge<String, String>> actual = fash.proceed(g);
		assertNotNull(actual);
		assertTrue(actual.isEmpty(), "Feedback arc set of an empty graph must be empty");
	}

	@Test
	void testOneNodeLoop() {
		Node<String, String> n1 = new Node<>("n1", new ArrayList<>());
		Edge<String, String> e1 = new Edge<>("e1", n1, n1, 1);
		n1.setEdges(Collections.singletonList(e1));

		Graph<String, String> g = new Graph<>(new ArrayList<>(Collections.singletonList(n1)));
		List<Edge<String, String>> actual = fash.proceed(g);
		assertNotNull(actual);
		assertEquals(1, actual.size(), "Feedback arc set of an one node one arc loop must contain that arc");
	}

	@Test
	void testTwoNodeLoop() {
		Node<String, String> n1 = new Node<>("n1", new ArrayList<>());
		Node<String, String> n2 = new Node<>("n2", new ArrayList<>());
		Edge<String, String> e1 = new Edge<>("e1", n1, n2, 1);
		Edge<String, String> e2 = new Edge<>("e2", n2, n1, 2);
		n1.setEdges(new ArrayList<>(Arrays.asList(e1, e2)));
		n2.setEdges(new ArrayList<>(Arrays.asList(e1, e2)));

		Graph<String, String> g = new Graph<>(new ArrayList<>(Arrays.asList(n1, n2)));
		List<Edge<String, String>> actual = fash.proceed(g);
		assertNotNull(actual);
		assertEquals(1, actual.size(), "Feedback arc set of an two node loop must contain one arc");
		assertEquals(e1, actual.get(0), "Feedback arc set must contain the arc with the least weight");
	}

	@Test
	void testFourNodeWithLoops() {
		Node<String, String> n1 = new Node<>("n1", new ArrayList<>());
		Node<String, String> n2 = new Node<>("n2", new ArrayList<>());
		Node<String, String> n3 = new Node<>("n3", new ArrayList<>());
		Node<String, String> n4 = new Node<>("n4", new ArrayList<>());
		Edge<String, String> e1 = new Edge<>("e1", n1, n2, 2);
		Edge<String, String> e2 = new Edge<>("e2", n2, n3, 2);
		Edge<String, String> e3 = new Edge<>("e3", n3, n4, 2);
		Edge<String, String> e4 = new Edge<>("e4", n4, n1, 2);
		Edge<String, String> e5 = new Edge<>("e5", n1, n3, 2);
		Edge<String, String> e6 = new Edge<>("e6", n2, n4, 2);
		n1.setEdges(new ArrayList<>(Arrays.asList(e1, e4, e5)));
		n2.setEdges(new ArrayList<>(Arrays.asList(e1, e2, e6)));
		n3.setEdges(new ArrayList<>(Arrays.asList(e2, e3, e5)));
		n4.setEdges(new ArrayList<>(Arrays.asList(e3, e4, e6)));

		Graph<String, String> g = new Graph<>(new ArrayList<>(Arrays.asList(n1, n2, n3, n4)));
		List<Edge<String, String>> actual = fash.proceed(g);
		assertNotNull(actual);
		assertEquals(1, actual.size(), "Feedback arc set of this graph must contain one arc");
		assertTrue(actual.contains(e4), "Feedback arc set of this graph must contain the arc e4 (n4 -> n1)");
	}

}
