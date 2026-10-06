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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

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
import org.openflexo.diana.cp.ShapeResizingControlPoint;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.impl.DrawingImpl;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.pamela.exceptions.ModelDefinitionException;

/**
 * A square and a circle keep their width equal to their height: whatever the way their size is changed, and from the corners of their
 * bounds (their sides cannot be dragged)
 */
public class TestSquareCircleResize {

	private static DianaModelFactory FACTORY;

	@BeforeClass
	public static void beforeClass() throws ModelDefinitionException {
		FACTORY = new DianaModelFactoryImpl();
	}

	private static ShapeNode<TestGraphNode> makeNode(ShapeType shapeType, double width, double height) {
		TestGraph graph = new TestGraph();
		new TestGraphNode("node", graph);
		DrawingImpl<TestGraph> drawing = new DrawingImpl<TestGraph>(graph, FACTORY, DrawingImpl.PersistenceMode.UniqueGraphicalRepresentations) {
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
						ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation(shapeType);
						gr.setWidth(width);
						gr.setHeight(height);
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
		};
		@SuppressWarnings("unchecked")
		ShapeNode<TestGraphNode> node = (ShapeNode<TestGraphNode>) drawing.getRoot().getChildNodes().get(0);
		return node;
	}

	private static List<ShapeResizingControlPoint> resizingControlPoints(ShapeNode<?> node) {
		List<ShapeResizingControlPoint> returned = new ArrayList<>();
		for (Object area : node.getControlAreas()) {
			if (area instanceof ShapeResizingControlPoint) {
				returned.add((ShapeResizingControlPoint) area);
			}
		}
		return returned;
	}

	private static boolean isCorner(ShapeResizingControlPoint cp) {
		return (cp.getPoint().x == 0 || cp.getPoint().x == 1) && (cp.getPoint().y == 0 || cp.getPoint().y == 1);
	}

	/** Whatever the way the size is changed, the biggest dimension wins */
	@Test
	public void testSizeStaysSquare() {
		for (ShapeType type : new ShapeType[] { ShapeType.SQUARE, ShapeType.CIRCLE }) {
			ShapeNode<TestGraphNode> node = makeNode(type, 40, 40);
			node.setSize(new DianaDimension(60, 30));
			assertEquals(type + " width", 60, node.getWidth(), 0.001);
			assertEquals(type + " height", 60, node.getHeight(), 0.001);
			node.setHeight(80);
			assertEquals(type + " width", 80, node.getWidth(), 0.001);
			assertEquals(type + " height", 80, node.getHeight(), 0.001);
			node.setWidth(50);
			// 50 x 80: the biggest wins, nothing changes
			assertEquals(type + " width", 80, node.getWidth(), 0.001);
			assertEquals(type + " height", 80, node.getHeight(), 0.001);
		}
	}

	/** A rectangle is still free */
	@Test
	public void testRectangleIsStillFree() {
		ShapeNode<TestGraphNode> node = makeNode(ShapeType.RECTANGLE, 40, 40);
		node.setSize(new DianaDimension(60, 30));
		assertEquals(60, node.getWidth(), 0.001);
		assertEquals(30, node.getHeight(), 0.001);
	}

	/** A rectangle or an oval turned into a square or a circle is squared */
	@Test
	public void testChangeTypeSquares() {
		ShapeNode<TestGraphNode> node = makeNode(ShapeType.RECTANGLE, 40, 20);
		node.setShapeSpecification(FACTORY.makeShape(ShapeType.CIRCLE));
		assertEquals(40, node.getWidth(), 0.001);
		assertEquals(40, node.getHeight(), 0.001);

		node = makeNode(ShapeType.OVAL, 30, 50);
		node.setShapeSpecification(FACTORY.makeShape(ShapeType.SQUARE));
		assertEquals(50, node.getWidth(), 0.001);
		assertEquals(50, node.getHeight(), 0.001);
	}

