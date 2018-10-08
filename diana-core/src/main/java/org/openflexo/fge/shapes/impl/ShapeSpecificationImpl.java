/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.fge.shapes.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.impl.FGEObjectImpl;
import org.openflexo.fge.shapes.ShapeSpecification;

/**
 * Default implementation of {@link ShapeSpecification}
 * 
 * @author sylvain
 * 
 */
public abstract class ShapeSpecificationImpl extends FGEObjectImpl implements ShapeSpecification {

	private static final Logger logger = Logger.getLogger(ShapeSpecificationImpl.class.getPackage().getName());

	// private transient ShapeGraphicalRepresentation graphicalRepresentation;

	// private transient Vector<ControlPoint> controlPoints = null;

	// private static final FGEModelFactory SHADOW_FACTORY = FGECoreUtils.TOOLS_FACTORY;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public ShapeSpecificationImpl() {
		super();
	}

	/**
	 * Must be overriden when shape requires it
	 * 
	 * @return
	 */
	@Override
	public boolean areDimensionConstrained() {
		return false;
	}

	@Override
	public abstract ShapeType getShapeType();

	private final List<ShapeImpl<?>> createdShapes = new ArrayList<>();

	@Override
	public final ShapeImpl<?> makeShape(ShapeNode<?> node) {
		ShapeImpl<?> returned = new ShapeImpl<>(node);
		if (getPropertyChangeSupport() != null) {
			// TODO
			createdShapes.add(returned);
			getPropertyChangeSupport().addPropertyChangeListener(returned);
		}
		return returned;
	}

	@Override
	public boolean delete(Object... context) {
		for (ShapeImpl<?> s : createdShapes) {
			if (getPropertyChangeSupport() != null) {
				getPropertyChangeSupport().removePropertyChangeListener(s);
			}
		}
		createdShapes.clear();
		return super.delete(context);
	}

}
