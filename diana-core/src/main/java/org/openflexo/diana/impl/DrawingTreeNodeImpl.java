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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.DianaPrefs;
import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing;
import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.PersistenceMode;
import org.openflexo.diana.GRBinding;
import org.openflexo.diana.GRBinding.DynamicPropertyValue;
import org.openflexo.diana.GRProperty;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.diana.GraphicalRepresentation.LabelMetricsProvider;
import org.openflexo.diana.GraphicalRepresentation.VerticalTextAlignment;
import org.openflexo.diana.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.diana.TextStyle;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.notifications.DianaAttributeNotification;
import org.openflexo.diana.notifications.DianaNotification;
import org.openflexo.diana.notifications.LabelHasEdited;
import org.openflexo.diana.notifications.LabelHasMoved;
import org.openflexo.diana.notifications.LabelWillEdit;
import org.openflexo.diana.notifications.LabelWillMove;
import org.openflexo.diana.notifications.NodeDeleted;
import org.openflexo.pamela.factory.DeletableProxyObject;
import org.openflexo.pamela.factory.EditingContext;
import org.openflexo.pamela.factory.ProxyMethodHandler;
import org.openflexo.pamela.undo.UndoManager;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * This is the base implementation of a node in the drawing tree. (see DrawingTreeNode<O,GR)<br>
 * A node essentially references {@link GraphicalRepresentation} and the represented drawable (an arbitrary java {@link Object}).<br>
 * The {@link GraphicalRepresentation} is observed using {@link PropertyChangeSupport} scheme<br>
 * The drawable object may be observed both ways:
 * <ul>
 * <li>(preferably)using {@link PropertyChangeSupport} scheme, if drawable implements {@link HasPropertyChangeSupport} mechanism</li>
 * <li>(preferably)using classical {@link Observer}/{@link Observable} scheme, if drawable extends {@link Observable}</li>
 * </ul>
 * 
 * Also references the {@link GRBinding} which is the specification of a node in the drawing tree
 * 
 * @author sylvain
 * 
 * @param <O>
 * @param <GR>
 */
public abstract class DrawingTreeNodeImpl<O, GR extends GraphicalRepresentation> implements DrawingTreeNode<O, GR> {

	private static final Logger logger = Logger.getLogger(DrawingTreeNodeImpl.class.getPackage().getName());

	private final DrawingImpl<?> drawing;
	private O drawable;
	private ContainerNodeImpl<?, ?> parentNode;
	private GR graphicalRepresentation;
	private final GRBinding<O, GR> grBinding;

	private boolean isInvalidated = true;
	private boolean isDeleted = false;

	protected LabelMetricsProvider labelMetricsProvider;

	private boolean isSelected = false;
	private boolean isFocused = false;

	private PropertyChangeSupport pcSupport;

	/**
	 * Store temporary properties that may not be serialized
	 */
	private Map<GRProperty, Object> propertyValues = new HashMap<>();

	protected DrawingTreeNodeImpl(DrawingImpl<?> drawingImpl, O drawable, GRBinding<O, GR> grBinding, ContainerNodeImpl<?, ?> parentNode) {

		pcSupport = new PropertyChangeSupport(this);

		this.drawing = drawingImpl;
		// logger.info("New DrawingTreeNode for "+aDrawable+" under "+aParentDrawable+" (is "+this+")");
		this.drawable = drawable;
		this.grBinding = grBinding;

		this.parentNode = parentNode;

		Hashtable<Object, DrawingTreeNode<?, ?>> hash = this.drawing.retrieveHash(grBinding);

		hash.put(drawable, this);

		propertyValues = new HashMap<>();

		retrieveGraphicalRepresentation();

		bindingValueObserver = new BindingValueObserver();
	}

	private BindingValueObserver bindingValueObserver;

	/**
	 * This method is called whenever it was detected that the value of a property declared as dynamic (specified by a {@link DataBinding}
	 * in {@link GRBinding}) has changed
	 * 
	 * @param parameter
	 * @param oldValue
	 * @param newValue
	 */
	public <T> void fireDynamicPropertyChanged(GRProperty<T> parameter, T oldValue, T newValue) {
		getPropertyChangeSupport().firePropertyChange(parameter.getName(), oldValue, newValue);
	}

	/**
	 * Utility class used to observe all dynamic property values, relatively to their value at run-time
	 * 
	 * @author sylvain
	 * 
	 */
	public class BindingValueObserver {

		private Map<DynamicPropertyValue<?>, BindingValueChangeListener<?>> listeners;

		public BindingValueObserver() {
			listeners = new HashMap<>();
			for (final DynamicPropertyValue<?> dpv : getGRBinding().getDynamicPropertyValues()) {
				registerBindingValueChangeListener(dpv);
			}
		}

		private <T> void registerBindingValueChangeListener(final DynamicPropertyValue<T> dynamicPropertyValue) {
			BindingValueChangeListener<T> listener = new BindingValueChangeListener<T>(dynamicPropertyValue.dataBinding,
					getBindingEvaluationContext()) {
				@Override
				public void bindingValueChanged(Object source, T newValue) {
					// Detected that a data modification caused the evaluation of DataBinding to be changed
					fireDynamicPropertyChanged(dynamicPropertyValue.parameter, null, newValue);
				}
			};
			listeners.put(dynamicPropertyValue, listener);
		}

