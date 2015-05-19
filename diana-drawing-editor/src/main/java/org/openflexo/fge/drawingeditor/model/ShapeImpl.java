/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-drawing-editor, a component of the software infrastructure 
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

package org.openflexo.fge.drawingeditor.model;

import org.openflexo.fge.ShapeGraphicalRepresentation;

public abstract class ShapeImpl extends DiagramElementImpl<Shape, ShapeGraphicalRepresentation> implements Shape {
	// public String name;

	private static int INDEX = 0;
	private int index = 0;

	// Called for LOAD
	/*public ShapeImpl(DrawingBuilder builder) {
		super(builder.drawing);
		// initializeDeserialization();
	}*/

	// Used by PAMELA, do not use it
	public ShapeImpl() {
		super(null);
		index = INDEX++;
	}

	// Called for NEW
	/*public ShapeImpl(ShapeType shape, FGEPoint p, DiagramDrawing drawing) {
		super(drawing.getModel());
		MyShapeGraphicalRepresentation gr = drawing.getModel().getFactory().makeNewShapeGR(shape, this, drawing);
		if (gr.getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
			gr.setWidth(80);
			gr.setHeight(80);
		} else {
			gr.setWidth(100);
			gr.setHeight(80);
		}
		gr.setX(p.x);
		gr.setY(p.y);
		setGraphicalRepresentation(gr);
	}

	public ShapeImpl(ShapeGraphicalRepresentation aGR, FGEPoint p, DiagramDrawing drawing) {
		super(drawing.getModel());
		MyShapeGraphicalRepresentation gr = drawing.getModel().getFactory().makeNewShapeGR(aGR, this, drawing);
		gr.setX(p.x);
		gr.setY(p.y);
		setGraphicalRepresentation(gr);
	}*/

	/*@Override
	public String getName() {
		if (name == null) {
			return "unnamed" + index;
		}
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}*/

	@Override
	public String toString() {
		return "Shape[" + getName() + "]";
	}

}
