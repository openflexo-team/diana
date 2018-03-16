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

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represent shadow properties which should be applied to a graphical representation
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "ShadowStyle")
public interface ShadowStyle extends FGEStyle {

	public static final String NONE_CONFIGURATION = "none";
	public static final String DEFAULT_CONFIGURATION = "default";

	@PropertyIdentifier(type = Boolean.class)
	public static final String DRAW_SHADOW_KEY = "drawShadow";
	@PropertyIdentifier(type = Integer.class)
	public static final String SHADOW_DARKNESS_KEY = "shadowDarkness";
	@PropertyIdentifier(type = Integer.class)
	public static final String SHADOW_DEPTH_KEY = "shadowDepth";
	@PropertyIdentifier(type = Integer.class)
	public static final String SHADOW_BLUR_KEY = "shadowBlur";

	public static GRProperty<Boolean> DRAW_SHADOW = GRProperty.getGRParameter(ShadowStyle.class, DRAW_SHADOW_KEY, Boolean.TYPE);
	public static GRProperty<Integer> SHADOW_DARKNESS = GRProperty.getGRParameter(ShadowStyle.class, SHADOW_DARKNESS_KEY, Integer.TYPE);
	public static GRProperty<Integer> SHADOW_DEPTH = GRProperty.getGRParameter(ShadowStyle.class, SHADOW_DEPTH_KEY, Integer.TYPE);
	public static GRProperty<Integer> SHADOW_BLUR = GRProperty.getGRParameter(ShadowStyle.class, SHADOW_BLUR_KEY, Integer.TYPE);

	/*public static enum Parameters implements GRProperty {
		drawShadow, shadowDarkness, shadowDepth, shadowBlur
	}*/

	@Getter(value = DRAW_SHADOW_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getDrawShadow();

	@Setter(value = DRAW_SHADOW_KEY)
	public void setDrawShadow(boolean aFlag);

	@Getter(value = SHADOW_DARKNESS_KEY, defaultValue = "150")
	@XMLAttribute
	public int getShadowDarkness();

	@Setter(value = SHADOW_DARKNESS_KEY)
	public void setShadowDarkness(int aValue);

	@Getter(value = SHADOW_DEPTH_KEY, defaultValue = "2")
	@XMLAttribute
	public int getShadowDepth();

	@Setter(value = SHADOW_DEPTH_KEY)
	public void setShadowDepth(int aValue);

	@Getter(value = SHADOW_BLUR_KEY, defaultValue = "4")
	@XMLAttribute
	public int getShadowBlur();

	@Setter(value = SHADOW_BLUR_KEY)
	public void setShadowBlur(int aValue);

	// public ShadowStyle clone();

}
