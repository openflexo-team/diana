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

import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.PersistenceMode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.GRProperty;
import org.openflexo.diana.connectors.Connector;
import org.openflexo.diana.connectors.ConnectorSpecification;
import org.openflexo.diana.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.diana.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaUnionArea;
import org.openflexo.diana.graphics.DianaConnectorGraphics;
import org.openflexo.diana.shapes.Shape;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * This is an instance of a {@link ConnectorSpecification}. As it, it is attached to a {@link ConnectorNode}. A {@link ConnectorImpl}
 * observes its {@link ConnectorSpecification}
 * 
 * @author sylvain
 * 
 */
public abstract class ConnectorImpl<CS extends ConnectorSpecification> implements Connector<CS> {

	private static final Logger LOGGER = Logger.getLogger(ConnectorSpecification.class.getPackage().getName());

	// private transient ConnectorGraphicalRepresentation graphicalRepresentation;

	private boolean debug = false;

	protected ConnectorNode<?> connectorNode;

	protected DianaRectangle NORMALIZED_BOUNDS = new DianaRectangle(0, 0, 1, 1, Filling.FILLED);

	private Shape<?> startShape;
	private DianaDimension startShapeDimension;
	private DianaPoint startShapeLocation;
	private Shape<?> endShape;
	private DianaDimension endShapeDimension;
	private DianaPoint endShapeLocation;
	private DianaRectangle knownConnectorUsedBounds;

	private final CS connectorSpecification;

	/**
	 * Store temporary properties that may not be serialized
	 */
	private Map<GRProperty<?>, Object> propertyValues = new HashMap<>();

	public ConnectorImpl(ConnectorNode<?> connectorNode) {
		super();
		this.connectorNode = connectorNode;
		connectorSpecification = (CS) connectorNode.getConnectorSpecification();
		propertyValues = new HashMap<>();
		if (connectorSpecification != null && connectorSpecification.getPropertyChangeSupport() != null) {
			connectorSpecification.getPropertyChangeSupport().addPropertyChangeListener(this);
		}
	}

	@Override
	public void delete() {

		if (getConnectorSpecification() != null && getConnectorSpecification().getPropertyChangeSupport() != null) {
			getConnectorSpecification().getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		connectorNode = null;
		startShape = null;
		startShape = null;
		startShapeLocation = null;
		endShape = null;
		endShape = null;
		endShape = null;
		knownConnectorUsedBounds = null;
		isDeleted = true;
	}

	private boolean isDeleted = false;

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public CS getConnectorSpecification() {
		/*if (connectorNode != null) {
			return (CS) connectorNode.getConnectorSpecification();
		}
		return null;*/
		return connectorSpecification;
	}

	@Override
	public ConnectorNode<?> getConnectorNode() {
		return connectorNode;
	}

	@Override
	public ShapeNode<?> getStartNode() {
		if (connectorNode == null) {
			return null;
		}
		return connectorNode.getStartNode();
	}

	@Override
	public ShapeNode<?> getEndNode() {
		if (connectorNode == null) {
			return null;
		}
		return connectorNode.getEndNode();
	}

	@Override
	public ConnectorType getConnectorType() {
		return getConnectorSpecification().getConnectorType();
	}

	// *******************************************************************************
	// * Abstract Methods *
	// *******************************************************************************

	@Override
	public abstract double getStartAngle();

	@Override
	public abstract double getEndAngle();

	@Override
	public abstract double distanceToConnector(DianaPoint aPoint, double scale);

	@Override
	public abstract void drawConnector(DianaConnectorGraphics g);

	/**
	 * Retrieve all control area used to manage this connector
	 * 
	 * @return
	 */
	@Override
	public abstract List<? extends ControlArea<?>> getControlAreas();

	/**
	 * Return bounds of actually required area to fully display current connector (which might require to be paint outside normalized
	 * bounds)
	 * 
	 * @return
	 */
	@Override
	public abstract DianaRectangle getConnectorUsedBounds();

	/**
	 * Return start point, relative to start object
	 * 
	 * @return
	 */
	@Override
	public abstract DianaPoint getStartLocation();

	/**
	 * Return end point, relative to end object
	 * 
	 * @return
	 */
	@Override
	public abstract DianaPoint getEndLocation();

	// *******************************************************************************
	// * Implementation *
	// *******************************************************************************

	public boolean getDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
		refreshConnector();
	}

	public void connectorWillBeModified() {

	}

	public void connectorHasBeenModified() {

	}

