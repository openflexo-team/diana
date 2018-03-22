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
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaExclusiveOrArea;
import org.openflexo.diana.geom.area.DianaHalfLine;
import org.openflexo.diana.geom.area.DianaIntersectionArea;
import org.openflexo.diana.geom.area.DianaSubstractionArea;
import org.openflexo.diana.geom.area.DianaUnionArea;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

/**
 * The <code>DianaPolylin</code> class encapsulates a description of an open path, defined as an arbitrary number of line segments
 * 
 * @author sylvain
 * 
 */
public class DianaPolylin implements DianaGeometricObject<DianaPolylin> {

	private static final Logger logger = Logger.getLogger(DianaPolylin.class.getPackage().getName());

	protected Vector<DianaPoint> _points;
	protected Vector<DianaSegment> _segments;

	private DianaRectangle bounds;

	public DianaPolylin() {
		super();
		_points = new Vector<>();
		_segments = new Vector<>();
	}

	public DianaPolylin(List<DianaPoint> points) {
		this();
		for (DianaPoint p : points) {
			addToPoints(p);
		}
	}

	public DianaPolylin(DianaPoint... points) {
		this();
		for (DianaPoint p : points) {
			addToPoints(p);
		}
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
			_segments.add(s2);
		}
		reCalculateBounds();
	}

	public void removeFromPoints(DianaPoint aPoint) {
		_points.remove(aPoint);
		reCalculateBounds();
	}

	public void removePointAtIndex(int index) {
		_points.remove(index);
		if (index == getSegmentNb()) {
			// Last segment
			_segments.remove(index - 1);
		}
		else {
			if (index < _segments.size()) {
				_segments.remove(index);
			}
			if (index >= 1 && index - 1 < _segments.size() && index < _points.size()) {
				_segments.elementAt(index - 1).setP2(_points.elementAt(index));
			}
		}
		boundsChanged();
	}

	public void insertPointAtIndex(DianaPoint aPoint, int index) {
		_points.add(index, aPoint);
		if (index >= 1 && index - 1 < _segments.size()) {
			_segments.get(index - 1).setP2(aPoint);
		}
		if (index + 1 < _points.size()) {
			_segments.add(index, new DianaSegment(aPoint, _points.get(index + 1)));
		}
		boundsChanged();
	}

	public void updatePointAt(int index, DianaPoint aPoint) {
		_points.elementAt(index).setX(aPoint.x);
		_points.elementAt(index).setY(aPoint.y);
		if (getPointsNb() > 1) {
			if (index < _segments.size()) {
				_segments.elementAt(index).setP1(aPoint);
			}
			if (index > 0) {
				_segments.elementAt(index - 1).setP2(aPoint);
			}
		}
		boundsChanged();
	}

	public int getPointsNb() {
		return _points.size();
	}

	public DianaPoint getPointAt(int index) {
		if (index >= 0 && index < _points.size()) {
			return _points.elementAt(index);
		}
		return null;
	}

	public DianaPoint getFirstPoint() {
		return getPointAt(0);
	}

	public DianaPoint getLastPoint() {
		return getPointAt(getPointsNb() - 1);
	}

	public int getPointIndex(DianaPoint point) {
		int index = 0;
		for (DianaPoint p : getPoints()) {
			if (p.equals(point)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	public Vector<DianaSegment> getSegments() {
		return _segments;
	}

	public int getSegmentNb() {
		return _segments.size();
	}

	public DianaSegment getSegmentAt(int index) {
		if (index >= 0 && index < _segments.size()) {
			return _segments.elementAt(index);
		}
		return null;
	}

	public int getSegmentIndex(DianaSegment segment) {
		int index = 0;
		for (DianaSegment s : getSegments()) {
			if (s.equals(segment)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	public DianaSegment getFirstSegment() {
		return getSegmentAt(0);
	}

	public DianaSegment getLastSegment() {
		return getSegmentAt(getSegmentNb() - 1);
	}

	public DianaSegment getBiggestSegment() {
		double length = 0;
		DianaSegment returned = null;
		for (DianaSegment seg : getSegments()) {
			if (seg.getLength() > length) {
				length = seg.getLength();
				returned = seg;
			}
		}
		return returned;
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
		bounds = new DianaRectangle(boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY, Filling.FILLED);
		boundsChanged = false;
	}

	public DianaRectangle getBoundingBox() {
		if (boundsChanged || bounds == null) {
			reCalculateBounds();
		}
		return bounds;
	}

	private boolean boundsChanged = false;

	public void boundsChanged() {
		boundsChanged = true;
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		if (l instanceof DianaSegment) {
			for (DianaSegment s : _segments) {
				if (s.containsLine(l)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean contains(double x, double y) {
		return containsPoint(new DianaPoint(x, y));
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
	public DianaPolylin clone() {
		try {
			return (DianaPolylin) super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint aPoint) {
		return nearestOutlinePoint(aPoint);
	}

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
		return returned;
	}

	/**
	 * Return the closest segment of supplied point
	 * 
	 * @return
	 */
	public DianaSegment getNearestSegment(DianaPoint p) {
		double shortestDistance = Double.POSITIVE_INFINITY;
		DianaSegment returned = null;
		for (DianaSegment s : getSegments()) {
			double distance = DianaPoint.distance(p, s.getNearestPoint(p));
			if (distance <= shortestDistance) {
				returned = s;
				shortestDistance = distance;
			}
		}
		return returned;
	}

	public double getRelativeLocation(DianaPoint p) {
		double cumulated = 0;
		DianaSegment s = getNearestSegment(p);
		DianaPoint proj = s.getNearestPointOnSegment(p);
		int index = getSegmentIndex(s);
		for (int i = 0; i < index; i++) {
			cumulated += getSegmentAt(i).getLength();
		}
		cumulated += s.getLength() * s.getRelativeLocation(proj);
		return cumulated / getLength();
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
					if (line.contains(intersection)) {
						crossed.add(intersection);
					}
				}
			} catch (ParallelLinesException e) {
				// don't care
			}
		}

		return DianaUnionArea.makeUnion(crossed);
	}

	private DianaArea computePolylinIntersection(DianaPolylin polylin) {
		/*logger.info("computePolylinIntersection()");
		logger.info("polylin1="+this);
		logger.info("polylin2="+polylin);*/
		Vector<DianaArea> unionAreas = new Vector<>();
		for (DianaSegment s1 : getSegments()) {
			for (DianaSegment s2 : polylin.getSegments()) {
				DianaArea i = s1.intersect(s2);
				if (!(i instanceof DianaEmptyArea)) {
					unionAreas.add(i);
				}
			}
		}

		// logger.info("return="+DianaUnionArea.makeUnion(unionAreas));
		return DianaUnionArea.makeUnion(unionAreas);
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
		if (area instanceof DianaPolylin) {
			return computePolylinIntersection((DianaPolylin) area);
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
	public final DianaArea union(DianaArea area) {
		if (containsArea(area)) {
			return clone();
		}
		if (area.containsArea(this)) {
			return area.clone();
		}

		if (area instanceof DianaPolylin) {
			DianaPolylin p = (DianaPolylin) area;
			// logger.info("Union of "+this+" and "+p);
			DianaArea returned = clone();
			for (DianaSegment s : p.getSegments()) {
				returned = returned.union(s);
			}
			if (returned instanceof DianaUnionArea) {
				return new DianaUnionArea(this, area);
			}
			return returned;
		}
		if (area instanceof DianaSegment) {
			DianaPolylin clone = clone();
			if (containsArea(area)) {
				return clone;
			}
			DianaSegment s = (DianaSegment) area;
			if (s.getP1().equals(getFirstPoint())) {
				if (s.getP2().equals(getLastPoint())) {
					return DianaPolygon.makeArea(Filling.NOT_FILLED, getPoints());
					// return new DianaPolygon(Filling.NOT_FILLED,getPoints());
				}
				clone.insertPointAtIndex(s.getP2(), 0);
				return clone;
			}
			else if (s.getP2().equals(getFirstPoint())) {
				if (s.getP1().equals(getLastPoint())) {
					return DianaPolygon.makeArea(Filling.NOT_FILLED, getPoints());
					// return new DianaPolygon(Filling.NOT_FILLED,getPoints());
				}
				clone.insertPointAtIndex(s.getP1(), 0);
				return clone;
			}
			else if (s.getP1().equals(getLastPoint())) {
				if (s.getP2().equals(getFirstPoint())) {
					return DianaPolygon.makeArea(Filling.NOT_FILLED, getPoints());
					// return new DianaPolygon(Filling.NOT_FILLED,getPoints());
				}
				clone.addToPoints(s.getP2());
				return clone;
			}
			else if (s.getP2().equals(getLastPoint())) {
				if (s.getP1().equals(getFirstPoint())) {
					return DianaPolygon.makeArea(Filling.NOT_FILLED, getPoints());
					// return new DianaPolygon(Filling.NOT_FILLED,getPoints());
				}
				clone.addToPoints(s.getP1());
				return clone;
			}
		}
		return new DianaUnionArea(this, area);
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		for (DianaSegment s : _segments) {
			if (s.containsPoint(p)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsArea(DianaArea a) {
		if (a instanceof DianaSegment && _segments.contains(a)) {
			return true;
		}
		if (a instanceof DianaPolylin) {
			boolean allContained = true;
			for (DianaSegment s : ((DianaPolylin) a).getSegments()) {
				if (!containsArea(s)) {
					allContained = false;
					break;
				}
			}
			if (allContained) {
				return true;
			}
		}
		for (DianaSegment s : _segments) {
			if (s.containsArea(a)) {
				return true;
			}
		}
		if (a instanceof DianaShape) {
			return DianaShape.AreaComputation.isShapeContainedInArea((DianaShape<?>) a, this);
		}
		return false;
	}

	@Override
	public DianaPolylin transform(AffineTransform t) {
		Vector<DianaPoint> points = new Vector<>();
		for (DianaPoint p : _points) {
			points.add(p.transform(t));
		}
		DianaPolylin returned = new DianaPolylin(points);
		return returned;
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		g.useDefaultForegroundStyle();
		for (DianaSegment s : _segments) {
			s.paint(g);
		}
	}

	@Override
	public String toString() {
		return "DianaPolylin: " + _points;
	}

	@Override
	public String getStringRepresentation() {
		return toString();
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
		if (obj instanceof DianaPolylin) {
			DianaPolylin p = (DianaPolylin) obj;
			if (getPointsNb() != p.getPointsNb()) {
				return false;
			}

			boolean isEquals = true;
			// Test in same order
			for (int i = 0; i < getPointsNb(); i++) {
				if (!getPointAt(i).equals(p.getPointAt(i))) {
					isEquals = false;
				}
			}
			if (!isEquals) {
				isEquals = true;
				// Test in reverse order
				for (int i = 0; i < getPointsNb(); i++) {
					if (!getPointAt(i).equals(p.getPointAt(getPointsNb() - 1 - i))) {
						isEquals = false;
					}
				}
			}
			return isEquals;
		}
		return super.equals(obj);
	}

	public double getLength() {
		double returned = 0;
		for (DianaSegment s : getSegments()) {
			returned += s.getLength();
		}
		return returned;
	}

	public DianaPoint getMiddle() {
		return getPointAtRelativePosition(0.5);
	}

	public DianaPoint getPointAtRelativePosition(double position) {
		double middleDistancePath = getLength() * position;
		double distance = 0;
		for (DianaSegment s : getSegments()) {
			if (distance <= middleDistancePath && distance + s.getLength() >= middleDistancePath) {
				double ratio = (middleDistancePath - distance) / s.getLength();
				DianaPoint p = new DianaPoint();
				p.x = s.getP1().x + (s.getP2().x - s.getP1().x) * ratio;
				p.y = s.getP1().y + (s.getP2().y - s.getP1().y) * ratio;
				return p;
			}
			distance += s.getLength();
		}
		logger.warning("Unexpected situation while computing relative position of polylin");
		return new DianaPoint(0, 0);
	}

	@Override
	public DianaArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		Vector<DianaArea> allAreas = new Vector<>();
		for (DianaSegment s : getSegments()) {
			allAreas.add(s.getOrthogonalPerspectiveArea(orientation));
		}
		return DianaUnionArea.makeUnion(allAreas);
	}

	@Override
	public DianaArea getAnchorAreaFrom(SimplifiedCardinalDirection orientation) {
		return computeVisibleSegmentsFrom(orientation, getSegments());

		// This algorithm is not quite correct, you can find *very* pathologic cases, but works in most cases

		/*Vector<DianaSegment> keptSegments = new Vector<DianaSegment>();
		
		for (DianaSegment s : getSegments()) {
			DianaHalfLine hl = DianaHalfLine.makeHalfLine(s.getMiddle(), orientation);
			// Test if this half-line "cuts" an other segment
			boolean cutsAnOtherSegment = false;
			for (DianaSegment s2 : getSegments()) {
				if (!s.equals(s2)) {
					DianaArea intersect = s2.intersect(hl);
					if (intersect instanceof DianaPoint) cutsAnOtherSegment = true;
					else if (intersect instanceof DianaEmptyArea) / OK
					else {
						logger.warning("Unexpected intersection: "+intersect);
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
			Vector<DianaSegment> chain = new Vector<DianaSegment>();
			for (DianaSegment s : keptSegments) {
				if (chain.size() == 0) chain.add(s);
				else {
					if (s.getP1().equals(chain.firstElement().getP1())) {
						chain.add(0, new DianaSegment(s.getP2(),s.getP1()));
					}
					else if (s.getP2().equals(chain.firstElement().getP1())) {
						chain.add(0, s);
					}
					else if (s.getP1().equals(chain.lastElement().getP2())) {
						chain.add(s);
					}
					else if (s.getP2().equals(chain.lastElement().getP2())) {
						chain.add(new DianaSegment(s.getP2(),s.getP1()));
					}
					else {
						logger.warning("Multiple chains not implemented yet");
					}
				}
			}
		
			Vector<DianaPoint> pts = new Vector<DianaPoint>();
			pts.add(chain.firstElement().getP1());
			for (DianaSegment s : chain) {
				pts.add(s.getP2());
			}
		
			//logger.info("anchor area for "+orientation+" : "+new DianaPolylin(pts) );
		
			return new DianaPolylin(pts);
		}
		*/
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

	public static DianaArea computeVisibleSegmentsFrom(SimplifiedCardinalDirection orientation, Vector<DianaSegment> segments) {
		// This algorithm is not quite correct, you can find *very* pathologic cases, but works in most cases

		Vector<DianaSegment> keptSegments = new Vector<>();

		for (DianaSegment s : segments) {
			DianaHalfLine hl = DianaHalfLine.makeHalfLine(s.getMiddle(), orientation);
			// Test if this half-line "cuts" an other segment
			boolean cutsAnOtherSegment = false;
			for (DianaSegment s2 : segments) {
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
			return DianaUnionArea.makeUnion(keptSegments);

			// Chains segments
			/*Vector<DianaSegment> chain = new Vector<DianaSegment>();
			for (DianaSegment s : keptSegments) {
				if (chain.size() == 0) chain.add(s);
				else {
					if (s.getP1().equals(chain.firstElement().getP1())) {
						chain.add(0, new DianaSegment(s.getP2(),s.getP1()));
					}
					else if (s.getP2().equals(chain.firstElement().getP1())) {
						chain.add(0, s);
					}
					else if (s.getP1().equals(chain.lastElement().getP2())) {
						chain.add(s);
					}
					else if (s.getP2().equals(chain.lastElement().getP2())) {
						chain.add(new DianaSegment(s.getP2(),s.getP1()));
					}
					else {
						logger.warning("Multiple chains not implemented yet");
					}
				}
			}
			
			Vector<DianaPoint> pts = new Vector<DianaPoint>();
			pts.add(chain.firstElement().getP1());
			for (DianaSegment s : chain) {
				pts.add(s.getP2());
			}
			
			//logger.info("anchor area for "+orientation+" : "+new DianaPolylin(pts) );
			
			return new DianaPolylin(pts);*/
		}

	}

}
