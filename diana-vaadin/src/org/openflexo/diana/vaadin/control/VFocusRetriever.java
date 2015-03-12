package org.openflexo.diana.vaadin.control;


import java.util.logging.Logger;

import org.openflexo.diana.vaadin.view.VDrawingView;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.control.DianaInteractiveViewer;
import com.vaadin.ui.Component;


/**
 * Utility class used in a general context to retrieve the focus owner in a graphical context.<br>
 * 
 * The policy is to return the closest connector or geometrical object from the cursor, or the top-most shape where the cursor is located
 * in.<br>
 * Manage focusable properties, as well as layers.
 * 
 * @author Peiqi SHI
 * 
 */

public class VFocusRetriever {
	private static final Logger logger = Logger.getLogger(VFocusRetriever.class.getPackage().getName());

	private VDrawingView<?> drawingView;

	public VFocusRetriever(VDrawingView<?> aDrawingView) {
		drawingView = aDrawingView;
	}
	private boolean cursorChanged = false;

	private Component cursoredComponent;

	private void resetCursorIfRequired() {
		if (cursorChanged) {
			cursorChanged = false;
		}
	}
	
	public DianaInteractiveViewer<?, ?, ?> getController() {
		return (DianaInteractiveViewer<?, ?, ?>) drawingView.getController();
	}
	/*public boolean focusOnFloatingLabel(DrawingTreeNode<?, ?> node, MouseEvent event) {
		return focusOnFloatingLabel(node, (Component) event.getSource(), event.getPoint());
	}

	private boolean focusOnFloatingLabel(DrawingTreeNode<?, ?> node, Component eventSource, Point eventLocation) {
		// if (!graphicalRepresentation.hasText()) return false;

		if (node instanceof GeometricNode) {
			return false;
		}
		
		VFGEView<?, ?> view = drawingView.viewForNode(node); 
		if (view == null) {
			logger.warning("Unexpected null view for node " + node + " AbstractDianaEditor=" + getController() + " JDrawingView="
					+ drawingView);
		}
		return false;
	
	}*/

}
