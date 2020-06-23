/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.diana.connectors.impl;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.connectors.ConnectorSpecification;
import org.openflexo.diana.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.diana.connectors.RectPolylinConnectorSpecification;
import org.openflexo.diana.connectors.RectPolylinConnectorSpecification.RectPolylinAdjustability;
import org.openflexo.diana.connectors.RectPolylinConnectorSpecification.RectPolylinConstraints;
import org.openflexo.diana.connectors.rpc.AdjustableEndControlPoint;
import org.openflexo.diana.connectors.rpc.AdjustableFirstControlPoint;
import org.openflexo.diana.connectors.rpc.AdjustableFirstSegment;
import org.openflexo.diana.connectors.rpc.AdjustableIntermediateControlPoint;
import org.openflexo.diana.connectors.rpc.AdjustableIntermediateSegment;
import org.openflexo.diana.connectors.rpc.AdjustableLastControlPoint;
import org.openflexo.diana.connectors.rpc.AdjustableLastSegment;
import org.openflexo.diana.connectors.rpc.AdjustableMiddleControlPoint;
import org.openflexo.diana.connectors.rpc.AdjustableStartControlPoint;
import org.openflexo.diana.connectors.rpc.AdjustableUniqueSegment;
import org.openflexo.diana.connectors.rpc.RectPolylinAdjustingArea;
import org.openflexo.diana.cp.ConnectorAdjustingControlPoint;
import org.openflexo.diana.cp.ConnectorNonAdjustableControlPoint;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaArc;
import org.openflexo.diana.geom.DianaGeometricObject;
import org.openflexo.diana.geom.DianaGeometricObject.CardinalQuadrant;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolylin;
import org.openflexo.diana.geom.DianaRectPolylin;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.area.DefaultAreaProvider;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaAreaProvider;
import org.openflexo.diana.geom.area.DianaUnionArea;
import org.openflexo.diana.graphics.DianaConnectorGraphics;
import org.openflexo.toolbox.ConcatenedList;

public class RectPolylinConnector extends ConnectorImpl<RectPolylinConnectorSpecification> {

	static final Logger LOGGER = Logger.getLogger(RectPolylinConnectorSpecification.class.getPackage().getName());

	private DianaRectPolylin polylin;
	private final Vector<DianaRectPolylin> potentialPolylin;
	// public DianaRectPolylin debugPolylin;

	private ControlPoint p_start;
	private ControlPoint p_end;
	// private ControlPoint crossedControlPoint;

	private Vector<ControlPoint> controlPoints;
	private final Vector<ControlArea<?>> controlAreas;

	private boolean firstUpdated = false;

	private boolean wasManuallyAdjusted = false;

	private DianaRectPolylin polylinRelativeToStartObject;
	private DianaRectPolylin polylinRelativeToEndObject;

	private DianaRectPolylin lastKnownCleanPolylinBeforeConnectorRestructuration;
	private boolean isCleaningPolylin = false;

	private DianaPoint fixedStartLocationRelativeToStartObject;
	private DianaPoint fixedEndLocationRelativeToEndObject;
	// private DianaPoint _crossedPoint;

	private DianaRectPolylin _deserializedPolylin;

	private ReflexiveConnectorDelegate reflexiveConnectorDelegate;

	// private static final DianaModelFactory DEBUG_FACTORY = DianaCoreUtils.TOOLS_FACTORY;
	// private static final ForegroundStyle DEBUG_GRAY_STROKE = DEBUG_FACTORY.makeForegroundStyle(Color.GRAY, 1.0f, DashStyle.SMALL_DASHES);
	// private static final ForegroundStyle DEBUG_BLACK_STROKE = DEBUG_FACTORY.makeForegroundStyle(Color.BLACK, 3.0f,
	// DashStyle.PLAIN_STROKE);

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	public RectPolylinConnector(ConnectorNode<?> connectorNode) {
		super(connectorNode);
		controlPoints = new Vector<>();
		controlAreas = new Vector<>();
		potentialPolylin = new Vector<>();
	}

	@Override
	public void delete() {
		super.delete();
		controlPoints.clear();
		controlPoints = null;
	}

	protected ReflexiveConnectorDelegate getReflexiveConnectorDelegate() {
		if (getStartNode() == getEndNode()) {
			if (reflexiveConnectorDelegate == null) {
				reflexiveConnectorDelegate = new ReflexiveConnectorDelegate(getConnectorNode());
			}
			return reflexiveConnectorDelegate;
		}
		return null;
	}

	public List<ControlPoint> getControlPoints() {
		// TODO: perfs issue : do not update all the time !!!
		// updateLayout();
		return controlPoints;
	}

	@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		if (getMiddleSymbol() == MiddleSymbolType.NONE && controlAreas.size() == 0) {
			return controlPoints;
		}

