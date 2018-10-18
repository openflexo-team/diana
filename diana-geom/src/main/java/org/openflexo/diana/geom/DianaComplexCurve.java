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
import java.awt.geom.QuadCurve2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Represents a complex curve, with at least 2 points<br>
 * This complex curve is defined by a list of points linked with bezier curves, depending on defined closure.<br>
 * 
 * @author sylvain
 * 
 */
public class DianaComplexCurve extends DianaGeneralShape<DianaComplexCurve> {

	private static final Logger logger = Logger.getLogger(DianaComplexCurve.class.getPackage().getName());

	private Vector<DianaPoint> _points;

	// FD : unused
	// private DianaRectangle bounds;

	public DianaComplexCurve(Closure closure) {
		super(closure);
		_points = new Vector<>();
	}

	public DianaComplexCurve() {
		this(Closure.OPEN_NOT_FILLED);
	}

	public DianaComplexCurve(Closure closure, List<DianaPoint> points) {
		this(closure);
		if (points != null) {
			for (DianaPoint p : points) {
				_addToPoints(p);
			}
			updateCurve();
		}
	}

	public DianaComplexCurve(Closure closure, DianaPoint... points) {
		this(closure);
		for (DianaPoint p : points) {
			_addToPoints(p);
		}
		updateCurve();
	}

	public void clearPoints() {
		_points.clear();
	}

	public Vector<DianaPoint> getPoints() {
		return _points;
	}

	public void setPoints(Vector<DianaPoint> points) {
		_points.clear();
		for (DianaPoint p : points) {
			addToPoints(p);
		}
	}

	private void _addToPoints(DianaPoint p) {
		_points.add(p);
	}

	public void addToPoints(DianaPoint p) {
		_points.add(p);
		updateCurve();
		// reCalculateBounds();
	}

	public int getPointsNb() {
		return _points.size();
	}

	public DianaPoint getPointAt(int index) {
		return _points.elementAt(index);
	}

	private void updateCurve() {
		if (_points.size() == 2) {
			beginAtPoint(_points.get(0));
			addSegment(_points.get(1));

		}
		else if (_points.size() > 2) {
			if (getClosure() == Closure.OPEN_FILLED || getClosure() == Closure.OPEN_NOT_FILLED) {
				updateAsOpenedCurve();
			}
			else if (getClosure() == Closure.CLOSED_FILLED || getClosure() == Closure.CLOSED_NOT_FILLED) {
				updateAsClosedCurve();
			}
			else {
				logger.warning("unexpected closure:" + getClosure());
			}
		}
	}

	private void updateAsOpenedCurve() {
		if (_points.size() > 2) {
			for (int i = 0; i < _points.size(); i++) {
				DianaPoint current = _points.get(i);
				if (i == 0) {
					beginAtPoint(current);
				}
				else if (i == 1) {
					// First segment: quadratic curve
					DianaQuadCurve curve = DianaQuadCurve.makeCurveFromPoints(_points.get(0), _points.get(1), _points.get(2));
					QuadCurve2D left = new QuadCurve2D.Double();
					QuadCurve2D right = new QuadCurve2D.Double();
					curve.subdivide(left, right);
					addQuadCurve(left.getCtrlPt(), left.getP2());
				}
				else if (i == _points.size() - 1) {
					// Last segment: quadratic curve
					DianaQuadCurve curve = DianaQuadCurve.makeCurveFromPoints(_points.get(i - 2), _points.get(i - 1), _points.get(i));
					QuadCurve2D left = new QuadCurve2D.Double();
					QuadCurve2D right = new QuadCurve2D.Double();
					curve.subdivide(left, right);
					addQuadCurve(right.getCtrlPt(), right.getP2());
				}
				else {
					// Cubic segment
					DianaQuadCurve leftCurve = DianaQuadCurve.makeCurveFromPoints(_points.get(i - 2), _points.get(i - 1), _points.get(i));
					DianaQuadCurve rightCurve = DianaQuadCurve.makeCurveFromPoints(_points.get(i - 1), _points.get(i), _points.get(i + 1));
					/*DianaCubicCurve curve = new DianaCubicCurve(
							_points.get(i-1),
							rightCurve.getPP1(),
							leftCurve.getPP2(),
							_points.get(i));*/
					addCubicCurve(leftCurve.getPP2(), rightCurve.getPP1(), _points.get(i));
				}
			}
		}
	}

	private void updateAsClosedCurve() {
		if (_points.size() > 2) {
			for (int i = 0; i < _points.size() + 1; i++) {
				// DianaPoint current = _points.get(i);
				if (i == 0) {
					beginAtPoint(_points.get(0));
				}

				// Cubic segment
				DianaQuadCurve leftCurve = DianaQuadCurve.makeCurveFromPoints(getPointAtIndex(i - 2), getPointAtIndex(i - 1),
						getPointAtIndex(i));
				DianaQuadCurve rightCurve = DianaQuadCurve.makeCurveFromPoints(getPointAtIndex(i - 1), getPointAtIndex(i),
						getPointAtIndex(i + 1));
				/*DianaCubicCurve curve = new DianaCubicCurve(
						_points.get(i-1),
						rightCurve.getPP1(),
						leftCurve.getPP2(),
						_points.get(i));*/
				if (i > 0) {
					addCubicCurve(leftCurve.getPP2(), rightCurve.getPP1(), getPointAtIndex(i));
				}
			}
		}
	}

	// Implements circular index
	private DianaPoint getPointAtIndex(int i) {
		if (i < 0) {
			return getPointAt(i + _points.size());
		}
		else if (i >= _points.size()) {
			return getPointAt(i - _points.size());
		}
		else {
			return _points.get(i);
		}
	}

	public void geometryChanged() {
		updateCurve();
		// reCalculateBounds();
	}

	/*private void reCalculateBounds() {
		double boundsMinX = Double.POSITIVE_INFINITY;
		double boundsMinY = Double.POSITIVE_INFINITY;
		double boundsMaxX = Double.NEGATIVE_INFINITY;
		double boundsMaxY = Double.NEGATIVE_INFINITY;
	
		for (int i = 0; i < getPointsNb(); i++) {
			DianaPoint p = getPointAt(i);
			double x = p.getX();
			boundsMinX = Math.min(boundsMinX, x);
			boundsMaxX = Math.max(boundsMaxX, x);
			double y = p.getY();
			boundsMinY = Math.min(boundsMinY, y);
			boundsMaxY = Math.max(boundsMaxY, y);
		}
		bounds = new DianaRectangle(boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY, getIsFilled() ? Filling.FILLED
				: Filling.NOT_FILLED);
	}*/

	/*@Override
	public DianaRectangle getBoundingBox() {
		if (bounds == null) {
			reCalculateBounds();
		}
		return bounds;
	}*/

	/*@Override
	public DianaPoint getCenter() {
		return getBoundingBox().getCenter();
	}*/

	@Override
	public DianaComplexCurve transform(AffineTransform t) {
		Vector<DianaPoint> points = new Vector<>();
		for (DianaPoint p : _points) {
			points.add(p.transform(t));
		}
		DianaComplexCurve returned = new DianaComplexCurve(getClosure(), points);
		returned.setForeground(getForeground());
		returned.setBackground(getBackground());
		return returned;
	}

}
