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
import java.awt.geom.Line2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaBand;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaExclusiveOrArea;
import org.openflexo.diana.geom.area.DianaHalfBand;
import org.openflexo.diana.geom.area.DianaHalfLine;
import org.openflexo.diana.geom.area.DianaHalfPlane;
import org.openflexo.diana.geom.area.DianaIntersectionArea;
import org.openflexo.diana.geom.area.DianaSubstractionArea;
import org.openflexo.diana.geom.area.DianaUnionArea;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

@SuppressWarnings("serial")
public abstract class DianaAbstractLine<L extends DianaAbstractLine<L>> extends Line2D.Double implements DianaGeometricObject<L> {

	private static final Logger logger = Logger.getLogger(DianaAbstractLine.class.getPackage().getName());

	// Equation is a*x + b*y + c = 0
	private double a;
	private double b;
	private double c;

	private DianaPoint p1;
	private DianaPoint p2;

	@SuppressWarnings("unchecked")
	@Override
	public DianaAbstractLine<L> clone() {
		return (DianaAbstractLine<L>) super.clone();
	}

	public DianaAbstractLine(double X1, double Y1, double X2, double Y2) {
		super(X1, Y1, X2, Y2);
	}

	public DianaAbstractLine(DianaPoint p1, DianaPoint p2) {
		super(p1, p2);
	}

	public DianaAbstractLine(DianaAbstractLine<?> line) {
		super(line.getP1(), line.getP2());
	}

	public DianaAbstractLine() {
		super();
	}

	public DianaAbstractLine(double pA, double pB, double pC) {
		super();

		if (pB != 0) {
			DianaPoint p1 = new DianaPoint(0, -pC / pB);
			DianaPoint p2 = new DianaPoint(1, -(pA + pC) / pB);
			setLine(p1, p2);
		}
		else {
			DianaPoint p1 = new DianaPoint(-pC / pA, 0);
			DianaPoint p2 = new DianaPoint(-pC / pA, 1);
			setLine(p1, p2);
		}
	}

	@Override
	public List<DianaPoint> getControlPoints() {
		Vector<DianaPoint> returned = new Vector<>();
		returned.add(getP1());
		returned.add(getP2());
		return returned;
	}

	@Override
	public DianaPoint getP1() {
		return p1;
		/*
		 * Point2D returned = super.getP1(); return new DianaPoint(returned.getX(),returned.getY());
		 */
	}

	public void setP1(DianaPoint p1) {
		setX1(p1.x);
		setY1(p1.y);
	}

	@Override
	public DianaPoint getP2() {
		return p2;
		/*
		 * Point2D returned = super.getP2(); return new DianaPoint(returned.getX(),returned.getY());
		 */
	}

	public void setP2(DianaPoint p2) {
		setX2(p2.x);
		setY2(p2.y);
	}

	@Override
	public final void setLine(double X1, double Y1, double X2, double Y2) {
		super.setLine(X1, Y1, X2, Y2);
		updateCoeffs();
		// System.out.println("hop: "+(a*X1+b*Y1+c)+" et "+(a*X2+b*Y2+c));
	}

	public void setX1(double x1) {
		this.x1 = x1;
		updateCoeffs();
	}

	public void setX2(double x2) {
		this.x2 = x2;
		updateCoeffs();
	}

	public void setY1(double y1) {
		this.y1 = y1;
		updateCoeffs();
	}

	public void setY2(double y2) {
		this.y2 = y2;
		updateCoeffs();
	}

	public double getA() {
		return a;
	}

	public double getB() {
		return b;
	}

	public double getC() {
		return c;
	}

	private void updateCoeffs() {
		if (GeomUtils.doubleEquals(x1, x2) && GeomUtils.doubleEquals(y1, y2)) {
			// if (x1==x2 && y1==y2) {
			a = 0;
			b = 0;
			c = 0;
		}

		if (!GeomUtils.doubleEquals(x1, x2)) {
			// if (x1 != x2) {
			b = 1;
			a = (y2 - y1) / (x1 - x2);
			c = -(a * x1 + y1);
		}
		else {
			b = 0;
			a = 1;
			c = -x1;
		}
		p1 = new DianaPoint(x1, y1);
		p2 = new DianaPoint(x2, y2);
	}

