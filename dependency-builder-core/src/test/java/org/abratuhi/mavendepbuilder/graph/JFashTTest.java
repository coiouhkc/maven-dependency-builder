package org.abratuhi.mavendepbuilder.graph;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JFashTTest {

  @Test
  void testEmpty() {
    DefaultDirectedWeightedGraph<String, DefaultEdge> g = new DefaultDirectedWeightedGraph<>(DefaultEdge.class);
    List<DefaultEdge> actual = JFashT.proceed(g);
    assertNotNull(actual);
    assertTrue(actual.isEmpty(), "Feedback arc set of an empty graph must be empty");
  }

  @Test
  void testOneNode() {
    DefaultDirectedWeightedGraph<String, DefaultEdge> g = new DefaultDirectedWeightedGraph<>(DefaultEdge.class);
    g.addVertex("a");
    List<DefaultEdge> actual = JFashT.proceed(g);
    assertNotNull(actual);
    assertTrue(actual.isEmpty(), "Feedback arc set of an graph with one node and no edges must be empty");
  }

  @Test
  void testOneNodeLoop() {
    DefaultDirectedWeightedGraph<String, DefaultEdge> g = new DefaultDirectedWeightedGraph<>(DefaultEdge.class);
    g.addVertex("a");
    g.addEdge("a", "a");
    g.setEdgeWeight(g.getEdge("a", "a"), 1);
    List<DefaultEdge> actual = JFashT.proceed(g);

    assertNotNull(actual);
    assertEquals(1, actual.size(), "Feedback arc set of an one node one arc loop must contain that arc");
  }

  @Test
  void testTwoNodeLoop() {
    DefaultDirectedWeightedGraph<String, DependencyEdge> g = new DefaultDirectedWeightedGraph<>(DependencyEdge.class);
    g.addVertex("a");
    g.addVertex("b");

    DependencyEdge ab = g.addEdge("a", "b");
    ab.setLabel("ab");
    DependencyEdge ba = g.addEdge("b", "a");
    ba.setLabel("ba");
    g.setEdgeWeight(ab, 1);
    g.setEdgeWeight(ba, 2);
    List<DependencyEdge> actual = JFashT.proceed(g);

    assertNotNull(actual);
    assertEquals(1, actual.size(), "Feedback arc set of an two node loop must contain one arc");
    assertEquals(1, actual.get(0).getWeight(), "Feedback arc set must contain the arc with the least weight");
  }

  @Test
  void testFourNodeWithLoops() {

    DefaultDirectedWeightedGraph<String, DependencyEdge> g = new DefaultDirectedWeightedGraph<>(DependencyEdge.class);

    g.addVertex("a");
    g.addVertex("b");
    g.addVertex("c");
    g.addVertex("d");

    DependencyEdge ab = g.addEdge("a", "b");
    ab.setLabel("ab");
    DependencyEdge bc = g.addEdge("b", "c");
    bc.setLabel("bc");
    DependencyEdge cd = g.addEdge("c", "d");
    cd.setLabel("cd");
    DependencyEdge da = g.addEdge("d", "a");
    da.setLabel("da");
    DependencyEdge ac = g.addEdge("a", "c");
    ac.setLabel("ac");
    DependencyEdge bd = g.addEdge("b", "d");
    bd.setLabel("bd");

    g.setEdgeWeight(ab, 1);
    g.setEdgeWeight(bc, 1);
    g.setEdgeWeight(cd, 1);
    g.setEdgeWeight(da, 1);
    g.setEdgeWeight(ac, 1);
    g.setEdgeWeight(bd, 1);

    List<DependencyEdge> actual = JFashT.proceed(g);

    assertNotNull(actual);
    assertEquals(1, actual.size(), "Feedback arc set of this graph must contain one arc");
    assertEquals("d", g.getEdgeSource(actual.get(0)), "Feedback arc set of this graph must contain the arc e4 (n4 -> n1)");
    assertEquals("a", g.getEdgeTarget(actual.get(0)), "Feedback arc set of this graph must contain the arc e4 (n4 -> n1)");
  }
}
