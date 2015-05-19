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

package org.openflexo.fge.geom.area;

import java.awt.geom.AffineTransform;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.ParallelLinesException;
import org.openflexo.fge.graphics.AbstractFGEGraphics;

@SuppressWarnings("serial")
public class FGEHalfLine extends FGEAbstractLine<FGEHalfLine> {

	private static final Logger logger = Logger.getLogger(FGEHalfLine.class.getPackage().getName());

	public FGEHalfLine(double limitX, double limitY, double px, double py) {
		super(limitX, limitY, px, py);
	}

	// oppositePoint if the infinite direction
	public FGEHalfLine(FGEPoint limit, FGEPoint oppositePoint) {
		super(limit, oppositePoint);
	}

	public static FGEHalfLine makeHalfLine(FGEPoint limit, SimplifiedCardinalDirection orientation) {
		FGEHalfLine hl = null;
		switch (orientation) {
		case NORTH:
			hl = new FGEHalfLine(limit, limit.transform(AffineTransform.getTranslateInstance(0, -1)));
			break;
		case SOUTH:
			hl = new FGEHalfLine(limit, limit.transform(AffineTransform.getTranslateInstance(0, 1)));
			break;
		case EAST:
			hl = new FGEHalfLine(limit, limit.transform(AffineTransform.getTranslateInstance(1, 0)));
			break;
		case WEST:
			hl = new FGEHalfLine(limit, limit.transform(AffineTransform.getTranslateInstance(-1, 0)));
			break;
		default:
			break;
		}
		return hl;
	}

	public FGEHalfLine() {
		super();
	}

	public FGEPoint getLimit() {
		return getP1();
	}

	public void setLimit(FGEPoint aPoint) {
		setP1(aPoint);
	}

	@Override
	public boolean containsLine(FGEAbstractLine<?> l) {
		if (!overlap(l)) {
			return false;
		}

		if (!(containsPoint(l.getP1()) && containsPoint(l.getP2()))) {
			return false;
		}

		if (l instanceof FGEHalfLine) {
			return getHalfPlane().intersect(((FGEHalfLine) l).getHalfPlane()) instanceof FGEHalfPlane;
		}
		if (l instanceof FGESegment) {
			return true;
		}

		// If this is a line this is false
		return false;
	}

