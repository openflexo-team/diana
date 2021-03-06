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

package org.openflexo.diana.impl;

import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.DataBinding;
import org.openflexo.diana.DianaLayoutManager;
import org.openflexo.diana.DianaLayoutManagerSpecification;
import org.openflexo.diana.Drawing.ContainerNode;

/**
 * Represents the specification of a LayoutManager in DIANA<br>
 * 
 * @author sylvain
 * 
 */
public abstract class DianaLayoutManagerSpecificationImpl<LM extends DianaLayoutManager<?, ?>> extends DianaObjectImpl
		implements DianaLayoutManagerSpecification<LM> {

	@Override
	public DraggingMode getDraggingMode() {
		DraggingMode returned = (DraggingMode) performSuperGetter(DRAGGING_MODE_KEY);
		if (returned == null) {
			return getDefaultDraggingMode();
		}
		return returned;
	}

	public abstract DraggingMode getDefaultDraggingMode();

	@Override
	public Boolean paintDecoration() {
		Boolean returned = (Boolean) performSuperGetter(PAINT_DECORATION_KEY);
		if (returned == null) {
			return supportDecoration();
		}
		return returned;
	}

	@Override
	public BindingModel getBindingModel() {
		return null;
	}

	@Override
	public BindingFactory getBindingFactory() {
		return null;
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
	}

	@Override
	public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
	}

	@Override
	public LM makeLayoutManager(ContainerNode<?, ?> containerNode) {
		LM layoutManager = getFactory().newInstance(getLayoutManagerClass());
		((DianaLayoutManager) layoutManager).setLayoutManagerSpecification(this);
		layoutManager.setContainerNode((ContainerNode) containerNode);
		getPropertyChangeSupport().addPropertyChangeListener(layoutManager);
		System.out.println("Created LayoutManager " + getIdentifier() + " : " + layoutManager);
		return layoutManager;
	}

	@Override
	public boolean delete(Object... context) {
		System.out.println("---------> Hop on delete " + this);
		return super.delete(context);
	}
}
