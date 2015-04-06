package org.openflexo.diana.vaadin.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.openflexo.diana.vaadin.tests.TestEdge;
import org.openflexo.diana.vaadin.tests.TestGraph;

public class TestGraphNode extends Observable {

	private String name;
	private final TestGraph graph;
	private List<TestEdge> inputEdges;
	private List<TestEdge> outputEdges;
	private double x, y;

	public TestGraphNode(String name, TestGraph graph) {
		inputEdges = new ArrayList<TestEdge>();
		outputEdges = new ArrayList<TestEdge>();
		this.name = name;
		this.graph = graph;
		x = Math.random() * 500;
		y = Math.random() * 500;
		graph.addToNodes(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		System.out.println("Set node name with " + name);
		this.name = name;
	}

	public TestGraph getGraph() {
		return graph;
	}

	public List<TestEdge> getInputEdges() {
		return inputEdges;
	}

	public void setInputEdges(List<TestEdge> inputEdges) {
		this.inputEdges = inputEdges;
		setChanged();
		notifyObservers();
	}

	public void addToInputEdges(TestEdge aNode) {
		inputEdges.add(aNode);
		setChanged();
		notifyObservers();
	}

	public void removeFromInputEdges(TestEdge aNode) {
		inputEdges.remove(aNode);
		setChanged();
		notifyObservers();
	}

	public List<TestEdge> getOutputEdges() {
		return outputEdges;
	}

	public void setOutputEdges(List<TestEdge> outputEdges) {
		this.outputEdges = outputEdges;
		setChanged();
		notifyObservers();
	}

	public void addToOutputEdges(TestEdge aNode) {
		outputEdges.add(aNode);
		setChanged();
		notifyObservers();
	}

	public void removeFromOutputEdges(TestEdge aNode) {
		outputEdges.remove(aNode);
		setChanged();
		notifyObservers();
	}

	public void connectTo(TestGraphNode toNode) {
		TestEdge newEdge = new TestEdge(this, toNode);
		addToOutputEdges(newEdge);
		toNode.addToInputEdges(newEdge);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "GraphNode[" + name + "](" + x + "," + y + ")";
	}

	private double labelX = 30;
	private double labelY = 0;

	public double getLabelX() {
		return labelX;
	}

	public void setLabelX(double labelX) {
		this.labelX = labelX;
	}

	public double getLabelY() {
		return labelY;
	}

	public void setLabelY(double labelY) {
		this.labelY = labelY;
	}

}
