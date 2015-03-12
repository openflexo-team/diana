package org.openflexo.diana.vaadin.graphics;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.graphics.FGEShapeDecorationGraphics;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.view.FGEView;

public class VFGEShapeGraphics extends VFGEGraphics implements FGEShapeGraphics{

	public VFGEShapeGraphics(DrawingTreeNode<?, ?> dtn, FGEView<?, ?> view) {
		super(dtn, view);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return (ShapeGraphicalRepresentation) super.getGraphicalRepresentation();
	}

	@Override
	public ShapeNode<?> getNode() {
		return (ShapeNode<?>) super.getNode();
	}
	
	@Override
	public FGEShapeDecorationGraphics getShapeDecorationGraphics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void paintShadow() {
		// TODO Auto-generated method stub
		
	}

}
