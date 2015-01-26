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

package org.openflexo.fge.connectors.impl;

import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.connectors.LineConnectorSpecification;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.notifications.FGEAttributeNotification;

public abstract class LineConnectorSpecificationImpl extends ConnectorSpecificationImpl implements LineConnectorSpecification {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(LineConnectorSpecification.class.getPackage().getName());

	private FGEPoint cp1RelativeToStartObject;
	private FGEPoint cp2RelativeToEndObject;
	private LineConnectorType lineConnectorType = LineConnectorType.MINIMAL_LENGTH;

	// Used for deserialization
	public LineConnectorSpecificationImpl() {
		super();
	}

	@Override
	public LineConnectorType getLineConnectorType() {
		return lineConnectorType;
	}

	@Override
	public void setLineConnectorType(LineConnectorType aLineConnectorType) {
		FGEAttributeNotification notification = requireChange(LINE_CONNECTOR_TYPE, aLineConnectorType);
		if (notification != null) {
			lineConnectorType = aLineConnectorType;
			hasChanged(notification);
		}
	}

	@Override
	public FGEPoint getCp1RelativeToStartObject() {
		return cp1RelativeToStartObject;
	}

	@Override
	public void setCp1RelativeToStartObject(FGEPoint aPoint) {
		FGEAttributeNotification notification = requireChange(CP1_RELATIVE_TO_START_OBJECT, aPoint);
		if (notification != null) {
			this.cp1RelativeToStartObject = aPoint;
			hasChanged(notification);
		}
	}

	@Override
	public FGEPoint getCp2RelativeToEndObject() {
		return cp2RelativeToEndObject;
	}

	@Override
	public void setCp2RelativeToEndObject(FGEPoint aPoint) {
		FGEAttributeNotification notification = requireChange(CP2_RELATIVE_TO_END_OBJECT, aPoint);
		if (notification != null) {
			this.cp2RelativeToEndObject = aPoint;
			hasChanged(notification);
		}
	}

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.LINE;
	}

	/*@Override
	public LineConnectorSpecification clone() {
		LineConnectorSpecification returned = (LineConnectorSpecification) cloneObject();
		returned.setLineConnectorType(getLineConnectorType());
		returned.setCp1RelativeToStartObject(getCp1RelativeToStartObject());
		returned.setCp2RelativeToEndObject(getCp2RelativeToEndObject());
		return returned;
	}*/

	@Override
	public LineConnector makeConnector(ConnectorNode<?> connectorNode) {
		return new LineConnector(connectorNode);
	}

}
