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

import org.openflexo.diana.geomedit.model.RectangleWithTwoPointsConstruction.RectangleWithTwoPointsConstructionImpl;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(RectangleWithTwoPointsConstructionImpl.class)
@XMLElement
public interface RectangleWithTwoPointsConstruction extends RectangleConstruction {

	@PropertyIdentifier(type = PointConstruction.class)
	public static final String POINT_CONSTRUCTION_1_KEY = "pointConstruction1";
	@PropertyIdentifier(type = PointConstruction.class)
	public static final String POINT_CONSTRUCTION_2_KEY = "pointConstruction2";

	@Getter(value = POINT_CONSTRUCTION_1_KEY)
	@XMLElement(context = "P1_")
	public PointConstruction getPointConstruction1();

	@Setter(value = POINT_CONSTRUCTION_1_KEY)
	public void setPointConstruction1(PointConstruction pointConstruction1);

	@Getter(value = POINT_CONSTRUCTION_2_KEY)
	@XMLElement(context = "P2_")
	public PointConstruction getPointConstruction2();

	@Setter(value = POINT_CONSTRUCTION_2_KEY)
	public void setPointConstruction2(PointConstruction pointConstruction2);

	public static abstract class RectangleWithTwoPointsConstructionImpl extends RectangleConstructionImpl
			implements RectangleWithTwoPointsConstruction {

		private PointConstruction pointConstruction1;
		private PointConstruction pointConstruction2;

		@Override
		protected DianaRectangle computeData() {
			DianaPoint p1 = pointConstruction1.getPoint();
			DianaPoint p2 = pointConstruction2.getPoint();

			DianaPoint p = new DianaPoint();
			p.x = Math.min(p1.x, p2.x);
			p.y = Math.min(p1.y, p2.y);

			double width = Math.abs(p1.x - p2.x);
			double height = Math.abs(p1.y - p2.y);

			return new DianaRectangle(p.x, p.y, width, height, getIsFilled() ? Filling.FILLED : Filling.NOT_FILLED);
		}

		@Override
		public String toString() {
			return "RectangleWithTwoPointsConstruction[\n" + "> " + pointConstruction1.toString() + "\n> " + pointConstruction2.toString()
					+ "\n]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { pointConstruction1, pointConstruction2 };
			return returned;
		}

		@Override
		public PointConstruction getPointConstruction1() {
			return pointConstruction1;
		}

		@Override
		public void setPointConstruction1(PointConstruction pointConstruction1) {
			if ((pointConstruction1 == null && this.pointConstruction1 != null)
					|| (pointConstruction1 != null && !pointConstruction1.equals(this.pointConstruction1))) {
				PointConstruction oldValue = this.pointConstruction1;
				this.pointConstruction1 = pointConstruction1;
				getPropertyChangeSupport().firePropertyChange("pointConstruction1", oldValue, pointConstruction1);
			}
		}

		@Override
		public PointConstruction getPointConstruction2() {
			return pointConstruction2;
		}

		@Override
		public void setPointConstruction2(PointConstruction pointConstruction2) {
			if ((pointConstruction2 == null && this.pointConstruction2 != null)
					|| (pointConstruction2 != null && !pointConstruction2.equals(this.pointConstruction2))) {
				PointConstruction oldValue = this.pointConstruction2;
				this.pointConstruction2 = pointConstruction2;
				getPropertyChangeSupport().firePropertyChange("pointConstruction2", oldValue, pointConstruction2);
			}
		}

	}

}
