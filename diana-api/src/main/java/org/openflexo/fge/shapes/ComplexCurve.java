/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fge.shapes;

import java.util.List;

import org.openflexo.fge.GRProperty;
import org.openflexo.fge.geom.FGEGeneralShape.Closure;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a complex curve, with at least 2 points<br>
 * This complex curve is defined by a list of points linked with bezier curves, depending on defined closure.<br>
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 */
@ModelEntity
@XMLElement(xmlTag = "ComplexCurve")
public interface ComplexCurve extends ShapeSpecification {

	// Property keys

	@PropertyIdentifier(type = FGEPoint.class, cardinality = Cardinality.LIST)
	public static final String POINTS_KEY = "points";
	@PropertyIdentifier(type = Closure.class)
	public static final String CLOSURE_KEY = "closure";

	public static GRProperty<List> POINTS = GRProperty.getGRParameter(ComplexCurve.class, POINTS_KEY, List.class);
	public static GRProperty<Closure> CLOSURE = GRProperty.getGRParameter(ComplexCurve.class, CLOSURE_KEY, Closure.class);

	/*public static enum PolygonParameters implements GRProperty {
		points;
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = POINTS_KEY, cardinality = Cardinality.LIST, isStringConvertable = true)
	@XMLElement(primary = true)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<FGEPoint> getPoints();

	@Setter(POINTS_KEY)
	public void setPoints(List<FGEPoint> points);

	@Adder(POINTS_KEY)
	public void addToPoints(FGEPoint aPoint);

	@Remover(POINTS_KEY)
	public void removeFromPoints(FGEPoint aPoint);

	@Getter(value = CLOSURE_KEY, defaultValue = "CLOSED_FILLED")
	@XMLAttribute
	public Closure getClosure();

	@Setter(value = CLOSURE_KEY)
	public void setClosure(Closure aClosure);

}
