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

package org.openflexo.diana.geomedit.model.gr;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.diana.geomedit.controller.ComputedControlPoint;
import org.openflexo.diana.geomedit.controller.DraggableControlPoint;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.construction.ExplicitPointConstruction;
import org.openflexo.fge.geomedit.construction.GeometricConstruction;
import org.openflexo.fge.geomedit.construction.PointConstruction;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(PointGraphicalRepresentation.PointGraphicalRepresentationImpl.class)
@XMLElement
public interface PointGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGEPoint> {

	public static abstract class PointGraphicalRepresentationImpl extends GeometricObjectGraphicalRepresentationImpl<FGEPoint>
			implements PointGraphicalRepresentation {

		@Override
		public List<? extends ControlArea<?>> makeControlAreasFor(
				final DrawingTreeNode<GeometricConstruction<FGEPoint>, GeometricGraphicalRepresentation> dtn) {
			List<ControlPoint> returned = new ArrayList<ControlPoint>();

			PointConstruction pc = (PointConstruction) dtn.getDrawable();

			if (pc instanceof ExplicitPointConstruction) {
				returned.add(new DraggableControlPoint<FGEPoint>((GeometricNode<?>) dtn, "point", pc.getPoint(),
						(ExplicitPointConstruction) pc) {
					@Override
					public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
							FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
						getGeometricObject().x = newAbsolutePoint.x;
						getGeometricObject().y = newAbsolutePoint.y;
						setPoint(newAbsolutePoint);
						((GeometricNode<?>) dtn).notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(FGEPoint geometricObject) {
						setPoint(geometricObject);
					}
				});
			}
			else {
				returned.add(new ComputedControlPoint<FGEPoint>((GeometricNode<?>) dtn, "point", pc.getPoint()) {

					@Override
					public void update(FGEPoint geometricObject) {
						setPoint(geometricObject);
					}
				});
			}

			return returned;
		}

	}
}