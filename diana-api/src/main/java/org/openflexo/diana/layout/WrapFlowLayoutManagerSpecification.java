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
 * Represents the specification of a {@link WrapFlowLayoutManager} in DIANA.<br>
 *
 * A wrap-flow layout places child shapes along a primary axis ({@link #getOrientation()}) and wraps to a new line when the current line
 * would overflow the container's inner extent — modelling {@link java.awt.FlowLayout} / CSS <code>flex-wrap</code>. Children keep their own
 * size (they are positioned, never resized); there is therefore no per-child constraint data.
 *
 * @author sylvain
 *
 */
@ModelEntity
@XMLElement
@Imports({ @Import(WrapFlowLayoutManager.class) })
public interface WrapFlowLayoutManagerSpecification extends DianaLayoutManagerSpecification<WrapFlowLayoutManager<?>> {

	@PropertyIdentifier(type = Orientation.class)
	public static final String ORIENTATION_KEY = "orientation";
	@PropertyIdentifier(type = double.class)
	public static final String HGAP_KEY = "hgap";
	@PropertyIdentifier(type = double.class)
	public static final String VGAP_KEY = "vgap";
	@PropertyIdentifier(type = LineAlignment.class)
	public static final String LINE_ALIGNMENT_KEY = "lineAlignment";
	@PropertyIdentifier(type = double.class)
	public static final String INSET_TOP_KEY = "insetTop";
	@PropertyIdentifier(type = double.class)
	public static final String INSET_BOTTOM_KEY = "insetBottom";
	@PropertyIdentifier(type = double.class)
	public static final String INSET_LEFT_KEY = "insetLeft";
	@PropertyIdentifier(type = double.class)
	public static final String INSET_RIGHT_KEY = "insetRight";

	/**
	 * Primary axis: <code>HORIZONTAL</code> lays items in rows that wrap on the container width; <code>VERTICAL</code> lays items in columns
	 * that wrap on the container height.
	 */
	public static enum Orientation {
		HORIZONTAL, VERTICAL
	}

	/**
	 * How items in a line are positioned when the line has slack (the analogue of {@link java.awt.FlowLayout}'s LEFT/CENTER/RIGHT).
	 */
	public static enum LineAlignment {
		LEADING, CENTER, TRAILING
	}

	@Getter(value = ORIENTATION_KEY, defaultValue = "HORIZONTAL")
	@XMLAttribute
	public Orientation getOrientation();

	@Setter(ORIENTATION_KEY)
	public void setOrientation(Orientation orientation);

	@Getter(value = HGAP_KEY, defaultValue = "5.0")
	@XMLAttribute
	public double getHgap();

	@Setter(HGAP_KEY)
	public void setHgap(double hgap);

	@Getter(value = VGAP_KEY, defaultValue = "5.0")
	@XMLAttribute
	public double getVgap();

	@Setter(VGAP_KEY)
	public void setVgap(double vgap);

	@Getter(value = LINE_ALIGNMENT_KEY, defaultValue = "LEADING")
	@XMLAttribute
	public LineAlignment getLineAlignment();

	@Setter(LINE_ALIGNMENT_KEY)
	public void setLineAlignment(LineAlignment alignment);

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
