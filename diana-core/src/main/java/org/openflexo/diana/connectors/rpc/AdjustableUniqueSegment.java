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
import java.util.logging.Logger;

import org.openflexo.diana.connectors.impl.RectPolylinConnector;
import org.openflexo.diana.control.DianaEditor;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;

public class AdjustableUniqueSegment extends RectPolylinAdjustableSegment {
	static final Logger LOGGER = Logger.getLogger(AdjustableUniqueSegment.class.getPackage().getName());

	private boolean consistentData = false;
	private DianaSegment currentSegment;
	private SimplifiedCardinalDirection currentOrientation;
	private DianaArea startArea;
	private DianaArea endArea;
	private DianaArea draggingAuthorizedArea;

	public AdjustableUniqueSegment(DianaSegment segment, RectPolylinConnector connector) {
		super(segment, connector);
		retrieveInfos();
	}

	private void retrieveInfos() {
		currentSegment = getArea();
		if (currentSegment.getApproximatedOrientation() == null) {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return;
		}
		currentOrientation = currentSegment.getApproximatedOrientation();

		startArea = getConnector().retrieveAllowedStartArea(false);
		endArea = getConnector().retrieveAllowedEndArea(false);

		/*
		AffineTransform at1 = GraphicalRepresentation.convertNormalizedCoordinatesAT(
				getConnector().getStartObject(), getGraphicalRepresentation());
		startArea = getConnector().getStartObject().getShape().getOutline().transform(at1);
		DianaArea startOrthogonalPerspectiveArea = startArea.getOrthogonalPerspectiveArea(currentOrientation);
		
		AffineTransform at2 = GraphicalRepresentation.convertNormalizedCoordinatesAT(
				getConnector().getEndObject(), getGraphicalRepresentation());
		endArea = getConnector().getEndObject().getShape().getOutline().transform(at2);
		DianaArea endOrthogonalPerspectiveArea = endArea.getOrthogonalPerspectiveArea(currentOrientation.getOpposite());
		*/

		DianaArea startOrthogonalPerspectiveArea = startArea.getOrthogonalPerspectiveArea(currentOrientation);
		DianaArea endOrthogonalPerspectiveArea = endArea.getOrthogonalPerspectiveArea(currentOrientation.getOpposite());

		draggingAuthorizedArea = startOrthogonalPerspectiveArea.intersect(endOrthogonalPerspectiveArea);

		consistentData = true;
	}

	@Override
	public void startDragging(DianaEditor<?> controller, DianaPoint startPoint) {
		super.startDragging(controller, startPoint);
		retrieveInfos();
		LOGGER.info("start cpts=" + getConnector().getControlAreas());
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

		DianaPoint p1 = getPolylin().getPointAt(0);
		if (p1 == null) {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}
		DianaPoint p2 = getPolylin().getPointAt(1);
		if (p2 == null) {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}

		// System.out.println("draggingAuthorizedArea="+draggingAuthorizedArea);
		// System.out.println("pt="+pt);

		p1 = startArea.nearestPointFrom(pt, currentOrientation.getOpposite());
		p2 = endArea.nearestPointFrom(pt, currentOrientation);

		/*if (currentOrientation.isHorizontal()) {
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
		}*/

		getPolylin().updatePointAt(0, p1);
		getConnector().getControlPoints().get(0).setPoint(p1);

		getPolylin().updatePointAt(1, p2);
		getConnector().getControlPoints().get(1).setPoint(p2);

		getConnector()._connectorChanged(true);
		getNode().notifyConnectorModified();

		// logger.info("drag cpts="+getConnector().getControlAreas());
		return true;
	}

}
