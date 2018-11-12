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

package org.openflexo.diana.geomedit.model.gr;

import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import org.openflexo.diana.geomedit.controller.ComputedControlPoint;
import org.openflexo.diana.geomedit.controller.DraggableControlPoint;
import org.openflexo.diana.geomedit.model.ComplexCurveConstruction;
import org.openflexo.diana.geomedit.model.ComplexCurveWithNPointsConstruction;
import org.openflexo.diana.geomedit.model.ExplicitPointConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.PointConstruction;
import org.openflexo.diana.geomedit.model.gr.ComplexCurveGraphicalRepresentation.ComplexCurveGraphicalRepresentationImpl;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.GeometricGraphicalRepresentation;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaComplexCurve;
import org.openflexo.diana.geom.DianaPoint;

@ModelEntity
@ImplementationClass(ComplexCurveGraphicalRepresentationImpl.class)
@XMLElement
public interface ComplexCurveGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<DianaComplexCurve> {

	public static abstract class ComplexCurveGraphicalRepresentationImpl extends GeometricObjectGraphicalRepresentationImpl<DianaComplexCurve>
			implements ComplexCurveGraphicalRepresentation {

		@Override
		public List<? extends ControlArea<?>> makeControlAreasFor(
				DrawingTreeNode<GeometricConstruction<DianaComplexCurve>, GeometricGraphicalRepresentation> dtn) {
			Vector<ControlPoint> returned = new Vector<ControlPoint>();

			ComplexCurveConstruction curveContruction = (ComplexCurveConstruction) dtn.getDrawable();

			if (curveContruction instanceof ComplexCurveWithNPointsConstruction) {

				for (int i = 0; i < ((ComplexCurveWithNPointsConstruction) curveContruction).getPointConstructions().size(); i++) {

					final int pointIndex = i;
					PointConstruction pc = ((ComplexCurveWithNPointsConstruction) curveContruction).getPointConstructions().get(i);

					if (pc instanceof ExplicitPointConstruction) {
						returned.add(new DraggableControlPoint<DianaComplexCurve>((GeometricNode<?>) dtn, "pt" + i, pc.getPoint(),
								(ExplicitPointConstruction) pc) {
							@Override
							public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
									DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
								if (pointIndex == 0) {
									getGeometricObject().getElementAt(0).getP1().x = newAbsolutePoint.x;
									getGeometricObject().getElementAt(0).getP1().y = newAbsolutePoint.y;
									getGeometricObject().refresh();
								}
								else {
									getGeometricObject().getElementAt(pointIndex - 1).getP2().x = newAbsolutePoint.x;
									getGeometricObject().getElementAt(pointIndex - 1).getP2().y = newAbsolutePoint.y;
									getGeometricObject().refresh();
								}
								setPoint(newAbsolutePoint);
								((GeometricNode<?>) dtn).notifyGeometryChanged();
								return true;
							}

							@Override
							public void update(DianaComplexCurve geometricObject) {
								if (pointIndex == 0) {
									setPoint(geometricObject.getElementAt(0).getP1());
								}
								else {
									setPoint(geometricObject.getElementAt(pointIndex - 1).getP2());
								}
							}
						});
					}
					else {
						returned.add(new ComputedControlPoint<DianaComplexCurve>((GeometricNode<?>) dtn, "pt" + i, pc.getPoint()) {
							@Override
							public void update(DianaComplexCurve geometricObject) {
								if (pointIndex == 0) {
									setPoint(geometricObject.getElementAt(0).getP1());
								}
								else {
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

}
