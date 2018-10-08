/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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
	@XMLElement(xmlTag = "Point", primary = true)
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
