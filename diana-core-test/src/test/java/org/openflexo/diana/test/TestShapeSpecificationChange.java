/**
 * 
 * Copyright (c) 2026, Openflexo
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.DianaModelFactoryImpl;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.GRBinding.DrawingGRBinding;
import org.openflexo.diana.GRBinding.ShapeGRBinding;
import org.openflexo.diana.GRProvider.DrawingGRProvider;
import org.openflexo.diana.GRProvider.ShapeGRProvider;
import org.openflexo.diana.GRStructureVisitor;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.impl.DrawingImpl;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.pamela.exceptions.ModelDefinitionException;

/**
 * A shape keeps its control points (the handles allowing to resize it) when its shape specification is changed
 */
public class TestShapeSpecificationChange {

	private static DianaModelFactory FACTORY;

	@BeforeClass
	public static void beforeClass() throws ModelDefinitionException {
		FACTORY = new DianaModelFactoryImpl();
	}

	private static class GraphDrawing extends DrawingImpl<TestGraph> {

		GraphDrawing(TestGraph graph) {
			super(graph, FACTORY, PersistenceMode.UniqueGraphicalRepresentations);
		}

		@Override
		public void init() {
			DrawingGRBinding<TestGraph> graphBinding = bindDrawing(TestGraph.class, "graph", new DrawingGRProvider<TestGraph>() {
				@Override
				public DrawingGraphicalRepresentation provideGR(TestGraph drawable, DianaModelFactory factory) {
					return factory.makeDrawingGraphicalRepresentation();
				}
			});
			ShapeGRBinding<TestGraphNode> nodeBinding = bindShape(TestGraphNode.class, "node", new ShapeGRProvider<TestGraphNode>() {
				@Override
				public ShapeGraphicalRepresentation provideGR(TestGraphNode drawable, DianaModelFactory factory) {
					ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
					gr.setWidth(40);
					gr.setHeight(40);
					return gr;
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
		}
	}

	@Test
	public void testControlPointsAfterShapeChange() {
		TestGraph graph = new TestGraph();
		new TestGraphNode("node", graph);
		GraphDrawing drawing = new GraphDrawing(graph);
		@SuppressWarnings("unchecked")
		ShapeNode<TestGraphNode> node = (ShapeNode<TestGraphNode>) drawing.getRoot().getChildNodes().get(0);

		int initial = node.getControlAreas().size();
		assertFalse("a rectangle has control points", initial == 0);

		for (ShapeType type : new ShapeType[] { ShapeType.OVAL, ShapeType.CIRCLE, ShapeType.RECTANGLE, ShapeType.LOSANGE,
				ShapeType.TRIANGLE }) {
			node.setShapeSpecification(FACTORY.makeShape(type));
			assertSame(type, node.getShape().getShapeType());
			// What the views of the node show when it is selected or focused
			assertFalse(type + " has lost its control areas", node.getControlAreas().isEmpty());
			assertEquals(type + ": the control areas of the node are the ones of its shape", node.getShape().getControlAreas().size(),
					node.getControlAreas().size());
			assertTrue(type + ": the control areas of the node are the ones of its current shape",
					node.getControlAreas().containsAll(node.getShape().getControlAreas()));
			assertFalse(type + " has lost its control areas", node.getShape().getControlAreas().isEmpty());
			assertFalse(type + " has lost its control points", node.getShape().getControlPoints().isEmpty());
			assertEquals(node.getShape().getControlPoints().size(), node.getShape().getControlAreas().size());
			for (org.openflexo.diana.cp.ControlPoint cp : node.getShape().getControlPoints()) {
				assertSame("a control point of the old shape is kept", node, cp.getNode());
			}
		}
	}
}
