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

package org.openflexo.diana.layout.impl;

import java.awt.Point;
import java.beans.PropertyChangeEvent;

import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.impl.FGELayoutManagerImpl;
import org.openflexo.diana.layout.OutlineLayoutManager;
import org.openflexo.diana.layout.OutlineLayoutManagerSpecification;
import org.openflexo.diana.layout.OutlineLayoutManagerSpecification.OutlineLocationType;

/**
 * Default implementation for {@link OutlineLayoutManager}
 * 
 * @author sylvain
 * 
 */
public abstract class OutlineLayoutManagerImpl<O> extends FGELayoutManagerImpl<OutlineLayoutManagerSpecification, O>
		implements OutlineLayoutManager<O> {

	@Override
	public OutlineLocationType getOutlineLocationType() {
		return getLayoutManagerSpecification().getOutlineLocationType();
	}

	/**
	 * Return flag indicating if the move or resize of one node might invalidate the whole container
	 * 
	 * @return
	 */
	@Override
	public boolean isFullyLayouted() {
		// Temporary fixes issues when border of shape changes because of layouted shapes are moving
		return true;
	}

	/**
	 * Perform layout for supplied {@link ShapeNode}
	 * 
	 * @param node
	 *            node to layout
	 */
	@Override
	protected void performLayout(ShapeNode<?> node) {

		// Applicable only to SHAPE as container
		if (getContainerNode() instanceof ShapeNode) {

			// System.out.println("Relayouting " + node);

			// Forces the shape node beeing layouted to leave it's original bounds
			// (this outline layout put the layouted shapes at the border of represented shape)
			node.setAllowsToLeaveBounds(true);

			// Then perform the layout, according to the center of layouted shape
			FGEPoint currentPoint = new FGEPoint(node.getLocation());
			ShapeNode<?> container = (ShapeNode<?>) getContainerNode();
			FGEPoint center = new FGEPoint(currentPoint.x + node.getWidth() / 2, currentPoint.y + node.getHeight() / 2);
			FGEPoint normalizedCenterPoint = container.convertViewCoordinatesToNormalizedPoint((int) center.getX(), (int) center.getY(),
					1.0);
			// FGEPoint normalizedPointInOutline = container.getShape().outlineIntersect(normalizedCenterPoint);
			FGEPoint normalizedPointInOutline = container.getShape().nearestOutlinePoint(normalizedCenterPoint);
			Point pointInOutline = container.convertNormalizedPointToViewCoordinates(normalizedPointInOutline, 1.0);
			node.setLocation(new FGEPoint(pointInOutline.x - node.getWidth() / 2, pointInOutline.y - node.getHeight() / 2));
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		// System.out.println("propertyChange in OutlineLayoutManagerImpl with " + evt.getPropertyName() + " evt=" + evt);

		super.propertyChange(evt);
		if (evt.getPropertyName().equals(OutlineLayoutManagerSpecification.OUTLINE_LOCATION_TYPE_KEY)) {
			invalidate();
			doLayout(true);
			// getContainerNode().notifyNodeLayoutDecorationChanged(this);
		}
	}
}
