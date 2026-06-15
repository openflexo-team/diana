/**
 *
 * Copyright (c) 2024, Openflexo
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
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.GRBinding.DrawingGRBinding;
import org.openflexo.diana.GRBinding.ShapeGRBinding;
import org.openflexo.diana.GRProvider.DrawingGRProvider;
import org.openflexo.diana.GRProvider.ShapeGRProvider;
import org.openflexo.diana.GRStructureVisitor;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.diana.GraphicalRepresentation.VerticalTextAlignment;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.impl.DrawingImpl;
import org.openflexo.diana.layout.BorderLayoutConstraints;
import org.openflexo.diana.layout.BorderLayoutManagerSpecification;
import org.openflexo.diana.layout.BorderRegion;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.diana.test.TestGraph;
import org.openflexo.diana.test.TestGraphNode;

/**
 * Demonstrates {@link org.openflexo.diana.layout.BorderLayoutManager} on a <b>resizable container shape</b> placed in the drawing (the root
 * node is the container box; the other five nodes are its children, one per border region NORTH / SOUTH / WEST / EAST / CENTER).
 *
 * <p>
 * Select the box and drag its resize handles: NORTH/SOUTH keep their own height and follow the width, WEST/EAST keep their own width and
 * follow the middle-band height, and CENTER fills whatever is left — re-laid out live.
 *
 * @author sylvain
 *
 */
public class BorderLayoutManagerDrawing extends DrawingImpl<TestGraph> {

	/** Region assigned to each child node, in declared (non-root) order. */
	private static final BorderRegion[] REGIONS = { BorderRegion.NORTH, BorderRegion.SOUTH, BorderRegion.WEST, BorderRegion.EAST,
			BorderRegion.CENTER };

	private static final Color[] COLORS = { Color.pink, Color.green, Color.orange, Color.yellow, Color.cyan };

	public BorderLayoutManagerDrawing(TestGraph graph, DianaModelFactory factory) {
		super(graph, factory, PersistenceMode.SharedGraphicalRepresentations);
	}

	/**
	 * Builds a small graph: the root node is the container box; its five other nodes are the children placed in the five border regions.
	 */
	public static TestGraph makeBorderGraph() {
		TestGraph graph = new TestGraph();
		TestGraphNode box = new TestGraphNode("Container (border layout)", graph);
		graph.setRootNode(box);
		new TestGraphNode("NORTH", graph);
		new TestGraphNode("SOUTH", graph);
		new TestGraphNode("WEST", graph);
		new TestGraphNode("EAST", graph);
		new TestGraphNode("CENTER", graph);
		return graph;
	}

	@Override
	public void init() {

		DrawingGraphicalRepresentation drawingGR = getFactory().makeDrawingGraphicalRepresentation();
		drawingGR.setWidth(360);
		drawingGR.setHeight(300);

		final DrawingGRBinding<TestGraph> drawingBinding = bindDrawing(TestGraph.class, "graph", new DrawingGRProvider<TestGraph>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(TestGraph drawable, DianaModelFactory factory) {
				return drawingGR;
			}
		});

		// The resizable container box (the root node). It owns the BorderLayoutManager.
		final ShapeGRBinding<TestGraphNode> boxBinding = bindShape(TestGraphNode.class, "box", new ShapeGRProvider<TestGraphNode>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(TestGraphNode node, DianaModelFactory factory) {
				ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
				gr.setX(40);
				gr.setY(30);
				gr.setWidth(280);
				gr.setHeight(220);
				gr.setBackground(factory.makeColoredBackground(Color.white));
				gr.setForeground(factory.makeForegroundStyle(Color.darkGray, 1.5f));

				BorderLayoutManagerSpecification border = factory.makeLayoutManagerSpecification("border",
						BorderLayoutManagerSpecification.class);
				border.setHgap(6);
				border.setVgap(6);
				border.setInsetTop(8);
				border.setInsetBottom(8);
				border.setInsetLeft(8);
				border.setInsetRight(8);
				gr.addToLayoutManagerSpecifications(border);
				return gr;
			}
		});

		// One GR per child region. Children are not directly interactive: the layout manager places them,
		// and the user resizes the box, not the children.
		final ShapeGRBinding<TestGraphNode> regionBinding = bindShape(TestGraphNode.class, "region", new ShapeGRProvider<TestGraphNode>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(TestGraphNode node, DianaModelFactory factory) {
				int index = childIndexOf(node); // 0..4, in declared order
				ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
				gr.setLayoutManagerIdentifier("border");
				BorderLayoutConstraints lc = factory.newInstance(BorderLayoutConstraints.class);
				lc.setRegion(REGIONS[index % REGIONS.length]);
				gr.setLayoutConstraints(lc);
				gr.setBackground(factory.makeColoredBackground(COLORS[index % COLORS.length]));
				// Labels drawn centered inside the shape rather than floating at its top-left corner
				gr.setIsFloatingLabel(false);
				gr.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
				gr.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
				// Children are selectable so they can be inspected (Location/Size + per-child region) — the
				// border layout still re-places them, so a user drag is overridden by the next layout pass.
				gr.setIsSelectable(true);
				gr.setIsFocusable(true);
				// Preferred sizes: NORTH/SOUTH keep their height (width filled), WEST/EAST keep their width (height filled)
				gr.setWidth(80);
				gr.setHeight(36);
				return gr;
			}
		});

		// Draw the box as a child of the drawing
		drawingBinding.addToWalkers(new GRStructureVisitor<TestGraph>() {
			@Override
			public void visit(TestGraph graph) {
				if (graph.getRootNode() != null) {
					drawShape(boxBinding, graph.getRootNode());
				}
			}
		});

		// Draw each region child as a child of the box
		boxBinding.addToWalkers(new GRStructureVisitor<TestGraphNode>() {
			@Override
			public void visit(TestGraphNode box) {
				for (TestGraphNode node : box.getGraph().getNodes()) {
					if (node != box) {
						drawShape(regionBinding, node, box);
					}
				}
			}
		});

		// The box keeps its size/location in the model (so resize persists and re-triggers layout)
		boxBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.X, new DataBinding<Double>("drawable.x"), true);
		boxBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.Y, new DataBinding<Double>("drawable.y"), true);

		regionBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), false);
	}

	/** Index of a node among the non-root nodes (0-based, declared order). */
	private int childIndexOf(TestGraphNode node) {
		int i = 0;
		for (TestGraphNode n : node.getGraph().getNodes()) {
			if (n == node.getGraph().getRootNode()) {
				continue;
			}
			if (n == node) {
				return i;
			}
			i++;
		}
		return -1;
	}
}
