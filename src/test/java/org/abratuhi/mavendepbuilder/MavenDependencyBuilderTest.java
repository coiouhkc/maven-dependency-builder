package org.abratuhi.mavendepbuilder;

import org.abratuhi.mavendepbuilder.model.JavaClass;
import org.abratuhi.mavendepbuilder.model.Project;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by bratuhia on 04.08.2016.
 */
public class MavenDependencyBuilderTest {

	@Test
	public void testBuildDependencies0() throws IOException {
		MavenDependencyBuilder mdb = new MavenDependencyBuilder();
		List<Project> projects = mdb.visitDirectory(new File("src/test/resources/payara-issue-959-deps-0"));
		mdb.buildDependencies(projects);
		assertEquals(3, mdb.getClassProjectMap().size());
		assertEquals(0, mdb.getNumberOfDependenciesBetweenProjects());
	}

	@Test
	public void testBuildDependencies1() throws IOException {
		MavenDependencyBuilder mdb = new MavenDependencyBuilder();
		List<Project> projects = mdb.visitDirectory(new File("src/test/resources/payara-issue-959-deps-1"));
		mdb.buildDependencies(projects);
		assertEquals(3, mdb.getClassProjectMap().size());
		assertEquals(1, mdb.getNumberOfDependenciesBetweenProjects());
	}

	@Test
	public void testVisitDirectory() throws IOException {
		MavenDependencyBuilder mdb = new MavenDependencyBuilder();
		List<Project> projects = mdb.visitDirectory(new File("src/test/resources/payara-issue-959-deps-0"));
		assertEquals(3, projects.size());
	}

	@Test
	public void testVisitJavaClass() throws IOException {
		MavenDependencyBuilder mdb = new MavenDependencyBuilder();
		JavaClass javaClass = mdb.visitJavaClass(new File("src/test/resources/payara-issue-959-deps-0/payara-issue-959-ejb/src/main/java/org/abratuhi/payara/issue959/PayaraIssue959Impl.java"));
		assertNotNull(javaClass);
		assertEquals("org.abratuhi.payara.issue959.PayaraIssue959Impl", javaClass.getName());
		assertEquals(4, javaClass.getImports().size());
	}
}
