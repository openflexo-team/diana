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
import org.openflexo.diana.layout.WrapFlowLayoutManagerSpecification;
import org.openflexo.diana.layout.WrapFlowLayoutManagerSpecification.LineAlignment;
import org.openflexo.diana.layout.WrapFlowLayoutManagerSpecification.Orientation;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.diana.test.TestGraph;
import org.openflexo.diana.test.TestGraphNode;

/**
 * Demonstrates {@link org.openflexo.diana.layout.WrapFlowLayoutManager} on a <b>resizable container shape</b>: a set of variable-width
 * items that flow left-to-right and wrap to a new line when the container width is exceeded. Resize the box narrower/wider to see the items
 * re-wrap live; switch the line alignment / orientation in the Layout Manager inspector.
 *
 * @author sylvain
 *
 */
public class WrapFlowLayoutManagerDrawing extends DrawingImpl<TestGraph> {

	/** Item labels of varying length → varying widths, to exercise wrapping. */
	private static final String[] ITEMS = { "name", "type", "owner", "description", "id", "createdAt", "tags", "status", "priority",
			"assignee", "due", "notes" };

	public WrapFlowLayoutManagerDrawing(TestGraph graph, DianaModelFactory factory) {
		// UniqueGraphicalRepresentations: node geometry is stored in (and read from) the GR, so the Location/Size
		// inspector actually controls the shapes. In SharedGraphicalRepresentations the geometry is node-local and the
		// inspector's writes to the GR would have no effect (see diana-analysis.md §21.2).
		super(graph, factory, PersistenceMode.UniqueGraphicalRepresentations);
	}

	/** Root node = the container box; the other nodes are the wrapped items. */
	public static TestGraph makeWrapFlowGraph() {
		TestGraph graph = new TestGraph();
		TestGraphNode box = new TestGraphNode("Wrap-flow", graph);
		graph.setRootNode(box);
		for (String item : ITEMS) {
			new TestGraphNode(item, graph);
		}
		return graph;
	}

	@Override
	public void init() {

		DrawingGraphicalRepresentation drawingGR = getFactory().makeDrawingGraphicalRepresentation();
		drawingGR.setWidth(360);
		drawingGR.setHeight(280);

		final DrawingGRBinding<TestGraph> drawingBinding = bindDrawing(TestGraph.class, "graph", new DrawingGRProvider<TestGraph>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(TestGraph drawable, DianaModelFactory factory) {
				return drawingGR;
			}
		});

		final ShapeGRBinding<TestGraphNode> boxBinding = bindShape(TestGraphNode.class, "box", new ShapeGRProvider<TestGraphNode>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(TestGraphNode node, DianaModelFactory factory) {
				ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
				gr.setX(30);
				gr.setY(30);
				gr.setWidth(280);
				gr.setHeight(180);
				gr.setBackground(factory.makeColoredBackground(Color.white));
				gr.setForeground(factory.makeForegroundStyle(Color.darkGray, 1.5f));

				WrapFlowLayoutManagerSpecification wrap = factory.makeLayoutManagerSpecification("wrap-flow",
						WrapFlowLayoutManagerSpecification.class);
				wrap.setOrientation(Orientation.HORIZONTAL);
				wrap.setLineAlignment(LineAlignment.LEADING);
				wrap.setHgap(6);
				wrap.setVgap(6);
				wrap.setInsetTop(8);
				wrap.setInsetBottom(8);
				wrap.setInsetLeft(8);
				wrap.setInsetRight(8);
				gr.addToLayoutManagerSpecifications(wrap);
				return gr;
			}
		});

		// One GR per item, sized to its label. Selectable so it can be inspected.
		final ShapeGRBinding<TestGraphNode> itemBinding = bindShape(TestGraphNode.class, "item", new ShapeGRProvider<TestGraphNode>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(TestGraphNode node, DianaModelFactory factory) {
				ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
				gr.setLayoutManagerIdentifier("wrap-flow");
				gr.setIsFloatingLabel(false);
				gr.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
				gr.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
				gr.setBackground(factory.makeColoredBackground(new Color(210, 230, 250)));
				gr.setForeground(factory.makeForegroundStyle(Color.gray, 1f));
				// Width driven by the label length, so items have heterogeneous sizes to wrap
				gr.setWidth(24 + 8.0 * node.getName().length());
				gr.setHeight(24);
				return gr;
			}
		});

		drawingBinding.addToWalkers(new GRStructureVisitor<TestGraph>() {
			@Override
			public void visit(TestGraph graph) {
				if (graph.getRootNode() != null) {
					drawShape(boxBinding, graph.getRootNode());
				}
			}
		});

		boxBinding.addToWalkers(new GRStructureVisitor<TestGraphNode>() {
			@Override
			public void visit(TestGraphNode box) {
				for (TestGraphNode node : box.getGraph().getNodes()) {
					if (node != box) {
						drawShape(itemBinding, node, box);
					}
				}
			}
		});

		boxBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.X, new DataBinding<Double>("drawable.x"), true);
		boxBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.Y, new DataBinding<Double>("drawable.y"), true);

		itemBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), false);
	}
}
