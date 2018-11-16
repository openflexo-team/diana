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

import java.awt.geom.AffineTransform;

import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.shapes.ShapeSpecification;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(NodeWithRelativePositionConstruction.NodeWithRelativePositionConstructionImpl.class)
@XMLElement
public interface NodeWithRelativePositionConstruction extends NodeConstruction {

	@PropertyIdentifier(type = NodeConstruction.class)
	public static final String NODE_REFERENCE_KEY = "nodeReference";
	@PropertyIdentifier(type = Double.class)
	public static final String TX_KEY = "tx";
	@PropertyIdentifier(type = Double.class)
	public static final String TY_KEY = "ty";

	@Getter(value = NODE_REFERENCE_KEY)
	@XMLElement
	public NodeConstruction getReference();

	@Setter(value = NODE_REFERENCE_KEY)
	public void setReference(NodeConstruction nodeReference);

	@Getter(value = TX_KEY, defaultValue = "100.0")
	public double getTX();

	@Setter(TX_KEY)
	public void setTX(double value);

	@Getter(value = TY_KEY, defaultValue = "100.0")
	public double getTY();

	@Setter(TY_KEY)
	public void setTY(double value);

	@Override
	@Getter(value = WIDTH_KEY, defaultValue = "40.0")
	@XMLAttribute
	public double getWidth();

	@Setter(WIDTH_KEY)
	public void setWidth(double value);

	@Override
	@Getter(value = HEIGHT_KEY, defaultValue = "40.0")
	@XMLAttribute
	public double getHeight();

	@Setter(HEIGHT_KEY)
	public void setHeight(double value);

	public static abstract class NodeWithRelativePositionConstructionImpl extends NodeConstructionImpl
			implements NodeWithRelativePositionConstruction {

		@Override
		protected DianaShape<?> computeData() {
			if (getReference() != null && getFactory() != null) {
				DianaPoint referenceCenter = getReference().getData().getCenter();
				DianaPoint center = referenceCenter.transform(AffineTransform.getTranslateInstance(getTX(), getTY()));

				double width = getWidth();
				double height = getHeight();
				DianaPoint p = new DianaPoint(center.x - width / 2, center.y - width / 2);
				DianaDimension dim = new DianaDimension(width, height);

				if (getShapeSpecification() == null) {
					ShapeSpecification shapeSpecification = getFactory().makeShape(getShapeType());
					shapeSpecification.setX(p.x);
					shapeSpecification.setY(p.y);
					shapeSpecification.setWidth(width);
					shapeSpecification.setHeight(height);
					_setShapeSpecificationNoNotification(shapeSpecification);
				}

				getShapeSpecification().setX(p.x);
				getShapeSpecification().setY(p.y);
				getShapeSpecification().setWidth(width);
				getShapeSpecification().setHeight(height);
				DianaShape<?> returned = getShapeSpecification()
						.makeDianaShape(new DianaRectangle(p, dim, getIsFilled() ? Filling.FILLED : Filling.NOT_FILLED));

				return returned;
			}

			return null;
		}

		@Override
		public String toString() {
			return "NodeWithCenterAndDimension[" + getReference().toString() + "] tx=" + getTX() + " ty=" + getTY();
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { getReference() };
			return returned;
		}

		@Override
		public double getX() {
			return getShapeSpecification().getX();
		}

		@Override
		public double getY() {
			return getShapeSpecification().getY();
		}

		@Override
		public void setWidth(double width) {
			if (width != getWidth()) {
				performSuperSetter(WIDTH_KEY, width);
				refresh();
				notifyGeometryChanged();
			}
		}

		@Override
		public void setHeight(double height) {
			if (height != getHeight()) {
				performSuperSetter(HEIGHT_KEY, height);
				refresh();
				notifyGeometryChanged();
			}
		}

	}

}
