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
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

public class DianaHalfPlane implements DianaArea {

	private static final Logger logger = Logger.getLogger(DianaHalfPlane.class.getPackage().getName());

	public DianaLine line;
	public DianaPoint testPoint; // located on the "good" side

	public DianaHalfPlane() {
		super();
	}

	public DianaHalfPlane(DianaLine line, DianaPoint testPoint) {
		super();
		this.line = line;
		this.testPoint = testPoint;
	}

	public DianaHalfPlane(DianaAbstractLine<?> line, DianaPoint testPoint) {
		super();
		this.line = new DianaLine(line);
		this.testPoint = testPoint;
	}

	public static DianaHalfPlane makeDianaHalfPlane(DianaPoint point, CardinalDirection orientation) {
		DianaLine line;
		DianaPoint testPoint;

		if (orientation == CardinalDirection.NORTH) {
			line = DianaLine.makeHorizontalLine(point);
			testPoint = new DianaPoint(point);
			testPoint.y -= 1;
		} else if (orientation == CardinalDirection.EAST) {
			line = DianaLine.makeVerticalLine(point);
			testPoint = new DianaPoint(point);
			testPoint.x += 1;
		} else if (orientation == CardinalDirection.SOUTH) {
			line = DianaLine.makeHorizontalLine(point);
			testPoint = new DianaPoint(point);
			testPoint.y += 1;
		} else /* orientation == CardinalDirection.WEST */{
			line = DianaLine.makeVerticalLine(point);
			testPoint = new DianaPoint(point);
			testPoint.x -= 1;
		}

		return new DianaHalfPlane(line, testPoint);
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		if (line.contains(p)) {
			return true;
		}
		if (line.getPlaneLocation(testPoint) == line.getPlaneLocation(p)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		if (!(containsPoint(l.getP1()) && containsPoint(l.getP2()))) {
			return false;
		}

		if (l instanceof DianaHalfLine) {
			if (l.isParallelTo(line)) {
				return true;
			}
			return !l.containsPoint(line.getLineIntersection(l)) || line.containsPoint(((DianaHalfLine) l).getLimit());
		} else if (l instanceof DianaSegment) {
			return true;
		} else if (l instanceof DianaLine) {
			return l.isParallelTo(line);
		}
		logger.warning("Unexpected: " + l);
		return false;
	}

	public boolean containsHalfBand(DianaHalfBand hb) {
		return containsLine(hb.halfLine1) && containsLine(hb.halfLine2);
	}

	public boolean containsBand(DianaBand band) {
		return containsLine(band.line1) && containsLine(band.line2);
	}

	@Override
	public boolean containsArea(DianaArea a) {
		if (a instanceof DianaPoint) {
			return containsPoint((DianaPoint) a);
		}
		if (a instanceof DianaAbstractLine) {
			return containsLine((DianaAbstractLine<?>) a);
		}
		if (a instanceof DianaHalfBand) {
			return containsHalfBand((DianaHalfBand) a);
		}
		if (a instanceof DianaBand) {
			return containsBand((DianaBand) a);
		}
		if (a instanceof DianaShape) {
			return DianaShape.AreaComputation.isShapeContainedInArea((DianaShape<?>) a, this);
		}
		if (a instanceof DianaHalfPlane) {
			return containsHalfPlane((DianaHalfPlane) a);
		}
		return false;
	}

	public DianaHalfLine getHalfLine() {
		DianaPoint p = line.getProjection(testPoint);
		return new DianaHalfLine(p, testPoint);
	}

	private boolean containsHalfPlane(DianaHalfPlane hp) {
		if (line.overlap(hp.line)) {
			if (containsPoint(hp.testPoint)) {
				return true;
			} else {
				return false; // Only line is common
			}
		} else if (line.isParallelTo(hp.line)) {
			if (containsLine(hp.line) && !hp.containsLine(line)) {
				return true;
			}
		}
		return false;
	}

	private DianaArea computeLineIntersection(DianaAbstractLine<?> aLine) {
		if (aLine instanceof DianaLine) {
			if (aLine.equals(line)) {
				return aLine.clone();
			}
			if (aLine.isParallelTo(line)) {
				if (containsPoint(aLine.getP1())) {
					return aLine.clone();
				} else {
					return new DianaEmptyArea();
				}
			} else {
				DianaPoint limit = line.getLineIntersection(aLine);
				if (limit.equals(aLine.getP1())) {
					if (containsPoint(aLine.getP2())) {
						return new DianaHalfLine(limit, aLine.getP2());
					} else {
						return new DianaHalfLine(limit, DianaAbstractLine.getOppositePoint(aLine.getP2(), limit));
					}
				} else if (containsPoint(aLine.getP1())) {
					return new DianaHalfLine(limit, aLine.getP1());
				} else {
					return new DianaHalfLine(limit, DianaAbstractLine.getOppositePoint(aLine.getP1(), limit));
				}
			}
		} else if (aLine instanceof DianaHalfLine) {
			DianaHalfLine hl = (DianaHalfLine) aLine;
			if (hl.overlap(line)) {
				return hl.clone();
			}
			if (hl.isParallelTo(line)) {
				if (containsPoint(hl.getP1())) {
					return hl.clone();
				} else {
					return new DianaEmptyArea();
				}
			} else {
				DianaPoint intersect = hl.getLineIntersection(line);
				DianaPoint limit = hl.getLimit();
				if (intersect.equals(limit)) {
					return intersect.clone();
				}
				DianaPoint opposite = DianaAbstractLine.getOppositePoint(limit, intersect);
				if (containsPoint(limit)) {
					if (hl.contains(opposite)) {
						return new DianaSegment(limit, intersect);
					} else {
						return hl.clone();
					}
				} else {
					if (hl.contains(opposite)) {
						return new DianaHalfLine(intersect, opposite);
					} else {
						return new DianaEmptyArea();
					}
				}
			}
		} else if (aLine instanceof DianaSegment) {
			DianaSegment segment = (DianaSegment) aLine;
			if (segment.overlap(line)) {
				return segment.clone();
			}
			if (segment.isParallelTo(line)) {
				if (containsPoint(segment.getP1())) {
					return segment.clone();
				} else {
					return new DianaEmptyArea();
				}
			} else {
				if (containsPoint(segment.getP1())) {
					if (containsPoint(segment.getP2())) {
						return segment.clone();
					} else {
						DianaPoint p1 = segment.getP1();
						DianaPoint p2 = segment.getLineIntersection(line);
						if (p1.equals(p2)) {
							return p1.clone();
						} else {
							return new DianaSegment(p1, p2);
						}
					}
				} else {
					if (containsPoint(segment.getP2())) {
						DianaPoint p1 = segment.getP2();
						DianaPoint p2 = segment.getLineIntersection(line);
						if (p1.equals(p2)) {
							return p1.clone();
						} else {
							return new DianaSegment(p1, p2);
						}
					} else {
						return new DianaEmptyArea();
					}
				}
			}
		} else {
			logger.warning("Unexpected: " + line);
			return null;
		}
	}

	private DianaArea computeHalfPlaneIntersection(DianaHalfPlane hp) {
		if (line.overlap(hp.line)) {
			if (containsPoint(hp.testPoint)) {
				return clone(); // Same half planes
			} else {
				return line.clone(); // Only line is common
			}
		} else if (line.isParallelTo(hp.line)) {
			if (hp.containsLine(line)) {
				if (containsLine(hp.line)) {
					return new DianaBand(line, hp.line);
				} else {
					return clone();
				}
			} else {
				if (containsLine(hp.line)) {
					return hp.clone();
				} else {
					return new DianaEmptyArea();
				}
			}
		} else {
			// Don't try to formalize it
			return DianaIntersectionArea.makeIntersection(this, hp);
		}
	}

	private DianaArea computeShapeIntersection(DianaShape<?> shape) {
		DianaArea workingArea = intersect(shape.getBoundingBox());
		if (workingArea instanceof DianaEmptyArea) {
			return workingArea; // Empty area
		}
		if (workingArea instanceof DianaShape || workingArea instanceof DianaAbstractLine || workingArea instanceof DianaPoint) {
			return workingArea.intersect(shape);
		} else {
			logger.warning("Unexpected: " + workingArea);
			return new DianaEmptyArea();
		}
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
		if (area instanceof DianaAbstractLine) {
			return computeLineIntersection((DianaAbstractLine<?>) area);
		}
		if (area instanceof DianaRectangle) {
			return area.intersect(this);
		}
		if (area instanceof DianaBand) {
			return area.intersect(this);
		}
		if (area instanceof DianaHalfBand) {
			return area.intersect(this);
		}
		if (area instanceof DianaHalfPlane) {
			return computeHalfPlaneIntersection((DianaHalfPlane) area);
		}
		if (area instanceof DianaShape) {
			return computeShapeIntersection((DianaShape<?>) area);
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
	public DianaHalfPlane clone() {
		try {
			return (DianaHalfPlane) super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	@Override
	public DianaHalfPlane transform(AffineTransform t) {
		return new DianaHalfPlane(line.transform(t), testPoint.transform(t));
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		DianaRectangle bounds = g.getNodeNormalizedBounds();

		g.useDefaultForegroundStyle();
		bounds.intersect(line).paint(g);

		g.useDefaultBackgroundStyle();
		bounds.intersect(this).paint(g);
	}

	@Override
	public String toString() {
		return "DianaHalfPlane: " + line.toString() + " testPoint=" + testPoint;
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		} else {
			return line.getProjection(aPoint);
		}
	}

	@Override
	public int hashCode() {//TODO AB: to be checked
		return (line.hashCode() + testPoint.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaHalfPlane) {
			DianaHalfPlane hp = (DianaHalfPlane) obj;
			return line.overlap(hp.line) && containsPoint(hp.testPoint);
		}
		return super.equals(obj);
	}

	@Override
	public DianaArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		return line.getOrthogonalPerspectiveArea(orientation);
	}

	@Override
	public DianaHalfPlane getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		return clone();
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
