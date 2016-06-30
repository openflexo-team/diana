/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-api, a component of the software infrastructure 
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

import java.awt.geom.AffineTransform;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.graph.FGEGraph.ElementRepresentation;
import org.openflexo.fge.graph.FGEGraph.FunctionRepresentation;
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

	public static enum GraphType {
		POINTS, POLYLIN, RECT_POLYLIN, CURVE, BAR_GRAPH, COLORED_STEPS
	}

	private final String functionName;
	private final Class<T> functionType;
	private final DataBinding<T> functionExpression;
	private final GraphType graphType;

	private ForegroundStyle foregroundStyle;
	private BackgroundStyle backgroundStyle;

	private final FGEGraph graph;

	private FunctionRepresentation representation = null;

	protected List<T> valueSamples;

	public FGEFunction(String functionName, Class<T> functionType, DataBinding<T> functionExpression, GraphType graphType, FGEGraph graph) {
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

	public GraphType getGraphType() {
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

	public FunctionRepresentation getRepresentation() {

		if (representation == null) {
			representation = buildRepresentation();
		}

		return representation;
	}

	protected FunctionRepresentation buildRepresentation() {

		return getGraph().buildRepresentationForFunction(this);

		/*if (getGraph() instanceof FGEFunctionGraph) {
			return buildRepresentationForFunctionGraph((FGEFunctionGraph<?>) getGraph());
		}
		return null;*/
	}

	static class FunctionSample<X, T> {
		X x;
		T value;

		public FunctionSample(X x, T value) {
			super();
			this.x = x;
			this.value = value;
		}
	}

	protected <X> List<FunctionSample<X, T>> retrieveSamples(FGESingleParameteredGraph<X> graph) {

		if (valueSamples != null) {
			valueSamples.clear();
		}
		else {
			valueSamples = new ArrayList<T>();
		}

		List<FunctionSample<X, T>> samples = new ArrayList<FunctionSample<X, T>>();
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

			samples.add(new FunctionSample<X, T>(p, value));

			// System.out.println("Sampling function " + getFunctionName() + "(" + p + ") = " + value);

			if (!valueSamples.contains(value)) {
				valueSamples.add(value);
			}

		}

		return samples;
	}

	/*protected <X> FGEArea buildRepresentationForFunctionGraph(FGEFunctionGraph<X> graph) {
	
		List<FunctionSample<X, T>> samples = retrieveSamples(graph);
	
		List<FGEPoint> points = new ArrayList<FGEPoint>();
	
		for (FunctionSample<X, T> s : samples) {
			FGEPoint pt;
			if (graph.getParameterOrientation() == Orientation.HORIZONTAL) {
				pt = new FGEPoint(graph.getNormalizedPosition(s.x), getNormalizedPosition(s.value));
			}
			else {
				pt = new FGEPoint(getNormalizedPosition(s.value), graph.getNormalizedPosition(s.x));
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
					}
					else { // Vertical
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
					}
					else {
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
	}*/

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

		FunctionRepresentation functionRepresentation = getRepresentation();

		for (ElementRepresentation e : functionRepresentation.elements) {
			if (e.area != null) {
				g.setDefaultForeground(e.foregroundStyle);
				g.setDefaultBackground(e.backgroundStyle);
				e.area.transform(at).paint(g);
			}
		}

	}

}
