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

package org.openflexo.diana.connectors.impl;

import java.util.logging.Logger;

import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.connectors.CurveConnectorSpecification;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.notifications.DianaAttributeNotification;

public abstract class CurveConnectorSpecificationImpl extends ConnectorSpecificationImpl implements CurveConnectorSpecification {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(CurveConnectorSpecification.class.getPackage().getName());

	private DianaPoint cpPosition;
	private DianaPoint cp1Position;
	private DianaPoint cp2Position;

	private CurveConnectorType curveConnectorType = CurveConnectorType.CUBIC_CURVE;

	@Override
	public CurveConnectorType getCurveConnectorType() {
		return curveConnectorType;
	}

	@Override
	public void setCurveConnectorType(CurveConnectorType aCurveConnectorType) {
		DianaAttributeNotification<?> notification = requireChange(CURVE_CONNECTOR_TYPE, aCurveConnectorType);
		if (notification != null) {
			curveConnectorType = aCurveConnectorType;
			hasChanged(notification);
		}
	}

	@Override
	public DianaPoint getCpPosition() {
		return cpPosition;
	}

	@Override
	public void setCpPosition(DianaPoint aPoint) {
		DianaAttributeNotification<?> notification = requireChange(CP_POSITION, aPoint);
		if (notification != null) {
			this.cpPosition = aPoint;
			hasChanged(notification);
		}
	}

	@Override
	public DianaPoint getCp1Position() {
		return cp1Position;
	}

	@Override
	public void setCp1Position(DianaPoint aPoint) {
		DianaAttributeNotification<?> notification = requireChange(CP1_POSITION, aPoint);
		if (notification != null) {
			this.cp1Position = aPoint;
			hasChanged(notification);
		}
	}

	@Override
	public DianaPoint getCp2Position() {
		return cp2Position;
	}

	@Override
	public void setCp2Position(DianaPoint aPoint) {
		DianaAttributeNotification<?> notification = requireChange(CP2_POSITION, aPoint);
		if (notification != null) {
			this.cp2Position = aPoint;
			hasChanged(notification);
		}
	}

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.CURVE;
	}

	@Override
	public CurveConnector makeConnector(ConnectorNode<?> connectorNode) {
		return new CurveConnector(connectorNode);
	}

}
