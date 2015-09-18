package org.openflexo.diana.vaadin.client.vdianaviewercomponentsvg;

import com.vaadin.shared.communication.ClientRpc;

public interface VDianaViewerComponentSVGClientRpc extends ClientRpc {

	public void drawRect(double x, double y, double width, double height);

	public void drawPoint(double x, double y);

	// public void drawRoundRect(double x, double y, double width, double height, double arcwidth, double archeight);

}
