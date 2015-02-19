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

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.GraphicalRepresentation.VerticalTextAlignment;
import org.openflexo.fge.animation.impl.AnimationImpl;
import org.openflexo.fge.animation.impl.TranslationTransition;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.impl.FGELayoutManagerImpl;
import org.openflexo.fge.layout.TreeBasedLayoutManager;
import org.openflexo.fge.layout.TreeBasedLayoutManagerSpecification;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Forest;

/**
 * Default partial implementation for {@link TreeBasedLayoutManager}
 * 
 * @author sylvain
 * 
 */
public abstract class TreeBasedLayoutManagerImpl<LMS extends TreeBasedLayoutManagerSpecification<?>, O> extends
		FGELayoutManagerImpl<LMS, O> implements TreeBasedLayoutManager<LMS, O> {

	private DirectedGraph<ShapeNode<?>, ConnectorNode<?>> graph;
	private Forest<ShapeNode<?>, ConnectorNode<?>> forest;
	private int maxDepth = 0;

	public DirectedGraph<ShapeNode<?>, ConnectorNode<?>> getGraph() {
		return graph;
	}

	public Forest<ShapeNode<?>, ConnectorNode<?>> getForest() {
		return forest;
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
		Point2D newLocation = getLayout().transform(node);
		return new FGEPoint(newLocation.getX() - node.getWidth() / 2 - node.getBorder().getLeft(), newLocation.getY() - node.getHeight()
				/ 2 - node.getBorder().getTop());
	}

	/**
	 * Abstract method returning the {@link TreeLayout} which is used here
	 * 
	 * @return
	 */
	public abstract TreeLayout<ShapeNode<?>, ConnectorNode<?>> getLayout();

	/**
	 * Internally called to build the layout
	 * 
	 * @return
	 */
	protected abstract TreeLayout<ShapeNode<?>, ConnectorNode<?>> buildLayout();

	/**
	 * Called at the beginning of layout computation for the whole container
	 */
	@Override
	public void initLayout() {
		super.initLayout();

		graph = new DirectedOrderedSparseMultigraph<ShapeNode<?>, ConnectorNode<?>>();

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

		forest = new DelegateForest<ShapeNode<?>, ConnectorNode<?>>(graph);

		buildLayout();

	}

	@Override
	public void computeLayout() {

		super.computeLayout();

		Map<ShapeNode<?>, Double> xMap = new HashMap<ShapeNode<?>, Double>();
		Map<ShapeNode<?>, Double> yMap = new HashMap<ShapeNode<?>, Double>();

		maxDepth = 0;
		for (ShapeNode<?> shapeNode : getNodes()) {
			maxDepth = Math.max(maxDepth, getDepth(shapeNode));
			xMap.put(shapeNode, shapeNode.getX());
			yMap.put(shapeNode, shapeNode.getY());
		}

		// No global layout to launch

		if (animateLayout()) {
			List<TranslationTransition> transitions = new ArrayList<TranslationTransition>();
			for (ShapeNode<?> shapeNode : getNodes()) {
				FGEPoint newLocation = locationForNode(shapeNode);
				transitions.add(new TranslationTransition(shapeNode, new FGEPoint(xMap.get(shapeNode), yMap.get(shapeNode)), newLocation));
			}

			AnimationImpl.performTransitions(transitions, getAnimationStepsNumber(), getContainerNode().getDrawing());
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getPropertyName().equals(TreeBasedLayoutManagerSpecification.HORIZONTAL_ALIGNEMENT_KEY)) {
			invalidate();
			doLayout(true);
		} else if (evt.getPropertyName().equals(TreeBasedLayoutManagerSpecification.VERTICAL_ALIGNEMENT_KEY)) {
			invalidate();
			doLayout(true);
		}
	}

	protected int getDepth(ShapeNode<?> node) {
		if (forest.getPredecessorCount(node) > 0) {
			int returned = -1;
			List<ShapeNode<?>> predecessors = new ArrayList<ShapeNode<?>>(forest.getPredecessors(node));
			for (int i = 0; i < predecessors.size(); i++) {
				int parentDepth = getDepth(predecessors.get(i));
				if (parentDepth + 1 > returned) {
					returned = parentDepth + 1;
				}
			}
			return returned;
		}
		return 0;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	@Override
	public void attemptToPlaceNodeManually(ShapeNode<?> node) {
		super.attemptToPlaceNodeManually(node);
		invalidate();
	}

	public HorizontalTextAlignment getHorizontalAlignment() {
		return getLayoutManagerSpecification().getHorizontalAlignment();
	}

	public VerticalTextAlignment getVerticalAlignment() {
		return getLayoutManagerSpecification().getVerticalAlignment();
	}

}
