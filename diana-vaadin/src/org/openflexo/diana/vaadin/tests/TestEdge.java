package org.openflexo.diana.vaadin.tests;

import java.util.Observable;

import org.openflexo.diana.vaadin.tests.TestGraphNode;

public class TestEdge extends Observable {

	private final TestGraphNode startNode;
	private final TestGraphNode endNode;

	public TestEdge(TestGraphNode startNode, TestGraphNode endNode) {
		super();
		this.startNode = startNode;
		this.endNode = endNode;
	}

	public TestGraphNode getStartNode() {
		return startNode;
	}

	public TestGraphNode getEndNode() {
		return endNode;
	}
}
