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
import org.openflexo.diana.FGEConstants;
import org.openflexo.diana.FGEModelFactory;
import org.openflexo.diana.GRStructureVisitor;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.diana.GRBinding.DrawingGRBinding;
import org.openflexo.diana.GRBinding.GraphGRBinding;
import org.openflexo.diana.GRProvider.DrawingGRProvider;
import org.openflexo.diana.GRProvider.ShapeGRProvider;
import org.openflexo.diana.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.diana.graph.FGEDiscretePolarFunctionGraph;
import org.openflexo.diana.graph.FGEDiscreteSimpleFunctionGraph;
import org.openflexo.diana.graph.FGENumericFunction;
import org.openflexo.diana.graph.FGEFunction.FGEGraphType;
import org.openflexo.diana.impl.DrawingImpl;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;

/**
 * This is an example of drawing containing a {@link FGEDiscreteSimpleFunctionGraph} showing 2 functions represented as bar graphs
 * 
 * @author sylvain
 * 
 */
public class ExampleBDARepresentation extends DrawingImpl<Object> {

	private FGEDiscretePolarFunctionGraph<Theme> graph;
	private FGENumericFunction<Float> evaluationFunction;

	private DrawingGraphicalRepresentation drawingRepresentation;
	private ShapeGraphicalRepresentation graphGR;

	private Theme gouvernance, territoire, eau, confort, energie, social, materiaux;

	public ExampleBDARepresentation(Object obj, FGEModelFactory factory) {
		super(obj, factory, PersistenceMode.SharedGraphicalRepresentations);
	}

	@Override
	public void init() {

		List<Theme> themes = new ArrayList<Theme>();
		themes.add(gouvernance = new Theme("Gouvernance", 10, 3.2f));
		themes.add(territoire = new Theme("Territoire", 12, 5.7f));
		themes.add(eau = new Theme("Eau", 5, 5.6f));
		themes.add(confort = new Theme("Confort", 8, 3.4f));
		themes.add(energie = new Theme("Energie", 15, 6.8f));
		themes.add(social = new Theme("Social", 10, 2.1f));
		themes.add(materiaux = new Theme("Materiaux", 7, 6.1f));

		graph = new FGEDiscretePolarFunctionGraph<Theme>();

		graph.setParameter("theme", Theme.class);
		graph.setDiscreteValues(themes);
		graph.setDiscreteValuesLabel(new DataBinding<String>("theme.name"));
		graph.setWeight(new DataBinding<Double>("theme.weight"));

		evaluationFunction = graph.addNumericFunction("size", Float.class, new DataBinding<Float>("theme.value"),
				FGEGraphType.COLORED_STEPS);
		evaluationFunction.setStepsNb(7);
		evaluationFunction.setRange(0f, 7.0f);
		evaluationFunction.setForegroundStyle(getFactory().makeForegroundStyle(Color.GREEN, 1.0f));
		evaluationFunction
				.setBackgroundStyle(getFactory().makeColorGradientBackground(Color.GREEN, Color.WHITE, ColorGradientDirection.NORTH_SOUTH));

		drawingRepresentation = getFactory().makeDrawingGraphicalRepresentation();

		final DrawingGRBinding<Object> drawingBinding = bindDrawing(Object.class, "drawing", new DrawingGRProvider<Object>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(Object drawable, FGEModelFactory factory) {
				return drawingRepresentation;
			}
		});

		graphGR = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		graphGR.setText("This is an example of BDA representation");
		graphGR.setX(50);
		graphGR.setY(50);
		graphGR.setWidth(500);
		graphGR.setHeight(500);
		graphGR.setIsFloatingLabel(true);
		graphGR.setAbsoluteTextX(400);
		graphGR.setAbsoluteTextY(-10);
		graphGR.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		graphGR.setTextStyle(getFactory().makeTextStyle(Color.BLACK, FGEConstants.DEFAULT_TEXT_FONT));
		graphGR.setShadowStyle(getFactory().makeNoneShadowStyle());
		graphGR.setBackground(getFactory().makeColorGradientBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR, Color.white,
				ColorGradientDirection.NORTH_WEST_SOUTH_EAST));
		graphGR.setForeground(getFactory().makeForegroundStyle(Color.ORANGE));
		// Very important: give some place for labels, legend and other informations
		// graphGR.setBorder(getFactory().makeShapeBorder(20, 20, 20, 20));

		final GraphGRBinding<FGEDiscretePolarFunctionGraph> graphBinding = bindGraph(FGEDiscretePolarFunctionGraph.class, "graph",
				new ShapeGRProvider<FGEDiscretePolarFunctionGraph>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(FGEDiscretePolarFunctionGraph drawable, FGEModelFactory factory) {
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

	public static class Theme {
		public String name;
		public double weight;
		public List<Objectif> objectifs;
		public float value;

		public Theme(String name, double weight, float value) {
			super();
			this.name = name;
			this.weight = weight;
			this.value = value;
			objectifs = new ArrayList<>();
		}

		@Override
		public String toString() {
			return "Theme:" + name;
		}
	}

	public static class Objectif {
		public String name;
		public double weight;
		public float value;

		public Objectif(String name, double weight, float value) {
			super();
			this.name = name;
			this.weight = weight;
			this.value = value;
		}

		@Override
		public String toString() {
			return "Objectif:" + name;
		}
	}

}
