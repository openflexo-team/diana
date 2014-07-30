package org.openflexo.fge.graph;

import java.awt.Color;
import java.awt.Font;

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
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.graph.FGEGraph.GraphType;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;

public class FGEGraphDrawing extends DrawingImpl<Object> {

	public static final double ROW_HEIGHT = 60.0;
	public static final double LIFE_LINE_WIDTH = 150.0;

	private DrawingGraphicalRepresentation drawingRepresentation;
	// private GeometricGraphicalRepresentation geomGR;
	private ShapeGraphicalRepresentation graphGR;

	public FGEGraphDrawing(Object obj, FGEModelFactory factory) {
		super(obj, factory, PersistenceMode.SharedGraphicalRepresentations);
	}

	@Override
	public void init() {

		drawingRepresentation = getFactory().makeDrawingGraphicalRepresentation();

		final DrawingGRBinding<Object> drawingBinding = bindDrawing(Object.class, "drawing", new DrawingGRProvider<Object>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(Object drawable, FGEModelFactory factory) {
				return drawingRepresentation;
			}
		});

		final FGEContinuousFunctionGraph<Double> graph = new FGEContinuousFunctionGraph<Double>(Double.class);
		graph.setParameter("x", Double.class);
		graph.setParameterRange(-10.0, 10.0);
		graph.setStepsNumber(100);

		FGENumericFunction<Double> yFunction = graph.addNumericFunction("y", Double.class, new DataBinding<Double>("x*x-2*x+1"),
				GraphType.POLYLIN);
		yFunction.setRange(0.0, 100.0);
		yFunction.setForegroundStyle(getFactory().makeForegroundStyle(Color.BLUE, 1.0f));

		FGENumericFunction<Double> y2Function = graph.addNumericFunction("y2", Double.class, new DataBinding<Double>("cos(x)"),
				GraphType.CURVE);
		y2Function.setRange(-1.0, 1.0);
		y2Function.setForegroundStyle(getFactory().makeForegroundStyle(Color.RED, 1.0f));
		y2Function
				.setBackgroundStyle(getFactory().makeColorGradientBackground(Color.BLUE, Color.WHITE, ColorGradientDirection.NORTH_SOUTH));

		FGENumericFunction<Integer> y3Function = graph.addNumericFunction("y3", Integer.class, new DataBinding<Integer>(
				"($java.lang.Integer)(x*x/2+1)"), GraphType.POINTS);
		y3Function.setRange(0, 12);
		y3Function.setForegroundStyle(getFactory().makeForegroundStyle(Color.BLACK, 1.0f));

		FGENumericFunction<Double> y4Function = graph.addNumericFunction("y", Double.class, new DataBinding<Double>("-3*x*(x-10)+20"),
				GraphType.RECT_POLYLIN);
		y4Function.setRange(0.0, 100.0);
		y4Function.setForegroundStyle(getFactory().makeForegroundStyle(Color.PINK, 1.0f));

		graphGR = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		graphGR.setText("Graph legend");
		graphGR.setX(50);
		graphGR.setY(50);
		graphGR.setWidth(200);
		graphGR.setHeight(200);
		graphGR.setAbsoluteTextX(40);
		graphGR.setAbsoluteTextY(13);
		graphGR.setTextStyle(getFactory().makeTextStyle(Color.BLACK, FGEConstants.DEFAULT_TEXT_FONT.deriveFont(Font.BOLD)));
		graphGR.setShadowStyle(getFactory().makeNoneShadowStyle());
		graphGR.setBackground(getFactory().makeColorGradientBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR, Color.white,
				ColorGradientDirection.SOUTH_EAST_NORTH_WEST));
		graphGR.setForeground(getFactory().makeForegroundStyle(Color.ORANGE));

		final GraphGRBinding<FGEContinuousFunctionGraph> graphBinding = bindGraph(FGEContinuousFunctionGraph.class, "graph",
				new ShapeGRProvider<FGEContinuousFunctionGraph>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(FGEContinuousFunctionGraph drawable, FGEModelFactory factory) {
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
