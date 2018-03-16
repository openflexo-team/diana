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

import org.openflexo.diana.geom.FGEAbstractLine;
import org.openflexo.diana.geom.FGEGeometricObject;
import org.openflexo.diana.geom.FGELine;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.geom.FGERectangle;
import org.openflexo.diana.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.graphics.AbstractFGEGraphics;

public class FGEGrid implements FGEArea {

	private static final Logger logger = Logger.getLogger(FGEGrid.class.getPackage().getName());

	public FGEPoint origin;
	public double hStep;
	public double vStep;

	public FGEGrid() {
		this(new FGEPoint(0, 0), 1.0, 1.0);
	}

	public FGEGrid(FGEPoint origin, double hStep, double vStep) {
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
	public boolean containsArea(FGEArea a) {
		if (a instanceof FGEPoint) {
			return containsPoint((FGEPoint) a);
		}
		if (a instanceof FGEGrid) {
			FGEGrid grid = (FGEGrid) a;
			return grid.origin.equals(origin) && grid.hStep == hStep && grid.vStep == vStep;
		}
		return false;
	}

	@Override
	public boolean containsLine(FGEAbstractLine<?> l) {
		return false;
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		return Math.abs(Math.IEEEremainder(p.x - origin.x, hStep)) < FGEGeometricObject.EPSILON
				&& Math.abs(Math.IEEEremainder(p.y - origin.y, vStep)) < FGEGeometricObject.EPSILON;
	}

	@Override
	public FGEArea exclusiveOr(FGEArea area) {
		return new FGEExclusiveOrArea(this, area);
	}

	@Override
	public FGEArea getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		return clone();
	}

	@Override
	public FGERectangle getEmbeddingBounds() {
		// Infinite--> null
		return null;
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint point) {
		FGEPoint translatedPoint = new FGEPoint(point.x - origin.x, point.y - origin.y);
		FGEPoint remainderPoint = new FGEPoint(Math.IEEEremainder(translatedPoint.x, hStep), Math.IEEEremainder(translatedPoint.y, vStep));
		FGEPoint ulPoint = new FGEPoint(point.x - remainderPoint.x, point.y - remainderPoint.y);

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
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FGEArea intersect(FGEArea area) {
		return null;
	}

	@Override
	public boolean isFinite() {
		return false;
	}

	@Override
	public FGEPoint nearestPointFrom(FGEPoint from, SimplifiedCardinalDirection orientation) {
		return getNearestPoint(from).nearestPointFrom(from, orientation);
	}

	@Override
	public void paint(AbstractFGEGraphics g) {

		FGERectangle bounds = g.getNodeNormalizedBounds();

		int nx = (int) (bounds.getWidth() / hStep);
		int ny = (int) (bounds.getHeight() / vStep);

		for (int i = 0; i < nx; i++) {
			FGELine l = new FGELine(new FGEPoint(i * hStep, 0), new FGEPoint(i * hStep, 1));
			bounds.intersect(l).paint(g);
		}
		for (int j = 0; j < ny; j++) {
			FGELine l = new FGELine(new FGEPoint(0, j * vStep), new FGEPoint(1, j * vStep));
			bounds.intersect(l).paint(g);
		}

	}

	@Override
	public FGEArea substract(FGEArea area, boolean isStrict) {
		return new FGESubstractionArea(this, area, isStrict);
	}

	@Override
	public FGEArea transform(AffineTransform t) {
		return clone();
	}

	@Override
	public FGEArea union(FGEArea area) {
		return new FGEUnionArea(this, area);
	}

	@Override
	public FGEGrid clone() {
		return new FGEGrid(new FGEPoint(origin), hStep, vStep);
	}

}
