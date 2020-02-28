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
import java.awt.geom.CubicCurve2D.Double;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
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
public class DianaCubicCurve extends Double implements DianaGeneralShape.GeneralShapePathElement<DianaCubicCurve> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DianaCubicCurve.class.getPackage().getName());

	/**
	 * This value is internally used to compute approximated data (nearest point, distance, etc...)
	 */
	private static final double FLATTENING_PATH_LEVEL = 0.01;

	public DianaCubicCurve() {
		super();
	}

	public DianaCubicCurve(DianaPoint p1, DianaPoint ctrlP1, DianaPoint ctrlP2, DianaPoint p2) {
		super();
		setCurve(p1.x, p1.y, ctrlP1.x, ctrlP1.y, ctrlP2.x, ctrlP2.y, p2.x, p2.y);
	}

	@Override
	public DianaPoint getP1() {
		return new DianaPoint(x1, y1);
	}

	public void setP1(DianaPoint aPoint) {
		x1 = aPoint.x;
		y1 = aPoint.y;
	}

	@Override
	public DianaPoint getP2() {
		return new DianaPoint(x2, y2);
	}

	public void setP2(DianaPoint aPoint) {
		x2 = aPoint.x;
		y2 = aPoint.y;
	}

	@Override
	public DianaPoint getCtrlP1() {
		return new DianaPoint(ctrlx1, ctrly1);
	}

	public void setCtrlP1(DianaPoint aPoint) {
		ctrlx1 = aPoint.x;
		ctrly1 = aPoint.y;
	}

	@Override
	public DianaPoint getCtrlP2() {
		return new DianaPoint(ctrlx2, ctrly2);
	}

	public void setCtrlP2(DianaPoint aPoint) {
		ctrlx2 = aPoint.x;
		ctrly2 = aPoint.y;
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		g.useDefaultForegroundStyle();
		g.drawCurve(this);
	}

	@Override
	public DianaCubicCurve transform(AffineTransform t) {
		return new DianaCubicCurve(getP1().transform(t), getCtrlP1().transform(t), getCtrlP2().transform(t), getP2().transform(t));
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
		return "DianaCubicCurve: [" + getP1() + "," + getCtrlP1() + "," + getCtrlP2() + "," + getP2() + "]";
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
	public DianaCubicCurve clone() {
		return (DianaCubicCurve) super.clone();
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
	public DianaPoint nearestOutlinePoint(DianaPoint p) {
		return getApproximatedNearestPoint(p);
	}

	@Override
	public DianaArea getOrthogonalPerspectiveArea(org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection orientation) {
		return null;
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
		return getP1().hashCode() + getP2().hashCode() + getCtrlP1().hashCode() + getCtrlP2().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaCubicCurve) {
			DianaCubicCurve s = (DianaCubicCurve) obj;
			return getP1().equals(s.getP1()) && getP2().equals(s.getP2()) && getCtrlP1().equals(s.getCtrlP1())
					&& getCtrlP2().equals(s.getCtrlP2())
					|| getP1().equals(s.getP2()) && getP2().equals(s.getP1()) && getCtrlP1().equals(s.getCtrlP2())
							&& getCtrlP2().equals(s.getCtrlP1());
		}
		return false;
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

	public DianaSegment getApproximatedStartTangent() {
		return buildFlattenPath(FLATTENING_PATH_LEVEL).getSegments().firstElement();
	}

	public DianaSegment getApproximatedEndTangent() {
		return buildFlattenPath(FLATTENING_PATH_LEVEL).getSegments().lastElement();
	}

	public DianaSegment getApproximatedTangent(DianaPoint point) {
		DianaPoint pt = getNearestPoint(point);
		double minimizedDistance = java.lang.Double.POSITIVE_INFINITY;
		DianaSegment returned = null;
		DianaPolylin flattenPath = buildFlattenPath(FLATTENING_PATH_LEVEL);
		for (DianaSegment s : flattenPath.getSegments()) {
			DianaPoint nearestPoint = s.getNearestPointOnSegment(pt);
			double currentDistance = DianaPoint.distance(nearestPoint, pt);
			if (currentDistance < minimizedDistance) {
				minimizedDistance = currentDistance;
				returned = s;
			}
		}
		return returned;
	}

	public DianaPoint getMiddle() {
		return getPointAtRelativePosition(0.5);
	}

	public DianaPoint getPointAtRelativePosition(double position) {
		DianaPolylin flattenPath = buildFlattenPath(FLATTENING_PATH_LEVEL);
		DianaPoint pointAtRelativePosition = flattenPath.getPointAtRelativePosition(position);
		return getNearestPoint(pointAtRelativePosition);
	}

}
