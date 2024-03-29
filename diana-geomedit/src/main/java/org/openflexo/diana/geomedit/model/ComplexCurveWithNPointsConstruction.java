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

import java.util.List;
import java.util.Vector;

import org.openflexo.diana.geom.DianaComplexCurve;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geomedit.model.ComplexCurveWithNPointsConstruction.ComplexCurveWithNPointsConstructionImpl;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.annotations.Getter.Cardinality;

@ModelEntity
@ImplementationClass(ComplexCurveWithNPointsConstructionImpl.class)
@XMLElement
public interface ComplexCurveWithNPointsConstruction extends ComplexCurveConstruction {

	@PropertyIdentifier(type = PointConstruction.class, cardinality = Cardinality.LIST)
	public static final String POINT_CONSTRUCTIONS_KEY = "pointConstructions";

	@Getter(value = POINT_CONSTRUCTIONS_KEY, cardinality = Cardinality.LIST)
	@XMLElement
	public List<PointConstruction> getPointConstructions();

	@Adder(POINT_CONSTRUCTIONS_KEY)
	public void addToPointConstructions(PointConstruction pointConstruction);

	@Remover(POINT_CONSTRUCTIONS_KEY)
	public void removeFromPointConstructions(PointConstruction pointConstruction);

	public static abstract class ComplexCurveWithNPointsConstructionImpl extends ComplexCurveConstructionImpl
			implements ComplexCurveWithNPointsConstruction {

		@Override
		protected DianaComplexCurve computeData() {
			if (getPointConstructions() != null) {
				Vector<DianaPoint> pts = new Vector<>();
				for (PointConstruction pc : getPointConstructions()) {
					pts.add(pc.getPoint());
				}
				return new DianaComplexCurve(getClosure(), pts);
			}
			return null;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("CurveWithNPointsConstruction[\n");
			for (PointConstruction pc : getPointConstructions()) {
				sb.append("> " + pc.toString() + "\n");
			}
			sb.append("]");
			return sb.toString();
		}

		@Override
		public GeometricConstruction[] getDepends() {
			return getPointConstructions().toArray(new GeometricConstruction[getPointConstructions().size()]);
		}

	}

}
