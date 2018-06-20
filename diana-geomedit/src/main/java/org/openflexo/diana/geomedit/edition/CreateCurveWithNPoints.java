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

import java.util.Vector;

import org.openflexo.diana.geomedit.model.ComplexCurveWithNPointsConstruction;
import org.openflexo.diana.geomedit.model.PointConstruction;
import org.openflexo.fge.geom.FGEComplexCurve;
import org.openflexo.fge.geom.FGEGeneralShape.Closure;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geomedit.ComplexCurve;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.swing.graphics.JFGEDrawingGraphics;

public class CreateCurveWithNPoints extends Edition {

	public CreateCurveWithNPoints(GeomEditController controller) {
		super("Create curve from a list of points", controller);
		appendNewPointEdition(controller, 1);
	}

	private void appendNewPointEdition(final GeomEditController controller, int index) {
		ObtainPoint newObtainPoint = new ObtainPoint("Select point " + index, controller, true) {
			@Override
			public void done() {
				appendNewPointEdition(controller, currentStep + 2);
				super.done();
			}

			@Override
			public void endEdition() {
				System.out.println("End edition called");
				super.done();
			}
		};
		inputs.add(newObtainPoint);
	}

	@Override
	public void performEdition() {
		Vector<PointConstruction> pc = new Vector<PointConstruction>();
		for (EditionInput o : inputs) {
			PointConstruction pp = ((ObtainPoint) o).getConstruction();
			if (pp != null) {
				pc.add(pp);
			}
		}
		addObject(new ComplexCurve(getController().getDrawing().getModel(), new ComplexCurveWithNPointsConstruction(pc)));
	}

	/*public void addObject(GeometricObject object)
	{
		getController().getDrawing().getModel().addToChilds(object);
	}*/

	@Override
	public void paintEdition(JFGEDrawingGraphics graphics, FGEPoint lastMouseLocation) {
		if (currentStep == 0) {
			// Nothing to draw
		}
		if (currentStep == 1) {
			FGEPoint p1 = ((ObtainPoint) inputs.get(0)).getInputData();
			graphics.setDefaultForeground(focusedForegroundStyle);
			p1.paint(graphics);
			new FGESegment(p1, lastMouseLocation).paint(graphics);
		} else {
			Vector<FGEPoint> pts = new Vector<FGEPoint>();
			graphics.setDefaultForeground(focusedForegroundStyle);
			for (int i = 0; i < currentStep; i++) {
				FGEPoint p = ((ObtainPoint) inputs.get(i)).getInputData();
				p.paint(graphics);
				pts.add(p);
			}
			pts.add(lastMouseLocation);
			new FGEComplexCurve(Closure.OPEN_NOT_FILLED, pts).paint(graphics);

		}
	}
}
