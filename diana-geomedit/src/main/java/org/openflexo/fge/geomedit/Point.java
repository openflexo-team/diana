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

import org.openflexo.diana.geomedit.model.gr.PointGraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.PointConstruction;
import org.openflexo.fge.geomedit.construction.EllipsReference.EllipsReferenceImpl;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(PointImpl.class)
@XMLElement
public interface Point extends GeometricObject<FGEPoint> {

	public static abstract class PointImpl extends GeometricObjectImpl<FGEPoint> {

		private PointGraphicalRepresentation graphicalRepresentation;

		// Called for LOAD
		public Point(GeomEditBuilder builder) {
			super(builder);
		}

		public Point(GeometricSet set, PointConstruction construction) {
			super(set, construction);
			graphicalRepresentation = new PointGraphicalRepresentation(this, set.getEditedDrawing());
		}

		@Override
		public String getInspectorName() {
			return "Point.inspector";
		}

		@Override
		public PointGraphicalRepresentation getGraphicalRepresentation() {
			return graphicalRepresentation;
		}

		public void setGraphicalRepresentation(PointGraphicalRepresentation aGR) {
			aGR.setDrawable(this);
			graphicalRepresentation = aGR;
		}

		@Override
		public PointConstruction getConstruction() {
			return (PointConstruction) super.getConstruction();
		}

		public void setConstruction(PointConstruction pointConstruction) {
			_setConstruction(pointConstruction);
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

	}

}
