/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
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

	private final List<ShapeImpl<?>> createdShapes = new ArrayList<ShapeImpl<?>>();

	@Override
	public ShapeImpl<?> makeShape(ShapeNode<?> node) {
		ShapeImpl returned = new ShapeImpl(node);
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
