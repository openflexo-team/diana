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

import org.openflexo.fge.GRProperty;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a rectangular octogon.
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * @author etienne
 */
@ModelEntity
@XMLElement(xmlTag = "RectangularOctogonShape")
public interface RectangularOctogon extends ShapeSpecification {

	// Property Keys
	/**
	 * Cut ratio of rectangular octogon.
	 */
	@PropertyIdentifier(type = Double.class)
	public static final String			RATIO_KEY	= "ratio";

	public static GRProperty<Double>	RATIO		= GRProperty.getGRParameter(Plus.class, RATIO_KEY, Double.class);

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = RATIO_KEY, defaultValue = "0.2")
	@XMLAttribute
	public double getRatio();

	@Setter(value = RATIO_KEY)
	public void setRatio(final double aRatio);
}
