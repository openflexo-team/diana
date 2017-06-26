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

import org.openflexo.connie.DataBinding;

/**
 * Represents a discrete function as a typed expression<br>
 * 
 * @author sylvain
 * 
 * @param <T>
 *            type of values given by the expression (can be of any class)
 */
public class FGEDiscreteFunction<T> extends FGEFunction<T> {

	public FGEDiscreteFunction(String functionName, Class<T> functionType, DataBinding<T> functionExpression, FGEGraphType graphType,
			FGEGraph graph) {
		super(functionName, functionType, functionExpression, graphType, graph);
	}

	/*@Override
	protected <X> List<org.openflexo.fge.graph.FGEFunction.FunctionSample<X, T>> retrieveSamples(FGESingleParameteredGraph<X> graph) {
		System.out.println("Pour les samples, je devrais pas trier par valeur ???");
		List<FunctionSample<X, T>> returned = super.retrieveSamples(graph);
		for (FunctionSample<X, T> s : returned) {
			if (TypeUtils.isInteger(s.value.getClass()) || TypeUtils.isFloat(s.value.getClass()) || TypeUtils.isDouble(s.value.getClass())
					|| TypeUtils.isLong(s.value.getClass())) {
				Number n = (Number) s.value;
				if (n.doubleValue() == 0) {
					System.out.println("Je ne prends pas " + s.x);
				}
			}
		}
		return returned;
	}*/
}
