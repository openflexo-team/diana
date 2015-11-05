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

package org.openflexo.fge.layout.impl;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.layout.TreeLayoutManager;
import org.openflexo.fge.layout.TreeLayoutManagerSpecification;
import org.openflexo.gina.annotation.FIBPanel;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.util.TreeUtils;

/**
 * Default implementation for {@link TreeLayoutManager}
 * 
 * @author sylvain
 * 
 */
@FIBPanel("Fib/Layout/TreeLayoutManagerPanel.fib")
public abstract class TreeLayoutManagerImpl<O> extends TreeBasedLayoutManagerImpl<TreeLayoutManagerSpecification<O>, O> implements
		TreeLayoutManager<O> {

	private DianaTreeLayout<ShapeNode<?>, ConnectorNode<?>> layout;

	private Map<ShapeNode<?>, FGERectangle> layoutPositions;
	private Map<Integer, Double> rowHeights;
	private final Map<Integer, Double> fixedRowHeights;

	public TreeLayoutManagerImpl() {
		super();
		controlAreas = new ArrayList<ControlArea<?>>();
		fixedRowHeights = new HashMap<Integer, Double>();
	}

	@Override
	protected TreeLayout<ShapeNode<?>, ConnectorNode<?>> buildLayout() {
		layout = new DianaTreeLayout<ShapeNode<?>, ConnectorNode<?>>(getForest(), getSpacingX(), getSpacingY(), getBorderX(), getBorderY());
		// layout.setSize(new Dimension((int) getContainerNode().getWidth(), (int) getContainerNode().getHeight()));

		layoutPositions = new HashMap<ShapeNode<?>, FGERectangle>();
		rowHeights = new HashMap<Integer, Double>();

		for (Integer i : getLayout().getVerticesByRows().keySet()) {
			double height = getLayout().getSpacingY();
			for (ShapeNode<?> n : getLayout().getVerticesByRows().get(i)) {
				height = Math.max(height, n.getHeight() + getBorderY() * 2);
				if (getFixedRowHeight(i) != null) {
					if (getFixedRowHeight(i) > height) {
						height = getFixedRowHeight(i);
					} /*else {
						fixedRowHeights.remove(i);
						}*/
				}
			}
			rowHeights.put(i, height);
		}

		Collection<ShapeNode<?>> roots = TreeUtils.getRoots(getForest());

		for (ShapeNode<?> root : roots) {
			double xPosition = 0;
			double yPosition = 0;
			buildRequiredBounds(root, xPosition, yPosition, getHeight(root));
		}

		/*	for (ShapeNode<?> n : getGraph().getVertices()) {
				System.out.println("Base position " + layoutPositions.get(n) + " depth=" + getDepth(n) + " for node " + n);
			}*/

		controlAreas.clear();

		double h = 0;
		for (int i = 0; i < getLayout().getVerticesByRows().size(); i++) {
			switch (getVerticalAlignment()) {
			case TOP:
				controlAreas.add(new TreeBaseLineAdjustingArea(this, i, h));
				break;
			case MIDDLE:
				controlAreas.add(new TreeBaseLineAdjustingArea(this, i, h + getRowHeight(i) / 2));
				break;
			case BOTTOM:
				controlAreas.add(new TreeBaseLineAdjustingArea(this, i, h + getRowHeight(i)));
				break;
			}
			h += getRowHeight(i);
		}

		return layout;
	}

	public Double getHeight(ShapeNode<?> n) {
		int depth = getLayout().getDepth(n);
		// System.out.println("height for " + n.getText() + ": " + rowHeights.get(depth));
		return rowHeights.get(depth);
	}

	@Override
	public double getRowHeight(int depth) {
		return rowHeights.get(depth);
	}

	@Override
	public Double getFixedRowHeight(int depth) {
		return fixedRowHeights.get(depth);
	}

	@Override
	public void setFixedRowHeight(int depth, double height) {
		layoutInProgress = true;
		fixedRowHeights.put(depth, height);
		invalidate();
		doLayout(false);
		layoutInProgress = false;
	}

	private FGERectangle buildRequiredBounds(ShapeNode<?> n, double xPosition, double yPosition, double height) {

		double startXPosition = xPosition;
		double requiredWithForChildren = 0;
		for (ShapeNode<?> child : getGraph().getSuccessors(n)) {
			FGERectangle childRequiredBounds = buildRequiredBounds(child, xPosition, yPosition + height, getHeight(child));
			requiredWithForChildren += childRequiredBounds.getWidth();
			xPosition += childRequiredBounds.getWidth();
		}

		double width = Math.max(Math.max(getLayout().getSpacingX(), n.getWidth() + getLayout().getBorderX() * 2), requiredWithForChildren);
		FGERectangle returned = new FGERectangle(startXPosition, yPosition, width, height);

		// System.out.println("Node " + n.getText() + " " + returned);
		layoutPositions.put(n, returned);

		return returned;
	}

	@Override
	public DianaTreeLayout<ShapeNode<?>, ConnectorNode<?>> getLayout() {
		return layout;
	}

	@Override
	protected FGEPoint locationForNode(ShapeNode<?> node) {
		// Point2D newLocation = getLayout().transform(node);
		FGERectangle bounds = layoutPositions.get(node);
		double x = 0;
		double y = 0;

		if (bounds != null) {

			switch (getHorizontalAlignment()) {
			case CENTER:
				x = bounds.getCenter().getX() - node.getWidth() / 2 - node.getBorder().getLeft();
				break;
			case LEFT:
				x = bounds.getX() - node.getBorder().getLeft();
				break;
			case RIGHT:
				x = bounds.getX() + bounds.getWidth() - node.getWidth() - node.getBorder().getLeft();
				break;
			}

			switch (getVerticalAlignment()) {
			case MIDDLE:
				y = bounds.getCenter().getY() - node.getHeight() / 2 - node.getBorder().getTop();
				break;
			case TOP:
				y = bounds.getY() - node.getBorder().getTop();
				break;
			case BOTTOM:
				y = bounds.getY() + bounds.getHeight() - node.getHeight() - node.getBorder().getTop();
				break;
			}

		}
		// return new FGEPoint(bounds.getCenter().getX() - node.getWidth() / 2 - node.getBorder().getLeft(), bounds.getCenter().getY()
		// - node.getHeight() / 2 - node.getBorder().getTop());

		return new FGEPoint(x, y);
	}

	/*@Override
	protected FGEPoint locationForNode(ShapeNode<?> node) {
		FGEPoint returned = super.locationForNode(node);
		// returned.setX(returned.getX() - getLayout().getDistX());
		returned.setY(returned.getY() - getLayout().getDistY());
		return returned;
	}*/

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getPropertyName().equals(TreeLayoutManagerSpecification.SPACING_X_KEY)) {
			invalidate();
			doLayout(true);
		} else if (evt.getPropertyName().equals(TreeLayoutManagerSpecification.SPACING_Y_KEY)) {
			invalidate();
			doLayout(true);
		} else if (evt.getPropertyName().equals(TreeLayoutManagerSpecification.BORDER_X_KEY)) {
			invalidate();
			doLayout(true);
		} else if (evt.getPropertyName().equals(TreeLayoutManagerSpecification.BORDER_Y_KEY)) {
			invalidate();
			doLayout(true);
		}

	}

	/**
	 * Called to paint decoration
	 * 
	 * @param g
	 */
	@Override
	public void paintDecoration(FGEGraphics g) {

		g.setDefaultForeground(getFactory().makeForegroundStyle(Color.BLUE, 1));
		g.useDefaultForegroundStyle();

		if (layoutPositions != null) {
			for (FGERectangle rectangle : layoutPositions.values()) {
				rectangle.paint(g);
			}
		}

	}

	private final List<ControlArea<?>> controlAreas;

	@Override
	public List<ControlArea<?>> getControlAreas() {
		return controlAreas;
	}

	public double getSpacingX() {
		return getLayoutManagerSpecification().getSpacingX();
	}

	public double getSpacingY() {
		return getLayoutManagerSpecification().getSpacingY();
	}

	public double getBorderX() {
		return getLayoutManagerSpecification().getBorderX();
	}

	public double getBorderY() {
		return getLayoutManagerSpecification().getBorderY();
	}

}
