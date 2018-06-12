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
