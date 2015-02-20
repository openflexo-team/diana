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

package org.openflexo.fge.impl;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGELayoutManager;
import org.openflexo.fge.FGELayoutManagerSpecification;
import org.openflexo.fge.FGELayoutManagerSpecification.DraggingMode;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.FGEGraphics;

/**
 * Default implementation for {@link FGELayoutManager}
 * 
 * @author sylvain
 * 
 */
public abstract class FGELayoutManagerImpl<LMS extends FGELayoutManagerSpecification<?>, O> extends FGEObjectImpl implements
		FGELayoutManager<LMS, O> {

	private boolean invalidated = true;

	private final List<ShapeNode<?>> nodes;

	public FGELayoutManagerImpl() {
		nodes = new ArrayList<ShapeNode<?>>();
	}

	public List<ShapeNode<?>> getNodes() {
		return nodes;
	}

	/**
	 * Called to invalidate the whole layout<br>
	 * All contained {@link ShapeNode} will be invalidated
	 */
	@Override
	public void invalidate() {
		invalidated = true;
		for (DrawingTreeNode<?, ?> dtn : getContainerNode().getChildNodes()) {
			if (dtn instanceof ShapeNode) {
				if (((ShapeNode<O>) dtn).getLayoutManager() == this) {
					invalidate((ShapeNode<O>) dtn);
				}
			}
		}
	}

	/**
	 * Called to invalidate a {@link ShapeNode}
	 * 
	 * @param node
	 */
	@Override
	public void invalidate(ShapeNode<?> node) {
		if (node.isLayoutValidated()) {
			System.out.println("Invalidate: " + node);
			node.invalidateLayout();
		}

		// If layout is declared as fully layouted (move or resize of one node might invalidate the whole container)
		// Invalidate all nodes
		if (isFullyLayouted()) {
			for (ShapeNode<?> dtn : nodes) {
				if (dtn.isLayoutValidated()) {
					System.out.println("invalidate " + dtn);
					invalidate(dtn);
				}
			}
		}

	}

	protected boolean layoutInProgress = false;

	@Override
	public void doLayout(boolean force) {

		computeLayout();

		layoutInProgress = true;
		for (ShapeNode<?> node : nodes) {
			if (node.isValid()) {
				doLayout(node, force);
			}
		}
		layoutInProgress = false;

	}

	/**
	 * Return flag indicating if layout is in progress
	 * 
	 * @return
	 */
	@Override
	public boolean isLayoutInProgress() {
		return layoutInProgress;
	}

	/**
	 * Perform layout for supplied {@link ShapeNode}, if this node is invalidated<br>
	 * If node was not invalidated, simply return
	 * 
	 * @param node
	 */
	@Override
	public final void doLayout(ShapeNode<?> node, boolean force) {
		if (!node.isLayoutValidated() || force) {
			performLayout(node);
		}
	}

	/**
	 * Perform layout for supplied {@link ShapeNode}
	 * 
	 * @param node
	 */
	protected abstract void performLayout(ShapeNode<?> node);

	/**
	 * Hook used to detect that a shape has moved from a location to another location<br>
	 * Default implementation does nothing.
	 * 
	 * @param oldLocation
	 * @param location
	 */
	@Override
	public void shapeMoved(FGEPoint oldLocation, FGEPoint location) {
	}

	/**
	 * Called at the beginning of layout computation for the whole container
	 */
	protected void initLayout() {
		retrieveNodesToLayout();
	}

	/**
	 * Compute the whole layout, do not place elements
	 */
	@Override
	public void computeLayout() {

		boolean wasInvalidated = invalidated;
		if (wasInvalidated) {
			initLayout();
			invalidated = false;
		}

	}

	/**
	 * Internally used to retrieve in the container all nodes which are to be layouted
	 */
	private void retrieveNodesToLayout() {
		nodes.clear();
		for (DrawingTreeNode<?, ?> dtn : getContainerNode().getChildNodes()) {
			if (dtn instanceof ShapeNode) {
				if (((ShapeNode<O>) dtn).getLayoutManager() == this) {
					nodes.add((ShapeNode<O>) dtn);
				}
			}
		}
	}

	@Override
	public void randomLayout(boolean force) {

		// computeLayout();

		layoutInProgress = true;
		for (ShapeNode<?> node : nodes) {
			if (node.isValid()) {
				int randX = (new Random()).nextInt((int) getContainerNode().getWidth());
				int randY = (new Random()).nextInt((int) getContainerNode().getHeight());
				node.setLocation(new FGEPoint(randX, randY));
			}
		}
		layoutInProgress = false;

	}

	/**
	 * Return flag indicating whether this layout manager supports autolayout
	 * 
	 * @return
	 */
	@Override
	public final boolean supportAutolayout() {
		return getLayoutManagerSpecification().supportAutolayout();
	}

	/**
	 * Return flag indicating whether this layout manager supports decoration painting<br>
	 * 
	 * @return
	 */
	@Override
	public final boolean supportDecoration() {
		return getLayoutManagerSpecification().supportDecoration();
	}

	/**
	 * Return flag indicating whether layout manager decoration is to be paint<br>
	 * Note that this is relevant only if this layout manager supports decoration painting
	 * 
	 * @return
	 */
	@Override
	public final Boolean paintDecoration() {
		return getLayoutManagerSpecification().paintDecoration();
	}

	/**
	 * Return flag indicating whether layout should be performed using animation
	 * 
	 * @return
	 */
	public final boolean animateLayout() {
		return getLayoutManagerSpecification().animateLayout() /*&& !layoutInProgress*/;
	}

	/**
	 * Return number of steps to be performed for animations
	 * 
	 * @return
	 */
	public int getAnimationStepsNumber() {
		return getLayoutManagerSpecification().getAnimationStepsNumber();
	}

	/**
	 * Called to paint decoration
	 * 
	 * @param g
	 */
	public void paintDecoration(FGEGraphics g) {
	}

	@Override
	public DraggingMode getDraggingMode() {
		return getLayoutManagerSpecification().getDraggingMode();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// System.out.println("Received " + evt.getPropertyName() + " with " + evt);
		if (evt.getPropertyName().equals(FGELayoutManagerSpecification.DRAGGING_MODE_KEY)) {
			// Nothing to do yet
		} else if (evt.getPropertyName().equals(FGELayoutManagerSpecification.PAINT_DECORATION_KEY)) {
			// TODO: this is really brutal for a simple repaint request ???
			getContainerNode().invalidate();
			getContainerNode().getDrawing().updateGraphicalObjectsHierarchy();
		}
	}

	@Override
	public void attemptToPlaceNodeManually(ShapeNode<?> node) {
		System.out.println("On essaie de fixer " + node.getText() + " a " + node.getLocation());
	}

	/**
	 * Return {@link ControlArea} managed by this layout manager<br>
	 * Default value is null
	 * 
	 * @return
	 */
	@Override
	public List<ControlArea<?>> getControlAreas() {
		return null;
	}

}
