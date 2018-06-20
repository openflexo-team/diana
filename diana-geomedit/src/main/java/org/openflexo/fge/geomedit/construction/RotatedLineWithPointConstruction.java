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
import org.openflexo.fge.geomedit.construction.RotatedLineWithPointConstruction.RotatedLineWithPointConstructionImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(RotatedLineWithPointConstructionImpl.class)
@XMLElement
public interface RotatedLineWithPointConstruction extends LineConstruction {

	@PropertyIdentifier(type = LineConstruction.class)
	public static final String LINE_CONSTRUCTION_KEY = "lineConstruction";
	@PropertyIdentifier(type = PointConstruction.class)
	public static final String POINT_CONSTRUCTION_KEY = "pointConstruction";
	@PropertyIdentifier(type = Double.class)
	public static final String ANGLE_KEY = "angle";

	@Getter(value = LINE_CONSTRUCTION_KEY)
	public LineConstruction getLineConstruction();

	@Setter(value = LINE_CONSTRUCTION_KEY)
	public void setLineConstruction(LineConstruction lineConstruction);

	@Getter(value = POINT_CONSTRUCTION_KEY)
	public PointConstruction getPointConstruction();

	@Setter(value = POINT_CONSTRUCTION_KEY)
	public void setPointConstruction(PointConstruction pointConstruction);

	@Getter(value = ANGLE_KEY, defaultValue = "0.0")
	public double getAngle();

	@Setter(ANGLE_KEY)
	public void setAngle(double angle);

	public static abstract class RotatedLineWithPointConstructionImpl extends LineConstructionImpl
			implements RotatedLineWithPointConstruction {

		@Override
		protected FGELine computeData() {
			FGELine computedLine = FGEAbstractLine.getRotatedLine(getLineConstruction().getLine(), getAngle(),
					getPointConstruction().getPoint());

			FGEPoint p1, p2;
			p1 = getPointConstruction().getPoint().clone();
			if (getLineConstruction().getLine().contains(getPointConstruction().getPoint())) {
				FGEEllips ellips = new FGEEllips(p1, new FGEDimension(200, 200), Filling.NOT_FILLED);
				p2 = ellips.intersect(computedLine).getNearestPoint(p1);
			}
			else {
				p2 = computedLine.getLineIntersection(getLineConstruction().getLine()).clone();
			}
			return new FGELine(p1, p2);

		}

		@Override
		public String toString() {
			return "RotatedLineWithPointConstruction[" + getAngle() + "\n" + "> " + getLineConstruction().toString() + "\n> "
					+ getPointConstruction().toString() + "\n]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { getPointConstruction(), getLineConstruction() };
			return returned;
		}

	}

}
