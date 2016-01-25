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
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.Line;
import org.openflexo.fge.geomedit.construction.LineConstruction;
import org.openflexo.fge.geomedit.construction.PointConstruction;
import org.openflexo.fge.geomedit.construction.RotatedLineWithPointConstruction;
import org.openflexo.fge.swing.graphics.JFGEDrawingGraphics;

public class CreateRotatedLineWithPoint extends Edition {

	public CreateRotatedLineWithPoint(GeomEditController controller) {
		super("Create horizontal line crossing point", controller);
		inputs.add(new ObtainLine("Select line", controller));
		inputs.add(new ObtainDouble("Select rotation angle (degree)", 45, controller));
		inputs.add(new ObtainPoint("Select point", controller));
	}

	@Override
	public void performEdition() {
		if (((ObtainLine) inputs.get(0)).getReferencedLine() != null) {
			((ObtainLine) inputs.get(0)).getReferencedLine().getGraphicalRepresentation().setIsSelected(false);
		}

		LineConstruction line = ((ObtainLine) inputs.get(0)).getConstruction();
		double angle = ((ObtainDouble) inputs.get(1)).getInputData();
		PointConstruction point = ((ObtainPoint) inputs.get(2)).getConstruction();

		addObject(new Line(getController().getDrawing().getModel(), new RotatedLineWithPointConstruction(line, point, angle)));

	}

	@Override
	public void paintEdition(JFGEDrawingGraphics graphics, FGEPoint lastMouseLocation) {
		if (currentStep == 0) {
			// Nothing to draw
		} else if (currentStep == 1) {
			if (((ObtainLine) inputs.get(0)).getReferencedLine() != null) {
				((ObtainLine) inputs.get(0)).getReferencedLine().getGraphicalRepresentation().setIsSelected(true);
			}
		} else if (currentStep == 2) {
			graphics.setDefaultForeground(focusedForegroundStyle);
			FGELine line = ((ObtainLine) inputs.get(0)).getConstruction().getData();
			double angle = ((ObtainDouble) inputs.get(1)).getInputData();
			FGELine.getRotatedLine(line, angle, lastMouseLocation).paint(graphics);
		}
	}
}
