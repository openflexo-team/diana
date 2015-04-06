package org.openflexo.diana.vaadin.graphics;

import java.util.logging.Logger;

import org.openflexo.diana.vaadin.view.VConnectorView;
import org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview.VDrawingViewClientRpc;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.connectors.ConnectorSymbol;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.fge.graphics.FGESymbolGraphics;

public class VDianaConnectorGraphics extends VDianaGraphics implements FGEConnectorGraphics {

	private static final Logger logger = Logger.getLogger(VDianaConnectorGraphics.class.getPackage().getName());

	private final VDianaConnectorDecorationGraphics ConnectorDecorationGraphics;
	
	public <O> VDianaConnectorGraphics(ConnectorNode<O> node, VConnectorView<O> view) {
		super(node, view);
		ConnectorDecorationGraphics= new VDianaConnectorDecorationGraphics(node, view);	
	}
	@Override
	public ConnectorGraphicalRepresentation getGraphicalRepresentation() {
		return (ConnectorGraphicalRepresentation) super.getGraphicalRepresentation();
	}

	/**
	 * 
	 * @param point
	 * @param symbol
	 * @param size
	 * @param angle
	 *            in radians
	 */
	@Override
	public void drawSymbol(FGEPoint point, ConnectorSymbol symbol, double size, double angle) {
		drawSymbol(point.x, point.y, symbol, size, angle);
	}

	@Override
	public FGESymbolGraphics getSymbolGraphics() {
		// TODO Auto-generated method stub
		return null;
	}

	// need to implement to factory the connector node
	@Override
	public void drawSymbol(double x, double y, ConnectorSymbol symbol,
			double size, double angle) {
		beginPath();
		moveTo(x,y);
		lineTo(x,y);	
	}
	
	public void beginPath(){
		Delegate.rpc.beginPath();
	}
	
	public void moveTo(double x, double y){
		Delegate.rpc.moveTo(x, y);
	}
	
	public void lineTo(double x, double y){
		Delegate.rpc.lineTo(x, y);
	}

}

