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

package org.openflexo.diana;

import java.util.List;

import org.openflexo.diana.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaSteppedDimensionConstraint;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a container in a diagram <br>
 * A container has a size (a width and an height), and may defines layout features<br>
 * Basic implementations of a container include drawing (root of a diagram), and shapes<br>
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
public interface ContainerGraphicalRepresentation extends GraphicalRepresentation {

	public static final double DEFAULT_WIDTH = 100;
	public static final double DEFAULT_HEIGHT = 100;

	// Property keys

	@PropertyIdentifier(type = Double.class)
	public static final String WIDTH_KEY = "width";
	@PropertyIdentifier(type = Double.class)
	public static final String HEIGHT_KEY = "height";

	@PropertyIdentifier(type = Double.class)
	public static final String MINIMAL_WIDTH_KEY = "minimalWidth";
	@PropertyIdentifier(type = Double.class)
	public static final String MINIMAL_HEIGHT_KEY = "minimalHeight";
	@PropertyIdentifier(type = Double.class)
	public static final String MAXIMAL_WIDTH_KEY = "maximalWidth";
	@PropertyIdentifier(type = Double.class)
	public static final String MAXIMAL_HEIGHT_KEY = "maximalHeight";
	@PropertyIdentifier(type = DianaSteppedDimensionConstraint.class)
	public static final String DIMENSION_CONSTRAINT_STEP_KEY = "dimensionConstraintStep";
	@PropertyIdentifier(type = DimensionConstraints.class)
	public static final String DIMENSION_CONSTRAINTS_KEY = "dimensionConstraints";
	@PropertyIdentifier(type = Boolean.class)
	public static final String ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH_KEY = "adjustMinimalWidthToLabelWidth";
	@PropertyIdentifier(type = Boolean.class)
	public static final String ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT_KEY = "adjustMinimalHeightToLabelHeight";
	@PropertyIdentifier(type = Boolean.class)
	public static final String ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH_KEY = "adjustMaximalWidthToLabelWidth";
	@PropertyIdentifier(type = Boolean.class)
	public static final String ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT_KEY = "adjustMaximalHeightToLabelHeight";
	@PropertyIdentifier(type = DianaLayoutManagerSpecification.class, cardinality = Cardinality.LIST)
	public static final String LAYOUT_MANAGER_SPECIFICATIONS_KEY = "layoutManagerSpecifications";

	public static GRProperty<Double> WIDTH = GRProperty.getGRParameter(ContainerGraphicalRepresentation.class, WIDTH_KEY, Double.TYPE);
	public static GRProperty<Double> HEIGHT = GRProperty.getGRParameter(ContainerGraphicalRepresentation.class, HEIGHT_KEY, Double.TYPE);

	public static GRProperty<Boolean> ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT = GRProperty.getGRParameter(ShapeGraphicalRepresentation.class,
			ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT_KEY, Boolean.class);
	public static GRProperty<Boolean> ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH = GRProperty
			.getGRParameter(ContainerGraphicalRepresentation.class, ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH_KEY, Boolean.class);
	public static GRProperty<Boolean> ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT = GRProperty.getGRParameter(ShapeGraphicalRepresentation.class,
			ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT_KEY, Boolean.class);
	public static GRProperty<Boolean> ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH = GRProperty
			.getGRParameter(ContainerGraphicalRepresentation.class, ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH_KEY, Boolean.class);
	public static GRProperty<Double> MINIMAL_WIDTH = GRProperty.getGRParameter(ContainerGraphicalRepresentation.class, MINIMAL_WIDTH_KEY,
			Double.class);
	public static GRProperty<Double> MINIMAL_HEIGHT = GRProperty.getGRParameter(ContainerGraphicalRepresentation.class, MINIMAL_HEIGHT_KEY,
			Double.class);
	public static GRProperty<Double> MAXIMAL_WIDTH = GRProperty.getGRParameter(ContainerGraphicalRepresentation.class, MAXIMAL_WIDTH_KEY,
			Double.class);
	public static GRProperty<Double> MAXIMAL_HEIGHT = GRProperty.getGRParameter(ContainerGraphicalRepresentation.class, MAXIMAL_HEIGHT_KEY,
			Double.class);
	public static GRProperty<DimensionConstraints> DIMENSION_CONSTRAINTS = GRProperty.getGRParameter(ContainerGraphicalRepresentation.class,
			DIMENSION_CONSTRAINTS_KEY, DimensionConstraints.class);
	public static GRProperty<DianaSteppedDimensionConstraint> DIMENSION_CONSTRAINT_STEP = GRProperty
			.getGRParameter(ContainerGraphicalRepresentation.class, DIMENSION_CONSTRAINT_STEP_KEY, DianaSteppedDimensionConstraint.class);

