package org.abratuhi.mavendepbuilder.graph;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Alexei Bratuhin
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "object")
public class Node<S, T> {
  private S object;
  @Builder.Default
  private List<Edge<S, T>> edges = new ArrayList<>();

  public List<Edge<S, T>> in() {
    return getEdges().stream().filter(edge -> edge.getTo().equals(this)).collect(Collectors.toList());
  }

  public List<Edge<S, T>> out() {
    return getEdges().stream().filter(edge -> edge.getFrom().equals(this)).collect(Collectors.toList());
  }

  public Integer inWeight() {
    return in().stream().map(Edge::getWeight).mapToInt(i -> i).sum();
  }

  public Integer outWeight() {
    return out().stream().map(Edge::getWeight).mapToInt(i -> i).sum();
  }

  public Integer outInWightDiff() {
    return outWeight() - inWeight();
  }

  public void remove(Node<S, T> node) {
    edges.removeIf(edge -> edge.getFrom().equals(node) || edge.getTo().equals(node) || edge.getFrom().getObject().equals(node.getObject()) || edge.getTo().getObject().equals(node.getObject()));
  }

  public void removeAll(List<Node<S, T>> nodes) {
    edges.removeIf(edge -> nodes.contains(edge.getFrom()) || nodes.contains(edge.getTo()));
//		nodes.forEach(node -> remove(node));
  }
}
