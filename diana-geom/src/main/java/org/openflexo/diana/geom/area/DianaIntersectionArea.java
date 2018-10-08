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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.diana.geom.AreaComputation;
import org.openflexo.diana.geom.DianaAbstractLine;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

public class DianaIntersectionArea extends DianaOperationArea {

	private static final Logger logger = Logger.getLogger(DianaIntersectionArea.class.getPackage().getName());

	private Vector<DianaArea> _objects;

	public static DianaArea makeIntersection(DianaArea... objects) {
		return makeIntersection(Arrays.asList(objects));
	}

	public static DianaArea makeIntersection(List<? extends DianaArea> objects) {
		// 1. Amongst the complete list, are there any objects which are completely contained by another? If yes, then the containing-object
		// is no longer necessary since, the contained object is equal to the intersection of the containing object and the contained
		// object.
		// For example, the intersection of a point and a line (considering that the point is on that line, it intersects that line), is the
		// point itself.
		List<DianaArea> areas = getDevelopedAreas(objects);

		List<DianaArea> nonEmbeddedObjects = new ArrayList<>();
		for (DianaArea area : areas) {
			boolean shouldAdd = true;
			for (DianaArea a : nonEmbeddedObjects) {
				if (area.containsArea(a)) {
					shouldAdd = false;
				}
			}
			if (shouldAdd) {
				Vector<DianaArea> noMoreNecessaryObjects = new Vector<>();
				for (DianaArea a : nonEmbeddedObjects) {
					if (a.containsArea(area)) {
						noMoreNecessaryObjects.add(a);
					}
				}
				for (DianaArea removeThat : noMoreNecessaryObjects) {
					while (nonEmbeddedObjects.remove(removeThat)) {
						;
					}
				}
				nonEmbeddedObjects.add(area);
			}
		}

		// Try to make intersection both/both with all objects
		// > reduce intersection as much as possible

		boolean tryToIntersectBothBoth = true;

		while (tryToIntersectBothBoth) {
			tryToIntersectBothBoth = false;
			for (int i = 0; i < nonEmbeddedObjects.size(); i++) {
				DianaArea a1 = nonEmbeddedObjects.get(i);
				for (int j = i + 1; j < nonEmbeddedObjects.size(); j++) {
					DianaArea a2 = nonEmbeddedObjects.get(j);
					DianaArea intersect = a1.intersect(a2);
					if (!(intersect instanceof DianaIntersectionArea)) {
						if (intersect instanceof DianaEmptyArea) {
							return new DianaEmptyArea();
						}
						nonEmbeddedObjects.remove(a1);
						nonEmbeddedObjects.remove(a2);
						nonEmbeddedObjects.add(intersect);
						i = j = nonEmbeddedObjects.size();
						tryToIntersectBothBoth = true;
					}
				}
			}
		}

		if (nonEmbeddedObjects.size() == 0) {
			return new DianaEmptyArea();
		}
		else if (nonEmbeddedObjects.size() == 1) {
			return nonEmbeddedObjects.get(0).clone();
		}
		else {
			DianaIntersectionArea returned = new DianaIntersectionArea(nonEmbeddedObjects);

			if (returned.isDevelopable()) {
				return returned.makeDevelopped();
			}

			return returned;
		}
	}

	private static List<DianaArea> getDevelopedAreas(List<? extends DianaArea> objects) {
		List<DianaArea> areas = new ArrayList<>();
		for (DianaArea area : objects) {
			if (area instanceof DianaIntersectionArea) {
				areas.addAll(getDevelopedAreas(((DianaIntersectionArea) area).getObjects()));
			}
			else {
				areas.add(area);
			}
		}
		return areas;
	}

	public static void main(String[] args) {
		DianaLine line1 = new DianaLine(new DianaPoint(0, 0), new DianaPoint(0, 1));// Vertical left line
		DianaLine line2 = new DianaLine(new DianaPoint(0, 1), new DianaPoint(1, 1));// Horizontal bottom line
		DianaLine line3 = new DianaLine(new DianaPoint(0, 0), new DianaPoint(1, 1));// Diagonal line (top-left to bottom-right)
		DianaLine line4 = new DianaLine(new DianaPoint(0, 1), new DianaPoint(1, 0));// Diagonal line (top-right to bottom-left)

		System.out.println("Intersection1: " + makeIntersection(line1, line2));
		System.out.println("Intersection2: " + makeIntersection(line3, line2));
		System.out.println("Intersection2: " + makeIntersection(line3, line4));
		System.out.println("Intersection3: " + makeIntersection(line1, line2, line3));
		System.out.println("Intersection4: " + makeIntersection(line1, line2, line4));
	}

	public DianaIntersectionArea() {
		super();
		_objects = new Vector<>();
	}

	public DianaIntersectionArea(DianaArea... objects) {
		this();
		for (DianaArea o : objects) {
			_objects.add(o.clone());
		}
	}

	public DianaIntersectionArea(List<? extends DianaArea> objects) {
		this();
		for (DianaArea o : objects) {
			_objects.add(o.clone());
		}
	}

