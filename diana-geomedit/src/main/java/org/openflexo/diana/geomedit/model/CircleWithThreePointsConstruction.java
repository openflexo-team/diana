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

package org.openflexo.diana.geomedit.model;

import org.openflexo.diana.geom.DianaCircle;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geomedit.model.CircleWithThreePointsConstruction.CircleWithThreePointsConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.CircleWithThreePointsGraphicalRepresentation;
import org.openflexo.diana.geomedit.model.gr.GeometricObjectGraphicalRepresentation;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(CircleWithThreePointsConstructionImpl.class)
@XMLElement
public interface CircleWithThreePointsConstruction extends CircleConstruction {

	@PropertyIdentifier(type = PointConstruction.class)
	public static final String POINT_CONSTRUCTION_1_KEY = "pointConstruction1";
	@PropertyIdentifier(type = PointConstruction.class)
	public static final String POINT_CONSTRUCTION_2_KEY = "pointConstruction2";
	@PropertyIdentifier(type = PointConstruction.class)
	public static final String POINT_CONSTRUCTION_3_KEY = "pointConstruction3";

	@Getter(value = POINT_CONSTRUCTION_1_KEY)
	@XMLElement(context = "P1_")
	public PointConstruction getPointConstruction1();

	@Setter(value = POINT_CONSTRUCTION_1_KEY)
	public void setPointConstruction1(PointConstruction pointConstruction);

	@Getter(value = POINT_CONSTRUCTION_2_KEY)
	@XMLElement(context = "P2_")
	public PointConstruction getPointConstruction2();

	@Setter(value = POINT_CONSTRUCTION_2_KEY)
	public void setPointConstruction2(PointConstruction pointConstruction);

	@Getter(value = POINT_CONSTRUCTION_3_KEY)
	@XMLElement(context = "P3_")
	public PointConstruction getPointConstruction3();

	@Setter(value = POINT_CONSTRUCTION_3_KEY)
	public void setPointConstruction3(PointConstruction pointConstruction);

	public static abstract class CircleWithThreePointsConstructionImpl extends CircleConstructionImpl
			implements CircleWithThreePointsConstruction {

		@Override
		protected DianaCircle computeData() {
			if (getPointConstruction1() != null && getPointConstruction2() != null && getPointConstruction3() != null) {
				DianaPoint p1 = getPointConstruction1().getPoint();
				DianaPoint p2 = getPointConstruction2().getPoint();
				DianaPoint p3 = getPointConstruction3().getPoint();
				return DianaCircle.getCircle(p1, p2, p3, getIsFilled() ? Filling.FILLED : Filling.NOT_FILLED);
			}
			return null;
		}

		@Override
		public String toString() {
			return "CircleWithThreePointsConstruction[\n" + "> " + getPointConstruction1().toString() + "\n> "
					+ getPointConstruction2().toString() + "\n> " + getPointConstruction3().toString() + "\n]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { getPointConstruction1(), getPointConstruction2(), getPointConstruction3() };
			return returned;
		}

		@Override
		public GeometricObjectGraphicalRepresentation<DianaCircle> makeNewConstructionGR(GeometricConstructionFactory factory) {
			return (GeometricObjectGraphicalRepresentation) factory.newInstance(CircleWithThreePointsGraphicalRepresentation.class);
		}

	}

}
