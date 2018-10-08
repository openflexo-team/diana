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
import org.openflexo.diana.geom.DianaArc;
import org.openflexo.diana.geom.DianaGeometricObject;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolygon;
import org.openflexo.diana.geom.DianaPolylin;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

public class DianaUnionArea extends DianaOperationArea {

	private static final Logger logger = Logger.getLogger(DianaUnionArea.class.getPackage().getName());

	private Vector<DianaArea> _objects;

	public static DianaArea makeUnion(DianaArea... objects) {
		Vector<DianaArea> v = new Vector<>();
		for (DianaArea o : objects) {
			v.add(o.clone());
		}
		return makeUnion(v);
	}

	public static DianaArea makeUnion(List<? extends DianaArea> objects) {
		return makeUnion(objects, true);
	}

	private static DianaArea makeUnion(List<? extends DianaArea> objects, boolean tryToReduceUnionByConcatenation) {
		List<? extends DianaArea> objectsToTakeUnderAccount = reduceUnionByEmbedding(objects);

		if (tryToReduceUnionByConcatenation) {

			List<? extends DianaArea> concatenedObjects = reduceUnionByConcatenation(objectsToTakeUnderAccount);

			if (concatenedObjects.size() == 0) {
				return new DianaEmptyArea();
			}
			else if (concatenedObjects.size() == 1) {
				return concatenedObjects.get(0).clone();
			}

			// System.out.println("Concatened objects: ");
			// for (DianaArea o : concatenedObjects) System.out.println(" > "+o);

			return new DianaUnionArea(concatenedObjects);
		}

		else {
			if (objectsToTakeUnderAccount.size() == 1) {
				return objectsToTakeUnderAccount.get(0);
			}
			return new DianaUnionArea(objectsToTakeUnderAccount);
		}
	}

	private static List<? extends DianaArea> reduceUnionByEmbedding(List<? extends DianaArea> objects) {
		Vector<DianaArea> objectsToTakeUnderAccount = new Vector<>();

		for (int i = 0; i < objects.size(); i++) {
			DianaArea o = objects.get(i);
			if (o instanceof DianaEmptyArea) {
				// Ignore
			}
			else {
				boolean isAlreadyContained = false;
				for (DianaArea a : objectsToTakeUnderAccount) {
					if (a.containsArea(o)) {
						isAlreadyContained = true;
					}
				}
				if (!isAlreadyContained) {
					Vector<DianaArea> noMoreNecessaryObjects = new Vector<>();
					for (DianaArea a2 : objectsToTakeUnderAccount) {
						if (o.containsArea(a2)) {
							noMoreNecessaryObjects.add(a2);
						}
					}
					for (DianaArea removeThat : noMoreNecessaryObjects) {
						objectsToTakeUnderAccount.remove(removeThat);
					}
					objectsToTakeUnderAccount.add(o);
				}

			}
		}

		return objectsToTakeUnderAccount;
	}

	private static List<? extends DianaArea> reduceUnionByConcatenation(List<? extends DianaArea> objectsToTakeUnderAccount) {
		List<? extends DianaArea> listOfObjects = objectsToTakeUnderAccount;

		boolean continueReducing = true;

		while (continueReducing) {

			continueReducing = false;

			Vector<DianaArea> concatenedObjects = new Vector<>();
			concatenedObjects.addAll(listOfObjects);

			for (int i = 0; i < listOfObjects.size(); i++) {
				DianaArea a1 = listOfObjects.get(i);
				for (int j = i + 1; j < listOfObjects.size(); j++) {
					DianaArea a2 = listOfObjects.get(j);
					DianaArea concatenation = a1.union(a2);
					if (!(concatenation instanceof DianaUnionArea)) {
						// Those 2 objects are concatenable, do it
						// logger.info("Concatenate "+a1+" and "+a2+" to form "+concatenation);
						concatenedObjects.add(concatenation);
						concatenedObjects.remove(a1);
						concatenedObjects.remove(a2);
						continueReducing = true;
						break;
					}
				}
				if (continueReducing) {
					break;
				}
			}

			listOfObjects = reduceUnionByEmbedding(concatenedObjects);

		}

		return listOfObjects;
	}

	public static void main(String[] args) {
		DianaPoint pt = new DianaPoint(0, 0);
		DianaLine line1 = new DianaLine(new DianaPoint(0, 0), new DianaPoint(1, 0));
		DianaLine line2 = new DianaLine(new DianaPoint(0, 0), new DianaPoint(1, 1));
		DianaRectangle rectangle = new DianaRectangle(new DianaPoint(0, 0), new DianaPoint(1, 1), Filling.FILLED);
		DianaHalfPlane hp = new DianaHalfPlane(line1, new DianaPoint(1, 1));

		System.out.println("Union1: " + makeUnion(line1, line2));
		System.out.println("Union2: " + makeUnion(rectangle, line1, pt));
		System.out.println("Union3: " + makeUnion(pt, line1, rectangle, hp));
		System.out.println("Union4: " + makeUnion(pt, line1, rectangle, hp, line2));
	}

