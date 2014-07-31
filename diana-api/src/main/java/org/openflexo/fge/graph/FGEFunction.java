/*
 * (c) Copyright 2014-2015 Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fge.graph;

import java.awt.geom.AffineTransform;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.geom.FGEComplexCurve;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeneralShape.Closure;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolylin;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graph.FGEFunctionGraph.Orientation;
import org.openflexo.fge.graph.FGEGraph.GraphType;
import org.openflexo.fge.graphics.FGEShapeGraphics;

/**
 * Represents a function as a typed expression<br>
 * Note that computed values must not be {@link Number} instances
 * 
 * @author sylvain
 * 
 * @param <T>
 *            type of values given by the expression
 */
public class FGEFunction<T> {

	private static final Logger logger = Logger.getLogger(FGEFunction.class.getPackage().getName());

	private final String functionName;
	private final Class<T> functionType;
	private final DataBinding<T> functionExpression;
	private final FGEFunctionGraph.GraphType graphType;

	private ForegroundStyle foregroundStyle;
	private BackgroundStyle backgroundStyle;

	private final FGEGraph graph;

	private FGEArea representation = null;

	protected List<T> valueSamples;

	public FGEFunction(String functionName, Class<T> functionType, DataBinding<T> functionExpression, FGEFunctionGraph.GraphType graphType,
			FGEGraph graph) {
		super();
		this.functionName = functionName;
		this.functionType = functionType;
		this.functionExpression = new DataBinding<T>(functionExpression.toString());
		this.functionExpression.setOwner(graph);
		this.functionExpression.setDeclaredType(functionType);
		this.functionExpression.setBindingDefinitionType(BindingDefinitionType.GET);
		if (!this.functionExpression.isValid()) {
			logger.warning("Invalid expression in FGEFunction:" + this.functionExpression + " reason="
					+ this.functionExpression.invalidBindingReason());
		}
		this.graph = graph;
		this.graphType = graphType;
	}

	public String getFunctionName() {
		return functionName;
	}

	public Class<T> getFunctionType() {
		return functionType;
	}

	public DataBinding<T> getFunctionExpression() {
		return functionExpression;
	}

	public FGEGraph getGraph() {
		return graph;
	}

	public FGEFunctionGraph.GraphType getGraphType() {
		return graphType;
	}

	public ForegroundStyle getForegroundStyle() {
		return foregroundStyle;
	}

	public void setForegroundStyle(ForegroundStyle foregroundStyle) {
		this.foregroundStyle = foregroundStyle;
	}

	public BackgroundStyle getBackgroundStyle() {
		return backgroundStyle;
	}

	public void setBackgroundStyle(BackgroundStyle backgroundStyle) {
		this.backgroundStyle = backgroundStyle;
	}

	public T evaluate() throws TypeMismatchException, NullReferenceException, InvocationTargetException {
		T returned = functionExpression.getBindingValue(getGraph().getEvaluator());
		getGraph().getEvaluator().set(functionName, returned);
		return returned;
	}

	public FGEArea getRepresentation() {

		if (representation == null) {
			representation = buildRepresentation();
		}

		return representation;
	}

	protected FGEArea buildRepresentation() {

		if (getGraph() instanceof FGEFunctionGraph) {
			return buildRepresentationForFunctionGraph((FGEFunctionGraph) getGraph());
		}
		return null;
	}

	class FunctionSample<P> {
		P p;
		T value;

		public FunctionSample(P p, T value) {
			super();
			this.p = p;
			this.value = value;
		}
	}

