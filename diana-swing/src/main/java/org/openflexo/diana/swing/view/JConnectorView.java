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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.batik.svggen.SVGGraphics2D;
import org.openflexo.diana.ConnectorGraphicalRepresentation;
import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.DianaInteractiveViewer;
import org.openflexo.diana.notifications.ConnectorModified;
import org.openflexo.diana.notifications.NodeDeleted;
import org.openflexo.diana.notifications.ObjectHasMoved;
import org.openflexo.diana.notifications.ObjectHasResized;
import org.openflexo.diana.notifications.ObjectMove;
import org.openflexo.diana.notifications.ObjectResized;
import org.openflexo.diana.notifications.ObjectWillMove;
import org.openflexo.diana.notifications.ObjectWillResize;
import org.openflexo.diana.swing.JDianaInteractiveEditor;
import org.openflexo.diana.swing.SwingViewFactory;
import org.openflexo.diana.swing.control.tools.DianaViewDropListener;
import org.openflexo.diana.swing.graphics.DrawUtils;
import org.openflexo.diana.swing.graphics.JDianaConnectorGraphics;
import org.openflexo.diana.swing.paint.DianaPaintManager;
import org.openflexo.diana.view.ConnectorView;
import org.openflexo.diana.view.DianaView;

/**
 * The JConnectorView is the SWING implementation of a panel showing a {@link ConnectorNode}
 * 
 * @author sylvain
 * 
 * @param <O>
 */
@SuppressWarnings("serial")
public class JConnectorView<O> extends JPanel implements ConnectorView<O, JPanel>, JDianaView<O, JPanel> {

	private static final Logger logger = Logger.getLogger(JConnectorView.class.getPackage().getName());

	private ConnectorNode<O> connectorNode;
	private DianaViewMouseListener mouseListener;
	private AbstractDianaEditor<?, SwingViewFactory, JComponent> controller;

	private JLabelView<O> labelView;

	protected JDianaConnectorGraphics graphics;

	// The registered dropListener for this view, working with the DropTarget
	private DianaViewDropListener dropListener;

	public JConnectorView(ConnectorNode<O> node, AbstractDianaEditor<?, SwingViewFactory, JComponent> controller) {
		super();
		this.controller = controller;
		this.connectorNode = node;
		updateLabelView();
		relocateAndResizeView();
		mouseListener = controller.getDianaFactory().makeViewMouseListener(connectorNode, this, controller);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		connectorNode.getPropertyChangeSupport().addPropertyChangeListener(this);
		setOpaque(false);

		updateVisibility();

		graphics = new JDianaConnectorGraphics(node, this);

	}

