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

package org.openflexo.fge.geomedit.gr;

import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.ComputedControlPoint;
import org.openflexo.fge.geomedit.DraggableControlPoint;
import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.Line;
import org.openflexo.fge.geomedit.construction.ExplicitPointConstruction;
import org.openflexo.fge.geomedit.construction.HorizontalLineWithPointConstruction;
import org.openflexo.fge.geomedit.construction.LineConstruction;
import org.openflexo.fge.geomedit.construction.LineWithTwoPointsConstruction;
import org.openflexo.fge.geomedit.construction.OrthogonalLineWithPointConstruction;
import org.openflexo.fge.geomedit.construction.ParallelLineWithPointConstruction;
import org.openflexo.fge.geomedit.construction.RotatedLineWithPointConstruction;
import org.openflexo.fge.geomedit.construction.TangentLineWithCircleAndPointConstruction;
import org.openflexo.fge.geomedit.construction.VerticalLineWithPointConstruction;
import org.openflexo.xmlcode.XMLSerializable;

public class LineGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGELine, Line> implements XMLSerializable {
	// Called for LOAD
	public LineGraphicalRepresentation(GeomEditBuilder builder) {
		this(null, builder.drawing);
		initializeDeserialization();
	}

	public LineGraphicalRepresentation(Line line, GeometricDrawing aDrawing) {
		super(line, aDrawing);
	}

	/*@Override
	public Line getDrawable() {
		return super.getDrawable();
	}
	
	@Override
	public FGELine getGeometricObject() {
		return super.getGeometricObject();
	}*/

	@Override
	protected List<ControlPoint> buildControlPointsForLine(FGEAbstractLine line) {
		Vector<ControlPoint> returned = new Vector<ControlPoint>();

		LineConstruction lineConstruction = getDrawable().getConstruction();

		ExplicitPointConstruction pc1 = null;
		ExplicitPointConstruction pc2 = null;

		if (lineConstruction instanceof LineWithTwoPointsConstruction) {
			if (((LineWithTwoPointsConstruction) lineConstruction).pointConstruction1 instanceof ExplicitPointConstruction) {
				pc1 = (ExplicitPointConstruction) ((LineWithTwoPointsConstruction) lineConstruction).pointConstruction1;
			}
			if (((LineWithTwoPointsConstruction) lineConstruction).pointConstruction2 instanceof ExplicitPointConstruction) {
				pc2 = (ExplicitPointConstruction) ((LineWithTwoPointsConstruction) lineConstruction).pointConstruction2;
			}
		}

		if (lineConstruction instanceof OrthogonalLineWithPointConstruction) {
			if (((OrthogonalLineWithPointConstruction) lineConstruction).pointConstruction instanceof ExplicitPointConstruction) {
				pc1 = (ExplicitPointConstruction) ((OrthogonalLineWithPointConstruction) lineConstruction).pointConstruction;
			}
		}

		if (lineConstruction instanceof ParallelLineWithPointConstruction) {
			if (((ParallelLineWithPointConstruction) lineConstruction).pointConstruction instanceof ExplicitPointConstruction) {
				pc1 = (ExplicitPointConstruction) ((ParallelLineWithPointConstruction) lineConstruction).pointConstruction;
			}
		}

		if (lineConstruction instanceof RotatedLineWithPointConstruction) {
			if (((RotatedLineWithPointConstruction) lineConstruction).pointConstruction instanceof ExplicitPointConstruction) {
				pc1 = (ExplicitPointConstruction) ((RotatedLineWithPointConstruction) lineConstruction).pointConstruction;
			}
		}

		if (lineConstruction instanceof HorizontalLineWithPointConstruction) {
			if (((HorizontalLineWithPointConstruction) lineConstruction).pointConstruction instanceof ExplicitPointConstruction) {
				pc1 = (ExplicitPointConstruction) ((HorizontalLineWithPointConstruction) lineConstruction).pointConstruction;
			}
		}

		if (lineConstruction instanceof VerticalLineWithPointConstruction) {
			if (((VerticalLineWithPointConstruction) lineConstruction).pointConstruction instanceof ExplicitPointConstruction) {
				pc1 = (ExplicitPointConstruction) ((VerticalLineWithPointConstruction) lineConstruction).pointConstruction;
			}
		}

		if (lineConstruction instanceof TangentLineWithCircleAndPointConstruction) {
			if (((TangentLineWithCircleAndPointConstruction) lineConstruction).pointConstruction instanceof ExplicitPointConstruction) {
				pc1 = (ExplicitPointConstruction) ((TangentLineWithCircleAndPointConstruction) lineConstruction).pointConstruction;
			}
		}

		if (pc1 != null) {
			returned.add(new DraggableControlPoint<FGELine>(this, "p1", line.getP1(), pc1) {
				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					getGeometricObject().setP1(newAbsolutePoint);
					setPoint(newAbsolutePoint);
					notifyGeometryChanged();
					return true;
				}

				@Override
				public void update(FGELine geometricObject) {
					setPoint(geometricObject.getP1());
				}
			});
		} else {
			returned.add(new ComputedControlPoint<FGELine>(this, "p1", line.getP1()) {
				@Override
				public void update(FGELine geometricObject) {
					setPoint(geometricObject.getP1());
				}
			});
		}

		if (pc2 != null) {
			returned.add(new DraggableControlPoint<FGELine>(this, "p2", line.getP2(), pc2) {
				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					getGeometricObject().setP2(newAbsolutePoint);
					setPoint(newAbsolutePoint);
					notifyGeometryChanged();
					return true;
				}

				@Override
				public void update(FGELine geometricObject) {
					setPoint(geometricObject.getP2());
				}
			});
		} else {
			returned.add(new ComputedControlPoint<FGELine>(this, "p2", line.getP1()) {
				@Override
				public void update(FGELine geometricObject) {
					setPoint(geometricObject.getP2());
				}
			});
		}

		return returned;
	}

}
