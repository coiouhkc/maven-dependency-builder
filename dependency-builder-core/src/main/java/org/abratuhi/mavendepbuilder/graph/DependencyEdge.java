package org.abratuhi.mavendepbuilder.graph;

import lombok.*;
import org.jgrapht.graph.DefaultWeightedEdge;

// TODO: investigate why adding Lombok @Data annotation seems to ruin smth. within equals/hashCode in deep depth of BaseIntrusiveEdgesSpecifics
public class DependencyEdge extends DefaultWeightedEdge {
  @Getter @Setter private String label;

  public double getWeight() {
    return super.getWeight();
  }
}