	private boolean isDeleted = false;

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public synchronized void delete() {
		if (isDeleted()) {
			return;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Delete JConnectorView for " + connectorNode);
		}
		if (getParentView() != null) {
			JDianaLayeredView<?> parentView = getParentView();
			// logger.warning("Unexpected not null parent, proceeding anyway");
			parentView.remove(this);
			parentView.revalidate();
			if (getPaintManager() != null) {
				getPaintManager().invalidate(connectorNode);
				getPaintManager().repaint(parentView);
			}
		}
		if (connectorNode != null) {
			connectorNode.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		getController().unreferenceViewForDrawingTreeNode(connectorNode);
		setDropTarget(null);
		removeMouseListener(mouseListener);
		removeMouseMotionListener(mouseListener);
		if (labelView != null) {
			labelView.delete();
		}
		labelView = null;
		controller = null;
		mouseListener = null;
		connectorNode = null;
		isDeleted = true;
	}

	@Override
	public O getDrawable() {
		return connectorNode.getDrawable();
	}

	@Override
	public ConnectorNode<O> getNode() {
		return connectorNode;
	}

	@Override
	public JDianaConnectorGraphics getDianaGraphics() {
		return graphics;
	}

	@Override
	public JDrawingView<?> getDrawingView() {
		if (getController() != null) {
			return (JDrawingView<?>) getController().getDrawingView();
		}
		return null;
	}

	@Override
	public JDianaLayeredView<?> getParent() {
		return (JDianaLayeredView<?>) super.getParent();
	}

	@Override
	public JDianaLayeredView<?> getParentView() {
		return getParent();
	}

	@Override
	public double getScale() {
		return getController().getScale();
	}

	@Override
	public void rescale() {
		relocateAndResizeView();
	}

	private void relocateAndResizeView() {
		relocateView();
		resizeView();
	}

	private void relocateView() {
		/*logger.info("relocateView to ("
				+getGraphicalRepresentation().getViewX(getScale())+","+
				getGraphicalRepresentation().getViewY(getScale())+")");*/
		if (labelView != null) {
			labelView.updateBounds();
		}
		int newX, newY;
		newX = connectorNode.getViewX(getScale());
		newY = connectorNode.getViewY(getScale());
		if (newX != getX() || newY != getY()) {
			setLocation(newX, newY);
		}
	}

	private void resizeView() {
		/*logger.info("resizeView to ("
				+getGraphicalRepresentation().getViewWidth(getScale())+","+
				getGraphicalRepresentation().getViewHeight(getScale())+")");*/
		if (labelView != null) {
			labelView.updateBounds();
		}
		int newWidth, newHeight;
		newWidth = connectorNode.getViewWidth(getScale());
		newHeight = connectorNode.getViewHeight(getScale());
		if (newWidth != getWidth() || newHeight != getHeight()) {
			setSize(newWidth, newHeight);
			if (getDrawingView().isBuffering()) {
				/* Something very bad happened here:
				 * the view is resizing while drawing view is beeing buffered:
				 * all the things we were buffering may be wrong now, we have to
				 * start buffering again
				 */
				getDrawingView().startBufferingAgain();
			}
		}
	}

	/*private void updateLayer()
	{
		//logger.info("GR: "+getGraphicalRepresentation()+" update layer to "+getLayer());
		if (getParent() instanceof JLayeredPane) {
			if (labelView!=null)
				((JLayeredPane)getParent()).setLayer(labelView, getLayer());
			((JLayeredPane)getParent()).setLayer(this, getLayer());
		}
	}*/

	private void updateLayer() {
		if (getParent() != null) {
			if (labelView != null) {
				getParent().setLayer((Component) labelView, getLayer());
				getParent().setPosition(labelView, connectorNode.getIndex() * 2);
			}
			getParent().setLayer((Component) this, getLayer());
			getParent().setPosition(this, connectorNode.getIndex() * 2 + 1);
		}
	}

	private void updateVisibility() {
		if (labelView != null) {
			labelView.setVisible(connectorNode.shouldBeDisplayed());
		}
		setVisible(connectorNode.shouldBeDisplayed());
	}

	private void updateLabelView() {
		if (!connectorNode.hasText() && labelView != null) {
			labelView.delete();
			labelView = null;
		}
		else if (connectorNode.hasText() && labelView == null && connectorNode.getConnector() != null) {
			labelView = new JLabelView<O>(getNode(), getController(), this);
			if (getParentView() != null) {
				getParentView().add(getLabelView());
			}
		}
	}

	public Integer getLayer() {
		if (connectorNode != null && connectorNode.getGraphicalRepresentation() != null) {
			return DianaConstants.INITIAL_LAYER + connectorNode.getGraphicalRepresentation().getLayer();
		}
		return 0;
	}

	private void doPaint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		DrawUtils.turnOnAntiAlising(g2);
		DrawUtils.setRenderQuality(g2);
		DrawUtils.setColorRenderQuality(g2);
		graphics.createGraphics(g2/*, controller*/);
		getNode().paint(graphics);
		graphics.releaseGraphics();
		super.paint(g);
	}

