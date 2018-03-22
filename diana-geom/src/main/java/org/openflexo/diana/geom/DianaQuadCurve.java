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
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.QuadCurve2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaExclusiveOrArea;
import org.openflexo.diana.geom.area.DianaIntersectionArea;
import org.openflexo.diana.geom.area.DianaSubstractionArea;
import org.openflexo.diana.geom.area.DianaUnionArea;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

@SuppressWarnings("serial")
public class DianaQuadCurve extends Double implements DianaGeneralShape.GeneralShapePathElement<DianaQuadCurve> {

	private static final Logger logger = Logger.getLogger(DianaQuadCurve.class.getPackage().getName());

	/**
	 * This value is internally used to compute approximated data (nearest point, distance, etc...)
	 */
	private static final double FLATTENING_PATH_LEVEL = 0.01;

	private DianaPoint p3, pp1, pp2;

	public DianaQuadCurve() {
		super();
	}

	/**
	 * Build a quadratic curve given 3 points.
	 * 
	 * @param p1
	 *            start point
	 * @param ctrlP
	 *            real control point (not located on curve)
	 * @param p2
	 *            end point
	 */
	public DianaQuadCurve(DianaPoint p1, DianaPoint ctrlP, DianaPoint p2) {
		super();
		try {
			setCurve(p1.x, p1.y, ctrlP.x, ctrlP.y, p2.x, p2.y);
		} catch (NullPointerException e) {
			e.printStackTrace();
			logger.warning("Unexpected NPE");
		}
	}

	/**
	 * Build a quadratic curve given 3 points. The curve will cross each point
	 * 
	 * @param p1
	 *            start point
	 * @param p3
	 *            control point LOCATED on curve
	 * @param p2
	 *            end point
	 * @return
	 */
	public static DianaQuadCurve makeCurveFromPoints(DianaPoint p1, DianaPoint p3, DianaPoint p2) {
		DianaQuadCurve returned = new DianaQuadCurve(p1, p3, p2);
		returned.setP3(p3);
		return returned;
	}

	@Override
	public DianaPoint getP1() {
		return new DianaPoint(x1, y1);
	}

	public void setP1(DianaPoint aPoint) {
		x1 = aPoint.x;
		y1 = aPoint.y;
		update();
	}

	@Override
	public DianaPoint getP2() {
		return new DianaPoint(x2, y2);
	}

	public void setP2(DianaPoint aPoint) {
		x2 = aPoint.x;
		y2 = aPoint.y;
		update();
	}

	public DianaPoint getCtrlPoint() {
		return new DianaPoint(ctrlx, ctrly);
	}

	public void setCtrlPoint(DianaPoint aPoint) {
		ctrlx = aPoint.x;
		ctrly = aPoint.y;
		update();
	}

	@Override
	public void setCurve(double x1, double y1, double ctrlx, double ctrly, double x2, double y2) {
		super.setCurve(x1, y1, ctrlx, ctrly, x2, y2);
		update();
	}

	private void update() {
		pp1 = DianaPoint.middleOf(getP1(), getCtrlPoint());
		pp2 = DianaPoint.middleOf(getP2(), getCtrlPoint());
		p3 = DianaPoint.middleOf(pp1, pp2);
	}

	public DianaPoint getP3() {
		return p3;
	}

	public void setP3(DianaPoint aPoint) {
		DianaPoint pp = DianaPoint.middleOf(getP1(), getP2());
		DianaPoint cp = new DianaSegment(pp, aPoint).getScaledPoint(2);
		setCtrlPoint(cp);
	}

	public DianaPoint getPP1() {
		return pp1;
	}

	public DianaPoint getPP2() {
		return pp2;
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		g.useDefaultForegroundStyle();
		g.drawCurve(this);

		// DEBUG
		// g.setDefaultForeground(ForegroundStyle.makeStyle(Color.RED));
		// (buildFlattenPath(0.01)).paint(g);
	}

	@Override
	public DianaQuadCurve transform(AffineTransform t) {
		return new DianaQuadCurve(getP1().transform(t), getCtrlPoint().transform(t), getP2().transform(t));
	}

	@Override
	public List<DianaPoint> getControlPoints() {
		Vector<DianaPoint> returned = new Vector<>();
		returned.add(getP1());
		returned.add(getP2());
		return returned;
	}

	@Override
	public String toString() {
		return "DianaQuadCurve: [" + getP1() + "," + getCtrlPoint() + "," + getP2() + "]";
	}

	@Override
	public boolean containsArea(DianaArea a) {
		if (a instanceof DianaPoint) {
			return containsPoint((DianaPoint) a);
		}
		return false;
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		return false;
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		return super.contains(p);
	}

	@Override
	public DianaArea getAnchorAreaFrom(org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection direction) {
		return clone();
	}

	@Override
	public DianaQuadCurve clone() {
		return (DianaQuadCurve) super.clone();
	}

