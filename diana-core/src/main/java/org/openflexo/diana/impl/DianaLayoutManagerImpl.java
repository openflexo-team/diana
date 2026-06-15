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

package org.openflexo.diana.impl;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.java.JavaBindingFactory;
import org.openflexo.diana.DianaLayoutManager;
import org.openflexo.diana.DianaLayoutManagerSpecification;
import org.openflexo.diana.DianaLayoutManagerSpecification.DraggingMode;
import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.layout.LayoutConstraints;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.graphics.DianaGraphics;

/**
 * Default implementation for {@link DianaLayoutManager}
 * 
 * @author sylvain
 * 
 */
public abstract class DianaLayoutManagerImpl<LMS extends DianaLayoutManagerSpecification<?>, O> extends DianaObjectImpl
		implements DianaLayoutManager<LMS, O> {

	private boolean invalidated = true;

	// Nodes beeing layouted
	private final List<ShapeNode<?>> layoutedNodes;

	// Child GRs and their LayoutConstraints objects this manager has subscribed to, to react to per-child layout
	// changes (e.g. editing a weight / region / grid cell in an inspector). Kept in sync with layoutedNodes in
	// retrieveNodesToLayout().
	private final List<ShapeNode<?>> observedChildren = new ArrayList<>();
	private final List<LayoutConstraints> observedConstraints = new ArrayList<>();

	public DianaLayoutManagerImpl() {
		layoutedNodes = new ArrayList<ShapeNode<?>>() {
			@Override
			public boolean add(ShapeNode<?> shapeNode) {
				if (!getIdentifier().equals(shapeNode.getGraphicalRepresentation().getLayoutManagerIdentifier())) {
					// In this case, we have detected that the node was not layouted with this layout manager
					// Just add it
					shapeNode.getGraphicalRepresentation().setLayoutManagerIdentifier(getIdentifier());
					return super.add(shapeNode);
				}
				return super.add(shapeNode);
			}

			@Override
			public boolean remove(Object o) {
				if (o instanceof ShapeNode) {
					ShapeNode<?> shapeNode = (ShapeNode<?>) o;
					if (getIdentifier().equals(shapeNode.getGraphicalRepresentation().getLayoutManagerIdentifier())) {
						// In this case, we have detected that the node was layouted with this layout manager
						// Just remove it
						shapeNode.getGraphicalRepresentation().setLayoutManagerIdentifier(null);
						return super.remove(o);
					}
					return super.remove(o);
				}
				return false;
			}
		};
	}

	@Override
	public String getIdentifier() {
		if (getLayoutManagerSpecification() != null) {
			return getLayoutManagerSpecification().getIdentifier();
		}
		return null;
	}

	/**
	 * Called to invalidate the whole layout<br>
	 * All contained {@link ShapeNode} will be invalidated
	 */
	@Override
	public void invalidate() {
		invalidated = true;
		ContainerNode<O, ?> ctn = getContainerNode();
		// NPE Protection
		if (ctn != null) {
			for (DrawingTreeNode<?, ?> dtn : ctn.getChildNodes()) {
				if (dtn instanceof ShapeNode) {
					if (((ShapeNode<O>) dtn).getActiveLayoutManager() == this) {
						invalidate((ShapeNode<O>) dtn);
					}
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
		// Invalidate all layoutedNodes
		if (isFullyLayouted()) {
			for (ShapeNode<?> dtn : layoutedNodes) {
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
		for (ShapeNode<?> node : layoutedNodes) {
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
		// If the whole layout is invalidated, just return
		if (invalidated) {
			return;
		}
		if (!node.isLayoutValidated() || force) {
			performLayout(node);
		}
	}

	/**
	 * Perform layout for supplied {@link ShapeNode}
	 * 
	 * @param node
	 *            node to layout
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
	public void shapeMoved(DianaPoint oldLocation, DianaPoint location) {
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
	 * Internally used to retrieve in the container all layoutedNodes which are to be layouted
	 */
	private void retrieveNodesToLayout() {

		layoutedNodes.clear();
		for (DrawingTreeNode<?, ?> dtn : getContainerNode().getChildNodes()) {
			if (dtn instanceof ShapeNode) {
				if (((ShapeNode<O>) dtn).getActiveLayoutManager() == this) {
					layoutedNodes.add((ShapeNode<O>) dtn);
				}
			}
		}

		// Detach listeners before (re)assigning default constraints so the assignment does not re-enter propertyChange,
		// then re-attach to the current children and their constraints objects.
		detachChildListeners();
		assignDefaultConstraints();
		attachChildListeners();

		getPropertyChangeSupport().firePropertyChange("layoutedNodes", null, layoutedNodes);

	}

	/**
	 * Ensures every layouted child carries a {@link LayoutConstraints} of the type this manager understands (from
	 * {@link #makeDefaultConstraints()}): a child with no constraints, or constraints of the wrong manager's type (e.g. left over from a
	 * previous manager), is given a fresh default. No-op for managers without per-child data ({@code makeDefaultConstraints() == null}).
	 */
	private void assignDefaultConstraints() {
		LayoutConstraints sample = makeDefaultConstraints();
		if (sample == null) {
			return;
		}
		for (ShapeNode<?> n : layoutedNodes) {
			ShapeGraphicalRepresentation gr = n.getGraphicalRepresentation();
			if (gr == null) {
				continue;
			}
			LayoutConstraints current = gr.getLayoutConstraints();
			if (current == null || !sample.getClass().isInstance(current)) {
				gr.setLayoutConstraints(makeDefaultConstraints());
			}
		}
	}

	/**
	 * Subscribes this manager to each layouted child's {@link ShapeGraphicalRepresentation} (for {@code layoutConstraints} replacement and
	 * {@code layoutManagerIdentifier} changes) and to its {@link LayoutConstraints} object (for per-field edits), so editing a child's layout
	 * property through an inspector re-triggers the layout.
	 */
	private void attachChildListeners() {
		for (ShapeNode<?> n : layoutedNodes) {
			ShapeGraphicalRepresentation gr = n.getGraphicalRepresentation();
			if (gr != null && gr.getPropertyChangeSupport() != null) {
				gr.getPropertyChangeSupport().addPropertyChangeListener(this);
				observedChildren.add(n);
				LayoutConstraints c = gr.getLayoutConstraints();
				if (c != null && c.getPropertyChangeSupport() != null) {
					c.getPropertyChangeSupport().addPropertyChangeListener(this);
					observedConstraints.add(c);
				}
			}
		}
	}

	/** Unsubscribes from every previously observed child GR and constraints object. */
	private void detachChildListeners() {
		for (ShapeNode<?> n : observedChildren) {
			ShapeGraphicalRepresentation gr = n.getGraphicalRepresentation();
			if (gr != null && gr.getPropertyChangeSupport() != null) {
				gr.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
		}
		observedChildren.clear();
		for (LayoutConstraints c : observedConstraints) {
			if (c.getPropertyChangeSupport() != null) {
				c.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
		}
		observedConstraints.clear();
	}

	@Override
	public void randomLayout(boolean force) {

		// computeLayout();

		layoutInProgress = true;
		for (ShapeNode<?> node : layoutedNodes) {
			if (node.isValid()) {
				int randX = (new Random()).nextInt((int) getContainerNode().getWidth());
				int randY = (new Random()).nextInt((int) getContainerNode().getHeight());
				node.setLocation(new DianaPoint(randX, randY));
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
		if (getLayoutManagerSpecification() != null) {
			return getLayoutManagerSpecification().supportDecoration();
		}
		return false;
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
	public void paintDecoration(DianaGraphics g) {
	}

	@Override
	public DraggingMode getDraggingMode() {
		if (getLayoutManagerSpecification() == null) {
			return DraggingMode.FreeDraggingNoLayout;
		}
		return getLayoutManagerSpecification().getDraggingMode();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// System.out.println("Received " + evt.getPropertyName() + " with " + evt);
		String propertyName = evt.getPropertyName();
		// A per-child layout change re-triggers the layout: either a field of a child's LayoutConstraints object was
		// edited (e.g. a weight / region / grid cell in an inspector), or the child replaced its constraints / changed
		// the manager it opts into. Guarded against re-entrancy (a layout pass writes x/y/width/height on the GR, which
		// is neither a LayoutConstraints source nor the two GR keys below, so it cannot loop here).
		if (evt.getSource() instanceof LayoutConstraints) {
			if (!layoutInProgress) {
				invalidate();
				doLayout(true);
			}
			return;
		}
		if (evt.getSource() instanceof ShapeGraphicalRepresentation
				&& (ShapeGraphicalRepresentation.LAYOUT_CONSTRAINTS_KEY.equals(propertyName)
						|| ShapeGraphicalRepresentation.LAYOUT_MANAGER_IDENTIFIER_KEY.equals(propertyName))) {
			if (!layoutInProgress) {
				invalidate();
				doLayout(true);
			}
			return;
		}
		// A layouted child's own geometry changed from outside a layout pass (e.g. its size/position edited in the
		// Location/Size inspector): re-run the layout so the change is taken into account (a width change re-wraps a
		// flow, re-distributes a box/gridbag, …). The manager subscribes only to its layouted children's GRs, so any
		// such event is from a managed child. Guarded by layoutInProgress: the manager's own setLocation/setSize during
		// a layout pass fire these same keys but must not re-enter.
		if (evt.getSource() instanceof ShapeGraphicalRepresentation
				&& (ShapeGraphicalRepresentation.X_KEY.equals(propertyName) || ShapeGraphicalRepresentation.Y_KEY.equals(propertyName)
						|| ShapeGraphicalRepresentation.WIDTH_KEY.equals(propertyName)
						|| ShapeGraphicalRepresentation.HEIGHT_KEY.equals(propertyName))) {
			if (!layoutInProgress) {
				invalidate();
				doLayout(true);
			}
			return;
		}
		if (propertyName.equals(DianaLayoutManagerSpecification.DELETED)) {
			delete();
		}
		else if (evt.getPropertyName().equals(DianaLayoutManagerSpecification.DRAGGING_MODE_KEY)) {
			// Nothing to do yet
		}
		else if (evt.getPropertyName().equals(DianaLayoutManagerSpecification.PAINT_DECORATION_KEY)) {
			getContainerNode().notifyNodeLayoutDecorationChanged(this);
		}
	}

	@Override
	public void attemptToPlaceNodeManually(ShapeNode<?> node) {
		// System.out.println("On essaie de fixer " + node.getText() + " a " + node.getLocation());
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

	/**
	 * Default implementation returns {@code null}: this layout manager exposes no per-child layout property panel. Subclasses that have
	 * editable per-child properties (e.g. a weight) override this to return their child-inspector FIB.
	 */
	@Override
	public org.openflexo.rm.Resource getChildInspectorFIB() {
		return null;
	}

	/**
	 * Default implementation returns {@code null}: this layout manager has no per-child constraint data. Managers with per-child constraints
	 * (Box/Border/GridBag) override this to return a fresh constraints object of their type.
	 */
	@Override
	public LayoutConstraints makeDefaultConstraints() {
		return null;
	}

	@Override
	public boolean delete(Object... context) {
		detachChildListeners();
		for (ShapeNode<?> n : layoutedNodes) {
			// Disconnect all layouted layoutedNodes from related DianaLayoutManagerSpecification
			n.getGraphicalRepresentation().setLayoutManagerIdentifier(null);
		}
		return super.delete(context);
	}

	@Override
	public List<ShapeNode<?>> getLayoutedNodes() {
		return layoutedNodes;
	}

	@Override
	public void setLayoutedNodes(List<ShapeNode<?>> nodes) {
	}

	@Override
	public void addToLayoutedNodes(ShapeNode<?> node) {
		System.out.println("on rajoute le noeud " + node);
		node.getGraphicalRepresentation().setLayoutManagerIdentifier(getIdentifier());
	}

	@Override
	public void removeFromLayoutedNodes(ShapeNode<?> node) {
		System.out.println("on enleve le noeud " + node);
		node.getGraphicalRepresentation().setLayoutManagerIdentifier(null);
	}

	@Override
	public BindingModel getBindingModel() {
		return null;
	}

	private static BindingFactory BINDING_FACTORY = new JavaBindingFactory();

	@Override
	public BindingFactory getBindingFactory() {
		return BINDING_FACTORY;
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
	}

	@Override
	public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
	}
}