	@Override
	public void paint(Graphics g) {

		if (isDeleted()) {
			return;
		}

		if (g instanceof SVGGraphics2D) {
			doPaint(g);
			return;
		}

		/*Graphics2D g2 = (Graphics2D) g;
		DrawUtils.turnOnAntiAlising(g2);
		DrawUtils.setRenderQuality(g2);
		DrawUtils.setColorRenderQuality(g2);
		graphics.createGraphics(g2);*/

		if (getPaintManager().isPaintingCacheEnabled()) {
			if (getDrawingView().isBuffering()) {
				// Buffering painting
				if (getPaintManager().isTemporaryObject(connectorNode)) {
					// This object is declared to be a temporary object, to be redrawn
					// continuously, so we need to ignore it: do nothing
					if (DianaPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
						DianaPaintManager.paintPrimitiveLogger.fine("JConnectorView: buffering paint, ignore: " + connectorNode);
					}
				}
				else {
					if (DianaPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
						DianaPaintManager.paintPrimitiveLogger
								.fine("JConnectorView: buffering paint, draw: " + connectorNode + " clip=" + g.getClip());
					}
					doPaint(g);
					/*getNode().paint(graphics);
					super.paint(g);*/
				}
			}
			else {
				if (!getPaintManager().renderUsingBuffer((Graphics2D) g, g.getClipBounds(), connectorNode, getScale())) {
					doPaint(g);
					// getNode().paint(graphics);
					// super.paint(g);
				}

			}
		}
		else {
			// Normal painting
			doPaint(g);
			// getNode().paint(graphics);
			// super.paint(g);
		}

		// super.paint(g);
		// getGraphicalRepresentation().paint(g,getController());

		// graphics.releaseGraphics();
	}

	@Override
	public AbstractDianaEditor<?, SwingViewFactory, JComponent> getController() {
		return controller;
	}

	/*	protected void handleNodeDeleted(NodeDeleted notification) {
			DrawingTreeNode<?, ?> deletedNode = notification.getDeletedNode();
			if (deletedNode == getNode()) {
				// If was not removed, try to do it now
				// TODO: is this necessary ???
				if (deletedNode != null && deletedNode.getParentNode() != null
						&& deletedNode.getParentNode().getChildNodes().contains(deletedNode)) {
					deletedNode.getParentNode().removeChild(deletedNode);
				}
				if (getController() instanceof DianaInteractiveViewer) {
					if (getNode() != null
							&& ((DianaInteractiveViewer<?, SwingViewFactory, JComponent>) getController()).getFocusedObjects().contains(
									getNode())) {
						((DianaInteractiveViewer<?, SwingViewFactory, JComponent>) getController()).removeFromFocusedObjects(getNode());
					}
					if (getNode() != null
							&& ((DianaInteractiveViewer<?, SwingViewFactory, JComponent>) getController()).getSelectedObjects().contains(
									getNode())) {
						((DianaInteractiveViewer<?, SwingViewFactory, JComponent>) getController()).removeFromSelectedObjects(getNode());
					}
				}
				// Now delete the view
				delete();
			}
		}*/

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		if (isDeleted) {
			logger.warning("Received notifications for deleted view " + evt);
			return;
		}

		if (evt.getPropertyName().equals(NodeDeleted.EVENT_NAME) && !isDeleted()) {
			// System.out.println("Je recois bien l'ordre de deletion, je supprime maintenant !!!");
			delete();
			return;
		}

		if ((!evt.getPropertyName().equals(NodeDeleted.EVENT_NAME)) && getNode().isDeleted()) {
			logger.warning("Received notifications for deleted ConnectorNode " + evt);
			return;
		}

