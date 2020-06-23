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

import org.openflexo.diana.geomedit.model.PolygonConstruction.PolygonConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.PolygonGraphicalRepresentation;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.diana.geom.DianaPolygon;

@ModelEntity(isAbstract = true)
@ImplementationClass(PolygonConstructionImpl.class)
@Imports({ @Import(PolygonWithNPointsConstruction.class) })
public interface PolygonConstruction extends GeometricConstruction<DianaPolygon> {

	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_FILLED_KEY = "isFilled";

	@Getter(value = IS_FILLED_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsFilled();

	@Setter(IS_FILLED_KEY)
	public void setIsFilled(boolean isFilled);

	public DianaPolygon getPolygon();

	public static abstract class PolygonConstructionImpl extends GeometricConstructionImpl<DianaPolygon> implements PolygonConstruction {

		@Override
		public String getBaseName() {
			return "Polygon";
		}

		@Override
		public final DianaPolygon getPolygon() {
			return getData();
		}

		@Override
		public PolygonGraphicalRepresentation makeNewConstructionGR(GeometricConstructionFactory factory) {
			PolygonGraphicalRepresentation returned = factory.newInstance(PolygonGraphicalRepresentation.class);
			return returned;
		}

		@Override
		protected abstract DianaPolygon computeData();

	}
}