	public DianaUnionArea() {
		super();
		_objects = new Vector<>();
	}

	public DianaUnionArea(DianaArea... objects) {
		this();

		// System.out.println("Making DianaUnionArea");
		/*for (DianaArea o : objects) {
			System.out.println("Object: " + o);
		}*/
		for (DianaArea o : new ArrayList<>(Arrays.asList(objects))) {
			// System.out.println("o=" + o);
			// System.out.println("o.clone()=" + o.clone());
			addArea(o.clone());
		}
		// logger.info(">>> Creating DianaUnionArea with "+objects);
		if (objects.length == 1) {
			logger.warning("Called constructor for DianaUnionArea with 1 object");
		}

	}

	public DianaUnionArea(List<? extends DianaArea> objects) {
		this();
		for (DianaArea o : objects) {
			addArea(o.clone());
		}
		// logger.info(">>> Creating DianaUnionArea with "+objects);
		if (objects.size() == 1) {
			logger.warning("Called constructor for DianaUnionArea with 1 object");
		}

		// if (objects.size() == 2 && objects.get(0) instanceof DianaArc && objects.get(1) instanceof DianaPoint ) {
		// (new Exception("------------ Ca vient de la ce truc bizarre !!!")).printStackTrace();
		// }
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
			addArea(o.clone());
		}
	}

	public void addToObjects(DianaArea obj) {
		addArea(obj.clone());
	}

	public void removeFromObjects(DianaArea obj) {
		removeArea(obj);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("DianaUnionArea: nObjects=" + _objects.size() + "\n");
		for (int i = 0; i < _objects.size(); i++) {
			sb.append(" " + (i + 1) + " > " + _objects.elementAt(i) + "\n");
		}
		return sb.toString();
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		for (DianaArea a : _objects) {
			if (a.containsPoint(p)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		// TODO: what if Union of two objects contains the line ? Implement this...
		for (DianaArea a : _objects) {
			if (a.containsLine(l)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsPolylin(DianaPolylin p) {
		for (DianaSegment s : p.getSegments()) {
			if (!containsLine(s)) {
				return false;
			}
		}
		return true;
	}

	public boolean containsPolygon(DianaPolygon p) {
		for (DianaSegment s : p.getSegments()) {
			if (!containsLine(s)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public DianaArea transform(AffineTransform t) {
		DianaArea[] all = new DianaArea[_objects.size()];
		for (int i = 0; i < _objects.size(); i++) {
			all[i] = _objects.get(i).transform(t);
		}
		return new DianaUnionArea(all);
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		for (DianaArea a : _objects) {
			a.paint(g);
		}
	}

	public void addArea(DianaArea area) {
		if (area instanceof DianaEmptyArea) {
			return;
		}

		if (containsArea(area)) {
			return;
		}

		if (area instanceof DianaUnionArea) {
			for (DianaArea a : ((DianaUnionArea) area).getObjects()) {
				addArea(a);
			}
			return;
		}

		Vector<DianaArea> uselessObjects = new Vector<>();

		for (DianaArea a : _objects) {
			if (area.containsArea(a)) {
				uselessObjects.add(a);
			}
		}

		for (DianaArea a : uselessObjects) {
			_objects.remove(a);
		}

		_objects.add(area);
	}

	public void removeArea(DianaArea area) {
		if (!containsArea(area)) {
			return;
		}

		_objects.remove(area);
	}

	@Override
	public boolean containsArea(DianaArea a) {
		for (DianaArea o : _objects) {
			if (o.containsArea(a)) {
				return true;
			}
		}
		if (a instanceof DianaPoint) {
			return containsPoint((DianaPoint) a);
		}
		if (a instanceof DianaAbstractLine) {
			return containsLine((DianaAbstractLine<?>) a);
		}
		if (a instanceof DianaPolylin) {
			return containsPolylin((DianaPolylin) a);
		}
		if (a instanceof DianaPolygon) {
			return containsPolygon((DianaPolygon) a);
		}
		if (a instanceof DianaShape) {
			return AreaComputation.isShapeContainedInArea((DianaShape<?>) a, this);
		}
		return false;
	}

	public <T> boolean isUnionOf(Class<T> aClass) {
		if (_objects.size() == 0) {
			return false;
		}
		for (DianaArea a : _objects) {
			if (!aClass.isAssignableFrom(a.getClass())) {
				return false;
			}
		}
		return true;
	}

	public boolean isUnionOfFiniteGeometricObjects() {
		return isUnionOf(DianaGeometricObject.class) && isFinite();
	}

	public boolean isUnionOfPoints() {
		return isUnionOf(DianaPoint.class);
	}

	public boolean isUnionOfSegments() {
		return isUnionOf(DianaSegment.class);
	}

	public boolean isUnionOfArcs() {
		return isUnionOf(DianaArc.class);
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		}

		Vector<DianaPoint> pts = new Vector<>();
		for (DianaArea o : getObjects()) {
			pts.add(o.getNearestPoint(aPoint));
		}
		return DianaPoint.getNearestPoint(aPoint, pts);
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
		if (obj instanceof DianaUnionArea) {
			DianaUnionArea u = (DianaUnionArea) obj;
			if (getObjects().size() != u.getObjects().size()) {
				return false;
			}
			for (int i = 0; i < getObjects().size(); i++) {
				DianaArea a = getObjects().get(i);
				// Equals even if not same order
				if (u.getObjects().indexOf(a) == -1) {
					return false;
				}
			}
			return true;
		}
		return super.equals(obj);
	}

	@Override
	public DianaArea intersect(DianaArea area) {
		if (area.containsArea(this)) {
			return this.clone();
		}
		if (containsArea(area)) {
			return area.clone();
		}

		Vector<DianaArea> intersections = new Vector<>();

		for (DianaArea o : _objects) {
			intersections.add(o.intersect(area));
		}

		return makeUnion(intersections);
	}

	@Override
	public DianaArea union(DianaArea area) {
		if (containsArea(area)) {
			return clone();
		}
		if (area.containsArea(this)) {
			return area.clone();
		}

		Vector<DianaArea> objects = new Vector<>();
		objects.addAll(getObjects());
		objects.add(area);
		return makeUnion(objects, false);
	}

	@Override
	public DianaArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		/*Vector<DianaArea> newObjects = new Vector<DianaArea>();
		for (DianaArea a : getObjects()) {
			newObjects.add(a.getOrthogonalPerspectiveArea(orientation));
		}
		return new DianaUnionArea(newObjects);*/
		DianaArea anchorArea = getAnchorAreaFrom(orientation);
		if (anchorArea instanceof DianaUnionArea) {
			DianaUnionArea unionAnchorArea = (DianaUnionArea) anchorArea;
			Vector<DianaArea> newObjects = new Vector<>();
			for (DianaArea a : unionAnchorArea.getObjects()) {
				newObjects.add(a.getOrthogonalPerspectiveArea(orientation));
			}
			return DianaUnionArea.makeUnion(newObjects);
		}
		else {
			return anchorArea.getOrthogonalPerspectiveArea(orientation);
		}
	}

	@Override
	public DianaArea getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		if (isUnionOfSegments()) {
			Vector<DianaSegment> segments = new Vector<>();
			for (DianaArea s : getObjects()) {
				segments.add((DianaSegment) s);
			}
			return DianaPolylin.computeVisibleSegmentsFrom(direction, segments);
		}
		Vector<DianaArea> newObjects = new Vector<>();
		for (DianaArea a : getObjects()) {
			newObjects.add(a.getAnchorAreaFrom(direction));
		}
		return DianaUnionArea.makeUnion(newObjects);
	}

	/**
	 * Return a flag indicating if this area is finite or not An union area is finite if and only if all areas are finite
	 * 
	 * @return
	 */
	@Override
	public final boolean isFinite() {
		for (DianaArea a : _objects) {
			if (!a.isFinite()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * If this area is finite, return embedding bounds as a DianaRectangle (this is not guaranteed to be optimal in some cases). For
	 * non-finite areas (if this area contains a least one non-finite area), return null
	 * 
	 * @return
	 */
	@Override
	public final DianaRectangle getEmbeddingBounds() {
		DianaRectangle returned = null;
		for (DianaArea a : _objects) {
			if (!a.isFinite()) {
				return null;
			}
			DianaRectangle r = a.getEmbeddingBounds();
			if (r != null) {
				if (returned == null) {
					returned = r;
				}
				else {
					returned = returned.rectangleUnion(r);
				}
			}
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
		if (containsPoint(from)) {
			return from.clone();
		}

		Vector<DianaPoint> pts = new Vector<>();
		for (DianaArea o : getObjects()) {
			pts.add(o.nearestPointFrom(from, orientation));
		}
		return DianaPoint.getNearestPoint(from, pts);
	}

	/*public void simplify()
	{
		Vector<DianaArea> newObjects = new Vector<DianaArea>();
		for (DianaArea o : getObjects()) {
			if (o instanceof DianaUnionArea) {
				DianaUnionArea union = (DianaUnionArea)
			}
		}
	}*/

}
