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

import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geomedit.model.NodeWithTwoPointsConstruction.NodeWithTwoPointsConstructionImpl;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(NodeWithTwoPointsConstructionImpl.class)
@XMLElement
public interface NodeWithTwoPointsConstruction extends NodeConstruction {

	@PropertyIdentifier(type = PointConstruction.class)
	public static final String POINT_CONSTRUCTION_1_KEY = "pointConstruction1";
	@PropertyIdentifier(type = PointConstruction.class)
	public static final String POINT_CONSTRUCTION_2_KEY = "pointConstruction2";

	@Getter(value = POINT_CONSTRUCTION_1_KEY)
	@XMLElement(context = "P1_")
	public PointConstruction getPointConstruction1();

	@Setter(value = POINT_CONSTRUCTION_1_KEY)
	public void setPointConstruction1(PointConstruction pointConstruction1);

	@Getter(value = POINT_CONSTRUCTION_2_KEY)
	@XMLElement(context = "P2_")
	public PointConstruction getPointConstruction2();

	@Setter(value = POINT_CONSTRUCTION_2_KEY)
	public void setPointConstruction2(PointConstruction pointConstruction2);

	public static abstract class NodeWithTwoPointsConstructionImpl extends NodeConstructionImpl implements NodeWithTwoPointsConstruction {

		@Override
		protected DianaShape<?> computeData() {
			if (getPointConstruction1() != null && getPointConstruction2() != null && getFactory() != null) {
				DianaPoint p1 = getPointConstruction1().getPoint();
				DianaPoint p2 = getPointConstruction2().getPoint();

				DianaPoint p = new DianaPoint();
				p.x = Math.min(p1.x, p2.x);
				p.y = Math.min(p1.y, p2.y);

				double width = Math.abs(p1.x - p2.x);
				double height = Math.abs(p1.y - p2.y);
				DianaDimension dim = new DianaDimension(width, height);

				if (getShapeSpecification() == null) {
					setShapeSpecification(getFactory().makeShape(getShapeType()));
				}

				System.out.println("On se construit une nouvelle shape pour " + getShapeSpecification());
				System.out.println("avec " + p + " et " + dim);
				System.out.println("gr=" + getGraphicalRepresentation());

				DianaShape<?> returned = getShapeSpecification()
						.makeDianaShape(new DianaRectangle(p, dim, getIsFilled() ? Filling.FILLED : Filling.NOT_FILLED));
				getShapeSpecification().setX(p.x);
				getShapeSpecification().setY(p.y);
				getShapeSpecification().setWidth(width);
				getShapeSpecification().setHeight(height);

				System.out.println("On retourne " + returned);

				if (getGraphicalRepresentation() != null) {
					getGraphicalRepresentation().setGeometricObject(returned);
				}
				return returned;
			}

			System.out.println("Zut alors on peut pas encore construire");
			Thread.dumpStack();

			return null;
		}

		@Override
		public String toString() {
			return "NodeWithTwoPointsConstruction[\n" + "> " + getPointConstruction1().toString() + "\n> "
					+ getPointConstruction2().toString() + "\n]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { getPointConstruction1(), getPointConstruction2() };
			return returned;
		}

	}

}
