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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.diana.geom.DianaAbstractLine;
import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.ParallelLinesException;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

@SuppressWarnings("serial")
public class DianaHalfLine extends DianaAbstractLine<DianaHalfLine> {

	private static final Logger logger = Logger.getLogger(DianaHalfLine.class.getPackage().getName());

	public DianaHalfLine(double limitX, double limitY, double px, double py) {
		super(limitX, limitY, px, py);
	}

	// oppositePoint if the infinite direction
	public DianaHalfLine(DianaPoint limit, DianaPoint oppositePoint) {
		super(limit, oppositePoint);
	}

	public static DianaHalfLine makeHalfLine(DianaPoint limit, SimplifiedCardinalDirection orientation) {
		DianaHalfLine hl = null;
		switch (orientation) {
		case NORTH:
			hl = new DianaHalfLine(limit, limit.transform(AffineTransform.getTranslateInstance(0, -1)));
			break;
		case SOUTH:
			hl = new DianaHalfLine(limit, limit.transform(AffineTransform.getTranslateInstance(0, 1)));
			break;
		case EAST:
			hl = new DianaHalfLine(limit, limit.transform(AffineTransform.getTranslateInstance(1, 0)));
			break;
		case WEST:
			hl = new DianaHalfLine(limit, limit.transform(AffineTransform.getTranslateInstance(-1, 0)));
			break;
		default:
			break;
		}
		return hl;
	}

	public DianaHalfLine() {
		super();
	}

	public DianaPoint getLimit() {
		return getP1();
	}

