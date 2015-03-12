package org.openflexo.diana.vaadin.view;
import org.openflexo.fge.Drawing.DrawingTreeNode;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseWheelListener;
import com.google.gwt.user.client.ui.MouseWheelVelocity;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("deprecation")
public class VFGEMouseListener implements MouseListener, MouseWheelListener {

	private final DrawingTreeNode<?, ?> node;
	protected VFGEView<?, ?> view;
	private MouseEvent previousEvent;

	public <O> VFGEMouseListener(DrawingTreeNode<O, ?> node, VFGEView<O, ?> aView) {
		this.node = node;
		view = aView;
	}

	@Override
	public void onMouseWheel(Widget sender, MouseWheelVelocity velocity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseDown(Widget sender, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseEnter(Widget sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseLeave(Widget sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseMove(Widget sender, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseUp(Widget sender, int x, int y) {
		// TODO Auto-generated method stub
		
	}

}
