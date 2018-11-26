package org.openflexo.diana.swing.control.tools;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.swing.view.JDianaView;

/**
 * An implementation of a {@link DropTargetListener} defined for a {@link JDianaView}
 * 
 * Basically support two flavors:
 * <ul>
 * <li>JDianaPalette.PALETTE_ELEMENT_FLAVOR</li>
 * <li>FIBBrowserModel.BROWSER_CELL_FLAVOR</li>
 * </ul>
 * 
 * @see java.awt.dnd.DropTargetListener
 * @see java.awt.dnd.DropTarget
 */
public class DianaViewDropListener implements DropTargetListener {

	private final JDianaView<?, ?> dropContainer;
	private final AbstractDianaEditor<?, ?, ?> dianaEditor;
	private List<DataFlavorDelegate> flavors;

	public DianaViewDropListener(JDianaView<?, ?> dropContainer, AbstractDianaEditor<?, ?, ?> dianaEditor) {
		super();
		this.dropContainer = dropContainer;
		this.dianaEditor = dianaEditor;
		flavors = new ArrayList<>();
		addFlavor(new PaletteElementDataFlavorDelegate(this));
	}

	public void addFlavor(DataFlavorDelegate flavor) {
		flavors.add(flavor);
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
	 * Return boolean indicating if there is one acceptable flavor
	 * 
	 * @param e
	 *            the DropTargetDragEvent object
	 * @return
	 */
	private boolean isDragFlavorSupported(DropTargetDragEvent e) {
		for (DataFlavorDelegate flavor : flavors) {
			if (e.isDataFlavorSupported(flavor.getDataFlavor())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return acceptable flavor for {@link DropTargetDragEvent} event if there is one, otherwise return null
	 * 
	 * @param e
	 *            the DropTargetDragEvent object
	 * @return
	 */
	private DataFlavorDelegate getDragFlavor(DropTargetDragEvent e) {
		for (DataFlavorDelegate flavor : flavors) {
			if (e.isDataFlavorSupported(flavor.getDataFlavor())) {
				return flavor;
			}
		}
		return null;
	}

	/**
	 * Return acceptable flavor for {@link DropTargetDropEvent} event if there is one, otherwise return null
	 * 
	 * @param e
	 *            the DropTargetDropEvent object
	 * @return
	 */
	private DataFlavorDelegate getDropFlavor(DropTargetDropEvent e) {
		for (DataFlavorDelegate flavor : flavors) {
			if (e.isLocalTransfer() == true && e.isDataFlavorSupported(flavor.getDataFlavor())) {
				return flavor;
			}
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
			System.out.println("DragFlavor not supported");
			return false;
		}

		DataFlavorDelegate flavor = getDragFlavor(e);

		int da = e.getDropAction();
		// we're saying that these actions are necessary
		if ((da & flavor.getAcceptableActions()) == 0) {
			return false;
		}

		return flavor.isDragOk(e);
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

	private DataFlavorDelegate currentFlavor;

	/**
	 * continue "drag under" feedback on component invoke acceptDrag or rejectDrag based on isDragOk
	 * 
	 * @param e
	 */
	@Override
	public void dragOver(DropTargetDragEvent e) {

		DataFlavorDelegate flavor = getDragFlavor(e);

		if (flavor == null) {
			e.rejectDrag();
			return;
		}

		flavor.fireDragOver(e);

		if (!isDragOk(e)) {
			if (getDianaEditor().getDragSourceContext() != null) {
				getDianaEditor().getDragSourceContext().setCursor(JDianaPalette.dropKO);
			}
			e.rejectDrag();
			return;
		}
		if (getDianaEditor().getDragSourceContext() != null) {
			getDianaEditor().getDragSourceContext().setCursor(JDianaPalette.dropOK);
		}
		e.acceptDrag(e.getDropAction());
		currentFlavor = flavor;
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
		if (currentFlavor != null) {
			currentFlavor.fireDragExit(e);
		}
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

		DataFlavorDelegate flavor = getDropFlavor(e);
		flavor.drop(e);

		/*try {
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
		}*/
	}

	/*private JFocusRetriever getFocusRetriever() {
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
	}*/

}
