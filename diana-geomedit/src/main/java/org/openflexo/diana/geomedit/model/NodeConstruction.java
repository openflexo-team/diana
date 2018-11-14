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

import org.openflexo.diana.ShadowStyle;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geomedit.model.NodeConstruction.NodeConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.GeometricObjectGraphicalRepresentation;
import org.openflexo.diana.geomedit.model.gr.NodeGraphicalRepresentation;
import org.openflexo.diana.shapes.ShapeSpecification;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity(isAbstract = true)
@ImplementationClass(NodeConstructionImpl.class)
@Imports({ @Import(NodeWithTwoPointsConstruction.class) })
public interface NodeConstruction extends GeometricConstruction<DianaShape<?>> {

	@PropertyIdentifier(type = Double.class)
	public static final String X_KEY = "x";
	@PropertyIdentifier(type = Double.class)
	public static final String Y_KEY = "y";
	@PropertyIdentifier(type = Double.class)
	public static final String WIDTH_KEY = "width";
	@PropertyIdentifier(type = Double.class)
	public static final String HEIGHT_KEY = "height";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_FILLED_KEY = "isFilled";
	@PropertyIdentifier(type = ShapeSpecification.class)
	public static final String SHAPE_SPECIFICATION_KEY = "shapeSpecification";
	@PropertyIdentifier(type = ShadowStyle.class)
	public static final String SHADOW_STYLE_KEY = "shadowStyle";

	@Getter(value = X_KEY, defaultValue = "0.0")
	public double getX();

	@Setter(X_KEY)
	public void setX(double value);

	@Getter(value = Y_KEY, defaultValue = "0.0")
	public double getY();

	@Setter(Y_KEY)
	public void setY(double value);

	@Getter(value = WIDTH_KEY, defaultValue = "0.0")
	public double getWidth();

	@Setter(WIDTH_KEY)
	public void setWidth(double value);

	@Getter(value = HEIGHT_KEY, defaultValue = "0.0")
	public double getHeight();

	@Setter(HEIGHT_KEY)
	public void setHeight(double value);

	public DianaShape<?> getShape();

	@Getter(value = IS_FILLED_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsFilled();

	@Setter(IS_FILLED_KEY)
	public void setIsFilled(boolean isFilled);

	@Getter(value = SHAPE_SPECIFICATION_KEY)
	@XMLElement
	@Embedded
	@CloningStrategy(StrategyType.CLONE)
	public ShapeSpecification getShapeSpecification();

	@Setter(value = SHAPE_SPECIFICATION_KEY)
	public void setShapeSpecification(ShapeSpecification aShape);

	@Getter(value = SHADOW_STYLE_KEY)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public ShadowStyle getShadowStyle();

	@Setter(value = SHADOW_STYLE_KEY)
	public void setShadowStyle(ShadowStyle aShadowStyle);

	public ShapeType getShapeType();

	public void setShapeType(ShapeType shapeType);

	public abstract class NodeConstructionImpl extends GeometricConstructionImpl<DianaShape<?>> implements NodeConstruction {

		@Override
		public String getBaseName() {
			return "Node";
		}

		@Override
		public final DianaShape<?> getShape() {
			return getData();
		}

		@Override
		public GeometricObjectGraphicalRepresentation<DianaShape<?>> makeNewConstructionGR(GeometricConstructionFactory factory) {
			NodeGraphicalRepresentation returned = factory.newInstance(NodeGraphicalRepresentation.class);
			return returned;
		}

		@Override
		protected abstract DianaShape<?> computeData();

		@Override
		public void setIsFilled(boolean isFilled) {
			performSuperSetter(IS_FILLED_KEY, isFilled);
			refresh();
			notifyGeometryChanged();
		}

		@Override
		public ShapeType getShapeType() {
			if (getShapeSpecification() == null) {
				setShapeSpecification(getGeometricDiagram().getFactory().makeShape(ShapeType.RECTANGLE));
			}
			return getShapeSpecification().getShapeType();
		}

		@Override
		public void setShapeType(ShapeType shapeType) {
			if (getShapeType() != shapeType) {
				setShapeSpecification(getGeometricDiagram().getFactory().makeShape(shapeType));
				System.out.println("SS: " + getShapeSpecification());
			}
		}

	}
}
