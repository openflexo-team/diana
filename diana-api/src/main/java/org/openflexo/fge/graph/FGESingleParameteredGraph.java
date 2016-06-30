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

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;

/**
 * A graph determined by a unique parameter on which we iterate<br>
 * This parameter might be continuous or take values on a discrete set of values
 * 
 * @param <P>
 *            type of parameter which plays iterator role
 */
public abstract class FGESingleParameteredGraph<P> extends FGEGraph {

	public FGESingleParameteredGraph() {
		super();
	}

	public final String getParameter() {
		if (parameterTypes.size() > 0) {
			return parameterTypes.keySet().iterator().next();
		}
		return null;
	}

	@Override
	public final void setParameter(String parameterName, Class<?> parameterType) {

		if (parameterTypes.size() > 0) {
			throw new IllegalArgumentException("FGESingleParameteredGraph could not have more than one parameter: existing parameter "
					+ getParameter() + " while adding " + parameterName);
		}
		super.setParameter(parameterName, parameterType);
	}

	public <Y> Y evaluateFunction(FGEFunction<Y> function, P value)
			throws TypeMismatchException, NullReferenceException, InvocationTargetException {
		getEvaluator().set(getParameter(), value);
		return function.evaluate();
	}

	protected abstract Iterator<P> iterateParameter();
}
