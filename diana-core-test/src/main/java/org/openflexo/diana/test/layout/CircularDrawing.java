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

package org.openflexo.diana.test.layout;

import java.awt.Color;

import org.openflexo.connie.DataBinding;
import org.openflexo.diana.ConnectorGraphicalRepresentation;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.FGEModelFactory;
import org.openflexo.diana.GRStructureVisitor;
import org.openflexo.diana.GeometricGraphicalRepresentation;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.ForegroundStyle.DashStyle;
import org.openflexo.diana.GRBinding.ConnectorGRBinding;
import org.openflexo.diana.GRBinding.DrawingGRBinding;
import org.openflexo.diana.GRBinding.GeometricGRBinding;
import org.openflexo.diana.GRBinding.ShapeGRBinding;
import org.openflexo.diana.GRProvider.ConnectorGRProvider;
import org.openflexo.diana.GRProvider.DrawingGRProvider;
import org.openflexo.diana.GRProvider.GeometricGRProvider;
import org.openflexo.diana.GRProvider.ShapeGRProvider;
import org.openflexo.diana.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.diana.geom.FGECircle;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.geom.FGEGeometricObject.Filling;
import org.openflexo.diana.geom.area.FGEArea;
import org.openflexo.diana.geom.area.FGEUnionArea;
import org.openflexo.diana.impl.DrawingImpl;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.diana.test.TestEdge;
import org.openflexo.diana.test.TestGraph;
import org.openflexo.diana.test.TestGraphNode;

public class CircularDrawing extends DrawingImpl<TestGraph> {

	private DrawingGraphicalRepresentation graphRepresentation;
	private ShapeGraphicalRepresentation nodeRepresentation;
	private ConnectorGraphicalRepresentation edgeRepresentation;
	private GeometricGraphicalRepresentation circle1GR;

	public CircularDrawing(TestGraph graph, FGEModelFactory factory) {
		super(graph, factory, PersistenceMode.SharedGraphicalRepresentations);
	}

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
		nodeRepresentation.setBackground(getFactory().makeColoredBackground(Color.red));
		edgeRepresentation = getFactory().makeConnectorGraphicalRepresentation(ConnectorType.CURVE);

		FGECircle circle1 = new FGECircle(new FGEPoint(310, 310), 100, Filling.NOT_FILLED);
		FGECircle circle2 = new FGECircle(new FGEPoint(310, 310), 200, Filling.NOT_FILLED);
		FGEArea union = FGEUnionArea.makeUnion(circle1, circle2);
		circle1GR = getFactory().makeGeometricGraphicalRepresentation(union);
		circle1GR.setForeground(getFactory().makeForegroundStyle(Color.GRAY, 0.5f, DashStyle.MEDIUM_DASHES));

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

		final GeometricGRBinding<TestGraph> circle1Binding = bindGeometric(TestGraph.class, "circle1",
				new GeometricGRProvider<TestGraph>() {
					@Override
					public GeometricGraphicalRepresentation provideGR(TestGraph drawable, FGEModelFactory factory) {
						return circle1GR;
					}
				});

		graphBinding.addToWalkers(new GRStructureVisitor<TestGraph>() {

			@Override
			public void visit(TestGraph graph) {
				drawGeometricObject(circle1Binding, graph);
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
		nodeBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.X, new DataBinding<Double>("drawable.circularX"), true);
		nodeBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.Y, new DataBinding<Double>("drawable.circularY"), true);

	}
}
