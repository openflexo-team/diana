/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.diana.control;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.DianaPrefs;
import org.openflexo.diana.Drawing;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.DrawingTreeNodeIdentifier;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.control.notifications.ObjectAddedToSelection;
import org.openflexo.diana.control.notifications.ObjectRemovedFromSelection;
import org.openflexo.diana.control.notifications.SelectionCleared;
import org.openflexo.diana.cp.ConnectorAdjustingControlPoint;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.notifications.DrawingTreeNodeHierarchyRebuildEnded;
import org.openflexo.diana.notifications.DrawingTreeNodeHierarchyRebuildStarted;
import org.openflexo.diana.view.DianaView;
import org.openflexo.diana.view.DianaViewFactory;

/**
 * Represents a basic viewer of a {@link Drawing}<br>
 * 
 * The {@link Drawing} can only be edited with basic editing possibilities whose can be allowed or denied<br>
 * No interaction is provided on structural modifications of the drawing which should be externally controlled.<br>
 * 
 * A {@link DianaInteractiveViewer} manages a focused object and a selection.
 * 
 * @author sylvain
 * 
 * @param <M>
 */
public abstract class DianaInteractiveViewer<M, F extends DianaViewFactory<F, C>, C> extends AbstractDianaEditor<M, F, C> {

	private static final Logger logger = Logger.getLogger(DianaInteractiveViewer.class.getPackage().getName());

	private boolean shapesAreMovable = true;
	private boolean labelsAreEditable = false;
	private boolean objectsAreFocusable = true;
	private boolean objectsAreSelectable = true;
	private boolean objectsAreInspectable = true;

	// private EditorToolbox toolbox;
	// private DianaInspectors inspectors;

	private DrawingTreeNode<?, ?> focusedFloatingLabel;

	private List<DrawingTreeNode<?, ?>> focusedObjects;
	private List<DrawingTreeNode<?, ?>> expectedFocusedObjects;
	private List<DrawingTreeNode<?, ?>> selectedObjects;
	private List<ShapeNode<?>> selectedShapes;
	private List<DrawingTreeNode<?, ?>> selectedShapesAndGeometricNodes;
	private List<ConnectorNode<?>> selectedConnectors;
	private List<ContainerNode<?, ?>> selectedContainers;

	private DianaView<?, ?> currentlyEditedLabelView;
	private ControlArea<?> focusedControlArea;

	private MouseDragControl<DianaInteractiveViewer<?, ?, ?>> currentMouseDrag = null;

	private DianaPoint lastClickedPoint;
	private DrawingTreeNode<?, ?> lastSelectedNode;

	/**
	 * Stores selection as a persistent list of identified objects
	 */
	private List<DrawingTreeNodeIdentifier<?>> storedSelection;

	private boolean isRestoringSelection = false;

	public DianaInteractiveViewer(Drawing<M> aDrawing, DianaModelFactory factory, F dianaFactory, DianaToolFactory<C> toolFactory) {
		this(aDrawing, factory, dianaFactory, toolFactory, true, false, true, true, true);
	}

	public DianaInteractiveViewer(Drawing<M> aDrawing, DianaModelFactory factory, F dianaFactory, DianaToolFactory<C> toolFactory,
			boolean shapesAreMovable, boolean labelsAreEditable, boolean objectsAreFocusable, boolean objectsAreSelectable,
			boolean objectsAreInspectable) {
		super(aDrawing, factory, dianaFactory, toolFactory);
		this.shapesAreMovable = shapesAreMovable;
		this.labelsAreEditable = labelsAreEditable;
		this.objectsAreFocusable = objectsAreFocusable;
		this.objectsAreSelectable = objectsAreSelectable;
		this.objectsAreInspectable = objectsAreInspectable;
		// inspectors = new DianaInspectors(this);
		focusedObjects = new ArrayList<>();
		selectedObjects = new ArrayList<>();
		expectedFocusedObjects = new ArrayList<>();
	}

	@Override
	public void delete() {
		/*if (inspectors != null) {
			inspectors.delete();
		}*/
		focusedObjects.clear();
		selectedObjects.clear();
		focusedControlArea = null;
		// inspectors = null;
		storedSelection = null;
		super.delete();
	}

	public boolean areShapesMovable() {
		return shapesAreMovable;
	}

	public void setShapesAreMovable(boolean shapesAreMovable) {
		this.shapesAreMovable = shapesAreMovable;
	}

