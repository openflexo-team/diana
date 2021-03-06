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

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import org.apache.batik.svggen.SVGGraphics2D;
import org.openflexo.diana.ContainerGraphicalRepresentation;
import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.DianaInteractiveViewer;
import org.openflexo.diana.impl.GraphNodeImpl;
import org.openflexo.diana.notifications.ControlAreasChange;
import org.openflexo.diana.notifications.NodeAdded;
import org.openflexo.diana.notifications.NodeDeleted;
import org.openflexo.diana.notifications.NodeRemoved;
import org.openflexo.diana.notifications.ObjectHasMoved;
import org.openflexo.diana.notifications.ObjectHasResized;
import org.openflexo.diana.notifications.ObjectMove;
import org.openflexo.diana.notifications.ObjectResized;
import org.openflexo.diana.notifications.ObjectWillMove;
import org.openflexo.diana.notifications.ObjectWillResize;
import org.openflexo.diana.notifications.ShapeChanged;
import org.openflexo.diana.notifications.ShapeNeedsToBeRedrawn;
import org.openflexo.diana.swing.SwingViewFactory;
import org.openflexo.diana.swing.graphics.DrawUtils;
import org.openflexo.diana.swing.graphics.JDianaShapeGraphics;
import org.openflexo.diana.swing.paint.DianaPaintManager;
import org.openflexo.diana.view.ShapeView;

/**
 * The {@link JShapeView} is the SWING implementation of a panel showing a {@link ShapeNode}
 * 
 * @author sylvain
 * 
 * @param <O>
 */
@SuppressWarnings("serial")
public class JShapeView<O> extends JDianaLayeredView<O> implements ShapeView<O, JLayeredPane> {

	private static final Logger logger = Logger.getLogger(JShapeView.class.getPackage().getName());

	private ShapeNode<O> shapeNode;
	private DianaViewMouseListener mouseListener;
	private AbstractDianaEditor<?, SwingViewFactory, JComponent> controller;

	private JLabelView<O> labelView;

	protected JDianaShapeGraphics graphics;

	public JShapeView(ShapeNode<O> node, AbstractDianaEditor<?, SwingViewFactory, JComponent> controller) {
		super();
		logger.fine("Create JShapeView " + Integer.toHexString(hashCode()) + " for " + node);

		this.controller = controller;
		this.shapeNode = node;
		node.finalizeConstraints();
		updateLabelView();

		if (getController() != null) {
			relocateAndResizeView();
		}
		mouseListener = controller.getDianaFactory().makeViewMouseListener(node, this, controller);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		if (shapeNode.getPropertyChangeSupport() != null) {
			shapeNode.getPropertyChangeSupport().addPropertyChangeListener(this);
		}
		else {
			logger.warning("JShapeView() constructor called for a deleted shape node !!!");
		}
		setOpaque(false);
		updateVisibility();
		setFocusable(true);

		graphics = new JDianaShapeGraphics(node, this);

		// setBorder(BorderFactory.createLineBorder(Color.RED));
	}

	@Override
	public JDianaShapeGraphics getDianaGraphics() {
		return graphics;
	}

	public void disableDianaViewMouseListener() {
		// System.out.println("Disable DianaViewMouseListener ");
		removeMouseListener(mouseListener);
		removeMouseMotionListener(mouseListener);
	}

