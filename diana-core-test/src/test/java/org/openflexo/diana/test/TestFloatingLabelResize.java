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
import org.openflexo.diana.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.diana.GraphicalRepresentation.VerticalTextAlignment;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.impl.DrawingImpl;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.pamela.exceptions.ModelDefinitionException;

/**
 * The anchor of a floating label follows its shape when the shape is resized, even before the label has a text
 */
public class TestFloatingLabelResize {

	private static DianaModelFactory FACTORY;

	@BeforeClass
	public static void beforeClass() throws ModelDefinitionException {
		FACTORY = new DianaModelFactoryImpl();
	}

	/**
	 * A drawing showing each node of a graph as a 40x40 rectangle, its label floating above it, centered
	 */
	private static class FloatingLabelDrawing extends DrawingImpl<TestGraph> {

		FloatingLabelDrawing(TestGraph graph) {
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
					gr.setIsFloatingLabel(true);
					gr.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
					gr.setVerticalTextAlignment(VerticalTextAlignment.BOTTOM);
					gr.setAbsoluteTextX(20);
					gr.setAbsoluteTextY(-2);
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
	public void testAnchorFollowsResize() {
		TestGraph graph = new TestGraph();
		new TestGraphNode("node", graph);
		FloatingLabelDrawing drawing = new FloatingLabelDrawing(graph);
		@SuppressWarnings("unchecked")
		ShapeNode<TestGraphNode> node = (ShapeNode<TestGraphNode>) drawing.getRoot().getChildNodes().get(0);
		ShapeGraphicalRepresentation gr = node.getGraphicalRepresentation();

		node.setSize(new DianaDimension(80, 60));
		// Still centered horizontally, still 2 pixels above the shape
		assertEquals(40, gr.getAbsoluteTextX(), 0.001);
		assertEquals(-2, gr.getAbsoluteTextY(), 0.001);

		node.setSize(new DianaDimension(30, 30));
		assertEquals(15, gr.getAbsoluteTextX(), 0.001);
		assertEquals(-2, gr.getAbsoluteTextY(), 0.001);
	}
}
