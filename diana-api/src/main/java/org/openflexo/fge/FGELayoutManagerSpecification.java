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

package org.openflexo.fge;

import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DefaultBindable;

/**
 * Represents the specification of a LayoutManager in DIANA<br>
 * 
 * @author sylvain
 * 
 */
public class FGELayoutManagerSpecification<LM extends FGELayoutManager<?>> extends DefaultBindable {

	private final String identifier;
	private final Class<? extends LM> layoutManagerClass;
	private final FGEModelFactory factory;

	public FGELayoutManagerSpecification(String identifier, Class<? extends LM> layoutManagerClass, FGEModelFactory factory) {
		this.identifier = identifier;
		this.layoutManagerClass = layoutManagerClass;
		this.factory = factory;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Class<? extends LM> getLayoutManagerClass() {
		return layoutManagerClass;
	}

	public FGEModelFactory getFactory() {
		return factory;
	}

	@Override
	public BindingModel getBindingModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BindingFactory getBindingFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
		// TODO Auto-generated method stub

	}

	public LM makeLayoutManager() {
		LM layoutManager = factory.newInstance(layoutManagerClass);
		System.out.println("Created LayoutManager " + identifier + " : " + layoutManager);
		return layoutManager;
	}
}