		public void delete() {
			for (BindingValueChangeListener<?> l : listeners.values()) {
				l.stopObserving();
				l.delete();
			}
			listeners.clear();
			listeners = null;
		}
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return NodeDeleted.EVENT_NAME;
	}

	@Override
	public Drawing<?> getDrawing() {
		return this.drawing;
	}

	@Override
	public DianaModelFactory getFactory() {
		return getDrawing().getFactory();
	}

	/**
	 * Return the EditingContext
	 * 
	 * @return
	 */
	@Override
	public EditingContext getEditingContext() {
		return getFactory().getEditingContext();
	}

	@Override
	public UndoManager getUndoManager() {
		if (getEditingContext() != null) {
			return getEditingContext().getUndoManager();
		}
		return null;
	}

	@Override
	public GRBinding<O, GR> getGRBinding() {
		return grBinding;
	}

	/**
	 * Return boolean indicating if this {@link DrawingTreeNode} is valid.<br>
	 * 
	 * A {@link DrawingTreeNode} is valid when it is correctely embedded inside {@link Drawing} tree (which means that parent and child are
	 * set and correct, and that start and end shapes are set for connectors)
	 * 
	 * @return
	 */
	@Override
	public boolean isValid() {
		if (getDrawable() == null) {
			return false;
		}
		if (getDrawing().getDrawingTreeNode(getDrawable(), getGRBinding()) != this) {
			logger.warning(
					"Please investigate here: something strange at this point, see isValid() in DrawingTreeNode. More informations in the console");
			System.out.println("drawable=" + getDrawable());
			System.out.println("grBinding=" + getGRBinding());
			DrawingTreeNode<?, ?> dtn = getDrawing().getDrawingTreeNode(getDrawable(), getGRBinding());
			if (dtn != null) {
				System.out.println("dtn.drawable=" + dtn.getDrawable());
				System.out.println("dtn.grBinding=" + dtn.getGRBinding());
			}
			else {
				System.out.println("dtn=null");
			}
			// Thread.dumpStack();
			// return false;
		}

		DrawingTreeNode<?, ?> current = this;
		while (current != getDrawing().getRoot()) {
			DrawingTreeNode<?, ?> container = current.getParentNode();
			if (container == null) {
				return false;
			}
			current = container;
		}
		return true;

	}

	@Override
	public final boolean isInvalidated() {
		return isInvalidated;
	}

	@Override
	public void invalidate() {
		isInvalidated = true;
	}

	public void validate() {
		isInvalidated = false;
	}

	@Override
	public ContainerNodeImpl<?, ?> getParentNode() {
		return parentNode;
	}

	protected void setParentNode(ContainerNodeImpl<?, ?> parentNode) {
		this.parentNode = parentNode;
	}

	@Override
	public List<DrawingTreeNode<?, ?>> getAncestors() {
		List<DrawingTreeNode<?, ?>> ancestors = new ArrayList<>();
		ancestors.add(this);
		if (parentNode != null) {
			ancestors.addAll(parentNode.getAncestors());
		}
		return ancestors;
	}

	public DrawingTreeNode<?, ?> getCommonAncestor(DrawingTreeNode<?, ?> o) {
		List<DrawingTreeNode<?, ?>> ancestors = o.getAncestors();
		for (DrawingTreeNode<?, ?> ancestor : getAncestors()) {
			if (ancestors.contains(ancestor)) {
				return ancestor;
			}
		}
		return null;
	}

	@Override
	public int getDepth() {
		int returned = 0;
		DrawingTreeNode<?, ?> current = this;
		while (current.getParentNode() != null) {
			returned++;
			current = current.getParentNode();
		}
		return returned;
	}

	@Override
	public GR getGraphicalRepresentation() {

		return graphicalRepresentation;
	}

