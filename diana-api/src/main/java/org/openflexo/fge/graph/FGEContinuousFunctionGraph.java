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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a 2D-base graph, where a coordinates is based on an expression using opposite coordinate
 * 
 * @author sylvain
 * 
 * @param <O>
 *            the represented type
 */
public class FGEContinuousFunctionGraph<X extends Number> extends FGEFunctionGraph<X> {

	private final Class<X> numberClass;

	private X minValue = null;
	private X maxValue = null;
	private X minorTickSpacing = null;
	private X majorTickSpacing = null;
	private Integer stepsNumber = null;

	public FGEContinuousFunctionGraph(Class<X> numberClass) {
		super();
		this.numberClass = numberClass;
	}

	/**
	 * Return the number of steps beeing drawn for continuous function (sampling for float or double value)<br>
	 * Spacing between each sample is then obtained by (maxValue - minValue)/stepNumber<br>
	 * Default value is 20 for floating types, -1 for other numbers<br>
	 * Note that Integer/Long/Short/Byte types are iterated by unit increment, and do not use this parameter by default
	 * 
	 * @return
	 */
	public int getStepsNumber() {
		if (stepsNumber == null) {
			if (isFloatingType()) {
				return 20;
			} else {
				return -1;
			}
		}
		return stepsNumber;
	}

	/**
	 * Sets the number of steps beeing drawn for continuous function (sampling for float or double value)<br>
	 * Spacing between each sample is then obtained by (maxValue - minValue)/stepNumber<br>
	 * Default value is 20 for floating types, -1 for other numbers<br>
	 * Note that Integer/Long/Short/Byte types are iterated by unit increment, and do not use this parameter by default
	 * 
	 * @param stepNumber
	 */
	public void setStepsNumber(int stepsNumber) {
		this.stepsNumber = stepsNumber;
	}

	/**
	 * Sets range for parameter<br>
	 * This range configure the drawing of continuous function (sampling)
	 * 
	 * @param minValue
	 * @param maxValue
	 */
	public void setParameterRange(X minValue, X maxValue) {
		setParameterMinValue(minValue);
		setParameterMaxValue(maxValue);
	}

	/**
	 * Internal method used to retrieve min value of right type for each {@link Number} class
	 * 
	 * @return
	 */
	private X getDefaultMinValue() {
		if (getNumberClass().equals(Double.class)) {
			return (X) (Double) Double.MIN_VALUE;
		} else if (getNumberClass().equals(Float.class)) {
			return (X) (Float) Float.MIN_VALUE;
		} else if (getNumberClass().equals(Long.class)) {
			return (X) (Long) Long.MIN_VALUE;
		} else if (getNumberClass().equals(Integer.class)) {
			return (X) (Integer) Integer.MIN_VALUE;
		} else if (getNumberClass().equals(Short.class)) {
			return (X) (Short) Short.MIN_VALUE;
		} else if (getNumberClass().equals(Byte.class)) {
			return (X) (Byte) Byte.MIN_VALUE;
		}
		return null;
	}

	/**
	 * Internal method used to retrieve max value of right type for each {@link Number} class
	 * 
	 * @return
	 */
	private X getDefaultMaxValue() {
		if (getNumberClass().equals(Double.class)) {
			return (X) (Double) Double.MAX_VALUE;
		} else if (getNumberClass().equals(Float.class)) {
			return (X) (Float) Float.MAX_VALUE;
		} else if (getNumberClass().equals(Long.class)) {
			return (X) (Long) Long.MAX_VALUE;
		} else if (getNumberClass().equals(Integer.class)) {
			return (X) (Integer) Integer.MAX_VALUE;
		} else if (getNumberClass().equals(Short.class)) {
			return (X) (Short) Short.MAX_VALUE;
		} else if (getNumberClass().equals(Byte.class)) {
			return (X) (Byte) Byte.MAX_VALUE;
		}
		return null;
	}

	public X getParameterMinValue() {
		if (minValue == null) {
			return getDefaultMinValue();
		}
		return minValue;
	}

	public void setParameterMinValue(X minValue) {
		this.minValue = minValue;
	}

	public X getParameterMaxValue() {
		if (maxValue == null) {
			return getDefaultMaxValue();
		}
		return maxValue;
	}

