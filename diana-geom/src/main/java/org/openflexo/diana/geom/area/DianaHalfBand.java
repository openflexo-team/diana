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

import org.openflexo.diana.geom.AreaComputation;
import org.openflexo.diana.geom.DianaAbstractLine;
import org.openflexo.diana.geom.DianaArc;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolygon;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.ParallelLinesException;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

public class DianaHalfBand implements DianaArea {

	private static final Logger logger = Logger.getLogger(DianaHalfBand.class.getPackage().getName());

	// Storage data
	public DianaHalfLine halfLine1;
	public DianaHalfLine halfLine2;

	// Computed data
	public DianaSegment limit;
	public DianaHalfPlane halfPlane;
	public DianaBand embeddingBand;

	public DianaHalfBand() {
		super();
	}

	public DianaHalfBand(DianaHalfLine anHalfLine1, DianaHalfLine anHalfLine2) {
		super();
		this.halfLine1 = anHalfLine1;
		this.halfLine2 = anHalfLine2;
		updateParametersFromHalflines();
	}

	public DianaHalfBand(DianaLine line1, DianaLine line2, DianaHalfPlane hp) {
		this((DianaHalfLine) hp.intersect(line1), (DianaHalfLine) hp.intersect(line2));
	}

	public DianaHalfBand(DianaSegment segment, SimplifiedCardinalDirection orientation) {
		this(segment.getP1().getOrthogonalPerspectiveArea(orientation), segment.getP2().getOrthogonalPerspectiveArea(orientation));
	}

	private void updateParametersFromHalflines() {
		if (halfLine1.overlap(halfLine2)) {
			throw new IllegalArgumentException("lines are overlaping halfLine1=" + halfLine1 + " halfLine2=" + halfLine2);
		}
		if (!halfLine1.isParallelTo(halfLine2)) {
			throw new IllegalArgumentException("lines are not parallel: " + halfLine1 + " and " + halfLine2);
		}
		limit = new DianaSegment(halfLine1.getLimit(), halfLine2.getLimit());
		halfPlane = new DianaHalfPlane(new DianaLine(limit), halfLine1.getOpposite());
		if (!halfPlane.containsPoint(halfLine2.getOpposite())) {
			throw new IllegalArgumentException("half lines have opposite directions");
		}
		embeddingBand = new DianaBand(new DianaLine(halfLine1), new DianaLine(halfLine2));
	}

