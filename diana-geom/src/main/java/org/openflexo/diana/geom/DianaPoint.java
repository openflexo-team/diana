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

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;

import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaExclusiveOrArea;
import org.openflexo.diana.geom.area.DianaHalfLine;
import org.openflexo.diana.geom.area.DianaUnionArea;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

@SuppressWarnings("serial")
public class DianaPoint extends Point2D.Double implements DianaGeometricObject<DianaPoint> {

	public static final DianaPoint ORIGIN_POINT = new DianaPoint() {
		@Override
		public void setX(double value) {
		};

		@Override
		public void setY(double value) {
		};
	};

	public static final DianaPoint NORMALIZED_CENTRAL_POINT = new DianaPoint(0.5, 0.5) {
		@Override
		public void setX(double value) {
		};

		@Override
		public void setY(double value) {
		};
	};

	public DianaPoint(double aX, double aY) {
		super(aX, aY);
	}

	public DianaPoint(DianaPoint p) {
		super(p.getX(), p.getY());
	}

	public DianaPoint(Point2D p) {
		super(p.getX(), p.getY());
	}

	public DianaPoint() {
		super(0, 0);
	}

	public void setX(double value) {
		x = value;
	}

	public void setY(double value) {
		y = value;
	}

	public static double distance(DianaPoint p1, DianaPoint p2) {
		return p1.distance(p2);
	}

	public static double distanceSq(DianaPoint p1, DianaPoint p2) {
		return p1.distanceSq(p2);
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
	public DianaPoint clone() {
		return (DianaPoint) super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaPoint) {
			DianaPoint p = (DianaPoint) obj;
			boolean result = (Math.abs(getX() - p.getX()) < EPSILON || java.lang.Double.isNaN(getX()) && java.lang.Double.isNaN(p.getX())
					|| getX() == p.getX())
					&& (Math.abs(getY() - p.getY()) < EPSILON || java.lang.Double.isNaN(getY()) && java.lang.Double.isNaN(p.getY())
							|| getY() == p.getY());
			return result;
		}
		return super.equals(obj);
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		return equals(p);
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		return false;
	}

	@Override
	public boolean containsArea(DianaArea a) {
		return a instanceof DianaPoint && containsPoint((DianaPoint) a);
	}

	@Override
	public DianaArea exclusiveOr(DianaArea area) {
		return new DianaExclusiveOrArea(this, area);
	}

	@Override
	public DianaArea intersect(DianaArea area) {
		if (area.containsPoint(this)) {
			return this.clone();
		}
		else {
			return new DianaEmptyArea();
		}
	}

