package org.openflexo.diana.vaadin.graphics;

import org.openflexo.diana.vaadin.view.VDrawingView;
import org.openflexo.diana.vaadin.view.VFGEView;
import org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview.VDrawingViewClientRpc;
import org.openflexo.fge.Drawing.RootNode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.graphics.FGEDrawingDecorationGraphics;
import org.openflexo.fge.graphics.FGEDrawingGraphics;

public class VDianaDrawingGraphics extends VDianaGraphics implements FGEDrawingGraphics {

	private VDianaDrawingDecorationGraphics drawingDecorationGraphics;
	
	public <O> VDianaDrawingGraphics(RootNode<O> rootNode, VDrawingView<O> view) {
		super(rootNode, view);
		drawingDecorationGraphics = new VDianaDrawingDecorationGraphics(rootNode, view);
		//System.out.print("DDDDDDDD in the VDianaDrawingGraphcis the rpc is  " + rpc);
	}

	@Override
	public FGEDrawingDecorationGraphics getDrawingDecorationGraphics() {
		return drawingDecorationGraphics;
	}
	
	@Override 
	public DrawingGraphicalRepresentation getGraphicalRepresentation() {
		return (DrawingGraphicalRepresentation) super.getGraphicalRepresentation();
	}
	
	/**
	 * 
	 * @param graphics2D
	 * @param controller
	 */
	public void createGraphics() {
		super.createGraphics();
		// used to draw the graphics
		drawingDecorationGraphics.createGraphics();
	}

}