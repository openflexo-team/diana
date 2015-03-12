package org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview;

import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.geom.FGERectangle;

import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.vaadin.shared.communication.ClientRpc;

public interface VDrawingViewClientRpc extends ClientRpc {

	public void clear();
	public void setFillStyle(String color);
	public void fillRect(Double startX, Double startY, Double rectWidth,Double rectHeight);
	public void fill();
	public void stroke();
	
	// methodes needed to implement in order to meet the  FGEGraphicsImpl in diana corresponding to VDianaGraphics
	public void delete();
	public void createGraphics();
	public void applyCurrentTextStyle();
	public void drawPoint(Double x, Double y);
	public void drawRect(Double x, Double y, Double width, Double height);
	public void fillRoundRect(Double x, Double y, Double width, Double height, Double arcwidth, Double archeight);
	public void drawRoundRect(Double x, Double y, Double width, Double height, Double arcwidth, Double archeight);
	public void drawLine(Double x1, Double y1, Double x2, Double y2);
	public void drawCircle(Double x, Double y, Double width, Double height);
	public void fillCircle(Double x, Double y, Double width, Double height);
	public void fillGeneralShape(FGEGeneralShape shape);
	public FGERectangle drawString(String text, Double x, Double y, int orientation, HorizontalTextAlignment alignment);
	public void drawArc(Double x,  Double y,  Double radius,
			 Double startAngle,  Double endAngle);
	//public void drawImage(CanvasElement image, Double x, Double y);
	//public void drawImage2(ImageElement image, Double x, Double y);
	public void beginPath();
	
	public void fillText(String text, Double x, Double y);
	public void lineTo(Double x, Double y);
	public void moveTo(Double x, Double y);
	public void closePath();
	public void setStrokeStyle(String rgb);
	
	 
}