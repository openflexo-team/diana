/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geom, a component of the software infrastructure 
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

package org.openflexo.diana.geom.area;

import java.awt.geom.AffineTransform;

import org.openflexo.diana.geom.FGEAbstractLine;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.geom.FGERectangle;
import org.openflexo.diana.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.graphics.AbstractFGEGraphics;

public class FGEPlane implements FGEArea {

	public FGEPlane() {
		super();
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		return true;
	}

	@Override
	public boolean containsArea(FGEArea a) {
		return true;
	}

	@Override
	public boolean containsLine(FGEAbstractLine<?> l) {
		return true;
	}

	@Override
	public FGEArea exclusiveOr(FGEArea area) {
		return new FGEEmptyArea();
	}

	@Override
	public FGEArea intersect(FGEArea area) {
		return area.clone();
	}

	@Override
	public FGEArea substract(FGEArea area, boolean isStrict) {
		return new FGESubstractionArea(this, area, isStrict);
	}

	@Override
	public FGEArea union(FGEArea area) {
		if (containsArea(area)) {
			return clone();
		}
		if (area.containsArea(this)) {
			return area.clone();
		}

		return clone();
	}

	/**
	 * Creates a new object of the same class and with the same contents as this object.
	 * 
	 * @return a clone of this instance.
	 * @exception OutOfMemoryError
	 *                if there is not enough memory.
	 * @see java.lang.Cloneable
	 * @since 1.2
	 */
	@Override
	public FGEPlane clone() {
		try {
			return (FGEPlane) super.clone();
		} catch (CloneNotSupportedException e) {
			// cannot happen
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return "FGEPlane";
	}

	@Override
	public FGEPlane transform(AffineTransform t) {
		return clone();
	}

	@Override
	public void paint(AbstractFGEGraphics g) {
		// TODO
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		return aPoint.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGEPlane) {
			return true;
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		return this;
	}

	@Override
	public FGEArea getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		return this;
	}

	/**
	 * This area is infinite, so always return false
	 */
	@Override
	public final boolean isFinite() {
		return false;
	}

	/**
	 * This area is infinite, so always return null
	 */
	@Override
	public final FGERectangle getEmbeddingBounds() {
		return null;
	}

	/**
	 * Return nearest point from point "from" following supplied orientation
	 * 
	 * Returns null if no intersection was found
	 * 
	 * @param from
	 *            point from which we are coming to area
	 * @param orientation
	 *            orientation we are coming from
	 * @return
	 */
	@Override
	public FGEPoint nearestPointFrom(FGEPoint from, SimplifiedCardinalDirection orientation) {
		return from.clone();
	}

}
