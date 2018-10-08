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
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
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
public class DianaRectangle extends Rectangle2D.Double implements DianaShape<DianaRectangle> {

	private static final Logger logger = Logger.getLogger(DianaRectangle.class.getPackage().getName());

	protected Filling _filling;

	public DianaRectangle() {
		this(0, 0, 0, 0, Filling.NOT_FILLED);
	}

	public DianaRectangle(Filling filling) {
		this(0, 0, 0, 0, filling);
	}

	public DianaRectangle(double aX, double aY, double aWidth, double aHeight) {
		this(aX, aY, aWidth, aHeight, Filling.NOT_FILLED);
	}

	public DianaRectangle(DianaRectangle rectangle) {
		this(rectangle.x, rectangle.y, rectangle.width, rectangle.height, rectangle._filling);
	}

	public DianaRectangle(double aX, double aY, double aWidth, double aHeight, Filling filling) {
		super(aX, aY, aWidth, aHeight);
		if (aWidth < 0) {
			x = x + aWidth;
			aWidth = -aWidth;
		}
		if (aHeight < 0) {
			y = y + aHeight;
			aHeight = -aHeight;
		}
		_filling = filling;
	}

	public DianaRectangle(DianaPoint point, DianaDimension dimension, Filling filling) {
		this(point.x, point.y, dimension.width, dimension.height, filling);
	}

