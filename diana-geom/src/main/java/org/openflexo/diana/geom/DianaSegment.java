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

package org.openflexo.diana.geom;

import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaHalfBand;
import org.openflexo.diana.geom.area.DianaHalfLine;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

@SuppressWarnings("serial")
public class DianaSegment extends DianaAbstractLine<DianaSegment> implements DianaGeneralShape.GeneralShapePathElement<DianaSegment> {

	private static final Logger logger = Logger.getLogger(DianaSegment.class.getPackage().getName());

	public DianaSegment(double X1, double Y1, double X2, double Y2) {
		super(X1, Y1, X2, Y2);
	}

	public DianaSegment(DianaPoint p1, DianaPoint p2) {
		super(p1, p2);
	}

	public DianaSegment() {
		super();
	}

	public DianaPoint getMiddle() {
		return new DianaPoint((getP1().x + getP2().x) / 2, (getP1().y + getP2().y) / 2);
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		if (!overlap(l)) {
			return false;
		}

		if (!(containsPoint(l.getP1()) && containsPoint(l.getP2()))) {
			return false;
		}

		if (l instanceof DianaHalfLine) {
			return false;
		}
		if (l instanceof DianaSegment) {
			return true;
		}

		// If this is a line this is false
		return false;
	}

	@Override
	public boolean contains(DianaPoint p) {
		// First see if located on line
		if (!_containsPointIgnoreBounds(p)) {
			return false;
		}

		// Now check bounds
		if (getB() != 0) {
			DianaPoint pp1 = getP1();
			DianaPoint pp2 = getP2();
			if (pp1.x > pp2.x) {
				pp1 = getP2();
				pp2 = getP1();
			}
			if (p.x >= pp1.x - EPSILON) {
				if (p.x > pp2.x + EPSILON) {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		} else {
			DianaPoint pp1 = getP1();
			DianaPoint pp2 = getP2();
			if (pp1.y > pp2.y) {
				pp1 = getP2();
				pp2 = getP1();
			}
			if (p.y >= pp1.y - EPSILON) {
				if (p.y > pp2.y + EPSILON) {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}
	}

	public boolean projectionIntersectsInsideSegment(DianaPoint p) {
		DianaPoint projection = getProjection(p);
		return contains(projection);
	}

	/**
	 * Return flag indicating if intersection of this segment and supplied line occurs somewhere INSIDE this segment
	 * 
	 * @param line
	 * @return
	 */
	public boolean intersectsInsideSegment(DianaAbstractLine<?> line) {
		DianaPoint intersection;
		try {
			intersection = getLineIntersection(line);
		} catch (ParallelLinesException e) {
			return false;
		}
		return contains(intersection) && line.contains(intersection);
	}

	/**
	 * Return flag indicating if intersection of this segment and supplied line occurs somewhere INSIDE this segment. If insideOnly set to
	 * true and intersection is one of extremities return false
	 * 
	 * @param line
	 * @return
	 */
	public boolean intersectsInsideSegment(DianaAbstractLine<?> line, boolean insideOnly) {
		DianaPoint intersection;
		try {
			intersection = getLineIntersection(line);
		} catch (ParallelLinesException e) {
			return false;
		}
		if (!(contains(intersection) && line.contains(intersection))) {
			return false;
		}
		if (insideOnly) {
			if (intersection.equals(getP1()) || intersection.equals(getP2()) || line instanceof DianaSegment
					&& (intersection.equals(line.getP1()) || intersection.equals(line.getP2()))) {
				return false;
			}
		}
		return true;
	}

	public static DianaPoint getClosestPointOnSegment(DianaPoint p, DianaSegment segment) {
		return segment.getNearestPointOnSegment(p);
	}

	public static boolean intersectsInsideSegment(DianaPoint p, DianaSegment segment) {
		return segment.projectionIntersectsInsideSegment(p);
	}

	/**
	 * Return flag indicating if intersection of supplied segment and supplied line occurs somewhere INSIDE supplied segment
	 * 
	 * @param line
	 * @return
	 */
	public static boolean intersectsInsideSegment(DianaSegment segment, DianaAbstractLine<?> line) {
		return segment.intersectsInsideSegment(line);
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint p) {
		return getNearestPointOnSegment(p);
	}

	/**
	 * Return nearest point on segment
	 * 
	 * If orthogonal projection of supplied point on segment is inside the segment, return this projection. Otherwise, return adequate
	 * segment extremity
	 * 
	 * @param p
	 * @return
	 */
	public DianaPoint getNearestPointOnSegment(DianaPoint p) {
		DianaPoint projection = getProjection(p);
		if (getB() != 0) {
			DianaPoint pp1 = getP1();
			DianaPoint pp2 = getP2();
			if (pp1.x > pp2.x) {
				pp1 = getP2();
				pp2 = getP1();
			}
			if (projection.x >= pp1.x) {
				if (projection.x >= pp2.x) {
					return pp2;
				} else {
					return projection;
				}
			} else {
				return pp1;
			}
		} else {
			DianaPoint pp1 = getP1();
			DianaPoint pp2 = getP2();
			if (pp1.y > pp2.y) {
				pp1 = getP2();
				pp2 = getP1();
			}
			if (projection.y >= pp1.y) {
				if (projection.y >= pp2.y) {
					return pp2;
				} else {
					return projection;
				}
			} else {
				return pp1;
			}
		}
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
	public DianaSegment clone() {
		return (DianaSegment) super.clone();
	}

	@Override
	public DianaSegment transform(AffineTransform t) {
		return new DianaSegment(getP1().transform(t), getP2().transform(t));
	}

	@Override
	public int hashCode() {
		return getP1().hashCode() + getP2().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaSegment) {
			DianaSegment s = (DianaSegment) obj;
			if (!overlap(s)) {
				return false;
			}
			return (getP1().equals(s.getP1()) || getP1().equals(s.getP2())) && (getP2().equals(s.getP1()) || getP2().equals(s.getP2()));
		}
		return false;
	}

	@Override
	public String toString() {
		return "DianaSegment: [" + getP1() + "," + getP2() + "]";
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		g.useDefaultForegroundStyle();
		g.drawLine(getP1(), getP2());
	}

	public double getLength() {
		return Math.sqrt(getSqLength());
	}

	public double getSqLength() {
		return (getP1().x - getP2().x) * (getP1().x - getP2().x) + (getP1().y - getP2().y) * (getP1().y - getP2().y);
	}

	public static double getLength(DianaPoint p1, DianaPoint p2) {
		return new DianaSegment(p1, p2).getLength();
	}

	@Override
	protected DianaArea computeLineIntersection(DianaAbstractLine<?> line) {
		// logger.info("computeIntersection() between "+this+"\n and "+line+" overlap="+overlap(line));
		if (overlap(line)) {
			if (line instanceof DianaHalfLine) {
				return _compute_hl_segment_Intersection((DianaHalfLine) line, this);
			} else if (line instanceof DianaSegment) {
				return _compute_segment_segment_Intersection(this, (DianaSegment) line);
			} else {
				return clone();
			}
		} else if (isParallelTo(line)) {
			return new DianaEmptyArea();
		} else {
			DianaPoint returned;
			try {
				returned = getLineIntersection(line);
				if (containsPoint(returned) && line.containsPoint(returned)) {
					return returned;
				}
			} catch (ParallelLinesException e) {
				// cannot happen
			}
			return new DianaEmptyArea();
		}
	}

	private static DianaArea _compute_segment_segment_Intersection(DianaSegment s1, DianaSegment s2) {
		if (s1.containsPoint(s2.getP1())) {
			if (s1.containsPoint(s2.getP2())) {
				return s2.clone();
			} else {
				if (s2.containsPoint(s1.getP1())) {
					if (s1.getP1().equals(s2.getP1())) {
						return s1.getP1().clone();
					}
					return new DianaSegment(s1.getP1(), s2.getP1());
				} else {
					if (s2.getP1().equals(s1.getP2())) {
						return s2.getP1().clone();
					}
					return new DianaSegment(s2.getP1(), s1.getP2());
				}
			}
		} else {
			if (s1.containsPoint(s2.getP2())) {
				if (s2.containsPoint(s1.getP1())) {
					if (s1.getP1().equals(s2.getP2())) {
						return s1.getP1().clone();
					}
					return new DianaSegment(s1.getP1(), s2.getP2());
				} else {
					if (s2.getP2().equals(s1.getP2())) {
						return s2.getP2().clone();
					}
					return new DianaSegment(s2.getP2(), s1.getP2());
				}
			} else {
				if (s2.containsPoint(s1.getP1()) && s2.containsPoint(s1.getP2())) {
					return s1.clone();
				} else {
					return new DianaEmptyArea();
				}
			}

		}
	}

	@Override
	public DianaArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		DianaHalfLine hl1 = null;
		DianaHalfLine hl2 = null;
		DianaPoint ps1 = getP1();
		DianaPoint ps2 = getP2();
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			hl1 = new DianaHalfLine(ps1, new DianaPoint(ps1.x, ps1.y - 1));
			hl2 = new DianaHalfLine(ps2, new DianaPoint(ps2.x, ps2.y - 1));
			if (Math.abs(ps1.x - ps2.x) < EPSILON) {
				return hl1;
			}
		} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			hl1 = new DianaHalfLine(ps1, new DianaPoint(ps1.x, ps1.y + 1));
			hl2 = new DianaHalfLine(ps2, new DianaPoint(ps2.x, ps2.y + 1));
			if (Math.abs(ps1.x - ps2.x) < EPSILON) {
				return hl1;
			}
		} else if (orientation == SimplifiedCardinalDirection.EAST) {
			hl1 = new DianaHalfLine(ps1, new DianaPoint(ps1.x + 1, ps1.y));
			hl2 = new DianaHalfLine(ps2, new DianaPoint(ps2.x + 1, ps2.y));
			if (Math.abs(ps1.y - ps2.y) < EPSILON) {
				return hl1;
			}
		} else if (orientation == SimplifiedCardinalDirection.WEST) {
			hl1 = new DianaHalfLine(ps1, new DianaPoint(ps1.x - 1, ps1.y));
			hl2 = new DianaHalfLine(ps2, new DianaPoint(ps2.x - 1, ps2.y));
			if (Math.abs(ps1.y - ps2.y) < EPSILON) {
				return hl1;
			}
		}
		// System.out.println("Segment: "+this+" orientation="+orientation);
		try {
			return new DianaHalfBand(hl1, hl2);
		} catch (IllegalArgumentException e) {
			logger.warning("Could not obtain resulting half-band, hl1=" + hl1 + " hl2=" + hl2 + " orientation=" + orientation + " ps1="
					+ ps1 + " ps2=" + ps2);
			return new DianaEmptyArea();
		}
	}

	@Override
	public DianaSegment getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		return clone();
	}

