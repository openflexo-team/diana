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

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEGrid;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.impl.FGELayoutManagerImpl;
import org.openflexo.fge.layout.GridLayoutManager;
import org.openflexo.fge.layout.GridLayoutManagerSpecification;
import org.openflexo.fib.annotation.FIBPanel;

/**
 * Default implementation for {@link GridLayoutManager}
 * 
 * @author sylvain
 * 
 */
@FIBPanel("Fib/Layout/GridLayoutManagerPanel.fib")
public abstract class GridLayoutManagerImpl<O> extends FGELayoutManagerImpl<GridLayoutManagerSpecification, O> implements
		GridLayoutManager<O> {

	@Override
	public double getGridX() {
		return getLayoutManagerSpecification().getGridX();
	}

	@Override
	public double getGridY() {
		return getLayoutManagerSpecification().getGridY();
	}

	@Override
	public boolean isFullyLayouted() {
		return false;
	}

	private FGEGrid grid = null;

	public FGEGrid getGrid() {
		if (grid == null) {
			// System.out.println("Nouvelle grid " + getGridX() + "x" + getGridY());
			grid = new FGEGrid(new FGEPoint(0, 0), getGridX(), getGridY());
		}
		return grid;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		grid = null;
	}

	@Override
	protected void performLayout(ShapeNode<?> node) {

		double newX = node.getX();
		double newY = node.getY();

		switch (getLayoutManagerSpecification().getHorizontalAlignment()) {
		case CENTER:
			newX = ((int) (((node.getX() + node.getWidth() / 2 + node.getBorder().getLeft()) / getGridX()) + 0.5) * getGridX())
					- node.getWidth() / 2 - node.getBorder().getLeft();
			break;
		case LEFT:
			newX = ((int) (((node.getX() + node.getBorder().getLeft()) / getGridX()) + 0.5) * getGridX()) - node.getBorder().getLeft();
			break;
		case RIGHT:
			newX = ((int) (((node.getX() + node.getWidth() + node.getBorder().getLeft()) / getGridX()) + 0.5) * getGridX())
					- node.getWidth() - node.getBorder().getLeft();
			break;
		default:
			break;
		}

		switch (getLayoutManagerSpecification().getVerticalAlignment()) {
		case MIDDLE:
			newY = ((int) (((node.getY() + node.getHeight() / 2 + node.getBorder().getTop()) / getGridY()) + 0.5) * getGridY())
					- node.getHeight() / 2 - node.getBorder().getTop();
			break;
		case TOP:
			newY = ((int) (((node.getY() + node.getBorder().getTop()) / getGridY()) + 0.5) * getGridY()) - node.getBorder().getTop();
			break;
		case BOTTOM:
			newY = ((int) (((node.getY() + node.getHeight() + node.getBorder().getTop()) / getGridY()) + 0.5) * getGridY())
					- node.getHeight() - node.getBorder().getTop();
			break;
		default:
			break;
		}

		node.setLocation(new FGEPoint(newX, newY));
	}

	/**
	 * Called to paint decoration
	 * 
	 * @param g
	 */
	@Override
	public void paintDecoration(FGEGraphics g) {

		g.setDefaultForeground(getFactory().makeForegroundStyle(Color.LIGHT_GRAY, 1, DashStyle.DOTS_DASHES));
		g.useDefaultForegroundStyle();

		getGrid().paint(g);

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		System.out.println("propertyChange in GridLayoutManagerImpl with " + evt.getPropertyName() + " evt=" + evt);

		super.propertyChange(evt);
		if (evt.getPropertyName().equals(GridLayoutManagerSpecification.GRID_X_KEY)) {
			invalidate();
			doLayout(true);
			getContainerNode().notifyNodeLayoutDecorationChanged(this);
		} else if (evt.getPropertyName().equals(GridLayoutManagerSpecification.GRID_Y_KEY)) {
			invalidate();
			doLayout(true);
			getContainerNode().notifyNodeLayoutDecorationChanged(this);
		} else if (evt.getPropertyName().equals(GridLayoutManagerSpecification.HORIZONTAL_ALIGNEMENT_KEY)) {
			invalidate();
			doLayout(true);
			getContainerNode().notifyNodeLayoutDecorationChanged(this);
		} else if (evt.getPropertyName().equals(GridLayoutManagerSpecification.VERTICAL_ALIGNEMENT_KEY)) {
			invalidate();
			doLayout(true);
			getContainerNode().notifyNodeLayoutDecorationChanged(this);
		}
	}
}
