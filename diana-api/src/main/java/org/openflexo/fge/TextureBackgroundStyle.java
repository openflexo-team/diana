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

package org.openflexo.fge;

import java.awt.Color;

import javax.swing.ImageIcon;

import org.openflexo.fge.FGEUtils.HasIcon;
import org.openflexo.icon.ImageIconResource;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.rm.ResourceLocator;

/**
 * Represents a textured background defined with a texture and two colors
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "TexturedBackgroundStyle")
public interface TextureBackgroundStyle extends BackgroundStyle {

	@PropertyIdentifier(type = Color.class)
	public static final String COLOR1_KEY = "color1";
	@PropertyIdentifier(type = Color.class)
	public static final String COLOR2_KEY = "color2";
	@PropertyIdentifier(type = TextureType.class)
	public static final String TEXTURE_TYPE_KEY = "textureType";

	public static GRProperty<Color> COLOR1 = GRProperty.getGRParameter(TextureBackgroundStyle.class, COLOR1_KEY, Color.class);
	public static GRProperty<Color> COLOR2 = GRProperty.getGRParameter(TextureBackgroundStyle.class, COLOR2_KEY, Color.class);
	public static GRProperty<TextureType> TEXTURE_TYPE = GRProperty.getGRParameter(TextureBackgroundStyle.class, TEXTURE_TYPE_KEY,
			TextureType.class);

	public static enum TextureType implements HasIcon {
		TEXTURE1,
		TEXTURE2,
		TEXTURE3,
		TEXTURE4,
		TEXTURE5,
		TEXTURE6,
		TEXTURE7,
		TEXTURE8,
		TEXTURE9,
		TEXTURE10,
		TEXTURE11,
		TEXTURE12,
		TEXTURE13,
		TEXTURE14,
		TEXTURE15,
		TEXTURE16;

		public ImageIcon getImageIcon() {
			return new ImageIconResource(ResourceLocator.locateResource("Motifs/Motif" + (ordinal() + 1) + ".gif"));
		}

		@Override
		public ImageIcon getIcon() {
			return getImageIcon();
		}
	}

	@Getter(value = TEXTURE_TYPE_KEY)
	@XMLAttribute
	public TextureType getTextureType();

	@Setter(value = TEXTURE_TYPE_KEY)
	public void setTextureType(TextureType aTextureType);

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

}
