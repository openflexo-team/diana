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

import java.awt.Color;

import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.diana.geomedit.model.CircleConstruction;
import org.openflexo.diana.geomedit.model.PointConstruction;
import org.openflexo.diana.ForegroundStyle.DashStyle;
import org.openflexo.diana.geom.DianaCircle;
import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.PointInsideCircleException;
import org.openflexo.diana.geom.area.DianaUnionArea;
import org.openflexo.diana.swing.graphics.JDianaDrawingGraphics;

public class CreateTangentLineWithCircleAndPoint extends Edition {

	public CreateTangentLineWithCircleAndPoint(GeomEditDrawingController controller) {
		super("Create tangent line to a circle and crossing point", controller);
		inputs.add(new ObtainCircle("Select circle", controller));
		inputs.add(new ObtainPoint("Select point", controller));
		inputs.add(new ObtainPoint("Select a point identifying side", controller));
	}

	@Override
	public void performEdition() {
		CircleConstruction circle = ((ObtainCircle) inputs.get(0)).getConstruction();
		PointConstruction point = ((ObtainPoint) inputs.get(1)).getConstruction();
		PointConstruction choosingPoint = ((ObtainPoint) inputs.get(2)).getConstruction();

		addConstruction(getController().getFactory().makeTangentLineWithCircleAndPointConstruction(circle, point, choosingPoint));

	}

	@Override
	public void paintEdition(JDianaDrawingGraphics graphics, DianaPoint lastMouseLocation) {
		if (currentStep == 0) {
			// Nothing to draw
		}
		else if (currentStep == 1) {
			DianaCircle circle = ((ObtainCircle) inputs.get(0)).getInputData();
			try {
				DianaUnionArea tangentPoints = DianaCircle.getTangentsPointsToCircle(circle, lastMouseLocation);
				if (tangentPoints.isUnionOfPoints()) {
					graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.LIGHT_GRAY, 1, DashStyle.MEDIUM_DASHES));
					DianaLine line1 = new DianaLine(lastMouseLocation, (DianaPoint) tangentPoints.getObjects().firstElement());
					DianaLine line2 = new DianaLine(lastMouseLocation, (DianaPoint) tangentPoints.getObjects().elementAt(1));
					line1.paint(graphics);
					line2.paint(graphics);
					graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.RED, 1));
					tangentPoints.getObjects().firstElement().paint(graphics);
					tangentPoints.getObjects().elementAt(1).paint(graphics);
				}
			} catch (PointInsideCircleException e) {
				// Cannot paint
			}
		}
		else if (currentStep == 2) {
			DianaCircle circle = ((ObtainCircle) inputs.get(0)).getInputData();
			DianaPoint point = ((ObtainPoint) inputs.get(1)).getInputData();
			DianaUnionArea tangentPoints = DianaCircle.getTangentsPointsToCircle(circle, point);
			if (tangentPoints.isUnionOfPoints()) {
				graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.LIGHT_GRAY, 1, DashStyle.MEDIUM_DASHES));
				DianaLine line1 = new DianaLine(point, (DianaPoint) tangentPoints.getObjects().firstElement());
				DianaLine line2 = new DianaLine(point, (DianaPoint) tangentPoints.getObjects().elementAt(1));
				line1.paint(graphics);
				line2.paint(graphics);
				graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.RED, 1));
				tangentPoints.getObjects().firstElement().paint(graphics);
				tangentPoints.getObjects().elementAt(1).paint(graphics);
				(new DianaLine(point, tangentPoints.getNearestPoint(lastMouseLocation))).paint(graphics);
			}
		}
	}
}
