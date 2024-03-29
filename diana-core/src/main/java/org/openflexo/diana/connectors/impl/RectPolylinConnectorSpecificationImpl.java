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

import java.util.logging.Logger;

import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.connectors.RectPolylinConnectorSpecification;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectPolylin;
import org.openflexo.diana.notifications.DianaAttributeNotification;

public abstract class RectPolylinConnectorSpecificationImpl extends ConnectorSpecificationImpl
		implements RectPolylinConnectorSpecification {

	static final Logger LOGGER = Logger.getLogger(RectPolylinConnectorSpecification.class.getPackage().getName());

	private boolean straightLineWhenPossible = true;
	private RectPolylinAdjustability adjustability = RectPolylinAdjustability.AUTO_LAYOUT;

	private SimplifiedCardinalDirection startOrientation;
	private SimplifiedCardinalDirection endOrientation;

	private int pixelOverlap = DianaConstants.DEFAULT_RECT_POLYLIN_PIXEL_OVERLAP; // overlap expressed in pixels relative to 1.0 scale

	private int arcSize = DianaConstants.DEFAULT_ROUNDED_RECT_POLYLIN_ARC_SIZE;
	private boolean isRounded = true;

	private boolean isStartingLocationFixed = false;
	private boolean isEndingLocationFixed = false;

	private boolean isStartingLocationDraggable = false;
	private boolean isEndingLocationDraggable = false;

	private DianaPoint crossedControlPoint = null;
	private DianaRectPolylin polylin = null;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Used for deserialization
	public RectPolylinConnectorSpecificationImpl() {
		super();
	}

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.RECT_POLYLIN;
	}

	private RectPolylinConstraints rectPolylinConstraints = RectPolylinConstraints.NONE;

	@Override
	public RectPolylinConstraints getRectPolylinConstraints() {
		return rectPolylinConstraints;
	}

	@Override
	public void setRectPolylinConstraints(RectPolylinConstraints aRectPolylinConstraints) {
		DianaAttributeNotification<?> notification = requireChange(RECT_POLYLIN_CONSTRAINTS, aRectPolylinConstraints);
		if (notification != null) {
			rectPolylinConstraints = aRectPolylinConstraints;
			hasChanged(notification);
		}
	}

	@Override
	public void setRectPolylinConstraints(RectPolylinConstraints someRectPolylinConstraints, SimplifiedCardinalDirection aStartOrientation,
			SimplifiedCardinalDirection aEndOrientation) {
		if (someRectPolylinConstraints != rectPolylinConstraints || startOrientation != aStartOrientation
				|| endOrientation != aEndOrientation) {
			rectPolylinConstraints = someRectPolylinConstraints;
			startOrientation = aStartOrientation;
			endOrientation = aEndOrientation;
			setRectPolylinConstraints(someRectPolylinConstraints);
		}
	}

	@Override
	public boolean getStraightLineWhenPossible() {
		return straightLineWhenPossible;
	}

	@Override
	public void setStraightLineWhenPossible(boolean aFlag) {
		DianaAttributeNotification<?> notification = requireChange(STRAIGHT_LINE_WHEN_POSSIBLE, aFlag);
		if (notification != null) {
			straightLineWhenPossible = aFlag;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsAdjustable() {
		return getAdjustability() == RectPolylinAdjustability.FULLY_ADJUSTABLE;
	}

	@Override
	public void setIsAdjustable(boolean aFlag) {
		setAdjustability(aFlag ? RectPolylinAdjustability.FULLY_ADJUSTABLE : RectPolylinAdjustability.AUTO_LAYOUT);
	}

	@Override
	public RectPolylinAdjustability getAdjustability() {
		return adjustability;
	}

	@Override
	public void setAdjustability(RectPolylinAdjustability anAdjustability) {
		DianaAttributeNotification<?> notification = requireChange(ADJUSTABILITY, anAdjustability);
		if (notification != null) {
			adjustability = anAdjustability;
			hasChanged(notification);
		}
	}

	@Override
	public SimplifiedCardinalDirection getEndOrientation() {
		return endOrientation;
	}

	@Override
	public void setEndOrientation(SimplifiedCardinalDirection anOrientation) {
		DianaAttributeNotification<?> notification = requireChange(END_ORIENTATION, anOrientation);
		if (notification != null) {
			endOrientation = anOrientation;
			hasChanged(notification);
		}
	}

	@Override
	public SimplifiedCardinalDirection getStartOrientation() {
		return startOrientation;
	}

	@Override
	public void setStartOrientation(SimplifiedCardinalDirection anOrientation) {
		DianaAttributeNotification<?> notification = requireChange(START_ORIENTATION, anOrientation);
		if (notification != null) {
			startOrientation = anOrientation;
			hasChanged(notification);
		}
	}

	@Override
	public int getPixelOverlap() {
		return pixelOverlap;
	}

	@Override
	public void setPixelOverlap(int aPixelOverlap) {
		DianaAttributeNotification<?> notification = requireChange(PIXEL_OVERLAP, aPixelOverlap);
		if (notification != null) {
			pixelOverlap = aPixelOverlap;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsRounded() {
		return isRounded;
	}

	@Override
	public void setIsRounded(boolean aFlag) {
		DianaAttributeNotification<?> notification = requireChange(IS_ROUNDED, aFlag);
		if (notification != null) {
			isRounded = aFlag;
			hasChanged(notification);
		}

	}

	@Override
	public int getArcSize() {
		return arcSize;
	}

	@Override
	public void setArcSize(int anArcSize) {
		DianaAttributeNotification<?> notification = requireChange(ARC_SIZE, anArcSize);
		if (notification != null) {
			arcSize = anArcSize;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsStartingLocationFixed() {
		return isStartingLocationFixed;
	}

	@Override
	public void setIsStartingLocationFixed(boolean aFlag) {
		DianaAttributeNotification<?> notification = requireChange(IS_STARTING_LOCATION_FIXED, aFlag);
		if (notification != null) {
			isStartingLocationFixed = aFlag;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsStartingLocationDraggable() {
		return isStartingLocationDraggable;
	}

	@Override
	public void setIsStartingLocationDraggable(boolean aFlag) {
		DianaAttributeNotification<?> notification = requireChange(IS_STARTING_LOCATION_DRAGGABLE, aFlag);
		if (notification != null) {
			isStartingLocationDraggable = aFlag;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsEndingLocationFixed() {
		return isEndingLocationFixed;
	}

	@Override
	public void setIsEndingLocationFixed(boolean aFlag) {
		DianaAttributeNotification<?> notification = requireChange(IS_ENDING_LOCATION_FIXED, aFlag);
		if (notification != null) {
			isEndingLocationFixed = aFlag;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsEndingLocationDraggable() {
		return isEndingLocationDraggable;
	}

	@Override
	public void setIsEndingLocationDraggable(boolean aFlag) {
		DianaAttributeNotification<?> notification = requireChange(IS_ENDING_LOCATION_DRAGGABLE, aFlag);
		if (notification != null) {
			isEndingLocationDraggable = aFlag;
			hasChanged(notification);
		}
	}

	private DianaPoint fixedStartLocationRelativeToStartObject;
	private DianaPoint fixedEndLocationRelativeToEndObject;

	/**
	 * Return start location asserting start location is fixed. Return position relative to start object (in the start-object coordinates
	 * system)
	 * 
	 * @return
	 */
	@Override
	public DianaPoint getFixedStartLocation() {
		if (!getIsStartingLocationFixed()) {
			return null;
		}
		if (fixedStartLocationRelativeToStartObject == null) {
			fixedStartLocationRelativeToStartObject = new DianaPoint(0.9, 0.9);
			/*DianaPoint centerOfEndObjectSeenFromStartObject = DianaUtils.convertNormalizedPoint(getEndObject(), new DianaPoint(0.5, 0.5),
					getStartObject());
			fixedStartLocationRelativeToStartObject = getStartObject().getShape().outlineIntersect(centerOfEndObjectSeenFromStartObject);
			if (fixedStartLocationRelativeToStartObject == null) {
				logger.warning("outlineIntersect() returned null");
				fixedStartLocationRelativeToStartObject = new DianaPoint(0.9, 0.9);
			}*/
		}
		return fixedStartLocationRelativeToStartObject;
	}

	/**
	 * Sets start location asserting start location is fixed. Sets position relative to start object (in the start-object coordinates
	 * system)
	 * 
	 * @param aPoint
	 *            : relative to start object
	 */
	@Override
	public void setFixedStartLocation(DianaPoint aPoint) {
		DianaAttributeNotification<?> notification = requireChange(FIXED_START_LOCATION, aPoint);
		if (notification != null) {
			if (!getIsStartingLocationFixed() && aPoint != null) {
				setIsStartingLocationFixed(true);
			}
			fixedStartLocationRelativeToStartObject = aPoint;
			hasChanged(notification);
		}
	}

	/**
	 * Return end location asserting end location is fixed. Return position relative to end object (in the end-object coordinates system)
	 * 
	 * @return
	 */
	@Override
	public DianaPoint getFixedEndLocation() {
		if (!getIsEndingLocationFixed()) {
			return null;
		}
		if (fixedEndLocationRelativeToEndObject == null) {
			fixedEndLocationRelativeToEndObject = new DianaPoint(0.1, 0.1);
			/*DianaPoint centerOfStartObjectSeenFromEndObject = DianaUtils.convertNormalizedPoint(getStartObject(), new DianaPoint(0.5, 0.5),
					getEndObject());
			fixedEndLocationRelativeToEndObject = getEndObject().getShape().outlineIntersect(centerOfStartObjectSeenFromEndObject);
			if (fixedEndLocationRelativeToEndObject == null) {
				logger.warning("outlineIntersect() returned null");
				fixedEndLocationRelativeToEndObject = new DianaPoint(0.1, 0.1);
			}*/
		}
		return fixedEndLocationRelativeToEndObject;
	}

	/**
	 * Sets end location asserting end location is fixed. Sets position relative to end object (in the end-object coordinates system)
	 * 
	 * @param aPoint
	 *            , relative to end object
	 */
	@Override
	public void setFixedEndLocation(DianaPoint aPoint) {
		DianaAttributeNotification<?> notification = requireChange(FIXED_END_LOCATION, aPoint);
		if (notification != null) {
			if (!getIsEndingLocationFixed() && aPoint != null) {
				setIsEndingLocationFixed(true);
			}
			fixedEndLocationRelativeToEndObject = aPoint;
			hasChanged(notification);
		}
	}

	/*@Override
	public RectPolylinConnectorSpecification clone() {
		RectPolylinConnectorSpecification returned = (RectPolylinConnectorSpecificationImpl) cloneObject();
		returned.setRectPolylinConstraints(getRectPolylinConstraints());
		returned.setStraightLineWhenPossible(getStraightLineWhenPossible());
		returned.setAdjustability(getAdjustability());
		returned.setStartOrientation(getStartOrientation());
		returned.setEndOrientation(getEndOrientation());
		returned.setIsRounded(getIsRounded());
		returned.setArcSize(getArcSize());
		returned.setIsStartingLocationFixed(getIsStartingLocationFixed());
		if (getIsStartingLocationFixed()) {
			returned.setFixedStartLocation(getFixedStartLocation());
		}
		returned.setIsEndingLocationFixed(getIsEndingLocationFixed());
		if (getIsEndingLocationFixed()) {
			returned.setFixedEndLocation(getFixedEndLocation());
		}
		returned.setIsStartingLocationDraggable(getIsStartingLocationDraggable());
		returned.setIsEndingLocationDraggable(getIsEndingLocationDraggable());
		returned.setCrossedControlPoint(getCrossedControlPoint());
		returned.setPolylin(getPolylin());
		return returned;
	}*/

	@Override
	public RectPolylinConnector makeConnector(ConnectorNode<?> connectorNode) {
		return new RectPolylinConnector(connectorNode);
	}

	@Override
	public DianaPoint getCrossedControlPoint() {
		return crossedControlPoint;
	}

	@Override
	public void setCrossedControlPoint(DianaPoint aPoint) {
		DianaAttributeNotification<?> notification = requireChange(CROSSED_CONTROL_POINT, aPoint);
		if (notification != null) {
			crossedControlPoint = aPoint;
			hasChanged(notification);
		}
	}

	@Override
	public DianaRectPolylin getPolylin() {
		return polylin;
	}

	@Override
	public void setPolylin(DianaRectPolylin aPolylin) {
		DianaAttributeNotification<?> notification = requireChange(POLYLIN, aPolylin);
		if (notification != null) {
			polylin = aPolylin;
			hasChanged(notification);
		}
	}

}
