package org.abratuhi.mavendepbuilder.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alexei Bratuhin
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Node<S, T> {
	private T object;
	private List<Edge<S, T>> edges = new ArrayList<>();

	public List<Edge<S,T>> in() {
		return getEdges().stream().filter(edge -> edge.getTo().equals(this)).collect(Collectors.toList());
	}

	public List<Edge<S,T>> out() {
		return getEdges().stream().filter(edge -> edge.getFrom().equals(this)).collect(Collectors.toList());
	}

	public Integer inWeight() {
		return in().stream().map(Edge::getWeight).mapToInt(i -> i).sum();
	}

	public Integer outWeight() {
		return out().stream().map(Edge::getWeight).mapToInt(i -> i).sum();
	}

	public Integer outInWightDiff() {
		return outWeight() - inWeight();
	}

	public void remove (Node<S, T> node) {
		edges.removeIf(edge -> edge.getFrom().equals(node) || edge.getTo().equals(node));
	}

	public void removeAll (List<Node<S, T>> nodes) {
		edges.removeIf(edge -> nodes.contains(edge.getFrom()) || nodes.contains(edge.getTo()));
	}
}
