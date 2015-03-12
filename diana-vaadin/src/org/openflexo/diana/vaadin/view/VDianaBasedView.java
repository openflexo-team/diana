package org.openflexo.diana.vaadin.view;

import java.beans.PropertyChangeEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.diana.vaadin.graphics.VDianaGraphics;
import org.openflexo.diana.vaadin.graphics.formes.Line;
import org.openflexo.diana.vaadin.graphics.formes.Rectangle;
import org.openflexo.diana.vaadin.paint.VDianaPaintManager;
import org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview.VDrawingViewClientRpc;
import org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview.VDrawingViewServerRpc;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.control.tools.DianaPalette;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEContainerView;
import org.openflexo.fge.view.FGEView;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbstractComponent;
// this classed is not aimed to create multipule Views but a basic class for the Views
public abstract class VDianaBasedView<O> extends AbstractComponent implements FGEContainerView<O,AbstractComponent>, VFGEView<O,AbstractComponent>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8735000251261982864L;
	private static final Logger logger = Logger.getLogger(VDianaBasedView.class.getPackage().getName());
	public VDrawingViewClientRpc rpc = getRpcProxy(VDrawingViewClientRpc.class);
	//public static VDrawingViewClientRpc staticRpc;
	
	public VDianaBasedView(){
		super();
		//staticRpc = rpc;
		//System.out.print("BBBBBBBBBBBBBBBBBBBBB VDianaBasedView is " + rpcDelegate );
	}
	@Override
	public AbstractDianaEditor<?, ?, ? super AbstractComponent> getController() {
		return getDrawingView().getController();
	}

	//@Override
	public VDianaPaintManager getPaintManager() {
		return getDrawingView().getPaintManager();
	}

	// must to implement;
	@Override
	public void addView(FGEView<?, ?> view) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeView(FGEView<?, ?> view) {
		// TODO Auto-generated method stub
		
	}
	
	protected void handleNodeAdded(DrawingTreeNode<?, ?> newNode) {
		DrawingTreeNode<?, ?> parentNode = newNode.getParentNode();
		logger.fine("VShapeView: Received NodeAdded notification, creating view for " + newNode);
		if (newNode instanceof ShapeNode) {
			ShapeNode<?> shapeNode = (ShapeNode<?>) newNode;
			VShapeView<?> shapeView = (VShapeView<?>) getController().makeShapeView(shapeNode);
			shapeView.setRPC(rpc);
			addView(shapeView);
			shapeNode.notifyShapeNeedsToBeRedrawn(); // TODO: is this necessary ?
		} else if (newNode instanceof ConnectorNode) {
			ConnectorNode<?> connectorNode = (ConnectorNode<?>) newNode;
			VConnectorView<?> connectorView = (VConnectorView<?>) getController().makeConnectorView(connectorNode);
			connectorView.setRPC(rpc);
			addView(connectorView);
		} else if (newNode instanceof GeometricNode) {
			newNode.getPropertyChangeSupport().addPropertyChangeListener(this);
		}
	}
	
	protected void handleNodeRemoved(DrawingTreeNode<?, ?> removedNode, ContainerNode<?, ?> parentNode) {
	}
	
	// a must to implement 
	//public void paint(FGEGraphics graphics, DianaEditor<?> controller) {
		
	//}
	//public void paintComponent(FGEGraphics graphics){
		
	//}
	@Override
	public DrawingTreeNode<O, ?> getNode() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public FGEContainerView<?, ?> getParentView() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public O getDrawable() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public FGEGraphics getFGEGraphics() {
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
	public <RC> List<FGEView<?, ? extends RC>> getChildViews() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void paintComponent() {
		
	}
	
	public void SetRPC(VDrawingViewClientRpc rpc){
		//this.rpc = rpc;
	}
	
	public VDrawingViewClientRpc GetRPC(){
		return null;//rpc;
	}

}
