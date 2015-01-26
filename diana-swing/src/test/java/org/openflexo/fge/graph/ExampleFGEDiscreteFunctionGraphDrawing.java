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

package org.openflexo.fge.graph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.GraphGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.graph.FGEFunctionGraph.Orientation;
import org.openflexo.fge.graph.FGEGraph.GraphType;
import org.openflexo.fge.graph.TestFGEDiscreteFunctionGraph.Person;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;

/**
 * This is an example of drawing containing a FGEDiscreteFunctionGraph showing 2 functions represented as bar graphs
 * 
 * @author sylvain
 * 
 */
public class ExampleFGEDiscreteFunctionGraphDrawing extends DrawingImpl<Object> {

	private FGEDiscreteFunctionGraph<Person> graph;
	private FGENumericFunction<Integer> sizeFunction;
	private FGENumericFunction<Double> weightFunction;
	private FGENumericFunction<Double> bmiFunction;

	private DrawingGraphicalRepresentation drawingRepresentation;
	private ShapeGraphicalRepresentation graphGR;

	private Person martin, mary, john, martinJr;

	public ExampleFGEDiscreteFunctionGraphDrawing(Object obj, FGEModelFactory factory) {
		super(obj, factory, PersistenceMode.SharedGraphicalRepresentations);
	}

	@Override
	public void init() {

		List<Person> persons = new ArrayList<Person>();
		persons.add(martin = new Person("Martin", 173, 73.7));
		persons.add(mary = new Person("Mary", 165, 57.0));
		persons.add(john = new Person("John", 107, 26.3));
		persons.add(martinJr = new Person("Martin Jr", 97, 19.2));

		graph = new FGEDiscreteFunctionGraph<Person>();

		graph.setParameter("person", Person.class);
		graph.setDiscreteValues(persons);
		graph.setDiscreteValuesLabel(new DataBinding<String>("person.name"));
		graph.setParameterOrientation(Orientation.HORIZONTAL);

		sizeFunction = graph.addNumericFunction("size", Integer.class, new DataBinding<Integer>("person.size"), GraphType.BAR_GRAPH);
		sizeFunction.setRange(0, 200);
		sizeFunction.setForegroundStyle(getFactory().makeForegroundStyle(Color.BLUE, 1.0f));
		sizeFunction.setBackgroundStyle(getFactory().makeColorGradientBackground(Color.BLUE, Color.WHITE,
				ColorGradientDirection.NORTH_SOUTH));

		weightFunction = graph.addNumericFunction("weight", Double.class, new DataBinding<Double>("person.weight"), GraphType.BAR_GRAPH);
		weightFunction.setForegroundStyle(getFactory().makeForegroundStyle(Color.ORANGE, 1.0f));
		weightFunction.setBackgroundStyle(getFactory().makeColorGradientBackground(Color.YELLOW, Color.ORANGE,
				ColorGradientDirection.NORTH_SOUTH));
		weightFunction.setRange(0.0, 100.0);

		bmiFunction = graph.addNumericFunction("bmi", Double.class, new DataBinding<Double>("person.weight / (person.size * person.size)"),
				GraphType.POLYLIN);
		bmiFunction.setForegroundStyle(getFactory().makeForegroundStyle(Color.RED, 3.0f));
		bmiFunction.setRange(0.001, 0.004);

		drawingRepresentation = getFactory().makeDrawingGraphicalRepresentation();

		final DrawingGRBinding<Object> drawingBinding = bindDrawing(Object.class, "drawing", new DrawingGRProvider<Object>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(Object drawable, FGEModelFactory factory) {
				return drawingRepresentation;
			}
		});

		graphGR = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		graphGR.setText("This is an example of 2 discretes functions represented as bargraph");
		graphGR.setX(50);
		graphGR.setY(50);
		graphGR.setWidth(600);
		graphGR.setHeight(400);
		graphGR.setAbsoluteTextX(20);
		graphGR.setAbsoluteTextY(5);
		graphGR.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		graphGR.setTextStyle(getFactory().makeTextStyle(Color.BLACK, FGEConstants.DEFAULT_TEXT_FONT));
		graphGR.setShadowStyle(getFactory().makeNoneShadowStyle());
		graphGR.setBackground(getFactory().makeColorGradientBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR, Color.white,
				ColorGradientDirection.SOUTH_EAST_NORTH_WEST));
		graphGR.setForeground(getFactory().makeForegroundStyle(Color.ORANGE));
		// Very important: give some place for labels, legend and other informations
		graphGR.setBorder(getFactory().makeShapeBorder(20, 20, 20, 20));

		final GraphGRBinding<FGEDiscreteFunctionGraph> graphBinding = bindGraph(FGEDiscreteFunctionGraph.class, "graph",
				new ShapeGRProvider<FGEDiscreteFunctionGraph>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(FGEDiscreteFunctionGraph drawable, FGEModelFactory factory) {
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
