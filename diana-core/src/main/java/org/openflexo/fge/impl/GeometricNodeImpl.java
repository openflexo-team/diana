/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.fge.impl;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GRBinding;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.cp.GeometryAdjustingControlPoint;
import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGECircle;
import org.openflexo.fge.geom.FGECubicCurve;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEEllips;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.geom.FGEGeneralShape.GeneralShapePathElement;
import org.openflexo.fge.geom.FGEGeometricObject.CardinalQuadrant;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGEPolylin;
import org.openflexo.fge.geom.FGEQuadCurve;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGERoundRectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEPlane;
import org.openflexo.fge.geom.area.FGEQuarterPlane;
import org.openflexo.fge.graphics.FGEGeometricGraphics;
import org.openflexo.fge.notifications.GeometryModified;

public class GeometricNodeImpl<O> extends DrawingTreeNodeImpl<O, GeometricGraphicalRepresentation> implements GeometricNode<O> {

	private static final Logger logger = Logger.getLogger(GeometricNodeImpl.class.getPackage().getName());

	// TODO: change to protected
	public GeometricNodeImpl(DrawingImpl<?> drawingImpl, O drawable, GRBinding<O, GeometricGraphicalRepresentation> grBinding,
			ContainerNodeImpl<?, ?> parentNode) {
		super(drawingImpl, drawable, grBinding, parentNode);
		// graphics = new FGEGeometricGraphicsImpl(this);
		startDrawableObserving();
	}

	@Override
	public boolean delete() {
		if (!isDeleted()) {
			stopDrawableObserving();
			super.delete();
			finalizeDeletion();
			return true;
		}
		return false;
	}

	private List<? extends ControlArea<?>> controlAreas = null;

