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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.DianaInteractiveViewer;
import org.openflexo.diana.control.MouseControlContext;

public class RectangleSelectingAction extends MouseDragControlActionImpl<DianaInteractiveViewer<?, ?, ?>> {

	static final Logger logger = Logger.getLogger(RectangleSelectingAction.class.getPackage().getName());

	private Point rectangleSelectingOriginInDrawingView;
	private Point currentMousePositionInDrawingView;

	@Override
	public boolean handleMousePressed(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> editor, MouseControlContext context) {

		if (editor != null) {
			DianaInteractiveViewer<?, ?, ?> controller = editor;

			// logger.info("Perform mouse PRESSED on RectangleSelectingAction");

			rectangleSelectingOriginInDrawingView = getPointInDrawingView(controller, context);

			currentMousePositionInDrawingView = rectangleSelectingOriginInDrawingView;
			if (controller.getDrawingView() == null) {
				return false;
			}
			controller.getDrawingView().setRectangleSelectingAction(this);
			return true;
		}
		return false;
	}

	@Override
	public boolean handleMouseReleased(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> editor, MouseControlContext context,
			boolean isSignificativeDrag) {
		if (editor != null) {

			// logger.info("Perform mouse RELEASED on RectangleSelectingAction, isSignificative=" + isSignificativeDrag);

			DianaInteractiveViewer<?, ?, ?> controller = editor;
			if (isSignificativeDrag && node instanceof ContainerNode) {
				List<DrawingTreeNode<?, ?>> newSelection = buildCurrentSelection((ContainerNode<?, ?>) node, controller);
				controller.setSelectedObjects(newSelection);
				if (controller.getDrawingView() == null) {
					return false;
				}
				controller.getDrawingView().resetRectangleSelectingAction();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean handleMouseDragged(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> editor, MouseControlContext context) {
		if (editor != null) {
			DianaInteractiveViewer<?, ?, ?> controller = editor;

			// logger.info("Perform mouse DRAGGED on RectangleSelectingAction");

			currentMousePositionInDrawingView = getPointInDrawingView(controller, context);

			List<DrawingTreeNode<?, ?>> newFocusSelection;
			if (node instanceof ContainerNode) {
				newFocusSelection = buildCurrentSelection((ContainerNode<?, ?>) node, controller);
			}
			else {
				newFocusSelection = Collections.emptyList();
			}
			controller.setFocusedObjects(newFocusSelection);
			if (controller.getDrawingView() == null) {
				return false;
			}
			if (controller.getDelegate() != null) {
				controller.getDelegate().repaintAll();
			}

			return true;
		}
		return false;
	}

	private List<DrawingTreeNode<?, ?>> buildCurrentSelection(ContainerNode<?, ?> node, AbstractDianaEditor<?, ?, ?> controller) {
		if (getRectangleSelection() == null) {
			return null;
		}
		List<DrawingTreeNode<?, ?>> returned = new Vector<>();
		for (DrawingTreeNode<?, ?> child : node.getChildNodes()) {
			if (child.getGraphicalRepresentation().getIsVisible()) {
				if (child.isContainedInSelection(getRectangleSelection(), controller.getScale())) {
					returned.add(child);
				}
				if (child instanceof ContainerNode) {
					returned.addAll(buildCurrentSelection((ContainerNode<?, ?>) child, controller));
				}

			}
		}
		return returned;
	}

	/**
	 * Return current rectangle selection
	 * 
	 * @return Rectangle object as current selection
	 */
	private Rectangle getRectangleSelection() {
		if (rectangleSelectingOriginInDrawingView != null && currentMousePositionInDrawingView != null) {
			Point origin = new Point();
			Dimension dim = new Dimension();
			if (rectangleSelectingOriginInDrawingView.x <= currentMousePositionInDrawingView.x) {
				origin.x = rectangleSelectingOriginInDrawingView.x;
				dim.width = currentMousePositionInDrawingView.x - rectangleSelectingOriginInDrawingView.x;
			}
			else {
				origin.x = currentMousePositionInDrawingView.x;
				dim.width = rectangleSelectingOriginInDrawingView.x - currentMousePositionInDrawingView.x;
			}
			if (rectangleSelectingOriginInDrawingView.y <= currentMousePositionInDrawingView.y) {
				origin.y = rectangleSelectingOriginInDrawingView.y;
				dim.height = currentMousePositionInDrawingView.y - rectangleSelectingOriginInDrawingView.y;
			}
			else {
				origin.y = currentMousePositionInDrawingView.y;
				dim.height = rectangleSelectingOriginInDrawingView.y - currentMousePositionInDrawingView.y;
			}
			return new Rectangle(origin, dim);
		}
		return null;
	}

	public void paint(Graphics g, AbstractDianaEditor<?, ?, ?> controller) {
		Rectangle selection = getRectangleSelection();
		if (selection == null) {
			return;
		}
		g.setColor(controller.getDrawing().getRoot().getGraphicalRepresentation().getRectangleSelectingSelectionColor());
		g.drawRect(selection.x, selection.y, selection.width, selection.height);
	}

}
