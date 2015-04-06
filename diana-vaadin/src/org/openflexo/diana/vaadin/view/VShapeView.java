package org.openflexo.diana.vaadin.view;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.ContainerGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.tools.DianaPalette;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.notifications.NodeAdded;
import org.openflexo.fge.notifications.NodeDeleted;
import org.openflexo.fge.notifications.NodeRemoved;
import org.openflexo.fge.notifications.ObjectHasMoved;
import org.openflexo.fge.notifications.ObjectHasResized;
import org.openflexo.fge.notifications.ObjectMove;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.notifications.ObjectWillMove;
import org.openflexo.fge.notifications.ObjectWillResize;
import org.openflexo.fge.notifications.ShapeChanged;
import org.openflexo.fge.notifications.ShapeNeedsToBeRedrawn;
import org.openflexo.diana.vaadin.graphics.VDianaDrawingGraphics;
import org.openflexo.diana.vaadin.graphics.VDianaGraphics;
import org.openflexo.diana.vaadin.graphics.VDianaShapeGraphics;
import org.openflexo.diana.vaadin.view.VaadinViewFactory;
import org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview.VDrawingViewClientRpc;
import org.openflexo.fge.view.DianaViewFactory;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEContainerView;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fge.view.ShapeView;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

// paint and doPaint need to implement 
public class VShapeView<O> extends VDianaBasedView<O> implements ShapeView<O, AbstractComponent>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2707577384666869581L;
	private static final Logger logger = Logger.getLogger(VShapeView.class.getPackage().getName());
	private ShapeNode<O> shapeNode;
	private VDianaShapeGraphics graphics;
	//private VDianaDrawingGraphics Vgraphics;
	private AbstractDianaEditor<?, VaadinViewFactory, AbstractComponent> controller;
	//protected final VDianaBasedView<Object> Delegate = new VDianaBasedView<Object>();
	//public VDrawingViewClientRpc rpc = getRpcProxy(VDrawingViewClientRpc.class);
	
	public VShapeView(){
		
	}
	
	public VShapeView(ShapeNode<O> node, AbstractDianaEditor<?, VaadinViewFactory, AbstractComponent> controller) {
		super();
		logger.fine("Create VShapeView " + Integer.toHexString(hashCode()) + " for " + node);
		this.controller = controller;
		this.shapeNode = node;
		node.finalizeConstraints();
		if (getController() != null) {
		}
		shapeNode.getPropertyChangeSupport().addPropertyChangeListener(this);
		//setRPC
		graphics = new VDianaShapeGraphics(node, this);
		//paintComponent();
	}

	@Override
	public void paintComponent() {
		graphics.applyCurrentBackgroundStyle();
		graphics.fillRect(100,100,40,30);
		graphics.fillRect(300,300,40,50);
		graphics.fillRect(600,200,50,40);
		//shapeNode.paint(graphics);  // it works but not proprely
		System.out.print("xxxxxxxx");
	}
	
	private boolean isDeleted = false;

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		if (isDeleted) {
			logger.warning("Received notifications for deleted view: " + evt);
			return;
		}else {
			if (evt.getPropertyName() == null) {
				return;
			}
			if (evt.getPropertyName().equals(ShapeGraphicalRepresentation.BACKGROUND_STYLE_TYPE_KEY)) {
				System.out.println("Received BACKGROUND_STYLE changed !");
			}
			if (evt.getPropertyName().equals(NodeAdded.EVENT_NAME)) {
				}
			else if (evt.getPropertyName().equals(NodeRemoved.EVENT_NAME)) {
				//handleNodeRemoved((DrawingTreeNode<?, ?>) evt.getOldValue(), (ContainerNode<?, ?>) evt.getNewValue());
			} else if (evt.getPropertyName().equals(NodeDeleted.EVENT_NAME)) {
				delete();
			} else if (evt.getPropertyName().equals(ObjectWillMove.EVENT_NAME)) {
				/*if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().addToTemporaryObjects(shapeNode);
					getPaintManager().invalidate(shapeNode);
				}*/
			} else if (evt.getPropertyName().equals(ObjectMove.PROPERTY_NAME)) {
				//relocateView();
				if (getParentView() != null) {
				}
			} else if (evt.getPropertyName().equals(ObjectHasMoved.EVENT_NAME)) {
/*				if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().removeFromTemporaryObjects(shapeNode);
					getPaintManager().invalidate(shapeNode);
					getPaintManager().repaint(getParentView());
				}*/
			} else if (evt.getPropertyName().equals(ObjectWillResize.EVENT_NAME)) {
			/*	if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().addToTemporaryObjects(shapeNode);
					getPaintManager().invalidate(shapeNode);
				}*/
			} else if (evt.getPropertyName().equals(ObjectResized.PROPERTY_NAME)) {
				// TODO: (sylvain) those two cases may be joined ??? Please check
				// resizeView();
				/*if (getParentView() != null) {
					getParentView().revalidate();
					getPaintManager().repaint(this);
				}*/
//				if (getPaintManager().isPaintingCacheEnabled()) {
//					getPaintManager().removeFromTemporaryObjects(shapeNode);
//					getPaintManager().invalidate(shapeNode);
//					getPaintManager().repaint(getParentView());
//				}
			} else if (evt.getPropertyName().equals(ObjectHasResized.EVENT_NAME)
					|| evt.getPropertyName().equals(ContainerGraphicalRepresentation.WIDTH_KEY)
					|| evt.getPropertyName().equals(ContainerGraphicalRepresentation.HEIGHT_KEY)) {
//				resizeView();
//				if (getPaintManager().isPaintingCacheEnabled()) {
//					getPaintManager().removeFromTemporaryObjects(shapeNode);
//					getPaintManager().invalidate(shapeNode);
//					getPaintManager().repaint(getParentView());
//				}
			} else if (evt.getPropertyName().equals(ShapeGraphicalRepresentation.X_KEY)
					|| evt.getPropertyName().equals(ShapeGraphicalRepresentation.Y_KEY)) {
				// System.out.println("Relocating view");
//				relocateView();
//				if (getPaintManager().isPaintingCacheEnabled()) {
//					getPaintManager().removeFromTemporaryObjects(shapeNode);
//					getPaintManager().invalidate(shapeNode);
//					getPaintManager().repaint(getParentView());
//				}
			} else if (evt.getPropertyName().equals(ShapeNeedsToBeRedrawn.EVENT_NAME)) {
				//getPaintManager().invalidate(shapeNode);

			} else if (evt.getPropertyName().equals(ShapeChanged.EVENT_NAME)) {
				//getPaintManager().invalidate(shapeNode);

			} else if (evt.getPropertyName().equals(GraphicalRepresentation.LAYER.getName())) {
				//updateLayer();
				//if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(shapeNode)) {
				//	getPaintManager().invalidate(shapeNode);
				//}


			} else if (evt.getPropertyName().equals(DrawingTreeNode.IS_FOCUSED.getName())) {
			} else {
			}
		}
		
	}

	@Override
	public ShapeNode<O> getNode() {
		return shapeNode;
	}
	
	@Override
	public O getDrawable() {
		return shapeNode.getDrawable();
	}

	@Override
	public void removeView(FGEView<?, ?> view) {
	}

	@Override
	public AbstractDianaEditor<?,VaadinViewFactory, AbstractComponent> getController() {
		return controller;
	}

	/*@Override
	public VDianaLayeredView<?> getParentView() {
		return getParent();
	}
	
	@Override
	public VDianaLayeredView<?> getParent() {
		return (VDianaLayeredView<?>) super.getParent();
	}*/
	

	@Override
	public VDrawingView<?> getDrawingView() {
		if (getController() != null) {
			return (VDrawingView<?>) getController().getDrawingView();
		}
		return null;
	}
	
	@Override
	public double getScale() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void rescale() {
		
	}

	@Override
	public void activatePalette(DianaPalette<?, ?> aPalette) {
		
	}

	@Override
	public synchronized void delete() {
		if (getParentView() != null) {
		}
		if (shapeNode != null) {
			shapeNode.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		getController().unreferenceViewForDrawingTreeNode(shapeNode);
		controller = null;
		shapeNode = null;
		isDeleted = true;
	}
	
	@Override
	public void stopLabelEdition() {
		//getLabelView().stopEdition();
	}


	@Override
	public FGEContainerView<?, ?> getParentView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FGEShapeGraphics getFGEGraphics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <RC> List<FGEView<?, ? extends RC>> getChildViews() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// used to obtain the right reference of ClientRpc
	//public void setRPC(VDrawingViewClientRpc rpc){
		//this.Delegate.staticRpc = rpc;
	//}
	
	// used to obtain the right reference of ClientRpc
	public VDrawingViewClientRpc getRPC(){
		return null;//this.Delegate.staticRpc;
	}
	
	public void setRPC(VDrawingViewClientRpc rpc){
		this.graphics.setRPC(rpc);
		System.out.print("the Rpc in the VShapeView is "+rpc + "/n");
	} 
	
	public VDrawingViewClientRpc GetRPC(){
		return rpc;
	}

}
