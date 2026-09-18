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
 * Demonstrates {@link org.openflexo.diana.layout.BoxLayoutManager}: the nodes of a {@link TestGraph} are stacked vertically inside the
 * drawing, full-width (cross-axis STRETCH), with a fixed row height (weight 0) and a gap, leaving the configured insets around them.
 *
 * @author sylvain
 *
 */
public class BoxLayoutManagerDrawing extends DrawingImpl<TestGraph> {

	private DrawingGraphicalRepresentation graphRepresentation;
	private ShapeGraphicalRepresentation nodeRepresentation;

	public BoxLayoutManagerDrawing(TestGraph graph, DianaModelFactory factory) {
		super(graph, factory, PersistenceMode.SharedGraphicalRepresentations);
	}

	@Override
	public void init() {

		graphRepresentation = getFactory().makeDrawingGraphicalRepresentation();

		BoxLayoutManagerSpecification boxLayout = getFactory().makeLayoutManagerSpecification("box", BoxLayoutManagerSpecification.class);
		boxLayout.setOrientation(Orientation.VERTICAL);
		boxLayout.setCrossAxisPolicy(CrossAxisPolicy.STRETCH);
		boxLayout.setMainAxisPolicy(MainAxisPolicy.PACK_START);
		boxLayout.setGap(8);
		boxLayout.setInsetTop(10);
		boxLayout.setInsetBottom(10);
		boxLayout.setInsetLeft(10);
		boxLayout.setInsetRight(10);
		graphRepresentation.addToLayoutManagerSpecifications(boxLayout);

		nodeRepresentation = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		nodeRepresentation.setBackground(getFactory().makeColoredBackground(Color.cyan));
		nodeRepresentation.setLayoutManagerIdentifier("box");
		nodeRepresentation.setWidth(120);
		nodeRepresentation.setHeight(24);

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

		graphBinding.addToWalkers(new GRStructureVisitor<TestGraph>() {
			@Override
			public void visit(TestGraph graph) {
				for (TestGraphNode node : graph.getNodes()) {
					drawShape(nodeBinding, node);
				}
			}
		});

		nodeBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);
	}
}
