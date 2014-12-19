/*
 * (c) Copyright 2014- Openflexo
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
 * Interface for a parallelogram. The sole property is the shift_ratio (between total shape width and actual top side width) used to
 * calculate the shape If shift_ratio > 0, the parallelogram leans on the right, and if shift_ratio <0 it leans in the left
 * 
 * @author xtof
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "ParallelogramShape")
public interface Parallelogram extends ShapeSpecification {

	// Property Keys
	@PropertyIdentifier(type = Double.class)
	public static final String SHIFT_RATIO_KEY = "shiftRatio";

	public static GRProperty<Double> SHIFT_RATIO = GRProperty.getGRParameter(Parallelogram.class, SHIFT_RATIO_KEY, Double.class);

	// *******************************************************************************
	// * Properties
	// *******************************************************************************
	@Getter(value = SHIFT_RATIO_KEY, defaultValue = "0.2")
	@XMLAttribute
	public double getShiftRatio();

	@Setter(value = SHIFT_RATIO_KEY)
	public void setShiftRatio(final double anAngle);
}
