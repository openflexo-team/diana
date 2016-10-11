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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DefaultBindable;
import org.openflexo.connie.java.JavaBindingFactory;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.graph.FGEFunction.FGEGraphType;
import org.openflexo.fge.graphics.FGEShapeGraphics;

/**
 * This is the common super class for all graphs
 * 
 * @author sylvain
 * 
 */
public abstract class FGEGraph extends DefaultBindable implements Bindable {

	private static BindingFactory JAVA_BINDING_FACTORY = new JavaBindingFactory();

	private BindingModel bindingModel = null;
	private BindingFactory bindingFactory = null;
	private final List<FGEFunction<?>> functions;

	protected final Map<String, Type> parameterTypes;

	private final FGEGraphEvaluator evaluator;

	public FGEGraph() {
		functions = new ArrayList<FGEFunction<?>>();
		parameterTypes = new HashMap<String, Type>();
		bindingModel = new BindingModel();
		evaluator = new FGEGraphEvaluator();
	}

	public void update() {
		// System.out.println("Updating graph " + this);
		for (FGEFunction<?> f : functions) {
			// System.out.println("On recalcule la representation de la fonction " + f.getFunctionExpression());
			f.updateRepresentation();
		}
	}

	public void setParameter(String parameterName, Type parameterType) {
		parameterTypes.put(parameterName, parameterType);
		bindingModel.addToBindingVariables(new BindingVariable(parameterName, parameterType));
	}

	/**
	 * Called for graph painting
	 * 
	 * @param g
	 */
	public void paint(FGEShapeGraphics g) {

		g.translate(getBorderLeft(), getBorderTop());
	}

	public <T> FGEFunction<T> addDiscreteFunction(String functionName, Class<T> functionType, DataBinding<T> functionExpression,
			FGEGraphType type) {
		FGEFunction<T> returned = new FGEDiscreteFunction<T>(functionName, functionType, functionExpression, type, this);
		functions.add(returned);
		return returned;
	}

	public <T extends Number> FGENumericFunction<T> addNumericFunction(String functionName, Type functionType,
			DataBinding<T> functionExpression, FGEGraphType type, T minValue, T maxValue) {
		FGENumericFunction<T> returned = new FGENumericFunction<T>(functionName, functionType, functionExpression, type, minValue, maxValue,
				this);
		functions.add(returned);
		return returned;
	}

	public <T extends Number> FGENumericFunction<T> addNumericFunction(String functionName, Type functionType,
			DataBinding<T> functionExpression, FGEGraphType type) {
		FGENumericFunction<T> returned = new FGENumericFunction<T>(functionName, functionType, functionExpression, type, this);
		functions.add(returned);
		return returned;
	}

	public FGEFunction<?> getFunction(String functionName) {
		for (FGEFunction<?> f : getFunctions()) {
			if (f.getFunctionName() != null && f.getFunctionName().equals(functionName)) {
				return f;
			}
		}
		return null;
	}

	public List<FGEFunction<?>> getFunctions() {
		return functions;
	}

	/**
	 * Returns the number of functions of supplied graph type declared in this FGEGraph
	 * 
	 * @param graphType
	 * @return
	 */
	public int getNumberOfFunctionsOfType(FGEGraphType graphType) {
		int returned = 0;
		for (FGEFunction<?> f : functions) {
			if (f.getGraphType() == graphType) {
				returned++;
			}
		}
		return returned;
	}

	/**
	 * Return the index of supplied function in the collection represented by all functions declared in this FGEGraph which have the same
	 * {@link FGEGraphType}
	 * 
	 * @param function
	 * @return
	 */
	public int getIndexOfFunctionsOfType(FGEFunction<?> function) {
		int returned = 0;
		for (FGEFunction<?> f : functions) {
			if (f == function) {
				return returned;
			}
			if (f.getGraphType() == function.getGraphType()) {
				returned++;
			}
		}
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
		// System.out.println("On s'en fout que le binding ait change ??? " + dataBinding);
	}

	@Override
	public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
		// System.out.println("On s'en fout que le binding ait ete decode ??? " + dataBinding);
	}

	public FGEGraphEvaluator getEvaluator() {
		return evaluator;
	}

	protected abstract <T> FunctionRepresentation buildRepresentationForFunction(FGEFunction<T> function);

	public int getBorderTop() {
		return borderTop;
	}

	public void setBorderTop(int borderTop) {
		if (borderTop != this.borderTop) {
			int oldValue = this.borderTop;
			this.borderTop = borderTop;
			getPropertyChangeSupport().firePropertyChange("borderTop", oldValue, borderTop);
		}
	}

	public int getBorderBottom() {
		return borderBottom;
	}

	public void setBorderBottom(int borderBottom) {
		if (borderBottom != this.borderBottom) {
			int oldValue = this.borderBottom;
			this.borderBottom = borderBottom;
			getPropertyChangeSupport().firePropertyChange("borderBottom", oldValue, borderBottom);
		}
	}

	public int getBorderLeft() {
		return borderLeft;
	}

	public void setBorderLeft(int borderLeft) {
		if (borderLeft != this.borderLeft) {
			int oldValue = this.borderLeft;
			this.borderLeft = borderLeft;
			getPropertyChangeSupport().firePropertyChange("borderLeft", oldValue, borderLeft);
		}
	}

	public int getBorderRight() {
		return borderRight;
	}

	public void setBorderRight(int borderRight) {
		if (borderRight != this.borderRight) {
			int oldValue = this.borderRight;
			this.borderRight = borderRight;
			getPropertyChangeSupport().firePropertyChange("borderRight", oldValue, borderRight);
		}
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

	private int borderTop = 10;
	private int borderBottom = 10;
	private int borderLeft = 10;
	private int borderRight = 10;

	public class ElementRepresentation {
		public FGEArea area;
		public ForegroundStyle foregroundStyle;
		public BackgroundStyle backgroundStyle;

		public ElementRepresentation(FGEArea area, ForegroundStyle foregroundStyle, BackgroundStyle backgroundStyle) {
			super();
			this.area = area;
			this.foregroundStyle = foregroundStyle;
			this.backgroundStyle = backgroundStyle;
		}
	}

	public class FunctionRepresentation {
		public List<ElementRepresentation> elements;

		public FunctionRepresentation() {
			elements = new ArrayList<>();
		}

		public FunctionRepresentation(FGEArea area, ForegroundStyle foregroundStyle, BackgroundStyle backgroundStyle) {
			this();
			elements.add(new ElementRepresentation(area, foregroundStyle, backgroundStyle));
		}

		public FunctionRepresentation(List<ElementRepresentation> elements) {
			super();
			this.elements = elements;
		}

	}

}
