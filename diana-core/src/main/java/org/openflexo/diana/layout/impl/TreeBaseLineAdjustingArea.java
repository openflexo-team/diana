/**
 * 
 * Copyright (c) 2013-2014, Openflexo
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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.ForegroundStyle.DashStyle;
import org.openflexo.diana.GraphicalRepresentation.VerticalTextAlignment;
import org.openflexo.diana.control.DianaEditor;
import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaPlane;
import org.openflexo.diana.graphics.DianaGraphics;
import org.openflexo.diana.impl.LayoutAdjustingArea;
import org.openflexo.diana.layout.TreeLayoutManager;

public class TreeBaseLineAdjustingArea extends LayoutAdjustingArea<DianaLine> {

	private final int rowIndex;

	private ForegroundStyle foregroundStyle = null;

	public TreeBaseLineAdjustingArea(TreeLayoutManager<?> layoutManager, int rowIndex, double height) {
		super(layoutManager, new DianaLine(0, height, 1, height));
		this.rowIndex = rowIndex;
		foregroundStyle = getNode().getFactory().makeForegroundStyle(Color.GRAY, 0.5f, DashStyle.DOTS_DASHES);
	}

	@Override
	public TreeLayoutManager<?> getLayoutManager() {
		return (TreeLayoutManager<?>) super.getLayoutManager();
	}

	@Override
	public ContainerNode<?, ?> getNode() {
		return super.getNode();
	}

	@Override
	public DianaArea getDraggingAuthorizedArea() {
		return new DianaPlane();
	}

	@Override
	public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration, DianaPoint newAbsolutePoint,
			DianaPoint initialPoint, MouseEvent event) {
		super.dragToPoint(newRelativePoint, pointRelativeToInitialConfiguration, newAbsolutePoint, initialPoint, event);
		System.out.println("On draggue a " + newRelativePoint + " pointRelativeToInitialConfiguration="
				+ pointRelativeToInitialConfiguration + " startY=" + startY + " pour " + rowIndex);

		switch (getLayoutManager().getVerticalAlignment()) {
			case TOP:
				if (rowIndex > 0) {
					getLayoutManager().setFixedRowHeight(rowIndex - 1, startHeight + newRelativePoint.y - startY);
				}
				break;
			case MIDDLE:
				getLayoutManager().setFixedRowHeight(rowIndex, (newRelativePoint.y - startY) * 2);
				break;
			case BOTTOM:
				System.out.println("row=" + rowIndex);
				System.out.println("startHeight=" + startHeight);
				System.out.println("startY=" + startY);
				System.out.println("newRelativePoint.y=" + newRelativePoint.y);
				System.out.println("NEW row height =" + (newRelativePoint.y - startY));
				getLayoutManager().setFixedRowHeight(rowIndex, newRelativePoint.y - startY);
				break;
			default:
				break;
		}

		draggedArea.setY1(newRelativePoint.y);
		draggedArea.setY2(newRelativePoint.y);
		return true;
	}

	private DianaLine draggedArea;
	private double startY;
	private double startHeight;

	@Override
	public void startDragging(DianaEditor<?> controller, DianaPoint startPoint) {
		super.startDragging(controller, startPoint);
		System.out.println("On commence a dragguer a " + startPoint);
		draggedArea = getArea().clone();
		startY = 0;
		for (int i = 0; i < rowIndex; i++) {
			startY += getLayoutManager().getRowHeight(i);
		}
		if (getLayoutManager().getVerticalAlignment() == VerticalTextAlignment.TOP) {
			startHeight = getLayoutManager().getRowHeight(rowIndex - 1);
		}
		else {
			startHeight = getLayoutManager().getRowHeight(rowIndex);
		}
	}

	@Override
	public void stopDragging(DianaEditor<?> controller, DrawingTreeNode<?, ?> focused) {
		super.stopDragging(controller, focused);
		System.out.println("On fini de dragguer");
		draggedArea = null;
		getLayoutManager().invalidate();
		getLayoutManager().doLayout(true);
	}

	@Override
	public Cursor getDraggingCursor() {
		return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
	}

	@Override
	public boolean isDraggable() {
		return true;
	}

	@Override
	public Rectangle paint(DianaGraphics graphics) {
		graphics.setDefaultForeground(foregroundStyle);
		graphics.useDefaultForegroundStyle();

		if (draggedArea != null) {
			draggedArea.paint(graphics);
		}
		else {
			getArea().paint(graphics);
		}

		return null;
	}

}
