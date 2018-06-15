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

import org.openflexo.diana.geomedit.model.gr.LineGraphicalRepresentation;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.LineConstruction;
import org.openflexo.fge.notifications.FGEAttributeNotification;

public class Line extends GeometricObject<FGELine> {

	private LineGraphicalRepresentation graphicalRepresentation;

	// Called for LOAD
	public Line(GeomEditBuilder builder) {
		super(builder);
	}

	public Line(GeometricSet set, LineConstruction construction) {
		super(set, construction);
		graphicalRepresentation = new LineGraphicalRepresentation(this, set.getEditedDrawing());
	}

	@Override
	public LineGraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(LineGraphicalRepresentation aGR) {
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}

	@Override
	public LineConstruction getConstruction() {
		return (LineConstruction) super.getConstruction();
	}

	public void setConstruction(LineConstruction lineConstruction) {
		_setConstruction(lineConstruction);
	}

	@Override
	public String getInspectorName() {
		return "Line.inspector";
	}

	public double getX1() {
		return getGeometricObject().getX1();
	}

	public void setX1(double x1) {
		if (x1 != getX1()) {
			double oldX1 = getX1();
			getGeometricObject().setX1(x1);
			getGraphicalRepresentation().notify(new FGEAttributeNotification("x1", oldX1, x1));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getX2() {
		return getGeometricObject().getX2();
	}

	public void setX2(double x2) {
		if (x2 != getX2()) {
			double oldX2 = getX2();
			getGeometricObject().setX2(x2);
			getGraphicalRepresentation().notify(new FGEAttributeNotification("x2", oldX2, x2));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getY1() {
		return getGeometricObject().getY1();
	}

	public void setY1(double y1) {
		if (y1 != getY1()) {
			double oldY1 = getY1();
			getGeometricObject().setY1(y1);
			getGraphicalRepresentation().notify(new FGEAttributeNotification("y1", oldY1, y1));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getY2() {
		return getGeometricObject().getY2();
	}

	public void setY2(double y2) {
		if (y2 != getY2()) {
			double oldY2 = getY2();
			getGeometricObject().setY2(y2);
			getGraphicalRepresentation().notify(new FGEAttributeNotification("y2", oldY2, y2));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

}
