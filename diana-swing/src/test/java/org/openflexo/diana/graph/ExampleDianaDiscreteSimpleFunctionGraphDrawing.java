/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-swing, a component of the software infrastructure 
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

package org.openflexo.diana.graph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.GRStructureVisitor;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.diana.GRBinding.DrawingGRBinding;
import org.openflexo.diana.GRBinding.GraphGRBinding;
import org.openflexo.diana.GRProvider.DrawingGRProvider;
import org.openflexo.diana.GRProvider.ShapeGRProvider;
import org.openflexo.diana.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.diana.graph.DianaDiscreteSimpleFunctionGraph;
import org.openflexo.diana.graph.DianaNumericFunction;
import org.openflexo.diana.graph.DianaFunction.DianaGraphType;
import org.openflexo.diana.graph.DianaSimpleFunctionGraph.Orientation;
import org.openflexo.diana.impl.DrawingImpl;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.diana.test.data.SimplePerson;

/**
 * This is an example of drawing containing a {@link DianaDiscreteSimpleFunctionGraph} showing 2 functions represented as bar graphs
 * 
 * @author sylvain
 * 
 */
public class ExampleDianaDiscreteSimpleFunctionGraphDrawing extends DrawingImpl<Object> {

	private DianaDiscreteSimpleFunctionGraph<SimplePerson> graph;
	private DianaNumericFunction<Integer> sizeFunction;
	private DianaNumericFunction<Double> weightFunction;
	private DianaNumericFunction<Double> bmiFunction;

	private DrawingGraphicalRepresentation drawingRepresentation;
	private ShapeGraphicalRepresentation graphGR;

	private SimplePerson martin, mary, john, martinJr;

	public ExampleDianaDiscreteSimpleFunctionGraphDrawing(Object obj, DianaModelFactory factory) {
		super(obj, factory, PersistenceMode.SharedGraphicalRepresentations);
	}

	@Override
	public void init() {

		List<SimplePerson> persons = new ArrayList<>();
		persons.add(martin = new SimplePerson("Martin", 173, 73.7));
		persons.add(mary = new SimplePerson("Mary", 165, 57.0));
		persons.add(john = new SimplePerson("John", 107, 26.3));
		persons.add(martinJr = new SimplePerson("Martin Jr", 97, 19.2));

		graph = new DianaDiscreteSimpleFunctionGraph<>();

		graph.setParameter("person", SimplePerson.class);
		graph.setDiscreteValues(persons);
		graph.setDiscreteValuesLabel(new DataBinding<String>("person.name"));
		graph.setParameterOrientation(Orientation.HORIZONTAL);

		sizeFunction = graph.addNumericFunction("size", Integer.class, new DataBinding<Integer>("person.size"), DianaGraphType.BAR_GRAPH);
		sizeFunction.setRange(0, 200);
		sizeFunction.setForegroundStyle(getFactory().makeForegroundStyle(Color.BLUE, 1.0f));
		sizeFunction
				.setBackgroundStyle(getFactory().makeColorGradientBackground(Color.BLUE, Color.WHITE, ColorGradientDirection.NORTH_SOUTH));

		weightFunction = graph.addNumericFunction("weight", Double.class, new DataBinding<Double>("person.weight"),
				DianaGraphType.BAR_GRAPH);
		weightFunction.setForegroundStyle(getFactory().makeForegroundStyle(Color.ORANGE, 1.0f));
		weightFunction.setBackgroundStyle(
				getFactory().makeColorGradientBackground(Color.YELLOW, Color.ORANGE, ColorGradientDirection.NORTH_SOUTH));
		weightFunction.setRange(0.0, 100.0);

		bmiFunction = graph.addNumericFunction("bmi", Double.class, new DataBinding<Double>("person.weight / (person.size * person.size)"),
				DianaGraphType.POLYLIN);
		bmiFunction.setForegroundStyle(getFactory().makeForegroundStyle(Color.RED, 3.0f));
		bmiFunction.setRange(0.001, 0.004);

		drawingRepresentation = getFactory().makeDrawingGraphicalRepresentation();

		final DrawingGRBinding<Object> drawingBinding = bindDrawing(Object.class, "drawing", new DrawingGRProvider<Object>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(Object drawable, DianaModelFactory factory) {
				return drawingRepresentation;
			}
		});

		graphGR = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		graphGR.setText("This is an example of 2 discretes functions represented as bargraph");
		graphGR.setX(50);
		graphGR.setY(50);
		graphGR.setWidth(600);
		graphGR.setHeight(400);
		graphGR.setIsFloatingLabel(true);
		graphGR.setAbsoluteTextX(400);
		graphGR.setAbsoluteTextY(-10);
		graphGR.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		graphGR.setTextStyle(getFactory().makeTextStyle(Color.BLACK, DianaConstants.DEFAULT_TEXT_FONT));
		graphGR.setShadowStyle(getFactory().makeNoneShadowStyle());
		graphGR.setBackground(getFactory().makeColorGradientBackground(DianaConstants.DEFAULT_BACKGROUND_COLOR, Color.white,
				ColorGradientDirection.NORTH_WEST_SOUTH_EAST));
		graphGR.setForeground(getFactory().makeForegroundStyle(Color.ORANGE));
		// Very important: give some place for labels, legend and other informations
		// graphGR.setBorder(getFactory().makeShapeBorder(20, 20, 20, 20));

		final GraphGRBinding<DianaDiscreteSimpleFunctionGraph> graphBinding = bindGraph(DianaDiscreteSimpleFunctionGraph.class, "graph",
				new ShapeGRProvider<DianaDiscreteSimpleFunctionGraph>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(DianaDiscreteSimpleFunctionGraph drawable, DianaModelFactory factory) {
						return graphGR;
					}
				});

		drawingBinding.addToWalkers(new GRStructureVisitor<Object>() {

			@Override
			public void visit(Object route) {
				drawGraph(graphBinding, graph);
			}
		});

	}
}