	/** The corners of the bounds are the active handles: the circle gets them, and their sides are disabled */
	@Test
	public void testHandles() {
		for (ShapeType type : new ShapeType[] { ShapeType.SQUARE, ShapeType.CIRCLE }) {
			ShapeNode<TestGraphNode> node = makeNode(type, 40, 40);
			int draggableCorners = 0;
			for (ShapeResizingControlPoint cp : resizingControlPoints(node)) {
				if (isCorner(cp)) {
					assertTrue(type + " corner " + cp.getPoint() + " must be draggable", cp.isDraggable());
					draggableCorners++;
				}
				else {
					assertFalse(type + " side " + cp.getPoint() + " must not be draggable", cp.isDraggable());
				}
			}
			assertEquals(type + " has the four corners", 4, draggableCorners);
		}

		// A rectangle keeps all of its handles, active
		for (ShapeResizingControlPoint cp : resizingControlPoints(makeNode(ShapeType.RECTANGLE, 40, 40))) {
			assertTrue(cp.isDraggable());
		}
		// ... and an oval its four sides
		assertEquals(4, resizingControlPoints(makeNode(ShapeType.OVAL, 40, 40)).size());
	}

	/** Dragging a corner resizes the shape as a square, the opposite corner staying where it is */
	@Test
	public void testDragCorners() {
		for (ShapeType type : new ShapeType[] { ShapeType.SQUARE, ShapeType.CIRCLE }) {
			for (ShapeResizingControlPoint cornerReference : resizingControlPoints(makeNode(type, 40, 40))) {
				if (!isCorner(cornerReference)) {
					continue;
				}
				ShapeNode<TestGraphNode> node = makeNode(type, 40, 40);
				node.setLocation(new DianaPoint(100, 100));
				ShapeResizingControlPoint cp = null;
				for (ShapeResizingControlPoint candidate : resizingControlPoints(node)) {
					if (isCorner(candidate) && candidate.getPoint().equals(cornerReference.getPoint())) {
						cp = candidate;
					}
				}
				DianaPoint corner = cp.getPoint();
				// The cursor goes further away from the opposite corner, and aside
				double dx = corner.x == 1 ? 0.5 : -0.5;
				double dy = corner.y == 1 ? 0.2 : -0.2;
				DianaPoint cursor = new DianaPoint(corner.x + dx, corner.y + dy);

				cp.startDragging(null, corner);
				cp.dragToPoint(cursor, cursor, cursor, corner, null);
				cp.stopDragging(null, null);

				// The projection of the cursor on the diagonal is 1.35 times the initial size away from the opposite corner
				assertEquals(type + " " + corner + " width", 54, node.getWidth(), 0.001);
				assertEquals(type + " " + corner + " height", 54, node.getHeight(), 0.001);
				// The opposite corner did not move
				double oppositeX = corner.x == 1 ? 100 : 140;
				double oppositeY = corner.y == 1 ? 100 : 140;
				assertEquals(type + " " + corner + " x of the opposite corner", oppositeX,
						corner.x == 1 ? node.getX() : node.getX() + node.getWidth(), 0.001);
				assertEquals(type + " " + corner + " y of the opposite corner", oppositeY,
						corner.y == 1 ? node.getY() : node.getY() + node.getHeight(), 0.001);
			}
		}
	}

	/** The corners of a rectangle still resize it freely */
	@Test
	public void testRectangleCornerStillFree() {
		ShapeNode<TestGraphNode> node = makeNode(ShapeType.RECTANGLE, 40, 40);
		ShapeResizingControlPoint cp = null;
		for (ShapeResizingControlPoint candidate : resizingControlPoints(node)) {
			if (candidate.getPoint().x == 1 && candidate.getPoint().y == 1) {
				cp = candidate;
			}
		}
		DianaPoint cursor = new DianaPoint(1.5, 1.2);
		cp.startDragging(null, cp.getPoint());
		cp.dragToPoint(cursor, cursor, cursor, cp.getPoint(), null);
		cp.stopDragging(null, null);
		assertEquals(60, node.getWidth(), 0.001);
		assertEquals(48, node.getHeight(), 0.001);
	}
}
