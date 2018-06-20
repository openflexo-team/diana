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
import org.openflexo.diana.geomedit.model.PointConstruction;
import org.openflexo.diana.geomedit.model.PolylinConstruction;
import org.openflexo.diana.geomedit.model.PolylinWithNPointsConstruction;
import org.openflexo.diana.geomedit.model.gr.PolylinGraphicalRepresentation.PolylinGraphicalRepresentationImpl;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolylin;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(PolylinGraphicalRepresentationImpl.class)
@XMLElement
public interface PolylinGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGEPolylin> {

	public static abstract class PolylinGraphicalRepresentationImpl extends GeometricObjectGraphicalRepresentationImpl<FGEPolylin>
			implements PolylinGraphicalRepresentation {

		/*@Override
		public void paint(Graphics g, AbstractDianaEditor controller) {
			// TODO: un petit @brutal pour avancer, il faudrait faire les choses plus proprement
			if (getGeometricObject() instanceof FGEPolylin) {
				rebuildControlPoints();
			}
			super.paint(g, controller);
		}
		
		// DEBUG
		@Override
		public void paintGeometricObject(JFGEGeometricGraphics graphics) {
			super.paintGeometricObject(graphics);
			// System.out.println("getGeometricObject()"+getGeometricObject());
			if (getGeometricObject() instanceof FGERectPolylin) {
				FGERectPolylin rectPoly = (FGERectPolylin) getGeometricObject();
				if (rectPoly.missingPath != null) {
					graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.YELLOW, 1.0f, DashStyle.SMALL_DASHES));
					rectPoly.missingPath.paint(graphics);
				}
				graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.GREEN));
				FGERectPolylin debugPolylin = rectPoly.makeNormalizedRectPolylin();
				debugPolylin.paint(graphics);
				for (FGEPoint p : debugPolylin.getPoints()) {
					p.paint(graphics);
				}
				if (rectPoly.currentPointStartingSide != null) {
					graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.RED, 2.0f));
					rectPoly.currentPointStartingSide.paint(graphics);
					rectPoly.currentPointEndingSide.paint(graphics);
				}
				if (rectPoly.getPointsNb() == 5) {
					//FGERectPolylin tempPoly
					//= FGERectPolylin.makeShortestRectPolylin(
					//		rectPoly.getPointAt(1), 
					//		rectPoly.getPointAt(3), 
					//		true, 
					//		rectPoly.getOverlap());
					//graphics.setDefaultForeground(ForegroundStyleImpl.makeStyle(Color.GRAY));
					//tempPoly.paint(graphics);
					FGERectPolylin polylinCrossingPoint = FGERectPolylin.makeRectPolylinCrossingPoint(rectPoly.getPointAt(1),
							rectPoly.getPointAt(3), rectPoly.getPointAt(2), true, rectPoly.getOverlapX(), rectPoly.getOverlapY()
							//,null, 
						null);
		
					graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.BLUE));
					polylinCrossingPoint.paint(graphics);
		
				}
		
			}
		}*/

		@Override
		public List<? extends ControlArea<?>> makeControlAreasFor(
				DrawingTreeNode<GeometricConstruction<FGEPolylin>, GeometricGraphicalRepresentation> dtn) {
			Vector<ControlPoint> returned = new Vector<ControlPoint>();

			PolylinConstruction polylinContruction = (PolylinConstruction) dtn.getDrawable();

			if (polylinContruction instanceof PolylinWithNPointsConstruction) {

				for (int i = 0; i < ((PolylinWithNPointsConstruction) polylinContruction).getPointConstructions().size(); i++) {

					final int pointIndex = i;
					PointConstruction pc = ((PolylinWithNPointsConstruction) polylinContruction).getPointConstructions().get(i);

					if (pc instanceof ExplicitPointConstruction) {
						returned.add(new DraggableControlPoint<FGEPolylin>((GeometricNode<?>) dtn, "pt" + i, pc.getPoint(),
								(ExplicitPointConstruction) pc) {
							@Override
							public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
									FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
								getGeometricObject().getPointAt(pointIndex).x = newAbsolutePoint.x;
								getGeometricObject().getPointAt(pointIndex).y = newAbsolutePoint.y;
								setPoint(newAbsolutePoint);
								((GeometricNode<?>) dtn).notifyGeometryChanged();
								return true;
							}

							@Override
							public void update(FGEPolylin geometricObject) {
								setPoint(geometricObject.getPointAt(pointIndex));
							}
						});
					}
					else {
						returned.add(new ComputedControlPoint<FGEPolylin>((GeometricNode<?>) dtn, "pt" + i, pc.getPoint()) {
							@Override
							public void update(FGEPolylin geometricObject) {
								setPoint(geometricObject.getPointAt(pointIndex));
							}
						});
					}

				}
			}

			return returned;

		}
	}
}