	public Vector<DianaArea> getObjects() {
		return _objects;
	}

	public void setObjects(Vector<DianaArea> objects) {
		if (_objects != null) {
			_objects.clear();
		}
		else {
			_objects = new Vector<>();
		}
		for (DianaArea o : objects) {
			_objects.add(o.clone());
		}
	}

	public void addToObjects(DianaArea obj) {
		_objects.add(obj.clone());
	}

	public void removeFromObjects(DianaArea obj) {
		_objects.remove(obj);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("DianaIntersectionArea: nObjects=" + _objects.size() + "\n");
		for (int i = 0; i < _objects.size(); i++) {
			sb.append(" " + (i + 1) + " > " + _objects.elementAt(i) + "\n");
		}
		return sb.toString();
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		if (_objects.size() == 0) {
			return false;
		}
		for (DianaArea a : _objects) {
			if (!a.containsPoint(p)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		if (_objects.size() == 0) {
			return false;
		}
		for (DianaArea a : _objects) {
			if (!a.containsLine(l)) {
				return false;
			}
		}
		return true;
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
		return false;
	}

	@Override
	public DianaArea transform(AffineTransform t) {
		DianaArea[] all = new DianaArea[_objects.size()];
		for (int i = 0; i < _objects.size(); i++) {
			all[i] = _objects.get(i).transform(t);
		}
		return new DianaIntersectionArea(all);
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		DianaRectangle bounds = g.getNodeNormalizedBounds();
		DianaArea resultingArea = bounds;
		for (DianaArea o : getObjects()) {
			resultingArea = resultingArea.intersect(o);
		}
		g.useDefaultForegroundStyle();
		g.useDefaultBackgroundStyle();
		if (!(resultingArea instanceof DianaIntersectionArea)) {
			resultingArea.paint(g);
		}
		else {
			logger.warning("Cannot paint intersection area: " + this);
		}
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		}

		if (getObjects().size() == 0) {
			logger.warning("getNearestPoint() called for " + this + ": no objects !");
			return null;
		}

		// Little heuristic to find nearest point
		// (not working in all cases !)
		Vector<DianaPoint> potentialPoints = new Vector<>();
		for (int i = 0; i < getObjects().size(); i++) {
			DianaPoint tryThis = _getApproximatedNearestPoint(aPoint, i);
			if (tryThis != null) {
				potentialPoints.add(tryThis);
			}
		}

		if (potentialPoints.size() == 0) {
			logger.warning("getNearestPoint() called for " + this
					+ ": Not implemented yet (tried to compute heuristic, but failing to obtain a result)");
			return null;
		}

		else {
			double bestDistance = Double.POSITIVE_INFINITY;
			DianaPoint bestPoint = null;

			for (DianaPoint p : potentialPoints) {
				double dist = DianaPoint.distance(p, aPoint);
				if (dist < bestDistance) {
					bestPoint = p;
					bestDistance = dist;
				}
			}

			return bestPoint;
		}

	}

	/**
	 * Little heuristic to find nearest point for a formal intersection (not working in all cases !)
	 * 
	 * @param aPoint
	 * @param firstTriedObjectIndex
	 * @return
	 */
	private DianaPoint _getApproximatedNearestPoint(DianaPoint aPoint, int firstTriedObjectIndex) {
		int MAX_TRIES = 10;
		int tries = 0;
		DianaPoint returned = aPoint.clone();

		// System.out.println("_getApproximatedNearestPoint() called for "+aPoint+" on "+this);

		while (!containsPoint(returned) && tries < MAX_TRIES) {
			DianaArea current = getObjects().elementAt(firstTriedObjectIndex);
			firstTriedObjectIndex++;
			if (firstTriedObjectIndex >= getObjects().size()) {
				firstTriedObjectIndex = 0;
			}
			// System.out.println("> Try on "+current);
			// System.out.println(" obtained "+current.getNearestPoint(returned)+" from "+returned);
			returned = current.getNearestPoint(returned);
			tries++;
		}

		if (containsPoint(returned)) {
			// System.out.println("Found this: "+returned);
			return returned;
		}

		return null;
	}

	@Override
	public int hashCode() {
		int res = 27;
		for (int i = 0; i < getObjects().size(); i++) {
			DianaArea a = getObjects().get(i);
			res += a.hashCode(); // commute, order does not matter
		}
		return res;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaIntersectionArea) {
			DianaIntersectionArea inters = (DianaIntersectionArea) obj;
			if (getObjects().size() != inters.getObjects().size()) {
				return false;
			}
			for (int i = 0; i < getObjects().size(); i++) {
				DianaArea a = getObjects().get(i);
				// Equals even if not same order
				if (inters.getObjects().indexOf(a) == -1) {
					return false;
				}
			}
			return true;
		}
		return super.equals(obj);
	}

