package org.abratuhi.mavendepbuilder.layout;

import org.abratuhi.mavendepbuilder.graph.DependencyEdge;
import org.abratuhi.mavendepbuilder.graph.Edge;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.abratuhi.mavendepbuilder.options.LayoutOptions;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Alexei Bratuhin
 */
public interface ILayout {
  <S extends Graphable, T> void doLayout(
      DefaultDirectedGraph<S, DependencyEdge> graph,
      List<DependencyEdge> violations,
      File toFile,
      LayoutOptions layoutOptions
  ) throws IOException;
}
