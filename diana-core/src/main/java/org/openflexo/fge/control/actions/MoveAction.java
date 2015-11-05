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

package org.openflexo.fge.control.actions;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.view.FGEView;
import org.openflexo.gina.utils.FIBIconLibrary;
import org.openflexo.toolbox.ToolBox;

public class MoveAction extends MouseDragControlActionImpl<DianaInteractiveViewer<?, ?, ?>> {
	private static final Logger logger = Logger.getLogger(MoveAction.class.getPackage().getName());

	private MoveInfo currentMove = null;
	private DNDInfo currentDND = null;

	private static final Image DROP_OK_IMAGE = FIBIconLibrary.DROP_OK_CURSOR.getImage();
	private static final Image DROP_KO_IMAGE = FIBIconLibrary.DROP_KO_CURSOR.getImage();

	public static Cursor dropOK = ToolBox.getPLATFORM() == ToolBox.MACOS ? Toolkit.getDefaultToolkit().createCustomCursor(DROP_OK_IMAGE,
			new Point(16, 16), "Drop OK") : DragSource.DefaultMoveDrop;
	public static Cursor dropKO = ToolBox.getPLATFORM() == ToolBox.MACOS ? Toolkit.getDefaultToolkit().createCustomCursor(DROP_KO_IMAGE,
			new Point(16, 16), "Drop KO") : DragSource.DefaultMoveNoDrop;

	public Point initialClickOffset;

	@Override
	public boolean handleMouseDragged(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> editor, MouseControlContext context) {
		if (editor instanceof DianaInteractiveViewer) {
			DianaInteractiveViewer<?, ?, ?> controller = (DianaInteractiveViewer<?, ?, ?>) editor;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Perform mouse DRAGGED on MOVE AbstractMouseDragControlActionImpl");
			}
			if (currentMove != null) {
				Point newPointLocation = getPointInDrawingView(controller, context);

				if (node instanceof ShapeNode
						&& ((ShapeNode<?>) node).getGraphicalRepresentation().isAllowedToBeDraggedOutsideParentContainer()
						&& currentMove.isDnDPattern(newPointLocation, context) && currentDND == null) {
					currentMove.stopDragging();
					currentMove = null;
					currentDND = controller.getDianaFactory().makeDNDInfo(this, (ShapeNode<?>) node, controller, context);
				} else {
					currentMove.moveTo(newPointLocation);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean handleMousePressed(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> editor, MouseControlContext context) {
		if (editor instanceof DianaInteractiveViewer) {
			DianaInteractiveViewer<?, ?, ?> controller = (DianaInteractiveViewer<?, ?, ?>) editor;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Perform mouse PRESSED on MOVE AbstractMouseDragControlActionImpl");
			}
			initialClickOffset = getPointInView(node, controller, context);
			FGEView<?, ?> view = ((AbstractDianaEditor<?, ?, ?>) controller).getDrawingView().viewForNode(node);
			if (node instanceof ShapeNode && !node.getGraphicalRepresentation().getIsReadOnly() && node.getDrawing().isEditable()
					&& ((ShapeNode<?>) node).getGraphicalRepresentation().getLocationConstraints() != LocationConstraints.UNMOVABLE) {
				// Let's go for a move
				currentMove = new MoveInfo((ShapeNode<?>) node, context, view, controller);
				// controller.notifyWillMove(currentMove);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean handleMouseReleased(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> editor, MouseControlContext context,
			boolean isSignificativeDrag) {
		if (editor instanceof DianaInteractiveViewer) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Perform mouse RELEASED on MOVE AbstractMouseDragControlActionImpl");
			}
			if (currentMove != null) {
				if (isSignificativeDrag) {
					currentMove.stopDragging();
				}
				// controller.notifyHasMoved(currentMove);
				currentMove = null;
				return true;
			}
		}
		return false;
	}

	public static class ShapeNodeTransferable implements Transferable {

		private static DataFlavor _defaultFlavor;

		private final TransferedShapeNode _transferedData;

		public ShapeNodeTransferable(ShapeNode<?> element, Point dragOrigin) {
			_transferedData = new TransferedShapeNode(element, dragOrigin);
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { defaultFlavor() };
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return true;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			return _transferedData;
		}

		public static DataFlavor defaultFlavor() {
			if (_defaultFlavor == null) {
				_defaultFlavor = new DataFlavor(ShapeNodeTransferable.class, "ShapeNode");
			}
			return _defaultFlavor;
		}

	}

	public static class TransferedShapeNode {
		private final Point offset;

		private final ShapeNode<?> transfered;

		public TransferedShapeNode(ShapeNode<?> element, Point dragOffset) {
			super();
			transfered = element;
			offset = dragOffset;
		}

		public Point getOffset() {
			return offset;
		}

		public ShapeNode<?> getTransferedElement() {
			return transfered;
		}

	}

	public void resetCurrentDND() {
		currentDND = null;
	}

}
