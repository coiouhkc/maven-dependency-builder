package org.abratuhi.mavendepbuilder.graph;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author Alexei Bratuhin
 */
public class NodeTest {
	@Test
	void testRemoveAllEmptyList() {
		Node<String, String> n1 = new Node<>(null, new ArrayList<>());
		Node<String, String> n2 = new Node<>(null, new ArrayList<>());
		Edge<String, String> e1 = new Edge<>(null, n1, n2, 1);
		Edge<String, String> e2 = new Edge<>(null, n2, n1, 1);
		n1.setEdges(Arrays.asList(e1, e2));
		n1.removeAll(Collections.emptyList());
		assertEquals(2, n1.getEdges().size(), "RemoveAll emptyList may not affect anything");
	}
}
