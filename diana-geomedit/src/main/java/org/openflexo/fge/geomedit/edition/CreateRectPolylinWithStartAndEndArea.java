/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geomedit, a component of the software infrastructure 
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

package org.openflexo.fge.geomedit.edition;

import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geomedit.edition.Edition;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.Polylin;
import org.openflexo.fge.geomedit.construction.RectPolylinWithStartAndEndAreaConstruction;
import org.openflexo.fge.swing.graphics.JFGEDrawingGraphics;

public class CreateRectPolylinWithStartAndEndArea extends Edition {

	public CreateRectPolylinWithStartAndEndArea(GeomEditController controller) {
		super("Create line from points", controller);
		inputs.add(new ObtainObject("Select starting area", controller));
		inputs.add(new ObtainSimplifiedCardinalDirection("Select start orientation", SimplifiedCardinalDirection.NORTH, controller));
		inputs.add(new ObtainObject("Select end area", controller));
		inputs.add(new ObtainSimplifiedCardinalDirection("Select end orientation", SimplifiedCardinalDirection.NORTH, controller));
	}

	@Override
	public void performEdition() {
		ObtainObject o1 = (ObtainObject) inputs.get(0);
		ObtainSimplifiedCardinalDirection startOrientation = (ObtainSimplifiedCardinalDirection) inputs.get(1);
		ObtainObject o2 = (ObtainObject) inputs.get(2);
		ObtainSimplifiedCardinalDirection endOrientation = (ObtainSimplifiedCardinalDirection) inputs.get(3);

		addObject(new Polylin(getController().getDrawing().getModel(), new RectPolylinWithStartAndEndAreaConstruction(o1.getConstruction(),
				startOrientation.getInputData(), o2.getConstruction(), endOrientation.getInputData())));

	}

	/*public void addObject(GeometricObject object)
	{
		getController().getDrawing().getModel().addToChilds(object);
	}*/

	@Override
	public void paintEdition(JFGEDrawingGraphics graphics, FGEPoint lastMouseLocation) {
		// Nothing to draw
	}
}
