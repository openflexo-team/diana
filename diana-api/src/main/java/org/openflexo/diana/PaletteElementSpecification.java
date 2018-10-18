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

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents the specification of a palette element which can be serialized<br>
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement
public interface PaletteElementSpecification extends DianaObject {

	@PropertyIdentifier(type = Integer.class)
	public static final String INDEX_KEY = "index";
	@PropertyIdentifier(type = String.class)
	public static final String NAME_KEY = "name";
	@PropertyIdentifier(type = String.class)
	public static final String DESCRIPTION_KEY = "description";
	@PropertyIdentifier(type = ShapeGraphicalRepresentation.class)
	public static final String GRAPHICAL_REPRESENTATION_KEY = "graphicalRepresentation";

	@PropertyIdentifier(type = Boolean.class)
	public static final String APPLY_CURRENT_FOREGROUND_KEY = "applyCurrentForeground";
	@PropertyIdentifier(type = Boolean.class)
	public static final String APPLY_CURRENT_BACKGROUND_KEY = "applyCurrentBackground";
	@PropertyIdentifier(type = Boolean.class)
	public static final String APPLY_CURRENT_TEXT_STYLE_KEY = "applyCurrentTextStyle";
	@PropertyIdentifier(type = Boolean.class)
	public static final String APPLY_CURRENT_SHADOW_STYLE_KEY = "applyCurrentShadowStyle";
	@PropertyIdentifier(type = Boolean.class)
	public static final String ASK_FOR_IMAGE_KEY = "askForImage";

	@Getter(value = INDEX_KEY)
	@XMLAttribute
	public Integer getIndex();

	@Setter(INDEX_KEY)
	public void setIndex(Integer index);

	@Getter(value = NAME_KEY)
	@XMLAttribute
	public String getName();

	@Setter(NAME_KEY)
	public void setName(String name);

	@Getter(value = DESCRIPTION_KEY)
	@XMLAttribute
	public String getDescription();

	@Setter(DESCRIPTION_KEY)
	public void setDescription(String name);

	@Getter(value = GRAPHICAL_REPRESENTATION_KEY)
	@XMLElement
	public ShapeGraphicalRepresentation getGraphicalRepresentation();

	@Setter(value = GRAPHICAL_REPRESENTATION_KEY)
	public void setGraphicalRepresentation(ShapeGraphicalRepresentation aGraphicalRepresentation);

	@Getter(value = APPLY_CURRENT_FOREGROUND_KEY, defaultValue = "true")
	@XMLAttribute
	public Boolean getApplyCurrentForeground();

	@Setter(APPLY_CURRENT_FOREGROUND_KEY)
	public void setApplyCurrentForeground(Boolean applyCurrentForeground);

	@Getter(value = APPLY_CURRENT_BACKGROUND_KEY, defaultValue = "true")
	@XMLAttribute
	public Boolean getApplyCurrentBackground();

	@Setter(APPLY_CURRENT_BACKGROUND_KEY)
	public void setApplyCurrentBackground(Boolean applyCurrentBackground);

	@Getter(value = APPLY_CURRENT_TEXT_STYLE_KEY, defaultValue = "true")
	@XMLAttribute
	public Boolean getApplyCurrentTextStyle();

	@Setter(APPLY_CURRENT_TEXT_STYLE_KEY)
	public void setApplyCurrentTextStyle(Boolean applyCurrentTextStyle);

	@Getter(value = APPLY_CURRENT_SHADOW_STYLE_KEY, defaultValue = "true")
	@XMLAttribute
	public Boolean getApplyCurrentShadowStyle();

	@Setter(APPLY_CURRENT_SHADOW_STYLE_KEY)
	public void setApplyCurrentShadowStyle(Boolean applyCurrentShadowStyle);

	@Getter(value = ASK_FOR_IMAGE_KEY, defaultValue = "false")
	@XMLAttribute
	public Boolean getAskForImage();

	@Setter(ASK_FOR_IMAGE_KEY)
	public void setAskForImage(Boolean askForImage);

}
