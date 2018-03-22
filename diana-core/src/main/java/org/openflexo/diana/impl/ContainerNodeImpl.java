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

package org.openflexo.diana.impl;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.openflexo.connie.DataBinding;
import org.openflexo.diana.ContainerGraphicalRepresentation;
import org.openflexo.diana.DianaLayoutManager;
import org.openflexo.diana.DianaLayoutManagerSpecification;
import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.Drawing.GraphNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.GRBinding;
import org.openflexo.diana.GRBinding.ConnectorGRBinding;
import org.openflexo.diana.GRBinding.ContainerGRBinding;
import org.openflexo.diana.GRBinding.GeometricGRBinding;
import org.openflexo.diana.GRBinding.GraphGRBinding;
import org.openflexo.diana.GRBinding.ShapeGRBinding;
import org.openflexo.diana.GRProperty;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaGeometricObject;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaSteppedDimensionConstraint;
import org.openflexo.diana.graph.DianaGraph;
import org.openflexo.diana.graphics.DianaGraphics;
import org.openflexo.diana.notifications.NodeAdded;
import org.openflexo.diana.notifications.NodeRemoved;
import org.openflexo.diana.notifications.ObjectHasResized;
import org.openflexo.diana.notifications.ObjectResized;
import org.openflexo.diana.notifications.ObjectWillResize;
import org.openflexo.toolbox.ConcatenedList;

