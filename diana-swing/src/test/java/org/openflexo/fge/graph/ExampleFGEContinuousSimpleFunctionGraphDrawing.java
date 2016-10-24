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

import org.openflexo.connie.DataBinding;
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
import org.openflexo.fge.graph.FGEFunction.FGEGraphType;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;

/**
 * This is an example of drawing containing a {@link FGEContinuousSimpleFunctionGraph} showing 4 functions with different representations
 * 
 * @author sylvain
 * 
 */
public class ExampleFGEContinuousSimpleFunctionGraphDrawing extends DrawingImpl<Object> {

	private FGEContinuousSimpleFunctionGraph<Double> graph;

	private DrawingGraphicalRepresentation drawingRepresentation;
	private ShapeGraphicalRepresentation graphGR;

	public ExampleFGEContinuousSimpleFunctionGraphDrawing(Object obj, FGEModelFactory factory) {
		super(obj, factory, PersistenceMode.SharedGraphicalRepresentations);
	}

	@Override
	public void init() {

		graph = new FGEContinuousSimpleFunctionGraph<Double>(Double.class);
		graph.setParameter("x", Double.class);
		graph.setParameterRange(-10.0, 10.0);
		graph.setStepsNumber(100);

		FGENumericFunction<Double> y1Function = graph.addNumericFunction("y1", Double.class, new DataBinding<Double>("x*x-2*x+1"),
				FGEGraphType.POLYLIN);
		y1Function.setRange(0.0, 100.0);
		y1Function.setForegroundStyle(getFactory().makeForegroundStyle(Color.BLUE, 1.0f));

		FGENumericFunction<Double> y2Function = graph.addNumericFunction("y2", Double.class, new DataBinding<Double>("cos(x)"),
				FGEGraphType.CURVE);
		y2Function.setRange(-1.0, 1.0);
		y2Function.setForegroundStyle(getFactory().makeForegroundStyle(Color.RED, 1.0f));
		y2Function
				.setBackgroundStyle(getFactory().makeColorGradientBackground(Color.BLUE, Color.WHITE, ColorGradientDirection.NORTH_SOUTH));

		FGENumericFunction<Integer> y3Function = graph.addNumericFunction("y3", Integer.class,
				new DataBinding<Integer>("($java.lang.Integer)(x*x/2+1)"), FGEGraphType.POINTS);
		y3Function.setRange(0, 12);
		y3Function.setForegroundStyle(getFactory().makeForegroundStyle(Color.BLACK, 1.0f));

		FGENumericFunction<Double> y4Function = graph.addNumericFunction("y", Double.class, new DataBinding<Double>("-3*x*(x-10)+20"),
				FGEGraphType.RECT_POLYLIN);
		y4Function.setRange(0.0, 100.0);
		y4Function.setForegroundStyle(getFactory().makeForegroundStyle(Color.PINK, 1.0f));

		drawingRepresentation = getFactory().makeDrawingGraphicalRepresentation();

		final DrawingGRBinding<Object> drawingBinding = bindDrawing(Object.class, "drawing", new DrawingGRProvider<Object>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(Object drawable, FGEModelFactory factory) {
				return drawingRepresentation;
			}
		});

		graphGR = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		graphGR.setText("This is an example of 4 continuous functions");
		graphGR.setX(50);
		graphGR.setY(50);
		graphGR.setWidth(600);
		graphGR.setHeight(400);
		graphGR.setIsFloatingLabel(true);
		graphGR.setAbsoluteTextX(500);
		graphGR.setAbsoluteTextY(-10);
		graphGR.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		graphGR.setTextStyle(getFactory().makeTextStyle(Color.BLACK, FGEConstants.DEFAULT_TEXT_FONT));
		graphGR.setShadowStyle(getFactory().makeNoneShadowStyle());
		graphGR.setBackground(getFactory().makeColorGradientBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR, Color.white,
				ColorGradientDirection.NORTH_WEST_SOUTH_EAST));
		graphGR.setForeground(getFactory().makeForegroundStyle(Color.ORANGE));
		// Very important: give some place for labels, legend and other informations
		// graphGR.setBorder(getFactory().makeShapeBorder(20, 20, 20, 20));

		final GraphGRBinding<FGEContinuousSimpleFunctionGraph> graphBinding = bindGraph(FGEContinuousSimpleFunctionGraph.class, "graph",
				new ShapeGRProvider<FGEContinuousSimpleFunctionGraph>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(FGEContinuousSimpleFunctionGraph drawable, FGEModelFactory factory) {
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
