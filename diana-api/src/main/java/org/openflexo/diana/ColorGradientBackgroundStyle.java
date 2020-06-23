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

package org.openflexo.diana;

import java.awt.Color;

import javax.swing.ImageIcon;

import org.openflexo.diana.DianaUtils.HasIcon;
import org.openflexo.icon.ImageIconResource;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.rm.ResourceLocator;

/**
 * Represents a background colored with a linear gradient between two colors
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "ColorGradientBackgroundStyle")
public interface ColorGradientBackgroundStyle extends BackgroundStyle {

	@PropertyIdentifier(type = Color.class)
	public static final String COLOR1_KEY = "color1";
	@PropertyIdentifier(type = Color.class)
	public static final String COLOR2_KEY = "color2";
	@PropertyIdentifier(type = ColorGradientDirection.class)
	public static final String DIRECTION_KEY = "direction";

	public static GRProperty<Color> COLOR1 = GRProperty.getGRParameter(ColorGradientBackgroundStyle.class, COLOR1_KEY, Color.class);
	public static GRProperty<Color> COLOR2 = GRProperty.getGRParameter(ColorGradientBackgroundStyle.class, COLOR2_KEY, Color.class);
	public static GRProperty<ColorGradientDirection> DIRECTION = GRProperty.getGRParameter(ColorGradientBackgroundStyle.class,
			DIRECTION_KEY, ColorGradientDirection.class);

	public static enum ColorGradientDirection implements HasIcon {
		NORTH_SOUTH {
			@Override
			public ImageIcon getIcon() {
				return new ImageIconResource(ResourceLocator.locateResource("Icons/Gradient/NorthSouth.png"));
			}
		},
		WEST_EAST {
			@Override
			public ImageIcon getIcon() {
				return new ImageIconResource(ResourceLocator.locateResource("Icons/Gradient/WestEast.png"));
			}
		},
		NORTH_WEST_SOUTH_EAST {
			@Override
			public ImageIcon getIcon() {
				return new ImageIconResource(ResourceLocator.locateResource("Icons/Gradient/NorthWestSouthEast.png"));
			}
		},
		SOUTH_WEST_NORTH_EAST {
			@Override
			public ImageIcon getIcon() {
				return new ImageIconResource(ResourceLocator.locateResource("Icons/Gradient/SouthWestNorthEast.png"));
			}
		}
	}

	@Getter(value = COLOR1_KEY)
	@XMLAttribute
	public java.awt.Color getColor1();

	@Setter(value = COLOR1_KEY)
	public void setColor1(java.awt.Color aColor);

	@Getter(value = COLOR2_KEY)
	@XMLAttribute
	public java.awt.Color getColor2();

	@Setter(value = COLOR2_KEY)
	public void setColor2(java.awt.Color aColor);

	@Getter(value = DIRECTION_KEY)
	@XMLAttribute
	public ColorGradientDirection getDirection();

	@Setter(value = DIRECTION_KEY)
	public void setDirection(ColorGradientDirection aDirection);

}
