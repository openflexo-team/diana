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

package org.openflexo.diana;

import org.openflexo.diana.connectors.ConnectorSpecification;
import org.openflexo.diana.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;

/**
 * Represents a connector linking two shapes in a diagram<br>
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "ConnectorGraphicalRepresentation")
public interface ConnectorGraphicalRepresentation extends GraphicalRepresentation {

	// Property keys

	// public static final String START_OBJECT = "startObject";
	// public static final String END_OBJECT = "endObject";
	@PropertyIdentifier(type = ConnectorSpecification.class)
	public static final String CONNECTOR_SPECIFICATION_KEY = "connectorSpecification";
	@PropertyIdentifier(type = ConnectorType.class)
	public static final String CONNECTOR_TYPE_KEY = "connectorType";

	@PropertyIdentifier(type = ForegroundStyle.class)
	public static final String FOREGROUND_KEY = "foreground";
	@PropertyIdentifier(type = ForegroundStyle.class)
	public static final String SELECTED_FOREGROUND_KEY = "selectedForeground";
	@PropertyIdentifier(type = ForegroundStyle.class)
	public static final String FOCUSED_FOREGROUND_KEY = "focusedForeground";
	@PropertyIdentifier(type = Boolean.class)
	public static final String HAS_SELECTED_FOREGROUND_KEY = "hasSelectedForeground";
	@PropertyIdentifier(type = Boolean.class)
	public static final String HAS_FOCUSED_FOREGROUND_KEY = "hasFocusedForeground";

	@PropertyIdentifier(type = Boolean.class)
	public static final String APPLY_FOREGROUND_TO_SYMBOLS_KEY = "applyForegroundToSymbols";
	@PropertyIdentifier(type = Boolean.class)
	public static final String DEBUG_COVERING_AREA_KEY = "debugCoveringArea";

	public static GRProperty<ConnectorSpecification> CONNECTOR = GRProperty.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.CONNECTOR_SPECIFICATION_KEY, ConnectorSpecification.class);
	public static GRProperty<ConnectorType> CONNECTOR_TYPE = GRProperty.getGRParameter(ConnectorGraphicalRepresentation.class,
			CONNECTOR_TYPE_KEY, ConnectorType.class);

	public static GRProperty<ForegroundStyle> FOREGROUND = GRProperty.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.FOREGROUND_KEY, ForegroundStyle.class);
	public static GRProperty<ForegroundStyle> SELECTED_FOREGROUND = GRProperty.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.SELECTED_FOREGROUND_KEY, ForegroundStyle.class);
	public static GRProperty<ForegroundStyle> FOCUSED_FOREGROUND = GRProperty.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.FOCUSED_FOREGROUND_KEY, ForegroundStyle.class);

	public static GRProperty<Boolean> APPLY_FOREGROUND_TO_SYMBOLS = GRProperty.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.APPLY_FOREGROUND_TO_SYMBOLS_KEY, Boolean.class);
	public static GRProperty<Boolean> DEBUG_COVERING_AREA = GRProperty.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.DEBUG_COVERING_AREA_KEY, Boolean.class);

	/*public static enum ConnectorParameters implements GRProperty {
		connector,
		foreground,
		selectedForeground,
		focusedForeground,
		hasSelectedForeground,
		hasFocusedForeground,
		startSymbol,
		endSymbol,
		middleSymbol,
		startSymbolSize,
		endSymbolSize,
		middleSymbolSize,
		relativeMiddleSymbolLocation,
		applyForegroundToSymbols,
		debugCoveringArea
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	/*@Getter(value = START_OBJECT)
	@XMLElement(context = "Start")
	public ShapeGraphicalRepresentation getStartObject();

	@Setter(value = START_OBJECT)
	public void setStartObject(ShapeGraphicalRepresentation aStartObject);

	@Getter(value = END_OBJECT)
	@XMLElement(context = "End")
	public ShapeGraphicalRepresentation getEndObject();

	@Setter(value = END_OBJECT)
	public void setEndObject(ShapeGraphicalRepresentation anEndObject);*/

	@Getter(value = CONNECTOR_SPECIFICATION_KEY)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	public ConnectorSpecification getConnectorSpecification();

	@Setter(value = CONNECTOR_SPECIFICATION_KEY)
	public void setConnectorSpecification(ConnectorSpecification aConnector);

	@Getter(value = FOREGROUND_KEY)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	public ForegroundStyle getForeground();

	@Setter(value = FOREGROUND_KEY)
	public void setForeground(ForegroundStyle aForeground);

	@Getter(value = SELECTED_FOREGROUND_KEY)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement(context = "Selected")
	public ForegroundStyle getSelectedForeground();

	@Setter(value = SELECTED_FOREGROUND_KEY)
	public void setSelectedForeground(ForegroundStyle aForeground);

	@Getter(value = HAS_SELECTED_FOREGROUND_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getHasSelectedForeground();

	@Setter(value = HAS_SELECTED_FOREGROUND_KEY)
	public void setHasSelectedForeground(boolean aFlag);

	@Getter(value = FOCUSED_FOREGROUND_KEY)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement(context = "Focused")
	public ForegroundStyle getFocusedForeground();

	@Setter(value = FOCUSED_FOREGROUND_KEY)
	public void setFocusedForeground(ForegroundStyle aForeground);

	@Getter(value = HAS_FOCUSED_FOREGROUND_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getHasFocusedForeground();

	@Setter(value = HAS_FOCUSED_FOREGROUND_KEY)
	public void setHasFocusedForeground(boolean aFlag);

	@Getter(value = APPLY_FOREGROUND_TO_SYMBOLS_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getApplyForegroundToSymbols();

	@Setter(value = APPLY_FOREGROUND_TO_SYMBOLS_KEY)
	public void setApplyForegroundToSymbols(boolean applyForegroundToSymbols);

	// *******************************************************************************
	// * Utils
	// *******************************************************************************

	// public void notifyConnectorChanged();

	public void notifyConnectorModified();

	public void notifyConnectorNeedsToBeRedrawn();

	public ConnectorType getConnectorType();

	public void setConnectorType(ConnectorType connectorType);

	// public void observeRelevantObjects();

	// public int getExtendedX(double scale);

	// public int getExtendedY(double scale);

	/**
	 * Return normalized bounds Those bounds corresponds to the normalized area defined as (0.0,0.0)-(1.0,1.0) enclosing EXACTELY the two
	 * related shape bounds. Those bounds should eventually be extended to contain connector contained outside this area.
	 */
	// public Rectangle getNormalizedBounds(double scale);

	/**
	 * Return distance from point to connector representation with a given scale
	 * 
	 * @param aPoint
	 *            expressed in local normalized coordinates system
	 * @param scale
	 * @return
	 */
	// public double distanceToConnector(DianaPoint aPoint, double scale, ConnectorNode<?> connectorNode);

	// public boolean isConnectorConsistent();

	// public void refreshConnector();

	// Override for a custom view management
	// public ConnectorView makeConnectorView(DianaEditor controller);

	public boolean getDebugCoveringArea();

	public void setDebugCoveringArea(boolean debugCoveringArea);

	// public DianaConnectorGraphics getGraphics();

	// public List<? extends ControlArea<?>> getControlAreas();

	// public ConnectorGraphicalRepresentation clone();

}
