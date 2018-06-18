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
import org.openflexo.diana.geomedit.model.gr.LineGraphicalRepresentation.LineGraphicalRepresentationImpl;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.construction.ExplicitPointConstruction;
import org.openflexo.fge.geomedit.construction.GeometricConstruction;
import org.openflexo.fge.geomedit.construction.HorizontalLineWithPointConstruction;
import org.openflexo.fge.geomedit.construction.LineConstruction;
import org.openflexo.fge.geomedit.construction.LineWithTwoPointsConstruction;
import org.openflexo.fge.geomedit.construction.OrthogonalLineWithPointConstruction;
import org.openflexo.fge.geomedit.construction.ParallelLineWithPointConstruction;
import org.openflexo.fge.geomedit.construction.RotatedLineWithPointConstruction;
import org.openflexo.fge.geomedit.construction.TangentLineWithCircleAndPointConstruction;
import org.openflexo.fge.geomedit.construction.VerticalLineWithPointConstruction;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(LineGraphicalRepresentationImpl.class)
@XMLElement
public interface LineGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGELine> {

	public static abstract class LineGraphicalRepresentationImpl extends GeometricObjectGraphicalRepresentationImpl<FGELine>
			implements LineGraphicalRepresentation {

		@Override
		public List<? extends ControlArea<?>> makeControlAreasFor(
				DrawingTreeNode<GeometricConstruction<FGELine>, GeometricGraphicalRepresentation> dtn) {
			Vector<ControlPoint> returned = new Vector<ControlPoint>();

			LineConstruction lineConstruction = (LineConstruction) dtn.getDrawable();
			FGELine line = lineConstruction.getData();

			ExplicitPointConstruction pc1 = null;
			ExplicitPointConstruction pc2 = null;

			if (lineConstruction instanceof LineWithTwoPointsConstruction) {
				if (((LineWithTwoPointsConstruction) lineConstruction).getPointConstruction1() instanceof ExplicitPointConstruction) {
					pc1 = (ExplicitPointConstruction) ((LineWithTwoPointsConstruction) lineConstruction).getPointConstruction1();
				}
				if (((LineWithTwoPointsConstruction) lineConstruction).getPointConstruction2() instanceof ExplicitPointConstruction) {
					pc2 = (ExplicitPointConstruction) ((LineWithTwoPointsConstruction) lineConstruction).getPointConstruction2();
				}
			}

			if (lineConstruction instanceof OrthogonalLineWithPointConstruction) {
				if (((OrthogonalLineWithPointConstruction) lineConstruction).getPointConstruction() instanceof ExplicitPointConstruction) {
					pc1 = (ExplicitPointConstruction) ((OrthogonalLineWithPointConstruction) lineConstruction).getPointConstruction();
				}
			}

			if (lineConstruction instanceof ParallelLineWithPointConstruction) {
				if (((ParallelLineWithPointConstruction) lineConstruction).getPointConstruction() instanceof ExplicitPointConstruction) {
					pc1 = (ExplicitPointConstruction) ((ParallelLineWithPointConstruction) lineConstruction).getPointConstruction();
				}
			}

			if (lineConstruction instanceof RotatedLineWithPointConstruction) {
				if (((RotatedLineWithPointConstruction) lineConstruction).getPointConstruction() instanceof ExplicitPointConstruction) {
					pc1 = (ExplicitPointConstruction) ((RotatedLineWithPointConstruction) lineConstruction).getPointConstruction();
				}
			}

			if (lineConstruction instanceof HorizontalLineWithPointConstruction) {
				if (((HorizontalLineWithPointConstruction) lineConstruction).getPointConstruction() instanceof ExplicitPointConstruction) {
					pc1 = (ExplicitPointConstruction) ((HorizontalLineWithPointConstruction) lineConstruction).getPointConstruction();
				}
			}

			if (lineConstruction instanceof VerticalLineWithPointConstruction) {
				if (((VerticalLineWithPointConstruction) lineConstruction).getPointConstruction() instanceof ExplicitPointConstruction) {
					pc1 = (ExplicitPointConstruction) ((VerticalLineWithPointConstruction) lineConstruction).getPointConstruction();
				}
			}

			if (lineConstruction instanceof TangentLineWithCircleAndPointConstruction) {
				if (((TangentLineWithCircleAndPointConstruction) lineConstruction)
						.getPointConstruction() instanceof ExplicitPointConstruction) {
					pc1 = (ExplicitPointConstruction) ((TangentLineWithCircleAndPointConstruction) lineConstruction).getPointConstruction();
				}
			}

			if (pc1 != null) {
				returned.add(new DraggableControlPoint<FGELine>((GeometricNode<?>) dtn, "p1", line.getP1(), pc1) {
					@Override
					public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
							FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
						getGeometricObject().setP1(newAbsolutePoint);
						setPoint(newAbsolutePoint);
						((GeometricNode<?>) dtn).notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(FGELine geometricObject) {
						setPoint(geometricObject.getP1());
					}
				});
			}
			else {
				returned.add(new ComputedControlPoint<FGELine>((GeometricNode<?>) dtn, "p1", line.getP1()) {
					@Override
					public void update(FGELine geometricObject) {
						setPoint(geometricObject.getP1());
					}
				});
			}

			if (pc2 != null) {
				returned.add(new DraggableControlPoint<FGELine>((GeometricNode<?>) dtn, "p2", line.getP2(), pc2) {
					@Override
					public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
							FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
						getGeometricObject().setP2(newAbsolutePoint);
						setPoint(newAbsolutePoint);
						((GeometricNode<?>) dtn).notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(FGELine geometricObject) {
						setPoint(geometricObject.getP2());
					}
				});
			}
			else {
				returned.add(new ComputedControlPoint<FGELine>((GeometricNode<?>) dtn, "p2", line.getP1()) {
					@Override
					public void update(FGELine geometricObject) {
						setPoint(geometricObject.getP2());
					}
				});
			}

			return returned;
		}

	}

}
