package org.openflexo.diana.swing.control.tools;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.logging.Level;

import javax.swing.JComponent;

import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.PaletteElement;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.impl.ShapeNodeImpl;
import org.openflexo.diana.swing.control.JFocusRetriever;
import org.openflexo.diana.swing.control.tools.JDianaPalette.PaletteElementTransferable;
import org.openflexo.diana.swing.control.tools.JDianaPalette.TransferedPaletteElement;
import org.openflexo.diana.swing.view.JDianaView;
import org.openflexo.diana.swing.view.JDrawingView;
import org.openflexo.diana.view.DianaView;

/**
 * An implementation of a {@link DropTargetListener} defined for a {@link JDianaView}
 * 
 * @see java.awt.dnd.DropTargetListener
 * @see java.awt.dnd.DropTarget
 */
public class DianaViewDropListener implements DropTargetListener {

	private final int acceptableActions = DnDConstants.ACTION_COPY | DnDConstants.ACTION_MOVE;
	private final JDianaView<?, ?> dropContainer;
	private final AbstractDianaEditor<?, ?, ?> dianaEditor;

	public DianaViewDropListener(JDianaView<?, ?> dropContainer, AbstractDianaEditor<?, ?, ?> dianaEditor) {
		super();
		this.dropContainer = dropContainer;
		this.dianaEditor = dianaEditor;
	}

	/**
	 * Delete this listener
	 */
	public void delete() {

	}

	/**
	 * Return the {@link JComponent} in which this {@link DropTargetListener} has been registered
	 * 
	 * @return
	 */
	public JDianaView<?, ?> getDropContainer() {
		return dropContainer;
	}

	/**
	 * Return DIANA editor related to this {@link DropTargetListener}
	 * 
	 * @return
	 */
	public AbstractDianaEditor<?, ?, ?> getDianaEditor() {
		return dianaEditor;
	}

	/**
	 * Called by isDragOk Checks to see if the flavor drag flavor is acceptable
	 * 
	 * @param e
	 *            the DropTargetDragEvent object
	 * @return whether the flavor is acceptable
	 */
	private boolean isDragFlavorSupported(DropTargetDragEvent e) {
		boolean ok = false;
		if (e.isDataFlavorSupported(PaletteElementTransferable.defaultFlavor())) {
			ok = true;
		}
		return ok;
	}

	/**
	 * Called by drop Checks the flavors and operations
	 * 
	 * @param e
	 *            the DropTargetDropEvent object
	 * @return the chosen DataFlavor or null if none match
	 */
	private DataFlavor chooseDropFlavor(DropTargetDropEvent e) {
		if (e.isLocalTransfer() == true && e.isDataFlavorSupported(PaletteElementTransferable.defaultFlavor())) {
			return PaletteElementTransferable.defaultFlavor();
		}
		return null;
	}

	/**
	 * Called by dragEnter and dragOver Checks the flavors and operations
	 * 
	 * @param e
	 *            the event object
	 * @return whether the flavor and operation is ok
	 */
	private boolean isDragOk(DropTargetDragEvent e) {

		if (isDragFlavorSupported(e) == false) {
			return false;
		}

		int da = e.getDropAction();
		// we're saying that these actions are necessary
		if ((da & acceptableActions) == 0) {
			return false;
		}

		try {
			PaletteElement element = ((TransferedPaletteElement) e.getTransferable()
					.getTransferData(PaletteElementTransferable.defaultFlavor())).getPaletteElement();
			if (element == null) {
				return false;
			}
			DrawingTreeNode<?, ?> focused = getFocusedObject(e);
			if (focused == null) {
				return false;
			}
			return element.acceptDragging(focused);

		} catch (UnsupportedFlavorException e1) {
			JDianaPalette.logger.warning("Unexpected: " + e1);
			e1.printStackTrace();
			return false;
		} catch (IOException e1) {
			JDianaPalette.logger.warning("Unexpected: " + e1);
			e1.printStackTrace();
			return false;
		} catch (Exception e1) {
			JDianaPalette.logger.warning("Unexpected: " + e1);
			e1.printStackTrace();
			return false;
		}
	}

	/**
	 * start "drag under" feedback on component invoke acceptDrag or rejectDrag based on isDragOk
	 * 
	 * @param e
	 */
	@Override
	public void dragEnter(DropTargetDragEvent e) {
		if (!isDragOk(e)) {
			// DropLabel.this.borderColor=Color.red;
			// showBorder(true);
			e.rejectDrag();
			return;
		}
		e.acceptDrag(e.getDropAction());
	}

