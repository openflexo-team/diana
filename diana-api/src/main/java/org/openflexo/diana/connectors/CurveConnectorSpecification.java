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
@XMLElement(xmlTag = "ArcConnector")
public interface CurveConnectorSpecification extends ConnectorSpecification {

	public static enum CurveConnectorType {
		QUAD_CURVE, CUBIC_CURVE
	}

	// Property keys

	@PropertyIdentifier(type = CurveConnectorType.class)
	public static final String CURVE_CONNECTOR_TYPE_KEY = "curveConnectorType";
	@PropertyIdentifier(type = DianaPoint.class)
	public static final String CP_POSITION_KEY = "cpPosition";
	@PropertyIdentifier(type = DianaPoint.class)
	public static final String CP1_POSITION_KEY = "cp1Position";
	@PropertyIdentifier(type = DianaPoint.class)
	public static final String CP2_POSITION_KEY = "cp2Position";

	public static GRProperty<CurveConnectorType> CURVE_CONNECTOR_TYPE = GRProperty.getGRParameter(CurveConnectorSpecification.class,
			CURVE_CONNECTOR_TYPE_KEY, CurveConnectorType.class);

	public static GRProperty<DianaPoint> CP_POSITION = GRProperty.getGRParameter(CurveConnectorSpecification.class, CP_POSITION_KEY,
			DianaPoint.class);
	public static GRProperty<DianaPoint> CP1_POSITION = GRProperty.getGRParameter(CurveConnectorSpecification.class, CP1_POSITION_KEY,
			DianaPoint.class);
	public static GRProperty<DianaPoint> CP2_POSITION = GRProperty.getGRParameter(CurveConnectorSpecification.class, CP2_POSITION_KEY,
			DianaPoint.class);

	@Getter(value = CURVE_CONNECTOR_TYPE_KEY)
	@XMLAttribute
	public CurveConnectorType getCurveConnectorType();

	@Setter(value = CURVE_CONNECTOR_TYPE_KEY)
	public void setCurveConnectorType(CurveConnectorType aCurveConnectorType);

	@Getter(value = CP_POSITION_KEY, isStringConvertable = true)
	@XMLAttribute
	public DianaPoint getCpPosition();

	@Setter(value = CP_POSITION_KEY)
	public void setCpPosition(DianaPoint cpPosition);

	@Getter(value = CP1_POSITION_KEY, isStringConvertable = true)
	@XMLAttribute
	public DianaPoint getCp1Position();

	@Setter(value = CP1_POSITION_KEY)
	public void setCp1Position(DianaPoint cp1Position);

	@Getter(value = CP2_POSITION_KEY, isStringConvertable = true)
	@XMLAttribute
	public DianaPoint getCp2Position();

	@Setter(value = CP2_POSITION_KEY)
	public void setCp2Position(DianaPoint cp2Position);

}
