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
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ForegroundStyle;

/**
 * Represents a function as a typed expression<br>
 * Note that computed values may not be {@link Number} instances
 * 
 * @author sylvain
 * 
 * @param <T>
 *            type of values given by the expression
 */
public class FGEFunction<T> {

	private static final Logger logger = Logger.getLogger(FGEFunction.class.getPackage().getName());

	private final String functionName;
	private final Class<T> functionType;
	private final DataBinding<T> functionExpression;
	private final FGEFunctionGraph.GraphType graphType;

	private ForegroundStyle foregroundStyle;
	private BackgroundStyle backgroundStyle;

	private final FGEGraph graph;

	public FGEFunction(String functionName, Class<T> functionType, DataBinding<T> functionExpression, FGEFunctionGraph.GraphType graphType,
			FGEGraph graph) {
		super();
		this.functionName = functionName;
		this.functionType = functionType;
		this.functionExpression = new DataBinding<T>(functionExpression.toString());
		this.functionExpression.setOwner(graph);
		this.functionExpression.setDeclaredType(functionType);
		this.functionExpression.setBindingDefinitionType(BindingDefinitionType.GET);
		if (!this.functionExpression.isValid()) {
			logger.warning("Invalid expression in FGEFunction:" + this.functionExpression + " reason="
					+ this.functionExpression.invalidBindingReason());
		}
		this.graph = graph;
		this.graphType = graphType;
	}

	public String getFunctionName() {
		return functionName;
	}

	public Class<T> getFunctionType() {
		return functionType;
	}

	public DataBinding<T> getFunctionExpression() {
		return functionExpression;
	}

	public FGEGraph getGraph() {
		return graph;
	}

	public FGEFunctionGraph.GraphType getGraphType() {
		return graphType;
	}

	public ForegroundStyle getForegroundStyle() {
		return foregroundStyle;
	}

	public void setForegroundStyle(ForegroundStyle foregroundStyle) {
		this.foregroundStyle = foregroundStyle;
	}

	public BackgroundStyle getBackgroundStyle() {
		return backgroundStyle;
	}

	public void setBackgroundStyle(BackgroundStyle backgroundStyle) {
		this.backgroundStyle = backgroundStyle;
	}

	public T evaluate() throws TypeMismatchException, NullReferenceException, InvocationTargetException {
		T returned = functionExpression.getBindingValue(getGraph().getEvaluator());
		getGraph().getEvaluator().set(functionName, returned);
		return returned;
	}

}