	public DianaPoint getScaledPoint(double scale) {
		DianaPoint p = new DianaPoint();
		p.x = getP1().x + (getP2().x - getP1().x) * scale;
		p.y = getP1().y + (getP2().y - getP1().y) * scale;
		return p;
	}

	public double getRelativeLocation(DianaPoint p) {
		DianaPoint proj = getNearestPointOnSegment(p);
		if (Math.abs(getP2().x - getP1().x) < EPSILON) {
			return (proj.y - getP1().y) / (getP2().y - getP1().y);
		} else {
			return (proj.x - getP1().x) / (getP2().x - getP1().x);
		}
	}

	public static DianaRectangle getBoundingBox(DianaSegment... segments) {
		DianaPolylin polylin = new DianaPolylin();
		for (DianaSegment s : segments) {
			polylin.addToPoints(s.getP1());
			polylin.addToPoints(s.getP2());
		}
		return polylin.getBoundingBox();
	}

	public SimplifiedCardinalDirection getOrientation() {
		double angle = getAngle();

		/**
		 * GPO: Angle will be between -PI/2 and 3PI/2 0-->WEST PI/2--> SOUTH -PI or PI-->EAST (Almost sure that -PI will never come but it
		 * does not cost too much so let's do it) -PI/2 or 3PI/2-->NORTH
		 */
		if (Math.abs(angle) < EPSILON) {
			return SimplifiedCardinalDirection.WEST;
		} else if (Math.abs(angle - Math.PI) < EPSILON || Math.abs(angle + Math.PI) < EPSILON) {
			return SimplifiedCardinalDirection.EAST;
		} else if (Math.abs(angle - Math.PI / 2) < EPSILON) {
			return SimplifiedCardinalDirection.SOUTH;
		} else if (Math.abs(angle - 3 * Math.PI / 2) < EPSILON || Math.abs(angle + Math.PI / 2) < EPSILON) {
			return SimplifiedCardinalDirection.NORTH;
		} else {
			return null;
		}
	}

