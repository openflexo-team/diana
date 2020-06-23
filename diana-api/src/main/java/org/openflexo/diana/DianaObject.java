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

package org.openflexo.diana;

import org.openflexo.diana.notifications.DianaAttributeNotification;
import org.openflexo.pamela.factory.AccessibleProxyObject;
import org.openflexo.pamela.factory.CloneableProxyObject;
import org.openflexo.pamela.factory.DeletableProxyObject;

/**
 * Root interface for all model objects involved in Diana modelling layer
 * 
 * @author sylvain
 * 
 */
public interface DianaObject extends AccessibleProxyObject, DeletableProxyObject, CloneableProxyObject, Cloneable, DianaConstants {

	// TODO: remove this, don't reference the factory in the object
	@Deprecated
	public DianaModelFactory getFactory();

	// TODO: remove this, don't reference the factory in the object
	@Deprecated
	public void setFactory(DianaModelFactory factory);

	public <T> void notifyChange(GRProperty<T> parameter, T oldValue, T newValue);

	public <T> void notifyChange(GRProperty<T> parameter);

	public <T> void notifyAttributeChange(GRProperty<T> parameter);

	public void notify(DianaAttributeNotification<?> notification);

	// *******************************************************************************
	// * Deletion management
	// *******************************************************************************

	/**
	 * Delete this object
	 */
	@Override
	public boolean delete(Object... context);

	/**
	 * Return a flag indicating if this object has been deleted
	 * 
	 * @return
	 */
	@Override
	public boolean isDeleted();

	/**
	 * Clone this Diana object using persistant properties defined as PAMELA model
	 * 
	 * @return
	 */
	public Object clone();
}
