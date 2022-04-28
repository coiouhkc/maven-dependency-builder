package org.abratuhi.mavendepbuilder.layout;

import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.abratuhi.mavendepbuilder.options.LayoutOptions;

/**
 * @author Alexei Bratuhin
 */
public class LayoutUtil {
  public static <S extends Graphable, T> String getNodeLabel(String label, LayoutOptions.NodeLayout nodeLayout) {
    switch (nodeLayout) {
      case NONE:
        return "";
      case TEXT:
        return label;
      default:
        return null;
    }
  }

  public static <S extends Graphable, T> String getEdgeLabel(String label, LayoutOptions.EdgeLayout edgeLayout) {
    switch (edgeLayout) {
      case NONE:
        return "";
      case WEIGHT:
      case TEXT:
        return label;
      default:
        return null;
    }
  }
}
