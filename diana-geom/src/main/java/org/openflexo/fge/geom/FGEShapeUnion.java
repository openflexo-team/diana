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

package org.openflexo.fge.geom;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEExclusiveOrArea;
import org.openflexo.fge.geom.area.FGESubstractionArea;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graphics.AbstractFGEGraphics;
import org.openflexo.fge.graphics.BGStyle;
import org.openflexo.fge.graphics.FGStyle;

/**
 * Implements a union (a finite collection) of {@link FGEShape}<br>
 * 
 * Note the difference with the {@link FGEUnionArea}: we implement here the {@link FGEShape} API: result is considered as a finite shape
 * 
 * @author sylvain
 *
 */
public class FGEShapeUnion extends Rectangle2D.Double implements FGEShape<FGEShapeUnion> {

	private static final Logger logger = Logger.getLogger(FGEShapeUnion.class.getPackage().getName());

	private List<FGEShape<?>> shapes;
	private FGStyle foreground;
	private BGStyle background;

	public FGEShapeUnion() {
		super();
		shapes = new ArrayList<>();
	}

	public FGEShapeUnion(FGEShape<?>... shapes) {
		this();

		for (FGEShape<?> o : shapes) {
			addToShapes(o);
		}

	}

	public FGEShapeUnion(List<? extends FGEShape<?>> shapes) {
		this();
		for (FGEShape<?> shape : shapes) {
			addToShapes(shape);
		}
	}

	public List<FGEShape<?>> getShapes() {
		return shapes;
	}

	public void setShapes(List<FGEShape<?>> shapes) {
		if (shapes != null) {
			shapes.clear();
		}
		else {
			shapes = new ArrayList<>();
		}
		for (FGEShape<?> shape : shapes) {
			addToShapes(shape);
		}
	}

	public void addToShapes(FGEShape<?> shape) {
		shapes.add(shape);
		updateBounds();
	}

	public void removeFromShapes(FGEShape<?> shape) {
		shapes.remove(shape);
		updateBounds();
	}

