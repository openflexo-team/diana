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

import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPolylin;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geomedit.construction.RectPolylinWithStartAndEndAreaConstruction.RectPolylinWithStartAndEndAreaConstructionImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(RectPolylinWithStartAndEndAreaConstructionImpl.class)
@XMLElement
public interface RectPolylinWithStartAndEndAreaConstruction extends PolylinConstruction {

	@PropertyIdentifier(type = ObjectReference.class)
	public static final String START_AREA_CONSTRUCTION_KEY = "startAreaConstruction";
	@PropertyIdentifier(type = ObjectReference.class)
	public static final String END_AREA_CONSTRUCTION_KEY = "endAreaConstruction";
	@PropertyIdentifier(type = SimplifiedCardinalDirection.class)
	public static final String START_ORIENTATION_KEY = "startOrientation";
	@PropertyIdentifier(type = SimplifiedCardinalDirection.class)
	public static final String END_ORIENTATION_KEY = "endOrientation";

	@Getter(START_AREA_CONSTRUCTION_KEY)
	public ObjectReference<? extends FGEArea> getStartAreaConstruction();

	@Setter(START_AREA_CONSTRUCTION_KEY)
	public void setStartAreaConstruction(ObjectReference<? extends FGEArea> startAreaConstruction);

	@Getter(END_AREA_CONSTRUCTION_KEY)
	public ObjectReference<? extends FGEArea> getEndAreaConstruction();

	@Setter(END_AREA_CONSTRUCTION_KEY)
	public void setEndAreaConstruction(ObjectReference<? extends FGEArea> endAreaConstruction);

	@Getter(START_ORIENTATION_KEY)
	public SimplifiedCardinalDirection getStartOrientation();

	@Setter(START_ORIENTATION_KEY)
	public void setStartOrientation(SimplifiedCardinalDirection startOrientation);

	@Getter(END_ORIENTATION_KEY)
	public SimplifiedCardinalDirection getEndOrientation();

	@Setter(END_ORIENTATION_KEY)
	public void setEndOrientation(SimplifiedCardinalDirection endOrientation);

	public static abstract class RectPolylinWithStartAndEndAreaConstructionImpl extends PolylinConstructionImpl
			implements RectPolylinWithStartAndEndAreaConstruction {

		@Override
		protected FGEPolylin computeData() {
			FGEArea startArea = getStartAreaConstruction().getData();
			FGEArea endArea = getEndAreaConstruction().getData();
			if (getStartOrientation() == null) {
				setStartOrientation(SimplifiedCardinalDirection.NORTH);
			}
			if (getEndOrientation() == null) {
				setEndOrientation(SimplifiedCardinalDirection.SOUTH);
			}
			return new FGERectPolylin(startArea, getStartOrientation(), endArea, getEndOrientation(), false, 10, 10);
		}

		@Override
		public String toString() {
			return "RectPolylinWithStartAndEndAreaConstruction[\n" + "> " + getStartAreaConstruction().toString() + "-"
					+ getStartOrientation() + "\n> " + "> " + getEndAreaConstruction().toString() + "-" + getEndOrientation() + "\n]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { getStartAreaConstruction(), getEndAreaConstruction() };
			return returned;
		}

	}

}
