package org.openflexo.diana.vaadin.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.openflexo.diana.vaadin.tests.TestGraphNode;

public class TestGraph extends Observable {

	private List<TestGraphNode> nodes;
	private TestGraphNode rootNode;

	public TestGraph() {
		nodes = new ArrayList<TestGraphNode>();
	}

	public List<TestGraphNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<TestGraphNode> nodes) {
		this.nodes = nodes;
		setChanged();
		notifyObservers();
	}

	public void addToNodes(TestGraphNode aNode) {
		nodes.add(aNode);
		setChanged();
		notifyObservers();
	}

	public void removeFromNodes(TestGraphNode aNode) {
		nodes.remove(aNode);
		setChanged();
		notifyObservers();
	}

	public TestGraphNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(TestGraphNode rootNode) {
		this.rootNode = rootNode;
		setChanged();
		notifyObservers();
	}

}
