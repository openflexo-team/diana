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

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fge.graphics.FGEShapeGraphics;

/**
 * Represents a 2D-base graph representing functions where:
 * <ul>
 * <li>a coordinate is iterated over continuous or discrete values</li>
 * <li>a coordinates is based on an expression using opposite coordinate (iterated value, which can be discrete or continuous)</li><br>
 * </ul>
 * 
 * Such graphs allows only one parameter.
 * 
 * @author sylvain
 * 
 * @param <X>
 *            type of value which plays iterator role
 */
public abstract class FGEFunctionGraph<X> extends FGEGraph {

	public static enum Orientation {
		HORIZONTAL, VERTICAL;
	}

	private Orientation parameterOrientation = Orientation.HORIZONTAL;

	public FGEFunctionGraph() {
		super();
	}

	public String getParameter() {
		if (parameterTypes.size() > 0) {
			return parameterTypes.keySet().iterator().next();
		}
		return null;
	}

	@Override
	public void setParameter(String parameterName, Class<?> parameterType) {

		if (parameterTypes.size() > 0) {
			throw new IllegalArgumentException("FGEFunctionGraph could not have more than one parameter: existing parameter "
					+ getParameter() + " while adding " + parameterName);
		}
		super.setParameter(parameterName, parameterType);
	}

	public Orientation getParameterOrientation() {
		return parameterOrientation;
	}

	public void setParameterOrientation(Orientation orientation) {
		this.parameterOrientation = orientation;
	}

	public <Y> Y evaluateFunction(FGEFunction<Y> function, X value) throws TypeMismatchException, NullReferenceException,
			InvocationTargetException {
		getEvaluator().set(getParameter(), value);
		return function.evaluate();
	}

	protected abstract Iterator<X> iterateParameter();

	protected abstract Double getNormalizedPosition(X value);

	/**
	 * Called for graph painting
	 * 
	 * @param g
	 */
	@Override
	public void paint(FGEShapeGraphics g) {

		// System.out.println("Painting graph");
		// System.out.println("width = " + g.getViewWidth());
		// System.out.println("height = " + g.getViewHeight());

		super.paint(g);

		// Paint parameters
		paintParameters(g);

		for (FGEFunction<?> f : getFunctions()) {

			f.paint(g);

		}

	}

	public abstract void paintParameters(FGEShapeGraphics g);

}
