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
import java.util.Random;

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

	private boolean displayReferenceMarks = true;
	private boolean displayGrid = false;
	private boolean displayLabels = false;

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

		super.paint(g);

		if (getDisplayReferenceMarks()) {
			g.useForegroundStyle(g.getCurrentForeground());
			FGELine horizontalCoordinates = new FGELine(0, 0.5, 1, 0.5);
			FGELine verticalCoordinates = new FGELine(0.5, 0, 0.5, 1);
			horizontalCoordinates.paint(g);
			verticalCoordinates.paint(g);
		}

		for (FGEFunction<?> f : getFunctions()) {

			f.paint(g);

		}

		// Paint parameters
		paintParameters(g);

	}

	public abstract void paintParameters(FGEShapeGraphics g);

	public boolean getDisplayReferenceMarks() {
		return displayReferenceMarks;
	}

	public void setDisplayReferenceMarks(boolean displayReferenceMarks) {
		if (displayReferenceMarks != this.displayReferenceMarks) {
			this.displayReferenceMarks = displayReferenceMarks;
			getPropertyChangeSupport().firePropertyChange("displayReferenceMarks", !displayReferenceMarks, displayReferenceMarks);
		}
	}

	public abstract Double getNormalizedAngle(A parameterValue);

	public abstract Double getNormalizedAngleExtent(A parameterValue);

	@Override
	protected <T> FunctionRepresentation buildRepresentationForFunction(FGEFunction<T> function) {

		System.out.println("On recalcule la representation");

		List<FunctionSample<A, T>> samples = function.retrieveSamples(this);

		/*System.out.println("OK voila mes echantillons: ");
		for (FunctionSample<A, T> s : samples) {
			System.out.println(" > " + s.x + " value=" + s.value);
		}*/

		List<FGEPoint> points = new ArrayList<FGEPoint>();
		for (FunctionSample<A, T> s : samples) {
			Double angle = getNormalizedAngle(s.x);
			Double radius = function.getNormalizedPosition(s.value) / 2;
			// System.out.println("x:" + s.x + " v:" + s.value + " radius=" + radius);
			FGEPoint pt = new FGEPoint(radius * Math.cos(angle * Math.PI / 180) + 0.5, radius * Math.sin(angle * Math.PI / 180) + 0.5);
			// System.out.println("Point: " + pt);
			points.add(pt);
		}

		int numberOfFunctions = getNumberOfFunctionsOfType(function.getGraphType());
		int functionIndex = getIndexOfFunctionsOfType(function);

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
				for (FunctionSample<A, T> s : samples) {
					Double angle = getNormalizedAngle(s.x); // Middle of angle
					Double angleExtent = getNormalizedAngleExtent(s.x) / numberOfFunctions - 5;
					double startAngle = angle - angleExtent / 2 + functionIndex * angleExtent;
					int requiredSteps = (int) (angleExtent / 3); // Draw all 3 degrees
					Double radius = function.getNormalizedPosition(s.value) / 2;
					List<FGEPoint> pts = new ArrayList<FGEPoint>();
					pts.add(new FGEPoint(0.5, 0.5));
					for (int i = 0; i <= requiredSteps; i++) {
						double a = startAngle + angleExtent * i / requiredSteps;
						pts.add(new FGEPoint(radius * Math.cos(a * Math.PI / 180) + 0.5, radius * Math.sin(a * Math.PI / 180) + 0.5));
					}
					polygons.add(new FGEPolygon(Filling.FILLED, pts));
				}

				return new FunctionRepresentation(FGEUnionArea.makeUnion(polygons), function.getForegroundStyle(),
						function.getBackgroundStyle());
			case COLORED_STEPS:
				if (function instanceof FGENumericFunction) {
					FGENumericFunction numFunction = (FGENumericFunction) function;
					List<ElementRepresentation> elements = new ArrayList<ElementRepresentation>();
					Color color1 = Color.RED;
					Color color2 = Color.GREEN;
					for (FunctionSample<A, T> s : samples) {
						Double angle = getNormalizedAngle(s.x); // Middle of angle
						Double angleExtent = getNormalizedAngleExtent(s.x) / numberOfFunctions - numFunction.getAngleSpacing();
						double startAngle = angle - angleExtent / 2 + functionIndex * angleExtent;
						int requiredSteps = (int) (angleExtent / 3);// Draw all 3 degrees
						int stepsToShow = (int) (function.getNormalizedPosition(s.value).doubleValue() * numFunction.getStepsNb() + 0.5);
						// System.out.println(" pour " + s.x + " value=" + s.value + " n_value=" + function.getNormalizedPosition(s.value)
						// + " stepsToShow=" + stepsToShow);
						for (int step = 0; step < stepsToShow; step++) {
							int red = color1.getRed() + (color2.getRed() - color1.getRed()) * step / numFunction.getStepsNb();
							int green = color1.getGreen() + (color2.getGreen() - color1.getGreen()) * step / numFunction.getStepsNb();
							int blue = color1.getBlue() + (color2.getBlue() - color1.getBlue()) * step / numFunction.getStepsNb();
							Color color = new Color(red, green, blue);
							double startRadius = (double) step / numFunction.getStepsNb() / 2;
							double endRadius = (step + (1 - numFunction.getStepsSpacing())) / numFunction.getStepsNb() / 2;
							// System.out.println("step=" + step + " startRadius=" + startRadius + " endRadius=" + endRadius);
							List<FGEPoint> pts = new ArrayList<FGEPoint>();
							for (int i = 0; i <= requiredSteps; i++) {
								double a = startAngle + angleExtent * i / requiredSteps;
								pts.add(new FGEPoint(startRadius * Math.cos(a * Math.PI / 180) + 0.5,
										startRadius * Math.sin(a * Math.PI / 180) + 0.5));
							}
							for (int i = requiredSteps; i >= 0; i--) {
								double a = startAngle + angleExtent * i / requiredSteps;
								pts.add(new FGEPoint(endRadius * Math.cos(a * Math.PI / 180) + 0.5,
										endRadius * Math.sin(a * Math.PI / 180) + 0.5));
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
			case SECTORS:
				if (function instanceof FGENumericFunction) {
					List<ElementRepresentation> elements = new ArrayList<ElementRepresentation>();
					byte[] bytes = new byte[3];
					Random r = new Random(0);
					for (FunctionSample<A, T> s : samples) {
						Double angle = getNormalizedAngleForSectors(s.x, (FGENumericFunction) function); // Middle of angle
						Double angleExtent = getNormalizedAngleExtentForSectors(s.x, (FGENumericFunction) function) / numberOfFunctions - 0;
						double startAngle = angle - angleExtent / 2 + functionIndex * angleExtent;
						int requiredSteps = (int) (angleExtent / 3); // Draw all 3 degrees
						Double radius = 0.45;
						List<FGEPoint> pts = new ArrayList<FGEPoint>();
						pts.add(new FGEPoint(0.5, 0.5));
						for (int i = 0; i <= requiredSteps; i++) {
							double a = startAngle + angleExtent * i / requiredSteps;
							pts.add(new FGEPoint(radius * Math.cos(a * Math.PI / 180) + 0.5, radius * Math.sin(a * Math.PI / 180) + 0.5));
						}
						FGEModelFactory factory = function.getBackgroundStyle().getFactory();
						r.nextBytes(bytes);
						Color color = new Color(bytes[0] + 128, bytes[1] + 128, bytes[2] + 128);
						ForegroundStyle fg = factory.makeForegroundStyle(color);
						BackgroundStyle bg = factory.makeColoredBackground(color.brighter());
						elements.add(new ElementRepresentation(new FGEPolygon(Filling.FILLED, pts), fg, bg));
					}
					return new FunctionRepresentation(elements);
				}
			default:
				break;
		}

		return null;
	}

	protected <N extends Number> Double getNormalizedAngleForSectors(A parameterValue, FGENumericFunction<N> function) {
		if (function.getFunctionExpression() != null && function.getFunctionExpression().isSet()
				&& function.getFunctionExpression().isValid()) {
			Iterator<A> it = iterateParameter();
			double cumulatedWeight = 0;
			while (it.hasNext()) {
				A a = it.next();
				double angleExtent = getNormalizedAngleExtentForSectors(a, function);
				if (a == parameterValue) {
					return cumulatedWeight + angleExtent / 2;
				}
				else {
					cumulatedWeight += angleExtent;
				}
			}

		}
		// return (discreteValues.indexOf(value) + 0.5) / (discreteValues.size()) * 360;
		return 0.0;
	}

	protected <N extends Number> Double getNormalizedAngleExtentForSectors(A parameterValue, FGENumericFunction<N> function) {
		if (function.getFunctionExpression() != null && function.getFunctionExpression().isSet()
				&& function.getFunctionExpression().isValid()) {
			double totalWeight = 0;
			Iterator<A> it = iterateParameter();
			while (it.hasNext()) {
				A a = it.next();
				Double weight = getAngleExtent(a, function);
				totalWeight += weight;
			}
			return getAngleExtent(parameterValue, function) / totalWeight * 360;
		}
		return 10.0;
	}

	protected <N extends Number> Double getAngleExtent(A parameterValue, FGENumericFunction<N> function) {
		if (function.getFunctionExpression() != null && function.getFunctionExpression().isSet()
				&& function.getFunctionExpression().isValid()) {
			getEvaluator().set(getParameter(), parameterValue);
			try {
				return function.getFunctionExpression().getBindingValue(getEvaluator()).doubleValue();
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return 10.0;
	}

	public boolean getDisplayGrid() {
		return displayGrid;
	}

	public void setDisplayGrid(boolean displayGrid) {
		if (displayGrid != this.displayGrid) {
			this.displayGrid = displayGrid;
			getPropertyChangeSupport().firePropertyChange("displayGrid", !displayGrid, displayGrid);
		}
	}

	public boolean getDisplayLabels() {
		return displayLabels;
	}

	public void setDisplayLabels(boolean displayLabels) {
		if (displayLabels != this.displayLabels) {
			this.displayLabels = displayLabels;
			getPropertyChangeSupport().firePropertyChange("displayLabels", !displayLabels, displayLabels);
		}
	}

}
