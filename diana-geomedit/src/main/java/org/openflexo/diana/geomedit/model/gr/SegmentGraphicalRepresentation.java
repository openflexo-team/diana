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
import org.openflexo.diana.geomedit.model.ExplicitPointConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.SegmentConstruction;
import org.openflexo.diana.geomedit.model.SegmentWithTwoPointsConstruction;
import org.openflexo.diana.geomedit.model.gr.SegmentGraphicalRepresentation.SegmentGraphicalRepresentationImpl;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.GeometricGraphicalRepresentation;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaSegment;

@ModelEntity
@ImplementationClass(SegmentGraphicalRepresentationImpl.class)
@XMLElement
public interface SegmentGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<DianaSegment> {

	public static abstract class SegmentGraphicalRepresentationImpl extends GeometricObjectGraphicalRepresentationImpl<DianaSegment>
			implements SegmentGraphicalRepresentation {

		@Override
		public List<? extends ControlArea<?>> makeControlAreasFor(
				DrawingTreeNode<GeometricConstruction<DianaSegment>, GeometricGraphicalRepresentation> dtn) {

			Vector<ControlPoint> returned = new Vector<ControlPoint>();

			SegmentConstruction segmentConstruction = (SegmentConstruction) dtn.getDrawable();
			DianaSegment segment = segmentConstruction.getSegment();

			ExplicitPointConstruction pc1 = null;
			ExplicitPointConstruction pc2 = null;

			if (segmentConstruction instanceof SegmentWithTwoPointsConstruction) {
				if (((SegmentWithTwoPointsConstruction) segmentConstruction).getPointConstruction1() instanceof ExplicitPointConstruction) {
					pc1 = (ExplicitPointConstruction) ((SegmentWithTwoPointsConstruction) segmentConstruction).getPointConstruction1();
				}
				if (((SegmentWithTwoPointsConstruction) segmentConstruction).getPointConstruction2() instanceof ExplicitPointConstruction) {
					pc2 = (ExplicitPointConstruction) ((SegmentWithTwoPointsConstruction) segmentConstruction).getPointConstruction2();
				}
			}

			if (pc1 != null) {
				returned.add(new DraggableControlPoint<DianaSegment>((GeometricNode<?>) dtn, "p1", segment.getP1(), pc1) {
					@Override
					public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
							DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
						getGeometricObject().setP1(newAbsolutePoint);
						setPoint(newAbsolutePoint);
						((GeometricNode<?>) dtn).notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(DianaSegment geometricObject) {
						setPoint(geometricObject.getP1());
					}
				});
			}
			else {
				returned.add(new ComputedControlPoint<DianaSegment>((GeometricNode<?>) dtn, "p1", segment.getP1()) {
					@Override
					public void update(DianaSegment geometricObject) {
						setPoint(geometricObject.getP1());
					}
				});
			}

			if (pc2 != null) {
				returned.add(new DraggableControlPoint<DianaSegment>((GeometricNode<?>) dtn, "p2", segment.getP2(), pc2) {
					@Override
					public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
							DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
						getGeometricObject().setP2(newAbsolutePoint);
						setPoint(newAbsolutePoint);
						((GeometricNode<?>) dtn).notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(DianaSegment geometricObject) {
						setPoint(geometricObject.getP2());
					}
				});
			}
			else {
				returned.add(new ComputedControlPoint<DianaSegment>((GeometricNode<?>) dtn, "p2", segment.getP1()) {
					@Override
					public void update(DianaSegment geometricObject) {
						setPoint(geometricObject.getP2());
					}
				});
			}

			return returned;
		}
	}
}
