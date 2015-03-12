package org.openflexo.diana.vaadin.graphics;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.graphics.FGEDrawingDecorationGraphics;
import org.openflexo.fge.graphics.FGEDrawingGraphics;
import org.openflexo.fge.view.FGEView;

public class VFGEDrawingGraphics extends VFGEGraphics implements FGEDrawingGraphics {

	public VFGEDrawingGraphics(DrawingTreeNode<?, ?> dtn, FGEView<?, ?> view) {
		super(dtn, view);
		// TODO Auto-generated constructor stub
	}

	@Override
	public FGEDrawingDecorationGraphics getDrawingDecorationGraphics() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override 
	public DrawingGraphicalRepresentation getGraphicalRepresentation() {
		return (DrawingGraphicalRepresentation) super.getGraphicalRepresentation();
	}

}
