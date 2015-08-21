package org.openflexo.fge.vaadin.view;

import org.openflexo.fge.Drawing.RootNode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.graphics.FGEDrawingDecorationGraphics;
import org.openflexo.fge.graphics.FGEDrawingGraphics;

public class VFGEDrawingGraphics extends VFGEGraphics implements FGEDrawingGraphics {

	protected <O> VFGEDrawingGraphics(RootNode<O> rootNode, VDrawingView<O> view) {
		super(rootNode, view);
		// not implemented : JFGEDecorationGraphics
	}

	@Override
	public DrawingGraphicalRepresentation getGraphicalRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FGEDrawingDecorationGraphics getDrawingDecorationGraphics() {
		// TODO Auto-generated method stub
		return null;
	}

}
