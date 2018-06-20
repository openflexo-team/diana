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

import org.openflexo.diana.geomedit.model.CircleConstruction;
import org.openflexo.diana.geomedit.model.PointConstruction;
import org.openflexo.diana.geomedit.model.TangentLineWithCircleAndPointConstruction;
import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.geom.FGECircle;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.Line;
import org.openflexo.fge.swing.graphics.JFGEDrawingGraphics;

public class CreateTangentLineWithCircleAndPoint extends Edition {

	public CreateTangentLineWithCircleAndPoint(GeomEditController controller) {
		super("Create tangent line to a circle and crossing point", controller);
		inputs.add(new ObtainCircle("Select circle", controller));
		inputs.add(new ObtainPoint("Select point", controller));
		inputs.add(new ObtainPoint("Select a point identifying side", controller));
	}

	@Override
	public void performEdition() {
		if (((ObtainCircle) inputs.get(0)).getReferencedCircle() != null) {
			((ObtainCircle) inputs.get(0)).getReferencedCircle().getGraphicalRepresentation().setIsSelected(false);
		}

		CircleConstruction circle = ((ObtainCircle) inputs.get(0)).getConstruction();
		PointConstruction point = ((ObtainPoint) inputs.get(1)).getConstruction();
		PointConstruction choosingPoint = ((ObtainPoint) inputs.get(2)).getConstruction();

		addObject(new Line(getController().getDrawing().getModel(), new TangentLineWithCircleAndPointConstruction(circle, point,
				choosingPoint)));

	}

	@Override
	public void paintEdition(JFGEDrawingGraphics graphics, FGEPoint lastMouseLocation) {
		if (currentStep == 0) {
			// Nothing to draw
		} else if (currentStep == 1) {
			if (((ObtainCircle) inputs.get(0)).getReferencedCircle() != null) {
				((ObtainCircle) inputs.get(0)).getReferencedCircle().getGraphicalRepresentation().setIsSelected(true);
			}
			FGECircle circle = ((ObtainCircle) inputs.get(0)).getInputData();
			FGEUnionArea tangentPoints = FGECircle.getTangentsPointsToCircle(circle, lastMouseLocation);

			if (tangentPoints.isUnionOfPoints()) {
				graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.LIGHT_GRAY, 1, DashStyle.MEDIUM_DASHES));
				FGELine line1 = new FGELine(lastMouseLocation, (FGEPoint) tangentPoints.getObjects().firstElement());
				FGELine line2 = new FGELine(lastMouseLocation, (FGEPoint) tangentPoints.getObjects().elementAt(1));
				line1.paint(graphics);
				line2.paint(graphics);
				graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.RED, 1));
				tangentPoints.getObjects().firstElement().paint(graphics);
				tangentPoints.getObjects().elementAt(1).paint(graphics);
			}
		} else if (currentStep == 2) {
			FGECircle circle = ((ObtainCircle) inputs.get(0)).getInputData();
			FGEPoint point = ((ObtainPoint) inputs.get(1)).getInputData();
			FGEUnionArea tangentPoints = FGECircle.getTangentsPointsToCircle(circle, point);
			if (tangentPoints.isUnionOfPoints()) {
				graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.LIGHT_GRAY, 1, DashStyle.MEDIUM_DASHES));
				FGELine line1 = new FGELine(point, (FGEPoint) tangentPoints.getObjects().firstElement());
				FGELine line2 = new FGELine(point, (FGEPoint) tangentPoints.getObjects().elementAt(1));
				line1.paint(graphics);
				line2.paint(graphics);
				graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.RED, 1));
				tangentPoints.getObjects().firstElement().paint(graphics);
				tangentPoints.getObjects().elementAt(1).paint(graphics);
				(new FGELine(point, tangentPoints.getNearestPoint(lastMouseLocation))).paint(graphics);
			}
		}
	}
}
