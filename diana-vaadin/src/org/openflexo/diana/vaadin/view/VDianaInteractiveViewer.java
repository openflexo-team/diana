package org.openflexo.diana.vaadin.view;

import org.openflexo.diana.vaadin.control.VaadinToolFactory;
import org.openflexo.diana.vaadin.view.VDrawingView;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.DianaInteractiveViewer;

import com.vaadin.ui.AbstractComponent;

public class VDianaInteractiveViewer<M> extends DianaInteractiveViewer<M, VaadinViewFactory, AbstractComponent > {

	public VDianaInteractiveViewer(Drawing<M> aDrawing, FGEModelFactory factory, VaadinViewFactory dianaFactory, VaadinToolFactory toolFactory) {
		super(aDrawing, factory, dianaFactory, toolFactory);
		setDelegate(new VaadinEditorDelegate(this));
	}
	
	public VDianaInteractiveViewer(Drawing<M> aDrawing, FGEModelFactory factory, VaadinToolFactory toolFactory) {
		super(aDrawing, factory, VaadinViewFactory.INSTANCE, toolFactory);
		setDelegate(new VaadinEditorDelegate(this));
	}
	@SuppressWarnings("unchecked")
	@Override
	public VDrawingView<M> getDrawingView() {
		return (VDrawingView<M>) super.getDrawingView();
	}

	@Override
	public VaadinEditorDelegate getDelegate() {
		return (VaadinEditorDelegate) super.getDelegate();
	}
	

}