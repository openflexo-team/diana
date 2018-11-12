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

import org.openflexo.diana.geom.area.DianaHalfPlane;
import org.openflexo.diana.geomedit.model.HalfPlaneWithLineAndPointConstruction.HalfPlaneWithLineAndPointConstructionImpl;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(HalfPlaneWithLineAndPointConstructionImpl.class)
@XMLElement
public interface HalfPlaneWithLineAndPointConstruction extends HalfPlaneConstruction {

	@PropertyIdentifier(type = LineConstruction.class)
	public static final String LINE_CONSTRUCTION_KEY = "lineConstruction";
	@PropertyIdentifier(type = PointConstruction.class)
	public static final String POINT_CONSTRUCTION_KEY = "pointConstruction";

	@Getter(value = LINE_CONSTRUCTION_KEY)
	@XMLElement
	public LineConstruction getLineConstruction();

	@Setter(value = LINE_CONSTRUCTION_KEY)
	public void setLineConstruction(LineConstruction lineConstruction);

	@Getter(value = POINT_CONSTRUCTION_KEY)
	@XMLElement
	public PointConstruction getPointConstruction();

	@Setter(value = POINT_CONSTRUCTION_KEY)
	public void setPointConstruction(PointConstruction pointConstruction);

	public static abstract class HalfPlaneWithLineAndPointConstructionImpl extends HalfPlaneConstructionImpl
			implements HalfPlaneWithLineAndPointConstruction {

		private LineConstruction lineConstruction;
		private PointConstruction pointConstruction;

		@Override
		protected DianaHalfPlane computeData() {
			if (getLineConstruction() != null && getPointConstruction() != null) {
				DianaHalfPlane returned = new DianaHalfPlane(getLineConstruction().getLine(), getPointConstruction().getPoint());
				return returned;
			}
			return null;
		}

		@Override
		public String toString() {
			return "HalfPlaneWithLineAndPointConstruction[\n" + "> " + getLineConstruction().toString() + "\n> "
					+ getPointConstruction().toString() + "\n]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { lineConstruction, pointConstruction };
			return returned;
		}

		@Override
		public LineConstruction getLineConstruction() {
			return lineConstruction;
		}

		@Override
		public void setLineConstruction(LineConstruction lineConstruction) {
			if ((lineConstruction == null && this.lineConstruction != null)
					|| (lineConstruction != null && !lineConstruction.equals(this.lineConstruction))) {
				LineConstruction oldValue = this.lineConstruction;
				this.lineConstruction = lineConstruction;
				getPropertyChangeSupport().firePropertyChange("lineConstruction", oldValue, lineConstruction);
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

	}

}
