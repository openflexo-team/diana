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

import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.GeometricGraphicalRepresentation;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geomedit.controller.DraggableControlPoint;
import org.openflexo.diana.geomedit.model.ExplicitPointConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.NodeWithTwoPointsConstruction;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(NodeWithTwoPointsGraphicalRepresentation.NodeWithTwoPointsGraphicalRepresentationImpl.class)
@XMLElement
public interface NodeWithTwoPointsGraphicalRepresentation extends NodeGraphicalRepresentation {

	public static abstract class NodeWithTwoPointsGraphicalRepresentationImpl extends NodeGraphicalRepresentationImpl
			implements NodeWithTwoPointsGraphicalRepresentation {

		private DraggableControlPoint<?> controlPoint1;
		private DraggableControlPoint<?> controlPoint2;

		public DraggableControlPoint<?> getControlPoint1() {
			return controlPoint1;
		}

		public DraggableControlPoint<?> getControlPoint2() {
			return controlPoint2;
		}

		@Override
		public List<ControlPoint> makeControlAreasFor(
				DrawingTreeNode<GeometricConstruction<DianaShape<?>>, GeometricGraphicalRepresentation> dtn) {

			List<ControlPoint> returned = super.makeControlAreasFor(dtn);

			NodeWithTwoPointsConstruction nodeConstruction = (NodeWithTwoPointsConstruction) dtn.getDrawable();

			if (nodeConstruction.getPointConstruction1() instanceof ExplicitPointConstruction) {
				final ExplicitPointConstruction pc1 = (ExplicitPointConstruction) nodeConstruction.getPointConstruction1();
				returned.add(controlPoint1 = new DraggableControlPoint<DianaShape<?>>((GeometricNode<?>) dtn, "p1", pc1.getPoint(), pc1) {
					@Override
					public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
							DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
						DianaPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
						setPoint(pt);
						pc1.setPoint(newAbsolutePoint);
						pc1.notifyGeometryChanged();
						setPoint(newAbsolutePoint);
						// ((GeometricNode<?>) dtn).notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(DianaShape<?> geometricObject) {
						setPoint(pc1.getPoint());
					}
				});

			}
			if (nodeConstruction.getPointConstruction2() instanceof ExplicitPointConstruction) {
				final ExplicitPointConstruction pc2 = (ExplicitPointConstruction) nodeConstruction.getPointConstruction2();
				returned.add(controlPoint2 = new DraggableControlPoint<DianaShape<?>>((GeometricNode<?>) dtn, "p2", pc2.getPoint(), pc2) {
					@Override
					public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
							DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
						DianaPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
						setPoint(pt);
						pc2.setPoint(newAbsolutePoint);
						pc2.notifyGeometryChanged();
						setPoint(newAbsolutePoint);
						// ((GeometricNode<?>) dtn).notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(DianaShape<?> geometricObject) {
						setPoint(pc2.getPoint());
					}
				});
			}

			return returned;
		}

	}

}