	@Override
	public void retrieveGraphicalRepresentation() {
		if (graphicalRepresentation == null && grBinding != null) {

			graphicalRepresentation = grBinding.getGRProvider().provideGR(drawable, drawing.getFactory());
			if (graphicalRepresentation != null && graphicalRepresentation.getPropertyChangeSupport() != null) {
				graphicalRepresentation.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			if (getPropertyChangeSupport() != null) {
				getPropertyChangeSupport().firePropertyChange(GRAPHICAL_REPRESENTATION_KEY, null, graphicalRepresentation);
			}
		}

	}

	@Override
	public abstract List<? extends ControlArea<?>> getControlAreas();

	/*protected final void updateControlAreas() {
		controlAreas.clear();
		controlAreas.addAll(rebuildControlAreas());
	}*/

	// protected abstract List<? extends ControlArea<?>> rebuildControlAreas();

	/*private void update()
	{
		if (parentNode == null) { // This is the root node
			graphicalRepresentation = (GraphicalRepresentation)getDrawingGraphicalRepresentation();
		}
		else {
			GraphicalRe
			parentNode.notifyDrawableRemoved(removedGR);
			graphicalRepresentation = retrieveGraphicalRepresentation(drawable);
			System.out.println("Tiens maintenant la GR c'est "+graphicalRepresentation);
		}
	}*/

	/**
	 * Recursively delete this DrawingTreeNode and all its descendants
	 */
	@Override
	public boolean delete() {
		if (!isDeleted) {
			// Normally, it is already done, but check and do it when required...
			if (parentNode != null && ((ContainerNode<?, ?>) parentNode).getChildNodes() != null
					&& ((ContainerNode<?, ?>) parentNode).getChildNodes().contains(this)) {
				parentNode.removeChild(this);
			}

			bindingValueObserver.delete();
			bindingValueObserver = null;

			Hashtable<Object, DrawingTreeNode<?, ?>> hash = this.drawing.retrieveHash(grBinding);

			if (drawable != null) {
				hash.remove(drawable);
			}

			drawable = null;
			parentNode = null;

			if (graphicalRepresentation != null && graphicalRepresentation.getPropertyChangeSupport() != null) {
				graphicalRepresentation.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			graphicalRepresentation = null;

			isDeleted = true;

			notifyObservers(new NodeDeleted<>(this));

			return true;
		}
		return false;
	}

	/**
	 * Internally called at the very end of deletion procedure
	 */
	protected void finalizeDeletion() {
		if (pcSupport.hasListeners(null)) {
			logger.warning("WARNING: finalizeDeletion called for " + this + " while object still being observed");
			for (PropertyChangeListener l : pcSupport.getPropertyChangeListeners()) {
				logger.info("Remaining PropertyChangeListener:  " + l);
			}
			// Thread.dumpStack();
		}
		pcSupport = null;
	}

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	/*Vector<DrawingTreeNode<?>> nodesToRemove = new Vector<DrawingTreeNode<?>>();
	
	protected void beginUpdateObjectHierarchy() {
	
		// Invalidated nodes are to be removed rigth now
		// (we are sure that we don't want to keep it)
		if (childNodes != null) {
			for (DrawingTreeNode<?> n : new ArrayList<DrawingTreeNode<?>>(childNodes)) {
				if (n.isInvalidated) {
					removeDrawable(n.drawable, drawable);
				}
			}
		}
	
		// Remaining nodes are marked to potential deletion
		if (childNodes != null) {
			for (DrawingTreeNode<?> n : childNodes) {
				nodesToRemove.add(n);
			}
		}
	}
	
	protected void endUpdateObjectHierarchy() {
		// Nodes that keep marked for deletion are deleted now
		for (DrawingTreeNode<?> n : nodesToRemove) {
			removeDrawable(n.drawable, drawable);
		}
		nodesToRemove.clear();
	}*/

	@Override
	public O getDrawable() {
		return drawable;
	}

	// *******************************************************************************
	// * Observer implementation *
	// *******************************************************************************

	protected Set<HasPropertyChangeSupport> temporaryIgnoredObservables = new HashSet<>();

	/**
	 * 
	 * @param observable
	 * @return a flag indicating if observable was added to the list of ignored observables
	 */
	protected boolean ignoreNotificationsFrom(HasPropertyChangeSupport observable) {
		if (temporaryIgnoredObservables.contains(observable)) {
			return false;
		}
		temporaryIgnoredObservables.add(observable);
		return true;
	}

	protected void observeAgain(HasPropertyChangeSupport observable) {
		temporaryIgnoredObservables.remove(observable);
	}

	private boolean isObservingDrawable = false;

	protected void startDrawableObserving() {
		if (isObservingDrawable) {
			logger.warning("START drawable observing called for an already observed drawable");
			Thread.dumpStack();
			return;
		}
		// Now start to observe drawable for drawing structural modifications
		if (drawable instanceof Observable) {
			((Observable) drawable).addObserver(this);
		}
		else if (drawable instanceof HasPropertyChangeSupport) {
			((HasPropertyChangeSupport) drawable).getPropertyChangeSupport().addPropertyChangeListener(this);
		}
		isObservingDrawable = true;
	}

	protected void stopDrawableObserving() {
		if (!isObservingDrawable) {
			logger.warning("STOP drawable observing called for an non-observed drawable");
			return;
		}
		// Now start to observe drawable for drawing structural modifications
		if (drawable instanceof Observable) {
			((Observable) drawable).deleteObserver(this);
		}
		else if (drawable instanceof HasPropertyChangeSupport) {
			((HasPropertyChangeSupport) drawable).getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		isObservingDrawable = false;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == getDrawable()) {
			logger.info("Received a notification from my drawable that something change: " + arg);
			fireStructureMayHaveChanged();
		}
	}

	private void fireStructureMayHaveChanged() {
		if (getDrawable() instanceof DeletableProxyObject) {
			if (((DeletableProxyObject) getDrawable()).isDeleted()) {
				// delete();
				return;
			}
		}
		getDrawing().invalidateGraphicalObjectsHierarchy(getDrawable());
		getDrawing().updateGraphicalObjectsHierarchy(getDrawable());
	}

	/**
	 * Return boolean indicating if received event might change structure of DrawingTreeNode hierarchy<br>
	 * By default, this is
	 * 
	 * @param evt
	 * @return
	 */
	private boolean eventMightRequireStructureModification(PropertyChangeEvent evt, O drawable) {
		String propertyName = evt.getPropertyName();
		switch (propertyName) {
			case ProxyMethodHandler.MODIFIED:
			case ProxyMethodHandler.SERIALIZING:
			case ProxyMethodHandler.DESERIALIZING:
				return false;
			default:
				return true;
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		// System.out.println("!!!!!!!Received PropertyChangeEvent " + evt);

		if (isDeleted()) {
			logger.warning("Received PropertyChangeEvent " + evt + " for DELETED node !!!!");
			return;
		}

		if (temporaryIgnoredObservables.contains(evt.getSource())) {
			// System.out.println("IGORE NOTIFICATION " + notification);
			return;
		}

		if (evt.getSource() == getDrawable()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Received a notification from my drawable that " + evt.getPropertyName() + " change: " + evt);
			}
			if (eventMightRequireStructureModification(evt, getDrawable())) {
				fireStructureMayHaveChanged();
			}
		}

		if (evt.getSource() == getGraphicalRepresentation()) {

			if (getDrawing().getPersistenceMode() == PersistenceMode.SharedGraphicalRepresentations) {
				// This is a tricky area
				// We share GR, which means that we don't use GR to store values, but we store them in propertyValues hashtable
				GRProperty<?> parameter = GRProperty.getGRParameter(evt.getSource().getClass(), evt.getPropertyName());
				// System.out.println("Value needs to be updated for parameter " + evt.getPropertyName()
				// + " parameter=" + parameter);
				if (parameter != null && propertyValues.get(parameter) != evt.getNewValue()) {
					propertyValues.put(parameter, evt.getNewValue());
					// System.out.println("Value for " + parameter.getName() + " changed for " + evt.getNewValue());
				}
			}

			// Those notifications comes from my graphical representation, forward them
			forward(evt);

			// setChanged();
			// notifyObservers(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());

			/*if (notif instanceof BindingChanged) {
				updateDependanciesForBinding(((BindingChanged) notif).getBinding());
			}*/

			/*if (notif.getParameter() == Parameters.text) {
				checkAndUpdateDimensionBoundsIfRequired();
			}*/
		}

		if (evt.getSource() instanceof TextStyle) {
			notifyAttributeChanged(GraphicalRepresentation.TEXT_STYLE, null, getGraphicalRepresentation().getTextStyle());
		}

	}

	public void notifyObservers(DianaNotification<?, ?> notification) {
		if ((!(notification instanceof NodeDeleted)) && isDeleted()) {
			logger.warning("notifyObservers() called by a deleted DrawingTreeNode");
			// Thread.dumpStack();
			return;
		}
		getPropertyChangeSupport().firePropertyChange(notification.propertyName(), notification.oldValue(), notification.newValue());
	}

	public void notifyObservers(String propertyName, Object oldValue, Object newValue) {
		getPropertyChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
	}

	// TODO: (sylvain) i think this is no more necessary, remove this ???
	@Deprecated
	public <T> void notifyAttributeChanged(GRProperty<T> parameter, T oldValue, T newValue) {
		notifyObservers(new DianaAttributeNotification<>(parameter, oldValue, newValue));
	}

	public void forward(PropertyChangeEvent evt) {

		getPropertyChangeSupport().firePropertyChange(evt);

		// getPropertyChangeSupport().firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
	}

	@Override
	public Point convertNormalizedPointToViewCoordinates(double x, double y, double scale) {
		AffineTransform at = convertNormalizedPointToViewCoordinatesAT(scale);
		DianaPoint returned = new DianaPoint();
		at.transform(new DianaPoint(x, y), returned);
		return new Point((int) returned.x, (int) returned.y);
	}

	@Override
	public Rectangle convertNormalizedRectangleToViewCoordinates(DianaRectangle r, double scale) {
		DianaPoint p1 = new DianaPoint(r.x, r.y);
		DianaPoint p2 = new DianaPoint(r.x + r.width, r.y + r.height);
		Point pp1 = convertNormalizedPointToViewCoordinates(p1, scale);
		Point pp2 = convertNormalizedPointToViewCoordinates(p2, scale);
		return new Rectangle(pp1.x, pp1.y, pp2.x - pp1.x, pp2.y - pp1.y);
	}

	@Override
	public abstract AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale);

	@Override
	public DianaPoint convertViewCoordinatesToNormalizedPoint(int x, int y, double scale) {
		AffineTransform at = convertViewCoordinatesToNormalizedPointAT(scale);
		DianaPoint returned = new DianaPoint();
		at.transform(new DianaPoint(x, y), returned);
		return returned;
	}

	@Override
	public abstract AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale);

