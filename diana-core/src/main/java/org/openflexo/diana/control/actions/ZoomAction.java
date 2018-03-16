/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.diana.control.actions;

import java.awt.Point;

import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.control.DianaInteractiveViewer;
import org.openflexo.diana.control.MouseControlContext;
import org.openflexo.diana.geom.FGELine;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.geom.FGEGeometricObject.SimplifiedCardinalDirection;

public class ZoomAction extends MouseDragControlActionImpl<DianaInteractiveViewer<?, ?, ?>> {

	private Point startPoint;
	private double initialScale;
	private static final double PIXEL_TO_PERCENT = 0.005;
	private FGELine refLine;

	@Override
	public boolean handleMouseDragged(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> editor, MouseControlContext context) {
		if (editor != null) {
			DianaInteractiveViewer<?, ?, ?> controller = editor;
			Point currentMousePositionInDrawingView = getPointInDrawingView(controller, context);
			SimplifiedCardinalDirection card = FGEPoint.getSimplifiedOrientation(new FGEPoint(startPoint),
					new FGEPoint(currentMousePositionInDrawingView));
			boolean isPositive = true;
			double distance = 0.0;
			switch (card) {
				case NORTH:
				case WEST:
					isPositive = false;
					break;
				default:
					break;
			}
			// We compute a distance to this refline instead of a distance to the start point so that that there is no gap when going from
			// positive to negative
			distance = refLine.ptLineDist(currentMousePositionInDrawingView);
			double newScale = initialScale;
			if (isPositive) {
				newScale += distance * PIXEL_TO_PERCENT;
			}
			else {
				newScale -= distance * PIXEL_TO_PERCENT;
			}
			controller.setScale(newScale);
			return true;
		}
		return false;
	}

	@Override
	public boolean handleMousePressed(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> editor, MouseControlContext context) {
		if (editor != null) {
			DianaInteractiveViewer<?, ?, ?> controller = editor;
			startPoint = getPointInDrawingView(controller, context);
			// Virtual line that goes through the start point and its orientation is NORTH_EAST (or SOUTH_WEST, it's the same)
			refLine = new FGELine(new FGEPoint(startPoint), new FGEPoint(startPoint.x + 1, startPoint.y - 1));
			initialScale = controller.getScale();
			return true;
		}
		return false;
	}

	@Override
	public boolean handleMouseReleased(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> editor, MouseControlContext context,
			boolean isSignificativeDrag) {
		if (editor != null) {
			DianaInteractiveViewer<?, ?, ?> controller = editor;
			startPoint = null;
			refLine = null;
			initialScale = controller.getScale();
			return true;
		}
		return false;
	}
}
