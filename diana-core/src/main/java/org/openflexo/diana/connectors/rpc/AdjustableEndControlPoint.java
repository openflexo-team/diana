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

import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.connectors.impl.RectPolylinConnector;
import org.openflexo.diana.control.DianaEditor;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectPolylin;
import org.openflexo.diana.geom.area.DianaArea;

public class AdjustableEndControlPoint extends RectPolylinAdjustableControlPoint {
	static final Logger LOGGER = Logger.getLogger(AdjustableEndControlPoint.class.getPackage().getName());

	public AdjustableEndControlPoint(DianaPoint point, RectPolylinConnector connector) {
		super(point, connector);
	}

	@Override
	public DianaArea getDraggingAuthorizedArea() {
		return getConnector().retrieveAllowedEndArea(false);
	}

	@Override
	public void startDragging(DianaEditor<?> controller, DianaPoint startPoint) {
		super.startDragging(controller, startPoint);
		getConnectorSpecification().setIsStartingLocationFixed(true);
	}

	@Override
	public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration, DianaPoint newAbsolutePoint,
			DianaPoint initialPoint, MouseEvent event) {
		DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
		if (pt == null) {
			LOGGER.warning("Cannot nearest point for point " + newRelativePoint + " and area " + getDraggingAuthorizedArea());
			return false;
		}
		setPoint(pt);
		DianaPoint ptRelativeToEndObject = DianaUtils.convertNormalizedPoint(getNode(), pt, getConnector().getEndNode());
		getConnector().setFixedEndLocation(ptRelativeToEndObject);
		switch (getConnectorSpecification().getAdjustability()) {
			case AUTO_LAYOUT:
				// Nothing special to do
				break;
			case BASICALLY_ADJUSTABLE:
				// Nothing special to do
				break;
			case FULLY_ADJUSTABLE:
				if (initialPolylin.getSegmentNb() == 1 && getConnector()._updateAsFullyAdjustableForUniqueSegment(pt)
						&& !getConnectorSpecification().getIsStartingLocationFixed()) {
					// OK this is still a unique segment, nice !
				}
				else {
					DianaRectPolylin newPolylin = initialPolylin.clone();
					newPolylin.updatePointAt(newPolylin.getPointsNb() - 1, pt);
					getConnector().updateWithNewPolylin(newPolylin, true);
				}
				break;
			default:
				break;
		}
		getConnector()._connectorChanged(true);
		getNode().notifyConnectorModified();

		return true;
	}

}
