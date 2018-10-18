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
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaHalfLine;
import org.openflexo.diana.geom.area.DianaIntersectionArea;
import org.openflexo.diana.geom.area.DianaSubstractionArea;
import org.openflexo.diana.geom.area.DianaUnionArea;
import org.openflexo.diana.graphics.AbstractDianaGraphics;
import org.openflexo.diana.graphics.BGStyle;
import org.openflexo.diana.graphics.FGStyle;

public class DianaGeneralShape<O extends DianaGeneralShape<O>> implements DianaShape<O> {

	private static final Logger logger = Logger.getLogger(DianaGeneralShape.class.getPackage().getName());

	private Vector<GeneralShapePathElement<?>> pathElements;
	private Closure closure;
	private GeneralPath _generalPath;
	private DianaPoint currentPoint;
	private DianaPoint startPoint;

	private Vector<DianaPoint> _controlPoints;

	private FGStyle foreground;
	private BGStyle background;

	public static enum Closure {
		OPEN_NOT_FILLED, CLOSED_NOT_FILLED, OPEN_FILLED, CLOSED_FILLED;
	}

	public static interface GeneralShapePathElement<E extends GeneralShapePathElement<E>> extends DianaGeometricObject<E> {
		public DianaPoint getP1();

		public DianaPoint getP2();

		public DianaPoint nearestOutlinePoint(DianaPoint aPoint);
	}

	public DianaGeneralShape() {
		this(Closure.OPEN_NOT_FILLED);
	}

	public DianaGeneralShape(Closure aClosure) {
		super();
		closure = aClosure;
		pathElements = new Vector<>();
		_generalPath = new GeneralPath();
		_controlPoints = new Vector<>();
	}

	/*public DianaGeneralShape(Closure aClosure, GeneralPath generalPath) {
		this(aClosure);
		logger.warning("DianaGeneralShape from generalPath not implemented yet");
	}*/

	public Vector<GeneralShapePathElement<?>> getPathElements() {
		return pathElements;
	}

	public void setPathElements(Vector<GeneralShapePathElement<?>> elements) {
		currentPoint = null;
		for (GeneralShapePathElement<?> e : elements) {
			addToPathElements(e);
		}
	}

	public void addToPathElements(GeneralShapePathElement<?> element) {
		if (element instanceof DianaSegment) {
			if (currentPoint == null) {
				beginAtPoint(((DianaSegment) element).getP1());
			}
			addSegment(((DianaSegment) element).getP2());
		}
		else if (element instanceof DianaQuadCurve) {
			if (currentPoint == null) {
				beginAtPoint(((DianaQuadCurve) element).getP1());
			}
			addQuadCurve(((DianaQuadCurve) element).getCtrlPoint(), ((DianaQuadCurve) element).getP2());
		}
		else if (element instanceof DianaCubicCurve) {
			if (currentPoint == null) {
				beginAtPoint(((DianaCubicCurve) element).getP1());
			}
			addCubicCurve(((DianaCubicCurve) element).getCtrlP1(), ((DianaCubicCurve) element).getCtrlP2(),
					((DianaCubicCurve) element).getP2());
		}
	}

	public void removeFromPathElements(GeneralShapePathElement<?> element) {
		logger.warning("Not implemented yet");
	}

	public GeneralShapePathElement<?> getElementAt(int index) {
		return pathElements.get(index);
	}

	public void beginAtPoint(DianaPoint p) {
		pathElements.clear();
		startPoint = p;
		currentPoint = startPoint;
	}

	public DianaPoint getStartPoint() {
		return startPoint;
	}

	public void addSegment(Point2D p) {
		addSegment(new DianaPoint(p));
	}

	public void addSegment(DianaPoint p) {
		if (currentPoint == null) {
			throw new IllegalArgumentException("No current point defined");
		}
		pathElements.add(new DianaSegment(currentPoint, p));
		currentPoint = p;
		updateGeneralPath();
	}

	public void addQuadCurve(Point2D cp, Point2D p) {
		addQuadCurve(new DianaPoint(cp), new DianaPoint(p));
	}

	public void addQuadCurve(DianaPoint cp, DianaPoint p) {
		if (currentPoint == null) {
			throw new IllegalArgumentException("No current point defined");
		}
		pathElements.add(new DianaQuadCurve(currentPoint, cp, p));
		currentPoint = p;
		updateGeneralPath();
	}

