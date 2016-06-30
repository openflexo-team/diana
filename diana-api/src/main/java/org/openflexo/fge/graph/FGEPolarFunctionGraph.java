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

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.geom.FGEComplexCurve;
import org.openflexo.fge.geom.FGEGeneralShape.Closure;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGEPolylin;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graph.FGEFunction.FunctionSample;
import org.openflexo.fge.graph.FGEFunction.GraphType;
import org.openflexo.fge.graphics.FGEShapeGraphics;

/**
 * Represents a polar graph [R=f(A)] representing functions where:
 * <ul>
 * <li>angle is iterated over continuous or discrete values</li>
 * <li>functions are computed with expressions using angle as parameter (iterated value, which can be discrete or continuous)</li><br>
 * </ul>
 * 
 * Such graphs allows only one parameter.
 * 
 * @author sylvain
 * 
 * @param <A>
 *            type of value which plays iterator role (whose angle is representation)
 */
public abstract class FGEPolarFunctionGraph<A> extends FGESingleParameteredGraph<A> {

	public FGEPolarFunctionGraph() {
		super();
	}

	@Override
	public <R> R evaluateFunction(FGEFunction<R> function, A value)
			throws TypeMismatchException, NullReferenceException, InvocationTargetException {
		getEvaluator().set(getParameter(), value);
		return function.evaluate();
	}

	@Override
	protected abstract Iterator<A> iterateParameter();

	/**
	 * Called for graph painting
	 * 
	 * @param g
	 */
	@Override
	public void paint(FGEShapeGraphics g) {

		// System.out.println("Painting graph");
		// System.out.println("width = " + g.getViewWidth());
		// System.out.println("height = " + g.getViewHeight());

		FGELine horizontalCoordinates = new FGELine(0, 0.5, 1, 0.5);
		FGELine verticalCoordinates = new FGELine(0.5, 0, 0.5, 1);

		// g.setDefaultForeground(get);
		horizontalCoordinates.paint(g);
		verticalCoordinates.paint(g);

		super.paint(g);

		// Paint parameters
		paintParameters(g);

		for (FGEFunction<?> f : getFunctions()) {

			f.paint(g);

		}

	}

	public abstract void paintParameters(FGEShapeGraphics g);

	public abstract Double getNormalizedAngle(A parameterValue);

	public abstract Double getNormalizedAngleExtent(A parameterValue);

