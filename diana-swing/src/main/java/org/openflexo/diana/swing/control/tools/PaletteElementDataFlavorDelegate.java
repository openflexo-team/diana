package org.openflexo.diana.swing.control.tools;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.io.IOException;
import java.util.logging.Logger;

import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.control.PaletteElement;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.impl.ShapeNodeImpl;
import org.openflexo.diana.swing.control.tools.JDianaPalette.TransferedPaletteElement;
import org.openflexo.diana.view.DianaView;

/**
 * Implementation of a {@link DataFlavorDelegate} dedicated to JDianaPalette.PALETTE_ELEMENT_FLAVOR<br>
 * 
 * @author sylvain
 *
 */
public class PaletteElementDataFlavorDelegate extends DataFlavorDelegate {

	private static final Logger logger = Logger.getLogger(PaletteElementDataFlavorDelegate.class.getPackage().getName());

	public PaletteElementDataFlavorDelegate(DianaViewDropListener dropListener) {
		super(dropListener);
	}

	@Override
	public DataFlavor getDataFlavor() {
		return JDianaPalette.PALETTE_ELEMENT_FLAVOR;
	}

	@Override
	public int getAcceptableActions() {
		return DnDConstants.ACTION_COPY | DnDConstants.ACTION_MOVE;
	}

	@Override
	public boolean isDragOk(DropTargetDragEvent e) {

		try {
			PaletteElement element = ((TransferedPaletteElement) e.getTransferable().getTransferData(JDianaPalette.PALETTE_ELEMENT_FLAVOR))
					.getPaletteElement();
			if (element == null) {
				return false;
			}
			DrawingTreeNode<?, ?> focused = getFocusedObject(e);
			if (focused == null) {
				return false;
			}
			return element.acceptDragging(focused);

		} catch (UnsupportedFlavorException e1) {
			logger.warning("Unexpected: " + e1);
			e1.printStackTrace();
			return false;
		} catch (IOException e1) {
			logger.warning("Unexpected: " + e1);
			e1.printStackTrace();
			return false;
		} catch (Exception e1) {
			logger.warning("Unexpected: " + e1);
			e1.printStackTrace();
			return false;
		}
	}

	@Override
	public void fireDragOver(DropTargetDragEvent e) {
		getDrawingView().updateCapturedDraggedNodeImagePosition(e, getDrawingView().getActivePalette().getPaletteView());
	}

	@Override
	public void fireDragExit(DropTargetEvent e) {
		getDrawingView().resetCapturedNode();
	}

	@Override
	public void fireDragFinished(DropTargetEvent e) {
		// Resets the screenshot stored by the editable drawing view (not the palette drawing view).
		getDrawingView().resetCapturedNode();
	}

	@Override
	public boolean performDrop(DropTargetDropEvent e) {
		Object data = getTransferData(e);

		if (data instanceof TransferedPaletteElement) {

			try {
				PaletteElement element = ((TransferedPaletteElement) data).getPaletteElement();
				if (element == null) {
					return false;
				}
				DrawingTreeNode<?, ?> focused = getFocusedObject(e);
				if (focused == null) {
					return false;
				}
				// OK, let's got for the drop
				Component targetComponent = e.getDropTargetContext().getComponent();
				Point pt = e.getLocation();
				DianaPoint modelLocation = new DianaPoint();
				if (targetComponent instanceof DianaView) {
					pt = DianaUtils.convertPoint(((DianaView<?, ?>) targetComponent).getNode(), pt, focused,
							((DianaView<?, ?>) targetComponent).getScale());
					modelLocation.x = pt.x / ((DianaView<?, ?>) targetComponent).getScale();
					modelLocation.y = pt.y / ((DianaView<?, ?>) targetComponent).getScale();
					modelLocation.x -= ((TransferedPaletteElement) data).getOffset().x;
					modelLocation.y -= ((TransferedPaletteElement) data).getOffset().y;
				}
				else {
					modelLocation.x -= ((TransferedPaletteElement) data).getOffset().x;
					modelLocation.y -= ((TransferedPaletteElement) data).getOffset().y;
				}

				// System.out.println("node was: " + ((DianaView<?, ?>) targetComponent).getNode());
				// System.out.println("element: " + element);
				modelLocation.x += ShapeNodeImpl.DEFAULT_BORDER_LEFT;
				modelLocation.y += ShapeNodeImpl.DEFAULT_BORDER_TOP;

				return element.elementDragged(focused, modelLocation);

			} catch (Exception e1) {
				return false;
			}

		}
		return false;
	}

}
