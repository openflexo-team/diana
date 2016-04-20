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

package org.openflexo.fge.layout;

import org.openflexo.fge.FGELayoutManagerSpecification;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents the specification of a OutlineLayoutManager in DIANA<br>
 * (places layouted nodes around outline of container shape)
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement
@Imports({ @Import(OutlineLayoutManager.class) })
public interface OutlineLayoutManagerSpecification extends FGELayoutManagerSpecification<OutlineLayoutManager> {

	@PropertyIdentifier(type = OutlineLocationType.class)
	public static final String OUTLINE_LOCATION_TYPE_KEY = "outlineLocationType";

	@Getter(value = OUTLINE_LOCATION_TYPE_KEY, defaultValue = "ON")
	@XMLAttribute
	public OutlineLocationType getOutlineLocationType();

	@Setter(value = OUTLINE_LOCATION_TYPE_KEY)
	public void setOutlineLocationType(OutlineLocationType outlineLocationType);

	/**
	 * Represents type of location constraints relative to outline (should the layouted node be inside the shape, outside the shape, or just
	 * on outline)
	 * 
	 * @author sylvain
	 *
	 */
	public static enum OutlineLocationType {
		INNER, ON, OUTER
	}

}