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
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.geom.AreaComputation;
import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.graphics.AbstractFGEGraphics;

public class FGESubstractionArea extends FGEOperationArea {

	private static final Logger logger = Logger.getLogger(FGESubstractionArea.class.getPackage().getName());

	// private static final FGEModelFactory FACTORY = GeomUtils.TOOLS_FACTORY;
	// private static final BackgroundStyle BACKGROUND = FACTORY.makeColoredBackground(java.awt.Color.GRAY);

	private final FGEArea containerArea;
	private final FGEArea substractedArea;
	private final boolean isStrict;

	/**
	 * Build a new FGESubstractionArea given container area and substracted area. Really build this operation area without trying to compute
	 * or simplify it.
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
	public FGESubstractionArea(FGEArea containerArea, FGEArea substractedArea, boolean isStrict) {
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
	public static FGEArea makeSubstraction(FGEArea containerArea, FGEArea substractedArea, boolean isStrict) {
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
	protected static FGEArea makeSubstraction(FGEArea containerArea, FGEArea substractedArea, boolean isStrict,
			boolean checkNonNullIntersection) {
		if (containerArea instanceof FGEEmptyArea) {
			return new FGEEmptyArea();
		}
		if (substractedArea instanceof FGEEmptyArea) {
			return containerArea.clone();
		}
		if (checkNonNullIntersection && containerArea.intersect(substractedArea) instanceof FGEEmptyArea) {
			return containerArea.clone();
		}
		if (substractedArea.containsArea(containerArea)) {
			return new FGEEmptyArea();
		}
		if (containerArea instanceof FGEUnionArea) {
			List<FGEArea> objects = new ArrayList<>();
			FGEUnionArea union = (FGEUnionArea) containerArea;
			for (FGEArea a : union.getObjects()) {
				if (substractedArea.containsArea(a)) {
					continue;
				}
				objects.add(makeSubstraction(a, substractedArea, isStrict));
			}
			if (objects.size() == 0) {
				return new FGEEmptyArea();
			}
			else if (objects.size() == 1) {
				return objects.get(0);
			}
			else {
				return FGEUnionArea.makeUnion(objects);
			}
		}
		return new FGESubstractionArea(containerArea, substractedArea, isStrict);
	}

	@Override
	public String toString() {
		return "FGESubstractionArea: " + containerArea + "-" + substractedArea;
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		return containerArea.containsPoint(p)
				&& (!substractedArea.containsPoint(p) || !isStrict() && isPointLocatedOnSubstractedAreaBorder(p));
	}

	@Override
	public boolean containsLine(FGEAbstractLine<?> l) {
		return containsPoint(l.getP1()) && containsPoint(l.getP2());
	}

	@Override
	public boolean containsArea(FGEArea a) {
		if (a instanceof FGEPoint) {
			return containsPoint((FGEPoint) a);
		}
		if (a instanceof FGELine) {
			return containsLine((FGELine) a);
		}
		if (a instanceof FGEShape) {
			return AreaComputation.isShapeContainedInArea((FGEShape<?>) a, this);
		}
		return false;
	}

	protected boolean isPointLocatedOnSubstractedAreaBorder(FGEPoint testPoint) {
		if (!substractedArea.containsPoint(testPoint)) {
			return false;
		}

		if (substractedArea instanceof FGEShape) {
			return ((FGEShape<?>) substractedArea).nearestOutlinePoint(testPoint).equals(testPoint);
		}
		else {
			// Little hack
			// Test with 4 points located juste near this point (at 2*EPSILON, which is the equals criteria)
			// If one of those point is not located inside substracted area, this means
			// that test point was "borderline"
			FGEPoint p1 = new FGEPoint(testPoint.x - 2 * FGEGeometricObject.EPSILON, testPoint.y);
			if (!substractedArea.containsPoint(p1)) {
				return true;
			}
			FGEPoint p2 = new FGEPoint(testPoint.x + 2 * FGEGeometricObject.EPSILON, testPoint.y);
			if (!substractedArea.containsPoint(p2)) {
				return true;
			}
			FGEPoint p3 = new FGEPoint(testPoint.x, testPoint.y - 2 * FGEGeometricObject.EPSILON);
			if (!substractedArea.containsPoint(p3)) {
				return true;
			}
			FGEPoint p4 = new FGEPoint(testPoint.x, testPoint.y + 2 * FGEGeometricObject.EPSILON);
			if (!substractedArea.containsPoint(p4)) {
				return true;
			}
			return false;
		}
	}

	@Override
	public FGESubstractionArea transform(AffineTransform t) {
		return new FGESubstractionArea(containerArea.transform(t), substractedArea.transform(t), isStrict);
	}

	@Override
	public void paint(AbstractFGEGraphics g) {
		// TODO
		// Use a finite method, using Java2D to perform shape computation
		// in the area defined by supplied FGEGraphics

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
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		}

		FGEPoint returned = containerArea.getNearestPoint(aPoint);
		if (returned == null) {
			return null;
		}
		if (!substractedArea.containsPoint(returned)) {
			return returned;
		}

		// We have an other chance here !
		if (substractedArea instanceof FGEShape && !isStrict()) {
			FGEPoint outlinePoint = ((FGEShape<?>) substractedArea).nearestOutlinePoint(aPoint);
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
	 * If this area is finite, return embedding bounds as a FGERectangle (this is not guaranteed to be optimal in some cases). For
	 * non-finite areas (if this area is not finite), return null
	 * 
	 * @return
	 */
	@Override
	public final FGERectangle getEmbeddingBounds() {
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
	public FGEPoint nearestPointFrom(FGEPoint from, SimplifiedCardinalDirection orientation) {
		if (containsPoint(from)) {
			return from.clone();
		}

		FGEPoint returned = containerArea.nearestPointFrom(from, orientation);
		if (!substractedArea.containsPoint(returned)) {
			return returned;
		}

		// TODO: to implement
		logger.warning("Not implemented yet !!!!");
		return null;
	}

	public FGEArea getContainerArea() {
		return containerArea;
	}

	public FGEArea getSubstractedArea() {
		return substractedArea;
	}

	@Override
	public int hashCode() {
		return getContainerArea().hashCode() + getSubstractedArea().hashCode() + (isStrict ? 1 : 0);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGESubstractionArea) {
			FGESubstractionArea sub = (FGESubstractionArea) obj;
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
