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

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fge.graph.FGEFunction.TwoLevelsFunctionSample;
import org.openflexo.fge.graphics.FGEShapeGraphics;

/**
 * Represents a polar graph [R=f(A)] representing functions where:
 * <ul>
 * <li>angle is iterated over discrete values</li>
 * <li>functions are computed with expressions using angle as parameter (iterated value, which can be discrete or continuous)</li><br>
 * </ul>
 * 
 * Specialize {@link FGEDiscretePolarFunctionGraph} by providing the cabalility to group the iterated items in a main item. Iterated item
 * are expressed using a parent item
 * 
 * @author sylvain
 * 
 * @param <T>
 *            type of discrete values
 */
public class FGEDiscreteTwoLevelsPolarFunctionGraph<T1, T2> extends FGEDiscretePolarFunctionGraph<T2> {

	private Map<T1, List<T2>> secondaryDiscreteValues;
	// private DataBinding<List<T>> secondaryValuesBinding;
	private DataBinding<String> secondaryLabelBinding;
	private DataBinding<Double> secondaryWeightBinding;

	private String secondaryParameter;

	public FGEDiscreteTwoLevelsPolarFunctionGraph() {
		super();
	}

	public String getSecondaryParameter() {
		return secondaryParameter;
	}

	public void setSecondaryParameter(String secondaryParameter) {
		if ((secondaryParameter == null && this.secondaryParameter != null)
				|| (secondaryParameter != null && !secondaryParameter.equals(this.secondaryParameter))) {
			String oldValue = this.secondaryParameter;
			this.secondaryParameter = secondaryParameter;
			getPropertyChangeSupport().firePropertyChange("secondaryParameter", oldValue, secondaryParameter);
		}
	}

	public Map<T1, List<T2>> getSecondaryDiscreteValues() {
		return secondaryDiscreteValues;
	}

	public void setSecondaryDiscreteValues(Map<T1, List<T2>> secondaryDiscreteValues) {
		this.secondaryDiscreteValues = secondaryDiscreteValues;
	}

	public DataBinding<String> getSecondaryDiscreteValuesLabel() {
		return secondaryLabelBinding;
	}

	@Override
	public void setDiscreteValuesLabel(DataBinding<String> secondaryLabelBinding) {
		this.secondaryLabelBinding = secondaryLabelBinding;
		/*this.secondaryLabelBinding.setOwner(this);
		this.secondaryLabelBinding.setDeclaredType(String.class);
		this.secondaryLabelBinding.setBindingDefinitionType(BindingDefinitionType.GET);*/
	}

	protected Iterator<T1> iteratePrimaryParameter() {
		if (secondaryDiscreteValues == null) {
			return (Iterator<T1>) Collections.emptyList().iterator();
		}
		return secondaryDiscreteValues.keySet().iterator();
	}

	protected Iterator<T2> iterateSecondaryParameter(T1 primaryValue) {
		if (secondaryDiscreteValues.get(primaryValue) != null) {
			return secondaryDiscreteValues.get(primaryValue).iterator();
		}
		return ((List<T2>) Collections.emptyList()).iterator();
	}

	public Double getPrimaryNormalizedAngle(T1 value) {
		if (getWeight() != null && getWeight().isSet() && getWeight().isValid()) {
			Iterator<T1> it = iteratePrimaryParameter();
			double cumulatedWeight = 0;
			while (it.hasNext()) {
				T1 t1 = it.next();
				double angleExtent = getNormalizedPrimaryAngleExtent(t1);
				if (t1 == value) {
					return cumulatedWeight + angleExtent / 2;
				}
				else {
					cumulatedWeight += angleExtent;
				}
			}

		}
		return (getDiscreteValues().indexOf(value) + 0.5) / (getDiscreteValues().size()) * 360;
	}

	public Double getSecondaryNormalizedAngle(T1 primaryValue, T2 secondaryValue) {
		List<T2> allSecondaryValues = secondaryDiscreteValues.get(primaryValue);
		if (secondaryWeightBinding != null && secondaryWeightBinding.isSet() && secondaryWeightBinding.isValid()) {
			double cumulatedWeight = 0;
			for (T2 t2 : allSecondaryValues) {
				double angleExtent = getNormalizedSecondaryAngleExtent(primaryValue, t2);
				if (t2 == secondaryValue) {
					return cumulatedWeight + angleExtent / 2;
				}
				else {
					cumulatedWeight += angleExtent;
				}
			}
		}
		return (allSecondaryValues.indexOf(secondaryValue) + 0.5) / (allSecondaryValues.size())
				* getNormalizedPrimaryAngleExtent(primaryValue) / 360;
	}

	public Double getNormalizedPrimaryAngleExtent(T1 primaryValue) {
		if (getWeight() != null && getWeight().isSet() && getWeight().isValid()) {
			double totalWeight = 0;
			Iterator<T1> it = iteratePrimaryParameter();
			while (it.hasNext()) {
				T1 t1 = it.next();
				Double weight = getPrimaryWeight(t1);
				totalWeight += weight;
			}
			return getPrimaryWeight(primaryValue) / totalWeight * 360;
		}
		return 360.0 / getDiscreteValues().size();
	}

