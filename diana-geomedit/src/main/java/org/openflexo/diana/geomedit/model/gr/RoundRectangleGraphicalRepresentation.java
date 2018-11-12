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
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.GeometricGraphicalRepresentation;
import org.openflexo.diana.control.DianaEditor;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaGeometricObject.CardinalQuadrant;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRoundRectangle;
import org.openflexo.diana.geom.area.DianaQuarterPlane;

@ModelEntity
@ImplementationClass(RoundRectangleGraphicalRepresentationImpl.class)
@XMLElement
public interface RoundRectangleGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<DianaRoundRectangle> {

	public static abstract class RoundRectangleGraphicalRepresentationImpl
			extends GeometricObjectGraphicalRepresentationImpl<DianaRoundRectangle> implements RoundRectangleGraphicalRepresentation {

		private DraggableControlPoint nwCP1;
		private DraggableControlPoint seCP2;

		@Override
		public List<? extends ControlArea<?>> makeControlAreasFor(
				DrawingTreeNode<GeometricConstruction<DianaRoundRectangle>, GeometricGraphicalRepresentation> dtn) {

			Vector<ControlPoint> returned = new Vector<ControlPoint>();

			RoundRectangleConstruction rectangleConstruction = (RoundRectangleConstruction) dtn.getDrawable();
			DianaRoundRectangle rectangle = rectangleConstruction.getRectangle();

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
				returned.add(nwCP1 = new DraggableControlPoint<DianaRoundRectangle>((GeometricNode<?>) dtn, "northWest",
						rectangle.getNorthWestPt(), pc1) {
					private double initialWidth;
					private double initialHeight;

					@Override
					public void startDragging(DianaEditor<?> controller, DianaPoint startPoint) {
						super.startDragging(controller, startPoint);
						initialWidth = getGeometricObject().width;
						initialHeight = getGeometricObject().height;
						setDraggingAuthorizedArea(
								DianaQuarterPlane.makeDianaQuarterPlane(getGeometricObject().getSouthEastPt(), CardinalQuadrant.NORTH_WEST));
					}

					@Override
					public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
							DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
						DianaPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
						setPoint(pt);

						getGeometricObject().x = pt.x;
						getGeometricObject().y = pt.y;
						getGeometricObject().width = -pt.x + initialPoint.x + initialWidth;
						getGeometricObject().height = -pt.y + initialPoint.y + initialHeight;

						((GeometricNode<?>) dtn).notifyGeometryChanged();

						return true;
					}

					@Override
					public void update(DianaRoundRectangle geometricObject) {
						setPoint(geometricObject.getNorthWestPt());
					}
				});
			}
			else {
				returned.add(new ComputedControlPoint<DianaRoundRectangle>((GeometricNode<?>) dtn, "northWest", rectangle.getNorthWestPt()) {
					@Override
					public void update(DianaRoundRectangle geometricObject) {
						setPoint(geometricObject.getNorthWestPt());
					}
				});
			}

			if (pc2 != null) {
				returned.add(seCP2 = new DraggableControlPoint<DianaRoundRectangle>((GeometricNode<?>) dtn, "southEast",
						rectangle.getSouthEastPt(), pc2) {
					private double initialWidth;
					private double initialHeight;

					@Override
					public void startDragging(DianaEditor<?> controller, DianaPoint startPoint) {
						super.startDragging(controller, startPoint);
						initialWidth = getGeometricObject().width;
						initialHeight = getGeometricObject().height;
						setDraggingAuthorizedArea(
								DianaQuarterPlane.makeDianaQuarterPlane(getGeometricObject().getNorthWestPt(), CardinalQuadrant.SOUTH_EAST));
					}

					@Override
					public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
							DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
						DianaPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
						setPoint(pt);

						getGeometricObject().width = pt.x - initialPoint.x + initialWidth;
						getGeometricObject().height = pt.y - initialPoint.y + initialHeight;

						((GeometricNode<?>) dtn).notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(DianaRoundRectangle geometricObject) {
						setPoint(geometricObject.getSouthEastPt());
					}
				});
			}
			else {
				returned.add(new ComputedControlPoint<DianaRoundRectangle>((GeometricNode<?>) dtn, "southEast", rectangle.getSouthEastPt()) {
					@Override
					public void update(DianaRoundRectangle geometricObject) {
						setPoint(geometricObject.getSouthEastPt());
					}
				});
			}

