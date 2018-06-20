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

import org.openflexo.diana.geomedit.model.LineIntersectionPointConstruction.LineIntersectionPointConstructionImpl;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(LineIntersectionPointConstructionImpl.class)
@XMLElement
public interface LineIntersectionPointConstruction extends PointConstruction {

	@PropertyIdentifier(type = LineConstruction.class)
	public static final String LINE_CONSTRUCTION_1_KEY = "lineConstruction1";
	@PropertyIdentifier(type = LineConstruction.class)
	public static final String LINE_CONSTRUCTION_2_KEY = "lineConstruction2";

	@Getter(value = LINE_CONSTRUCTION_1_KEY)
	public LineConstruction getLineConstruction1();

	@Setter(value = LINE_CONSTRUCTION_1_KEY)
	public void setLineConstruction1(LineConstruction lineConstruction1);

	@Getter(value = LINE_CONSTRUCTION_2_KEY)
	public LineConstruction getLineConstruction2();

	@Setter(value = LINE_CONSTRUCTION_2_KEY)
	public void setLineConstruction2(LineConstruction lineConstruction2);

	public static abstract class LineIntersectionPointConstructionImpl extends PointConstructionImpl
			implements LineIntersectionPointConstruction {

		@Override
		protected FGEPoint computeData() {
			return getLineConstruction1().getData().getLineIntersection(getLineConstruction2().getData());
		}

		@Override
		public String toString() {
			return "LineIntersectionPointConstruction[\n" + "> " + getLineConstruction1().toString() + "\n> "
					+ getLineConstruction2().toString() + "\n]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { getLineConstruction1(), getLineConstruction2() };
			return returned;
		}

	}

}
