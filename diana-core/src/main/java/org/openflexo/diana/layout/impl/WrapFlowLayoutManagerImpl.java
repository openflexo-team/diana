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
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.impl.DianaLayoutManagerImpl;
import org.openflexo.diana.layout.WrapFlowLayoutManager;
import org.openflexo.diana.layout.WrapFlowLayoutManagerSpecification;
import org.openflexo.diana.layout.WrapFlowLayoutManagerSpecification.LineAlignment;
import org.openflexo.diana.layout.WrapFlowLayoutManagerSpecification.Orientation;

/**
 * Default implementation for {@link WrapFlowLayoutManager}.<br>
 *
 * Places child shapes along the primary axis and wraps to a new line when the current line would overflow the container's inner extent
 * ({@link java.awt.FlowLayout} / CSS <code>flex-wrap</code>). Children keep their own size; only their location is set.
 *
 * @author sylvain
 *
 */
public abstract class WrapFlowLayoutManagerImpl<O> extends DianaLayoutManagerImpl<WrapFlowLayoutManagerSpecification, O>
		implements WrapFlowLayoutManager<O> {

	@Override
	public Orientation getOrientation() {
		return getLayoutManagerSpecification().getOrientation();
	}

	@Override
	public double getHgap() {
		return getLayoutManagerSpecification().getHgap();
	}

	@Override
	public double getVgap() {
		return getLayoutManagerSpecification().getVgap();
	}

	@Override
	public LineAlignment getLineAlignment() {
		return getLayoutManagerSpecification().getLineAlignment();
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

	/**
	 * Adding/removing/resizing a node, or resizing the container, re-affects the wrapping of the whole flow.
	 */
	@Override
	public boolean isFullyLayouted() {
		return true;
	}

	/** Resolved target location per node, computed in {@link #computeLayout()} and applied in {@link #performLayout(ShapeNode)}. */
	private final Map<ShapeNode<?>, DianaPoint> locationMap = new HashMap<>();

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

		locationMap.clear();

		List<ShapeNode<?>> nodes = new ArrayList<>();
		for (ShapeNode<?> node : getLayoutedNodes()) {
			if (!isSelfConstrained(node)) {
				nodes.add(node);
			}
		}
		if (nodes.isEmpty()) {
			return;
		}

		boolean horizontal = getOrientation() == Orientation.HORIZONTAL;
		double hgap = getHgap();
		double vgap = getVgap();
		// Primary-axis gap = the gap between consecutive items on a line; cross-axis gap = the gap between lines.
		double itemGap = horizontal ? hgap : vgap;
		double lineGap = horizontal ? vgap : hgap;

		double innerMain = horizontal ? getContainerNode().getWidth() - getInsetLeft() - getInsetRight()
				: getContainerNode().getHeight() - getInsetTop() - getInsetBottom();
		double mainStart = horizontal ? getInsetLeft() : getInsetTop();
		double crossPos = horizontal ? getInsetTop() : getInsetLeft();

		List<ShapeNode<?>> line = new ArrayList<>();
		double lineMain = 0; // used main-axis extent of the current line (items + gaps)
		double lineCross = 0; // max cross-axis extent of the current line

		for (ShapeNode<?> node : nodes) {
			double main = horizontal ? node.getWidth() : node.getHeight();
			double cross = horizontal ? node.getHeight() : node.getWidth();
			double prospective = line.isEmpty() ? main : lineMain + itemGap + main;
			if (!line.isEmpty() && prospective > innerMain) {
				// wrap: place the current line, then start a new one
				placeLine(line, mainStart, innerMain, crossPos, horizontal, itemGap);
				crossPos += lineCross + lineGap;
				line.clear();
				lineMain = 0;
				lineCross = 0;
			}
			line.add(node);
			lineMain = line.size() == 1 ? main : lineMain + itemGap + main;
			lineCross = Math.max(lineCross, cross);
		}
		placeLine(line, mainStart, innerMain, crossPos, horizontal, itemGap);
	}

	/** Positions one line's nodes at their own size, {@code itemGap} apart, applying {@link LineAlignment} over the line's slack. */
	private void placeLine(List<ShapeNode<?>> line, double mainStart, double innerMain, double crossPos, boolean horizontal,
			double itemGap) {
		if (line.isEmpty()) {
			return;
		}
		double used = 0;
		for (int i = 0; i < line.size(); i++) {
			used += horizontal ? line.get(i).getWidth() : line.get(i).getHeight();
		}
		used += itemGap * (line.size() - 1);
		double slack = Math.max(0, innerMain - used);
		double offset;
		switch (getLineAlignment()) {
			case CENTER:
				offset = slack / 2;
				break;
			case TRAILING:
				offset = slack;
				break;
			case LEADING:
			default:
				offset = 0;
				break;
		}
		double main = mainStart + offset;
		for (ShapeNode<?> node : line) {
			if (horizontal) {
				locationMap.put(node, new DianaPoint(main, crossPos));
				main += node.getWidth() + itemGap;
			}
			else {
				locationMap.put(node, new DianaPoint(crossPos, main));
				main += node.getHeight() + itemGap;
			}
		}
	}

	@Override
	protected void performLayout(ShapeNode<?> node) {
		DianaPoint p = locationMap.get(node);
		if (p != null) {
			node.setLocation(p);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String pn = evt.getPropertyName();
		if (WrapFlowLayoutManagerSpecification.ORIENTATION_KEY.equals(pn) || WrapFlowLayoutManagerSpecification.HGAP_KEY.equals(pn)
				|| WrapFlowLayoutManagerSpecification.VGAP_KEY.equals(pn)
				|| WrapFlowLayoutManagerSpecification.LINE_ALIGNMENT_KEY.equals(pn)
				|| WrapFlowLayoutManagerSpecification.INSET_TOP_KEY.equals(pn)
				|| WrapFlowLayoutManagerSpecification.INSET_BOTTOM_KEY.equals(pn)
				|| WrapFlowLayoutManagerSpecification.INSET_LEFT_KEY.equals(pn)
				|| WrapFlowLayoutManagerSpecification.INSET_RIGHT_KEY.equals(pn)) {
			invalidate();
			doLayout(true);
		}
	}
}
