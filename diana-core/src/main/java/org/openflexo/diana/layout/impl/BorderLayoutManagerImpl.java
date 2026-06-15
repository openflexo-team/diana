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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.openflexo.connie.DataBinding;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.impl.DianaLayoutManagerImpl;
import org.openflexo.diana.layout.BorderLayoutConstraints;
import org.openflexo.diana.layout.BorderLayoutManager;
import org.openflexo.diana.layout.BorderLayoutManagerSpecification;
import org.openflexo.diana.layout.BorderRegion;

/**
 * Default implementation for {@link BorderLayoutManager}.<br>
 *
 * Positions up to five child shapes in the regions of {@link java.awt.BorderLayout}: NORTH/SOUTH span the full inner width keeping their own
 * height, WEST/EAST take the remaining middle-band height keeping their own width, CENTER fills whatever is left. The algorithm reproduces
 * {@code java.awt.BorderLayout.layoutContainer} inside the container bounds reduced by the configured insets, with the configured
 * horizontal/vertical gaps between regions.
 *
 * @author sylvain
 *
 */
public abstract class BorderLayoutManagerImpl<O> extends DianaLayoutManagerImpl<BorderLayoutManagerSpecification, O>
		implements BorderLayoutManager<O> {

	@Override
	public double getHgap() {
		return getLayoutManagerSpecification().getHgap();
	}

	@Override
	public double getVgap() {
		return getLayoutManagerSpecification().getVgap();
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
	 * A resize of the container (or of a region node) re-affects the placement of the other regions.
	 */
	@Override
	public boolean isFullyLayouted() {
		return true;
	}

	/** FIB editing a border-layouted child's properties (its {@code layoutBorderRegion}). */
	private static final org.openflexo.rm.Resource CHILD_INSPECTOR_FIB = org.openflexo.rm.ResourceLocator
			.locateResource("LayoutChildInspectors/BorderLayoutManager.fib");

	@Override
	public org.openflexo.rm.Resource getChildInspectorFIB() {
		return CHILD_INSPECTOR_FIB;
	}

	/** Resolved target geometry per node, computed in {@link #computeLayout()} and applied in {@link #performLayout(ShapeNode)}. */
	private final Map<ShapeNode<?>, DianaRectangle> geometryMap = new HashMap<>();

	private BorderRegion regionOf(ShapeNode<?> node) {
		org.openflexo.diana.layout.LayoutConstraints c = node.getGraphicalRepresentation().getLayoutConstraints();
		if (c instanceof BorderLayoutConstraints && ((BorderLayoutConstraints) c).getRegion() != null) {
			return ((BorderLayoutConstraints) c).getRegion();
		}
		return BorderRegion.CENTER;
	}

	@Override
	public org.openflexo.diana.layout.LayoutConstraints makeDefaultConstraints() {
		return getFactory().newInstance(BorderLayoutConstraints.class);
	}

	/**
	 * A node is "self-constrained" when it carries any of its own geometry constraint bindings (x/y/width/height). Such a node positions
	 * itself and must be left untouched by the border layout (mirrors {@link BoxLayoutManagerImpl}).
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

		// Bucket nodes by region, keeping only the first node declared for each region (AWT BorderLayout
		// holds a single component per region). Self-constrained nodes (pinned by their own x/y/width/height
		// bindings) position themselves and are excluded — a single-manager container auto-assigns its
		// manager to every added child (ContainerNodeImpl.notifyNodeAdded).
		EnumMap<BorderRegion, ShapeNode<?>> byRegion = new EnumMap<>(BorderRegion.class);
		for (ShapeNode<?> node : getLayoutedNodes()) {
			if (isSelfConstrained(node)) {
				continue;
			}
			byRegion.putIfAbsent(regionOf(node), node);
		}
		if (byRegion.isEmpty()) {
			return;
		}

		double hgap = getHgap();
		double vgap = getVgap();

		double x = getInsetLeft();
		double y = getInsetTop();
		double w = Math.max(0, getContainerNode().getWidth() - getInsetLeft() - getInsetRight());
		double h = Math.max(0, getContainerNode().getHeight() - getInsetTop() - getInsetBottom());

		ShapeNode<?> north = byRegion.get(BorderRegion.NORTH);
		ShapeNode<?> south = byRegion.get(BorderRegion.SOUTH);
		ShapeNode<?> west = byRegion.get(BorderRegion.WEST);
		ShapeNode<?> east = byRegion.get(BorderRegion.EAST);
		ShapeNode<?> center = byRegion.get(BorderRegion.CENTER);

		if (north != null) {
			double nh = north.getHeight();
			geometryMap.put(north, new DianaRectangle(x, y, w, nh));
			y += nh + vgap;
			h = Math.max(0, h - nh - vgap);
		}
		if (south != null) {
			double sh = south.getHeight();
			geometryMap.put(south, new DianaRectangle(x, y + h - sh, w, sh));
			h = Math.max(0, h - sh - vgap);
		}
		if (west != null) {
			double ww = west.getWidth();
			geometryMap.put(west, new DianaRectangle(x, y, ww, h));
			x += ww + hgap;
			w = Math.max(0, w - ww - hgap);
		}
		if (east != null) {
			double ew = east.getWidth();
			geometryMap.put(east, new DianaRectangle(x + w - ew, y, ew, h));
			w = Math.max(0, w - ew - hgap);
		}
		if (center != null) {
			geometryMap.put(center, new DianaRectangle(x, y, w, h));
		}
	}

	/** One node per region (first declared wins), self-constrained nodes excluded — same bucketing as {@link #computeLayout()}. */
	private EnumMap<BorderRegion, ShapeNode<?>> byRegion() {
		EnumMap<BorderRegion, ShapeNode<?>> byRegion = new EnumMap<>(BorderRegion.class);
		for (ShapeNode<?> node : getLayoutedNodes()) {
			if (!isSelfConstrained(node)) {
				byRegion.putIfAbsent(regionOf(node), node);
			}
		}
		return byRegion;
	}

	/**
	 * Minimum container width: WEST and EAST keep their own width (NORTH/SOUTH span the full width and CENTER is stretched, so they impose no
	 * width lower bound) + the horizontal gaps between them and the center + the left/right insets.
	 */
	@Override
	public double getMinimumWidth() {
		EnumMap<BorderRegion, ShapeNode<?>> r = byRegion();
		ShapeNode<?> west = r.get(BorderRegion.WEST);
		ShapeNode<?> east = r.get(BorderRegion.EAST);
		double w = (west != null ? childMinWidth(west) : 0) + (east != null ? childMinWidth(east) : 0);
		int gaps = (west != null ? 1 : 0) + (east != null ? 1 : 0);
		return w + getHgap() * gaps + getInsetLeft() + getInsetRight();
	}

	/**
	 * Minimum container height (width-independent): NORTH and SOUTH keep their own height (WEST/EAST/CENTER are stretched vertically and
	 * impose no height lower bound) + the vertical gaps + the top/bottom insets.
	 */
	@Override
	public double getMinimumHeightForWidth(double width) {
		EnumMap<BorderRegion, ShapeNode<?>> r = byRegion();
		ShapeNode<?> north = r.get(BorderRegion.NORTH);
		ShapeNode<?> south = r.get(BorderRegion.SOUTH);
		double h = (north != null ? childMinHeight(north) : 0) + (south != null ? childMinHeight(south) : 0);
		int gaps = (north != null ? 1 : 0) + (south != null ? 1 : 0);
		return h + getVgap() * gaps + getInsetTop() + getInsetBottom();
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
		if (BorderLayoutManagerSpecification.HGAP_KEY.equals(pn) || BorderLayoutManagerSpecification.VGAP_KEY.equals(pn)
				|| BorderLayoutManagerSpecification.INSET_TOP_KEY.equals(pn) || BorderLayoutManagerSpecification.INSET_BOTTOM_KEY.equals(pn)
				|| BorderLayoutManagerSpecification.INSET_LEFT_KEY.equals(pn) || BorderLayoutManagerSpecification.INSET_RIGHT_KEY.equals(pn)) {
			invalidate();
			doLayout(true);
		}
	}
}
