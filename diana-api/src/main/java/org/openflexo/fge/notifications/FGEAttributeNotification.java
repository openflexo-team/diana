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

package org.openflexo.fge.notifications;

import org.openflexo.fge.GRProperty;

/**
 * Notification thrown when an attribute changed its value from a value to another value
 * 
 * @author sylvain
 * 
 * @param <T>
 */
public class FGEAttributeNotification<T> extends FGENotification {

	public GRProperty<T> parameter;

	public FGEAttributeNotification(GRProperty<T> parameter, T oldValue, T newValue) {
		super(parameter != null ? parameter.getName() : null, oldValue, newValue);
		this.parameter = parameter;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Override
	public String toString() {
		return "FGEAttributeNotification of " + getClass().getSimpleName() + " " + getParameter() + " old: " + oldValue + " new: "
				+ newValue;
	}

	public GRProperty<T> getParameter() {
		return parameter;
	}

	public String propertyName() {
		if (parameter != null) {
			return parameter.getName();
		}
		return null;
	}

	public T newValue() {
		return (T) newValue;
	}

	@Override
	public T oldValue() {
		return (T) super.oldValue();
	}
}