	public void addCubicCurve(Point2D cp1, Point2D cp2, Point2D p) {
		addCubicCurve(new DianaPoint(cp1), new DianaPoint(cp2), new DianaPoint(p));
	}

	public void addCubicCurve(DianaPoint cp1, DianaPoint cp2, DianaPoint p) {
		if (currentPoint == null) {
			throw new IllegalArgumentException("No current point defined");
		}
		pathElements.add(new DianaCubicCurve(currentPoint, cp1, cp2, p));
		currentPoint = p;
		updateGeneralPath();
	}

	public Closure getClosure() {
		return closure;
	}

	public void setClosure(Closure aClosure) {
		if (aClosure != closure) {
			this.closure = aClosure;
			updateGeneralPath();
		}
	}

	@Override
	public boolean getIsFilled() {
		return getClosure() == Closure.OPEN_NOT_FILLED || getClosure() == Closure.CLOSED_NOT_FILLED;
	}

	@Override
	public void setIsFilled(boolean filled) {
		if (filled) {
			if (getClosure() == Closure.OPEN_NOT_FILLED) {
				setClosure(Closure.OPEN_FILLED);
			}
			else if (getClosure() == Closure.CLOSED_NOT_FILLED) {
				setClosure(Closure.CLOSED_FILLED);
			}
		}
		else {
			if (getClosure() == Closure.OPEN_FILLED) {
				setClosure(Closure.OPEN_NOT_FILLED);
			}
			else if (getClosure() == Closure.CLOSED_FILLED) {
				setClosure(Closure.CLOSED_NOT_FILLED);
			}
		}
	}

	public void refresh() {
		updateGeneralPath();
	}

	private void updateGeneralPath() {
		_controlPoints.clear();
		_generalPath = new GeneralPath();
		DianaPoint current = null;
		for (GeneralShapePathElement<?> e : pathElements) {
			if (e instanceof DianaSegment) {
				if (current == null) {
					current = ((DianaSegment) e).getP1();
					_generalPath.moveTo((float) current.x, (float) current.y);
					_controlPoints.add(current);
				}
				current = ((DianaSegment) e).getP2();
				_controlPoints.add(current);
				_generalPath.lineTo((float) current.x, (float) current.y);
			}
			else if (e instanceof DianaQuadCurve) {
				if (current == null) {
					current = ((DianaQuadCurve) e).getP1();
					_generalPath.moveTo((float) current.x, (float) current.y);
					_controlPoints.add(current);
				}
				DianaPoint cp = ((DianaQuadCurve) e).getCtrlPoint();
				current = ((DianaQuadCurve) e).getP2();
				_controlPoints.add(current);
				_generalPath.quadTo((float) cp.x, (float) cp.y, (float) current.x, (float) current.y);
			}
			else if (e instanceof DianaCubicCurve) {
				if (current == null) {
					current = ((DianaCubicCurve) e).getP1();
					_generalPath.moveTo((float) current.x, (float) current.y);
					_controlPoints.add(current);
				}
				DianaPoint cp1 = ((DianaCubicCurve) e).getCtrlP1();
				DianaPoint cp2 = ((DianaCubicCurve) e).getCtrlP2();
				current = ((DianaCubicCurve) e).getP2();
				_controlPoints.add(current);
				_generalPath.curveTo((float) cp1.x, (float) cp1.y, (float) cp2.x, (float) cp2.y, (float) current.x, (float) current.y);
			}
		}
		if (pathElements.size() > 0 && (closure == Closure.CLOSED_FILLED || closure == Closure.CLOSED_NOT_FILLED)) {
			_generalPath.closePath();
		}
		_generalPath.setWindingRule(Path2D.WIND_NON_ZERO);
	}

	@Override
	public List<DianaPoint> getControlPoints() {
		return _controlPoints;
	}

	@Override
	public String getStringRepresentation() {
		return "DianaGeneralShape: " + pathElements;
	}

	@Override
	public boolean containsArea(DianaArea a) {
		return false;
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		/*if (l instanceof DianaSegment) {
			return containsPoint(((DianaSegment)l).getP1()) && containsPoint(((DianaSegment)l).getP2());
		}*/
		// Obviously false: might be concave
		return false;
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		if (p != null) {
			return contains(p.x, p.y);
		}
		return false;
	}

	@Override
	public DianaArea exclusiveOr(DianaArea area) {
		return this;
	}

