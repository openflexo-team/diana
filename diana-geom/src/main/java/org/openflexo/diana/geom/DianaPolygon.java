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

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Random;
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

/**
 * The <code>DianaPolygon</code> class encapsulates a description of a closed, two-dimensional region within a coordinate space. This region
 * is bounded by an arbitrary number of line segments, each of which is one side of the polygon.
 * 
 * Some parts of this code are "inspired" from java.awt.Polygon implementation
 * 
 * @author sylvain
 * 
 */
public class DianaPolygon implements DianaShape<DianaPolygon> {

	private static final Logger logger = Logger.getLogger(DianaPolygon.class.getPackage().getName());

	protected Filling _filling;

	protected Vector<DianaPoint> _points;
	protected Vector<DianaSegment> _segments;

	private DianaRectangle bounds;

	public DianaPolygon() {
		this(Filling.NOT_FILLED);
	}

	public DianaPolygon(Filling filling) {
		super();
		_filling = filling;
		_points = new Vector<>();
		_segments = new Vector<>();
	}

	public DianaPolygon(Filling filling, List<DianaPoint> points) {
		this(filling);
		if (points != null) {
			for (DianaPoint p : points) {
				addToPoints(p);
			}
		}
	}

	public DianaPolygon(Filling filling, DianaPoint... points) {
		this(filling);
		for (DianaPoint p : points) {
			addToPoints(p);
		}
	}

	@Override
	public boolean getIsFilled() {
		return _filling == Filling.FILLED;
	}

	@Override
	public void setIsFilled(boolean filled) {
		_filling = filled ? Filling.FILLED : Filling.NOT_FILLED;
	}

	@Override
	public DianaPoint getCenter() {
		if (_points.size() == 0) {
			return new DianaPoint(0, 0);
		}

		double sumX = 0;
		double sumY = 0;

		for (DianaPoint p : _points) {
			sumX += p.x;
			sumY += p.y;
		}
		return new DianaPoint(sumX / _points.size(), sumY / _points.size());
	}

	@Override
	public List<DianaPoint> getControlPoints() {
		return getPoints();
	}

	public void clearPoints() {
		_points.clear();
		_segments.clear();
	}

	public Vector<DianaPoint> getPoints() {
		return _points;
	}

	public void setPoints(Vector<DianaPoint> points) {
		_points.clear();
		_segments.clear();
		for (DianaPoint p : points) {
			addToPoints(p);
		}
	}

	public void addToPoints(DianaPoint aPoint) {
		_points.add(aPoint);
		if (_points.size() > 1) {
			DianaSegment s2 = new DianaSegment(_points.elementAt(_points.size() - 2), _points.elementAt(_points.size() - 1));
			if (_segments.size() <= _points.size() - 2) {
				_segments.add(s2);
			}
			else {
				_segments.set(_points.size() - 2, s2);
			}
			DianaSegment s3 = new DianaSegment(_points.elementAt(_points.size() - 1), _points.elementAt(0));
			_segments.add(s3);
		}
		reCalculateBounds();
	}

	public void removeFromPoints(DianaPoint aPoint) {
		_points.remove(aPoint);
		reCalculateBounds();
	}

	public void updateSegmentsFromPoints() {
		_segments.clear();
		int index = 0;
		for (index = 0; index < _points.size(); index++) {
			if (index > 0) {
				DianaSegment s2 = new DianaSegment(_points.elementAt(index - 1), _points.elementAt(index));
				_segments.add(s2);
			}
		}
		DianaSegment s3 = new DianaSegment(_points.elementAt(_points.size() - 1), _points.elementAt(0));
		_segments.add(s3);
		reCalculateBounds();
	}

	public Vector<DianaSegment> getSegments() {
		return _segments;
	}

	public int getPointsNb() {
		return _points.size();
	}

	public DianaPoint getPointAt(int index) {
		return _points.elementAt(index);
	}

	public void geometryChanged() {
		reCalculateBounds();
	}

	private void reCalculateBounds() {
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
		bounds = new DianaRectangle(boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY, _filling);
	}

