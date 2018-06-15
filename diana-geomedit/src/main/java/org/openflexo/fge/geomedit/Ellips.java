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

import org.openflexo.diana.geomedit.model.gr.EllipsGraphicalRepresentation;
import org.openflexo.fge.geom.FGEEllips;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.EllipsConstruction;
import org.openflexo.fge.notifications.FGEAttributeNotification;

public class Ellips extends GeometricObject<FGEEllips> {

	private EllipsGraphicalRepresentation graphicalRepresentation;

	// Called for LOAD
	public Ellips(GeomEditBuilder builder) {
		super(builder);
	}

	public Ellips(GeometricSet set, EllipsConstruction construction) {
		super(set, construction);
		graphicalRepresentation = new EllipsGraphicalRepresentation(this, set.getEditedDrawing());
	}

	@Override
	public EllipsGraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(EllipsGraphicalRepresentation aGR) {
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}

	@Override
	public EllipsConstruction getConstruction() {
		return (EllipsConstruction) super.getConstruction();
	}

	public void setConstruction(EllipsConstruction ellipsConstruction) {
		_setConstruction(ellipsConstruction);
	}

	@Override
	public String getInspectorName() {
		return "Ellips.inspector";
	}

	public double getX() {
		return getGeometricObject().getX();
	}

	public void setX(double x) {
		if (x != getX()) {
			double oldX = getX();
			getGeometricObject().x = x;
			getGraphicalRepresentation().notify(new FGEAttributeNotification("x", oldX, x));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getWidth() {
		return getGeometricObject().getWidth();
	}

	public void setWidth(double width) {
		if (width != getWidth()) {
			double oldWidth = getWidth();
			getGeometricObject().width = width;
			getGraphicalRepresentation().notify(new FGEAttributeNotification("width", oldWidth, width));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getY() {
		return getGeometricObject().getY();
	}

	public void setY(double y) {
		if (y != getY()) {
			double oldY = getY();
			getGeometricObject().y = y;
			getGraphicalRepresentation().notify(new FGEAttributeNotification("y", oldY, y));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getHeight() {
		return getGeometricObject().getHeight();
	}

	public void setHeight(double height) {
		if (height != getHeight()) {
			double oldHeight = getHeight();
			getGeometricObject().height = height;
			getGraphicalRepresentation().notify(new FGEAttributeNotification("height", oldHeight, height));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public boolean getIsFilled() {
		return getConstruction().getIsFilled();
	}

	public void setIsFilled(boolean filled) {
		if (filled != getIsFilled()) {
			getConstruction().setIsFilled(filled);
			getGraphicalRepresentation().notify(new FGEAttributeNotification("isFilled", !filled, filled));
		}
	}
}
