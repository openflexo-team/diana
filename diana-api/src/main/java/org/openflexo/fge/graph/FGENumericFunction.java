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

import java.util.List;

import org.openflexo.antar.binding.DataBinding;

/**
 * Represents a numeric function as a typed expression<br>
 * This function return numeric values.
 * 
 * @author sylvain
 * 
 * @param <T>
 *            type of values given by the expression (should be a {@link Number} subclass)
 */
public class FGENumericFunction<T extends Number> extends FGEFunction<T> {

	private T minValue = null;
	private T maxValue = null;
	private T minorTickSpacing = null;
	private T majorTickSpacing = null;

	public FGENumericFunction(String functionName, Class<T> functionType, DataBinding<T> functionExpression,
			FGEFunctionGraph.GraphType graphType, FGEGraph graph) {
		super(functionName, functionType, functionExpression, graphType, graph);
	}

	public FGENumericFunction(String functionName, Class<T> functionType, DataBinding<T> functionExpression,
			FGEFunctionGraph.GraphType graphType, T minValue, T maxValue, FGEGraph graph) {
		super(functionName, functionType, functionExpression, graphType, graph);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public void setRange(T minValue, T maxValue) {
		setMinValue(minValue);
		setMaxValue(maxValue);
	}

	public T getMinValue() {
		if (minValue == null) {
			return computedMinValue;
		}
		return minValue;
	}

	public void setMinValue(T minValue) {
		this.minValue = minValue;
	}

	public T getMaxValue() {
		if (maxValue == null) {
			return computedMaxValue;
		}
		return maxValue;
	}

	public void setMaxValue(T maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * This method returns the major tick spacing. The number that is returned represents the distance, measured in values, between each
	 * major tick mark.
	 * 
	 * @return the number of values between minor ticks
	 */
	public T getMajorTickSpacing() {
		return majorTickSpacing;
	}

	/**
	 * This method sets the minor tick spacing. The number that is passed in represents the distance, measured in values, between each major
	 * tick mark.
	 * 
	 */
	public void setMajorTickSpacing(T majorTickSpacing) {
		this.majorTickSpacing = majorTickSpacing;
	}

	/**
	 * This method returns the minor tick spacing. The number that is returned represents the distance, measured in values, between each
	 * minor tick mark.
	 * 
	 * @return the number of values between minor ticks
	 */
	public T getMinorTickSpacing() {
		return minorTickSpacing;
	}

	/**
	 * This method sets the minor tick spacing. The number that is passed in represents the distance, measured in values, between each minor
	 * tick mark.
	 * 
	 */
	public void setMinorTickSpacing(T minorTickSpacing) {
		this.minorTickSpacing = minorTickSpacing;
	}

	private T computedMinValue;
	private T computedMaxValue;

	@Override
	protected <X> List<FunctionSample<X>> retrieveSamples(FGEFunctionGraph<X> graph) {

		List<FunctionSample<X>> samples = super.retrieveSamples(graph);

		computedMinValue = null;
		computedMaxValue = null;

		for (FunctionSample<X> s : samples) {
			T value = s.value;
			if (value instanceof Double) {
				if (computedMinValue == null || (Double) value < (Double) computedMinValue) {
					computedMinValue = value;
				}
				if (computedMaxValue == null || (Double) value > (Double) computedMaxValue) {
					computedMaxValue = value;
				}
			} else if (value instanceof Float) {
				if (computedMinValue == null || (Float) value < (Float) computedMinValue) {
					computedMinValue = value;
				}
				if (computedMaxValue == null || (Float) value > (Float) computedMaxValue) {
					computedMaxValue = value;
				}
			} else if (value instanceof Long) {
				if (computedMinValue == null || (Long) value < (Long) computedMinValue) {
					computedMinValue = value;
				}
				if (computedMaxValue == null || (Long) value > (Long) computedMaxValue) {
					computedMaxValue = value;
				}
			} else if (value instanceof Integer) {
				if (computedMinValue == null || (Integer) value < (Integer) computedMinValue) {
					computedMinValue = value;
				}
				if (computedMaxValue == null || (Integer) value > (Integer) computedMaxValue) {
					computedMaxValue = value;
				}
			} else if (value instanceof Short) {
				if (computedMinValue == null || (Short) value < (Short) computedMinValue) {
					computedMinValue = value;
				}
				if (computedMaxValue == null || (Short) value > (Short) computedMaxValue) {
					computedMaxValue = value;
				}
			} else if (value instanceof Byte) {
				if (computedMinValue == null || (Byte) value < (Byte) computedMinValue) {
					computedMinValue = value;
				}
				if (computedMaxValue == null || (Byte) value > (Byte) computedMaxValue) {
					computedMaxValue = value;
				}
			}
		}

		System.out.println("computedMinValue=" + computedMinValue);
		System.out.println("computedMaxValue=" + computedMaxValue);

		return samples;
	}

	@Override
	protected Double getNormalizedPosition(T value) {
		return (value.doubleValue() - getMinValue().doubleValue()) / (getMaxValue().doubleValue() - getMinValue().doubleValue());
	}
}