	public SimplifiedCardinalDirection getApproximatedOrientation() {
		SimplifiedCardinalDirection returned = getOrientation();
		if (returned != null) {
			return returned;
		}

		return DianaPoint.getSimplifiedOrientation(getP1(), getP2());
	}

	@Override
	public final DianaArea union(DianaArea area) {
		if (containsArea(area)) {
			return clone();
		}
		if (area.containsArea(this)) {
			return area.clone();
		}

		if (area instanceof DianaPolylin) {
			return ((DianaPolylin) area).union(this);
		}
		if (area instanceof DianaSegment) {
			DianaSegment s = (DianaSegment) area;
			if (s.containsLine(this)) {
				return s;
			}
			if (containsLine(s)) {
				return clone();
			}
			if (overlap(s)) {
				if (containsPoint(s.getP1())) {
					// Doesn't contains P2, otherwise all contained, see above
					if (s.containsPoint(getP1())) {
						return new DianaSegment(getP2(), s.getP2());
					}
					if (s.containsPoint(getP2())) {
						return new DianaSegment(getP1(), s.getP2());
					}
				}
				if (containsPoint(s.getP2())) {
					// Doesn't contains P2, otherwise all contained, see above
					if (s.containsPoint(getP1())) {
						return new DianaSegment(getP2(), s.getP1());
					}
					if (s.containsPoint(getP2())) {
						return new DianaSegment(getP1(), s.getP1());
					}
				}
			}
			if (getP1().equals(s.getP2())) {
				return new DianaPolylin(getP2(), getP1(), s.getP1());
			}
			if (getP1().equals(s.getP1())) {
				return new DianaPolylin(getP2(), getP1(), s.getP2());
			}
			if (getP2().equals(s.getP2())) {
				return new DianaPolylin(getP1(), getP2(), s.getP1());
			}
			if (getP2().equals(s.getP1())) {
				return new DianaPolylin(getP1(), getP2(), s.getP2());
			}
			return super.union(area);
		}
		return super.union(area);
	}

	/**
	 * This area is finite, so always return true
	 */
	@Override
	public final boolean isFinite() {
		return true;
	}

	/**
	 * This area is finite, so always return null
	 */
	@Override
	public final DianaRectangle getEmbeddingBounds() {
		return new DianaRectangle(getP1(), getP2(), Filling.FILLED);
	}

}
