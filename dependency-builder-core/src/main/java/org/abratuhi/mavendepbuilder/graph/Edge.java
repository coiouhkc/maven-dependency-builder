package org.abratuhi.mavendepbuilder.graph;

import lombok.*;

/**
 * @author Alexei Bratuhin
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Edge<S, T> {
  private T object;
  private Node<S, T> from;
  private Node<S, T> to;
  private Integer weight;

  public String toString() {
    return from.getObject().toString() + " -> " + to.getObject().toString() + " | " + weight + " | " + object.toString();
  }
}
