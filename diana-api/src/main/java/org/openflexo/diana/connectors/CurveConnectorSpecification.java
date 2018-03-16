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
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "ArcConnector")
public interface CurveConnectorSpecification extends ConnectorSpecification {

	// Property keys

	@PropertyIdentifier(type = FGEPoint.class)
	public static final String CP_POSITION_KEY = "cpPosition";
	@PropertyIdentifier(type = FGEPoint.class)
	public static final String CP1_RELATIVE_TO_START_OBJECT_KEY = "cp1RelativeToStartObject";
	@PropertyIdentifier(type = FGEPoint.class)
	public static final String CP2_RELATIVE_TO_END_OBJECT_KEY = "cp2RelativeToEndObject";
	@PropertyIdentifier(type = Boolean.class)
	public static final String ARE_BOUNDS_ADJUSTABLE_KEY = "areBoundsAdjustable";

	/*public static enum CurveConnectorParameters implements GRProperty {
		cpPosition, cp1RelativeToStartObject, cp2RelativeToEndObject, areBoundsAdjustable;
	}*/

	public static GRProperty<FGEPoint> CP_POSITION = GRProperty.getGRParameter(CurveConnectorSpecification.class, CP_POSITION_KEY,
			FGEPoint.class);
	public static GRProperty<FGEPoint> CP1_RELATIVE_TO_START_OBJECT = GRProperty.getGRParameter(CurveConnectorSpecification.class,
			CP1_RELATIVE_TO_START_OBJECT_KEY, FGEPoint.class);
	public static GRProperty<FGEPoint> CP2_RELATIVE_TO_END_OBJECT = GRProperty.getGRParameter(CurveConnectorSpecification.class,
			CP2_RELATIVE_TO_END_OBJECT_KEY, FGEPoint.class);
	public static GRProperty<Boolean> ARE_BOUNDS_ADJUSTABLE = GRProperty.getGRParameter(CurveConnectorSpecification.class,
			ARE_BOUNDS_ADJUSTABLE_KEY, Boolean.class);

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = CP1_RELATIVE_TO_START_OBJECT_KEY, isStringConvertable = true)
	@XMLAttribute
	public FGEPoint getCp1RelativeToStartObject();

	@Setter(value = CP1_RELATIVE_TO_START_OBJECT_KEY)
	public void setCp1RelativeToStartObject(FGEPoint aPoint);

	@Getter(value = CP2_RELATIVE_TO_END_OBJECT_KEY, isStringConvertable = true)
	@XMLAttribute
	public FGEPoint getCp2RelativeToEndObject();

	@Setter(value = CP2_RELATIVE_TO_END_OBJECT_KEY)
	public void setCp2RelativeToEndObject(FGEPoint aPoint);

	@Getter(value = CP_POSITION_KEY, isStringConvertable = true)
	@XMLAttribute
	public FGEPoint getCpPosition();

	@Setter(value = CP_POSITION_KEY)
	public void setCpPosition(FGEPoint cpPosition);

	@Getter(value = ARE_BOUNDS_ADJUSTABLE_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getAreBoundsAdjustable();

	@Setter(value = ARE_BOUNDS_ADJUSTABLE_KEY)
	public void setAreBoundsAdjustable(boolean aFlag);

}
