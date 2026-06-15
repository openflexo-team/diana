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
import org.openflexo.diana.layout.GridBagAnchor;
import org.openflexo.diana.layout.GridBagFill;
import org.openflexo.diana.layout.GridBagLayoutConstraints;
import org.openflexo.diana.layout.GridBagLayoutManagerSpecification;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.diana.test.TestGraph;
import org.openflexo.diana.test.TestGraphNode;

/**
 * Demonstrates {@link org.openflexo.diana.layout.GridBagLayoutManager} on a <b>resizable container shape</b>: a small two-column form. The
 * root node is the container box; its children are arranged as rows of {@code label : field}, the <b>label</b> column anchored WEST (own
 * size) and the <b>field</b> column stretched (weightX=1, fill HORIZONTAL).
 *
 * <p>
 * Resize the box: the label column keeps its width while the field column absorbs the extra width; vertically the rows keep their height.
 *
 * @author sylvain
 *
 */
public class GridBagLayoutManagerDrawing extends DrawingImpl<TestGraph> {

	/** Children, in declared order: (label, field) pairs — even index = label (column 0), odd = field (column 1). */
	private static final String[] CELLS = { "name:", "String", "type:", "Person", "owner:", "Company" };

	public GridBagLayoutManagerDrawing(TestGraph graph, DianaModelFactory factory) {
		// UniqueGraphicalRepresentations so the Location/Size inspector controls the shapes (geometry stored in the GR,
		// not node-local as in Shared mode — see diana-analysis.md §21.2).
		super(graph, factory, PersistenceMode.UniqueGraphicalRepresentations);
	}

	/**
	 * Builds a small graph: the root node is the container box; its other nodes are the form cells (label/field pairs).
	 */
	public static TestGraph makeGridBagGraph() {
		TestGraph graph = new TestGraph();
		TestGraphNode box = new TestGraphNode("Form (gridbag layout)", graph);
		graph.setRootNode(box);
		for (String cell : CELLS) {
			new TestGraphNode(cell, graph);
		}
		return graph;
	}

	@Override
	public void init() {

		DrawingGraphicalRepresentation drawingGR = getFactory().makeDrawingGraphicalRepresentation();
		drawingGR.setWidth(380);
		drawingGR.setHeight(280);

		final DrawingGRBinding<TestGraph> drawingBinding = bindDrawing(TestGraph.class, "graph", new DrawingGRProvider<TestGraph>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(TestGraph drawable, DianaModelFactory factory) {
				return drawingGR;
			}
		});

		// The resizable container box (the root node). It owns the GridBagLayoutManager.
		final ShapeGRBinding<TestGraphNode> boxBinding = bindShape(TestGraphNode.class, "box", new ShapeGRProvider<TestGraphNode>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(TestGraphNode node, DianaModelFactory factory) {
				ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
				gr.setX(40);
				gr.setY(30);
				gr.setWidth(300);
				gr.setHeight(160);
				gr.setBackground(factory.makeColoredBackground(Color.white));
				gr.setForeground(factory.makeForegroundStyle(Color.darkGray, 1.5f));

				GridBagLayoutManagerSpecification gridbag = factory.makeLayoutManagerSpecification("gridbag",
						GridBagLayoutManagerSpecification.class);
				gridbag.setHgap(8);
				gridbag.setVgap(6);
				gridbag.setInsetTop(8);
				gridbag.setInsetBottom(8);
				gridbag.setInsetLeft(8);
				gridbag.setInsetRight(8);
				gr.addToLayoutManagerSpecifications(gridbag);
				return gr;
			}
		});

		// One GR per form cell. Children are selectable so they can be inspected (Location/Size + grid constraints).
		final ShapeGRBinding<TestGraphNode> cellBinding = bindShape(TestGraphNode.class, "cell", new ShapeGRProvider<TestGraphNode>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(TestGraphNode node, DianaModelFactory factory) {
				int index = childIndexOf(node);
				int row = index / 2;
				boolean isLabel = (index % 2 == 0);

				ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
				gr.setLayoutManagerIdentifier("gridbag");
				GridBagLayoutConstraints lc = factory.newInstance(GridBagLayoutConstraints.class);
				lc.setGridX(isLabel ? 0 : 1);
				lc.setGridY(row);
				gr.setIsFloatingLabel(false);
				gr.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
				gr.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);

				if (isLabel) {
					// Label column: own size, anchored to the left (WEST), no growth
					lc.setAnchor(GridBagAnchor.WEST);
					lc.setFill(GridBagFill.NONE);
					lc.setWeightX(0);
					gr.setBackground(factory.makeColoredBackground(new Color(225, 235, 250)));
					gr.setWidth(60);
					gr.setHeight(24);
				}
				else {
					// Field column: stretched horizontally, absorbs the extra width
					lc.setFill(GridBagFill.HORIZONTAL);
					lc.setWeightX(1);
					gr.setBackground(factory.makeColoredBackground(Color.white));
					gr.setForeground(factory.makeForegroundStyle(Color.gray, 1f));
					gr.setWidth(120);
					gr.setHeight(24);
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

		// Draw each form cell as a child of the box
		boxBinding.addToWalkers(new GRStructureVisitor<TestGraphNode>() {
			@Override
			public void visit(TestGraphNode box) {
				for (TestGraphNode node : box.getGraph().getNodes()) {
					if (node != box) {
						drawShape(cellBinding, node, box);
					}
				}
			}
		});

		// The box keeps its size/location in the model (so resize persists and re-triggers layout)
		boxBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.X, new DataBinding<Double>("drawable.x"), true);
		boxBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.Y, new DataBinding<Double>("drawable.y"), true);

		cellBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), false);
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
