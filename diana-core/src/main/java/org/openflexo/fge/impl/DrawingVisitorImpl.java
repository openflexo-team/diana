package org.openflexo.fge.impl;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.RootNode;
import org.openflexo.fge.DrawingVisitor;

public abstract class DrawingVisitorImpl implements DrawingVisitor {
	@Override
	public abstract void visit(Drawing drawing);

	@Override
	public abstract void visit(DrawingTreeNode<?, ?> node);

	/*@Override
	public abstract void visit(ContainerNode<?, ?> node);*/

	@Override
	public abstract void visit(RootNode<?> node);
	// public abstract void visit(ShapeNode<?> node);

	// @Override
	// public abstract void visit(ConnectorNode<?> node);

	// @Override
	// public abstract void visit(GeometricNode<?> node);

}