	public void setParameterMaxValue(X maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * This method returns the major tick spacing. The number that is returned represents the distance, measured in values, between each
	 * major tick mark.
	 * 
	 * @return the number of values between minor ticks
	 */
	public X getParameterMajorTickSpacing() {
		return majorTickSpacing;
	}

	/**
	 * This method sets the minor tick spacing. The number that is passed in represents the distance, measured in values, between each major
	 * tick mark.
	 * 
	 */
	public void setParameterMajorTickSpacing(X majorTickSpacing) {
		this.majorTickSpacing = majorTickSpacing;
	}

	/**
	 * This method returns the minor tick spacing. The number that is returned represents the distance, measured in values, between each
	 * minor tick mark.
	 * 
	 * @return the number of values between minor ticks
	 */
	public X getParameterMinorTickSpacing() {
		return minorTickSpacing;
	}

	/**
	 * This method sets the minor tick spacing. The number that is passed in represents the distance, measured in values, between each minor
	 * tick mark.
	 * 
	 */
	public void setParameterMinorTickSpacing(X minorTickSpacing) {
		this.minorTickSpacing = minorTickSpacing;
	}

	protected Class<X> getNumberClass() {
		return numberClass;
	}

	protected boolean isFloatingType() {
		return getNumberClass().equals(Double.class) || getNumberClass().equals(Float.class);
	}

	@Override
	protected Iterator<X> iterateParameter() {

		List<X> returned = new ArrayList<X>();

		if (getStepsNumber() > -1) {
			for (int i = 0; i < getStepsNumber() + 1; i++) {
				X value = null;
				if (getNumberClass().equals(Double.class)) {
					value = (X) (new Double(getParameterMinValue().doubleValue()
							+ ((getParameterMaxValue().doubleValue() - getParameterMinValue().doubleValue()) * i / getStepsNumber())));
				} else if (getNumberClass().equals(Float.class)) {
					value = (X) (new Float(getParameterMinValue().floatValue()
							+ ((getParameterMaxValue().floatValue() - getParameterMinValue().floatValue()) * i / getStepsNumber())));
				} else if (getNumberClass().equals(Long.class)) {
					value = (X) (new Long(getParameterMinValue().longValue()
							+ ((getParameterMaxValue().longValue() - getParameterMinValue().longValue()) * i / getStepsNumber())));
				} else if (getNumberClass().equals(Integer.class)) {
					value = (X) (new Integer(getParameterMinValue().intValue()
							+ ((getParameterMaxValue().intValue() - getParameterMinValue().intValue()) * i / getStepsNumber())));
				} else if (getNumberClass().equals(Short.class)) {
					value = (X) (new Short(
							(short) (getParameterMinValue().shortValue() + ((getParameterMaxValue().shortValue() - getParameterMinValue()
									.shortValue()) * i / getStepsNumber()))));
				} else if (getNumberClass().equals(Byte.class)) {
					value = (X) (new Byte(
							(byte) (getParameterMinValue().byteValue() + ((getParameterMaxValue().byteValue() - getParameterMinValue()
									.byteValue()) * i / getStepsNumber()))));
				}
				returned.add(value);
			}
		} else {
			// Type can be only long/int/short/byte
			if (getNumberClass().equals(Long.class)) {
				for (long i = getParameterMinValue().longValue(); i <= getParameterMaxValue().longValue(); i++) {
					returned.add((X) (Long) i);
				}
			} else if (getNumberClass().equals(Integer.class)) {
				for (int i = getParameterMinValue().intValue(); i <= getParameterMaxValue().intValue(); i++) {
					returned.add((X) (Integer) i);
				}
			} else if (getNumberClass().equals(Short.class)) {
				for (short i = getParameterMinValue().shortValue(); i <= getParameterMaxValue().shortValue(); i++) {
					returned.add((X) (Short) i);
				}
			} else if (getNumberClass().equals(Byte.class)) {
				for (byte i = getParameterMinValue().byteValue(); i <= getParameterMaxValue().byteValue(); i++) {
					returned.add((X) (Byte) i);
				}
			}
		}
		return returned.iterator();
	}

	@Override
	protected Double getNormalizedPosition(X value) {
		if (getNumberClass().equals(Double.class)) {
			return (value.doubleValue() - getParameterMinValue().doubleValue())
					/ (getParameterMaxValue().doubleValue() - getParameterMinValue().doubleValue());
		} else if (getNumberClass().equals(Float.class)) {
			return (double) (value.floatValue() - getParameterMinValue().floatValue())
					/ (getParameterMaxValue().floatValue() - getParameterMinValue().floatValue());
		} else if (getNumberClass().equals(Long.class)) {
			return (double) (value.longValue() - getParameterMinValue().longValue())
					/ (getParameterMaxValue().longValue() - getParameterMinValue().longValue());
		} else if (getNumberClass().equals(Integer.class)) {
			return (double) (value.intValue() - getParameterMinValue().intValue())
					/ (getParameterMaxValue().intValue() - getParameterMinValue().intValue());
		} else if (getNumberClass().equals(Short.class)) {
			return (double) (value.shortValue() - getParameterMinValue().shortValue())
					/ (getParameterMaxValue().shortValue() - getParameterMinValue().shortValue());
		} else if (getNumberClass().equals(Byte.class)) {
			return (double) (value.byteValue() - getParameterMinValue().byteValue())
					/ (getParameterMaxValue().byteValue() - getParameterMinValue().byteValue());
		}
		return 0.0;
	}

}
