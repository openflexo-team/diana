package org.openflexo.diana.vaadin.graphics;


import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;

import org.openflexo.diana.vaadin.widgetset.client.VaadinClientRPC;
import org.openflexo.diana.vaadin.widgetset.client.VaadinServerRpc;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.geom.FGECubicCurve;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEQuadCurve;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEGraphicsImpl;
import org.openflexo.fge.view.FGEView;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbstractComponent;
// inner class to implement the FGEGraphicsImpl
public class VFGEGraphics extends FGEGraphicsImpl{

	public interface VaadinClickListener {

		public void clicked(MouseEventDetails med);
	}

	public interface VaadinPressListener {

		public void pressed(MouseEventDetails med);
	}

@SuppressWarnings("serial")
public class Inner extends AbstractComponent{
	private final ArrayList<VaadinClickListener> clickListeners = new ArrayList<VaadinClickListener>();
	private final ArrayList<VaadinPressListener> pressListeners = new ArrayList<VaadinPressListener>();
	private final VaadinClientRPC rpc =getRpcProxy(VaadinClientRPC.class);
	
	public Inner(){
		registerRpc(new VaadinServerRpc() {
			public void clicked(MouseEventDetails med) {
				fireClicked(med);
			}

			public void pressed(MouseEventDetails med) {
				firePressed(med);
			}
		});
	}
	public void addListener(VaadinClickListener listener) {
		if (!clickListeners.contains(listener)) {
			clickListeners.add(listener);
		}
	}

	public void removeListener(VaadinClickListener listener) {
		if (clickListeners.contains(listener)) {
			clickListeners.remove(listener);
		}
	}
	public void addListener(VaadinPressListener listener) {
		if (!pressListeners.contains(listener)) {
			pressListeners.add(listener);
		}
	}

	public void removeListener(VaadinPressListener listener) {
		if (pressListeners.contains(listener)) {
			pressListeners.remove(listener);
		}
	}

	private void fireClicked(MouseEventDetails med) {
		for (VaadinClickListener listener : clickListeners) {
			listener.clicked(med);
		}
	}

	private void firePressed(MouseEventDetails med) {
		for (VaadinPressListener listener : pressListeners) {
			listener.pressed(med);
		}
	}
}
	public final Inner inner = new Inner();
	public VFGEGraphics (DrawingTreeNode<?, ?> dtn, FGEView<?, ?> view) {
		super(dtn, view);
		// TODO Auto-generated constructor stub
		
	}
	
	@Override
	public FGERectangle drawString(String text, double x, double y,
			int orientation, HorizontalTextAlignment alignment) {
		// TODO Auto-generated method stub
		return null;
	}
// need to make connection with the client side
	public void createGraphics(Graphics2D graphics2D/*, AbstractDianaEditor<?, ?, ?> controller*/) {
		
	}

	public void releaseGraphics() {
		
	}

	@Override
	public void drawPoint(double x, double y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawRoundArroundPoint(double x, double y, int size) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawRect(double x, double y, double width, double height) {
		// TODO Auto-generated method stub
		inner.rpc.drawRect(x, y, width, height);
	}

	@Override
	public void fillRect(double x, double y, double width, double height) {
		// TODO Auto-generated method stub
		inner.rpc.fillRect(x, y, width, height);
	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}


	@Override
	public void fillCircle(double x, double y, double width, double height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawArc(double x, double y, double width, double height,
			double angleStart, double arcAngle) {
		// TODO Auto-generated method stub
		
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
	protected void applyCurrentBackgroundStyle() {
		// TODO Auto-generated method stub
		setFillStyle("white");
		inner.rpc.fillRect(new Integer (0).doubleValue(),new Integer (0).doubleValue(),new Integer (1000).doubleValue(),new Integer (1000).doubleValue());
		setFillStyle("black");
	}
	
	public void setFillStyle(String color) {
		inner.rpc.setFillStyle(color);
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
	}
