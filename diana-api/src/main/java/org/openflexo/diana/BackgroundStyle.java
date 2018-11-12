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

import org.openflexo.diana.graphics.BGStyle;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;

/**
 * Represent background properties which should be applied to a graphical representation
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@Imports({ @Import(NoneBackgroundStyle.class), @Import(ColorBackgroundStyle.class), @Import(ColorGradientBackgroundStyle.class),
		@Import(TextureBackgroundStyle.class), @Import(BackgroundImageBackgroundStyle.class) })
public interface BackgroundStyle extends DianaStyle, BGStyle {

	@PropertyIdentifier(type = Float.class)
	public static final String TRANSPARENCY_LEVEL_KEY = "transparencyLevel";
	@PropertyIdentifier(type = Boolean.class)
	public static final String USE_TRANSPARENCY_KEY = "useTransparency";

	public static GRProperty<Float> TRANSPARENCY_LEVEL = GRProperty.getGRParameter(BackgroundStyle.class, TRANSPARENCY_LEVEL_KEY,
			Float.TYPE);
	public static GRProperty<Boolean> USE_TRANSPARENCY = GRProperty.getGRParameter(BackgroundStyle.class, USE_TRANSPARENCY_KEY,
			Boolean.TYPE);

	/*public static enum Parameters implements GRProperty {
		color,
		color1,
		color2,
		direction,
		textureType,
		imageFile,
		deltaX,
		deltaY,
		imageBackgroundType,
		scaleX,
		scaleY,
		fitToShape,
		imageBackgroundColor,
		transparencyLevel,
		useTransparency;
	}*/

	public static enum BackgroundStyleType {
		NONE, COLOR, COLOR_GRADIENT, TEXTURE, IMAGE
	}

	// public Paint getPaint(DrawingTreeNode<?, ?> dtn, double scale);

	public BackgroundStyleType getBackgroundStyleType();

	@Override
	public String toString();

	@Getter(value = TRANSPARENCY_LEVEL_KEY, defaultValue = "1.0")
	@XMLAttribute
	public float getTransparencyLevel();

	@Setter(value = TRANSPARENCY_LEVEL_KEY)
	public void setTransparencyLevel(float aLevel);

	@Getter(value = USE_TRANSPARENCY_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getUseTransparency();

	@Setter(value = USE_TRANSPARENCY_KEY)
	public void setUseTransparency(boolean aFlag);

	// public BackgroundStyle clone();

}
