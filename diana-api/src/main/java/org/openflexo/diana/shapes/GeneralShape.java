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

package org.openflexo.diana.shapes;

import java.util.List;

import org.openflexo.diana.DianaObject;
import org.openflexo.diana.GRProperty;
import org.openflexo.diana.geom.DianaGeneralShape.Closure;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.shapes.GeneralShape.CubicCurvePathElement;
import org.openflexo.diana.shapes.GeneralShape.QuadCurvePathElement;
import org.openflexo.diana.shapes.GeneralShape.SegmentPathElement;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a shape defined with a path (composed of segment and/or curves) and depending on defined closure.<br>
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 */
@ModelEntity
@XMLElement(xmlTag = "GeneralShape")
@Imports({ @Import(SegmentPathElement.class), @Import(QuadCurvePathElement.class), @Import(CubicCurvePathElement.class) })
public interface GeneralShape extends ShapeSpecification {

	@PropertyIdentifier(type = DianaPoint.class)
	public static final String START_POINT_KEY = "startPoint";
	@PropertyIdentifier(type = GeneralShapePathElement.class, cardinality = Cardinality.LIST)
	public static final String PATH_ELEMENTS_KEY = "pathElements";
	@PropertyIdentifier(type = Closure.class)
	public static final String CLOSURE_KEY = "closure";

	public static GRProperty<List> PATH_ELEMENTS = GRProperty.getGRParameter(GeneralShape.class, PATH_ELEMENTS_KEY, List.class);
	public static GRProperty<Closure> CLOSURE = GRProperty.getGRParameter(GeneralShape.class, CLOSURE_KEY, Closure.class);
	public static GRProperty<DianaPoint> START_POINT = GRProperty.getGRParameter(GeneralShape.class, START_POINT_KEY, DianaPoint.class);

	@Getter(value = START_POINT_KEY, isStringConvertable = true)
	@XMLAttribute
	public DianaPoint getStartPoint();

	@Setter(START_POINT_KEY)
	public void setStartPoint(DianaPoint point);

	@Getter(value = PATH_ELEMENTS_KEY, cardinality = Cardinality.LIST)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<GeneralShapePathElement> getPathElements();

	@Setter(PATH_ELEMENTS_KEY)
	public void setPathElements(List<GeneralShapePathElement> pathElements);

	@Adder(PATH_ELEMENTS_KEY)
	public void addToPathElements(GeneralShapePathElement pathElement);

	@Remover(PATH_ELEMENTS_KEY)
	public void removeFromPathElements(GeneralShapePathElement pathElement);

	@Getter(value = CLOSURE_KEY, defaultValue = "CLOSED_FILLED")
	@XMLAttribute
	public Closure getClosure();

	@Setter(value = CLOSURE_KEY)
	public void setClosure(Closure aClosure);

	@ModelEntity(isAbstract = true)
	public static interface GeneralShapePathElement extends DianaObject {}

	@ModelEntity
	@XMLElement(xmlTag = "Segment")
	public static interface SegmentPathElement extends GeneralShapePathElement {

		@PropertyIdentifier(type = DianaPoint.class)
		public static final String POINT_KEY = "point";

		@Getter(value = POINT_KEY, isStringConvertable = true)
		@XMLAttribute
		public DianaPoint getPoint();

		@Setter(POINT_KEY)
		public void setPoint(DianaPoint point);

	}

	@ModelEntity
	@XMLElement(xmlTag = "QuadCurve")
	public static interface QuadCurvePathElement extends GeneralShapePathElement {

		@PropertyIdentifier(type = DianaPoint.class)
		public static final String POINT_KEY = "point";
		@PropertyIdentifier(type = DianaPoint.class)
		public static final String CONTROL_POINT_KEY = "controlPoint";

		@Getter(value = POINT_KEY, isStringConvertable = true)
		@XMLAttribute
		public DianaPoint getPoint();

		@Setter(POINT_KEY)
		public void setPoint(DianaPoint point);

		@Getter(value = CONTROL_POINT_KEY, isStringConvertable = true)
		@XMLAttribute
		public DianaPoint getControlPoint();

		@Setter(CONTROL_POINT_KEY)
		public void setControlPoint(DianaPoint point);

	}

	@ModelEntity
	@XMLElement(xmlTag = "CubicCurve")
	public static interface CubicCurvePathElement extends GeneralShapePathElement {

		@PropertyIdentifier(type = DianaPoint.class)
		public static final String POINT_KEY = "point";
		@PropertyIdentifier(type = DianaPoint.class)
		public static final String CONTROL_POINT_1_KEY = "controlPoint1";
		@PropertyIdentifier(type = DianaPoint.class)
		public static final String CONTROL_POINT_2_KEY = "controlPoint2";

		@Getter(value = POINT_KEY, isStringConvertable = true)
		@XMLAttribute
		public DianaPoint getPoint();

		@Setter(POINT_KEY)
		public void setPoint(DianaPoint point);

		@Getter(value = CONTROL_POINT_1_KEY, isStringConvertable = true)
		@XMLAttribute
		public DianaPoint getControlPoint1();

		@Setter(CONTROL_POINT_1_KEY)
		public void setControlPoint1(DianaPoint point);

		@Getter(value = CONTROL_POINT_2_KEY, isStringConvertable = true)
		@XMLAttribute
		public DianaPoint getControlPoint2();

		@Setter(CONTROL_POINT_2_KEY)
		public void setControlPoint2(DianaPoint point);

	}

}
