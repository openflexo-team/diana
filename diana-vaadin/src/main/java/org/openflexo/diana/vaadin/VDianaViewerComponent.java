package org.openflexo.diana.vaadin;

import org.openflexo.diana.vaadin.client.vdianaviewercomponent.VDianaViewerComponentClientRpc;
import org.openflexo.diana.vaadin.client.vdianaviewercomponent.VDianaViewerComponentServerRpcImpl;
import org.openflexo.diana.vaadin.client.vdianaviewercomponent.VDianaViewerComponentState;

public class VDianaViewerComponent<M> extends com.vaadin.ui.AbstractComponent {

	private final VDianaViewer<M> viewer;
	private final VDianaViewerComponentClientRpc rpc;

	/*private final VDianaViewerComponentServerRpc rpc = new VDianaViewerComponentServerRpc() {
		private int clickCount = 0;
	
		@Override
		public void clicked(MouseEventDetails mouseDetails) {
			// nag every 5:th click using RPC
			if (++clickCount % 5 == 0) {
				getRpcProxy(VDianaViewerComponentClientRpc.class).alert("Ok, that's enough!");
			}
			// update shared state
			getState().text = "You have clicked " + clickCount + " times";
		}
	};*/

	public VDianaViewerComponent(VDianaViewer<M> viewer) {
		registerRpc(new VDianaViewerComponentServerRpcImpl());
		rpc = getRpcProxy(VDianaViewerComponentClientRpc.class);
		this.viewer = viewer;
	}

	public VDianaViewerComponentClientRpc getRpc() {
		return rpc;
	}

	@Override
	public VDianaViewerComponentState getState() {
		return (VDianaViewerComponentState) super.getState();
	}
}
