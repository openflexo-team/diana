package org.openflexo.diana.vaadin.client.vdianaviewercomponentsvg;

import org.openflexo.diana.vaadin.VDianaViewerComponentSVG;
import org.vaadin.gwtgraphics.client.DrawingArea;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;

@Connect(VDianaViewerComponentSVG.class)
public class VDianaViewerComponentSVGConnector extends AbstractComponentConnector {

	VDianaViewerComponentSVGServerRpc rpc = RpcProxy.create(VDianaViewerComponentSVGServerRpc.class, this);

	public VDianaViewerComponentSVGConnector() {
		registerRpc(VDianaViewerComponentSVGClientRpc.class, new VDianaViewerComponentSVGClientRpc() {

			@Override
			public void drawRect(double x, double y, double width, double height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawPoint(double x, double y) {
				// TODO Auto-generated method stub

			}

		});

		// TODO ServerRpc usage example, do something useful instead
		getWidget().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final MouseEventDetails mouseDetails = MouseEventDetailsBuilder.buildMouseEventDetails(event.getNativeEvent(),
						getWidget().getElement());
				rpc.clicked(mouseDetails);
			}
		});

	}

	@Override
	protected Widget createWidget() {
		return GWT.create(DrawingArea.class);
	}

	@Override
	public DrawingArea getWidget() {
		return (DrawingArea) super.getWidget();
	}

	@Override
	public VDianaViewerComponentSVGState getState() {
		return (VDianaViewerComponentSVGState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

		/*// TODO do something useful
		final String text = getState().text;
		getWidget().setText(text);*/
	}

}
