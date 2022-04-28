package org.abratuhi.mavendepbuilder.graph;

/**
 * @author Alexei Bratuhin
 */
public interface Graphable {

  Integer getId();

  String getLabel();

  default String getColor() {
    return "#ccccff";
  }
}
