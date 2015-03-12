package org.openflexo.diana.vaadin;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Set;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaEditorDelegate;
import org.openflexo.fge.view.FGEView;

import com.vaadin.ui.AbstractComponent;

public class VaadinEditorDelegate implements DianaEditorDelegate{
	
	private AbstractDianaEditor<?, VaadinViewFactory, AbstractComponent> controller;

	public VaadinEditorDelegate(AbstractDianaEditor<?, VaadinViewFactory, AbstractComponent> controller) {
		super();
		this.controller = controller;
	}
	
	@Override
	public void focusedObjectChanged(DrawingTreeNode<?, ?> oldFocusedObject,
			DrawingTreeNode<?, ?> newFocusedObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectStartMoving(DrawingTreeNode<?, ?> node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectStopMoving(DrawingTreeNode<?, ?> node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectsStartMoving(Set<? extends DrawingTreeNode<?, ?>> nodes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectsStopMoving(Set<? extends DrawingTreeNode<?, ?>> nodes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void repaintAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Point getPointInView(Object source, Point point, FGEView<?, ?> view) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedImage makeScreenshot(DrawingTreeNode<?, ?> node) {
		// TODO Auto-generated method stub
		return null;
	}

}