	public boolean areLabelsEditable() {
		return labelsAreEditable;
	}

	public void setLabelsAreEditable(boolean labelsAreEditable) {
		this.labelsAreEditable = labelsAreEditable;
	}

	public boolean areObjectsFocusable() {
		return objectsAreFocusable;
	}

	public void setObjectsAreFocusable(boolean objectsAreFocusable) {
		this.objectsAreFocusable = objectsAreFocusable;
	}

	public boolean areObjectsSelectable() {
		return objectsAreSelectable;
	}

	public void setObjectsAreSelectable(boolean objectsAreSelectable) {
		this.objectsAreSelectable = objectsAreSelectable;
	}

	public boolean areObjectInspectable() {
		return objectsAreInspectable;
	}

	public void setObjectsAreInspectable(boolean objectsAreInspectable) {
		this.objectsAreInspectable = objectsAreInspectable;
	}

	/*public DianaInspectors getInspectors() {
		return inspectors;
	}*/

	public DrawingTreeNode<?, ?> getFocusedFloatingLabel() {
		return focusedFloatingLabel;
	}

	public void setFocusedFloatingLabel(DrawingTreeNode<?, ?> aFocusedlabel) {
		if (focusedFloatingLabel != aFocusedlabel) {
			DrawingTreeNode<?, ?> oldFocusedFloatingLabel = this.focusedFloatingLabel;
			this.focusedFloatingLabel = aFocusedlabel;
			if (getDelegate() != null) {
				getDelegate().focusedObjectChanged(oldFocusedFloatingLabel, focusedFloatingLabel);
			}
		}
	}

	public ShapeNode<?> getFirstSelectedShape() {
		for (DrawingTreeNode<?, ?> node : getSelectedObjects()) {
			if (node instanceof ShapeNode) {
				return (ShapeNode<?>) node;
			}
		}
		return null;
	}

	public List<DrawingTreeNode<?, ?>> getSelectedObjects() {
		return selectedObjects;
	}

	public void setSelectedObjects(List<? extends DrawingTreeNode<?, ?>> someSelectedObjects) {
		if (someSelectedObjects == null) {
			setSelectedObjects(new ArrayList<DrawingTreeNode<?, ?>>());
			return;
		}

		if (!selectedObjects.equals(someSelectedObjects)) {
			stopEditionOfEditedLabelIfAny();
			clearSelection();
			for (DrawingTreeNode<?, ?> d : someSelectedObjects) {
				addToSelectedObjects(d);
			}
		}
	}

	public void setSelectedObject(DrawingTreeNode<?, ?> aNode) {
		// stopEditionOfEditedLabelIfAny();
		setSelectedObjects(Collections.singletonList(aNode));
		/*if (getInspectors() != null) {
			getInspectors().update();
		}*/
	}

	protected void clearSelectedNodesComputation() {
		selectedShapes = null;
		selectedShapesAndGeometricNodes = null;
		selectedConnectors = null;
		selectedContainers = null;
	}

	public void addToSelectedObjects(DrawingTreeNode<?, ?> aNode) {

		if (!isRestoringSelection) {
			stopEditionOfEditedLabelIfAny();
		}
		if (aNode == null) {
			logger.warning("Cannot add null object");
			return;
		}
		aNode.setIsSelected(true);
		if (!selectedObjects.contains(aNode)) {
			selectedObjects.add(aNode);
			notifyObservers(new ObjectAddedToSelection(aNode));
			clearSelectedNodesComputation();
		}
		/*if (getInspectors() != null) {
			getInspectors().update();
		}*/

		fireSelectionUpdated();
	}

	public void removeFromSelectedObjects(DrawingTreeNode<?, ?> aNode) {
		stopEditionOfEditedLabelIfAny();
		if (aNode == null) {
			logger.warning("Cannot remove null object");
			return;
		}
		aNode.setIsSelected(false);
		if (selectedObjects.contains(aNode)) {
			selectedObjects.remove(aNode);
			notifyObservers(new ObjectRemovedFromSelection(aNode));
			clearSelectedNodesComputation();
		}
		/*if (getInspectors() != null) {
			getInspectors().update();
		}*/
		fireSelectionUpdated();

	}