	public final boolean overlap(DianaAbstractLine<?> anOtherLine) {
		return isParallelTo(anOtherLine) && anOtherLine._containsPointIgnoreBounds(getP1());
	}

	public static DianaPoint getOppositePoint(DianaPoint p, DianaPoint pivot) {
		return DianaPoint.getOppositePoint(p, pivot);
	}

	public static DianaPoint getMiddlePoint(DianaPoint p1, DianaPoint p2) {
		return DianaPoint.getMiddlePoint(p1, p2);
	}

	/**
	 * Return a new line orthogonal to this instance and crossing supplied point
	 * 
	 * @param p
	 *            point the returned line must cross
	 * @return a new DianaLine instance
	 */
	public DianaLine getOrthogonalLine(DianaPoint p) {
		double a1 = -b;
		double b1 = a;
		double c1 = -(a1 * p.x + b1 * p.y);
		return new DianaLine(a1, b1, c1);
	}

	/**
	 * Return a new line orthogonal to reference line and crossing supplied point
	 * 
	 * @param reference
	 *            reference line
	 * @param p
	 *            point the returned line must cross
	 * @return a new DianaLine instance
	 */
	public static DianaLine getOrthogonalLine(DianaAbstractLine<?> reference, DianaPoint p) {
		return reference.getOrthogonalLine(p);
	}

	/**
	 * Return a new line parallel to this instance and crossing supplied point
	 * 
	 * @param p
	 *            point the returned line must cross
	 * @return a new DianaLine instance
	 */
	public DianaLine getParallelLine(DianaPoint p) {
		double a1 = a;
		double b1 = b;
		double c1 = -(a1 * p.x + b1 * p.y);
		return new DianaLine(a1, b1, c1);
	}

	/**
	 * Return a new line parallel to reference line and crossing supplied point
	 * 
	 * @param reference
	 *            reference line
	 * @param p
	 *            point the returned line must cross
	 * @return a new DianaLine instance
	 */
	public static DianaLine getParallelLine(DianaAbstractLine<?> reference, DianaPoint p) {
		return reference.getParallelLine(p);
	}

	/**
	 * Return a new line obtained by rotation of supplied angle (given in degree) and crossing supplied point
	 * 
	 * @param angle
	 *            (in degree)
	 * @param p
	 *            point the returned line must cross
	 * @return a new DianaLine instance
	 */
	public DianaLine getRotatedLine(double angle, DianaPoint p) {
		DianaAbstractLine<?> l = transform(AffineTransform.getRotateInstance(Math.toRadians(angle)));
		return l.getParallelLine(p);
	}

	/**
	 * Return a new line obtained by rotation of supplied angle (given in degree) and crossing supplied point
	 * 
	 * @param reference
	 *            reference line
	 * @param angle
	 *            (in degree)
	 * @param p
	 *            point the returned line must cross
	 * @return a new DianaLine instance
	 */
	public static DianaLine getRotatedLine(DianaAbstractLine<?> reference, double angle, DianaPoint p) {
		return reference.getRotatedLine(angle, p);
	}

	public DianaPoint getLineIntersection(DianaAbstractLine<?> aLine) throws ParallelLinesException {
		double a1 = aLine.a;
		double b1 = aLine.b;
		double c1 = aLine.c;

		double det = a1 * b - a * b1;

		if (Math.abs(det) < EPSILON) { // parallel lines
			throw new ParallelLinesException();
		}
		else {
			return new DianaPoint(-(b * c1 - b1 * c) / det, (a * c1 - a1 * c) / det);
		}
	}