	public void setPaintAttributes(DianaConnectorGraphics g) {

		// Foreground
		if (connectorNode.getIsSelected()) {
			if (connectorNode.getGraphicalRepresentation().getHasSelectedForeground()) {
				g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getSelectedForeground());
			}
			else if (connectorNode.getGraphicalRepresentation().getHasFocusedForeground()) {
				g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getFocusedForeground());
			}
			else {
				g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getForeground());
			}
		}
		else if (connectorNode.getIsFocused() && connectorNode.getGraphicalRepresentation().getHasFocusedForeground()) {
			g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getFocusedForeground());
		}
		else {
			g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getForeground());
		}

		g.setDefaultTextStyle(connectorNode.getGraphicalRepresentation().getTextStyle());
	}

	@Override
	public final void paintConnector(DianaConnectorGraphics g) {
		/*
		 * if (DianaConstants.DEBUG || getGraphicalRepresentation().getDebugCoveringArea()) { drawCoveringAreas(g); }
		 */

		setPaintAttributes(g);
		drawConnector(g);
	}

	/**
	 * Perform an area computation related to the both extremity objects
	 * 
	 * If order equals 0, return intersection between shapes representing the two object If order equals 1, return intersection of
	 * 
	 * @param order
	 * @return
	 */
	public DianaArea computeCoveringArea(int order) {
		AffineTransform at1 = DianaUtils.convertNormalizedCoordinatesAT(connectorNode.getStartNode(), connectorNode);

		AffineTransform at2 = DianaUtils.convertNormalizedCoordinatesAT(connectorNode.getEndNode(), connectorNode);

		if (order == 0) {
			DianaArea startObjectShape = connectorNode.getStartNode().getDianaShape().transform(at1);
			DianaArea endObjectShape = connectorNode.getEndNode().getDianaShape().transform(at2);
			DianaArea returned = startObjectShape.intersect(endObjectShape);
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("computeCoveringArea(" + order + ") = " + returned);
			}
			return returned;
		}

		DianaArea start_east = connectorNode.getStartNode().getShape().getAllowedHorizontalConnectorLocationFromEast().transform(at1);
		DianaArea start_west = connectorNode.getStartNode().getShape().getAllowedHorizontalConnectorLocationFromWest().transform(at1);
		DianaArea start_north = connectorNode.getStartNode().getShape().getAllowedVerticalConnectorLocationFromNorth().transform(at1);
		DianaArea start_south = connectorNode.getStartNode().getShape().getAllowedVerticalConnectorLocationFromSouth().transform(at1);

		DianaArea end_east = connectorNode.getEndNode().getShape().getAllowedHorizontalConnectorLocationFromEast().transform(at2);
		DianaArea end_west = connectorNode.getEndNode().getShape().getAllowedHorizontalConnectorLocationFromWest().transform(at2);
		DianaArea end_north = connectorNode.getEndNode().getShape().getAllowedVerticalConnectorLocationFromNorth().transform(at2);
		DianaArea end_south = connectorNode.getEndNode().getShape().getAllowedVerticalConnectorLocationFromSouth().transform(at2);

		DianaArea returned = new DianaEmptyArea();

		if (order == 1) {
			returned = DianaUnionArea.makeUnion(start_east.intersect(end_west), start_west.intersect(end_east),
					start_north.intersect(end_south), start_south.intersect(end_north));
		}

		else if (order == 2) {
			returned = DianaUnionArea.makeUnion(start_east.intersect(end_north), start_east.intersect(end_south),
					start_west.intersect(end_north), start_west.intersect(end_south), start_north.intersect(end_east),
					start_north.intersect(end_west), start_south.intersect(end_east), start_south.intersect(end_west));
		}

		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("computeCoveringArea(" + order + ") = " + returned);
		}

		return returned;
	}

	public final void refreshConnector() {
		refreshConnector(false);
	}

	public void refreshConnector(boolean forceRefresh) {
		/*
		 * if (DianaConstants.DEBUG || getGraphicalRepresentation().getDebugCoveringArea()) { computeCoveringAreas(); }
		 */
		storeLayoutOfStartOrEndObject(connectorNode);
	}

	public boolean needsRefresh() {
		return /*getGraphicalRepresentation().isRegistered() &&*/layoutOfStartOrEndObjectHasChanged(connectorNode);
	}

	private void storeLayoutOfStartOrEndObject(ConnectorNode<?> connectorNode) {
		startShape = connectorNode.getStartNode().getShape();
		startShapeDimension = connectorNode.getStartNode().getSize();
		startShapeLocation = connectorNode.getStartNode().getLocationInDrawing();
		endShape = connectorNode.getEndNode().getShape();
		endShapeDimension = connectorNode.getEndNode().getSize();
		endShapeLocation = connectorNode.getEndNode().getLocationInDrawing();
		knownConnectorUsedBounds = getConnectorUsedBounds();
	}

	private boolean layoutOfStartOrEndObjectHasChanged(ConnectorNode<?> connectorNode) {
		// if (true) return true;
		if (startShape == null
				|| startShape != null && startShape.getShapeType() != connectorNode.getStartNode().getShape().getShapeType()) {
			// logger.info("Layout has changed because start shape change");
			return true;
		}
		if (startShapeDimension == null
				|| startShapeDimension != null && !startShapeDimension.equals(connectorNode.getStartNode().getSize())) {
			// logger.info("Layout has changed because start shape dimension change");
			return true;
		}
		if (startShapeLocation == null
				|| startShapeLocation != null && !startShapeLocation.equals(connectorNode.getStartNode().getLocationInDrawing())) {
			// logger.info("Layout has changed because start shape location change");
			return true;
		}
		if (endShape == null || endShape != null && endShape.getShapeType() != connectorNode.getEndNode().getShape().getShapeType()) {
			// logger.info("Layout has changed because end shape change");
			return true;
		}
		if (endShapeDimension == null || endShapeDimension != null && !endShapeDimension.equals(connectorNode.getEndNode().getSize())) {
			// logger.info("Layout has changed because end shape dimension change");
			return true;
		}
		if (endShapeLocation == null
				|| endShapeLocation != null && !endShapeLocation.equals(connectorNode.getEndNode().getLocationInDrawing())) {
			// logger.info("Layout has changed because end shape location change");
			return true;
		}
		if (knownConnectorUsedBounds == null
				|| knownConnectorUsedBounds != null && !knownConnectorUsedBounds.equals(getConnectorUsedBounds())) {
			// logger.info("Layout has changed because knownConnectorUsedBounds change");
			return true;
		}
		return false;
	}

	protected void notifyConnectorModified() {
		connectorNode.notifyConnectorModified();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (isDeleted()) {
			LOGGER.warning("Received PropertyChangeEvent=" + evt + " for a deleted connector !!!");
			return;
		}

		if (temporaryIgnoredObservables.contains(evt.getSource())) {
			// System.out.println("IGORE NOTIFICATION " + notification);
			return;
		}

		// System.out.println("Received " + evt.getPropertyName() + " old=" + evt.getOldValue() + " new=" + evt.getNewValue());

		if (evt.getSource() == getConnectorSpecification()) { // TODO: test structural modifications to optimize this
			// System.out.println("notifyConnectorModified()");
			notifyConnectorModified();
		}

	}

	/**
	 * Returns the property value for supplied parameter<br>
	 * If many Connectors share same ConnectorSpecification (as indicated by {@link Drawing#getPersistenceMode()), do not store value in
	 * ConnectorSpecification, but store it in the Connector itself.<br> This implies that this value is not persistent (not serializable)
	 * 
	 * @param parameter
	 * @return
	 */
	@Override
	public <T> T getPropertyValue(GRProperty<T> parameter) {

		// Now we have to think of this:
		// New architecture of Diana now authorizes a ConnectorSpecification to be shared by many Connectors
		// If UniqueGraphicalRepresentations is active, use ConnectorSpecification to store graphical properties

		if (getConnectorNode() == null) {
			LOGGER.warning("Called getPropertyValue() for null ConnectorNode");
			return null;
		}
		else if (getConnectorNode().isDeleted()) {
			LOGGER.warning("Called getPropertyValue() for deleted ConnectorNode");
			return null;
		}

		if (getConnectorNode().getDrawing().getPersistenceMode() == PersistenceMode.UniqueGraphicalRepresentations) {
			if (getConnectorSpecification() == null) {
				return null;
			}
			return (T) getConnectorSpecification().objectForKey(parameter.getName());
		}

		// If SharedGraphicalRepresentations is active, GR should not be used to store graphical properties

		else if (getConnectorNode().getDrawing().getPersistenceMode() == PersistenceMode.SharedGraphicalRepresentations) {

			T returned = (T) propertyValues.get(parameter);
			if (returned == null) {
				// Init default value with GR
				returned = (T) getConnectorSpecification().objectForKey(parameter.getName());
				if (returned != null) {
					propertyValues.put(parameter, returned);
				}
			}

			return returned;
		}

		else {
			LOGGER.warning("Not implemented: " + getConnectorNode().getDrawing().getPersistenceMode());
			return null;
		}
	}

	/**
	 * Sets the property value for supplied parameter<br>
	 * If many Connectors share same ConnectorSpecification (as indicated by {@link Drawing#getPersistenceMode()), do not store value in
	 * ConnectorSpecification, but store it in the Connector itself.<br> This implies that this value is not persistent (not serializable)
	 * 
	 * @param parameter
	 * @return
	 */
	@Override
	public <T> void setPropertyValue(GRProperty<T> parameter, T value) {

		// Now we have to think of this:
		// New architecture of Diana now authorizes a ConnectorSpecification to be shared by many Connectors
		// If UniqueGraphicalRepresentations is active, use ConnectorSpecification to store graphical properties

		if (getConnectorNode().getDrawing().getPersistenceMode() == PersistenceMode.UniqueGraphicalRepresentations) {
			boolean wasObserving = ignoreNotificationsFrom(getConnectorSpecification());
			// This line is really important, since it avoid GR to be notified of this set
			// Otherwise GR forward notification to DTN whiich will delete current connector
			getConnectorSpecification().getPropertyChangeSupport().removePropertyChangeListener(connectorNode.getGraphicalRepresentation());
			// Unused T oldValue = (T) getConnectorSpecification().objectForKey(parameter.getName());
			getConnectorSpecification().setObjectForKey(value, parameter.getName());
			if (wasObserving) {
				observeAgain(getConnectorSpecification());
			}
			// At the end, let the GR observes again the CS
			getConnectorSpecification().getPropertyChangeSupport().addPropertyChangeListener(connectorNode.getGraphicalRepresentation());
			// Since CS is prevented to fire notifications, do it myself
			// getPropertyChangeSupport().firePropertyChange(parameter.getName(), oldValue, value);
		}

		// If SharedGraphicalRepresentations is active, GR should not be used to store graphical properties

		else if (getConnectorNode().getDrawing().getPersistenceMode() == PersistenceMode.SharedGraphicalRepresentations) {
			propertyValues.put(parameter, value);
		}

		else {
			LOGGER.warning("Not implemented: " + getConnectorNode().getDrawing().getPersistenceMode());
		}

	}

	protected Set<HasPropertyChangeSupport> temporaryIgnoredObservables = new HashSet<>();

	/**
	 * 
	 * @param observable
	 * @return a flag indicating if observable was added to the list of ignored observables
	 */
	protected boolean ignoreNotificationsFrom(HasPropertyChangeSupport observable) {
		if (temporaryIgnoredObservables.contains(observable)) {
			return false;
		}
		temporaryIgnoredObservables.add(observable);
		return true;
	}

	protected void observeAgain(HasPropertyChangeSupport observable) {
		temporaryIgnoredObservables.remove(observable);
	}

	@Override
	public StartSymbolType getStartSymbol() {
		return getPropertyValue(ConnectorSpecification.START_SYMBOL);
	}

	public void setStartSymbol(StartSymbolType startSymbol) {
		setPropertyValue(ConnectorSpecification.START_SYMBOL, startSymbol);
	}

	@Override
	public EndSymbolType getEndSymbol() {
		return getPropertyValue(ConnectorSpecification.END_SYMBOL);
	}

	public void setEndSymbol(EndSymbolType endSymbol) {
		setPropertyValue(ConnectorSpecification.END_SYMBOL, endSymbol);
	}

	@Override
	public MiddleSymbolType getMiddleSymbol() {
		return getPropertyValue(ConnectorSpecification.MIDDLE_SYMBOL);
	}

	public void setMiddleSymbol(MiddleSymbolType middleSymbol) {
		setPropertyValue(ConnectorSpecification.MIDDLE_SYMBOL, middleSymbol);
	}

	@Override
	public double getStartSymbolSize() {
		return getPropertyValue(ConnectorSpecification.START_SYMBOL_SIZE);
	}

	public void setStartSymbolSize(double startSymbolSize) {
		setPropertyValue(ConnectorSpecification.START_SYMBOL_SIZE, startSymbolSize);
	}

	@Override
	public double getEndSymbolSize() {
		return getPropertyValue(ConnectorSpecification.END_SYMBOL_SIZE);
	}

	public void setEndSymbolSize(double endSymbolSize) {
		setPropertyValue(ConnectorSpecification.END_SYMBOL_SIZE, endSymbolSize);
	}

	@Override
	public double getMiddleSymbolSize() {
		return getPropertyValue(ConnectorSpecification.MIDDLE_SYMBOL_SIZE);
	}

	public void setMiddleSymbolSize(double middleSymbolSize) {
		setPropertyValue(ConnectorSpecification.MIDDLE_SYMBOL_SIZE, middleSymbolSize);
	}

	@Override
	public double getRelativeMiddleSymbolLocation() {
		return getPropertyValue(ConnectorSpecification.RELATIVE_MIDDLE_SYMBOL_LOCATION);
	}

	public void setRelativeMiddleSymbolLocation(double relativeMiddleSymbolLocation) {
		setPropertyValue(ConnectorSpecification.RELATIVE_MIDDLE_SYMBOL_LOCATION, relativeMiddleSymbolLocation);
	}

	public double getRelativeLabelLocation() {
		return getPropertyValue(ConnectorSpecification.RELATIVE_LABEL_LOCATION);
	}

	public void setRelativeLabelLocation(double relativeMiddleSymbolLocation) {
		setPropertyValue(ConnectorSpecification.RELATIVE_LABEL_LOCATION, relativeMiddleSymbolLocation);
	}

	@Override
	public boolean getIsStartingLocationFixed() {
		return getPropertyValue(ConnectorSpecification.IS_STARTING_LOCATION_FIXED);
	}

	public void setIsStartingLocationFixed(boolean aFlag) {
		setPropertyValue(ConnectorSpecification.IS_STARTING_LOCATION_FIXED, aFlag);
	}

	@Override
	public boolean getIsStartingLocationDraggable() {
		return getPropertyValue(ConnectorSpecification.IS_STARTING_LOCATION_DRAGGABLE);
	}

	public void setIsStartingLocationDraggable(boolean aFlag) {
		setPropertyValue(ConnectorSpecification.IS_STARTING_LOCATION_DRAGGABLE, aFlag);
	}

	@Override
	public boolean getIsEndingLocationFixed() {
		return getPropertyValue(ConnectorSpecification.IS_ENDING_LOCATION_FIXED);
	}

	public void setIsEndingLocationFixed(boolean aFlag) {
		setPropertyValue(ConnectorSpecification.IS_ENDING_LOCATION_FIXED, aFlag);
	}

	@Override
	public boolean getIsEndingLocationDraggable() {
		return getPropertyValue(ConnectorSpecification.IS_ENDING_LOCATION_DRAGGABLE);
	}

	public void setIsEndingLocationDraggable(boolean aFlag) {
		setPropertyValue(ConnectorSpecification.IS_ENDING_LOCATION_DRAGGABLE, aFlag);
	}

	/**
	 * Return start location asserting start location is fixed. Return position relative to start object (in the start-object coordinates
	 * system)
	 * 
	 * @return
	 */
	public DianaPoint getFixedStartLocation() {
		return getPropertyValue(ConnectorSpecification.FIXED_START_LOCATION);
	}

	/**
	 * Sets start location asserting start location is fixed. Sets position relative to start object (in the start-object coordinates
	 * system)
	 * 
	 * @param aPoint
	 *            : relative to start object
	 */
	public void setFixedStartLocation(DianaPoint aPoint) {
		DianaShape<?> startArea = getStartNode().getShape().getOutline();
		aPoint = startArea.getNearestPoint(aPoint);
		setPropertyValue(ConnectorSpecification.FIXED_START_LOCATION, aPoint);
	}

	/**
	 * Return end location asserting end location is fixed. Return position relative to end object (in the end-object coordinates system)
	 * 
	 * @return
	 */
	public DianaPoint getFixedEndLocation() {
		return getPropertyValue(ConnectorSpecification.FIXED_END_LOCATION);
	}

	/**
	 * Sets end location asserting end location is fixed. Sets position relative to end object (in the end-object coordinates system)
	 * 
	 * @param aPoint
	 *            , relative to end object
	 */
	public void setFixedEndLocation(DianaPoint aPoint) {
		DianaShape<?> endArea = getEndNode().getShape().getOutline();
		aPoint = endArea.getNearestPoint(aPoint);
		setPropertyValue(ConnectorSpecification.FIXED_END_LOCATION, aPoint);
	}

	public DianaPoint getCp1RelativeToStartObject() {
		return getPropertyValue(ConnectorSpecification.FIXED_START_LOCATION);
	}

	public void setCp1RelativeToStartObject(DianaPoint aPoint) {
		setPropertyValue(ConnectorSpecification.FIXED_START_LOCATION, aPoint);
	}

	public DianaPoint getCp2RelativeToEndObject() {
		return getPropertyValue(ConnectorSpecification.FIXED_END_LOCATION);
	}

	public void setCp2RelativeToEndObject(DianaPoint aPoint) {
		setPropertyValue(ConnectorSpecification.FIXED_END_LOCATION, aPoint);
	}

}