	@Override
	public DianaRectangle getBoundingBox() {
		return bounds;
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		if (l instanceof DianaHalfLine) {
			return false;
		}
		if (l instanceof DianaSegment) {
			return containsPoint(l.getP1()) && containsPoint(l.getP2());
		}
		return false;
	}

	@Override
	public boolean contains(double x, double y) {
		DianaPoint pt = new DianaPoint(x, y);
		for (DianaSegment s : getSegments()) {
			if (s.contains(pt)) {
				return true;
			}
		}

		if (!getIsFilled()) {
			return false;
		}

		// Otherwise test on inside

		if (getPointsNb() <= 2 || !bounds.contains(x, y)) {
			return false;
		}
		int hits = 0;

		DianaPoint lastPoint = getPointAt(getPointsNb() - 1);

		double lastx = lastPoint.getX();
		double lasty = lastPoint.getY();

		DianaPoint currentPoint;
		double curx, cury;

		// Walk the edges of the polygon
		for (int i = 0; i < getPointsNb(); lastx = curx, lasty = cury, i++) {

			currentPoint = getPointAt(i);

			curx = currentPoint.getX();
			cury = currentPoint.getY();

			if (cury == lasty) {
				continue;
			}

			double leftx;
			if (curx < lastx) {
				if (x >= lastx) {
					continue;
				}
				leftx = curx;
			}
			else {
				if (x >= curx) {
					continue;
				}
				leftx = lastx;
			}

			double test1, test2;
			if (cury < lasty) {
				if (y < cury || y >= lasty) {
					continue;
				}
				if (x < leftx) {
					hits++;
					continue;
				}
				test1 = x - curx;
				test2 = y - cury;
			}
			else {
				if (y < lasty || y >= cury) {
					continue;
				}
				if (x < leftx) {
					hits++;
					continue;
				}
				test1 = x - lastx;
				test2 = y - lasty;
			}

			if (test1 < test2 / (lasty - cury) * (lastx - curx)) {
				hits++;
			}
		}

		return (hits & 1) != 0;
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
	public DianaPolygon clone() {
		try {
			return (DianaPolygon) super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint aPoint) {
		return nearestOutlinePoint(aPoint);
	}

	@Override
	public DianaPoint nearestOutlinePoint(DianaPoint aPoint) {
		DianaPoint returnedPoint = null;
		double smallestDistance = Double.POSITIVE_INFINITY;

		for (DianaSegment segment : _segments) {
			double sqDistanceToSegment = segment.ptSegDistSq(aPoint);
			if (sqDistanceToSegment < smallestDistance) {
				returnedPoint = segment.getNearestPointOnSegment(aPoint);
				smallestDistance = sqDistanceToSegment;
			}
		}
		return returnedPoint;
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

		DianaPoint returned = null;
		double minimalDistanceSq = Double.POSITIVE_INFINITY;

		for (DianaSegment segment : _segments) {
			if (DianaSegment.intersectsInsideSegment(segment, hl)) {
				try {
					DianaPoint p = DianaAbstractLine.getLineIntersection(segment, hl);
					double distSq = DianaPoint.distanceSq(from, p);
					if (distSq < minimalDistanceSq) {
						returned = p;
						minimalDistanceSq = distSq;
					}
				} catch (ParallelLinesException e) {
					// Don't care
				}
			}
		}
		// logger.info("from: "+from+" orientation="+orientation+" return "+returned);
		return returned;
	}

	private DianaArea computeAreaIntersection(DianaArea area) {
		// System.out.println("Intersection between "+this+" and "+area);

		boolean fullyInside = true;
		boolean fullyOutside = true;

		for (DianaSegment s : getSegments()) {
			if (!area.containsArea(s)) {
				fullyInside = false;
			}
			if (area.containsPoint(s.getP1()) || area.containsPoint(s.getP2())) {
				fullyOutside = false;
			}
		}

		if (fullyOutside) {
			// System.out.println("Fully outside");
			return new DianaEmptyArea();
		}

		if (fullyInside) {
			// System.out.println("Fully inside");
			return this.clone();
		}

		// Otherwise non null intersection

		DianaRectangle filledBoundingBox = getBoundingBox().clone();
		filledBoundingBox.setIsFilled(true);
		DianaArea boundingIntersect = area.intersect(filledBoundingBox);

		// System.out.println("Y'a une intersection zarrebi: boundingIntersect="+boundingIntersect+" resultat:
		// "+intersect(boundingIntersect));

		return intersect(boundingIntersect);

	}

	private DianaArea computeLineIntersection(DianaAbstractLine<?> line) {
		Vector<DianaPoint> crossed = new Vector<>();
		for (DianaSegment s : _segments) {
			if (line.overlap(s)) {
				return s.clone(); // TODO: perform union of potential multiple overlaping segments
			}
			try {
				if (s.intersectsInsideSegment(line)) {
					DianaPoint intersection = s.getLineIntersection(line);
					if (line.contains(intersection) && (crossed.size() == 0 || !crossed.lastElement().equals(intersection))) {
						crossed.add(intersection);
					}
				}
			} catch (ParallelLinesException e) {
				// don't care
			}
		}

		if (crossed.size() == 0) {
			return new DianaEmptyArea();
		}

		if (crossed.size() == 1) {
			return crossed.firstElement();
		}
		else if (crossed.size() == 2) {
			if (getIsFilled()) {
				return new DianaSegment(crossed.firstElement(), crossed.elementAt(1));
			}
			else {
				return DianaUnionArea.makeUnion(crossed.firstElement(), crossed.elementAt(1));
			}
		}
		else {
			// TODO: not yet implemented for filled polygon
			logger.warning("computeLineIntersection() not yet implemented for polygon");
			return DianaUnionArea.makeUnion(crossed);
		}

	}

	@Override
	public DianaArea exclusiveOr(DianaArea area) {
		return new DianaExclusiveOrArea(this, area);
	}

	@Override
	public DianaArea intersect(DianaArea area) {
		// logger.info("Polygon "+this+" intersect with "+area);

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
			return ((DianaRectangle) area).intersect(this);
		}
		if (area instanceof DianaHalfPlane) {
			return ((DianaHalfPlane) area).intersect(this);
		}
		if (area instanceof DianaPolygon) {
			return AreaComputation.computeShapeIntersection(this, (DianaPolygon) area);
		}
		if (area instanceof DianaBand) {
			return computeAreaIntersection(area);
		}
		if (area instanceof DianaHalfBand) {
			return computeAreaIntersection(area);
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
	public boolean containsPoint(DianaPoint p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean containsArea(DianaArea a) {
		if (a instanceof DianaPoint) {
			return containsPoint((DianaPoint) a);
		}
		if (a instanceof DianaSegment) {
			return containsPoint(((DianaSegment) a).getP1()) && containsPoint(((DianaSegment) a).getP2());
		}
		if (a instanceof DianaShape) {
			return AreaComputation.isShapeContainedInArea((DianaShape<?>) a, this);
		}
		return false;
	}

	@Override
	public DianaPolygon transform(AffineTransform t) {
		Vector<DianaPoint> points = new Vector<>();
		for (DianaPoint p : _points) {
			points.add(p.transform(t));
		}
		DianaPolygon returned = new DianaPolygon(_filling, points);
		return returned;
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		if (getIsFilled()) {
			g.useDefaultBackgroundStyle();
			g.fillPolygon(getPoints().toArray(new DianaPoint[getPoints().size()]));
		}
		g.useDefaultForegroundStyle();
		g.drawPolygon(getPoints().toArray(new DianaPoint[getPoints().size()]));
	}

	// Alternative implementation
	/*@Override
	public void paint(AbstractFGEGraphics g) {
		g.useDefaultForegroundStyle();
		for (FGESegment s : _segments) {
			s.paint(g);
		}
	}*/

	@Override
	public String toString() {
		return "DianaPolygon: " + _points;
	}

	@Override
	public String getStringRepresentation() {
		return toString();
	}

	// TODO: this algorithm is really not optimal since we explore all solution with a combinational algorithm !!!
	// As we stay with a small number of points, we keep it for now
	private static List<DianaPoint> sortToAvoidCuts(List<DianaPoint> aList) {
		for (DianaPoint p : aList) {
			if (Double.isNaN(p.x) || p.x == Double.POSITIVE_INFINITY || p.x == Double.NEGATIVE_INFINITY) {
				return aList; // On laisse tomber
			}
			if (Double.isNaN(p.y) || p.y == Double.POSITIVE_INFINITY || p.y == Double.NEGATIVE_INFINITY) {
				return aList; // On laisse tomber
			}
		}

		return sortToAvoidCuts(new Vector<DianaPoint>(), aList);
	}

	private static List<DianaPoint> sortToAvoidCuts(List<DianaPoint> aList, List<DianaPoint> remainingPoints) {
		if (remainingPoints.size() == 0) {
			return aList;
		}

		for (DianaPoint newP : remainingPoints) {
			Vector<DianaSegment> sl = new Vector<>();
			DianaPoint previous = null;
			for (DianaPoint p : aList) {
				if (previous != null) {
					sl.add(new DianaSegment(previous, p));
				}
				previous = p;
			}
			boolean thisPointMightBeGood = true;
			if (sl.size() > 0) {
				// System.out.println("Segments = "+sl);
				DianaSegment newSegment = new DianaSegment(aList.get(aList.size() - 1), newP);
				for (DianaSegment oldS : sl) {
					if (oldS.intersectsInsideSegment(newSegment, true)) {
						thisPointMightBeGood = false;
						// System.out.println("Failed because new segment "+newSegment+" intersect with segment "+oldS);
					}
				}
				if (remainingPoints.size() == 1) {
					// This is the last point, we must also check closure
					DianaSegment closure = new DianaSegment(newP, aList.get(0));
					// System.out.println("Also check closure = "+closure);
					for (DianaSegment oldS : sl) {
						if (oldS.intersectsInsideSegment(closure, true)) {
							thisPointMightBeGood = false;
							// System.out.println("Failed because closure "+closure+" intersect with segment "+oldS);
						}
					}
				}
			}
			if (thisPointMightBeGood) {
				Vector<DianaPoint> newList = new Vector<>();
				newList.addAll(aList);
				newList.add(newP);
				Vector<DianaPoint> newRemainingList = new Vector<>();
				newRemainingList.addAll(remainingPoints);
				newRemainingList.remove(newP);
				List<DianaPoint> returned = sortToAvoidCuts(newList, newRemainingList);
				if (returned != null) {
					// System.out.println("return "+returned);
					return returned;
				}
			}
		}

		// No point found, return
		return null;
	}

	public static void main(String[] args) {

		for (int n = 1; n < 100; n++) {
			Vector<DianaPoint> pts = new Vector<>();

			logger.info("n=" + n);
			Random rand = new Random();

			for (int i = 0; i < n; i++) {
				pts.add(new DianaPoint(rand.nextDouble(), rand.nextDouble()));
			}

			logger.info("resultat: " + sortToAvoidCuts(pts));
		}
	}

	/**
	 * Make a new area given a list of point May return a rectangle, a polygon or a segment
	 * 
	 * @param filling
	 * @param points
	 * @return
	 */
	public static DianaArea makeArea(Filling filling, List<DianaPoint> somePoints) {
		if (somePoints.size() < 1) {
			throw new IllegalArgumentException("makeArea() called with " + somePoints.size() + " points");
		}
		else if (somePoints.size() == 1) {
			return new DianaPoint(somePoints.get(0));
		}
		else if (somePoints.size() == 2) {
			return new DianaSegment(somePoints.get(0), somePoints.get(1));
		}
		else {
			List<DianaPoint> points = sortToAvoidCuts(somePoints);

			if (points.size() == 4) {
				boolean isRectangle = true;
				double minx = Double.POSITIVE_INFINITY;
				double miny = Double.POSITIVE_INFINITY;
				double maxx = Double.NEGATIVE_INFINITY;
				double maxy = Double.NEGATIVE_INFINITY;
				for (int i = 0; i < points.size(); i++) {
					if (points.get(i).x < minx) {
						minx = points.get(i).x;
					}
					if (points.get(i).y < miny) {
						miny = points.get(i).y;
					}
					if (points.get(i).x > maxx) {
						maxx = points.get(i).x;
					}
					if (points.get(i).y > maxy) {
						maxy = points.get(i).y;
					}
				}
				for (int i = 0; i < points.size(); i++) {
					DianaPoint p = points.get(i);
					if (!((p.x == minx || p.x == maxx) && (p.y == miny || p.y == maxy))) {
						isRectangle = false;
					}
				}
				if (isRectangle) {
					if (maxx - minx == 0) { // width = 0
						if (maxy - miny == 0) {
							return new DianaPoint(minx, miny);
						}
						else {
							return new DianaSegment(minx, miny, minx, maxy);
						}
					}
					else {
						if (maxy - miny == 0) {
							return new DianaSegment(minx, miny, maxx, miny); // height = 0;
						}
						else {
							return new DianaRectangle(minx, miny, maxx - minx, maxy - miny, filling);
						}
					}
				}
			}

			return new DianaPolygon(filling, points);
		}
	}

	public static DianaArea makeArea(Filling filling, DianaPoint... points) {
		Vector<DianaPoint> v = new Vector<>();
		for (DianaPoint p : points) {
			v.add(p);
		}
		return makeArea(filling, v);
	}

	@Override
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		if (_points.size() <= 0 || !bounds.intersects(x, y, w, h)) {
			return false;
		}

		// Implement this;
		// Crossings cross = getCrossings(x, y, x+w, y+h);
		// return (cross != null && cross.covers(y, y+h));

		return true;
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		// TODO Implement this

		// Crossings cross = getCrossings(x, y, x+w, y+h);
		// return (cross == null || !cross.isEmpty());

		return false;
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());
	}

	@Override
	public Rectangle2D getBounds2D() {
		return getBounds();
	}

	/**
	 * Returns an iterator object that iterates along the boundary of this <code>Polygon</code> and provides access to the geometry of the
	 * outline of this <code>Polygon</code>. An optional {@link AffineTransform} can be specified so that the coordinates returned in the
	 * iteration are transformed accordingly.
	 * 
	 * @param at
	 *            an optional <code>AffineTransform</code> to be applied to the coordinates as they are returned in the iteration, or
	 *            <code>null</code> if untransformed coordinates are desired
	 * @return a {@link PathIterator} object that provides access to the geometry of this <code>Polygon</code>.
	 */
	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return new PolygonPathIterator(this, at);
	}

	/**
	 * Returns an iterator object that iterates along the boundary of the <code>ShapeSpecification</code> and provides access to the
	 * geometry of the outline of the <code>ShapeSpecification</code>. Only SEG_MOVETO, SEG_LINETO, and SEG_CLOSE point types are returned
	 * by the iterator. Since polygons are already flat, the <code>flatness</code> parameter is ignored. An optional
	 * <code>AffineTransform</code> can be specified in which case the coordinates returned in the iteration are transformed accordingly.
	 * 
	 * @param at
	 *            an optional <code>AffineTransform</code> to be applied to the coordinates as they are returned in the iteration, or
	 *            <code>null</code> if untransformed coordinates are desired
	 * @param flatness
	 *            the maximum amount that the control points for a given curve can vary from colinear before a subdivided curve is replaced
	 *            by a straight line connecting the endpoints. Since polygons are already flat the <code>flatness</code> parameter is
	 *            ignored.
	 * @return a <code>PathIterator</code> object that provides access to the <code>ShapeSpecification</code> object's geometry.
	 */
	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return getPathIterator(at);
	}

