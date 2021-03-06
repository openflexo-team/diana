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

import org.openflexo.diana.geomedit.model.QuadCurveConstruction.QuadCurveConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.QuadCurveGraphicalRepresentation;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.diana.geom.DianaQuadCurve;

@ModelEntity(isAbstract = true)
@ImplementationClass(QuadCurveConstructionImpl.class)
@Imports({ @Import(QuadCurveWithThreePointsConstruction.class) })
public interface QuadCurveConstruction extends GeometricConstruction<DianaQuadCurve> {

	public DianaQuadCurve getCurve();

	public static abstract class QuadCurveConstructionImpl extends GeometricConstructionImpl<DianaQuadCurve>
			implements QuadCurveConstruction {

		@Override
		public String getBaseName() {
			return "Curve";
		}

		@Override
		public final DianaQuadCurve getCurve() {
			return getData();
		}

		@Override
		public QuadCurveGraphicalRepresentation makeNewConstructionGR(GeometricConstructionFactory factory) {
			QuadCurveGraphicalRepresentation returned = factory.newInstance(QuadCurveGraphicalRepresentation.class);
			return returned;
		}

		@Override
		protected abstract DianaQuadCurve computeData();

	}

}
