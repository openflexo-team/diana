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
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.diana.geom.AreaComputation;
import org.openflexo.diana.geom.DianaAbstractLine;
import org.openflexo.diana.geom.DianaGeometricObject;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

public class DianaSubstractionArea extends DianaOperationArea {

	private static final Logger logger = Logger.getLogger(DianaSubstractionArea.class.getPackage().getName());

	// private static final DianaModelFactory FACTORY = GeomUtils.TOOLS_FACTORY;
	// private static final BackgroundStyle BACKGROUND = FACTORY.makeColoredBackground(java.awt.Color.GRAY);

	private final DianaArea containerArea;
	private final DianaArea substractedArea;
	private final boolean isStrict;

	/**
	 * Build a new DianaSubstractionArea given container area and substracted area. Really build this operation area without trying to
	 * compute or simplify it.
	 * 
	 * @param containerArea
	 *            container area
	 * @param substractedArea
	 *            area to substract to container area
	 * @param isStrict
	 *            boolean indicating if a point located in the border of substracted area should be consider inside resulting area or not If
	 * 
	 *            <pre>
	 *            isStrict
	 *            </pre>
	 * 
	 *            is true, point should NOT be considered.
	 */
	public DianaSubstractionArea(DianaArea containerArea, DianaArea substractedArea, boolean isStrict) {
		super();
		this.containerArea = containerArea;
		this.substractedArea = substractedArea;
		this.isStrict = isStrict;
	}

	/**
	 * Build a new Area given container area and substracted area. Try to compute, and simplify resulting area
	 * 
	 * @param containerArea
	 *            container area
	 * @param substractedArea
	 *            area to substract to container area
	 * @param isStrict
	 *            boolean indicating if a point located in the border of substracted area should be consider inside resulting area or not If
	 * 
	 *            <pre>
	 *            isStrict
	 *            </pre>
	 * 
	 *            is true, point should NOT be considered.
	 * @return
	 */
	public static DianaArea makeSubstraction(DianaArea containerArea, DianaArea substractedArea, boolean isStrict) {
		return makeSubstraction(containerArea, substractedArea, isStrict, true);
	}

	/**
	 * Build a new Area given container area and substracted area. Try to compute, and simplify resulting area
	 * 
	 * @param containerArea
	 *            container area
	 * @param substractedArea
	 *            area to substract to container area
	 * @param isStrict
	 *            boolean indicating if a point located in the border of substracted area should be consider inside resulting area or not If
	 * 
	 *            <pre>
	 *            isStrict
	 *            </pre>
	 * 
	 *            is true, point should NOT be considered.
	 * @param checkNonNullIntersection
	 *            indicates if non-null intersection should be checked (take care about infinite loop !)
	 * @return
	 */
	protected static DianaArea makeSubstraction(DianaArea containerArea, DianaArea substractedArea, boolean isStrict,
			boolean checkNonNullIntersection) {
		if (containerArea instanceof DianaEmptyArea) {
			return new DianaEmptyArea();
		}
		if (substractedArea instanceof DianaEmptyArea) {
			return containerArea.clone();
		}
		if (checkNonNullIntersection && containerArea.intersect(substractedArea) instanceof DianaEmptyArea) {
			return containerArea.clone();
		}
		if (substractedArea.containsArea(containerArea)) {
			return new DianaEmptyArea();
		}
		if (containerArea instanceof DianaUnionArea) {
			List<DianaArea> objects = new ArrayList<>();
			DianaUnionArea union = (DianaUnionArea) containerArea;
			for (DianaArea a : union.getObjects()) {
				if (substractedArea.containsArea(a)) {
					continue;
				}
				objects.add(makeSubstraction(a, substractedArea, isStrict));
			}
			if (objects.size() == 0) {
				return new DianaEmptyArea();
			}
			else if (objects.size() == 1) {
				return objects.get(0);
			}
			else {
				return DianaUnionArea.makeUnion(objects);
			}
		}
		if (containerArea instanceof DianaShape && substractedArea instanceof DianaShape) {
			return AreaComputation.computeShapeSubstraction((DianaShape<?>) containerArea, (DianaShape<?>) substractedArea);
		}
		return new DianaSubstractionArea(containerArea, substractedArea, isStrict);
	}

