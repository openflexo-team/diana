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

package org.openflexo.fge.geom.area;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.geom.AreaComputation;
import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGEArc;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGEPolylin;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.FGEShapeUnion;
import org.openflexo.fge.graphics.AbstractFGEGraphics;

public class FGEUnionArea extends FGEOperationArea {

	private static final Logger logger = Logger.getLogger(FGEUnionArea.class.getPackage().getName());

	private Vector<FGEArea> _objects;

	public static FGEArea makeUnion(FGEArea... objects) {
		Vector<FGEArea> v = new Vector<>();
		for (FGEArea o : objects) {
			v.add(o.clone());
		}
		return makeUnion(v);
	}

	public static FGEArea makeUnion(List<? extends FGEArea> objects) {
		return makeUnion(objects, true);
	}

	public static FGEArea makeUnion(List<? extends FGEArea> objects, boolean tryToReduceUnion) {

		if (tryToReduceUnion) {

			List<? extends FGEArea> objectsToTakeUnderAccount = reduceUnionByEmbedding(objects);
			List<? extends FGEArea> concatenedObjects = reduceUnionByConcatenation(objectsToTakeUnderAccount);

			if (concatenedObjects.size() == 0) {
				return new FGEEmptyArea();
			}
			else if (concatenedObjects.size() == 1) {
				return concatenedObjects.get(0).clone();
			}

			// System.out.println("Concatened objects: ");
			// for (FGEArea o : concatenedObjects) System.out.println(" > "+o);

			if (areAllShapes(concatenedObjects)) {
				List<FGEShape<?>> shapes = new ArrayList<>();
				for (FGEArea o : concatenedObjects) {
					shapes.add((FGEShape<?>) o);
				}
				return new FGEShapeUnion(shapes);
			}

			return new FGEUnionArea(concatenedObjects);
		}

		else {
			if (objects.size() == 1) {
				return objects.get(0);
			}
			if (areAllShapes(objects)) {
				List<FGEShape<?>> shapes = new ArrayList<>();
				for (FGEArea o : objects) {
					shapes.add((FGEShape<?>) o);
				}

				return new FGEShapeUnion(shapes);
			}
			return new FGEUnionArea(objects);
		}
	}

	private static boolean areAllShapes(List<? extends FGEArea> objects) {
		for (FGEArea area : objects) {
			if (!(area instanceof FGEShape)) {
				return false;
			}
		}
		return true;
	}

	private static List<? extends FGEArea> reduceUnionByEmbedding(List<? extends FGEArea> objects) {
		Vector<FGEArea> objectsToTakeUnderAccount = new Vector<>();

		for (int i = 0; i < objects.size(); i++) {
			FGEArea o = objects.get(i);
			if (o instanceof FGEEmptyArea) {
				// Ignore
			}
			else {
				boolean isAlreadyContained = false;
				for (FGEArea a : objectsToTakeUnderAccount) {
					if (a.containsArea(o)) {
						isAlreadyContained = true;
					}
				}
				if (!isAlreadyContained) {
					Vector<FGEArea> noMoreNecessaryObjects = new Vector<>();
					for (FGEArea a2 : objectsToTakeUnderAccount) {
						if (o.containsArea(a2)) {
							noMoreNecessaryObjects.add(a2);
						}
					}
					for (FGEArea removeThat : noMoreNecessaryObjects) {
						objectsToTakeUnderAccount.remove(removeThat);
					}
					objectsToTakeUnderAccount.add(o);
				}

			}
		}

		return objectsToTakeUnderAccount;
	}

