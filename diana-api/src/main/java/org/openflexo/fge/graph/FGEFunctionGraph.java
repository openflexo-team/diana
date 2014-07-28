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
public class FGEFunctionGraph<X, Y> extends FGEGraph<Y> {

	public static enum GraphType {
		POINTS, POLYLIN, RECT_POLYLIN, CURVE, BAR_GRAPH
	}

	public static enum Orientation {
		HORIZONTAL, VERTICAL;
	}

	private GraphType graphType;

	/*public Y getValue(X x) {
		return getValue(x, 0);
	}*/

	/*public Y getValue(X x, int inputId) {
		DataBinding<Y> input = getInput(inputId);
		
	}*/
}