	private void updateBounds() {
		FGERectangle newBounds = null;
		for (FGEShape<?> shape : getShapes()) {
			FGERectangle boundingBox = shape.getBoundingBox();
			if (newBounds == null) {
				newBounds = boundingBox;
			}
			else {
				newBounds = newBounds.rectangleUnion(boundingBox);
			}
		}
		setRect(newBounds);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("FGEShapeUnion: nObjects=" + shapes.size() + "\n");
		for (int i = 0; i < shapes.size(); i++) {
			sb.append(" " + (i + 1) + " > " + shapes.get(i)
			/*+ " BG=" + shapes.get(i).getBackground() + " FG="
			+ shapes.get(i).getForeground()*/ + "\n");
		}
		return sb.toString();
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		for (FGEShape<?> a : shapes) {
			if (a.containsPoint(p)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsLine(FGEAbstractLine<?> l) {
		// TODO: what if Union of two objects contains the line ? Implement this...
		for (FGEShape<?> a : shapes) {
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
	public FGEShapeUnion transform(AffineTransform t) {
		FGEShape<?>[] all = new FGEShape<?>[shapes.size()];
		for (int i = 0; i < shapes.size(); i++) {
			all[i] = (FGEShape<?>) shapes.get(i).transform(t);
		}
		FGEShapeUnion returned = new FGEShapeUnion(all);
		returned.setForeground(getForeground());
		returned.setBackground(getBackground());
		return returned;
	}

	@Override
	public void paint(AbstractFGEGraphics g) {
		g.setDefaultBackgroundStyle(this);
		g.setDefaultForegroundStyle(this);

		for (FGEShape<?> a : shapes) {
			a.paint(g);
		}
	}

	@Override
	public boolean containsArea(FGEArea a) {
		for (FGEShape<?> o : shapes) {
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
		if (shapes.size() == 0) {
			return false;
		}
		for (FGEShape<?> a : shapes) {
			if (!aClass.isAssignableFrom(a.getClass())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		}

		Vector<FGEPoint> pts = new Vector<>();
		for (FGEShape<?> o : shapes) {
			pts.add(o.getNearestPoint(aPoint));
		}
		return FGEPoint.getNearestPoint(aPoint, pts);
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

		for (FGEShape<?> o : shapes) {
			intersections.add(o.intersect(area));
		}

		return FGEUnionArea.makeUnion(intersections);
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
		objects.addAll(getShapes());
		objects.add(area);
		return FGEUnionArea.makeUnion(objects, false);
	}

	@Override
	public FGEArea substract(FGEArea area, boolean isStrict) {
		if (area.containsArea(this)) {
			return new FGEEmptyArea();
		}
		if (!containsArea(area)) {
			return this.clone();
		}

		if (area instanceof FGEShape) {
			return AreaComputation.computeShapeSubstraction(this, (FGEShape<?>) area);
		}

		return new FGESubstractionArea(this, area, isStrict);
	}

	@Override
	public FGEArea exclusiveOr(FGEArea area) {
		// TODO
		return new FGEExclusiveOrArea(this, area);
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
		Vector<FGEArea> newObjects = new Vector<>();
		for (FGEShape<?> a : shapes) {
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
		for (FGEShape<?> a : shapes) {
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
		for (FGEShape<?> o : shapes) {
			pts.add(o.nearestPointFrom(from, orientation));
		}
		return FGEPoint.getNearestPoint(from, pts);
	}

	@Override
	public FGERectangle getBoundingBox() {
		return new FGERectangle(x, y, width, height);
	}

	@Override
	public FGEShapeUnion clone() {
		return (FGEShapeUnion) super.clone();
	}

	@Override
	public List<FGEPoint> getControlPoints() {
		List<FGEPoint> returned = new ArrayList<>();
		for (FGEShape<?> o : shapes) {
			returned.addAll(o.getControlPoints());
		}
		return returned;
	}

	@Override
	public String getStringRepresentation() {
		StringBuffer sb = new StringBuffer();
		sb.append("FGEShapeUnion: nObjects=" + shapes.size() + "\n");
		for (int i = 0; i < shapes.size(); i++) {
			sb.append(" " + (i + 1) + " > " + shapes.get(i) + "\n");
		}
		return sb.toString();
	}

	@Override
	public boolean getIsFilled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setIsFilled(boolean aFlag) {
		// TODO Auto-generated method stub
	}

	@Override
	public FGEPoint nearestOutlinePoint(FGEPoint aPoint) {
		Vector<FGEPoint> pts = new Vector<>();
		for (FGEShape<?> o : shapes) {
			pts.add(o.nearestOutlinePoint(aPoint));
		}
		return FGEPoint.getNearestPoint(aPoint, pts);
	}

	@Override
	public FGEPoint getCenter() {
		return new FGEPoint(getCenterX(), getCenterY());
	}

	/**
	 * Return background eventually overriding default background (usefull in ShapeUnion)<br>
	 * Default value is null
	 * 
	 * @return
	 */
	@Override
	public BGStyle getBackground() {
		return background;
	}

	/**
	 * Sets background eventually overriding default background (usefull in ShapeUnion)<br>
	 * 
	 * @param aBackground
	 */
	@Override
	public void setBackground(BGStyle aBackground) {
		this.background = aBackground;
	}

	/**
	 * Return foreground eventually overriding default foreground (usefull in ShapeUnion)<br>
	 * Default value is null
	 * 
	 * @return
	 */
	@Override
	public FGStyle getForeground() {
		return foreground;
	}

	/**
	 * Sets foreground eventually overriding default foreground (usefull in ShapeUnion)<br>
	 * 
	 * @param aForeground
	 */
	@Override
	public void setForeground(FGStyle aForeground) {
		this.foreground = aForeground;
	}

}
