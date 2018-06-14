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

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geomedit.construction.SymetricPointConstruction.SymetricPointConstructionImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(SymetricPointConstructionImpl.class)
@XMLElement
public interface SymetricPointConstruction extends PointConstruction {

	@PropertyIdentifier(type = PointConstruction.class)
	public static final String POINT_CONSTRUCTION_KEY = "pointConstruction";
	@PropertyIdentifier(type = PointConstruction.class)
	public static final String PIVOT_CONSTRUCTION_KEY = "pivotConstruction";

	@Getter(value = POINT_CONSTRUCTION_KEY)
	public PointConstruction getPointConstruction();

	@Setter(value = POINT_CONSTRUCTION_KEY)
	public void setPointConstruction(PointConstruction pointConstruction);

	@Getter(value = PIVOT_CONSTRUCTION_KEY)
	public PointConstruction getPivotConstruction();

	@Setter(value = PIVOT_CONSTRUCTION_KEY)
	public void setPivotConstruction(PointConstruction pivotConstruction);

	public static abstract class SymetricPointConstructionImpl extends PointConstructionImpl implements SymetricPointConstruction {

		private PointConstruction pointConstruction;
		private PointConstruction pivotConstruction;

		@Override
		protected FGEPoint computeData() {
			return new FGESegment(pointConstruction.getPoint(), pivotConstruction.getPoint()).getScaledPoint(2);
		}

		@Override
		public String toString() {
			return "SymetricPointConstruction[\n" + "> " + pointConstruction.toString() + "\n> " + pivotConstruction.toString() + "\n]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { pointConstruction, pivotConstruction };
			return returned;
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
		public PointConstruction getPivotConstruction() {
			return pivotConstruction;
		}

		@Override
		public void setPivotConstruction(PointConstruction pivotConstruction) {
			if ((pivotConstruction == null && this.pivotConstruction != null)
					|| (pivotConstruction != null && !pivotConstruction.equals(this.pivotConstruction))) {
				PointConstruction oldValue = this.pivotConstruction;
				this.pivotConstruction = pivotConstruction;
				getPropertyChangeSupport().firePropertyChange("pivotConstruction", oldValue, pivotConstruction);
			}
		}

	}

}
