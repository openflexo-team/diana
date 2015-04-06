package org.openflexo.fge.impl;

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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingValueChangeListener;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.TargetObject;
import org.openflexo.antar.expr.NotSettableContextException;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.ConstraintDependency;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DependencyLoopException;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.PersistenceMode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.GRBinding;
import org.openflexo.fge.GRBinding.DynamicPropertyValue;
import org.openflexo.fge.GRProperty;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.LabelMetricsProvider;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.LabelHasEdited;
import org.openflexo.fge.notifications.LabelHasMoved;
import org.openflexo.fge.notifications.LabelWillEdit;
import org.openflexo.fge.notifications.LabelWillMove;
import org.openflexo.fge.notifications.NodeDeleted;
import org.openflexo.model.factory.DeletableProxyObject;
import org.openflexo.model.factory.EditingContext;
import org.openflexo.model.undo.UndoManager;
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

	// private List<ControlArea<?>> controlAreas;

	private final List<ConstraintDependency> dependancies;
	private final List<ConstraintDependency> alterings;

	// TODO: manage validated/isInvalidated: is this still required ???
	private boolean isInvalidated = true;
	private boolean isDeleted = false;
	private boolean validated = true;
	protected LabelMetricsProvider labelMetricsProvider;

	private boolean isSelected = false;
	private boolean isFocused = false;

	private PropertyChangeSupport pcSupport;

	/**
	 * Store temporary properties that may not be serialized
	 */
	private Map<GRProperty, Object> propertyValues = new HashMap<GRProperty, Object>();

	protected DrawingTreeNodeImpl(DrawingImpl<?> drawingImpl, O drawable, GRBinding<O, GR> grBinding, ContainerNodeImpl<?, ?> parentNode) {

		pcSupport = new PropertyChangeSupport(this);

		this.drawing = drawingImpl;
		// logger.info("New DrawingTreeNode for "+aDrawable+" under "+aParentDrawable+" (is "+this+")");
		this.drawable = drawable;
		this.grBinding = grBinding;

		this.parentNode = parentNode;

		Hashtable<Object, DrawingTreeNode<?, ?>> hash = this.drawing.retrieveHash(grBinding);

		hash.put(drawable, this);

		propertyValues = new HashMap<GRProperty, Object>();

		// parentNode.addChild(this);

		graphicalRepresentation = grBinding.getGRProvider().provideGR(drawable, drawing.getFactory());
		if (graphicalRepresentation != null && graphicalRepresentation.getPropertyChangeSupport() != null) {
			graphicalRepresentation.getPropertyChangeSupport().addPropertyChangeListener(this);
		}

		// System.out.println("Hop");

		/*if (aParentDrawable == null) { // This is the root node
			graphicalRepresentation = (GraphicalRepresentation) getDrawingGraphicalRepresentation();
		} else {
			graphicalRepresentation = retrieveGraphicalRepresentation(aDrawable);
		}*/

		dependancies = new ArrayList<ConstraintDependency>();
		alterings = new ArrayList<ConstraintDependency>();

		// controlAreas = new ArrayList<ControlArea<?>>();

		bindingValueObserver = new BindingValueObserver();
	}

	private BindingValueObserver bindingValueObserver;

	// private final Map<GRProperty<?>, BindingValueChangeListener<?>> bindingValueListeners = new HashMap<GRProperty<?>,
	// BindingValueChangeListener<?>>();

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
			listeners = new HashMap<GRBinding.DynamicPropertyValue<?>, BindingValueChangeListener<?>>();
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
		// TODO check this
		return null;
	}

	@Override
	public Drawing<?> getDrawing() {
		return this.drawing;
	}

	@Override
	public FGEModelFactory getFactory() {
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

	@Override
	public void invalidate() {
		// System.out.println("* Invalidate " + drawable.getClass().getSimpleName() + " : " + drawable);
		isInvalidated = true;
		/*for (DrawingTreeNode<?, ?> dtn : childNodes) {
			dtn.invalidate();
		}*/
	}

	@Override
	public void validate() {
		isInvalidated = false;
	}

	@Override
	public boolean isInvalidated() {
		return isInvalidated;
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
		List<DrawingTreeNode<?, ?>> ancestors = new ArrayList<DrawingTreeNode<?, ?>>();
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
			if (parentNode instanceof ContainerNode && ((ContainerNode<?, ?>) parentNode).getChildNodes() != null
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

			notifyObservers(new NodeDeleted(this));

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
				System.out.println(" > " + l);
			}

			Thread.dumpStack();
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

	protected void updateDependanciesForBinding(DataBinding<?> binding) {
		if (binding == null) {
			return;
		}

		// logger.info("Searching dependancies for "+this);

		DrawingTreeNode<?, ?> node = this;
		// TODO !!!!
		List<TargetObject> targetList = binding.getTargetObjects(getBindingEvaluationContext());
		if (targetList != null) {
			for (TargetObject o : targetList) {
				// System.out.println("> "+o.target+" for "+o.propertyName);
				if (o.target instanceof DrawingTreeNode) {
					DrawingTreeNode<?, ?> c = (DrawingTreeNode<?, ?>) o.target;
					GRProperty<?> param = GRProperty.getGRParameter(c.getGraphicalRepresentation().getClass(), o.propertyName);
					// logger.info("OK, found "+getBindingAttribute()+" of "+getOwner()+" depends of "+param+" , "+c);
					try {
						node.declareDependantOf(c, param, param);
					} catch (DependencyLoopException e) {
						logger.warning("DependancyLoopException raised while declaring dependancy (data lookup)"
								+ "in the context of binding: " + binding.toString() + " node: " + node + " dependancy: " + c
								+ " message: " + e.getMessage());
					}
				}
			}
		}

	}

	@Override
	public List<ConstraintDependency> getDependancies() {
		return dependancies;
	}

	@Override
	public List<ConstraintDependency> getAlterings() {
		return alterings;
	}

	@Override
	public void declareDependantOf(DrawingTreeNode<?, ?> aNode, GRProperty requiringParameter, GRProperty requiredParameter)
			throws DependencyLoopException {
		// logger.info("Component "+this+" depends of "+aComponent);
		if (aNode == this) {
			logger.warning("Forbidden reflexive dependancies");
			return;
		}
		// Look if this dependancy may cause a loop in dependancies
		try {
			List<DrawingTreeNode<?, ?>> actualDependancies = new Vector<DrawingTreeNode<?, ?>>();
			actualDependancies.add(aNode);
			searchLoopInDependenciesWith(aNode, actualDependancies);
		} catch (DependencyLoopException e) {
			logger.warning("Forbidden loop in dependancies: " + e.getMessage());
			throw e;
		}

		ConstraintDependency newDependancy = new ConstraintDependency(this, requiringParameter, aNode, requiredParameter);

		if (!dependancies.contains(newDependancy)) {
			dependancies.add(newDependancy);
			logger.info("Parameter " + requiringParameter + " of GR " + this + " depends of parameter " + requiredParameter + " of GR "
					+ aNode);
		}
		if (!((DrawingTreeNodeImpl<?, ?>) aNode).alterings.contains(newDependancy)) {
			((DrawingTreeNodeImpl<?, ?>) aNode).alterings.add(newDependancy);
		}
	}

	private void searchLoopInDependenciesWith(DrawingTreeNode<?, ?> aNode, List<DrawingTreeNode<?, ?>> actualDependancies)
			throws DependencyLoopException {
		for (ConstraintDependency dependancy : ((DrawingTreeNodeImpl<?, ?>) aNode).dependancies) {
			DrawingTreeNode<?, ?> c = dependancy.requiredGR;
			if (c == this) {
				throw new DependencyLoopException(actualDependancies);
			}
			Vector<DrawingTreeNode<?, ?>> newVector = new Vector<DrawingTreeNode<?, ?>>();
			newVector.addAll(actualDependancies);
			newVector.add(c);
			searchLoopInDependenciesWith(c, newVector);
		}
	}

	// *******************************************************************************
	// * Observer implementation *
	// *******************************************************************************

	protected Set<HasPropertyChangeSupport> temporaryIgnoredObservables = new HashSet<HasPropertyChangeSupport>();

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
			fireStructureMayHaveChanged();
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

	@Deprecated
	public void setChanged() {
	}

	public void notifyObservers(FGENotification notification) {
		if ((!(notification instanceof NodeDeleted)) && isDeleted()) {
			logger.warning("notifyObservers() called by a deleted DrawingTreeNode");
			Thread.dumpStack();
			return;
		}
		getPropertyChangeSupport().firePropertyChange(notification.propertyName(), notification.oldValue, notification.newValue);
	}

	public void notifyObservers(String propertyName, Object oldValue, Object newValue) {
		getPropertyChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
	}

	// TODO: (sylvain) i think this is no more necessary, remove this ???
	@Deprecated
	public <T> void notifyAttributeChanged(GRProperty<T> parameter, T oldValue, T newValue) {
		propagateConstraintsAfterModification(parameter);
		setChanged();
		notifyObservers(new FGEAttributeNotification<T>(parameter, oldValue, newValue));
	}

	public void forward(PropertyChangeEvent evt) {
		getPropertyChangeSupport().firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
	}

	// TODO: (sylvain) i think this is no more necessary, remove this ???
	@Deprecated
	protected <T> void propagateConstraintsAfterModification(GRProperty<T> parameter) {
		for (ConstraintDependency dependency : alterings) {
			if (dependency.requiredParameter == parameter) {
				((DrawingTreeNodeImpl<?, ?>) dependency.requiringGR).computeNewConstraint(dependency);
			}
		}
	}

	// TODO: (sylvain) i think this is no more necessary, remove this ???
	@Deprecated
	protected void computeNewConstraint(ConstraintDependency dependency) {
		// None known at this level
	}

	@Override
	public final Point convertNormalizedPointToViewCoordinates(double x, double y, double scale) {
		AffineTransform at = convertNormalizedPointToViewCoordinatesAT(scale);
		FGEPoint returned = new FGEPoint();
		at.transform(new FGEPoint(x, y), returned);
		return new Point((int) returned.x, (int) returned.y);
	}

	@Override
	public Rectangle convertNormalizedRectangleToViewCoordinates(FGERectangle r, double scale) {
		FGEPoint p1 = new FGEPoint(r.x, r.y);
		FGEPoint p2 = new FGEPoint(r.x + r.width, r.y + r.height);
		Point pp1 = convertNormalizedPointToViewCoordinates(p1, scale);
		Point pp2 = convertNormalizedPointToViewCoordinates(p2, scale);
		return new Rectangle(pp1.x, pp1.y, pp2.x - pp1.x, pp2.y - pp1.y);
	}

	@Override
	public abstract AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale);

	@Override
	public final FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y, double scale) {
		AffineTransform at = convertViewCoordinatesToNormalizedPointAT(scale);
		FGEPoint returned = new FGEPoint();
		at.transform(new FGEPoint(x, y), returned);
		return returned;
	}

	@Override
	public abstract AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale);

	@Override
	public Point convertNormalizedPointToViewCoordinates(FGEPoint p, double scale) {
		return convertNormalizedPointToViewCoordinates(p.x, p.y, scale);
	}

	@Override
	public FGEPoint convertViewCoordinatesToNormalizedPoint(Point p, double scale) {
		return convertViewCoordinatesToNormalizedPoint(p.x, p.y, scale);
	}

	@Override
	public boolean isConnectedToDrawing() {
		if (!isValidated()) {
			return false;
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
	public boolean isAncestorOf(DrawingTreeNode<?, ?> child) {
		if (!isValidated()) {
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
	public FGEPoint convertRemoteViewCoordinatesToLocalNormalizedPoint(Point p, DrawingTreeNode<?, ?> source, double scale) {
		if (!isConnectedToDrawing() || !source.isConnectedToDrawing()) {
			return new FGEPoint(p.x / scale, p.y / scale);
		}
		Point pointRelativeToCurrentView = FGEUtils.convertPoint(source, p, this, scale);
		return convertViewCoordinatesToNormalizedPoint(pointRelativeToCurrentView, scale);
	}

	@Override
	public FGEPoint convertLocalViewCoordinatesToRemoteNormalizedPoint(Point p, DrawingTreeNode<?, ?> destination, double scale) {
		if (!isConnectedToDrawing() || !destination.isConnectedToDrawing()) {
			return new FGEPoint(p.x * scale, p.y * scale);
		}
		Point pointRelativeToRemoteView = FGEUtils.convertPoint(this, p, destination, scale);
		return destination.convertViewCoordinatesToNormalizedPoint(pointRelativeToRemoteView, scale);
	}

	@Override
	public Point convertLocalNormalizedPointToRemoteViewCoordinates(FGEPoint p, DrawingTreeNode<?, ?> destination, double scale) {
		Point point = convertNormalizedPointToViewCoordinates(p, scale);
		return FGEUtils.convertPoint(this, point, destination, scale);
	}

	@Override
	public Rectangle convertLocalNormalizedRectangleToRemoteViewCoordinates(FGERectangle r, DrawingTreeNode<?, ?> destination, double scale) {
		FGEPoint p1 = new FGEPoint(r.x, r.y);
		FGEPoint p2 = new FGEPoint(r.x + r.width, r.y + r.height);
		Point pp1 = convertLocalNormalizedPointToRemoteViewCoordinates(p1, destination, scale);
		Point pp2 = convertLocalNormalizedPointToRemoteViewCoordinates(p2, destination, scale);
		return new Rectangle(pp1.x, pp1.y, pp2.x - pp1.x, pp2.y - pp1.y);
	}

	@Override
	public Point convertRemoteNormalizedPointToLocalViewCoordinates(FGEPoint p, DrawingTreeNode<?, ?> source, double scale) {
		Point point = source.convertNormalizedPointToViewCoordinates(p, scale);
		return FGEUtils.convertPoint(source, point, this, scale);
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
	public FGERectangle getNormalizedBounds() {
		return new FGERectangle(0, 0, 1, 1, Filling.FILLED);
	}

	/**
	 * Return boolean indicating if this graphical representation is validated. A validated graphical representation is a graphical
	 * representation fully embedded in its graphical representation tree, which means that parent and child are set and correct, and that
	 * start and end shapes are set for connectors
	 * 
	 * 
	 * @return
	 */
	@Override
	public boolean isValidated() {
		return validated;
	}

	@Override
	public void setValidated(boolean validated) {
		this.validated = validated;
	}

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
		if (!isValidated()) {
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
		if (!isValidated()) {
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
		return getGraphicalRepresentation().getIsVisible() && getParentNode() != null && getParentNode().shouldBeDisplayed();
	}

	@Override
	public void notifyLabelWillBeEdited() {
		setChanged();
		notifyObservers(new LabelWillEdit());
	}

	@Override
	public void notifyLabelHasBeenEdited() {
		setChanged();
		notifyObservers(new LabelHasEdited());
	}

	@Override
	public void notifyLabelWillMove() {
		setChanged();
		notifyObservers(new LabelWillMove());
	}

	@Override
	public void notifyLabelHasMoved() {
		setChanged();
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
		if (aFlag != isFocused) {
			isFocused = aFlag;
			setChanged();
			notifyObservers(new FGEAttributeNotification(IS_FOCUSED, !isFocused, isFocused));
		}
	}

	@Override
	public boolean getIsSelected() {
		return isSelected;
	}

	@Override
	public void setIsSelected(boolean aFlag) {
		if (aFlag != isSelected) {
			isSelected = aFlag;
			setChanged();
			notifyObservers(new FGEAttributeNotification(IS_SELECTED, !isSelected, isSelected));
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
		// New architecture of FGE now authorize a GR to be shared by many DrawingTreeNode
		// If UniqueGraphicalRepresentations is active, use GR to store graphical properties

		if (getDrawing().getPersistenceMode() == PersistenceMode.UniqueGraphicalRepresentations) {
			if (getGraphicalRepresentation() == null) {
				return null;
			}
			if (getGraphicalRepresentation().hasKey(parameter.getName())) {
				return (T) getGraphicalRepresentation().objectForKey(parameter.getName());
			}
			else {
				return null;
			}
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
				else {
					returned = null;
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
		if (hasDynamicSettablePropertyValue(parameter)) {
			try {
				setDynamicPropertyValue(parameter, value);
				return;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		// Now we have to think of this:
		// New architecture of FGE now authorize a GR to be shared by many DrawingTreeNode
		// If UniqueGraphicalRepresentations is active, use GR to store graphical properties

		if (getDrawing().getPersistenceMode() == PersistenceMode.UniqueGraphicalRepresentations) {
			boolean wasObserving = ignoreNotificationsFrom(getGraphicalRepresentation());
			T oldValue = null;
			if (getGraphicalRepresentation().hasKey(parameter.getName())) {
				oldValue = (T) getGraphicalRepresentation().objectForKey(parameter.getName());
				getGraphicalRepresentation().setObjectForKey(value, parameter.getName());
			}
			if (wasObserving) {
				observeAgain(getGraphicalRepresentation());
			}
			// Since GR is prevented to fire notifications, do it myself
			getPropertyChangeSupport().firePropertyChange(parameter.getName(), oldValue, value);
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
			if (variable.getVariableName().equals("this")) {
				return getGraphicalRepresentation();
			}
			else if (variable.getVariableName().equals("parent")) {
				return getParentNode().getGraphicalRepresentation();
			}
			else if (variable.getVariableName().equals("drawable")) {
				return getDrawable();
			}
			else if (variable.getVariableName().equals("gr")) {
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
	 * Convenient method used to retrieve text property value
	 */
	@Override
	public String getText() {
		/*if (hasDynamicPropertyValue(GraphicalRepresentation.TEXT)) {
			DataBinding<String> db = getGRBinding().getDynamicPropertyValue(GraphicalRepresentation.TEXT).dataBinding;
			if (db.toString().equals("drawable.name")) {
				System.out.println("Content de l'avoir celui la !!!!!!!!!!");
				System.out.println("valid=" + db.isValid());
				System.out.println("reason:" + db.invalidBindingReason());
				System.out.println("bindable:" + db.getOwner());
				System.out.println("bindingModel:" + db.getOwner().getBindingModel());
				System.out.println("bindingFactory:" + db.getOwner().getBindingFactory());
				System.out.println("expression " + db.getExpression() + " of " + db.getExpression().getClass());
				Expression expression = db.getExpression();
				System.out.println("BP");
				String value = null;
				try {
					value = getGRBinding().getDynamicPropertyValue(GraphicalRepresentation.TEXT).dataBinding
							.getBindingValue(getBindingEvaluationContext());
				} catch (TypeMismatchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullReferenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("valeur=" + value);
			}

		}*/

		return getPropertyValue(GraphicalRepresentation.TEXT);
	}

	/**
	 * Convenient method used to set text property value
	 */
	@Override
	public void setText(String text) {
		// System.out.println("set text with " + text);
		setPropertyValue(GraphicalRepresentation.TEXT, text);
	}

	@Override
	public boolean hasText() {
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
