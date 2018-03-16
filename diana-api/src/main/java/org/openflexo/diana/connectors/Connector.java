/**
 * 
 * Copyright (c) 2014, Openflexo
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

import java.beans.PropertyChangeListener;
import java.util.List;

import org.openflexo.diana.GRProperty;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.geom.FGERectangle;
import org.openflexo.diana.graphics.FGEConnectorGraphics;

public interface Connector<CS extends ConnectorSpecification> extends PropertyChangeListener {

	public abstract CS getConnectorSpecification();

	public abstract ConnectorNode<?> getConnectorNode();

	public abstract ShapeNode<?> getStartNode();

	public abstract ShapeNode<?> getEndNode();

	public abstract ConnectorType getConnectorType();

	public abstract double getStartAngle();

	public abstract double getEndAngle();

	public abstract double distanceToConnector(FGEPoint aPoint, double scale);

	public abstract void drawConnector(FGEConnectorGraphics g);

	/**
	 * Retrieve all control area used to manage this connector
	 * 
	 * @return
	 */
	public abstract List<? extends ControlArea<?>> getControlAreas();

	public abstract FGEPoint getMiddleSymbolLocation();

	/**
	 * Return bounds of actually required area to fully display current connector (which might require to be paint outside normalized
	 * bounds)
	 * 
	 * @return
	 */
	public abstract FGERectangle getConnectorUsedBounds();

	/**
	 * Return start point, relative to start object
	 * 
	 * @return
	 */
	public abstract FGEPoint getStartLocation();

	/**
	 * Return end point, relative to end object
	 * 
	 * @return
	 */
	public abstract FGEPoint getEndLocation();

	public abstract void paintConnector(FGEConnectorGraphics g);

	public abstract void delete();

	public boolean isDeleted();

	/**
	 * Returns the property value for supplied parameter<br>
	 * If many Connectors share same ConnectorSpecification (as indicated by {@link Drawing#getPersistenceMode()), do not store value in
	 * ConnectorSpecification, but store it in the Connector itself.<br> This implies that this value is not persistent (not serializable)
	 * 
	 * @param parameter
	 * @return
	 */
	public <T> T getPropertyValue(GRProperty<T> parameter);

	/**
	 * Sets the property value for supplied parameter<br>
	 * If many Connectors share same ConnectorSpecification (as indicated by {@link Drawing#getPersistenceMode()), do not store value in
	 * ConnectorSpecification, but store it in the Connector itself.<br> This implies that this value is not persistent (not serializable)
	 * 
	 * @param parameter
	 * @return
	 */
	public <T> void setPropertyValue(GRProperty<T> parameter, T value);

}
