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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graph.FGEFunction.FGEGraphType;
import org.openflexo.fge.graph.FGEFunction.FunctionSample;
import org.openflexo.fge.graphics.FGEShapeGraphics;

/**
 * Represents a polar graph [R=f(A)] representing functions where:
 * <ul>
 * <li>angle is iterated over discrete values</li>
 * <li>functions are computed with expressions using angle as parameter (iterated value, which can be discrete or continuous)</li><br>
 * </ul>
 * 
 * @author sylvain
 * 
 * @param <T>
 *            type of discrete values
 */
public class FGEDiscretePolarFunctionGraph<T> extends FGEPolarFunctionGraph<T> {

	private List<T> discreteValues;
	// private DataBinding<List<T>> secondaryValuesBinding;
	private DataBinding<String> labelBinding;
	private DataBinding<Double> weightBinding;

	public FGEDiscretePolarFunctionGraph() {
		super();
	}

	public List<T> getDiscreteValues() {
		return discreteValues;
	}

	public void setDiscreteValues(List<T> discreteValues) {
		this.discreteValues = discreteValues;
	}

	public DataBinding<String> getDiscreteValuesLabel() {
		return labelBinding;
	}

	public void setDiscreteValuesLabel(DataBinding<String> labelBinding) {
		this.labelBinding = labelBinding;
		/*this.labelBinding.setOwner(this);
		this.labelBinding.setDeclaredType(String.class);
		this.labelBinding.setBindingDefinitionType(BindingDefinitionType.GET);*/
	}

	/*public DataBinding<List<T>> getSecondaryValues() {
		return secondaryValuesBinding;
	}
	
	public void setSecondaryValues(DataBinding<List<T>> secondaryValuesBinding) {
		this.secondaryValuesBinding = secondaryValuesBinding;
	}*/

	public DataBinding<Double> getWeight() {
		return weightBinding;
	}

	public void setWeight(DataBinding<Double> weightBinding) {
		this.weightBinding = weightBinding;
		this.weightBinding.setOwner(this);
		this.weightBinding.setDeclaredType(Double.class);
		this.weightBinding.setBindingDefinitionType(BindingDefinitionType.GET);
	}

	@Override
	protected Iterator<T> iterateParameter() {
		if (discreteValues != null) {
			return discreteValues.iterator();
		}
		return ((List<T>) Collections.emptyList()).iterator();
	}

	@Override
	protected <U> List<FunctionSample<T, U>> retrieveSamples(FGEFunction<U> function) {
		List<FunctionSample<T, U>> returned = super.retrieveSamples(function);
		if (function.getGraphType() == FGEGraphType.SECTORS && function instanceof FGENumericFunction) {
			// In this case, we should discard data with null values
			for (FunctionSample<T, U> s : new ArrayList<>(returned)) {
				if (((Number) s.value).doubleValue() <= 0) {
					// Discard it
					returned.remove(s);
				}
			}
		}
		return returned;
	}

	@Override
	public Double getNormalizedAngle(T value) {
		if (weightBinding != null && weightBinding.isSet() && weightBinding.isValid()) {
			Iterator<T> it = iterateParameter();
			double cumulatedWeight = 0;
			while (it.hasNext()) {
				T t = it.next();
				double angleExtent = getNormalizedAngleExtent(t);
				if (t == value) {
					return cumulatedWeight + angleExtent / 2;
				}
				else {
					cumulatedWeight += angleExtent;
				}
			}

		}
		return (discreteValues.indexOf(value) + 0.5) / (discreteValues.size()) * 360;
	}

	@Override
	public Double getNormalizedAngleExtent(T parameterValue) {
		if (weightBinding != null && weightBinding.isSet() && weightBinding.isValid()) {
			double totalWeight = 0;
			Iterator<T> it = iterateParameter();
			while (it.hasNext()) {
				T t = it.next();
				Double weight = getWeight(t);
				totalWeight += weight;
			}
			return getWeight(parameterValue) / totalWeight * 360;
		}
		return 360.0 / getDiscreteValues().size();
	}

	public String getLabel(T param) {
		getEvaluator().set(getParameter(), param);
		if (getDiscreteValuesLabel() != null && getDiscreteValuesLabel().isSet() && getDiscreteValuesLabel().isValid()) {
			try {
				return getDiscreteValuesLabel().getBindingValue(getEvaluator());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return param.toString();
	}

	/*public List<T> getSecondaryValues(Object primaryValue) {
		getEvaluator().set(getParameter(), primaryValue);
		if (getSecondaryValues() != null && getSecondaryValues().isSet() && getSecondaryValues().isValid()) {
			try {
				return getSecondaryValues().getBindingValue(getEvaluator());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}*/

	public Double getWeight(T param) {
		if (weightBinding != null && weightBinding.isSet() && weightBinding.isValid()) {
			getEvaluator().set(getParameter(), param);
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

	@Override
	public void paintParameters(FGEShapeGraphics g) {

		// TODO: find first numeric function
		if (getFunctions().size() >= 1 && getFunctions().get(0) instanceof FGENumericFunction) {
			FGENumericFunction<?> function = (FGENumericFunction<?>) getFunctions().get(0);
			paintParametersForFunction(g, function);
		}
	}

	protected <N extends Number> void paintParametersForFunction(FGEShapeGraphics g, FGENumericFunction<N> function) {

		if (function.getDisplayLabels()) {

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
						try {
							value = evaluateFunction(function, t);
						} catch (Exception e) {
							e.printStackTrace();
						}
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
					if (value != null && value.doubleValue() > 0) {
						g.drawString(label, new FGEPoint(Math.cos(angle * Math.PI / 180) * radius + 0.5,
								0.5 - Math.sin(angle * Math.PI / 180) * radius), HorizontalTextAlignment.CENTER);
					}
					else {
						// Do not display empty sectors (TODO: should it be configurable ?)
					}
				}
			}
		}
	}

	@Override
	protected <N extends Number> Double getNormalizedAngleForSectors(T parameterValue, FGENumericFunction<N> function) {
		if (function.getFunctionExpression() != null && function.getFunctionExpression().isSet()
				&& function.getFunctionExpression().isValid()) {
			return super.getNormalizedAngleForSectors(parameterValue, function);
		}
		return (discreteValues.indexOf(parameterValue) + 0.5) / (discreteValues.size()) * 360;
	}

	@Override
	protected <N extends Number> Double getNormalizedAngleExtentForSectors(T parameterValue, FGENumericFunction<N> function) {
		if (function.getFunctionExpression() != null && function.getFunctionExpression().isSet()
				&& function.getFunctionExpression().isValid()) {
			return super.getNormalizedAngleExtentForSectors(parameterValue, function);
		}
		return 360.0 / getDiscreteValues().size();
	}

	@Override
	protected <N extends Number> Double getAngleExtent(T parameterValue, FGENumericFunction<N> function) {
		if (function.getFunctionExpression() != null && function.getFunctionExpression().isSet()
				&& function.getFunctionExpression().isValid()) {
			return super.getAngleExtent(parameterValue, function);
		}
		return 360.0 / getDiscreteValues().size();
	}

}
