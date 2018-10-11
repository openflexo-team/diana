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

import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.geom.area.FGEIntersectionArea;
import org.openflexo.fge.geom.area.FGESubstractionArea;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graphics.AbstractFGEGraphics;
import org.openflexo.fge.graphics.BGStyle;
import org.openflexo.fge.graphics.FGStyle;

public class FGEGeneralShape<O extends FGEGeneralShape<O>> implements FGEShape<O> {

	private static final Logger logger = Logger.getLogger(FGEGeneralShape.class.getPackage().getName());

	private Vector<GeneralShapePathElement<?>> pathElements;
	private Closure closure;
	private GeneralPath _generalPath;
	private FGEPoint currentPoint;
	private FGEPoint startPoint;

	private Vector<FGEPoint> _controlPoints;

	private FGStyle foreground;
	private BGStyle background;

	public static enum Closure {
		OPEN_NOT_FILLED, CLOSED_NOT_FILLED, OPEN_FILLED, CLOSED_FILLED;
	}

	public static interface GeneralShapePathElement<E extends GeneralShapePathElement<E>> extends FGEGeometricObject<E> {
		public FGEPoint getP1();

		public FGEPoint getP2();

		public FGEPoint nearestOutlinePoint(FGEPoint aPoint);
	}

	public FGEGeneralShape() {
		this(Closure.OPEN_NOT_FILLED);
	}

	public FGEGeneralShape(Closure aClosure) {
		super();
		closure = aClosure;
		pathElements = new Vector<>();
		_generalPath = new GeneralPath();
		_controlPoints = new Vector<>();
	}