	public static void main(String[] args) {
		DianaHalfLine hl1 = new DianaHalfLine(new DianaPoint(1, 2), new DianaPoint(4, 2));
		DianaHalfLine hl2 = new DianaHalfLine(new DianaPoint(0, 1), new DianaPoint(8, 1));
		DianaHalfBand test = new DianaHalfBand(hl1, hl2);
		System.out.println("test=" + test);
		DianaPoint p = new DianaPoint(5, 1.5);
		System.out.println("contains " + p + " = " + test.containsPoint(p));
		p = new DianaPoint(-0.5, 1.5);
		System.out.println("contains " + p + " = " + test.containsPoint(p));
		p = new DianaPoint(5, 2.5);
		System.out.println("contains " + p + " = " + test.containsPoint(p));
		AffineTransform at = AffineTransform.getScaleInstance(100, 200);
		System.out.println("test2=" + test.transform(at));
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		if (!embeddingBand.containsPoint(p)) {
			return false;
		}
		return halfPlane.containsPoint(p);
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		if (!embeddingBand.containsLine(l)) {
			return false;
		}
		return halfPlane.containsLine(l);
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
	public DianaHalfBand clone() {
		try {
			return (DianaHalfBand) super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	@Override
	public DianaArea transform(AffineTransform t) {
		DianaHalfLine l1 = halfLine1.transform(t);
		DianaHalfLine l2 = halfLine2.transform(t);
		if (l1.overlap(l2)) {
			return l1.intersect(l2);
		}
		else {
			return new DianaHalfBand(l1, l2);
		}
	}

	@Override
	public String toString() {
		return "DianaHalfBand: " + halfLine1.toString() + " " + halfLine2.toString();
	}

	/*public void paint(DianaGraphics g)
	{
		super.paint(g);
		halfLine1.paint(g);
		halfLine2.paint(g);
		limit.paint(g);
	}*/

	@Override
	public void paint(AbstractDianaGraphics g) {
		// System.out.println("paint() for "+this);
		DianaRectangle bounds = g.getNodeNormalizedBounds();
		DianaArea l1 = bounds.intersect(halfLine1);
		DianaArea l2 = bounds.intersect(halfLine2);

		// System.out.println("l1="+l1);
		// System.out.println("l2="+l2);
		if (l1 instanceof DianaSegment && l2 instanceof DianaSegment) {
			DianaSegment s1 = (DianaSegment) l1;
			DianaSegment s2 = (DianaSegment) l2;
			g.useDefaultBackgroundStyle();
			if (new DianaSegment(s1.getP1(), s2.getP1()).intersectsInsideSegment(new DianaSegment(s1.getP2(), s2.getP2()))) {
				DianaPolygon.makeArea(Filling.FILLED, s1.getP1(), s1.getP2(), s2.getP1(), s2.getP2()).paint(g);
			}
			else {
				DianaPolygon.makeArea(Filling.FILLED, s1.getP1(), s1.getP2(), s2.getP2(), s2.getP1()).paint(g);
			}
		}

		g.useDefaultForegroundStyle();
		halfLine1.paint(g);
		halfLine2.paint(g);
		limit.paint(g);
	}

	protected DianaArea computeBandIntersection(DianaBand band) {
		DianaArea returned = embeddingBand.computeBandIntersection(band);
		DianaArea reply = returned.intersect(halfPlane);
		return reply;
	}

	protected DianaArea computeHalfBandIntersection(DianaHalfBand band) {
		DianaArea returned = embeddingBand.computeHalfBandIntersection(band);
		DianaArea reply = returned.intersect(halfPlane);
		return reply;
	}

	private DianaArea computeLineIntersection(DianaAbstractLine<?> aLine) {
		DianaArea returned = embeddingBand.intersect(aLine);
		return returned.intersect(halfPlane);
	}

	protected DianaArea computeHalfPlaneIntersection(DianaHalfPlane anHalfPlane) {
		// DianaArea returned = embeddingBand.intersect(anHalfPlane);
		// return returned.intersect(halfPlane);

		if (anHalfPlane.containsArea(this)) {
			return clone();
		}

		if (anHalfPlane.line.isParallelTo(halfLine1)) {
			if (anHalfPlane.containsLine(halfLine1)) {
				if (anHalfPlane.containsLine(halfLine2)) {
					return clone();
				}
				else {
					DianaPoint inters;
					try {
						inters = anHalfPlane.line.getLineIntersection(limit);
						// We must project opposite pt on line, parallel to limit
						DianaLine l = halfLine1.getParallelLine(inters);
						DianaLine l2 = limit.getParallelLine(halfLine1.getOpposite());
						DianaPoint opp = l.getLineIntersection(l2);
						return new DianaHalfBand(halfLine1, new DianaHalfLine(inters, opp));
					} catch (ParallelLinesException e) {
						// cannot happen
						e.printStackTrace();
						return null;
					} catch (IllegalArgumentException e) {
						logger.warning("IllegalArgumentException raised while computing intersection of " + this + " and " + anHalfPlane);
						e.printStackTrace();
						return new DianaEmptyArea();
					}
				}
			}
			else {
				if (anHalfPlane.containsLine(halfLine2)) {
					try {
						DianaPoint inters = anHalfPlane.line.getLineIntersection(limit);

						// We must project opposite pt on line, parallel to limit
						DianaLine l = halfLine2.getParallelLine(inters);
						DianaLine l2 = limit.getParallelLine(halfLine2.getOpposite());
						DianaPoint opp = l.getLineIntersection(l2);
						return new DianaHalfBand(halfLine2, new DianaHalfLine(inters, opp));

						// TODO: this is not exact, we must project opposite pt on line, parallel to limit
						/*logger.warning("Please implement this better");
						DianaPoint opp = halfLine2.getProjection(halfLine2.getOpposite());
						return new DianaHalfBand(halfLine2,new DianaHalfLine(inters,opp));*/
					} catch (ParallelLinesException e) {
						// cannot happen
						e.printStackTrace();
						return null;
					}
				}
				else {
					return new DianaEmptyArea();
				}
			}
		}

		DianaArea returned = embeddingBand.computeHalfPlaneIntersection(anHalfPlane);

		if (returned instanceof DianaHalfBand) {
			// we must now intersect with halfplane
			DianaHalfBand hb = (DianaHalfBand) returned;
			DianaPoint pp1 = hb.halfLine1.getLineIntersection(halfPlane.line);
			DianaPoint pp2 = hb.halfLine2.getLineIntersection(halfPlane.line);
			if (hb.halfLine1.containsPoint(pp1) && hb.halfLine2.containsPoint(pp2)) {
				DianaArea returnThis = DianaPolygon.makeArea(Filling.FILLED, hb.halfLine1.getLimit(), hb.halfLine2.getLimit(), pp2, pp1);
				return returnThis;
			}
			else if (hb.halfLine1.containsPoint(pp1) || hb.halfLine2.containsPoint(pp2)) {
				return new DianaIntersectionArea(returned, this);
			}
			else {
				// No intersection, we have all or nothing
				if (halfPlane.containsLine(hb.halfLine1) && halfPlane.containsLine(hb.halfLine2)) {
					return hb;
				}
				return new DianaEmptyArea();
			}
		}

		return returned;
	}

	@Override
	public int hashCode() {
		return (halfLine1.hashCode() + halfLine2.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaHalfBand) {
			DianaHalfBand hb = (DianaHalfBand) obj;
			return hb.halfLine1.equals(halfLine1) && hb.halfLine2.equals(halfLine2)
					|| hb.halfLine2.equals(halfLine1) && hb.halfLine1.equals(halfLine2);
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
			return AreaComputation.isShapeContainedInArea((DianaShape<?>) a, this);
		}
		if (a instanceof DianaHalfBand) {
			return containsLine(((DianaHalfBand) a).halfLine1) && containsLine(((DianaHalfBand) a).halfLine2);
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
		}
		else {
			return returned;
		}
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		}
		else {
			return DianaPoint.getNearestPoint(aPoint, halfLine1.getNearestPoint(aPoint), halfLine2.getNearestPoint(aPoint));
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
	public DianaArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			if (halfLine1.isVertical()) {
				return clone();
			}
			else {
				DianaPoint p = limit.getP1().y > limit.getP2().y ? limit.getP1() : limit.getP2();
				return new DianaHalfPlane(DianaLine.makeVerticalLine(p), halfLine1.getOpposite());
			}
		}
		else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			if (halfLine1.isVertical()) {
				return clone();
			}
			else {
				DianaPoint p = limit.getP1().y > limit.getP2().y ? limit.getP2() : limit.getP1();
				return new DianaHalfPlane(DianaLine.makeVerticalLine(p), halfLine1.getOpposite());
			}
		}
		else if (orientation == SimplifiedCardinalDirection.EAST) {
			if (halfLine1.isHorizontal()) {
				return clone();
			}
			else {
				DianaPoint p = limit.getP1().x > limit.getP2().x ? limit.getP2() : limit.getP1();
				return new DianaHalfPlane(DianaLine.makeHorizontalLine(p), halfLine1.getOpposite());
			}
		}
		else if (orientation == SimplifiedCardinalDirection.WEST) {
			if (halfLine1.isHorizontal()) {
				return clone();
			}
			else {
				DianaPoint p = limit.getP1().x > limit.getP2().x ? limit.getP1() : limit.getP2();
				return new DianaHalfPlane(DianaLine.makeHorizontalLine(p), halfLine1.getOpposite());
			}
		}
		return null;
	}

	@Override
	public DianaHalfBand getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
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
