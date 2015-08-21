package org.openflexo.fge.vaadin.view;

import org.openflexo.diana.vaadin.VaadinViewFactory;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.view.FGEContainerView;
import org.openflexo.fge.view.ShapeView;

import com.vaadin.ui.AbstractComponent;

public class VShapeView<O> extends VDianaLayeredView<O>implements ShapeView<O, AbstractComponent> {

	private ShapeNode<O> shapeNode;
	private AbstractDianaEditor<?, VaadinViewFactory, AbstractComponent> controller;

	private VFGEShapeGraphics graphics;

	@Override
	public ShapeNode<O> getNode() {
		return shapeNode;
	}

	@Override
	public VFGEShapeGraphics getFGEGraphics() {
		return graphics;
	}

	@Override
	public FGEContainerView<?, ?> getParentView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VDrawingView<?> getDrawingView() {
		// TODO Auto-generated method stub
		return null;
	}

}
