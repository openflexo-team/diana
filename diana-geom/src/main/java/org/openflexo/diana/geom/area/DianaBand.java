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
import java.util.Random;
import java.util.logging.Logger;

import org.openflexo.diana.geom.AreaComputation;
import org.openflexo.diana.geom.DianaAbstractLine;
import org.openflexo.diana.geom.DianaArc;
import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolygon;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.ParallelLinesException;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

public class DianaBand implements DianaArea {

	private static final Logger logger = Logger.getLogger(DianaBand.class.getPackage().getName());

	public DianaLine line1;
	public DianaLine line2;

	private DianaPoint testPoint;

	public DianaBand() {
		super();
	}

	public DianaBand(DianaLine aLine1, DianaLine aLine2) {
		super();
		this.line1 = new DianaLine(aLine1);
		this.line2 = new DianaLine(aLine2);
		if (!line1.isParallelTo(line2)) {
			throw new IllegalArgumentException("lines are not parallel");
		}
		computeTestPoint();
	}

	protected void computeTestPoint() {
		DianaPoint randPoint = null;
		boolean found = false;
		while (!found) {
			Random rand = new Random();
			randPoint = new DianaPoint(rand.nextInt(), rand.nextInt());
			if (!line1.contains(randPoint) && !line2.contains(randPoint)) {
				found = true;
			}
		}
		DianaSegment segment = new DianaSegment(line1.getProjection(randPoint), line2.getProjection(randPoint));
		testPoint = segment.getMiddle();
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		return bandContainsPoint(p);
	}

	private boolean bandContainsPoint(DianaPoint p) {
		if (line1.contains(p)) {
			return true;
		}
		if (line2.contains(p)) {
			return true;
		}
		return line1.getPlaneLocation(testPoint) == line1.getPlaneLocation(p)
				&& line2.getPlaneLocation(testPoint) == line2.getPlaneLocation(p);
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		if (!containsLineIgnoreBounds(l)) {
			return false;
		}

		if (l instanceof DianaHalfLine) {
			return l.isParallelTo(line1);
		}
		if (l instanceof DianaSegment) {
			return true;
		}
		if (l instanceof DianaLine) {
			return l.isParallelTo(line1);
		}
		logger.warning("Unexpected: " + l);
		return false;
	}

