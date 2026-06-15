/**
 *
 * Copyright (c) 2024, Openflexo
 *
 * This file is part of Diana-core, a component of the software infrastructure
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

package org.openflexo.diana.layout.impl;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.connie.DataBinding;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.impl.DianaLayoutManagerImpl;
import org.openflexo.diana.layout.BoxLayoutManager;
import org.openflexo.diana.layout.BoxLayoutManagerSpecification;
import org.openflexo.diana.layout.BoxLayoutManagerSpecification.CrossAxisPolicy;
import org.openflexo.diana.layout.BoxLayoutManagerSpecification.MainAxisPolicy;
import org.openflexo.diana.layout.BoxLayoutManagerSpecification.Orientation;

/**
 * Default implementation for {@link BoxLayoutManager}.<br>
 *
 * Stacks the layouted nodes along the configured {@link Orientation} (the <i>main</i> axis), inside the container bounds reduced by the
 * insets. The main-axis extent of each node is either its own current size (weight <code>0</code>) or a share of the free space proportional
 * to its weight (weight &gt; 0). On the cross axis, nodes are either stretched to the inner extent or aligned while keeping their own size.
 *
 * @author sylvain
 *
 */
public abstract class BoxLayoutManagerImpl<O> extends DianaLayoutManagerImpl<BoxLayoutManagerSpecification, O>
		implements BoxLayoutManager<O> {

	@Override
	public Orientation getOrientation() {
		return getLayoutManagerSpecification().getOrientation();
	}

	@Override
	public double getGap() {
		return getLayoutManagerSpecification().getGap();
	}

	@Override
	public double getInsetTop() {
		return getLayoutManagerSpecification().getInsetTop();
	}

	@Override
	public double getInsetBottom() {
		return getLayoutManagerSpecification().getInsetBottom();
	}

	@Override
	public double getInsetLeft() {
		return getLayoutManagerSpecification().getInsetLeft();
	}

	@Override
	public double getInsetRight() {
		return getLayoutManagerSpecification().getInsetRight();
	}

	@Override
	public CrossAxisPolicy getCrossAxisPolicy() {
		return getLayoutManagerSpecification().getCrossAxisPolicy();
	}

	@Override
	public MainAxisPolicy getMainAxisPolicy() {
		return getLayoutManagerSpecification().getMainAxisPolicy();
	}

	/**
	 * A move or resize of one node (or of the container) re-affects the whole stack.
	 */
	@Override
	public boolean isFullyLayouted() {
		return true;
	}

	/** FIB editing a box-layouted child's properties (its {@code layoutWeight}). */
	private static final org.openflexo.rm.Resource CHILD_INSPECTOR_FIB =
			org.openflexo.rm.ResourceLocator.locateResource("LayoutChildInspectors/BoxLayoutManager.fib");

	@Override
	public org.openflexo.rm.Resource getChildInspectorFIB() {
		return CHILD_INSPECTOR_FIB;
	}

	/** Resolved target geometry per node, computed in {@link #computeLayout()} and applied in {@link #performLayout(ShapeNode)}. */
	private final Map<ShapeNode<?>, DianaRectangle> geometryMap = new HashMap<>();

	/**
	 * The main-axis base extent of a node, following the circularity-safe rule: a fixed node (weight 0) keeps its own current main-axis size
	 * (idempotent — the manager re-writes the same value), a weighted node has base 0 (it never reads the dimension it writes).
	 */
	private double baseExtent(ShapeNode<?> node) {
		if (weight(node) == 0) {
			return getOrientation() == Orientation.VERTICAL ? node.getHeight() : node.getWidth();
		}
		return 0;
	}

	private double weight(ShapeNode<?> node) {
		return Math.max(0, node.getGraphicalRepresentation().getLayoutWeight());
	}

	/**
	 * A node is "self-constrained" when it carries any of its own geometry constraint bindings (x/y/width/height). Such a node positions
	 * itself (e.g. a header pinned to the top via {@code parent.width}) and must be left untouched by the box stacking.
	 */
	private boolean isSelfConstrained(ShapeNode<?> node) {
		ShapeGraphicalRepresentation gr = node.getGraphicalRepresentation();
		return isSet(gr.getXConstraints()) || isSet(gr.getYConstraints()) || isSet(gr.getWidthConstraints())
				|| isSet(gr.getHeightConstraints());
	}

	private static boolean isSet(DataBinding<?> binding) {
		return binding != null && binding.isSet();
	}

	@Override
	public void computeLayout() {

		super.computeLayout();

		geometryMap.clear();

		// Exclude self-constrained nodes (those pinned by their own x/y/width/height constraint
		// bindings, e.g. a header band). A single-layout-manager container auto-assigns its manager
		// to every added child (ContainerNodeImpl.notifyNodeAdded), so such a pinned child may end up
		// "layouted" here even though it positions itself; it must not be stacked by the box.
		List<ShapeNode<?>> nodes = new ArrayList<>();
		for (ShapeNode<?> node : getLayoutedNodes()) {
			if (!isSelfConstrained(node)) {
				nodes.add(node);
			}
		}
		int n = nodes.size();
		if (n == 0) {
			return;
		}

		double gap = getGap();
		double containerW = getContainerNode().getWidth();
		double containerH = getContainerNode().getHeight();

		boolean vertical = getOrientation() == Orientation.VERTICAL;

		// Inner band (container minus insets) along main and cross axes
		double innerMain = (vertical ? containerH - getInsetTop() - getInsetBottom() : containerW - getInsetLeft() - getInsetRight());
		double innerCross = (vertical ? containerW - getInsetLeft() - getInsetRight() : containerH - getInsetTop() - getInsetBottom());
		double mainStart = (vertical ? getInsetTop() : getInsetLeft());
		double crossStart = (vertical ? getInsetLeft() : getInsetTop());

		double available = innerMain - gap * Math.max(0, n - 1);

		double sumBaseFixed = 0;
		double sumWeight = 0;
		for (ShapeNode<?> node : nodes) {
			double w = weight(node);
			if (w == 0) {
				sumBaseFixed += baseExtent(node);
			}
			else {
				sumWeight += w;
			}
		}
		double free = Math.max(0, available - sumBaseFixed);

		// When no weights are involved, the leftover free space is placed according to mainAxisPolicy
		double mainOffset = mainStart;
		double extraGap = 0;
		if (sumWeight == 0) {
			double leftover = free; // == available - sumBaseFixed
			switch (getMainAxisPolicy()) {
				case PACK_CENTER:
					mainOffset = mainStart + leftover / 2;
					break;
				case PACK_END:
					mainOffset = mainStart + leftover;
					break;
				case SPACE_BETWEEN:
					if (n > 1) {
						extraGap = leftover / (n - 1);
					}
					else {
						mainOffset = mainStart + leftover / 2;
					}
					break;
				case PACK_START:
				case DISTRIBUTE_WEIGHTS:
				default:
					break;
			}
		}

		double mainPos = mainOffset;
		for (ShapeNode<?> node : nodes) {
			double w = weight(node);
			double extent;
			if (sumWeight > 0) {
				extent = (w == 0) ? baseExtent(node) : free * w / sumWeight;
			}
			else {
				extent = baseExtent(node);
			}

			// Cross axis: stretch to inner cross extent, or keep own size and align
			double crossSize;
			double crossPos;
			if (getCrossAxisPolicy() == CrossAxisPolicy.STRETCH) {
				crossSize = innerCross;
				crossPos = crossStart;
			}
			else {
				crossSize = vertical ? node.getWidth() : node.getHeight();
				crossPos = crossStart + crossAlignOffset(innerCross, crossSize);
			}

			DianaRectangle rect;
			if (vertical) {
				rect = new DianaRectangle(crossPos, mainPos, crossSize, extent);
			}
			else {
				rect = new DianaRectangle(mainPos, crossPos, extent, crossSize);
			}
			geometryMap.put(node, rect);

			mainPos += extent + gap + extraGap;
		}
	}

	private double crossAlignOffset(double innerCross, double crossSize) {
		switch (getCrossAxisPolicy()) {
			case ALIGN_CENTER:
				return (innerCross - crossSize) / 2;
			case ALIGN_TRAILING:
				return innerCross - crossSize;
			case ALIGN_LEADING:
			case STRETCH:
			default:
				return 0;
		}
	}

	@Override
	protected void performLayout(ShapeNode<?> node) {
		DianaRectangle rect = geometryMap.get(node);
		if (rect != null) {
			node.setLocation(new DianaPoint(rect.getX(), rect.getY()));
			node.setSize(new DianaDimension(rect.getWidth(), rect.getHeight()));
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String pn = evt.getPropertyName();
		if (BoxLayoutManagerSpecification.ORIENTATION_KEY.equals(pn) || BoxLayoutManagerSpecification.GAP_KEY.equals(pn)
				|| BoxLayoutManagerSpecification.INSET_TOP_KEY.equals(pn) || BoxLayoutManagerSpecification.INSET_BOTTOM_KEY.equals(pn)
				|| BoxLayoutManagerSpecification.INSET_LEFT_KEY.equals(pn) || BoxLayoutManagerSpecification.INSET_RIGHT_KEY.equals(pn)
				|| BoxLayoutManagerSpecification.CROSS_AXIS_POLICY_KEY.equals(pn)
				|| BoxLayoutManagerSpecification.MAIN_AXIS_POLICY_KEY.equals(pn)) {
			invalidate();
			doLayout(true);
		}
	}
}
