package org.openflexo.diana.vaadin.tests;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.diana.vaadin.tests.TestEdge;
import org.openflexo.diana.vaadin.tests.TestGraph;
import org.openflexo.diana.vaadin.tests.TestGraphNode;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.GRBinding.ConnectorGRBinding;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.ConnectorGRProvider;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;

public class GraphDrawing2 extends DrawingImpl<TestGraph> {

	private DrawingGraphicalRepresentation graphRepresentation;
	private ShapeGraphicalRepresentation nodeRepresentation;
	private ConnectorGraphicalRepresentation edgeRepresentation;

	public GraphDrawing2(TestGraph graph, FGEModelFactory factory) {
		super(graph, factory, PersistenceMode.SharedGraphicalRepresentations);
	}

	@Override
	public void init() {
		graphRepresentation = getFactory().makeDrawingGraphicalRepresentation();
		nodeRepresentation = getFactory().makeShapeGraphicalRepresentation(ShapeType.CIRCLE);
		edgeRepresentation = getFactory().makeConnectorGraphicalRepresentation(ConnectorType.CURVE);

		final DrawingGRBinding<TestGraph> graphBinding = bindDrawing(TestGraph.class, "graph", new DrawingGRProvider<TestGraph>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(TestGraph drawable, FGEModelFactory factory) {
				return graphRepresentation;
			}
		});
		final ShapeGRBinding<TestGraphNode> nodeBinding = bindShape(TestGraphNode.class, "node", new ShapeGRProvider<TestGraphNode>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(TestGraphNode drawable, FGEModelFactory factory) {
				return nodeRepresentation;
			}
		});
		final ConnectorGRBinding<TestEdge> edgeBinding = bindConnector(TestEdge.class, "edge", nodeBinding, nodeBinding, graphBinding,
				new ConnectorGRProvider<TestEdge>() {
					@Override
					public ConnectorGraphicalRepresentation provideGR(TestEdge drawable, FGEModelFactory factory) {
						return edgeRepresentation;
					}
				});

		graphBinding.addToWalkers(new GRStructureVisitor<TestGraph>() {

			@Override
			public void visit(TestGraph graph) {
				for (TestGraphNode node : graph.getNodes()) {
					drawShape(nodeBinding, node, graph);
				}
			}
		});

		graphBinding.addToWalkers(new GRStructureVisitor<TestGraph>() {
			@Override
			public void visit(TestGraph graph) {
				System.out.println("Walking for edges ");
				for (TestGraphNode node : graph.getNodes()) {
					for (TestEdge edge : node.getInputEdges()) {
						drawConnector(edgeBinding, edge, edge.getStartNode(), edge.getEndNode());
					}
					for (TestEdge edge : node.getOutputEdges()) {
						drawConnector(edgeBinding, edge, edge.getStartNode(), edge.getEndNode());
					}
				}
			}
		});

		nodeBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);

	}
}
