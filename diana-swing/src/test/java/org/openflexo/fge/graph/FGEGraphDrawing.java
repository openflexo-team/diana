package org.openflexo.fge.graph;

import java.awt.Color;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.GeometricGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.GeometricGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.graph.FGEGraph.GraphType;
import org.openflexo.fge.impl.DrawingImpl;

public class FGEGraphDrawing extends DrawingImpl<Object> {

	public static final double ROW_HEIGHT = 60.0;
	public static final double LIFE_LINE_WIDTH = 150.0;

	private DrawingGraphicalRepresentation drawingRepresentation;
	private GeometricGraphicalRepresentation geomGR;

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

		final FGEContinuousFunctionGraph<Double> graph = new FGEContinuousFunctionGraph<Double>();
		graph.setParameter("x", Double.class);
		graph.setParameterRange(-10.0, 10.0);
		FGENumericFunction<Double> yFunction = graph.addNumericFunction("y", Double.class, new DataBinding<Double>("x*x-2*x+1"),
				GraphType.CURVE);
		yFunction.setRange(0.0, 100.0);
		yFunction.setForegroundStyle(getFactory().makeForegroundStyle(Color.RED, 1.0f));
		yFunction.setBackgroundStyle(getFactory().makeColorGradientBackground(Color.BLUE, Color.WHITE, ColorGradientDirection.NORTH_SOUTH));

		graph.setX(50.0);
		graph.setY(50.0);
		graph.setWidth(200.0);
		graph.setHeight(200.0);

		// final Object myRectangle = new Object();
		// FGERectangle rect = new FGERectangle(Filling.FILLED);
		// rect.setRect(200, 200, 200, 200);

		geomGR = getFactory().makeGeometricGraphicalRepresentation(graph.getResultingArea());
		geomGR.setBackground(getFactory().makeColoredBackground(/*TextureType.TEXTURE1, Color.red, Color.white*/Color.YELLOW));
		geomGR.setForeground(getFactory().makeForegroundStyle(Color.blue));

		final GeometricGRBinding<FGEContinuousFunctionGraph> geomBinding = bindGeometric(FGEContinuousFunctionGraph.class, "graph",
				new GeometricGRProvider<FGEContinuousFunctionGraph>() {
					@Override
					public GeometricGraphicalRepresentation provideGR(FGEContinuousFunctionGraph drawable, FGEModelFactory factory) {
						return geomGR;
					}
				});

		drawingBinding.addToWalkers(new GRStructureVisitor<Object>() {

			@Override
			public void visit(Object route) {
				drawGeometricObject(geomBinding, graph);
			}
		});

	}
}