	public void setLimit(DianaPoint aPoint) {
		setP1(aPoint);
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
			return getHalfPlane().intersect(((DianaHalfLine) l).getHalfPlane()) instanceof DianaHalfPlane;
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
			if (p.x >= pp1.x) {
				if (p.x > pp2.x) {
					return pp2.equals(getOpposite());
				} else {
					return true;
				}
			} else {
				return pp1.equals(getOpposite());
			}
		} else {
			DianaPoint pp1 = getP1();
			DianaPoint pp2 = getP2();
			if (pp1.y > pp2.y) {
				pp1 = getP2();
				pp2 = getP1();
			}
			if (p.y >= pp1.y) {
				if (p.y > pp2.y) {
					return pp2.equals(getOpposite());
				} else {
					return true;
				}
			} else {
				return pp1.equals(getOpposite());
			}
		}
	}

	public DianaPoint getOpposite() {
		return getP2();
	}

	public void setOpposite(DianaPoint aPoint) {
		setP2(aPoint);
	}

	public DianaHalfPlane getHalfPlane() {
		return new DianaHalfPlane(getOrthogonalLine(getLimit()), getOpposite());
	}

	@Override
	public int hashCode() {//TODO AB to be checked
		return (getLimit().hashCode() + getOpposite().hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaHalfLine) {
			DianaHalfLine hl = (DianaHalfLine) obj;
			if (!overlap(hl)) {
				return false;
			}
			return getLimit().equals(hl.getLimit()) && hl.getNearestPointOnHalfLine(getOpposite()).equals(getOpposite());
		}
		return false;
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint p) {
		return getNearestPointOnHalfLine(p);
	}

	/**
	 * Return nearest point on half line
	 * 
	 * If orthogonal projection of supplied point on segment is inside the halfline, return this projection. Otherwise, return half line
	 * limit
	 * 
	 * @param p
	 * @return
	 */
	public DianaPoint getNearestPointOnHalfLine(DianaPoint p) {
		DianaPoint projection = getProjection(p);
		if (contains(projection)) {
			return projection;
		} else {
			return getLimit();
		}
	}

	@Override
	public DianaHalfLine transform(AffineTransform t) {
		return new DianaHalfLine(getLimit().transform(t), getOpposite().transform(t));
	}

	@Override
	public String toString() {
		return "DianaHalfLine: [" + getLimit() + "," + getOpposite() + ")";
	}

	protected DianaPoint getOppositePointProjection(AbstractDianaGraphics g) {
		DianaRectangle bounds = g.getNodeNormalizedBounds();
		DianaArea area = bounds.intersect(new DianaLine(getP1(), getP2()));
		if (area instanceof DianaSegment) {
			// We must now find which point to choose
			DianaPoint pp1 = ((DianaSegment) area).getP1();
			DianaPoint pp2 = ((DianaSegment) area).getP2();
			if (containsPoint(pp1) && !containsPoint(pp2)) {
				return pp1;
			}
			if (containsPoint(pp2) && !containsPoint(pp1)) {
				return pp2;
			}
			logger.warning("Unexpected situation here...");
			return getOpposite();
			/*
			DianaPoint pp1 = ((DianaSegment)area).getP1();
			DianaPoint pp2 = ((DianaSegment)area).getP2();
			if (getP1().x < getP2().x) {
				if (pp1.x<pp2.x) return pp2;
				else return pp1;
			}
			else if (getP1().x > getP2().x) {
				if (pp1.x>pp2.x) return pp2;
				else return pp1;
			}
			else {
				if (getP1().y < getP2().y) {
					if (pp1.y<pp2.y) return pp2;
					else return pp1;
				}
				else if (getP1().y > getP2().y) {
					if (pp1.y>pp2.y) return pp2;
					else return pp1;
				}
				else return pp1;			
			}*/
		}
		logger.warning("Unexpected situation here...");
		return null;
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		g.useDefaultForegroundStyle();
		DianaPoint limit = getLimit();
		DianaPoint p2 = getOppositePointProjection(g);
		if (limit != null && p2 != null) {
			new DianaSegment(limit, p2).paint(g);
		}

		/*
		DianaRectangle bounds = g.getGraphicalRepresentation().getLogicalBounds();
		DianaArea area = bounds.intersect(new DianaLine(getP1(),getP2()));

		DianaPoint pp1 = ((DianaSegment)area).getP1();
		DianaPoint pp2 = ((DianaSegment)area).getP2();

		pp1.paint(g);
		pp2.paint(g);
		(new DianaSegment(getP1(),getP2())).paint(g);*/
	}

	@Override
	protected DianaArea computeLineIntersection(DianaAbstractLine<?> line) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("computeIntersection() between " + this + "\n and " + line + " overlap=" + overlap(line));
		}
		if (overlap(line)) {
			if (line instanceof DianaHalfLine) {
				return _compute_hl_hl_Intersection(this, (DianaHalfLine) line);
			} else if (line instanceof DianaSegment) {
				return _compute_hl_segment_Intersection(this, (DianaSegment) line);
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

	private static DianaArea _compute_hl_hl_Intersection(DianaHalfLine hl1, DianaHalfLine hl2) {
		DianaHalfPlane hp1 = hl1.getHalfPlane();
		DianaHalfPlane hp2 = hl2.getHalfPlane();

		DianaArea intersect = hp1.intersect(hp2);

		if (intersect instanceof DianaEmptyArea) {
			return new DianaEmptyArea();
		} else if (intersect instanceof DianaBand) {
			return new DianaSegment(hl1.getLimit(), hl2.getLimit());
		}

		else if (intersect.equals(hp1)) {
			return hl1.clone();
		} else if (intersect.equals(hp2)) {
			return hl2.clone();
		} else if (intersect instanceof DianaLine) {
			// The 2 halfline are adjacent by their limit
			if (hl1.getLimit().equals(hl2.getLimit())) {
				return hl1;
			} else {
				logger.warning("Unexpected line intersection: " + intersect + " while computing intersection of " + hl1 + " and " + hl2);
				return new DianaEmptyArea();
			}
		} else {
			logger.warning("Unexpected intersection: " + intersect + " while computing intersection of " + hl1 + " and " + hl2);
			return new DianaEmptyArea();
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
	public DianaHalfLine clone() {
		return (DianaHalfLine) super.clone();
	}

	@Override
	public DianaArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			if (isVertical()) {
				return clone();
			} else {
				return new DianaHalfPlane(DianaLine.makeVerticalLine(getLimit()), getOpposite());
			}
		} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			if (isVertical()) {
				return clone();
			} else {
				return new DianaHalfPlane(DianaLine.makeVerticalLine(getLimit()), getOpposite());
			}
		} else if (orientation == SimplifiedCardinalDirection.EAST) {
			if (isHorizontal()) {
				return clone();
			} else {
				return new DianaHalfPlane(DianaLine.makeHorizontalLine(getLimit()), getOpposite());
			}
		} else if (orientation == SimplifiedCardinalDirection.WEST) {
			if (isHorizontal()) {
				return clone();
			} else {
				return new DianaHalfPlane(DianaLine.makeHorizontalLine(getLimit()), getOpposite());
			}
		}
		return null;
	}

	@Override
	public DianaHalfLine getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
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
}
