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
import org.openflexo.model.annotations.XMLAttribute;

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
	@PropertyIdentifier(type = Boolean.class)
	public static final String PAINT_DECORATION_KEY = "paintDecoration";

	/**
	 * Return identifier (a String) for this layout manager specification<br>
	 * This string should be unique regarding the layout managers defined for container
	 * 
	 * @return
	 */
	@Getter(IDENTIFIER_KEY)
	@XMLAttribute
	public String getIdentifier();

	/**
	 * Sets identifier for this layout manager specification<br>
	 * 
	 * @param identifier
	 */
	@Setter(IDENTIFIER_KEY)
	public void setIdentifier(String identifier);

	public Class<? extends LM> getLayoutManagerClass();

	/**
	 * Return {@link FGEModelFactory} to use when creating new layout manager conform to this specification
	 */
	@Override
	@Getter(value = FACTORY, ignoreType = true)
	public FGEModelFactory getFactory();

	/**
	 * Sets {@link FGEModelFactory} to use when creating new layout manager conform to this specification
	 */
	@Override
	@Setter(FACTORY)
	public void setFactory(FGEModelFactory aFactory);

	/**
	 * Return flag indicating whether layout manager decoration is to be paint<br>
	 * Note that this is relevant only if this layout manager supports decoration painting
	 * 
	 * @return
	 */
	@Getter(PAINT_DECORATION_KEY)
	@XMLAttribute
	public Boolean paintDecoration();

	/**
	 * Sets flag indicating whether layout manager decoration is to be paint<br>
	 * Note that this is relevant only if this layout manager supports decoration painting
	 * 
	 * @param paintDecoration
	 */
	@Setter(PAINT_DECORATION_KEY)
	public void setPaintDecoration(Boolean paintDecoration);

	/**
	 * Build and return a new {@link FGELayoutManager} conform to this {@link FGELayoutManagerSpecification}
	 * 
	 * @param containerNode
	 * @return
	 */
	public LM makeLayoutManager(ContainerNode<?, ?> containerNode);

	/**
	 * Return flag indicating whether this layout manager supports autolayout
	 * 
	 * @return
	 */
	public boolean supportAutolayout();

	/**
	 * Return flag indicating whether this layout manager supports decoration painting<br>
	 * 
	 * @return
	 */
	public boolean supportDecoration();

}
