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

import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.DianaPolylin;
import org.openflexo.diana.geom.DianaRectPolylin;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geomedit.model.RectPolylinWithStartAndEndAreaConstruction.RectPolylinWithStartAndEndAreaConstructionImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
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
	@XMLElement(context = "Start_")
	public ObjectReference<? extends DianaArea> getStartAreaConstruction();

	@Setter(START_AREA_CONSTRUCTION_KEY)
	public void setStartAreaConstruction(ObjectReference<? extends DianaArea> startAreaConstruction);

	@Getter(END_AREA_CONSTRUCTION_KEY)
	@XMLElement(context = "End_")
	public ObjectReference<? extends DianaArea> getEndAreaConstruction();

	@Setter(END_AREA_CONSTRUCTION_KEY)
	public void setEndAreaConstruction(ObjectReference<? extends DianaArea> endAreaConstruction);

	@Getter(START_ORIENTATION_KEY)
	@XMLAttribute
	public SimplifiedCardinalDirection getStartOrientation();

	@Setter(START_ORIENTATION_KEY)
	public void setStartOrientation(SimplifiedCardinalDirection startOrientation);

	@Getter(END_ORIENTATION_KEY)
	@XMLAttribute
	public SimplifiedCardinalDirection getEndOrientation();

	@Setter(END_ORIENTATION_KEY)
	public void setEndOrientation(SimplifiedCardinalDirection endOrientation);

	public static abstract class RectPolylinWithStartAndEndAreaConstructionImpl extends PolylinConstructionImpl
			implements RectPolylinWithStartAndEndAreaConstruction {

		@Override
		protected DianaPolylin computeData() {
			if (getStartAreaConstruction() != null && getEndAreaConstruction() != null) {
				DianaArea startArea = getStartAreaConstruction().getData();
				DianaArea endArea = getEndAreaConstruction().getData();
				if (getStartOrientation() == null) {
					setStartOrientation(SimplifiedCardinalDirection.NORTH);
				}
				if (getEndOrientation() == null) {
					setEndOrientation(SimplifiedCardinalDirection.SOUTH);
				}
				return new DianaRectPolylin(startArea, getStartOrientation(), endArea, getEndOrientation(), false, 10, 10);
			}
			return null;
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

		@Override
		public SimplifiedCardinalDirection getStartOrientation() {
			SimplifiedCardinalDirection returned = (SimplifiedCardinalDirection) performSuperGetter(START_ORIENTATION_KEY);
			if (returned == null) {
				return SimplifiedCardinalDirection.NORTH;
			}
			return returned;
		}

		@Override
		public void setStartOrientation(SimplifiedCardinalDirection orientation) {
			SimplifiedCardinalDirection oldOrientation = (SimplifiedCardinalDirection) performSuperGetter(START_ORIENTATION_KEY);
			performSuperSetter(START_ORIENTATION_KEY, orientation);
			if (oldOrientation != null && oldOrientation != orientation) {
				refresh();
				notifyGeometryChanged();
			}
		}

		@Override
		public SimplifiedCardinalDirection getEndOrientation() {
			SimplifiedCardinalDirection returned = (SimplifiedCardinalDirection) performSuperGetter(END_ORIENTATION_KEY);
			if (returned == null) {
				return SimplifiedCardinalDirection.NORTH;
			}
			return returned;
		}

		@Override
		public void setEndOrientation(SimplifiedCardinalDirection orientation) {
			SimplifiedCardinalDirection oldOrientation = (SimplifiedCardinalDirection) performSuperGetter(END_ORIENTATION_KEY);
			performSuperSetter(END_ORIENTATION_KEY, orientation);
			if (oldOrientation != null && oldOrientation != orientation) {
				refresh();
				notifyGeometryChanged();
			}
		}

	}

}
