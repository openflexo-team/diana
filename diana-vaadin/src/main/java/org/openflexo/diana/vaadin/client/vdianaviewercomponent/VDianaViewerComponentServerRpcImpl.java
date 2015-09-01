package org.openflexo.diana.vaadin.client.vdianaviewercomponent;

import com.vaadin.shared.MouseEventDetails;

public class VDianaViewerComponentServerRpcImpl implements VDianaViewerComponentServerRpc {

	private int clickCount = 0;

	@Override
	public void clicked(MouseEventDetails mouseDetails) {
		// nag every 5:th click using RPC
		if (++clickCount % 5 == 0) {
			// getRpcProxy(VDianaViewerComponentClientRpc.class).alert("Ok, that's enough!");
		}
		// update shared state
		// getState().text = "You have clicked " + clickCount + " times";
	}

	@Override
	public void message(String string) {
		System.out.println(string);
	}

}
