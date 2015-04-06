package org.openflexo.fge.impl;

import java.util.logging.Logger;

import org.openflexo.fge.Drawing.GraphNode;
import org.openflexo.fge.GRBinding;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.graph.FGEGraph;
import org.openflexo.fge.graphics.FGEShapeGraphics;

public class GraphNodeImpl<G extends FGEGraph> extends ShapeNodeImpl<G> implements GraphNode<G> {

	private static final Logger logger = Logger.getLogger(GraphNodeImpl.class.getPackage().getName());

	// TODO: change to protected
	public GraphNodeImpl(DrawingImpl<?> drawingImpl, G graph, GRBinding<G, ShapeGraphicalRepresentation> grBinding,
			ContainerNodeImpl<?, ?> parentNode) {
		super(drawingImpl, graph, grBinding, parentNode);
	}

	@Override
	public boolean delete() {
		return super.delete();
	}

	@Override
	public void paint(FGEShapeGraphics g) {
		// First draw outline (fg and bg)
		super.paint(g);

		// Paint the graph
		getDrawable().paint(g);

	}
}