	@Override
	public DianaArea substract(DianaArea area, boolean isStrict) {
		if (area.containsPoint(this)) {
			return new DianaEmptyArea();
		}
		else {
			return this.clone();
		}
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
	public DianaPoint transform(AffineTransform t) {
		DianaPoint returned = new DianaPoint();
		t.transform(this, returned);
		return returned;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	public Point toPoint() {
		return new Point((int) x, (int) y);
	}

	@Override
	public String getStringRepresentation() {
		return "DianaPoint: " + toString();
	}

	@Override
	public List<DianaPoint> getControlPoints() {
		Vector<DianaPoint> returned = new Vector<>();
		returned.add(new DianaPoint(x, y));
		return returned;
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint p) {
		return clone();
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		g.useDefaultForegroundStyle();
		g.drawPoint(this);
	}

	public static CardinalQuadrant getCardinalQuadrant(DianaPoint source, DianaPoint destination) {
		if (destination.x > source.x) {
			if (destination.y > source.y) {
				return CardinalQuadrant.SOUTH_EAST;
			}
			else {
				return CardinalQuadrant.NORTH_EAST;
			}
		}
		else {
			if (destination.y > source.y) {
				return CardinalQuadrant.SOUTH_WEST;
			}
			else {
				return CardinalQuadrant.NORTH_WEST;
			}
		}
	}

	public static SimplifiedCardinalDirection getSimplifiedOrientation(DianaPoint source, DianaPoint destination) {
		AffineTransform rotation = AffineTransform.getRotateInstance(Math.PI / 4);
		DianaPoint s2 = source.transform(rotation);
		DianaPoint d2 = destination.transform(rotation);
		CardinalQuadrant d = getCardinalQuadrant(s2, d2);
		if (d == CardinalQuadrant.NORTH_EAST) {
			return SimplifiedCardinalDirection.NORTH;
		}
		else if (d == CardinalQuadrant.SOUTH_EAST) {
			return SimplifiedCardinalDirection.EAST;
		}
		else if (d == CardinalQuadrant.SOUTH_WEST) {
			return SimplifiedCardinalDirection.SOUTH;
		}
		else if (d == CardinalQuadrant.NORTH_WEST) {
			return SimplifiedCardinalDirection.WEST;
		}
		return null;
	}

	private static final double tanPI_8 = Math.tan(Math.PI / 8);
	private static final double tan3PI_8 = Math.tan(3 * Math.PI / 8);

	public static CardinalDirection getOrientation(DianaPoint source, DianaPoint destination) {
		try {
			double slope = (destination.y - source.y) / (destination.x - source.x);
			if (slope >= 0) {
				if (slope < tanPI_8) {
					if (destination.x > source.x) {
						return CardinalDirection.EAST;
					}
					else {
						return CardinalDirection.WEST;
					}
				}
				else if (slope > tanPI_8 && slope < tan3PI_8) {
					if (destination.y > source.y) {
						return CardinalDirection.SOUTH_EAST;
					}
					else {
						return CardinalDirection.NORTH_WEST;
					}
				}
				else {
					if (destination.y > source.y) {
						return CardinalDirection.SOUTH;
					}
					else {
						return CardinalDirection.NORTH;
					}
				}
			}
			else {
				slope = -slope;
				if (slope < tanPI_8) {
					if (destination.x > source.x) {
						return CardinalDirection.EAST;
					}
					else {
						return CardinalDirection.WEST;
					}
				}
				else if (slope > tanPI_8 && slope < tan3PI_8) {
					if (destination.y > source.y) {
						return CardinalDirection.SOUTH_WEST;
					}
					else {
						return CardinalDirection.NORTH_EAST;
					}
				}
				else {
					if (destination.y > source.y) {
						return CardinalDirection.SOUTH;
					}
					else {
						return CardinalDirection.NORTH;
					}
				}
			}
		} catch (ArithmeticException e) {
			if (destination.y > source.y) {
				return CardinalDirection.SOUTH;
			}
			else {
				return CardinalDirection.NORTH;
			}
		}

	}

	public static DianaPoint getNearestPoint(DianaPoint aPoint, DianaPoint... pts) {
		Vector<DianaPoint> v = new Vector<>();
		for (DianaPoint pt : pts) {
			v.add(pt);
		}
		return getNearestPoint(aPoint, v);
	}

	public static DianaPoint getNearestPoint(DianaPoint aPoint, List<DianaPoint> pts) {
		if (aPoint == null) {
			return null;
		}

		double minimalDistanceSq = java.lang.Double.POSITIVE_INFINITY;
		DianaPoint returned = null;

		for (DianaPoint pt : pts) {
			if (pt != null) {
				double currentDistance = distanceSq(pt, aPoint);
				if (currentDistance < minimalDistanceSq) {
					returned = pt;
					minimalDistanceSq = currentDistance;
				}
			}
		}
		return returned;
	}

	@Override
	public DianaHalfLine getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			return new DianaHalfLine(this, new DianaPoint(x, y - 1));
		}
		else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			return new DianaHalfLine(this, new DianaPoint(x, y + 1));
		}
		else if (orientation == SimplifiedCardinalDirection.EAST) {
			return new DianaHalfLine(this, new DianaPoint(x + 1, y));
		}
		else if (orientation == SimplifiedCardinalDirection.WEST) {
			return new DianaHalfLine(this, new DianaPoint(x - 1, y));
		}
		return null;
	}

	@Override
	public DianaPoint getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		return clone();
	}

	public static boolean areAligned(DianaPoint p1, DianaPoint p2, DianaPoint p3) {
		DianaLine line1 = new DianaLine(p1, p2);
		DianaLine line2 = new DianaLine(p2, p3);
		return line1.overlap(line2);
	}

	public static DianaPoint middleOf(DianaPoint p1, DianaPoint p2) {
		return new DianaSegment(p1, p2).getMiddle();
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
		return new DianaRectangle(x, y, 0, 0, Filling.FILLED);
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

		if (hl.contains(this)) {
			return clone();
		}
		else {
			return null;
		}

	}

	public static DianaPoint getOppositePoint(DianaPoint p, DianaPoint pivot) {
		return new DianaPoint(2.0 * pivot.x - p.x, 2.0 * pivot.y - p.y);
	}

	public static DianaPoint getMiddlePoint(DianaPoint p1, DianaPoint p2) {
		return new DianaPoint((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
	}

}
