package org.openflexo.diana.vaadin.widgetset.client;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

public interface VaadinServerRpc extends ServerRpc {
	public void clicked(MouseEventDetails med);

	public void pressed(MouseEventDetails med);
}