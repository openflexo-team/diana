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
import org.openflexo.diana.geomedit.model.ExplicitPointConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.RoundRectangleConstruction;
import org.openflexo.diana.geomedit.model.RoundRectangleWithTwoPointsConstruction;
import org.openflexo.diana.geomedit.model.gr.RoundRectangleGraphicalRepresentation.RoundRectangleGraphicalRepresentationImpl;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEGeometricObject.CardinalQuadrant;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGERoundRectangle;
import org.openflexo.fge.geom.area.FGEQuarterPlane;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(RoundRectangleGraphicalRepresentationImpl.class)
@XMLElement
public interface RoundRectangleGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGERoundRectangle> {

	public static abstract class RoundRectangleGraphicalRepresentationImpl
			extends GeometricObjectGraphicalRepresentationImpl<FGERoundRectangle> implements RoundRectangleGraphicalRepresentation {

		private DraggableControlPoint nwCP1;
		private DraggableControlPoint seCP2;

		@Override
		public List<? extends ControlArea<?>> makeControlAreasFor(
				DrawingTreeNode<GeometricConstruction<FGERoundRectangle>, GeometricGraphicalRepresentation> dtn) {

			Vector<ControlPoint> returned = new Vector<ControlPoint>();

			RoundRectangleConstruction rectangleConstruction = (RoundRectangleConstruction) dtn.getDrawable();
			FGERoundRectangle rectangle = rectangleConstruction.getRectangle();

			ExplicitPointConstruction pc1 = null;
			ExplicitPointConstruction pc2 = null;

			if (rectangleConstruction instanceof RoundRectangleWithTwoPointsConstruction) {
				if (((RoundRectangleWithTwoPointsConstruction) rectangleConstruction)
						.getPointConstruction1() instanceof ExplicitPointConstruction) {
					pc1 = (ExplicitPointConstruction) ((RoundRectangleWithTwoPointsConstruction) rectangleConstruction)
							.getPointConstruction1();
				}
				if (((RoundRectangleWithTwoPointsConstruction) rectangleConstruction)
						.getPointConstruction2() instanceof ExplicitPointConstruction) {
					pc2 = (ExplicitPointConstruction) ((RoundRectangleWithTwoPointsConstruction) rectangleConstruction)
							.getPointConstruction2();
				}
			}

			if (pc1 != null) {
				returned.add(nwCP1 = new DraggableControlPoint<FGERectangle>((GeometricNode<?>) dtn, "northWest",
						rectangle.getNorthWestPt(), pc1) {
					private double initialWidth;
					private double initialHeight;

					@Override
					public void startDragging(DianaEditor<?> controller, FGEPoint startPoint) {
						super.startDragging(controller, startPoint);
						initialWidth = getGeometricObject().width;
						initialHeight = getGeometricObject().height;
						setDraggingAuthorizedArea(
								FGEQuarterPlane.makeFGEQuarterPlane(getGeometricObject().getSouthEastPt(), CardinalQuadrant.NORTH_WEST));
					}

					@Override
					public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
							FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
						FGEPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
						setPoint(pt);

						getGeometricObject().x = pt.x;
						getGeometricObject().y = pt.y;
						getGeometricObject().width = -pt.x + initialPoint.x + initialWidth;
						getGeometricObject().height = -pt.y + initialPoint.y + initialHeight;

						((GeometricNode<?>) dtn).notifyGeometryChanged();

						return true;
					}

					@Override
					public void update(FGERectangle geometricObject) {
						setPoint(geometricObject.getNorthWestPt());
					}
				});
			}
			else {
				returned.add(new ComputedControlPoint<FGERectangle>((GeometricNode<?>) dtn, "northWest", rectangle.getNorthWestPt()) {
					@Override
					public void update(FGERectangle geometricObject) {
						setPoint(geometricObject.getNorthWestPt());
					}
				});
			}

			if (pc2 != null) {
				returned.add(seCP2 = new DraggableControlPoint<FGERectangle>((GeometricNode<?>) dtn, "southEast",
						rectangle.getSouthEastPt(), pc2) {
					private double initialWidth;
					private double initialHeight;

					@Override
					public void startDragging(DianaEditor<?> controller, FGEPoint startPoint) {
						super.startDragging(controller, startPoint);
						initialWidth = getGeometricObject().width;
						initialHeight = getGeometricObject().height;
						setDraggingAuthorizedArea(
								FGEQuarterPlane.makeFGEQuarterPlane(getGeometricObject().getNorthWestPt(), CardinalQuadrant.SOUTH_EAST));
					}

					@Override
					public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
							FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
						FGEPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
						setPoint(pt);

						getGeometricObject().width = pt.x - initialPoint.x + initialWidth;
						getGeometricObject().height = pt.y - initialPoint.y + initialHeight;

						((GeometricNode<?>) dtn).notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(FGERectangle geometricObject) {
						setPoint(geometricObject.getSouthEastPt());
					}
				});
			}
			else {
				returned.add(new ComputedControlPoint<FGERectangle>((GeometricNode<?>) dtn, "southEast", rectangle.getSouthEastPt()) {
					@Override
					public void update(FGERectangle geometricObject) {
						setPoint(geometricObject.getSouthEastPt());
					}
				});
			}

			if (pc1 != null && pc2 != null) {
				returned.add(new ComputedControlPoint<FGERectangle>((GeometricNode<?>) dtn, "northEast", rectangle.getNorthEastPt()) {
					private double initialWidth;
					private double initialHeight;

					@Override
					public boolean isDraggable() {
						return true;
					}

					@Override
					public void startDragging(DianaEditor<?> controller, FGEPoint startPoint) {
						super.startDragging(controller, startPoint);
						initialWidth = getGeometricObject().width;
						initialHeight = getGeometricObject().height;
						setDraggingAuthorizedArea(
								FGEQuarterPlane.makeFGEQuarterPlane(getGeometricObject().getSouthWestPt(), CardinalQuadrant.NORTH_EAST));
					}

					@Override
					public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
							FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
						FGEPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
						setPoint(pt);

						getGeometricObject().y = pt.y;
						getGeometricObject().width = pt.x - initialPoint.x + initialWidth;
						getGeometricObject().height = -pt.y + initialPoint.y + initialHeight;

						nwCP1.setPoint(new FGEPoint(getGeometricObject().x, pt.y));
						seCP2.setPoint(new FGEPoint(pt.x, getGeometricObject().y + getGeometricObject().height));

						((GeometricNode<?>) dtn).notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(FGERectangle geometricObject) {
						setPoint(geometricObject.getNorthEastPt());
					}
				});
			}
			else {
				returned.add(new ComputedControlPoint<FGERectangle>((GeometricNode<?>) dtn, "northEast", rectangle.getSouthEastPt()) {
					@Override
					public void update(FGERectangle geometricObject) {
						setPoint(geometricObject.getNorthEastPt());
					}
				});
			}

			if (pc1 != null && pc2 != null) {
				returned.add(new ComputedControlPoint<FGERectangle>((GeometricNode<?>) dtn, "southWest", rectangle.getSouthWestPt()) {
					private double initialWidth;
					private double initialHeight;

					@Override
					public boolean isDraggable() {
						return true;
					}

					@Override
					public void startDragging(DianaEditor<?> controller, FGEPoint startPoint) {
						super.startDragging(controller, startPoint);
						initialWidth = getGeometricObject().width;
						initialHeight = getGeometricObject().height;
						setDraggingAuthorizedArea(
								FGEQuarterPlane.makeFGEQuarterPlane(getGeometricObject().getNorthEastPt(), CardinalQuadrant.SOUTH_WEST));
					}

					@Override
					public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
							FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
						FGEPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
						setPoint(pt);

						getGeometricObject().x = pt.x;
						getGeometricObject().width = -pt.x + initialPoint.x + initialWidth;
						getGeometricObject().height = pt.y - initialPoint.y + initialHeight;

						nwCP1.setPoint(new FGEPoint(pt.x, getGeometricObject().y));
						seCP2.setPoint(new FGEPoint(getGeometricObject().x + getGeometricObject().width, pt.y));

						((GeometricNode<?>) dtn).notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(FGERectangle geometricObject) {
						setPoint(geometricObject.getSouthWestPt());
					}
				});
			}
			else {
				returned.add(new ComputedControlPoint<FGERectangle>((GeometricNode<?>) dtn, "southWest", rectangle.getSouthEastPt()) {
					@Override
					public void update(FGERectangle geometricObject) {
						setPoint(geometricObject.getSouthWestPt());
					}
				});
			}

			return returned;
		}

	}

}