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

import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import org.openflexo.diana.geomedit.controller.ComputedControlPoint;
import org.openflexo.diana.geomedit.controller.DraggableControlPoint;
import org.openflexo.diana.geomedit.model.gr.GeometricObjectGraphicalRepresentation;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEComplexCurve;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.ComplexCurve;
import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.ComplexCurveConstruction;
import org.openflexo.fge.geomedit.construction.ComplexCurveWithNPointsConstruction;
import org.openflexo.fge.geomedit.construction.ExplicitPointConstruction;
import org.openflexo.fge.geomedit.construction.PointConstruction;
import org.openflexo.xmlcode.XMLSerializable;

public class ComplexCurveGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGEComplexCurve, ComplexCurve> implements
		XMLSerializable {
	// Called for LOAD
	public ComplexCurveGraphicalRepresentation(GeomEditBuilder builder) {
		this(null, builder.drawing);
		initializeDeserialization();
	}

	public ComplexCurveGraphicalRepresentation(ComplexCurve curve, GeometricDrawing aDrawing) {
		super(curve, aDrawing);
	}

	@Override
	protected List<ControlPoint> buildControlPointsForGeneralShape(FGEGeneralShape shape) {
		Vector<ControlPoint> returned = new Vector<ControlPoint>();

		ComplexCurveConstruction curveContruction = getDrawable().getConstruction();

		if (curveContruction instanceof ComplexCurveWithNPointsConstruction) {

			for (int i = 0; i < ((ComplexCurveWithNPointsConstruction) curveContruction).pointConstructions.size(); i++) {

				final int pointIndex = i;
				PointConstruction pc = ((ComplexCurveWithNPointsConstruction) curveContruction).pointConstructions.get(i);

				if (pc instanceof ExplicitPointConstruction) {
					returned.add(new DraggableControlPoint<FGEComplexCurve>(this, "pt" + i, pc.getPoint(), (ExplicitPointConstruction) pc) {
						@Override
						public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
								FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
							if (pointIndex == 0) {
								getGeometricObject().getElementAt(0).getP1().x = newAbsolutePoint.x;
								getGeometricObject().getElementAt(0).getP1().y = newAbsolutePoint.y;
								getGeometricObject().refresh();
							} else {
								getGeometricObject().getElementAt(pointIndex - 1).getP2().x = newAbsolutePoint.x;
								getGeometricObject().getElementAt(pointIndex - 1).getP2().y = newAbsolutePoint.y;
								getGeometricObject().refresh();
							}
							setPoint(newAbsolutePoint);
							notifyGeometryChanged();
							return true;
						}

						@Override
						public void update(FGEComplexCurve geometricObject) {
							if (pointIndex == 0) {
								setPoint(geometricObject.getElementAt(0).getP1());
							} else {
								setPoint(geometricObject.getElementAt(pointIndex - 1).getP2());
							}
						}
					});
				} else {
					returned.add(new ComputedControlPoint<FGEComplexCurve>(this, "pt" + i, pc.getPoint()) {
						@Override
						public void update(FGEComplexCurve geometricObject) {
							if (pointIndex == 0) {
								setPoint(geometricObject.getElementAt(0).getP1());
							} else {
								setPoint(geometricObject.getElementAt(pointIndex - 1).getP2());
							}
						}
					});
				}

			}
		}

		return returned;

	}

}
