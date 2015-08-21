package org.openflexo.diana.vaadin.client;

import com.google.gwt.user.client.ui.Label;

// TODO extend any GWT Widget
public class CanvasWidget extends Label {

	public static final String CLASSNAME = "canvas";

	public CanvasWidget() {

		// setText("Canvas sets the text via CanvasConnector using CanvasState");
		setStyleName(CLASSNAME);

	}

}