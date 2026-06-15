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
import org.openflexo.diana.layout.GridBagAnchor;
import org.openflexo.diana.layout.GridBagFill;
import org.openflexo.diana.layout.GridBagLayoutConstraints;
import org.openflexo.diana.layout.GridBagLayoutManager;
import org.openflexo.diana.layout.GridBagLayoutManagerSpecification;

/**
 * Default implementation for {@link GridBagLayoutManager}.<br>
 *
 * Arranges child shapes in a flexible 2D grid, modelling {@link java.awt.GridBagLayout}: column/row minimum sizes are derived from the
 * children, the container's free space is distributed across columns and rows proportionally to the per-child weights, then each child is
 * placed within its cell block according to its fill and anchor.
 *
 * @author sylvain
 *
 */
public abstract class GridBagLayoutManagerImpl<O> extends DianaLayoutManagerImpl<GridBagLayoutManagerSpecification, O>
		implements GridBagLayoutManager<O> {

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
	 * A resize of the container (or a change of any cell) re-affects the whole grid.
	 */
	@Override
	public boolean isFullyLayouted() {
		return true;
	}

	/** FIB editing a grid-bag-layouted child's properties (its grid coordinates, span, weights, fill and anchor). */
	private static final org.openflexo.rm.Resource CHILD_INSPECTOR_FIB = org.openflexo.rm.ResourceLocator
			.locateResource("LayoutChildInspectors/GridBagLayoutManager.fib");

	@Override
	public org.openflexo.rm.Resource getChildInspectorFIB() {
		return CHILD_INSPECTOR_FIB;
	}

	/** Resolved target geometry per node, computed in {@link #computeLayout()} and applied in {@link #performLayout(ShapeNode)}. */
	private final Map<ShapeNode<?>, DianaRectangle> geometryMap = new HashMap<>();

	/** The child's GridBag constraints, or {@code null} if it carries none (defaults are then used). */
	private static GridBagLayoutConstraints gbc(ShapeNode<?> n) {
		org.openflexo.diana.layout.LayoutConstraints c = n.getGraphicalRepresentation().getLayoutConstraints();
		return (c instanceof GridBagLayoutConstraints) ? (GridBagLayoutConstraints) c : null;
	}

	private static int gridX(ShapeNode<?> n) {
		GridBagLayoutConstraints c = gbc(n);
		return c != null ? Math.max(0, c.getGridX()) : 0;
	}

	private static int gridY(ShapeNode<?> n) {
		GridBagLayoutConstraints c = gbc(n);
		return c != null ? Math.max(0, c.getGridY()) : 0;
	}

	private static int gridWidth(ShapeNode<?> n) {
		GridBagLayoutConstraints c = gbc(n);
		return c != null ? Math.max(1, c.getGridWidth()) : 1;
	}

	private static int gridHeight(ShapeNode<?> n) {
		GridBagLayoutConstraints c = gbc(n);
		return c != null ? Math.max(1, c.getGridHeight()) : 1;
	}

	private static double weightX(ShapeNode<?> n) {
		GridBagLayoutConstraints c = gbc(n);
		return c != null ? Math.max(0, c.getWeightX()) : 0;
	}

	private static double weightY(ShapeNode<?> n) {
		GridBagLayoutConstraints c = gbc(n);
		return c != null ? Math.max(0, c.getWeightY()) : 0;
	}

	@Override
	public org.openflexo.diana.layout.LayoutConstraints makeDefaultConstraints() {
		return getFactory().newInstance(GridBagLayoutConstraints.class);
	}

	/**
	 * Circularity-safe base width a child contributes to its column's minimum: a non-weighted child (weightX 0) keeps its own current width
	 * (idempotent — the manager re-writes the same value), a weighted child contributes 0 (it grows from the free space and must never read
	 * back the width the manager just wrote to it, otherwise the column would ratchet up on every resize and never shrink). Mirrors
	 * {@link BoxLayoutManagerImpl}'s base rule.
	 */
	private static double colBase(ShapeNode<?> n) {
		return weightX(n) == 0 ? n.getWidth() : 0;
	}

	/** Circularity-safe base height a child contributes to its row's minimum (see {@link #colBase(ShapeNode)}). */
	private static double rowBase(ShapeNode<?> n) {
		return weightY(n) == 0 ? n.getHeight() : 0;
	}

	/**
	 * A node is "self-constrained" when it carries any of its own geometry constraint bindings (x/y/width/height). Such a node positions
	 * itself and must be left untouched by the grid (mirrors {@link BoxLayoutManagerImpl}).
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

		List<ShapeNode<?>> nodes = new ArrayList<>();
		for (ShapeNode<?> node : getLayoutedNodes()) {
			if (!isSelfConstrained(node)) {
				nodes.add(node);
			}
		}
		if (nodes.isEmpty()) {
			return;
		}

		double hgap = getHgap();
		double vgap = getVgap();

		// Grid dimensions
		int nCols = 0;
		int nRows = 0;
		for (ShapeNode<?> n : nodes) {
			nCols = Math.max(nCols, gridX(n) + gridWidth(n));
			nRows = Math.max(nRows, gridY(n) + gridHeight(n));
		}

		double[] minCol = new double[nCols];
		double[] minRow = new double[nRows];
		double[] colWeight = new double[nCols];
		double[] rowWeight = new double[nRows];

		// 1. Minimum sizes from single-span children, and per-column/row weights (max over covering children)
		for (ShapeNode<?> n : nodes) {
			int gx = gridX(n), gy = gridY(n), gw = gridWidth(n), gh = gridHeight(n);
			if (gw == 1) {
				minCol[gx] = Math.max(minCol[gx], colBase(n));
			}
			if (gh == 1) {
				minRow[gy] = Math.max(minRow[gy], rowBase(n));
			}
			for (int c = gx; c < gx + gw; c++) {
				colWeight[c] = Math.max(colWeight[c], weightX(n));
			}
			for (int r = gy; r < gy + gh; r++) {
				rowWeight[r] = Math.max(rowWeight[r], weightY(n));
			}
		}

		// 2. Multi-span children: top up the spanned columns/rows if their own size exceeds the current span sum
		for (ShapeNode<?> n : nodes) {
			int gx = gridX(n), gy = gridY(n), gw = gridWidth(n), gh = gridHeight(n);
			if (gw > 1) {
				double current = sum(minCol, gx, gw) + hgap * (gw - 1);
				double deficit = colBase(n) - current;
				if (deficit > 0) {
					double add = deficit / gw;
					for (int c = gx; c < gx + gw; c++) {
						minCol[c] += add;
					}
				}
			}
			if (gh > 1) {
				double current = sum(minRow, gy, gh) + vgap * (gh - 1);
				double deficit = rowBase(n) - current;
				if (deficit > 0) {
					double add = deficit / gh;
					for (int r = gy; r < gy + gh; r++) {
						minRow[r] += add;
					}
				}
			}
		}

		// 3. Distribute the container free space proportionally to the column/row weights
		double innerW = Math.max(0, getContainerNode().getWidth() - getInsetLeft() - getInsetRight());
		double innerH = Math.max(0, getContainerNode().getHeight() - getInsetTop() - getInsetBottom());
		double[] colW = distribute(minCol, colWeight, innerW - hgap * Math.max(0, nCols - 1));
		double[] rowH = distribute(minRow, rowWeight, innerH - vgap * Math.max(0, nRows - 1));

		// 4. Cell origins
		double[] colX = new double[nCols];
		double[] rowY = new double[nRows];
		double x = getInsetLeft();
		for (int c = 0; c < nCols; c++) {
			colX[c] = x;
			x += colW[c] + hgap;
		}
		double y = getInsetTop();
		for (int r = 0; r < nRows; r++) {
			rowY[r] = y;
			y += rowH[r] + vgap;
		}

		// 5. Place each child in its cell block, applying fill and anchor
		for (ShapeNode<?> n : nodes) {
			int gx = gridX(n), gy = gridY(n), gw = gridWidth(n), gh = gridHeight(n);
			double blockX = colX[gx];
			double blockY = rowY[gy];
			double blockW = sum(colW, gx, gw) + hgap * (gw - 1);
			double blockH = sum(rowH, gy, gh) + vgap * (gh - 1);

			GridBagLayoutConstraints c = gbc(n);
			GridBagFill fill = (c != null && c.getFill() != null) ? c.getFill() : GridBagFill.NONE;
			double cw = (fill == GridBagFill.BOTH || fill == GridBagFill.HORIZONTAL) ? blockW : Math.min(n.getWidth(), blockW);
			double ch = (fill == GridBagFill.BOTH || fill == GridBagFill.VERTICAL) ? blockH : Math.min(n.getHeight(), blockH);

			double slackX = Math.max(0, blockW - cw);
			double slackY = Math.max(0, blockH - ch);
			GridBagAnchor anchor = (c != null && c.getAnchor() != null) ? c.getAnchor() : GridBagAnchor.CENTER;
			double offX = anchorOffsetX(anchor, slackX);
			double offY = anchorOffsetY(anchor, slackY);

			geometryMap.put(n, new DianaRectangle(blockX + offX, blockY + offY, cw, ch));
		}
	}

	private static double sum(double[] a, int from, int count) {
		double s = 0;
		for (int i = from; i < from + count && i < a.length; i++) {
			s += a[i];
		}
		return s;
	}

	/** Returns base sizes grown by a weight-proportional share of {@code available - Σbase} (clamped to a non-negative free space). */
	private static double[] distribute(double[] base, double[] weight, double available) {
		double[] result = base.clone();
		double used = 0;
		double sumWeight = 0;
		for (int i = 0; i < base.length; i++) {
			used += base[i];
			sumWeight += weight[i];
		}
		double free = Math.max(0, available - used);
		if (sumWeight > 0 && free > 0) {
			for (int i = 0; i < result.length; i++) {
				result[i] += free * weight[i] / sumWeight;
			}
		}
		return result;
	}

	private static double anchorOffsetX(GridBagAnchor anchor, double slack) {
		switch (anchor) {
			case WEST:
			case NORTHWEST:
			case SOUTHWEST:
				return 0;
			case EAST:
			case NORTHEAST:
			case SOUTHEAST:
				return slack;
			case CENTER:
			case NORTH:
			case SOUTH:
			default:
				return slack / 2;
		}
	}

	private static double anchorOffsetY(GridBagAnchor anchor, double slack) {
		switch (anchor) {
			case NORTH:
			case NORTHEAST:
			case NORTHWEST:
				return 0;
			case SOUTH:
			case SOUTHEAST:
			case SOUTHWEST:
				return slack;
			case CENTER:
			case EAST:
			case WEST:
			default:
				return slack / 2;
		}
	}

	private List<ShapeNode<?>> placeableNodes() {
		List<ShapeNode<?>> nodes = new ArrayList<>();
		for (ShapeNode<?> node : getLayoutedNodes()) {
			if (!isSelfConstrained(node)) {
				nodes.add(node);
			}
		}
		return nodes;
	}

	/**
	 * Minimum sizes of the grid tracks (columns if {@code columns}, else rows), computed like {@link #computeLayout()}'s minimum pass but from
	 * each child's intrinsic minimum ({@link #childMinWidth(ShapeNode)} / {@link #childMinHeight(ShapeNode)}, bottom-up) and the
	 * circularity-safe base rule: a track-spanning child with a positive weight on that axis can shrink, so it contributes 0.
	 */
	private double[] minTracks(boolean columns) {
		List<ShapeNode<?>> nodes = placeableNodes();
		double gap = columns ? getHgap() : getVgap();
		int nTracks = 0;
		for (ShapeNode<?> n : nodes) {
			nTracks = Math.max(nTracks, columns ? gridX(n) + gridWidth(n) : gridY(n) + gridHeight(n));
		}
		double[] min = new double[nTracks];
		// single-track children
		for (ShapeNode<?> n : nodes) {
			int span = columns ? gridWidth(n) : gridHeight(n);
			if (span == 1) {
				int idx = columns ? gridX(n) : gridY(n);
				min[idx] = Math.max(min[idx], trackBase(n, columns));
			}
		}
		// multi-track children: top up the spanned tracks if their own min exceeds the current span sum
		for (ShapeNode<?> n : nodes) {
			int span = columns ? gridWidth(n) : gridHeight(n);
			if (span > 1) {
				int start = columns ? gridX(n) : gridY(n);
				double current = sum(min, start, span) + gap * (span - 1);
				double deficit = trackBase(n, columns) - current;
				if (deficit > 0) {
					double add = deficit / span;
					for (int t = start; t < start + span; t++) {
						min[t] += add;
					}
				}
			}
		}
		return min;
	}

	/**
	 * Minimum extent a child contributes to a track. A child <b>stretched on that axis</b> (fill HORIZONTAL/BOTH for columns,
	 * VERTICAL/BOTH for rows) has its size written by the manager, so its current size cannot be used as a minimum (it would drift); its only
	 * stable floor is its declared {@link org.openflexo.diana.ShapeGraphicalRepresentation#getMinimalWidth() minimalWidth} /
	 * {@code minimalHeight} (default 0). A child <b>not</b> stretched on that axis keeps its own (natural) size, used directly (bottom-up via
	 * {@link #childMinWidth(ShapeNode)}). Weight only governs how free space is distributed, not the floor, so it does not enter here.
	 */
	private double trackBase(ShapeNode<?> n, boolean columns) {
		GridBagLayoutConstraints c = gbc(n);
		GridBagFill fill = (c != null && c.getFill() != null) ? c.getFill() : GridBagFill.NONE;
		if (columns) {
			boolean stretched = fill == GridBagFill.HORIZONTAL || fill == GridBagFill.BOTH;
			return stretched ? n.getGraphicalRepresentation().getMinimalWidth() : childMinWidth(n);
		}
		boolean stretched = fill == GridBagFill.VERTICAL || fill == GridBagFill.BOTH;
		return stretched ? n.getGraphicalRepresentation().getMinimalHeight() : childMinHeight(n);
	}

	@Override
	public double getMinimumWidth() {
		double[] cols = minTracks(true);
		return sum(cols, 0, cols.length) + getHgap() * Math.max(0, cols.length - 1) + getInsetLeft() + getInsetRight();
	}

	@Override
	public double getMinimumHeightForWidth(double width) {
		double[] rows = minTracks(false);
		return sum(rows, 0, rows.length) + getVgap() * Math.max(0, rows.length - 1) + getInsetTop() + getInsetBottom();
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
		if (GridBagLayoutManagerSpecification.HGAP_KEY.equals(pn) || GridBagLayoutManagerSpecification.VGAP_KEY.equals(pn)
				|| GridBagLayoutManagerSpecification.INSET_TOP_KEY.equals(pn) || GridBagLayoutManagerSpecification.INSET_BOTTOM_KEY.equals(pn)
				|| GridBagLayoutManagerSpecification.INSET_LEFT_KEY.equals(pn)
				|| GridBagLayoutManagerSpecification.INSET_RIGHT_KEY.equals(pn)) {
			invalidate();
			doLayout(true);
		}
	}
}
