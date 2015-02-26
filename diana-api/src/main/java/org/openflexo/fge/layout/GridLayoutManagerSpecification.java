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
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.GraphicalRepresentation.VerticalTextAlignment;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents the specification of a GridLayoutManager in DIANA<br>
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement
@Imports({ @Import(GridLayoutManager.class) })
public interface GridLayoutManagerSpecification extends FGELayoutManagerSpecification<GridLayoutManager> {

	@PropertyIdentifier(type = double.class)
	public static final String GRID_X_KEY = "gridX";
	@PropertyIdentifier(type = double.class)
	public static final String GRID_Y_KEY = "gridY";
	@PropertyIdentifier(type = HorizontalTextAlignment.class)
	public static final String HORIZONTAL_ALIGNEMENT_KEY = "horizontalAlignment";
	@PropertyIdentifier(type = VerticalTextAlignment.class)
	public static final String VERTICAL_ALIGNEMENT_KEY = "verticalAlignment";

	@Getter(value = GRID_X_KEY, defaultValue = "20.0")
	@XMLAttribute
	public double getGridX();

	@Setter(GRID_X_KEY)
	public void setGridX(double gridX);

	@Getter(value = GRID_Y_KEY, defaultValue = "20.0")
	@XMLAttribute
	public double getGridY();

	@Setter(GRID_Y_KEY)
	public void setGridY(double gridY);

	@Getter(value = HORIZONTAL_ALIGNEMENT_KEY, defaultValue = "LEFT")
	@XMLAttribute
	public HorizontalTextAlignment getHorizontalAlignment();

	@Setter(value = HORIZONTAL_ALIGNEMENT_KEY)
	public void setHorizontalAlignment(HorizontalTextAlignment horizontalAlignment);

	@Getter(value = VERTICAL_ALIGNEMENT_KEY, defaultValue = "TOP")
	@XMLAttribute
	public VerticalTextAlignment getVerticalAlignment();

	@Setter(value = VERTICAL_ALIGNEMENT_KEY)
	public void setVerticalAlignment(VerticalTextAlignment verticalAlignment);

}
