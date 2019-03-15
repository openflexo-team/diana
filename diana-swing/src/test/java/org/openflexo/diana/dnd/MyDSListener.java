package org.openflexo.diana.dnd;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

/**
 * DSListener a listener that will track the state of the DnD operation
 * 
 * @see java.awt.dnd.DragSourceListener
 * @see java.awt.dnd.DragSource
 * @see java.awt.datatransfer.StringSelection
 */
public class MyDSListener implements DragSourceListener {

	/**
	 * @param e
	 *            the event
	 */
	@Override
	public void dragDropEnd(DragSourceDropEvent e) {
		// Resets the screenshot stored by the palette view.
		/*if (getDrawingView() != null) {
		getDrawingView().resetCapturedNode();
		}*/
		if (!e.getDropSuccess()) {
			System.out.println("Dropping was not successful");
			return;
		}
		/*
		 * the dropAction should be what the drop target specified in
		 * acceptDrop
		 */
		// this is the action selected by the drop target
		if (e.getDropAction() == DnDConstants.ACTION_MOVE) {

			System.out.println("dragDropEnd !!!");
			// setName("");

			System.out.println("OK on fait le drop !!!!");
		}
	}

	/**
	 * @param e
	 *            the event
	 */
	@Override
	public void dragEnter(DragSourceDragEvent e) {
		DragSourceContext context = e.getDragSourceContext();
		// intersection of the users selected action, and the source and
		// target actions
		int myaction = e.getDropAction();
		if ((myaction /*& dragAction*/) != 0) {
			context.setCursor(DragSource.DefaultCopyDrop);
		}
		else {
			context.setCursor(DragSource.DefaultCopyNoDrop);
		}
	}

	/**
	 * @param e
	 *            the event
	 */
	@Override
	public void dragOver(DragSourceDragEvent e) {
		// interface
		// getController().getPalette().setDragSourceContext(e.getDragSourceContext());

		System.out.println("Hop, dragOver with " + e.getDragSourceContext());
		// Thread.dumpStack();

		// getEditor().setDragSourceContext(e.getDragSourceContext());
	}

	/**
	 * @param e
	 *            the event
	 */
	@Override
	public void dragExit(DragSourceEvent e) {
		// interface
	}

	/**
	 * for example, press shift during drag to change to a link action
	 * 
	 * @param e
	 *            the event
	 */
	@Override
	public void dropActionChanged(DragSourceDragEvent e) {
		DragSourceContext context = e.getDragSourceContext();
		context.setCursor(DragSource.DefaultCopyNoDrop);
	}
}