	@Override
	protected <T> FunctionRepresentation buildRepresentationForFunction(FGEFunction<T> function) {

		List<FunctionSample<A, T>> samples = function.retrieveSamples(this);

		List<FGEPoint> points = new ArrayList<FGEPoint>();
		for (FunctionSample<A, T> s : samples) {
			Double angle = getNormalizedAngle(s.x);
			Double radius = function.getNormalizedPosition(s.value) / 2;
			// System.out.println("x:" + s.x + " v:" + s.value + " radius=" + radius);
			FGEPoint pt = new FGEPoint(radius * Math.cos(angle) + 0.5, radius * Math.sin(angle) + 0.5);
			// System.out.println("Point: " + pt);
			points.add(pt);
		}

		switch (function.getGraphType()) {
			case POINTS:
				return new FunctionRepresentation(FGEUnionArea.makeUnion(points), function.getForegroundStyle(),
						function.getBackgroundStyle());
			case POLYLIN:
				return new FunctionRepresentation(new FGEPolylin(points), function.getForegroundStyle(), function.getBackgroundStyle());
			case RECT_POLYLIN:
				// Not supported for polar functions
				return new FunctionRepresentation(new FGEPolylin(points), function.getForegroundStyle(), function.getBackgroundStyle());
			case CURVE:
				return new FunctionRepresentation(new FGEComplexCurve(Closure.OPEN_NOT_FILLED, points), function.getForegroundStyle(),
						function.getBackgroundStyle());
			case BAR_GRAPH:
				List<FGEPolygon> polygons = new ArrayList<FGEPolygon>();
				double sampleSize = (double) 1 / points.size() * 2 * Math.PI;
				double barWidth = 0.8 * sampleSize / getNumberOfFunctionsOfType(GraphType.BAR_GRAPH);
				double barSpacing = sampleSize / 10;
				int index = getIndexOfFunctionsOfType(function);
				for (FunctionSample<A, T> s : samples) {
					Double angle = getNormalizedAngle(s.x);
					double startAngle = angle - sampleSize / 2 + barSpacing + (index * barWidth);
					// double endAngle = startAngle + barWidth;
					int requiredSteps = (int) (barWidth * 20);
					Double radius = function.getNormalizedPosition(s.value) / 2;
					List<FGEPoint> pts = new ArrayList<FGEPoint>();
					pts.add(new FGEPoint(0.5, 0.5));
					for (int i = 0; i <= requiredSteps; i++) {
						double a = startAngle + barWidth * i / requiredSteps;
						pts.add(new FGEPoint(radius * Math.cos(a) + 0.5, radius * Math.sin(a) + 0.5));
					}
					polygons.add(new FGEPolygon(Filling.FILLED, pts));
				}

				return new FunctionRepresentation(FGEUnionArea.makeUnion(polygons), function.getForegroundStyle(),
						function.getBackgroundStyle());
			case COLORED_STEPS:
				if (function instanceof FGENumericFunction) {
					FGENumericFunction numFunction = (FGENumericFunction) function;
					List<ElementRepresentation> elements = new ArrayList<ElementRepresentation>();
					int numberOfFunctions = getNumberOfFunctionsOfType(GraphType.COLORED_STEPS);
					int functionIndex = getIndexOfFunctionsOfType(function);

					// double sampleSize2 = (double) 1 / points.size() * 2 * Math.PI;
					// double barWidth2 = 0.8 * sampleSize2 / getNumberOfFunctionsOfType(GraphType.COLORED_STEPS);
					// double barSpacing2 = sampleSize2 / 10;
					// int index2 = getIndexOfFunctionsOfType(function);
					Color color1 = Color.RED;
					Color color2 = Color.GREEN;
					for (FunctionSample<A, T> s : samples) {
						Double angle = getNormalizedAngle(s.x); // Middle of angle
						Double angleExtent = getNormalizedAngleExtent(s.x) / numberOfFunctions - 0.1;
						double startAngle = angle - angleExtent / 2 + functionIndex * angleExtent;
						int requiredSteps = (int) (angleExtent * 20);
						int stepsToShow = (int) (function.getNormalizedPosition(s.value).doubleValue() * numFunction.getStepsNb() + 0.5);
						// System.out.println(" pour " + s.x + " value=" + s.value + " n_value=" + function.getNormalizedPosition(s.value)
						// + " stepsToShow=" + stepsToShow);
						for (int step = 0; step < stepsToShow; step++) {
							int red = color1.getRed() + (color2.getRed() - color1.getRed()) * step / numFunction.getStepsNb();
							int green = color1.getGreen() + (color2.getGreen() - color1.getGreen()) * step / numFunction.getStepsNb();
							int blue = color1.getBlue() + (color2.getBlue() - color1.getBlue()) * step / numFunction.getStepsNb();
							Color color = new Color(red, green, blue);
							double startRadius = (double) step / numFunction.getStepsNb() / 2;
							double endRadius = (step + 0.8) / numFunction.getStepsNb() / 2;
							// System.out.println("step=" + step + " startRadius=" + startRadius + " endRadius=" + endRadius);
							List<FGEPoint> pts = new ArrayList<FGEPoint>();
							for (int i = 0; i <= requiredSteps; i++) {
								double a = startAngle + angleExtent * i / requiredSteps;
								pts.add(new FGEPoint(startRadius * Math.cos(a) + 0.5, startRadius * Math.sin(a) + 0.5));
							}
							for (int i = requiredSteps; i >= 0; i--) {
								double a = startAngle + angleExtent * i / requiredSteps;
								pts.add(new FGEPoint(endRadius * Math.cos(a) + 0.5, endRadius * Math.sin(a) + 0.5));
							}
							FGEModelFactory factory = function.getBackgroundStyle().getFactory();
							ForegroundStyle fg = factory.makeForegroundStyle(color);
							BackgroundStyle bg = factory.makeColoredBackground(color.brighter());
							elements.add(new ElementRepresentation(new FGEPolygon(Filling.FILLED, pts), fg, bg));
						}

					}

					return new FunctionRepresentation(elements);
				}
				break;
			default:
				break;
		}

		return null;
	}

}