	/*public FGEGeneralShape(Closure aClosure, GeneralPath generalPath) {
		this(aClosure);
		logger.warning("FGEGeneralShape from generalPath not implemented yet");
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
		if (element instanceof FGESegment) {
			if (currentPoint == null) {
				beginAtPoint(((FGESegment) element).getP1());
			}
			addSegment(((FGESegment) element).getP2());
		}
		else if (element instanceof FGEQuadCurve) {
			if (currentPoint == null) {
				beginAtPoint(((FGEQuadCurve) element).getP1());
			}
			addQuadCurve(((FGEQuadCurve) element).getCtrlPoint(), ((FGEQuadCurve) element).getP2());
		}
		else if (element instanceof FGECubicCurve) {
			if (currentPoint == null) {
				beginAtPoint(((FGECubicCurve) element).getP1());
			}
			addCubicCurve(((FGECubicCurve) element).getCtrlP1(), ((FGECubicCurve) element).getCtrlP2(), ((FGECubicCurve) element).getP2());
		}
	}

	public void removeFromPathElements(GeneralShapePathElement<?> element) {
		logger.warning("Not implemented yet");
	}

	public GeneralShapePathElement<?> getElementAt(int index) {
		return pathElements.get(index);
	}

	public void beginAtPoint(FGEPoint p) {
		pathElements.clear();
		startPoint = p;
		currentPoint = startPoint;
	}

	public FGEPoint getStartPoint() {
		return startPoint;
	}

	public void addSegment(Point2D p) {
		addSegment(new FGEPoint(p));
	}

	public void addSegment(FGEPoint p) {
		if (currentPoint == null) {
			throw new IllegalArgumentException("No current point defined");
		}
		pathElements.add(new FGESegment(currentPoint, p));
		currentPoint = p;
		updateGeneralPath();
	}

	public void addQuadCurve(Point2D cp, Point2D p) {
		addQuadCurve(new FGEPoint(cp), new FGEPoint(p));
	}

	public void addQuadCurve(FGEPoint cp, FGEPoint p) {
		if (currentPoint == null) {
			throw new IllegalArgumentException("No current point defined");
		}
		pathElements.add(new FGEQuadCurve(currentPoint, cp, p));
		currentPoint = p;
		updateGeneralPath();
	}

	public void addCubicCurve(Point2D cp1, Point2D cp2, Point2D p) {
		addCubicCurve(new FGEPoint(cp1), new FGEPoint(cp2), new FGEPoint(p));
	}

	public void addCubicCurve(FGEPoint cp1, FGEPoint cp2, FGEPoint p) {
		if (currentPoint == null) {
			throw new IllegalArgumentException("No current point defined");
		}
		pathElements.add(new FGECubicCurve(currentPoint, cp1, cp2, p));
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
		FGEPoint current = null;
		for (GeneralShapePathElement<?> e : pathElements) {
			if (e instanceof FGESegment) {
				if (current == null) {
					current = ((FGESegment) e).getP1();
					_generalPath.moveTo((float) current.x, (float) current.y);
					_controlPoints.add(current);
				}
				current = ((FGESegment) e).getP2();
				_controlPoints.add(current);
				_generalPath.lineTo((float) current.x, (float) current.y);
			}
			else if (e instanceof FGEQuadCurve) {
				if (current == null) {
					current = ((FGEQuadCurve) e).getP1();
					_generalPath.moveTo((float) current.x, (float) current.y);
					_controlPoints.add(current);
				}
				FGEPoint cp = ((FGEQuadCurve) e).getCtrlPoint();
				current = ((FGEQuadCurve) e).getP2();
				_controlPoints.add(current);
				_generalPath.quadTo((float) cp.x, (float) cp.y, (float) current.x, (float) current.y);
			}
			else if (e instanceof FGECubicCurve) {
				if (current == null) {
					current = ((FGECubicCurve) e).getP1();
					_generalPath.moveTo((float) current.x, (float) current.y);
					_controlPoints.add(current);
				}
				FGEPoint cp1 = ((FGECubicCurve) e).getCtrlP1();
				FGEPoint cp2 = ((FGECubicCurve) e).getCtrlP2();
				current = ((FGECubicCurve) e).getP2();
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
	public List<FGEPoint> getControlPoints() {
		return _controlPoints;
	}

	@Override
	public String getStringRepresentation() {
		return "FGEGeneralShape: " + pathElements;
	}

	@Override
	public boolean containsArea(FGEArea a) {
		return false;
	}

	@Override
	public boolean containsLine(FGEAbstractLine<?> l) {
		/*if (l instanceof FGESegment) {
			return containsPoint(((FGESegment)l).getP1()) && containsPoint(((FGESegment)l).getP2());
		}*/
		// Obviously false: might be concave
		return false;
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		if (p != null) {
			return contains(p.x, p.y);
		}
		return false;
	}

	@Override
	public FGEArea exclusiveOr(FGEArea area) {
		return this;
	}

	@Override
	public FGEArea getAnchorAreaFrom(org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection direction) {
		return this;
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		// return null;

		return nearestOutlinePoint(aPoint);
	}

	@Override
	public FGEPoint nearestOutlinePoint(FGEPoint aPoint) {
		FGEPoint returnedPoint = null;
		double smallestDistance = Double.POSITIVE_INFINITY;

		for (GeneralShapePathElement<?> generalShapePathElement : getPathElements()) {
			FGEPoint nearestPoint = generalShapePathElement.nearestOutlinePoint(aPoint);
			double sqDistanceToSegment = FGESegment.getLength(aPoint, nearestPoint);
			if (sqDistanceToSegment < smallestDistance) {
				returnedPoint = nearestPoint;
				smallestDistance = sqDistanceToSegment;
			}
		}
		return returnedPoint;
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection orientation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void paint(AbstractFGEGraphics g) {
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
	public FGEGeneralShape<O> transform(AffineTransform t) {
		FGEGeneralShape<O> returned = new FGEGeneralShape<O>(getClosure());
		for (GeneralShapePathElement<?> pathElement : getPathElements()) {
			returned.addToPathElements((GeneralShapePathElement<?>) pathElement.transform(t));
		}
		returned.setForeground(getForeground());
		returned.setBackground(getBackground());
		return returned;
	}

	@Override
	public FGEArea intersect(FGEArea area) {

		if (area.containsArea(this)) {
			return this.clone();
		}
		if (containsArea(area)) {
			return area.clone();
		}

		if (area instanceof FGEShape) {
			return AreaComputation.computeShapeIntersection(this, (FGEShape<?>) area);
		}

		FGEIntersectionArea returned = new FGEIntersectionArea(this, area);
		if (returned.isDevelopable()) {
			return returned.makeDevelopped();
		}
		else {
			return returned;
		}
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
	public FGEArea union(FGEArea area) {
		if (containsArea(area)) {
			return clone();
		}
		if (area.containsArea(this)) {
			return area.clone();
		}

		if (area instanceof FGEShape) {
			return AreaComputation.computeShapeUnion(this, (FGEShape<?>) area);
		}

		return new FGEUnionArea(this, area);
	}

	@Override
	public FGERectangle getBoundingBox() {
		Rectangle2D r = getGeneralPath().getBounds2D();
		return new FGERectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight(), Filling.FILLED);
	}

	@Override
	public FGEPoint getCenter() {
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
	public FGEPoint nearestPointFrom(FGEPoint from, SimplifiedCardinalDirection orientation) {
		@SuppressWarnings("unused")
		FGEHalfLine hl = FGEHalfLine.makeHalfLine(from, orientation);

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
	public FGEGeneralShape<O> clone() {
		try {
			return (FGEGeneralShape<O>) super.clone();
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
		if (obj instanceof FGEGeneralShape) {
			FGEGeneralShape<?> p = (FGEGeneralShape<?>) obj;
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
	public final FGERectangle getEmbeddingBounds() {
		return getBoundingBox();
	}

	@Override
	public String toString() {
		return "FGEGeneralShape: start=" + getStartPoint() + " elements=" + getPathElements();
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
