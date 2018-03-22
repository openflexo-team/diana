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
import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

public class DianaExclusiveOrArea extends DianaOperationArea {

	private static final Logger logger = Logger.getLogger(DianaExclusiveOrArea.class.getPackage().getName());

	private DianaArea area1;
	private DianaArea area2;

	public DianaExclusiveOrArea(DianaArea area1, DianaArea area2) {
		super();
		this.area1 = area1;
		this.area2 = area2;
	}

	@Override
	public String toString() {
		return "DianaExclusiveOrArea: " + area1 + " XOR " + area2;
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		return area1.containsPoint(p) && !area2.containsPoint(p) || area2.containsPoint(p) && !area1.containsPoint(p);
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		// TODO: do it better
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
			return DianaShape.AreaComputation.isShapeContainedInArea((DianaShape<?>) a, this);
		}
		return false;
	}

	@Override
	public DianaExclusiveOrArea transform(AffineTransform t) {
		return new DianaExclusiveOrArea(area1.transform(t), area2.transform(t));
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		// TODO
		// Use a finite method, using Java2D to perform shape computation
		// in the area defined by supplied DianaGraphics
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint aPoint) {
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
	 * If this area is finite, return embedding bounds as a DianaRectangle (this is not guaranteed to be optimal in some cases). For
	 * non-finite areas (if this area is not finite), return null
	 * 
	 * @return
	 */
	@Override
	public final DianaRectangle getEmbeddingBounds() {
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
	public DianaPoint nearestPointFrom(DianaPoint from, SimplifiedCardinalDirection orientation) {
		if (containsPoint(from)) {
			return from.clone();
		}

		// TODO: to implement
		logger.warning("Not implemented yet !!!!");
		return null;
	}

}