	public static GRProperty<List> LAYOUT_MANAGER_SPECIFICATIONS = GRProperty.getGRParameter(ContainerGraphicalRepresentation.class,
			LAYOUT_MANAGER_SPECIFICATIONS_KEY, List.class);

	/*public static enum ContainerParameters implements GRProperty {
		width, height;
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = WIDTH_KEY, defaultValue = "" + DEFAULT_WIDTH)
	@XMLAttribute
	public abstract double getWidth();

	@Setter(value = WIDTH_KEY)
	public abstract void setWidth(double aValue);

	@Getter(value = HEIGHT_KEY, defaultValue = "" + DEFAULT_HEIGHT)
	@XMLAttribute
	public abstract double getHeight();

	@Setter(value = HEIGHT_KEY)
	public abstract void setHeight(double aValue);

	public DianaDimension getSize();

	@Getter(value = MINIMAL_WIDTH_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getMinimalWidth();

	@Setter(value = MINIMAL_WIDTH_KEY)
	public void setMinimalWidth(double minimalWidth);

	@Getter(value = MINIMAL_HEIGHT_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getMinimalHeight();

	@Setter(value = MINIMAL_HEIGHT_KEY)
	public void setMinimalHeight(double minimalHeight);

	@Getter(value = MAXIMAL_HEIGHT_KEY, defaultValue = "POSITIVE_INFINITY")
	@XMLAttribute
	public double getMaximalHeight();

	@Setter(value = MAXIMAL_HEIGHT_KEY)
	public void setMaximalHeight(double maximalHeight);

	@Getter(value = MAXIMAL_WIDTH_KEY, defaultValue = "POSITIVE_INFINITY")
	@XMLAttribute
	public double getMaximalWidth();

	@Setter(value = MAXIMAL_WIDTH_KEY)
	public void setMaximalWidth(double maximalWidth);

	@Getter(value = DIMENSION_CONSTRAINT_STEP_KEY, isStringConvertable = true)
	@XMLAttribute
	public DianaSteppedDimensionConstraint getDimensionConstraintStep();

	@Setter(value = DIMENSION_CONSTRAINT_STEP_KEY)
	public void setDimensionConstraintStep(DianaSteppedDimensionConstraint dimensionConstraintStep);

	@Getter(value = DIMENSION_CONSTRAINTS_KEY)
	@XMLAttribute
	public DimensionConstraints getDimensionConstraints();

	@Setter(value = DIMENSION_CONSTRAINTS_KEY)
	public void setDimensionConstraints(DimensionConstraints dimensionConstraints);

	@Getter(value = ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getAdjustMinimalWidthToLabelWidth();

	@Setter(value = ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH_KEY)
	public void setAdjustMinimalWidthToLabelWidth(boolean adjustMinimalWidthToLabelWidth);

	@Getter(value = ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getAdjustMinimalHeightToLabelHeight();

	@Setter(value = ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT_KEY)
	public void setAdjustMinimalHeightToLabelHeight(boolean adjustMinimalHeightToLabelHeight);

	@Getter(value = ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getAdjustMaximalWidthToLabelWidth();

	@Setter(value = ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH_KEY)
	public void setAdjustMaximalWidthToLabelWidth(boolean adjustMaximalWidthToLabelWidth);

	@Getter(value = ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getAdjustMaximalHeightToLabelHeight();

	@Setter(value = ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT_KEY)
	public void setAdjustMaximalHeightToLabelHeight(boolean adjustMaximalHeightToLabelHeight);

	@Getter(value = LAYOUT_MANAGER_SPECIFICATIONS_KEY, cardinality = Cardinality.LIST)
	@Embedded
	@XMLElement
	public List<DianaLayoutManagerSpecification<?>> getLayoutManagerSpecifications();

	@Setter(value = LAYOUT_MANAGER_SPECIFICATIONS_KEY)
	public void setLayoutManagerSpecifications(List<DianaLayoutManagerSpecification<?>> layoutManagerSpecifications);

	@Adder(value = LAYOUT_MANAGER_SPECIFICATIONS_KEY)
	public void addToLayoutManagerSpecifications(DianaLayoutManagerSpecification<?> layoutManagerSpecification);

	@Remover(value = LAYOUT_MANAGER_SPECIFICATIONS_KEY)
	public void removeFromLayoutManagerSpecifications(DianaLayoutManagerSpecification<?> layoutManagerSpecification);

	@Finder(collection = LAYOUT_MANAGER_SPECIFICATIONS_KEY, attribute = DianaLayoutManagerSpecification.IDENTIFIER_KEY)
	public DianaLayoutManagerSpecification<?> getLayoutManagerSpecification(String identifier);
}
