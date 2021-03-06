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

package org.openflexo.diana.shapes.impl;

import java.util.logging.Logger;

import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaRoundRectangle;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.notifications.DianaAttributeNotification;
import org.openflexo.diana.shapes.Rectangle;

public abstract class RectangleImpl extends ShapeSpecificationImpl implements Rectangle {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(RectangleImpl.class.getPackage().getName());

	private boolean isRounded = false;
	private double arcSize = DianaConstants.DEFAULT_ROUNDED_RECTANGLE_ARC_SIZE;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/*@Deprecated
	private RectangleImpl(ShapeGraphicalRepresentation aGraphicalRepresentation) {
		this();
		setGraphicalRepresentation(aGraphicalRepresentation);
		updateShape();
	}*/

	@Override
	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	@Override
	public DianaShape<?> makeNormalizedDianaShape(DianaRectangle bounds) {
		if (bounds != null && isRounded) {
			double arcwidth = arcSize / bounds.getWidth();
			double archeight = arcSize / bounds.getHeight();
			return new DianaRoundRectangle(0, 0, 1, 1, arcwidth, archeight, Filling.FILLED);
		}
		return new DianaRectangle(0, 0, 1, 1, Filling.FILLED);
	}

	/**
	 * Returns arc size (expressed in pixels for a 1.0 scale)
	 * 
	 * @return
	 */
	@Override
	public double getArcSize() {
		return arcSize;
	}

	/**
	 * Sets arc size (expressed in pixels for a 1.0 scale)
	 * 
	 * @param anArcSize
	 */
	@Override
	public void setArcSize(double anArcSize) {
		DianaAttributeNotification<?> notification = requireChange(ARC_SIZE, anArcSize);
		if (notification != null) {
			arcSize = anArcSize;
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

	/*@Override
	public DianaShape getOutline()
	{
		if (getIsRounded()) {
			double arcwidth = arcSize/getGraphicalRepresentation().getWidth();
			double archeight = arcSize/getGraphicalRepresentation().getHeight();
			return new DianaRoundRectangle(0,0,1,1,arcwidth,archeight,Filling.NOT_FILLED);
		}
		else {
			return new DianaRectangle(0,0,1,1,Filling.NOT_FILLED);
		}
	}*/

	/*@Override
	public void notifyObjectResized() {
		if (getIsRounded()) {
			setArcSize(arcSize);
		}
	}*/

}
