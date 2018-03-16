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

package org.openflexo.diana.graph;

import java.awt.geom.AffineTransform;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.diana.BackgroundStyle;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.graph.FGEGraph.ElementRepresentation;
import org.openflexo.diana.graph.FGEGraph.FunctionRepresentation;
import org.openflexo.diana.graphics.FGEShapeGraphics;
import org.openflexo.toolbox.PropertyChangedSupportDefaultImplementation;

/**
 * Represents a function as a typed expression<br>
 * Note that computed values must not be {@link Number} instances
 * 
 * @author sylvain
 * 
 * @param <T>
 *            type of values given by the expression
 */
public abstract class FGEFunction<T> extends PropertyChangedSupportDefaultImplementation {

	private static final Logger logger = Logger.getLogger(FGEFunction.class.getPackage().getName());

	public static enum FGEGraphType {
		POINTS, POLYLIN, RECT_POLYLIN, CURVE, BAR_GRAPH, COLORED_STEPS, SECTORS
	}

	private final String functionName;
	private final Type functionType;
	private DataBinding<T> functionExpression;
	private FGEGraphType graphType;

	private ForegroundStyle foregroundStyle;
	private BackgroundStyle backgroundStyle;

	private final FGEGraph graph;

	double DEFAULT_ANGLE_SPACING = 2; // 2 degrees
	double DEFAULT_STEPS_SPACING = 0.2; // 20% spacing

	private double angleSpacing = DEFAULT_ANGLE_SPACING;
	private double stepsSpacing = DEFAULT_STEPS_SPACING;

	private FunctionRepresentation representation = null;

	protected List<T> valueSamples;
	protected Map<?, List<T>> twoLevelsValueSamples;

	public FGEFunction(String functionName, Type functionType, DataBinding<T> functionExpression, FGEGraphType graphType, FGEGraph graph) {
		super();
		this.functionName = functionName;
		this.functionType = functionType;
		this.functionExpression = new DataBinding<>(functionExpression.toString(), graph, functionType, BindingDefinitionType.GET);
		if (!this.functionExpression.isValid()) {
			logger.warning("Invalid expression in FGEFunction:" + this.functionExpression + " reason="
					+ this.functionExpression.invalidBindingReason());
			logger.warning("BM=" + graph.getBindingModel());
			logger.warning("BF=" + graph.getBindingFactory());
			for (int i = 0; i < graph.getBindingModel().getBindingVariablesCount(); i++) {
				System.out.println("> " + graph.getBindingModel().getBindingVariableAt(i));
			}
			Thread.dumpStack();
		}
		this.graph = graph;
		this.graphType = graphType;
	}

	public String getFunctionName() {
		return functionName;
	}

	public Type getFunctionType() {
		return functionType;
	}

	public DataBinding<T> getFunctionExpression() {
		return functionExpression;
	}

	public void setFunctionExpression(DataBinding<T> functionExpression) {
		this.functionExpression = functionExpression;
	}

	public FGEGraph getGraph() {
		return graph;
	}

	public FGEGraphType getGraphType() {
		return graphType;
	}