public abstract class ContainerNodeImpl<O, GR extends ContainerGraphicalRepresentation> extends DrawingTreeNodeImpl<O, GR>
		implements ContainerNode<O, GR> {

	private static final Logger logger = Logger.getLogger(ContainerNodeImpl.class.getPackage().getName());

	private List<DrawingTreeNodeImpl<?, ?>> childNodes;

	private boolean isResizing = false;
	private boolean isCheckingDimensionConstraints = false;

	private final List<DianaLayoutManager<?, O>> layoutManagers;

	protected ContainerNodeImpl(DrawingImpl<?> drawing, O drawable, ContainerGRBinding<O, GR> grBinding,
			ContainerNodeImpl<?, ?> parentNode) {
		super(drawing, drawable, grBinding, parentNode);
		childNodes = new ArrayList<>();
		layoutManagers = new ArrayList<>();
		/*for (DianaLayoutManagerSpecification<?> spec : getGraphicalRepresentation().getLayoutManagerSpecifications()) {
			DianaLayoutManager<?, O> layoutManager = (DianaLayoutManager<?, O>) spec.makeLayoutManager(this);
			layoutManagers.add(layoutManager);
		}*/
		updateLayoutManagers();

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);

		if (evt.getSource() == getGraphicalRepresentation()) {
			if (evt.getPropertyName().equals(ContainerGraphicalRepresentation.LAYOUT_MANAGER_SPECIFICATIONS_KEY)) {
				// Detected that the layout manager specifications have changed
				if (evt.getNewValue() == null) {
					System.out.println("Les LMS changent: DELETE de " + evt.getOldValue());
				}
				else if (evt.getOldValue() == null) {
					System.out.println("Les LMS changent: ADD de " + evt.getNewValue());
				}
				updateLayoutManagers();
			}
		}
	}

	/**
	 * Internally called to update instantiated {@link DianaLayoutManager} from {@link DianaLayoutManagerSpecification} list as defined in
	 * {@link ContainerGraphicalRepresentation}
	 */
	private void updateLayoutManagers() {
		DianaLayoutManager<?, O> oldDefaultLayoutManager = getDefaultLayoutManager();
		List<DianaLayoutManager<?, O>> lmToRemove = new ArrayList<>(getLayoutManagers());
		for (DianaLayoutManagerSpecification<?> spec : getGraphicalRepresentation().getLayoutManagerSpecifications()) {
			if (spec.isDeleted())
				continue;

			boolean found = false;
			for (DianaLayoutManager<?, ?> lm : getLayoutManagers()) {
				if (lm.getLayoutManagerSpecification() == spec) {
					lmToRemove.remove(lm);
					found = true;
					break;
				}
			}
			if (!found) {
				DianaLayoutManager<?, O> newLayoutManager = (DianaLayoutManager<?, O>) spec.makeLayoutManager(this);
				layoutManagers.add(newLayoutManager);
			}
		}
		for (DianaLayoutManager<?, ?> lm : new ArrayList<>(lmToRemove)) {
			lm.delete();
			layoutManagers.remove(lm);
		}

		// Now select nodes
		/*if (getLayoutManagers().size() == 1) {
			for (ShapeNode<?> shapeNode : getShapeNodes()) {
				shapeNode.setLayoutManager(getDefaultLayoutManager());
			}
			getDefaultLayoutManager().invalidate();
			getDefaultLayoutManager().doLayout(true);
		}*/

		getPropertyChangeSupport().firePropertyChange("layoutManagers", null, getLayoutManagers());
		getPropertyChangeSupport().firePropertyChange("defaultLayoutManager", oldDefaultLayoutManager, getDefaultLayoutManager());

		for (DianaLayoutManager<?, O> lm : getLayoutManagers()) {
			notifyNodeLayoutDecorationChanged(lm);
		}

		if (getLayoutManagers().isEmpty()) {
			// Big hack to repaint decoration (more precisely do not paint it anymore) for a removed layout
			getPropertyChangeSupport().firePropertyChange(LAYOUT_DECORATION_KEY, false, true);
		}

	}

	/**
	 * Convenient method used to retrieve border property value
	 */
	@Override
	public List<DianaLayoutManagerSpecification<?>> getLayoutManagerSpecifications() {
		return getPropertyValue(ContainerGraphicalRepresentation.LAYOUT_MANAGER_SPECIFICATIONS);
	}

	/**
	 * Return DianaLayoutManager identified by identifier
	 * 
	 * @param identifier
	 * @return
	 */
	@Override
	public DianaLayoutManager<?, O> getLayoutManager(String identifier) {
		if (identifier == null) {
			return null;
		}
		for (DianaLayoutManager<?, O> layoutManager : layoutManagers) {
			if (identifier.equals(layoutManager.getLayoutManagerSpecification().getIdentifier())) {
				return layoutManager;
			}
		}
		return null;
	}

	@Override
	public List<DianaLayoutManager<?, O>> getLayoutManagers() {
		return layoutManagers;
	}

	/**
	 * Return default DianaLayoutManager (the first one found)
	 * 
	 * @return
	 */
	@Override
	public DianaLayoutManager<?, O> getDefaultLayoutManager() {
		if (layoutManagers.size() > 0) {
			return layoutManagers.get(0);
		}
		return null;
	}

	@Override
	public ShapeNode<?> getTopLevelShapeGraphicalRepresentation(DianaPoint p) {
		return getTopLevelShapeGraphicalRepresentation(this, p);
	}

	private ShapeNode<?> getTopLevelShapeGraphicalRepresentation(ContainerNode<?, ?> container, DianaPoint p) {

		List<ShapeNode<?>> enclosingShapes = new ArrayList<>();

		for (DrawingTreeNode<?, ?> dtn : container.getChildNodes()) {
			if (dtn instanceof ShapeNode) {
				ShapeNode<?> child = (ShapeNode<?>) dtn;
				if (child.getShape().getShape().containsPoint(DianaUtils.convertNormalizedPoint(this, p, child))) {
					enclosingShapes.add(child);
				}
				else {
					// Look if we are not contained in a child shape outside current shape
					ShapeNode<?> insideFocusedShape = getTopLevelShapeGraphicalRepresentation(child, p);
					if (insideFocusedShape != null && insideFocusedShape instanceof ShapeGraphicalRepresentation) {
						enclosingShapes.add(insideFocusedShape);
					}
				}
			}
		}

		if (enclosingShapes.size() > 0) {

			Collections.sort(enclosingShapes, new Comparator<ShapeNode<?>>() {
				@Override
				public int compare(ShapeNode<?> o1, ShapeNode<?> o2) {
					if (o2.getGraphicalRepresentation().getLayer() == o1.getGraphicalRepresentation().getLayer()
							&& o1.getParentNode() != null && o1.getParentNode() == o2.getParentNode()) {
						return o1.getParentNode().getOrder(o1, o2);
					}
					return o2.getGraphicalRepresentation().getLayer() - o1.getGraphicalRepresentation().getLayer();
				}
			});

			ShapeNode<?> focusedShape = enclosingShapes.get(0);

			ShapeNode<?> insideFocusedShape = getTopLevelShapeGraphicalRepresentation(focusedShape, p);

			if (insideFocusedShape != null) {
				return insideFocusedShape;
			}
			else {
				return focusedShape;
			}
		}

		return null;

	}

	@Override
	public List<ShapeNodeImpl<?>> getShapeNodes() {
		List<ShapeNodeImpl<?>> returned = new ArrayList<>();

		List<DrawingTreeNodeImpl<?, ?>> children = getChildNodes();
		if (children != null) {
			for (DrawingTreeNodeImpl<?, ?> child : getChildNodes()) {
				if (child instanceof ShapeNodeImpl) {
					returned.add((ShapeNodeImpl) child);
				}
			}
		}
		else {
			logger.warning("INVESTIGATE: found a container with no child");
		}
		return returned;
	}

	@Override
	public List<DrawingTreeNodeImpl<?, ?>> getChildNodes() {
		return childNodes;
	}

	@Override
	public void addChild(DrawingTreeNode<?, ?> aChildNode) {
		if (aChildNode == null) {
			logger.warning("Cannot add null node");
			return;
		}
		if (childNodes.contains(aChildNode)) {
			logger.warning("Node already present");
		}
		else {
			// System.out.println("Add child " + aChildNode + " as child as " + this);
			((DrawingTreeNodeImpl<?, ?>) aChildNode).setParentNode(this);
			childNodes.add((DrawingTreeNodeImpl<?, ?>) aChildNode);
		}
	}

	@Override
	public void removeChild(DrawingTreeNode<?, ?> aChildNode) {
		if (aChildNode == null) {
			logger.warning("Cannot remove null node");
			return;
		}
		if (childNodes.contains(aChildNode)) {
			childNodes.remove(aChildNode);
		}
		else {
			DrawingImpl.logger.warning("Cannot remove node: not present");
		}
		notifyNodeRemoved(aChildNode);
		// aChildNode.delete();
	}

	@Override
	public int getOrder(DrawingTreeNode<?, ?> child1, DrawingTreeNode<?, ?> child2) {
		if (!getChildNodes().contains(child1)) {
			return 0;
		}
		if (!getChildNodes().contains(child2)) {
			return 0;
		}
		return getChildNodes().indexOf(child1) - getChildNodes().indexOf(child2);
	}

	@Override
	public void validate() {
		super.validate();
		performRelayout(false);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		for (DrawingTreeNode<?, ?> dtn : childNodes) {
			dtn.invalidate();
		}
	}

	/**
	 * Recursively delete this DrawingTreeNode and all its descendants
	 */
	@Override
	public boolean delete() {
		if (!isDeleted()) {
			if (childNodes != null) {
				for (DrawingTreeNode<?, ?> n : new ArrayList<DrawingTreeNode<?, ?>>(childNodes)) {
					removeChild(n);
				}
				childNodes.clear();
			}

			childNodes = null;

			return super.delete();
		}
		return false;
	}

	@Override
	public void notifyNodeLayoutDecorationChanged(DianaLayoutManager<?, O> layoutManager) {
		getPropertyChangeSupport().firePropertyChange(LAYOUT_DECORATION_KEY, Boolean.valueOf(!layoutManager.paintDecoration()),
				Boolean.valueOf(layoutManager.paintDecoration()));

	}

	@Override
	public void notifyNodeAdded(DrawingTreeNode<?, ?> addedNode) {

		if ((addedNode instanceof ShapeNode)
				&& StringUtils.isEmpty(((ShapeNode<?>) addedNode).getGraphicalRepresentation().getLayoutManagerIdentifier())
				&& getLayoutManagers().size() == 1) {
			// Only one layout manager defined in container
			// This new ShapeNode has no layout manager, we force the default one

			System.out.println("OK, pour le nouveau noeud, je le mets a " + getLayoutManagers().get(0));

			((ShapeNode<?>) addedNode).getGraphicalRepresentation().setLayoutManagerIdentifier(getLayoutManagers().get(0).getIdentifier());

			getLayoutManagers().get(0).invalidate();
			getLayoutManagers().get(0).doLayout(true);
		}

		if (addedNode.getGraphicalRepresentation() != null) {
			addedNode.getGraphicalRepresentation().updateBindingModel();
		}
		notifyObservers(new NodeAdded(addedNode, this));

		performRelayout(true);
	}

	@Override
	public void notifyNodeRemoved(DrawingTreeNode<?, ?> removedNode) {
		if (removedNode.getGraphicalRepresentation() != null) {
			removedNode.getGraphicalRepresentation().updateBindingModel();
		}
		notifyObservers(new NodeRemoved(removedNode, this));

		if (!getDrawing().isDeleting()) {
			performRelayout(true);
		}

	}

	private void performRelayout(boolean invalidate) {
		for (DianaLayoutManager<?, O> lm : getLayoutManagers()) {
			if (getDrawing().isUpdatingGraphicalObjectsHierarchy()) {
				getDrawing().invokeLayoutAfterGraphicalObjectsHierarchyUpdating(lm);
			}
			else {
				lm.invalidate();
				lm.doLayout(true);
			}
		}
	}

	@Override
	public <O2> boolean hasShapeFor(ShapeGRBinding<O2> binding, O2 aDrawable) {
		return getShapeFor(binding, aDrawable) != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O2> ShapeNode<O2> getShapeFor(ShapeGRBinding<O2> binding, O2 aDrawable) {
		for (DrawingTreeNode<?, ?> child : childNodes) {
			if (child instanceof ShapeNode && child.getGRBinding() == binding && child.getDrawable() == aDrawable) {
				return (ShapeNode<O2>) child;
			}
		}
		return null;
	}

	@Override
	public <G extends DianaGraph> boolean hasGraphFor(GraphGRBinding<G> binding, G aDrawable) {
		return getGraphFor(binding, aDrawable) != null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <G extends DianaGraph> GraphNode<G> getGraphFor(GraphGRBinding<G> binding, G aDrawable) {
		for (DrawingTreeNode<?, ?> child : childNodes) {
			if (child instanceof GraphNode && child.getGRBinding() == binding && child.getDrawable() == aDrawable) {
				return (GraphNode<G>) child;
			}
		}
		return null;
	}

	@Override
	public <O2> boolean hasConnectorFor(ConnectorGRBinding<O2> binding, O2 aDrawable) {
		return getConnectorFor(binding, aDrawable) != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O2> ConnectorNode<O2> getConnectorFor(ConnectorGRBinding<O2> binding, O2 aDrawable) {
		for (DrawingTreeNode<?, ?> child : childNodes) {
			if (child instanceof ConnectorNode && child.getGRBinding() == binding && child.getDrawable() == aDrawable) {
				return (ConnectorNode<O2>) child;
			}
		}
		return null;
	}

	@Override
	public <O2> boolean hasGeometricObjectFor(GeometricGRBinding<O2> binding, O2 aDrawable) {
		return getGeometricObjectFor(binding, aDrawable) != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O2> GeometricNode<O2> getGeometricObjectFor(GeometricGRBinding<O2> binding, O2 aDrawable) {
		for (DrawingTreeNode<?, ?> child : childNodes) {
			if (child instanceof GeometricNode && child.getGRBinding() == binding && child.getDrawable() == aDrawable) {
				return (GeometricNode<O2>) child;
			}
		}
		return null;
	}

	// ********************************************
	// Size management
	// ********************************************

	@Override
	public double getWidth() {
		return getPropertyValue(ContainerGraphicalRepresentation.WIDTH);
	}

	@Override
	public final void setWidth(double aValue) {
		if (aValue != getWidth()) {
			DianaDimension newDimension = new DianaDimension(aValue, getHeight());
			updateSize(newDimension);
		}
	}

	protected void setWidthNoNotification(double aValue) {
		setPropertyValue(ContainerGraphicalRepresentation.WIDTH, aValue);
	}

	@Override
	public double getHeight() {
		return getPropertyValue(ContainerGraphicalRepresentation.HEIGHT);
	}

	@Override
	public final void setHeight(double aValue) {
		if (aValue != getHeight()) {
			DianaDimension newDimension = new DianaDimension(getWidth(), aValue);
			updateSize(newDimension);
		}
	}

	public void setHeightNoNotification(double aValue) {
		setPropertyValue(ContainerGraphicalRepresentation.HEIGHT, aValue);
	}

	@Override
	public DianaDimension getSize() {
		return new DianaDimension(getWidth(), getHeight());
	}

	@Override
	public void setSize(DianaDimension newSize) {
		updateSize(newSize);
	}

	public double getMinimalWidth() {
		return getPropertyValue(ContainerGraphicalRepresentation.MINIMAL_WIDTH);
	}

	protected void setMinimalWidth(double aValue) {
		setPropertyValue(ContainerGraphicalRepresentation.MINIMAL_WIDTH, aValue);
	}

	public double getMaximalWidth() {
		return getPropertyValue(ContainerGraphicalRepresentation.MAXIMAL_WIDTH);
	}

	protected void setMaximalWidth(double aValue) {
		setPropertyValue(ContainerGraphicalRepresentation.MAXIMAL_WIDTH, aValue);
	}

	public double getMinimalHeight() {
		return getPropertyValue(ContainerGraphicalRepresentation.MINIMAL_HEIGHT);
	}

	protected void setMinimalHeight(double aValue) {
		setPropertyValue(ContainerGraphicalRepresentation.MINIMAL_HEIGHT, aValue);
	}

	public double getMaximalHeight() {
		return getPropertyValue(ContainerGraphicalRepresentation.MAXIMAL_HEIGHT);
	}

	protected void setMaximalHeight(double aValue) {
		setPropertyValue(ContainerGraphicalRepresentation.MAXIMAL_HEIGHT, aValue);
	}

	protected boolean getAdjustMinimalWidthToLabelWidth() {
		return getPropertyValue(ContainerGraphicalRepresentation.ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH);
	}

	protected void setAdjustMinimalWidthToLabelWidth(boolean adjustMinimalWidthToLabelWidth) {
		setPropertyValue(ContainerGraphicalRepresentation.ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH, adjustMinimalWidthToLabelWidth);
	}

	protected boolean getAdjustMinimalHeightToLabelHeight() {
		return getPropertyValue(ContainerGraphicalRepresentation.ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT);
	}

	protected void setAdjustMinimalHeightToLabelHeight(boolean adjustMinimalHeightToLabelHeight) {
		setPropertyValue(ContainerGraphicalRepresentation.ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT, adjustMinimalHeightToLabelHeight);
	}

	protected boolean getAdjustMaximalWidthToLabelWidth() {
		return getPropertyValue(ContainerGraphicalRepresentation.ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH);
	}

	protected void setAdjustMaximalWidthToLabelWidth(boolean adjustMaximalWidthToLabelWidth) {
		setPropertyValue(ContainerGraphicalRepresentation.ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH, adjustMaximalWidthToLabelWidth);
	}

	protected boolean getAdjustMaximalHeightToLabelHeight() {
		return getPropertyValue(ContainerGraphicalRepresentation.ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT);
	}

	protected void setAdjustMaximalHeightToLabelHeight(boolean adjustMaximalHeightToLabelHeight) {
		setPropertyValue(ContainerGraphicalRepresentation.ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT, adjustMaximalHeightToLabelHeight);
	}

	protected DianaSteppedDimensionConstraint getDimensionConstraintStep() {
		return getPropertyValue(ContainerGraphicalRepresentation.DIMENSION_CONSTRAINT_STEP);
	}

	protected void setDimensionConstraintStep(DianaSteppedDimensionConstraint dimensionConstraintStep) {
		setPropertyValue(ContainerGraphicalRepresentation.DIMENSION_CONSTRAINT_STEP, dimensionConstraintStep);
	}

	protected DimensionConstraints getDimensionConstraints() {
		return getPropertyValue(ContainerGraphicalRepresentation.DIMENSION_CONSTRAINTS);
	}

	protected void setDimensionConstraints(DimensionConstraints dimensionConstraints) {
		setPropertyValue(ContainerGraphicalRepresentation.DIMENSION_CONSTRAINTS, dimensionConstraints);
	}

	/**
	 * General method called to update size of a ContainerNode
	 * 
	 * @param requestedSize
	 */
	private void updateSize(DianaDimension requestedSize) {

		// If no value supplied, just ignore
		if (requestedSize == null) {
			return;
		}

		// If value is same, also ignore
		if (requestedSize.equals(getSize())) {
			return;
		}

		// Prelude of update, first select new size respecting contextual constraints
		DianaDimension newSize = getConstrainedSize(requestedSize);
		if (!newSize.equals(requestedSize)) {
			logger.info("Dimension constraints force " + requestedSize + " to be " + newSize);
		}

		DianaDimension oldSize = getSize();
		if (!newSize.equals(oldSize)) {
			double oldWidth = getWidth();
			double oldHeight = getHeight();
			/*if (isParentLayoutedAsContainer()) {
				setLocationForContainerLayout(newLocation);
			} else {*/
			setWidthNoNotification(newSize.width);
			setHeightNoNotification(newSize.height);
			// }
			notifyObjectResized(oldSize);
			notifyAttributeChanged(ContainerGraphicalRepresentation.WIDTH, oldWidth, getWidth());
			notifyAttributeChanged(ContainerGraphicalRepresentation.HEIGHT, oldHeight, getHeight());
			/*if (!isFullyContainedInContainer()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("setLocation() lead shape going outside it's parent view");
				}
			}*/
		}

		/*if (newSize == null) {
			return;
		}
		// Preventing size from being negative or equals to 0
		if (newSize.width <= 0) {
			newSize.width = DianaGeometricObject.EPSILON;
		}
		if (newSize.height <= 0) {
			newSize.height = DianaGeometricObject.EPSILON;
		}
		DianaDimension oldSize = getSize();
		if (!newSize.equals(oldSize)) {
			double oldWidth = getWidth();
			double oldHeight = getHeight();
			setWidthNoNotification(newSize.width);
			setHeightNoNotification(newSize.height);
			if (hasFloatingLabel()) {
				if (getGraphicalRepresentation().getAbsoluteTextX() >= 0) {
					if (getGraphicalRepresentation().getAbsoluteTextX() < getWidth()) {
						getGraphicalRepresentation().setAbsoluteTextX(
								getGraphicalRepresentation().getAbsoluteTextX() / oldSize.width * getWidth());
					} else {
						getGraphicalRepresentation().setAbsoluteTextX(
								getGraphicalRepresentation().getAbsoluteTextX() + getWidth() - oldSize.width);
					}
				}
				if (getGraphicalRepresentation().getAbsoluteTextY() >= 0) {
					if (getGraphicalRepresentation().getAbsoluteTextY() < getHeight()) {
						getGraphicalRepresentation().setAbsoluteTextY(
								getGraphicalRepresentation().getAbsoluteTextY() / oldSize.height * getHeight());
					} else {
						getGraphicalRepresentation().setAbsoluteTextY(
								getGraphicalRepresentation().getAbsoluteTextY() + getHeight() - oldSize.height);
					}
				}
			}
			checkAndUpdateDimensionBoundsIfRequired();
			if (isParentLayoutedAsContainer()) {
				((ShapeNodeImpl<?>) getParentNode()).checkAndUpdateDimensionIfRequired();
			}
			notifyObjectResized(oldSize);
			notifyAttributeChanged(ContainerGraphicalRepresentation.WIDTH, oldWidth, getWidth());
			notifyAttributeChanged(ContainerGraphicalRepresentation.HEIGHT, oldHeight, getHeight());
			// getGraphicalRepresentation().getShape().notifyObjectResized();
		}*/
	}

	/**
	 * Notify that the object just resized
	 */
	@Override
	public void notifyObjectResized() {
		notifyObjectResized(null);
	}

	/**
	 * Notify that the object just resized
	 */
	@Override
	public void notifyObjectResized(DianaDimension oldSize) {
		notifyObservers(new ObjectResized(oldSize, getSize()));
	}

	/**
	 * Notify that the object will be resized
	 */
	@Override
	public void notifyObjectWillResize() {
		isResizing = true;
		notifyObservers(new ObjectWillResize());
	}

	/**
	 * Notify that the object resizing has finished (take care that this just notify END of resize, this should NOT be used to notify a
	 * resizing: use notifyObjectResize() instead)
	 */
	@Override
	public void notifyObjectHasResized() {
		isResizing = false;
		for (DrawingTreeNode<?, ?> gr : getChildNodes()) {
			if (gr instanceof ShapeNodeImpl) {
				((ShapeNodeImpl<?>) gr).checkAndUpdateLocationIfRequired();
			}
		}
		notifyObservers(new ObjectHasResized());
	}

	@Override
	public boolean isResizing() {
		return isResizing;
	}

	/**
	 * Calling this method forces Diana to check (and eventually update) dimension of current graphical representation according defined
	 * dimension constraints
	 */
	protected void checkAndUpdateDimensionIfRequired() {
		setSize(getSize());
	}

	private DianaDimension getConstrainedSize(DianaDimension requestedSize) {

		if (isCheckingDimensionConstraints || labelMetricsProvider == null) {
			return requestedSize;
		}

		try {

			// if (getAdaptBoundsToContents()) {
			// extendBoundsToHostContents();
			// }

			isCheckingDimensionConstraints = true;

			// DianaRectangle requiredBounds = getRequiredBoundsForContents();

			DianaDimension newDimension = new DianaDimension(requestedSize.getWidth(), requestedSize.getHeight());

			// Preventing size from being negative or equals to 0
			if (newDimension.width <= 0) {
				newDimension.width = DianaGeometricObject.EPSILON;
			}
			if (newDimension.height <= 0) {
				newDimension.height = DianaGeometricObject.EPSILON;
			}

			// double minWidth = (getAdaptBoundsToContents() ? Math.max(getMinimalWidth(), requiredBounds.width) : getMinimalWidth());
			// double minHeight = (getAdaptBoundsToContents() ? Math.max(getMinimalHeight(), requiredBounds.height) : getMinimalHeight());
			double minWidth = getMinimalWidth();
			double minHeight = getMinimalHeight();
			double maxWidth = getMaximalWidth();
			double maxHeight = getMaximalHeight();

			if (hasContainedLabel()) {
				Dimension normalizedLabelSize = getNormalizedLabelSize();
				int labelWidth = normalizedLabelSize.width;
				int labelHeight = normalizedLabelSize.height;
				DianaDimension requiredLabelDimension = getRequiredLabelSize();
				double rh = requiredLabelDimension.getHeight();
				double rw = requiredLabelDimension.getWidth();
				double requiredWidth = Math.max(rw, labelWidth);
				double requiredHeight = Math.max(rh, labelHeight);

				if (getAdjustMinimalWidthToLabelWidth()) {
					minWidth = Math.max(requiredWidth, minWidth);
				}

				if (getGraphicalRepresentation().getAdjustMinimalHeightToLabelHeight()) {
					minHeight = Math.max(requiredHeight, minHeight);
				}

				if (getGraphicalRepresentation().getAdjustMaximalWidthToLabelWidth()) {
					maxWidth = Math.min(requiredWidth, maxWidth);
				}

				if (getGraphicalRepresentation().getAdjustMaximalHeightToLabelHeight()) {
					maxHeight = Math.min(requiredHeight, maxHeight);
				}
			}

			if (minWidth > maxWidth) {
				logger.warning("Minimal width > maximal width, cannot proceed");
			}
			else {
				if (newDimension.width < minWidth) {
					newDimension.width = minWidth;
				}
				if (newDimension.width > maxWidth) {
					newDimension.width = maxWidth;
				}
			}
			if (minHeight > maxHeight) {
				logger.warning("Minimal height > maximal height, cannot proceed");
			}
			else {
				if (newDimension.height < minHeight) {
					newDimension.height = minHeight;
				}
				if (newDimension.height > maxHeight) {
					newDimension.height = maxHeight;
				}
			}

			boolean useStepDimensionConstraints = getDimensionConstraints() == DimensionConstraints.STEP_CONSTRAINED
					&& getDimensionConstraintStep() != null;

			if (useStepDimensionConstraints && hasContainedLabel()) {
				if (getAdjustMinimalWidthToLabelWidth() && getAdjustMaximalWidthToLabelWidth()) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Too many constraints on width! Cannot proceed.");
					}
					useStepDimensionConstraints = false;
				}
				if (getAdjustMinimalHeightToLabelHeight() && getAdjustMaximalHeightToLabelHeight()) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Too many constraints on height! Cannot proceed.");
					}
					useStepDimensionConstraints = false;
				}
			}

			if (useStepDimensionConstraints) {
				DianaDimension d = getDimensionConstraintStep().getNearestDimension(newDimension, minWidth, maxWidth, minHeight, maxHeight);
				newDimension.width = d.width;
				newDimension.height = d.height;
			}

			return newDimension;

		} finally {
			isCheckingDimensionConstraints = false;
		}
	}

	@Override
	public Dimension getNormalizedLabelSize() {
		if (labelMetricsProvider != null) {
			return labelMetricsProvider.getScaledPreferredDimension(1.0);
		}
		else {
			return new Dimension(0, 0);
		}
	}

	@Override
	public Rectangle getNormalizedLabelBounds() {
		Dimension normalizedLabelSize = getNormalizedLabelSize();
		Rectangle r = new Rectangle(getLabelLocation(1.0), normalizedLabelSize);
		return r;
	}

	@Override
	public DianaRectangle getRequiredBoundsForContents() {
		DianaRectangle requiredBounds = null;
		if (getChildNodes() == null) {
			return new DianaRectangle(getGraphicalRepresentation().getMinimalWidth() / 2,
					getGraphicalRepresentation().getMinimalHeight() / 2, getGraphicalRepresentation().getMinimalWidth(),
					getGraphicalRepresentation().getMinimalHeight());
		}
		for (DrawingTreeNode<?, ?> gr : getChildNodes()) {
			if (gr instanceof ShapeNode) {
				ShapeNodeImpl<?> shapeGR = (ShapeNodeImpl<?>) gr;
				DianaRectangle bounds = shapeGR.getBounds();
				if (shapeGR.hasText()) {
					Rectangle labelBounds = shapeGR.getNormalizedLabelBounds(); // getLabelBounds((new JLabel()), 1.0);
					DianaRectangle labelBounds2 = new DianaRectangle(labelBounds.x, labelBounds.y, labelBounds.width, labelBounds.height);
					bounds = bounds.rectangleUnion(labelBounds2);
				}

				if (requiredBounds == null) {
					requiredBounds = bounds;
				}
				else {
					requiredBounds = requiredBounds.rectangleUnion(bounds);
				}
			}
		}
		if (requiredBounds == null) {
			requiredBounds = new DianaRectangle(getGraphicalRepresentation().getMinimalWidth() / 2,
					getGraphicalRepresentation().getMinimalHeight() / 2, getGraphicalRepresentation().getMinimalWidth(),
					getGraphicalRepresentation().getMinimalHeight());
		}
		else {
			if (requiredBounds.width < getGraphicalRepresentation().getMinimalWidth()) {
				requiredBounds.x = requiredBounds.x - (int) ((getGraphicalRepresentation().getMinimalWidth() - requiredBounds.width) / 2.0);
				requiredBounds.width = getGraphicalRepresentation().getMinimalWidth();
			}
			if (requiredBounds.height < getGraphicalRepresentation().getMinimalHeight()) {
				requiredBounds.y = requiredBounds.y
						- (int) ((getGraphicalRepresentation().getMinimalHeight() - requiredBounds.height) / 2.0);
				requiredBounds.height = getGraphicalRepresentation().getMinimalHeight();
			}
		}

		return requiredBounds;
	}

	/**
	 * This method is called whenever it was detected that the value of a property declared as dynamic (specified by a {@link DataBinding}
	 * in {@link GRBinding}) has changed
	 * 
	 * @param parameter
	 * @param oldValue
	 * @param newValue
	 */
	@Override
	public <T> void fireDynamicPropertyChanged(GRProperty<T> parameter, T oldValue, T newValue) {
		super.fireDynamicPropertyChanged(parameter, oldValue, newValue);
		if (parameter == ShapeGraphicalRepresentation.WIDTH || parameter == ShapeGraphicalRepresentation.HEIGHT) {
			notifyObjectResized();
		}
	}

	@Override
	public void paint(DianaGraphics g) {

		for (DianaLayoutManager<?, ?> layoutManager : layoutManagers) {
			// System.out.println("Paint LayoutManager " + layoutManager + " supportDecoration=" + layoutManager.supportDecoration()
			// + " paintDecoration=" + layoutManager.paintDecoration());
			if (layoutManager.supportDecoration() && layoutManager.paintDecoration()) {
				((DianaLayoutManagerImpl<?, ?>) layoutManager).paintDecoration(g);
			}
			if (layoutManager.getControlAreas() != null) {
				for (ControlArea<?> ca : layoutManager.getControlAreas()) {
					ca.paint(g);
				}
			}
		}
	}

	private List<? extends ControlArea<?>> controlAreas = null;

	@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		if (controlAreas == null) {
			List<? extends ControlArea<?>> customControlAreas = getGRBinding().makeControlAreasFor(this);
			List<ControlArea<?>> layoutManagerAreas = null;

			if (getLayoutManagers().size() > 0) {
				for (DianaLayoutManager<?, O> layoutManager : getLayoutManagers()) {
					if (layoutManagerAreas == null) {
						layoutManagerAreas = layoutManager.getControlAreas();
					}
					else if (layoutManagerAreas instanceof ConcatenedList) {
						((ConcatenedList<ControlArea<?>>) layoutManagerAreas).addElementList(layoutManager.getControlAreas());
					}
					else {
						controlAreas = new ConcatenedList<>(controlAreas, layoutManager.getControlAreas());
					}
				}
			}
			if (customControlAreas != null && customControlAreas.size() > 0) {
				if (controlAreas == null) {
					controlAreas = customControlAreas;
				}
				else if (controlAreas instanceof ConcatenedList) {
					((ConcatenedList<ControlArea<?>>) controlAreas).addElementList(customControlAreas);
				}
				else {
					controlAreas = new ConcatenedList<>(controlAreas, customControlAreas);
				}
			}
			if (layoutManagerAreas != null && layoutManagerAreas.size() > 0) {
				if (controlAreas == null) {
					controlAreas = layoutManagerAreas;
				}
				else if (controlAreas instanceof ConcatenedList) {
					((ConcatenedList<ControlArea<?>>) controlAreas).addElementList(layoutManagerAreas);
				}
				else {
					controlAreas = new ConcatenedList<>(controlAreas, layoutManagerAreas);
				}
			}
		}
		return controlAreas;
	}

}