	@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		if (controlAreas == null) {
			controlAreas = getGRBinding().makeControlAreasFor(this);
			if (controlAreas == null) {
				controlAreas = makeDefaultControlPoints();
			}
		}
		return controlAreas;
	}

	@Override
	public void clearControlAreas() {
		controlAreas = null;
	}

	public void rebuildControlAreas() {
		clearControlAreas();
		getControlAreas();
	}

	@Override
	public int getViewX(double scale) {
		return getViewBounds(scale).x;
	}

	@Override
	public int getViewY(double scale) {
		return getViewBounds(scale).y;
	}

	@Override
	public int getViewWidth(double scale) {
		return getViewBounds(scale).width;
	}

	@Override
	public int getViewHeight(double scale) {
		return getViewBounds(scale).height;
	}

	@Override
	public Rectangle getViewBounds(double scale) {
		return getBounds(scale);
	}

	@Override
	public Rectangle getBounds(double scale) {
		return getParentNode().getViewBounds(scale);
		// return new Rectangle(0,0,1,1);
	}

	@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		return AffineTransform.getScaleInstance(scale, scale);

		/*double width = getContainerGraphicalRepresentation().getViewWidth(scale);
		double height = getContainerGraphicalRepresentation().getViewHeight(scale);
		AffineTransform returned = AffineTransform.getScaleInstance(width,height);
		if (scale != 1) {
			returned.preConcatenate(AffineTransform.getScaleInstance(scale,scale));
		}
		return returned;*/
	}

	@Override
	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale) {
		return AffineTransform.getScaleInstance(1 / scale, 1 / scale);

		/*double width = getContainerGraphicalRepresentation().getViewWidth(scale);
		double height = getContainerGraphicalRepresentation().getViewHeight(scale);
		AffineTransform returned = new AffineTransform();
		if (scale != 1) returned = AffineTransform.getScaleInstance(1/scale, 1/scale);
		returned.preConcatenate(AffineTransform.getScaleInstance(1/width,1/height));
		return returned;*/
	}

	@Override
	public FGERectangle getNormalizedBounds() {
		Rectangle bounds = getBounds(1);
		return new FGERectangle(0, 0, bounds.width, bounds.height, Filling.FILLED);
	}

	@Override
	public boolean isContainedInSelection(Rectangle drawingViewSelection, double scale) {
		// Unused FGERectangle drawingViewBounds =
		new FGERectangle(drawingViewSelection.getX(), drawingViewSelection.getY(), drawingViewSelection.getWidth(),
				drawingViewSelection.getHeight(), Filling.FILLED);
		boolean isFullyContained = true;
		/*	for (ControlPoint cp : getConnector().getControlPoints()) {
			Point cpInContainerView = convertLocalNormalizedPointToRemoteViewCoordinates(
					cp.getPoint(), 
					getDrawingGraphicalRepresentation(), 
					scale);
			FGEPoint preciseCPInContainerView = new FGEPoint(cpInContainerView.x,cpInContainerView.y);
			if (!drawingViewBounds.containsPoint(preciseCPInContainerView)) {
				//System.out.println("Going outside: point="+preciseCPInContainerView+" bounds="+containerViewBounds);
				isFullyContained = false;
			}
		}*/
		return isFullyContained;
	}

	@Override
	public void paint(FGEGeometricGraphics g) {
		/*if (!isRegistered()) {
			setRegistered(true);
		}*/

		// super.paint(g, controller);

		/*Graphics2D g2 = (Graphics2D) g;
		if (controller instanceof AbstractDianaEditor<?, ?, ?>) {
			graphics.createGraphics(g2, (AbstractDianaEditor<?, ?, ?>) controller);
		} else {
			logger.warning("Unsupported controller: " + controller);
		}*/

		g.setDefaultBackground(getGraphicalRepresentation().getBackground());
		g.setDefaultForeground(getGraphicalRepresentation().getForeground());
		g.setDefaultTextStyle(getGraphicalRepresentation().getTextStyle());

		if (getIsSelected() || getIsFocused()) {
			ForegroundStyle style = (ForegroundStyle) getGraphicalRepresentation().getForeground().clone();
			if (getIsSelected()) {
				style.setColorNoNotification(getDrawing().getRoot().getGraphicalRepresentation().getSelectionColor());
			}
			else if (getIsFocused()) {
				style.setColorNoNotification(getDrawing().getRoot().getGraphicalRepresentation().getFocusColor());
			}
			g.setDefaultForeground(style);
		}

		// System.out.println("Attempt to paint " + getGeometricObject() + " with " + g + " dtn=" +
		// g.getNode());

		paintGeometricObject(g);

		if (getIsSelected() || getIsFocused()) {
			// Unused Color color = null;
			// Unused if (getIsSelected()) {
			// Unused color = getDrawing().getRoot().getGraphicalRepresentation().getSelectionColor();
			// Unused }
			// Unused else if (getIsFocused()) {
			// Unused color = getDrawing().getRoot().getGraphicalRepresentation().getFocusColor();
			// Unused }

			// getControlAreas();

			for (ControlArea<?> ca : getControlAreas()) {
				ca.paint(g);
			}

			/*for (ControlPoint cp : getControlPoints()) {
				cp.paint(g);
			}*/
		}

		if (hasFloatingLabel()) {
			g.useTextStyle(getGraphicalRepresentation().getTextStyle());
			g.drawString(getGraphicalRepresentation().getText(),
					new FGEPoint(getLabelRelativePosition().x + getGraphicalRepresentation().getAbsoluteTextX(),
							getLabelRelativePosition().y + getGraphicalRepresentation().getAbsoluteTextY()),
					getGraphicalRepresentation().getHorizontalTextAlignment());
		}

		// g.releaseGraphics();
	}

	@Override
	public void paintGeometricObject(FGEGeometricGraphics graphics) {
		getGeometricObject().paint(graphics);
	}

	protected FGEPoint getLabelRelativePosition() {
		if (getGeometricObject() instanceof FGEPoint) {
			return (FGEPoint) getGeometricObject();
		}
		else if (getGeometricObject() instanceof FGEAbstractLine) {
			FGEAbstractLine line = (FGEAbstractLine) getGeometricObject();
			return new FGESegment(line.getP1(), line.getP2()).getMiddle();
		}
		else if (getGeometricObject() instanceof FGEShape) {
			return ((FGEShape) getGeometricObject()).getCenter();
		}
		else if (getGeometricObject() instanceof FGEShape) {
			return ((FGEShape<?>) getGeometricObject()).getCenter();
		}
		return new FGEPoint(0, 0);
	}

	protected List<ControlPoint> buildControlPointsForPoint(FGEPoint point) {
		Vector<ControlPoint> returned = new Vector<>();
		returned.add(new GeometryAdjustingControlPoint<FGEPoint>(this, "point", point.clone()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGEPoint) getGeometricObject()).x = newAbsolutePoint.x;
				((FGEPoint) getGeometricObject()).y = newAbsolutePoint.y;
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGEPoint geometricObject) {
				setPoint(geometricObject);
			}
		});
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForLine(FGEAbstractLine<?> line) {
		Vector<ControlPoint> returned = new Vector<>();
		returned.add(new GeometryAdjustingControlPoint<FGELine>(this, "p1", line.getP1()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGELine) getGeometricObject()).setP1(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGELine geometricObject) {
				setPoint(geometricObject.getP1());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGELine>(this, "p2", line.getP2()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGELine) getGeometricObject()).setP2(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGELine geometricObject) {
				setPoint(geometricObject.getP2());
			}
		});
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForCurve(FGEQuadCurve curve) {
		Vector<ControlPoint> returned = new Vector<>();
		returned.add(new GeometryAdjustingControlPoint<FGEQuadCurve>(this, "p1", curve.getP1()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGEQuadCurve) getGeometricObject()).setP1(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGEQuadCurve geometricObject) {
				setPoint(geometricObject.getP1());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGEQuadCurve>(this, "cp", curve.getCtrlPoint()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGEQuadCurve) getGeometricObject()).setCtrlPoint(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGEQuadCurve geometricObject) {
				setPoint(geometricObject.getCtrlPoint());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGEQuadCurve>(this, "p3", curve.getP3()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGEQuadCurve) getGeometricObject()).setP3(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGEQuadCurve geometricObject) {
				setPoint(geometricObject.getP3());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGEQuadCurve>(this, "p2", curve.getP2()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGEQuadCurve) getGeometricObject()).setP2(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGEQuadCurve geometricObject) {
				setPoint(geometricObject.getP2());
			}
		});
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForCurve(FGECubicCurve curve) {
		Vector<ControlPoint> returned = new Vector<>();
		returned.add(new GeometryAdjustingControlPoint<FGECubicCurve>(this, "p1", curve.getP1()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGECubicCurve) getGeometricObject()).setP1(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGECubicCurve geometricObject) {
				setPoint(geometricObject.getP1());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGECubicCurve>(this, "cp1", curve.getCtrlP1()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGECubicCurve) getGeometricObject()).setCtrlP1(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGECubicCurve geometricObject) {
				setPoint(geometricObject.getCtrlP1());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGECubicCurve>(this, "cp2", curve.getCtrlP2()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGECubicCurve) getGeometricObject()).setCtrlP2(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGECubicCurve geometricObject) {
				setPoint(geometricObject.getCtrlP2());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGECubicCurve>(this, "p2", curve.getP2()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGECubicCurve) getGeometricObject()).setP2(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGECubicCurve geometricObject) {
				setPoint(geometricObject.getP2());
			}
		});
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForPolygon(FGEPolygon polygon) {
		Vector<ControlPoint> returned = new Vector<>();

		for (int i = 0; i < polygon.getPointsNb(); i++) {
			final int index = i;
			returned.add(new GeometryAdjustingControlPoint<FGEPolygon>(this, "p" + i, polygon.getPointAt(i)) {
				@Override
				public FGEArea getDraggingAuthorizedArea() {
					return new FGEPlane();
				}

				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					FGEPoint p = ((FGEPolygon) getGeometricObject()).getPointAt(index);
					p.x = newAbsolutePoint.x;
					p.y = newAbsolutePoint.y;
					setPoint(newAbsolutePoint);
					notifyGeometryChanged();
					return true;
				}

				@Override
				public void update(FGEPolygon geometricObject) {
					if (geometricObject == null) {
						return;
					}
					setPoint(geometricObject.getPointAt(index));
				}
			});
		}
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForPolylin(FGEPolylin polylin) {
		Vector<ControlPoint> returned = new Vector<>();

		for (int i = 0; i < polylin.getPointsNb(); i++) {
			final int index = i;
			returned.add(new GeometryAdjustingControlPoint<FGEPolylin>(this, "p" + i, polylin.getPointAt(i)) {
				@Override
				public FGEArea getDraggingAuthorizedArea() {
					return new FGEPlane();
				}

				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					FGEPoint p = ((FGEPolylin) getGeometricObject()).getPointAt(index);
					p.x = newAbsolutePoint.x;
					p.y = newAbsolutePoint.y;
					setPoint(newAbsolutePoint);
					notifyGeometryChanged();
					return true;
				}

				@Override
				public void update(FGEPolylin geometricObject) {
					setPoint(geometricObject.getPointAt(index));
				}
			});
		}
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForEllips(FGEEllips ellips) {
		Vector<ControlPoint> returned = new Vector<>();
		// TODO
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForRectangle(FGERectangle rectangle) {
		Vector<ControlPoint> returned = new Vector<>();
		returned.add(new GeometryAdjustingControlPoint<FGERectangle>(this, "northWest", rectangle.getNorthWestPt()) {
			private double initialWidth;
			private double initialHeight;

			@Override
			public void startDragging(DianaEditor<?> controller, FGEPoint startPoint) {
				initialWidth = ((FGERectangle) getGeometricObject()).width;
				initialHeight = ((FGERectangle) getGeometricObject()).height;
				setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(((FGERectangle) getGeometricObject()).getSouthEastPt(),
						CardinalQuadrant.NORTH_WEST));
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				FGEPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
				setPoint(pt);

				((FGERectangle) getGeometricObject()).x = pt.x;
				((FGERectangle) getGeometricObject()).y = pt.y;
				((FGERectangle) getGeometricObject()).width = -pt.x + initialPoint.x + initialWidth;
				((FGERectangle) getGeometricObject()).height = -pt.y + initialPoint.y + initialHeight;

				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGERectangle geometricObject) {
				setPoint(geometricObject.getNorthWestPt());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGERectangle>(this, "northEast", rectangle.getNorthEastPt()) {
			private double initialWidth;
			private double initialHeight;

			@Override
			public void startDragging(DianaEditor<?> controller, FGEPoint startPoint) {
				initialWidth = ((FGERectangle) getGeometricObject()).width;
				initialHeight = ((FGERectangle) getGeometricObject()).height;
				setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(((FGERectangle) getGeometricObject()).getSouthWestPt(),
						CardinalQuadrant.NORTH_EAST));
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				FGEPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
				setPoint(pt);

				((FGERectangle) getGeometricObject()).y = pt.y;
				((FGERectangle) getGeometricObject()).width = pt.x - initialPoint.x + initialWidth;
				((FGERectangle) getGeometricObject()).height = -pt.y + initialPoint.y + initialHeight;

				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGERectangle geometricObject) {
				setPoint(geometricObject.getNorthEastPt());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGERectangle>(this, "southWest", rectangle.getSouthWestPt()) {
			private double initialWidth;
			private double initialHeight;

			@Override
			public void startDragging(DianaEditor<?> controller, FGEPoint startPoint) {
				initialWidth = ((FGERectangle) getGeometricObject()).width;
				initialHeight = ((FGERectangle) getGeometricObject()).height;
				setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(((FGERectangle) getGeometricObject()).getNorthEastPt(),
						CardinalQuadrant.SOUTH_WEST));
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				FGEPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
				setPoint(pt);

				((FGERectangle) getGeometricObject()).x = pt.x;
				((FGERectangle) getGeometricObject()).width = -pt.x + initialPoint.x + initialWidth;
				((FGERectangle) getGeometricObject()).height = pt.y - initialPoint.y + initialHeight;

				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGERectangle geometricObject) {
				setPoint(geometricObject.getSouthWestPt());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGERectangle>(this, "southEast", rectangle.getSouthEastPt()) {
			private double initialWidth;
			private double initialHeight;

			@Override
			public void startDragging(DianaEditor<?> controller, FGEPoint startPoint) {
				initialWidth = ((FGERectangle) getGeometricObject()).width;
				initialHeight = ((FGERectangle) getGeometricObject()).height;
				setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(((FGERectangle) getGeometricObject()).getNorthWestPt(),
						CardinalQuadrant.SOUTH_EAST));
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				FGEPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
				setPoint(pt);

				((FGERectangle) getGeometricObject()).width = pt.x - initialPoint.x + initialWidth;
				((FGERectangle) getGeometricObject()).height = pt.y - initialPoint.y + initialHeight;

				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGERectangle geometricObject) {
				setPoint(geometricObject.getSouthEastPt());
			}
		});
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForGeneralShape(FGEGeneralShape<?> shape) {
		Vector<ControlPoint> returned = new Vector<>();

		returned.add(new GeometryAdjustingControlPoint<FGEGeneralShape<?>>(this, "p0", shape.getPathElements().firstElement().getP1()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				FGEPoint p = ((FGEGeneralShape<?>) getGeometricObject()).getPathElements().firstElement().getP1();
				p.x = newAbsolutePoint.x;
				p.y = newAbsolutePoint.y;
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGEGeneralShape<?> geometricObject) {
				if (geometricObject != null && geometricObject.getPathElements() != null && geometricObject.getPathElements().size() > 0) {
					setPoint(geometricObject.getPathElements().firstElement().getP1());
				}
			}
		});
		for (int i = 0; i < shape.getPathElements().size(); i++) {
			final int index = i;
			GeneralShapePathElement<?> element = shape.getPathElements().get(i);
			returned.add(new GeometryAdjustingControlPoint<FGEGeneralShape<?>>(this, "p" + i, element.getP2()) {
				@Override
				public FGEArea getDraggingAuthorizedArea() {
					return new FGEPlane();
				}

				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					FGEPoint p = ((FGEGeneralShape<?>) getGeometricObject()).getPathElements().get(index).getP2();
					p.x = newAbsolutePoint.x;
					p.y = newAbsolutePoint.y;
					((FGEGeneralShape<?>) getGeometricObject()).refresh();
					setPoint(newAbsolutePoint);
					notifyGeometryChanged();
					return true;
				}

				@Override
				public void update(FGEGeneralShape<?> geometricObject) {
					if (geometricObject != null && geometricObject.getPathElements() != null
							&& geometricObject.getPathElements().size() > 0) {
						setPoint(geometricObject.getPathElements().get(index).getP2());
					}
				}
			});

		}
		return returned;
	}

	private void updateControlAreas() {
		boolean caNeedsToBeRebuilt = false;
		for (ControlArea<?> ca : getControlAreas()) {
			if (ca instanceof GeometryAdjustingControlPoint) {
				((GeometryAdjustingControlPoint) ca).update(getGeometricObject());
			}
			else {
				caNeedsToBeRebuilt = true;
			}
		}
		if (caNeedsToBeRebuilt) {
			rebuildControlAreas();
		}
	}

	@Override
	public List<ControlPoint> makeDefaultControlPoints() {

		List<ControlPoint> returned = new ArrayList<>();

		/*if (controlPoints == null) {
			controlPoints = new Vector<>();
		}
		controlPoints.clear();
		
		if (getGeometricObject() == null) {
			return controlPoints;
		}*/

		if (getGeometricObject() instanceof FGEPoint) {
			returned.addAll(buildControlPointsForPoint((FGEPoint) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof FGEAbstractLine) {
			returned.addAll(buildControlPointsForLine((FGEAbstractLine) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof FGERectangle) {
			returned.addAll(buildControlPointsForRectangle((FGERectangle) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof FGERoundRectangle) {
			returned.addAll(buildControlPointsForRectangle(((FGERoundRectangle) getGeometricObject()).getBoundingBox()));
		}
		else if (getGeometricObject() instanceof FGEEllips) {
			returned.addAll(buildControlPointsForEllips((FGEEllips) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof FGEPolygon) {
			returned.addAll(buildControlPointsForPolygon((FGEPolygon) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof FGEPolylin) {
			returned.addAll(buildControlPointsForPolylin((FGEPolylin) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof FGEQuadCurve) {
			returned.addAll(buildControlPointsForCurve((FGEQuadCurve) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof FGECubicCurve) {
			returned.addAll(buildControlPointsForCurve((FGECubicCurve) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof FGEGeneralShape) {
			returned.addAll(buildControlPointsForGeneralShape((FGEGeneralShape) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof FGEGeneralShape) {
			returned.addAll(buildControlPointsForGeneralShape((FGEGeneralShape<?>) getGeometricObject()));
		}

		// controlPoints.addAll(index, c)

		// controlAreas = null;

		return returned;
	}

	@Override
	public void notifyGeometryChanged() {

		// System.out.println("notifyGeometryChanged()");

		updateControlAreas();
		notifyObservers(new GeometryModified());
		// Hack: for the inspector !!!
		if (getGeometricObject() instanceof FGEPoint) {
			notifyChange("drawable.x");
			notifyChange("drawable.y");
		}
		else if (getGeometricObject() instanceof FGELine) {
			notifyChange("drawable.x1");
			notifyChange("drawable.y1");
			notifyChange("drawable.x2");
			notifyChange("drawable.y2");
		}
		else if (getGeometricObject() instanceof FGERectangle) {
			notifyChange("drawable.x");
			notifyChange("drawable.y");
			notifyChange("drawable.width");
			notifyChange("drawable.height");
		}
		else if (getGeometricObject() instanceof FGECircle) {
			notifyChange("drawable.centerX");
			notifyChange("drawable.centerY");
			notifyChange("drawable.radius");
		}
		else if (getGeometricObject() instanceof FGEEllips) {
			notifyChange("drawable.x");
			notifyChange("drawable.y");
			notifyChange("drawable.width");
			notifyChange("drawable.height");
		}
	}

	// Hack: for the inspector !!!
	private void notifyChange(final String string) {
		// setChanged();
		// notifyObservers(new FGEAttributeNotification(string, null, null));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (temporaryIgnoredObservables.contains(evt.getSource())) {
			// System.out.println("IGORE NOTIFICATION " + notification);
			return;
		}

		super.propertyChange(evt);

		if (evt.getSource() == getGraphicalRepresentation()) {
			if (evt.getPropertyName().equals(GeometricGraphicalRepresentation.GEOMETRIC_OBJECT.getName())) {
				notifyGeometryChanged();
			}
		}

		if (evt.getSource() == getDrawable()) {
			if (evt.getPropertyName().equals(GeometryModified.EVENT_NAME)) {
				notifyGeometryChanged();
			}
		}

		if (evt.getSource() instanceof BackgroundStyle) {
			notifyAttributeChanged(GeometricGraphicalRepresentation.BACKGROUND, null, getGraphicalRepresentation().getBackground());
		}
		if (evt.getSource() instanceof ForegroundStyle) {
			notifyAttributeChanged(GeometricGraphicalRepresentation.FOREGROUND, null, getGraphicalRepresentation().getForeground());
		}

	}

	@Override
	public boolean hasContainedLabel() {
		return false;
	}

	@Override
	public boolean hasFloatingLabel() {
		return hasText();
	}

	@Override
	public FGEDimension getRequiredLabelSize() {
		return null;
	}

	@Override
	public ForegroundStyle getForegroundStyle() {
		return getPropertyValue(ShapeGraphicalRepresentation.FOREGROUND);
	}

	@Override
	public void setForegroundStyle(ForegroundStyle aValue) {
		setPropertyValue(ShapeGraphicalRepresentation.FOREGROUND, aValue);
	}

	/**
	 * Convenient method used to retrieve geometric object
	 */
	public FGEArea getGeometricObject() {
		return getPropertyValue(GeometricGraphicalRepresentation.GEOMETRIC_OBJECT);
	}

	/**
	 * Convenient method used to set geometric object
	 */
	public void setGeometricObject(FGEArea geometricObject) {
		setPropertyValue(GeometricGraphicalRepresentation.GEOMETRIC_OBJECT, geometricObject);
	}

}
