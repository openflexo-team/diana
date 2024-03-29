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

import javax.swing.ImageIcon;

import org.openflexo.diana.DianaIconLibrary;
import org.openflexo.diana.DianaObject;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.GRProperty;
import org.openflexo.diana.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;

/**
 * This is the specification of a Connector<br>
 * Contains all the properties required to manage a Connector
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@Imports({ @Import(LineConnectorSpecification.class), @Import(CurveConnectorSpecification.class),
		@Import(RectPolylinConnectorSpecification.class), @Import(CurvedPolylinConnectorSpecification.class) })
public interface ConnectorSpecification extends DianaObject {

	@PropertyIdentifier(type = StartSymbolType.class)
	public static final String START_SYMBOL_KEY = "startSymbol";
	@PropertyIdentifier(type = EndSymbolType.class)
	public static final String END_SYMBOL_KEY = "endSymbol";
	@PropertyIdentifier(type = MiddleSymbolType.class)
	public static final String MIDDLE_SYMBOL_KEY = "middleSymbol";
	@PropertyIdentifier(type = Double.class)
	public static final String START_SYMBOL_SIZE_KEY = "startSymbolSize";
	@PropertyIdentifier(type = Double.class)
	public static final String END_SYMBOL_SIZE_KEY = "endSymbolSize";
	@PropertyIdentifier(type = Double.class)
	public static final String MIDDLE_SYMBOL_SIZE_KEY = "middleSymbolSize";
	@PropertyIdentifier(type = Double.class)
	public static final String RELATIVE_MIDDLE_SYMBOL_LOCATION_KEY = "relativeMiddleSymbolLocation";
	@PropertyIdentifier(type = Double.class)
	public static final String RELATIVE_LABEL_LOCATION_KEY = "relativeLabelLocation";
	@PropertyIdentifier(type = DianaPoint.class)
	public static final String REFLEXIVE_CONTROL_POINT_LOCATION_KEY = "reflexiveControlPoint";

	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_STARTING_LOCATION_FIXED_KEY = "isStartingLocationFixed";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_ENDING_LOCATION_FIXED_KEY = "isEndingLocationFixed";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_STARTING_LOCATION_DRAGGABLE_KEY = "isStartingLocationDraggable";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_ENDING_LOCATION_DRAGGABLE_KEY = "isEndingLocationDraggable";

	@PropertyIdentifier(type = DianaPoint.class)
	public static final String FIXED_START_LOCATION_KEY = "fixedStartLocation";
	@PropertyIdentifier(type = DianaPoint.class)
	public static final String FIXED_END_LOCATION_KEY = "fixedEndLocation";

	public static GRProperty<StartSymbolType> START_SYMBOL = GRProperty.getGRParameter(ConnectorSpecification.class, START_SYMBOL_KEY,
			StartSymbolType.class);
	public static GRProperty<Double> START_SYMBOL_SIZE = GRProperty.getGRParameter(ConnectorSpecification.class, START_SYMBOL_SIZE_KEY,
			Double.class);
	public static GRProperty<MiddleSymbolType> MIDDLE_SYMBOL = GRProperty.getGRParameter(ConnectorSpecification.class, MIDDLE_SYMBOL_KEY,
			MiddleSymbolType.class);
	public static GRProperty<Double> MIDDLE_SYMBOL_SIZE = GRProperty.getGRParameter(ConnectorSpecification.class, MIDDLE_SYMBOL_SIZE_KEY,
			Double.class);
	public static GRProperty<EndSymbolType> END_SYMBOL = GRProperty.getGRParameter(ConnectorSpecification.class, END_SYMBOL_KEY,
			EndSymbolType.class);
	public static GRProperty<Double> END_SYMBOL_SIZE = GRProperty.getGRParameter(ConnectorSpecification.class, END_SYMBOL_SIZE_KEY,
			Double.class);

	public static GRProperty<DianaPoint> REFLEXIVE_CONTROL_POINT_LOCATION = GRProperty.getGRParameter(LineConnectorSpecification.class,
			REFLEXIVE_CONTROL_POINT_LOCATION_KEY, DianaPoint.class);

	public static GRProperty<Double> RELATIVE_MIDDLE_SYMBOL_LOCATION = GRProperty.getGRParameter(ConnectorSpecification.class,
			RELATIVE_MIDDLE_SYMBOL_LOCATION_KEY, Double.class);

	public static GRProperty<Double> RELATIVE_LABEL_LOCATION = GRProperty.getGRParameter(ConnectorSpecification.class,
			RELATIVE_LABEL_LOCATION_KEY, Double.class);

	public static GRProperty<Boolean> IS_STARTING_LOCATION_FIXED = GRProperty.getGRParameter(RectPolylinConnectorSpecification.class,
			IS_STARTING_LOCATION_FIXED_KEY, Boolean.class);
	public static GRProperty<Boolean> IS_ENDING_LOCATION_FIXED = GRProperty.getGRParameter(RectPolylinConnectorSpecification.class,
			IS_ENDING_LOCATION_FIXED_KEY, Boolean.class);
	public static GRProperty<Boolean> IS_STARTING_LOCATION_DRAGGABLE = GRProperty.getGRParameter(RectPolylinConnectorSpecification.class,
			IS_STARTING_LOCATION_DRAGGABLE_KEY, Boolean.class);
	public static GRProperty<Boolean> IS_ENDING_LOCATION_DRAGGABLE = GRProperty.getGRParameter(RectPolylinConnectorSpecification.class,
			IS_ENDING_LOCATION_DRAGGABLE_KEY, Boolean.class);

	public static GRProperty<DianaPoint> FIXED_START_LOCATION = GRProperty.getGRParameter(RectPolylinConnectorSpecification.class,
			FIXED_START_LOCATION_KEY, DianaPoint.class);
	public static GRProperty<DianaPoint> FIXED_END_LOCATION = GRProperty.getGRParameter(RectPolylinConnectorSpecification.class,
			FIXED_END_LOCATION_KEY, DianaPoint.class);

	public static enum ConnectorType {
		LINE, RECT_POLYLIN, CURVE, CURVED_POLYLIN;

		public ImageIcon getIcon() {
			if (this == RECT_POLYLIN) {
				return DianaIconLibrary.RECT_POLYLIN_CONNECTOR_ICON;
			}
			else if (this == CURVE) {
				return DianaIconLibrary.CURVE_CONNECTOR_ICON;
			}
			else if (this == LINE) {
				return DianaIconLibrary.LINE_CONNECTOR_ICON;
			}
			return null;
		}

	}

	public ConnectorType getConnectorType();

	@Getter(value = START_SYMBOL_KEY)
	@XMLAttribute
	public StartSymbolType getStartSymbol();

	@Setter(value = START_SYMBOL_KEY)
	public void setStartSymbol(StartSymbolType startSymbol);

	@Getter(value = END_SYMBOL_KEY)
	@XMLAttribute
	public EndSymbolType getEndSymbol();

	@Setter(value = END_SYMBOL_KEY)
	public void setEndSymbol(EndSymbolType endSymbol);

	@Getter(value = MIDDLE_SYMBOL_KEY)
	@XMLAttribute
	public MiddleSymbolType getMiddleSymbol();

	@Setter(value = MIDDLE_SYMBOL_KEY)
	public void setMiddleSymbol(MiddleSymbolType middleSymbol);

	@Getter(value = START_SYMBOL_SIZE_KEY, defaultValue = "10.0")
	@XMLAttribute
	public double getStartSymbolSize();

	@Setter(value = START_SYMBOL_SIZE_KEY)
	public void setStartSymbolSize(double startSymbolSize);

	@Getter(value = END_SYMBOL_SIZE_KEY, defaultValue = "10.0")
	@XMLAttribute
	public double getEndSymbolSize();

	@Setter(value = END_SYMBOL_SIZE_KEY)
	public void setEndSymbolSize(double endSymbolSize);

	@Getter(value = MIDDLE_SYMBOL_SIZE_KEY, defaultValue = "10.0")
	@XMLAttribute
	public double getMiddleSymbolSize();

	@Setter(value = MIDDLE_SYMBOL_SIZE_KEY)
	public void setMiddleSymbolSize(double middleSymbolSize);

	@Getter(value = RELATIVE_MIDDLE_SYMBOL_LOCATION_KEY, defaultValue = "0.5")
	@XMLAttribute
	public double getRelativeMiddleSymbolLocation();

	@Setter(value = RELATIVE_MIDDLE_SYMBOL_LOCATION_KEY)
	public void setRelativeMiddleSymbolLocation(double relativeMiddleSymbolLocation);

	@Getter(value = RELATIVE_LABEL_LOCATION_KEY, defaultValue = "0.5")
	@XMLAttribute
	public double getRelativeLabelLocation();

	@Setter(value = RELATIVE_LABEL_LOCATION_KEY)
	public void setRelativeLabelLocation(double relativeLabelLocation);

	/**
	 * Returns location of control point in the case of a reflexive connector (source and destination shapes are the same)
	 * 
	 * @return
	 */
	@Getter(value = REFLEXIVE_CONTROL_POINT_LOCATION_KEY, isStringConvertable = true)
	@XMLAttribute
	public DianaPoint getReflexiveControlPointLocation();

	/**
	 * Sets location of control point in the case of a reflexive connector (source and destination shapes are the same)
	 * 
	 */
	@Setter(value = REFLEXIVE_CONTROL_POINT_LOCATION_KEY)
	public void setReflexiveControlPointLocation(DianaPoint aPoint);

	@Getter(value = IS_STARTING_LOCATION_FIXED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsStartingLocationFixed();

	@Setter(value = IS_STARTING_LOCATION_FIXED_KEY)
	public void setIsStartingLocationFixed(boolean aFlag);

	@Getter(value = IS_STARTING_LOCATION_DRAGGABLE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsStartingLocationDraggable();

	@Setter(value = IS_STARTING_LOCATION_DRAGGABLE_KEY)
	public void setIsStartingLocationDraggable(boolean aFlag);

	@Getter(value = IS_ENDING_LOCATION_FIXED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsEndingLocationFixed();

	@Setter(value = IS_ENDING_LOCATION_FIXED_KEY)
	public void setIsEndingLocationFixed(boolean aFlag);

	@Getter(value = IS_ENDING_LOCATION_DRAGGABLE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsEndingLocationDraggable();

	@Setter(value = IS_ENDING_LOCATION_DRAGGABLE_KEY)
	public void setIsEndingLocationDraggable(boolean aFlag);

	/**
	 * Return start location asserting start location is fixed. Return position relative to start object (in the start-object coordinates
	 * system)
	 * 
	 * @return
	 */
	@Getter(value = FIXED_START_LOCATION_KEY, isStringConvertable = true)
	@XMLAttribute
	public DianaPoint getFixedStartLocation();

	/**
	 * Sets start location asserting start location is fixed. Sets position relative to start object (in the start-object coordinates
	 * system)
	 * 
	 * @param aPoint
	 *            : relative to start object
	 */
	@Setter(value = FIXED_START_LOCATION_KEY)
	public void setFixedStartLocation(DianaPoint aPoint);

	/**
	 * Return end location asserting end location is fixed. Return position relative to end object (in the end-object coordinates system)
	 * 
	 * @return
	 */
	@Getter(value = FIXED_END_LOCATION_KEY, isStringConvertable = true)
	@XMLAttribute
	public DianaPoint getFixedEndLocation();

	/**
	 * Sets end location asserting end location is fixed. Sets position relative to end object (in the end-object coordinates system)
	 * 
	 * @param aPoint
	 *            , relative to end object
	 */
	@Setter(value = FIXED_END_LOCATION_KEY)
	public void setFixedEndLocation(DianaPoint aPoint);

	public Connector<?> makeConnector(ConnectorNode<?> connectorNode);
}
