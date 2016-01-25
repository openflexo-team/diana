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

import java.util.logging.Logger;

import org.openflexo.diana.geomedit.edition.Edition;
import org.openflexo.diana.geomedit.edition.ObtainPoint;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEBand;
import org.openflexo.fge.geom.area.FGEHalfBand;
import org.openflexo.fge.geom.area.FGEHalfPlane;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.HalfBand;
import org.openflexo.fge.geomedit.construction.HalfBandWithLinesConstruction;
import org.openflexo.fge.swing.graphics.JFGEDrawingGraphics;

public class CreateHalfBandWithLines extends Edition {

	private static final Logger logger = Logger.getLogger(CreateHalfBandWithLines.class.getPackage().getName());

	public CreateHalfBandWithLines(GeomEditController controller) {
		super("Create half-plane with line and point", controller);
		inputs.add(new ObtainLine("Select first line", controller));
		inputs.add(new ObtainLine("Select second line", controller));
		inputs.add(new ObtainLine("Select a line delimiting half-plane", controller));
		inputs.add(new ObtainPoint("Select point inside half-plane", controller));
	}

	@Override
	public void performEdition() {
		ObtainLine l1 = (ObtainLine) inputs.get(0);
		ObtainLine l2 = (ObtainLine) inputs.get(1);
		ObtainLine l3 = (ObtainLine) inputs.get(2);
		ObtainPoint p = (ObtainPoint) inputs.get(3);

		addObject(new HalfBand(getController().getDrawing().getModel(), new HalfBandWithLinesConstruction(l1.getConstruction(),
				l2.getConstruction(), l3.getConstruction(), p.getConstruction())));

	}

	private FGEArea whatToPaint = null;
	private boolean requireRepaint = true;

	@Override
	public void paintEdition(JFGEDrawingGraphics graphics, FGEPoint lastMouseLocation) {
		if (currentStep == 0) {
			// Nothing to draw
			return;
		} else if (currentStep == 1) {
			FGELine line1 = ((ObtainLine) inputs.get(0)).getInputData();
			whatToPaint = new FGEHalfPlane(line1, lastMouseLocation);
		} else if (currentStep == 2) {
			FGELine line1 = ((ObtainLine) inputs.get(0)).getInputData();
			FGELine line2 = ((ObtainLine) inputs.get(1)).getInputData();
			whatToPaint = new FGEBand(line1, line2);
		} else if (currentStep == 3) {
			FGELine line1 = ((ObtainLine) inputs.get(0)).getInputData();
			FGELine line2 = ((ObtainLine) inputs.get(1)).getInputData();
			FGELine limitLine = ((ObtainLine) inputs.get(2)).getInputData();
			whatToPaint = new FGEHalfBand(line1, line2, new FGEHalfPlane(limitLine, lastMouseLocation.clone()));
		}
		graphics.setDefaultForeground(focusedForegroundStyle);
		graphics.setDefaultBackground(focusedBackgroundStyle);
		whatToPaint.paint(graphics);
	}

	@Override
	public boolean requireRepaint(FGEPoint lastMouseLocation) {
		FGEArea thingToPaint = null;
		if (currentStep == 0) {
			// Nothing to draw
			return false;
		} else if (currentStep == 1) {
			FGELine line1 = ((ObtainLine) inputs.get(0)).getInputData();
			thingToPaint = new FGEHalfPlane(line1, lastMouseLocation);
		} else if (currentStep == 2) {
			FGELine line1 = ((ObtainLine) inputs.get(0)).getInputData();
			FGELine line2 = ((ObtainLine) inputs.get(1)).getInputData();
			thingToPaint = new FGEBand(line1, line2);
		} else if (currentStep == 3) {
			FGELine line1 = ((ObtainLine) inputs.get(0)).getInputData();
			FGELine line2 = ((ObtainLine) inputs.get(1)).getInputData();
			FGELine limitLine = ((ObtainLine) inputs.get(2)).getInputData();
			thingToPaint = new FGEHalfBand(line1, line2, new FGEHalfPlane(limitLine, lastMouseLocation.clone()));
		}
		if (whatToPaint == null || !whatToPaint.equals(thingToPaint)) {
			whatToPaint = thingToPaint;
			requireRepaint = true;
		} else {
			requireRepaint = false;
		}

		// System.out.println("Require repaint = "+requireRepaint+" currentStep="+currentStep+" thingToPaint="+thingToPaint);

		return requireRepaint;
	}
}