		// System.out.println("JConnectorView, received: "+aNotification);
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> propertyChange(evt));
		}
		else {
			if (evt.getPropertyName().equals(NodeDeleted.EVENT_NAME)) {
				delete();
			}
			else if (evt.getPropertyName().equals(ConnectorModified.EVENT_NAME)) {
				if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(connectorNode)) {
					getPaintManager().invalidate(connectorNode);
				}
				relocateAndResizeView();
				revalidate();
				getPaintManager().repaint(this);
			}
			/*else if (notification instanceof NodeDeleted) {
			handleNodeDeleted((NodeDeleted) notification);
			}*/else if (evt.getPropertyName().equals(GraphicalRepresentation.LAYER.getName())) {
				updateLayer();
				if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(connectorNode)) {
					getPaintManager().invalidate(connectorNode);
				}
				getPaintManager().repaint(this);
				/*if (getParentView() != null) {
					getParentView().revalidate();
					getPaintManager().repaint(this);
				}*/
			}
			else if (evt.getPropertyName().equals(DrawingTreeNode.IS_FOCUSED.getName())) {
				getPaintManager().repaint(this);
			}
			else if (evt.getPropertyName().equals(DrawingTreeNode.IS_LONG_TIME_FOCUSED.getName())) {
				getPaintManager().repaint(this);
			}
			else if (evt.getPropertyName().equals(DrawingTreeNode.IS_SELECTED.getName())) {
				// TODO: ugly hack, please fix this, implement a ForceRepaint in DianaPaintManager
				if (connectorNode.getIsSelected()) {
					requestFocusInWindow();
				}
			}
			else if (evt.getPropertyName().equals(GraphicalRepresentation.TEXT.getName())) {
				updateLabelView();
			}
			else if (evt.getPropertyName().equals(GraphicalRepresentation.IS_VISIBLE.getName())) {
				updateVisibility();
				if (getPaintManager().isPaintingCacheEnabled()) {
					if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(connectorNode)) {
						getPaintManager().invalidate(connectorNode);
					}
				}
				getPaintManager().repaint(this);
			}
			else if (evt.getPropertyName().equals(ConnectorGraphicalRepresentation.APPLY_FOREGROUND_TO_SYMBOLS.getName())) {
				getPaintManager().repaint(this);
			}
			else if (evt.getPropertyName().equals(ObjectWillMove.EVENT_NAME)) {
				if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().addToTemporaryObjects(connectorNode);
					getPaintManager().invalidate(connectorNode);
				}
			}
			else if (evt.getPropertyName().equals(ObjectMove.PROPERTY_NAME)) {
				relocateView();
				if (getParentView() != null) {
					// getParentView().revalidate();
					getPaintManager().repaint(this);
				}
			}
			else if (evt.getPropertyName().equals(ObjectHasMoved.EVENT_NAME)) {
				if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().removeFromTemporaryObjects(connectorNode);
					getPaintManager().invalidate(connectorNode);
					getPaintManager().repaint(getParentView());
				}
			}
			else if (evt.getPropertyName().equals(ObjectWillResize.EVENT_NAME)) {
				if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().addToTemporaryObjects(connectorNode);
					getPaintManager().invalidate(connectorNode);
				}
			}
			else if (evt.getPropertyName().equals(ObjectResized.PROPERTY_NAME)) {
				relocateView();
				if (getParentView() != null) {
					// getParentView().revalidate();
					getPaintManager().repaint(this);
				}
			}
			else if (evt.getPropertyName().equals(ObjectHasResized.EVENT_NAME)) {
				if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().removeFromTemporaryObjects(connectorNode);
					getPaintManager().invalidate(connectorNode);
					getPaintManager().repaint(getParentView());
				}
			}
			else {
				// revalidate();
				if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(connectorNode)) {
					getPaintManager().invalidate(connectorNode);
				}
				getPaintManager().repaint(this);
			}
		}
	}

	@Override
	public JLabelView<O> getLabelView() {
		return labelView;
	}

	/*@Override
	public void activatePalette(DianaPalette<?, ?> aPalette) {
		if (aPalette instanceof JDianaPalette) {
			// A palette is registered, listen to drag'n'drop events
			setDropTarget(
					new DropTarget(this, DnDConstants.ACTION_COPY, ((JDianaPalette) aPalette).buildDropListener(this, controller), true));
		}
	
	}*/

	@Override
	public DianaPaintManager getPaintManager() {
		return getDrawingView().getPaintManager();
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		if (getController() instanceof DianaInteractiveViewer) {
			return ((DianaInteractiveViewer<?, SwingViewFactory, JComponent>) getController()).getToolTipText();
		}
		return super.getToolTipText(event);
	}

	@Override
	public void stopLabelEdition() {
		getLabelView().stopEdition();
	}

	/**
	 * Activate Drag&Drop for this {@link DianaView} if not already activated
	 * 
	 * @return
	 */
	@Override
	public DropTarget activateDragAndDrop() {
		if (!isDragAndDropActivated() && getController() instanceof JDianaInteractiveEditor) {

			dropListener = ((JDianaInteractiveEditor<?>) getController()).makeDropListener(this);
			setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY | DnDConstants.ACTION_MOVE, dropListener, true));
		}
		if (!getDropTarget().isActive()) {
			getDropTarget().setActive(true);
		}
		/*for (DianaView<?, ?> v : controller.getContents().values()) {
			if (v != this) {
				v.activateDragAndDrop();
			}
		}*/
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