	private static List<? extends FGEArea> reduceUnionByConcatenation(List<? extends FGEArea> objectsToTakeUnderAccount) {
		List<? extends FGEArea> listOfObjects = objectsToTakeUnderAccount;

		boolean continueReducing = true;

		while (continueReducing) {

			continueReducing = false;

			Vector<FGEArea> concatenedObjects = new Vector<>();
			concatenedObjects.addAll(listOfObjects);

			for (int i = 0; i < listOfObjects.size(); i++) {
				FGEArea a1 = listOfObjects.get(i);
				for (int j = i + 1; j < listOfObjects.size(); j++) {
					FGEArea a2 = listOfObjects.get(j);
					FGEArea concatenation = a1.union(a2);
					if (!(concatenation instanceof FGEUnionArea)) {
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
		FGEPoint pt = new FGEPoint(0, 0);
		FGELine line1 = new FGELine(new FGEPoint(0, 0), new FGEPoint(1, 0));
		FGELine line2 = new FGELine(new FGEPoint(0, 0), new FGEPoint(1, 1));
		FGERectangle rectangle = new FGERectangle(new FGEPoint(0, 0), new FGEPoint(1, 1), Filling.FILLED);
		FGEHalfPlane hp = new FGEHalfPlane(line1, new FGEPoint(1, 1));

		System.out.println("Union1: " + makeUnion(line1, line2));
		System.out.println("Union2: " + makeUnion(rectangle, line1, pt));
		System.out.println("Union3: " + makeUnion(pt, line1, rectangle, hp));
		System.out.println("Union4: " + makeUnion(pt, line1, rectangle, hp, line2));
	}

	public FGEUnionArea() {
		super();
		_objects = new Vector<>();
	}

	public FGEUnionArea(FGEArea... objects) {
		this();

		// System.out.println("Making FGEUnionArea");
		/*for (FGEArea o : objects) {
			System.out.println("Object: " + o);
		}*/
		for (FGEArea o : new ArrayList<>(Arrays.asList(objects))) {
			// System.out.println("o=" + o);
			// System.out.println("o.clone()=" + o.clone());
			addArea(o.clone());
		}
		// logger.info(">>> Creating FGEUnionArea with "+objects);
		if (objects.length == 1) {
			logger.warning("Called constructor for FGEUnionArea with 1 object");
		}

	}

	public FGEUnionArea(List<? extends FGEArea> objects) {
		this();
		for (FGEArea o : objects) {
			addArea(o.clone());
		}
		// logger.info(">>> Creating FGEUnionArea with "+objects);
		if (objects.size() == 1) {
			logger.warning("Called constructor for FGEUnionArea with 1 object");
		}

		// if (objects.size() == 2 && objects.get(0) instanceof FGEArc && objects.get(1) instanceof FGEPoint ) {
		// (new Exception("------------ Ca vient de la ce truc bizarre !!!")).printStackTrace();
		// }
	}

	public Vector<FGEArea> getObjects() {
		return _objects;
	}

	public void setObjects(Vector<FGEArea> objects) {
		if (_objects != null) {
			_objects.clear();
		}
		else {
			_objects = new Vector<>();
		}
		for (FGEArea o : objects) {
			addArea(o.clone());
		}
	}

	public void addToObjects(FGEArea obj) {
		addArea(obj.clone());
	}

	public void removeFromObjects(FGEArea obj) {
		removeArea(obj);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("FGEUnionArea: nObjects=" + _objects.size() + "\n");
		for (int i = 0; i < _objects.size(); i++) {
			sb.append(" " + (i + 1) + " > " + _objects.elementAt(i) + "\n");
		}
		return sb.toString();
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		for (FGEArea a : _objects) {
			if (a.containsPoint(p)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsLine(FGEAbstractLine<?> l) {
		// TODO: what if Union of two objects contains the line ? Implement this...
		for (FGEArea a : _objects) {
			if (a.containsLine(l)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsPolylin(FGEPolylin p) {
		for (FGESegment s : p.getSegments()) {
			if (!containsLine(s)) {
				return false;
			}
		}
		return true;
	}

	public boolean containsPolygon(FGEPolygon p) {
		for (FGESegment s : p.getSegments()) {
			if (!containsLine(s)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public FGEArea transform(AffineTransform t) {
		FGEArea[] all = new FGEArea[_objects.size()];
		for (int i = 0; i < _objects.size(); i++) {
			all[i] = _objects.get(i).transform(t);
		}
		return new FGEUnionArea(all);
	}

	@Override
	public void paint(AbstractFGEGraphics g) {
		for (FGEArea a : _objects) {
			a.paint(g);
		}
	}

	public void addArea(FGEArea area) {
		if (area instanceof FGEEmptyArea) {
			return;
		}

		if (containsArea(area)) {
			return;
		}

		if (area instanceof FGEUnionArea) {
			for (FGEArea a : ((FGEUnionArea) area).getObjects()) {
				addArea(a);
			}
			return;
		}

		Vector<FGEArea> uselessObjects = new Vector<>();

		for (FGEArea a : _objects) {
			if (area.containsArea(a)) {
				uselessObjects.add(a);
			}
		}

		for (FGEArea a : uselessObjects) {
			_objects.remove(a);
		}

		_objects.add(area);
	}

	public void removeArea(FGEArea area) {
		if (!containsArea(area)) {
			return;
		}

		_objects.remove(area);
	}

	@Override
	public boolean containsArea(FGEArea a) {
		for (FGEArea o : _objects) {
			if (o.containsArea(a)) {
				return true;
			}
		}
		if (a instanceof FGEPoint) {
			return containsPoint((FGEPoint) a);
		}
		if (a instanceof FGEAbstractLine) {
			return containsLine((FGEAbstractLine<?>) a);
		}
		if (a instanceof FGEPolylin) {
			return containsPolylin((FGEPolylin) a);
		}
		if (a instanceof FGEPolygon) {
			return containsPolygon((FGEPolygon) a);
		}
		if (a instanceof FGEShape) {
			return AreaComputation.isShapeContainedInArea((FGEShape<?>) a, this);
		}
		return false;
	}

	public <T> boolean isUnionOf(Class<T> aClass) {
		if (_objects.size() == 0) {
			return false;
		}
		for (FGEArea a : _objects) {
			if (!aClass.isAssignableFrom(a.getClass())) {
				return false;
			}
		}
		return true;
	}

	public boolean isUnionOfFiniteGeometricObjects() {
		return isUnionOf(FGEGeometricObject.class) && isFinite();
	}

	public boolean isUnionOfPoints() {
		return isUnionOf(FGEPoint.class);
	}

	public boolean isUnionOfSegments() {
		return isUnionOf(FGESegment.class);
	}

	public boolean isUnionOfArcs() {
		return isUnionOf(FGEArc.class);
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		}

		Vector<FGEPoint> pts = new Vector<>();
		for (FGEArea o : getObjects()) {
			pts.add(o.getNearestPoint(aPoint));
		}
		return FGEPoint.getNearestPoint(aPoint, pts);
	}

	@Override
	public int hashCode() {
		int res = 27;
		for (int i = 0; i < getObjects().size(); i++) {
			FGEArea a = getObjects().get(i);
			res += a.hashCode(); // commute, order does not matter
		}
		return res;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGEUnionArea) {
			FGEUnionArea u = (FGEUnionArea) obj;
			if (getObjects().size() != u.getObjects().size()) {
				return false;
			}
			for (int i = 0; i < getObjects().size(); i++) {
				FGEArea a = getObjects().get(i);
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
	public FGEArea intersect(FGEArea area) {
		if (area.containsArea(this)) {
			return this.clone();
		}
		if (containsArea(area)) {
			return area.clone();
		}

		Vector<FGEArea> intersections = new Vector<>();

		for (FGEArea o : _objects) {
			intersections.add(o.intersect(area));
		}

		return makeUnion(intersections);
	}

	@Override
	public FGEArea union(FGEArea area) {
		if (containsArea(area)) {
			return clone();
		}
		if (area.containsArea(this)) {
			return area.clone();
		}

		Vector<FGEArea> objects = new Vector<>();
		objects.addAll(getObjects());
		objects.add(area);
		return makeUnion(objects, false);
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		/*Vector<FGEArea> newObjects = new Vector<FGEArea>();
		for (FGEArea a : getObjects()) {
			newObjects.add(a.getOrthogonalPerspectiveArea(orientation));
		}
		return new FGEUnionArea(newObjects);*/
		FGEArea anchorArea = getAnchorAreaFrom(orientation);
		if (anchorArea instanceof FGEUnionArea) {
			FGEUnionArea unionAnchorArea = (FGEUnionArea) anchorArea;
			Vector<FGEArea> newObjects = new Vector<>();
			for (FGEArea a : unionAnchorArea.getObjects()) {
				newObjects.add(a.getOrthogonalPerspectiveArea(orientation));
			}
			return FGEUnionArea.makeUnion(newObjects);
		}
		else {
			return anchorArea.getOrthogonalPerspectiveArea(orientation);
		}
	}

	@Override
	public FGEArea getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		if (isUnionOfSegments()) {
			Vector<FGESegment> segments = new Vector<>();
			for (FGEArea s : getObjects()) {
				segments.add((FGESegment) s);
			}
			return FGEPolylin.computeVisibleSegmentsFrom(direction, segments);
		}
		Vector<FGEArea> newObjects = new Vector<>();
		for (FGEArea a : getObjects()) {
			newObjects.add(a.getAnchorAreaFrom(direction));
		}
		return FGEUnionArea.makeUnion(newObjects);
	}

	/**
	 * Return a flag indicating if this area is finite or not An union area is finite if and only if all areas are finite
	 * 
	 * @return
	 */
	@Override
	public final boolean isFinite() {
		for (FGEArea a : _objects) {
			if (!a.isFinite()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * If this area is finite, return embedding bounds as a FGERectangle (this is not guaranteed to be optimal in some cases). For
	 * non-finite areas (if this area contains a least one non-finite area), return null
	 * 
	 * @return
	 */
	@Override
	public final FGERectangle getEmbeddingBounds() {
		FGERectangle returned = null;
		for (FGEArea a : _objects) {
			if (!a.isFinite()) {
				return null;
			}
			FGERectangle r = a.getEmbeddingBounds();
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
	public FGEPoint nearestPointFrom(FGEPoint from, SimplifiedCardinalDirection orientation) {
		if (containsPoint(from)) {
			return from.clone();
		}

		Vector<FGEPoint> pts = new Vector<>();
		for (FGEArea o : getObjects()) {
			pts.add(o.nearestPointFrom(from, orientation));
		}
		return FGEPoint.getNearestPoint(from, pts);
	}

	/*public void simplify()
	{
		Vector<FGEArea> newObjects = new Vector<FGEArea>();
		for (FGEArea o : getObjects()) {
			if (o instanceof FGEUnionArea) {
				FGEUnionArea union = (FGEUnionArea)
			}
		}
	}*/

}
