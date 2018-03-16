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

import java.awt.Color;
import java.awt.Font;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represent text properties which should be applied to a graphical representation
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "TextStyle")
public interface TextStyle extends FGEStyle {

	// Property keys

	@PropertyIdentifier(type = Color.class)
	public static final String COLOR_KEY = "color";
	@PropertyIdentifier(type = Color.class)
	public static final String BACKGROUND_COLOR_KEY = "backgroundColor";
	@PropertyIdentifier(type = Font.class)
	public static final String FONT_KEY = "font";
	@PropertyIdentifier(type = Integer.class, isPrimitive = true)
	public static final String ORIENTATION_KEY = "orientation";
	@PropertyIdentifier(type = Boolean.class, isPrimitive = true)
	public static final String IS_BACKGROUND_COLORED_KEY = "isBackgroundColored";

	public static GRProperty<Color> COLOR = GRProperty.getGRParameter(TextStyle.class, COLOR_KEY, Color.class);
	public static GRProperty<Color> BACKGROUND_COLOR = GRProperty.getGRParameter(TextStyle.class, BACKGROUND_COLOR_KEY, Color.class);
	public static GRProperty<Font> FONT = GRProperty.getGRParameter(TextStyle.class, FONT_KEY, Font.class);
	public static GRProperty<Integer> ORIENTATION = GRProperty.getGRParameter(TextStyle.class, ORIENTATION_KEY, Integer.TYPE);
	public static GRProperty<Boolean> IS_BACKGROUND_COLORED = GRProperty.getGRParameter(TextStyle.class, IS_BACKGROUND_COLORED_KEY,
			Boolean.TYPE);

	/*public static enum Parameters implements GRProperty {
		color, backgroundColor, font, orientation, backgroundColored
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = COLOR_KEY)
	@XMLAttribute
	public Color getColor();

	@Setter(value = COLOR_KEY)
	public void setColor(Color aColor);

	@Getter(value = BACKGROUND_COLOR_KEY)
	@XMLAttribute
	public Color getBackgroundColor();

	@Setter(value = BACKGROUND_COLOR_KEY)
	public void setBackgroundColor(Color aColor);

	@Getter(value = FONT_KEY)
	@XMLAttribute
	public Font getFont();

	@Setter(value = FONT_KEY)
	public void setFont(Font aFont);

	@Getter(value = ORIENTATION_KEY, defaultValue = "0")
	@XMLAttribute
	public int getOrientation();

	@Setter(value = ORIENTATION_KEY)
	public void setOrientation(int anOrientation);

	@Getter(value = IS_BACKGROUND_COLORED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsBackgroundColored();

	@Setter(value = IS_BACKGROUND_COLORED_KEY)
	public void setIsBackgroundColored(boolean aFlag);

	// public TextStyle clone();

}
