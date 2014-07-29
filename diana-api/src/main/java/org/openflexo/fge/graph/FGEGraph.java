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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.JavaBindingFactory;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.area.FGEArea;

/**
 * This is the common super class for all graphs
 * 
 * @author sylvain
 * 
 */
public abstract class FGEGraph implements Bindable {

	private static BindingFactory JAVA_BINDING_FACTORY = new JavaBindingFactory();

	public static enum GraphType {
		POINTS, POLYLIN, RECT_POLYLIN, CURVE, BAR_GRAPH
	}

	private BindingModel bindingModel = null;
	private BindingFactory bindingFactory = null;
	private final List<FGEFunction<?>> functions;

	protected final Map<String, Class<?>> parameterTypes;

	private final FGEGraphEvaluator evaluator;

	private double x = 0;
	private double y = 0;
	private double width = 100;
	private double height = 100;

	private FGEArea resultingArea = null;

	public FGEGraph() {
		functions = new ArrayList<FGEFunction<?>>();
		parameterTypes = new HashMap<String, Class<?>>();
		bindingModel = new BindingModel();
		evaluator = new FGEGraphEvaluator();
	}

	public void setParameter(String parameterName, Class<?> parameterType) {
		parameterTypes.put(parameterName, parameterType);
		bindingModel.addToBindingVariables(new BindingVariable(parameterName, parameterType));
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
		invalidateResultingArea();
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
		invalidateResultingArea();
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
		invalidateResultingArea();
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
		invalidateResultingArea();
	}

	public void invalidateResultingArea() {
		resultingArea = null;
	}

	public FGEArea getResultingArea() {
		if (resultingArea == null) {
			resultingArea = new FGERectangle(x, y, width, height, Filling.FILLED);
		}
		return resultingArea;
	}

	public <T> FGEFunction<T> addFunction(String functionName, Class<T> functionType, DataBinding<T> functionExpression, GraphType type) {
		FGEFunction<T> returned = new FGEFunction<T>(functionName, functionType, functionExpression, type, this);
		functions.add(returned);
		return returned;
	}

	public <T extends Number> FGENumericFunction<T> addNumericFunction(String functionName, Class<T> functionType,
			DataBinding<T> functionExpression, GraphType type, T minValue, T maxValue) {
		FGENumericFunction<T> returned = new FGENumericFunction<T>(functionName, functionType, functionExpression, type, minValue,
				maxValue, this);
		functions.add(returned);
		return returned;
	}

	public <T extends Number> FGENumericFunction<T> addNumericFunction(String functionName, Class<T> functionType,
			DataBinding<T> functionExpression, GraphType type) {
		FGENumericFunction<T> returned = new FGENumericFunction<T>(functionName, functionType, functionExpression, type, this);
		functions.add(returned);
		return returned;
	}

	@Override
	public BindingModel getBindingModel() {
		return bindingModel;
	}

	@Override
	public BindingFactory getBindingFactory() {
		if (this.bindingFactory != null) {
			return this.bindingFactory;
		}
		return FGEGraph.JAVA_BINDING_FACTORY;
	}

	public void setBindingFactory(BindingFactory bindingFactory) {
		this.bindingFactory = bindingFactory;
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
		System.out.println("On s'en fout que le binding ait change ??? " + dataBinding);
	}

	@Override
	public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
		System.out.println("On s'en fout que le binding ait ete decode ??? " + dataBinding);
	}

	public FGEGraphEvaluator getEvaluator() {
		return evaluator;
	}

	public class FGEGraphEvaluator implements BindingEvaluationContext {

		private final Map<String, Object> values = new HashMap<String, Object>();

		public Object get(String parameter) {
			return values.get(parameter);
		}

		public void set(String parameter, Object value) {
			values.put(parameter, value);
		}

		@Override
		public Object getValue(BindingVariable variable) {
			return values.get(variable.getVariableName());
		}

	}

}
