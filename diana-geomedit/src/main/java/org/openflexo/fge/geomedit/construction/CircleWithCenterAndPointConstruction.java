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

import org.openflexo.fge.geom.FGECircle;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geomedit.construction.CircleReference.CircleReferenceImpl;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(CircleReferenceImpl.class)
@XMLElement
public interface CircleWithCenterAndPointConstruction extends CircleConstruction {

	public PointConstruction getCenterConstruction();

	public void setCenterConstruction(PointConstruction centerConstruction);

	public PointConstruction getPointConstruction();

	public void setPointConstruction(PointConstruction pointConstruction);

	public static abstract class CircleWithCenterAndPointConstructionImpl extends CircleConstructionImpl
			implements CircleWithCenterAndPointConstruction {

		private PointConstruction centerConstruction;
		private PointConstruction pointConstruction;

		@Override
		public PointConstruction getCenterConstruction() {
			return centerConstruction;
		}

		@Override
		public void setCenterConstruction(PointConstruction centerConstruction) {
			if ((centerConstruction == null && this.centerConstruction != null)
					|| (centerConstruction != null && !centerConstruction.equals(this.centerConstruction))) {
				PointConstruction oldValue = this.centerConstruction;
				this.centerConstruction = centerConstruction;
				getPropertyChangeSupport().firePropertyChange("centerConstruction", oldValue, centerConstruction);
			}
		}

		@Override
		public PointConstruction getPointConstruction() {
			return pointConstruction;
		}

		@Override
		public void setPointConstruction(PointConstruction pointConstruction) {
			if ((pointConstruction == null && this.pointConstruction != null)
					|| (pointConstruction != null && !pointConstruction.equals(this.pointConstruction))) {
				PointConstruction oldValue = this.pointConstruction;
				this.pointConstruction = pointConstruction;
				getPropertyChangeSupport().firePropertyChange("pointConstruction", oldValue, pointConstruction);
			}
		}

		@Override
		protected FGECircle computeData() {
			FGEPoint center = centerConstruction.getPoint();
			FGEPoint p = pointConstruction.getPoint();

			double radius = FGESegment.getLength(center, p);
			return new FGECircle(center, radius, getIsFilled() ? Filling.FILLED : Filling.NOT_FILLED);
		}

		@Override
		public String toString() {
			return "CircleWithCenterAndPointConstruction[\n" + "> " + centerConstruction.toString() + "\n> " + pointConstruction.toString()
					+ "\n]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { centerConstruction, pointConstruction };
			return returned;
		}

	}

}
