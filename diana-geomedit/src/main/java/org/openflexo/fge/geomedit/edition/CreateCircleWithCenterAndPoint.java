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

import org.openflexo.diana.geomedit.edition.Edition;
import org.openflexo.diana.geomedit.edition.ObtainPoint;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEEllips;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geomedit.Circle;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.construction.CircleWithCenterAndPointConstruction;
import org.openflexo.fge.swing.graphics.JFGEDrawingGraphics;

public class CreateCircleWithCenterAndPoint extends Edition {

	public CreateCircleWithCenterAndPoint(GeomEditController controller) {
		super("Create circle from center and point", controller);
		inputs.add(new ObtainPoint("Select center", controller));
		inputs.add(new ObtainPoint("Select a point", controller));
	}

	@Override
	public void performEdition() {
		ObtainPoint p1 = (ObtainPoint) inputs.get(0);
		ObtainPoint p2 = (ObtainPoint) inputs.get(1);

		addObject(new Circle(getController().getDrawing().getModel(), new CircleWithCenterAndPointConstruction(p1.getConstruction(),
				p2.getConstruction())));

	}

	/*public void addObject(GeometricObject object)
	{
		getController().getDrawing().getModel().addToChilds(object);
	}*/

	@Override
	public void paintEdition(JFGEDrawingGraphics graphics, FGEPoint lastMouseLocation) {
		if (currentStep == 0) {
			// Nothing to draw
		} else if (currentStep == 1) {
			// Nothing to draw

			FGEPoint center = ((ObtainPoint) inputs.get(0)).getInputData();
			FGEPoint p = lastMouseLocation;

			double diameter = FGESegment.getLength(center, p) * 2;

			graphics.setDefaultForeground(focusedForegroundStyle);
			center.paint(graphics);
			new FGEEllips(center, new FGEDimension(diameter, diameter), Filling.NOT_FILLED).paint(graphics);
		}
	}
}
