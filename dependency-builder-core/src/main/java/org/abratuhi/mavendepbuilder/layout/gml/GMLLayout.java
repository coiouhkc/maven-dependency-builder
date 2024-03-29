package org.abratuhi.mavendepbuilder.layout.gml;

import org.abratuhi.mavendepbuilder.graph.DependencyEdge;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.abratuhi.mavendepbuilder.layout.ILayout;
import org.abratuhi.mavendepbuilder.layout.LayoutUtil;
import org.abratuhi.mavendepbuilder.options.LayoutOptions;
import org.apache.commons.io.FileUtils;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Alexei Bratuhin
 * @see <a href="http://www.fim.uni-passau.de/fileadmin/files/lehrstuhl/brandenburg/projekte/gml/gml-technical-report.pdf">http://www.fim.uni-passau.de/fileadmin/files/lehrstuhl/brandenburg/projekte/gml/gml-technical-report.pdf</a>
 */
public class GMLLayout implements ILayout {

  public <S extends Graphable, T> void doLayout(DefaultDirectedGraph<S, DependencyEdge> graph, List<DependencyEdge> violations, File toFile, LayoutOptions layoutOptions) throws IOException {
    // build directed graph in gml notation (using wikipedia example as reference)
    StringBuilder sb = new StringBuilder();
    sb.append("graph [ \n");
    sb.append("directed 1 \n");
    graph.vertexSet().forEach(node -> {
      sb.append("node [ \n");
      sb.append("id " + node.getLabel() + " \n");
      sb.append("label \"" + LayoutUtil.getNodeLabel(node.getLabel(), layoutOptions.getNodeLayout()) + "\" \n");
      sb.append("] \n");
    });

    graph.edgeSet().forEach(edge -> {
      sb.append("edge [ \n");
      sb.append("source " + graph.getEdgeSource(edge) + " \n");
      sb.append("target " + graph.getEdgeTarget(edge) + " \n");
      sb.append("label \"");
      sb.append(LayoutUtil.getEdgeLabel("", layoutOptions.getEdgeLayout()) + " ");
      sb.append("\" \n");
      sb.append("] \n");
    });
    sb.append("] \n");

    // write result to file
    FileUtils.writeStringToFile(toFile, sb.toString());
  }


}
