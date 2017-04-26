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
public class MavenDependencyBuilderMasterclassTest {

	public static final String PATH_MASTERCLASS = "C:/Users/bratuhia/Documents/GitHub/maven-masterclass";

	private Fash analyzer = new Fash();

	private LayoutOptions layoutOptionsGmlTextText = new LayoutOptions(LayoutOptions.FormatLayout.GML, LayoutOptions.NodeLayout.TEXT, LayoutOptions.EdgeLayout.TEXT);

	@Test
	public void testAnalyzeMasterclass() throws IOException {
		MavenDependencyBuilder mdb = new MavenDependencyBuilder();

		Set<Project> projects = mdb.visit(new File(PATH_MASTERCLASS));
		assertEquals(8, projects.size());

		Graph<Project, String> projectDepGraph = mdb.buildProjectDependencyGraph(projects);
		assertNotNull(projectDepGraph);
		mdb.layout(projectDepGraph, new File("masterclass-project.gml"), layoutOptionsGmlTextText);
		DependencyTestUtil.testViolations(analyzer, projectDepGraph);

		Graph<JavaPackage, String> packageDepGraph = mdb.buildPackageDependencyGraph(projects);
		assertNotNull(packageDepGraph);
		assertEquals(14, packageDepGraph.getNodes().size());

		mdb.layout(packageDepGraph, new File("masterclass-package.gml"), layoutOptionsGmlTextText);
		DependencyTestUtil.testViolations(analyzer, packageDepGraph);
	}

}
