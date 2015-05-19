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

import org.openflexo.fge.GRProperty;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a rectangle, which could have rounded corners
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 */
@ModelEntity
@XMLElement(xmlTag = "RectangleShape")
public interface Rectangle extends ShapeSpecification {

	// Property keys

	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_ROUNDED_KEY = "isRounded";
	@PropertyIdentifier(type = Double.class)
	public static final String ARC_SIZE_KEY = "arcSize";

	public static GRProperty<Boolean> IS_ROUNDED = GRProperty.getGRParameter(Rectangle.class, IS_ROUNDED_KEY, Boolean.class);
	public static GRProperty<Double> ARC_SIZE = GRProperty.getGRParameter(Rectangle.class, ARC_SIZE_KEY, Double.class);

	/*public static enum RectangleParameters implements GRProperty {
		isRounded, arcSize;
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	/**
	 * Returns arc size (expressed in pixels for a 1.0 scale)
	 * 
	 * @return
	 */
	@Getter(value = ARC_SIZE_KEY, defaultValue = "30")
	@XMLAttribute
	public double getArcSize();

	/**
	 * Sets arc size (expressed in pixels for a 1.0 scale)
	 * 
	 * @param anArcSize
	 */
	@Setter(value = ARC_SIZE_KEY)
	public void setArcSize(double anArcSize);

	@Getter(value = IS_ROUNDED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsRounded();

	@Setter(value = IS_ROUNDED_KEY)
	public void setIsRounded(boolean aFlag);

}
