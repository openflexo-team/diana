package org.openflexo.fge.view;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.actions.RectangleSelectingAction;
import org.openflexo.fge.graphics.FGEDrawingGraphics;

public interface DrawingView<M, C> extends FGEContainerView<M, C> {

	public abstract Drawing<M> getDrawing();

	@Override
	public abstract M getDrawable();

	@Override
	public abstract AbstractDianaEditor<M, ?, ? super C> getController();

	public abstract <O> FGEView<O, ?> viewForNode(DrawingTreeNode<O, ?> node);

	public abstract <O> ConnectorView<O, ?> connectorViewForNode(ConnectorNode<O> node);

	public abstract <O> ShapeView<O, ?> shapeViewForNode(ShapeNode<O> node);

	public abstract boolean contains(FGEView<?, ?> view);

	@Override
	public abstract void delete();

	public void setRectangleSelectingAction(RectangleSelectingAction action);

	public void resetRectangleSelectingAction();

	@Override
	public FGEDrawingGraphics getFGEGraphics();

}