	/**
	 * continue "drag under" feedback on component invoke acceptDrag or rejectDrag based on isDragOk
	 * 
	 * @param e
	 */
	@Override
	public void dragOver(DropTargetDragEvent e) {
		if (isDragFlavorSupported(e)) {
			getDrawingView().updateCapturedDraggedNodeImagePosition(e, getDrawingView().getActivePalette().getPaletteView());
		}
		if (!isDragOk(e)) {
			if (getDianaEditor().getDragSourceContext() == null) {
				// logger.warning("dragSourceContext should NOT be null for " + getPalette().getTitle()
				// + Integer.toHexString(JDianaPalette.this.hashCode()) + " of " + JDianaPalette.this.getClass().getName());
			}
			else {
				getDianaEditor().getDragSourceContext().setCursor(JDianaPalette.dropKO);
			}
			e.rejectDrag();
			return;
		}
		if (getDianaEditor().getDragSourceContext() == null) {
			// logger.warning("dragSourceContext should NOT be null");
		}
		else {
			getDianaEditor().getDragSourceContext().setCursor(JDianaPalette.dropOK);
		}
		e.acceptDrag(e.getDropAction());
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent e) {
		if (!isDragOk(e)) {
			e.rejectDrag();
			return;
		}
		e.acceptDrag(e.getDropAction());
	}

	@Override
	public void dragExit(DropTargetEvent e) {
		// interface method
		getDrawingView().resetCapturedNode();
	}

	/**
	 * perform action from getSourceActions on the transferrable invoke acceptDrop or rejectDrop invoke dropComplete if its a local (same
	 * JVM) transfer, use StringTransferable.localStringFlavor find a match for the flavor check the operation get the transferable
	 * according to the chosen flavor do the transfer
	 * 
	 * @param e
	 */
	@Override
	public void drop(DropTargetDropEvent e) {
		try {
			DataFlavor chosen = chooseDropFlavor(e);
			if (chosen == null) {
				e.rejectDrop();
				return;
			}

			// the actions that the source has specified with DragGestureRecognizer
			int sa = e.getSourceActions();

			if ((sa & acceptableActions) == 0) {
				e.rejectDrop();
				return;
			}

			Object data = null;

			try {

				/*
				 * the source listener receives this action in dragDropEnd. if the
				 * action is DnDConstants.ACTION_COPY_OR_MOVE then the source
				 * receives MOVE!
				 */

				data = e.getTransferable().getTransferData(chosen);
				if (JDianaPalette.logger.isLoggable(Level.FINE)) {
					JDianaPalette.logger.fine("data is a " + data.getClass().getName());
				}
				if (data == null) {
					throw new NullPointerException();
				}
			} catch (Throwable t) {
				if (JDianaPalette.logger.isLoggable(Level.WARNING)) {
					JDianaPalette.logger.warning("Couldn't get transfer data: " + t.getMessage());
				}
				t.printStackTrace();
				e.dropComplete(false);
				return;
			}

			if (data instanceof TransferedPaletteElement) {

				try {
					PaletteElement element = ((TransferedPaletteElement) data).getPaletteElement();
					if (element == null) {
						e.rejectDrop();
						return;
					}
					DrawingTreeNode<?, ?> focused = getFocusedObject(e);
					if (focused == null) {
						e.rejectDrop();
						return;
					}
					// OK, let's got for the drop
					if (element.acceptDragging(focused)) {
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

						if (element.elementDragged(focused, modelLocation)) {
							e.acceptDrop(acceptableActions);
							e.dropComplete(true);
							JDianaPalette.logger.info("OK, valid drop, proceed");
							return;
						}
						else {
							e.rejectDrop();
							e.dropComplete(false);
							return;
						}
					}

				} catch (Exception e1) {
					JDianaPalette.logger.warning("Unexpected: " + e1);
					e1.printStackTrace();
					e.rejectDrop();
					e.dropComplete(false);
					return;
				}

			}

			e.rejectDrop();
			e.dropComplete(false);
			return;
		} finally {
			// Resets the screenshot stored by the editable drawing view (not the palette drawing view).
			getDrawingView().resetCapturedNode();
		}
	}

	private JFocusRetriever getFocusRetriever() {
		if (getDropContainer() instanceof DianaView) {
			return getDrawingView().getFocusRetriever();
		}
		return null;
	}

	private DianaView<?, ?> getDianaView() {
		if (getDropContainer() instanceof DianaView) {
			return getDropContainer();
		}
		return null;
	}

	public DrawingTreeNode<?, ?> getFocusedObject(DropTargetDragEvent event) {
		if (getFocusRetriever() != null) {
			DrawingTreeNode<?, ?> returned = getFocusRetriever().getFocusedObject(event);
			if (returned == null) {
				// Since we are in a DianaView, a null value indicates that we are on the Drawing view
				return getDianaView().getDrawingView().getDrawing().getRoot();
			}
			return returned;
		}
		// No focus retriever: we are not in a DianaView....
		return null;
	}

	public DrawingTreeNode<?, ?> getFocusedObject(DropTargetDropEvent event) {
		if (getFocusRetriever() != null) {
			DrawingTreeNode<?, ?> returned = getFocusRetriever().getFocusedObject(event);
			if (returned == null) {
				// Since we are in a DianaView, a null value indicates that we are on the Drawing view
				return getDianaView().getDrawingView().getDrawing().getRoot();
			}
			return returned;
		}
		// No focus retriever: we are not in a DianaView....
		return null;
	}

	public JDrawingView<?> getDrawingView() {
		return (JDrawingView<?>) getDianaEditor().getDrawingView();
	}

}
