package org.abratuhi.mavendepbuilder;

import org.abratuhi.mavendepbuilder.model.JavaClass;
import org.abratuhi.mavendepbuilder.model.Project;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Alexei Bratuhin
 */
public class MavenDependencyBuilderTest {

	@Test
	public void testProjectEquals() {
		Project p1 = new Project(1, "project", new HashMap<>(), new TreeSet<>(), true, false);
		Project p2 = new Project(2, "project", new HashMap<>(), null, false, false);
		assertEquals(p1, p2);
	}

	@Test
	public void testProjectNotEquals() {
		Project p1 = new Project(1, "project1", new HashMap<>(), null, false, false);
		Project p2 = new Project(1, "project2", new HashMap<>(), null, false, false);
		assertNotEquals(p1, p2);
	}

	@Test
	void testVisitDirectory() throws IOException {
		MavenDependencyBuilder mdb = new MavenDependencyBuilder();
		Set<Project> projects = mdb.visitDirectory(new File("src/test/resources/payara-issue-959-deps-0"));
		assertEquals(4, projects.size());
	}

	@Test
	void testVisitJavaClass() throws IOException {
		MavenDependencyBuilder mdb = new MavenDependencyBuilder();
		JavaClass javaClass = mdb.visitJavaClass(new File("src/test/resources/payara-issue-959-deps-0/payara-issue-959-ejb/src/main/java/org/abratuhi/payara/issue959/PayaraIssue959Impl.java"));
		assertNotNull(javaClass);
		assertEquals("org.abratuhi.payara.issue959.PayaraIssue959Impl", javaClass.getName());
		assertEquals("org.abratuhi.payara.issue959", javaClass.getPakkage());
		assertEquals(4, javaClass.getImports().size());
	}

	@Test
	void testVisitExcludedDirectory() throws IOException {
		MavenDependencyBuilder mdb = new MavenDependencyBuilder(Set.of("payara-issue-959-deps-0"));
		Set<Project> projects = mdb.visitDirectory(new File("src/test/resources/payara-issue-959-deps-0"));
		assertNotNull(projects);
		assertTrue(projects.isEmpty());
	}

	@Test
	public void testVisitParentInDependencies() throws IOException {
		MavenDependencyBuilder mdb = new MavenDependencyBuilder();
		Set<Project> projects = mdb.visitDirectory(new File("src/test/resources/payara-issue-959-deps-1"));
		assertNotNull(projects);
		Project projectApi = projects.stream().filter(project -> project.getName().contains("api")).findFirst().orElse(null);
		assertNotNull(projectApi);
		assertTrue(projectApi.getDependencies().containsKey("org.abratuhi:payara-issue-959"));
	}
}
