package org.openflexo.diana.vaadin.client.vdianaviewercomponent;

import org.openflexo.fge.geom.FGEPoint;

import com.vaadin.shared.communication.ClientRpc;

/* Separate in several RPC
 * Decide what to do with FGEStuff
 */
public interface VDianaViewerComponentClientRpc extends ClientRpc {
	public void drawRect(double x, double y, double width, double height);

	public void drawPoint(double x, double y);

	public void drawPoint(FGEPoint point);
}
