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

import org.openflexo.diana.geom.DianaCircle;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.diana.swing.graphics.JDianaDrawingGraphics;

public class CreateCircleWithThreePoints extends Edition {

	public CreateCircleWithThreePoints(GeomEditDrawingController controller) {
		super("Create circle with three points", controller);
		inputs.add(new ObtainPoint("Select point 1", controller));
		inputs.add(new ObtainPoint("Select point 2", controller));
		inputs.add(new ObtainPoint("Select point 3", controller));
	}

	@Override
	public void performEdition() {
		ObtainPoint pt1 = (ObtainPoint) inputs.get(0);
		ObtainPoint pt2 = (ObtainPoint) inputs.get(1);
		ObtainPoint pt3 = (ObtainPoint) inputs.get(2);

		addConstruction(getController().getFactory().makeCircleWithThreePointsConstruction(pt1.getConstruction(), pt2.getConstruction(),
				pt3.getConstruction()));

	}

	@Override
	public void paintEdition(JDianaDrawingGraphics graphics, DianaPoint lastMouseLocation) {
		if (currentStep == 0) {
			// Nothing to draw
		}
		else if (currentStep == 1) {
			DianaPoint p1 = ((ObtainPoint) inputs.get(0)).getInputData();
			graphics.setDefaultForeground(focusedForegroundStyle);
			p1.paint(graphics);
		}
		else if (currentStep == 2) {
			DianaPoint p1 = ((ObtainPoint) inputs.get(0)).getInputData();
			DianaPoint p2 = ((ObtainPoint) inputs.get(1)).getInputData();
			DianaPoint p3 = lastMouseLocation;
			graphics.setDefaultForeground(focusedForegroundStyle);
			p1.paint(graphics);
			p2.paint(graphics);
			DianaCircle.getCircle(p1, p2, p3, Filling.NOT_FILLED).paint(graphics);
		}
	}
}
