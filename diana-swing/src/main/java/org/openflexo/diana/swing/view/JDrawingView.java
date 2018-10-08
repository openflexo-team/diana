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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.openflexo.diana.Drawing;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.GeometricGraphicalRepresentation;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.DianaInteractiveEditor;
import org.openflexo.diana.control.DianaInteractiveViewer;
import org.openflexo.diana.control.DianaInteractiveEditor.EditorTool;
import org.openflexo.diana.control.actions.RectangleSelectingAction;
import org.openflexo.diana.control.tools.DianaPalette;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.impl.DianaCachedModelFactory;
import org.openflexo.diana.notifications.DrawingNeedsToBeRedrawn;
import org.openflexo.diana.notifications.GeometryModified;
import org.openflexo.diana.notifications.NodeAdded;
import org.openflexo.diana.notifications.NodeDeleted;
import org.openflexo.diana.notifications.NodeRemoved;
import org.openflexo.diana.notifications.ObjectResized;
import org.openflexo.diana.swing.SwingEditorDelegate;
import org.openflexo.diana.swing.SwingViewFactory;
import org.openflexo.diana.swing.control.JFocusRetriever;
import org.openflexo.diana.swing.control.tools.JDianaPalette;
import org.openflexo.diana.swing.control.tools.JDrawConnectorToolController;
import org.openflexo.diana.swing.graphics.DrawUtils;
import org.openflexo.diana.swing.graphics.JDianaConnectorGraphics;
import org.openflexo.diana.swing.graphics.JDianaDrawingGraphics;
import org.openflexo.diana.swing.graphics.JDianaGeometricGraphics;
import org.openflexo.diana.swing.graphics.JDianaGraphics;
import org.openflexo.diana.swing.graphics.JDianaShapeGraphics;
import org.openflexo.diana.swing.paint.DianaPaintManager;
import org.openflexo.diana.view.DrawingView;
import org.openflexo.diana.view.DianaContainerView;
import org.openflexo.diana.view.DianaView;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.swing.MouseResizer;

/**
 * The JDrawingView is the SWING implementation of the root pane of a Diana graphical editor<br>
 * The managing of the JDrawingView is performed by the {@link AbstractDianaEditor}.
 * 
 * @author sylvain
 * 
 * @param <M>
 *            the type of represented model
 */
@SuppressWarnings("serial")
public class JDrawingView<M> extends JDianaLayeredView<M> implements Autoscroll, DrawingView<M, JLayeredPane> {

	private static final Logger logger = Logger.getLogger(JDrawingView.class.getPackage().getName());

	private final Drawing<M> drawing;
	// private Map<DrawingTreeNode<?, ?>, DianaView<?,?>> contents;
	private final AbstractDianaEditor<M, SwingViewFactory, JComponent> controller;
	private final JFocusRetriever _focusRetriever;
	private final DianaPaintManager _paintManager;

	private MouseResizer resizer;
	private final DianaViewMouseListener mouseListener;

	protected JDianaDrawingGraphics graphics;

	private static DianaCachedModelFactory PAINT_FACTORY = null;

