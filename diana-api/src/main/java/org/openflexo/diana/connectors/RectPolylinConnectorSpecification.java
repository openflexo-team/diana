/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-api, a component of the software infrastructure 
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

package org.openflexo.diana.connectors;

import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.GRProperty;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectPolylin;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

/**
 * A RectPolylinConnectorSpecification is a connector joining 2 shapes with a path of orthogonal segments (this connector is encoded as a
 * {@link DianaRectPolylin} instance).
 * 
 * This connector has many configuration parameters.
 * 
 * Mainly, there are three principal modes, regarding user control
 * 
 * <ul>
 * <li>automatic layout: the layout is continuously recomputed and updated, given location and orientation constraints (adjustability =
 * AUTO_LAYOUT)</li>
 * <li>semi-adjustable layout: a user is editing the layout by moving a single control point (any point located on connector). Locations and
 * orientation constraints are respected (adjustability = BASICALLY_ADJUSTABLE)</li>
 * <li>adjustable layout: layout is fully controlled by user: layout is editable by moving, adding and removing control points, and segments
 * are translatable. Locations and orientation constraints are respected</li>
 * </ul>
 * 
 * Layout mode is configurable by using {@link #setAdjustability(RectPolylinAdjustability)} method (default is automatic layout).
 * 
 * This connector encodes many control points:
 * <ul>
 * <li>start control point is the starting control point, located on the outline of starting shape</li>
 * <li>first control point is the (eventual) first control point located outside both shapes (just after start control point)</li>
 * <li>end control point is the ending control point, located on the outline of ending shape</li>
 * <li>last control point is the (eventual) last control point located outside both shapes (just before end control point)</li>
 * <li>an intermediate control point is an other control point, which is not the start, first, last or end control point</li>
 * </ul>
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "RectPolylinConnectorSpecification")
public interface RectPolylinConnectorSpecification extends ConnectorSpecification {

	// Property keys

	@PropertyIdentifier(type = RectPolylinConstraints.class)
	public static final String RECT_POLYLIN_CONSTRAINTS_KEY = "rectPolylinConstraints";
	@PropertyIdentifier(type = Boolean.class)
	public static final String STRAIGHT_LINE_WHEN_POSSIBLE_KEY = "straightLineWhenPossible";
	@PropertyIdentifier(type = RectPolylinAdjustability.class)
	public static final String ADJUSTABILITY_KEY = "adjustability";
	@PropertyIdentifier(type = SimplifiedCardinalDirection.class)
	public static final String START_ORIENTATION_KEY = "startOrientation";
	@PropertyIdentifier(type = SimplifiedCardinalDirection.class)
	public static final String END_ORIENTATION_KEY = "endOrientation";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_ROUNDED_KEY = "isRounded";
	@PropertyIdentifier(type = Integer.class)
	public static final String ARC_SIZE_KEY = "arcSize";
	@PropertyIdentifier(type = DianaPoint.class)
	public static final String CROSSED_CONTROL_POINT_KEY = "crossedControlPoint";
	@PropertyIdentifier(type = DianaRectPolylin.class)
	public static final String POLYLIN_KEY = "polylin";
	@PropertyIdentifier(type = Integer.class)
	public static final String PIXEL_OVERLAP_KEY = "pixelOverlap";

	public static GRProperty<RectPolylinConstraints> RECT_POLYLIN_CONSTRAINTS = GRProperty
			.getGRParameter(RectPolylinConnectorSpecification.class, RECT_POLYLIN_CONSTRAINTS_KEY, RectPolylinConstraints.class);
	public static GRProperty<Boolean> STRAIGHT_LINE_WHEN_POSSIBLE = GRProperty.getGRParameter(RectPolylinConnectorSpecification.class,
			STRAIGHT_LINE_WHEN_POSSIBLE_KEY, Boolean.class);
	public static GRProperty<RectPolylinAdjustability> ADJUSTABILITY = GRProperty.getGRParameter(RectPolylinConnectorSpecification.class,
			ADJUSTABILITY_KEY, RectPolylinAdjustability.class);
	public static GRProperty<SimplifiedCardinalDirection> START_ORIENTATION = GRProperty
			.getGRParameter(RectPolylinConnectorSpecification.class, START_ORIENTATION_KEY, SimplifiedCardinalDirection.class);
	public static GRProperty<SimplifiedCardinalDirection> END_ORIENTATION = GRProperty
			.getGRParameter(RectPolylinConnectorSpecification.class, END_ORIENTATION_KEY, SimplifiedCardinalDirection.class);
	public static GRProperty<Boolean> IS_ROUNDED = GRProperty.getGRParameter(RectPolylinConnectorSpecification.class, IS_ROUNDED_KEY,
			Boolean.class);
	public static GRProperty<Integer> ARC_SIZE = GRProperty.getGRParameter(RectPolylinConnectorSpecification.class, ARC_SIZE_KEY,
			Integer.class);
	public static GRProperty<DianaPoint> CROSSED_CONTROL_POINT = GRProperty.getGRParameter(RectPolylinConnectorSpecification.class,
			CROSSED_CONTROL_POINT_KEY, DianaPoint.class);
	public static GRProperty<DianaRectPolylin> POLYLIN = GRProperty.getGRParameter(RectPolylinConnectorSpecification.class, POLYLIN_KEY,
			DianaRectPolylin.class);
	public static GRProperty<Integer> PIXEL_OVERLAP = GRProperty.getGRParameter(RectPolylinConnectorSpecification.class, PIXEL_OVERLAP_KEY,
			Integer.class);

	/*public static enum RectPolylinConnectorParameters implements GRProperty {
		rectPolylinConstraints,
		straightLineWhenPossible,
		adjustability,
		startOrientation,
		endOrientation,
		isRounded,
		arcSize,
		isStartingLocationFixed,
		isEndingLocationFixed,
		isStartingLocationDraggable,
		isEndingLocationDraggable,
		crossedControlPoint,
		fixedStartLocation,
		fixedEndLocation,
		polylin,
		pixelOverlap
	}*/

	public static enum RectPolylinAdjustability {
		AUTO_LAYOUT, BASICALLY_ADJUSTABLE, FULLY_ADJUSTABLE
	}

	public static enum RectPolylinConstraints {
		NONE,
		ORTHOGONAL_LAYOUT,
		HORIZONTAL_FIRST,
		VERTICAL_FIRST,
		HORIZONTAL_OR_VERTICAL,
		HORIZONTAL_LAYOUT,
		VERTICAL_LAYOUT,
		ORIENTATIONS_FIXED,
		START_ORIENT_FIXED,
		END_ORIENT_FIXED
	}

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = RECT_POLYLIN_CONSTRAINTS_KEY)
	@XMLAttribute
	public RectPolylinConstraints getRectPolylinConstraints();

	@Setter(value = RECT_POLYLIN_CONSTRAINTS_KEY)
	public void setRectPolylinConstraints(RectPolylinConstraints aRectPolylinConstraints);

	public void setRectPolylinConstraints(RectPolylinConstraints someRectPolylinConstraints, SimplifiedCardinalDirection aStartOrientation,
			SimplifiedCardinalDirection aEndOrientation);

	@Getter(value = STRAIGHT_LINE_WHEN_POSSIBLE_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getStraightLineWhenPossible();

	@Setter(value = STRAIGHT_LINE_WHEN_POSSIBLE_KEY)
	public void setStraightLineWhenPossible(boolean aFlag);

	@Getter(value = ADJUSTABILITY_KEY)
	@XMLAttribute
	public RectPolylinAdjustability getAdjustability();

	@Setter(value = ADJUSTABILITY_KEY)
	public void setAdjustability(RectPolylinAdjustability anAdjustability);

	@Getter(value = END_ORIENTATION_KEY)
	@XMLAttribute
	public SimplifiedCardinalDirection getEndOrientation();

	@Setter(value = END_ORIENTATION_KEY)
	public void setEndOrientation(SimplifiedCardinalDirection anOrientation);

	@Getter(value = START_ORIENTATION_KEY)
	@XMLAttribute
	public SimplifiedCardinalDirection getStartOrientation();

	@Setter(value = START_ORIENTATION_KEY)
	public void setStartOrientation(SimplifiedCardinalDirection anOrientation);

	@Getter(value = IS_ROUNDED_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsRounded();

	@Setter(value = IS_ROUNDED_KEY)
	public void setIsRounded(boolean aFlag);

	@Getter(value = ARC_SIZE_KEY, defaultValue = "10")
	@XMLAttribute
	public int getArcSize();

	@Setter(value = ARC_SIZE_KEY)
	public void setArcSize(int anArcSize);

	@Getter(value = CROSSED_CONTROL_POINT_KEY, isStringConvertable = true)
	@XMLAttribute
	public DianaPoint getCrossedControlPoint();

	@Setter(value = CROSSED_CONTROL_POINT_KEY)
	public void setCrossedControlPoint(DianaPoint aPoint);

	@Getter(value = PIXEL_OVERLAP_KEY, defaultValue = "" + DianaConstants.DEFAULT_RECT_POLYLIN_PIXEL_OVERLAP)
	@XMLAttribute
	public int getPixelOverlap();

	@Setter(value = PIXEL_OVERLAP_KEY)
	public void setPixelOverlap(int aPixelOverlap);

	@Getter(value = POLYLIN_KEY, ignoreType = true)
	public DianaRectPolylin getPolylin();

	@Setter(value = POLYLIN_KEY)
	public void setPolylin(DianaRectPolylin aPolylin);

	// *******************************************************************************
	// * Utils
	// *******************************************************************************

	public boolean getIsAdjustable();

	public void setIsAdjustable(boolean aFlag);

	/*public boolean getWasManuallyAdjusted();
	
	public void setWasManuallyAdjusted(boolean aFlag);*/

	/**
	 * 
	 * @return angle expressed in radians
	 */
	// public double getMiddleSymbolAngle(ConnectorNode<?> node);

	/*public DianaRectPolylin getCurrentPolylin();
	
	public void manuallySetPolylin(DianaRectPolylin aPolylin);
	
	public ControlPoint getEndControlPoint();
	
	public ControlPoint getStartControlPoint();
	
	public DianaPoint getCrossedControlPointOnRoundedArc();
	
	public Vector<SimplifiedCardinalDirection> getAllowedStartOrientations();*/

	/**
	 * Return all allowed start orientation as this is defined in orientation constraint Does NOT take under account the fact that starting
	 * position could have been fixed and can also induced an other start orientation.
	 * 
	 * @return
	 */
	// public Vector<SimplifiedCardinalDirection> getPrimitiveAllowedStartOrientations();

	/*public Vector<SimplifiedCardinalDirection> getExcludedStartOrientations();
	
	public Vector<SimplifiedCardinalDirection> getAllowedEndOrientations();
	
	public Vector<SimplifiedCardinalDirection> getPrimitiveAllowedEndOrientations();
	
	public Vector<SimplifiedCardinalDirection> getExcludedEndOrientations();
	
	public Vector<ControlPoint> _getControlPoints();
	
	public void updateLayout();
	
	public void updateWithNewPolylin(DianaRectPolylin aPolylin);
	
	public void updateWithNewPolylin(DianaRectPolylin aPolylin, boolean temporary);
	
	public void updateWithNewPolylin(DianaRectPolylin aPolylin, boolean assertLayoutIsValid, boolean temporary);
	
	public boolean _updateAsFullyAdjustableForUniqueSegment(DianaPoint pt);
	
	public void _connectorChanged(boolean temporary);*/

	/**
	 * Compute and return start area outline, in the connector coordinates system
	 * 
	 * If some orientation constraints are defined, return portion of start area outline matching allowed orientations
	 * 
	 * If starting location is fixed return this location
	 * 
	 * @return DianaArea
	 */
	// public DianaArea retrieveStartArea();

	/**
	 * Compute and return allowed start area, in the connector coordinates system If some orientation constraints are defined, return
	 * portion of start area outline matching allowed orientations
	 * 
	 * @return DianaArea
	 */
	// public DianaArea retrieveAllowedStartArea(boolean takeFixedControlPointUnderAccount);

	/**
	 * Compute and return end area outline, in the connector coordinates system
	 * 
	 * If some orientation constraints are defined, return portion of end area outline matching allowed orientations
	 * 
	 * If starting location is fixed return this location
	 * 
	 * @return DianaArea
	 */
	// public DianaArea retrieveEndArea();

	/**
	 * Compute and return allowed end area, in the connector coordinates system If some orientation constraints are defined, return portion
	 * of end area outline matching allowed orientations
	 * 
	 * @return DianaArea
	 */
	// public DianaArea retrieveAllowedEndArea(boolean takeFixedControlPointUnderAccount);

	// public double getOverlapXResultingFromPixelOverlap(ConnectorNode<?> node);

	// public double getOverlapYResultingFromPixelOverlap(ConnectorNode<?> node);

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
	// public void checkAndUpdateStartCP(DianaRectPolylin initialPolylin);

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
	// public void checkAndUpdateEndCP(DianaRectPolylin initialPolylin);

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
	// public DianaRectPolylin mergePolylins(DianaRectPolylin p1, int startIndex1, int endIndex1, DianaRectPolylin p2, int startIndex2, int
	// endIndex2);

	/**
	 * Simplify layout of current polylin asserting that two points are safelly removable.
	 * 
	 * @param index
	 */
	// public void _simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(int index);

	/**
	 * Simplify layout of current polylin asserting that two points are safelly removable. If a location is given, this location will be
	 * used to adapt position of previous and next point asserting that they must be located on an horizontal or vertical segment.
	 * 
	 * @param index
	 * @param newCPLocation
	 */
	// public void _simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(int index, DianaPoint newCPLocation);
}
