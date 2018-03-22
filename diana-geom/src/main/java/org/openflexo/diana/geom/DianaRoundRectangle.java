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
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.diana.geom.DianaArc.ArcType;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaExclusiveOrArea;
import org.openflexo.diana.geom.area.DianaHalfLine;
import org.openflexo.diana.geom.area.DianaHalfPlane;
import org.openflexo.diana.geom.area.DianaIntersectionArea;
import org.openflexo.diana.geom.area.DianaSubstractionArea;
import org.openflexo.diana.geom.area.DianaUnionArea;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

@SuppressWarnings("serial")
public class DianaRoundRectangle extends RoundRectangle2D.Double implements DianaShape<DianaRoundRectangle> {

	private static final Logger logger = Logger.getLogger(DianaRoundRectangle.class.getPackage().getName());

	protected Filling _filling;

	public DianaRoundRectangle() {
		this(0, 0, 0, 0, 0, 0, Filling.NOT_FILLED);
	}

	public DianaRoundRectangle(double aX, double aY, double aWidth, double aHeight, double anArcWidth, double anArcHeight) {
		this(aX, aY, aWidth, aHeight, anArcWidth, anArcHeight, Filling.NOT_FILLED);
	}

	public DianaRoundRectangle(double aX, double aY, double aWidth, double aHeight, double anArcWidth, double anArcHeight, Filling filling) {
		super(aX, aY, aWidth, aHeight, anArcWidth, anArcHeight);
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

	public DianaRoundRectangle(DianaPoint p1, DianaPoint p2, double anArcWidth, double anArcHeight, Filling filling) {
		this(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y), anArcWidth, anArcHeight, filling);
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
	public DianaRoundRectangle clone() {
		return (DianaRoundRectangle) super.clone();
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

	public DianaSegment getArcExcludedNorth() {
		return new DianaSegment(getX() + arcwidth / 2, getY(), getX() + getWidth() - arcwidth / 2, getY());
	}

	public DianaSegment getArcExcludedSouth() {
		return new DianaSegment(getX() + arcwidth / 2, getY() + getHeight(), getX() + getWidth() - arcwidth / 2, getY() + getHeight());
	}

	public DianaSegment getArcExcludedEast() {
		return new DianaSegment(getX() + getWidth(), getY() + archeight / 2, getX() + getWidth(), getY() - archeight / 2 + getHeight());
	}

	public DianaSegment getArcExcludedWest() {
		return new DianaSegment(getX(), getY() + archeight / 2, getX(), getY() + getHeight() - archeight / 2);
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
		if (returned == null) {
			returned = new DianaPoint(0, 0);
		}
		if (getNorthWestRoundBounds().containsPoint(returned)) {
			// System.out.println("nearestOutlinePoint() in NW");
			return getNorthWestRound().nearestOutlinePoint(aPoint);
		}

		if (getSouthWestRoundBounds().containsPoint(returned)) {
			// System.out.println("nearestOutlinePoint() in SW");
			return getSouthWestRound().nearestOutlinePoint(aPoint);
		}

		if (getNorthEastRoundBounds().containsPoint(returned)) {
			// System.out.println("nearestOutlinePoint() in NE");
			return getNorthEastRound().nearestOutlinePoint(aPoint);
		}

		if (getSouthEastRoundBounds().containsPoint(returned)) {
			// System.out.println("nearestOutlinePoint() in SE");
			return getSouthEastRound().nearestOutlinePoint(aPoint);
		}

		return returned;
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
		else if (intersection instanceof DianaUnionArea) {
			double minimalDistanceSq = java.lang.Double.POSITIVE_INFINITY;
			DianaPoint returned = null;
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

		else {
			logger.warning("Unexpected area: " + intersection);
			return null;
		}

		/*if (getNorthWestRoundBounds().contains(returned)) {
			//System.out.println("outlineIntersect() in NW");
			return getNorthWestRound().nearestPointFrom(returned,orientation);
		}
		
		if (getSouthWestRoundBounds().contains(returned)) {
			//System.out.println("outlineIntersect() in SW");
			return getSouthWestRound().nearestPointFrom(returned,orientation);
		}
		
		if (getNorthEastRoundBounds().contains(returned)) {
			//System.out.println("outlineIntersect() in NE");
			return getNorthEastRound().nearestPointFrom(returned,orientation);
		}
		
		
		if (getSouthEastRoundBounds().contains(returned)) {
			//System.out.println("outlineIntersect() in SE");
			return getSouthEastRound().nearestPointFrom(returned,orientation);
		}*/

		// return returned;
	}

	protected DianaArc getNorthWestRound() {
		return new DianaArc(x, y, arcwidth, archeight, 90, 90);
	}

	protected DianaArc getFilledNorthWestRound() {
		return new DianaArc(x, y, arcwidth, archeight, 90, 90, ArcType.PIE);
	}

	protected DianaArc getSouthWestRound() {
		return new DianaArc(x, y + height - archeight, arcwidth, archeight, 180, 90);
	}

	protected DianaArc getFilledSouthWestRound() {
		return new DianaArc(x, y + height - archeight, arcwidth, archeight, 180, 90, ArcType.PIE);
	}

	protected DianaArc getNorthEastRound() {
		return new DianaArc(x + width - arcwidth, y, arcwidth, archeight, 0, 90);
	}

	protected DianaArc getFilledNorthEastRound() {
		return new DianaArc(x + width - arcwidth, y, arcwidth, archeight, 0, 90, ArcType.PIE);
	}

	protected DianaArc getSouthEastRound() {
		return new DianaArc(x + width - arcwidth, y + height - archeight, arcwidth, archeight, -90, 90);
	}

	protected DianaArc getFilledSouthEastRound() {
		return new DianaArc(x + width - arcwidth, y + height - archeight, arcwidth, archeight, -90, 90, ArcType.PIE);
	}

	protected DianaRectangle getNorthWestRoundBounds() {
		return getNorthWestRound().getBoundingBox();
	}

	protected DianaRectangle getSouthWestRoundBounds() {
		return getSouthWestRound().getBoundingBox();
	}

	protected DianaRectangle getNorthEastRoundBounds() {
		return getNorthEastRound().getBoundingBox();
	}

	protected DianaRectangle getSouthEastRoundBounds() {
		return getSouthEastRound().getBoundingBox();
	}

	private DianaArea computeRectangleIntersection(DianaRoundRectangle rect) {
		Vector<DianaPoint> pts = new Vector<DianaPoint>() {
			@Override
			public synchronized boolean add(DianaPoint o) {
				if (!contains(o)) {
					return super.add(o);
				}
				return false;
			}
		};

		List<DianaSegment> sl = rect.getFrameSegments();
		for (DianaSegment seg : sl) {
			DianaArea a = intersect(seg);
			if (a instanceof DianaPoint) {
				pts.add((DianaPoint) a);
			}
			else if (a instanceof DianaSegment) {
				pts.add(((DianaSegment) a).getP1());
				pts.add(((DianaSegment) a).getP2());
			}
			else if (a instanceof DianaUnionArea) {
				for (DianaArea a2 : ((DianaUnionArea) a).getObjects()) {
					if (a2 instanceof DianaPoint) {
						pts.add((DianaPoint) a2);
					}
					if (a2 instanceof DianaSegment) {
						pts.add(((DianaSegment) a2).getP1());
						pts.add(((DianaSegment) a2).getP2());
					}
				}
			}
		}

		DianaPoint ne, nw, se, sw;
		ne = new DianaPoint(x + width, y);
		nw = new DianaPoint(x, y);
		se = new DianaPoint(x + width, y + height);
		sw = new DianaPoint(x, y + height);
		if (rect.containsPoint(ne)) {
			pts.add(ne);
		}
		if (rect.containsPoint(nw)) {
			pts.add(nw);
		}
		if (rect.containsPoint(se)) {
			pts.add(se);
		}
		if (rect.containsPoint(sw)) {
			pts.add(sw);
		}

		if (pts.size() == 0) {
			return new DianaEmptyArea();
		}
		else if (pts.size() == 2) {
			return new DianaSegment(pts.firstElement(), pts.elementAt(1));
		}
		else if (pts.size() != 4) {
			logger.warning("Strange situation here while computeRectangleIntersection between " + this + " and " + rect);
		}

		double minx = java.lang.Double.POSITIVE_INFINITY;
		double miny = java.lang.Double.POSITIVE_INFINITY;
		double maxx = java.lang.Double.NEGATIVE_INFINITY;
		double maxy = java.lang.Double.NEGATIVE_INFINITY;
		for (DianaPoint p : pts) {
			if (p.x < minx) {
				minx = p.x;
			}
			if (p.y < miny) {
				miny = p.y;
			}
			if (p.x > maxx) {
				maxx = p.x;
			}
			if (p.y > maxy) {
				maxy = p.y;
			}
		}
		return new DianaRoundRectangle(minx, miny, maxx - minx, maxy - miny, arcwidth, archeight, Filling.FILLED);
	}

	private DianaArea computeHalfPlaneIntersection(DianaHalfPlane hp) {
		if (hp.containsArea(this)) {
			return this.clone();
		}
		DianaArea computeLineIntersection = computeLineIntersection(hp.line);
		if (computeLineIntersection instanceof DianaEmptyArea) {
			return new DianaEmptyArea();
		}
		else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("computeHalfPlaneIntersection() for rectangle when halfplane cross rectangle");
			}
			DianaArea a = computeLineIntersection;
			Vector<DianaPoint> pts = new Vector<>();
			if (a instanceof DianaUnionArea && ((DianaUnionArea) a).isUnionOfPoints() && ((DianaUnionArea) a).getObjects().size() == 2) {
				pts.add((DianaPoint) ((DianaUnionArea) a).getObjects().firstElement());
				pts.add((DianaPoint) ((DianaUnionArea) a).getObjects().elementAt(1));
			}
			else if (a instanceof DianaSegment) {
				if (getArcExcludedEast().containsArea(a) || getArcExcludedWest().containsArea(a) || getArcExcludedNorth().containsArea(a)
						|| getArcExcludedSouth().containsArea(a)) {
					return a;
				}
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
			if (getIsFilled()) {
				return DianaPolygon.makeArea(Filling.FILLED, pts);
			}
			else {
				return new DianaUnionArea(pts);
			}
		}
	}

	@SuppressWarnings("unused")
	private DianaArea computeLineIntersection(DianaAbstractLine<?> line) {
		DianaRectangle rectangle = new DianaRectangle(x, y, width, height, _filling);
		DianaArea returned = rectangle.intersect(line);
		if (returned instanceof DianaEmptyArea) {
			return returned;
		}
		else if (returned instanceof DianaPoint) {
			DianaPoint p = (DianaPoint) returned;
			if (containsPoint(p)) {
				return p;
			}
			if (getNorthEastRoundBounds().containsPoint(p)) {
				return getNorthEastRound().intersect(line);
			}
			if (getSouthEastRoundBounds().containsPoint(p)) {
				return getSouthEastRound().intersect(line);
			}
			if (getNorthWestRoundBounds().containsPoint(p)) {
				return getNorthWestRound().intersect(line);
			}
			if (getSouthWestRoundBounds().containsPoint(p)) {
				return getSouthWestRound().intersect(line);
			}
			return new DianaEmptyArea();
		}
		else {
			DianaPoint p1;
			DianaPoint p2;
			if (returned instanceof DianaUnionArea && ((DianaUnionArea) returned).isUnionOfPoints()) {
				p1 = (DianaPoint) ((DianaUnionArea) returned).getObjects().firstElement();
				p2 = (DianaPoint) ((DianaUnionArea) returned).getObjects().elementAt(1);
				if (containsPoint(p1) && containsPoint(p2)) {
					return returned;
				}
			}
			else if (returned instanceof DianaSegment) {
				p1 = ((DianaSegment) returned).getP1();
				p2 = ((DianaSegment) returned).getP2();
				boolean p1Contained = containsPoint(p1);
				boolean p2Contained = containsPoint(p2);
				if (p1Contained && p2Contained) {
					return returned;
				}
				else if (p1Contained || p2Contained) {
					DianaPoint contained = p1Contained ? p1 : p2;
					DianaArea p = getNorthEastRound().intersect(returned);
					if (p instanceof DianaPoint) {
						return new DianaSegment(contained, (DianaPoint) p);
					}
					p = getNorthWestRound().intersect(returned);
					if (p instanceof DianaPoint) {
						return new DianaSegment(contained, (DianaPoint) p);
					}
					p = getSouthEastRound().intersect(returned);
					if (p instanceof DianaPoint) {
						return new DianaSegment(contained, (DianaPoint) p);
					}
					p = getSouthWestRound().intersect(returned);
					if (p instanceof DianaPoint) {
						return new DianaSegment(contained, (DianaPoint) p);
					}
				}
				else {
					// FD pour SG :
					// Le code ci-après est bizarre l594 => p = null, donc on passe dans les else et du coup on renvoie toujours le dernier
					// resultat (SouthWest)
					// SG pour FD :
					// Non, on sette p entre temps. Mais tu as raison pour la première intersection, la premiere clause du if est caduque
					// J'ai commenté le code inutile
					DianaArea area = getNorthEastRound().intersect(returned);
					DianaPoint p = null;
					if (area instanceof DianaPoint) {
						/*if (p != null) {
							return new DianaSegment(p, (DianaPoint) area);
						}
						else {*/
						p = (DianaPoint) area;
						// }
					}
					area = getNorthWestRound().intersect(returned);
					if (area instanceof DianaPoint) {
						if (p != null) {
							return new DianaSegment(p, (DianaPoint) area);
						}
						else {
							p = (DianaPoint) area;
						}
					}
					area = getSouthEastRound().intersect(returned);
					if (area instanceof DianaPoint) {
						if (p != null) {
							return new DianaSegment(p, (DianaPoint) area);
						}
						else {
							p = (DianaPoint) area;
						}
					}
					area = getSouthWestRound().intersect(returned);
					if (area instanceof DianaPoint) {
						if (p != null) {
							return new DianaSegment(p, (DianaPoint) area);
						}
						else {
							p = (DianaPoint) area;
						}
					}
					if (p != null) {
						return p;
					}
					else {
						return new DianaEmptyArea();
					}
				}
			}
			else {
				logger.warning("Unexpected " + returned);
				return new DianaEmptyArea();
			}
			DianaArea newP1 = p1;
			DianaArea newP2 = p2;
			if (getNorthEastRoundBounds().containsPoint(p1)) {
				newP1 = getNorthEastRound().intersect(line);
			}
			if (getSouthEastRoundBounds().containsPoint(p1)) {
				newP1 = getSouthEastRound().intersect(line);
			}
			if (getNorthWestRoundBounds().containsPoint(p1)) {
				newP1 = getNorthWestRound().intersect(line);
			}
			if (getSouthWestRoundBounds().containsPoint(p1)) {
				newP1 = getSouthWestRound().intersect(line);
			}
			if (getNorthEastRoundBounds().containsPoint(p2)) {
				newP2 = getNorthEastRound().intersect(line);
			}
			if (getSouthEastRoundBounds().containsPoint(p2)) {
				newP2 = getSouthEastRound().intersect(line);
			}
			if (getNorthWestRoundBounds().containsPoint(p2)) {
				newP2 = getNorthWestRound().intersect(line);
			}
			if (getSouthWestRoundBounds().containsPoint(p2)) {
				newP2 = getSouthWestRound().intersect(line);
			}

			if (newP1 instanceof DianaPoint) {
				if (newP2 instanceof DianaPoint) {
					if (returned instanceof DianaUnionArea) {
						return new DianaUnionArea(newP1, newP2);
					}
					else if (returned instanceof DianaSegment) {
						return new DianaSegment((DianaPoint) newP1, (DianaPoint) newP2);
					}
				}
				else if (newP2 instanceof DianaEmptyArea) {
					return newP1;
				}
			}
			else if (newP1 instanceof DianaEmptyArea) {
				if (newP2 instanceof DianaPoint) {
					return newP2;
				}
				else {
					return new DianaEmptyArea();
				}
			}
			else if (newP1 instanceof DianaUnionArea && ((DianaUnionArea) newP1).isUnionOfPoints() && newP2 instanceof DianaUnionArea
					&& ((DianaUnionArea) newP2).isUnionOfPoints() && newP1.equals(newP2)) {
				return newP1;
			}

			logger.warning("Unexpected " + returned + " newP1=" + newP1 + " newP2=" + newP2);
			return new DianaEmptyArea();
		}

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
		if (area instanceof DianaRoundRectangle) {
			return computeRectangleIntersection((DianaRoundRectangle) area);
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
				DianaSegment north = getArcExcludedNorth();
				DianaSegment south = getArcExcludedSouth();
				DianaSegment west = getArcExcludedWest();
				DianaSegment east = getArcExcludedEast();
				return north.contains(p) || south.contains(p) || east.contains(p) || west.contains(p) || getNorthEastRound().contains(p)
						|| getNorthWestRound().contains(p) || getSouthEastRound().contains(p) || getSouthWestRound().contains(p);
			}
			else {
				if (new DianaRectangle(new DianaPoint(getX(), getY() + archeight / 2), new DianaDimension(getWidth(), getHeight() - archeight),
						Filling.FILLED).contains(p)) {
					return true;
				}
				if (new DianaRectangle(new DianaPoint(getX() + arcwidth / 2, getY()), new DianaDimension(getWidth() - arcwidth, getHeight()),
						Filling.FILLED).contains(p)) {
					return true;
				}
				return getFilledNorthEastRound().containsPoint(p) || getFilledNorthWestRound().containsPoint(p)
						|| getFilledSouthEastRound().containsPoint(p) || getFilledSouthWestRound().containsPoint(p);
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
		if (a instanceof DianaSegment) {
			return containsPoint(((DianaSegment) a).getP1()) && containsPoint(((DianaSegment) a).getP2());
		}
		if (a instanceof DianaShape) {
			return DianaShape.AreaComputation.isShapeContainedInArea((DianaShape<?>) a, this);
		}
		return false;
	}

	@Override
	public DianaArea transform(AffineTransform t) {
		// TODO: not valid for AffineTransform containing rotations

		DianaPoint p1 = new DianaPoint(getX(), getY()).transform(t);
		DianaPoint p2 = new DianaPoint(getX() + getWidth(), getY() + getHeight()).transform(t);

		// TODO: if transformation contains a rotation, turn into a regular polygon
		// arcwidth,archeight must also be computed according to this rotation

		return new DianaRoundRectangle(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y),
				arcwidth * t.getScaleX(), archeight * t.getScaleY(), _filling);
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		if (getIsFilled()) {
			g.useDefaultBackgroundStyle();
			g.fillRoundRect(getX(), getY(), getWidth(), getHeight(), arcwidth, archeight);
		}

		/*getNorthWestRoundBounds().paint(g);
		getSouthWestRoundBounds().paint(g);
		getNorthEastRoundBounds().paint(g);
		getSouthEastRoundBounds().paint(g);*/

		/*getFilledNorthWestRound().paint(g);
		getFilledSouthWestRound().paint(g);
		getFilledNorthEastRound().paint(g);
		getFilledSouthEastRound().paint(g);*/

		/*new DianaRectangle(
				new DianaPoint(getX(),getY()+archeight/2),
				new DianaDimension(getWidth(),getHeight()-archeight),Filling.FILLED)
		.paint(g);
		new DianaRectangle(
				new DianaPoint(getX()+arcwidth/2,getY()),
				new DianaDimension(getWidth()-arcwidth,getHeight()),Filling.FILLED)
		.paint(g);*/

		g.useDefaultForegroundStyle();
		g.drawRoundRect(getX(), getY(), getWidth(), getHeight(), arcwidth, archeight);

	}

	@Override
	public String toString() {
		return "DianaRoundRectangle: (" + x + "," + y + "," + width + "," + height + "," + arcwidth + "," + archeight + ")";
	}

	@Override
	public String getStringRepresentation() {
		return toString();
	}

	@Override
	public DianaRectangle getBoundingBox() {
		return new DianaRectangle(x, y, width, height);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaRoundRectangle) {
			DianaRoundRectangle p = (DianaRoundRectangle) obj;
			if (getIsFilled() != p.getIsFilled()) {
				return false;
			}
			return Math.abs(getX() - p.getX()) <= EPSILON && Math.abs(getY() - p.getY()) <= EPSILON
					&& Math.abs(getWidth() - p.getWidth()) <= EPSILON && Math.abs(getHeight() - p.getHeight()) <= EPSILON
					&& Math.abs(getArcWidth() - p.getArcWidth()) <= EPSILON && Math.abs(getArcHeight() - p.getArcHeight()) <= EPSILON;
		}
		return super.equals(obj);
	}

	@Override
	public DianaArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		return getAnchorAreaFrom(orientation).getOrthogonalPerspectiveArea(orientation);
	}

