package org.openflexo.fge.vaadin.view;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Stroke;

import org.openflexo.diana.vaadin.VDianaViewer;
import org.openflexo.diana.vaadin.client.vdianaviewercomponent.VDianaViewerComponentClientRpc;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.geom.FGECubicCurve;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEQuadCurve;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEGraphicsImpl;
import org.openflexo.fge.view.FGEView;

import com.google.gwt.canvas.dom.client.Context2d;

public class VFGEGraphics extends FGEGraphicsImpl {

	private Context2d ctx;
	// private VDianaViewerComponentClientRpc rpc;

	protected VFGEGraphics(DrawingTreeNode<?, ?> dtn, FGEView<?, ?> view) {
		super(dtn, view);
	}

	public Context2d getContext() {
		return ctx;
	}

	public VDianaViewerComponentClientRpc getRpc() {
		return getController().getComponent().getRpc();
	}

	@Override
	public VDianaViewer<?> getController() {
		return (VDianaViewer) super.getController();
	}

	@Override
	public FGERectangle drawString(String text, double x, double y, int orientation, HorizontalTextAlignment alignment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle drawControlPoint(double x, double y, int size) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void drawPoint(double x, double y) {
		getRpc().drawPoint(x, y);

	}

	@Override
	public void drawRoundArroundPoint(double x, double y, int size) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRect(double x, double y, double width, double height) {
		getRpc().drawRect(x, y, width, height);
	}

	@Override
	public void fillRect(double x, double y, double width, double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawImage(Image image, FGEPoint p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRoundRect(double x, double y, double width, double height, double arcwidth, double archeight) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillRoundRect(double x, double y, double width, double height, double arcwidth, double archeight) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawPolygon(FGEPoint[] points) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillPolygon(FGEPoint[] points) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawCircle(double x, double y, double width, double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawCircle(double x, double y, double width, double height, Stroke stroke) {
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
	public void fillArc(double x, double y, double width, double height, double angleStart, double arcAngle, boolean chord) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawCurve(FGEQuadCurve curve) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawCurve(FGECubicCurve curve) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillGeneralShape(FGEGeneralShape shape) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void applyCurrentForegroundStyle() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void applyCurrentBackgroundStyle() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void applyCurrentTextStyle() {
		// TODO Auto-generated method stub

	}

}
