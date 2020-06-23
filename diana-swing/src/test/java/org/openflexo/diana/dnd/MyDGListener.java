package org.openflexo.diana.dnd;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;

/**
 * DGListener a listener that will start the drag. has access to top level's dsListener and dragSource
 * 
 * @see java.awt.dnd.DragGestureListener
 * @see java.awt.dnd.DragSource
 * @see java.awt.datatransfer.StringSelection
 */
public class MyDGListener implements DragGestureListener {

	private MyDnDJTree sourceComponent;

	public MyDGListener(MyDnDJTree sourceComponent) {
		this.sourceComponent = sourceComponent;
	}

	/**
	 * Start the drag if the operation is ok. uses java.awt.datatransfer.StringSelection to transfer the label's data
	 * 
	 * @param e
	 *            the event object
	 */
	@Override
	public void dragGestureRecognized(DragGestureEvent e) {

		// if the action is ok we go ahead
		// otherwise we punt

		System.out.println("on est la");

		if ((e.getDragAction() & DnDConstants.ACTION_MOVE) == 0) {
			System.out.println("on s'arrete tout de suite");
			return;
			// get the label's text and put it inside a Transferable
			// Transferable transferable = new StringSelection(
			// DragLabel.this.getText() );
		}

		Transferable dragNode = sourceComponent.getSelectedBrowserCell();
		System.out.println("dragNode=" + dragNode);
		if (dragNode != null) {
			// Get the Transferable Object
			// Transferable transferable = (Transferable) dragNode.getUserObject();
			Transferable transferable = dragNode;

			try {
				// initial cursor, transferable, dsource listener
				e.startDrag(TestDND.dropKO, transferable, TestDND.dsListener);
				System.out.println("Starting drag for " + dragNode);
				// getDrawingView().captureDraggedNode(JPaletteElementView.this, e);
			} catch (Exception idoe) {
				System.out.println("Unexpected exception " + idoe);
			}
		}

	}

}