/*
 * (c) Copyright 2010-2011 AgileBirds
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
import org.openflexo.fge.shapes.RectangularOctogon;

public abstract class RectangularOctogonImpl extends ShapeSpecificationImpl implements RectangularOctogon {

	private double	ratio	= 0.2;

	@Override
	public ShapeType getShapeType() {
		return ShapeType.RECTANGULAROCTOGON;
	}

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public RectangularOctogonImpl() {
		super();
	}

	/**
	 * Set ratio
	 * 
	 * @param ratio
	 *            if ratio > 0.5 ratio is divide by 2
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setRatio(final double aRatio) {
		final FGEAttributeNotification<Double> notification = this.requireChange(RATIO, aRatio);
		if (notification != null) {
			this.ratio = aRatio;
			this.hasChanged(notification);
		}
	}

	@Override
	public double getRatio() {
		return this.ratio;
	}

	@Override
	public FGEShape<?> makeFGEShape(final ShapeNode<?> node) {
		final FGEPolygon returned = new FGEPolygon(Filling.FILLED);

		returned.addToPoints(new FGEPoint(0, this.ratio));
		returned.addToPoints(new FGEPoint(0, 1 - this.ratio));
		returned.addToPoints(new FGEPoint(this.ratio / 2, 1));
		returned.addToPoints(new FGEPoint(1 - this.ratio / 2, 1));
		returned.addToPoints(new FGEPoint(1, 1 - this.ratio));
		returned.addToPoints(new FGEPoint(1, this.ratio));
		returned.addToPoints(new FGEPoint(1 - this.ratio / 2, 0));
		returned.addToPoints(new FGEPoint(this.ratio / 2, 0));

		return returned;

	}

}