	public Double getNormalizedSecondaryAngleExtent(T1 primaryValue, T2 secondaryValue) {
		if (secondaryWeightBinding != null && secondaryWeightBinding.isSet() && secondaryWeightBinding.isValid()) {
			double totalWeight = 0;
			Iterator<T2> it = iterateSecondaryParameter(primaryValue);
			while (it.hasNext()) {
				T2 t2 = it.next();
				Double weight = getSecondaryWeight(primaryValue, t2);
				totalWeight += weight;
			}
			return getPrimaryWeight(primaryValue) / totalWeight * getNormalizedPrimaryAngleExtent(primaryValue);
		}
		return getNormalizedPrimaryAngleExtent(primaryValue) / getDiscreteValues().size();
	}

	public String getSecondaryLabel(T2 secondaryValue) {
		getEvaluator().set(getSecondaryParameter(), secondaryValue);
		if (getSecondaryDiscreteValuesLabel() != null && getSecondaryDiscreteValuesLabel().isSet()
				&& getSecondaryDiscreteValuesLabel().isValid()) {
			try {
				return getSecondaryDiscreteValuesLabel().getBindingValue(getEvaluator());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return secondaryValue.toString();
	}

	public Double getPrimaryWeight(T1 primaryValue) {
		if (getWeight() != null && getWeight().isSet() && getWeight().isValid()) {
			getEvaluator().set(getParameter(), primaryValue);
			try {
				return getWeight().getBindingValue(getEvaluator());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return 360.0 / getDiscreteValues().size();
	}

	public Double getSecondaryWeight(T1 primaryValue, T2 secondaryValue) {
		if (secondaryWeightBinding != null && secondaryWeightBinding.isSet() && secondaryWeightBinding.isValid()) {
			getEvaluator().set(getSecondaryParameter(), secondaryValue);
			try {
				return getWeight().getBindingValue(getEvaluator());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		List<T2> allSecondaryValues = secondaryDiscreteValues.get(primaryValue);
		return getPrimaryWeight(primaryValue) / allSecondaryValues.size();
	}

	@Override
	public void paintParameters(FGEShapeGraphics g) {

		// TODO: find first numeric function
		if (getFunctions().size() >= 1 && getFunctions().get(0) instanceof FGENumericFunction) {
			FGENumericFunction<?> function = (FGENumericFunction<?>) getFunctions().get(0);
			paintParametersForFunction(g, function);
		}
	}

	@Override
	protected <N extends Number> void paintParametersForFunction(FGEShapeGraphics g, FGENumericFunction<N> function) {

		super.paintParametersForFunction(g, function);

		/*if (function.getDisplayLabels()) {
		
			TextStyle ts = g.getNode().getTextStyle();
		
			g.useTextStyle(ts);
		
			Iterator<T> it = iterateParameter();
		
			if (it != null) {
				while (it.hasNext()) {
					T t = it.next();
					String label = getLabel(t);
					Double angle = null;
					Double radius = null;
					N value = null;
					if (function.getGraphType() == FGEGraphType.SECTORS) {
						radius = 0.25;
						angle = getNormalizedAngleForSectors(t, function);
					}
					else {
						try {
							angle = getNormalizedAngle(t);
							value = evaluateFunction(function, t);
							radius = function.getNormalizedPosition(value) / 2 + 0.05;
						} catch (Exception e) {
							e.printStackTrace();
							radius = 0.5;
						}
					}
					g.drawString(label,
							new FGEPoint(Math.cos(angle * Math.PI / 180) * radius + 0.5, 0.5 - Math.sin(angle * Math.PI / 180) * radius),
							HorizontalTextAlignment.CENTER);
				}
			}
		}*/
	}

	/*@Override
	protected <N extends Number> Double getAngleExtent(T2 parameterValue, FGENumericFunction<N> function) {
		if (function.getFunctionExpression() != null && function.getFunctionExpression().isSet()
				&& function.getFunctionExpression().isValid()) {
			return super.getAngleExtent(parameterValue, function);
		}
		return 360.0 / getDiscreteValues().size();
	}*/

	@Override
	protected <T> FunctionRepresentation buildRepresentationForFunction(FGEFunction<T> function) {

		System.out.println("Alors la faut choper les bons echantillons");

		List<TwoLevelsFunctionSample<T1, T2, T>> samples = function.retrieveTwoLevelsSamples(this);

		System.out.println("OK voila mes echantillons: " + samples);
		for (TwoLevelsFunctionSample<T1, T2, T> s : samples) {
			System.out.println(" > " + s.primaryValue + " secondary values=" + s.secondaryValues + " values=" + s.values);
		}

		return super.buildRepresentationForFunction(function);

	}

	/*@Override
	protected FunctionRepresentation buildRepresentationForFunction(FGEFunction<T2> function) {
	
		System.out.println("On recalcule la representation");
	
		List<FunctionSample<A, T>> samples = function.retrieveSamples(this);
	
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
						Double angleExtent = getNormalizedAngleExtent(s.x) / numberOfFunctions - 5;
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
							double endRadius = (step + 0.8) / numFunction.getStepsNb() / 2;
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
	}*/

}
