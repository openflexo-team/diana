package org.openflexo.diana.vaadin.client.vdianaviewercomponent;

public interface DistantGraphics {

	public abstract void useDefaultForegroundStyle();

	public abstract void useDefaultBackgroundStyle();

	public abstract void useDefaultTextStyle();

	public abstract void drawPoint(double x, double y);

	public abstract void drawRoundArroundPoint(double x, double y, int size);

	public abstract void drawRect(double x, double y, double width, double height);

	public abstract void fillRect(double x, double y, double width, double height);

	public abstract void drawLine(double x1, double y1, double x2, double y2);

	public abstract void drawRoundRect(double x, double y, double width, double height, double arcwidth, double archeight);

	public abstract void fillRoundRect(double x, double y, double width, double height, double arcwidth, double archeight);

	public abstract void drawCircle(double x, double y, double width, double height);

	public abstract void fillCircle(double x, double y, double width, double height);

	public abstract void drawArc(double x, double y, double width, double height, double angleStart, double arcAngle);

	public abstract void fillArc(double x, double y, double width, double height, double angleStart, double arcAngle);

}
