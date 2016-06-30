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
import java.util.Iterator;
import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.FGEShapeGraphics;

/**
 * Represents a 2D-base graph, where parameter takes discrete values
 * 
 * @author sylvain
 * 
 * @param <T>
 *            type of discrete values
 */
public class FGEDiscretePolarFunctionGraph<T> extends FGEPolarFunctionGraph<T> {

	private List<T> discreteValues;
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
		this.labelBinding.setOwner(this);
		this.labelBinding.setDeclaredType(String.class);
		this.labelBinding.setBindingDefinitionType(BindingDefinitionType.GET);
	}

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
		return discreteValues.iterator();
	}

	@Override
	public Double getNormalizedAngle(T value) {
		if (weightBinding != null && weightBinding.isSet() && weightBinding.isValid()) {
			System.out.println("OK faut que je regarde le poids respectif de chaque valeur");
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
		return (discreteValues.indexOf(value) + 0.5) / (discreteValues.size()) * 2 * Math.PI;
	}

	@Override
	public Double getNormalizedAngleExtent(T parameterValue) {
		if (weightBinding != null && weightBinding.isSet() && weightBinding.isValid()) {
			double totalWeight = 0;
			Iterator<T> it = iterateParameter();
			while (it.hasNext()) {
				T t = it.next();
				Double weight = getWeight(t);
				System.out.println("For " + t + " weight=" + weight);
				totalWeight += weight;
			}
			return getWeight(parameterValue) / totalWeight * 2 * Math.PI;
		}
		return 2 * Math.PI / getDiscreteValues().size();
	}

	public String getLabel(T param) {
		getEvaluator().set(getParameter(), param);
		try {
			return getDiscreteValuesLabel().getBindingValue(getEvaluator());
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return param.toString();
	}

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
		return 2 * Math.PI / getDiscreteValues().size();
	}

	@Override
	public void paintParameters(FGEShapeGraphics g) {

		TextStyle ts = g.getNode().getTextStyle();

		// double relativeTextWidth = (double) ts.getFont().getSize() / g.getViewWidth();
		// double relativeTextHeight = (double) ts.getFont().getSize() / g.getViewHeight();

		g.useTextStyle(ts);

		Iterator<T> it = iterateParameter();
		while (it.hasNext()) {
			T t = it.next();
			String label = getLabel(t);
			Double angle = getNormalizedAngle(t);
			Double radius = 0.5;
			if (getFunctions().size() == 1) {
				FGEFunction function = getFunctions().get(0);
				Object value = null;
				try {
					value = evaluateFunction(function, t);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				radius = function.getNormalizedPosition(value) / 2 + 0.05;
			}
			g.drawString(label, new FGEPoint(Math.cos(angle) * radius + 0.5, 0.5 - Math.sin(angle) * radius),
					HorizontalTextAlignment.CENTER);
		}
	}
}
