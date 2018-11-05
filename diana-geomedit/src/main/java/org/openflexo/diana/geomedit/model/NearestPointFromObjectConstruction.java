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

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(NearestPointFromObjectConstruction.NearestPointFromObjectConstructionImpl.class)
@XMLElement
public interface NearestPointFromObjectConstruction extends PointConstruction {

	@PropertyIdentifier(type = PointConstruction.class)
	public static final String POINT_CONSTRUCTION_KEY = "pointConstruction";
	@PropertyIdentifier(type = ObjectReference.class)
	public static final String OBJECT_REFERENCE_KEY = "objectReference";

	@Getter(value = POINT_CONSTRUCTION_KEY)
	@XMLElement(context = "P_")
	public PointConstruction getPointConstruction();

	@Setter(value = POINT_CONSTRUCTION_KEY)
	public void setPointConstruction(PointConstruction pointConstruction);

	@Getter(value = OBJECT_REFERENCE_KEY)
	@XMLElement(context = "Ref_")
	public ObjectReference<?> getObjectReference();

	@Setter(value = OBJECT_REFERENCE_KEY)
	public void setObjectReference(ObjectReference<?> objectReference);

	public static abstract class NearestPointFromObjectConstructionImpl extends PointConstructionImpl
			implements NearestPointFromObjectConstruction {

		@Override
		protected FGEPoint computeData() {
			if (getObjectReference() != null && getPointConstruction() != null) {
				return getObjectReference().getReference().getData().getNearestPoint(getPointConstruction().getPoint());
			}
			return null;
		}

		@Override
		public String toString() {
			return "NearestPointFromObjectConstruction[\n" + "> " + getPointConstruction().toString() + "\n> "
					+ getObjectReference().toString() + "\n]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { getPointConstruction(), getObjectReference() };
			return returned;
		}

	}

}
