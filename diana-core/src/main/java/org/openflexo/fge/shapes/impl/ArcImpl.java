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

package org.openflexo.fge.shapes.impl;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.geom.FGEArc;
import org.openflexo.fge.geom.FGEArc.ArcType;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.fge.shapes.Arc;

public abstract class ArcImpl extends ShapeSpecificationImpl implements Arc {

	// private FGEArc arc;

	private int angleStart = 0;
	private int angleExtent = 90;
	private ArcType arcType = ArcType.PIE;

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public ArcImpl() {
		super();
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.ARC;
	}

	@Override
	public FGEShape<?> makeFGEShape(ShapeNode<?> node) {
		return new FGEArc(0, 0, 1, 1, angleStart, angleExtent, arcType);
	}

	@Override
	public int getAngleStart() {
		return angleStart;
	}

	@Override
	public void setAngleStart(int anAngle) {
		FGEAttributeNotification notification = requireChange(ANGLE_START, anAngle);
		if (notification != null) {
			angleStart = anAngle;
			hasChanged(notification);
		}
	}

	@Override
	public int getAngleExtent() {
		return angleExtent;
	}

	@Override
	public void setAngleExtent(int anAngle) {
		FGEAttributeNotification notification = requireChange(ANGLE_EXTENT, anAngle);
		if (notification != null) {
			angleExtent = anAngle;
			hasChanged(notification);
		}
	}

	@Override
	public ArcType getArcType() {
		return arcType;
	}

	@Override
	public void setArcType(ArcType anArcType) {
		FGEAttributeNotification notification = requireChange(ARC_TYPE, anArcType);
		if (notification != null) {
			arcType = anArcType;
			hasChanged(notification);
		}
	}

}
