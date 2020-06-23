/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geomedit, a component of the software infrastructure 
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

package org.openflexo.diana.geomedit.model;

import java.beans.PropertyChangeEvent;

import org.openflexo.diana.connectors.ConnectorSpecification;
import org.openflexo.diana.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geomedit.model.ConnectorConstruction.ConnectorConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.NodeConnectorGraphicalRepresentation;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.DeserializationInitializer;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(ConnectorConstructionImpl.class)
public interface ConnectorConstruction extends GeometricConstruction<DianaArea> {

	@PropertyIdentifier(type = ConnectorSpecification.class)
	public static final String CONNECTOR_SPECIFICATION_KEY = "connectorSpecification";
	@PropertyIdentifier(type = NodeConstruction.class)
	public static final String START_NODE_KEY = "startNode";
	@PropertyIdentifier(type = NodeConstruction.class)
	public static final String END_NODE_KEY = "endNode";

	@Getter(value = START_NODE_KEY)
	@XMLElement
	public NodeConstruction getStartNode();

	@Setter(value = START_NODE_KEY)
	public void setStartNode(NodeConstruction startNode);

	@Getter(value = END_NODE_KEY)
	@XMLElement
	public NodeConstruction getEndNode();

	@Setter(value = END_NODE_KEY)
	public void setEndNode(NodeConstruction endNode);

	@Getter(value = CONNECTOR_SPECIFICATION_KEY)
	@XMLElement
	@Embedded
	@CloningStrategy(StrategyType.CLONE)
	public ConnectorSpecification getConnectorSpecification();

	@Setter(value = CONNECTOR_SPECIFICATION_KEY)
	public void setConnectorSpecification(ConnectorSpecification connectorSpecification);

	public DianaArea getConnector();

	public ConnectorType getConnectorType();

	public void setConnectorType(ConnectorType connectorType);

	public GeometricConstructionFactory getFactory();

	public void setFactory(GeometricConstructionFactory factory);

	@DeserializationInitializer
	public void initializeDeserialization(GeometricConstructionFactory factory);

	public abstract class ConnectorConstructionImpl extends GeometricConstructionImpl<DianaArea> implements ConnectorConstruction {

		private GeometricConstructionFactory factory;

		@Override
		public void initializeDeserialization(GeometricConstructionFactory factory) {
			setFactory(factory);
		}

		@Override
		public String getBaseName() {
			return "Connector";
		}

		@Override
		public final DianaArea getConnector() {
			return getData();
		}

		@Override
		public NodeConnectorGraphicalRepresentation makeNewConstructionGR(GeometricConstructionFactory factory) {
			NodeConnectorGraphicalRepresentation returned = factory.newInstance(NodeConnectorGraphicalRepresentation.class);
			return returned;
		}

		@Override
		public ConnectorType getConnectorType() {
			if (getConnectorSpecification() != null) {
				return getConnectorSpecification().getConnectorType();
			}
			else {
				return ConnectorType.LINE;
			}
		}

		@Override
		public void setConnectorType(ConnectorType connectorType) {
			System.out.println("******** setConnectorType with " + connectorType);
			if (getConnectorType() != connectorType) {
				ConnectorSpecification connectorSpecification = getFactory().makeConnector(connectorType);
				setConnectorSpecification(connectorSpecification);
				getPropertyChangeSupport().firePropertyChange("connectorType", null, connectorType);
				refresh();
				notifyGeometryChanged();
			}
		}

		@Override
		public GeometricConstructionFactory getFactory() {
			return factory;
		}

		@Override
		public void setFactory(GeometricConstructionFactory factory) {
			this.factory = factory;
		}

		@Override
		public void setConnectorSpecification(ConnectorSpecification aConnectorSpecification) {
			ConnectorSpecification oldConnectorSpecification = getConnectorSpecification();
			if (oldConnectorSpecification != aConnectorSpecification) {
				_setConnectorSpecificationNoNotification(aConnectorSpecification);
				refresh();
				notifyGeometryChanged();
			}
		}

		protected void _setConnectorSpecificationNoNotification(ConnectorSpecification aConnectorSpecification) {
			ConnectorSpecification oldConnectorSpecification = getConnectorSpecification();
			if (oldConnectorSpecification != aConnectorSpecification) {
				if (oldConnectorSpecification != null) {
					oldConnectorSpecification.getPropertyChangeSupport().removePropertyChangeListener(this);
				}
				performSuperSetter(CONNECTOR_SPECIFICATION_KEY, aConnectorSpecification);
				if (aConnectorSpecification != null) {
					aConnectorSpecification.getPropertyChangeSupport().addPropertyChangeListener(this);
					getPropertyChangeSupport().firePropertyChange("connectorType", null, getConnectorType());
				}
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == getConnectorSpecification()) {
				refresh();
				notifyGeometryChanged();
			}
			else {
				super.propertyChange(evt);
			}
		}

		@Override
		public void notifyGeometryChanged() {
			super.notifyGeometryChanged();
		}

		@Override
		protected DianaArea computeData() {

			if (getStartNode() != null && getEndNode() != null && getFactory() != null) {
				DianaPoint startNodeCenter = getStartNode().getData().getCenter();
				DianaPoint endNodeCenter = getEndNode().getData().getCenter();

				if (getConnectorSpecification() == null) {
					ConnectorSpecification connectorSpecification = getFactory().makeConnector(getConnectorType());
					_setConnectorSpecificationNoNotification(connectorSpecification);
				}

				DianaArea returned = new DianaSegment(startNodeCenter, endNodeCenter);

				return returned;
			}

			return null;
		}

		@Override
		public String toString() {
			return "ConnectorConstruction[" + getStartNode().toString() + " - " + getEndNode().toString() + "]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { getStartNode(), getEndNode() };
			return returned;
		}

	}
}
