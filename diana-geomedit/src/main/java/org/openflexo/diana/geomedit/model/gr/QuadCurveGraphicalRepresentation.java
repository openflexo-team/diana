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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import org.openflexo.diana.geomedit.controller.ComputedControlPoint;
import org.openflexo.diana.geomedit.controller.DraggableControlPoint;
import org.openflexo.diana.geomedit.model.ExplicitPointConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.PointConstruction;
import org.openflexo.diana.geomedit.model.QuadCurveConstruction;
import org.openflexo.diana.geomedit.model.QuadCurveWithThreePointsConstruction;
import org.openflexo.diana.geomedit.model.gr.QuadCurveGraphicalRepresentation.QuadCurveGraphicalRepresentationImpl;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.ForegroundStyle.DashStyle;
import org.openflexo.diana.GeometricGraphicalRepresentation;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaQuadCurve;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.graphics.DianaGraphics;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(QuadCurveGraphicalRepresentationImpl.class)
@XMLElement
public interface QuadCurveGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<DianaQuadCurve> {

	public static abstract class QuadCurveGraphicalRepresentationImpl extends GeometricObjectGraphicalRepresentationImpl<DianaQuadCurve>
			implements QuadCurveGraphicalRepresentation {

		@Override
		public List<? extends ControlArea<?>> makeControlAreasFor(
				DrawingTreeNode<GeometricConstruction<DianaQuadCurve>, GeometricGraphicalRepresentation> dtn) {
			Vector<ControlArea<?>> returned = new Vector<ControlArea<?>>();

			QuadCurveConstruction curveConstruction = (QuadCurveConstruction) dtn.getDrawable();
			DianaQuadCurve curve = curveConstruction.getCurve();

			if (curveConstruction instanceof QuadCurveWithThreePointsConstruction) {
				PointConstruction startPointConstruction = ((QuadCurveWithThreePointsConstruction) curveConstruction)
						.getStartPointConstruction();

				if (startPointConstruction instanceof ExplicitPointConstruction) {
					returned.add(new DraggableControlPoint<DianaQuadCurve>((GeometricNode<?>) dtn, "p1", curve.getP1(),
							(ExplicitPointConstruction) startPointConstruction) {
						@Override
						public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
								DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
							getGeometricObject().setP1(newAbsolutePoint);
							setPoint(newAbsolutePoint);
							((GeometricNode<?>) dtn).notifyGeometryChanged();
							return true;
						}

						@Override
						public void update(DianaQuadCurve geometricObject) {
							setPoint(geometricObject.getP1());
						}
					});
				}
				else {
					returned.add(new ComputedControlPoint<DianaQuadCurve>((GeometricNode<?>) dtn, "p1", curve.getP1()) {
						@Override
						public void update(DianaQuadCurve geometricObject) {
							setPoint(geometricObject.getP1());
						}
					});
				}
				PointConstruction controlPointConstruction = ((QuadCurveWithThreePointsConstruction) curveConstruction)
						.getControlPointConstruction();
				if (controlPointConstruction instanceof ExplicitPointConstruction) {
					returned.add(new DraggableControlPoint<DianaQuadCurve>((GeometricNode<?>) dtn, "cp", curve.getCtrlPoint(),
							(ExplicitPointConstruction) controlPointConstruction) {
						@Override
						public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
								DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
							getGeometricObject().setCtrlPoint(newAbsolutePoint);
							setPoint(newAbsolutePoint);
							((GeometricNode<?>) dtn).notifyGeometryChanged();
							return true;
						}

						@Override
						public void update(DianaQuadCurve geometricObject) {
							setPoint(geometricObject.getCtrlPoint());
						}
					});
				}
				else {
					returned.add(new ComputedControlPoint<DianaQuadCurve>((GeometricNode<?>) dtn, "cp", curve.getCtrlPoint()) {
						@Override
						public void update(DianaQuadCurve geometricObject) {
							setPoint(geometricObject.getCtrlPoint());
						}
					});
				}
				PointConstruction endPointConstruction = ((QuadCurveWithThreePointsConstruction) curveConstruction)
						.getEndPointConstruction();
				if (endPointConstruction instanceof ExplicitPointConstruction) {
					returned.add(new DraggableControlPoint<DianaQuadCurve>((GeometricNode<?>) dtn, "p2", curve.getP2(),
							(ExplicitPointConstruction) endPointConstruction) {
						@Override
						public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
								DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
							getGeometricObject().setP2(newAbsolutePoint);
							setPoint(newAbsolutePoint);
							((GeometricNode<?>) dtn).notifyGeometryChanged();
							return true;
						}

						@Override
						public void update(DianaQuadCurve geometricObject) {
							setPoint(geometricObject.getP2());
						}
					});
				}
				else {
					returned.add(new ComputedControlPoint<DianaQuadCurve>((GeometricNode<?>) dtn, "p2", curve.getP2()) {
						@Override
						public void update(DianaQuadCurve geometricObject) {
							setPoint(geometricObject.getP2());
						}
					});
				}
				returned.add(new ComputedControlPoint<DianaQuadCurve>((GeometricNode<?>) dtn, "p3", curve.getP3()) {
					@Override
					public void update(DianaQuadCurve geometricObject) {
						setPoint(geometricObject.getP3());
					}
				});

				final DianaSegment line1 = new DianaSegment(getGeometricObject().getP1(), getGeometricObject().getCtrlPoint());
				returned.add(new ControlArea<DianaSegment>(dtn, line1) {
					@Override
					public boolean isDraggable() {
						return false;
					}

					@Override
					public Rectangle paint(DianaGraphics graphics) {
						graphics.setDefaultForeground(
								graphics.getFactory().makeForegroundStyle(Color.LIGHT_GRAY, 0.5f, DashStyle.BIG_DASHES));
						graphics.useDefaultForegroundStyle();
						line1.paint(graphics);
						return null;
					}
				});
				final DianaSegment line2 = new DianaSegment(getGeometricObject().getP2(), getGeometricObject().getCtrlPoint());
				returned.add(new ControlArea<DianaSegment>(dtn, line2) {
					@Override
					public boolean isDraggable() {
						return false;
					}

					@Override
					public Rectangle paint(DianaGraphics graphics) {
						graphics.setDefaultForeground(
								graphics.getFactory().makeForegroundStyle(Color.LIGHT_GRAY, 0.5f, DashStyle.BIG_DASHES));
						graphics.useDefaultForegroundStyle();
						line2.paint(graphics);
						return null;
					}
				});

			}
			return returned;
		}

	}

}
