package org.openflexo.fge.vaadin.view;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.graphics.FGEShapeDecorationGraphics;
import org.openflexo.fge.graphics.FGEShapeGraphics;

public class VFGEShapeGraphics extends VFGEGraphics implements FGEShapeGraphics {

	public <O> VFGEShapeGraphics(ShapeNode<O> node, VShapeView view) {
		super(node, view);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public ShapeNode<?> getNode() {
		// TODO Auto-generated method stub
		return null;
	}

}