	public DianaRectangle(DianaPoint p1, DianaPoint p2, Filling filling) {
		this(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y), filling);
	}

	public DianaRectangle(Rectangle rect) {
		this(rect.x, rect.y, rect.width, rect.height);
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
		return new DianaPoint(getCenterX(), getCenterY());
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
	public DianaRectangle clone() {
		return (DianaRectangle) super.clone();
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

		DianaArea intersection = computeLineIntersection(hl);
		if (intersection instanceof DianaEmptyArea) {
			return null;
		}
		else if (intersection instanceof DianaPoint) {
			return (DianaPoint) intersection;
		}
		else if (intersection instanceof DianaPoint) {
			return (DianaPoint) intersection;
		}
		else if (intersection instanceof DianaUnionArea) {
			DianaPoint returned = null;
			double minimalDistanceSq = java.lang.Double.POSITIVE_INFINITY;
			for (DianaArea a : ((DianaUnionArea) intersection).getObjects()) {
				if (a instanceof DianaPoint) {
					double distSq = DianaPoint.distanceSq(from, (DianaPoint) a);
					if (distSq < minimalDistanceSq) {
						returned = (DianaPoint) a;
						minimalDistanceSq = distSq;
					}
				}
			}
			return returned;
		}
		else if (intersection instanceof DianaSegment) {
			DianaPoint p1, p2;
			p1 = ((DianaSegment) intersection).getP1();
			p2 = ((DianaSegment) intersection).getP2();
			if (DianaPoint.distanceSq(from, p1) < DianaPoint.distanceSq(from, p2)) {
				return p1;
			}
			else {
				return p2;
			}
		}

		logger.warning("Unexpected area: " + intersection);

		return null;
	}

	public DianaPoint getNorthWestPt() {
		return getNorth().getP1();
	}

	public DianaPoint getNorthPt() {
		return getNorth().getMiddle();
	}

	public DianaPoint getNorthEastPt() {
		return getNorth().getP2();
	}

	public DianaPoint getSouthWestPt() {
		return getSouth().getP1();
	}

	public DianaPoint getSouthPt() {
		return getSouth().getMiddle();
	}

	public DianaPoint getSouthEastPt() {
		return getSouth().getP2();
	}

	public DianaPoint getEastPt() {
		return getEast().getMiddle();
	}

	public DianaPoint getWestPt() {
		return getWest().getMiddle();
	}

	public DianaSegment getNorth() {
		return new DianaSegment(getX(), getY(), getX() + getWidth(), getY());
	}

	public DianaSegment getSouth() {
		return new DianaSegment(getX(), getY() + getHeight(), getX() + getWidth(), getY() + getHeight());
	}

	public DianaSegment getEast() {
		return new DianaSegment(getX() + getWidth(), getY(), getX() + getWidth(), getY() + getHeight());
	}

	public DianaSegment getWest() {
		return new DianaSegment(getX(), getY(), getX(), getY() + getHeight());
	}

	public List<DianaSegment> getFrameSegments() {
		Vector<DianaSegment> returned = new Vector<>();
		returned.add(getNorth());
		returned.add(getSouth());
		returned.add(getEast());
		returned.add(getWest());
		return returned;
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint aPoint) {
		if (getIsFilled() && containsPoint(aPoint)) {
			return aPoint.clone();
		}
		return nearestOutlinePoint(aPoint);
	}

	@Override
	public DianaPoint nearestOutlinePoint(DianaPoint aPoint) {
		DianaPoint returned = null;
		double distSq, minimalDistanceSq = java.lang.Double.POSITIVE_INFINITY;

		DianaSegment north = getNorth();
		DianaSegment south = getSouth();
		DianaSegment east = getEast();
		DianaSegment west = getWest();

		DianaPoint p = north.getNearestPointOnSegment(aPoint);
		distSq = DianaPoint.distanceSq(p, aPoint);
		if (distSq < minimalDistanceSq) {
			returned = p;
			minimalDistanceSq = distSq;
		}

		p = south.getNearestPointOnSegment(aPoint);
		distSq = DianaPoint.distanceSq(p, aPoint);
		if (distSq < minimalDistanceSq) {
			returned = p;
			minimalDistanceSq = distSq;
		}

		p = east.getNearestPointOnSegment(aPoint);
		distSq = DianaPoint.distanceSq(p, aPoint);
		if (distSq < minimalDistanceSq) {
			returned = p;
			minimalDistanceSq = distSq;
		}

		p = west.getNearestPointOnSegment(aPoint);
		distSq = DianaPoint.distanceSq(p, aPoint);
		if (distSq < minimalDistanceSq) {
			returned = p;
			minimalDistanceSq = distSq;
		}

		return returned;
	}

	private DianaArea computeRectangleIntersection(DianaRectangle rect) {
		if (!getIsFilled() && rect.getIsFilled()) {
			return rect.computeRectangleIntersection(this);
		}

		if (rect.equals(this)) {
			return clone();
		}

		double x1 = Math.max(getMinX(), rect.getMinX());
		double y1 = Math.max(getMinY(), rect.getMinY());
		double x2 = Math.min(getMaxX(), rect.getMaxX());
		double y2 = Math.min(getMaxY(), rect.getMaxY());

		if (y1 > y2 || x1 > x2) {
			return new DianaEmptyArea();
		}

		// There is a common intersection
		DianaRectangle intersection = new DianaRectangle(x1, y1, x2 - x1, y2 - y1);

		/*if (intersection.getWidth() == 0) {
			if (DianaPoint.getSimplifiedOrientation(getCenter(), rect.getCenter()) == SimplifiedCardinalDirection.EAST) {
				return getEast().intersect(rect.getWest());
			}
			else if (DianaPoint.getSimplifiedOrientation(getCenter(), rect.getCenter()) == SimplifiedCardinalDirection.WEST) {
				return getWest().intersect(rect.getEast());
			}
			else {
				logger.warning("Unexpected situation encountered here while computing rectangle intersection");
			}
		}
		
		if (intersection.getHeight() == 0) {
			if (DianaPoint.getSimplifiedOrientation(getCenter(), rect.getCenter()) == SimplifiedCardinalDirection.NORTH) {
				return getNorth().intersect(rect.getSouth());
			}
			else if (DianaPoint.getSimplifiedOrientation(getCenter(), rect.getCenter()) == SimplifiedCardinalDirection.SOUTH) {
				return getSouth().intersect(rect.getNorth());
			}
			else {
				logger.warning("Unexpected situation encountered here while computing rectangle intersection");
			}
		}*/

		if (getIsFilled()) {
			if (rect.getIsFilled()) {
				intersection.setIsFilled(true);
				return intersection;
			}
			else {
				DianaUnionArea returned = new DianaUnionArea();
				returned.addArea(computeLineIntersection(rect.getNorth()));
				returned.addArea(computeLineIntersection(rect.getSouth()));
				returned.addArea(computeLineIntersection(rect.getEast()));
				returned.addArea(computeLineIntersection(rect.getWest()));
				if (returned.getObjects().size() == 0) {
					return new DianaEmptyArea();
				}
				if (returned.getObjects().size() == 1) {
					return returned.getObjects().firstElement();
				}
				return returned;
			}
		}

		// Both rectangle are opened

		DianaUnionArea returned = new DianaUnionArea();
		returned.addArea(getNorth().intersect(rect.getNorth()));
		returned.addArea(getNorth().intersect(rect.getSouth()));
		returned.addArea(getNorth().intersect(rect.getEast()));
		returned.addArea(getNorth().intersect(rect.getWest()));
		returned.addArea(getSouth().intersect(rect.getNorth()));
		returned.addArea(getSouth().intersect(rect.getSouth()));
		returned.addArea(getSouth().intersect(rect.getEast()));
		returned.addArea(getSouth().intersect(rect.getWest()));
		returned.addArea(getEast().intersect(rect.getNorth()));
		returned.addArea(getEast().intersect(rect.getSouth()));
		returned.addArea(getEast().intersect(rect.getEast()));
		returned.addArea(getEast().intersect(rect.getWest()));
		returned.addArea(getWest().intersect(rect.getNorth()));
		returned.addArea(getWest().intersect(rect.getSouth()));
		returned.addArea(getWest().intersect(rect.getEast()));
		returned.addArea(getWest().intersect(rect.getWest()));

		if (returned.getObjects().size() == 0) {
			return new DianaEmptyArea();
		}
		if (returned.getObjects().size() == 1) {
			return returned.getObjects().firstElement();
		}
		return returned;
	}

	/*private DianaArea computeRectangleIntersection(DianaRectangle rect)
	{
		Vector<DianaPoint> pts = new Vector<DianaPoint>() {
			@Override
			public synchronized boolean add(DianaPoint o)
			{
				if (!contains(o))
					return super.add(o);
				return false;
			}
		};
	
		List<DianaSegment> sl = rect.getFrameSegments();
		for (DianaSegment seg : sl) {
			DianaArea a = intersect(seg);
			if (a instanceof DianaPoint) pts.add((DianaPoint)a);
			else if (a instanceof DianaSegment) {
				pts.add(((DianaSegment)a).getP1());
				pts.add(((DianaSegment)a).getP2());
			}
			else if (a instanceof DianaUnionArea) {
				for (DianaArea a2: ((DianaUnionArea)a).getObjects()) {
					if (a2 instanceof DianaPoint) pts.add((DianaPoint)a2);
					if (a2 instanceof DianaSegment) {
						pts.add(((DianaSegment)a2).getP1());
						pts.add(((DianaSegment)a2).getP2());
					}
				}
			}
		}
	
		DianaPoint ne,nw,se,sw;
		ne = new DianaPoint(x+width,y);
		nw = new DianaPoint(x,y);
		se = new DianaPoint(x+width,y+height);
		sw = new DianaPoint(x,y+height);
		if (rect.containsPoint(ne)) pts.add(ne);
		if (rect.containsPoint(nw)) pts.add(nw);
		if (rect.containsPoint(se)) pts.add(se);
		if (rect.containsPoint(sw)) pts.add(sw);
	
		if (pts.size() == 0) return new DianaEmptyArea();
	
		else if (pts.size() == 2) {
			return new DianaSegment(pts.firstElement(),pts.elementAt(1));
		}
	
		else if (pts.size() != 4) {
			logger.warning("Strange situation here while computeRectangleIntersection between "+this+" and "+rect);
		}
	
		double minx = java.lang.Double.POSITIVE_INFINITY;
		double miny = java.lang.Double.POSITIVE_INFINITY;
		double maxx = java.lang.Double.NEGATIVE_INFINITY;
		double maxy = java.lang.Double.NEGATIVE_INFINITY;
		for (DianaPoint p : pts) {
			if (p.x<minx) minx = p.x;
			if (p.y<miny) miny = p.y;
			if (p.x>maxx) maxx = p.x;
			if (p.y>maxy) maxy = p.y;
		}
		return new DianaRectangle(minx,miny,maxx-minx,maxy-miny,Filling.FILLED);
	}*/

	private DianaArea computeHalfPlaneIntersection(DianaHalfPlane hp) {
		if (hp.containsArea(this)) {
			return this.clone();
		}
		if (computeLineIntersection(hp.line) instanceof DianaEmptyArea) {
			return new DianaEmptyArea();
		}
		else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("computeHalfPlaneIntersection() for rectangle when halfplane cross rectangle");
			}

			if (getIsFilled()) {

				DianaArea a = computeLineIntersection(hp.line);
				Vector<DianaPoint> pts = new Vector<>();
				if (a instanceof DianaUnionArea && ((DianaUnionArea) a).isUnionOfPoints() && ((DianaUnionArea) a).getObjects().size() == 2) {
					pts.add((DianaPoint) ((DianaUnionArea) a).getObjects().firstElement());
					pts.add((DianaPoint) ((DianaUnionArea) a).getObjects().elementAt(1));
				}
				else if (a instanceof DianaSegment) {
					pts.add(((DianaSegment) a).getP1());
					pts.add(((DianaSegment) a).getP2());
				}
				DianaPoint ne, nw, se, sw;
				ne = new DianaPoint(x + width, y);
				nw = new DianaPoint(x, y);
				se = new DianaPoint(x + width, y + height);
				sw = new DianaPoint(x, y + height);
				if (hp.containsPoint(ne) && !pts.contains(ne)) {
					pts.add(ne);
				}
				if (hp.containsPoint(nw) && !pts.contains(nw)) {
					pts.add(nw);
				}
				if (hp.containsPoint(se) && !pts.contains(se)) {
					pts.add(se);
				}
				if (hp.containsPoint(sw) && !pts.contains(sw)) {
					pts.add(sw);
				}
				return DianaPolygon.makeArea(Filling.FILLED, pts);
			}

			else { // open rectangle

				Vector<DianaArea> returned = new Vector<>();
				returned.add(hp.intersect(getNorth()));
				returned.add(hp.intersect(getSouth()));
				returned.add(hp.intersect(getEast()));
				returned.add(hp.intersect(getWest()));
				return DianaUnionArea.makeUnion(returned);

			}
		}
	}

	private DianaArea computeHalfBandIntersection(DianaHalfBand hb) {
		DianaArea bandIntersection = computeBandIntersection(hb.embeddingBand);
		DianaArea returned = bandIntersection.intersect(hb.halfPlane);
		return returned;
	}

	private DianaArea computeBandIntersection(DianaBand band) {
		if (getIsFilled()) {

			Vector<DianaPoint> pts = new Vector<>();

			DianaArea a1 = intersect(new DianaLine(band.line1));
			if (a1 instanceof DianaSegment) {
				DianaSegment s1 = (DianaSegment) a1;
				pts.add(s1.getP1());
				pts.add(s1.getP2());
			}
			else if (a1 instanceof DianaPoint) {
				pts.add((DianaPoint) a1);
			}
			else if (a1 instanceof DianaEmptyArea) {
			}
			else {
				logger.warning("Unexpected intersection: " + a1);
			}

			DianaArea a2 = intersect(new DianaLine(band.line2));
			if (a2 instanceof DianaSegment) {
				DianaSegment s2 = (DianaSegment) a2;
				pts.add(s2.getP1());
				pts.add(s2.getP2());
			}
			else if (a2 instanceof DianaPoint) {
				pts.add((DianaPoint) a2);
			}
			else if (a2 instanceof DianaEmptyArea) {
			}
			else {
				logger.warning("Unexpected intersection: " + a2);
			}

			if (band.containsPoint(getNorthEastPt())) {
				pts.add(getNorthEastPt());
			}
			if (band.containsPoint(getNorthWestPt())) {
				pts.add(getNorthWestPt());
			}
			if (band.containsPoint(getSouthEastPt())) {
				pts.add(getSouthEastPt());
			}
			if (band.containsPoint(getSouthWestPt())) {
				pts.add(getSouthWestPt());
			}

			if (pts.size() == 0) {
				return new DianaEmptyArea();
			}

			if (pts.size() == 1) {
				return pts.firstElement().clone();
			}

			return DianaPolygon.makeArea(Filling.FILLED, pts);
		}

		else { // Open rectangle

			Vector<DianaArea> returned = new Vector<>();
			returned.add(band.intersect(getNorth()));
			returned.add(band.intersect(getSouth()));
			returned.add(band.intersect(getEast()));
			returned.add(band.intersect(getWest()));
			return DianaUnionArea.makeUnion(returned);
		}
	}

	private DianaArea computeLineIntersection(DianaAbstractLine<?> line) {

		DianaSegment north = getNorth();
		DianaSegment south = getSouth();
		DianaSegment east = getEast();
		DianaSegment west = getWest();

		if (line == null) {
			return new DianaEmptyArea();
		}

		if (line.overlap(north)) {
			return north.intersect(line);// north.clone();
		}
		if (line.overlap(south)) {
			return south.intersect(line);// south.clone();
		}
		if (line.overlap(east)) {
			return east.intersect(line);// east.clone();
		}
		if (line.overlap(west)) {
			return west.intersect(line);// west.clone();
		}

		Vector<DianaPoint> crossed = new Vector<>();

		try {
			if (north.intersectsInsideSegment(line)) {
				DianaPoint intersection = north.getLineIntersection(line);
				if (line.contains(intersection) && !crossed.contains(intersection)) {
					crossed.add(intersection);
				}
			}
		} catch (ParallelLinesException e) {
			// don't care
		}
		try {
			if (south.intersectsInsideSegment(line)) {
				DianaPoint intersection = south.getLineIntersection(line);
				if (line.contains(intersection) && !crossed.contains(intersection)) {
					crossed.add(intersection);
				}
			}
		} catch (ParallelLinesException e) {
			// don't care
		}
		try {
			if (east.intersectsInsideSegment(line)) {
				DianaPoint intersection = east.getLineIntersection(line);
				if (line.contains(intersection) && !crossed.contains(intersection)) {
					crossed.add(intersection);
				}
			}
		} catch (ParallelLinesException e) {
			// don't care
		}
		try {
			if (west.intersectsInsideSegment(line)) {
				DianaPoint intersection = west.getLineIntersection(line);
				if (line.contains(intersection) && !crossed.contains(intersection)) {
					crossed.add(intersection);
				}
			}
		} catch (ParallelLinesException e) {
			// don't care
		}

		if (getIsFilled()) {
			if (line instanceof DianaHalfLine) {
				if (containsPoint(((DianaHalfLine) line).getLimit())) {
					if (!crossed.contains(((DianaHalfLine) line).getLimit())) {
						crossed.add(((DianaHalfLine) line).getLimit());
					}
				}
			}
			else if (line instanceof DianaSegment) {
				if (containsPoint(((DianaSegment) line).getP1())) {
					if (!crossed.contains(((DianaSegment) line).getP1())) {
						crossed.add(((DianaSegment) line).getP1());
					}
				}
				if (containsPoint(((DianaSegment) line).getP2())) {
					if (!crossed.contains(((DianaSegment) line).getP2())) {
						crossed.add(((DianaSegment) line).getP2());
					}
				}
			}
		}

		if (crossed.size() == 0) {
			return new DianaEmptyArea();
		}

		DianaArea returned;

		if (crossed.size() == 1) {
			returned = crossed.firstElement();
		}
		else if (crossed.size() == 2) {
			DianaPoint p1 = crossed.firstElement();
			DianaPoint p2 = crossed.elementAt(1);
			if (getIsFilled()) {
				returned = new DianaSegment(p1, p2);
			}
			else {
				returned = DianaUnionArea.makeUnion(p1, p2);
			}
		}
		else if (crossed.size() == 4) { // Crossed on edges
			DianaPoint p1 = crossed.firstElement();
			DianaPoint p2 = crossed.elementAt(1);
			// Choose those because north and south tested at first (cannot intersect)
			if (getIsFilled()) {
				returned = new DianaSegment(p1, p2);
			}
			else {
				returned = DianaUnionArea.makeUnion(p1, p2);
			}
		}
		else {
			logger.warning("crossed.size()=" + crossed.size() + " How is it possible ??? rectangle=" + this + " line=" + line + "\ncrossed="
					+ crossed);
			return null;
		}

		return returned;

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
			return computeRectangleIntersection((DianaRectangle) area);
		}
		if (area instanceof DianaHalfPlane) {
			return computeHalfPlaneIntersection((DianaHalfPlane) area);
		}
		if (area instanceof DianaBand) {
			return computeBandIntersection((DianaBand) area);
		}
		if (area instanceof DianaHalfBand) {
			return computeHalfBandIntersection((DianaHalfBand) area);
		}
		if (area instanceof DianaPolygon) {
			return AreaComputation.computeShapeIntersection(this, (DianaPolygon) area);
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

		if (area instanceof DianaRectangle && ((DianaRectangle) area).getIsFilled() == getIsFilled()) {
			DianaRectangle r = (DianaRectangle) area;
			if (containsArea(r.getNorth()) && getWidth() == r.getWidth() || containsArea(r.getSouth()) && getWidth() == r.getWidth()
					|| containsArea(r.getEast()) && getHeight() == r.getHeight()
					|| containsArea(r.getWest()) && getHeight() == r.getHeight()) {
				return rectangleUnion(r);
			}
		}
		return new DianaUnionArea(this, area);
	}

	@Override
	public List<DianaPoint> getControlPoints() {
		Vector<DianaPoint> returned = new Vector<>();
		returned.add(new DianaPoint(x, y));
		returned.add(new DianaPoint(x + width, y));
		returned.add(new DianaPoint(x, y + height));
		returned.add(new DianaPoint(x + width, y + height));
		returned.add(getNorth().getMiddle());
		returned.add(getEast().getMiddle());
		returned.add(getWest().getMiddle());
		returned.add(getSouth().getMiddle());
		return returned;
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		if (p.x >= getX() - EPSILON && p.x <= getX() + getWidth() + EPSILON && p.y >= getY() - EPSILON
				&& p.y <= getY() + getHeight() + EPSILON) {
			if (!getIsFilled()) {
				DianaSegment north = new DianaSegment(new DianaPoint(x, y), new DianaPoint(x + width, y));
				DianaSegment south = new DianaSegment(new DianaPoint(x, y + height), new DianaPoint(x + width, y + height));
				DianaSegment west = new DianaSegment(new DianaPoint(x, y), new DianaPoint(x, y + height));
				DianaSegment east = new DianaSegment(new DianaPoint(x + width, y), new DianaPoint(x + width, y + height));
				return north.contains(p) || south.contains(p) || east.contains(p) || west.contains(p);
			}
			else {
				return true;
			}
		}
		return false;
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
	public boolean containsArea(DianaArea a) {
		if (a instanceof DianaPoint) {
			return containsPoint((DianaPoint) a);
		}
		if (a instanceof DianaSegment && !(a instanceof DianaHalfLine)) {
			return containsPoint(((DianaSegment) a).getP1()) && containsPoint(((DianaSegment) a).getP2());
		}
		if (a instanceof DianaShape) {
			return AreaComputation.isShapeContainedInArea((DianaShape<?>) a, this);
		}
		return false;
	}

	@Override
	public DianaArea transform(AffineTransform t) {
		// Better implementation allowing rotations

		DianaPoint p1 = new DianaPoint(getX(), getY()).transform(t);
		DianaPoint p2 = new DianaPoint(getX() + getWidth(), getY()).transform(t);
		DianaPoint p3 = new DianaPoint(getX(), getY() + getHeight()).transform(t);
		DianaPoint p4 = new DianaPoint(getX() + getWidth(), getY() + getHeight()).transform(t);

		return DianaPolygon.makeArea(_filling, p1, p2, p3, p4);

		// Old implementation follows (commented)

		/*	DianaPoint p1 = (new DianaPoint(getX(),getY())).transform(t);
		DianaPoint p2 = (new DianaPoint(getX()+getWidth(),getY()+getHeight())).transform(t);
		
		// TODO: if transformation contains a rotation, turn into a regular polygon
		return new DianaRectangle(
				Math.min(p1.x,p2.x),
				Math.min(p1.y,p2.y),
				Math.abs(p1.x-p2.x),
				Math.abs(p1.y-p2.y),
				_filling);*/
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		if (getIsFilled()) {
			g.useDefaultBackgroundStyle();
			g.fillRect(getX(), getY(), getWidth(), getHeight());
		}
		g.useDefaultForegroundStyle();
		g.drawRect(getX(), getY(), getWidth(), getHeight());
	}

	@Override
	public String toString() {
		return "DianaRectangle: (" + x + "," + y + "," + width + "," + height + ")";
	}

	@Override
	public String getStringRepresentation() {
		return toString();
	}

	@Override
	public DianaRectangle getBoundingBox() {
		return clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaRectangle) {
			DianaRectangle p = (DianaRectangle) obj;
			if (getIsFilled() != p.getIsFilled()) {
				return false;
			}
			return Math.abs(getX() - p.getX()) <= EPSILON && Math.abs(getY() - p.getY()) <= EPSILON
					&& Math.abs(getWidth() - p.getWidth()) <= EPSILON && Math.abs(getHeight() - p.getHeight()) <= EPSILON;
		}
		return super.equals(obj);
	}

	@Override
	public DianaArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		return getAnchorAreaFrom(orientation).getOrthogonalPerspectiveArea(orientation);
	}

	@Override
	public DianaSegment getAnchorAreaFrom(SimplifiedCardinalDirection orientation) {
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			return getNorth();
		}
		else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			return getSouth();
		}
		else if (orientation == SimplifiedCardinalDirection.EAST) {
			return getEast();
		}
		else if (orientation == SimplifiedCardinalDirection.WEST) {
			return getWest();
		}
		logger.warning("Unexpected: " + orientation);
		return null;
	}

	public DianaRectangle rectangleUnion(DianaRectangle r) {
		double x1 = Math.min(x, r.x);
		double x2 = Math.max(x + width, r.x + r.width);
		double y1 = Math.min(y, r.y);
		double y2 = Math.max(y + height, r.y + r.height);
		return new DianaRectangle(x1, y1, x2 - x1, y2 - y1, Filling.FILLED);
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
		return new DianaRectangle(x, y, width, height, Filling.FILLED);
	}

	/**
	 * Build and return new polylin representing outline
	 * 
	 * @return
	 */
	public DianaPolylin getOutline() {
		return new DianaPolylin(getNorthEastPt(), getSouthEastPt(), getSouthWestPt(), getNorthWestPt());
	}

}
