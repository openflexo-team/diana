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

import org.openflexo.diana.geom.DianaComplexCurve;
import org.openflexo.diana.geom.DianaGeneralShape.Closure;
import org.openflexo.diana.geomedit.model.ComplexCurveConstruction.ComplexCurveConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.ComplexCurveGraphicalRepresentation;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;

@ModelEntity(isAbstract = true)
@ImplementationClass(ComplexCurveConstructionImpl.class)
@Imports({ @Import(ComplexCurveWithNPointsConstruction.class) })
public interface ComplexCurveConstruction extends GeometricConstruction<DianaComplexCurve> {

	@PropertyIdentifier(type = Closure.class)
	public static final String CLOSURE_KEY = "closure";

	public DianaComplexCurve getCurve();

	@Getter(CLOSURE_KEY)
	@XMLAttribute
	public Closure getClosure();

	@Setter(CLOSURE_KEY)
	public void setClosure(Closure aClosure);

	public static abstract class ComplexCurveConstructionImpl extends GeometricConstructionImpl<DianaComplexCurve>
			implements ComplexCurveConstruction {

		@Override
		public String getBaseName() {
			return "Curve";
		}

		@Override
		public ComplexCurveGraphicalRepresentation makeNewConstructionGR(GeometricConstructionFactory factory) {
			ComplexCurveGraphicalRepresentation returned = factory.newInstance(ComplexCurveGraphicalRepresentation.class);
			return returned;
		}

		@Override
		public final DianaComplexCurve getCurve() {
			return getData();
		}

		@Override
		protected abstract DianaComplexCurve computeData();

		@Override
		public Closure getClosure() {
			Closure returned = (Closure) performSuperGetter(CLOSURE_KEY);
			if (returned == null) {
				return Closure.CLOSED_FILLED;
			}
			return returned;
		}

		@Override
		public void setClosure(Closure aClosure) {
			Closure oldClosure = getClosure();
			performSuperSetter(CLOSURE_KEY, aClosure);
			if (oldClosure != null && oldClosure != aClosure) {
				refresh();
				notifyGeometryChanged();
			}
		}

	}

}