	/**
	 * Return a flag indicating if this area is finite or not An union area is finite if and only if at least on area is finite
	 * 
	 * @return
	 */
	@Override
	public final boolean isFinite() {
		if (_objects.size() == 0) {
			return true;
		}
		for (DianaArea a : _objects) {
			if (a.isFinite()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If this area is finite, return embedding bounds as a DianaRectangle (this is not guaranteed to be optimal in some cases). For
	 * non-finite areas (if this area contains a least one non-finite area), return null
	 * 
	 * @return
	 */
	@Override
	public final DianaRectangle getEmbeddingBounds() {
		if (!isFinite()) {
			return null;
		}
		DianaRectangle returned = null;
		for (DianaArea a : _objects) {
			DianaRectangle r = a.getEmbeddingBounds();
			if (r != null) {
				if (returned == null) {
					returned = r;
				}
				else {
					DianaArea intersect = returned.intersect(r);
					if (intersect instanceof DianaRectangle) {
						returned = (DianaRectangle) intersect;
					}
					else if (!(intersect instanceof DianaIntersectionArea) && intersect.isFinite()) {
						returned = intersect.getEmbeddingBounds();
					}
					else {
						logger.warning("Cannot compute embedding bounds for " + this);
						return null;
					}
				}
			}
		}
		return returned;
	}

	public boolean isDevelopable() {
		if (getObjects().size() <= 1) {
			return false;
		}

		return _findFirstUnionArea(this) != null || _findFirstSubstractionArea(this) != null;

	}

	private static DianaUnionArea _findFirstUnionArea(DianaIntersectionArea intersection) {
		for (DianaArea o : intersection.getObjects()) {
			if (o instanceof DianaUnionArea) {
				return (DianaUnionArea) o;
			}
		}

		return null;
	}

	private static DianaSubstractionArea _findFirstSubstractionArea(DianaIntersectionArea intersection) {
		for (DianaArea o : intersection.getObjects()) {
			if (o instanceof DianaSubstractionArea) {
				return (DianaSubstractionArea) o;
			}
		}

		return null;
	}

	public DianaArea makeDevelopped() {
		if (!isDevelopable()) {
			return clone();
		}

		DianaArea returned = developUnions();

		if (returned instanceof DianaIntersectionArea) {
			DianaIntersectionArea intersection = (DianaIntersectionArea) returned;
			if (!intersection.isDevelopable()) {
				return clone();
			}
			returned = developSubstractions();
		}

		return returned;
	}

	private DianaArea developUnions() {
		if (_findFirstUnionArea(this) == null) {
			return clone();
		}

		DianaUnionArea union = _findFirstUnionArea(this);
		DianaArea area = null;
		Vector<DianaArea> others = new Vector<>();
		for (DianaArea o : getObjects()) {
			if (o != union) {
				if (area == null) {
					area = o;
				}
				else {
					others.add(o);
				}
			}
		}
		if (area == null) {
			logger.warning("Inconsistent data while computing developUnions() in DianaIntersectionArea");
			return clone();
		}
		else {

			// logger.info("develop "+this);

			DianaArea developedArea = _developAsIntersection(union, area);
			if (others.size() > 0) {
				others.add(developedArea);
				DianaIntersectionArea result = new DianaIntersectionArea(others);
				if (result.isDevelopable()) {
					return result.developUnions();
				}
			}

			// logger.info("obtain "+developedArea);

			return developedArea;
		}
	}

	private static DianaArea _developAsIntersection(DianaUnionArea union, DianaArea area) {
		Vector<DianaArea> unionObjects = new Vector<>();

		for (DianaArea o : union.getObjects()) {
			unionObjects.add(o.intersect(area));
		}

		return DianaUnionArea.makeUnion(unionObjects);
	}

	private DianaArea developSubstractions() {
		if (_findFirstSubstractionArea(this) == null) {
			return clone();
		}

		DianaSubstractionArea sub = _findFirstSubstractionArea(this);
		DianaArea area = null;
		Vector<DianaArea> others = new Vector<>();
		for (DianaArea o : getObjects()) {
			if (o != sub) {
				if (area == null) {
					area = o;
				}
				else {
					others.add(o);
				}
			}
		}
		if (area == null) {
			logger.warning("Inconsistent data while computing developSubstractions() in DianaIntersectionArea");
			return clone();
		}
		else {

			// logger.info("develop "+this);

			DianaArea developedArea = _developAsSubstraction(sub, area);
			if (others.size() > 0) {
				others.add(developedArea);
				DianaIntersectionArea result = new DianaIntersectionArea(others);
				if (result.isDevelopable()) {
					return result.developSubstractions();
				}
			}

			// logger.info("obtain "+developedArea);

			return developedArea;
		}
	}

	private static DianaArea _developAsSubstraction(DianaSubstractionArea sub, DianaArea area) {
		return DianaSubstractionArea.makeSubstraction(sub.getContainerArea().intersect(area), sub.getSubstractedArea().intersect(area),
				sub.isStrict(), false);
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

		if (intersect instanceof DianaIntersectionArea) {
			// Avoid infinite loop
			logger.warning("Cannot find nearest from " + from + " from " + orientation);
			return null;
		}

		return intersect.nearestPointFrom(from, orientation);
	}

}
