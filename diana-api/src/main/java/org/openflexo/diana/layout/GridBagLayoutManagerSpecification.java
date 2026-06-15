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

import org.openflexo.diana.DianaLayoutManagerSpecification;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

/**
 * Represents the specification of a {@link GridBagLayoutManager} in DIANA.<br>
 *
 * A grid-bag layout arranges child shapes in a flexible 2D grid of cells (modelling {@link java.awt.GridBagLayout}). Each child opts into a
 * cell through its per-child GR properties (column/row {@link org.openflexo.diana.ShapeGraphicalRepresentation#getLayoutGridX()
 * layoutGridX}/{@link org.openflexo.diana.ShapeGraphicalRepresentation#getLayoutGridY() layoutGridY}, span, weights, fill and anchor). This
 * specification carries only the grid-wide configuration: the spacing between cells ({@link #getHgap()} / {@link #getVgap()}) and the outer
 * insets.
 *
 * @author sylvain
 *
 */
@ModelEntity
@XMLElement
@Imports({ @Import(GridBagLayoutManager.class) })
public interface GridBagLayoutManagerSpecification extends DianaLayoutManagerSpecification<GridBagLayoutManager<?>> {

	@PropertyIdentifier(type = double.class)
	public static final String HGAP_KEY = "hgap";
	@PropertyIdentifier(type = double.class)
	public static final String VGAP_KEY = "vgap";
	@PropertyIdentifier(type = double.class)
	public static final String INSET_TOP_KEY = "insetTop";
	@PropertyIdentifier(type = double.class)
	public static final String INSET_BOTTOM_KEY = "insetBottom";
	@PropertyIdentifier(type = double.class)
	public static final String INSET_LEFT_KEY = "insetLeft";
	@PropertyIdentifier(type = double.class)
	public static final String INSET_RIGHT_KEY = "insetRight";

	/**
	 * Horizontal spacing inserted between consecutive columns.
	 */
	@Getter(value = HGAP_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getHgap();

	@Setter(HGAP_KEY)
	public void setHgap(double hgap);

	/**
	 * Vertical spacing inserted between consecutive rows.
	 */
	@Getter(value = VGAP_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getVgap();

	@Setter(VGAP_KEY)
	public void setVgap(double vgap);

	@Getter(value = INSET_TOP_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getInsetTop();

	@Setter(INSET_TOP_KEY)
	public void setInsetTop(double inset);

	@Getter(value = INSET_BOTTOM_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getInsetBottom();

	@Setter(INSET_BOTTOM_KEY)
	public void setInsetBottom(double inset);

	@Getter(value = INSET_LEFT_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getInsetLeft();

	@Setter(INSET_LEFT_KEY)
	public void setInsetLeft(double inset);

	@Getter(value = INSET_RIGHT_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getInsetRight();

	@Setter(INSET_RIGHT_KEY)
	public void setInsetRight(double inset);

}
