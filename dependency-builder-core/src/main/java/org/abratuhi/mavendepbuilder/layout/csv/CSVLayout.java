package org.abratuhi.mavendepbuilder.layout.csv;

import com.opencsv.CSVWriter;
import org.abratuhi.mavendepbuilder.graph.DependencyEdge;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.abratuhi.mavendepbuilder.layout.ILayout;
import org.abratuhi.mavendepbuilder.options.LayoutOptions;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Alexei Bratuhin.
 */
public class CSVLayout implements ILayout {
	@Override
	public <S extends Graphable, T> void doLayout(
			DefaultDirectedGraph<S, DependencyEdge> graph,
			List<DependencyEdge> violations,
			File toFile,
			LayoutOptions layoutOptions
	) throws IOException {
		CSVWriter csvWriter = new CSVWriter(new FileWriter(toFile));
		csvWriter.writeNext(new String[]{
			"From",
			"To",
			"# of Violations",
			"Violation imports (comma-separated)"
		});
		violations.forEach(
			violation ->
				csvWriter.writeNext(
					new String[]{
						graph.getEdgeSource(violation).getLabel(),
						graph.getEdgeTarget(violation).getLabel(),
						String.valueOf(violation.getWeight()),
						violation.getLabel()}));
		csvWriter.flush();
		csvWriter.close();
	}
}