	public void setGraphType(FGEGraphType graphType) {
		if ((graphType == null && this.graphType != null) || (graphType != null && !graphType.equals(this.graphType))) {
			FGEGraphType oldValue = this.graphType;
			this.graphType = graphType;
			getPropertyChangeSupport().firePropertyChange("graphType", oldValue, graphType);
		}
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

	// Angle in degree
	public double getAngleSpacing() {
		return angleSpacing;
	}

	public void setAngleSpacing(double angleSpacing) {
		if (angleSpacing != this.angleSpacing) {
			double oldValue = this.angleSpacing;
			this.angleSpacing = angleSpacing;
			getPropertyChangeSupport().firePropertyChange("angleSpacing", oldValue, angleSpacing);
		}
	}

	// Between 0.0 and 1.0
	public double getStepsSpacing() {
		return stepsSpacing;
	}

	public void setStepsSpacing(double stepsSpacing) {
		if (stepsSpacing != this.stepsSpacing) {
			double oldValue = this.stepsSpacing;
			this.stepsSpacing = stepsSpacing;
			getPropertyChangeSupport().firePropertyChange("stepsSpacing", oldValue, stepsSpacing);
		}
	}

	public T evaluate() throws TypeMismatchException, NullReferenceException, InvocationTargetException {
		// System.out.println("on evalue " + functionExpression + " valid=" + functionExpression.isValid() + " reason: "
		// + functionExpression.invalidBindingReason());
		T returned = functionExpression.getBindingValue(getGraph().getEvaluator());
		getGraph().getEvaluator().set(functionName, returned);
		return returned;
	}

	public FunctionRepresentation getRepresentation() {

		if (representation == null) {
			try {
				representation = buildRepresentation();
			} catch (Exception e) {
				logger.warning("Unexpected exception " + e);
			}
		}

		return representation;
	}

	protected void updateRepresentation() {
		representation = buildRepresentation();
	}

	protected FunctionRepresentation buildRepresentation() {

		System.out.println(">>>>>>>>>>>> On reconstruit la representation de la fonction " + functionName + " " + getFunctionExpression());
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

	static class TwoLevelsFunctionSample<T1, T2, V> {
		T1 primaryValue;
		List<T2> secondaryValues;
		List<V> values;

		public TwoLevelsFunctionSample(T1 primaryValue, List<T2> secondaryValues, List<V> values) {
			super();
			this.primaryValue = primaryValue;
			this.secondaryValues = secondaryValues;
			this.values = values;
		}

	}

	protected <X> List<FunctionSample<X, T>> retrieveSamples(FGESingleParameteredGraph<X> graph) {

		if (valueSamples != null) {
			valueSamples.clear();
		}
		else {
			valueSamples = new ArrayList<>();
		}

		// System.out.println("On calcule les samples pour " + getFunctionExpression());

		List<FunctionSample<X, T>> samples = new ArrayList<>();
		Iterator<X> it = graph.iterateParameter();

		if (it != null) {
			while (it.hasNext()) {

				X p = it.next();

				// System.out.println("pour la valeur " + p);

				T value = null;
				try {
					value = graph.evaluateFunction(this, p);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}

				// System.out.println("value=" + value);

				samples.add(new FunctionSample<>(p, value));

				// System.out.println("Sampling function " + getFunctionName() + "(" + p + ") = " + value);

				if (!valueSamples.contains(value)) {
					valueSamples.add(value);
				}
			}
		}

		return samples;
	}

	protected <T1, T2> List<TwoLevelsFunctionSample<T1, T2, T>> retrieveTwoLevelsSamples(
			FGEDiscreteTwoLevelsPolarFunctionGraph<T1, T2> graph) {

		if (twoLevelsValueSamples != null) {
			twoLevelsValueSamples.clear();
		}
		else {
			twoLevelsValueSamples = new HashMap<>();
		}

		// System.out.println("On calcule les two-levels samples pour " + getFunctionExpression());

		List<TwoLevelsFunctionSample<T1, T2, T>> samples = new ArrayList<>();
		Iterator<T1> it = graph.iteratePrimaryParameter();

		if (it != null) {

			while (it.hasNext()) {

				T1 primaryValue = it.next();
				List<T2> secondaryValues = graph.getSecondaryDiscreteValues().get(primaryValue);
				List<T> values = new ArrayList<>();
				TwoLevelsFunctionSample<T1, T2, T> newSample = new TwoLevelsFunctionSample<>(primaryValue, secondaryValues, values);

				if (secondaryValues != null) {
					for (T2 secondaryValue : secondaryValues) {
						T value = null;
						try {
							value = graph.evaluateFunction(this, primaryValue, secondaryValue);
						} catch (TypeMismatchException e) {
							e.printStackTrace();
						} catch (NullReferenceException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
						values.add(value);
					}
				}

				samples.add(newSample);

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

		if (functionRepresentation != null) {
			for (ElementRepresentation e : functionRepresentation.elements) {
				if (e.area != null) {
					g.setDefaultForeground(e.foregroundStyle);
					g.setDefaultBackground(e.backgroundStyle);
					e.area.transform(at).paint(g);
				}
			}
		}

	}

}
