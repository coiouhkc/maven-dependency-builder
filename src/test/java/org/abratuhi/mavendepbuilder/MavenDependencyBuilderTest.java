package org.abratuhi.mavendepbuilder;

import org.abratuhi.mavendepbuilder.gml.GMLLayout;
import org.abratuhi.mavendepbuilder.model.JavaClass;
import org.abratuhi.mavendepbuilder.model.Project;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Alexei Bratuhin
 */
public class MavenDependencyBuilderTest {

	@Test
	public void testProjectEquals() {
		Project p1 = new Project(1, "project", new TreeSet<>(), new TreeSet<>());
		Project p2 = new Project(2, "project", null, null);
		assertEquals(p1, p2);
	}

	@Test
	public void testProjectNotEquals() {
		Project p1 = new Project(1, "project1", null, null);
		Project p2 = new Project(1, "project2", null, null);
		assertNotEquals(p1, p2);
	}

	@Test
	public void testBuildDependencies0() throws IOException {
		MavenDependencyBuilder mdb = new MavenDependencyBuilder();
		Set<Project> projects = mdb.visitDirectory(new File("src/test/resources/payara-issue-959-deps-0"));
		GMLLayout gml = new GMLLayout();
		gml.buildDependencies(projects);
		assertEquals(3, gml.getClassProjectMap().size());
		assertEquals(0, gml.getNumberOfDependenciesBetweenProjects());
	}

	@Test
	public void testBuildDependencies1() throws IOException {
		MavenDependencyBuilder mdb = new MavenDependencyBuilder();
		Set<Project> projects = mdb.visitDirectory(new File("src/test/resources/payara-issue-959-deps-1"));
		GMLLayout gml = new GMLLayout();
		gml.buildDependencies(projects);
		assertEquals(3, gml.getClassProjectMap().size());
		assertEquals(1, gml.getNumberOfDependenciesBetweenProjects());
	}

	@Test
	public void testVisitDirectory() throws IOException {
		MavenDependencyBuilder mdb = new MavenDependencyBuilder();
		Set<Project> projects = mdb.visitDirectory(new File("src/test/resources/payara-issue-959-deps-0"));
		assertEquals(4, projects.size());
	}

	@Test
	public void testVisitJavaClass() throws IOException {
		MavenDependencyBuilder mdb = new MavenDependencyBuilder();
		JavaClass javaClass = mdb.visitJavaClass(new File("src/test/resources/payara-issue-959-deps-0/payara-issue-959-ejb/src/main/java/org/abratuhi/payara/issue959/PayaraIssue959Impl.java"));
		assertNotNull(javaClass);
		assertEquals("org.abratuhi.payara.issue959.PayaraIssue959Impl", javaClass.getName());
		assertEquals("org.abratuhi.payara.issue959", javaClass.getPakkage());
		assertEquals(4, javaClass.getImports().size());
	}
}
