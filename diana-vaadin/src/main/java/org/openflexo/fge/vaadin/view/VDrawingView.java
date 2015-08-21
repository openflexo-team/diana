package org.openflexo.fge.vaadin.view;

import java.awt.Insets;
import java.awt.Point;
import java.awt.dnd.Autoscroll;

import org.openflexo.diana.vaadin.VaadinViewFactory;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.actions.RectangleSelectingAction;
import org.openflexo.fge.graphics.FGEDrawingGraphics;
import org.openflexo.fge.view.ConnectorView;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEContainerView;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fge.view.ShapeView;

import com.vaadin.ui.AbstractComponent;

public class VDrawingView<M> extends VDianaLayeredView<M>implements Autoscroll, DrawingView<M, AbstractComponent> {

	private final Drawing<M> drawing;
	private final AbstractDianaEditor<M, VaadinViewFactory, AbstractComponent> controller;

	protected VFGEDrawingGraphics graphics;

	public VDrawingView(AbstractDianaEditor<M, VaadinViewFactory, AbstractComponent> controller) {
		System.out.println("DrawingView");
		this.controller = controller;
		drawing = controller.getDrawing();
	}

	@Override
	public M getDrawable() {
		return getModel();
	}

	@Override
	public Drawing<M> getDrawing() {
		return drawing;
	}

	@Override
	public VDrawingView<?> getDrawingView() {
		return this;
	}

	@Override
	public AbstractDianaEditor<M, ?, AbstractComponent> getController() {
		return controller;
	}

	public M getModel() {
		return drawing.getModel();
	}

	@Override
	public <O> FGEView<O, ?> viewForNode(DrawingTreeNode<O, ?> node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <O> ConnectorView<O, ?> connectorViewForNode(ConnectorNode<O> node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <O> ShapeView<O, ?> shapeViewForNode(ShapeNode<O> node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(FGEView<?, ?> view) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRectangleSelectingAction(RectangleSelectingAction action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetRectangleSelectingAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public FGEDrawingGraphics getFGEGraphics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Insets getAutoscrollInsets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void autoscroll(Point cursorLocn) {
		// TODO Auto-generated method stub

	}

	@Override
	public DrawingTreeNode<M, ?> getNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FGEContainerView<?, ?> getParentView() {
		// TODO Auto-generated method stub
		return null;
	};

}
