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
import org.openflexo.fge.geom.FGEEllips;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.Ellips;
import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.CircleWithCenterAndPointConstruction;
import org.openflexo.fge.geomedit.construction.EllipsConstruction;
import org.openflexo.fge.geomedit.construction.ExplicitPointConstruction;
import org.openflexo.fge.geomedit.construction.PointConstruction;
import org.openflexo.xmlcode.XMLSerializable;

public class EllipsGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGEEllips, Ellips> implements XMLSerializable {
	// Called for LOAD
	public EllipsGraphicalRepresentation(GeomEditBuilder builder) {
		this(null, builder.drawing);
		initializeDeserialization();
	}

	public EllipsGraphicalRepresentation(Ellips ellips, GeometricDrawing aDrawing) {
		super(ellips, aDrawing);
	}

	@Override
	protected List<ControlPoint> buildControlPointsForEllips(FGEEllips ellips) {
		Vector<ControlPoint> returned = new Vector<ControlPoint>();

		EllipsConstruction ellipsConstruction = getDrawable().getConstruction();

		if (ellipsConstruction instanceof CircleWithCenterAndPointConstruction) {
			PointConstruction centerConstruction = ((CircleWithCenterAndPointConstruction) ellipsConstruction).centerConstruction;
			if (centerConstruction instanceof ExplicitPointConstruction) {
				returned.add(new DraggableControlPoint<FGEEllips>(this, "center", ellips.getCenter(),
						(ExplicitPointConstruction) centerConstruction) {
					@Override
					public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
							FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
						// getGeometricObject().setCenter
						setPoint(newAbsolutePoint);
						notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(FGEEllips geometricObject) {
						setPoint(geometricObject.getCenter());
					}
				});
			} else {
				returned.add(new ComputedControlPoint<FGEEllips>(this, "center", ellips.getCenter()) {
					@Override
					public void update(FGEEllips geometricObject) {
						setPoint(geometricObject.getCenter());
					}
				});
			}

			final PointConstruction pointConstruction = ((CircleWithCenterAndPointConstruction) ellipsConstruction).pointConstruction;
			if (pointConstruction instanceof ExplicitPointConstruction) {
				returned.add(new DraggableControlPoint<FGEEllips>(this, "point", pointConstruction.getData(),
						(ExplicitPointConstruction) pointConstruction) {
					@Override
					public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
							FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
						// getGeometricObject().setCenter
						setPoint(newAbsolutePoint);
						notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(FGEEllips geometricObject) {
						// setPoint(pointConstruction.getData());
					}
				});
			} else {
				returned.add(new ComputedControlPoint<FGEEllips>(this, "point", pointConstruction.getData()) {
					@Override
					public void update(FGEEllips geometricObject) {
						// setPoint(pointConstruction.getData());
					}
				});
			}
		}
		return returned;
	}

