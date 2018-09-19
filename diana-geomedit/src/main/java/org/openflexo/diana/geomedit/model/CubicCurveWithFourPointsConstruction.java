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

import org.openflexo.diana.geomedit.model.CubicCurveWithFourPointsConstruction.CubicCurveWithFourPointsConstructionImpl;
import org.openflexo.fge.geom.FGECubicCurve;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(CubicCurveWithFourPointsConstructionImpl.class)
@XMLElement
public interface CubicCurveWithFourPointsConstruction extends CubicCurveConstruction {

	@PropertyIdentifier(type = PointConstruction.class)
	public static final String START_POINT_CONSTRUCTION_KEY = "startPointConstruction";
	@PropertyIdentifier(type = PointConstruction.class)
	public static final String END_POINT_CONSTRUCTION_KEY = "endPointConstruction";
	@PropertyIdentifier(type = PointConstruction.class)
	public static final String CONTROL_POINT_CONSTRUCTION_1_KEY = "controlPointConstruction1";
	@PropertyIdentifier(type = PointConstruction.class)
	public static final String CONTROL_POINT_CONSTRUCTION_2_KEY = "controlPointConstruction2";

	@Getter(value = START_POINT_CONSTRUCTION_KEY)
	@XMLElement(context = "Start_")
	public PointConstruction getStartPointConstruction();

	@Setter(value = START_POINT_CONSTRUCTION_KEY)
	public void setStartPointConstruction(PointConstruction startPointConstruction);

	@Getter(value = CONTROL_POINT_CONSTRUCTION_1_KEY)
	@XMLElement(context = "Ctrl1_")
	public PointConstruction getControlPointConstruction1();

	@Setter(value = CONTROL_POINT_CONSTRUCTION_1_KEY)
	public void setControlPointConstruction1(PointConstruction controlPointConstruction);

	@Getter(value = CONTROL_POINT_CONSTRUCTION_2_KEY)
	@XMLElement(context = "Ctrl2_")
	public PointConstruction getControlPointConstruction2();

	@Setter(value = CONTROL_POINT_CONSTRUCTION_2_KEY)
	public void setControlPointConstruction2(PointConstruction controlPointConstruction);

	@Getter(value = END_POINT_CONSTRUCTION_KEY)
	@XMLElement(context = "End_")
	public PointConstruction getEndPointConstruction();

	@Setter(value = END_POINT_CONSTRUCTION_KEY)
	public void setEndPointConstruction(PointConstruction endPointConstruction);

	public static abstract class CubicCurveWithFourPointsConstructionImpl extends CubicCurveConstructionImpl
			implements CubicCurveWithFourPointsConstruction {

		private PointConstruction startPointConstruction;
		private PointConstruction controlPointConstruction1;
		private PointConstruction controlPointConstruction2;
		private PointConstruction endPointConstruction;

		@Override
		protected FGECubicCurve computeData() {
			return new FGECubicCurve(startPointConstruction.getPoint(), controlPointConstruction1.getPoint(),
					controlPointConstruction2.getPoint(), endPointConstruction.getPoint());
		}

		@Override
		public String toString() {
			return "CubicCurveWithFourPointsConstruction[\n" + "> " + startPointConstruction.toString() + "\n> "
					+ controlPointConstruction1.toString() + "\n> " + controlPointConstruction2.toString() + "\n> "
					+ endPointConstruction.toString() + "\n]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { startPointConstruction, controlPointConstruction1, controlPointConstruction2,
					endPointConstruction };
			return returned;
		}

		@Override
		public PointConstruction getStartPointConstruction() {
			return startPointConstruction;
		}

		@Override
		public void setStartPointConstruction(PointConstruction startPointConstruction) {
			if ((startPointConstruction == null && this.startPointConstruction != null)
					|| (startPointConstruction != null && !startPointConstruction.equals(this.startPointConstruction))) {
				PointConstruction oldValue = this.startPointConstruction;
				this.startPointConstruction = startPointConstruction;
				getPropertyChangeSupport().firePropertyChange("startPointConstruction", oldValue, startPointConstruction);
			}
		}

		@Override
		public PointConstruction getControlPointConstruction1() {
			return controlPointConstruction1;
		}

		@Override
		public void setControlPointConstruction1(PointConstruction controlPointConstruction1) {
			if ((controlPointConstruction1 == null && this.controlPointConstruction1 != null)
					|| (controlPointConstruction1 != null && !controlPointConstruction1.equals(this.controlPointConstruction1))) {
				PointConstruction oldValue = this.controlPointConstruction1;
				this.controlPointConstruction1 = controlPointConstruction1;
				getPropertyChangeSupport().firePropertyChange("controlPointConstruction1", oldValue, controlPointConstruction1);
			}
		}

		@Override
		public PointConstruction getControlPointConstruction2() {
			return controlPointConstruction2;
		}

		@Override
		public void setControlPointConstruction2(PointConstruction controlPointConstruction2) {
			if ((controlPointConstruction2 == null && this.controlPointConstruction2 != null)
					|| (controlPointConstruction2 != null && !controlPointConstruction2.equals(this.controlPointConstruction2))) {
				PointConstruction oldValue = this.controlPointConstruction2;
				this.controlPointConstruction2 = controlPointConstruction2;
				getPropertyChangeSupport().firePropertyChange("controlPointConstruction2", oldValue, controlPointConstruction2);
			}
		}

		@Override
		public PointConstruction getEndPointConstruction() {
			return endPointConstruction;
		}

		@Override
		public void setEndPointConstruction(PointConstruction endPointConstruction) {
			if ((endPointConstruction == null && this.endPointConstruction != null)
					|| (endPointConstruction != null && !endPointConstruction.equals(this.endPointConstruction))) {
				PointConstruction oldValue = this.endPointConstruction;
				this.endPointConstruction = endPointConstruction;
				getPropertyChangeSupport().firePropertyChange("endPointConstruction", oldValue, endPointConstruction);
			}
		}

	}

}
