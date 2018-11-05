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

package org.openflexo.fge.shapes.impl;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.fge.shapes.Star;

public abstract class StarImpl extends ShapeSpecificationImpl implements Star {

	private int npoints = 6;
	private double ratio = 0.5;
	private int startAngle = 90;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public StarImpl() {
		super();
	}

	/*@Deprecated
	private StarImpl(ShapeGraphicalRepresentation aGraphicalRepresentation) {
		this(aGraphicalRepresentation, 5);
	}
	
	@Deprecated
	private StarImpl(ShapeGraphicalRepresentation aGraphicalRepresentation, int pointsNb) {
		this();
		setGraphicalRepresentation(aGraphicalRepresentation);
		if (pointsNb < 3) {
			throw new IllegalArgumentException("Cannot build polygon with less then 3 points (" + pointsNb + ")");
		}
		updateShape();
	}*/

	@Override
	public ShapeType getShapeType() {
		return ShapeType.STAR;
	}

	@Override
	public int getNPoints() {
		return npoints;
	}

	@Override
	public void setNPoints(int pointsNb) {
		FGEAttributeNotification<?> notification = requireChange(N_POINTS, pointsNb);
		if (notification != null) {
			npoints = pointsNb;
			hasChanged(notification);
		}
	}

	@Override
	public int getStartAngle() {
		return startAngle;
	}

	@Override
	public void setStartAngle(int anAngle) {
		FGEAttributeNotification<?> notification = requireChange(START_ANGLE, anAngle);
		if (notification != null) {
			startAngle = anAngle;
			hasChanged(notification);
		}
	}

	@Override
	public double getRatio() {
		return ratio;
	}

	@Override
	public void setRatio(double aRatio) {
		if (aRatio > 0 && aRatio < 1.0) {
			FGEAttributeNotification<?> notification = requireChange(RATIO, aRatio);
			if (notification != null) {
				ratio = aRatio;
				hasChanged(notification);
			}
		}
	}

	@Override
	public FGEShape<?> makeNormalizedFGEShape(ShapeNode<?> node) {
		FGEPolygon returned = new FGEPolygon(Filling.FILLED);
		double startA = getStartAngle() * Math.PI / 180;
		double angleInterval = Math.PI * 2 / npoints;
		for (int i = 0; i < npoints; i++) {
			double angle = i * angleInterval + startA;
			double angle1 = (i - 0.5) * angleInterval + startA;
			returned.addToPoints(new FGEPoint(Math.cos(angle1) * 0.5 * ratio + 0.5, Math.sin(angle1) * 0.5 * ratio + 0.5));
			returned.addToPoints(new FGEPoint(Math.cos(angle) * 0.5 + 0.5, Math.sin(angle) * 0.5 + 0.5));
		}
		return returned;
	}

}
