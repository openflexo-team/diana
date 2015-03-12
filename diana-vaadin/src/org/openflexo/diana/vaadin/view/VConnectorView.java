package org.openflexo.diana.vaadin.view;


import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.diana.vaadin.graphics.VDianaConnectorGraphics;
import org.openflexo.diana.vaadin.graphics.VDianaShapeGraphics;
import org.openflexo.diana.vaadin.view.VaadinViewFactory;
import org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview.VDrawingViewClientRpc;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.tools.DianaPalette;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.fge.view.ConnectorView;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEContainerView;
import org.openflexo.fge.view.FGEView;

import com.vaadin.client.ui.VPanel;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;

@SuppressWarnings("serial")
public class VConnectorView<O> extends Panel implements ConnectorView<O,AbstractComponent>, VFGEView<O, AbstractComponent> {

	
	private static final Logger logger = Logger.getLogger(VConnectorView.class.getPackage().getName());

	private ConnectorNode<O> connectorNode;
	private VDianaConnectorGraphics graphics;
	
	public VDrawingViewClientRpc rpc = getRpcProxy(VDrawingViewClientRpc.class);
	//need to implement the class FGEViewMouseListener under directory view 
	private AbstractDianaEditor<?, VaadinViewFactory, AbstractComponent> controller;
	
	public VConnectorView(ConnectorNode<O> node, AbstractDianaEditor<?, VaadinViewFactory, AbstractComponent> controller) {
		super();
		this.controller = controller;
		this.connectorNode = node;
		connectorNode.getPropertyChangeSupport().addPropertyChangeListener(this);
		Panel panel = new Panel();
		graphics = new VDianaConnectorGraphics(node, this);
		System.out.print("the rpc in the VConnector is " + rpc +"\n");

	}
	
	public void paint(){
		getNode().paint(graphics);
	}
	
	@Override
	public O getDrawable() {
		return connectorNode.getDrawable();
	}

	@Override
	public ConnectorNode<O> getNode() {
		return connectorNode;
	}

	@Override
	public VDrawingView< ?> getDrawingView() {
		// TODO Auto-generated method stub
		return (VDrawingView< ?>) getController().getDrawingView();
	}

	@Override
	public FGEContainerView<?, ?> getParentView() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public double getScale() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void rescale() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activatePalette(DianaPalette<?, ?> aPalette) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDeleted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stopLabelEdition() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FGEConnectorGraphics getFGEGraphics() {
		// TODO Auto-generated method stub
		return graphics;
	}

	@Override
	public AbstractDianaEditor<?, ?, ? super AbstractComponent> getController() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void paintComponent() {
			//graphics.applyCurrentBackgroundStyle();
			//graphics.fillRect(20, 20, 801, 801);
			connectorNode.paint(graphics);
			System.out.print("ccccccccc");
		}

	public void SetRPC(VDrawingViewClientRpc rpc){
		this.rpc = rpc;
	}
	
	public VDrawingViewClientRpc GetRPC(){
		return rpc;
	}
	public void setRPC(VDrawingViewClientRpc rpc){
		this.graphics.setRPC(rpc);
		System.out.print("the Rpc in the VConnectorView is "+rpc + "/n");
	}

	@Override
	public VDrawingViewClientRpc getRPC() {
		// TODO Auto-generated method stub
		return null;
	} 
	

}