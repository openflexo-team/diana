/**
 *
 * Copyright (c) 2024, Openflexo
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

package org.openflexo.diana.layout;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

/**
 * {@link LayoutConstraints} for a child laid out by a {@link GridBagLayoutManager}: the cell coordinates and span, the per-axis growth
 * weights, the fill and the anchor (the analogue of {@link java.awt.GridBagConstraints}).
 *
 * @author sylvain
 *
 */
@ModelEntity
@XMLElement(xmlTag = "GridBagLayoutConstraints")
public interface GridBagLayoutConstraints extends LayoutConstraints {

	@PropertyIdentifier(type = int.class)
	public static final String GRID_X_KEY = "gridX";
	@PropertyIdentifier(type = int.class)
	public static final String GRID_Y_KEY = "gridY";
	@PropertyIdentifier(type = int.class)
	public static final String GRID_WIDTH_KEY = "gridWidth";
	@PropertyIdentifier(type = int.class)
	public static final String GRID_HEIGHT_KEY = "gridHeight";
	@PropertyIdentifier(type = double.class)
	public static final String WEIGHT_X_KEY = "weightX";
	@PropertyIdentifier(type = double.class)
	public static final String WEIGHT_Y_KEY = "weightY";
	@PropertyIdentifier(type = GridBagFill.class)
	public static final String FILL_KEY = "fill";
	@PropertyIdentifier(type = GridBagAnchor.class)
	public static final String ANCHOR_KEY = "anchor";

	@Getter(value = GRID_X_KEY, defaultValue = "0")
	@XMLAttribute
	public int getGridX();

	@Setter(GRID_X_KEY)
	public void setGridX(int gridX);

	@Getter(value = GRID_Y_KEY, defaultValue = "0")
	@XMLAttribute
	public int getGridY();

	@Setter(GRID_Y_KEY)
	public void setGridY(int gridY);

	@Getter(value = GRID_WIDTH_KEY, defaultValue = "1")
	@XMLAttribute
	public int getGridWidth();

	@Setter(GRID_WIDTH_KEY)
	public void setGridWidth(int gridWidth);

	@Getter(value = GRID_HEIGHT_KEY, defaultValue = "1")
	@XMLAttribute
	public int getGridHeight();

	@Setter(GRID_HEIGHT_KEY)
	public void setGridHeight(int gridHeight);

	@Getter(value = WEIGHT_X_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getWeightX();

	@Setter(WEIGHT_X_KEY)
	public void setWeightX(double weightX);

	@Getter(value = WEIGHT_Y_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getWeightY();

	@Setter(WEIGHT_Y_KEY)
	public void setWeightY(double weightY);

	@Getter(value = FILL_KEY, defaultValue = "NONE")
	@XMLAttribute
	public GridBagFill getFill();

	@Setter(FILL_KEY)
	public void setFill(GridBagFill fill);

	@Getter(value = ANCHOR_KEY, defaultValue = "CENTER")
	@XMLAttribute
	public GridBagAnchor getAnchor();

	@Setter(ANCHOR_KEY)
	public void setAnchor(GridBagAnchor anchor);

}
