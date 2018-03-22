/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolygon;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.notifications.DianaAttributeNotification;
import org.openflexo.diana.shapes.RectangularOctogon;

public abstract class RectangularOctogonImpl extends ShapeSpecificationImpl implements RectangularOctogon {

	private double ratio = 0.2;

	@Override
	public ShapeType getShapeType() {
		return ShapeType.RECTANGULAROCTOGON;
	}

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public RectangularOctogonImpl() {
		super();
	}

	/**
	 * Set ratio
	 * 
	 * @param ratio
	 *            if ratio > 0.5 ratio is divide by 2
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
	public DianaShape<?> makeDianaShape(final ShapeNode<?> node) {
		final DianaPolygon returned = new DianaPolygon(Filling.FILLED);

		returned.addToPoints(new DianaPoint(0, this.ratio));
		returned.addToPoints(new DianaPoint(0, 1 - this.ratio));
		returned.addToPoints(new DianaPoint(this.ratio / 2, 1));
		returned.addToPoints(new DianaPoint(1 - this.ratio / 2, 1));
		returned.addToPoints(new DianaPoint(1, 1 - this.ratio));
		returned.addToPoints(new DianaPoint(1, this.ratio));
		returned.addToPoints(new DianaPoint(1 - this.ratio / 2, 0));
		returned.addToPoints(new DianaPoint(this.ratio / 2, 0));

		return returned;

	}

}
