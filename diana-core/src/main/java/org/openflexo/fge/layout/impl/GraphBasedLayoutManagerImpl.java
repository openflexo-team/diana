/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-api, a component of the software infrastructure 
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

package org.openflexo.fge.layout.impl;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.tools.animations.Animation;
import org.openflexo.fge.control.tools.animations.TranslationTransition;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.impl.FGELayoutManagerImpl;
import org.openflexo.fge.layout.ForceDirectedGraphLayoutManagerSpecification;
import org.openflexo.fge.layout.GraphBasedLayoutManager;
import org.openflexo.fge.layout.GraphBasedLayoutManagerSpecification;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * Default partial implementation for {@link GraphBasedLayoutManager}
 * 
 * @author sylvain
 * 
 */
public abstract class GraphBasedLayoutManagerImpl<LMS extends GraphBasedLayoutManagerSpecification<?>, O> extends
		FGELayoutManagerImpl<LMS, O> implements GraphBasedLayoutManager<LMS, O> {

	private Graph<ShapeNode<?>, ConnectorNode<?>> graph;

	public Graph<ShapeNode<?>, ConnectorNode<?>> getGraph() {
		return graph;
	}

	@Override
	public int getStepsNumber() {
		return getLayoutManagerSpecification().getStepsNumber();
	}

	@Override
	public boolean isFullyLayouted() {
		return true;
	}

	@Override
	protected void performLayout(ShapeNode<?> node) {

		// computeLayout();

		FGEPoint newLocation = new FGEPoint(getLayout().getX(node), getLayout().getY(node));
		// System.out.println("New location for " + node + " " + newLocation);
		node.setLocation(newLocation);
	}

	/**
	 * Abstract method returning the {@link AbstractLayout} which is used here
	 * 
	 * @return
	 */
	public abstract AbstractLayout<ShapeNode<?>, ConnectorNode<?>> getLayout();

	/**
	 * Return the layout asserting that this {@link AbstractLayout} implements {@link IterativeContext}
	 * 
	 * @return
	 */
	public IterativeContext getIterativeContextLayout() {
		if (getLayout() instanceof IterativeContext) {
			return (IterativeContext) getLayout();
		}
		return null;
	}

	/**
	 * Internally called to build the layout
	 * 
	 * @return
	 */
	protected abstract AbstractLayout<ShapeNode<?>, ConnectorNode<?>> buildLayout();

	/*
	 * Called at the beginning of layout computation for the whole container
	 */
	@Override
	public void initLayout() {
		super.initLayout();

		graph = new DirectedSparseMultigraph<ShapeNode<?>, ConnectorNode<?>>();

		for (DrawingTreeNode<?, ?> dtn : getContainerNode().getChildNodes()) {
			if (dtn instanceof ShapeNode) {
				graph.addVertex((ShapeNode<?>) dtn);
			}
		}

		for (DrawingTreeNode<?, ?> dtn : getContainerNode().getChildNodes()) {
			if (dtn instanceof ConnectorNode) {
				ConnectorNode<?> connectorNode = (ConnectorNode<?>) dtn;
				if (graph.containsVertex((connectorNode.getStartNode())) && graph.containsVertex((connectorNode.getEndNode()))) {
					graph.addEdge(connectorNode, connectorNode.getStartNode(), connectorNode.getEndNode());
				}
			}
		}

		System.out.println(graph.toString());

		buildLayout();

	}

	@Override
	public void computeLayout() {

		super.computeLayout();

		Map<ShapeNode<?>, Double> xMap = new HashMap<ShapeNode<?>, Double>();
		Map<ShapeNode<?>, Double> yMap = new HashMap<ShapeNode<?>, Double>();

		for (ShapeNode<?> shapeNode : getNodes()) {
			getLayout().setLocation(shapeNode, shapeNode.getX(), shapeNode.getY());
			xMap.put(shapeNode, shapeNode.getX());
			yMap.put(shapeNode, shapeNode.getY());
		}

		if (getIterativeContextLayout() != null) {
			for (int i = 0; i < getStepsNumber(); i++) {
				// System.out.println("Compute layout, step " + i);
				getIterativeContextLayout().step();
			}
		}

		List<TranslationTransition> transitions = new ArrayList<TranslationTransition>();
		for (ShapeNode<?> shapeNode : getNodes()) {
			transitions.add(new TranslationTransition(shapeNode, new FGEPoint(xMap.get(shapeNode), yMap.get(shapeNode)), new FGEPoint(
					getLayout().getX(shapeNode), getLayout().getY(shapeNode))));
		}

		layoutInProgress = true;
		Animation.performTransitions(transitions);
		layoutInProgress = false;

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getPropertyName().equals(ForceDirectedGraphLayoutManagerSpecification.STEPS_NUMBER_KEY)) {
			invalidate();
			doLayout(true);
		}
	}

	/*@Override
		public void invalidate(ShapeNode<?> node) {
			System.out.println("On invalide " + node);
			computeLayout();
			super.invalidate(node);
		}*/

	/*@Override
		public void shapeMoved(FGEPoint oldLocation, FGEPoint location) {
			if (!isLayoutInProgress()) {
				super.shapeMoved(oldLocation, location);

				new Thread(new Runnable() {
					@Override
					public void run() {
						for (int i = 0; i < 10; i++) {
							layout.step();
							doLayout(true);
						}
					}
				}).start();

			}
		}*/
}