	@Override
	public Point convertNormalizedPointToViewCoordinates(DianaPoint p, double scale) {
		return convertNormalizedPointToViewCoordinates(p.x, p.y, scale);
	}

	@Override
	public DianaPoint convertViewCoordinatesToNormalizedPoint(Point p, double scale) {
		return convertViewCoordinatesToNormalizedPoint(p.x, p.y, scale);
	}

	@Override
	public boolean isAncestorOf(DrawingTreeNode<?, ?> child) {
		if (!isValid()) {
			return false;
		}
		DrawingTreeNode<?, ?> father = child.getParentNode();
		while (father != null) {
			if (father == this) {
				return true;
			}
			father = father.getParentNode();
		}
		return false;
	}

	@Override
	public DianaPoint convertRemoteViewCoordinatesToLocalNormalizedPoint(Point p, DrawingTreeNode<?, ?> source, double scale) {
		if (!isValid() || !source.isValid()) {
			return new DianaPoint(p.x / scale, p.y / scale);
		}
		Point pointRelativeToCurrentView = DianaUtils.convertPoint(source, p, this, scale);
		return convertViewCoordinatesToNormalizedPoint(pointRelativeToCurrentView, scale);
	}

	@Override
	public DianaPoint convertLocalViewCoordinatesToRemoteNormalizedPoint(Point p, DrawingTreeNode<?, ?> destination, double scale) {
		if (!isValid() || !destination.isValid()) {
			return new DianaPoint(p.x * scale, p.y * scale);
		}
		Point pointRelativeToRemoteView = DianaUtils.convertPoint(this, p, destination, scale);
		return destination.convertViewCoordinatesToNormalizedPoint(pointRelativeToRemoteView, scale);
	}

