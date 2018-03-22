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

import org.openflexo.diana.geom.DianaAbstractLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

public class DianaEmptyArea implements DianaArea {

	public DianaEmptyArea() {
		super();
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		return false;
	}

	@Override
	public boolean containsArea(DianaArea a) {
		return false;
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		return false;
	}

	@Override
	public DianaArea exclusiveOr(DianaArea area) {
		return new DianaEmptyArea();
	}

	@Override
	public DianaArea intersect(DianaArea area) {
		return new DianaEmptyArea();
	}

	@Override
	public DianaArea substract(DianaArea area, boolean isStrict) {
		return new DianaEmptyArea();
	}

	@Override
	public DianaArea union(DianaArea area) {
		return area.clone();
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
	public DianaEmptyArea clone() {
		try {
			return (DianaEmptyArea) super.clone();
		} catch (CloneNotSupportedException e) {
			// cannot happen
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return "DianaEmptyArea";
	}

	@Override
	public DianaEmptyArea transform(AffineTransform t) {
		return clone();
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		// Easy !
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint aPoint) {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaEmptyArea) {
			return true;
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public DianaEmptyArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		return this;
	}

	@Override
	public DianaEmptyArea getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		return this;
	}

	/**
	 * This area trivially finite, so return true
	 */
	@Override
	public final boolean isFinite() {
		return true;
	}

	/**
	 * This area is trivially finite, but always return null (!)
	 */
	@Override
	public final DianaRectangle getEmbeddingBounds() {
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
	public DianaPoint nearestPointFrom(DianaPoint from, SimplifiedCardinalDirection orientation) {
		return null;

	}

}
