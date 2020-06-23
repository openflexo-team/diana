package org.openflexo.diana.dnd;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTargetListener;
import java.awt.event.InputEvent;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel.BrowserCell;

/**
 * Swing implementation of a {@link JTree} supporting drag&drop<br>
 * It suppose that some elements of the tree may be dragged somewhere
 * 
 * @see DropTargetListener
 * 
 * @author sylvain
 */
@SuppressWarnings("serial")
public class MyDnDJTree extends JTree implements DragGestureListener, TreeSelectionListener /*, BindingEvaluationContext*/ {

	/** Variables needed for DnD */
	protected DragSource dragSource = null;

	/** Stores the selected node info */
	protected TreePath selectedTreePath = null;
	private BrowserCell selectedBrowserCell = null;

	/** The browser cell beeing currently dragged */
	protected BrowserCell draggedBrowserCell;

	/** The browser cell destination of the drop: the one on we target */
	protected BrowserCell targetBrowserCell;

	public MyDnDJTree(TreeModel treeModel) {
		super(treeModel);

		addTreeSelectionListener(this);

	}

	/** Returns The selected node */
	public Transferable getSelectedBrowserCell() {

		System.out.println("ce qui est selectionne: " + getLastSelectedPathComponent());
		System.out.println("of " + getLastSelectedPathComponent().getClass());

		if (getLastSelectedPathComponent() instanceof DefaultMutableTreeNode) {
			return (Transferable) ((DefaultMutableTreeNode) getLastSelectedPathComponent()).getUserObject();
		}
		return null;
	}

	/** TreeSelectionListener - sets selected node */
	@Override
	public void valueChanged(TreeSelectionEvent evt) {
		selectedTreePath = evt.getNewLeadSelectionPath();
		if (selectedTreePath == null) {
			selectedBrowserCell = null;
			return;
		}
		Object lastPathComponent = selectedTreePath.getLastPathComponent();
		if (lastPathComponent instanceof BrowserCell) {
			selectedBrowserCell = (BrowserCell) lastPathComponent;
		}
	}

	/*	@Override
		public Object getValue(BindingVariable variable) {
			if (variable.getVariableName().equals("dragged")) {
				// System.out.println("dragged is" + draggedBrowserCell.getRepresentedObject());
				return draggedBrowserCell.getRepresentedObject();
			}
			else if (variable.getVariableName().equals("target")) {
				// System.out.println(" target is " + draggedBrowserCell.getRepresentedObject());
				return targetBrowserCell.getRepresentedObject();
			}
			else {
				return widget.getBindingEvaluationContext().getValue(variable);
			}
		}*/

	public DragSource getDragSource() {
		return dragSource;
	}

	private DragGestureListener dgListener;

	/**
	 * Register {@link DragGestureListener} for this JTree
	 * 
	 * @param dgListener
	 */
	public void registerDragGestureListener(DragGestureListener dgListener) {

		/*if (!getWidget().getComponent().getAllowsExternalDragAndDrop()) {
			logger.warning("External drag and drop not supported");
			return;
		}*/

		System.out.println("Hop ici");

		this.dgListener = dgListener;
		dragSource = DragSource.getDefaultDragSource();

		DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(this, // DragSource
				DnDConstants.ACTION_MOVE, // specifies valid actions
				this // DragGestureListener
		);

		dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);

	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {

		System.out.println("Hop ????");

		dgListener.dragGestureRecognized(dge);
	}

}
