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

import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRoundRectangle;
import org.openflexo.diana.swing.graphics.JDianaDrawingGraphics;

public class CreateRoundRectangleFromPoints extends Edition {

	public CreateRoundRectangleFromPoints(GeomEditDrawingController controller) {
		super("Create round rectangle from points", controller);
		inputs.add(new ObtainDouble("Select arc width", 15, controller));
		inputs.add(new ObtainDouble("Select arc height", 15, controller));
		inputs.add(new ObtainPoint("Select first point", controller));
		inputs.add(new ObtainPoint("Select second point", controller));
	}

	@Override
	public void performEdition() {
		ObtainPoint p1 = (ObtainPoint) inputs.get(2);
		ObtainPoint p2 = (ObtainPoint) inputs.get(3);
		double arcWidth = ((ObtainDouble) inputs.get(0)).getInputData();
		double arcHeight = ((ObtainDouble) inputs.get(1)).getInputData();

		addConstruction(getController().getFactory().makeRoundRectangleWithTwoPointsConstruction(p1.getConstruction(), p2.getConstruction(),
				arcWidth, arcHeight));
	}

	@Override
	public void paintEdition(JDianaDrawingGraphics graphics, DianaPoint lastMouseLocation) {
		if (currentStep < 3) {
			// Nothing to draw
		}
		else if (currentStep == 3) {
			double arcWidth = ((ObtainDouble) inputs.get(0)).getInputData();
			double arcHeight = ((ObtainDouble) inputs.get(1)).getInputData();
			DianaPoint p1 = ((ObtainPoint) inputs.get(2)).getInputData();
			DianaPoint p2 = lastMouseLocation;

			DianaPoint p = new DianaPoint();
			p.x = Math.min(p1.x, p2.x);
			p.y = Math.min(p1.y, p2.y);

			double width = Math.abs(p1.x - p2.x);
			double height = Math.abs(p1.y - p2.y);

			graphics.setDefaultForeground(focusedForegroundStyle);
			p1.paint(graphics);
			new DianaRoundRectangle(p.x, p.y, width, height, arcWidth, arcHeight).paint(graphics);
		}
	}
}
