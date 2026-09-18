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
import org.openflexo.diana.layout.BoxLayoutConstraints;
import org.openflexo.diana.layout.BoxLayoutManagerSpecification;
import org.openflexo.diana.layout.BoxLayoutManagerSpecification.CrossAxisPolicy;
import org.openflexo.diana.layout.BoxLayoutManagerSpecification.MainAxisPolicy;
import org.openflexo.diana.layout.BoxLayoutManagerSpecification.Orientation;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.diana.test.TestGraph;
import org.openflexo.diana.test.TestGraphNode;

/**
 * Faithful demonstration of {@link org.openflexo.diana.layout.BoxLayoutManager} reproducing the UML-class-box compartment mechanic.
 *
 * <p>
 * Unlike {@link BoxLayoutManagerDrawing} (where the drawing itself was the container, hence not resizable), here the container is a
 * <b>resizable shape</b> placed in the drawing — exactly like the {@code EntityView} box in pamela-editor. Select the box and drag its resize
 * handles: its children re-layout live.
 *
 * <p>
 * The box shape carries a single {@code BoxLayoutManager} (VERTICAL, cross-axis STRETCH, insets + gap). Its children combine the two sizing
 * modes pamela-editor needs:
 * <ul>
 * <li>the <b>header</b> row has {@code layoutWeight == 0} (fixed height 26) — it keeps a constant extent whatever the box height;</li>
 * <li>the <b>compartment</b> rows have <b>heterogeneous weights</b> (1, 3, 2) — they share the remaining height proportionally.</li>
 * </ul>
 * Resizing the box keeps the header at 26 px and grows/shrinks the compartments proportionally, with no drift across repeated resizes.
 *
 * @author sylvain
 *
 */
public class BoxCompartmentDrawing extends DrawingImpl<TestGraph> {

	private static final double HEADER_HEIGHT = 26;

	public BoxCompartmentDrawing(TestGraph graph, DianaModelFactory factory) {
		super(graph, factory, PersistenceMode.SharedGraphicalRepresentations);
	}

	/**
	 * Builds a small graph. The root node is the container box; its other nodes are the rows (a header + three compartments).
	 */
	public static TestGraph makeCompartmentGraph() {
		TestGraph graph = new TestGraph();
		TestGraphNode box = new TestGraphNode("MyEntity", graph); // the container box
		graph.setRootNode(box);
		new TestGraphNode("MyEntity", graph); // row 0 -> header (weight 0, fixed)
		new TestGraphNode("Initializers (weight 1)", graph); // row 1 -> weight 1
		new TestGraphNode("Properties (weight 3)", graph); // row 2 -> weight 3
		new TestGraphNode("Methods (weight 2)", graph); // row 3 -> weight 2
		return graph;
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

		// The resizable container box (the root node). It owns the BoxLayoutManager.
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
				box.setInsetTop(6);
				box.setInsetBottom(6);
				box.setInsetLeft(6);
				box.setInsetRight(6);
				gr.addToLayoutManagerSpecifications(box);
				return gr;
			}
		});

		// One GR per row so each can carry its own weight / color. Rows are not directly interactive:
		// the layout manager places them, and the user resizes the box, not the rows.
		final ShapeGRBinding<TestGraphNode> rowBinding = bindShape(TestGraphNode.class, "row", new ShapeGRProvider<TestGraphNode>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(TestGraphNode node, DianaModelFactory factory) {
				ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
				gr.setLayoutManagerIdentifier("box");
				// Non-floating label, centered in the row (a floating label would be drawn at the
				// row's top-left and appear clipped on a small row).
				gr.setIsFloatingLabel(false);
				gr.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
				gr.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
				gr.setIsSelectable(false);
				gr.setIsFocusable(false);

				int rowIndex = rowIndexOf(node); // 0 = header, 1..3 = compartments
				BoxLayoutConstraints lc = factory.newInstance(BoxLayoutConstraints.class);
				if (rowIndex == 0) {
					lc.setWeight(0); // fixed extent
					gr.setHeight(HEADER_HEIGHT);
					gr.setBackground(factory.makeColoredBackground(new Color(80, 110, 170)));
				}
				else {
					double[] weights = { 0, 1, 3, 2 };
					lc.setWeight(rowIndex < weights.length ? weights[rowIndex] : 1);
					gr.setBackground(
							factory.makeColoredBackground(rowIndex % 2 == 0 ? new Color(225, 235, 250) : new Color(240, 244, 250)));
				}
				gr.setLayoutConstraints(lc);
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

		// Draw each row as a child of the box
		boxBinding.addToWalkers(new GRStructureVisitor<TestGraphNode>() {
			@Override
			public void visit(TestGraphNode box) {
				for (TestGraphNode node : box.getGraph().getNodes()) {
					if (node != box) {
						drawShape(rowBinding, node, box);
					}
				}
			}
		});

		// The box keeps its size/location in the model (so resize persists and re-triggers layout)
		boxBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.X, new DataBinding<Double>("drawable.x"), true);
		boxBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.Y, new DataBinding<Double>("drawable.y"), true);

		rowBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), false);
	}

	/** Index of a row among the non-root nodes (0 = header). */
	private int rowIndexOf(TestGraphNode node) {
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
