/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-swing, a component of the software infrastructure 
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

package org.openflexo.diana.swing.view;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;

import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.DianaEditor;
import org.openflexo.diana.graphics.DianaGraphics;
import org.openflexo.diana.swing.JDianaInteractiveEditor;
import org.openflexo.diana.swing.control.tools.DianaViewDropListener;
import org.openflexo.diana.swing.graphics.DrawUtils;
import org.openflexo.diana.swing.graphics.JDianaGraphics;
import org.openflexo.diana.swing.paint.DianaPaintManager;
import org.openflexo.diana.view.DianaContainerView;
import org.openflexo.diana.view.DianaView;

@SuppressWarnings("serial")
public abstract class JDianaLayeredView<O> extends JLayeredPane
		implements DianaContainerView<O, JLayeredPane>, JDianaView<O, JLayeredPane> {

	private static final Logger logger = Logger.getLogger(JDianaLayeredView.class.getPackage().getName());

	private final List<DianaView<?, ? extends JComponent>> childViews;

	public JDianaLayeredView() {
		super();
		childViews = new ArrayList<DianaView<?, ? extends JComponent>>();
	}

	@Override
	public abstract JDrawingView<?> getDrawingView();

	/**
	 * Sets the layer attribute on the specified component, making it the bottommost component in that layer. Should be called before adding
	 * to parent.
	 * 
	 * @param c
	 *            the Component to set the layer for
	 * @param layer
	 *            an int specifying the layer to set, where lower numbers are closer to the bottom
	 */
	public void setLayer(DianaView<?, ?> c, int layer) {
		setLayer((Component) c, layer, -1);
	}

	/**
	 * Returns the layer attribute for the specified Component.
	 * 
	 * @param c
	 *            the Component to check
	 * @return an int specifying the component's current layer
	 */
	public int getLayer(DianaView<?, ?> c) {
		return super.getLayer((Component) c);
	}

	/**
	 * Moves the component to the top of the components in its current layer (position 0).
	 * 
	 * @param c
	 *            the Component to move
	 * @see #setPosition(Component, int)
	 */
	public void toFront(DianaView<?, ?> c) {
		super.moveToFront((Component) c);
	}

	/**
	 * Moves the component to the bottom of the components in its current layer (position -1).
	 * 
	 * @param c
	 *            the Component to move
	 * @see #setPosition(Component, int)
	 */
	public void toBack(DianaView<?, ?> c) {
		super.moveToBack((Component) c);
	}

	public List<DianaView<?, ?>> getViewsInLayer(int layer) {
		List<DianaView<?, ?>> returned = new ArrayList<DianaView<?, ?>>();
		for (Component c : super.getComponentsInLayer(layer)) {
			if (c instanceof DianaView) {
				returned.add((DianaView<?, ?>) c);
			}
		}
		return returned;
	}

	@Override
	public void addView(DianaView<?, ?> view) {
		// logger.info("add view " + view + " under " + this);
		if (view instanceof JShapeView) {
			((JShapeView<?>) view).setBackground(getBackground());
			// logger.info("add the label view " + ((JShapeView<?>) view).getLabelView());
			if (((JShapeView<?>) view).getLabelView() != null) {
				add(((JShapeView<?>) view).getLabelView(), ((JShapeView<?>) view).getLayer(), -1);
			}
			// logger.info("add the view");
			add(((JShapeView<?>) view), ((JShapeView<?>) view).getLayer(), -1);
			childViews.add((JShapeView<?>) view);
			if (isDragAndDropActivated()) {
				view.activateDragAndDrop();
			}
		}
		else if (view instanceof JConnectorView) {
			((JConnectorView<?>) view).setBackground(getBackground());
			if (((JConnectorView<?>) view).getLabelView() != null) {
				add(((JConnectorView<?>) view).getLabelView(), ((JConnectorView<?>) view).getLayer(), -1);
			}
			add(((JConnectorView<?>) view), ((JConnectorView<?>) view).getLayer(), -1);
			childViews.add((JConnectorView<?>) view);
			if (isDragAndDropActivated()) {
				view.activateDragAndDrop();
			}
		}
	}

	@Override
	public void removeView(DianaView<?, ?> view) {
		// logger.info("remove view " + view + " from " + this);
		if (view instanceof JShapeView) {
			if (((JShapeView<?>) view).getLabelView() != null) {
				remove(((JShapeView<?>) view).getLabelView());
			}
			view.disactivateDragAndDrop();
			remove(((JShapeView<?>) view));
			childViews.remove(view);
		}
		else if (view instanceof JConnectorView) {
			if (((JConnectorView<?>) view).getLabelView() != null) {
				remove(((JConnectorView<?>) view).getLabelView());
			}
			view.disactivateDragAndDrop();
			remove(((JConnectorView<?>) view));
			childViews.remove(view);
		}
	}

	protected void handleNodeAdded(DrawingTreeNode<?, ?> newNode) {
		DrawingTreeNode<?, ?> parentNode = newNode.getParentNode();
		logger.fine("JShapeView: Received NodeAdded notification, creating view for " + newNode);
		if (newNode instanceof ShapeNode) {
			ShapeNode<?> shapeNode = (ShapeNode<?>) newNode;
			JShapeView<?> shapeView = (JShapeView<?>) getController().makeShapeView(shapeNode);
			addView(shapeView);
			revalidate();
			getPaintManager().invalidate(parentNode);
			getPaintManager().repaint(this);
			// shapeNode.notifyShapeNeedsToBeRedrawn(); // TODO: is this necessary ?
		}
		else if (newNode instanceof ConnectorNode) {
			ConnectorNode<?> connectorNode = (ConnectorNode<?>) newNode;
			JConnectorView<?> connectorView = (JConnectorView<?>) getController().makeConnectorView(connectorNode);
			addView(connectorView);
			revalidate();
			getPaintManager().invalidate(parentNode);
			getPaintManager().repaint(this);
		}
		else if (newNode instanceof GeometricNode) {
			newNode.getPropertyChangeSupport().addPropertyChangeListener(this);
			revalidate();
			getPaintManager().invalidate(parentNode);
			getPaintManager().repaint(this);
		}
	}

	protected void handleNodeRemoved(DrawingTreeNode<?, ?> removedNode, ContainerNode<?, ?> parentNode) {
		if (removedNode instanceof ShapeNode) {
			ShapeNode<?> removedShapeNode = (ShapeNode<?>) removedNode;
			JShapeView<?> view = getDrawingView().shapeViewForNode(removedShapeNode);
			if (view != null) {
				remove(view);
				revalidate();
				getPaintManager().invalidate(parentNode);
				getPaintManager().invalidate(removedShapeNode);
				getPaintManager().repaint(this);
			}
			else {
				logger.warning("Cannot find view for " + removedShapeNode);
			}
		}
		else if (removedNode instanceof ConnectorNode) {
			ConnectorNode<?> removedConnectorNode = (ConnectorNode<?>) removedNode;
			JConnectorView<?> view = getDrawingView().connectorViewForNode(removedConnectorNode);
			if (view != null) {
				remove(view);
				revalidate();
				getPaintManager().invalidate(parentNode);
				getPaintManager().invalidate(removedConnectorNode);
				getPaintManager().repaint(this);
			}
			else {
				logger.warning("Cannot find view for " + removedConnectorNode);
			}
		}
		else if (removedNode instanceof GeometricNode) {
			removedNode.getPropertyChangeSupport().removePropertyChangeListener(this);
			revalidate();
			getPaintManager().repaint(this);
		}
	}

	/*protected void handleNodeDeleted(NodeDeleted notification) {
		DrawingTreeNode<?, ?> deletedNode = notification.getDeletedNode();
		if (deletedNode == getNode()) {
			// If was not removed, try to do it now
			// TODO: is this necessary ???
			if (deletedNode != null && deletedNode.getParentNode() != null
					&& deletedNode.getParentNode().getChildNodes().contains(deletedNode)) {
				deletedNode.getParentNode().removeChild(deletedNode);
			}
			if (getController() instanceof DianaInteractiveViewer) {
				if (getNode() != null && ((DianaInteractiveViewer<?, ?, ?>) getController()).getFocusedObjects().contains(getNode())) {
					((DianaInteractiveViewer<?, ?, ?>) getController()).removeFromFocusedObjects(getNode());
				}
				if (getNode() != null && ((DianaInteractiveViewer<?, ?, ?>) getController()).getSelectedObjects().contains(getNode())) {
					((DianaInteractiveViewer<?, ?, ?>) getController()).removeFromSelectedObjects(getNode());
				}
			}
			// Now delete the view
			delete();
		}
	}*/

	@Override
	public AbstractDianaEditor<?, ?, ? super JLayeredPane> getController() {
		return getDrawingView().getController();
	}

	@Override
	public List<DianaView<?, ? extends JComponent>> getChildViews() {
		return childViews;
	}

	@Override
	public DianaPaintManager getPaintManager() {
		return getDrawingView().getPaintManager();
	}

	public void paint(DianaGraphics graphics, DianaEditor<?> controller) {
		Graphics2D g2 = ((JDianaGraphics) graphics).getGraphics();
		DrawUtils.turnOnAntiAlising(g2);
		DrawUtils.setRenderQuality(g2);
		DrawUtils.setColorRenderQuality(g2);
	}

	// The registered dropListener for this view, working with the DropTarget
	private DianaViewDropListener dropListener;

	/**
	 * Activate Drag&Drop for this {@link DianaView} if not already activated
	 * 
	 * @return
	 */
	@Override
	public DropTarget activateDragAndDrop() {
		if (!isDragAndDropActivated()) {
			dropListener = ((JDianaInteractiveEditor<?>) getController()).makeDropListener(this);
			setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY | DnDConstants.ACTION_MOVE, dropListener, true));
		}
		if (!getDropTarget().isActive()) {
			getDropTarget().setActive(true);
		}

		for (DianaView<?, ? extends JComponent> childView : getChildViews()) {
			childView.activateDragAndDrop();
		}

		return getDropTarget();
	}

	/**
	 * Desactivate Drag&Drop for this {@link DianaView} when activated
	 * 
	 * @return
	 */
	@Override
	public DropTarget disactivateDragAndDrop() {
		if (isDragAndDropActivated()) {
			DropTarget returned = getDropTarget();
			returned.setActive(false);
			setDropTarget(null);
			dropListener.delete();
			dropListener = null;
			for (DianaView<?, ? extends JComponent> childView : getChildViews()) {
				childView.disactivateDragAndDrop();
			}
			return returned;
		}
		return null;
	}

	/**
	 * Return boolean indicating if drag&drop has been activated for this view
	 * 
	 * @return
	 */
	@Override
	public boolean isDragAndDropActivated() {
		return getDropTarget() != null;
	}

}