	class PolygonPathIterator implements PathIterator {
		DianaPolygon poly;
		AffineTransform transform;
		int index;

		public PolygonPathIterator(DianaPolygon pg, AffineTransform at) {
			poly = pg;
			transform = at;
			if (pg.getPointsNb() == 0) {
				// Prevent a spurious SEG_CLOSE segment
				index = 1;
			}
		}

		/**
		 * Returns the winding rule for determining the interior of the path.
		 * 
		 * @return an integer representing the current winding rule.
		 * @see PathIterator#WIND_NON_ZERO
		 */
		@Override
		public int getWindingRule() {
			return WIND_EVEN_ODD;
		}

		/**
		 * Tests if there are more points to read.
		 * 
		 * @return <code>true</code> if there are more points to read; <code>false</code> otherwise.
		 */
		@Override
		public boolean isDone() {
			return index > poly.getPointsNb();
		}

		/**
		 * Moves the iterator forwards, along the primary direction of traversal, to the next segment of the path when there are more points
		 * in that direction.
		 */
		@Override
		public void next() {
			index++;
		}

		/**
		 * Returns the coordinates and type of the current path segment in the iteration. The return value is the path segment type:
		 * SEG_MOVETO, SEG_LINETO, or SEG_CLOSE. A <code>float</code> array of length 2 must be passed in and can be used to store the
		 * coordinates of the point(s). Each point is stored as a pair of <code>float</code> x,&nbsp;y coordinates. SEG_MOVETO and
		 * SEG_LINETO types return one point, and SEG_CLOSE does not return any points.
		 * 
		 * @param coords
		 *            a <code>float</code> array that specifies the coordinates of the point(s)
		 * @return an integer representing the type and coordinates of the current path segment.
		 * @see PathIterator#SEG_MOVETO
		 * @see PathIterator#SEG_LINETO
		 * @see PathIterator#SEG_CLOSE
		 */
		@Override
		public int currentSegment(float[] coords) {
			if (index >= poly.getPointsNb()) {
				return SEG_CLOSE;
			}
			DianaPoint p = poly.getPointAt(index);
			coords[0] = (float) p.x;
			coords[1] = (float) p.y;
			if (transform != null) {
				transform.transform(coords, 0, coords, 0, 1);
			}
			return index == 0 ? SEG_MOVETO : SEG_LINETO;
		}

