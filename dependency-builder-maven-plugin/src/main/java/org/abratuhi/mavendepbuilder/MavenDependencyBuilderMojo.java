package org.abratuhi.mavendepbuilder;

import org.abratuhi.mavendepbuilder.graph.Edge;
import org.abratuhi.mavendepbuilder.graph.Fash;
import org.abratuhi.mavendepbuilder.graph.Graph;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.abratuhi.mavendepbuilder.model.Project;
import org.abratuhi.mavendepbuilder.options.LayoutOptions;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Alexei Bratuhin
 */
@Mojo(name = "mavendepbuilder")
public class MavenDependencyBuilderMojo extends AbstractMojo {
  @Parameter(property = "mavendepbuilder.inputDir")
  private String inputDir;

  @Parameter(property = "mavendepbuilder.outputFile")
  private String outputFile;

  @Parameter(property = "mavendepbuilder.formatLayoutType", defaultValue = "GML")
  private LayoutOptions.FormatLayout formatLayoutType;

  @Parameter(property = "mavendepbuilder.dependencyType", defaultValue = "PACKAGE")
  private LayoutOptions.DependencyType dependencyType;

  @Parameter(property = "mavendepbuilder.checkForViolations", defaultValue = "false")
  private boolean checkForViolations;

  @Parameter(property = "mavendepbuilder.nodeLayoutType", defaultValue = "TEXT")
  private LayoutOptions.NodeLayout nodeLayoutType;

  @Parameter(property = "mavendepbuilder.edgeLayoutType", defaultValue = "WEIGHT")
  private LayoutOptions.EdgeLayout edgeLayoutType;

	@Parameter(property = "mavendepbuilder.exclude", defaultValue = "")
	private String excludePatterns;


  public void execute() throws MojoExecutionException, MojoFailureException {
    if (StringUtils.isEmpty(inputDir)) {
      throw new MojoExecutionException("Input directory not specified");
    }

    if (StringUtils.isEmpty(outputFile)) {
      throw new MojoExecutionException("Output file not specified");
    }

    try {
			MavenDependencyBuilder mdb = new MavenDependencyBuilder(
					Arrays.stream(excludePatterns.split(",")).collect(Collectors.toSet())
			);

      Set<Project> projects = mdb.visit(new File(inputDir));

      DefaultDirectedGraph<? extends Graphable, DefaultEdge> dependencyGraph = mdb.buildDependencyGraph(projects, dependencyType);


      mdb.layout(dependencyGraph, new File(outputFile),
          new LayoutOptions(formatLayoutType, nodeLayoutType, edgeLayoutType));


      // FIXME: potentially destructive function, call last
//			if (checkForViolations) {
//				List<Edge> violations = new Fash().proceed(dependencyGraph);
//				violations.forEach(violation -> getLog().warn(violation.toString()));
//			}
    } catch (IOException e) {
      throw new MojoExecutionException("Dependency generation failed", e);
    }
  }
}