		// Otherwise, we have to manage a concatenation
		if (allControlAreas == null) {
			allControlAreas = new ConcatenedList<>();
			allControlAreas.addElementList(controlPoints);
			if (getMiddleSymbol() != MiddleSymbolType.NONE && middleSymbolLocationControlPoint != null) {
				allControlAreas.add(0, middleSymbolLocationControlPoint);
			}
			allControlAreas.addElementList(controlAreas);
		}
		return allControlAreas;
	}

	private ConcatenedList<ControlArea<?>> allControlAreas;

	@Override
	public void drawConnector(DianaConnectorGraphics g) {
		if (!firstUpdated) {
			refreshConnector();
		}

		/*
		 * if (debugPolylin != null) { g.setDefaultForeground(ForegroundStyle.makeStyle(Color.PINK, 1.0f, DashStyle.SMALL_DASHES));
		 * debugPolylin.paint(g); }
		 */

		/*if (getDebug()) {
			g.setDefaultForeground(DEBUG_GRAY_STROKE);
			for (DianaRectPolylin p : potentialPolylin) {
				p.paint(g);
			}
			g.setDefaultForeground(DEBUG_BLACK_STROKE);
			if (polylin != null) {
				polylin.debugPaint(g);
			}
		} else {*/
		g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getForeground());

		if (connectorNode.getStartNode() == connectorNode.getEndNode()) {
			getReflexiveConnectorDelegate().drawConnector(g);
		}

		else {
			if (polylin != null) {

				if (getIsRounded()) {
					polylin.paintWithRounds(g, getArcSize());
				}
				else {
					polylin.paint(g);
				}
			}
			// }

			/*
			 * if (debugPolylin != null) { g.setDefaultForeground(ForegroundStyle.makeStyle(Color.RED, 1.0f, DashStyle.PLAIN_STROKE));
			 * debugPolylin.paint(g); }
			 */

			// Draw eventual symbols
			if (polylin != null && polylin.getSegments() != null && polylin.getSegments().size() > 0) {
				// Segments are here all orthogonal, we can can then rely on getAngle() computation performed on geom layer
				// (we dont need to convert to view first)
				if (getStartSymbol() != StartSymbolType.NONE) {
					DianaSegment firstSegment = polylin.getSegments().firstElement();
					if (firstSegment != null) {
						g.drawSymbol(firstSegment.getP1(), getStartSymbol(), getStartSymbolSize(), firstSegment.getAngle());
					}
				}
				if (getEndSymbol() != EndSymbolType.NONE) {
					DianaSegment lastSegment = polylin.getSegments().lastElement();
					if (lastSegment != null) {
						g.drawSymbol(lastSegment.getP2(), getEndSymbol(), getEndSymbolSize(), lastSegment.getAngle() + Math.PI);
					}
				}
				if (getMiddleSymbol() != MiddleSymbolType.NONE) {
					g.drawSymbol(getMiddleSymbolLocation(), getMiddleSymbol(), getMiddleSymbolSize(), getMiddleSymbolAngle());
				}
			}
		}
	}

	@Override
	public DianaPoint getMiddleSymbolLocation() {
		if (connectorNode.getStartNode() == connectorNode.getEndNode()) {
			return getReflexiveConnectorDelegate().getMiddleSymbolLocation();
		}

		if (polylin == null) {
			return new DianaPoint(0, 0);
		}

		AffineTransform at = connectorNode.convertNormalizedPointToViewCoordinatesAT(1.0);

		DianaRectPolylin transformedPolylin = polylin.transform(at);
		DianaPoint point = transformedPolylin.getPointAtRelativePosition(getRelativeMiddleSymbolLocation());
		try {
			point = point.transform(at.createInverse());
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		if (!getIsRounded()) {
			return point;
		}
		UnnormalizedArcSize arcSize = computeUnnormalizedArcSize();
		DianaPoint returned = polylin.getNearestPointLocatedOnRoundedRepresentation(point, arcSize.arcWidth, arcSize.arcHeight);
		if (returned == null) {
			return new DianaPoint(0, 0);
		}
		return returned;
	}

	@Override
	public DianaPoint getLabelLocation() {
		if (connectorNode.getStartNode() == connectorNode.getEndNode()) {
			return getReflexiveConnectorDelegate().getReflexiveConnectorControlPoint().getPoint();
		}

		if (polylin == null) {
			return new DianaPoint(0, 0);
		}

		AffineTransform at = connectorNode.convertNormalizedPointToViewCoordinatesAT(1.0);

		DianaRectPolylin transformedPolylin = polylin.transform(at);
		DianaPoint point = transformedPolylin.getPointAtRelativePosition(getRelativeLabelLocation());
		try {
			point = point.transform(at.createInverse());
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		if (!getIsRounded()) {
			return point;
		}
		UnnormalizedArcSize arcSize = computeUnnormalizedArcSize();
		DianaPoint returned = polylin.getNearestPointLocatedOnRoundedRepresentation(point, arcSize.arcWidth, arcSize.arcHeight);
		if (returned == null) {
			return new DianaPoint(0, 0);
		}
		return returned;
	}

	/**
	 * 
	 * @return angle expressed in radians
	 */
	public double getMiddleSymbolAngle() {
		if (polylin == null) {
			return 0;
		}

		DianaPoint middleSymbolLocation = getMiddleSymbolLocation();
		DianaSegment relatedSegment = polylin.getNearestSegment(middleSymbolLocation);

		if (relatedSegment == null) {
			return 0;
		}
		if (!getIsRounded()) {
			return relatedSegment.getAngle() + Math.PI;
		}
		UnnormalizedArcSize arcSize = computeUnnormalizedArcSize();
		DianaArc arc = polylin.getArcForNearestPointLocatedOnRoundedRepresentation(middleSymbolLocation, arcSize.arcWidth,
				arcSize.arcHeight);
		if (arc != null) {
			double angle = arc.angleForPoint(middleSymbolLocation) + Math.PI / 2;
			if (arc.isClockWise()) {
				angle += Math.PI;
			}
			return angle;
		}
		return relatedSegment.getAngle() + Math.PI;
	}

	@Override
	public void refreshConnector(boolean force) {
		if (!force && !needsRefresh()) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Skipping refreshConnector() for " + connectorNode);
			}
			return;
		}
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Perform refreshConnector() for " + connectorNode);
		}
		updateLayout();
		if (getMiddleSymbol() != MiddleSymbolType.NONE) {
			updateMiddleSymbolLocationControlPoint();
		}

		super.refreshConnector(force);

		if (connectorNode.getStartNode() == connectorNode.getEndNode()) {
			getReflexiveConnectorDelegate().refreshConnectorUsedBounds();
		}

		firstUpdated = true;

	}

	@Override
	public boolean needsRefresh() {
		if (!firstUpdated) {
			return true;
		}
		if (polylin == null) {
			return true;
		}
		return super.needsRefresh();
	}

	@Override
	public double distanceToConnector(DianaPoint aPoint, double scale) {

		if (connectorNode.getStartNode() == connectorNode.getEndNode()) {
			return getReflexiveConnectorDelegate().distanceToConnector(aPoint, scale);
		}

		double returned = Double.POSITIVE_INFINITY;

		if (polylin == null) {
			return Double.POSITIVE_INFINITY;
		}

		Point testPoint = connectorNode.convertNormalizedPointToViewCoordinates(aPoint, scale);

		for (DianaSegment s : polylin.getSegments()) {
			Point point1 = connectorNode.convertNormalizedPointToViewCoordinates(s.getP1(), scale);
			Point point2 = connectorNode.convertNormalizedPointToViewCoordinates(s.getP2(), scale);
			double distanceToCurrentSegment = Line2D.ptSegDist(point1.x, point1.y, point2.x, point2.y, testPoint.x, testPoint.y);
			if (distanceToCurrentSegment < returned) {
				returned = distanceToCurrentSegment;
			}
		}
		return returned;

	}

	public double getOverlapXResultingFromPixelOverlap() {
		// Compute relative overlap along X-axis
		Point overlap_p1 = new Point(0, 0);
		Point overlap_p2 = new Point(getPixelOverlap(), 0);
		DianaPoint overlap_pp1 = connectorNode.convertViewCoordinatesToNormalizedPoint(overlap_p1, 1);
		DianaPoint overlap_pp2 = connectorNode.convertViewCoordinatesToNormalizedPoint(overlap_p2, 1);
		return Math.abs(overlap_pp1.x - overlap_pp2.x);
	}

	public double getOverlapYResultingFromPixelOverlap() {
		// Compute relative overlap along Y-axis
		Point overlap_p1 = new Point(0, 0);
		Point overlap_p2 = new Point(0, getPixelOverlap());
		DianaPoint overlap_pp1 = connectorNode.convertViewCoordinatesToNormalizedPoint(overlap_p1, 1);
		DianaPoint overlap_pp2 = connectorNode.convertViewCoordinatesToNormalizedPoint(overlap_p2, 1);
		return Math.abs(overlap_pp1.y - overlap_pp2.y);
	}

	@Override
	public DianaRectangle getConnectorUsedBounds() {

		if (connectorNode.getStartNode() == connectorNode.getEndNode()) {
			return getReflexiveConnectorDelegate().getConnectorUsedBounds();
		}

		// logger.info("Called getConnectorUsedBounds()");
		if (polylin != null) {
			DianaRectangle minimalBounds = polylin.getBoundingBox();
			DianaRectangle returned = new DianaRectangle(Filling.FILLED);

			// Compute required space to draw symbols, eg arrows
			double maxSymbolSize = Math.max(getStartSymbolSize(), Math.max(getMiddleSymbolSize(), getEndSymbolSize()));
			double relativeWidthToAdd = maxSymbolSize * 2 / connectorNode.getViewWidth(1.0);
			double relativeHeightToAdd = maxSymbolSize * 2 / connectorNode.getViewHeight(1.0);

			// Add space to draw symbols, eg arrows
			returned.x = minimalBounds.x - relativeWidthToAdd * minimalBounds.width;
			returned.y = minimalBounds.y - relativeHeightToAdd * minimalBounds.height;
			returned.width = (1 + 2 * relativeWidthToAdd) * minimalBounds.width;
			returned.height = (1 + 2 * relativeHeightToAdd) * minimalBounds.height;
			// logger.info("Called getConnectorUsedBounds() return "+returned);
			return returned;
		}
		return NORMALIZED_BOUNDS;
	}

	@Override
	public void connectorWillBeModified() {
		super.connectorWillBeModified();
		if (polylin != null) {
			lastKnownCleanPolylinBeforeConnectorRestructuration = polylin.clone();
		}
	}

	@Override
	public void connectorHasBeenModified() {
		super.connectorHasBeenModified();
		lastKnownCleanPolylinBeforeConnectorRestructuration = null;
		_connectorChanged(false);
	}

	public DianaRectPolylin getCurrentPolylin() {
		return polylin;
	}

	// Used for serialization only
	public DianaRectPolylin _getPolylin() {
		if (getAdjustability() != RectPolylinAdjustability.FULLY_ADJUSTABLE) {
			return null;
		}
		return polylin;
	}

	// Used for serialization only
	public void _setPolylin(DianaRectPolylin aPolylin) {
		if (aPolylin != null && aPolylin.getPointsNb() > 0) {
			_deserializedPolylin = aPolylin;
			wasManuallyAdjusted = true;
		}
	}

	public void manuallySetPolylin(DianaRectPolylin aPolylin) {
		updateWithNewPolylin(aPolylin);
	}

	private ConnectorAdjustingControlPoint updateMiddleSymbolLocationControlPoint() {
		if (middleSymbolLocationControlPoint == null) {
			middleSymbolLocationControlPoint = new ConnectorAdjustingControlPoint(connectorNode, getMiddleSymbolLocation()) {
				@Override
				public Cursor getDraggingCursor() {
					/*
					 * SimplifiedCardinalDirection orientation =
					 * polylin.getNearestSegment(getMiddleSymbolLocation()).getApproximatedOrientation(); if (orientation != null) { if
					 * (orientation.isHorizontal()) { return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR); } if
					 * (orientation.isVertical()) { return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR); } }
					 */
					// return DianaConstants.MOVE_CURSOR;
					return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				}

				@Override
				public DianaArea getDraggingAuthorizedArea() {
					return polylin;
				}

				@Override
				public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
						DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
					if (polylin == null) {
						LOGGER.warning("polylin is null");
						return false;
					}
					// logger.info("OK, moving to "+point);dds
					UnnormalizedArcSize arcSize = computeUnnormalizedArcSize();
					DianaPoint pt = polylin.getNearestPointLocatedOnRoundedRepresentation(newRelativePoint, arcSize.arcWidth,
							arcSize.arcHeight);

					// DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					AffineTransform at = connectorNode.convertNormalizedPointToViewCoordinatesAT(1.0);
					pt = pt.transform(at);
					DianaRectPolylin transformedPolylin = polylin.transform(at);

					// DianaSegment segment = new DianaSegment(cp1.getPoint(),cp2.getPoint());
					setRelativeMiddleSymbolLocation(transformedPolylin.getRelativeLocation(pt));

					/*
					 * cp1RelativeToStartObject = GraphicalRepresentation.convertNormalizedPoint( getGraphicalRepresentation(), pt,
					 * getStartObject());
					 */
					connectorNode.notifyConnectorModified();
					return true;
				}

			};
		}
		middleSymbolLocationControlPoint.setPoint(getMiddleSymbolLocation());
		return middleSymbolLocationControlPoint;
	}

	private ConnectorAdjustingControlPoint middleSymbolLocationControlPoint;

	private class UnnormalizedArcSize {
		double arcWidth;
		double arcHeight;
	}

	private UnnormalizedArcSize computeUnnormalizedArcSize() {
		UnnormalizedArcSize returned = new UnnormalizedArcSize();
		DianaPoint arcP1 = connectorNode.convertViewCoordinatesToNormalizedPoint(new Point(0, 0), 1.0);
		DianaPoint arcP2 = connectorNode.convertViewCoordinatesToNormalizedPoint(new Point(getArcSize(), getArcSize()), 1.0);
		returned.arcWidth = arcP2.x - arcP1.x;
		returned.arcHeight = arcP2.y - arcP1.y;
		return returned;
	}

	@Override
	public double getStartAngle() {
		if (polylin == null) {
			return 0;
		}
		return polylin.getFirstSegment().getAngle();
	}

	@Override
	public double getEndAngle() {
		if (polylin == null) {
			return -Math.PI;
		}
		return polylin.getLastSegment().getAngle();
	}

	public ControlPoint getEndControlPoint() {
		return p_end;
	}

	public ControlPoint getStartControlPoint() {
		return p_start;
	}

	public DianaPoint getCrossedControlPointOnRoundedArc() {
		if (getCrossedControlPoint() != null) {
			if (getIsRounded()) {
				UnnormalizedArcSize arcSize = computeUnnormalizedArcSize();
				return polylin.getNearestPointLocatedOnRoundedRepresentation(getCrossedControlPoint(), arcSize.arcWidth, arcSize.arcWidth);
			}
			return getCrossedControlPoint();
		}
		return null;
	}

	public Vector<SimplifiedCardinalDirection> getAllowedStartOrientations() {
		Vector<SimplifiedCardinalDirection> returned = getPrimitiveAllowedStartOrientations();
		if (getIsStartingLocationFixed() && getFixedStartLocation() != null) {
			Vector<SimplifiedCardinalDirection> newConstraints = SimplifiedCardinalDirection.intersection(returned,
					_getAllowedStartOrientationsDueToFixedStartingLocation());
			if (newConstraints.size() == 0) {
				LOGGER.warning("Cannot respect fixed start location orientation constraint primitives=" + returned
						+ " for fixed starting position=" + _getAllowedStartOrientationsDueToFixedStartingLocation());
				return returned;
			}
			return newConstraints;
		}
		return returned;
	}

	private Vector<SimplifiedCardinalDirection> _getAllowedStartOrientationsDueToFixedStartingLocation() {
		Vector<SimplifiedCardinalDirection> returned = new Vector<>();
		DianaArea startArea = getStartNode().getAllowedStartAreaForConnector(connectorNode);
		// startArea.setIsFilled(false);
		for (SimplifiedCardinalDirection o : SimplifiedCardinalDirection.values()) {
			if (startArea.getAnchorAreaFrom(o).containsPoint(getFixedStartLocation())) {
				returned.add(o);
				// System.out.println("CHOOSEN: "+(startArea.getAnchorAreaFrom(o).containsPoint(getFixedStartLocation()))+" Orientation:
				// "+o+" startArea.getAnchorAreaFrom(o)="+startArea.getAnchorAreaFrom(o)+" location="+getFixedStartLocation());
				/*
				 * if (startArea.getAnchorAreaFrom(o) instanceof DianaArc) { DianaArc arc = (DianaArc)startArea.getAnchorAreaFrom(o); double angle
				 * = arc.angleForPoint(getFixedStartLocation()); DianaPoint p = arc.getPointAtRadianAngle(angle);
				 * System.out.println("Point "+getFixedStartLocation
				 * ()+" on arc="+arc.containsPoint(getFixedStartLocation())+" angle="+angle+
				 * " otherPoint="+p+" on arc="+arc.containsPoint(p)); }
				 */
				// if (startArea.getOrthogonalPerspectiveArea(o).containsPoint(getFixedStartLocation())) returned.add(o);
			}
		}
		// logger.info("Allowed start orientations due to fixed starting location = "+returned);
		if (returned.size() == 0) {
			LOGGER.warning("Allowed start orientations due to fixed starting location returned an empty vector " + returned);
		}
		return returned;
	}

	/**
	 * Return all allowed start orientation as this is defined in orientation constraint Does NOT take under account the fact that starting
	 * position could have been fixed and can also induced an other start orientation.
	 * 
	 * @return
	 */
	public Vector<SimplifiedCardinalDirection> getPrimitiveAllowedStartOrientations() {
		if (getRectPolylinConstraints() == null) {
			return SimplifiedCardinalDirection.allDirections();
		}
		switch (getRectPolylinConstraints()) {
			case NONE:
				return SimplifiedCardinalDirection.allDirections();
			case START_ORIENT_FIXED:
				if (getStartOrientation() == null) {
					return SimplifiedCardinalDirection.uniqueDirection(SimplifiedCardinalDirection.NORTH);
				}
				return SimplifiedCardinalDirection.uniqueDirection(getStartOrientation());
			case ORIENTATIONS_FIXED:
				if (getStartOrientation() == null) {
					return SimplifiedCardinalDirection.uniqueDirection(SimplifiedCardinalDirection.NORTH);
				}
				return SimplifiedCardinalDirection.uniqueDirection(getStartOrientation());
			case HORIZONTAL_LAYOUT:
				return SimplifiedCardinalDirection.someDirections(SimplifiedCardinalDirection.EAST, SimplifiedCardinalDirection.WEST);
			case HORIZONTAL_FIRST:
				return SimplifiedCardinalDirection.someDirections(SimplifiedCardinalDirection.EAST, SimplifiedCardinalDirection.WEST);
			case VERTICAL_LAYOUT:
				return SimplifiedCardinalDirection.someDirections(SimplifiedCardinalDirection.NORTH, SimplifiedCardinalDirection.SOUTH);
			case VERTICAL_FIRST:
				return SimplifiedCardinalDirection.someDirections(SimplifiedCardinalDirection.NORTH, SimplifiedCardinalDirection.SOUTH);

			default:
				return SimplifiedCardinalDirection.allDirections();
		}
	}

	public Vector<SimplifiedCardinalDirection> getExcludedStartOrientations() {
		return SimplifiedCardinalDirection.allDirectionsExcept(getAllowedStartOrientations());
	}

	public Vector<SimplifiedCardinalDirection> getAllowedEndOrientations() {
		Vector<SimplifiedCardinalDirection> returned = getPrimitiveAllowedEndOrientations();
		if (getIsEndingLocationFixed() && getFixedEndLocation() != null) {
			Vector<SimplifiedCardinalDirection> newConstraints = SimplifiedCardinalDirection.intersection(returned,
					_getAllowedEndOrientationsDueToFixedEndingLocation());
			if (newConstraints.size() == 0) {
				LOGGER.warning("Cannot respect fixed end location orientation constraint primitives=" + returned
						+ " for fixed ending position=" + _getAllowedEndOrientationsDueToFixedEndingLocation());
				return returned;
			}
			return newConstraints;
		}
		return returned;
	}

	private Vector<SimplifiedCardinalDirection> _getAllowedEndOrientationsDueToFixedEndingLocation() {
		Vector<SimplifiedCardinalDirection> returned = new Vector<>();
		DianaArea endArea = getEndNode().getAllowedEndAreaForConnector(connectorNode);
		// endArea.setIsFilled(false);
		for (SimplifiedCardinalDirection o : SimplifiedCardinalDirection.values()) {
			// if (endArea.getOrthogonalPerspectiveArea(o).containsPoint(getFixedEndLocation())) returned.add(o);
			if (endArea.getAnchorAreaFrom(o).containsPoint(getFixedEndLocation())) {
				returned.add(o);
				// System.out.println("CHOOSEN: "+(endArea.getOrthogonalPerspectiveArea(o).containsPoint(getFixedEndLocation()))+"
				// Orientation: "+o+" endArea.getOrthogonalPerspectiveArea(o)="+endArea.getOrthogonalPerspectiveArea(o));
			}
		}
		// logger.info("Allowed end orientations due to fixed ending location="+returned);
		if (returned.size() == 0) {
			LOGGER.warning("Allowed start orientations due to fixed starting location returned an empty vector " + returned);
		}
		return returned;
	}

	public Vector<SimplifiedCardinalDirection> getPrimitiveAllowedEndOrientations() {
		switch (getRectPolylinConstraints()) {
			case NONE:
				return SimplifiedCardinalDirection.allDirections();
			case END_ORIENT_FIXED:
				if (getEndOrientation() == null) {
					return SimplifiedCardinalDirection.uniqueDirection(SimplifiedCardinalDirection.NORTH);
				}
				return SimplifiedCardinalDirection.uniqueDirection(getEndOrientation());
			case ORIENTATIONS_FIXED:
				if (getEndOrientation() == null) {
					return SimplifiedCardinalDirection.uniqueDirection(SimplifiedCardinalDirection.NORTH);
				}
				return SimplifiedCardinalDirection.uniqueDirection(getEndOrientation());
			case HORIZONTAL_LAYOUT:
				return SimplifiedCardinalDirection.someDirections(SimplifiedCardinalDirection.EAST, SimplifiedCardinalDirection.WEST);
			case VERTICAL_FIRST:
				return SimplifiedCardinalDirection.someDirections(SimplifiedCardinalDirection.EAST, SimplifiedCardinalDirection.WEST);
			case VERTICAL_LAYOUT:
				return SimplifiedCardinalDirection.someDirections(SimplifiedCardinalDirection.NORTH, SimplifiedCardinalDirection.SOUTH);
			case HORIZONTAL_FIRST:
				return SimplifiedCardinalDirection.someDirections(SimplifiedCardinalDirection.NORTH, SimplifiedCardinalDirection.SOUTH);

			default:
				return SimplifiedCardinalDirection.allDirections();
		}
	}

	public Vector<SimplifiedCardinalDirection> getExcludedEndOrientations() {
		return SimplifiedCardinalDirection.allDirectionsExcept(getAllowedEndOrientations());
	}

	// *******************************************************************************
	// * Internal A.P.I. for connector computation *
	// *******************************************************************************

	/**
	 * This is the general method used for connector updating Calling this method is generally safe regarding internal structure
	 */
	public void updateLayout() {
		if (connectorNode.getGraphicalRepresentation() == null) {
			return;
		}

		if (connectorNode.getStartNode() == connectorNode.getEndNode()) {
			// List<ControlPoint> newControlPoints = getReflexiveConnectorDelegate().updateControlPoints();
			controlPoints.clear();
			// controlPoints.addAll(newControlPoints);

			DianaPolylin polylin = getReflexiveConnectorDelegate().updateControlPoints();
			controlPoints.clear();
			controlPoints.add(getReflexiveConnectorDelegate().getCp1());
			controlPoints.add(getReflexiveConnectorDelegate().getReflexiveConnectorControlPoint());
			controlPoints.add(getReflexiveConnectorDelegate().getCp2());
			return;
		}

		/*if (!connectorNode.getGraphicalRepresentation().isRegistered()) {
			return;
		}*/

		if (getAdjustability() == RectPolylinAdjustability.AUTO_LAYOUT
				|| getAdjustability() == RectPolylinAdjustability.FULLY_ADJUSTABLE && !getWasManuallyAdjusted()) {

			_updateAsAutoLayout();

		}

		else if (getAdjustability() == RectPolylinAdjustability.BASICALLY_ADJUSTABLE) {

			if (polylin == null) {

				if (_deserializedPolylin != null) {
					// Rebuild from deserialized polylin
					updateWithNewPolylin(new DianaRectPolylin(_deserializedPolylin.getPoints(),
							getConnectorSpecification().getStraightLineWhenPossible(), getOverlapXResultingFromPixelOverlap(),
							getOverlapYResultingFromPixelOverlap()));
					_deserializedPolylin = null;
				}

				else {
					// Was never computed, do it now
					_updateAsAutoLayout();
				}
			}

			_updateAsBasicallyAdjustable();

		}

		else /* RectPolylinConnectorSpecification is adjustable, getAdjustability() == RectPolylinAdjustability.FULLY_ADJUSTABLE */ {

			_updateAsFullyAdjustable();

			/*
			 * if (polylin == null) {
			 * 
			 * if (_deserializedPolylin != null) { // Rebuild from deserialized polylin updateWithNewPolylin(new
			 * DianaRectPolylin(_deserializedPolylin
			 * .getPoints(),getStraightLineWhenPossible(),getOverlapXResultingFromPixelOverlap(),getOverlapYResultingFromPixelOverlap()));
			 * _deserializedPolylin = null; }
			 * 
			 * else { // Was never computed, do it now _updateAsAutoLayout(); } }
			 * 
			 * // Attempt to restore some locations from stored locations relative to start and end object // - start point is restored from
			 * relative location to start object and put on starting object outline // - end point is restored from relative location to end
			 * object and put on ending object outline // - first point is also restored from relative location to start object if there are
			 * more than 5 points // - next point is updated accordingly to orientation of second segment // - last point is also restored
			 * from relative location to end object if there are more than 5 points // - previous point is updated accordingly to
			 * orientation of previous-last segment
			 * 
			 * 
			 * int indexOfMiddleSegment = polylin.getSegments().indexOf(polylin.getMiddleSegment());
			 * 
			 * for (int i=0; i<polylin.getPointsNb(); i++) { if (i<=indexOfMiddleSegment && polylinRelativeToStartObject != null) { // That
			 * point is closest to start object // remember location stored relative to start object DianaPoint pointRelativeToStartObject =
			 * polylinRelativeToStartObject.getPointAt(i); if (i==0) { // This is the start object, when not, put it on starting object
			 * shape outline pointRelativeToStartObject =
			 * getStartObject().getShape().getOutline().nearestOutlinePoint(pointRelativeToStartObject); polylin.updatePointAt(i,
			 * GraphicalRepresentation.convertNormalizedPoint( getStartObject(), pointRelativeToStartObject, getGraphicalRepresentation()));
			 * } else if (i==1 && polylin.getPointsNb() >=6) { DianaPoint firstPoint = GraphicalRepresentation.convertNormalizedPoint(
			 * getStartObject(), pointRelativeToStartObject, getGraphicalRepresentation()); DianaPoint nextPoint = polylin.getPointAt(2); if
			 * (polylinRelativeToStartObject.getSegmentAt(1).getApproximatedOrientation().isHorizontal()) { nextPoint.y = firstPoint.y; }
			 * else { nextPoint.x = firstPoint.x; } polylin.updatePointAt(i,firstPoint); polylin.updatePointAt(i+1,nextPoint); } } else if
			 * (polylinRelativeToEndObject != null) { // That point is closest to end object // remember location stored relative to end
			 * object DianaPoint pointRelativeToEndObject = polylinRelativeToEndObject.getPointAt(i); if (i==polylin.getPointsNb()-1) { //
			 * This is the end object, when not, put it on ending object shape outline pointRelativeToEndObject =
			 * getEndObject().getShape().getOutline().nearestOutlinePoint(pointRelativeToEndObject); polylin.updatePointAt(i,
			 * GraphicalRepresentation.convertNormalizedPoint( getEndObject(), pointRelativeToEndObject, getGraphicalRepresentation())); }
			 * else if (i==polylin.getPointsNb()-2 && polylin.getPointsNb() >=6) { DianaPoint lastPoint =
			 * GraphicalRepresentation.convertNormalizedPoint( getEndObject(), pointRelativeToEndObject, getGraphicalRepresentation());
			 * DianaPoint previousPoint = polylin.getPointAt(polylin.getPointsNb()-3); if
			 * (polylinRelativeToEndObject.getSegmentAt(polylinRelativeToEndObject
			 * .getSegmentNb()-2).getApproximatedOrientation().isHorizontal()) { previousPoint.y = previousPoint.y; } else { previousPoint.x
			 * = previousPoint.x; } polylin.updatePointAt(i,lastPoint); polylin.updatePointAt(i-1,previousPoint); } } }
			 * 
			 * updateAndNormalizeCurrentPolylin();
			 */
		}

	}

	/**
	 * Compute and return start area outline, in the connector coordinates system
	 * 
	 * If some orientation constraints are defined, return portion of start area outline matching allowed orientations
	 * 
	 * If starting location is fixed return this location
	 * 
	 * @return DianaArea
	 */
	public DianaArea retrieveStartArea() {
		DianaArea startArea = retrieveAllowedStartArea(true);

		if (getIsStartingLocationFixed() && getFixedStartLocation() != null) {
			DianaPoint fixedPoint = DianaUtils.convertNormalizedPoint(getStartNode(), getFixedStartLocation(), connectorNode);
			/*
			 * if (startArea instanceof DianaShape) { return ((DianaShape<?>)startArea).nearestOutlinePoint(fixedPoint); } else
			 */DianaPoint returned = startArea.getNearestPoint(fixedPoint);
			if (!startArea.containsPoint(returned)) {
				LOGGER.warning(
						"Inconsistent data: point " + returned + " not located on area: " + startArea + " [was: " + fixedPoint + "]");
			}
			return returned;

		}

		return startArea;

		/*
		 * AffineTransform at1 = GraphicalRepresentation.convertNormalizedCoordinatesAT( getStartObject(), getGraphicalRepresentation());
		 * 
		 * DianaArea startArea = getStartObject().getShape().getShape().transform(at1); if (startArea instanceof DianaShape) {
		 * ((DianaShape<?>)startArea).setIsFilled(false); }
		 * 
		 * Vector<SimplifiedCardinalDirection> allowedStartOrientations = getAllowedStartOrientations();
		 * 
		 * if (getIsStartingLocationFixed() && getFixedStartLocation() != null) { if (startArea instanceof DianaShape) { DianaPoint startPoint =
		 * ((DianaShape<?>)startArea).nearestOutlinePoint(GraphicalRepresentation.convertNormalizedPoint(getStartObject(),
		 * getFixedStartLocation(), getGraphicalRepresentation())); Vector<SimplifiedCardinalDirection>
		 * allowedStartOrientationsBecauseOfFixedPoint = new Vector<SimplifiedCardinalDirection>(); for (SimplifiedCardinalDirection o :
		 * SimplifiedCardinalDirection.values()) { if (startArea.getOrthogonalPerspectiveArea(o).containsPoint(startPoint)) {
		 * allowedStartOrientationsBecauseOfFixedPoint.add(o); } } Vector<SimplifiedCardinalDirection> resultingAllowedOrientations =
		 * SimplifiedCardinalDirection.intersection(allowedStartOrientations, allowedStartOrientationsBecauseOfFixedPoint); if
		 * (resultingAllowedOrientations.size() > 0) allowedStartOrientations = resultingAllowedOrientations; // Otherwise, cannot respect
		 * this constraint, give up logger.warning("start area is a point !!!"); startArea = startPoint; } }
		 * 
		 * return startArea;
		 */
	}

	/**
	 * Compute and return allowed start area, in the connector coordinates system If some orientation constraints are defined, return
	 * portion of start area outline matching allowed orientations
	 * 
	 * @return DianaArea
	 */
	public DianaArea retrieveAllowedStartArea(boolean takeFixedControlPointUnderAccount) {
		AffineTransform at1 = DianaUtils.convertNormalizedCoordinatesAT(getStartNode(), connectorNode);

		DianaArea startArea = getStartNode().getAllowedStartAreaForConnector(connectorNode).transform(at1);
		/*
		 * if (startArea instanceof DianaShape) { ((DianaShape<?>)startArea).setIsFilled(false); }
		 */

		Vector<SimplifiedCardinalDirection> allowedStartOrientations = takeFixedControlPointUnderAccount ? getAllowedStartOrientations()
				: getPrimitiveAllowedStartOrientations();

		if (allowedStartOrientations.size() > 0 && allowedStartOrientations.size() < 4) {
			// Some directions should not be available
			Vector<DianaArea> allowedAreas = new Vector<>();
			for (SimplifiedCardinalDirection o : allowedStartOrientations) {
				// allowedAreas.add(startArea.intersect(startArea.getOrthogonalPerspectiveArea(o)));
				allowedAreas.add(startArea.getAnchorAreaFrom(o));
				/*
				 * logger.info("Orientation: "+o); logger.info("startArea="+startArea);
				 * logger.info("ortho="+startArea.getOrthogonalPerspectiveArea(o));
				 * logger.info("result="+startArea.intersect(startArea.getOrthogonalPerspectiveArea(o)));
				 */
			}
			return DianaUnionArea.makeUnion(allowedAreas);
		}
		else if (allowedStartOrientations.size() == 0) {
			LOGGER.warning("Cannot respect starting orientation constraints");
		}

		return startArea;

	}

	/**
	 * Compute and return end area outline, in the connector coordinates system
	 * 
	 * If some orientation constraints are defined, return portion of end area outline matching allowed orientations
	 * 
	 * If starting location is fixed return this location
	 * 
	 * @return DianaArea
	 */
	public DianaArea retrieveEndArea() {
		// System.out.println("retrieveAllowedEndArea()="+retrieveAllowedEndArea());

		DianaArea endArea = retrieveAllowedEndArea(true);

		if (getIsEndingLocationFixed() && getFixedEndLocation() != null) {
			DianaPoint fixedPoint = DianaUtils.convertNormalizedPoint(getEndNode(), getFixedEndLocation(), connectorNode);
			/*
			 * if (endArea instanceof DianaShape) { return ((DianaShape<?>)endArea).nearestOutlinePoint(fixedPoint); } else
			 */
			DianaPoint returned = endArea.getNearestPoint(fixedPoint);
			if (!endArea.containsPoint(returned)) {
				LOGGER.warning("Inconsistent data: point " + returned + " not located on area: " + endArea + " [was: " + fixedPoint + "]");
			}
			return returned;
		}

		return endArea;

		/*
		 * AffineTransform at2 = GraphicalRepresentation.convertNormalizedCoordinatesAT( getEndObject(), getGraphicalRepresentation());
		 * 
		 * DianaArea endArea = getEndObject().getShape().getShape().transform(at2); if (endArea instanceof DianaShape) {
		 * ((DianaShape<?>)endArea).setIsFilled(false); }
		 * 
		 * Vector<SimplifiedCardinalDirection> allowedEndOrientations = getAllowedEndOrientations();
		 * 
		 * if (getIsEndingLocationFixed() && getFixedEndLocation() != null) { if (endArea instanceof DianaShape) { DianaPoint endPoint =
		 * ((DianaShape<?>)endArea).nearestOutlinePoint(GraphicalRepresentation.convertNormalizedPoint(getEndObject(), getFixedEndLocation(),
		 * getGraphicalRepresentation())); Vector<SimplifiedCardinalDirection> allowedEndOrientationsBecauseOfFixedPoint = new
		 * Vector<SimplifiedCardinalDirection>(); for (SimplifiedCardinalDirection o : SimplifiedCardinalDirection.values()) { if
		 * (endArea.getOrthogonalPerspectiveArea(o).containsPoint(endPoint)) { allowedEndOrientationsBecauseOfFixedPoint.add(o); } }
		 * Vector<SimplifiedCardinalDirection> resultingAllowedOrientations =
		 * SimplifiedCardinalDirection.intersection(allowedEndOrientations, allowedEndOrientationsBecauseOfFixedPoint); if
		 * (resultingAllowedOrientations.size() > 0) allowedEndOrientations = resultingAllowedOrientations; // Otherwise, cannot respect
		 * this constraint, give up endArea = endPoint; } }
		 * 
		 * return endArea;
		 */
	}

	/**
	 * Compute and return allowed end area, in the connector coordinates system If some orientation constraints are defined, return portion
	 * of end area outline matching allowed orientations
	 * 
	 * @return DianaArea
	 */
	public DianaArea retrieveAllowedEndArea(boolean takeFixedControlPointUnderAccount) {
		AffineTransform at2 = DianaUtils.convertNormalizedCoordinatesAT(getEndNode(), connectorNode);

		DianaArea endArea = getEndNode().getAllowedEndAreaForConnector(connectorNode).transform(at2);
		/*
		 * if (endArea instanceof DianaShape) { ((DianaShape<?>)endArea).setIsFilled(false); }
		 */

		Vector<SimplifiedCardinalDirection> allowedEndOrientations = takeFixedControlPointUnderAccount ? getAllowedEndOrientations()
				: getPrimitiveAllowedEndOrientations();

		if (allowedEndOrientations.size() > 0 && allowedEndOrientations.size() < 4) {
			// Some directions should not be available
			Vector<DianaArea> allowedAreas = new Vector<>();
			for (SimplifiedCardinalDirection o : allowedEndOrientations) {
				// allowedAreas.add(endArea.intersect(endArea.getOrthogonalPerspectiveArea(o)));
				allowedAreas.add(endArea.getAnchorAreaFrom(o));
				// System.out.println("Orientation: "+o+" ortho: "+endArea.getOrthogonalPerspectiveArea(o)+"
				// intersect="+endArea.intersect(endArea.getOrthogonalPerspectiveArea(o)));
			}
			return DianaUnionArea.makeUnion(allowedAreas);
		}
		else if (allowedEndOrientations.size() == 0) {
			LOGGER.warning("Cannot respect ending orientation constraints");
		}

		return endArea;
	}

	/**
	 * Internal method called to update connector asserting layout is automatically performed Whatever happen, compute the shortest polylin
	 * respecting start and end constraints (positions and/or orientations) This new polylin is automatically recomputed and set to current
	 * connector.
	 * 
	 */
	private void _updateAsAutoLayout() {
		final DianaArea startArea = retrieveStartArea();
		final DianaArea endArea = retrieveEndArea();

		// logger.info("startArea="+startArea);
		// logger.info("endArea="+endArea);

		DianaPoint startMiddle = null;

		if (startArea != null && startArea.isFinite()) {
			DianaRectangle startAreaBounds = startArea.getEmbeddingBounds();
			if (startAreaBounds != null) {
				startMiddle = startAreaBounds.getCenter();
			}
		}
		if (startMiddle == null) {
			LOGGER.warning("Could not find middle of resulting start area: " + startArea);
			startMiddle = new DianaPoint(0, 0);
		}

		DianaPoint endMiddle = null;

		if (endArea != null && endArea.isFinite()) {
			DianaRectangle endAreaBounds = endArea.getEmbeddingBounds();
			if (endAreaBounds != null) {
				endMiddle = endAreaBounds.getCenter();
			}
		}
		if (endMiddle == null) {
			LOGGER.warning("Could not find middle of resulting start area: " + startArea);
			endMiddle = new DianaPoint(1, 1);
		}

		// First obtain the two affine transform allowing to convert from
		// extremity objects coordinates to connector drawable

		/*
		 * AffineTransform at1 = GraphicalRepresentation.convertNormalizedCoordinatesAT( getStartObject(), getGraphicalRepresentation());
		 * 
		 * AffineTransform at2 = GraphicalRepresentation.convertNormalizedCoordinatesAT( getEndObject(), getGraphicalRepresentation());
		 * 
		 * DianaPoint startMiddle = getStartObject().getShape().getShape().getCenter().transform(at1); DianaPoint endMiddle =
		 * getEndObject().getShape().getShape().getCenter().transform(at2);
		 */

		Vector<SimplifiedCardinalDirection> potentialStartOrientations = new Vector<>();
		Vector<SimplifiedCardinalDirection> potentialEndOrientations = new Vector<>();

		boolean testAllCombinations = true;

		// logger.info("getRectPolylinConstraints()="+getRectPolylinConstraints()+" getEndOrientation()="+getEndOrientation());

		if (getRectPolylinConstraints() == RectPolylinConstraints.ORIENTATIONS_FIXED) {
			potentialStartOrientations.add(getStartOrientation());
			potentialEndOrientations.add(getEndOrientation());
		}

		else {
			CardinalQuadrant quadrant = DianaPoint.getCardinalQuadrant(startMiddle, endMiddle);

			RectPolylinConstraints constraints = getRectPolylinConstraints();
			if (constraints == RectPolylinConstraints.START_ORIENT_FIXED || constraints == RectPolylinConstraints.END_ORIENT_FIXED) {
				constraints = RectPolylinConstraints.NONE;
			}

			if (constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT) {
				testAllCombinations = false;
			}
			if (constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL) {
				testAllCombinations = false;
			}

			if (quadrant == CardinalQuadrant.NORTH_EAST) {
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_FIRST || constraints == RectPolylinConstraints.VERTICAL_FIRST) {
					if (constraints != RectPolylinConstraints.HORIZONTAL_FIRST) {
						potentialStartOrientations.add(SimplifiedCardinalDirection.NORTH);
						potentialEndOrientations.add(SimplifiedCardinalDirection.WEST);
					}
					if (constraints != RectPolylinConstraints.VERTICAL_FIRST) {
						potentialStartOrientations.add(SimplifiedCardinalDirection.EAST);
						potentialEndOrientations.add(SimplifiedCardinalDirection.SOUTH);
					}
				}
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.HORIZONTAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL) {
					potentialStartOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialStartOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialStartOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.WEST);
				}
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.VERTICAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL) {
					potentialStartOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialStartOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialStartOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.SOUTH);
				}
			}
			else if (quadrant == CardinalQuadrant.SOUTH_EAST) {
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_FIRST || constraints == RectPolylinConstraints.VERTICAL_FIRST) {
					if (constraints != RectPolylinConstraints.HORIZONTAL_FIRST) {
						potentialStartOrientations.add(SimplifiedCardinalDirection.SOUTH);
						potentialEndOrientations.add(SimplifiedCardinalDirection.WEST);
					}
					if (constraints != RectPolylinConstraints.VERTICAL_FIRST) {
						potentialStartOrientations.add(SimplifiedCardinalDirection.EAST);
						potentialEndOrientations.add(SimplifiedCardinalDirection.NORTH);
					}
				}
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.HORIZONTAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL) {
					potentialStartOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialStartOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialStartOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.WEST);
				}
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.VERTICAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL) {
					potentialStartOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialStartOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialStartOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.NORTH);
				}
			}
			else if (quadrant == CardinalQuadrant.SOUTH_WEST) {
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_FIRST || constraints == RectPolylinConstraints.VERTICAL_FIRST) {
					if (constraints != RectPolylinConstraints.HORIZONTAL_FIRST) {
						potentialStartOrientations.add(SimplifiedCardinalDirection.SOUTH);
						potentialEndOrientations.add(SimplifiedCardinalDirection.EAST);
					}
					if (constraints != RectPolylinConstraints.VERTICAL_FIRST) {
						potentialStartOrientations.add(SimplifiedCardinalDirection.WEST);
						potentialEndOrientations.add(SimplifiedCardinalDirection.NORTH);
					}
				}
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.HORIZONTAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL) {
					potentialStartOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialStartOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialStartOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.EAST);
				}
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.VERTICAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL) {
					potentialStartOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialStartOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialStartOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.NORTH);
				}
			}
			else /* if (quadrant == CardinalQuadrant.NORTH_WEST) */ {
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_FIRST || constraints == RectPolylinConstraints.VERTICAL_FIRST) {
					if (constraints != RectPolylinConstraints.HORIZONTAL_FIRST) {
						potentialStartOrientations.add(SimplifiedCardinalDirection.NORTH);
						potentialEndOrientations.add(SimplifiedCardinalDirection.EAST);
					}
					if (constraints != RectPolylinConstraints.VERTICAL_FIRST) {
						potentialStartOrientations.add(SimplifiedCardinalDirection.WEST);
						potentialEndOrientations.add(SimplifiedCardinalDirection.SOUTH);
					}
				}
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.HORIZONTAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL) {
					potentialStartOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialStartOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialStartOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.EAST);
				}
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.VERTICAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL) {
					potentialStartOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialStartOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialStartOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.SOUTH);
				}
			}

			if (getRectPolylinConstraints() == RectPolylinConstraints.START_ORIENT_FIXED) {
				potentialStartOrientations.clear();
				potentialStartOrientations.add(getStartOrientation());

			}

			if (getRectPolylinConstraints() == RectPolylinConstraints.END_ORIENT_FIXED) {
				potentialEndOrientations.clear();
				potentialEndOrientations.add(getEndOrientation());
			}
		}

		/*
		 * DianaArea startArea = getStartObject().getShape().getShape().transform(at1); DianaArea endArea =
		 * getEndObject().getShape().getShape().transform(at2);
		 */

		potentialPolylin.clear();
		double minimalLength = Double.POSITIVE_INFINITY;
		SimplifiedCardinalDirection choosenStartOrientation = null;
		SimplifiedCardinalDirection choosenEndOrientation = null;

		Vector<SimplifiedCardinalDirection> allowedStartOrientations = getAllowedStartOrientations();
		Vector<SimplifiedCardinalDirection> allowedEndOrientations = getAllowedEndOrientations();

		if (!testAllCombinations && potentialStartOrientations.size() != potentialEndOrientations.size()) {
			LOGGER.warning("Inconsistent data: potentialStartOrientations.size() != potentialEndOrientations.size()");
		}

		if (testAllCombinations) {
			// Remove doublons
			Vector<SimplifiedCardinalDirection> newPotentialStartOrientations = new Vector<>();
			for (SimplifiedCardinalDirection o : potentialStartOrientations) {
				if (!newPotentialStartOrientations.contains(o)) {
					newPotentialStartOrientations.add(o);
				}
			}
			potentialStartOrientations = newPotentialStartOrientations;
			Vector<SimplifiedCardinalDirection> newPotentialEndOrientations = new Vector<>();
			for (SimplifiedCardinalDirection o : potentialEndOrientations) {
				if (!newPotentialEndOrientations.contains(o)) {
					newPotentialEndOrientations.add(o);
				}
			}
			potentialEndOrientations = newPotentialEndOrientations;
		}

		// !!! Faire des methodes pour ca !!!

		/*
		 * logger.info("Looking for best polylin"); logger.info("potentialStartOrientations="+potentialStartOrientations);
		 * logger.info("potentialEndOrientations="+potentialEndOrientations);
		 */

		if (!testAllCombinations && potentialStartOrientations.size() == potentialEndOrientations.size()) {
			for (int i = 0; i < potentialStartOrientations.size(); i++) {
				if (allowedStartOrientations.contains(potentialStartOrientations.get(i))
						&& allowedEndOrientations.contains(potentialEndOrientations.get(i))) {
					DianaRectPolylin newPolylin = new DianaRectPolylin(startArea, potentialStartOrientations.get(i), endArea,
							potentialEndOrientations.get(i), getStraightLineWhenPossible(), getOverlapXResultingFromPixelOverlap(),
							getOverlapYResultingFromPixelOverlap());
					potentialPolylin.add(newPolylin);
					if (newPolylin.getPointsNb() > 0 && newPolylin.getLength() < minimalLength + DianaGeometricObject.EPSILON /*
																																* Hysteresis to
																																* avoid
																																* blinking
																																*/) {
						polylin = newPolylin;
						minimalLength = newPolylin.getLength();
						choosenStartOrientation = potentialStartOrientations.get(i);
						choosenEndOrientation = potentialEndOrientations.get(i);
					}
				}
			}
		}

		else {
			for (SimplifiedCardinalDirection startOrientation : potentialStartOrientations) {
				if (allowedStartOrientations.contains(startOrientation)) {
					for (SimplifiedCardinalDirection endOrientation : potentialEndOrientations) {
						if (allowedEndOrientations.contains(endOrientation)) {
							DianaRectPolylin newPolylin = new DianaRectPolylin(startArea, startOrientation, endArea, endOrientation,
									getStraightLineWhenPossible(), getOverlapXResultingFromPixelOverlap(),
									getOverlapYResultingFromPixelOverlap());
							potentialPolylin.add(newPolylin);
							if (newPolylin.doesRespectAllConstraints()
									&& newPolylin.getLength() < minimalLength + DianaGeometricObject.EPSILON /*
																												* Hysteresis
																												* to
																												* avoid
																												* blinking
																												*/) {
								polylin = newPolylin;
								minimalLength = newPolylin.getLength();
								choosenStartOrientation = startOrientation;
								choosenEndOrientation = endOrientation;
							}
						}
					}
				}
			}
		}

		if (polylin != null) {

			setStartOrientation(polylin.getStartOrientation());
			setEndOrientation(polylin.getEndOrientation());
		}
		else {
			LOGGER.warning("polylin=null !!!!!!");
		}

		// logger.info("Best polylin found from/to "+startOrientation+"/"+endOrientation+" with "+polylin.getPointsNb()+" points");
		// logger.info("Polylin="+polylin);

		if (getStartOrientation() != choosenStartOrientation && LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Requested start orientation was: " + choosenStartOrientation + " but is finally: " + getStartOrientation());
		}
		if (getEndOrientation() != choosenEndOrientation && LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Requested end orientation was: " + choosenEndOrientation + " but is finally: " + getEndOrientation());
		}

		// logger.info("Before update, polylin from/to "+startOrientation+"/"+endOrientation+" with "+polylin.getPointsNb()+" points");
		// logger.info("Polylin="+polylin);

		// updateWithNewPolylin(polylin,true);

		if (polylin != null) {
			if (polylin.isNormalized()) {
				updateWithNewPolylin(polylin, true, false);
			}
			else {
				LOGGER.warning("Result of auto-layout computing returned a non-normalized polylin. Please investigate");
				updateWithNewPolylin(polylin, false, false);
			}
		}
		else {
			LOGGER.warning("polylin=null !!!!!!");
		}

		// logger.info("After update, polylin from/to "+startOrientation+"/"+endOrientation+" with "+polylin.getPointsNb()+" points");
		// logger.info("Polylin="+polylin);

	}

	/**
	 * Internal method called to update connector asserting layout is defined as BASICALLY_ADJUSTABLE. If crossedControlPoint is defined,
	 * compute the shortest polylin crossing this point and respecting start and end constraints (positions and/or orientations) If no
	 * crossedControlPoint is defined, compute the shortest polylin respecting start and end constraints (positions and/or orientations)
	 */
	private void _updateAsBasicallyAdjustable() {

		// System.out.println("_updateAsBasicallyAdjustable() with " + getCrossedControlPoint());

		final DianaArea startArea = retrieveStartArea();
		final DianaArea endArea = retrieveEndArea();
		Vector<SimplifiedCardinalDirection> allowedStartOrientations = getAllowedStartOrientations();
		Vector<SimplifiedCardinalDirection> allowedEndOrientations = getAllowedEndOrientations();

		DianaRectPolylin newPolylin;

		DianaAreaProvider<SimplifiedCardinalDirection> startAreaProvider = getIsStartingLocationFixed()
				? new DefaultAreaProvider<>(startArea)
				: (input) -> getStartNode().getAllowedStartAreaForConnectorForDirection(connectorNode, startArea, input);
		DianaAreaProvider<SimplifiedCardinalDirection> endAreaProvider = getIsEndingLocationFixed() ? new DefaultAreaProvider<>(endArea)
				: (input) -> getEndNode().getAllowedEndAreaForConnectorForDirection(connectorNode, endArea, input);
		if (getCrossedControlPoint() != null) {

			// System.out.println("startArea="+startArea);
			// System.out.println("endArea="+endArea);
			newPolylin = DianaRectPolylin.makeRectPolylinCrossingPoint(startAreaProvider, endAreaProvider, getCrossedControlPoint(), true,
					getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap(),
					SimplifiedCardinalDirection.allDirectionsExcept(allowedStartOrientations),
					SimplifiedCardinalDirection.allDirectionsExcept(allowedEndOrientations));
		}
		else {
			newPolylin = DianaRectPolylin.makeShortestRectPolylin(startAreaProvider, endAreaProvider,
					getConnectorSpecification().getStraightLineWhenPossible(), getOverlapXResultingFromPixelOverlap(),
					getOverlapYResultingFromPixelOverlap(), SimplifiedCardinalDirection.allDirectionsExcept(allowedStartOrientations),
					SimplifiedCardinalDirection.allDirectionsExcept(allowedEndOrientations));
		}

		if (newPolylin == null) {
			LOGGER.warning("Obtained null polylin allowedStartOrientations=" + allowedStartOrientations);
			return;
		}

		if (newPolylin.isNormalized()) {
			updateWithNewPolylin(newPolylin, true, false);
		}
		else {
			LOGGER.warning("Result of basically_adjustable layout computing returned a non-normalized polylin. Please investigate");
			updateWithNewPolylin(newPolylin, false, false);
		}

	}

	/**
	 * Internal method called to update connector asserting layout is defined as FULLY_ADJUSTABLE.
	 */
	private void _updateAsFullyAdjustable() {
		if (polylin == null) {

			if (_deserializedPolylin != null) {
				// Rebuild from deserialized polylin
				updateWithNewPolylin(
						new DianaRectPolylin(_deserializedPolylin.getPoints(), getConnectorSpecification().getStraightLineWhenPossible(),
								getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap()));
				_deserializedPolylin = null;
			}

			else {
				// Was never computed, do it now
				_updateAsAutoLayout();
			}
		}

		// Special case where there is a unique segment
		// Used when connector is restructuring (start and/or end shapes are moving or resizing)
		if (lastKnownCleanPolylinBeforeConnectorRestructuration != null
				&& lastKnownCleanPolylinBeforeConnectorRestructuration.getSegmentNb() == 1 && polylinRelativeToStartObject != null
				&& polylinRelativeToEndObject != null) {
			DianaPoint lastStartPoint = polylinRelativeToStartObject.getFirstPoint();
			DianaPoint lastEndPoint = polylinRelativeToEndObject.getLastPoint();
			lastStartPoint = DianaUtils.convertNormalizedPoint(getStartNode(), lastStartPoint, connectorNode);
			lastEndPoint = DianaUtils.convertNormalizedPoint(getEndNode(), lastEndPoint, connectorNode);
			DianaPoint pt = DianaPoint.getMiddlePoint(lastStartPoint, lastEndPoint);
			if (_updateAsFullyAdjustableForUniqueSegment(pt)) {
				return;
			}
		}

		// Special case where there is a unique segment
		// Used when connector beeing edited
		if (polylin != null && polylin.getSegmentNb() == 1) {
			DianaPoint lastStartPoint = polylin.getFirstPoint();
			DianaPoint lastEndPoint = polylin.getLastPoint();
			DianaPoint pt = DianaPoint.getMiddlePoint(lastStartPoint, lastEndPoint);
			if (_updateAsFullyAdjustableForUniqueSegment(pt)) {
				return;
			}
		}

		// Attempt to restore some locations from stored locations relative to start and end object
		// - start point is restored from relative location to start object and put on starting object outline
		// - end point is restored from relative location to end object and put on ending object outline
		// - first point is also restored from relative location to start object if there are more than 5 points
		// - next point is updated accordingly to orientation of second segment
		// - last point is also restored from relative location to end object if there are more than 5 points
		// - previous point is updated accordingly to orientation of previous-last segment

		int indexOfMiddleSegment = polylin.getSegments().indexOf(polylin.getMiddleSegment());

		for (int i = 0; i < polylin.getPointsNb(); i++) {
			if (i <= indexOfMiddleSegment && polylinRelativeToStartObject != null) {
				// That point is closest to start object
				// remember location stored relative to start object
				DianaPoint pointRelativeToStartObject = polylinRelativeToStartObject.getPointAt(i);
				if (pointRelativeToStartObject != null) {
					if (i == 0) {
						// This is the start object, when not, put it on starting object shape outline
						pointRelativeToStartObject = getStartNode().getAllowedStartAreaForConnector(connectorNode)
								.getNearestPoint(pointRelativeToStartObject);
						polylin.updatePointAt(i,
								DianaUtils.convertNormalizedPoint(getStartNode(), pointRelativeToStartObject, connectorNode));
					}
					else if (i == 1 && polylin.getPointsNb() >= 6) {
						DianaPoint firstPoint = DianaUtils.convertNormalizedPoint(getStartNode(), pointRelativeToStartObject,
								connectorNode);
						DianaPoint nextPoint = polylin.getPointAt(2);
						if (polylinRelativeToStartObject.getSegmentAt(1) != null
								&& polylinRelativeToStartObject.getSegmentAt(1).getApproximatedOrientation().isHorizontal()) {
							nextPoint.y = firstPoint.y;
						}
						else {
							nextPoint.x = firstPoint.x;
						}
						polylin.updatePointAt(i, firstPoint);
						polylin.updatePointAt(i + 1, nextPoint);
					}
				}
			}
			else if (polylinRelativeToEndObject != null) {
				// That point is closest to end object
				// remember location stored relative to end object
				DianaPoint pointRelativeToEndObject = polylinRelativeToEndObject.getPointAt(i);
				if (pointRelativeToEndObject != null) {
					if (i == polylin.getPointsNb() - 1) {
						// This is the end object, when not, put it on ending object shape outline
						pointRelativeToEndObject = getEndNode().getAllowedEndAreaForConnector(connectorNode)
								.getNearestPoint(pointRelativeToEndObject);
						polylin.updatePointAt(i, DianaUtils.convertNormalizedPoint(getEndNode(), pointRelativeToEndObject, connectorNode));
					}
					else if (i == polylin.getPointsNb() - 2 && polylin.getPointsNb() >= 6) {
						DianaPoint lastPoint = DianaUtils.convertNormalizedPoint(getEndNode(), pointRelativeToEndObject, connectorNode);
						DianaPoint previousPoint = polylin.getPointAt(polylin.getPointsNb() - 3);
						if (polylinRelativeToEndObject.getSegmentAt(polylinRelativeToEndObject.getSegmentNb() - 2) != null
								&& polylinRelativeToEndObject.getSegmentAt(polylinRelativeToEndObject.getSegmentNb() - 2)
										.getApproximatedOrientation().isHorizontal()) {
							previousPoint.y = previousPoint.y;
						}
						else {
							previousPoint.x = previousPoint.x;
						}
						polylin.updatePointAt(i, lastPoint);
						polylin.updatePointAt(i - 1, previousPoint);
					}
				}
			}
		}

		/*
		 * 
		 * DianaPoint newStartCPLocation = polylin.getFirstPoint(); //startCPRelativeToStartObject =
		 * getStartObject().getShape().outlineIntersect(startCPRelativeToStartObject); startCPRelativeToStartObject =
		 * getStartObject().getShape().getShape().getNearestPoint(startCPRelativeToStartObject); if (startCPRelativeToStartObject != null) {
		 * newStartCPLocation = GraphicalRepresentation.convertNormalizedPoint( getStartObject(), startCPRelativeToStartObject,
		 * getGraphicalRepresentation()); polylin.updatePointAt(0, newStartCPLocation); }
		 * 
		 * DianaPoint newEndCPLocation = polylin.getLastPoint(); //endCPRelativeToEndObject =
		 * getEndObject().getShape().outlineIntersect(endCPRelativeToEndObject); endCPRelativeToEndObject =
		 * getEndObject().getShape().getShape().getNearestPoint(endCPRelativeToEndObject); if (endCPRelativeToEndObject != null) {
		 * newEndCPLocation = GraphicalRepresentation.convertNormalizedPoint( getEndObject(), endCPRelativeToEndObject,
		 * getGraphicalRepresentation()); polylin.updatePointAt(polylin.getPointsNb()-1, newEndCPLocation); }
		 */

		updateAndNormalizeCurrentPolylin();

	}

	public void updateWithNewPolylin(DianaRectPolylin aPolylin) {
		updateWithNewPolylin(aPolylin, false, false);
	}

	public void updateWithNewPolylin(DianaRectPolylin aPolylin, boolean temporary) {
		updateWithNewPolylin(aPolylin, false, temporary);
	}

	public void updateWithNewPolylin(DianaRectPolylin aPolylin, boolean assertLayoutIsValid, boolean temporary) {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Update with polylin with " + aPolylin.getPointsNb() + " points");
		}

		if (aPolylin != null) {
			if (aPolylin.hasExtraPoints()) {
				aPolylin.removeExtraPoints();
			}

			polylin = aPolylin;

			_rebuildControlPoints();

			_connectorChanged(temporary);

			if (!assertLayoutIsValid) {
				updateAndNormalizeCurrentPolylin();
			}
		}
	}

	/**
	 * Internal method restoring valid layout given current polylin. Current polylin is used and adapted to new conditions (eg start and/or
	 * end object have been modified (moved or resized)
	 * 
	 * Normalize obtained polylin at the end of process
	 * 
	 */
	private void updateAndNormalizeCurrentPolylin() {
		/*if (!getGraphicalRepresentation().isRegistered()) {
			return;
		}*/

		if (isCleaningPolylin) {
			// Avoid infinite loop
			return;
		}

		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.finer("updateAndNormalizeCurrentPolylin()");
		}

		isCleaningPolylin = true;

		try {

			// First, check and update start and end control points
			checkAndUpdateStartCP(
					lastKnownCleanPolylinBeforeConnectorRestructuration != null ? lastKnownCleanPolylinBeforeConnectorRestructuration
							: polylin);
			checkAndUpdateEndCP(
					lastKnownCleanPolylinBeforeConnectorRestructuration != null ? lastKnownCleanPolylinBeforeConnectorRestructuration
							: polylin);

			if (polylin.isNormalized()) {
				return;
			}

			if (polylin.isNormalizable()) {
				polylin.normalize();
				for (int i = 0; i < polylin.getPointsNb(); i++) {
					controlPoints.elementAt(i).setPoint(polylin.getPointAt(i));
				}
			}

			else {
				if (LOGGER.isLoggable(Level.FINER)) {
					LOGGER.finer("RectPolylin layout changed");
				}
				// Layout has changed, update with new normalized polylin
				updateWithNewPolylin(polylin.makeNormalizedRectPolylin(), false, true);
			}

		}

		finally {
			isCleaningPolylin = false;
		}

	}

	/**
	 * Internal method rebuilding control points
	 */
	private void _rebuildControlPoints() {
		controlPoints.clear();
		controlAreas.clear();

		if (polylin == null) {
			return;
		}

		int nPoints = polylin.getPoints().size();

		switch (getAdjustability()) {

			case AUTO_LAYOUT:
				for (int i = 0; i < nPoints; i++) {
					DianaPoint p = polylin.getPointAt(i);
					if (i == 0 && getIsStartingLocationDraggable()) {
						controlPoints.add(new AdjustableStartControlPoint(p, this));
					}
					else if (i == nPoints - 1 && getIsEndingLocationDraggable()) {
						controlPoints.add(new AdjustableEndControlPoint(p, this));
					}
					else {
						controlPoints.add(new ConnectorNonAdjustableControlPoint(connectorNode, p));
					}
				}
				break;

			case BASICALLY_ADJUSTABLE:
				for (int i = 0; i < nPoints; i++) {
					DianaPoint p = polylin.getPointAt(i);
					if (i == 0 && getIsStartingLocationDraggable()) {
						controlPoints.add(new AdjustableStartControlPoint(p, this));
					}
					else if (i == nPoints - 1 && getIsEndingLocationDraggable()) {
						controlPoints.add(new AdjustableEndControlPoint(p, this));
					}
					else {
						controlPoints.add(new AdjustableIntermediateControlPoint(p, -1, this));
					}
				}
				controlAreas.add(new RectPolylinAdjustingArea(this));
				/*
				 * if (_crossedPoint != null) { if (crossedControlPoint == null) { crossedControlPoint = new
				 * ConnectorNonAdjustableControlPoint(getGraphicalRepresentation(),_crossedPoint); }
				 * crossedControlPoint.setPoint(_crossedPoint); } if (crossedControlPoint != null) controlAreas.add(crossedControlPoint);
				 */
				break;

			case FULLY_ADJUSTABLE:
				if (nPoints >= 2) {
					if (getIsStartingLocationDraggable()) {
						controlPoints.add(new AdjustableStartControlPoint(polylin.getFirstPoint(), this));
					}
					else {
						controlPoints.add(new ConnectorNonAdjustableControlPoint(connectorNode, polylin.getFirstPoint()));
					}
					if (nPoints == 3) {
						controlPoints.add(new AdjustableMiddleControlPoint(polylin.getPoints().elementAt(1), this));
					}
					else if (nPoints > 3) { // nPoints > 3
						controlPoints.add(new AdjustableFirstControlPoint(polylin.getPoints().elementAt(1), this));
						for (int i = 2; i < nPoints - 2; i++) {
							controlPoints.add(new AdjustableIntermediateControlPoint(polylin.getPoints().elementAt(i), i, this));
						}
						controlPoints.add(new AdjustableLastControlPoint(polylin.getPoints().elementAt(nPoints - 2), this));
					}
					if (getIsEndingLocationDraggable()) {
						controlPoints.add(new AdjustableEndControlPoint(polylin.getLastPoint(), this));
					}
					else {
						controlPoints.add(new ConnectorNonAdjustableControlPoint(connectorNode, polylin.getLastPoint()));
					}
				}
				int nSegments = polylin.getSegments().size();
				if (nSegments < 1) {
					// Pathologic case
					LOGGER.warning("Unexpected situation here");
				}
				else if (nSegments == 1) {
					if ((!getIsStartingLocationFixed() || getConnectorSpecification().getIsStartingLocationDraggable())
							&& (!getIsEndingLocationFixed() || getConnectorSpecification().getIsEndingLocationDraggable())) {
						controlAreas.add(new AdjustableUniqueSegment(polylin.getFirstSegment(), this));
					}
				}
				else {
					if (!getIsStartingLocationFixed() || getIsStartingLocationDraggable()) {
						controlAreas.add(new AdjustableFirstSegment(polylin.getFirstSegment(), this));
					}
					for (int i = 1; i < nSegments - 1; i++) {
						DianaSegment s = polylin.getSegmentAt(i);
						controlAreas.add(new AdjustableIntermediateSegment(s, this));
					}
					if (!getIsEndingLocationFixed() || getIsEndingLocationDraggable()) {
						controlAreas.add(new AdjustableLastSegment(polylin.getLastSegment(), this));
					}
				}
				break;

			default:
				break;
		}

	}

	/**
	 * Internal method generally called at the end of updating process Internally store polylin relative to start and end objects
	 * 
	 */
	public void _connectorChanged(boolean temporary) {
		if (/*getGraphicalRepresentation().isRegistered() &&*/!temporary) {

			if (polylin != null) {
				if (DianaUtils.areElementsConnectedInGraphicalHierarchy(connectorNode, getStartNode())) {
					AffineTransform at1 = DianaUtils.convertNormalizedCoordinatesAT(connectorNode, getStartNode());
					polylinRelativeToStartObject = polylin.transform(at1);
				}
				// Otherwise, don't try to remember layout, edge is probably beeing deleted

				if (DianaUtils.areElementsConnectedInGraphicalHierarchy(connectorNode, getEndNode())) {
					AffineTransform at2 = DianaUtils.convertNormalizedCoordinatesAT(connectorNode, getEndNode());
					polylinRelativeToEndObject = polylin.transform(at2);
				}
				// Otherwise, don't try to remember layout, edge is probably beeing deleted
			}
		}

		if (controlPoints.size() > 0) {
			p_start = controlPoints.firstElement();
			p_end = controlPoints.lastElement();
		}

		if (getMiddleSymbol() != MiddleSymbolType.NONE) {
			updateMiddleSymbolLocationControlPoint();
		}

	}

	/**
	 * Internal method called to update connector asserting layout is defined as FULLY_ADJUSTABLE, and when the last known polylin was a
	 * single segment
	 */
	public boolean _updateAsFullyAdjustableForUniqueSegment(DianaPoint pt) {
		DianaArea startArea = retrieveAllowedStartArea(true);
		if (getIsStartingLocationFixed() && !getIsStartingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			startArea = retrieveStartArea();
		}
		DianaArea endArea = retrieveAllowedEndArea(true);
		if (getIsEndingLocationFixed() && !getIsEndingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			endArea = retrieveEndArea();
		}

		Vector<SimplifiedCardinalDirection> allowedStartOrientations = getAllowedStartOrientations();
		Vector<SimplifiedCardinalDirection> allowedEndOrientations = getAllowedEndOrientations();

		SimplifiedCardinalDirection orientation = null;
		DianaPoint newPt = null;

		for (SimplifiedCardinalDirection o1 : allowedStartOrientations) {
			for (SimplifiedCardinalDirection o2 : allowedEndOrientations) {
				if (o1 == o2.getOpposite()) {
					DianaArea startOrthogonalPerspectiveArea = startArea.getOrthogonalPerspectiveArea(o1);
					DianaArea endOrthogonalPerspectiveArea = endArea.getOrthogonalPerspectiveArea(o2);
					DianaArea intersectArea = startOrthogonalPerspectiveArea.intersect(endOrthogonalPerspectiveArea);
					DianaPoint aPt = intersectArea.getNearestPoint(pt);
					if (aPt != null) {
						orientation = o1;
						newPt = aPt;
						// System.out.println("orientation:"+orientation+" intersectArea="+intersectArea);
					}
				}
			}
		}

		if (orientation != null) {
			DianaPoint p1 = startArea.nearestPointFrom(newPt, orientation.getOpposite());
			DianaPoint p2 = endArea.nearestPointFrom(newPt, orientation);
			updateWithNewPolylin(new DianaRectPolylin(p1, p2), true, false);
			// System.out.println("Found orientation "+orientation+" p1="+p1+" p2="+p2);
			return true;
		}

		// Could not find a layout with straight segment
		return false;
	}

	/**
	 * This method is internally called while updating starting point of polylin.
	 * 
	 * Calling this method assert that we are not sure anymore that start control point is still valid. When not, change this point to a
	 * valid location, and update polylin accordingly (eventually recompute new layout when required: modified orientation).
	 * 
	 * @param initialPolylin
	 *            Polylin to take under account to recreate new layout
	 * 
	 */
	public void checkAndUpdateStartCP(DianaRectPolylin initialPolylin) {
		if (p_start == null) {
			_connectorChanged(true);
		}
		if (p_start == null) {
			return;
		}

		if (!DianaUtils.areElementsConnectedInGraphicalHierarchy(getStartNode(), connectorNode)) {
			// Dont't try to do anything, edge is probably beeing deleted
			return;
		}

		// Retrieve start area in connector coordinates system
		DianaArea startArea = retrieveStartArea();
		DianaArea allowedStartArea = retrieveAllowedStartArea(true);

		if (getIsStartingLocationFixed() && getFixedStartLocation() != null && !getIsStartingLocationDraggable()) {
			allowedStartArea = getFixedStartLocation();
		}

		if (polylin.getPointsNb() == 0) {
			return;
		}

		// Retrieve control point location to update
		DianaPoint startCPLocation = polylin.getPointAt(0);
		if (startCPLocation == null) {
			return;
		}

		// Compute new location by computing nearest point of oldLocation
		// in end area (if this location was valid, change nothing)
		// DianaPoint oldCP = startCPLocation.clone();
		startCPLocation = startArea.getNearestPoint(startCPLocation);
		// logger.info("checkAndUpdateStartCP() from "+oldCP+" to "+startCPLocation);

		// Update polylin and end control point with this new location
		polylin.updatePointAt(0, startCPLocation);

		p_start.setPoint(startCPLocation);

		if (getIsStartingLocationFixed()) { // Don't forget this !!!
			setFixedStartLocation(DianaUtils.convertNormalizedPoint(connectorNode, startCPLocation, getStartNode()));
		}

		// Update for start cp
		SimplifiedCardinalDirection orientation = polylin.getOrientationOfSegment(0);

		if (orientation != null) {
			// This new location is valid
			// (the last segment is still horizontal or vertical)
			return;
		}
		// Start control point has moved (the first segment is not horizontal nor vertical anymore)

		// Find new orientation by minimizing distance between
		// current start point location and the nearest point of
		// all anchor location of all possible directions
		SimplifiedCardinalDirection newOrientation = null;
		double bestDistance = Double.POSITIVE_INFINITY;
		for (SimplifiedCardinalDirection o : getAllowedStartOrientations()) {
			double distance = DianaPoint.distance(startCPLocation, startArea.getAnchorAreaFrom(o).getNearestPoint(startCPLocation));
			if (distance < bestDistance) {
				newOrientation = o;
				bestDistance = distance;
			}
		}

		// debugPolylin = null;

		// Retrieve next point (also called "first" control point)
		if (polylin.getSegmentAt(0) == null) {
			LOGGER.warning("Unexpected null first segment. Abort.");
			return;
		}
		DianaPoint nextPoint = polylin.getSegmentAt(0).getP2();

		if (allowedStartArea.getOrthogonalPerspectiveArea(newOrientation).containsPoint(nextPoint)
		/* || (getIsStartingLocationFixed() && getFixedStartLocation() != null) */) {
			// The general layout of polylin will not change, since next point was
			// already located in this orthogonal perspective area
			// We just need here to update previous point according to new end point location
			DianaPoint newPoint = new DianaPoint(nextPoint);
			if (newOrientation.isHorizontal()) {
				newPoint.setY(startCPLocation.y);
			}
			else if (newOrientation.isVertical()) {
				newPoint.setX(startCPLocation.x);
			}
			polylin.updatePointAt(1, newPoint);
			controlPoints.elementAt(1).setPoint(newPoint);

		}
		else {
			// In this case, the situation is worse, that means that start orientation has changed
			// We need to recompute a new layout for the polylin

			// Recompute general layout of rect polylin

			if (initialPolylin.getSegmentNb() > 2) {
				DianaPoint toPoint = initialPolylin.getPointAt(2);
				SimplifiedCardinalDirection toPointOrientation = initialPolylin.getApproximatedOrientationOfSegment(2).getOpposite();
				DianaRectPolylin appendingPath;
				appendingPath = new DianaRectPolylin(startCPLocation, newOrientation, toPoint, toPointOrientation, true,
						getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap());

				// debugPolylin = appendingPath;
				DianaRectPolylin mergedPolylin = mergePolylins(appendingPath, 0, appendingPath.getPointsNb() - 2, initialPolylin, 2,
						initialPolylin.getPointsNb() - 1);
				updateWithNewPolylin(mergedPolylin, false, true);
			}
			else if (initialPolylin.getSegmentNb() > 1) {
				DianaPoint toPoint = initialPolylin.getPointAt(2);
				SimplifiedCardinalDirection toPointOrientation = initialPolylin.getApproximatedOrientationOfSegment(1).getOpposite();
				DianaRectPolylin appendingPath = new DianaRectPolylin(startCPLocation, newOrientation, toPoint, toPointOrientation, true,
						getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap());

				// debugPolylin = appendingPath;
				DianaRectPolylin mergedPolylin = mergePolylins(appendingPath, 0, appendingPath.getPointsNb() - 2, initialPolylin, 2,
						initialPolylin.getPointsNb() - 1);
				updateWithNewPolylin(mergedPolylin, false, true);
			}
			else {
				DianaPoint toPoint = initialPolylin.getPointAt(1);
				toPoint = retrieveEndArea().getNearestPoint(toPoint);
				newOrientation = initialPolylin.getApproximatedOrientationOfSegment(0);
				SimplifiedCardinalDirection toPointOrientation = newOrientation.getOpposite();
				DianaRectPolylin newPolylin = new DianaRectPolylin(startCPLocation, newOrientation, toPoint, toPointOrientation, true,
						getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap());

				updateWithNewPolylin(newPolylin, false, true);
			}
		}
	}

	/**
	 * This method is internally called while updating ending point of polylin.
	 * 
	 * Calling this method assert that we are not sure anymore that end control point is still valid. When not, change this point to a valid
	 * location, and update polylin accordingly (eventually recompute new layout when required: modified orientation).
	 * 
	 * @param initialPolylin
	 *            Polylin to take under account to recreate new layout
	 * 
	 */
	public void checkAndUpdateEndCP(DianaRectPolylin initialPolylin) {
		if (p_end == null) {
			_connectorChanged(true);
		}
		if (p_end == null) {
			return;
		}

		if (!DianaUtils.areElementsConnectedInGraphicalHierarchy(getEndNode(), connectorNode)) {
			// Dont't try to do anything, edge is probably beeing deleted
			return;
		}
		// Retrieve end area in connector coordinates system
		DianaArea endArea = retrieveEndArea();
		DianaArea allowedEndArea = retrieveAllowedEndArea(true);

		if (getIsEndingLocationFixed() && getFixedEndLocation() != null && !getIsEndingLocationDraggable()) {
			allowedEndArea = getFixedEndLocation();
		}

		if (polylin.getPointsNb() == 0) {
			return;
		}

		// Retrieve control point location to update
		DianaPoint endCPLocation = polylin.getPointAt(polylin.getPointsNb() - 1);

		if (endCPLocation == null) {
			return;
		}

		// Compute new location by computing nearest point of oldLocation
		// in end area (if this location was valid, change nothing)
		// logger.info("endArea="+endArea);
		// DianaPoint oldCP = endCPLocation.clone();
		endCPLocation = endArea.getNearestPoint(endCPLocation);
		// logger.info("checkAndUpdateEndCP() from "+oldCP+" to "+endCPLocation);

		// Update polylin and end control point with this new location
		polylin.updatePointAt(polylin.getPointsNb() - 1, endCPLocation);
		p_end.setPoint(endCPLocation);

		if (getIsEndingLocationFixed()) { // Don't forget this !!!
			setFixedEndLocation(DianaUtils.convertNormalizedPoint(connectorNode, endCPLocation, getEndNode()));
		}

		// Look for orientation of this newly computed segment
		SimplifiedCardinalDirection orientation = polylin.getOrientationOfSegment(polylin.getSegmentNb() - 1);

		if (orientation != null) {
			// This new location is valid
			// (the last segment is still horizontal or vertical)
			return;
		}
		// End control point has moved (the last segment is not horizontal nor vertical anymore)

		// Find new orientation by minimizing distance between
		// current end point location and the nearest point of
		// all anchor location of all possible directions
		SimplifiedCardinalDirection newOrientation = null;
		double bestDistance = Double.POSITIVE_INFINITY;
		for (SimplifiedCardinalDirection o : getAllowedEndOrientations()) {
			double distance = DianaPoint.distance(endCPLocation, endArea.getAnchorAreaFrom(o).getNearestPoint(endCPLocation));
			if (distance < bestDistance - DianaGeometricObject.EPSILON) {
				newOrientation = o;
				bestDistance = distance;
			}
		}

		// debugPolylin = null;

		// Retrieve previous point (also called "last" control point)
		if (polylin.getSegmentAt(polylin.getSegmentNb() - 1) == null) {
			LOGGER.warning("Unexpected null last segment. Abort.");
			return;
		}
		DianaPoint previousPoint = polylin.getSegmentAt(polylin.getSegmentNb() - 1).getP1();

		if (allowedEndArea.getOrthogonalPerspectiveArea(newOrientation).containsPoint(previousPoint)
		/* || (getIsEndingLocationFixed() && getFixedEndLocation() != null) */) {
			// The general layout of polylin will not change, since previous point was
			// already located in this orthogonal perspective area
			// We just need here to update previous point according to new end point location
			DianaPoint newPoint = new DianaPoint(previousPoint);
			if (newOrientation.isHorizontal()) {
				newPoint.setY(endCPLocation.y);
			}
			else if (newOrientation.isVertical()) {
				newPoint.setX(endCPLocation.x);
			}
			polylin.updatePointAt(polylin.getPointsNb() - 2, newPoint);
			controlPoints.elementAt(polylin.getPointsNb() - 2).setPoint(newPoint);
		}
		else {
			// In this case, the situation is worse, that means that end orientation has changed
			// We need to recompute a new layout for the polylin

			// Recompute general layout of rect polylin

			if (initialPolylin.getSegmentNb() > 2) {
				DianaPoint toPoint = initialPolylin.getPointAt(initialPolylin.getPointsNb() - 3);
				SimplifiedCardinalDirection toPointOrientation = initialPolylin
						.getApproximatedOrientationOfSegment(initialPolylin.getPointsNb() - 3);
				DianaRectPolylin appendingPath;
				appendingPath = new DianaRectPolylin(toPoint, toPointOrientation, endCPLocation, newOrientation, true,
						getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap());

				// debugPolylin = appendingPath;
				DianaRectPolylin mergedPolylin = mergePolylins(initialPolylin, 0, initialPolylin.getPointsNb() - 3, appendingPath, 1,
						appendingPath.getPointsNb() - 1);
				updateWithNewPolylin(mergedPolylin, false, true);
			}
			else if (initialPolylin.getSegmentNb() > 1) {
				DianaPoint toPoint = initialPolylin.getPointAt(initialPolylin.getPointsNb() - 3);
				SimplifiedCardinalDirection toPointOrientation = initialPolylin
						.getApproximatedOrientationOfSegment(initialPolylin.getPointsNb() - 3);
				DianaRectPolylin appendingPath = new DianaRectPolylin(toPoint, toPointOrientation, endCPLocation, newOrientation, true,
						getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap());

				// debugPolylin = appendingPath;

				// DianaRectPolylin mergedPolylin = mergePolylins(initialPolylin, 0, initialPolylin.getPointsNb()-2, appendingPath, 1,
				// appendingPath.getPointsNb()-1);
				// updateWithNewPolylin(mergedPolylin);

				updateWithNewPolylin(appendingPath, false, true);
			}
			else {
				DianaPoint fromPoint = initialPolylin.getPointAt(0);
				fromPoint = retrieveStartArea().getNearestPoint(fromPoint);
				newOrientation = initialPolylin.getApproximatedOrientationOfSegment(0);
				SimplifiedCardinalDirection toPointOrientation = newOrientation.getOpposite();
				DianaRectPolylin newPolylin = new DianaRectPolylin(fromPoint, newOrientation, endCPLocation, toPointOrientation, true,
						getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap());

				updateWithNewPolylin(newPolylin, false, true);
			}
		}
	}

	/**
	 * Compute a new polylin by concatening supplied polylins and given indexes
	 * 
	 * @param p1
	 * @param startIndex1
	 * @param endIndex1
	 * @param p2
	 * @param startIndex2
	 * @param endIndex2
	 * @return
	 */
	public DianaRectPolylin mergePolylins(DianaRectPolylin p1, int startIndex1, int endIndex1, DianaRectPolylin p2, int startIndex2,
			int endIndex2) {
		DianaRectPolylin returned = new DianaRectPolylin();
		returned.setOverlapX(getOverlapXResultingFromPixelOverlap());
		returned.setOverlapY(getOverlapYResultingFromPixelOverlap());
		for (int i = startIndex1; i <= endIndex1; i++) {
			returned.addToPoints(p1.getPointAt(i));
		}
		for (int i = startIndex2; i <= endIndex2; i++) {
			returned.addToPoints(p2.getPointAt(i));
		}
		return returned;
	}

	/**
	 * Simplify layout of current polylin asserting that two points are safelly removable.
	 * 
	 * @param index
	 */
	public void _simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(int index) {
		_simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(index, null);
	}

	/**
	 * Simplify layout of current polylin asserting that two points are safelly removable. If a location is given, this location will be
	 * used to adapt position of previous and next point asserting that they must be located on an horizontal or vertical segment.
	 * 
	 * @param index
	 * @param newCPLocation
	 */
	public void _simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(int index, DianaPoint newCPLocation) {
		SimplifiedCardinalDirection relatedSegmentOrientation = getCurrentPolylin().getApproximatedOrientationOfSegment(index);
		getCurrentPolylin().removePointAtIndex(index);
		getCurrentPolylin().removePointAtIndex(index);
		controlPoints.remove(index);
		controlPoints.remove(index);
		if (newCPLocation != null) {
			if (relatedSegmentOrientation.isHorizontal()) {
				getCurrentPolylin().updatePointAt(index - 1, new DianaPoint(newCPLocation.x, getCurrentPolylin().getPointAt(index - 1).y));
				getCurrentPolylin().updatePointAt(index, new DianaPoint(newCPLocation.x, getCurrentPolylin().getPointAt(index).y));
			}
			else if (relatedSegmentOrientation.isVertical()) {
				getCurrentPolylin().updatePointAt(index - 1, new DianaPoint(getCurrentPolylin().getPointAt(index - 1).x, newCPLocation.y));
				getCurrentPolylin().updatePointAt(index, new DianaPoint(getCurrentPolylin().getPointAt(index).x, newCPLocation.y));
			}
		}
		updateWithNewPolylin(getCurrentPolylin(), false, true);
	}

	/**
	 * Return start point, relative to start object
	 * 
	 * @return
	 */
	@Override
	public DianaPoint getStartLocation() {
		if (polylin == null) {
			return null;
		}
		DianaPoint returned = DianaUtils.convertNormalizedPoint(connectorNode, polylin.getFirstPoint(), getStartNode());
		returned = getStartNode().getDianaShape().getNearestPoint(returned);
		return returned;
	}

	/**
	 * Return end point, relative to end object
	 * 
	 * @return
	 */
	@Override
	public DianaPoint getEndLocation() {
		if (polylin == null) {
			return null;
		}
		DianaPoint returned = DianaUtils.convertNormalizedPoint(connectorNode, polylin.getLastPoint(), getEndNode());
		returned = getEndNode().getDianaShape().getNearestPoint(returned);
		return returned;
	}

	public boolean getWasManuallyAdjusted() {
		return wasManuallyAdjusted;
	}

	public void setWasManuallyAdjusted(boolean aFlag) {
		wasManuallyAdjusted = aFlag;
		if (!wasManuallyAdjusted) {
			if (polylin != null) {
				updateWithNewPolylin(polylin);
			}
			// if (isAdjustable) polylin = null;
			if (connectorNode != null) {
				updateLayout();
				if (polylin != null) {
					updateWithNewPolylin(polylin);
				}
				connectorNode.notifyConnectorModified();
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		// If ConnectorNode is either null or deleted, ignore this event
		// (This might happen during some edition phases with inspectors)
		if (getConnectorNode() == null) {
			// logger.warning("Called getPropertyValue() for null ConnectorNode");
			return;
		}
		else if (getConnectorNode().isDeleted()) {
			// logger.warning("Called getPropertyValue() for deleted ConnectorNode");
			return;
		}

		super.propertyChange(evt);

		if (temporaryIgnoredObservables.contains(evt.getSource())) {
			// System.out.println("IGORE NOTIFICATION " + notification);
			return;
		}

		// logger.info("propertyChanged() with " + evt + " adjustability=" + getAdjustability());

		if (evt.getSource() == getConnectorSpecification()) {

			if (evt.getPropertyName().equals(RectPolylinConnectorSpecification.RECT_POLYLIN_CONSTRAINTS.getName())) {
				p_start = null;
				p_end = null;
				updateLayout();
				connectorNode.notifyConnectorModified();
			}
			else if (evt.getPropertyName() == RectPolylinConnectorSpecification.STRAIGHT_LINE_WHEN_POSSIBLE.getName()
					|| evt.getPropertyName() == RectPolylinConnectorSpecification.START_ORIENTATION.getName()
					|| evt.getPropertyName() == RectPolylinConnectorSpecification.END_ORIENTATION.getName()
					|| evt.getPropertyName() == RectPolylinConnectorSpecification.PIXEL_OVERLAP.getName()
					|| evt.getPropertyName() == RectPolylinConnectorSpecification.IS_ROUNDED.getName()
					|| evt.getPropertyName() == RectPolylinConnectorSpecification.ARC_SIZE.getName()) {
				updateLayout();
				connectorNode.notifyConnectorModified();
			}
			else if (evt.getPropertyName() == RectPolylinConnectorSpecification.IS_STARTING_LOCATION_DRAGGABLE.getName()
					|| evt.getPropertyName() == RectPolylinConnectorSpecification.IS_ENDING_LOCATION_DRAGGABLE.getName()
			/*|| evt.getPropertyName() == RectPolylinConnectorSpecification.FIXED_START_LOCATION.getName()
			|| evt.getPropertyName() == RectPolylinConnectorSpecification.FIXED_END_LOCATION.getName()*/) {
				updateLayout();
				// Force control points to be rebuild in order to get draggable feature
				_rebuildControlPoints();
				connectorNode.notifyConnectorModified();
			}
			else if (evt.getPropertyName() == ConnectorSpecification.FIXED_START_LOCATION.getName()
					|| evt.getPropertyName() == ConnectorSpecification.FIXED_END_LOCATION.getName()) {
				// updateLayout();
				// connectorNode.notifyConnectorModified();
				refreshConnector();
			}
			else if (evt.getPropertyName() == RectPolylinConnectorSpecification.IS_STARTING_LOCATION_FIXED.getName()) {
				if (getIsStartingLocationFixed() && fixedStartLocationRelativeToStartObject == null && p_start != null) {
					// In this case, we can initialize fixed start location to its current value
					fixedStartLocationRelativeToStartObject = DianaUtils.convertNormalizedPoint(connectorNode, p_start.getPoint(),
							getStartNode());
				}
				updateLayout();
				_rebuildControlPoints();
				connectorNode.notifyConnectorModified();
			}
			else if (evt.getPropertyName() == RectPolylinConnectorSpecification.IS_ENDING_LOCATION_FIXED.getName()) {
				if (getIsEndingLocationFixed() && fixedEndLocationRelativeToEndObject == null && p_end != null) {
					// In this case, we can initialize fixed start location to its current value
					fixedEndLocationRelativeToEndObject = DianaUtils.convertNormalizedPoint(connectorNode, p_end.getPoint(), getEndNode());
				}
				updateLayout();
				_rebuildControlPoints();
				connectorNode.notifyConnectorModified();
			}
			else if (evt.getPropertyName() == RectPolylinConnectorSpecification.ADJUSTABILITY.getName()) {
				if (polylin != null) {
					updateWithNewPolylin(polylin);
				}
				updateLayout();
				if (polylin != null) {
					updateWithNewPolylin(polylin);
				}
				connectorNode.notifyConnectorModified();

				if (getAdjustability() != RectPolylinAdjustability.FULLY_ADJUSTABLE) {
					setWasManuallyAdjusted(false);
				}

				connectorNode.clearControlAreas();
				connectorNode.getControlAreas();
			}
		}
	}

	public RectPolylinConstraints getRectPolylinConstraints() {
		return getPropertyValue(RectPolylinConnectorSpecification.RECT_POLYLIN_CONSTRAINTS);
	}

	public void setRectPolylinConstraints(RectPolylinConstraints aRectPolylinConstraints) {
		setPropertyValue(RectPolylinConnectorSpecification.RECT_POLYLIN_CONSTRAINTS, aRectPolylinConstraints);
	}

	public boolean getStraightLineWhenPossible() {
		return getPropertyValue(RectPolylinConnectorSpecification.STRAIGHT_LINE_WHEN_POSSIBLE);
	}

	public void setStraightLineWhenPossible(boolean aFlag) {
		setPropertyValue(RectPolylinConnectorSpecification.STRAIGHT_LINE_WHEN_POSSIBLE, aFlag);
	}

	public RectPolylinAdjustability getAdjustability() {
		return getPropertyValue(RectPolylinConnectorSpecification.ADJUSTABILITY);
	}

	public void setAdjustability(RectPolylinAdjustability anAdjustability) {
		setPropertyValue(RectPolylinConnectorSpecification.ADJUSTABILITY, anAdjustability);
	}

	public SimplifiedCardinalDirection getEndOrientation() {
		return getPropertyValue(RectPolylinConnectorSpecification.END_ORIENTATION);
	}

	public void setEndOrientation(SimplifiedCardinalDirection anOrientation) {
		setPropertyValue(RectPolylinConnectorSpecification.END_ORIENTATION, anOrientation);
	}

	public SimplifiedCardinalDirection getStartOrientation() {
		return getPropertyValue(RectPolylinConnectorSpecification.START_ORIENTATION);
	}

	public void setStartOrientation(SimplifiedCardinalDirection anOrientation) {
		setPropertyValue(RectPolylinConnectorSpecification.START_ORIENTATION, anOrientation);
	}

	public boolean getIsRounded() {
		return getPropertyValue(RectPolylinConnectorSpecification.IS_ROUNDED);
	}

	public void setIsRounded(boolean aFlag) {
		setPropertyValue(RectPolylinConnectorSpecification.IS_ROUNDED, aFlag);
	}

	public int getArcSize() {
		return getPropertyValue(RectPolylinConnectorSpecification.ARC_SIZE);
	}

	public void setArcSize(int anArcSize) {
		setPropertyValue(RectPolylinConnectorSpecification.ARC_SIZE, anArcSize);
	}

	public DianaPoint getCrossedControlPoint() {
		return getPropertyValue(RectPolylinConnectorSpecification.CROSSED_CONTROL_POINT);
	}

	public void setCrossedControlPoint(DianaPoint aPoint) {
		setPropertyValue(RectPolylinConnectorSpecification.CROSSED_CONTROL_POINT, aPoint);
	}

	public int getPixelOverlap() {
		return getPropertyValue(RectPolylinConnectorSpecification.PIXEL_OVERLAP);
	}

	public void setPixelOverlap(int aPixelOverlap) {
		setPropertyValue(RectPolylinConnectorSpecification.PIXEL_OVERLAP, aPixelOverlap);
	}

	public DianaRectPolylin getPolylin() {
		return getPropertyValue(RectPolylinConnectorSpecification.POLYLIN);
	}

	public void setPolylin(DianaRectPolylin aPolylin) {
		setPropertyValue(RectPolylinConnectorSpecification.POLYLIN, aPolylin);
	}

}
