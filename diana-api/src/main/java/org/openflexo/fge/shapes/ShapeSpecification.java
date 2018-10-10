/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-api, a component of the software infrastructure 
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

package org.openflexo.fge.shapes;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEObject;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;

/**
 * This is the specification of a Shape<br>
 * Contains all the properties required to manage a Shape as a geometrical shape in a {@link ShapeNode}<br>
 * 
 * A {@link ShapeSpecification} is usually defined in a normed rectangle (bounds 0.0,0.0,1.0,1.0), but might be redefined in another bounds
 * (for example when grouped in a ShapeUnion).
 * 
 * This implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 */
@ModelEntity(isAbstract = true)
@Imports({ @Import(Arc.class), @Import(Circle.class), @Import(Losange.class), @Import(Oval.class), @Import(Polygon.class),
		@Import(Rectangle.class), @Import(RectangularOctogon.class), @Import(RegularPolygon.class), @Import(Square.class),
		@Import(Star.class), @Import(Triangle.class), @Import(ComplexCurve.class), @Import(Plus.class), @Import(Parallelogram.class),
		@Import(Chevron.class), @Import(GeneralShape.class), @Import(ShapeUnion.class) })
public interface ShapeSpecification extends FGEObject {

	public static enum ShapeType {
		RECTANGLE,
		SQUARE,
		RECTANGULAROCTOGON,
		POLYGON,
		TRIANGLE,
		LOSANGE,
		OVAL,
		CIRCLE,
		STAR,
		ARC,
		CUSTOM_POLYGON,
		COMPLEX_CURVE,
		PLUS,
		CHEVRON,
		PARALLELOGRAM,
		GENERALSHAPE,
		UNION
	}

	public static final FGEPoint CENTER = new FGEPoint(0.5, 0.5);
	public static final FGEPoint NORTH_EAST = new FGEPoint(1, 0);
	public static final FGEPoint SOUTH_EAST = new FGEPoint(1, 1);
	public static final FGEPoint SOUTH_WEST = new FGEPoint(0, 1);
	public static final FGEPoint NORTH_WEST = new FGEPoint(0, 0);
	public static final FGEPoint NORTH = new FGEPoint(0.5, 0);
	public static final FGEPoint EAST = new FGEPoint(1, 0.5);
	public static final FGEPoint SOUTH = new FGEPoint(0.5, 1);
	public static final FGEPoint WEST = new FGEPoint(0, 0.5);

	@PropertyIdentifier(type = Double.class)
	public static final String X_KEY = "x";
	@PropertyIdentifier(type = Double.class)
	public static final String Y_KEY = "y";
	@PropertyIdentifier(type = Double.class)
	public static final String WIDTH_KEY = "width";
	@PropertyIdentifier(type = Double.class)
	public static final String HEIGHT_KEY = "height";

	@Getter(value = X_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getX();

	@Setter(value = X_KEY)
	public void setX(double aValue);

	@Getter(value = Y_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getY();

	@Setter(value = Y_KEY)
	public void setY(double aValue);

	@Getter(value = WIDTH_KEY, defaultValue = "1.0")
	@XMLAttribute
	public double getWidth();

	@Setter(value = WIDTH_KEY)
	public void setWidth(double aValue);

	@Getter(value = HEIGHT_KEY, defaultValue = "1.0")
	@XMLAttribute
	public double getHeight();

	@Setter(value = HEIGHT_KEY)
	public void setHeight(double aValue);

	/**
	 * Must be overriden when shape requires it
	 * 
	 * @return
	 */
	public boolean areDimensionConstrained();

	/**
	 * Return {@link ShapeType} for this {@link ShapeSpecification}
	 * 
	 * @return
	 */
	public ShapeType getShapeType();

	/**
	 * Build a new {@link Shape} for this {@link ShapeNode}
	 * 
	 * @param node
	 * @return
	 */
	public abstract Shape<?> makeShape(ShapeNode<?> node);

	/**
	 * Build a new FGEShape for this {@link ShapeNode}, asserting the resulting shape will be defined in a normalized rectangle
	 * 
	 * @param node
	 * @return
	 */
	public FGEShape<?> makeNormalizedFGEShape(ShapeNode<?> node);

	/**
	 * Build a new FGEShape for this {@link ShapeNode}, when taking dimension/positionning properties into account
	 * 
	 * @param node
	 * @return
	 */
	public FGEShape<?> makeFGEShape(ShapeNode<?> node);

}
