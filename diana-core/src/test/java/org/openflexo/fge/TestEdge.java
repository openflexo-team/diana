package org.openflexo.fge;

import java.util.Observable;

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
