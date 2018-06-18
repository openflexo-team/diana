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
import java.util.List;
import java.util.Vector;

import org.openflexo.diana.geomedit.controller.ComputedControlPoint;
import org.openflexo.diana.geomedit.controller.DraggableControlPoint;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.geomedit.construction.ExplicitPointConstruction;
import org.openflexo.fge.geomedit.construction.GeometricConstruction;
import org.openflexo.fge.geomedit.construction.HalfLineConstruction;
import org.openflexo.fge.geomedit.construction.HalfLineWithTwoPointsConstruction;

public interface HalfLineGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGEHalfLine> {

	public static abstract class HalfLineGraphicalRepresentationImpl extends GeometricObjectGraphicalRepresentationImpl<FGEHalfLine> {

		@Override
		public List<? extends ControlArea<?>> makeControlAreasFor(
				DrawingTreeNode<GeometricConstruction<FGEHalfLine>, GeometricGraphicalRepresentation> dtn) {

			Vector<ControlPoint> returned = new Vector<ControlPoint>();

			HalfLineConstruction lineConstruction = (HalfLineConstruction) dtn.getDrawable();
			FGEHalfLine line = lineConstruction.getHalfLine();

			ExplicitPointConstruction pc1 = null;
			ExplicitPointConstruction pc2 = null;

			if (lineConstruction instanceof HalfLineWithTwoPointsConstruction) {
				if (((HalfLineWithTwoPointsConstruction) lineConstruction)
						.getLimitPointConstruction() instanceof ExplicitPointConstruction) {
					pc1 = (ExplicitPointConstruction) ((HalfLineWithTwoPointsConstruction) lineConstruction).getLimitPointConstruction();
				}
				if (((HalfLineWithTwoPointsConstruction) lineConstruction)
						.getOppositePointConstruction() instanceof ExplicitPointConstruction) {
					pc2 = (ExplicitPointConstruction) ((HalfLineWithTwoPointsConstruction) lineConstruction).getOppositePointConstruction();
				}
			}

			if (pc1 != null) {
				returned.add(new DraggableControlPoint<FGEHalfLine>((GeometricNode<?>) dtn, "limit", line.getP1(), pc1) {
					@Override
					public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
							FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
						getGeometricObject().setLimit(newAbsolutePoint);
						setPoint(newAbsolutePoint);
						((GeometricNode<?>) dtn).notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(FGEHalfLine geometricObject) {
						setPoint(geometricObject.getLimit());
					}
				});
			}
			else {
				returned.add(new ComputedControlPoint<FGEHalfLine>((GeometricNode<?>) dtn, "limit", line.getP1()) {
					@Override
					public void update(FGEHalfLine geometricObject) {
						setPoint(geometricObject.getLimit());
					}
				});
			}

			if (pc2 != null) {
				returned.add(new DraggableControlPoint<FGEHalfLine>((GeometricNode<?>) dtn, "opposite", line.getP2(), pc2) {
					@Override
					public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
							FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
						getGeometricObject().setOpposite(newAbsolutePoint);
						setPoint(newAbsolutePoint);
						((GeometricNode<?>) dtn).notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(FGEHalfLine geometricObject) {
						setPoint(geometricObject.getOpposite());
					}
				});
			}
			else {
				returned.add(new ComputedControlPoint<FGEHalfLine>((GeometricNode<?>) dtn, "opposite", line.getP1()) {
					@Override
					public void update(FGEHalfLine geometricObject) {
						setPoint(geometricObject.getOpposite());
					}
				});
			}

			return returned;
		}

	}

}
