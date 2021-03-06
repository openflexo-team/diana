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

import java.beans.PropertyChangeEvent;

import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geomedit.controller.ComputedControlPoint;
import org.openflexo.diana.geomedit.model.ControlPointReference.ControlPointReferenceImpl;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(ControlPointReferenceImpl.class)
@XMLElement
public interface ControlPointReference extends PointConstruction {

	@PropertyIdentifier(type = GeometricConstruction.class)
	public static final String REFERENCE_KEY = "reference";
	@PropertyIdentifier(type = String.class)
	public static final String CONTROL_POINT_NAME_KEY = "controlPointName";

	@Getter(value = REFERENCE_KEY)
	@XMLElement
	public GeometricConstruction<?> getReference();

	@Setter(value = REFERENCE_KEY)
	public void setReference(GeometricConstruction<?> reference);

	@Getter(value = CONTROL_POINT_NAME_KEY)
	@XMLAttribute
	public String getControlPointName();

	@Setter(value = CONTROL_POINT_NAME_KEY)
	public void setControlPointName(String cpName);

	public static abstract class ControlPointReferenceImpl extends PointConstructionImpl implements ControlPointReference {

		protected ComputedControlPoint getControlPoint() {

			if (getReference().getControlPoints() != null) {
				for (ControlPoint cp : getReference().getControlPoints()) {
					if (cp instanceof ComputedControlPoint && ((ComputedControlPoint) cp).getName().equals(getControlPointName())) {
						return (ComputedControlPoint) cp;
					}
				}
			}
			else {
				// Will be computed later, once control points for reference will be computed
				// See DrawingImpl.fireGraphicalObjectHierarchyRebuildEnded()
			}

			return null;
		}

		@Override
		protected DianaPoint computeData() {
			if (getControlPoint() != null) {
				return getControlPoint().getPoint();
			}
			// System.out
			// .println("computeData() for ControlPointReference: cannot find cp " + getControlPointName() + " for " + getReference());
			setModified(true);
			return new DianaPoint(0, 0);
		}

		@Override
		public String toString() {
			return "ControlPointReference[" + getControlPointName() + "," + getReference() + "]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { getReference() };
			return returned;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// System.out.println("!!!!!!!!!!! Tiens je recois " + evt.getPropertyName() + " evt=" + evt);
			super.propertyChange(evt);
		}
	}
}
