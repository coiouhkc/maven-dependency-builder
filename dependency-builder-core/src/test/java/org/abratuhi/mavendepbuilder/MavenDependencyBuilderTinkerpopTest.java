package org.abratuhi.mavendepbuilder;

import org.abratuhi.mavendepbuilder.graph.Fash;
import org.abratuhi.mavendepbuilder.graph.Graph;
import org.abratuhi.mavendepbuilder.model.JavaPackage;
import org.abratuhi.mavendepbuilder.model.Project;
import org.abratuhi.mavendepbuilder.options.LayoutOptions;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Alexei Bratuhin
 */
public class MavenDependencyBuilderTinkerpopTest {

	public static final String PATH_APACHE_TINKERPOP = "C:/Users/bratuhia/Documents/GitHub/tinkerpop";

	private Fash analyzer = new Fash();

	private LayoutOptions layoutOptionsGmlTextWeight = new LayoutOptions(LayoutOptions.FormatLayout.GML, LayoutOptions.NodeLayout.TEXT, LayoutOptions.EdgeLayout.WEIGHT);

	@Test
	public void testAnalyzeTinkerpop() throws IOException {
		MavenDependencyBuilder mdb = new MavenDependencyBuilder();

		Set<Project> projects = mdb.visit(new File(PATH_APACHE_TINKERPOP));
		assertEquals(22, projects.size());

		Graph<Project, String> projectDepGraph = mdb.buildProjectDependencyGraph(projects);
		assertNotNull(projectDepGraph);
		assertEquals(22, projectDepGraph.getNodes().size());
		assertEquals(0, projectDepGraph.getNodes().get(0).getEdges().size());
		assertEquals(0, projectDepGraph.getNodes().get(1).getEdges().size());
		assertEquals(3, projectDepGraph.getNodes().get(2).getEdges().size());
		assertEquals(7, projectDepGraph.getNodes().get(3).getEdges().size());
		assertEquals(4, projectDepGraph.getNodes().get(4).getEdges().size());

		mdb.layout(projectDepGraph, new File("tinkerpop-project.gml"), layoutOptionsGmlTextWeight);

		//DependencyTestUtil.testViolations(analyzer, projectDepGraph, 1);
		DependencyTestUtil.testViolations(analyzer, projectDepGraph);

		Graph<JavaPackage, String> packageDepGraph = mdb.buildPackageDependencyGraph(projects);
		mdb.layout(packageDepGraph, new File("tinkerpop-package.gml"), layoutOptionsGmlTextWeight);
		DependencyTestUtil.testViolations(analyzer, packageDepGraph);
	}
}
