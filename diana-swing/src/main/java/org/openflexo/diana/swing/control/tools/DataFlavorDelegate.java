package org.openflexo.diana.swing.control.tools;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.logging.Logger;

import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.swing.control.JFocusRetriever;
import org.openflexo.diana.swing.view.JDrawingView;
import org.openflexo.diana.view.DianaView;
import org.openflexo.diana.view.DrawingView;

/**
 * Abstract base implementation of a delegate to the DataViewDropListener<br>
 * Dedicated to a given {@link DataFlavor}
 * 
 * 
 * @author sylvain
 *
 */
public abstract class DataFlavorDelegate {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DataFlavorDelegate.class.getPackage().getName());

	private DianaViewDropListener dropListener;

	public DataFlavorDelegate(DianaViewDropListener dropListener) {
		this.dropListener = dropListener;
	}

	/**
	 * Return {@link DataFlavor} beeing managed by this delegate
	 * 
	 * @return
	 */
	public abstract DataFlavor getDataFlavor();

	/**
	 * Return acceptable actions
	 * 
	 * @return
	 */
	public abstract int getAcceptableActions();

	/**
	 * Return owner
	 * 
	 * @return
	 */
	public DianaViewDropListener getDropListener() {
		return dropListener;
	}

	/**
	 * Return boolean indicating if supplied {@link DropTargetDragEvent} maps a possible drag
	 * 
	 * @param e
	 * @return
	 */
	public abstract boolean isDragOk(DropTargetDragEvent e);

	/**
	 * Called while dragOver() in parent DropListener<br>
	 * Must be overriden when required
	 * 
	 * @param e
	 */
	public void fireDragOver(DropTargetDragEvent e) {
	}

	/**
	 * Called while dragExit() in parent DropListener<br>
	 * Must be overriden when required
	 * 
	 * @param e
	 */
	public void fireDragExit(DropTargetEvent e) {
	}

	/**
	 * Called while drag finished in parent DropListener<br>
	 * Must be overriden when required
	 * 
	 * @param e
	 */
	public void fireDragFinished(DropTargetEvent e) {
	}

	/**
	 * Return transfered object of related {@link DataFlavor}
	 * 
	 * @param e
	 * @return
	 */
	protected final Object getTransferData(DropTargetDropEvent e) {
		try {
			Object returned = e.getTransferable().getTransferData(getDataFlavor());
			if (returned != null) {
				return returned;
			}
		} catch (UnsupportedFlavorException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		throw new NullPointerException();
	}

	public void elementDragged() {

	}

	/**
	 * Perform the drop
	 * 
	 * @param e
	 * @return
	 */
	public abstract boolean performDrop(DropTargetDropEvent e);

	/**
	 * drop() delegate implementation<br>
	 * Rely on {@link #performDrop(DropTargetDropEvent)}
	 * 
	 * @param e
	 */
	public final void drop(DropTargetDropEvent e) {
		try {
			if (performDrop(e)) {
				e.acceptDrop(getAcceptableActions());
				e.dropComplete(true);
				JDianaPalette.logger.info("OK, valid drop, proceed");
				return;
			}
			else {
				e.rejectDrop();
				e.dropComplete(false);
				return;
			}
		} finally {
			fireDragFinished(e);
		}
	}

	/**
	 * Return focus retriever of related {@link DrawingView}
	 * 
	 * @return
	 */
	protected JFocusRetriever getFocusRetriever() {
		if (getDropListener().getDropContainer() instanceof DianaView) {
			return getDrawingView().getFocusRetriever();
		}
		return null;
	}

	/**
	 * Return {@link DianaView} parent {@link DropTargetListener} is related to
	 * 
	 * @return
	 */
	protected DianaView<?, ?> getDianaView() {
		if (getDropListener().getDropContainer() instanceof DianaView) {
			return getDropListener().getDropContainer();
		}
		return null;
	}

	/**
	 * Return DIANA editor related to this {@link DropTargetListener}
	 * 
	 * @return
	 */
	protected AbstractDianaEditor<?, ?, ?> getDianaEditor() {
		return getDropListener().getDianaEditor();
	}

	protected DrawingTreeNode<?, ?> getFocusedObject(DropTargetDragEvent event) {
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

	protected DrawingTreeNode<?, ?> getFocusedObject(DropTargetDropEvent event) {
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

	protected JDrawingView<?> getDrawingView() {
		return (JDrawingView<?>) getDropListener().getDianaEditor().getDrawingView();
	}

}
