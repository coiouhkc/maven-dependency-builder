package org.abratuhi.mavendepbuilder.layout.csv;

import com.opencsv.CSVWriter;
import org.abratuhi.mavendepbuilder.graph.Edge;
import org.abratuhi.mavendepbuilder.graph.Graph;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.abratuhi.mavendepbuilder.layout.ILayout;
import org.abratuhi.mavendepbuilder.options.LayoutOptions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Alexei Bratuhin.
 */
public class CSVLayout implements ILayout {
	@Override
	public <S extends Graphable, T> void doLayout(Graph<S, T> graph, List<Edge> violations, File toFile, LayoutOptions layoutOptions) throws IOException {
		CSVWriter csvWriter = new CSVWriter(new FileWriter(toFile), ',');
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
						violation.getFrom().getObject().toString(),
						violation.getTo().getObject().toString(),
						violation.getWeight().toString(),
						violation.getObject().toString()}));
		csvWriter.flush();
		csvWriter.close();
	}
}
