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

import org.openflexo.diana.geomedit.model.ExplicitPointConstruction.ExplicitPointConstructionImpl;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(ExplicitPointConstructionImpl.class)
@XMLElement
public interface ExplicitPointConstruction extends PointConstruction {

	@PropertyIdentifier(type = FGEPoint.class)
	public static final String POINT_KEY = "point";

	@Override
	@Getter(value = POINT_KEY, isStringConvertable = true)
	@XMLAttribute
	public FGEPoint getPoint();

	@Setter(POINT_KEY)
	public void setPoint(FGEPoint p);

	public static abstract class ExplicitPointConstructionImpl extends PointConstructionImpl implements ExplicitPointConstruction {

		private FGEPoint point;

		@Override
		public final FGEPoint getPoint() {
			return point;
		}

		@Override
		public void setPoint(FGEPoint p) {
			if (point == null || p.x != point.x || p.y != point.y) {
				point = p;
				System.out.println("Je mets le point a " + p);
				setModified(true);
				notifyGeometryChanged();
			}
		}

		@Override
		protected FGEPoint computeData() {
			System.out.println("Voila j'ai mon point: " + point);
			return point;
		}

		@Override
		public String toString() {
			return "ExplicitPointConstruction[" + point.toString() + "]";
		}

		@Override
		public GeometricConstruction<?>[] getDepends() {
			return null;
		}
	}

}