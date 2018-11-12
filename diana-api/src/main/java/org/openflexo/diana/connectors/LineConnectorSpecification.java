/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-api, a component of the software infrastructure 
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

package org.openflexo.diana.connectors;

import org.openflexo.diana.GRProperty;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "LineConnectorSpecification")
public interface LineConnectorSpecification extends ConnectorSpecification {

	// Property keys

	@PropertyIdentifier(type = LineConnectorType.class)
	public static final String LINE_CONNECTOR_TYPE_KEY = "lineConnectorType";
	@PropertyIdentifier(type = DianaPoint.class)
	public static final String CP1_RELATIVE_TO_START_OBJECT_KEY = "cp1RelativeToStartObject";
	@PropertyIdentifier(type = DianaPoint.class)
	public static final String CP2_RELATIVE_TO_END_OBJECT_KEY = "cp2RelativeToEndObject";

	public static GRProperty<LineConnectorType> LINE_CONNECTOR_TYPE = GRProperty.getGRParameter(LineConnectorSpecification.class,
			LINE_CONNECTOR_TYPE_KEY, LineConnectorType.class);
	public static GRProperty<DianaPoint> CP1_RELATIVE_TO_START_OBJECT = GRProperty.getGRParameter(LineConnectorSpecification.class,
			CP1_RELATIVE_TO_START_OBJECT_KEY, DianaPoint.class);
	public static GRProperty<DianaPoint> CP2_RELATIVE_TO_END_OBJECT = GRProperty.getGRParameter(LineConnectorSpecification.class,
			CP2_RELATIVE_TO_END_OBJECT_KEY, DianaPoint.class);

	/*public static enum LineConnectorParameters implements GRProperty {
		lineConnectorType, cp1RelativeToStartObject, cp2RelativeToEndObject;
	}*/

	public static enum LineConnectorType {
		CENTER_TO_CENTER, MINIMAL_LENGTH, FUNNY, ADJUSTABLE
	}

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = LINE_CONNECTOR_TYPE_KEY)
	@XMLAttribute
	public LineConnectorType getLineConnectorType();

	@Setter(value = LINE_CONNECTOR_TYPE_KEY)
	public void setLineConnectorType(LineConnectorType aLineConnectorType);

	@Getter(value = CP1_RELATIVE_TO_START_OBJECT_KEY, isStringConvertable = true)
	@XMLAttribute
	public DianaPoint getCp1RelativeToStartObject();

	@Setter(value = CP1_RELATIVE_TO_START_OBJECT_KEY)
	public void setCp1RelativeToStartObject(DianaPoint aPoint);

	@Getter(value = CP2_RELATIVE_TO_END_OBJECT_KEY, isStringConvertable = true)
	@XMLAttribute
	public DianaPoint getCp2RelativeToEndObject();

	@Setter(value = CP2_RELATIVE_TO_END_OBJECT_KEY)
	public void setCp2RelativeToEndObject(DianaPoint aPoint);

}
