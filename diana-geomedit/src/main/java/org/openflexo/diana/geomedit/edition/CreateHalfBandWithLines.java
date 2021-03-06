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

import java.util.logging.Logger;

import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaBand;
import org.openflexo.diana.geom.area.DianaHalfBand;
import org.openflexo.diana.geom.area.DianaHalfPlane;
import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.diana.swing.graphics.JDianaDrawingGraphics;

public class CreateHalfBandWithLines extends Edition {

	private static final Logger logger = Logger.getLogger(CreateHalfBandWithLines.class.getPackage().getName());

	public CreateHalfBandWithLines(GeomEditDrawingController controller) {
		super("Create half-band with lines and point", controller);
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

		addConstruction(getController().getFactory().makeHalfBandWithLinesConstruction(l1.getConstruction(), l2.getConstruction(),
				l3.getConstruction(), p.getConstruction()));
	}

	private DianaArea whatToPaint = null;
	private boolean requireRepaint = true;

	@Override
	public void paintEdition(JDianaDrawingGraphics graphics, DianaPoint lastMouseLocation) {
		if (currentStep == 0) {
			// Nothing to draw
			return;
		}
		else if (currentStep == 1) {
			DianaLine line1 = ((ObtainLine) inputs.get(0)).getInputData();
			whatToPaint = new DianaHalfPlane(line1, lastMouseLocation);
		}
		else if (currentStep == 2) {
			DianaLine line1 = ((ObtainLine) inputs.get(0)).getInputData();
			DianaLine line2 = ((ObtainLine) inputs.get(1)).getInputData();
			whatToPaint = new DianaBand(line1, line2);
		}
		else if (currentStep == 3) {
			DianaLine line1 = ((ObtainLine) inputs.get(0)).getInputData();
			DianaLine line2 = ((ObtainLine) inputs.get(1)).getInputData();
			DianaLine limitLine = ((ObtainLine) inputs.get(2)).getInputData();
			whatToPaint = new DianaHalfBand(line1, line2, new DianaHalfPlane(limitLine, lastMouseLocation.clone()));
		}
		graphics.setDefaultForeground(focusedForegroundStyle);
		graphics.setDefaultBackground(focusedBackgroundStyle);
		whatToPaint.paint(graphics);
	}

	@Override
	public boolean requireRepaint(DianaPoint lastMouseLocation) {
		DianaArea thingToPaint = null;
		if (currentStep == 0) {
			// Nothing to draw
			return false;
		}
		else if (currentStep == 1) {
			DianaLine line1 = ((ObtainLine) inputs.get(0)).getInputData();
			thingToPaint = new DianaHalfPlane(line1, lastMouseLocation);
		}
		else if (currentStep == 2) {
			DianaLine line1 = ((ObtainLine) inputs.get(0)).getInputData();
			DianaLine line2 = ((ObtainLine) inputs.get(1)).getInputData();
			thingToPaint = new DianaBand(line1, line2);
		}
		else if (currentStep == 3) {
			DianaLine line1 = ((ObtainLine) inputs.get(0)).getInputData();
			DianaLine line2 = ((ObtainLine) inputs.get(1)).getInputData();
			DianaLine limitLine = ((ObtainLine) inputs.get(2)).getInputData();
			thingToPaint = new DianaHalfBand(line1, line2, new DianaHalfPlane(limitLine, lastMouseLocation.clone()));
		}
		if (whatToPaint == null || !whatToPaint.equals(thingToPaint)) {
			whatToPaint = thingToPaint;
			requireRepaint = true;
		}
		else {
			requireRepaint = false;
		}

		// System.out.println("Require repaint = "+requireRepaint+" currentStep="+currentStep+" thingToPaint="+thingToPaint);

		return requireRepaint;
	}
}
