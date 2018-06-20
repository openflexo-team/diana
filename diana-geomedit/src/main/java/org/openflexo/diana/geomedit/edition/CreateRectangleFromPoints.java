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

package org.openflexo.diana.geomedit.edition;

import org.openflexo.diana.geomedit.model.RectangleWithTwoPointsConstruction;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.Rectangle;
import org.openflexo.fge.swing.graphics.JFGEDrawingGraphics;

public class CreateRectangleFromPoints extends Edition {

	public CreateRectangleFromPoints(GeomEditController controller) {
		super("Create rectangle from points", controller);
		inputs.add(new ObtainPoint("Select first point", controller));
		inputs.add(new ObtainPoint("Select second point", controller));
	}

	@Override
	public void performEdition() {
		ObtainPoint p1 = (ObtainPoint) inputs.get(0);
		ObtainPoint p2 = (ObtainPoint) inputs.get(1);

		addObject(new Rectangle(getController().getDrawing().getModel(), new RectangleWithTwoPointsConstruction(p1.getConstruction(),
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

			FGEPoint p1 = ((ObtainPoint) inputs.get(0)).getInputData();
			FGEPoint p2 = lastMouseLocation;

			FGEPoint p = new FGEPoint();
			p.x = Math.min(p1.x, p2.x);
			p.y = Math.min(p1.y, p2.y);

			double width = Math.abs(p1.x - p2.x);
			double height = Math.abs(p1.y - p2.y);

			graphics.setDefaultForeground(focusedForegroundStyle);
			p1.paint(graphics);
			new FGERectangle(p.x, p.y, width, height).paint(graphics);
		}
	}
}
