package org.abratuhi.mavendepbuilder.graph;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JGraphT-based implementation of algorithm FASH (FAS ~ Feedback Arc Set).
 *
 * @see <a href="http://ajc.maths.uq.edu.au/pdf/12/ajc-v12-p15.pdf">http://ajc.maths.uq.edu.au/pdf/12/ajc-v12-p15.pdf</a>.
 */
@UtilityClass
public class JFashT {

  public <V> List<DependencyEdge> proceed(
      DefaultDirectedWeightedGraph<V, DependencyEdge> graph
  ) {
    List<DependencyEdge> result = new ArrayList<>();

    // clone/copy
    DefaultDirectedWeightedGraph<V, DependencyEdge> copy =
        new DefaultDirectedWeightedGraph<>(DependencyEdge.class);

    graph.vertexSet().forEach(copy::addVertex);

    graph.edgeSet().forEach(e -> {
      DependencyEdge ec = copy.addEdge(graph.getEdgeSource(e), graph.getEdgeTarget(e));
      ec.setLabel(ec.getLabel());
      copy.setEdgeWeight(ec, graph.getEdgeWeight(e));
    });
    // clone/copy - end

    Function<V, Boolean> isSource = v -> copy.inDegreeOf(v) == 0;
    Function<V, Boolean> isSink = v -> copy.outDegreeOf(v) == 0;
    Function<V, Boolean> isSourceOrSink = v -> isSource.apply(v) || isSink.apply(v);


    Function<V, Double> getIncomingEdgeWeight = v -> copy.incomingEdgesOf(v).stream().mapToDouble(copy::getEdgeWeight).sum();
    Function<V, Double> getOutgoingEdgeWeight = v -> copy.outgoingEdgesOf(v).stream().mapToDouble(copy::getEdgeWeight).sum();
    Function<V, Double> getOutInWeightDiff = v -> getOutgoingEdgeWeight.apply(v) - getIncomingEdgeWeight.apply(v);


    while (!copy.vertexSet().isEmpty()) {
      // remove all sinks and sources
      while (copy.vertexSet().stream().anyMatch(isSourceOrSink::apply)) {
        copy.removeAllVertices(
            copy.vertexSet().stream()
                .filter(isSourceOrSink::apply)
                .collect(Collectors.toList())
        );
      }

      // break cycle
      if (!copy.vertexSet().isEmpty()) {
        copy.vertexSet().stream()
            .map(v -> Pair.of(v, getOutInWeightDiff.apply(v)))
            .max(Comparator.comparing(Pair::getRight))
            .map(Pair::getLeft)
            .ifPresent(v -> {
              result.addAll(copy.incomingEdgesOf(v));
              copy.removeVertex(v);
            });
      }
    }

    return result;
  }
}