		/**
		 * Returns the coordinates and type of the current path segment in the iteration. The return value is the path segment type:
		 * SEG_MOVETO, SEG_LINETO, or SEG_CLOSE. A <code>double</code> array of length 2 must be passed in and can be used to store the
		 * coordinates of the point(s). Each point is stored as a pair of <code>double</code> x,&nbsp;y coordinates. SEG_MOVETO and
		 * SEG_LINETO types return one point, and SEG_CLOSE does not return any points.
		 * 
		 * @param coords
		 *            a <code>double</code> array that specifies the coordinates of the point(s)
		 * @return an integer representing the type and coordinates of the current path segment.
		 * @see PathIterator#SEG_MOVETO
		 * @see PathIterator#SEG_LINETO
		 * @see PathIterator#SEG_CLOSE
		 */
		@Override
		public int currentSegment(double[] coords) {
			if (index >= poly.getPointsNb()) {
				return SEG_CLOSE;
			}
			DianaPoint p = poly.getPointAt(index);
			coords[0] = (float) p.x;
			coords[1] = (float) p.y;
			if (transform != null) {
				transform.transform(coords, 0, coords, 0, 1);
			}
			return index == 0 ? SEG_MOVETO : SEG_LINETO;
		}
	}

	@Override
	public int hashCode() {
		int res = 27;
		for (int j = 0; j < getPointsNb(); j++) {
			res += getPointAt(j).hashCode(); // commute, order does not matter
		}
		return res;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaPolygon) {
			DianaPolygon p = (DianaPolygon) obj;
			if (getPointsNb() != p.getPointsNb()) {
				return false;
			}
			if (getIsFilled() != p.getIsFilled()) {
				return false;
			}
			// Test same order with different indexes
			for (int j = 0; j < getPointsNb(); j++) {
				boolean testWithIndexJ = true;
				for (int i = 0; i < getPointsNb(); i++) {
					int k = i + j;
					if (k >= getPointsNb()) {
						k = k - getPointsNb();
					}
					if (!getPointAt(i).equals(p.getPointAt(k))) {
						testWithIndexJ = false;
					}
				}
				if (testWithIndexJ) {
					return true;
				}
			}
			// Test reverse order with different indexes
			for (int j = 0; j < getPointsNb(); j++) {
				boolean testWithIndexJ = true;
				for (int i = 0; i < getPointsNb(); i++) {
					int k = -i + j;
					if (k < 0) {
						k = k + getPointsNb();
					}
					if (!getPointAt(i).equals(p.getPointAt(k))) {
						testWithIndexJ = false;
					}
				}
				if (testWithIndexJ) {
					return true;
				}
			}
			return false;
		}
		return super.equals(obj);
	}

	@Override
	public DianaArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		return getAnchorAreaFrom(orientation).getOrthogonalPerspectiveArea(orientation);
	}

	@Override
	public DianaArea getAnchorAreaFrom(SimplifiedCardinalDirection orientation) {
		// This algorithm is not quite correct, you can find *very* pathologic cases, but works in most cases

		Vector<DianaSegment> keptSegments = new Vector<>();

		for (DianaSegment s : getSegments()) {
			DianaHalfLine hl = DianaHalfLine.makeHalfLine(s.getMiddle(), orientation);
			/*switch (orientation) {
			case NORTH:
				hl = new DianaHalfLine(s.getMiddle(),s.getMiddle().transform(AffineTransform.getTranslateInstance(0,-1)));
				break;
			case SOUTH:
				hl = new DianaHalfLine(s.getMiddle(),s.getMiddle().transform(AffineTransform.getTranslateInstance(0,1)));
				break;
			case EAST:
				hl = new DianaHalfLine(s.getMiddle(),s.getMiddle().transform(AffineTransform.getTranslateInstance(1,0)));
				break;
			case WEST:
				hl = new DianaHalfLine(s.getMiddle(),s.getMiddle().transform(AffineTransform.getTranslateInstance(-1,0)));
				break;
			default:
				break;
			}*/
			// Test if this half-line "cuts" an other segment
			boolean cutsAnOtherSegment = false;
			for (DianaSegment s2 : getSegments()) {
				if (!s.equals(s2)) {
					DianaArea intersect = s2.intersect(hl);
					if (intersect instanceof DianaPoint) {
						cutsAnOtherSegment = true;
					}
					else if (intersect instanceof DianaEmptyArea) {
						;
					}
					else {
						logger.warning("Unexpected intersection: " + intersect);
						cutsAnOtherSegment = true;
					}
				}
			}
			if (!cutsAnOtherSegment) {
				keptSegments.add(s);
			}
		}

		if (keptSegments.size() == 0) {
			return new DianaEmptyArea();
		}

		else if (keptSegments.size() == 1) {
			return keptSegments.firstElement();
		}

		else {
			// Chains segments
			Vector<DianaSegment> chain = new Vector<>();
			for (DianaSegment s : keptSegments) {
				if (chain.size() == 0) {
					chain.add(s);
				}
				else {
					if (s.getP1().equals(chain.firstElement().getP1())) {
						chain.add(0, new DianaSegment(s.getP2(), s.getP1()));
					}
					else if (s.getP2().equals(chain.firstElement().getP1())) {
						chain.add(0, s);
					}
					else if (s.getP1().equals(chain.lastElement().getP2())) {
						chain.add(s);
					}
					else if (s.getP2().equals(chain.lastElement().getP2())) {
						chain.add(new DianaSegment(s.getP2(), s.getP1()));
					}
					else {
						logger.warning("Multiple chains not implemented yet");
					}
				}
			}

			Vector<DianaPoint> pts = new Vector<>();
			pts.add(chain.firstElement().getP1());
			for (DianaSegment s : chain) {
				pts.add(s.getP2());
			}

			// logger.info("anchor area for "+orientation+" : "+new DianaPolylin(pts) );

			return new DianaPolylin(pts);
		}

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
		return getBoundingBox();
	}

	/**
	 * Build and return new polylin representing outline
	 * 
	 * @return
	 */
	public DianaPolylin getOutline() {
		Vector<DianaPoint> pts = new Vector<>();
		pts.addAll(getPoints());
		pts.add(getPoints().firstElement());
		return new DianaPolylin(pts);
	}

}
