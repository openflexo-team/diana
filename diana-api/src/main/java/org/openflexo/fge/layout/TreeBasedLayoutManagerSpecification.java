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

import org.openflexo.fge.FGELayoutManager;
import org.openflexo.fge.FGELayoutManagerSpecification;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.GraphicalRepresentation.VerticalTextAlignment;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;

/**
 * Abstract specification of a {@link FGELayoutManager} generally handling trees<br>
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@Imports({ @Import(TreeLayoutManagerSpecification.class), @Import(BalloonLayoutManagerSpecification.class),
		@Import(RadialTreeLayoutManagerSpecification.class) })
public interface TreeBasedLayoutManagerSpecification<LM extends TreeBasedLayoutManager<?, ?>> extends FGELayoutManagerSpecification<LM> {

	@PropertyIdentifier(type = HorizontalTextAlignment.class)
	public static final String HORIZONTAL_ALIGNEMENT_KEY = "horizontalAlignment";
	@PropertyIdentifier(type = VerticalTextAlignment.class)
	public static final String VERTICAL_ALIGNEMENT_KEY = "verticalAlignment";

	@Getter(value = HORIZONTAL_ALIGNEMENT_KEY, defaultValue = "CENTER")
	@XMLAttribute
	public HorizontalTextAlignment getHorizontalAlignment();

	@Setter(value = HORIZONTAL_ALIGNEMENT_KEY)
	public void setHorizontalAlignment(HorizontalTextAlignment horizontalAlignment);

	@Getter(value = VERTICAL_ALIGNEMENT_KEY, defaultValue = "MIDDLE")
	@XMLAttribute
	public VerticalTextAlignment getVerticalAlignment();

	@Setter(value = VERTICAL_ALIGNEMENT_KEY)
	public void setVerticalAlignment(VerticalTextAlignment verticalAlignment);

}
