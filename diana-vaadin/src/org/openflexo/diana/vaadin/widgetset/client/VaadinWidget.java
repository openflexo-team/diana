package org.openflexo.diana.vaadin.widgetset.client;

import com.google.gwt.user.client.ui.Label;

// TODO extend any GWT Widget
public class VaadinWidget extends Label {

	public static final String CLASSNAME = "canvas";

	public VaadinWidget() {

		// setText("Canvas sets the text via CanvasConnector using CanvasState");
		setStyleName(CLASSNAME);

	}

}