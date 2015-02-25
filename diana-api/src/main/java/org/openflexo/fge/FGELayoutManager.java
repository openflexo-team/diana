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

package org.openflexo.fge;

import java.beans.PropertyChangeListener;
import java.util.List;

import org.openflexo.connie.Bindable;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGELayoutManagerSpecification.DraggingMode;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;

/**
 * Represents a LayoutManager in DIANA<br>
 * 
 * A {@link FGELayoutManager} is associated to a {@link ContainerNode} and is used to layout multiple {@link DrawingTreeNode}
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
public interface FGELayoutManager<LMS extends FGELayoutManagerSpecification<?>, O> extends FGEObject, Bindable, PropertyChangeListener {

	@PropertyIdentifier(type = FGELayoutManagerSpecification.class)
	public static final String LAYOUT_MANAGER_SPECIFICATION_KEY = "layoutManagerSpecification";
	@PropertyIdentifier(type = ContainerNode.class)
	public static final String CONTAINER_NODE_KEY = "containerNode";
	@PropertyIdentifier(type = DrawingTreeNode.class, cardinality = Cardinality.SINGLE)
	public static final String LAYOUTED_NODES_KEY = "layoutedNodes";

	@Getter(value = LAYOUT_MANAGER_SPECIFICATION_KEY, ignoreType = true)
	public LMS getLayoutManagerSpecification();

	@Setter(LAYOUT_MANAGER_SPECIFICATION_KEY)
	public void setLayoutManagerSpecification(LMS layoutManagerSpecification);

	@Getter(value = CONTAINER_NODE_KEY, ignoreType = true)
	public ContainerNode<O, ?> getContainerNode();

	@Setter(CONTAINER_NODE_KEY)
	public void setContainerNode(ContainerNode<O, ?> node);

	@Getter(value = LAYOUTED_NODES_KEY, cardinality = Cardinality.LIST, ignoreType = true)
	public List<DrawingTreeNode<?, ?>> getLayoutedNodes();

	@Setter(value = LAYOUTED_NODES_KEY)
	public void setLayoutedNodes(List<DrawingTreeNode<?, ?>> nodes);

	@Adder(value = LAYOUTED_NODES_KEY)
	public void addToLayoutedNodes(DrawingTreeNode<?, ?> node);

	@Remover(value = LAYOUTED_NODES_KEY)
	public void removeFromLayoutedNodes(DrawingTreeNode<?, ?> node);

	/**
	 * Called to invalidate the whole layout<br>
	 * All contained {@link ShapeNode} will be invalidated
	 */
	public void invalidate();

	/**
	 * Called to invalidate a {@link ShapeNode}
	 * 
	 * @param node
	 */
	public void invalidate(ShapeNode<?> node);

	/**
	 * Perform layout for all invalidated {@link ShapeNode} contained in this layout
	 */
	public void doLayout(boolean force);

	/**
	 * Perform layout for supplied {@link ShapeNode}, if this node is invalidated<br>
	 * If node was not invalidated, simply return
	 * 
	 * @param node
	 */
	public void doLayout(ShapeNode<?> node, boolean force);

	/**
	 * Compute the whole layout, do not place elements
	 */
	public void computeLayout();

	/**
	 * Return flag indicating if the move or resize of one node might invalidate the whole container
	 * 
	 * @return
	 */
	public boolean isFullyLayouted();

	/**
	 * Return flag indicating whether this layout manager supports autolayout
	 * 
	 * @return
	 */
	public boolean supportAutolayout();

	/**
	 * Return flag indicating whether this layout manager supports decoration painting<br>
	 * 
	 * @return
	 */
	public boolean supportDecoration();

	/**
	 * Return flag indicating whether layout manager decoration is to be paint<br>
	 * Note that this is relevant only if this layout manager supports decoration painting
	 * 
	 * @return
	 */
	public Boolean paintDecoration();

	/**
	 * Return Dragging-mode to use
	 * 
	 * @return
	 */
	public DraggingMode getDraggingMode();

	/**
	 * Hook used to detect that a shape has moved from a location to another location
	 * 
	 * @param oldLocation
	 * @param location
	 */
	public void shapeMoved(FGEPoint oldLocation, FGEPoint location);

	/**
	 * Return flag indicating if layout is in progress
	 * 
	 * @return
	 */
	public boolean isLayoutInProgress();

	public void attemptToPlaceNodeManually(ShapeNode<?> node);

	/**
	 * Randomly layout invalidated {@link ShapeNode} contained in this layout, all when force to true
	 */
	public void randomLayout(boolean force);

	/**
	 * Return {@link ControlArea} managed by this layout manager<br>
	 * Default value is null
	 * 
	 * @return
	 */
	public List<ControlArea<?>> getControlAreas();

	public String getIdentifier();
}
