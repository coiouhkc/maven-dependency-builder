package org.abratuhi.mavendepbuilder.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Builder;
import org.apache.commons.collections4.CollectionUtils;

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
public class Graph<S, T> {
	private List<Node<S, T>> nodes = new ArrayList<>();

	public List<Node<S, T>> sinks() {
		return nodes.stream().filter(node -> CollectionUtils.isEmpty(node.out())).collect(Collectors.toList());
	}

	public List<Node<S, T>> sources() {
		return nodes.stream().filter(node -> CollectionUtils.isEmpty(node.in())).collect(Collectors.toList());
	}

	public Graph<S, T> remove(Node<S, T> node) {
		Graph<S, T> result = new Graph<>(nodes);
		result.getNodes().remove(node);
		nodes.stream().forEach(node1 -> node1.remove(node));
		return result;
	}

	public Graph<S, T> removeAll(List<Node<S, T>> nodes) {
		Graph<S, T> result = new Graph<>(getNodes());
		result.getNodes().removeAll(nodes);
		nodes.stream().forEach(node1 -> node1.removeAll(nodes));
		return result;
	}

	public boolean isEmpty() {
		return CollectionUtils.isEmpty(nodes);
	}
}