	private DianaPolylin buildFlattenPath(double flatness) {
		DianaPolylin returned = new DianaPolylin();
		PathIterator p = getPathIterator(null);
		FlatteningPathIterator f = new FlatteningPathIterator(p, flatness);
		while (!f.isDone()) {
			float[] pts = new float[6];
			switch (f.currentSegment(pts)) {
				case PathIterator.SEG_MOVETO:
					// returned.addToPoints(new DianaPoint(pts[0],pts[1]));
				case PathIterator.SEG_LINETO:
					returned.addToPoints(new DianaPoint(pts[0], pts[1]));
			}
			f.next();
		}
		return returned;
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint aPoint) {
		// TODO do something better later
		// logger.warning("Please implement me better later");
		return getApproximatedNearestPoint(aPoint);
	}

	public DianaPoint getApproximatedNearestPoint(DianaPoint aPoint) {
		double minimizedDistance = java.lang.Double.POSITIVE_INFINITY;
		DianaPoint returned = null;
		DianaPolylin flattenPath = buildFlattenPath(FLATTENING_PATH_LEVEL);
		for (DianaSegment s : flattenPath.getSegments()) {
			DianaPoint nearestPoint = s.getNearestPointOnSegment(aPoint);
			double currentDistance = DianaPoint.distance(nearestPoint, aPoint);
			if (currentDistance < minimizedDistance) {
				minimizedDistance = currentDistance;
				returned = nearestPoint;
			}
		}
		return returned;
	}

	@Override
	public DianaArea getOrthogonalPerspectiveArea(org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection orientation) {
		// TODO Auto-generated method stub
		return null;
	}

	public DianaSegment getApproximatedStartTangent() {
		return buildFlattenPath(FLATTENING_PATH_LEVEL).getSegments().firstElement();
	}

	public DianaSegment getApproximatedEndTangent() {
		return buildFlattenPath(FLATTENING_PATH_LEVEL).getSegments().lastElement();
	}

	public DianaSegment getApproximatedControlPointTangent() {
		double minimizedDistance = java.lang.Double.POSITIVE_INFINITY;
		DianaSegment returned = null;
		DianaPolylin flattenPath = buildFlattenPath(FLATTENING_PATH_LEVEL);
		for (DianaSegment s : flattenPath.getSegments()) {
			DianaPoint nearestPoint = s.getNearestPointOnSegment(getP3());
			double currentDistance = DianaPoint.distance(nearestPoint, getP3());
			if (currentDistance < minimizedDistance) {
				minimizedDistance = currentDistance;
				returned = s;
			}
		}
		return returned;
	}

	@Override
	public DianaArea exclusiveOr(DianaArea area) {
		return new DianaExclusiveOrArea(this, area);
	}

