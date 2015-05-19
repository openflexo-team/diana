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
import java.util.logging.Logger;

import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.graphics.AbstractFGEGraphics;

public class FGEExclusiveOrArea extends FGEOperationArea {

	private static final Logger logger = Logger.getLogger(FGEExclusiveOrArea.class.getPackage().getName());

	private FGEArea area1;
	private FGEArea area2;

	public FGEExclusiveOrArea(FGEArea area1, FGEArea area2) {
		super();
		this.area1 = area1;
		this.area2 = area2;
	}

	@Override
	public String toString() {
		return "FGEExclusiveOrArea: " + area1 + " XOR " + area2;
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		return area1.containsPoint(p) && !area2.containsPoint(p) || area2.containsPoint(p) && !area1.containsPoint(p);
	}

	@Override
	public boolean containsLine(FGEAbstractLine<?> l) {
		// TODO: do it better
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
			return FGEShape.AreaComputation.isShapeContainedInArea((FGEShape<?>) a, this);
		}
		return false;
	}

	@Override
	public FGEExclusiveOrArea transform(AffineTransform t) {
		return new FGEExclusiveOrArea(area1.transform(t), area2.transform(t));
	}

	@Override
	public void paint(AbstractFGEGraphics g) {
		// TODO
		// Use a finite method, using Java2D to perform shape computation
		// in the area defined by supplied FGEGraphics
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		}

		// TODO: to implement
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
		logger.warning("Not implemented yet !!!!");
		return false;
	}

	/**
	 * If this area is finite, return embedding bounds as a FGERectangle (this is not guaranteed to be optimal in some cases). For
	 * non-finite areas (if this area is not finite), return null
	 * 
	 * @return
	 */
	@Override
	public final FGERectangle getEmbeddingBounds() {
		logger.warning("Not implemented yet !!!!");
		return null;
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

		// TODO: to implement
		logger.warning("Not implemented yet !!!!");
		return null;
	}

}
