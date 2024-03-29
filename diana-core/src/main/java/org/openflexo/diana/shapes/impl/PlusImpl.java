/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.diana.shapes.impl;

import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolygon;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.notifications.DianaAttributeNotification;
import org.openflexo.diana.shapes.Plus;

/**
 * Implementation for a Plus shape.
 * 
 * @author eloubout
 * 
 */
public abstract class PlusImpl extends ShapeSpecificationImpl implements Plus {

	/** Ratio of cut of the base rectangle. 0.5 is no short side. */
	private double ratio = 0.25;

	public PlusImpl() {
		super();
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.PLUS;
	}

	/**
	 * Set ratio
	 * 
	 * @param ratio
	 *            0 < ratio < 0.5, handled in serialization
	 */
	@Override
	public void setRatio(final double aRatio) {
		final DianaAttributeNotification<Double> notification = this.requireChange(RATIO, aRatio);
		if (notification != null) {
			this.ratio = aRatio;
			this.hasChanged(notification);
		}
	}

	@Override
	public double getRatio() {
		return this.ratio;
	}

	@Override
	public boolean areDimensionConstrained() {
		return true;
	}

	/**
	 * Polygon with a big number of points well placed and that take car of ratio.
	 * 
	 * @return {@link DianaPolygon}
	 */
	@Override
	public DianaShape<?> makeNormalizedDianaShape(final DianaRectangle bounds) {
		final DianaPolygon returned = new DianaPolygon(Filling.FILLED);
		returned.addToPoints(new DianaPoint(0, this.ratio));
		returned.addToPoints(new DianaPoint(0, 1 - this.ratio));
		returned.addToPoints(new DianaPoint(this.ratio, 1 - this.ratio));
		returned.addToPoints(new DianaPoint(this.ratio, 1));
		returned.addToPoints(new DianaPoint(1 - this.ratio, 1));
		returned.addToPoints(new DianaPoint(1 - this.ratio, 1 - this.ratio));
		returned.addToPoints(new DianaPoint(1, 1 - this.ratio));
		returned.addToPoints(new DianaPoint(1, this.ratio));
		returned.addToPoints(new DianaPoint(1 - this.ratio, this.ratio));
		returned.addToPoints(new DianaPoint(1 - this.ratio, 0));
		returned.addToPoints(new DianaPoint(this.ratio, 0));
		returned.addToPoints(new DianaPoint(this.ratio, this.ratio));
		return returned;
	}

}
