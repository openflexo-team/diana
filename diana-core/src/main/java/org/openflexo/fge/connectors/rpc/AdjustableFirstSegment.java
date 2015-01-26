/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.fge.connectors.rpc;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.connectors.impl.RectPolylinConnector;
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEHalfPlane;

public class AdjustableFirstSegment extends RectPolylinAdjustableSegment {
	static final Logger LOGGER = Logger.getLogger(AdjustableFirstSegment.class.getPackage().getName());

	private boolean consistentData = false;
	private FGESegment currentSegment;
	private FGESegment nextSegment;
	private FGESegment afterNextSegment;
	private SimplifiedCardinalDirection currentOrientation;
	private SimplifiedCardinalDirection nextOrientation;
	private FGEArea startArea;
	private FGEArea draggingAuthorizedArea;

	public AdjustableFirstSegment(FGESegment segment, RectPolylinConnector connector) {
		super(segment, connector);
		retrieveInfos();
	}

	private void retrieveInfos() {
		currentSegment = getArea();
		nextSegment = getPolylin().getSegmentAt(1);
		if (currentSegment.getApproximatedOrientation() == null || nextSegment.getApproximatedOrientation() == null) {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return;
		}
		if (getPolylin().getSegmentNb() > 2) {
			afterNextSegment = getPolylin().getSegmentAt(2);
		}
		currentOrientation = currentSegment.getApproximatedOrientation();
		nextOrientation = nextSegment.getApproximatedOrientation();

		AffineTransform at1 = FGEUtils.convertNormalizedCoordinatesAT(getNode().getStartNode(), getNode());
		startArea = getNode().getStartNode().getFGEShapeOutline().transform(at1);
		FGEArea orthogonalPerspectiveArea = startArea.getOrthogonalPerspectiveArea(currentOrientation);
		if (!nextSegment.containsPoint(currentSegment.getP1())) {
			FGEHalfPlane hp = new FGEHalfPlane(nextSegment, currentSegment.getP1());
			draggingAuthorizedArea = orthogonalPerspectiveArea.intersect(hp);
		} else {
			draggingAuthorizedArea = orthogonalPerspectiveArea;
		}

		consistentData = true;
	}

	@Override
	public void startDragging(DianaEditor<?> controller, FGEPoint startPoint) {
		super.startDragging(controller, startPoint);
		retrieveInfos();
		LOGGER.info("Start dragging: " + draggingAuthorizedArea);
	}

	@Override
	public FGEArea getDraggingAuthorizedArea() {
		if (!consistentData) {
			return new FGEEmptyArea();
		}

		return draggingAuthorizedArea;

	}

	@Override
	public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
			FGEPoint initialPoint, MouseEvent event) {
		FGEPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);

		FGEPoint p1 = getPolylin().getFirstPoint();
		if (p1 == null) {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}
		FGEPoint p2 = getPolylin().getPointAt(1);
		if (p2 == null) {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}

		p1 = startArea.nearestPointFrom(pt, currentOrientation.getOpposite());
		if (currentOrientation.isHorizontal()) {
			p2.y = pt.y;
		} else if (currentOrientation.isVertical()) {
			p2.x = pt.x;
		} else {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}

		getPolylin().updatePointAt(0, p1);
		getConnector().getControlPoints().get(0).setPoint(p1);

		getPolylin().updatePointAt(1, p2);
		getConnector().getControlPoints().get(1).setPoint(p2);

		getConnector()._connectorChanged(true);
		getNode().notifyConnectorModified();

		return true;

	}

	/*@Override
	public void stopDragging(AbstractDianaEditor controller)
	{
		if (afterNextSegment != null && afterNextSegment.overlap(currentSegment)) {
			getConnector()._simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(1);
		}
		super.stopDragging(controller);
	}*/
}
