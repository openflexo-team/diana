package org.openflexo.diana.vaadin.client.vdianaviewercomponent;

import org.openflexo.fge.geom.FGEPoint;

public class VDianaViewerComponentClientRpcImpl implements VDianaViewerComponentClientRpc {
	private final VDianaViewerComponentConnector connector;

	public VDianaViewerComponentClientRpcImpl(VDianaViewerComponentConnector connector) {
		this.connector = connector;
	}

	@Override
	public void drawRect(final double x, final double y, final double width, final double height) {
		connector.getGraphics().drawRect(x, y, width, height);
	}

	@Override
	public void drawPoint(double x, double y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawPoint(FGEPoint point) {
		// TODO Auto-generated method stub

	}
}