	/*private DraggableControlPoint nwCP1;
	private DraggableControlPoint seCP2;

	protected List<ControlPoint> buildControlPointsForRectangle(FGERectangle rectangle)
	{
		Vector<ControlPoint> returned = new Vector<ControlPoint>();

		RectangleConstruction rectangleConstruction = (RectangleConstruction)getDrawable().getConstruction();

		ExplicitPointConstruction pc1 = null;
		ExplicitPointConstruction pc2 = null;

		if (rectangleConstruction instanceof RectangleWithTwoPointsConstruction) {
			if (((RectangleWithTwoPointsConstruction)rectangleConstruction).pointConstruction1 instanceof ExplicitPointConstruction) {
				pc1 = (ExplicitPointConstruction)((RectangleWithTwoPointsConstruction)rectangleConstruction).pointConstruction1;
			}
			if (((RectangleWithTwoPointsConstruction)rectangleConstruction).pointConstruction2 instanceof ExplicitPointConstruction) {
				pc2 = (ExplicitPointConstruction)((RectangleWithTwoPointsConstruction)rectangleConstruction).pointConstruction2;
			}
		}


		if (pc1 != null) {
			returned.add(nwCP1 = new DraggableControlPoint<FGERectangle>(this,"northWest",rectangle.getNorthWestPt(),pc1) {
				private double initialWidth;
				private double initialHeight;
				@Override
				public void startDragging()
				{
					initialWidth = ((FGERectangle)getGeometricObject()).width;
					initialHeight = ((FGERectangle)getGeometricObject()).height;
					setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(((FGERectangle)getGeometricObject()).getSouthEastPt(), CardinalQuadrant.NORTH_WEST));
				}
				@Override
				public void dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint, FGEPoint initialPoint)
				{
					FGEPoint pt  = getNearestPointOnAuthorizedArea(newAbsolutePoint);
					setPoint(pt);

					((FGERectangle)getGeometricObject()).x = pt.x;
					((FGERectangle)getGeometricObject()).y = pt.y;
					((FGERectangle)getGeometricObject()).width = -pt.x+initialPoint.x+initialWidth;
					((FGERectangle)getGeometricObject()).height = -pt.y+initialPoint.y+initialHeight;

					notifyGeometryChanged();
				}
				@Override
				public void update(FGERectangle geometricObject)
				{
					setPoint(geometricObject.getNorthWestPt());
				}
			});
		}
		else {
			returned.add(new ComputedControlPoint<FGERectangle>(this,"northWest",rectangle.getNorthWestPt()) {
				@Override
				public void update(FGERectangle geometricObject)
				{
					setPoint(geometricObject.getNorthWestPt());
				}
			});
		}

		if (pc2 != null) {
			returned.add(seCP2 = new DraggableControlPoint<FGERectangle>(this,"southEast",rectangle.getSouthEastPt(),pc2) {
				private double initialWidth;
				private double initialHeight;
				@Override
				public void startDragging()
				{
					initialWidth = ((FGERectangle)getGeometricObject()).width;
					initialHeight = ((FGERectangle)getGeometricObject()).height;
					setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(((FGERectangle)getGeometricObject()).getNorthWestPt(), CardinalQuadrant.SOUTH_EAST));
				}
				@Override
				public void dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint, FGEPoint initialPoint)
				{
					FGEPoint pt  = getNearestPointOnAuthorizedArea(newAbsolutePoint);
					setPoint(pt);

					((FGERectangle)getGeometricObject()).width = pt.x-initialPoint.x+initialWidth;
					((FGERectangle)getGeometricObject()).height = pt.y-initialPoint.y+initialHeight;

					notifyGeometryChanged();
				}
				@Override
				public void update(FGERectangle geometricObject)
				{
					setPoint(geometricObject.getSouthEastPt());
				}
			});
		}
		else {
			returned.add(new ComputedControlPoint<FGERectangle>(this,"southEast",rectangle.getSouthEastPt()) {
				@Override
				public void update(FGERectangle geometricObject)
				{
					setPoint(geometricObject.getSouthEastPt());
				}
			});
		}

		if (pc1 != null && pc2 != null) {
			returned.add(new ComputedControlPoint<FGERectangle>(this,"northEast",rectangle.getNorthEastPt()) {
				private double initialWidth;
				private double initialHeight;
				@Override
				public boolean isDraggable()
				{
					return true;
				}
				@Override
				public void startDragging()
				{
					initialWidth = ((FGERectangle)getGeometricObject()).width;
					initialHeight = ((FGERectangle)getGeometricObject()).height;
					setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(((FGERectangle)getGeometricObject()).getSouthWestPt(), CardinalQuadrant.NORTH_EAST));
				}
				@Override
				public void dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint, FGEPoint initialPoint)
				{
					FGEPoint pt  = getNearestPointOnAuthorizedArea(newAbsolutePoint);
					setPoint(pt);

					((FGERectangle)getGeometricObject()).y = pt.y;
					((FGERectangle)getGeometricObject()).width = pt.x-initialPoint.x+initialWidth;
					((FGERectangle)getGeometricObject()).height = -pt.y+initialPoint.y+initialHeight;

					nwCP1.setPoint(new FGEPoint(((FGERectangle)getGeometricObject()).x,pt.y));
					seCP2.setPoint(new FGEPoint(pt.x,((FGERectangle)getGeometricObject()).y+((FGERectangle)getGeometricObject()).height));

					notifyGeometryChanged();
				}
				@Override
				public void update(FGERectangle geometricObject)
				{
					setPoint(geometricObject.getNorthEastPt());
				}
			});
		}
		else {
			returned.add(new ComputedControlPoint<FGERectangle>(this,"northEast",rectangle.getSouthEastPt()) {
				@Override
				public void update(FGERectangle geometricObject)
				{
					setPoint(geometricObject.getNorthEastPt());
				}
			});
		}

		if (pc1 != null && pc2 != null) {
			returned.add(new ComputedControlPoint<FGERectangle>(this,"southWest",rectangle.getSouthWestPt()) {
				private double initialWidth;
				private double initialHeight;
				@Override
				public boolean isDraggable()
				{
					return true;
				}
				@Override
				public void startDragging()
				{
					initialWidth = ((FGERectangle)getGeometricObject()).width;
					initialHeight = ((FGERectangle)getGeometricObject()).height;
					setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(((FGERectangle)getGeometricObject()).getNorthEastPt(), CardinalQuadrant.SOUTH_WEST));
				}
				@Override
				public void dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint, FGEPoint initialPoint)
				{
					FGEPoint pt  = getNearestPointOnAuthorizedArea(newAbsolutePoint);
					setPoint(pt);

					((FGERectangle)getGeometricObject()).x = pt.x;
					((FGERectangle)getGeometricObject()).width = -pt.x+initialPoint.x+initialWidth;
					((FGERectangle)getGeometricObject()).height = pt.y-initialPoint.y+initialHeight;

					nwCP1.setPoint(new FGEPoint(pt.x,((FGERectangle)getGeometricObject()).y));
					seCP2.setPoint(new FGEPoint(((FGERectangle)getGeometricObject()).x+((FGERectangle)getGeometricObject()).width,pt.y));

					notifyGeometryChanged();
				}
				@Override
				public void update(FGERectangle geometricObject)
				{
					setPoint(geometricObject.getSouthWestPt());
				}
			});
		}
		else {
			returned.add(new ComputedControlPoint<FGERectangle>(this,"southWest",rectangle.getSouthEastPt()) {
				@Override
				public void update(FGERectangle geometricObject)
				{
					setPoint(geometricObject.getSouthWestPt());
				}
			});
		}


		return returned;
	}
	*/
}
