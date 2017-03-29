package org.abratuhi.mavendepbuilder.graphml;

import org.abratuhi.mavendepbuilder.model.Project;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Alexei Bratuhin
 */
public class GraphMLLayout {

	public void buildDependencies(Set<Project> projects, File toFile) throws IOException {
		// set numerical ids
		final AtomicInteger index = new AtomicInteger(0);
		projects.stream().forEach(project -> project.setId(index.getAndIncrement()));
		projects.stream().map(project -> project.getPackages()).flatMap(l -> l.stream()).forEach(pakkage -> pakkage.setId(index.getAndIncrement()));
		projects.stream().map(project -> project.getClasses()).flatMap(l -> l.stream()).forEach(clazz -> clazz.setId(index.getAndIncrement()));

		// build (nested) directed graph in graphml notation (using wikipedia example as reference) with yEd flavour
		StringBuffer sb = new StringBuffer();
		sb.append(""
				+ "<graphml\n" + " xmlns=\"http://graphml.graphdrawing.org/xmlns\"\n"
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
				+ " xmlns:y=\"http://www.yworks.com/xml/graphml\"\n"
				+ " xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd\">"
				+ "  <key for=\"node\" id=\"d0\" yfiles.type=\"nodegraphics\"/>"
				+ "  <graph id=\"G\" edgedefault=\"directed\">\n" );
		projects.stream().forEach(project -> {
			sb.append("<node id=\"" + project.getId() + "\">\n");
			sb.append(""
					+ "<data key=\"d0\">\n"
					+ "        <y:ShapeNode>\n"
					+ "          <y:NodeLabel>\"" + project.getName() + "\"</y:NodeLabel>\n"
					+ "        </y:ShapeNode>\n"
					+ "      </data>\n");
			sb.append("<graph id=\"G_" + project.getId() + "\" edgedefault=\"directed\">");
			project.getPackages().stream().forEach(pakkage -> {
				sb.append("<node id=\"" + pakkage.getId() + "\">");
				sb.append(""
						+ "<data key=\"d0\">\n"
						+ "        <y:ShapeNode>\n"
						+ "          <y:NodeLabel>\"" + pakkage.getName() + "\"</y:NodeLabel>\n"
						+ "        </y:ShapeNode>\n"
						+ "      </data>\n");
//				sb.append("<graph id=\"G_" + project.getId() + "_" + pakkage.getId() + "\" edgedefault=\"directed\">");
//				pakkage.getClasses().forEach(clazz -> {
//					sb.append("<node id=\"" + clazz.getName() + "\">\n");
//					sb.append(""
//							+ "<data key=\"d0\">\n"
//							+ "        <y:ShapeNode>\n"
//							+ "          <y:NodeLabel>\"" + clazz.getName() + "\"</y:NodeLabel>\n"
//							+ "        </y:ShapeNode>\n"
//							+ "      </data>\n");
//					sb.append("</node>\n");
//				});
//				sb.append("</graph>");
				sb.append("</node>\n");
			});
			sb.append("</graph>\n");
			sb.append("</node>\n");
		});

//		projects.stream().map(project -> project.getClasses()).flatMap(l -> l.stream()).forEach(clazzFrom -> {
//			clazzFrom.getImports().stream().filter(clazzTo1 -> projects.stream().map(project1 -> project1.getClasses()).flatMap(l -> l.stream()).filter(clazzFrom1 -> StringUtils.equals(clazzFrom1.getName(), clazzTo1)).count() > 0).forEach(clazzTo -> {
//				sb.append("<edge id=\"" + clazzFrom.getName() + clazzTo + "\" source=\"" + clazzFrom.getName() + "\" target=\"" + clazzTo + "\"/>\n");
//			});
//		});

		projects.stream().map(project -> project.getPackages()).flatMap(l -> l.stream()).forEach(packageFrom -> {
			projects.stream().map(project -> project.getPackages()).flatMap(l -> l.stream()).forEach(packageTo -> {
				if (CollectionUtils.intersection(
						packageFrom.getClasses().stream().map(clazz -> clazz.getImports()).flatMap(l -> l.stream()).collect(Collectors.toList()),
						packageTo.getClasses().stream().map(clazz -> clazz.getName()).collect(Collectors.toList())).size() > 0) {
					sb.append("<edge id=\"" + packageFrom.getId() + "_" + packageTo.getId() + "\" source=\"" + packageFrom.getId() + "\" target=\"" + packageTo.getId() + "\">\n");
					sb.append("<data key=\"d10\">\n"
							+ "        <y:PolyLineEdge>\n"
							+ "          <y:Path sx=\"0.0\" sy=\"0.0\" tx=\"0.0\" ty=\"0.0\"/>\n"
							+ "          <y:LineStyle color=\"#000000\" type=\"line\" width=\"1.0\"/>\n"
							+ "          <y:Arrows source=\"none\" target=\"standard\"/>\n"
							+ "          <y:BendStyle smoothed=\"false\"/>\n"
							+ "        </y:PolyLineEdge>\n" + "      </data>");
					sb.append("</edge>");
				}
			});
		});


		sb.append("</graph>\n"
				+ "</graphml>"
				+ "");

		// write result to file
		FileUtils.writeStringToFile(toFile, sb.toString());
	}
}
