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

package org.openflexo.diana.geom;

public class FGERegularPolygon extends FGEPolygon {

	private int npoints;
	private double startAngle = 90; // in degree

	public double x, y;
	public double width, height;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	public FGERegularPolygon(double aX, double aY, double aWidth, double aHeight, Filling filling, int pointsNb) {
		super(filling);
		this.x = aX;
		this.y = aY;
		this.width = aWidth;
		this.height = aHeight;
		if (pointsNb < 3) {
			throw new IllegalArgumentException("Cannot build regular polygon with less then 3 points (" + pointsNb + ")");
		}
		npoints = pointsNb;
		updatePoints();
	}

	public FGERegularPolygon(double aX, double aY, double aWidth, double aHeight, Filling filling, int pointsNb, double startAngle) {
		this(aX, aY, aWidth, aHeight, filling, pointsNb);
		setStartAngle(startAngle);
	}

	private void updatePoints() {
		clearPoints();
		for (int i = 0; i < npoints; i++) {
			double angle = i * 2 * Math.PI / npoints - startAngle * Math.PI / 180;
			addToPoints(new FGEPoint(Math.cos(angle) * width / 2 + x + width / 2, Math.sin(angle) * height / 2 + y + height / 2));
		}
	}

	public int getNPoints() {
		return npoints;
	}

	public void setNPoints(int pointsNb) {
		if (pointsNb < 3) {
			pointsNb = 3;
		}
		npoints = pointsNb;
		updatePoints();
	}

	public double getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(double anAngle) {
		startAngle = anAngle;
		updatePoints();
	}

}
