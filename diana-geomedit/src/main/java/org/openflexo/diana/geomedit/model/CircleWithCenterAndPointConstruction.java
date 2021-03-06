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
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geomedit.model.CircleWithCenterAndPointConstruction.CircleWithCenterAndPointConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.CircleWithCenterAndPointGraphicalRepresentation;
import org.openflexo.diana.geomedit.model.gr.GeometricObjectGraphicalRepresentation;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(CircleWithCenterAndPointConstructionImpl.class)
@XMLElement
public interface CircleWithCenterAndPointConstruction extends CircleConstruction {

	@PropertyIdentifier(type = PointConstruction.class)
	public static final String CENTER_CONSTRUCTION_KEY = "centerConstruction";
	@PropertyIdentifier(type = PointConstruction.class)
	public static final String POINT_CONSTRUCTION_KEY = "pointConstruction";

	@Getter(value = CENTER_CONSTRUCTION_KEY)
	@XMLElement(context = "Center_")
	public PointConstruction getCenterConstruction();

	@Setter(value = CENTER_CONSTRUCTION_KEY)
	public void setCenterConstruction(PointConstruction centerConstruction);

	@Getter(value = POINT_CONSTRUCTION_KEY)
	@XMLElement(context = "Point_")
	public PointConstruction getPointConstruction();

	@Setter(value = POINT_CONSTRUCTION_KEY)
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
		protected DianaCircle computeData() {
			if (getCenterConstruction() != null && getPointConstruction() != null) {
				DianaPoint center = getCenterConstruction().getPoint();
				DianaPoint p = getPointConstruction().getPoint();

				double radius = DianaSegment.getLength(center, p);
				return new DianaCircle(center, radius, getIsFilled() ? Filling.FILLED : Filling.NOT_FILLED);
			}
			return null;
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

		@Override
		public GeometricObjectGraphicalRepresentation<DianaCircle> makeNewConstructionGR(GeometricConstructionFactory factory) {
			return (GeometricObjectGraphicalRepresentation) factory.newInstance(CircleWithCenterAndPointGraphicalRepresentation.class);
		}

	}

}