	@Override
	public DianaArea intersect(DianaArea area) {
		DianaIntersectionArea returned = new DianaIntersectionArea(this, area);
		if (returned.isDevelopable()) {
			return returned.makeDevelopped();
		}
		else {
			return returned;
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
	public DianaArea substract(DianaArea area, boolean isStrict) {
		return new DianaSubstractionArea(this, area, isStrict);
	}

	@Override
	public String getStringRepresentation() {
		return toString();
	}

	@Override
	public int hashCode() {
		return getP1().hashCode() + getP2().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaQuadCurve) {
			DianaQuadCurve s = (DianaQuadCurve) obj;
			return (getP1().equals(s.getP1()) || getP1().equals(s.getP2())) && (getP2().equals(s.getP1()) || getP2().equals(s.getP2()))
					&& getCtrlPoint().equals(s.getCtrlPoint());
		}
		return false;
	}

	// http://forum.java.sun.com/thread.jspa?threadID=420749&messageID=3752991

	public boolean curveIntersects(QuadCurve2D curve, double rx, double ry, double rw, double rh) {
		/**
		 * A quadratic bezier curve has the following parametric equation:
		 * 
		 * Q(t) = P0(1-t)^2 + P1(2t(1-t)) + P2(t^2)
		 * 
		 * Where 0 <= t <= 1 and P0 is the starting point, P1 is the control point and P2 is the end point.
		 * 
		 * Therefore, the equations for the x and y coordinates are:
		 * 
		 * Qx(t) = Px0(1-t)^2 + Px1(2t(1-t)) + Px2(t^2) Qy(t) = Py0(1-t)^2 + Py1(2t(1-t)) + Py2(t^2)
		 * 
		 * 0 <= t <= 1
		 * 
		 * A bezier curve intersects a rectangle if:
		 * 
		 * 1 - Either one of its endpoints is inside of the rectangle 2 - The curve intersects one of the rectangles sides (top, bottom,
		 * left or right)
		 * 
		 * The equation for a horizontal line is:
		 * 
		 * y = c
		 * 
		 * The line intersects the bezier if:
		 * 
		 * Qy(t) = c and 0 <= t <= 1
		 * 
		 * We can rewrite this as:
		 * 
		 * -c + Py0 + (-2Py0 + 2Py1)t + (Py0 - 2Py1 + Py2) t^2 == 0 and 0 <= t <= 1
		 * 
		 * We can use the valid roots of the quadratic, to evaluate Qx(t), and see if the value falls withing the rectangle bounds.
		 * 
		 * The case for vertical lines is analogous to this one. (juancn)
		 */
		double y1 = curve.getY1();
		double y2 = curve.getY2();
		double x1 = curve.getX1();
		double x2 = curve.getX2();

		// If the rectangle contains one of the endpoints, it intersects the curve
		if (rectangleContains(x1, y1, rx, ry, rw, rh) || rectangleContains(x2, y2, rx, ry, rw, rh)) {
			return true;
		}

		double eqn[] = new double[3];
		double ctrlY = curve.getCtrlY();
		double ctrlX = curve.getCtrlX();

		return intersectsLine(eqn, y1, ctrlY, y2, ry, x1, ctrlX, x2, rx, rx + rw) // Top
				|| intersectsLine(eqn, y1, ctrlY, y2, ry + rh, x1, ctrlX, x2, rx, rx + rw) // Bottom
				|| intersectsLine(eqn, x1, ctrlX, x2, rx, y1, ctrlY, y2, ry, ry + rh) // Left
				|| intersectsLine(eqn, x1, ctrlX, x2, rx + rw, y1, ctrlY, y2, ry, ry + rh); // Right
	}

	private static boolean rectangleContains(double x, double y, double rx, double ry, double rw, double rh) {
		return x >= rx && y >= ry && x < rx + rw && y < ry + rh;
	}

	/**
	 * Returns true if a line segment parallel to one of the axis intersects the specified curve. This function works fine if you reverse
	 * the axes.
	 * 
	 * @param eqn
	 *            a double[] of lenght 3 used to hold the quadratic equation coeficients
	 * @param p0
	 *            starting point of the curve at the desired axis (i.e.: curve.getX1())
	 * @param p1
	 *            control point of the curve at the desired axis (i.e.: curve.getCtrlX())
	 * @param p2
	 *            end point of the curve at the desired axis (i.e.: curve.getX2())
	 * @param c
	 *            where is the line segment (i.e.: in X axis)
	 * @param pb0
	 *            starting point of the curve at the other axis (i.e.: curve.getY1())
	 * @param pb1
	 *            control point of the curve at the other axis (i.e.: curve.getCtrlY())
	 * @param pb2
	 *            end point of the curve at the other axis (i.e.: curve.getY2())
	 * @param from
	 *            starting point of the line segment (i.e.: in Y axis)
	 * @param to
	 *            end point of the line segment (i.e.: in Y axis)
	 * @return
	 */
	private static boolean intersectsLine(double[] eqn, double p0, double p1, double p2, double c, double pb0, double pb1, double pb2,
			double from, double to) {
		/**
		 * First we check if a line parallel to the axis we are evaluating intersects the curve (the line is at c).
		 * 
		 * Then we check if any of the intersection points is between 'from' and 'to' in the other axis (wether it belongs to the rectangle)
		 */

		// Fill the coefficients of the equation
		eqn[2] = p0 - 2 * p1 + p2;
		eqn[1] = 2 * p1 - 2 * p0;
		eqn[0] = p0 - c;

		int nRoots = QuadCurve2D.solveQuadratic(eqn);
		boolean result;
		switch (nRoots) {
			case 1:
				result = eqn[0] >= 0 && eqn[0] <= 1;
				if (result) {
					double intersection = evalQuadraticCurve(pb0, pb1, pb2, eqn[0]);
					result = intersection >= from && intersection <= to;
				}
				break;
			case 2:
				result = eqn[0] >= 0 && eqn[0] <= 1;
				if (result) {
					double intersection = evalQuadraticCurve(pb0, pb1, pb2, eqn[0]);
					result = intersection >= from && intersection <= to;
				}

				// If the first root is not a valid intersection, try the other one
				if (!result) {
					result = eqn[1] >= 0 && eqn[1] <= 1;
					if (result) {
						double intersection = evalQuadraticCurve(pb0, pb1, pb2, eqn[1]);
						result = intersection >= from && intersection <= to;
					}
				}

				break;
			default:
				result = false;
		}
		return result;
	}

	public static double evalQuadraticCurve(double c1, double ctrl, double c2, double t) {
		double u = 1 - t;
		double res = c1 * u * u + 2 * ctrl * t * u + c2 * t * t;

		return res;
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
		Rectangle2D bounds2D = getBounds2D();

		return new DianaRectangle(bounds2D.getX(), bounds2D.getY(), bounds2D.getWidth(), bounds2D.getHeight(), Filling.FILLED);
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
		// TODO: not implemented
		return null;

	}

}
