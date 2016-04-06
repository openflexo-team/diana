/**
 * 
 * Copyright (c) 2016, Openflexo
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

package org.openflexo.fge.layout.impl;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.impl.FGELayoutManagerImpl;
import org.openflexo.fge.layout.FlowLayoutManager;
import org.openflexo.fge.layout.FlowLayoutManagerSpecification;
import org.openflexo.fge.layout.FlowLayoutManagerSpecification.FlowOrientationType;

/**
 * Default implementation for {@link FlowLayoutManager}
 * 
 * @author fabien
 * 
 */
public abstract class FlowLayoutManagerImpl<O> extends FGELayoutManagerImpl<FlowLayoutManagerSpecification, O>
		implements FlowLayoutManager<O> {

	@Override
	public FlowOrientationType getFlowOrientationType() {
		return getLayoutManagerSpecification().getFlowOrientationType();
	}

	/**
	 * Return flag indicating if the move or resize of one node might invalidate the whole container
	 * 
	 * @return
	 */
	@Override
	public boolean isFullyLayouted() {
		return true;
	}

	private Map<ShapeNode<?>, FGEPoint> locationMap = new HashMap<>();

	@Override
	public void computeLayout() {
		super.computeLayout();
		/* TODO : should become a property of the LayoutManager */
		double separation = 10;
		
		/* TODO : should support horizontal placement of the LayoutManager */
		double xAxis = getContainerNode().getWidth()/2;
		double yLocation = 0;
		for (ShapeNode<?> shapeNode : getLayoutedNodes()) {
			FGEPoint location = new FGEPoint();
			location.x = xAxis - shapeNode.getWidth()/2;
			location.y = yLocation + separation;
			yLocation += shapeNode.getHeight() + separation;
			locationMap.put(shapeNode, location);
		}
	}

	/**
	 * Perform layout for supplied {@link ShapeNode}
	 * 
	 * @param node
	 *            node to layout
	 */
	@Override
	protected void performLayout(ShapeNode<?> node) {
		node.setLocation(locationMap.get(node));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getPropertyName().equals(FlowLayoutManagerSpecification.FLOW_ORIENTATION_TYPE_KEY)) {
			invalidate();
			doLayout(true);
			// getContainerNode().notifyNodeLayoutDecorationChanged(this);
		}
	}
}
