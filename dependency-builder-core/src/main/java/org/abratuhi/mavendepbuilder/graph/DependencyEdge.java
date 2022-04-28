package org.abratuhi.mavendepbuilder.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jgrapht.graph.DefaultWeightedEdge;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class DependencyEdge extends DefaultWeightedEdge {
  private String label;
}