	@Override
	public Point convertLocalNormalizedPointToRemoteViewCoordinates(DianaPoint p, DrawingTreeNode<?, ?> destination, double scale) {
		Point point = convertNormalizedPointToViewCoordinates(p, scale);
		return DianaUtils.convertPoint(this, point, destination, scale);
	}

	@Override
	public Rectangle convertLocalNormalizedRectangleToRemoteViewCoordinates(DianaRectangle r, DrawingTreeNode<?, ?> destination,
			double scale) {
		DianaPoint p1 = new DianaPoint(r.x, r.y);
		DianaPoint p2 = new DianaPoint(r.x + r.width, r.y + r.height);
		Point pp1 = convertLocalNormalizedPointToRemoteViewCoordinates(p1, destination, scale);
		Point pp2 = convertLocalNormalizedPointToRemoteViewCoordinates(p2, destination, scale);
		return new Rectangle(pp1.x, pp1.y, pp2.x - pp1.x, pp2.y - pp1.y);
	}

	@Override
	public Point convertRemoteNormalizedPointToLocalViewCoordinates(DianaPoint p, DrawingTreeNode<?, ?> source, double scale) {
		Point point = source.convertNormalizedPointToViewCoordinates(p, scale);
		return DianaUtils.convertPoint(source, point, this, scale);
	}

	@Override
	public abstract int getViewX(double scale);

	@Override
	public abstract int getViewY(double scale);

	@Override
	public abstract int getViewWidth(double scale);

	@Override
	public abstract int getViewHeight(double scale);

	@Override
	public Rectangle getViewBounds(double scale) {
		Rectangle bounds = new Rectangle();

		bounds.x = getViewX(scale);
		bounds.y = getViewY(scale);
		bounds.width = getViewWidth(scale);
		bounds.height = getViewHeight(scale);

		return bounds;
	}

	@Override
	public DianaRectangle getNormalizedBounds() {
		return new DianaRectangle(0, 0, 1, 1, Filling.FILLED);
	}

	/*public void setValidated(boolean validated) {
		this.validated = validated;
	}*/

	@Override
	public LabelMetricsProvider getLabelMetricsProvider() {
		return labelMetricsProvider;
	}

	@Override
	public void setLabelMetricsProvider(LabelMetricsProvider labelMetricsProvider) {
		this.labelMetricsProvider = labelMetricsProvider;
	}

	/**
	 * Returns the number of pixels available for the label considering its positioning. This method is used in case of line wrapping.
	 * 
	 * @param scale
	 * @return
	 */
	@Override
	public int getAvailableLabelWidth(double scale) {
		return Integer.MAX_VALUE;
	}