	@Override
	public boolean contains(FGEPoint p) {
		// First see if located on line
		if (!_containsPointIgnoreBounds(p)) {
			return false;
		}

		// Now check bounds
		if (getB() != 0) {
			FGEPoint pp1 = getP1();
			FGEPoint pp2 = getP2();
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
			FGEPoint pp1 = getP1();
			FGEPoint pp2 = getP2();
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

	public FGEPoint getOpposite() {
		return getP2();
	}

	public void setOpposite(FGEPoint aPoint) {
		setP2(aPoint);
	}

	public FGEHalfPlane getHalfPlane() {
		return new FGEHalfPlane(getOrthogonalLine(getLimit()), getOpposite());
	}

	@Override
	public int hashCode() {//TODO AB to be checked
		return (getLimit().hashCode() + getOpposite().hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGEHalfLine) {
			FGEHalfLine hl = (FGEHalfLine) obj;
			if (!overlap(hl)) {
				return false;
			}
			return getLimit().equals(hl.getLimit()) && hl.getNearestPointOnHalfLine(getOpposite()).equals(getOpposite());
		}
		return false;
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint p) {
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
	public FGEPoint getNearestPointOnHalfLine(FGEPoint p) {
		FGEPoint projection = getProjection(p);
		if (contains(projection)) {
			return projection;
		} else {
			return getLimit();
		}
	}

	@Override
	public FGEHalfLine transform(AffineTransform t) {
		return new FGEHalfLine(getLimit().transform(t), getOpposite().transform(t));
	}

	@Override
	public String toString() {
		return "FGEHalfLine: [" + getLimit() + "," + getOpposite() + ")";
	}

	protected FGEPoint getOppositePointProjection(AbstractFGEGraphics g) {
		FGERectangle bounds = g.getNodeNormalizedBounds();
		FGEArea area = bounds.intersect(new FGELine(getP1(), getP2()));
		if (area instanceof FGESegment) {
			// We must now find which point to choose
			FGEPoint pp1 = ((FGESegment) area).getP1();
			FGEPoint pp2 = ((FGESegment) area).getP2();
			if (containsPoint(pp1) && !containsPoint(pp2)) {
				return pp1;
			}
			if (containsPoint(pp2) && !containsPoint(pp1)) {
				return pp2;
			}
			logger.warning("Unexpected situation here...");
			return getOpposite();
			/*
			FGEPoint pp1 = ((FGESegment)area).getP1();
			FGEPoint pp2 = ((FGESegment)area).getP2();
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
	public void paint(AbstractFGEGraphics g) {
		g.useDefaultForegroundStyle();
		FGEPoint limit = getLimit();
		FGEPoint p2 = getOppositePointProjection(g);
		if (limit != null && p2 != null) {
			new FGESegment(limit, p2).paint(g);
		}

		/*
		FGERectangle bounds = g.getGraphicalRepresentation().getLogicalBounds();
		FGEArea area = bounds.intersect(new FGELine(getP1(),getP2()));

		FGEPoint pp1 = ((FGESegment)area).getP1();
		FGEPoint pp2 = ((FGESegment)area).getP2();

		pp1.paint(g);
		pp2.paint(g);
		(new FGESegment(getP1(),getP2())).paint(g);*/
	}

	@Override
	protected FGEArea computeLineIntersection(FGEAbstractLine<?> line) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("computeIntersection() between " + this + "\n and " + line + " overlap=" + overlap(line));
		}
		if (overlap(line)) {
			if (line instanceof FGEHalfLine) {
				return _compute_hl_hl_Intersection(this, (FGEHalfLine) line);
			} else if (line instanceof FGESegment) {
				return _compute_hl_segment_Intersection(this, (FGESegment) line);
			} else {
				return clone();
			}
		} else if (isParallelTo(line)) {
			return new FGEEmptyArea();
		} else {
			FGEPoint returned;
			try {
				returned = getLineIntersection(line);
				if (containsPoint(returned) && line.containsPoint(returned)) {
					return returned;
				}
			} catch (ParallelLinesException e) {
				// cannot happen
			}
			return new FGEEmptyArea();
		}
	}

	private static FGEArea _compute_hl_hl_Intersection(FGEHalfLine hl1, FGEHalfLine hl2) {
		FGEHalfPlane hp1 = hl1.getHalfPlane();
		FGEHalfPlane hp2 = hl2.getHalfPlane();

		FGEArea intersect = hp1.intersect(hp2);

		if (intersect instanceof FGEEmptyArea) {
			return new FGEEmptyArea();
		} else if (intersect instanceof FGEBand) {
			return new FGESegment(hl1.getLimit(), hl2.getLimit());
		}

		else if (intersect.equals(hp1)) {
			return hl1.clone();
		} else if (intersect.equals(hp2)) {
			return hl2.clone();
		} else if (intersect instanceof FGELine) {
			// The 2 halfline are adjacent by their limit
			if (hl1.getLimit().equals(hl2.getLimit())) {
				return hl1;
			} else {
				logger.warning("Unexpected line intersection: " + intersect + " while computing intersection of " + hl1 + " and " + hl2);
				return new FGEEmptyArea();
			}
		} else {
			logger.warning("Unexpected intersection: " + intersect + " while computing intersection of " + hl1 + " and " + hl2);
			return new FGEEmptyArea();
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
	public FGEHalfLine clone() {
		return (FGEHalfLine) super.clone();
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			if (isVertical()) {
				return clone();
			} else {
				return new FGEHalfPlane(FGELine.makeVerticalLine(getLimit()), getOpposite());
			}
		} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			if (isVertical()) {
				return clone();
			} else {
				return new FGEHalfPlane(FGELine.makeVerticalLine(getLimit()), getOpposite());
			}
		} else if (orientation == SimplifiedCardinalDirection.EAST) {
			if (isHorizontal()) {
				return clone();
			} else {
				return new FGEHalfPlane(FGELine.makeHorizontalLine(getLimit()), getOpposite());
			}
		} else if (orientation == SimplifiedCardinalDirection.WEST) {
			if (isHorizontal()) {
				return clone();
			} else {
				return new FGEHalfPlane(FGELine.makeHorizontalLine(getLimit()), getOpposite());
			}
		}
		return null;
	}

	@Override
	public FGEHalfLine getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
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
	public final FGERectangle getEmbeddingBounds() {
		return null;
	}
}
