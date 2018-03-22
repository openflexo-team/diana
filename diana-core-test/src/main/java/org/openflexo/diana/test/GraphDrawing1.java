/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.diana.test;

import org.openflexo.connie.DataBinding;
import org.openflexo.diana.ConnectorGraphicalRepresentation;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.GRStructureVisitor;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.GRBinding.ConnectorGRBinding;
import org.openflexo.diana.GRBinding.DrawingGRBinding;
import org.openflexo.diana.GRBinding.ShapeGRBinding;
import org.openflexo.diana.GRProvider.ConnectorGRProvider;
import org.openflexo.diana.GRProvider.DrawingGRProvider;
import org.openflexo.diana.GRProvider.ShapeGRProvider;
import org.openflexo.diana.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.diana.control.DianaInteractiveViewer;
import org.openflexo.diana.control.MouseControlContext;
import org.openflexo.diana.control.MouseDragControl;
import org.openflexo.diana.control.MouseControl.MouseButton;
import org.openflexo.diana.control.actions.MouseDragControlImpl;
import org.openflexo.diana.control.actions.MoveAction;
import org.openflexo.diana.impl.DrawingImpl;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;

public class GraphDrawing1 extends DrawingImpl<TestGraph> {

	private DrawingGraphicalRepresentation graphRepresentation;
	private ShapeGraphicalRepresentation nodeRepresentation;
	private ConnectorGraphicalRepresentation edgeRepresentation;

	public GraphDrawing1(TestGraph graph, DianaModelFactory factory) {
		super(graph, factory, PersistenceMode.SharedGraphicalRepresentations);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void init() {
		graphRepresentation = getFactory().makeDrawingGraphicalRepresentation();
		// graphRepresentation.setBackgroundColor(Color.RED);
		nodeRepresentation = getFactory().makeShapeGraphicalRepresentation(ShapeType.CIRCLE);
		// nodeRepresentation.setX(50);
		// nodeRepresentation.setY(50);
		nodeRepresentation.setWidth(20);
		nodeRepresentation.setHeight(20);
		nodeRepresentation.setAbsoluteTextX(30);
		nodeRepresentation.setAbsoluteTextY(0);
		edgeRepresentation = getFactory().makeConnectorGraphicalRepresentation(ConnectorType.LINE);

		MouseDragControl moveControl = nodeRepresentation.getMouseDragControl("Move");
		nodeRepresentation.removeFromMouseDragControls(moveControl);
		nodeRepresentation.addToMouseDragControls(new MouseDragControlImpl("dragNode", MouseButton.LEFT, new MoveAction() {

			@Override
			public boolean handleMouseReleased(org.openflexo.diana.Drawing.DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> editor,
					MouseControlContext context, boolean isSignificativeDrag) {
				boolean returned = super.handleMouseReleased(node, editor, context, isSignificativeDrag);
				System.out.println("Detected mouse released");
				return returned;
			}
		}, false, false, false, false, getFactory().getEditingContext()));

		final DrawingGRBinding<TestGraph> graphBinding = bindDrawing(TestGraph.class, "graph", new DrawingGRProvider<TestGraph>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(TestGraph drawable, DianaModelFactory factory) {
				return graphRepresentation;
			}
		});
		final ShapeGRBinding<TestGraphNode> nodeBinding = bindShape(TestGraphNode.class, "node", new ShapeGRProvider<TestGraphNode>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(TestGraphNode drawable, DianaModelFactory factory) {
				return nodeRepresentation;
			}
		});
		final ConnectorGRBinding<TestEdge> edgeBinding = bindConnector(TestEdge.class, "edge", nodeBinding, nodeBinding, graphBinding,
				new ConnectorGRProvider<TestEdge>() {
					@Override
					public ConnectorGraphicalRepresentation provideGR(TestEdge drawable, DianaModelFactory factory) {
						return edgeRepresentation;
					}
				});

		graphBinding.addToWalkers(new GRStructureVisitor<TestGraph>() {

			@Override
			public void visit(TestGraph graph) {
				for (TestGraphNode node : graph.getNodes()) {
					drawShape(nodeBinding, node);
				}
			}
		});

		nodeBinding.addToWalkers(new GRStructureVisitor<TestGraphNode>() {
			@Override
			public void visit(TestGraphNode node) {
				System.out.println("Walking for edges ");
				for (TestEdge edge : node.getInputEdges()) {
					drawConnector(edgeBinding, edge, edge.getStartNode(), edge.getEndNode(), node.getGraph());
				}
				for (TestEdge edge : node.getOutputEdges()) {
					drawConnector(edgeBinding, edge, edge.getStartNode(), edge.getEndNode(), node.getGraph());
				}
			}
		});

		nodeBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);
		// nodeBinding.setDynamicPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X, new DataBinding<Double>("drawable.labelX"));
		// nodeBinding.setDynamicPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y, new DataBinding<Double>("drawable.labelY"));
		nodeBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.X, new DataBinding<Double>("drawable.x"), true);
		nodeBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.Y, new DataBinding<Double>("drawable.y"), true);

	}
}
