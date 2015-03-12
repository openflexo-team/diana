package org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

public interface VDrawingViewServerRpc extends ServerRpc {

	public void clicked(MouseEventDetails mouseDetails);
	public void pressed(MouseEventDetails mouseDetails);

}
