/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-drawing-editor, a component of the software infrastructure 
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

package org.openflexo.diana.drawingeditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.MouseControlContext;
import org.openflexo.diana.control.actions.MouseDragControlActionImpl;
import org.openflexo.diana.control.actions.MouseDragControlImpl;
import org.openflexo.diana.drawingeditor.model.Connector;
import org.openflexo.diana.drawingeditor.model.DiagramElement;
import org.openflexo.diana.drawingeditor.model.DiagramFactory;
import org.openflexo.diana.drawingeditor.model.Shape;
import org.openflexo.diana.swing.control.JMouseControlContext;
import org.openflexo.model.undo.CompoundEdit;

public class DrawEdgeControl extends MouseDragControlImpl<DianaDrawingEditor> {

	public DrawEdgeControl(DiagramFactory factory) {
		super("Draw edge", MouseButton.LEFT, new DrawEdgeAction(factory), false, true, false, false, factory.getEditingContext()); // CTRL-DRAG
	}

	protected static class DrawEdgeAction extends MouseDragControlActionImpl<DianaDrawingEditor> {

		Point currentDraggingLocationInDrawingView = null;
		boolean drawEdge = false;
		ShapeNode<Shape> fromShape = null;
		ShapeNode<Shape> toShape = null;
		private final DiagramFactory factory;

		public DrawEdgeAction(DiagramFactory factory) {
			this.factory = factory;
		}

		@Override
		public boolean handleMousePressed(DrawingTreeNode<?, ?> node, DianaDrawingEditor controller, MouseControlContext context) {
			if (node instanceof ShapeNode) {
				drawEdge = true;
				fromShape = (ShapeNode<Shape>) node;
				controller.getDrawingView().setDrawEdgeAction(this);
				return true;
			}
			return false;
		}

		@Override
		public boolean handleMouseReleased(DrawingTreeNode<?, ?> node, DianaDrawingEditor controller, MouseControlContext context,
				boolean isSignificativeDrag) {
			if (drawEdge) {
				if (fromShape != null && toShape != null) {
					// System.out.println("Add ConnectorSpecification contextualMenuInvoker="+contextualMenuInvoker+"
					// point="+contextualMenuClickedPoint);
					CompoundEdit drawEdge = factory.getUndoManager().startRecording("Draw edge");
					Connector newConnector = factory.makeNewConnector(fromShape.getDrawable(), toShape.getDrawable(),
							controller.getDrawing().getModel());
					DrawingTreeNode<?, ?> fatherNode = DianaUtils.getFirstCommonAncestor(fromShape, toShape);
					((DiagramElement<?, ?>) fatherNode.getDrawable()).addToConnectors(newConnector);
					System.out.println("Add new connector !");
					factory.getUndoManager().stopRecording(drawEdge);
					controller.setSelectedObject(controller.getDrawing().getDrawingTreeNode(newConnector));
				}
				drawEdge = false;
				fromShape = null;
				toShape = null;
				controller.getDrawingView().setDrawEdgeAction(null);
				return true;
			}
			return false;
		}

		@Override
		public boolean handleMouseDragged(DrawingTreeNode<?, ?> node, DianaDrawingEditor controller, MouseControlContext context) {
			if (drawEdge) {
				MouseEvent event = ((JMouseControlContext) context).getMouseEvent();
				DrawingTreeNode<?, ?> dtn = controller.getDrawingView().getFocusRetriever().getFocusedObject(event);
				if (dtn instanceof ShapeNode && dtn != fromShape && !fromShape.getAncestors().contains(dtn)) {
					toShape = (ShapeNode<Shape>) dtn;
				}
				else {
					toShape = null;
				}
				currentDraggingLocationInDrawingView = getPointInDrawingView(controller, context);
				controller.getDrawingView().getPaintManager().repaint(controller.getDrawingView());
				return true;
			}
			return false;
		}

		public void paint(Graphics g, AbstractDianaEditor<?, ?, ?> controller) {
			if (drawEdge && currentDraggingLocationInDrawingView != null) {
				Point from = controller.getDrawing().getRoot().convertRemoteNormalizedPointToLocalViewCoordinates(
						fromShape.getShape().getShape().getCenter(), fromShape, controller.getScale());
				Point to = currentDraggingLocationInDrawingView;
				if (toShape != null) {
					to = controller.getDrawing().getRoot().convertRemoteNormalizedPointToLocalViewCoordinates(
							toShape.getShape().getShape().getCenter(), toShape, controller.getScale());
					g.setColor(Color.BLUE);
				}
				else {
					g.setColor(Color.RED);
				}
				g.drawLine(from.x, from.y, to.x, to.y);
			}
		}
	}

}