	@Override
	public DianaArea getAnchorAreaFrom(SimplifiedCardinalDirection orientation) {
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			DianaArc northWestRound = getNorthWestRound();
			northWestRound.extent = 45;
			DianaArc northEastRound = getNorthEastRound();
			northEastRound.extent = 45;
			northEastRound.start += 45;
			return new DianaUnionArea(getArcExcludedNorth(), northWestRound, northEastRound);
		}
		else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			// return getSouth();
			DianaArc southWestRound = getSouthWestRound();
			southWestRound.start += 45;
			southWestRound.extent = 45;
			DianaArc southEastRound = getSouthEastRound();
			southEastRound.extent = 45;
			return new DianaUnionArea(getArcExcludedSouth(), southWestRound, southEastRound);
		}
		else if (orientation == SimplifiedCardinalDirection.EAST) {
			// return getEast();
			DianaArc northEastRound = getNorthEastRound();
			northEastRound.extent = 45;
			DianaArc southEastRound = getSouthEastRound();
			southEastRound.extent = 45;
			southEastRound.start += 45;
			return new DianaUnionArea(getArcExcludedEast(), northEastRound, southEastRound);
		}
		else if (orientation == SimplifiedCardinalDirection.WEST) {
			// return getWest();
			DianaArc northWestRound = getNorthWestRound();
			northWestRound.extent = 45;
			northWestRound.start += 45;
			DianaArc southWestRound = getSouthWestRound();
			southWestRound.extent = 45;
			return new DianaUnionArea(getArcExcludedWest(), northWestRound, southWestRound);
		}
		logger.warning("Unexpected: " + orientation);
		return null;
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

}
