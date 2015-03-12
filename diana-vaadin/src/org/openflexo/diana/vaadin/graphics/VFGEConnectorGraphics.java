package org.openflexo.diana.vaadin.graphics;

import java.awt.Graphics2D;

import org.openflexo.diana.vaadin.view.VConnectorView;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.connectors.ConnectorSymbol;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.fge.graphics.FGESymbolGraphics;
import org.openflexo.fge.view.FGEView;

public class VFGEConnectorGraphics extends VFGEGraphics implements FGEConnectorGraphics {
	
	private final VFGESymbolGraphics symbolGraphics;
	
	public <O> VFGEConnectorGraphics(ConnectorNode<O> node, VConnectorView<O> view)
	{
		super(node, view);
		symbolGraphics = new VFGESymbolGraphics(node, view);
	}


@Override
public ConnectorGraphicalRepresentation getGraphicalRepresentation() {
	return (ConnectorGraphicalRepresentation) super.getGraphicalRepresentation();
}

@Override
public FGESymbolGraphics getSymbolGraphics() {
	// TODO Auto-generated method stub
	return symbolGraphics;
}

@Override
public void drawSymbol(FGEPoint point, ConnectorSymbol symbol, double size,
		double angle) {
	// TODO Auto-generated method stub
	drawSymbol(point.x, point.y, symbol, size, angle);
	
}

@Override
public void drawSymbol(double x, double y, ConnectorSymbol symbol, double size,
		double angle) {
	// TODO Auto-generated method stub
	
}

@Override
public void createGraphics(Graphics2D graphics2D) {
	super.createGraphics(graphics2D);
	symbolGraphics.createGraphics(graphics2D);
}

@Override
public void releaseGraphics() {
	super.releaseGraphics();
	symbolGraphics.releaseGraphics();
	}

}