	@Override
	public DianaArea getAnchorAreaFrom(org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection direction) {
		return this;
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint aPoint) {
		return nearestOutlinePoint(aPoint);
	}

	@Override
	public DianaPoint nearestOutlinePoint(DianaPoint aPoint) {
		DianaPoint returnedPoint = null;
		double smallestDistance = Double.POSITIVE_INFINITY;

		for (GeneralShapePathElement<?> generalShapePathElement : getPathElements()) {
			DianaPoint nearestPoint = generalShapePathElement.nearestOutlinePoint(aPoint);
			double sqDistanceToSegment = DianaSegment.getLength(aPoint, nearestPoint);
			if (sqDistanceToSegment < smallestDistance) {
				returnedPoint = nearestPoint;
				smallestDistance = sqDistanceToSegment;
			}
		}
		return returnedPoint;
	}

	@Override
	public DianaArea getOrthogonalPerspectiveArea(org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection orientation) {
		return null;
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		g.setDefaultBackgroundStyle(this);
		g.setDefaultForegroundStyle(this);

		if (closure == Closure.OPEN_FILLED || closure == Closure.CLOSED_FILLED) {
			g.useDefaultBackgroundStyle();
			g.fillGeneralShape(this);
		}
		g.useDefaultForegroundStyle();
		g.drawGeneralShape(this);
	}

	@Override
	public DianaGeneralShape<O> transform(AffineTransform t) {
		DianaGeneralShape<O> returned = new DianaGeneralShape<O>(getClosure());
		for (GeneralShapePathElement<?> pathElement : getPathElements()) {
			returned.addToPathElements((GeneralShapePathElement<?>) pathElement.transform(t));
		}
		returned.setForeground(getForeground());
		returned.setBackground(getBackground());
		return returned;
	}

	@Override
	public DianaArea intersect(DianaArea area) {

		if (area.containsArea(this)) {
			return this.clone();
		}
		if (containsArea(area)) {
			return area.clone();
		}

		if (area instanceof DianaShape) {
			return AreaComputation.computeShapeIntersection(this, (DianaShape<?>) area);
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
	public DianaArea union(DianaArea area) {
		if (containsArea(area)) {
			return clone();
		}
		if (area.containsArea(this)) {
			return area.clone();
		}

		if (area instanceof DianaShape) {
			return AreaComputation.computeShapeUnion(this, (DianaShape<?>) area);
		}

		return new DianaUnionArea(this, area);
	}

	@Override
	public DianaRectangle getBoundingBox() {
		Rectangle2D r = getGeneralPath().getBounds2D();
		return new DianaRectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight(), Filling.FILLED);
	}

	@Override
	public DianaPoint getCenter() {
		return getBoundingBox().getCenter();
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
		@SuppressWarnings("unused")
		DianaHalfLine hl = DianaHalfLine.makeHalfLine(from, orientation);

		// TODO not implemented
		return null;
	}

	@Override
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return false;
	}

	@Override
	public boolean contains(double x, double y) {
		return _generalPath.contains(x, y);
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return _generalPath.contains(x, y, w, h);
	}

	@Override
	public Rectangle getBounds() {
		return _generalPath.getBounds();
	}

	@Override
	public Rectangle2D getBounds2D() {
		return _generalPath.getBounds2D();
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return _generalPath.getPathIterator(at);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return _generalPath.getPathIterator(at, flatness);
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return _generalPath.intersects(r);
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return _generalPath.intersects(x, y, w, h);
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
	@SuppressWarnings("unchecked")
	@Override
	public DianaGeneralShape<O> clone() {
		try {
			return (DianaGeneralShape<O>) super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	public GeneralPath getGeneralPath() {
		return _generalPath;
	}

	@Override
	public int hashCode() {
		int res = 27;
		for (int i = 0; i < getPathElements().size(); i++) {
			res += getElementAt(i).hashCode();
		}
		return res;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaGeneralShape) {
			DianaGeneralShape<?> p = (DianaGeneralShape<?>) obj;
			if (getClosure() != p.getClosure()) {
				return false;
			}
			if (getPathElements().size() != p.getPathElements().size()) {
				return false;
			}
			for (int i = 0; i < getPathElements().size(); i++) {
				if (!getElementAt(i).equals(p.getElementAt(i))) {
					return false;
				}
			}
			return true;
		}
		return super.equals(obj);
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

	@Override
	public String toString() {
		return "DianaGeneralShape: start=" + getStartPoint() + " elements=" + getPathElements();
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