			if (pc1 != null && pc2 != null) {
				returned.add(new ComputedControlPoint<DianaRoundRectangle>((GeometricNode<?>) dtn, "northEast", rectangle.getNorthEastPt()) {
					private double initialWidth;
					private double initialHeight;

					@Override
					public boolean isDraggable() {
						return true;
					}

					@Override
					public void startDragging(DianaEditor<?> controller, DianaPoint startPoint) {
						super.startDragging(controller, startPoint);
						initialWidth = getGeometricObject().width;
						initialHeight = getGeometricObject().height;
						setDraggingAuthorizedArea(
								DianaQuarterPlane.makeDianaQuarterPlane(getGeometricObject().getSouthWestPt(), CardinalQuadrant.NORTH_EAST));
					}

					@Override
					public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
							DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
						DianaPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
						setPoint(pt);

						getGeometricObject().y = pt.y;
						getGeometricObject().width = pt.x - initialPoint.x + initialWidth;
						getGeometricObject().height = -pt.y + initialPoint.y + initialHeight;

						nwCP1.setPoint(new DianaPoint(getGeometricObject().x, pt.y));
						seCP2.setPoint(new DianaPoint(pt.x, getGeometricObject().y + getGeometricObject().height));

						((GeometricNode<?>) dtn).notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(DianaRoundRectangle geometricObject) {
						setPoint(geometricObject.getNorthEastPt());
					}
				});
			}
			else {
				returned.add(new ComputedControlPoint<DianaRoundRectangle>((GeometricNode<?>) dtn, "northEast", rectangle.getSouthEastPt()) {
					@Override
					public void update(DianaRoundRectangle geometricObject) {
						setPoint(geometricObject.getNorthEastPt());
					}
				});
			}

			if (pc1 != null && pc2 != null) {
				returned.add(new ComputedControlPoint<DianaRoundRectangle>((GeometricNode<?>) dtn, "southWest", rectangle.getSouthWestPt()) {
					private double initialWidth;
					private double initialHeight;

					@Override
					public boolean isDraggable() {
						return true;
					}

					@Override
					public void startDragging(DianaEditor<?> controller, DianaPoint startPoint) {
						super.startDragging(controller, startPoint);
						initialWidth = getGeometricObject().width;
						initialHeight = getGeometricObject().height;
						setDraggingAuthorizedArea(
								DianaQuarterPlane.makeDianaQuarterPlane(getGeometricObject().getNorthEastPt(), CardinalQuadrant.SOUTH_WEST));
					}

					@Override
					public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
							DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
						DianaPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
						setPoint(pt);

						getGeometricObject().x = pt.x;
						getGeometricObject().width = -pt.x + initialPoint.x + initialWidth;
						getGeometricObject().height = pt.y - initialPoint.y + initialHeight;

						nwCP1.setPoint(new DianaPoint(pt.x, getGeometricObject().y));
						seCP2.setPoint(new DianaPoint(getGeometricObject().x + getGeometricObject().width, pt.y));

						((GeometricNode<?>) dtn).notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(DianaRoundRectangle geometricObject) {
						setPoint(geometricObject.getSouthWestPt());
					}
				});
			}
			else {
				returned.add(new ComputedControlPoint<DianaRoundRectangle>((GeometricNode<?>) dtn, "southWest", rectangle.getSouthEastPt()) {
					@Override
					public void update(DianaRoundRectangle geometricObject) {
						setPoint(geometricObject.getSouthWestPt());
					}
				});
			}

			return returned;
		}

	}

}
