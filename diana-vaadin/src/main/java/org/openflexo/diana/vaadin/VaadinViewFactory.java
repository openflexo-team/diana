package org.openflexo.diana.vaadin;

import org.openflexo.fge.Drawing.ConnectorNode;
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
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.vaadin.view.VDrawingView;
import org.openflexo.fge.view.ConnectorView;
import org.openflexo.fge.view.DianaViewFactory;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.fge.view.widget.ConnectorPreviewPanel;
import org.openflexo.fge.view.widget.FIBBackgroundStyleSelector;
import org.openflexo.fge.view.widget.FIBForegroundStyleSelector;
import org.openflexo.fge.view.widget.FIBShadowStyleSelector;
import org.openflexo.fge.view.widget.FIBShapeSelector;
import org.openflexo.fge.view.widget.FIBTextStyleSelector;
import org.openflexo.fge.view.widget.ShapePreviewPanel;

import com.vaadin.ui.AbstractComponent;

public class VaadinViewFactory implements DianaViewFactory<VaadinViewFactory, AbstractComponent> {

	public static VaadinViewFactory INSTANCE = new VaadinViewFactory();

	@Override
	public <M> DrawingView<M, ? extends AbstractComponent> makeDrawingView(
			AbstractDianaEditor<M, VaadinViewFactory, AbstractComponent> controller) {
		// TODO Auto-generated method stub
		return new VDrawingView<M>(controller);
	}

	@Override
	public <O> ShapeView<O, ? extends AbstractComponent> makeShapeView(ShapeNode<O> shapeNode,
			AbstractDianaEditor<?, VaadinViewFactory, AbstractComponent> controller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <O> ConnectorView<O, ? extends AbstractComponent> makeConnectorView(ConnectorNode<O> connectorNode,
			AbstractDianaEditor<?, VaadinViewFactory, AbstractComponent> controller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaletteController<VaadinViewFactory, AbstractComponent> makePaletteController(DianaPalette<?, ?> palette) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBBackgroundStyleSelector<? extends AbstractComponent> makeFIBBackgroundStyleSelector(
			BackgroundStyleFactory backgroundStyleFactory) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBForegroundStyleSelector<? extends AbstractComponent> makeFIBForegroundStyleSelector(ForegroundStyle foregroundStyle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBTextStyleSelector<? extends AbstractComponent> makeFIBTextStyleSelector(TextStyle textStyle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBShadowStyleSelector<? extends AbstractComponent> makeFIBShadowStyleSelector(ShadowStyle shadowStyle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBShapeSelector<? extends AbstractComponent> makeFIBShapeSelector(ShapeSpecificationFactory shapeFactory) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ShapePreviewPanel<? extends AbstractComponent> makeShapePreviewPanel(ShapeSpecification shapeSpecification) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectorPreviewPanel<? extends AbstractComponent> makeConnectorPreviewPanel(ConnectorSpecification shapeSpecification) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DNDInfo makeDNDInfo(MoveAction moveAction, ShapeNode<?> shapeNode, DianaInteractiveViewer<?, ?, ?> controller,
			MouseControlContext initialContext) {
		// TODO Auto-generated method stub
		return null;
	}

}
