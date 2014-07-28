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
import java.util.List;

import org.openflexo.antar.binding.DataBinding;

/**
 * This is the common super class for all graphs
 * 
 * @author sylvain
 * 
 * @param <T>
 *            type
 */
public abstract class FGEGraph<T> {

	private final List<DataBinding<T>> inputs;

	public FGEGraph() {
		inputs = new ArrayList<DataBinding<T>>();
	}

	public void addInput(DataBinding<T> input) {
		inputs.add(input);
	}

	public List<DataBinding<T>> getInputs() {
		return inputs;
	}

	public DataBinding<T> getInput(int inputId) {
		return inputs.get(inputId);
	}
}
