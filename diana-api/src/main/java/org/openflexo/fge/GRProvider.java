/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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
package org.openflexo.fge;

import java.util.List;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.cp.ControlArea;

public abstract class GRProvider<O, GR extends GraphicalRepresentation> {
	public abstract GR provideGR(O drawable, FGEModelFactory factory);

	public List<ControlArea<?>> makeControlAreasFor(DrawingTreeNode<O, GR> dtn) {
		return null;
	}

	public static abstract class ContainerGRProvider<O, GR extends ContainerGraphicalRepresentation> extends GRProvider<O, GR> {

	}

	public static abstract class DrawingGRProvider<O> extends ContainerGRProvider<O, DrawingGraphicalRepresentation> {

	}

	public static abstract class ShapeGRProvider<O> extends ContainerGRProvider<O, ShapeGraphicalRepresentation> {

	}

	public static abstract class ConnectorGRProvider<O> extends GRProvider<O, ConnectorGraphicalRepresentation> {

	}

	public static abstract class GeometricGRProvider<O> extends GRProvider<O, GeometricGraphicalRepresentation> {

	}

}
