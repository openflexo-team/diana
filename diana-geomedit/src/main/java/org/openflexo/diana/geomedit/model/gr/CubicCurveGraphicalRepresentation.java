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
import org.openflexo.diana.geomedit.model.CubicCurveConstruction;
import org.openflexo.diana.geomedit.model.CubicCurveWithFourPointsConstruction;
import org.openflexo.diana.geomedit.model.ExplicitPointConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.PointConstruction;
import org.openflexo.diana.geomedit.model.gr.CubicCurveGraphicalRepresentation.CubicCurveGraphicalRepresentationImpl;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGECubicCurve;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(CubicCurveGraphicalRepresentationImpl.class)
@XMLElement
public interface CubicCurveGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGECubicCurve> {

	public static abstract class CubicCurveGraphicalRepresentationImpl extends GeometricObjectGraphicalRepresentationImpl<FGECubicCurve>
			implements CubicCurveGraphicalRepresentation {

		@Override
		public List<? extends ControlArea<?>> makeControlAreasFor(
				DrawingTreeNode<GeometricConstruction<FGECubicCurve>, GeometricGraphicalRepresentation> dtn) {
			Vector<ControlArea<?>> returned = new Vector<ControlArea<?>>();

			CubicCurveConstruction curveConstruction = (CubicCurveConstruction) dtn.getDrawable();
			FGECubicCurve curve = curveConstruction.getCurve();

			if (curveConstruction instanceof CubicCurveWithFourPointsConstruction) {
				PointConstruction startPointConstruction = ((CubicCurveWithFourPointsConstruction) curveConstruction)
						.getStartPointConstruction();

				if (startPointConstruction instanceof ExplicitPointConstruction) {
					returned.add(new DraggableControlPoint<FGECubicCurve>((GeometricNode<?>) dtn, "p1", curve.getP1(),
							(ExplicitPointConstruction) startPointConstruction) {
						@Override
						public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
								FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
							getGeometricObject().setP1(newAbsolutePoint);
							setPoint(newAbsolutePoint);
							((GeometricNode<?>) dtn).notifyGeometryChanged();
							return true;
						}

						@Override
						public void update(FGECubicCurve geometricObject) {
							setPoint(geometricObject.getP1());
						}
					});
				}
				else {
					returned.add(new ComputedControlPoint<FGECubicCurve>((GeometricNode<?>) dtn, "p1", curve.getP1()) {
						@Override
						public void update(FGECubicCurve geometricObject) {
							setPoint(geometricObject.getP1());
						}
					});
				}
				PointConstruction controlPointConstruction1 = ((CubicCurveWithFourPointsConstruction) curveConstruction)
						.getControlPointConstruction1();
				if (controlPointConstruction1 instanceof ExplicitPointConstruction) {
					returned.add(new DraggableControlPoint<FGECubicCurve>((GeometricNode<?>) dtn, "cp1", curve.getCtrlP1(),
							(ExplicitPointConstruction) controlPointConstruction1) {
						@Override
						public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
								FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
							getGeometricObject().setCtrlP1(newAbsolutePoint);
							setPoint(newAbsolutePoint);
							((GeometricNode<?>) dtn).notifyGeometryChanged();
							return true;
						}

						@Override
						public void update(FGECubicCurve geometricObject) {
							setPoint(geometricObject.getCtrlP1());
						}
					});
				}
				else {
					returned.add(new ComputedControlPoint<FGECubicCurve>((GeometricNode<?>) dtn, "cp1", curve.getCtrlP1()) {
						@Override
						public void update(FGECubicCurve geometricObject) {
							setPoint(geometricObject.getCtrlP1());
						}
					});
				}
				PointConstruction endPointConstruction = ((CubicCurveWithFourPointsConstruction) curveConstruction)
						.getEndPointConstruction();
				if (endPointConstruction instanceof ExplicitPointConstruction) {
					returned.add(new DraggableControlPoint<FGECubicCurve>((GeometricNode<?>) dtn, "p2", curve.getP2(),
							(ExplicitPointConstruction) endPointConstruction) {
						@Override
						public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
								FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
							getGeometricObject().setP2(newAbsolutePoint);
							setPoint(newAbsolutePoint);
							((GeometricNode<?>) dtn).notifyGeometryChanged();
							return true;
						}

						@Override
						public void update(FGECubicCurve geometricObject) {
							setPoint(geometricObject.getP2());
						}
					});
				}
				else {
					returned.add(new ComputedControlPoint<FGECubicCurve>((GeometricNode<?>) dtn, "p2", curve.getP2()) {
						@Override
						public void update(FGECubicCurve geometricObject) {
							setPoint(geometricObject.getP2());
						}
					});
				}
				PointConstruction controlPointConstruction2 = ((CubicCurveWithFourPointsConstruction) curveConstruction)
						.getControlPointConstruction2();
				if (controlPointConstruction2 instanceof ExplicitPointConstruction) {
					returned.add(new DraggableControlPoint<FGECubicCurve>((GeometricNode<?>) dtn, "cp2", curve.getCtrlP2(),
							(ExplicitPointConstruction) controlPointConstruction2) {
						@Override
						public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
								FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
							getGeometricObject().setCtrlP2(newAbsolutePoint);
							setPoint(newAbsolutePoint);
							((GeometricNode<?>) dtn).notifyGeometryChanged();
							return true;
						}

						@Override
						public void update(FGECubicCurve geometricObject) {
							setPoint(geometricObject.getCtrlP2());
						}
					});
				}
				else {
					returned.add(new ComputedControlPoint<FGECubicCurve>((GeometricNode<?>) dtn, "cp2", curve.getCtrlP2()) {
						@Override
						public void update(FGECubicCurve geometricObject) {
							setPoint(geometricObject.getCtrlP2());
						}
					});
				}

				final FGESegment line1 = new FGESegment(getGeometricObject().getP1(), getGeometricObject().getCtrlP1());
				returned.add(new ControlArea<FGESegment>(dtn, line1) {
					@Override
					public boolean isDraggable() {
						return false;
					}

					@Override
					public Rectangle paint(FGEGraphics graphics) {
						graphics.setDefaultForeground(
								graphics.getFactory().makeForegroundStyle(Color.LIGHT_GRAY, 0.5f, DashStyle.BIG_DASHES));
						graphics.useDefaultForegroundStyle();
						line1.paint(graphics);
						return null;
					}
				});
				final FGESegment line2 = new FGESegment(getGeometricObject().getP2(), getGeometricObject().getCtrlP2());
				returned.add(new ControlArea<FGESegment>(dtn, line2) {
					@Override
					public boolean isDraggable() {
						return false;
					}

					@Override
					public Rectangle paint(FGEGraphics graphics) {
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
