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
import java.util.logging.Logger;

import org.openflexo.diana.geom.DianaAbstractLine;
import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.DianaGeometricObject.CardinalDirection;
import org.openflexo.diana.geom.DianaGeometricObject.CardinalQuadrant;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

public class DianaQuarterPlane implements DianaArea {

	private static final Logger logger = Logger.getLogger(DianaQuarterPlane.class.getPackage().getName());

	public DianaHalfPlane halfPlane1;
	public DianaHalfPlane halfPlane2;

	public static DianaQuarterPlane makeDianaQuarterPlane(DianaPoint point, CardinalQuadrant quadrant) {
		if (quadrant == CardinalQuadrant.NORTH_EAST) {
			return new DianaQuarterPlane(DianaHalfPlane.makeDianaHalfPlane(point, CardinalDirection.NORTH), DianaHalfPlane.makeDianaHalfPlane(point,
					CardinalDirection.EAST));
		} else if (quadrant == CardinalQuadrant.NORTH_WEST) {
			return new DianaQuarterPlane(DianaHalfPlane.makeDianaHalfPlane(point, CardinalDirection.NORTH), DianaHalfPlane.makeDianaHalfPlane(point,
					CardinalDirection.WEST));
		} else if (quadrant == CardinalQuadrant.SOUTH_EAST) {
			return new DianaQuarterPlane(DianaHalfPlane.makeDianaHalfPlane(point, CardinalDirection.SOUTH), DianaHalfPlane.makeDianaHalfPlane(point,
					CardinalDirection.EAST));
		} else /* quadrant == CardinalQuadrant.SOUTH_WEST */{
			return new DianaQuarterPlane(DianaHalfPlane.makeDianaHalfPlane(point, CardinalDirection.SOUTH), DianaHalfPlane.makeDianaHalfPlane(point,
					CardinalDirection.WEST));
		}
	}

	public DianaQuarterPlane(DianaHalfPlane anHalfPlane1, DianaHalfPlane anHalfPlane2) {
		super();
		halfPlane1 = anHalfPlane1;
		halfPlane2 = anHalfPlane2;
		if (halfPlane1.line.isParallelTo(halfPlane2.line)) {
			throw new IllegalArgumentException("lines are parallel");
		}
	}

	public static void main(String[] args) {
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		return halfPlane1.containsPoint(p) && halfPlane2.containsPoint(p);
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
	public DianaQuarterPlane clone() {
		try {
			return (DianaQuarterPlane) super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	@Override
	public DianaQuarterPlane transform(AffineTransform t) {
		return new DianaQuarterPlane(halfPlane1.transform(t), halfPlane2.transform(t));
	}

	@Override
	public String toString() {
		return "DianaQuarterPlane: " + halfPlane1.toString() + " " + halfPlane2.toString();
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		// TODO
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		if (!(containsPoint(l.getP1()) && containsPoint(l.getP2()))) {
			return false;
		}

		if (l instanceof DianaHalfLine) {
			// TODO
			logger.warning("Not implemented yet");
			return false;
		}
		if (l instanceof DianaSegment) {
			return true;
		}
		return false;
	}

	@Override
	public boolean containsArea(DianaArea a) {
		if (a instanceof DianaPoint) {
			return containsPoint((DianaPoint) a);
		}
		if (a instanceof DianaLine) {
			return containsLine((DianaLine) a);
		}
		if (a instanceof DianaShape) {
			return DianaShape.AreaComputation.isShapeContainedInArea((DianaShape<?>) a, this);
		}
		return false;
	}

	@Override
	public DianaArea exclusiveOr(DianaArea area) {
		return new DianaExclusiveOrArea(this, area);
	}

	@Override
	public DianaArea intersect(DianaArea area) {
		if (area.containsArea(this)) {
			return this.clone();
		}
		if (containsArea(area)) {
			return area.clone();
		}

		DianaIntersectionArea returned = new DianaIntersectionArea(this, area);
		if (returned.isDevelopable()) {
			return returned.makeDevelopped();
		} else {
			return returned;
		}
	}

	@Override
	public DianaArea substract(DianaArea area, boolean isStrict) {
		return new DianaSubstractionArea(this, area, isStrict);
	}

	@Override
	public DianaArea union(DianaArea area) {
		if (containsArea(area)) {
			return clone();
		}
		if (area.containsArea(this)) {
			return area.clone();
		}

		return new DianaUnionArea(this, area);
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		}
		return DianaPoint.getNearestPoint(aPoint, halfPlane1.line.getProjection(aPoint), halfPlane2.line.getProjection(aPoint));
	}

	@Override
	public DianaArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		return this;
	}

	@Override
	public DianaArea getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
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
		DianaHalfLine hl = DianaHalfLine.makeHalfLine(from, orientation);
		DianaArea intersect = intersect(hl);
		return intersect.nearestPointFrom(from, orientation);
	}

}
