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

package org.openflexo.diana.control;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSourceContext;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.Drawing;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.control.notifications.ControlNotification;
import org.openflexo.diana.control.notifications.ScaleChanged;
import org.openflexo.diana.impl.DrawingImpl;
import org.openflexo.diana.view.ConnectorView;
import org.openflexo.diana.view.DianaView;
import org.openflexo.diana.view.DianaViewFactory;
import org.openflexo.diana.view.DrawingView;
import org.openflexo.diana.view.ShapeView;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.pamela.factory.EditingContext;
import org.openflexo.pamela.undo.UndoManager;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * This is the abstract core implementation of a {@link DianaEditor} representing and/or editing a {@link Drawing}
 * 
 * @author sylvain
 * 
 * @param <M>
 *            type of model beeing represented
 * @param <F>
 *            type of {@link GinaViewFactory} (the technology used in this editor)
 * @param <C>
 *            type of components beeing managed as view (DianaView)
 */
public abstract class AbstractDianaEditor<M, F extends DianaViewFactory<F, C>, C>
		implements DianaEditor<M>, PropertyChangeListener, HasPropertyChangeSupport {

	private static final Logger logger = Logger.getLogger(AbstractDianaEditor.class.getPackage().getName());

	public static final FIBLibrary EDITOR_FIB_LIBRARY = ApplicationFIBLibraryImpl.instance();

	private final Drawing<M> drawing;
	protected DrawingView<M, ? extends C> drawingView;
	private DianaEditorDelegate delegate;

	private double scale = 1.0;

	/**
	 * This factory is the one which is used to creates and maintains object graph
	 */
	private final DianaModelFactory factory;

	/**
	 * This is the view factory installed for this editor
	 */
	private final F dianaFactory;

	/**
	 * This is the view factory installed for this editor
	 */
	private final DianaToolFactory<C> toolFactory;

	protected Map<DrawingTreeNode<?, ?>, DianaView<?, ? extends C>> contents;

	private final PropertyChangeSupport pcSupport;

	// Stores dragSourceContext for drag and drop operation currently performed in this DianaEditor
	private DragSourceContext dragSourceContext;

	// Stored object beeing transfered
	private Transferable objectBeingTransfered;

	public AbstractDianaEditor(Drawing<M> aDrawing, DianaModelFactory factory, F dianaFactory, DianaToolFactory<C> toolFactory) {
		super();

		pcSupport = new PropertyChangeSupport(this);

		this.factory = factory;
		this.dianaFactory = dianaFactory;
		this.toolFactory = toolFactory;

		contents = new Hashtable<>();

		drawing = aDrawing;
		if (drawing instanceof DrawingImpl<?>) {
			((DrawingImpl<?>) drawing).getPropertyChangeSupport().addPropertyChangeListener(this);
		}

		buildDrawingView();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Building AbstractDianaEditor: " + this);
		}
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		// TODO
		return null;
	}

	public DianaModelFactory getFactory() {
		return factory;
	}

	public EditingContext getEditingContext() {
		return factory.getEditingContext();
	}

	public UndoManager getUndoManager() {
		if (getEditingContext() != null) {
			return getEditingContext().getUndoManager();
		}
		return null;
	}

	public F getDianaFactory() {
		return dianaFactory;
	}

	public DianaToolFactory<C> getToolFactory() {
		return toolFactory;
	}

	public DianaEditorDelegate getDelegate() {
		return delegate;
	}

	protected void setDelegate(DianaEditorDelegate delegate) {
		this.delegate = delegate;
	}

	public DrawingView<M, ? extends C> rebuildDrawingView() {
		if (drawingView != null) {
			drawingView.delete();
		}
		buildDrawingView();
		return drawingView;
	}

	private DrawingView<M, ?> buildDrawingView() {
		drawingView = makeDrawingView();
		contents.put(drawing.getRoot(), drawingView);
		// logger.info("Controller " + this + " Register " + drawingView + " for " + drawing.getRoot());
		for (DrawingTreeNode<?, ?> dtn : drawing.getRoot().getChildNodes()) {
			if (dtn instanceof ShapeNode) {
				ShapeView<?, ?> v = recursivelyBuildShapeView((ShapeNode<?>) dtn);
				drawingView.addView(v);
			}
			else if (dtn instanceof ConnectorNode) {
				ConnectorView<?, ?> v = makeConnectorView((ConnectorNode<?>) dtn);
				drawingView.addView(v);
			}
		}
		// System.out.println("JDrawingView: " + drawingView);
		return drawingView;
	}

	private <O> ShapeView<O, ? extends C> recursivelyBuildShapeView(ShapeNode<O> shapeNode) {
		ShapeView<O, ? extends C> returned = makeShapeView(shapeNode);
		for (DrawingTreeNode<?, ?> dtn : shapeNode.getChildNodes()) {
			if (dtn instanceof ShapeNode) {
				ShapeView<?, ?> v = recursivelyBuildShapeView((ShapeNode<?>) dtn);
				returned.addView(v);
			}
			else if (dtn instanceof ConnectorNode) {
				ConnectorView<?, ?> v = makeConnectorView((ConnectorNode<?>) dtn);
				returned.addView(v);
			}
		}
		return returned;
	}

	/**
	 * Instantiate a new JDrawingView<br>
	 * You might override this method for a custom view managing
	 * 
	 * @return
	 */
	public DrawingView<M, ? extends C> makeDrawingView() {
		return getDianaFactory().makeDrawingView(this);
	}

	/**
	 * Instantiate a new JShapeView for a shape node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> ShapeView<O, ? extends C> makeShapeView(ShapeNode<O> shapeNode) {
		ShapeView<O, ? extends C> returned = getDianaFactory().makeShapeView(shapeNode, this);
		contents.put(shapeNode, returned);
		return returned;
	}

	/**
	 * Instantiate a new JConnectorView for a connector node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> ConnectorView<O, ? extends C> makeConnectorView(ConnectorNode<O> connectorNode) {
		ConnectorView<O, ? extends C> returned = getDianaFactory().makeConnectorView(connectorNode, this);
		contents.put(connectorNode, returned);
		return returned;
	}

	/**
	 * Return a Map containing all the views declared for each DrawingTreeNode, where the key of the map are the {@link DrawingTreeNode}
	 * 
	 * @return
	 */
	public Map<DrawingTreeNode<?, ?>, DianaView<?, ? extends C>> getContents() {
		return contents;
	}

	/**
	 * Return view matching supplied node
	 * 
	 * @param node
	 * @return
	 */
	public <O> DianaView<?, ? extends C> viewForNode(DrawingTreeNode<?, ?> node) {
		return contents.get(node);
	}

	public void unreferenceViewForDrawingTreeNode(DrawingTreeNode<?, ?> node) {
		contents.remove(node);
	}

	/**
	 * Return view matching supplied node, asserting supplied node is a {@link ShapeNode}
	 * 
	 * @param node
	 * @return
	 */
	public ShapeView<?, ? extends C> shapeViewForNode(ShapeNode<?> node) {
		return (ShapeView<?, ? extends C>) viewForNode(node);
	}

	/**
	 * Return view matching supplied node, asserting supplied node is a {@link ConnectorNode}
	 * 
	 * @param node
	 * @return
	 */
	public ConnectorView<?, ? extends C> connectorViewForNode(ConnectorNode<?> node) {
		return (ConnectorView<?, ? extends C>) viewForNode(node);
	}

	@Override
	public double getScale() {
		return scale;
	}

	public void setScale(double aScale) {
		if (aScale < 0) {
			return;
		}
		if (scale != aScale) {
			double oldValue = scale;
			scale = aScale;
			notifyObservers(new ScaleChanged(oldValue, aScale));
			drawingView.rescale();
		}
	}

	@Override
	public Drawing<M> getDrawing() {
		return drawing;
	}

	public DrawingView<M, ? extends C> getDrawingView() {
		return drawingView;
	}

	public void installDrawingView(DrawingView<M, ? extends C> drawingView) {
		this.drawingView = drawingView;
	}

	public void delete() {
		if (drawing instanceof DrawingImpl<?>) {
			((DrawingImpl<?>) drawing).getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		if (drawingView != null) {
			drawingView.delete();
		}
		drawingView = null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}

	public void notifyObservers(ControlNotification notification) {
		getPropertyChangeSupport().firePropertyChange(notification.eventName(), notification.oldValue, notification.newValue);
	}

	/**
	 * Returns dragSourceContext for drag and drop operation currently performed in this DianaEditor
	 * 
	 * @return
	 */
	public DragSourceContext getDragSourceContext() {
		return dragSourceContext;
	}

	/**
	 * Sets dragSourceContext for drag and drop operation currently performed in this DianaEditor
	 * 
	 * @param dragSourceContext
	 */
	public void setDragSourceContext(DragSourceContext dragSourceContext) {
		this.dragSourceContext = dragSourceContext;
	}

	public Transferable getObjectBeingTransfered() {
		return objectBeingTransfered;
	}

	public void setObjectBeingTransfered(Transferable objectBeingTransfered) {
		this.objectBeingTransfered = objectBeingTransfered;
	}

}
