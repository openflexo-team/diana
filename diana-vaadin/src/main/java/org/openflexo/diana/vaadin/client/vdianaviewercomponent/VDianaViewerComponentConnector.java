package org.openflexo.diana.vaadin.client.vdianaviewercomponent;

import org.openflexo.axel.Axel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;

@Connect(org.openflexo.diana.vaadin.VDianaViewerComponent.class)
public class VDianaViewerComponentConnector extends AbstractComponentConnector {

	VDianaViewerComponentServerRpc rpc = RpcProxy.create(VDianaViewerComponentServerRpc.class, this);
	private final DistantGraphics graphics;
	// private final List<Command> commands;

	public VDianaViewerComponentConnector() {
		registerRpc(VDianaViewerComponentClientRpc.class, new VDianaViewerComponentClientRpcImpl(this));
		graphics = new DistantGraphicsImpl(getWidget().getContext2d());

		Axel a = new Axel(90, 60);
		/*rpc.message(String.valueOf(a.getX()));*/

		graphics.drawRect(2, 2, a.getX(), 60);

		/*FGERectangle rect = new FGERectangle(20, 30, 60, 50);
		graphics.drawRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
		*/
		// commands = new LinkedList<>();
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

	public DistantGraphics getGraphics() {
		return graphics;
	}

	@Override
	protected Widget createWidget() {
		return Canvas.createIfSupported();
	}

	@Override
	public Canvas getWidget() {
		return (Canvas) super.getWidget();
	}

	@Override
	public VDianaViewerComponentState getState() {
		return (VDianaViewerComponentState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

		// TODO do something useful
		final String text = getState().text;
	}

	/*public void runCommand(Command command) {
		rpc.message("runCommand");
		// if (commands.add(command))
		command.execute();
	}*/

	public void message(String string) {
		rpc.message(string);
	}

}
