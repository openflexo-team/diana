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

package org.openflexo.diana.impl;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openflexo.diana.BackgroundStyle;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.GRBinding;
import org.openflexo.diana.GeometricGraphicalRepresentation;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.control.DianaEditor;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.cp.GeometryAdjustingControlPoint;
import org.openflexo.diana.geom.DianaAbstractLine;
import org.openflexo.diana.geom.DianaCircle;
import org.openflexo.diana.geom.DianaCubicCurve;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaEllips;
import org.openflexo.diana.geom.DianaGeneralShape;
import org.openflexo.diana.geom.DianaGeneralShape.GeneralShapePathElement;
import org.openflexo.diana.geom.DianaGeometricObject.CardinalQuadrant;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolygon;
import org.openflexo.diana.geom.DianaPolylin;
import org.openflexo.diana.geom.DianaQuadCurve;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaRoundRectangle;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaPlane;
import org.openflexo.diana.geom.area.DianaQuarterPlane;
import org.openflexo.diana.graphics.DianaGeometricGraphics;
import org.openflexo.diana.notifications.GeometryModified;
import org.openflexo.diana.notifications.RebuildControlPoints;

public class GeometricNodeImpl<O> extends DrawingTreeNodeImpl<O, GeometricGraphicalRepresentation> implements GeometricNode<O> {