	public void toggleSelection(DrawingTreeNode<?, ?> aNode) {
		// logger.info("BEGIN toggle selection with "+aGraphicalRepresentation+" with selection="+selectedObjects);
		stopEditionOfEditedLabelIfAny();
		if (aNode.getIsSelected()) {
			removeFromSelectedObjects(aNode);
		}
		else {
			addToSelectedObjects(aNode);
		}
		// logger.info("END toggle selection with "+aGraphicalRepresentation+" with selection="+selectedObjects);
	}

	public void clearSelection() {
		// logger.info("Clear selection");
		stopEditionOfEditedLabelIfAny();
		for (DrawingTreeNode<?, ?> s : selectedObjects) {
			if (!s.isDeleted()) {
				s.setIsSelected(false);
			}
		}
		selectedObjects.clear();
		notifyObservers(new SelectionCleared());
		clearSelectedNodesComputation();

		/*if (getInspectors() != null) {
			getInspectors().update();
		}*/
		fireSelectionUpdated();
	}

	/**
	 * Called when selection was updated<br>
	 * Override when required
	 */
	protected void fireSelectionUpdated() {
	}

	/**
	 * Return list of selected object of either of supplied types
	 * 
	 * @param types
	 * @return
	 */
	public List<DrawingTreeNode<?, ?>> getSelectedObjects(Class<?>... types) {
		List<DrawingTreeNode<?, ?>> returned = new ArrayList<>();
		for (DrawingTreeNode<?, ?> node : selectedObjects) {
			boolean takeIt = false;
			for (Class<?> t : types) {
				if (t.isAssignableFrom(node.getClass())) {
					takeIt = true;
					break;
				}
			}
			if (takeIt) {
				returned.add(node);
			}
		}
		return returned;
	}

	/**
	 * Return list of selected drawable of either of supplied types
	 * 
	 * @param types
	 * @return
	 */
	@SafeVarargs
	public final <T> List<T> getSelectedDrawables(Class<? extends T>... types) {
		List<T> returned = new ArrayList<>();
		for (DrawingTreeNode<?, ?> node : selectedObjects) {
			boolean takeIt = false;
			for (Class<? extends T> t : types) {
				if (t.isAssignableFrom(node.getDrawable().getClass())) {
					takeIt = true;
					break;
				}
			}
			if (takeIt) {
				returned.add((T) node.getDrawable());
			}
		}
		return returned;
	}

	public List<ShapeNode<?>> getSelectedShapes() {
		if (selectedShapes == null) {
			selectedShapes = new ArrayList<>();
			for (DrawingTreeNode<?, ?> node : selectedObjects) {
				if (node instanceof ShapeNode) {
					selectedShapes.add((ShapeNode<?>) node);
				}
			}
		}
		return selectedShapes;
	}

	public List<DrawingTreeNode<?, ?>> getSelectedShapesAndGeometricObjects() {
		if (selectedShapesAndGeometricNodes == null) {
			selectedShapesAndGeometricNodes = new ArrayList<>();
			for (DrawingTreeNode<?, ?> node : selectedObjects) {
				if (node instanceof ShapeNode) {
					selectedShapesAndGeometricNodes.add(node);
				}
				if (node instanceof GeometricNode) {
					selectedShapesAndGeometricNodes.add(node);
				}
			}
		}
		return selectedShapesAndGeometricNodes;
	}

	public List<ConnectorNode<?>> getSelectedConnectors() {
		if (selectedConnectors == null) {
			selectedConnectors = new ArrayList<>();
			for (DrawingTreeNode<?, ?> node : selectedObjects) {
				if (node instanceof ConnectorNode) {
					selectedConnectors.add((ConnectorNode<?>) node);
				}
			}
		}
		return selectedConnectors;
	}

	public List<ContainerNode<?, ?>> getSelectedContainers() {
		if (selectedContainers == null) {
			selectedContainers = new ArrayList<>();
			for (DrawingTreeNode<?, ?> node : selectedObjects) {
				if (node instanceof ContainerNode) {
					selectedContainers.add((ContainerNode<?, ?>) node);
				}
			}
		}
		return selectedContainers;
	}

	public List<DrawingTreeNode<?, ?>> getFocusedObjects() {
		return focusedObjects;
	}

