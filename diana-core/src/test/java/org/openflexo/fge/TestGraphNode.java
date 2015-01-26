/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.fge;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

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
