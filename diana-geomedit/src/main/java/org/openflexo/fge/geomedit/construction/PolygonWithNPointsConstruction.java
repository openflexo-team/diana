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

import java.util.List;
import java.util.Vector;

import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geomedit.construction.PolygonWithNPointsConstruction.PolygonWithNPointsConstructionImpl;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(PolygonWithNPointsConstructionImpl.class)
@XMLElement
public interface PolygonWithNPointsConstruction extends PolygonConstruction {

	@PropertyIdentifier(type = PointConstruction.class, cardinality = Cardinality.LIST)
	public static final String POINT_CONSTRUCTIONS_KEY = "pointConstructions";

	@Getter(value = POINT_CONSTRUCTIONS_KEY, cardinality = Cardinality.LIST)
	public List<PointConstruction> getPointConstructions();

	@Adder(POINT_CONSTRUCTIONS_KEY)
	public void addToPointConstructions(PointConstruction pointConstruction);

	@Remover(POINT_CONSTRUCTIONS_KEY)
	public void removeFromPointConstructions(PointConstruction pointConstruction);

	public static abstract class PolygonWithNPointsConstructionImpl extends PolygonConstructionImpl
			implements PolygonWithNPointsConstruction {

		public List<PointConstruction> pointConstructions;

		@Override
		public List<PointConstruction> getPointConstructions() {
			return pointConstructions;
		}

		@Override
		public void addToPointConstructions(PointConstruction pointConstruction) {
			pointConstructions.add(pointConstruction);
		}

		@Override
		public void removeFromPointConstructions(PointConstruction pointConstruction) {
			pointConstructions.remove(pointConstruction);
		}

		@Override
		protected FGEPolygon computeData() {
			Vector<FGEPoint> pts = new Vector<FGEPoint>();
			for (PointConstruction pc : pointConstructions) {
				pts.add(pc.getData());
			}
			return new FGEPolygon(getIsFilled() ? Filling.FILLED : Filling.NOT_FILLED, pts);
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("PolygonWithNPointsConstruction[\n");
			for (PointConstruction pc : pointConstructions) {
				sb.append("> " + pc.toString() + "\n");
			}
			sb.append("]");
			return sb.toString();
		}

		@Override
		public GeometricConstruction[] getDepends() {
			return pointConstructions.toArray(new GeometricConstruction[pointConstructions.size()]);
		}

	}

}
