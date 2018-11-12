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

package org.openflexo.diana.shapes;

import org.openflexo.diana.BackgroundStyle;
import org.openflexo.diana.DianaObject;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;

/**
 * This is the specification of a Shape<br>
 * Contains all the properties required to manage a Shape as a geometrical shape in a {@link ShapeNode}<br>
 * 
 * A {@link ShapeSpecification} is usually defined in a normed rectangle (bounds 0.0,0.0,1.0,1.0), but might be redefined in another bounds
 * (for example when grouped in a {@link ShapeUnion}).
 * 
 * A {@link ShapeSpecification} may also define a particular background and/or foreground (especially usefull in {@link ShapeUnion})
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
public interface ShapeSpecification extends DianaObject {

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

	public static final DianaPoint CENTER = new DianaPoint(0.5, 0.5);
	public static final DianaPoint NORTH_EAST = new DianaPoint(1, 0);
	public static final DianaPoint SOUTH_EAST = new DianaPoint(1, 1);
	public static final DianaPoint SOUTH_WEST = new DianaPoint(0, 1);
	public static final DianaPoint NORTH_WEST = new DianaPoint(0, 0);
	public static final DianaPoint NORTH = new DianaPoint(0.5, 0);
	public static final DianaPoint EAST = new DianaPoint(1, 0.5);
	public static final DianaPoint SOUTH = new DianaPoint(0.5, 1);
	public static final DianaPoint WEST = new DianaPoint(0, 0.5);

	@PropertyIdentifier(type = Double.class)
	public static final String X_KEY = "x";
	@PropertyIdentifier(type = Double.class)
	public static final String Y_KEY = "y";
	@PropertyIdentifier(type = Double.class)
	public static final String WIDTH_KEY = "width";
	@PropertyIdentifier(type = Double.class)
	public static final String HEIGHT_KEY = "height";

	@PropertyIdentifier(type = ForegroundStyle.class)
	public static final String FOREGROUND_KEY = "foreground";
	@PropertyIdentifier(type = BackgroundStyle.class)
	public static final String BACKGROUND_KEY = "background";

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
	 * Return background eventually overriding default background (usefull in ShapeUnion)<br>
	 * Default value is null
	 * 
	 * @return
	 */
	@Getter(value = BACKGROUND_KEY)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	public BackgroundStyle getBackground();

	/**
	 * Sets background eventually overriding default background (usefull in ShapeUnion)<br>
	 * 
	 * @param aBackground
	 */
	@Setter(value = BACKGROUND_KEY)
	public void setBackground(BackgroundStyle aBackground);

	/**
	 * Return foreground eventually overriding default foreground (usefull in ShapeUnion)<br>
	 * Default value is null
	 * 
	 * @return
	 */
	@Getter(value = FOREGROUND_KEY)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public ForegroundStyle getForeground();

	/**
	 * Sets foreground eventually overriding default foreground (usefull in ShapeUnion)<br>
	 * 
	 * @param aForeground
	 */
	@Setter(value = FOREGROUND_KEY)
	public void setForeground(ForegroundStyle aForeground);

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
	 * Build a new DianaShape for this {@link ShapeNode}, asserting the resulting shape will be defined in a normalized rectangle
	 * 
	 * @param node
	 * @return
	 */
	public DianaShape<?> makeNormalizedDianaShape(ShapeNode<?> node);

	/**
	 * Build a new DianaShape for this {@link ShapeNode}, when taking dimension/positionning properties into account
	 * 
	 * @param node
	 * @return
	 */
	public DianaShape<?> makeDianaShape(ShapeNode<?> node);

}
