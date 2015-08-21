package org.openflexo.diana.vaadin.client;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

public interface CanvasServerRpc extends ServerRpc {
	public void clicked(MouseEventDetails med);

	public void pressed(MouseEventDetails med);

	public void imagesLoaded();
	
	
}
