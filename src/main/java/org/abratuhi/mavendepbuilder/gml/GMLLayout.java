package org.abratuhi.mavendepbuilder.gml;

import lombok.Getter;
import org.abratuhi.mavendepbuilder.model.Project;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Alexei Bratuhin
 */
public class GMLLayout {

	@Getter final Map<String, Project> classProjectMap = new HashMap<>();
	@Getter final Map<Project, Map<Project, Set<String>>> fromToDepclassesMap = new HashMap<>();

	public void buildDependencies(Set<Project> projects) throws IOException {
		buildDependencies(projects, new File("dependencies.gml"));
	}

	public void buildDependencies(Set<Project> projects, File toFile) throws IOException {
		// set numerical ids
		Iterator<Project> iterator = projects.iterator();
		for (int i = 1; iterator.hasNext(); i++) {
			Project project = iterator.next();
			project.setId(i);
		}

		// fill map className -> project
		projects.parallelStream()
				.forEach(project -> project.getClasses()
						.forEach(javaClass -> classProjectMap.put(javaClass.getName(), project)));

		// fill multimap <fromProject, toProject> -> importedClases
		projects.stream()
				.forEach(from -> {
					fromToDepclassesMap.put(from, new HashMap<>());
					from.getClasses()
							.forEach(javaClass -> javaClass.getImports()
									.forEach(importClass -> {
										Project to = classProjectMap.get(importClass);
										if (to != null) {
											if (!fromToDepclassesMap.get(from).containsKey(to)) {
												fromToDepclassesMap.get(from).put(to, new TreeSet<>());
											}
											fromToDepclassesMap.get(from).get(to).add(importClass);
										}
									}));
				});

		// build directed graph in gml notation (using wikipedia example as reference)
		StringBuilder sb = new StringBuilder();
		sb.append("graph [ \n");
		sb.append("directed 1 \n");
		projects.forEach(project -> {
			sb.append("node [ \n");
			sb.append("id " + project.getId() + " \n");
			sb.append("label \"" + project.getName() + "\" \n");
			sb.append("] \n");
		});
		fromToDepclassesMap.keySet().forEach(from -> {
			fromToDepclassesMap.get(from).keySet().forEach(to -> {
				if (! from.getName().equals(to.getName())) {
					sb.append("edge [ \n");
					sb.append("source " + from.getId() + " \n");
					sb.append("target " + to.getId() + " \n");
					sb.append("label \"");
					fromToDepclassesMap.get(from).get(to).forEach(classImport -> {
						sb.append(classImport + " ");
					});
					sb.append("\" \n");
					sb.append("] \n");
				}
			});
		});
		sb.append("] \n");

		// write result to file
		FileUtils.writeStringToFile(toFile, sb.toString());
	}

	public int getNumberOfDependenciesBetweenProjects() {
		return fromToDepclassesMap.values().stream()
				.map(map -> map.values())
				.flatMap(set -> set.stream())
				.mapToInt(set -> set.size())
				.sum();
	}
}
