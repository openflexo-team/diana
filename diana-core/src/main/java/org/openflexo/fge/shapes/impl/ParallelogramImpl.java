/*
 * (c) Copyright 2014- Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.fge.shapes.impl;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.fge.shapes.Parallelogram;

/**
 * Implementation of interface Parallelogram.
 * 
 * @author xtof
 * 
 */
public abstract class ParallelogramImpl extends ShapeSpecificationImpl implements Parallelogram {

	// angle is contained between 0 and 180
	private double shift_ratio = 0.2;

	public ParallelogramImpl() {
		super();
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.PARALLELOGRAM;
	}

	@Override
	public double getShiftRatio() {
		return this.shift_ratio;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setShiftRatio(final double aShiftRatio) {
		double normalized = Math.abs(aShiftRatio);
		if (normalized > 1) {
			normalized = aShiftRatio / normalized;
		}
		else
			normalized = aShiftRatio;
		final FGEAttributeNotification<Double> notification = this.requireChange(SHIFT_RATIO, normalized);
		if (notification != null) {
			this.shift_ratio = normalized;
			this.hasChanged(notification);
		}
	}

	/**
	 * Draw a parallellogram by creating a filled polygon of 4 good points, adjustable with predifined angle
	 * 
	 * @return FGEPolygon
	 */
	@Override
	public FGEShape<?> makeFGEShape(final ShapeNode<?> node) {
		final FGEPolygon returned = new FGEPolygon(Filling.FILLED);
		if (shift_ratio >= 0) {
			returned.addToPoints(new FGEPoint(shift_ratio, 0));
			returned.addToPoints(new FGEPoint(1, 0));
			returned.addToPoints(new FGEPoint(1 - shift_ratio, 1));
			returned.addToPoints(new FGEPoint(0, 1));
		}
		else {
			returned.addToPoints(new FGEPoint(0, 0));
			returned.addToPoints(new FGEPoint(1 + shift_ratio, 0));
			returned.addToPoints(new FGEPoint(1, 1));
			returned.addToPoints(new FGEPoint(-shift_ratio, 1));
		}
		return returned;
	}
}
