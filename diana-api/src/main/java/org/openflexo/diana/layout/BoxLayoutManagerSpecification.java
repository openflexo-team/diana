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
 * Represents the specification of a {@link BoxLayoutManager} in DIANA.<br>
 *
 * A box layout stacks the layouted nodes along a single axis (the <i>main</i> axis, see {@link #getOrientation()}), inside the bounds of the
 * container shape reduced by the configured insets. It generalizes {@link FlowLayoutManagerSpecification} (which only packs centered nodes
 * from the origin) by adding:
 * <ul>
 * <li>cross-axis sizing/alignment ({@link #getCrossAxisPolicy()}): each item can be stretched to the inner cross extent or aligned
 * (leading/center/trailing) while keeping its own cross size;</li>
 * <li>main-axis free-space distribution ({@link #getMainAxisPolicy()}) and per-node weights (see
 * {@link org.openflexo.diana.ShapeGraphicalRepresentation#getLayoutWeight()}): a node with weight <code>0</code> keeps a fixed main-axis
 * extent, a node with a strictly positive weight grows to take a proportional share of the free space;</li>
 * <li>insets (top/bottom/left/right) to reserve space (e.g. a header band).</li>
 * </ul>
 *
 * @author sylvain
 *
 */
@ModelEntity
@XMLElement
@Imports({ @Import(BoxLayoutManager.class) })
public interface BoxLayoutManagerSpecification extends DianaLayoutManagerSpecification<BoxLayoutManager<?>> {

	@PropertyIdentifier(type = Orientation.class)
	public static final String ORIENTATION_KEY = "orientation";
	@PropertyIdentifier(type = double.class)
	public static final String GAP_KEY = "gap";
	@PropertyIdentifier(type = double.class)
	public static final String INSET_TOP_KEY = "insetTop";
	@PropertyIdentifier(type = double.class)
	public static final String INSET_BOTTOM_KEY = "insetBottom";
	@PropertyIdentifier(type = double.class)
	public static final String INSET_LEFT_KEY = "insetLeft";
	@PropertyIdentifier(type = double.class)
	public static final String INSET_RIGHT_KEY = "insetRight";
	@PropertyIdentifier(type = CrossAxisPolicy.class)
	public static final String CROSS_AXIS_POLICY_KEY = "crossAxisPolicy";
	@PropertyIdentifier(type = MainAxisPolicy.class)
	public static final String MAIN_AXIS_POLICY_KEY = "mainAxisPolicy";

	/**
	 * Main axis along which the layouted nodes are stacked.
	 */
	public static enum Orientation {
		VERTICAL, HORIZONTAL
	}

	/**
	 * How an item occupies the cross axis (the axis orthogonal to {@link Orientation}).<br>
	 * <code>STRETCH</code> resizes the item to the inner cross extent; the <code>ALIGN_*</code> values keep the item's own cross size and only
	 * position it within the inner band.
	 */
	public static enum CrossAxisPolicy {
		STRETCH, ALIGN_LEADING, ALIGN_CENTER, ALIGN_TRAILING
	}

	/**
	 * How free space along the main axis is consumed.<br>
	 * This is consulted <b>only</b> when the total weight of the layouted nodes is <code>0</code> (i.e. all items are fixed-size). As soon as
	 * any node has a strictly positive weight, the free space is distributed by weight and there is no leftover, so this policy is ignored.
	 */
	public static enum MainAxisPolicy {
		PACK_START, PACK_CENTER, PACK_END, DISTRIBUTE_WEIGHTS, SPACE_BETWEEN
	}

	@Getter(value = ORIENTATION_KEY, defaultValue = "VERTICAL")
	@XMLAttribute
	public Orientation getOrientation();

	@Setter(ORIENTATION_KEY)
	public void setOrientation(Orientation orientation);

	@Getter(value = GAP_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getGap();

	@Setter(GAP_KEY)
	public void setGap(double gap);

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

	@Getter(value = CROSS_AXIS_POLICY_KEY, defaultValue = "STRETCH")
	@XMLAttribute
	public CrossAxisPolicy getCrossAxisPolicy();

	@Setter(CROSS_AXIS_POLICY_KEY)
	public void setCrossAxisPolicy(CrossAxisPolicy policy);

	@Getter(value = MAIN_AXIS_POLICY_KEY, defaultValue = "PACK_START")
	@XMLAttribute
	public MainAxisPolicy getMainAxisPolicy();

	@Setter(MAIN_AXIS_POLICY_KEY)
	public void setMainAxisPolicy(MainAxisPolicy policy);

}
