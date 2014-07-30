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

import java.util.Iterator;
import java.util.List;

import org.openflexo.antar.binding.DataBinding;

/**
 * Represents a 2D-base graph, where parameter takes discrete values
 * 
 * @author sylvain
 * 
 * @param <T>
 *            type of discrete values
 */
public class FGEDiscreteFunctionGraph<T> extends FGEFunctionGraph<T> {

	private List<T> discreteValues;
	private DataBinding<String> labelBinding;

	// private double spacing = 10;

	public FGEDiscreteFunctionGraph() {
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
	}

	/*public double getDiscreteValuesSpacing() {
		return spacing;
	}

	public void setDiscreteValuesSpacing(double spacing) {
		this.spacing = spacing;
	}*/

	@Override
	protected Iterator<T> iterateParameter() {
		return discreteValues.iterator();
	}

	@Override
	protected Double getNormalizedPosition(T value) {
		return ((double) discreteValues.indexOf(value)) / discreteValues.size();
	}

}
