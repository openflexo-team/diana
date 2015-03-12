package org.openflexo.diana.vaadin;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.control.actions.DNDInfo;
import org.openflexo.fge.control.actions.MoveAction;
import org.openflexo.fge.control.tools.BackgroundStyleFactory;
import org.openflexo.fge.control.tools.DianaPalette;
import org.openflexo.fge.control.tools.PaletteController;
import org.openflexo.fge.control.tools.ShapeSpecificationFactory;
import org.openflexo.diana.vaadin.control.tools.VDianaPalette;
import org.openflexo.diana.vaadin.view.VConnectorView;
import org.openflexo.diana.vaadin.view.VDrawingView;
import org.openflexo.diana.vaadin.view.VFGEMouseListener;
import org.openflexo.diana.vaadin.view.VFGEView;
import org.openflexo.diana.vaadin.view.VShapeView;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.diana.vaadin.widgets.VConnectorPreviewPanel;
import org.openflexo.diana.vaadin.widgets.VFIBBackgroundStyleSelector;
import org.openflexo.diana.vaadin.widgets.VFIBForegroundStyleSelector;
import org.openflexo.diana.vaadin.widgets.VFIBShadowStyleSelector;
import org.openflexo.diana.vaadin.widgets.VFIBShapeSelector;
import org.openflexo.diana.vaadin.widgets.VFIBTextStyleSelector;
import org.openflexo.diana.vaadin.widgets.VShapePreviewPanel;
import org.openflexo.fge.view.DianaViewFactory;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fge.view.widget.ConnectorPreviewPanel;
import org.openflexo.fge.view.widget.FIBBackgroundStyleSelector;
import org.openflexo.fge.view.widget.FIBForegroundStyleSelector;
import org.openflexo.fge.view.widget.FIBShadowStyleSelector;
import org.openflexo.fge.view.widget.FIBShapeSelector;
import org.openflexo.fge.view.widget.FIBTextStyleSelector;
import org.openflexo.fge.view.widget.ShapePreviewPanel;

import com.vaadin.ui.AbstractComponent;

public class VaadinViewFactory implements DianaViewFactory<VaadinViewFactory, AbstractComponent>{
	
	public static VaadinViewFactory INSTANCE = new VaadinViewFactory();
	private String s;

	protected VaadinViewFactory() {
		String s=new String();
		System.out.println(123456);
	}
	
	/**
	 * Build and return a MouseListener for supplied node and view<br>
	 * Here return null as a simple viewer doesn't allow any editing facility
	 * 
	 * @param node
	 * @param view
	 * @return
	 */
	public <O> VFGEMouseListener makeViewMouseListener(DrawingTreeNode<O, ?> node, FGEView<O, ? extends AbstractComponent> view,
			AbstractDianaEditor<?, VaadinViewFactory, AbstractComponent> controller) {
		return new VFGEMouseListener(node, (VFGEView<O, ? extends AbstractComponent>) view);
	}
	
	/**
	 * Instantiate a new VDrawingView<br>
	 * You might override this method for a custom view managing
	 * 
	 * @return
	 */
	public <M> VDrawingView<M> makeDrawingView(AbstractDianaEditor<M, VaadinViewFactory, AbstractComponent> controller) {
		return new VDrawingView<M>(controller);
	}

	/**
	 * Instantiate a new JShapeView for a shape node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> VShapeView<O> makeShapeView(ShapeNode<O> shapeNode, AbstractDianaEditor<?, VaadinViewFactory, AbstractComponent> controller) {
		return new VShapeView<O>(shapeNode, controller);
	}

	/**
	 * Instantiate a new JConnectorView for a connector node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> VConnectorView<O> makeConnectorView(ConnectorNode<O> connectorNode,
			AbstractDianaEditor<?, VaadinViewFactory, AbstractComponent> controller) {
		return new VConnectorView<O>(connectorNode, controller);
	}

	@Override
	public PaletteController<VaadinViewFactory, AbstractComponent> makePaletteController(
			DianaPalette<?, ?> palette) {
		return new VPaletteController((VDianaPalette) palette);
	}

	@Override
	public DNDInfo makeDNDInfo(MoveAction moveAction, ShapeNode<?> shapeNode,
			DianaInteractiveViewer<?, ?, ?> controller,
			MouseControlContext initialContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBBackgroundStyleSelector makeFIBBackgroundStyleSelector(BackgroundStyleFactory backgroundStyleFactory) {
		return new VFIBBackgroundStyleSelector((BackgroundStyle) backgroundStyleFactory);
	}

	@Override
	public FIBForegroundStyleSelector makeFIBForegroundStyleSelector(ForegroundStyle foregroundStyle) {
		return new VFIBForegroundStyleSelector(foregroundStyle);
	}

	@Override
	public FIBTextStyleSelector makeFIBTextStyleSelector(TextStyle textStyle) {
		return new VFIBTextStyleSelector(textStyle);
	}

	@Override
	public FIBShadowStyleSelector makeFIBShadowStyleSelector(ShadowStyle shadowStyle) {
		return new VFIBShadowStyleSelector(shadowStyle);
	}

	@Override
	public FIBShapeSelector makeFIBShapeSelector(ShapeSpecificationFactory shapeFactory) {
		return new VFIBShapeSelector((ShapeSpecification) shapeFactory);
	}

	@Override
	public ShapePreviewPanel makeShapePreviewPanel(ShapeSpecification shapeSpecification) {
		return new VShapePreviewPanel();
	}

	@Override
	public ConnectorPreviewPanel makeConnectorPreviewPanel(ConnectorSpecification connectorSpecification) {
		return new VConnectorPreviewPanel();
	}


}
