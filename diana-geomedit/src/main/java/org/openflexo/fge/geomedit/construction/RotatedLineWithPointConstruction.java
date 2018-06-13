/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geomedit, a component of the software infrastructure 
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

package org.openflexo.fge.geomedit.construction;

import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEEllips;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;

public class RotatedLineWithPointConstruction extends LineConstruction {

	public PointConstruction pointConstruction;
	public LineConstruction lineConstruction;
	public double angle;

	public RotatedLineWithPointConstruction() {
		super();
	}

	public RotatedLineWithPointConstruction(LineConstruction lineConstruction, PointConstruction pointConstruction, double anAngle) {
		this();
		this.lineConstruction = lineConstruction;
		this.pointConstruction = pointConstruction;
		angle = anAngle;
	}

	@Override
	protected FGELine computeData() {
		FGELine computedLine = FGEAbstractLine.getRotatedLine(lineConstruction.getLine(), angle, pointConstruction.getPoint());

		FGEPoint p1, p2;
		p1 = pointConstruction.getPoint().clone();
		if (lineConstruction.getLine().contains(pointConstruction.getPoint())) {
			FGEEllips ellips = new FGEEllips(p1, new FGEDimension(200, 200), Filling.NOT_FILLED);
			p2 = ellips.intersect(computedLine).getNearestPoint(p1);
		} else {
			p2 = computedLine.getLineIntersection(lineConstruction.getLine()).clone();
		}
		return new FGELine(p1, p2);

	}

	@Override
	public String toString() {
		return "RotatedLineWithPointConstruction[" + angle + "\n" + "> " + lineConstruction.toString() + "\n> "
				+ pointConstruction.toString() + "\n]";
	}

	@Override
	public GeometricConstruction[] getDepends() {
		GeometricConstruction[] returned = { pointConstruction, lineConstruction };
		return returned;
	}

}
