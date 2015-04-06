package org.openflexo.diana.vaadin.graphics;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import org.openflexo.diana.vaadin.view.VDianaBasedView;
import org.openflexo.diana.vaadin.view.VDrawingView;
import org.openflexo.diana.vaadin.view.VFGEView;
import org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview.VDrawingViewClientRpc;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.geom.FGECubicCurve;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEQuadCurve;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEGraphicsImpl;


public class VDianaGraphics extends FGEGraphicsImpl{
	
	protected final VDrawingView<Object> Delegate = new VDrawingView<Object>();
	
	public VDianaGraphics(DrawingTreeNode<?, ?> dtn, VFGEView<?, ?> view) {
		super(dtn, view);
	}		
	
	@Override
	public VFGEView<?, ?> getView() {
		return (VFGEView<?, ?>) super.getView();
	}

	@Override
	public void delete() {
		super.delete();
	}
	
	// need to make connection with the client side
	@Override
	public void drawLine(double x1, double y1, double x2, double y2) {
		beginPath();
		moveTo(x1,y1);
		lineTo(x2,y2);
		stroke();	
	}

	@Override
	public void drawRoundRect(double x, double y, double width, double height,
			double arcwidth, double archeight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fillRoundRect(double x, double y, double width, double height,
			double arcwidth, double archeight) {
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
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		Delegate.rpc.fillCircle(x, y, width, height);//fillArc(r.x, r.y, r.width, r.height, 0, 360);
	}


	@Override
	public void fillCircle(double x, double y, double width, double height) {
		Delegate.rpc.fillCircle(x, y, width, height);
		
	}

	@Override
	public void drawArc(double x, double y, double width, double height,
			double angleStart, double arcAngle) {
		// System.out.println("drawArc ("+x+","+y+","+width+","+height+")");
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		//Delegate.rpc.drawArc(r.x, r.y, r.width, r.height, (int) angleStart, (int) arcAngle);
		double radius;
		if (r.width > r.height){
			radius = r.height;
		}else {
			radius = r.width;
		}
		//Delegate.rpc.drawArc(r.x, r.y, radius, angleStart, arcAngle); 
	}

	@Override
	public void fillArc(double x, double y, double width, double height,
			double angleStart, double arcAngle, boolean chord) {
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
	protected void applyCurrentTextStyle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Rectangle drawControlPoint(double x, double y, int size) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void drawImage(Image image, FGEPoint p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawCircle(double x, double y, double width, double height,
			Stroke stroke) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FGERectangle drawString(String text, double x, double y,
			int orientation, HorizontalTextAlignment alignment) {
		return null;
	}

	@Override
	public void applyCurrentBackgroundStyle() {
		setFillStyle("white");
		//drawRect(new Integer (0).doubleValue(),new Integer (0).doubleValue(),new Integer (1000).doubleValue(),new Integer (1000).doubleValue());
		fillRect(new Integer (0).doubleValue(),new Integer (0).doubleValue(),new Integer (1000).doubleValue(),new Integer (1000).doubleValue());
		setFillStyle("blue");
	}
	public void createGraphics(){
		Delegate.rpc.createGraphics();
	};
	public void clearGraphics(){
		Delegate.rpc.clear();
	};
	
	public void beginPath(){
		Delegate.rpc.beginPath();
	}
	
	public void moveTo(double x, double y){
		Delegate.rpc.moveTo(x, y);
	}
	
	public void lineTo(double x, double y){
		Delegate.rpc.lineTo(x, y);
	}
/**
 	* Sets the color, gradient, or pattern used to fill the drawing.
 * 
 * @param color
 *            the new fill style
 */
	public void setFillStyle(String color) {
		Delegate.rpc.setFillStyle(color);
		//System.out.println("in the graphics the rpc is " + Delegate.rpc);
	}
	
	public void fillRect(double startX, double startY, double width,
			double height) {
		Delegate.rpc.fillRect(startX, startY, width, height);
	}
	
	@Override
	public void drawRect(double startX, double startY, double width, double height) {
		Delegate.rpc.drawRect(startX,startY,width,height);	
	}
	
	public void fillText(){
		
	}
	
	public void fillBackGround() {
		//setFillStyle("red");
		//Delegate.rpc.fillRect(new Integer (0).doubleValue(),new Integer (0).doubleValue(),new Integer (1000).doubleValue(),new Integer (1000).doubleValue());
		//setFillStyle("black");
		System.out.println("in the FILLBACKGROUND the rpc is " + Delegate.rpc);
	}
	
	// used to obtain the right reference of ClientRpc
	public void setRPC(VDrawingViewClientRpc rpc){
		this.Delegate.rpc = rpc;
	}

	public void stroke() {
		Delegate.rpc.stroke();
	}
	@Override
	public void drawPoint(double x, double y) {
		Delegate.rpc.drawPoint(x, y);
		
	}

	@Override
	public void drawRoundArroundPoint(double x, double y, int size) {
		// TODO Auto-generated method stub
		//ShapeGraphicalRepresentation gr = (ShapeGraphicalRepresentation) getGraphicalRepresentation();
	}
	
	private void fillInShapeWithImage(Shape aShape) {

		AffineTransform at = AffineTransform.getScaleInstance(getScale(), getScale());
	}

}