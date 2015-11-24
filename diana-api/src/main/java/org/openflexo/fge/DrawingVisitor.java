package org.openflexo.fge;

import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.RootNode;
import org.openflexo.fge.Drawing.ShapeNode;

public interface DrawingVisitor {
	public void visit(Drawing drawing);

	public void visit(DrawingTreeNode<?, ?> node);

	// public void visit(ContainerNode<?, ?> node);

	public void visit(RootNode<?> node);
	// public abstract void visit(ShapeNode<?> node);

	// public void visit(ConnectorNode<?> node);

	// public void visit(GeometricNode<?> node);

	// public abstract void visit(ShapeGraphicalRepresentation GR);

	// public abstract void visit(GRProperty<?> property);

	public void visit(ContainerNode<?, ?> dtn);

	public void visit(ShapeNode<?> dtn);

	public void visit(BackgroundStyle bg, DrawingTreeNode<?, ?> dtn);

	public void visit(ColorBackgroundStyle bg, DrawingTreeNode<?, ?> dtn);
}
