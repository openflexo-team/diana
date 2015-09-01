package org.openflexo.diana.vaadin.client.vdianaviewercomponent;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

public interface VDianaViewerComponentServerRpc extends ServerRpc {

	// TODO example API
	public void clicked(MouseEventDetails mouseDetails);

	public void message(String string);

}
