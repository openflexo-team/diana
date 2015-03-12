package org.openflexo.diana.vaadin.widgetset.client;

import com.vaadin.shared.communication.ClientRpc;

public interface VaadinClientRPC extends ClientRpc{

	public void fill();

	public void fillRect(Double startX, Double startY, Double rectWidth,
			Double rectHeight);

	public void fillText(String text, Double x, Double y, Double maxWidth);

	public void setFont(String font);

	public void lineTo(Double x, Double y);

	public void moveTo(Double x, Double y);

	public void saveContext();

	public void clear();

	public void setFillStyle(String color);

	public void drawRect(double x, double y, double width, double height);
	
}
