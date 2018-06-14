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

package org.openflexo.fge.geomedit;

import org.openflexo.diana.geomedit.model.gr.GeometricObjectGraphicalRepresentation;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geomedit.GeometricObject.GeometricObjectImpl;
import org.openflexo.fge.geomedit.construction.GeometricConstruction;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.model.factory.CloneableProxyObject;
import org.openflexo.model.factory.DeletableProxyObject;

@ModelEntity(isAbstract = true)
@ImplementationClass(GeometricObjectImpl.class)
@Imports({ @Import(Point.class), @Import(Line.class) })
public interface GeometricObject<A extends FGEArea> extends AccessibleProxyObject, DeletableProxyObject, CloneableProxyObject, Cloneable {

	@PropertyIdentifier(type = String.class)
	public static final String NAME_KEY = "name";
	@PropertyIdentifier(type = String.class)
	public static final String DESCRIPTION_KEY = "description";
	@PropertyIdentifier(type = GeometricConstruction.class)
	public static final String CONSTRUCTION_KEY = "construction";

	@Getter(NAME_KEY)
	public String getName();

	@Setter(NAME_KEY)
	public void setName(String name);

	@Getter(DESCRIPTION_KEY)
	public String getDescription();

	@Setter(DESCRIPTION_KEY)
	public void setDescription(String description);

	public GeometricSet getGeometricSet();

	@Getter(CONSTRUCTION_KEY)
	public GeometricConstruction<? extends A> getConstruction();

	@Setter(CONSTRUCTION_KEY)
	public void setConstruction(GeometricConstruction<? extends A> construction);

	public static abstract class GeometricObjectImpl<A extends FGEArea> implements GeometricObject<A> {

		private GeometricSet geometricSet;

		private static int index = 0;

		public GeometricObjectImpl() {
			index++;
			setName(getClass().getSimpleName() + index);
		}

		public abstract GeometricObjectGraphicalRepresentation<A> getGraphicalRepresentation();

		// public abstract void setGraphicalRepresentation(GeometricObjectGraphicalRepresentation<O,? extends GeometricObject<O>> aGR);

		@Override
		public String toString() {
			return "GeometricObject[" + getName() + ":" + getConstruction().getData() + "]";
		}

		@Override
		public GeometricObject clone() {
			try {
				return (GeometricObject) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				// cannot happen since we are clonable
				return null;
			}
		}

		public A getGeometricObject() {
			return getConstruction().getData();
		}

		private A _resultingGeometricObject;

		public void resetResultingGeometricObject() {
			_resultingGeometricObject = null;
			getConstruction().refresh();
			getResultingGeometricObject();
		}

		public A getResultingGeometricObject() {
			if (_resultingGeometricObject == null) {
				_resultingGeometricObject = getGeometricObject();
			}
			return _resultingGeometricObject;
		}

		public void setResultingGeometricObject(A object) {
			_resultingGeometricObject = object;
		}

		@Override
		public GeometricSet getGeometricSet() {
			return geometricSet;
		}
	}

}
