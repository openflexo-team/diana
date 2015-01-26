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

package org.openflexo.fge.geomedit.gr;

import java.awt.Color;
import java.awt.Graphics;

import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.geom.FGECubicCurve;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geomedit.CubicCurve;
import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.swing.graphics.JFGEGeometricGraphics;
import org.openflexo.xmlcode.XMLSerializable;

public class CubicCurveGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGECubicCurve, CubicCurve> implements
		XMLSerializable {
	// Called for LOAD
	public CubicCurveGraphicalRepresentation(GeomEditBuilder builder) {
		this(null, builder.drawing);
		initializeDeserialization();
	}

	public CubicCurveGraphicalRepresentation(CubicCurve curve, GeometricDrawing aDrawing) {
		super(curve, aDrawing);
	}

	@Override
	public void paint(Graphics g, AbstractDianaEditor controller) {
		// TODO: un petit @brutal pour avancer, il faudrait faire les choses plus proprement
		rebuildControlPoints();
		super.paint(g, controller);
	}

	@Override
	public void paintGeometricObject(JFGEGeometricGraphics graphics) {
		getGeometricObject().paint(graphics);

		if (getIsSelected() || getIsFocused()) {
			// Draw construction
			FGEPoint p1 = getGeometricObject().getP1();
			FGEPoint p2 = getGeometricObject().getP2();
			FGEPoint cp1 = getGeometricObject().getCtrlP1();
			FGEPoint cp2 = getGeometricObject().getCtrlP2();

			graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.LIGHT_GRAY, 0.5f, DashStyle.PLAIN_STROKE));

			FGESegment line1 = new FGESegment(p1, cp1);
			FGESegment line2 = new FGESegment(p2, cp2);
			line1.paint(graphics);
			line2.paint(graphics);

		}
	}

}
