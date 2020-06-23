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

import org.openflexo.diana.geom.DianaQuadCurve;
import org.openflexo.diana.geomedit.model.QuadCurveWithThreePointsConstruction.QuadCurveWithThreePointsConstructionImpl;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(QuadCurveWithThreePointsConstructionImpl.class)
@XMLElement
public interface QuadCurveWithThreePointsConstruction extends QuadCurveConstruction {

	@PropertyIdentifier(type = PointConstruction.class)
	public static final String START_POINT_CONSTRUCTION_KEY = "startPointConstruction";
	@PropertyIdentifier(type = PointConstruction.class)
	public static final String END_POINT_CONSTRUCTION_KEY = "endPointConstruction";
	@PropertyIdentifier(type = PointConstruction.class)
	public static final String CONTROL_POINT_CONSTRUCTION_KEY = "controlPointConstruction";

	@Getter(value = START_POINT_CONSTRUCTION_KEY)
	@XMLElement(context = "Start_")
	public PointConstruction getStartPointConstruction();

	@Setter(value = START_POINT_CONSTRUCTION_KEY)
	public void setStartPointConstruction(PointConstruction startPointConstruction);

	@Getter(value = CONTROL_POINT_CONSTRUCTION_KEY)
	@XMLElement(context = "Control_")
	public PointConstruction getControlPointConstruction();

	@Setter(value = CONTROL_POINT_CONSTRUCTION_KEY)
	public void setControlPointConstruction(PointConstruction controlPointConstruction);

	@Getter(value = END_POINT_CONSTRUCTION_KEY)
	@XMLElement(context = "End_")
	public PointConstruction getEndPointConstruction();

	@Setter(value = END_POINT_CONSTRUCTION_KEY)
	public void setEndPointConstruction(PointConstruction endPointConstruction);

	public static abstract class QuadCurveWithThreePointsConstructionImpl extends QuadCurveConstructionImpl
			implements QuadCurveWithThreePointsConstruction {

		private PointConstruction startPointConstruction;
		private PointConstruction controlPointConstruction;
		private PointConstruction endPointConstruction;

		@Override
		protected DianaQuadCurve computeData() {
			if (getStartPointConstruction() != null && getControlPointConstruction() != null && getEndPointConstruction() != null) {
				return new DianaQuadCurve(startPointConstruction.getPoint(), controlPointConstruction.getPoint(),
						endPointConstruction.getPoint());
			}
			return null;
		}

		@Override
		public String toString() {
			return "QuadCurveWithThreePointsConstruction[\n" + "> " + startPointConstruction.toString() + "\n> "
					+ controlPointConstruction.toString() + "\n> " + endPointConstruction.toString() + "\n]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { startPointConstruction, controlPointConstruction, endPointConstruction };
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
		public PointConstruction getControlPointConstruction() {
			return controlPointConstruction;
		}

		@Override
		public void setControlPointConstruction(PointConstruction controlPointConstruction) {
			if ((controlPointConstruction == null && this.controlPointConstruction != null)
					|| (controlPointConstruction != null && !controlPointConstruction.equals(this.controlPointConstruction))) {
				PointConstruction oldValue = this.controlPointConstruction;
				this.controlPointConstruction = controlPointConstruction;
				getPropertyChangeSupport().firePropertyChange("controlPointConstruction", oldValue, controlPointConstruction);
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
