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
import org.openflexo.diana.layout.BoxLayoutManagerSpecification;
import org.openflexo.diana.layout.BoxLayoutManagerSpecification.CrossAxisPolicy;
import org.openflexo.diana.layout.BoxLayoutManagerSpecification.MainAxisPolicy;
import org.openflexo.diana.layout.BoxLayoutManagerSpecification.Orientation;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.diana.test.TestGraph;
import org.openflexo.diana.test.TestGraphNode;

/**
 * Variant of {@link BoxCompartmentDrawing} demonstrating the <b>header-as-inset</b> mode (decision Q1, the one pamela-editor will use).
 *
 * <p>
 * Here the header is <b>not</b> a layouted child: the container box reserves its band with {@code insetTop = HEADER_HEIGHT}, and the header is
 * a separate child pinned to the top by its own constraint bindings ({@code x=0}, {@code y=0}, {@code width=parent.width},
 * {@code height=HEADER_HEIGHT}). The {@code BoxLayoutManager} therefore only sees and stacks the three compartments, inside the space
 * <i>below</i> the inset — it never touches the header.
 *
 * <p>
 * Because the header's geometry is expressed with {@code parent.width} constraint bindings (which resolve against the parent <i>GR</i>), this
 * drawing must use {@link PersistenceMode#UniqueGraphicalRepresentations} so the parent GR width tracks live resizing (see
 * {@code diana-analysis.md §21.2}). This is exactly the constraint-bindings + {@code BoxLayoutManager} combination pamela-editor relies on.
 *
 * @author sylvain
 *
 */
public class BoxCompartmentInsetDrawing extends DrawingImpl<TestGraph> {

	private static final double HEADER_HEIGHT = 26;

	public BoxCompartmentInsetDrawing(TestGraph graph, DianaModelFactory factory) {
		super(graph, factory, PersistenceMode.UniqueGraphicalRepresentations);
	}

	/** Root node = container box; first non-root = header; remaining = compartments. */
	public static TestGraph makeCompartmentGraph() {
		TestGraph graph = new TestGraph();
		TestGraphNode box = new TestGraphNode("MyEntity", graph);
		graph.setRootNode(box);
		new TestGraphNode("MyEntity", graph); // header (pinned, not layouted)
		new TestGraphNode("Initializers (weight 1)", graph);
		new TestGraphNode("Properties (weight 3)", graph);
		new TestGraphNode("Methods (weight 2)", graph);
		return graph;
	}

	/** Index of a node among the non-root nodes (0 = header, 1.. = compartments), or -1. */
	private int nonRootIndex(TestGraphNode node) {
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

	@Override
	public void init() {

		DrawingGraphicalRepresentation drawingGR = getFactory().makeDrawingGraphicalRepresentation();

		final DrawingGRBinding<TestGraph> drawingBinding = bindDrawing(TestGraph.class, "graph", new DrawingGRProvider<TestGraph>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(TestGraph drawable, DianaModelFactory factory) {
				return drawingGR;
			}
		});

		// The resizable container box. Reserves the header band with insetTop, and owns the BoxLayoutManager.
		final ShapeGRBinding<TestGraphNode> boxBinding = bindShape(TestGraphNode.class, "box", new ShapeGRProvider<TestGraphNode>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(TestGraphNode node, DianaModelFactory factory) {
				ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
				gr.setX(60);
				gr.setY(40);
				gr.setWidth(220);
				gr.setHeight(180);
				gr.setBackground(factory.makeColoredBackground(Color.white));
				gr.setForeground(factory.makeForegroundStyle(Color.darkGray, 1.5f));

				BoxLayoutManagerSpecification box = factory.makeLayoutManagerSpecification("box", BoxLayoutManagerSpecification.class);
				box.setOrientation(Orientation.VERTICAL);
				box.setCrossAxisPolicy(CrossAxisPolicy.STRETCH);
				box.setMainAxisPolicy(MainAxisPolicy.DISTRIBUTE_WEIGHTS);
				box.setGap(4);
				box.setInsetTop(HEADER_HEIGHT); // <-- reserve the header band; compartments start below it
				box.setInsetBottom(6);
				box.setInsetLeft(6);
				box.setInsetRight(6);
				gr.addToLayoutManagerSpecifications(box);
				return gr;
			}
		});

		// The header: NOT layouted. Pinned to the top of the box by its own constraint bindings.
		final ShapeGRBinding<TestGraphNode> headerBinding = bindShape(TestGraphNode.class, "header", new ShapeGRProvider<TestGraphNode>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(TestGraphNode node, DianaModelFactory factory) {
				ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
				// No layoutManagerIdentifier -> ignored by the BoxLayoutManager.
				// Explicit initial geometry first (before constraints), so the header starts pinned
				// to the top-left; the constraints then keep it there and track the box width.
				gr.setX(0);
				gr.setY(0);
				gr.setWidth(220);
				gr.setHeight(HEADER_HEIGHT);
				gr.setXConstraints(new DataBinding<Double>("0"));
				gr.setYConstraints(new DataBinding<Double>("0"));
				gr.setWidthConstraints(new DataBinding<Double>("parent.width"));
				gr.setHeightConstraints(new DataBinding<Double>(String.valueOf(HEADER_HEIGHT)));
				gr.setBackground(factory.makeColoredBackground(new Color(80, 110, 170)));
				gr.setIsFloatingLabel(false);
				gr.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
				gr.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
				gr.setIsSelectable(false);
				gr.setIsFocusable(false);
				return gr;
			}
		});

		// The compartments: layouted children, with heterogeneous weights.
		final ShapeGRBinding<TestGraphNode> rowBinding = bindShape(TestGraphNode.class, "row", new ShapeGRProvider<TestGraphNode>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(TestGraphNode node, DianaModelFactory factory) {
				ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
				gr.setLayoutManagerIdentifier("box");
				gr.setIsFloatingLabel(false);
				gr.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
				gr.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
				gr.setIsSelectable(false);
				gr.setIsFocusable(false);

				double[] weights = { 0, 1, 3, 2 }; // index 0 is the header (handled separately)
				int idx = nonRootIndex(node);
				gr.setLayoutWeight(idx >= 0 && idx < weights.length ? weights[idx] : 1);
				gr.setBackground(factory.makeColoredBackground(idx % 2 == 0 ? new Color(225, 235, 250) : new Color(240, 244, 250)));
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

		// Draw the header (pinned) and the compartments (layouted) as children of the box
		boxBinding.addToWalkers(new GRStructureVisitor<TestGraphNode>() {
			@Override
			public void visit(TestGraphNode box) {
				for (TestGraphNode node : box.getGraph().getNodes()) {
					if (node == box) {
						continue;
					}
					if (nonRootIndex(node) == 0) {
						drawShape(headerBinding, node, box); // header band
					}
					else {
						drawShape(rowBinding, node, box); // compartment
					}
				}
			}
		});

		// Box keeps its size/location in the model (so resize persists and re-triggers layout)
		boxBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.X, new DataBinding<Double>("drawable.x"), true);
		boxBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.Y, new DataBinding<Double>("drawable.y"), true);

		headerBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), false);
		rowBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), false);
	}
}
