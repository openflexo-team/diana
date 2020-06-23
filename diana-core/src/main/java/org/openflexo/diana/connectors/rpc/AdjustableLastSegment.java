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

package org.openflexo.diana.connectors.rpc;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.connectors.impl.RectPolylinConnector;
import org.openflexo.diana.control.DianaEditor;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaHalfPlane;

public class AdjustableLastSegment extends RectPolylinAdjustableSegment {
	static final Logger LOGGER = Logger.getLogger(AdjustableLastSegment.class.getPackage().getName());

	private boolean consistentData = false;
	private int segmentsNb;
	private DianaSegment currentSegment;
	private DianaSegment previousSegment;
	// Unused private DianaSegment beforePreviousSegment;
	private SimplifiedCardinalDirection currentOrientation;
	// Unused private SimplifiedCardinalDirection previousOrientation;
	private DianaArea endArea;
	private DianaArea draggingAuthorizedArea;

	public AdjustableLastSegment(DianaSegment segment, RectPolylinConnector connector) {
		super(segment, connector);
		retrieveInfos();
	}

	private void retrieveInfos() {
		currentSegment = getArea();
		segmentsNb = getPolylin().getSegmentNb();
		previousSegment = getPolylin().getSegmentAt(segmentsNb - 2);
		if (currentSegment.getApproximatedOrientation() == null || previousSegment.getApproximatedOrientation() == null) {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return;
		}
		// Unused if (getPolylin().getSegmentNb() > 2) {
		// Unused beforePreviousSegment = getPolylin().getSegmentAt(segmentsNb - 3);
		// Unused }
		currentOrientation = currentSegment.getApproximatedOrientation();
		// Unused previousOrientation = previousSegment.getApproximatedOrientation();

		AffineTransform at2 = DianaUtils.convertNormalizedCoordinatesAT(getNode().getEndNode(), getNode());
		endArea = getNode().getEndNode().getDianaShapeOutline().transform(at2);
		DianaArea orthogonalPerspectiveArea = endArea.getOrthogonalPerspectiveArea(currentOrientation.getOpposite());
		if (!previousSegment.containsPoint(currentSegment.getP2())) {
			DianaHalfPlane hp = new DianaHalfPlane(previousSegment, currentSegment.getP2());
			draggingAuthorizedArea = orthogonalPerspectiveArea.intersect(hp);
		}
		else {
			draggingAuthorizedArea = orthogonalPerspectiveArea;
		}

		consistentData = true;
	}

	@Override
	public void startDragging(DianaEditor<?> controller, DianaPoint startPoint) {
		super.startDragging(controller, startPoint);
		retrieveInfos();
	}

	@Override
	public DianaArea getDraggingAuthorizedArea() {
		if (!consistentData) {
			return new DianaEmptyArea();
		}

		return draggingAuthorizedArea;

	}

	@Override
	public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration, DianaPoint newAbsolutePoint,
			DianaPoint initialPoint, MouseEvent event) {
		DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);

		DianaPoint p1 = getPolylin().getPointAt(segmentsNb - 1);
		if (p1 == null) {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}
		DianaPoint p2 = getPolylin().getPointAt(segmentsNb);
		if (p2 == null) {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}

		p2 = endArea.nearestPointFrom(pt, currentOrientation);
		if (currentOrientation.isHorizontal()) {
			p1.y = pt.y;
		}
		else if (currentOrientation.isVertical()) {
			p1.x = pt.x;
		}
		else {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}

		/*
		if (currentOrientation.isHorizontal()) {
			p1.y = pt.y;
			p2.y = pt.y;
		}	
		else if (currentOrientation.isVertical()) {
			p1.x = pt.x;
			p2.x = pt.x;
		}	
		else {
			logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}			
		
		// Now we must ensure that p2 is located on shape outline
		// To do so, use outlineIntersect by projecting p2 along horizontal or vertical line
		
		DianaPoint outlineIntersect = endArea.nearestPointFrom(p2, currentOrientation);
		if (outlineIntersect != null) p2 = outlineIntersect;
		else {
			logger.warning("Could not compute outlineIntersect() from "+p2);
		}		*/

		/*if (endArea instanceof DianaShape) {
			DianaPoint outlineIntersect = null;
			if (currentOrientation.isHorizontal()) {
				outlineIntersect = ((DianaShape)endArea).outlineIntersect(DianaLine.makeHorizontalLine(p2), p2);
			}	
			else if (currentOrientation.isVertical()) {
				outlineIntersect = ((DianaShape)endArea).outlineIntersect(DianaLine.makeVerticalLine(p2), p2);
			}	
			if (outlineIntersect != null) p2 = outlineIntersect;
			else {
				logger.warning("Could not compute outlineIntersect() from "+p2);
			}		
		}*/

		getPolylin().updatePointAt(segmentsNb - 1, p1);
		getConnector().getControlPoints().get(segmentsNb - 1).setPoint(p1);

		getPolylin().updatePointAt(segmentsNb, p2);
		getConnector().getControlPoints().get(segmentsNb).setPoint(p2);

		getConnector()._connectorChanged(true);
		getNode().notifyConnectorModified();

		return true;

	}

	/*@Override
	public void stopDragging(AbstractDianaEditor controller)
	{
		if (beforePreviousSegment != null && beforePreviousSegment.overlap(currentSegment)) {
			getConnector()._simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(segmentsNb-2);
		}
		super.stopDragging(controller);
	}*/
}
