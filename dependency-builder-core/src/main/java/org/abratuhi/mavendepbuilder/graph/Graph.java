package org.abratuhi.mavendepbuilder.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
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

	public void remove(Node<S, T> node) {
		getNodes().remove(node);
		getNodes().forEach(node1 -> node1.remove(node));
	}

	public void removeAll(List<Node<S, T>> nodes) {
		getNodes().removeAll(nodes);
		getNodes().forEach(node1 -> node1.removeAll(nodes));
	}

	public boolean isEmpty() {
		return CollectionUtils.isEmpty(nodes);
	}

	public Node<S, T> getByNodeObject(S object) {
		//return nodes.stream().filter(node -> (node.getObject()==null && object == null) || node.getObject().equals(object)).findFirst().orElse(null);
		return nodes.stream().filter(node -> node.getObject().equals(object)).findFirst().orElse(null);
	}

	public Edge<S, T> addEdge(T edgeObject, Node<S, T> from, Node<S, T> to, int weight) {
		Edge<S, T> edge = new Edge<>(edgeObject, from, to, weight);
		from.getEdges().add(edge);
		if (!from.equals(to)) {
			to.getEdges().add(edge);
		}
		return edge;
	}

	public List<Edge<S, T>> edges() {
		return getNodes().stream().map(Node::out).flatMap(Collection::stream).collect(Collectors.toList());
	}

	public Graph<S, T> copy() {
		Graph<S, T> result = new Graph();
		getNodes().forEach(node -> result.getNodes().add(new Node<S, T>(node.getObject(), new ArrayList<>())));
		edges().forEach(edge -> result.addEdge(edge.getObject(), result.getByNodeObject(edge.getFrom().getObject()), result.getByNodeObject(edge.getTo().getObject()), edge.getWeight()));
		return result;
	}
}
