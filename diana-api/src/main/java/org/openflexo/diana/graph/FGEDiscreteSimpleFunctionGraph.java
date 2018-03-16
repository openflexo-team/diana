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

package org.openflexo.diana.graph;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.diana.TextStyle;
import org.openflexo.diana.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.graphics.FGEShapeGraphics;

/**
 * Represents a 2D-base graph, where parameter takes discrete values
 * 
 * @author sylvain
 * 
 * @param <T>
 *            type of discrete values
 */
public class FGEDiscreteSimpleFunctionGraph<T> extends FGESimpleFunctionGraph<T> {

	private List<T> discreteValues;
	private DataBinding<String> labelBinding;

	// private double spacing = 10;

	public FGEDiscreteSimpleFunctionGraph() {
		super();
	}

	public List<T> getDiscreteValues() {
		return discreteValues;
	}

	public void setDiscreteValues(List<T> discreteValues) {
		if ((discreteValues == null && this.discreteValues != null)
				|| (discreteValues != null && !discreteValues.equals(this.discreteValues))) {
			List<T> oldValue = this.discreteValues;
			this.discreteValues = discreteValues;
			getPropertyChangeSupport().firePropertyChange("discreteValues", oldValue, discreteValues);
			// And update the graph
			update();
		}
	}

	public DataBinding<String> getDiscreteValuesLabel() {
		return labelBinding;
	}

	public void setDiscreteValuesLabel(DataBinding<String> labelBinding) {
		this.labelBinding = labelBinding;
		this.labelBinding.setOwner(this);
		this.labelBinding.setDeclaredType(String.class);
		this.labelBinding.setBindingDefinitionType(BindingDefinitionType.GET);
	}

	/*public double getDiscreteValuesSpacing() {
		return spacing;
	}
	
	public void setDiscreteValuesSpacing(double spacing) {
		this.spacing = spacing;
	}*/

	@Override
	protected Iterator<T> iterateParameter() {
		if (discreteValues != null) {
			return discreteValues.iterator();
		}
		return ((List<T>) Collections.emptyList()).iterator();
	}

	@Override
	protected Double getNormalizedPosition(T value) {
		return (discreteValues.indexOf(value) + 0.5) / (discreteValues.size());
	}

	public String getLabel(T param) {
		getEvaluator().set(getParameter(), param);
		if (getDiscreteValuesLabel() != null && getDiscreteValuesLabel().isSet() && getDiscreteValuesLabel().isValid()) {
			try {
				return getDiscreteValuesLabel().getBindingValue(getEvaluator());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return param.toString();
	}

	@Override
	public void paintParameters(FGEShapeGraphics g) {

		TextStyle ts = g.getNode().getTextStyle();

		double relativeTextWidth = (double) ts.getFont().getSize() / g.getViewWidth();
		double relativeTextHeight = (double) ts.getFont().getSize() / g.getViewHeight();

		g.useTextStyle(ts);

		Iterator<T> it = iterateParameter();

		while (it.hasNext()) {
			T t = it.next();
			String label = getLabel(t);
			if (getParameterOrientation() == Orientation.HORIZONTAL) {
				g.drawString(label, new FGEPoint(getNormalizedPosition(t), 1.0 + relativeTextHeight), HorizontalTextAlignment.CENTER);
			}
			else {
				g.drawString(label, new FGEPoint(-relativeTextWidth, getNormalizedPosition(t)), HorizontalTextAlignment.CENTER);
			}
		}
	}
}
