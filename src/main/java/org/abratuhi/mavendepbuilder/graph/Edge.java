package org.abratuhi.mavendepbuilder.graph;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
	private T object;
	private Node<S, T> from;
	private Node<S, T> to;
	private Integer weight;

	public String toString() {
		return from.getObject().toString() + " -> " + to.getObject().toString() + " | " + weight + " | " + object.toString();
	}
}