	protected <X> List<FunctionSample<X>> retrieveSamples(FGEFunctionGraph<X> graph) {

		if (valueSamples != null) {
			valueSamples.clear();
		} else {
			valueSamples = new ArrayList<T>();
		}

		List<FunctionSample<X>> samples = new ArrayList<FunctionSample<X>>();
		Iterator<X> it = graph.iterateParameter();

		while (it.hasNext()) {

			X p = it.next();
			T value = null;
			try {
				value = graph.evaluateFunction(this, p);
			} catch (TypeMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullReferenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			samples.add(new FunctionSample<X>(p, value));

			// System.out.println("Sampling function " + getFunctionName() + "(" + p + ") = " + value);

			if (!valueSamples.contains(value)) {
				valueSamples.add(value);
			}

		}

		return samples;
	}

	protected <X> FGEArea buildRepresentationForFunctionGraph(FGEFunctionGraph<X> graph) {

		List<FunctionSample<X>> samples = retrieveSamples(graph);

		List<FGEPoint> points = new ArrayList<FGEPoint>();

		for (FunctionSample<X> s : samples) {
			FGEPoint pt;
			if (graph.getParameterOrientation() == Orientation.HORIZONTAL) {
				pt = new FGEPoint(graph.getNormalizedPosition(s.p), getNormalizedPosition(s.value));
			} else {
				pt = new FGEPoint(getNormalizedPosition(s.value), graph.getNormalizedPosition(s.p));
			}

			// System.out.println("Sampling function " + getFunctionName() + "(" + s.p + ") = " + s.value + " normalizedValue="
			// + getNormalizedPosition(s.value));

			points.add(pt);
		}

		switch (graphType) {
		case POINTS:
			return FGEUnionArea.makeUnion(points);
		case POLYLIN:
			return new FGEPolylin(points);
		case RECT_POLYLIN:
			List<FGEPoint> rectPoints = new ArrayList<FGEPoint>();
			double delta = (double) 1 / points.size() / 2;
			for (FGEPoint pt : points) {
				if (graph.getParameterOrientation() == Orientation.HORIZONTAL) {
					rectPoints.add(new FGEPoint(pt.x - delta, pt.y));
					rectPoints.add(new FGEPoint(pt.x + delta, pt.y));
				} else { // Vertical
					rectPoints.add(new FGEPoint(pt.x, pt.y - delta));
					rectPoints.add(new FGEPoint(pt.x, pt.y + delta));
				}
			}
			return new FGEPolylin(rectPoints);
		case CURVE:
			return new FGEComplexCurve(Closure.OPEN_NOT_FILLED, points);
		case BAR_GRAPH:
			List<FGERectangle> rectangles = new ArrayList<FGERectangle>();
			double sampleSize = (double) 1 / points.size();
			double barWidth = 0.8 * sampleSize / graph.getNumberOfFunctionsOfType(GraphType.BAR_GRAPH);
			double barSpacing = sampleSize / 10;
			int index = graph.getIndexOfFunctionsOfType(this);
			for (FGEPoint pt : points) {
				if (graph.getParameterOrientation() == Orientation.HORIZONTAL) {
					FGERectangle r = new FGERectangle(new FGEPoint(pt.x - sampleSize / 2 + barSpacing + (index * barWidth), 0),
							new FGEDimension(barWidth, pt.y), Filling.FILLED);
					rectangles.add(r);
				} else {
					FGERectangle r = new FGERectangle(new FGEPoint(0, pt.y - sampleSize / 2 + barSpacing + (index * barWidth)),
							new FGEDimension(pt.x, barWidth), Filling.FILLED);
					rectangles.add(r);
				}
			}
			return FGEUnionArea.makeUnion(rectangles);

		default:
			break;
		}
		return null;
	}

	protected Double getNormalizedPosition(T value) {

		if (valueSamples.size() <= 1) {
			return 0.0;
		}

		// System.out.println("For " + value + " return " + (((double) valueSamples.indexOf(value)) / (valueSamples.size() - 1)));

		return ((double) valueSamples.indexOf(value)) / (valueSamples.size() - 1);
	}

	public void paint(FGEShapeGraphics g) {

		// System.out.println("Painting function");

		// This AffinTransform allows to convert normalized coordinate system in FGE to the mathematical classical one
		// y-axis order is reversed
		AffineTransform at = new AffineTransform(new double[] { 1.0, 0.0, 0.0, -1.0, 0.0, 1.0 });

		g.setDefaultForeground(getForegroundStyle());
		g.setDefaultBackground(getBackgroundStyle());

		FGEArea functionRepresentation = getRepresentation();

		if (functionRepresentation != null) {
			functionRepresentation.transform(at).paint(g);
		}

	}

}