	public void setFocusedObjects(List<? extends DrawingTreeNode<?, ?>> someFocusedObjects) {
		if (someFocusedObjects == null) {
			setFocusedObjects(Collections.<DrawingTreeNode<?, ?>> emptyList());
			return;
		}

		if (!expectedFocusedObjects.equals(someFocusedObjects)) {
			List<DrawingTreeNode<?, ?>> nodesToUnfocus = new ArrayList<>();
			nodesToUnfocus.addAll(expectedFocusedObjects);
			for (DrawingTreeNode<?, ?> node : someFocusedObjects) {
				nodesToUnfocus.remove(node);
			}

			expectedFocusedObjects.clear();
			expectedFocusedObjects.addAll(someFocusedObjects);

			if (DianaPrefs.HANDLE_TRAILING_FOCUSING) {

				final List<ShapeNode<?>> trailingNodes = new ArrayList<>();
				for (DrawingTreeNode<?, ?> node : focusedObjects) {
					// only ShapeNode may be trailing
					if (node instanceof ShapeNode && node.getIsLongTimeFocused()) {
						trailingNodes.add((ShapeNode) node);
					}
				}
				for (DrawingTreeNode<?, ?> node : someFocusedObjects) {
					trailingNodes.remove(node);
				}

				// Handle nodes to unfocus now
				for (DrawingTreeNode<?, ?> d : nodesToUnfocus) {
					if (!trailingNodes.contains(d)) {
						removeFromFocusedObjects(d);
					}
				}

				new Thread(new Runnable() {
					@Override
					public void run() {
						// Waiting
						try {
							Thread.sleep(DianaPrefs.TRAILING_FOCUSING_DELAY);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						for (DrawingTreeNode<?, ?> node : trailingNodes) {
							if (!expectedFocusedObjects.contains(node)) {
								removeFromFocusedObjects(node);
							}
						}

					}
				}).start();

			}
			else {
				for (DrawingTreeNode<?, ?> d : nodesToUnfocus) {
					removeFromFocusedObjects(d);
				}

			}

			// clearFocusSelection();
			for (DrawingTreeNode<?, ?> d : someFocusedObjects) {
				addToFocusedObjects(d);
			}

		}
	}

	public void setFocusedObject(final DrawingTreeNode<?, ?> aNode) {
		setFocusedObjects(aNode == null ? null : Collections.singletonList(aNode));
	}

	public void addToFocusedObjects(DrawingTreeNode<?, ?> aNode) {
		if (aNode == null) {
			logger.warning("Cannot add null object");
			return;
		}
		if (!focusedObjects.contains(aNode)) {
			focusedObjects.add(aNode);
			aNode.setIsFocused(true);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Focusing on " + aNode);
			}
		}
	}

	public void removeFromFocusedObjects(DrawingTreeNode<?, ?> aNode) {
		if (aNode == null) {
			logger.warning("Cannot remove null object");
			return;
		}
		if (focusedObjects.contains(aNode)) {
			focusedObjects.remove(aNode);
		}
		aNode.setIsFocused(false);
	}

	public void toggleFocusSelection(DrawingTreeNode<?, ?> aNode) {
		if (aNode.getIsFocused()) {
			removeFromFocusedObjects(aNode);
		}
		else {
			addToFocusedObjects(aNode);
		}
	}

	public void clearFocusSelection() {
		// stopEditionOfEditedLabelIfAny();
		for (DrawingTreeNode<?, ?> node : focusedObjects) {
			if (!node.isDeleted()) {
				node.setIsFocused(false);
			}
		}
		focusedObjects.clear();
	}

	public void selectDrawing() {
		stopEditionOfEditedLabelIfAny();
		// Override when required
	}

	public void setEditedLabel(DianaView<?, ?> aLabel) {
		stopEditionOfEditedLabelIfAny();
		currentlyEditedLabelView = aLabel;
	}

	public void resetEditedLabel(DianaView<?, ?> editedLabel) {
		if (currentlyEditedLabelView == editedLabel) {
			currentlyEditedLabelView = null;
		}
	}

	public boolean hasEditedLabel() {
		return currentlyEditedLabelView != null;
	}

	public DianaView<?, ?> getEditedLabel() {
		return currentlyEditedLabelView;
	}

	public void stopEditionOfEditedLabelIfAny() {
		if (currentlyEditedLabelView != null) {
			currentlyEditedLabelView.stopLabelEdition();
		}

	}

	public MouseDragControl<DianaInteractiveViewer<?, ?, ?>> getCurrentMouseDrag() {
		return currentMouseDrag;
	}

