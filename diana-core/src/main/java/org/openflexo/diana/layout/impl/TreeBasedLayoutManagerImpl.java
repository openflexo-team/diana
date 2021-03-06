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

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.diana.GraphicalRepresentation.VerticalTextAlignment;
import org.openflexo.diana.animation.impl.AnimationImpl;
import org.openflexo.diana.animation.impl.TranslationTransition;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.impl.DianaLayoutManagerImpl;
import org.openflexo.diana.layout.TreeBasedLayoutManager;
import org.openflexo.diana.layout.TreeBasedLayoutManagerSpecification;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Forest;

/**
 * Default partial implementation for {@link TreeBasedLayoutManager}<br>
 * 
 * This implementation manages a {@link DirectedGraph} which is acyclic. We prevent here from cycle formation by ignoring extra edges
 * creating cycles. Note that the choice of relevant edges is here arbitrary.
 * 
 * @author sylvain
 * 
 */
public abstract class TreeBasedLayoutManagerImpl<LMS extends TreeBasedLayoutManagerSpecification<?>, O>
		extends DianaLayoutManagerImpl<LMS, O> implements TreeBasedLayoutManager<LMS, O> {

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

			DianaPoint newLocation = locationForNode(node);
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
	protected DianaPoint locationForNode(ShapeNode<?> node) {
		Point2D newLocation = getLayout().transform(node);
		return new DianaPoint(newLocation.getX() - node.getWidth() / 2 /*- node.getBorder().getLeft()*/,
				newLocation.getY() - node.getHeight() / 2 /*- node.getBorder().getTop()*/);
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
					if (getFirstCommonAncestor(connectorNode.getStartNode(), connectorNode.getEndNode()) == null) {
						graph.addEdge(connectorNode, connectorNode.getStartNode(), connectorNode.getEndNode());
					}
					else {
						// Will not connect those two nodes otherwise a cycle will be created
					}
				}
			}
		}

		forest = new DelegateForest<>(graph);

		System.out.println("tree=" + graph.toString());

		buildLayout();

	}

	public ShapeNode<?> getFirstCommonAncestor(ShapeNode<?> child1, ShapeNode<?> child2) {
		List<ShapeNode<?>> ancestors1 = getAncestors(child1);
		List<ShapeNode<?>> ancestors2 = getAncestors(child2);
		for (int i = 0; i < ancestors1.size(); i++) {
			ShapeNode<?> o1 = ancestors1.get(i);
			if (ancestors2.contains(o1)) {
				return o1;
			}
		}
		return null;
	}

	public List<ShapeNode<?>> getAncestors(ShapeNode<?> node) {
		List<ShapeNode<?>> ancestors = new ArrayList<>();
		ancestors.add(node);
		if (graph.getPredecessorCount(node) > 0) {
			for (ShapeNode<?> predecessor : graph.getPredecessors(node)) {
				ancestors.addAll(getAncestors(predecessor));
			}
		}
		return ancestors;
	}

	public boolean isChildOf(ShapeNode<?> child, ContainerNode<?, ?> parent) {
		if (child == parent)
			return true;

		if (graph.getPredecessorCount(child) > 0) {
			for (ShapeNode<?> predecessor : graph.getPredecessors(child)) {
				if (predecessor != null && isChildOf(predecessor, parent))
					return true;
			}
		}

		return false;
	}

	@Override
	public void computeLayout() {

		super.computeLayout();

		Map<ShapeNode<?>, Double> xMap = new HashMap<>();
		Map<ShapeNode<?>, Double> yMap = new HashMap<>();

		maxDepth = 0;
		for (ShapeNode<?> shapeNode : getLayoutedNodes()) {
			maxDepth = Math.max(maxDepth, getDepth(shapeNode));
			xMap.put(shapeNode, shapeNode.getX());
			yMap.put(shapeNode, shapeNode.getY());
		}

		// No global layout to launch

		if (animateLayout() && !layoutInProgress) {
			List<TranslationTransition> transitions = new ArrayList<>();
			for (ShapeNode<?> shapeNode : getLayoutedNodes()) {
				DianaPoint newLocation = locationForNode(shapeNode);
				DianaPoint oldLocation = new DianaPoint(xMap.get(shapeNode), yMap.get(shapeNode));
				if (!newLocation.equals(oldLocation)) {
					transitions.add(new TranslationTransition(shapeNode, oldLocation, newLocation));
				}
			}

			if (transitions.size() > 0) {
				AnimationImpl.performTransitions(transitions, getAnimationStepsNumber(), getContainerNode().getDrawing());
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getPropertyName().equals(TreeBasedLayoutManagerSpecification.HORIZONTAL_ALIGNEMENT_KEY)) {
			invalidate();
			doLayout(true);
		}
		else if (evt.getPropertyName().equals(TreeBasedLayoutManagerSpecification.VERTICAL_ALIGNEMENT_KEY)) {
			invalidate();
			doLayout(true);
		}
	}

	protected int getDepth(ShapeNode<?> node) {
		if (forest.getPredecessorCount(node) > 0) {
			int returned = -1;
			List<ShapeNode<?>> predecessors = new ArrayList<>(forest.getPredecessors(node));
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

	@Override
	public HorizontalTextAlignment getHorizontalAlignment() {
		return getLayoutManagerSpecification().getHorizontalAlignment();
	}

	@Override
	public VerticalTextAlignment getVerticalAlignment() {
		return getLayoutManagerSpecification().getVerticalAlignment();
	}

}