	public boolean isParallelTo(DianaAbstractLine<?> aLine) {
		double a1 = aLine.a;
		double b1 = aLine.b;

		double det = a1 * b - a * b1;

		if (Math.abs(det) < EPSILON) { // parallel lines
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isOrthogonalTo(DianaAbstractLine<?> aLine) {
		double a1 = aLine.a;
		double b1 = aLine.b;

		if (Math.abs(a * a1 - b * b1) < EPSILON) { // orthogonal lines
			return true;
		}
		else {
			return false;
		}
	}

	public static DianaPoint getLineIntersection(DianaAbstractLine<?> line1, DianaAbstractLine<?> line2) throws ParallelLinesException {
		return line1.getLineIntersection(line2);
	}

	public abstract boolean contains(DianaPoint p);

	protected final boolean _containsPointIgnoreBounds(DianaPoint p) {
		return Math.abs(a * p.x + b * p.y + c) < EPSILON;
	}

	public int getPlaneLocation(DianaPoint p) {
		double k = a * p.x + b * p.y + c;
		if (Math.abs(k) < EPSILON) {
			return 0; // We are on line
		}
		if (k > 0) {
			return 1; // We are on one side
		}
		else {
			return -1; // We are on the other side
		}
	}

	public DianaPoint getProjection(DianaPoint p) {
		if (contains(p)) {
			return new DianaPoint(p);
		}
		try {
			return getOrthogonalLine(p).getLineIntersection(this);
		} catch (ParallelLinesException e) {
			// cannot happen
			e.printStackTrace();
			return null;
		}
	}

	public static DianaPoint getProjection(DianaPoint p, DianaAbstractLine<?> line) {
		return line.getProjection(p);
	}

	public DianaPoint pointAtAbciss(double x) {
		if (b != 0) {
			return new DianaPoint(x, -(a * x + c) / b);
		}
		throw new IllegalArgumentException("Line does not pass through this abciss");
	}

	public DianaPoint pointAtOrdinate(double y) {
		if (a != 0) {
			return new DianaPoint(-(b * y + c) / a, y);
		}
		throw new IllegalArgumentException("Line does not pass through this ordinate");
	}

	@Override
	public abstract DianaPoint getNearestPoint(DianaPoint p);

	public boolean isHorizontal() {
		return Math.abs(getY1() - getY2()) < EPSILON;
	}

	public boolean isVertical() {
		return Math.abs(getX1() - getX2()) < EPSILON;
	}

	/**
	 * Compute and return angle formed by the abstract line (NOTE: assert that coordinate system is orthonormal !!!!!)
	 * 
	 * @return angle in radians, in range -PI/2 - 3*PI/2
	 */
	public double getAngle() {
		return GeomUtils.getSlope(getP1(), getP2());
	}

	public static DianaAbstractLine<?> getNorthernLine(DianaAbstractLine<?> source, DianaAbstractLine<?> destination) {
		if (source.isParallelTo(destination)) {
			DianaLine orthogonalLine = source.getOrthogonalLine(source.getP1());
			DianaPoint p1 = orthogonalLine.getLineIntersection(source);
			DianaPoint p2 = orthogonalLine.getLineIntersection(destination);
			return p1.y <= p2.y ? source : destination;
		}
		else {
			throw new IllegalArgumentException("lines are not parallel");
		}
	}

	public static DianaAbstractLine<?> getSouthernLine(DianaAbstractLine<?> source, DianaAbstractLine<?> destination) {
		if (source.isParallelTo(destination)) {
			DianaLine orthogonalLine = source.getOrthogonalLine(source.getP1());
			DianaPoint p1 = orthogonalLine.getLineIntersection(source);
			DianaPoint p2 = orthogonalLine.getLineIntersection(destination);
			return p1.y >= p2.y ? source : destination;
		}
		else {
			throw new IllegalArgumentException("lines are not parallel");
		}
	}

	public static DianaAbstractLine<?> getEasternLine(DianaAbstractLine<?> source, DianaAbstractLine<?> destination) {
		if (source.isParallelTo(destination)) {
			DianaLine orthogonalLine = source.getOrthogonalLine(source.getP1());
			DianaPoint p1 = orthogonalLine.getLineIntersection(source);
			DianaPoint p2 = orthogonalLine.getLineIntersection(destination);
			return p1.x >= p2.x ? source : destination;
		}
		else {
			throw new IllegalArgumentException("lines are not parallel");
		}
	}

	public static DianaAbstractLine<?> getWesternLine(DianaAbstractLine<?> source, DianaAbstractLine<?> destination) {
		if (source.isParallelTo(destination)) {
			DianaLine orthogonalLine = source.getOrthogonalLine(source.getP1());
			DianaPoint p1 = orthogonalLine.getLineIntersection(source);
			DianaPoint p2 = orthogonalLine.getLineIntersection(destination);
			return p1.x <= p2.x ? source : destination;
		}
		else {
			throw new IllegalArgumentException("lines are not parallel");
		}
	}

	protected abstract DianaArea computeLineIntersection(DianaAbstractLine<?> line);

	/*
	 * { logger.info("computeIntersection() between "+this+"\n and "+line+" overlap="+overlap(line)); if (overlap(line)) { if (this
	 * instanceof DianaHalfLine) { if (line instanceof DianaHalfLine) { return _compute_hl_hl_Intersection((DianaHalfLine)this,(DianaHalfLine)line);
	 * } else if (line instanceof DianaSegment) { return _compute_hl_segment_Intersection((DianaHalfLine)this,(DianaSegment)line); } else { return
	 * clone(); } } else if (this instanceof DianaSegment) { if (line instanceof DianaHalfLine) { return
	 * _compute_hl_segment_Intersection((DianaHalfLine)line,(DianaSegment)this); } else if (line instanceof DianaSegment) { return
	 * _compute_segment_segment_Intersection((DianaSegment)this,(DianaSegment)line); } else { return clone(); } } else { return line.clone(); }
	 * } else if (isParallelTo(line)) { return new DianaEmptyArea(); } else { DianaPoint returned; try { returned = getLineIntersection(line);
	 * if (containsPoint(returned) && line.containsPoint(returned)) return returned; } catch (ParallelLinesException e) { // cannot happen }
	 * return new DianaEmptyArea(); } }
	 */

	@Override
	public final DianaArea exclusiveOr(DianaArea area) {
		return new DianaExclusiveOrArea(this, area);
	}

	@Override
	public final DianaArea intersect(DianaArea area) {
		if (area.containsArea(this)) {
			return this.clone();
		}
		if (containsArea(area)) {
			return area.clone();
		}
		if (area instanceof DianaPoint) {
			DianaPoint p = (DianaPoint) area;
			if (containsPoint(p)) {
				return p.clone();
			}
			else {
				return new DianaEmptyArea();
			}
		}
		if (area instanceof DianaAbstractLine) {
			return computeLineIntersection((DianaAbstractLine<?>) area);
		}
		if (area instanceof DianaRectangle) {
			return ((DianaRectangle) area).intersect(this);
		}
		if (area instanceof DianaRoundRectangle) {
			return ((DianaRoundRectangle) area).intersect(this);
		}
		if (area instanceof DianaHalfPlane) {
			return ((DianaHalfPlane) area).intersect(this);
		}
		if (area instanceof DianaHalfBand) {
			return ((DianaHalfBand) area).intersect(this);
		}
		if (area instanceof DianaBand) {
			return ((DianaBand) area).intersect(this);
		}
		if (area instanceof DianaArc) {
			return ((DianaArc) area).intersect(this);
		}
		if (area instanceof DianaPolygon) {
			return ((DianaPolygon) area).intersect(this);
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
	public final DianaArea substract(DianaArea area, boolean isStrict) {
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
	public final boolean containsPoint(DianaPoint p) {
		return contains(p);
	}

	@Override
	public abstract boolean containsLine(DianaAbstractLine<?> l);

	@Override
	public final boolean containsArea(DianaArea a) {
		if (a instanceof DianaPoint) {
			return containsPoint((DianaPoint) a);
		}
		if (a instanceof DianaAbstractLine) {
			return containsLine((DianaAbstractLine<?>) a);
		}
		return false;
	}

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract DianaAbstractLine<?> transform(AffineTransform t);

	@Override
	public abstract String toString();

	@Override
	public String getStringRepresentation() {
		return toString();
	}

	@Override
	public abstract void paint(AbstractDianaGraphics g);

	public static DianaArea _compute_hl_segment_Intersection(DianaHalfLine hl, DianaSegment s) {
		if (hl.containsPoint(s.getP1())) {
			if (hl.containsPoint(s.getP2())) {
				return s.clone();
			}
			else {
				return new DianaSegment(hl.getLimit(), s.getP1());
			}
		}
		else {
			if (hl.containsPoint(s.getP2())) {
				return new DianaSegment(hl.getLimit(), s.getP2());
			}
			else {
				return new DianaEmptyArea();
			}
		}
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

		DianaArea returned = hl.intersect(this);

		if (hl.overlap(this)) {
			if (returned instanceof DianaArea) {
				return null;
			}
			else {
				return getNearestPoint(from);
			}
		}

		if (returned instanceof DianaEmptyArea) {
			return null;
		}
		else if (returned instanceof DianaPoint) {
			return (DianaPoint) returned;
		}
		else {
			logger.warning("Unexpected area: " + returned);
			return null;
		}

	}

}
