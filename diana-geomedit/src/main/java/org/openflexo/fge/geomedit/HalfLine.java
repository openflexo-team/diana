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

package org.openflexo.fge.geomedit;

import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.HalfLineConstruction;
import org.openflexo.fge.geomedit.gr.HalfLineGraphicalRepresentation;
import org.openflexo.fge.notifications.FGEAttributeNotification;

public class HalfLine extends GeometricObject<FGEHalfLine> {

	private HalfLineGraphicalRepresentation graphicalRepresentation;

	// Called for LOAD
	public HalfLine(GeomEditBuilder builder) {
		super(builder);
	}

	public HalfLine(GeometricSet set, HalfLineConstruction construction) {
		super(set, construction);
		graphicalRepresentation = new HalfLineGraphicalRepresentation(this, set.getEditedDrawing());
	}

	@Override
	public HalfLineGraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(HalfLineGraphicalRepresentation aGR) {
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}

	@Override
	public HalfLineConstruction getConstruction() {
		return (HalfLineConstruction) super.getConstruction();
	}

	public void setConstruction(HalfLineConstruction lineConstruction) {
		_setConstruction(lineConstruction);
	}

	@Override
	public String getInspectorName() {
		return "HalfLine.inspector";
	}

	public double getLimitX() {
		return getGeometricObject().getX1();
	}

	public void setLimitX(double limitX) {
		if (limitX != getLimitX()) {
			double oldLimitX = getLimitX();
			getGeometricObject().setX1(limitX);
			getGraphicalRepresentation().notify(new FGEAttributeNotification("x1", oldLimitX, limitX));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getLimitY() {
		return getGeometricObject().getY1();
	}

	public void setLimitY(double limitY) {
		if (limitY != getLimitY()) {
			double oldLimitY = getLimitY();
			getGeometricObject().setY1(limitY);
			getGraphicalRepresentation().notify(new FGEAttributeNotification("y1", oldLimitY, limitY));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getOppositeX() {
		return getGeometricObject().getX2();
	}

	public void setOppositeX(double oppositeX) {
		if (oppositeX != getOppositeX()) {
			double oldOppositeX = getOppositeX();
			getGeometricObject().setX2(oppositeX);
			getGraphicalRepresentation().notify(new FGEAttributeNotification("x2", oldOppositeX, oppositeX));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getOppositeY() {
		return getGeometricObject().getY2();
	}

	public void setOppositeY(double oppositeY) {
		if (oppositeY != getOppositeY()) {
			double oldOppositeY = getOppositeY();
			getGeometricObject().setY2(oppositeY);
			getGraphicalRepresentation().notify(new FGEAttributeNotification("y2", oldOppositeY, oppositeY));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

}
