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

package org.openflexo.fge.shapes.impl;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.fge.shapes.Chevron;

/**
 * Implementation of interface Chevron.
 * 
 * @author eloubout
 * 
 */
public abstract class ChevronImpl extends ShapeSpecificationImpl implements Chevron {

	private double arrowLength = 0.2;

	public ChevronImpl() {
		super();
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.CHEVRON;
	}

	@Override
	public double getArrowLength() {
		return this.arrowLength;
	}

	@Override
	public void setArrowLength(final double anArrowLength) {
		final FGEAttributeNotification<Double> notification = this.requireChange(ARROW_LENGTH, anArrowLength);
		if (notification != null) {
			this.arrowLength = anArrowLength;
			this.hasChanged(notification);
		}
	}

	/**
	 * Draw a chevron by creating a filled polygon of 6 good points, ajustable with predifined arrowLenght
	 * 
	 * @return FGEPolygon
	 */
	@Override
	public FGEShape<?> makeFGEShape(final ShapeNode<?> node) {
		final FGEPolygon returned = new FGEPolygon(Filling.FILLED);
		returned.addToPoints(new FGEPoint(0, 0));
		returned.addToPoints(new FGEPoint(this.arrowLength, 0.5));
		returned.addToPoints(new FGEPoint(0, 1));
		returned.addToPoints(new FGEPoint(1 - this.arrowLength, 1));
		returned.addToPoints(new FGEPoint(1, 0.5));
		returned.addToPoints(new FGEPoint(1 - this.arrowLength, 0));
		return returned;
	}

}
