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

import java.util.logging.Logger;

import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaSubstractionArea;
import org.openflexo.diana.geomedit.model.gr.ComputedAreaGraphicalRepresentation;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(SubstractionConstruction.SubstractionConstructionImpl.class)
@XMLElement
public interface SubstractionConstruction extends GeometricConstruction<DianaArea> {

	@PropertyIdentifier(type = ObjectReference.class)
	public static final String CONTAINER_OBJECT_CONSTRUCTION_KEY = "containerObjectConstruction";
	@PropertyIdentifier(type = ObjectReference.class)
	public static final String SUBSTRACTED_OBJECT_CONSTRUCTION_KEY = "substractedObjectConstruction";

	@Getter(value = CONTAINER_OBJECT_CONSTRUCTION_KEY)
	@XMLElement(context = "Container_")
	public ObjectReference<?> getContainerObjectConstruction();

	@Setter(value = CONTAINER_OBJECT_CONSTRUCTION_KEY)
	public void setContainerObjectConstruction(ObjectReference<?> pointConstruction1);

	@Getter(value = SUBSTRACTED_OBJECT_CONSTRUCTION_KEY)
	@XMLElement(context = "Substracted_")
	public ObjectReference<?> getSubstractedObjectConstruction();

	@Setter(value = SUBSTRACTED_OBJECT_CONSTRUCTION_KEY)
	public void setSubstractedObjectConstruction(ObjectReference<?> pointConstruction2);

	public static abstract class SubstractionConstructionImpl extends GeometricConstructionImpl<DianaArea>
			implements SubstractionConstruction {

		private static final Logger logger = FlexoLogger.getLogger(SubstractionConstruction.class.getPackage().getName());

		@Override
		public String getBaseName() {
			return "Substraction";
		}

		@Override
		public ComputedAreaGraphicalRepresentation makeNewConstructionGR(GeometricConstructionFactory factory) {
			ComputedAreaGraphicalRepresentation returned = factory.newInstance(ComputedAreaGraphicalRepresentation.class);
			return returned;
		}

		@Override
		protected DianaArea computeData() {
			if (getContainerObjectConstruction() != null && getSubstractedObjectConstruction() != null) {
				DianaArea returned = DianaSubstractionArea.makeSubstraction(getContainerObjectConstruction().getData(),
						getSubstractedObjectConstruction().getData(), false);

				if (returned == null) {
					new Exception("Unexpected substraction").printStackTrace();
				}
				return returned;
			}
			return null;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("SubstractionConstruction[\n");
			sb.append("> " + getContainerObjectConstruction().toString() + "\n");
			sb.append("> " + getSubstractedObjectConstruction().toString() + "\n");
			sb.append("-> " + getData() + "\n");
			sb.append("]");
			return sb.toString();
		}

		@Override
		public GeometricConstruction<?>[] getDepends() {
			GeometricConstruction[] returned = { getContainerObjectConstruction(), getSubstractedObjectConstruction() };
			return returned;
		}

	}

}