	// TODO: change to protected
	public GeometricNodeImpl(DrawingImpl<?> drawingImpl, O drawable, GRBinding<O, GeometricGraphicalRepresentation> grBinding,
			ContainerNodeImpl<?, ?> parentNode) {
		super(drawingImpl, drawable, grBinding, parentNode);
		// graphics = new DianaGeometricGraphicsImpl(this);
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
	public DianaRectangle getNormalizedBounds() {
		Rectangle bounds = getBounds(1);
		return new DianaRectangle(0, 0, bounds.width, bounds.height, Filling.FILLED);
	}

	@Override
	public boolean isContainedInSelection(Rectangle drawingViewSelection, double scale) {
		// Unused DianaRectangle drawingViewBounds =
		new DianaRectangle(drawingViewSelection.getX(), drawingViewSelection.getY(), drawingViewSelection.getWidth(),
				drawingViewSelection.getHeight(), Filling.FILLED);
		boolean isFullyContained = true;
		/*	for (ControlPoint cp : getConnector().getControlPoints()) {
			Point cpInContainerView = convertLocalNormalizedPointToRemoteViewCoordinates(
					cp.getPoint(), 
					getDrawingGraphicalRepresentation(), 
					scale);
			DianaPoint preciseCPInContainerView = new DianaPoint(cpInContainerView.x,cpInContainerView.y);
			if (!drawingViewBounds.containsPoint(preciseCPInContainerView)) {
				//System.out.println("Going outside: point="+preciseCPInContainerView+" bounds="+containerViewBounds);
				isFullyContained = false;
			}
		}*/
		return isFullyContained;
	}

	@Override
	public void paint(DianaGeometricGraphics g) {
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

		g.setDefaultBackground(getBackgroundStyle());
		g.setDefaultForeground(getForegroundStyle());
		g.setDefaultTextStyle(getTextStyle());

		paintGeometricObject(g);

		if (getIsSelected() || getIsFocused()) {
			ForegroundStyle style = (ForegroundStyle) getForegroundStyle().clone();
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

		if (getIsSelected() || getIsFocused()) {
			// Unused Color color = null;
			// Unused if (getIsSelected()) {
			// Unused color = getDrawing().getRoot().getSelectionColor();
			// Unused }
			// Unused else if (getIsFocused()) {
			// Unused color = getDrawing().getRoot().getFocusColor();
			// Unused }

			// getControlAreas();

			for (ControlArea<?> ca : getControlAreas()) {
				ca.paint(g);
			}

			/*for (ControlPoint cp : getControlPoints()) {
				cp.paint(g);
			}*/
		}

		/*System.out.println("On peint " + this.getDrawable());
		
		if (hasFloatingLabel()) {
			g.useTextStyle(getGraphicalRepresentation().getTextStyle());
			DianaPoint pt = new DianaPoint(getLabelRelativePosition().x + getGraphicalRepresentation().getAbsoluteTextX(),
					getLabelRelativePosition().y + getGraphicalRepresentation().getAbsoluteTextY());
			System.out.println("Label: " + getText() + " en " + pt);
			System.out.println("x=" + getGraphicalRepresentation().getAbsoluteTextX());
			System.out.println("y=" + getGraphicalRepresentation().getAbsoluteTextY());
			g.drawString(getText(), pt, getGraphicalRepresentation().getHorizontalTextAlignment());
		}*/

		// g.releaseGraphics();
	}

	@Override
	public void paintGeometricObject(DianaGeometricGraphics graphics) {
		if (getGeometricObject() != null) {
			getGeometricObject().paint(graphics);
		}
	}

	protected DianaPoint getLabelRelativePosition() {
		if (getGeometricObject() instanceof DianaPoint) {
			return ((DianaPoint) getGeometricObject()).transform(AffineTransform.getTranslateInstance(0, -13));
		}
		else if (getGeometricObject() instanceof DianaAbstractLine) {
			DianaAbstractLine<?> line = (DianaAbstractLine<?>) getGeometricObject();
			return new DianaSegment(line.getP1(), line.getP2()).getMiddle();
		}
		else if (getGeometricObject() instanceof DianaShape) {
			DianaRectangle boundingBox = ((DianaShape<?>) getGeometricObject()).getBoundingBox();
			return new DianaPoint(boundingBox.getSouth().getMiddle().transform(AffineTransform.getTranslateInstance(0, 10)));
		}
		return new DianaPoint(0, 0);
	}

	protected List<ControlPoint> buildControlPointsForPoint(DianaPoint point) {
		Vector<ControlPoint> returned = new Vector<>();
		returned.add(new GeometryAdjustingControlPoint<DianaPoint>(this, "point", point.clone()) {
			@Override
			public DianaArea getDraggingAuthorizedArea() {
				return new DianaPlane();
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				((DianaPoint) getGeometricObject()).x = newAbsolutePoint.x;
				((DianaPoint) getGeometricObject()).y = newAbsolutePoint.y;
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(DianaPoint geometricObject) {
				setPoint(geometricObject);
			}
		});
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForLine(DianaAbstractLine<?> line) {
		Vector<ControlPoint> returned = new Vector<>();
		returned.add(new GeometryAdjustingControlPoint<DianaLine>(this, "p1", line.getP1()) {
			@Override
			public DianaArea getDraggingAuthorizedArea() {
				return new DianaPlane();
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				((DianaLine) getGeometricObject()).setP1(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(DianaLine geometricObject) {
				setPoint(geometricObject.getP1());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<DianaLine>(this, "p2", line.getP2()) {
			@Override
			public DianaArea getDraggingAuthorizedArea() {
				return new DianaPlane();
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				((DianaLine) getGeometricObject()).setP2(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(DianaLine geometricObject) {
				setPoint(geometricObject.getP2());
			}
		});
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForCurve(DianaQuadCurve curve) {
		Vector<ControlPoint> returned = new Vector<>();
		returned.add(new GeometryAdjustingControlPoint<DianaQuadCurve>(this, "p1", curve.getP1()) {
			@Override
			public DianaArea getDraggingAuthorizedArea() {
				return new DianaPlane();
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				((DianaQuadCurve) getGeometricObject()).setP1(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(DianaQuadCurve geometricObject) {
				setPoint(geometricObject.getP1());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<DianaQuadCurve>(this, "cp", curve.getCtrlPoint()) {
			@Override
			public DianaArea getDraggingAuthorizedArea() {
				return new DianaPlane();
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				((DianaQuadCurve) getGeometricObject()).setCtrlPoint(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(DianaQuadCurve geometricObject) {
				setPoint(geometricObject.getCtrlPoint());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<DianaQuadCurve>(this, "p3", curve.getP3()) {
			@Override
			public DianaArea getDraggingAuthorizedArea() {
				return new DianaPlane();
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				((DianaQuadCurve) getGeometricObject()).setP3(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(DianaQuadCurve geometricObject) {
				setPoint(geometricObject.getP3());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<DianaQuadCurve>(this, "p2", curve.getP2()) {
			@Override
			public DianaArea getDraggingAuthorizedArea() {
				return new DianaPlane();
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				((DianaQuadCurve) getGeometricObject()).setP2(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(DianaQuadCurve geometricObject) {
				setPoint(geometricObject.getP2());
			}
		});
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForCurve(DianaCubicCurve curve) {
		Vector<ControlPoint> returned = new Vector<>();
		returned.add(new GeometryAdjustingControlPoint<DianaCubicCurve>(this, "p1", curve.getP1()) {
			@Override
			public DianaArea getDraggingAuthorizedArea() {
				return new DianaPlane();
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				((DianaCubicCurve) getGeometricObject()).setP1(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(DianaCubicCurve geometricObject) {
				setPoint(geometricObject.getP1());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<DianaCubicCurve>(this, "cp1", curve.getCtrlP1()) {
			@Override
			public DianaArea getDraggingAuthorizedArea() {
				return new DianaPlane();
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				((DianaCubicCurve) getGeometricObject()).setCtrlP1(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(DianaCubicCurve geometricObject) {
				setPoint(geometricObject.getCtrlP1());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<DianaCubicCurve>(this, "cp2", curve.getCtrlP2()) {
			@Override
			public DianaArea getDraggingAuthorizedArea() {
				return new DianaPlane();
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				((DianaCubicCurve) getGeometricObject()).setCtrlP2(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(DianaCubicCurve geometricObject) {
				setPoint(geometricObject.getCtrlP2());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<DianaCubicCurve>(this, "p2", curve.getP2()) {
			@Override
			public DianaArea getDraggingAuthorizedArea() {
				return new DianaPlane();
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				((DianaCubicCurve) getGeometricObject()).setP2(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(DianaCubicCurve geometricObject) {
				setPoint(geometricObject.getP2());
			}
		});
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForPolygon(DianaPolygon polygon) {
		Vector<ControlPoint> returned = new Vector<>();

		for (int i = 0; i < polygon.getPointsNb(); i++) {
			final int index = i;
			returned.add(new GeometryAdjustingControlPoint<DianaPolygon>(this, "p" + i, polygon.getPointAt(i)) {
				@Override
				public DianaArea getDraggingAuthorizedArea() {
					return new DianaPlane();
				}

				@Override
				public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
						DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
					DianaPoint p = ((DianaPolygon) getGeometricObject()).getPointAt(index);
					p.x = newAbsolutePoint.x;
					p.y = newAbsolutePoint.y;
					setPoint(newAbsolutePoint);
					notifyGeometryChanged();
					return true;
				}

				@Override
				public void update(DianaPolygon geometricObject) {
					if (geometricObject == null) {
						return;
					}
					setPoint(geometricObject.getPointAt(index));
				}
			});
		}
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForPolylin(DianaPolylin polylin) {
		Vector<ControlPoint> returned = new Vector<>();

		for (int i = 0; i < polylin.getPointsNb(); i++) {
			final int index = i;
			returned.add(new GeometryAdjustingControlPoint<DianaPolylin>(this, "p" + i, polylin.getPointAt(i)) {
				@Override
				public DianaArea getDraggingAuthorizedArea() {
					return new DianaPlane();
				}

				@Override
				public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
						DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
					DianaPoint p = ((DianaPolylin) getGeometricObject()).getPointAt(index);
					p.x = newAbsolutePoint.x;
					p.y = newAbsolutePoint.y;
					setPoint(newAbsolutePoint);
					notifyGeometryChanged();
					return true;
				}

				@Override
				public void update(DianaPolylin geometricObject) {
					setPoint(geometricObject.getPointAt(index));
				}
			});
		}
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForEllips(DianaEllips ellips) {
		Vector<ControlPoint> returned = new Vector<>();
		// TODO
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForRectangle(DianaRectangle rectangle) {
		Vector<ControlPoint> returned = new Vector<>();
		returned.add(new GeometryAdjustingControlPoint<DianaRectangle>(this, "northWest", rectangle.getNorthWestPt()) {
			private double initialWidth;
			private double initialHeight;

			@Override
			public void startDragging(DianaEditor<?> controller, DianaPoint startPoint) {
				initialWidth = ((DianaRectangle) getGeometricObject()).width;
				initialHeight = ((DianaRectangle) getGeometricObject()).height;
				setDraggingAuthorizedArea(DianaQuarterPlane.makeDianaQuarterPlane(((DianaRectangle) getGeometricObject()).getSouthEastPt(),
						CardinalQuadrant.NORTH_WEST));
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				DianaPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
				setPoint(pt);

				((DianaRectangle) getGeometricObject()).x = pt.x;
				((DianaRectangle) getGeometricObject()).y = pt.y;
				((DianaRectangle) getGeometricObject()).width = -pt.x + initialPoint.x + initialWidth;
				((DianaRectangle) getGeometricObject()).height = -pt.y + initialPoint.y + initialHeight;

				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(DianaRectangle geometricObject) {
				setPoint(geometricObject.getNorthWestPt());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<DianaRectangle>(this, "northEast", rectangle.getNorthEastPt()) {
			private double initialWidth;
			private double initialHeight;

			@Override
			public void startDragging(DianaEditor<?> controller, DianaPoint startPoint) {
				initialWidth = ((DianaRectangle) getGeometricObject()).width;
				initialHeight = ((DianaRectangle) getGeometricObject()).height;
				setDraggingAuthorizedArea(DianaQuarterPlane.makeDianaQuarterPlane(((DianaRectangle) getGeometricObject()).getSouthWestPt(),
						CardinalQuadrant.NORTH_EAST));
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				DianaPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
				setPoint(pt);

				((DianaRectangle) getGeometricObject()).y = pt.y;
				((DianaRectangle) getGeometricObject()).width = pt.x - initialPoint.x + initialWidth;
				((DianaRectangle) getGeometricObject()).height = -pt.y + initialPoint.y + initialHeight;

				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(DianaRectangle geometricObject) {
				setPoint(geometricObject.getNorthEastPt());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<DianaRectangle>(this, "southWest", rectangle.getSouthWestPt()) {
			private double initialWidth;
			private double initialHeight;

			@Override
			public void startDragging(DianaEditor<?> controller, DianaPoint startPoint) {
				initialWidth = ((DianaRectangle) getGeometricObject()).width;
				initialHeight = ((DianaRectangle) getGeometricObject()).height;
				setDraggingAuthorizedArea(DianaQuarterPlane.makeDianaQuarterPlane(((DianaRectangle) getGeometricObject()).getNorthEastPt(),
						CardinalQuadrant.SOUTH_WEST));
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				DianaPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
				setPoint(pt);

				((DianaRectangle) getGeometricObject()).x = pt.x;
				((DianaRectangle) getGeometricObject()).width = -pt.x + initialPoint.x + initialWidth;
				((DianaRectangle) getGeometricObject()).height = pt.y - initialPoint.y + initialHeight;

				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(DianaRectangle geometricObject) {
				setPoint(geometricObject.getSouthWestPt());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<DianaRectangle>(this, "southEast", rectangle.getSouthEastPt()) {
			private double initialWidth;
			private double initialHeight;

			@Override
			public void startDragging(DianaEditor<?> controller, DianaPoint startPoint) {
				initialWidth = ((DianaRectangle) getGeometricObject()).width;
				initialHeight = ((DianaRectangle) getGeometricObject()).height;
				setDraggingAuthorizedArea(DianaQuarterPlane.makeDianaQuarterPlane(((DianaRectangle) getGeometricObject()).getNorthWestPt(),
						CardinalQuadrant.SOUTH_EAST));
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				DianaPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
				setPoint(pt);

				((DianaRectangle) getGeometricObject()).width = pt.x - initialPoint.x + initialWidth;
				((DianaRectangle) getGeometricObject()).height = pt.y - initialPoint.y + initialHeight;

				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(DianaRectangle geometricObject) {
				setPoint(geometricObject.getSouthEastPt());
			}
		});
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForGeneralShape(DianaGeneralShape<?> shape) {
		Vector<ControlPoint> returned = new Vector<>();

		returned.add(new GeometryAdjustingControlPoint<DianaGeneralShape<?>>(this, "p0", shape.getPathElements().firstElement().getP1()) {
			@Override
			public DianaArea getDraggingAuthorizedArea() {
				return new DianaPlane();
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				DianaPoint p = ((DianaGeneralShape<?>) getGeometricObject()).getPathElements().firstElement().getP1();
				p.x = newAbsolutePoint.x;
				p.y = newAbsolutePoint.y;
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(DianaGeneralShape<?> geometricObject) {
				if (geometricObject != null && geometricObject.getPathElements() != null && geometricObject.getPathElements().size() > 0) {
					setPoint(geometricObject.getPathElements().firstElement().getP1());
				}
			}
		});
		for (int i = 0; i < shape.getPathElements().size(); i++) {
			final int index = i;
			GeneralShapePathElement<?> element = shape.getPathElements().get(i);
			returned.add(new GeometryAdjustingControlPoint<DianaGeneralShape<?>>(this, "p" + i, element.getP2()) {
				@Override
				public DianaArea getDraggingAuthorizedArea() {
					return new DianaPlane();
				}

				@Override
				public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
						DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
					DianaPoint p = ((DianaGeneralShape<?>) getGeometricObject()).getPathElements().get(index).getP2();
					p.x = newAbsolutePoint.x;
					p.y = newAbsolutePoint.y;
					((DianaGeneralShape<?>) getGeometricObject()).refresh();
					setPoint(newAbsolutePoint);
					notifyGeometryChanged();
					return true;
				}

				@Override
				public void update(DianaGeneralShape<?> geometricObject) {
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

		if (getGeometricObject() instanceof DianaPoint) {
			returned.addAll(buildControlPointsForPoint((DianaPoint) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof DianaAbstractLine) {
			returned.addAll(buildControlPointsForLine((DianaAbstractLine<?>) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof DianaRectangle) {
			returned.addAll(buildControlPointsForRectangle((DianaRectangle) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof DianaRoundRectangle) {
			returned.addAll(buildControlPointsForRectangle(((DianaRoundRectangle) getGeometricObject()).getBoundingBox()));
		}
		else if (getGeometricObject() instanceof DianaEllips) {
			returned.addAll(buildControlPointsForEllips((DianaEllips) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof DianaPolygon) {
			returned.addAll(buildControlPointsForPolygon((DianaPolygon) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof DianaPolylin) {
			returned.addAll(buildControlPointsForPolylin((DianaPolylin) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof DianaQuadCurve) {
			returned.addAll(buildControlPointsForCurve((DianaQuadCurve) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof DianaCubicCurve) {
			returned.addAll(buildControlPointsForCurve((DianaCubicCurve) getGeometricObject()));
		}
		else if (getGeometricObject() instanceof DianaGeneralShape) {
			returned.addAll(buildControlPointsForGeneralShape((DianaGeneralShape<?>) getGeometricObject()));
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
		if (getGeometricObject() instanceof DianaPoint) {
			notifyChange("drawable.x");
			notifyChange("drawable.y");
		}
		else if (getGeometricObject() instanceof DianaLine) {
			notifyChange("drawable.x1");
			notifyChange("drawable.y1");
			notifyChange("drawable.x2");
			notifyChange("drawable.y2");
		}
		else if (getGeometricObject() instanceof DianaRectangle) {
			notifyChange("drawable.x");
			notifyChange("drawable.y");
			notifyChange("drawable.width");
			notifyChange("drawable.height");
		}
		else if (getGeometricObject() instanceof DianaCircle) {
			notifyChange("drawable.centerX");
			notifyChange("drawable.centerY");
			notifyChange("drawable.radius");
		}
		else if (getGeometricObject() instanceof DianaEllips) {
			notifyChange("drawable.x");
			notifyChange("drawable.y");
			notifyChange("drawable.width");
			notifyChange("drawable.height");
		}

		getPropertyChangeSupport().firePropertyChange(GraphicalRepresentation.ABSOLUTE_TEXT_X_KEY, (Double) null,
				getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X));
		getPropertyChangeSupport().firePropertyChange(GraphicalRepresentation.ABSOLUTE_TEXT_Y_KEY, (Double) null,
				getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y));

	}

	// Hack: for the inspector !!!
	private void notifyChange(final String string) {
		// setChanged();
		// notifyObservers(new DianaAttributeNotification(string, null, null));
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
				// rebuildControlAreas();
			}
		}

		if (evt.getSource() == getDrawable()) {
			if (evt.getPropertyName().equals(GeometryModified.EVENT_NAME)) {
				notifyGeometryChanged();
				// rebuildControlAreas();
			}
		}

		if (evt.getSource() == getDrawable()) {
			if (evt.getPropertyName().equals(RebuildControlPoints.EVENT_NAME)) {
				rebuildControlAreas();
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
	public DianaDimension getRequiredLabelSize() {
		return null;
	}

	/**
	 * Convenient method used to retrieve geometric object
	 */
	public DianaArea getGeometricObject() {
		return getPropertyValue(GeometricGraphicalRepresentation.GEOMETRIC_OBJECT);
	}

	/**
	 * Convenient method used to set geometric object
	 */
	public void setGeometricObject(DianaArea geometricObject) {
		setPropertyValue(GeometricGraphicalRepresentation.GEOMETRIC_OBJECT, geometricObject);
	}

	/**
	 * Convenient method used to retrieve background style property value
	 */
	@Override
	public BackgroundStyle getBackgroundStyle() {
		return getPropertyValue(GeometricGraphicalRepresentation.BACKGROUND);
	}

	/**
	 * Convenient method used to set background style property value
	 */
	@Override
	public void setBackgroundStyle(BackgroundStyle style) {
		if (hasDynamicSettablePropertyValue(GeometricGraphicalRepresentation.BACKGROUND)) {
			try {
				setDynamicPropertyValue(GeometricGraphicalRepresentation.BACKGROUND, style);
				return;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		setPropertyValue(GeometricGraphicalRepresentation.BACKGROUND, style);
	}

	/**
	 * Convenient method used to retrieve foreground style property value
	 */
	@Override
	public ForegroundStyle getForegroundStyle() {
		return getPropertyValue(GeometricGraphicalRepresentation.FOREGROUND);
	}

	/**
	 * Convenient method used to set foreground style property value
	 */
	@Override
	public void setForegroundStyle(ForegroundStyle aValue) {
		setPropertyValue(GeometricGraphicalRepresentation.FOREGROUND, aValue);
	}

	@Override
	public Point getLabelLocation(double scale) {
		Point point;
		point = new Point(
				(int) (getLabelRelativePosition().x + getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X) * scale + getViewX(scale)),
				(int) (getLabelRelativePosition().y + getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y) * scale + getViewY(scale)));
		Dimension d = getLabelDimension(scale);
		if (getHorizontalTextAlignment() != null) {
			switch (getHorizontalTextAlignment()) {
				case CENTER:
				case RELATIVE:
					point.x -= d.width / 2;
					break;
				case LEFT:
					point.x = (point.x);
					break;
				case RIGHT:
					point.x = (point.x) - d.width;
					break;

			}
		}
		if (getVerticalTextAlignment() != null) {
			switch (getVerticalTextAlignment()) {
				case BOTTOM:
					point.y = (point.y) - d.height;
					// point.y -= d.height;
					break;
				case MIDDLE:
				case RELATIVE:
					point.y -= d.height / 2;
					break;
				case TOP:
					point.y = (point.y);
					break;
			}
		}

		return point;
	}

	/*@Override
	public void setLabelLocation(Point point, double scale) {
		setPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X, (point.x - getViewX(scale)) / scale - getLabelRelativePosition().x);
		setPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y, (point.y - getViewY(scale)) / scale - getLabelRelativePosition().y);
		// System.out.println("Moved, x=" + getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X) + " y="
		// + getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y));
	}*/

	@Override
	public void setLabelLocation(Point point, double scale) {

		Double oldAbsoluteTextX = getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X);
		Double oldAbsoluteTextY = getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y);
		Dimension d = getLabelDimension(scale);
		switch (getHorizontalTextAlignment()) {
			case CENTER:
				point.x += d.width / 2;
				break;
			case LEFT:
				break;
			case RIGHT:
				point.x += d.width;
				break;

		}
		switch (getVerticalTextAlignment()) {
			case BOTTOM:
				point.y += d.height;
				break;
			case MIDDLE:
				point.y += d.height / 2;
				break;
			case TOP:
				break;
		}
		DianaPoint p = new DianaPoint((point.x - getViewX(scale)) / scale - getLabelRelativePosition().x,
				(point.y - getViewY(scale)) / scale - getLabelRelativePosition().y);
		setPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X, p.x);
		setPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y, p.y);
		notifyAttributeChanged(GraphicalRepresentation.ABSOLUTE_TEXT_X, oldAbsoluteTextX,
				getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X));
		notifyAttributeChanged(GraphicalRepresentation.ABSOLUTE_TEXT_Y, oldAbsoluteTextY,
				getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y));
	}

}