	@Override
	public String toString() {
		return "DianaSubstractionArea: " + containerArea + "-" + substractedArea;
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		return containerArea.containsPoint(p)
				&& (!substractedArea.containsPoint(p) || !isStrict() && isPointLocatedOnSubstractedAreaBorder(p));
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		return containsPoint(l.getP1()) && containsPoint(l.getP2());
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

	protected boolean isPointLocatedOnSubstractedAreaBorder(DianaPoint testPoint) {
		if (!substractedArea.containsPoint(testPoint)) {
			return false;
		}

		if (substractedArea instanceof DianaShape) {
			return ((DianaShape<?>) substractedArea).nearestOutlinePoint(testPoint).equals(testPoint);
		}
		else {
			// Little hack
			// Test with 4 points located juste near this point (at 2*EPSILON, which is the equals criteria)
			// If one of those point is not located inside substracted area, this means
			// that test point was "borderline"
			DianaPoint p1 = new DianaPoint(testPoint.x - 2 * DianaGeometricObject.EPSILON, testPoint.y);
			if (!substractedArea.containsPoint(p1)) {
				return true;
			}
			DianaPoint p2 = new DianaPoint(testPoint.x + 2 * DianaGeometricObject.EPSILON, testPoint.y);
			if (!substractedArea.containsPoint(p2)) {
				return true;
			}
			DianaPoint p3 = new DianaPoint(testPoint.x, testPoint.y - 2 * DianaGeometricObject.EPSILON);
			if (!substractedArea.containsPoint(p3)) {
				return true;
			}
			DianaPoint p4 = new DianaPoint(testPoint.x, testPoint.y + 2 * DianaGeometricObject.EPSILON);
			if (!substractedArea.containsPoint(p4)) {
				return true;
			}
			return false;
		}
	}

	@Override
	public DianaSubstractionArea transform(AffineTransform t) {
		return new DianaSubstractionArea(containerArea.transform(t), substractedArea.transform(t), isStrict);
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		// TODO
		// Use a finite method, using Java2D to perform shape computation
		// in the area defined by supplied DianaGraphics

		getContainerArea().paint(g);

		// TODO: restore paint

		/*BackgroundStyle old = g.getDefaultBackground();
		BackgroundStyle bs = BACKGROUND;
		bs.setUseTransparency(true);
		g.setDefaultBackground(bs);
		getSubstractedArea().paint(g);
		g.setDefaultBackground(old);*/

		g.useDefaultBackgroundStyle();
		getSubstractedArea().paint(g);

	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		}

		DianaPoint returned = containerArea.getNearestPoint(aPoint);
		if (returned == null) {
			return null;
		}
		if (!substractedArea.containsPoint(returned)) {
			return returned;
		}

		// We have an other chance here !
		if (substractedArea instanceof DianaShape && !isStrict()) {
			DianaPoint outlinePoint = ((DianaShape<?>) substractedArea).nearestOutlinePoint(aPoint);
			if (containsPoint(outlinePoint)) {
				return outlinePoint;
			}
		}

		// TODO: we can implement a recursive method, trying to invoke getNearest() alternatively
		// on each objects, to obtain a possible result (not sure, but works on most cases)
		logger.warning("Not implemented yet !!!!");
		return null;
	}

	/**
	 * Return a flag indicating if this area is finite or not
	 * 
	 * @return
	 */
	@Override
	public final boolean isFinite() {
		return containerArea.isFinite();
		// TODO: doesn't handle the case where area2 makes this operation finite
	}

	/**
	 * If this area is finite, return embedding bounds as a DianaRectangle (this is not guaranteed to be optimal in some cases). For
	 * non-finite areas (if this area is not finite), return null
	 * 
	 * @return
	 */
	@Override
	public final DianaRectangle getEmbeddingBounds() {
		// TODO: Sub-optimal, but sufficient for now
		return containerArea.getEmbeddingBounds();
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

		DianaPoint returned = containerArea.nearestPointFrom(from, orientation);
		if (!substractedArea.containsPoint(returned)) {
			return returned;
		}

		// TODO: to implement
		logger.warning("Not implemented yet !!!!");
		return null;
	}

	public DianaArea getContainerArea() {
		return containerArea;
	}

	public DianaArea getSubstractedArea() {
		return substractedArea;
	}

	@Override
	public int hashCode() {
		return getContainerArea().hashCode() + getSubstractedArea().hashCode() + (isStrict ? 1 : 0);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaSubstractionArea) {
			DianaSubstractionArea sub = (DianaSubstractionArea) obj;
			return sub.getContainerArea().equals(getContainerArea()) && sub.getSubstractedArea().equals(getSubstractedArea())
					&& sub.isStrict == isStrict;
		}
		return super.equals(obj);
	}

	/**
	 * Return a boolean indicating if a point located in the border of substracted area should be consider inside resulting area or not. If
	 * 
	 * <pre>
	 * isStrict
	 * </pre>
	 * 
	 * is true, point should NOT be considered.
	 * 
	 * @return
	 */
	public boolean isStrict() {
		return isStrict;
	}

}
