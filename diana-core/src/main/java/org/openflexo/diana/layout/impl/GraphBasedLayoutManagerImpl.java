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

package org.openflexo.diana.layout.impl;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.animation.impl.AnimationImpl;
import org.openflexo.diana.animation.impl.TranslationTransition;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.impl.FGELayoutManagerImpl;
import org.openflexo.diana.layout.ForceDirectedGraphLayoutManagerSpecification;
import org.openflexo.diana.layout.GraphBasedLayoutManager;
import org.openflexo.diana.layout.GraphBasedLayoutManagerSpecification;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * Default partial implementation for {@link GraphBasedLayoutManager}
 * 
 * @author sylvain
 * 
 */
public abstract class GraphBasedLayoutManagerImpl<LMS extends GraphBasedLayoutManagerSpecification<?>, O>
		extends FGELayoutManagerImpl<LMS, O> implements GraphBasedLayoutManager<LMS, O> {

	private static final Logger logger = Logger.getLogger(GraphBasedLayoutManagerImpl.class.getPackage().getName());

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

		if (!animateLayout() || !node.getDrawing().isAnimationRunning()) {

			FGEPoint newLocation = locationForNode(node);
			node.setLocation(newLocation);
		}
	}

	/**
	 * Internally used to compute layout: the position computed by the layout is the center of the shape, so we need to translate it
	 * relatively to its size and border
	 * 
	 * @param node
	 * @return
	 */
	protected FGEPoint locationForNode(ShapeNode<?> node) {
		return new FGEPoint(getLayout().getX(node), getLayout().getY(node));
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

		graph = new DirectedOrderedSparseMultigraph<>();

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
		getLayout().setSize(new Dimension(getLayoutWidth().intValue(), getLayoutHeight().intValue()));

	}

	@Override
	public void computeLayout() {

		super.computeLayout();

		Map<ShapeNode<?>, Double> xMap = new HashMap<>();
		Map<ShapeNode<?>, Double> yMap = new HashMap<>();

		for (ShapeNode<?> shapeNode : getLayoutedNodes()) {
			getLayout().setLocation(shapeNode, shapeNode.getX(), shapeNode.getY());
			xMap.put(shapeNode, shapeNode.getX());
			yMap.put(shapeNode, shapeNode.getY());
		}

		if (getIterativeContextLayout() != null) {
			for (int i = 0; i < getStepsNumber(); i++) {
				// System.out.println("Compute layout, step " + i);
				try {
					getIterativeContextLayout().step();
				} catch (Exception e) {
					logger.warning("Unexpected exception: " + e.getMessage());
					e.printStackTrace();
				}
			}

		}

		if (animateLayout() && !layoutInProgress) {
			List<TranslationTransition> transitions = new ArrayList<>();
			for (ShapeNode<?> shapeNode : getLayoutedNodes()) {
				transitions.add(new TranslationTransition(shapeNode, new FGEPoint(xMap.get(shapeNode), yMap.get(shapeNode)),
						new FGEPoint(getLayout().getX(shapeNode), getLayout().getY(shapeNode))));
				// System.out.println("-----------> Node " + shapeNode + " moved from " + xMap.get(shapeNode) + "," + yMap.get(shapeNode)
				// + " to " + getLayout().getX(shapeNode) + "," + getLayout().getY(shapeNode));
			}

			AnimationImpl.performTransitions(transitions, getAnimationStepsNumber(), getContainerNode().getDrawing());
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getPropertyName().equals(ForceDirectedGraphLayoutManagerSpecification.STEPS_NUMBER_KEY)) {
			invalidate();
			doLayout(true);
		}
		else if (evt.getPropertyName().equals(ForceDirectedGraphLayoutManagerSpecification.LAYOUT_WIDTH_KEY)) {
			invalidate();
			doLayout(true);
		}
		else if (evt.getPropertyName().equals(ForceDirectedGraphLayoutManagerSpecification.LAYOUT_HEIGHT_KEY)) {
			invalidate();
			doLayout(true);
		}
	}

	@Override
	public Double getLayoutWidth() {
		if (getLayoutManagerSpecification() == null) {
			return 0.0;
		}

		Double returned = getLayoutManagerSpecification().getLayoutWidth();
		if (returned == null) {
			return getContainerNode().getWidth();
		}
		return returned;
	}

	@Override
	public void setLayoutWidth(Double aValue) {
		if (getLayoutManagerSpecification() == null) {
			return;
		}
		if (!getLayoutWidth().equals(aValue)) {
			getLayoutManagerSpecification().setLayoutWidth(aValue);
		}
	}

	@Override
	public Double getLayoutHeight() {
		if (getLayoutManagerSpecification() == null) {
			return 0.0;
		}

		Double returned = getLayoutManagerSpecification().getLayoutHeight();
		if (returned == null) {
			return getContainerNode().getHeight();
		}
		return returned;
	}

	@Override
	public void setLayoutHeight(Double aValue) {
		if (getLayoutManagerSpecification() == null) {
			return;
		}
		if (!getLayoutHeight().equals(aValue)) {
			getLayoutManagerSpecification().setLayoutHeight(aValue);
		}
	}

}
