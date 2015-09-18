package org.openflexo.diana.vaadin.client.vdianaviewercomponent;

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
	public void drawRoundRect(double x, double y, double width, double height, double arcwidth, double archeight) {
		connector.getGraphics().drawRoundRect(x, y, width, height, arcwidth, archeight);
	}

	/*@Override
	public void drawRect(FGERectangle rectangle) {
		drawRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
	}*/
}