	public void setCurrentMouseDrag(MouseDragControl<DianaInteractiveViewer<?, ?, ?>> aMouseDrag) {
		currentMouseDrag = aMouseDrag;
	}

	/**
	 * Implements strategy to preferencially choose a control point or an other during focus retrieving strategy
	 * 
	 * @param cp1
	 * @param cp2
	 * @return
	 */
	public ControlArea<?> preferredFocusedControlArea(ControlArea<?> ca1, ControlArea<?> ca2) {
		if (ca1.getNode().getGraphicalRepresentation().getLayer() == ca2.getNode().getGraphicalRepresentation().getLayer()) {
			// ControlPoint have priority on other ControlArea
			if (ca1 instanceof ConnectorAdjustingControlPoint) {
				return ca1;
			}
			else if (ca2 instanceof ConnectorAdjustingControlPoint) {
				return ca2;
			}
			if (ca1 instanceof ControlPoint) {
				return ca1;
			}
			else if (ca2 instanceof ControlPoint) {
				return ca2;
			}
		}
		return ca1.getNode().getGraphicalRepresentation().getLayer() > ca2.getNode().getGraphicalRepresentation().getLayer() ? ca1 : ca2;
	}

	public ControlArea<?> getFocusedControlArea() {
		return focusedControlArea;
	}

	public void _setFocusedControlArea(ControlArea<?> aControlArea) {
		if (focusedControlArea != aControlArea) {
			this.focusedControlArea = aControlArea;
			// getDrawingView().getPaintManager().repaint(getDrawingView());
		}
	}

	public String getToolTipText() {
		if (getFocusedObjects().size() > 0) {
			DrawingTreeNode<?, ?> node = getFocusedObjects().get(0);
			if (node.getGraphicalRepresentation().getToolTipText() != null) {
				// logger.info("getToolTipText() ? return "+gr.getToolTipText());
				return node.getGraphicalRepresentation().getToolTipText();
			}
		}
		// logger.info("getToolTipText() ? return null");
		return null;
	}

	/**
	 * Return last clicked point, when any, relatively to last selected node (see {@link #getLastSelectedNode()})
	 * 
	 * @return
	 */
	public DianaPoint getLastClickedPoint() {
		return lastClickedPoint;
	}

	public void setLastClickedPoint(DianaPoint lastClickedPoint, DrawingTreeNode<?, ?> node) {
		this.lastClickedPoint = lastClickedPoint;
	}

	/**
	 * Return last selected node
	 * 
	 * @return
	 */
	public DrawingTreeNode<?, ?> getLastSelectedNode() {
		return lastSelectedNode;
	}

	public void setLastSelectedNode(DrawingTreeNode<?, ?> lastSelectedNode) {
		this.lastSelectedNode = lastSelectedNode;
	}

	/**
	 * Retrieve stored selection as a persistent list of identified objects
	 */
	private void restoreStoredSelection() {
		if (storedSelection == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Cannot restore null selection");
			}
			return;
		}
		try {
			isRestoringSelection = true;
			for (DrawingTreeNodeIdentifier<?> identifier : storedSelection) {
				DrawingTreeNode<?, ?> node = getDrawing().getDrawingTreeNode(identifier);
				if (node != null && !node.isDeleted()) {
					addToSelectedObjects(node);
				}
			}
		} finally {
			if (storedSelection != null) {
				storedSelection.clear();
			}
			storedSelection = null;
			isRestoringSelection = false;
		}
	}

	/**
	 * Stores the current selection as a persistent list of identified objects<br>
	 * Guarantee persistence over tree restructurations
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void storeCurrentSelection() {
		if (getSelectedObjects() != null) {
			if (storedSelection != null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot store selection when there is already a stored selection");
				}
				return;
			}
			storedSelection = new ArrayList<>();
			for (DrawingTreeNode<?, ?> node : getSelectedObjects()) {
				storedSelection.add(new DrawingTreeNodeIdentifier(node.getDrawable(), node.getGRBinding()));
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getSource() == getDrawing()) {
			if (evt.getPropertyName().equals(DrawingTreeNodeHierarchyRebuildStarted.EVENT_NAME)) {
				storeCurrentSelection();
			}
			else if (evt.getPropertyName().equals(DrawingTreeNodeHierarchyRebuildEnded.EVENT_NAME) && storedSelection != null) {
				restoreStoredSelection();
			}
		}
	}

}
