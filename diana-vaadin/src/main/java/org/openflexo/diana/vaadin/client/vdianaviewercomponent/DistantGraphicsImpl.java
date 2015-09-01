package org.openflexo.diana.vaadin.client.vdianaviewercomponent;

import com.google.gwt.canvas.dom.client.Context2d;

public class DistantGraphicsImpl implements DistantGraphics {

	Context2d ctx;

	public DistantGraphicsImpl(Context2d aContext) {
		ctx = aContext;
	}

	@Override
	public void useDefaultForegroundStyle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void useDefaultBackgroundStyle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void useDefaultTextStyle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawPoint(double x, double y) {
		ctx.fillRect(x, y, 1, 1);

	}

	@Override
	public void drawRoundArroundPoint(double x, double y, int size) {
		ctx.arc(x, y, size, 0, 360);

	}

	@Override
	public void drawRect(double x, double y, double width, double height) {
		ctx.strokeRect(x, y, width, height);

	}

	@Override
	public void fillRect(double x, double y, double width, double height) {
		ctx.fillRect(x, y, width, height);

	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2) {
		ctx.moveTo(x1, y1);
		ctx.lineTo(x2, y2);
		ctx.stroke();
	}

	@Override
	public void drawRoundRect(double x, double y, double width, double height, double arcwidth, double archeight) {
		ctx.moveTo(x + arcwidth, y);
		ctx.lineTo(x + width - arcwidth, y);
		ctx.quadraticCurveTo(x + width, y, x + width, y + archeight);
		ctx.lineTo(x + width, y + height - archeight);
		ctx.quadraticCurveTo(x + width, y + height, x + width - arcwidth, y + height);
		ctx.lineTo(x + arcwidth, y + height);
		ctx.quadraticCurveTo(x, y + height, x, y + height - archeight);
		ctx.lineTo(x, y + archeight);
		ctx.quadraticCurveTo(x, y, x + arcwidth, y);
		ctx.stroke();
	}

	@Override
	public void fillRoundRect(double x, double y, double width, double height, double arcwidth, double archeight) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawCircle(double x, double y, double width, double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillCircle(double x, double y, double width, double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawArc(double x, double y, double width, double height, double angleStart, double arcAngle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillArc(double x, double y, double width, double height, double angleStart, double arcAngle) {
		// TODO Auto-generated method stub

	}

}
