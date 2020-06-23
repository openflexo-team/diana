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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaExclusiveOrArea;
import org.openflexo.diana.geom.area.DianaSubstractionArea;
import org.openflexo.diana.geom.area.DianaUnionArea;
import org.openflexo.diana.graphics.AbstractDianaGraphics;
import org.openflexo.diana.graphics.BGStyle;
import org.openflexo.diana.graphics.FGStyle;

/**
 * Implements a union (a finite collection) of {@link DianaShape}<br>
 * 
 * Note the difference with the {@link DianaUnionArea}: we implement here the {@link DianaShape} API: result is considered as a finite shape
 * 
 * @author sylvain
 *
 */
public class DianaShapeUnion extends Rectangle2D.Double implements DianaShape<DianaShapeUnion> {

	private static final Logger logger = Logger.getLogger(DianaShapeUnion.class.getPackage().getName());

	private List<DianaShape<?>> shapes;
	private FGStyle foreground;
	private BGStyle background;

	public DianaShapeUnion() {
		super();
		shapes = new ArrayList<>();
	}

	public DianaShapeUnion(DianaShape<?>... shapes) {
		this();

		for (DianaShape<?> o : shapes) {
			addToShapes(o);
		}

	}

	public DianaShapeUnion(List<? extends DianaShape<?>> shapes) {
		this();
		for (DianaShape<?> shape : shapes) {
			addToShapes(shape);
		}
	}

	public List<DianaShape<?>> getShapes() {
		return shapes;
	}

	public void setShapes(List<DianaShape<?>> shapes) {
		if (shapes != null) {
			shapes.clear();
		}
		else {
			shapes = new ArrayList<>();
		}
		for (DianaShape<?> shape : shapes) {
			addToShapes(shape);
		}
	}

	public void addToShapes(DianaShape<?> shape) {
		shapes.add(shape);
		updateBounds();
	}

	public void removeFromShapes(DianaShape<?> shape) {
		shapes.remove(shape);
		updateBounds();
	}

	private void updateBounds() {
		DianaRectangle newBounds = null;
		for (DianaShape<?> shape : getShapes()) {
			DianaRectangle boundingBox = shape.getBoundingBox();
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
		sb.append("DianaShapeUnion: nObjects=" + shapes.size() + "\n");
		for (int i = 0; i < shapes.size(); i++) {
			sb.append(" " + (i + 1) + " > " + shapes.get(i)
			/*+ " BG=" + shapes.get(i).getBackground() + " FG="
			+ shapes.get(i).getForeground()*/ + "\n");
		}
		return sb.toString();
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		for (DianaShape<?> a : shapes) {
			if (a.containsPoint(p)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		// TODO: what if Union of two objects contains the line ? Implement this...
		for (DianaShape<?> a : shapes) {
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
	public DianaShapeUnion transform(AffineTransform t) {
		DianaShape<?>[] all = new DianaShape<?>[shapes.size()];
		for (int i = 0; i < shapes.size(); i++) {
			all[i] = (DianaShape<?>) shapes.get(i).transform(t);
		}
		DianaShapeUnion returned = new DianaShapeUnion(all);
		returned.setForeground(getForeground());
		returned.setBackground(getBackground());
		return returned;
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		g.setDefaultBackgroundStyle(this);
		g.setDefaultForegroundStyle(this);

		for (DianaShape<?> a : shapes) {
			a.paint(g);
		}
	}

	@Override
	public boolean containsArea(DianaArea a) {
		for (DianaShape<?> o : shapes) {
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
		if (shapes.size() == 0) {
			return false;
		}
		for (DianaShape<?> a : shapes) {
			if (!aClass.isAssignableFrom(a.getClass())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		}

		Vector<DianaPoint> pts = new Vector<>();
		for (DianaShape<?> o : shapes) {
			pts.add(o.getNearestPoint(aPoint));
		}
		return DianaPoint.getNearestPoint(aPoint, pts);
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

		for (DianaShape<?> o : shapes) {
			intersections.add(o.intersect(area));
		}

		return DianaUnionArea.makeUnion(intersections);
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
		objects.addAll(getShapes());
		objects.add(area);
		return DianaUnionArea.makeUnion(objects, false);
	}

	@Override
	public DianaArea substract(DianaArea area, boolean isStrict) {
		if (area.containsArea(this)) {
			return new DianaEmptyArea();
		}
		if (!containsArea(area)) {
			return this.clone();
		}

		if (area instanceof DianaShape) {
			return AreaComputation.computeShapeSubstraction(this, (DianaShape<?>) area);
		}

		return new DianaSubstractionArea(this, area, isStrict);
	}

	@Override
	public DianaArea exclusiveOr(DianaArea area) {
		// TODO
		return new DianaExclusiveOrArea(this, area);
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
		Vector<DianaArea> newObjects = new Vector<>();
		for (DianaShape<?> a : shapes) {
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
		for (DianaShape<?> a : shapes) {
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
		for (DianaShape<?> o : shapes) {
			pts.add(o.nearestPointFrom(from, orientation));
		}
		return DianaPoint.getNearestPoint(from, pts);
	}

	@Override
	public DianaRectangle getBoundingBox() {
		return new DianaRectangle(x, y, width, height);
	}

	@Override
	public DianaShapeUnion clone() {
		return (DianaShapeUnion) super.clone();
	}

	@Override
	public List<DianaPoint> getControlPoints() {
		List<DianaPoint> returned = new ArrayList<>();
		for (DianaShape<?> o : shapes) {
			returned.addAll(o.getControlPoints());
		}
		return returned;
	}

	@Override
	public String getStringRepresentation() {
		StringBuffer sb = new StringBuffer();
		sb.append("DianaShapeUnion: nObjects=" + shapes.size() + "\n");
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
	public DianaPoint nearestOutlinePoint(DianaPoint aPoint) {
		Vector<DianaPoint> pts = new Vector<>();
		for (DianaShape<?> o : shapes) {
			pts.add(o.nearestOutlinePoint(aPoint));
		}
		return DianaPoint.getNearestPoint(aPoint, pts);
	}

	@Override
	public DianaPoint getCenter() {
		return new DianaPoint(getCenterX(), getCenterY());
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
