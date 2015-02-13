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

import org.openflexo.connie.Bindable;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.layout.GridLayoutManagerSpecification;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents the specification of a LayoutManager in DIANA<br>
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@Imports({ @Import(GridLayoutManagerSpecification.class) })
public interface FGELayoutManagerSpecification<LM extends FGELayoutManager<?, ?>> extends FGEObject, Bindable {

	@PropertyIdentifier(type = String.class)
	public static final String IDENTIFIER_KEY = "identifier";
	@PropertyIdentifier(type = FGEModelFactory.class)
	public static final String FACTORY = "factory";

	@Getter(IDENTIFIER_KEY)
	@XMLElement
	public String getIdentifier();

	@Setter(IDENTIFIER_KEY)
	public void setIdentifier(String identifier);

	public Class<? extends LM> getLayoutManagerClass();

	@Override
	@Getter(value = FACTORY, ignoreType = true)
	public FGEModelFactory getFactory();

	@Override
	@Setter(FACTORY)
	public void setFactory(FGEModelFactory aFactory);

	/**
	 * Build and return a new {@link FGELayoutManager} conform to this {@link FGELayoutManagerSpecification}
	 * 
	 * @param containerNode
	 * @return
	 */
	public LM makeLayoutManager(ContainerNode<?, ?> containerNode);

}
