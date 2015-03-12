package org.openflexo.diana.vaadin.graphics;

import org.openflexo.diana.vaadin.view.VConnectorView;
import org.openflexo.diana.vaadin.view.VFGEView;
import org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview.VDrawingViewClientRpc;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.FGESymbolGraphics;

public class VDianaConnectorDecorationGraphics extends VDianaGraphics implements FGESymbolGraphics {

	public <O> VDianaConnectorDecorationGraphics(ConnectorNode<O> node, VConnectorView<O> view) {
		super(node, view);
		// TODO Auto-generated constructor stub
	}
	
	// Symbol graphics doesn't used normalized coordinates system
	@Override
	public FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y) {
		return new FGEPoint(x, y);
	}


}
