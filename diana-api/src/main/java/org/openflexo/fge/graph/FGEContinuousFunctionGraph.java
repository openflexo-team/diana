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


/**
 * Represents a 2D-base graph, where a coordinates is based on an expression using opposite coordinate
 * 
 * @author sylvain
 * 
 * @param <O>
 *            the represented type
 */
public class FGEContinuousFunctionGraph<X extends Number> extends FGEFunctionGraph<X> {

	private X minValue = null;
	private X maxValue = null;
	private X minorTickSpacing = null;
	private X majorTickSpacing = null;

	public void setParameterRange(X minValue, X maxValue) {
		setParameterMinValue(minValue);
		setParameterMaxValue(maxValue);
	}

	public X getParameterMinValue() {
		return minValue;
	}

	public void setParameterMinValue(X minValue) {
		this.minValue = minValue;
	}

	public X getParameterMaxValue() {
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

}