	@Override
	public Point getLabelLocation(double scale) {
		return new Point((int) (getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X) * scale + getViewX(scale)),
				(int) (getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y) * scale + getViewY(scale)));
	}

	@Override
	public Dimension getLabelDimension(double scale) {
		Dimension d;
		if (labelMetricsProvider != null) {
			d = labelMetricsProvider.getScaledPreferredDimension(scale);
		}
		else {
			d = new Dimension(0, 0);
		}
		return d;
	}

	@Override
	public void setLabelLocation(Point point, double scale) {
		setPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X, (point.x - getViewX(scale)) / scale);
		setPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y, (point.y - getViewY(scale)) / scale);
	}

	@Override
	public Rectangle getLabelBounds(double scale) {
		return new Rectangle(getLabelLocation(scale), getLabelDimension(scale));
	}

	/**
	 * Return the index of this drawing tree node relatively to all children declared in parent node
	 * 
	 * @return
	 */
	@Override
	public int getIndex() {
		if (!isValid()) {
			return -1;
		}
		if (getParentNode() == null) {
			return -1;
		}

		List<DrawingTreeNodeImpl<?, ?>> orderedGRList = getParentNode().getChildNodes();
		if (orderedGRList == null) {
			// TODO, this should not happen
			logger.warning("Empty child Nodes list, please investigate");
			return -1;
		}
		return orderedGRList.indexOf(this);
	}

	/**
	 * Return flag indicating if this node should be displayed, relatively to the value returned by visible feature in
	 * {@link GraphicalRepresentation}, and the structure of the tree (the parent should be visible too)
	 */
	@Override
	public boolean shouldBeDisplayed() {
		if (!isValid()) {
			return false;
		}
		// logger.info("For " + this + " getIsVisible()=" + getGraphicalRepresentation().getIsVisible() + " getParentNode()="
		// + getParentNode());
		if (isDeleted()) {
			return false;
		}
		if (getGraphicalRepresentation() == null) {
			return false;
		}
		return getIsVisible() && getParentNode() != null && getParentNode().shouldBeDisplayed();
	}

	private boolean labelIsBeeingEdited = false;

	@Override
	public void notifyLabelWillBeEdited() {
		labelIsBeeingEdited = true;
		setIsLongTimeFocused(false);
		notifyObservers(new LabelWillEdit());
	}

	@Override
	public void notifyLabelHasBeenEdited() {
		labelIsBeeingEdited = false;
		notifyObservers(new LabelHasEdited());
	}

	@Override
	public void notifyLabelWillMove() {
		notifyObservers(new LabelWillMove());
	}

	@Override
	public void notifyLabelHasMoved() {
		notifyObservers(new LabelHasMoved());
	}

	@Override
	public void notifyObjectHierarchyWillBeUpdated() {
		// setRegistered(false);
		/*if (ancestors != null) {
			ancestors.clear();
		}
		ancestors = null;*/
	}

	@Override
	public void notifyObjectHierarchyHasBeenUpdated() {
		// setRegistered(true);
		/*if (ancestors != null) {
			ancestors.clear();
		}
		ancestors = null;*/
	}

	@Override
	public boolean getIsFocused() {
		return isFocused;
	}

	@Override
	public void setIsFocused(boolean aFlag) {
		// System.out.println("***** setIsFocused with " + aFlag + " was " + isFocused);
		if (aFlag != isFocused) {
			isFocused = aFlag;
			notifyObservers(new DianaAttributeNotification<>(IS_FOCUSED, !isFocused, isFocused));
			if (DianaPrefs.HANDLE_LONG_FOCUSING) {
				if (aFlag && !isAboutToBeLongTimeFocused) {
					handleLongTimeFocused(aFlag);
				}
				else {
					isAboutToBeLongTimeFocused = false;
				}
				if (!aFlag) {
					setIsLongTimeFocused(false);
				}
			}
			else {
				setIsLongTimeFocused(aFlag);
			}
		}
	}

	private void handleLongTimeFocused(boolean isFocused) {
		isAboutToBeLongTimeFocused = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Waiting
				try {
					Thread.sleep(DianaPrefs.LONG_FOCUSING_DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (getIsFocused() && !labelIsBeeingEdited) {
					// Still have the focus
					setIsLongTimeFocused(true);
				}
				else {
					// Lost the focus
					setIsLongTimeFocused(false);
				}
				isAboutToBeLongTimeFocused = false;
			}
		}).start();
	}

	private boolean isLongTimeFocused = false;
	private boolean isAboutToBeLongTimeFocused = false;

	@Override
	public boolean getIsLongTimeFocused() {
		return isLongTimeFocused;
	}

	private void setIsLongTimeFocused(boolean aFlag) {
		// System.out.println("***** setIsFocused with " + aFlag + " was " + isFocused);
		if (aFlag != isLongTimeFocused) {
			isLongTimeFocused = aFlag;
			notifyObservers(new DianaAttributeNotification<>(IS_LONG_TIME_FOCUSED, !isLongTimeFocused, isLongTimeFocused));
		}
	}

	@Override
	public boolean getIsSelected() {
		return isSelected;
	}

	@Override
	public void setIsSelected(boolean aFlag) {
		// System.out.println("***** setIsSelected with " + aFlag + " was " + isSelected);
		if (aFlag != isSelected) {
			isSelected = aFlag;
			notifyObservers(new DianaAttributeNotification<>(IS_SELECTED, !isSelected, isSelected));
		}
	}

	/**
	 * Return a flag indicating if supplied parameter is declared to be dynamically managed
	 * 
	 * @param parameter
	 * @return
	 */
	protected boolean hasDynamicPropertyValue(GRProperty<?> parameter) {
		return getGRBinding().hasDynamicPropertyValue(parameter);
	}

	/**
	 * Return a flag indicating if supplied parameter is declared to be dynamically managed, and if dynamic property (the binding) declared
	 * for this property is settable
	 * 
	 * @param parameter
	 * @return
	 */
	protected boolean hasDynamicSettablePropertyValue(GRProperty<?> parameter) {
		return getGRBinding().hasDynamicPropertyValue(parameter) && getGRBinding().getDynamicPropertyValue(parameter).isSettable();
	}

	/**
	 * Computes and return the value declared as dynamic property value for supplied parameter, asserting this property is declared as
	 * dynamic
	 * 
	 * @param parameter
	 * @return
	 * @throws InvocationTargetException
	 */
	protected <T> T getDynamicPropertyValue(final GRProperty<T> parameter) throws InvocationTargetException {
		if (hasDynamicPropertyValue(parameter)) {
			try {
				return getGRBinding().getDynamicPropertyValue(parameter).dataBinding.getBindingValue(getBindingEvaluationContext());
			} catch (TypeMismatchException e) {
				throw new InvocationTargetException(e);
			} catch (NullReferenceException e) {
				throw new InvocationTargetException(e);
			}
		}
		throw new InvocationTargetException(new IllegalArgumentException("Parameter " + parameter + " has no dynamic property value"));

	}

	/**
	 * Sets the value of dynamic property value for supplied parameter, asserting this property is declared as dynamic and settable
	 * 
	 * @param parameter
	 * @param value
	 * @throws InvocationTargetException
	 */
	protected <T> void setDynamicPropertyValue(GRProperty<T> parameter, T value) throws InvocationTargetException {
		if (hasDynamicSettablePropertyValue(parameter)) {
			try {
				getGRBinding().getDynamicPropertyValue(parameter).dataBinding.setBindingValue(value, getBindingEvaluationContext());
				return;
			} catch (TypeMismatchException e) {
				throw new InvocationTargetException(e);
			} catch (NullReferenceException e) {
				throw new InvocationTargetException(e);
			} catch (NotSettableContextException e) {
				throw new InvocationTargetException(e);
			}
		}
		throw new InvocationTargetException(new IllegalArgumentException("Parameter " + parameter + " has no dynamic property value"));

	}

	/**
	 * Returns the property value for supplied parameter<br>
	 * If a dynamic property was set, compute and return this value, according to binding declared as dynamic property value<br>
	 * If many {@link DrawingTreeNode} share same {@link GraphicalRepresentation} (as indicated by {@link Drawing#getPersistenceMode()), do
	 * not store value in GraphicalRepresentation, but store it in the {@link DrawingTreeNode} itself.<br>
	 * This implies that this value is not persistent (not serializable) Otherwise, use the {@link GraphicalRepresentation} as a support for
	 * this value.
	 * 
	 * @param parameter
	 *            parameter which is to be set
	 * @return
	 */
	@Override
	public <T> T getPropertyValue(GRProperty<T> parameter) {
		T returned = _getPropertyValue(parameter);
		if (parameter.getType().isPrimitive() && returned == null) {
			returned = parameter.getDefaultValue();
		}
		return returned;
	}

	protected <T> T _getPropertyValue(GRProperty<T> parameter) {
		if (hasDynamicPropertyValue(parameter)) {
			try {
				return getDynamicPropertyValue(parameter);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		// Now we have to think of this:
		// New architecture of Diana now authorize a GR to be shared by many DrawingTreeNode
		// If UniqueGraphicalRepresentations is active, use GR to store graphical properties

		if (getDrawing().getPersistenceMode() == PersistenceMode.UniqueGraphicalRepresentations) {
			if (getGraphicalRepresentation() == null) {
				return null;
			}
			if (getGraphicalRepresentation().hasKey(parameter.getName())) {
				return (T) getGraphicalRepresentation().objectForKey(parameter.getName());
			}
			return null;
		}

		// If SharedGraphicalRepresentations is active, GR should not be used to store graphical properties

		else if (getDrawing().getPersistenceMode() == PersistenceMode.SharedGraphicalRepresentations) {

			if (getGraphicalRepresentation() == null) {
				return null;
			}

			T returned = (T) propertyValues.get(parameter);
			if (returned == null) {
				// Init default value with GR
				if (getGraphicalRepresentation().hasKey(parameter.getName())) {
					returned = (T) getGraphicalRepresentation().objectForKey(parameter.getName());
				}
				if (returned != null) {
					propertyValues.put(parameter, returned);
				}
			}

			return returned;
		}

		else {
			logger.warning("Not implemented: " + getDrawing().getPersistenceMode());
			return null;
		}
	}

	/**
	 * Sets the property value for supplied parameter<br>
	 * If a dynamic property was set, sets this value according to binding declared as dynamic property value<br>
	 * If many {@link DrawingTreeNode} share same {@link GraphicalRepresentation} (as indicated by {@link Drawing#getPersistenceMode()), do
	 * not store value in GraphicalRepresentation, but store it in the {@link DrawingTreeNode} itself.<br>
	 * This implies that this value is not persistent (not serializable) Otherwise, use the {@link GraphicalRepresentation} as a support for
	 * this value.
	 * 
	 * @param parameter
	 *            parameter which is to be set
	 * @param value
	 *            value to be set
	 * @return
	 */
	@Override
	public <T> void setPropertyValue(GRProperty<T> parameter, T value) {

		T oldValue = null;

		if (getDrawing().getPersistenceMode() == PersistenceMode.UniqueGraphicalRepresentations) {
			boolean wasObserving = ignoreNotificationsFrom(getGraphicalRepresentation());
			if (getGraphicalRepresentation() != null) {
				if (getGraphicalRepresentation().hasKey(parameter.getName())) {
					oldValue = (T) getGraphicalRepresentation().objectForKey(parameter.getName());
					getGraphicalRepresentation().setObjectForKey(value, parameter.getName());
				}
				if (wasObserving) {
					observeAgain(getGraphicalRepresentation());
				}
			}
			// Since GR is prevented to fire notifications, do it myself
			if (getPropertyChangeSupport() != null) {
				getPropertyChangeSupport().firePropertyChange(parameter.getName(), oldValue, value);
			}
		}

		if (hasDynamicSettablePropertyValue(parameter)) {
			try {
				setDynamicPropertyValue(parameter, value);
				return;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		// Now we have to think of this:
		// New architecture of Diana now authorize a GR to be shared by many DrawingTreeNode
		// If UniqueGraphicalRepresentations is active, use GR to store graphical properties

		if (getDrawing().getPersistenceMode() == PersistenceMode.UniqueGraphicalRepresentations) {
			// Since GR is prevented to fire notifications, do it myself
			if (getPropertyChangeSupport() != null) {
				getPropertyChangeSupport().firePropertyChange(parameter.getName(), oldValue, value);
			}
		}

		// If SharedGraphicalRepresentations is active, GR should not be used to store graphical properties

		else if (getDrawing().getPersistenceMode() == PersistenceMode.SharedGraphicalRepresentations) {
			propertyValues.put(parameter, value);
		}

		else {
			logger.warning("Not implemented: " + getDrawing().getPersistenceMode());
		}

	}

	private BindingEvaluationContext bindingEvaluationContext;

	public BindingEvaluationContext getBindingEvaluationContext() {
		if (bindingEvaluationContext == null) {
			bindingEvaluationContext = new DrawingTreeNodeEvaluationContext();
		}
		return bindingEvaluationContext;
	}

	class DrawingTreeNodeEvaluationContext implements BindingEvaluationContext {
		@Override
		public Object getValue(BindingVariable variable) {
			if (variable.getVariableName().equals(THIS_KEY)) {
				return getGraphicalRepresentation();
			}
			else if (variable.getVariableName().equals(PARENT_KEY) && getParentNode() != null) {
				return getParentNode().getGraphicalRepresentation();
			}
			else if (variable.getVariableName().equals(DRAWABLE_KEY)) {
				return getDrawable();
			}
			else if (variable.getVariableName().equals(GR_KEY)) {
				return getGraphicalRepresentation();
			}
			else {
				DrawingImpl.logger.warning("Could not find variable named " + variable);
				return null;
			}
		}

		@Override
		public String toString() {
			return "DrawingTreeNodeEvaluationContext for " + DrawingTreeNodeImpl.this;
		}
	}

	/**
	 * Convenient method used to retrieve visible property value
	 */
	@Override
	public Boolean getIsVisible() {
		return getPropertyValue(GraphicalRepresentation.IS_VISIBLE);
	}

	/**
	 * Convenient method used to set visible property value
	 */
	@Override
	public void setIsVisible(Boolean visible) {
		setPropertyValue(GraphicalRepresentation.IS_VISIBLE, visible);
	}

	/**
	 * Convenient method used to retrieve focusable property value
	 */
	@Override
	public Boolean getIsFocusable() {
		return getPropertyValue(GraphicalRepresentation.IS_FOCUSABLE);
	}

	/**
	 * Convenient method used to set focusable property value
	 */
	@Override
	public void setIsFocusable(Boolean focusable) {
		setPropertyValue(GraphicalRepresentation.IS_FOCUSABLE, focusable);
	}

	/**
	 * Convenient method used to retrieve text property value
	 */
	@Override
	public String getText() {
		return getPropertyValue(GraphicalRepresentation.TEXT);
	}

	/**
	 * Convenient method used to set text property value
	 */
	@Override
	public void setText(String text) {
		setPropertyValue(GraphicalRepresentation.TEXT, text);
	}

	/**
	 * Convenient method used to retrieve horizontal text alignment property value
	 */
	@Override
	public HorizontalTextAlignment getHorizontalTextAlignment() {
		return getPropertyValue(GraphicalRepresentation.HORIZONTAL_TEXT_ALIGNEMENT);
	}

	/**
	 * Convenient method used to set horizontal text alignment property value
	 */
	@Override
	public void setHorizontalTextAlignment(HorizontalTextAlignment align) {
		setPropertyValue(GraphicalRepresentation.HORIZONTAL_TEXT_ALIGNEMENT, align);
	}

	/**
	 * Convenient method used to retrieve vertical text alignment property value
	 */
	@Override
	public VerticalTextAlignment getVerticalTextAlignment() {
		return getPropertyValue(GraphicalRepresentation.VERTICAL_TEXT_ALIGNEMENT);
	}

	/**
	 * Convenient method used to set vertical text alignment property value
	 */
	@Override
	public void setVerticalTextAlignment(VerticalTextAlignment align) {
		setPropertyValue(GraphicalRepresentation.VERTICAL_TEXT_ALIGNEMENT, align);
	}

	@Override
	public boolean hasText() {

		// Tried to optimize this computation

		if (hasDynamicPropertyValue(GraphicalRepresentation.TEXT)) {
			return true;
		}

		if (getDrawing().getPersistenceMode() == PersistenceMode.UniqueGraphicalRepresentations) {
			if (getGraphicalRepresentation() == null) {
				return false;
			}
			if (getGraphicalRepresentation().hasKey(GraphicalRepresentation.TEXT.getName())) {
				return true;
			}
		}

		else if (getDrawing().getPersistenceMode() == PersistenceMode.SharedGraphicalRepresentations) {

			if (getGraphicalRepresentation() == null) {
				return false;
			}

			return propertyValues != null && propertyValues.get(GraphicalRepresentation.TEXT) != null;
		}

		return getText() != null && !getText().trim().equals("");
	}

	@Override
	public boolean isParentLayoutedAsContainer() {
		return getParentNode() != null && getParentNode().getDimensionConstraints() == DimensionConstraints.CONTAINER;
	}

	/**
	 * Convenient method used to retrieve text property value
	 */
	@Override
	public TextStyle getTextStyle() {
		return getPropertyValue(GraphicalRepresentation.TEXT_STYLE);
	}

	/**
	 * Convenient method used to set text property value
	 */
	@Override
	public void setTextStyle(TextStyle style) {
		setPropertyValue(GraphicalRepresentation.TEXT_STYLE, style);
	}

	@Override
	public boolean getContinuousTextEditing() {
		return getPropertyValue(GraphicalRepresentation.CONTINUOUS_TEXT_EDITING);
	}

	@Override
	public void setContinuousTextEditing(boolean continuousTextEditing) {
		setPropertyValue(GraphicalRepresentation.CONTINUOUS_TEXT_EDITING, continuousTextEditing);
	}

	/*@Override
	public boolean hasKey(String key) {
		return KeyValueDecoder.hasKey(this, key);
	}
	
	@Override
	public Object objectForKey(String key) {
		return KeyValueDecoder.objectForKey(this, key);
	}
	
	@Override
	public void setObjectForKey(Object value, String key) {
		KeyValueCoder.setObjectForKey(this, value, key);
	}
	
	// Retrieving type
	
	@Override
	public Class getTypeForKey(String key) {
		return KeyValueDecoder.getTypeForKey(this, key);
	}*/

}
