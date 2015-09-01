package org.openflexo.diana.vaadin;

import org.openflexo.diana.vaadin.control.VaadinToolFactory;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.control.DianaViewer;
import org.openflexo.fge.vaadin.view.VDrawingView;

import com.vaadin.ui.AbstractComponent;

public class VDianaViewer<M> extends DianaViewer<M, VaadinViewFactory, AbstractComponent> {

	private final VDianaViewerComponent<M> component;

	public VDianaViewer(Drawing<M> aDrawing, FGEModelFactory factory, VaadinToolFactory toolFactory) {
		super(aDrawing, factory, VaadinViewFactory.INSTANCE, toolFactory);
		// no "delegate" implemented
		component = new VDianaViewerComponent(this);
		System.out.println("Viewer");
	}

	@Override
	public VDrawingView<M> getDrawingView() {
		return (VDrawingView<M>) drawingView;
	}

	public VDianaViewerComponent<M> getComponent() {
		return component;
	}
}
