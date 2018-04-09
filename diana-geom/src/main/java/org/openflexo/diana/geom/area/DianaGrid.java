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
import java.util.logging.Logger;

import org.openflexo.diana.geom.DianaAbstractLine;
import org.openflexo.diana.geom.DianaGeometricObject;
import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

public class DianaGrid implements DianaArea {

	private static final Logger logger = Logger.getLogger(DianaGrid.class.getPackage().getName());

	public DianaPoint origin;
	public double hStep;
	public double vStep;

	public DianaGrid() {
		this(new DianaPoint(0, 0), 1.0, 1.0);
	}

	public DianaGrid(DianaPoint origin, double hStep, double vStep) {
		this.origin = origin;
		this.hStep = hStep;
		this.vStep = vStep;
	}

	public double getHorizontalStep() {
		return hStep;
	}

	public double getVerticalStep() {
		return vStep;
	}

	@Override
	public boolean containsArea(DianaArea a) {
		if (a instanceof DianaPoint) {
			return containsPoint((DianaPoint) a);
		}
		if (a instanceof DianaGrid) {
			DianaGrid grid = (DianaGrid) a;
			return grid.origin.equals(origin) && grid.hStep == hStep && grid.vStep == vStep;
		}
		return false;
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		return false;
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		return Math.abs(Math.IEEEremainder(p.x - origin.x, hStep)) < DianaGeometricObject.EPSILON
				&& Math.abs(Math.IEEEremainder(p.y - origin.y, vStep)) < DianaGeometricObject.EPSILON;
	}

	@Override
	public DianaArea exclusiveOr(DianaArea area) {
		return new DianaExclusiveOrArea(this, area);
	}

	@Override
	public DianaArea getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		return clone();
	}

	@Override
	public DianaRectangle getEmbeddingBounds() {
		// Infinite--> null
		return null;
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint point) {
		DianaPoint translatedPoint = new DianaPoint(point.x - origin.x, point.y - origin.y);
		DianaPoint remainderPoint = new DianaPoint(Math.IEEEremainder(translatedPoint.x, hStep), Math.IEEEremainder(translatedPoint.y, vStep));
		DianaPoint ulPoint = new DianaPoint(point.x - remainderPoint.x, point.y - remainderPoint.y);

		double distanceUL = point.distance(ulPoint);
		double distanceUR = point.distance(ulPoint.x + hStep, ulPoint.y);
		double distanceLL = point.distance(ulPoint.x, ulPoint.y + vStep);
		double distanceLR = point.distance(ulPoint.x + hStep, ulPoint.y + vStep);
		if (distanceUL <= distanceUR && distanceUL <= distanceLL && distanceUL <= distanceLR) {
			// Upper left is closest
			return ulPoint;
		} else if (distanceUR <= distanceUL && distanceUR <= distanceLL && distanceUR <= distanceLR) {
			// Upper right is closest
			ulPoint.x += hStep;
			return ulPoint;
		} else if (distanceLL <= distanceUR && distanceLL <= distanceUL && distanceLL <= distanceLR) {
			// Lower left is closest
			ulPoint.y += vStep;
			return ulPoint;
		} else {
			// Lower right is closest
			ulPoint.x += hStep;
			ulPoint.y += vStep;
			return ulPoint;
		}
	}

	@Override
	public DianaArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		return null;
	}

	@Override
	public DianaArea intersect(DianaArea area) {
		return null;
	}

	@Override
	public boolean isFinite() {
		return false;
	}

	@Override
	public DianaPoint nearestPointFrom(DianaPoint from, SimplifiedCardinalDirection orientation) {
		return getNearestPoint(from).nearestPointFrom(from, orientation);
	}

	@Override
	public void paint(AbstractDianaGraphics g) {

		DianaRectangle bounds = g.getNodeNormalizedBounds();

		int nx = (int) (bounds.getWidth() / hStep);
		int ny = (int) (bounds.getHeight() / vStep);

		for (int i = 0; i < nx; i++) {
			DianaLine l = new DianaLine(new DianaPoint(i * hStep, 0), new DianaPoint(i * hStep, 1));
			bounds.intersect(l).paint(g);
		}
		for (int j = 0; j < ny; j++) {
			DianaLine l = new DianaLine(new DianaPoint(0, j * vStep), new DianaPoint(1, j * vStep));
			bounds.intersect(l).paint(g);
		}

	}

	@Override
	public DianaArea substract(DianaArea area, boolean isStrict) {
		return new DianaSubstractionArea(this, area, isStrict);
	}

	@Override
	public DianaArea transform(AffineTransform t) {
		return clone();
	}

	@Override
	public DianaArea union(DianaArea area) {
		return new DianaUnionArea(this, area);
	}

	@Override
	public DianaGrid clone() {
		return new DianaGrid(new DianaPoint(origin), hStep, vStep);
	}

}
