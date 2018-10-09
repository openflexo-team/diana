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
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;

/**
 * This is the specification of a Shape<br>
 * Contains all the properties required to manage a Shape as a geometrical shape in a {@link ShapeNode}<br>
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 */
@ModelEntity(isAbstract = true)
@Imports({ @Import(Arc.class), @Import(Circle.class), @Import(Losange.class), @Import(Oval.class), @Import(Polygon.class),
		@Import(Rectangle.class), @Import(RectangularOctogon.class), @Import(RegularPolygon.class), @Import(Square.class),
		@Import(Star.class), @Import(Triangle.class), @Import(ComplexCurve.class), @Import(Plus.class), @Import(Parallelogram.class),
		@Import(Chevron.class), @Import(GeneralShape.class) })
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
		GENERALSHAPE
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

	/**
	 * Must be overriden when shape requires it
	 * 
	 * @return
	 */
	public boolean areDimensionConstrained();

	public ShapeType getShapeType();

	// public ShapeSpecification clone();

	public abstract Shape<?> makeShape(ShapeNode<?> node);

	public FGEShape<?> makeFGEShape(ShapeNode<?> node);

}