	public void enableDianaViewMouseListener() {
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
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
		logger.fine("Delete JShapeView " + Integer.toHexString(hashCode()) + " for " + shapeNode);
		if (getParentView() != null) {
			JDianaLayeredView<?> parentView = getParentView();
			// logger.warning("Unexpected not null parent, proceeding anyway");
			parentView.remove(this);
			parentView.revalidate();
			if (getPaintManager() != null) {
				getPaintManager().invalidate(shapeNode);
				getPaintManager().repaint(parentView);
			}
		}
		if (shapeNode != null) {
			if (shapeNode.getPropertyChangeSupport() != null) {
				shapeNode.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
		}
		getController().unreferenceViewForDrawingTreeNode(shapeNode);
		setDropTarget(null);
		removeMouseListener(mouseListener);
		removeMouseMotionListener(mouseListener);
		if (labelView != null) {
			labelView.delete();
		}
		labelView = null;
		controller = null;
		mouseListener = null;
		shapeNode = null;
		isDeleted = true;
	}

	@Override
	public O getDrawable() {
		if (shapeNode != null) {
			return shapeNode.getDrawable();
		}
		return null;
	}

	@Override
	public ShapeNode<O> getNode() {
		return shapeNode;
	}

	@Override
	public JDrawingView<?> getDrawingView() {
		if (getController() != null) {
			return (JDrawingView<?>) getController().getDrawingView();
		}
		return null;
	}

	@Override
	public DianaPaintManager getPaintManager() {
		if (getDrawingView() != null) {
			return getDrawingView().getPaintManager();
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

	/*public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return shapeNode.getGraphicalRepresentation();
	}*/

	@Override
	public double getScale() {
		if (getController() != null) {
			return getController().getScale();
		}
		return 1.0;
	}

	@Override
	public void rescale() {
		relocateAndResizeView();
	}

	private void relocateAndResizeView() {
		relocateView();
		resizeView();
		// System.out.println("relocateAndResizeView() for "+drawable+" bounds="+getBounds());
	}

	private void relocateView() {

		int newX = shapeNode.getViewX(getScale()) /*+ (int) (DianaUtils.getCumulativeLeftBorders(shapeNode.getParentNode()) * getScale())*/
				- (int) (shapeNode.getBorderLeft() * getScale());
		int newY = shapeNode.getViewY(getScale()) /*+ (int) (DianaUtils.getCumulativeTopBorders(shapeNode.getParentNode()) * getScale())*/
				- (int) (shapeNode.getBorderTop() * getScale());

		if (shapeNode.getParentNode() instanceof ShapeNode) {
			newX += (((ShapeNode<?>) shapeNode.getParentNode()).getBorderLeft()) * getScale();
			newY += (((ShapeNode<?>) shapeNode.getParentNode()).getBorderTop()) * getScale();
		}

		if (shapeNode != null && (getX() != newX || getY() != newY)) {
			if (labelView != null) {
				labelView.updateBounds();
			}
			setLocation(newX, newY);
		}
		else {
			// logger.info("Ignore relocateView() because unchanged");
		}
	}

	private void resizeView() {

		if (shapeNode != null && (getWidth() != shapeNode.getViewWidth(getScale()) || getHeight() != shapeNode.getViewHeight(getScale()))) {
			if (labelView != null) {
				labelView.updateBounds();
			}
			setSize(shapeNode.getViewWidth(getScale()), shapeNode.getViewHeight(getScale()));
		}
		else {
			// logger.info("Ignore resizeView() because unchanged");
		}
	}

	private void updateLayer() {
		if (getParent() instanceof JLayeredPane) {
			if (labelView != null) {
				getParent().setLayer((Component) labelView, getLayer());
				getParent().setPosition(labelView, shapeNode.getIndex() * 2);
			}
			getParent().setLayer((Component) this, getLayer());
			getParent().setPosition(this, shapeNode.getIndex() * 2 + 1);
		}
	}

	private void updateVisibility() {
		if (labelView != null) {
			labelView.setVisible(shapeNode.shouldBeDisplayed());
		}

		setVisible(shapeNode.shouldBeDisplayed());
	}

	private void updateLabelView() {
		if (!shapeNode.hasText() && labelView != null) {
			getParentView().remove(getLabelView());
			labelView.delete();
			labelView = null;
		}
		else if (shapeNode.hasText() && labelView == null) {
			labelView = new JLabelView<O>(getNode(), getController(), this);
			if (getParentView() != null) {
				getParentView().add(getLabelView(), getLayer(), -1);
			}
		}
	}

	public Integer getLayer() {
		if (shapeNode.getGraphicalRepresentation() == null) {
			return DianaConstants.INITIAL_LAYER;
		}
		return DianaConstants.INITIAL_LAYER + shapeNode.getGraphicalRepresentation().getLayer();
	}

	@Override
	public void paint(Graphics g) {

		if (isDeleted()) {
			return;
		}

		// When exporting to SVG, ignore caching and just paint
		if (g instanceof SVGGraphics2D) {
			doPaint(g);
			return;
		}

		if (getPaintManager().isPaintingCacheEnabled()) {
			if (getDrawingView().isBuffering()) {
				// Buffering painting
				if (getPaintManager().isTemporaryObject(shapeNode)) {
					// This object is declared to be a temporary object, to be redrawn
					// continuously, so we need to ignore it: do nothing
					if (DianaPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
						DianaPaintManager.paintPrimitiveLogger.fine("JShapeView: buffering paint, ignore: " + shapeNode);
					}
				}
				else {
					if (DianaPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
						DianaPaintManager.paintPrimitiveLogger
								.fine("JShapeView: buffering paint, draw: " + shapeNode + " clip=" + g.getClip());
					}
					doPaint(g);
				}
			}
			else {
				if (!getPaintManager().renderUsingBuffer((Graphics2D) g, g.getClipBounds(), shapeNode, getScale())) {
					doPaint(g);
				}

				/*
				// Use buffer
				Image buffer = getPaintManager().getPaintBuffer();
				Rectangle localViewBounds = g.getClipBounds();
				Rectangle viewBoundsInDrawingView = GraphicalRepresentation.convertRectangle(getGraphicalRepresentation(), localViewBounds, getDrawingGraphicalRepresentation(), getScale());
				//System.out.println("SHAPEVIEW  Paint buffer "+g.getClipBounds());
				Point dp1 = localViewBounds.getLocation();
				Point dp2 = new Point(localViewBounds.x+localViewBounds.width,localViewBounds.y+localViewBounds.height);
				Point sp1 = viewBoundsInDrawingView.getLocation();
				Point sp2 = new Point(viewBoundsInDrawingView.x+viewBoundsInDrawingView.width,viewBoundsInDrawingView.y+viewBoundsInDrawingView.height);
				if (DianaPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE))
					DianaPaintManager.paintPrimitiveLogger.fine("JShapeView: use image buffer, copy area from "+sp1+"x"+sp2+" to "+dp1+"x"+dp2);
				g.drawImage(buffer,
						dp1.x,dp1.y,dp2.x,dp2.y,
						sp1.x,sp1.y,sp2.x,sp2.y,
						null);
				 */
			}
		}
		else {
			// Normal painting
			doPaint(g);
		}

		// getGraphicalRepresentation().paint(g,getController());
		// super.paint(g);
	}

	private void doPaint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		DrawUtils.turnOnAntiAlising(g2);
		DrawUtils.setRenderQuality(g2);
		DrawUtils.setColorRenderQuality(g2);
		graphics.createGraphics(g2/*, controller*/);
		shapeNode.paint(graphics);
		graphics.releaseGraphics();
		super.paint(g);
	}

	@Override
	public AbstractDianaEditor<?, SwingViewFactory, JComponent> getController() {
		return controller;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {

		// System.out.println("Received " + evt.getPropertyName() + " for " + getNode().getText());

		if (isDeleted) {
			logger.warning("Received notifications for deleted view: " + evt);
			return;
		}

		if (evt.getPropertyName().equals(NodeDeleted.EVENT_NAME) && !isDeleted()) {
			// System.out.println("Je recois bien l'ordre de deletion, je supprime maintenant !!!");
			delete();
			return;
		}

		if ((!evt.getPropertyName().equals(NodeDeleted.EVENT_NAME)) && getNode().isDeleted()) {
			logger.warning("Received notifications for deleted ShapeNode " + evt);
			return;
		}

		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> propertyChange(evt));
		}
		else {
			// logger.info("Received for " + getNode().getDrawable() + " in JShapeView: " + evt.getPropertyName() + " evt=" + evt);
			if (evt.getPropertyName() == null) {
				return;
			}

			if (evt.getPropertyName().equals(GraphNodeImpl.GRAPH_NEEDS_TO_BE_REDRAWN)) {
				// Graph updated
				getPaintManager().repaint(this);
			}

			if (evt.getPropertyName().equals(DrawingTreeNode.GRAPHICAL_REPRESENTATION_KEY)) {
				// GR changed
				relocateAndResizeView();
				if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().removeFromTemporaryObjects(shapeNode);
					getPaintManager().invalidate(shapeNode);
					getPaintManager().repaint(getParentView());
				}
			}

			if (evt.getPropertyName().equals(ShapeGraphicalRepresentation.BACKGROUND_STYLE_TYPE_KEY)) {
				// System.out.println("Received BACKGROUND_STYLE changed !");
			}

			if (evt.getPropertyName().equals(NodeAdded.EVENT_NAME)) {
				handleNodeAdded((DrawingTreeNode<?, ?>) evt.getNewValue());
			}
			else if (evt.getPropertyName().equals(NodeRemoved.EVENT_NAME)) {
				handleNodeRemoved((DrawingTreeNode<?, ?>) evt.getOldValue(), (ContainerNode<?, ?>) evt.getNewValue());
			}
			else if (evt.getPropertyName().equals(ControlAreasChange.EVENT_NAME)) {
				// Updating control areas
				relocateAndResizeView();
				if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().invalidate(shapeNode);
					getPaintManager().invalidate(shapeNode.getParentNode());
					getPaintManager().repaint(getParentView());
				}

				/*setDoubleBuffered(false);
				getPaintManager().addToTemporaryObjects(shapeNode);
				getPaintManager().invalidate(shapeNode);
				getPaintManager().repaint(getParentView());
				setDoubleBuffered(true);
				getPaintManager().removeFromTemporaryObjects(shapeNode);*/

				if (getParentView() != null) {
					getPaintManager().repaint(this);
				}
			}
			else if (evt.getPropertyName().equals(ObjectWillMove.EVENT_NAME)) {
				if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().addToTemporaryObjects(shapeNode);
					getPaintManager().invalidate(shapeNode);
				}
			}
			else if (evt.getPropertyName().equals(ObjectMove.PROPERTY_NAME)) {
				relocateView();
				if (getParentView() != null) {
					getPaintManager().repaint(this);
				}
			}
			else if (evt.getPropertyName().equals(ObjectHasMoved.EVENT_NAME)) {
				if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().removeFromTemporaryObjects(shapeNode);
					getPaintManager().invalidate(shapeNode);
					getPaintManager().repaint(getParentView());
				}
			}
			else if (evt.getPropertyName().equals(ObjectWillResize.EVENT_NAME)) {
				if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().addToTemporaryObjects(shapeNode);
					getPaintManager().invalidate(shapeNode);
				}
			}
			else if (evt.getPropertyName().equals(ObjectResized.PROPERTY_NAME)) {
				// TODO: (sylvain) those two cases may be joined ??? Please check
				resizeView();
				/*if (getParentView() != null) {
					getParentView().revalidate();
					getPaintManager().repaint(this);
				}*/
				if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().removeFromTemporaryObjects(shapeNode);
					getPaintManager().invalidate(shapeNode);
					getPaintManager().repaint(getParentView());
				}
			}
			else if (evt.getPropertyName().equals(ObjectHasResized.EVENT_NAME)
					|| evt.getPropertyName().equals(ContainerGraphicalRepresentation.WIDTH_KEY)
					|| evt.getPropertyName().equals(ContainerGraphicalRepresentation.HEIGHT_KEY)) {
				resizeView();
				if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().removeFromTemporaryObjects(shapeNode);
					getPaintManager().invalidate(shapeNode);
					getPaintManager().repaint(getParentView());
				}
			}
			else if (evt.getPropertyName().equals(ShapeGraphicalRepresentation.X_KEY)
					|| evt.getPropertyName().equals(ShapeGraphicalRepresentation.Y_KEY)) {
				// System.out.println("Relocating view");
				relocateView();
				if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().removeFromTemporaryObjects(shapeNode);
					getPaintManager().invalidate(shapeNode);
					getPaintManager().repaint(getParentView());
				}
			}
			else if (evt.getPropertyName().equals(ShapeNeedsToBeRedrawn.EVENT_NAME)) {
				getPaintManager().invalidate(shapeNode);
				getPaintManager().repaint(this);
			}
			else if (evt.getPropertyName().equals(ShapeChanged.EVENT_NAME)) {
				getPaintManager().invalidate(shapeNode);
				getPaintManager().repaint(this);
			}
			else if (evt.getPropertyName().equals(GraphicalRepresentation.LAYER.getName())) {
				updateLayer();
				if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(shapeNode)) {
					getPaintManager().invalidate(shapeNode);
				}
				getPaintManager().repaint(this);

			}
			else if (evt.getPropertyName().equals(DrawingTreeNode.IS_FOCUSED.getName())) {
				// if (shapeNode.getHasFocusedForegroundStyle() || shapeNode.getHasFocusedBackgroundStyle()) {
				getPaintManager().invalidate(shapeNode);
				// }
				getPaintManager().repaint(this);
			}
			else if (evt.getPropertyName().equals(DrawingTreeNode.IS_LONG_TIME_FOCUSED.getName())) {
				// if (shapeNode.getHasFocusedForegroundStyle() || shapeNode.getHasFocusedBackgroundStyle()) {
				getPaintManager().invalidate(shapeNode);
				// }
				getPaintManager().repaint(this);
			}
			else if (evt.getPropertyName().equals(GraphicalRepresentation.TEXT.getName())) {
				// System.out.println("Updating label view");
				// updateLabelView();
				getPaintManager().invalidate(shapeNode);
				getPaintManager().repaint(this);
			}
			else if (evt.getPropertyName().equals(DrawingTreeNode.IS_SELECTED.getName())) {

				if (getParent() != null) {
					getParent().moveToFront(this);
				}
				if (getParent() != null && getLabelView() != null) {
					getParent().moveToFront(getLabelView());
				}
				if (shapeNode.getHasSelectedForegroundStyle() || shapeNode.getHasSelectedBackgroundStyle()) {
					getPaintManager().invalidate(shapeNode);
				}
				getPaintManager().repaint(this);
				if (shapeNode.getIsSelected()) {
					requestFocusInWindow();
					// requestFocus();
				}
			}
			else if (evt.getPropertyName().equals(GraphicalRepresentation.IS_VISIBLE.getName())) {
				updateVisibility();
				if (getPaintManager().isPaintingCacheEnabled()) {
					if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(shapeNode)) {
						getPaintManager().invalidate(shapeNode);
					}
				}
				getPaintManager().repaint(this);

			}
			else {
				// revalidate();
				if (getPaintManager().isPaintingCacheEnabled()) {
					if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(shapeNode)) {
						getPaintManager().invalidate(shapeNode);
					}
				}
				getPaintManager().repaint(this);
				// revalidate();
				// getPaintManager().repaint(this);
			}
		}
	}

	@Override
	public JLabelView<O> getLabelView() {
		return labelView;
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		if (getController() instanceof DianaInteractiveViewer) {
			return ((DianaInteractiveViewer<?, SwingViewFactory, JComponent>) getController()).getToolTipText();
		}
		return super.getToolTipText(event);
	}

	private BufferedImage screenshot;

	public BufferedImage getScreenshot() {
		if (screenshot == null) {
			captureScreenshot();
		}
		return screenshot;
	}

	private void captureScreenshot() {
		JComponent lbl = this;
		getPaintManager().disablePaintingCache();
		try {
			Rectangle bounds = new Rectangle(getBounds());
			if (getLabelView() != null) {
				bounds = bounds.union(getLabelView().getBounds());
			}
			GraphicsConfiguration gc = getGraphicsConfiguration();
			if (gc == null) {
				gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			}
			screenshot = gc.createCompatibleImage(bounds.width, bounds.height, Transparency.TRANSLUCENT);// buffered image
			// reference passing
			// the label's ht
			// and width
			Graphics2D graphics = screenshot.createGraphics();// creating the graphics for buffered image
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)); // Sets the Composite for the Graphics2D
			// context
			lbl.print(graphics); // painting the graphics to label
			/*if (this.getGraphicalRepresentation().getBackground() instanceof BackgroundImage) {
				graphics.drawImage(((BackgroundImage)this.getGraphicalRepresentation().getBackground()).getImage(),0,0,null);
			}*/
			if (getLabelView() != null) {
				Rectangle r = getLabelView().getBounds();
				getLabelView().print(graphics.create(r.x - bounds.x, r.y - bounds.y, r.width, r.height));
			}
			graphics.dispose();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Captured image on " + this);
			}
		} finally {
			getPaintManager().enablePaintingCache();
		}
	}

	@Override
	public void stopLabelEdition() {
		getLabelView().stopEdition();
	}

}
