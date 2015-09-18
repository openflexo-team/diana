package org.openflexo.diana.vaadin;

import org.openflexo.diana.vaadin.client.vdianaviewercomponentsvg.VDianaViewerComponentSVGClientRpc;
import org.openflexo.diana.vaadin.client.vdianaviewercomponentsvg.VDianaViewerComponentSVGServerRpc;
import org.openflexo.diana.vaadin.client.vdianaviewercomponentsvg.VDianaViewerComponentSVGState;

import com.vaadin.shared.MouseEventDetails;

public class VDianaViewerComponentSVG<M> extends com.vaadin.ui.Embedded {

	private final VDianaViewer<M> viewer;
	private final VDianaViewerComponentSVGClientRpc rpc;

	public VDianaViewerComponentSVG(VDianaViewer<M> viewer) {
		this.viewer = viewer;
		rpc = getRpcProxy(VDianaViewerComponentSVGClientRpc.class);
		registerRpc(new VDianaViewerComponentSVGServerRpc() {

			@Override
			public void clicked(MouseEventDetails mouseDetails) {
				// TODO Auto-generated method stub

			}
		});
	}

	public VDianaViewerComponentSVGClientRpc getRpc() {
		return rpc;
	}

	@Override
	public VDianaViewerComponentSVGState getState() {
		return (VDianaViewerComponentSVGState) super.getState();
	}
}