	public boolean containsLineIgnoreBounds(DianaAbstractLine<?> l) {
		return bandContainsPoint(l.getP1()) && bandContainsPoint(l.getP2());
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
			return AreaComputation.isShapeContainedInArea((DianaShape<?>) a, this);
		}
		if (a instanceof DianaHalfBand) {
			return containsLine(((DianaHalfBand) a).halfLine1) && containsLine(((DianaHalfBand) a).halfLine2);
		}
		if (a instanceof DianaBand) {
			return containsLine(((DianaBand) a).line1) && containsLine(((DianaBand) a).line2);
		}
		return false;
	}

	@Override
	public DianaArea exclusiveOr(DianaArea area) {
		return new DianaExclusiveOrArea(this, area);
	}

	@Override
	public DianaArea intersect(DianaArea area) {
		// There is a big problem if we uncomment this here
		// We really need to build many JUnit tests in order to cover all area computation
		// Many cases are wrong !!!

		// if (area.containsArea(this)) return this.clone();
		// if (containsArea(area)) return area.clone();
		if (area instanceof DianaAbstractLine) {
			return computeLineIntersection((DianaAbstractLine<?>) area);
		}
		if (area instanceof DianaRectangle) {
			return area.intersect(this);
		}
		if (area instanceof DianaPolygon) {
			return area.intersect(this);
		}
		if (area instanceof DianaArc) {
			return area.intersect(this);
		}
		if (area instanceof DianaBand) {
			return computeBandIntersection((DianaBand) area);
		}
		if (area instanceof DianaHalfBand) {
			return computeHalfBandIntersection((DianaHalfBand) area);
		}
		if (area instanceof DianaHalfPlane) {
			return computeHalfPlaneIntersection((DianaHalfPlane) area);
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
	public DianaBand clone() {
		try {
			return (DianaBand) super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	@Override
	public DianaArea transform(AffineTransform t) {
		DianaLine l1 = line1.transform(t);
		DianaLine l2 = line1.transform(t);
		if (l1.overlap(l2)) {
			return l1;
		} else {
			return new DianaBand(l1, l2);
		}
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		DianaRectangle bounds = g.getNodeNormalizedBounds();

		g.useDefaultBackgroundStyle();
		bounds.intersect(this).paint(g);

		g.useDefaultForegroundStyle();
		line1.paint(g);
		line2.paint(g);

		/*
		System.out.println("paint() for "+this);
		
		DianaRectangle bounds = g.getGraphicalRepresentation().getLogicalBounds();
		Vector<DianaPoint> pts = new Vector<DianaPoint>();

		DianaArea a1 = bounds.intersect(new DianaLine(line1));
		if (a1 instanceof DianaSegment) {
			DianaSegment s1 = (DianaSegment)a1;
			pts.add(s1.getP1());
			pts.add(s1.getP2());
		}
		else if (a1 instanceof DianaPoint) {
			pts.add((DianaPoint)a1);
		}
		else if (a1 instanceof DianaEmptyArea) {
		}
		else {
			logger.warning("Unexpected intersection: "+a1);
		}
		
		DianaArea a2 = bounds.intersect(new DianaLine(line2));
		if (a2 instanceof DianaSegment) {
			DianaSegment s2 = (DianaSegment)a2;
			pts.add(s2.getP1());
			pts.add(s2.getP2());
		}
		else if (a2 instanceof DianaPoint) {
			pts.add((DianaPoint)a2);
		}
		else if (a2 instanceof DianaEmptyArea) {
		}
		else {
			logger.warning("Unexpected intersection: "+a2);
		}
		
		if (containsPoint(bounds.getNorthEastPt())) pts.add(bounds.getNorthEastPt());
		if (containsPoint(bounds.getNorthWestPt())) pts.add(bounds.getNorthWestPt());
		if (containsPoint(bounds.getSouthEastPt())) pts.add(bounds.getSouthEastPt());
		if (containsPoint(bounds.getSouthWestPt())) pts.add(bounds.getSouthWestPt());
		
		g.useDefaultBackgroundStyle();
		DianaPolygon.makeArea(Filling.FILLED,pts).paint(g);
		

		g.useDefaultForegroundStyle();
		line1.paint(g);
		line2.paint(g);*/
	}

	@Override
	public String toString() {
		return "DianaBand: " + line1.toString() + " " + line2.toString();
	}

	public static void main(String[] args) {
		DianaLine line0 = new DianaLine(0, 0, 10, 0);
		DianaLine line1 = new DianaLine(0, 1, 10, 1);
		DianaLine line2 = new DianaLine(0, 2, 10, 2);
		DianaLine line3 = new DianaLine(0, 3, 10, 3);
		DianaBand b1 = new DianaBand(line3, line0);
		DianaBand b2 = new DianaBand(line2, line1);
		System.out.println("intersection: " + b2.intersect(b1));

		DianaHalfLine hl3 = new DianaHalfLine(new DianaPoint(1, 3), new DianaPoint(-1, 3));
		DianaHalfLine hl2 = new DianaHalfLine(new DianaPoint(0, 2), new DianaPoint(2, 2));
		DianaHalfLine hl1 = new DianaHalfLine(new DianaPoint(0, 1), new DianaPoint(2, 1));
		DianaHalfLine hl0 = new DianaHalfLine(new DianaPoint(1, 0), new DianaPoint(-1, 0));

		DianaHalfBand hb1 = new DianaHalfBand(hl3, hl0);
		DianaHalfBand hb2 = new DianaHalfBand(hl2, hl1);
		System.out.println("intersection: " + hb2.intersect(hb1));

		System.out.println("hb1=" + hb1);
		System.out.println("hb1.intersect(hb1.halfPlane)=" + hb1.intersect(hb1.halfPlane));
	}

	protected DianaArea computeHalfPlaneIntersection(DianaHalfPlane hp) {
		if (hp.containsArea(this)) {
			return clone();
		}

		if (hp.line.isParallelTo(line1)) {
			if (hp.containsLine(line1)) {
				if (hp.containsLine(line2)) {
					return clone();
				} else {
					return new DianaBand(line1, hp.line);
				}
			} else {
				if (hp.containsLine(line2)) {
					return new DianaBand(line2, hp.line);
				} else {
					return new DianaEmptyArea();
				}
			}
		}

		else {
			try {
				DianaPoint i1 = line1.getLineIntersection(hp.line);
				DianaPoint i2 = line2.getLineIntersection(hp.line);
				DianaPoint pp1, pp2;

				if (!hp.line.containsPoint(line1.getP1())) {
					if (hp.containsPoint(line1.getP1())) {
						pp1 = line1.getP1();
					} else {
						pp1 = DianaAbstractLine.getOppositePoint(line1.getP1(), i1);
					}
				} else {
					if (hp.containsPoint(line1.getP2())) {
						pp1 = line1.getP2();
					} else {
						pp1 = DianaAbstractLine.getOppositePoint(line1.getP2(), i1);
					}
				}

				if (!hp.line.containsPoint(line2.getP1())) {
					if (hp.containsPoint(line2.getP1())) {
						pp2 = line2.getP1();
					} else {
						pp2 = DianaAbstractLine.getOppositePoint(line2.getP1(), i2);
					}
				} else {
					if (hp.containsPoint(line2.getP2())) {
						pp2 = line2.getP2();
					} else {
						pp2 = DianaAbstractLine.getOppositePoint(line2.getP2(), i2);
					}
				}

				DianaHalfLine hl1 = new DianaHalfLine(i1, pp1);
				DianaHalfLine hl2 = new DianaHalfLine(i2, pp2);

				if (hl1.overlap(hl2)) {
					return new DianaEmptyArea();
				} else {
					return new DianaHalfBand(hl1, hl2);
				}

			} catch (ParallelLinesException e) {
				// Cannot happen
				e.printStackTrace();
				return null;
			}
		}
	}

	protected DianaArea computeHalfBandIntersection(DianaHalfBand hb) {
		DianaArea returned = hb.embeddingBand.computeBandIntersection(this);
		DianaArea reply = returned.intersect(hb.halfPlane);
		return reply;
	}

	protected DianaArea computeBandIntersection(DianaBand band) {
		DianaArea returned = null;

		if (!line1.isParallelTo(band.line1)) {
			try {
				if (line1.isOrthogonalTo(band.line1)) {
					DianaPoint p1 = line1.getLineIntersection(band.line1);
					DianaPoint p2 = line2.getLineIntersection(band.line2);
					returned = new DianaRectangle(p1, p2, Filling.FILLED);
				} else {
					returned = DianaPolygon.makeArea(Filling.FILLED, line1.getLineIntersection(band.line1),
							line1.getLineIntersection(band.line2), line2.getLineIntersection(band.line2),
							line2.getLineIntersection(band.line1));
				}
			} catch (ParallelLinesException e) {
				// Cannot happen
				e.printStackTrace();
				return null;
			}
		} else {
			if (containsArea(band)) {
				returned = band.clone();
			} else if (band.containsArea(this)) {
				returned = this.clone();
			} else if (containsLineIgnoreBounds(band.line1)) {
				if (containsLine(band.line2)) {
					returned = band.clone();
				} else {
					if (band.containsLineIgnoreBounds(line2)) {
						returned = new DianaBand(band.line1, line2);
						// System.out.println("Built from Parallel lines "+returned);
					} else {
						returned = new DianaBand(band.line1, line1);
						// System.out.println("Built from Parallel lines "+returned);
					}
				}
			} else {
				if (containsLineIgnoreBounds(band.line2)) {
					if (band.containsLineIgnoreBounds(line2)) {
						returned = new DianaBand(band.line2, line2);
						// System.out.println("Built from Parallel lines "+returned);
					} else {
						returned = new DianaBand(band.line2, line1);
						// System.out.println("Built from Parallel lines "+returned);
					}
				} else {
					returned = new DianaEmptyArea();
				}
			}
		}

		if (returned == null) {
			return new DianaEmptyArea();
		}

		/*if (band instanceof DianaHalfBand) {
			return returned.intersect(((DianaHalfBand)band).halfPlane);
		}*/

		// if (containsArea(returned) && band.containsArea(returned)) return returned;
		return returned;

		// return new DianaEmptyArea();
	}

	private DianaArea computeLineIntersection(DianaAbstractLine<?> aLine) {
		DianaHalfPlane hp1 = new DianaHalfPlane(line1, line2.getP1());
		DianaArea a1 = hp1.intersect(aLine);
		if (a1 instanceof DianaEmptyArea) {
			return a1;
		}
		DianaHalfPlane hp2 = new DianaHalfPlane(line2, line1.getP1());
		return hp2.intersect(a1);
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		} else {
			return DianaPoint.getNearestPoint(aPoint, line1.getProjection(aPoint), line2.getProjection(aPoint));
		}
	}

	@Override
	public int hashCode() {
		return (line1.hashCode() + line2.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaBand) {
			DianaBand b = (DianaBand) obj;
			return (line1.overlap(b.line1) || line1.overlap(b.line2)) && (line2.overlap(b.line1) || line2.overlap(b.line2));
		}
		return false;
	}

	@Override
	public DianaArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			if (line1.isVertical()) {
				return clone();
			} else {
				return DianaAbstractLine.getNorthernLine(line1, line2);
			}
		} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			if (line1.isVertical()) {
				return clone();
			} else {
				return DianaAbstractLine.getSouthernLine(line1, line2);
			}
		} else if (orientation == SimplifiedCardinalDirection.EAST) {
			if (line1.isHorizontal()) {
				return clone();
			} else {
				return DianaAbstractLine.getEasternLine(line1, line2);
			}
		} else if (orientation == SimplifiedCardinalDirection.WEST) {
			if (line1.isHorizontal()) {
				return clone();
			} else {
				return DianaAbstractLine.getWesternLine(line1, line2);
			}
		}
		return null;
	}

	@Override
	public DianaBand getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
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
