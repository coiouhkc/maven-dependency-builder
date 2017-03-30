package org.abratuhi.mavendepbuilder.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Builder;

/**
 * @author Alexei Bratuhin
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Edge<S, T> {
	private S object;
	private Node<S, T> from;
	private Node<S, T> to;
	private Integer weight;
}