	static {
		try {
			PAINT_FACTORY = new DianaCachedModelFactory();
		} catch (ModelDefinitionException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	private final Rectangle drawnRectangle = new Rectangle();
	private BufferedImage capturedDraggedNodeImage;
	private Point capturedNodeLocation;
	private Point dragOrigin;

	private long cumulatedRepaintTime = 0;

	private boolean isBuffering = false;
	private boolean bufferingHasBeenStartedAgain = false;

	private boolean paintTemporary;

	private RectangleSelectingAction _rectangleSelectingAction;

	private static final int margin = 20;

	private boolean isDeleted = false;

	public JDrawingView(AbstractDianaEditor<M, SwingViewFactory, JComponent> controller) {
		this.controller = controller;
		drawing = controller.getDrawing();
		drawing.getRoot().getGraphicalRepresentation().updateBindingModel();
		// contents = new Hashtable<DrawingTreeNode<?, ?>, DianaView<?,?>>();
		graphics = new JDianaDrawingGraphics(drawing.getRoot(), this);
		_focusRetriever = new JFocusRetriever(this);
		if (drawing.getRoot().getGraphicalRepresentation().isResizable()) {
			resizer = new DrawingViewResizer();
		}
		mouseListener = controller.getDianaFactory().makeViewMouseListener(drawing.getRoot(), this, controller);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		installKeyBindings();
		resizeView();
		drawing.getRoot().getPropertyChangeSupport().addPropertyChangeListener(this);

		// graphics = new JDianaDrawingGraphics(drawing.getRoot());

		for (DrawingTreeNode<?, ?> dtn : drawing.getRoot().getChildNodes()) {
			if (dtn instanceof GeometricNode<?>) {
				((GeometricNode<?>) dtn).getPropertyChangeSupport().addPropertyChangeListener(this);
			}
		}

		/*if (getController() instanceof DianaInteractiveEditor) {
			if (((DianaInteractiveEditor<?, ?, ?>) controller).getPalettes() != null) {
				for (DrawingPalette p : ((DianaInteractiveEditor<?, ?, ?>) controller).getPalettes()) {
					activatePalette(p);
				}
			}
		}*/

		updateBackground();
		setOpaque(true);

		_paintManager = new DianaPaintManager(this);

		setToolTipText(getClass().getSimpleName() + hashCode());
		// setDoubleBuffered(true);
		setFocusable(true);
		// GPO: no LayoutManager here, so next line is useless?
		revalidate();
	}

	@Override
	public JDianaDrawingGraphics getDianaGraphics() {
		return graphics;
	}

	@Override
	public DrawingTreeNode<M, ?> getNode() {
		return drawing.getRoot();
	}

	public SwingEditorDelegate getDelegate() {
		return (SwingEditorDelegate) getController().getDelegate();
	}

	private void installKeyBindings() {
		if (getController() instanceof DianaInteractiveViewer) {
			getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "move_left");
			getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "move_right");
			getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "move_up");
			getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "move_down");
			getActionMap().put("move_left", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getDelegate().leftKeyPressed();
				}
			});
			getActionMap().put("move_right", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getDelegate().rightKeyPressed();
				}
			});
			getActionMap().put("move_up", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getDelegate().upKeyPressed();
				}
			});
			getActionMap().put("move_down", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getDelegate().downKeyPressed();
				}
			});
		}
	}

	/* (non-Javadoc)
	 * @see org.openflexo.diana.view.DrawingView#getDrawing()
	 */
	@Override
	public Drawing<M> getDrawing() {
		return drawing;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.diana.view.DrawingView#getDrawable()
	 */
	@Override
	public M getDrawable() {
		return getModel();
	}

	public M getModel() {
		return drawing.getModel();
	}

	@Override
	public JDrawingView<M> getDrawingView() {
		return this;
	}

	@Override
	public JLabelView<M> getLabelView() {
		return null;
	}

	public DrawingGraphicalRepresentation getGraphicalRepresentation() {
		return drawing.getRoot().getGraphicalRepresentation();
	}

	@Override
	public double getScale() {
		return getController().getScale();
	}

	public Integer getLayer() {
		return JLayeredPane.DEFAULT_LAYER;
	}

	public Dimension getComputedMinimumSize() {
		Rectangle r = getBounds();
		for (int i = 0; i < getComponents().length; i++) {
			Component c = getComponents()[i];
			r = r.union(c.getBounds());
		}
		return r.getSize();
	}

	@Override
	public void rescale() {
		for (DianaView<?, ?> v : controller.getContents().values()) {
			if (!(v instanceof JDrawingView)) {
				v.rescale();
			}
			if (v instanceof JDianaView) {
				if (((JDianaView<?, ?>) v).getLabelView() != null) {
					((JDianaView<?, ?>) v).getLabelView().rescale();
				}
			}
		}
		resizeView();
		// revalidate();
		getPaintManager().invalidate(drawing.getRoot());
		getPaintManager().repaint(this);
	}

	private void resizeView() {
		int offset = getGraphicalRepresentation().isResizable() ? 20 : 0;
		setPreferredSize(new Dimension(drawing.getRoot().getViewWidth(getController().getScale()) + offset,
				drawing.getRoot().getViewHeight(getController().getScale()) + offset));
		if (getParent() != null) {
			getParent().doLayout();
		}
	}

	private void updateBackground() {
		if (getGraphicalRepresentation().getDrawWorkingArea()) {
			setBackground(Color.GRAY);
		}
		else {
			setBackground(getGraphicalRepresentation().getBackgroundColor());
		}
	}

	/* (non-Javadoc)
	 * @see org.openflexo.diana.view.DrawingView#getController()
	 */
	@Override
	public AbstractDianaEditor<M, ?, JComponent> getController() {
		return controller;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		if (isDeleted) {
			logger.warning("Received notifications for deleted view: " + evt);
			return;
		}
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> propertyChange(evt));
		}
		else {
			// logger.info("Received: " + evt.getPropertyName() + " evt=" + evt);

			if (evt.getPropertyName().equals(NodeAdded.EVENT_NAME)) {
				handleNodeAdded((DrawingTreeNode<?, ?>) evt.getNewValue());
			}
			else if (evt.getPropertyName().equals(NodeRemoved.EVENT_NAME)) {
				handleNodeRemoved((DrawingTreeNode<?, ?>) evt.getOldValue(), (ContainerNode<?, ?>) evt.getNewValue());
			}
			else if (evt.getPropertyName().equals(NodeDeleted.EVENT_NAME)) {
				delete();
			}
			else if (evt.getPropertyName().equals(ObjectResized.PROPERTY_NAME)) {
				rescale();
				getPaintManager().invalidate(getDrawing().getRoot());
				getPaintManager().repaint(this);
			}
			else if (evt.getPropertyName().equals(DrawingGraphicalRepresentation.BACKGROUND_COLOR.getName())) {
				getPaintManager().invalidate(getDrawing().getRoot());
				updateBackground();
				getPaintManager().repaint(this);
			}
			else if (evt.getPropertyName().equals(DrawingGraphicalRepresentation.DRAW_WORKING_AREA.getName())) {
				getPaintManager().invalidate(getDrawing().getRoot());
				updateBackground();
				getPaintManager().repaint(this);
			}
			else if (evt.getPropertyName().equals(DrawingGraphicalRepresentation.WIDTH.getName())) {
				rescale();
				getPaintManager().invalidate(getDrawing().getRoot());
				getPaintManager().repaint(this);
			}
			else if (evt.getPropertyName().equals(DrawingGraphicalRepresentation.HEIGHT.getName())) {
				rescale();
				getPaintManager().invalidate(getDrawing().getRoot());
				getPaintManager().repaint(this);
			}
			else if (evt.getPropertyName().equals(DrawingGraphicalRepresentation.IS_RESIZABLE.getName())) {
				if (getDrawing().getRoot().getGraphicalRepresentation().isResizable()) {
					removeMouseListener(mouseListener); // We remove the mouse
					// listener, so that the
					// mouse resizer is
					// called before
					// mouseListener
					if (resizer == null) {
						resizer = new DrawingViewResizer();
					}
					else {
						addMouseListener(resizer);
					}
					addMouseListener(mouseListener);
				}
				else {
					removeMouseListener(resizer);
				}
			}
			else if (evt.getPropertyName().equals(DrawingTreeNode.IS_FOCUSED.getName())) {
				if (evt.getSource() instanceof GeometricNode) {
					// Painting a geometric node being focused or unfocused
					// TODO: optimize this later
					getPaintManager().invalidate(getDrawing().getRoot());
					getPaintManager().repaint(this);
				}
			}
			else if (evt.getPropertyName().equals(GeometryModified.EVENT_NAME)) {

				if (evt.getSource() instanceof GeometricNode) {
					// Painting a geometric node being focused or unfocused
					// TODO: optimize this later
					getPaintManager().invalidate(getDrawing().getRoot());
					getPaintManager().repaint(this);
				}
			}
			else if (evt.getPropertyName().equals(DrawingNeedsToBeRedrawn.EVENT_NAME)) {
				getPaintManager().invalidate(getDrawing().getRoot());
				getPaintManager().repaint(this);
			}
			else if (evt.getSource() instanceof GeometricGraphicalRepresentation) {
				getPaintManager().invalidate(getDrawing().getRoot());
				getPaintManager().repaint(this);
			}
			else if (evt.getPropertyName().equals(ContainerNode.LAYOUT_DECORATION_KEY)) {
				getPaintManager().invalidate(getDrawing().getRoot());
				getPaintManager().repaint(this);
			}
		}
	}

	@Override
	public void setRectangleSelectingAction(RectangleSelectingAction action) {
		_rectangleSelectingAction = action;
	}

	@Override
	public void resetRectangleSelectingAction() {
		_rectangleSelectingAction = null;
		getPaintManager().repaint(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		DrawUtils.turnOnAntiAlising(g2);
		DrawUtils.setRenderQuality(g2);
		DrawUtils.setColorRenderQuality(g2);
		graphics.createGraphics(g2/*, controller*/);
		getDrawing().getRoot().paint(graphics);
		graphics.releaseGraphics();
	}

	/**
	 * 
	 * @param g
	 *            graphics on which buffering will be performed
	 */
	public void prepareForBuffering(Graphics2D g) {
		isBuffering = true;
	}

	public boolean isBuffering() {
		return isBuffering;
	}

	/**
	 * Perform a new buffering (cancel currently performed buffering)
	 */
	protected void startBufferingAgain() {
		bufferingHasBeenStartedAgain = true;
	}

	private void forcePaintTemporaryObjects(ContainerNode<?, ?> parentNode, Graphics g) {
		forcePaintObjects(parentNode, g, true);
	}

	/*private void forcePaintObjects(ContainerNode<?, ?> parentNode, Graphics g) {
		forcePaintObjects(parentNode, g, false);
	}*/

	private void forcePaintObjects(ContainerNode<?, ?> parentNode, final Graphics g, boolean temporaryObjectsOnly) {
		List<? extends DrawingTreeNode<?, ?>> containedGR = parentNode.getChildNodes();
		if (containedGR == null) {
			return;
		}
		paintTemporary = true;
		for (DrawingTreeNode<?, ?> node : new ArrayList<DrawingTreeNode<?, ?>>(containedGR)) {
			if (node.shouldBeDisplayed() && (!temporaryObjectsOnly || getPaintManager().isTemporaryObject(node)
					|| getPaintManager().containsTemporaryObject(node))) {
				JDianaView<?, ?> view = viewForNode(node);
				if (view == null) {
					continue;
				}
				Component viewAsComponent = (Component) view;
				JDianaGraphics childGraphics = view.getDianaGraphics();
				Graphics2D g2d = (Graphics2D) g.create(viewAsComponent.getX(), viewAsComponent.getY(), viewAsComponent.getWidth(),
						viewAsComponent.getHeight());
				if (getPaintManager().isTemporaryObject(node) || !temporaryObjectsOnly) {
					if (DianaPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
						DianaPaintManager.paintPrimitiveLogger
								.fine("JDrawingView: continuous painting, paint " + node + " temporaryObjectsOnly=" + temporaryObjectsOnly);
					}
					childGraphics.createGraphics(g2d/*, controller*/);
					if (node instanceof ShapeNode) {
						((ShapeNode<?>) node).paint((JDianaShapeGraphics) childGraphics);
					}
					else if (node instanceof ConnectorNode) {
						((ConnectorNode<?>) node).paint((JDianaConnectorGraphics) childGraphics);
					}
					if (node instanceof GeometricNode) {
						((GeometricNode<?>) node).paint((JDianaGeometricGraphics) childGraphics);
					}
					childGraphics.releaseGraphics();
					JLabelView<?> labelView = view.getLabelView();
					if (labelView != null) {
						Graphics labelGraphics = g.create(labelView.getX(), labelView.getY(), labelView.getWidth(), labelView.getHeight());
						// Tricky area: if label is currently being edited,
						// call to paint is required here
						// to paint text component above buffer image.
						// Otherwise, just call doPaint to force paint label
						labelView.paint(labelGraphics);
						labelGraphics.dispose();
					}
					// do the job for childs
					if (node instanceof ContainerNode) {
						forcePaintObjects((ContainerNode<?, ?>) node, g2d, false);
					}
				}
				else {
					// do the job for childs
					if (node instanceof ContainerNode) {
						forcePaintObjects((ContainerNode<?, ?>) node, g2d, true);
					}
				}
			}
		}
		paintTemporary = false;
	}

	public boolean isPaintTemporary() {
		return paintTemporary;
	}

	@Override
	public void paint(Graphics g) {
		if (isDeleted()) {
			return;
		}

		long startTime = System.currentTimeMillis();
		if (getPaintManager().isPaintingCacheEnabled()) {
			if (isBuffering) {
				// Buffering painting
				if (DianaPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
					DianaPaintManager.paintPrimitiveLogger.fine("JDrawingView: Paint for image buffering area, clip=" + g.getClipBounds());
				}
				super.paint(g);
				if (bufferingHasBeenStartedAgain) {
					g.clearRect(0, 0, drawing.getRoot().getViewWidth(getController().getScale()),
							drawing.getRoot().getViewHeight(getController().getScale()));
					super.paint(g);
					bufferingHasBeenStartedAgain = false;
				}
			}
			else {
				if (getPaintManager().renderUsingBuffer((Graphics2D) g, g.getClipBounds(), drawing.getRoot(), getScale())) {
					// Now, we still have to paint objects that are declared
					// to be temporary and continuously to be redrawn
					forcePaintTemporaryObjects(drawing.getRoot(), g);
				}
				else {
					// This failed for some reasons (eg rendering request
					// outside cached image)
					// Skip buffering and perform normal rendering
					super.paint(g);
				}
				paintCapturedNode(g);
			}
		}
		else {
			// Normal painting
			super.paint(g);
		}

		paintGeometricObjects(g);

		if (!isBuffering) {

			Graphics2D g2 = (Graphics2D) g;
			DrawUtils.turnOnAntiAlising(g2);
			DrawUtils.setRenderQuality(g2);
			DrawUtils.setColorRenderQuality(g2);
			graphics.createGraphics(g2/*, controller*/);

			if (getController() instanceof DianaInteractiveViewer) {
				// Don't paint those things in case of buffering

				// System.out.println("focused: " + ((DianaInteractiveViewer<?, ?, ?>) getController()).getFocusedObjects());

				if (((DianaInteractiveViewer<?, ?, ?>) getController()).getFocusedObjects().size() == 0) {
					paintFocused(getDrawing().getRoot(), graphics);
				}

				for (DrawingTreeNode<?, ?> o : new ArrayList<DrawingTreeNode<?, ?>>(
						((DianaInteractiveViewer<?, ?, ?>) getController()).getFocusedObjects())) {
					// logger.info("Paint focused " + o);
					paintFocused(o, graphics);
				}

				for (DrawingTreeNode<?, ?> o : new ArrayList<DrawingTreeNode<?, ?>>(
						((DianaInteractiveViewer<?, ?, ?>) getController()).getSelectedObjects())) {
					// logger.info("Paint selected " + o + "shouldBeDisplayed=" + o.shouldBeDisplayed());
					if (o.shouldBeDisplayed()) {
						paintSelected(o, graphics);
					}
				}
			}

			/*
			 * if ((getController().getFocusedFloatingLabel() != null &&
			 * getController().getFocusedFloatingLabel().hasFloatingLabel())) {
			 * paintFocusedFloatingLabel
			 * (getController().getFocusedFloatingLabel(), g); }
			 */

			if (getController() instanceof DianaInteractiveEditor) {
				if (((DianaInteractiveEditor<?, ?, ?>) getController()).getCurrentTool() == EditorTool.DrawCustomShapeTool) {
					// logger.info("Painting current edited shape");
					paintCurrentEditedShape(graphics);
				}
				else if (((DianaInteractiveEditor<?, ?, ?>) getController()).getCurrentTool() == EditorTool.DrawConnectorTool) {
					// logger.info("Painting current edited shape");
					paintCurrentDrawnConnector(graphics);
				}
			}

			graphics.releaseGraphics();

			if (_rectangleSelectingAction != null) {
				_rectangleSelectingAction.paint(g, getController());
			}

		}

		// Do it once only !!!
		isBuffering = false;

		long endTime = System.currentTimeMillis();
		// System.out.println("END paint() in JDrawingView, this took "+(endTime-startTime)+" ms");

		cumulatedRepaintTime += endTime - startTime;

		if (DianaPaintManager.paintStatsLogger.isLoggable(Level.FINE)) {
			DianaPaintManager.paintStatsLogger.fine("PAINT " + getName() + " clip=" + g.getClip() + " time=" + (endTime - startTime)
					+ "ms cumulatedRepaintTime=" + cumulatedRepaintTime + " ms");
		}
	}

	private void paintGeometricObjects(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		DrawUtils.turnOnAntiAlising(g2);
		DrawUtils.setRenderQuality(g2);
		DrawUtils.setColorRenderQuality(g2);

		List<GeometricNode<?>> geomList = new ArrayList<GeometricNode<?>>();
		for (Object n : drawing.getRoot().getChildNodes()) {
			if (n instanceof GeometricNode) {
				geomList.add((GeometricNode<?>) n);
			}
		}
		if (geomList.size() > 0) {
			Collections.sort(geomList, new Comparator<GeometricNode<?>>() {
				@Override
				public int compare(GeometricNode<?> o1, GeometricNode<?> o2) {
					return o1.getGraphicalRepresentation().getLayer() - o2.getGraphicalRepresentation().getLayer();
				}
			});
			for (GeometricNode<?> gn : geomList) {
				// TODO: use the same graphics, just change DrawingTreeNode
				// JFGEGeometricGraphics geometricGraphics = new JFGEGeometricGraphics(gn, this);
				JDianaGeometricGraphics geometricGraphics = getGeometricGraphics(gn);
				geometricGraphics.createGraphics(g2/*, controller*/);
				gn.paint(geometricGraphics);
				geometricGraphics.releaseGraphics();
				geometricGraphics.delete();
			}
		}
	}

	private JDianaGeometricGraphics getGeometricGraphics(GeometricNode<?> gn) {
		JDianaGeometricGraphics returned = geometricGraphics.get(gn);
		if (returned == null) {
			returned = new JDianaGeometricGraphics(gn, this);
			geometricGraphics.put(gn, returned);
		}
		returned.setDrawingTreeNode(gn);
		return returned;
	}

	private Map<GeometricNode<?>, JDianaGeometricGraphics> geometricGraphics = new HashMap<>();

	private void paintFocusedFloatingLabel(DrawingTreeNode<?, ?> focusedFloatingLabel, Graphics g) {
		Color color = Color.BLACK;
		if (focusedFloatingLabel.getIsSelected()) {
			color = getGraphicalRepresentation().getSelectionColor();
		}
		else if (focusedFloatingLabel.getIsFocused()) {
			color = getGraphicalRepresentation().getFocusColor();
		}
		else {
			return;
		}
		JDianaView<?, ?> view = viewForNode(focusedFloatingLabel);
		if (view != null) {
			JLabelView<?> labelView = view.getLabelView();
			if (labelView != null) {
				Point p1 = SwingUtilities.convertPoint(labelView, new Point(0, labelView.getHeight() / 2), this);
				Point p2 = SwingUtilities.convertPoint(labelView, new Point(labelView.getWidth(), labelView.getHeight() / 2), this);
				paintControlPoint(p1, color, g);
				paintControlPoint(p2, color, g);
			}
		}
		else {
			// logger.warning("Could not find view for node " + focusedFloatingLabel);
		}
	}

	public void paintControlArea(ControlArea<?> ca, JDianaDrawingGraphics graphics) {
		Rectangle invalidatedArea = ca.paint(graphics);
		if (invalidatedArea != null) {
			getPaintManager().addTemporaryRepaintArea(invalidatedArea, this);
		}
	}

	private void paintControlPoint(Point location, Color color, Graphics g) {
		int size = DianaConstants.CONTROL_POINT_SIZE;
		g.setColor(color);
		Rectangle r = new Rectangle(location.x - size, location.y - size, size * 2, size * 2);
		g.fillRect(r.x, r.y, r.width, r.height);
		getPaintManager().addTemporaryRepaintArea(r, this);
	}

	private void paintSelected(DrawingTreeNode<?, ?> selected, JDianaDrawingGraphics graphics) {

		if (selected.isDeleted()) {
			logger.warning("Cannot paint for a deleted GR");
			return;
		}

		if (!selected.getGraphicalRepresentation().getDrawControlPointsWhenSelected()) {
			// Don't paint control points in this case
			return;
		}

		Graphics2D oldGraphics = graphics.cloneGraphics();
		graphics.setDefaultForeground(PAINT_FACTORY.makeForegroundStyle(getGraphicalRepresentation().getSelectionColor()));

		if (selected instanceof ShapeNode && selected != null) {
			ShapeNode<?> shapeNode = (ShapeNode<?>) selected;
			if (shapeNode.getControlAreas() != null) {
				for (ControlArea<?> ca : shapeNode.getControlAreas()) {
					if (selected.isValid()) {
						paintControlArea(ca, graphics);
					}
				}
			}
		}

		else if (selected instanceof ConnectorNode) {

			ConnectorNode<?> connectorNode = (ConnectorNode<?>) selected;
			// g.setColor(getGraphicalRepresentation().getSelectionColor());

			if (connectorNode.getStartNode() == null || connectorNode.getStartNode().isDeleted()) {
				logger.warning("Could not paint connector: start object is null or deleted");
				return;
			}

			if (connectorNode.getEndNode() == null || connectorNode.getEndNode().isDeleted()) {
				logger.warning("Could not paint connector: end object is null or deleted");
				return;
			}

			if (connectorNode.getControlAreas() != null) {
				for (ControlArea<?> ca : connectorNode.getControlAreas()) {
					if (selected.isValid()) {
						paintControlArea(ca, graphics);
					}
				}
			}
		}

		if (selected.hasFloatingLabel()) {
			paintFocusedFloatingLabel(selected, graphics.getGraphics());
		}

		graphics.releaseClonedGraphics(oldGraphics);

	}

	private void paintCurrentEditedShape(JDianaDrawingGraphics graphics) {

		// logger.info("Painting current edited shape");
		/*GeometricGraphicalRepresentation currentEditedShape = getController().getDrawShapeToolController().getCurrentEditedShapeGR();
		
		if (currentEditedShape.isDeleted()) {
			logger.warning("Cannot paint for a deleted GR");
			return;
		}*/

		if (!(getController() instanceof DianaInteractiveEditor)) {
			return;
		}

		if (!((DianaInteractiveEditor<?, ?, ?>) getController()).getDrawCustomShapeToolController().editionHasBeenStarted()) {
			return;
		}

		((JDianaGeometricGraphics) ((DianaInteractiveEditor<?, ?, ?>) getController()).getDrawCustomShapeToolController().getGraphics())
				.createGraphics(graphics.getGraphics()/*, controller*/);
		((DianaInteractiveEditor<?, ?, ?>) getController()).getDrawCustomShapeToolController().paintCurrentEditedShape();
		((JDianaGeometricGraphics) ((DianaInteractiveEditor<?, ?, ?>) getController()).getDrawCustomShapeToolController().getGraphics())
				.releaseGraphics();

		Graphics2D oldGraphics = graphics.cloneGraphics();
		graphics.setDefaultForeground(PAINT_FACTORY.makeForegroundStyle(getGraphicalRepresentation().getFocusColor()));

		for (ControlArea<?> ca : ((DianaInteractiveEditor<?, ?, ?>) getController()).getDrawCustomShapeToolController().getControlAreas()) {
			paintControlArea(ca, graphics);
		}

		graphics.releaseClonedGraphics(oldGraphics);
	}

	private void paintCurrentDrawnConnector(JDianaDrawingGraphics graphics) {

		if (!(getController() instanceof DianaInteractiveEditor)) {
			return;
		}

		JDrawConnectorToolController connectorController = (JDrawConnectorToolController) ((DianaInteractiveEditor<?, ?, ?>) getController())
				.getDrawConnectorToolController();

		if (!connectorController.editionHasBeenStarted()) {
			return;
		}

		if (connectorController.getConnectorNode() == null) {
			return;
		}

		// OK, now we paint the connector beeing drawn
		// Following code is not quite easy to understand, i try to document it well

		// We first "save" oldGraphics
		Graphics2D oldGraphics = graphics.cloneGraphics();

		// Then we obtain current Graphics2D (valid for Drawing, not for the connector node!)
		Graphics2D connectorGraphics2D = graphics.getGraphics();

		// We obtain the right AffineTransform (the Graphics2D is valid for Drawing, not for the connector node!)
		AffineTransform at = DianaUtils.convertCoordinatesAT(connectorController.getConnectorNode(), getDrawing().getRoot(), getScale());

		// We apply this transform
		connectorGraphics2D.transform(at);

		// We give the Graphics2D to the connector graphics
		connectorController.getGraphics().createGraphics(connectorGraphics2D);

		// The connector is drawn
		// connectorController.getConnectorNode().getConnector().drawConnector(connectorController.getGraphics());

		connectorController.getConnectorNode().paint(connectorController.getGraphics());

		// Graphics has been released
		connectorController.getGraphics().releaseGraphics();

		// We finally restore the original graphics for the drawing
		graphics.releaseClonedGraphics(oldGraphics);

	}

	private void paintFocused(DrawingTreeNode<?, ?> focused, JDianaDrawingGraphics graphics) {
		if (focused.isDeleted()) {
			// logger.warning("Cannot paint for a deleted DTN");
			return;
		}
		if (!focused.getGraphicalRepresentation().getDrawControlPointsWhenFocused()) {
			// Don't paint control points in this case
			return;
		}

		Graphics2D oldGraphics = graphics.cloneGraphics();
		graphics.setDefaultForeground(PAINT_FACTORY.makeForegroundStyle(getGraphicalRepresentation().getFocusColor()));

		/*if (focused instanceof RootNode) {
			if (focused.getControlAreas() != null) {
				for (ControlArea<?> ca : focused.getControlAreas()) {
					if (focused.isValid()) {
						paintControlArea(ca, graphics);
					}
				}
			}
		}*/

		if (focused instanceof ShapeNode) {
			ShapeNode<?> shapeNode = (ShapeNode<?>) focused;
			if (shapeNode.getControlAreas() != null) {
				for (ControlArea<?> ca : shapeNode.getControlAreas()) {
					if (focused.isValid()) {
						paintControlArea(ca, graphics);
					}
				}
			}
		}
		else if (focused instanceof ConnectorNode) {
			ConnectorNode<?> connectorNode = (ConnectorNode<?>) focused;

			if (connectorNode.getStartNode() == null || connectorNode.getStartNode().isDeleted()) {
				logger.warning("Could not paint connector: start object is null or deleted");
				return;
			}

			if (connectorNode.getEndNode() == null || connectorNode.getEndNode().isDeleted()) {
				logger.warning("Could not paint connector: end object is null or deleted");
				return;
			}

			if (connectorNode.getControlAreas() != null) {
				for (ControlArea<?> ca : connectorNode.getControlAreas()) {
					if (focused.isValid()) {
						paintControlArea(ca, graphics);
					}
				}
			}
		}
		if (focused.hasFloatingLabel()) {
			paintFocusedFloatingLabel(focused, graphics.getGraphics());
		}

		graphics.releaseClonedGraphics(oldGraphics);
	}

	@Override
	public <O> JDianaView<O, ? extends JComponent> viewForNode(DrawingTreeNode<O, ?> node) {
		return (JDianaView<O, ? extends JComponent>) controller.viewForNode(node);
	}

	@Override
	public <O> JShapeView<O> shapeViewForNode(ShapeNode<O> node) {
		return (JShapeView<O>) viewForNode(node);
	}

	@Override
	public <O> JConnectorView<O> connectorViewForNode(ConnectorNode<O> node) {
		return (JConnectorView<O>) viewForNode(node);
	}

	public JFocusRetriever getFocusRetriever() {
		return _focusRetriever;
	}

	public DianaViewMouseListener getMouseListener() {
		return mouseListener;
	}

	private JDianaPalette activePalette;

	@Override
	public void activatePalette(DianaPalette<?, ?> aPalette) {
		// A palette is registered, listen to drag'n'drop events
		if (aPalette instanceof JDianaPalette) {
			activePalette = (JDianaPalette) aPalette;
			logger.fine("Registering drop target");
			setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY, activePalette.buildPaletteDropListener(this, controller), true));
			for (DianaView<?, ?> v : controller.getContents().values()) {
				if (v != this) {
					v.activatePalette(aPalette);
				}
			}
		}
		else {
			logger.warning("Unexpected palette: " + aPalette);
		}
	}

	@Override
	public DianaPaintManager getPaintManager() {
		return _paintManager;
	}

	@Override
	public boolean contains(DianaView<?, ?> view) {
		if (view == null) {
			return false;
		}
		if (view == this) {
			return true;
		}
		if (((JComponent) view).getParent() != null && ((JComponent) view).getParent() instanceof DianaView) {
			return contains((DianaView<?, ?>) ((JComponent) view).getParent());
		}
		return false;
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		if (getController() instanceof DianaInteractiveViewer) {
			return ((DianaInteractiveViewer<?, ?, ?>) getController()).getToolTipText();
		}
		return super.getToolTipText(event);
	}

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public void delete() {
		// logger.info("delete() in JDrawingView");
		removeMouseListener(mouseListener);
		removeMouseMotionListener(mouseListener);

		List<DianaView<?, ?>> views = new ArrayList<DianaView<?, ?>>(controller.getContents().values());

		for (DianaView<?, ?> v : views) {
			if (v != this) {
				v.delete();
			}
			// logger.info("Deleted view "+v);
		}
		// contents.clear();

		// TODO ???
		if (getGraphicalRepresentation() != null && getGraphicalRepresentation().getPropertyChangeSupport() != null) {
			getGraphicalRepresentation().getPropertyChangeSupport().removePropertyChangeListener(this);
		}

		if (drawing.getRoot().getChildNodes() != null) {
			for (DrawingTreeNode<?, ?> dtn : drawing.getRoot().getChildNodes()) {
				if (dtn instanceof GeometricNode<?>) {
					((GeometricNode<?>) dtn).getPropertyChangeSupport().removePropertyChangeListener(this);
				}
				if (dtn instanceof ConnectorNode<?>) {
					((ConnectorNode<?>) dtn).getPropertyChangeSupport().removePropertyChangeListener(this);
				}
			}
		}

		drawing.getRoot().getPropertyChangeSupport().removePropertyChangeListener(this);

		isDeleted = true;
	}

	// This call is made on the edition drawing view
	public final void updateCapturedDraggedNodeImagePosition(DropTargetDragEvent e, JDrawingView<?> source) {
		capturedNodeLocation = SwingUtilities.convertPoint(((DropTarget) e.getSource()).getComponent(), e.getLocation(), this);
		if (source != this) {
			dragOrigin = activePalette.getPaletteView().dragOrigin; // transfer from
			// the palette
			// to the
			// edition view
		}
		if (dragOrigin == null) {
			return;
		}
		capturedNodeLocation.x -= dragOrigin.x * getScale();
		capturedNodeLocation.y -= dragOrigin.y * getScale();
		capturedDraggedNodeImage = source.capturedDraggedNodeImage;
		if (capturedNodeLocation == null || capturedDraggedNodeImage == null
				|| drawnRectangle != null && capturedNodeLocation.equals(drawnRectangle.getLocation())) {
			return;
		}
		getPaintManager().repaint(this, drawnRectangle.getBounds());
		// System.out.println("Paint: "+drawnRectangle.getBounds()+" isDoubleBuffered="+isDoubleBuffered());
		int scaledWidth = (int) (capturedDraggedNodeImage.getWidth() * getScale());
		int scaledHeight = (int) (capturedDraggedNodeImage.getHeight() * getScale());
		drawnRectangle.setRect((int) capturedNodeLocation.getX(), (int) capturedNodeLocation.getY(), scaledWidth, scaledHeight);
		getPaintManager().repaint(this, drawnRectangle.getBounds());
	}

	private void paintCapturedNode(Graphics g) {
		if (capturedDraggedNodeImage != null && drawnRectangle != null) {
			Graphics2D g2 = (Graphics2D) g;
			// g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.7f));
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.drawImage(capturedDraggedNodeImage, (int) drawnRectangle.getX(), (int) drawnRectangle.getY(),
					(int) drawnRectangle.getWidth(), (int) drawnRectangle.getHeight(), this);
		}
	}

	// This call is made on the drawing view of the palette
	public void captureDraggedNode(JShapeView<?> view, MouseEvent e) {
		capturedDraggedNodeImage = view.getScreenshot();
		dragOrigin = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), view);
	}

	public void captureDraggedNode(JShapeView<?> view, DragGestureEvent e) {
		capturedDraggedNodeImage = view.getScreenshot();
		dragOrigin = SwingUtilities.convertPoint(e.getComponent(), e.getDragOrigin(), view);
	}

	public void resetCapturedNode() {
		if (capturedDraggedNodeImage != null) {
			getPaintManager().repaint(this, drawnRectangle.getBounds());
			capturedDraggedNodeImage = null;
		}
	}

	public JDianaPalette getActivePalette() {
		return activePalette;
	}

	@Override
	public void autoscroll(Point p) {
		JScrollPane scroll = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, this);
		if (scroll == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Not inside a scroll pane, cannot scroll!");
			}
			return;
		}
		Rectangle visible = this.getVisibleRect();
		p.x -= visible.x;
		p.y -= visible.y;
		Rectangle inner = this.getParent().getBounds();
		inner.x += margin;
		inner.y += margin;
		inner.height -= 2 * margin;
		inner.width -= 2 * margin;
		if (p.x < inner.x) {// Move Left
			JScrollBar bar = scroll.getHorizontalScrollBar();
			if (bar != null) {
				if (bar.getValue() > bar.getMinimum()) {
					bar.setValue(bar.getValue() - bar.getUnitIncrement(-1));
				}
			}
		}
		else if (p.x > inner.x + inner.width) { // Move right
			JScrollBar bar = scroll.getHorizontalScrollBar();
			if (bar != null) {
				if (bar.getValue() < bar.getMaximum()) {
					bar.setValue(bar.getValue() + bar.getUnitIncrement(1));
				}
			}
		}
		if (p.y < inner.y) { // Move up
			JScrollBar bar = scroll.getVerticalScrollBar();
			if (bar != null) {
				if (bar.getValue() > bar.getMinimum()) {
					bar.setValue(bar.getValue() - bar.getUnitIncrement(-1));
				}
			}
		}
		else if (p.y > inner.y + inner.height) { // Move down
			JScrollBar bar = scroll.getVerticalScrollBar();
			if (bar != null) {
				if (bar.getValue() < bar.getMaximum()) {
					bar.setValue(bar.getValue() + bar.getUnitIncrement(1));
				}
			}
		}
	}

	@Override
	public Insets getAutoscrollInsets() {
		Rectangle outer = getBounds();
		Rectangle inner = getParent().getBounds();
		return new Insets(inner.y - outer.y + margin, inner.x - outer.x + margin, outer.height - inner.height - inner.y + outer.y + margin,
				outer.width - inner.width - inner.x + outer.x + margin);
	}

	private final class DrawingViewResizer extends MouseResizer {
		protected DrawingViewResizer() {
			super(JDrawingView.this, new MouseResizer.MouseResizerDelegate() {

				@Override
				public void resizeBy(int deltaX, int deltaY) {

				}

				@Override
				public void resizeDirectlyBy(int deltaX, int deltaY) {
					if (deltaX != 0) {
						getDrawing().getRoot().getGraphicalRepresentation()
								.setWidth(getDrawing().getRoot().getGraphicalRepresentation().getWidth() + deltaX / getScale());
					}
					if (deltaY != 0) {
						getDrawing().getRoot().getGraphicalRepresentation()
								.setHeight(getDrawing().getRoot().getGraphicalRepresentation().getHeight() + deltaY / getScale());
					}
				}
			}, ResizeMode.SOUTH, ResizeMode.EAST, ResizeMode.SOUTH_EAST);
		}

		@Override
		protected int getComponentWidth() {
			return getDrawing().getRoot().getViewWidth(getScale());
		}

		@Override
		protected int getComponentHeight() {
			return getDrawing().getRoot().getViewHeight(getScale());
		}
	}

	@Override
	public DianaContainerView<?, ?> getParentView() {
		return null;
	};

	@Override
	public void stopLabelEdition() {
		getLabelView().stopEdition();
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
			/*DianaRectangle requiredBounds = getDrawing().getRoot().getRequiredBoundsForContents();
			System.out.println("required: " + requiredBounds);
			Rectangle bounds = new Rectangle(0, 0, (int) (requiredBounds.x + requiredBounds.width),
					(int) (requiredBounds.y + requiredBounds.height));
			if (bounds.width < 100)
				bounds.width = 100;
			if (bounds.height < 100)
				bounds.height = 100;
			System.out.println("bounds: " + bounds);
			
			System.out.println("au lieu de " + (new Rectangle(getBounds())));*/
			/*if (getLabelView() != null) {
				bounds = bounds.union(getLabelView().getBounds());
			}*/

			Rectangle bounds = new Rectangle(getBounds());

			GraphicsConfiguration gc = getGraphicsConfiguration();
			if (gc == null) {
				gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			}
			// screenshot = gc.createCompatibleImage(bounds.width, bounds.height, Transparency.TRANSLUCENT);// buffered image
			screenshot = gc.createCompatibleImage(bounds.width, bounds.height, Transparency.OPAQUE);// buffered image
			// reference passing
			// the label's ht
			// and width
			Graphics2D graphics = screenshot.createGraphics();// creating the graphics for buffered image
			// graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)); // Sets the Composite for the Graphics2D
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

			DianaRectangle requiredBounds = getDrawing().getRoot().getRequiredBoundsForContents();
			requiredBounds.x = requiredBounds.x - 20;
			requiredBounds.y = requiredBounds.y - 20;
			requiredBounds.width = requiredBounds.width + 40;
			requiredBounds.height = requiredBounds.height + 40;
			if (requiredBounds.x < 0)
				requiredBounds.x = 0;
			if (requiredBounds.y < 0)
				requiredBounds.y = 0;
			if (requiredBounds.width < 100)
				requiredBounds.width = 100;
			if (requiredBounds.height < 100)
				requiredBounds.height = 100;
			Rectangle croppedBounds = new Rectangle((int) requiredBounds.x, (int) requiredBounds.y, (int) (requiredBounds.width),
					(int) (requiredBounds.height));

			croppedBounds = croppedBounds.intersection(new Rectangle(0, 0, screenshot.getWidth(), screenshot.getHeight()));
			screenshot = screenshot.getSubimage(croppedBounds.x, croppedBounds.y, croppedBounds.width, croppedBounds.height);

			if (logger.isLoggable(Level.INFO)) {
				logger.info("Cropped image to " + croppedBounds);
			}

		} finally {
			getPaintManager().enablePaintingCache();
		}
	}

}
