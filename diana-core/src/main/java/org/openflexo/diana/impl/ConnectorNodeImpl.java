/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.diana.impl;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.diana.ConnectorGraphicalRepresentation;
import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.GRBinding;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.connectors.Connector;
import org.openflexo.diana.connectors.ConnectorSpecification;
import org.openflexo.diana.connectors.impl.ConnectorImpl;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.GeomUtils;
import org.openflexo.diana.graphics.DianaConnectorGraphics;
import org.openflexo.diana.notifications.ConnectorModified;
import org.openflexo.diana.notifications.ObjectHasMoved;
import org.openflexo.diana.notifications.ObjectHasResized;
import org.openflexo.diana.notifications.ObjectMove;
import org.openflexo.diana.notifications.ObjectResized;
import org.openflexo.diana.notifications.ObjectWillMove;
import org.openflexo.diana.notifications.ObjectWillResize;
import org.openflexo.diana.notifications.ShapeChanged;
import org.openflexo.toolbox.ConcatenedList;

public class ConnectorNodeImpl<O> extends DrawingTreeNodeImpl<O, ConnectorGraphicalRepresentation> implements ConnectorNode<O> {

	private static final Logger logger = Logger.getLogger(ConnectorNodeImpl.class.getPackage().getName());

	private ShapeNodeImpl<?> startNode;
	private ShapeNodeImpl<?> endNode;

	private Connector<?> connector;

	public ConnectorNodeImpl(DrawingImpl<?> drawingImpl, O drawable, GRBinding<O, ConnectorGraphicalRepresentation> grBinding,
			ContainerNodeImpl<?, ?> parentNode) {
		super(drawingImpl, drawable, grBinding, parentNode);
		startDrawableObserving();
	}

	public ConnectorNodeImpl(DrawingImpl<?> drawingImpl, O drawable, GRBinding<O, ConnectorGraphicalRepresentation> grBinding,
			ContainerNodeImpl<?, ?> parentNode, ShapeNodeImpl<?> startNode, ShapeNodeImpl<?> endNode) {
		this(drawingImpl, drawable, grBinding, parentNode);
		setStartNode(startNode);
		setEndNode(endNode);
	}

	@Override
	public ShapeNodeImpl<?> getStartNode() {
		return startNode;
	}

	public void setStartNode(ShapeNodeImpl<?> startNode) {
		if (this.startNode != startNode) {
			disableStartObjectObserving();
			this.startNode = startNode;
			enableStartObjectObserving(startNode);
			refreshConnector();
		}
	}

	@Override
	public ShapeNodeImpl<?> getEndNode() {
		return endNode;
	}

	public void setEndNode(ShapeNodeImpl<?> endNode) {
		if (this.endNode != endNode) {
			disableEndObjectObserving();
			this.endNode = endNode;
			enableEndObjectObserving(endNode);
			refreshConnector();
		}
	}

	@Override
	public Connector<?> getConnector() {
		if (connector == null && getConnectorSpecification() != null) {
			// logger.info("Make new connector with " + getConnectorSpecification());
			connector = getConnectorSpecification().makeConnector(this);
			getConnectorSpecification().getPropertyChangeSupport().addPropertyChangeListener(connector);
		}
		return connector;
	}

	@Override
	public void notifyConnectorModified() {
		/*if (!isRegistered()) {
			return;
		}*/
		checkViewBounds();
		notifyObservers(new ConnectorModified());
	}

	private boolean enabledStartObjectObserving = false;
	private final List<DrawingTreeNode<?, ?>> observedStartObjects = new ArrayList<>();

	private boolean enabledEndObjectObserving = false;
	private final List<DrawingTreeNode<?, ?>> observedEndObjects = new ArrayList<>();

	protected void enableStartObjectObserving(ShapeNode<?> aStartNode) {

		if (aStartNode == null || !aStartNode.isValid()) {
			return;
		}

		if (enabledStartObjectObserving) {
			disableStartObjectObserving();
		}

		if (aStartNode != null /*&& !enabledStartObjectObserving*/) {
			aStartNode.getPropertyChangeSupport().addPropertyChangeListener(this);
			observedStartObjects.add(aStartNode);
			// if (!isDeserializing()) {
			for (DrawingTreeNode<?, ?> node : aStartNode.getAncestors()) {
				/*if (getGraphicalRepresentation(o) != null) {
					getGraphicalRepresentation(o).addObserver(this);
					observedStartObjects.add((Observable) getGraphicalRepresentation(o));
				}*/
				if (node != null) {
					node.getPropertyChangeSupport().addPropertyChangeListener(this);
					observedStartObjects.add(node);
				}
			}
			// }
			enabledStartObjectObserving = true;
		}
	}

	protected void disableStartObjectObserving() {
		if (enabledStartObjectObserving) {

			for (DrawingTreeNode<?, ?> node : observedStartObjects) {
				if (node.getPropertyChangeSupport() != null) {
					node.getPropertyChangeSupport().removePropertyChangeListener(this);
				}
			}

			enabledStartObjectObserving = false;
		}
	}

	protected void enableEndObjectObserving(ShapeNode<?> aEndNode) {

		if (aEndNode == null || !aEndNode.isValid()) {
			return;
		}

		if (enabledEndObjectObserving) {
			disableEndObjectObserving();
		}

		// Redondant check if (aEndNode != null /*&& !enabledEndObjectObserving*/) {
		aEndNode.getPropertyChangeSupport().addPropertyChangeListener(this);
		observedEndObjects.add(aEndNode);
		// if (!isDeserializing()) {
		for (DrawingTreeNode<?, ?> node : aEndNode.getAncestors()) {
			/*if (getGraphicalRepresentation(o) != null) {
				getGraphicalRepresentation(o).addObserver(this);
				observedEndObjects.add((Observable) getGraphicalRepresentation(o));
			}*/
			if (node != null) {
				node.getPropertyChangeSupport().addPropertyChangeListener(this);
				observedEndObjects.add(node);
			}
		}
		// }
		enabledEndObjectObserving = true;
		// }
	}

	protected void disableEndObjectObserving(/*ShapeGraphicalRepresentation anEndObject*/) {
		if (/*anEndObject != null &&*/enabledEndObjectObserving) {
			/*anEndObject.deleteObserver(this);
			if (!isDeserializing()) {
				for (Object o : anEndObject.getAncestors())
					if (getGraphicalRepresentation(o) != null) getGraphicalRepresentation(o).deleteObserver(this);
			}*/
			for (DrawingTreeNode<?, ?> node : observedEndObjects) {
				if (node.getPropertyChangeSupport() != null) {
					node.getPropertyChangeSupport().removePropertyChangeListener(this);
				}
			}
			enabledEndObjectObserving = false;
		}
	}

	protected void observeRelevantObjects() {
		enableStartObjectObserving(getStartNode());
		enableEndObjectObserving(getEndNode());
	}

	protected void stopObserveRelevantObjects() {
		if (getStartNode() != null) {
			disableStartObjectObserving();
		}
		if (getEndNode() != null) {
			disableEndObjectObserving();
		}
	}

	@Override
	public int getViewX(double scale) {
		return getViewBounds(scale).x;
	}

	@Override
	public int getViewY(double scale) {
		return getViewBounds(scale).y;
	}

	@Override
	public int getViewWidth(double scale) {
		return getViewBounds(scale).width;
	}

	@Override
	public int getViewHeight(double scale) {
		return getViewBounds(scale).height;
	}

	private double minX = 0.0;
	private double minY = 0.0;
	private double maxX = 1.0;
	private double maxY = 1.0;

	private void checkViewBounds() {
		DianaRectangle r = getConnector().getConnectorUsedBounds();
		if (GeomUtils.checkDoubleIsAValue(r.getMinX()) && GeomUtils.checkDoubleIsAValue(r.getMinY())
				&& GeomUtils.checkDoubleIsAValue(r.getMaxX()) && GeomUtils.checkDoubleIsAValue(r.getMaxY())) {
			minX = Math.min(r.getMinX(), 0.0);
			minY = Math.min(r.getMinY(), 0.0);
			maxX = Math.max(r.getMaxX(), 1.0);
			maxY = Math.max(r.getMaxY(), 1.0);
		}
	}

	@Override
	public Rectangle getViewBounds(double scale) {
		// return getNormalizedBounds(scale);

		Rectangle bounds = getNormalizedBounds(scale);
		/*System.out.println("Bounds="+bounds);
		System.out.println("minX="+minX);
		System.out.println("minY="+minY);
		System.out.println("maxX="+maxX);
		System.out.println("maxY="+maxY);*/
		Rectangle returned = new Rectangle();
		returned.x = (int) (bounds.x + (minX < 0 ? minX * bounds.width : 0));
		returned.y = (int) (bounds.y + (minY < 0 ? minY * bounds.height : 0));
		returned.width = (int) ((maxX - minX) * bounds.width);
		returned.height = (int) ((maxY - minY) * bounds.height);
		return returned;
	}

	@Override
	public int getExtendedX(double scale) {
		Rectangle bounds = getNormalizedBounds(scale);
		return (int) (bounds.x + (minX < 0 ? minX * bounds.width : 0));
	}

	@Override
	public int getExtendedY(double scale) {
		Rectangle bounds = getNormalizedBounds(scale);
		return (int) (bounds.y + (minY < 0 ? minY * bounds.height : 0));
	}

	/**
	 * Return normalized bounds Those bounds corresponds to the normalized area defined as (0.0,0.0)-(1.0,1.0) enclosing EXACTELY the two
	 * related shape bounds. Those bounds should eventually be extended to contain connector contained outside this area.
	 */
	@Override
	public Rectangle getNormalizedBounds(double scale) {
		if (getStartNode() == null || getStartNode().isDeleted() || getEndNode() == null || getEndNode().isDeleted()) {
			logger.warning("Could not obtain connector bounds: start or end object is null or deleted");
			logger.warning("Object: " + this + " startObject=" + getStartNode() + " endObject=" + getEndNode());
			// Here, we return a (1,1)-size to avoid obtaining Infinity AffinTransform !!!
			return new Rectangle(0, 0, 1, 1);
		}

		if (getParentNode() == null) {
			logger.warning("getNormalizedBounds() called for GR " + this + " with container=null, valid=" + isValid());
		}

		Rectangle startBounds = getStartNode().getViewBounds(getParentNode(), scale);
		Rectangle endsBounds = getEndNode().getViewBounds(getParentNode(), scale);

		Rectangle bounds = new Rectangle();
		Rectangle2D.union(startBounds, endsBounds, bounds);

		return bounds;
	}

	@Override
	public boolean isContainedInSelection(Rectangle drawingViewSelection, double scale) {
		DianaRectangle drawingViewBounds = new DianaRectangle(drawingViewSelection.getX(), drawingViewSelection.getY(),
				drawingViewSelection.getWidth(), drawingViewSelection.getHeight(), Filling.FILLED);
		boolean isFullyContained = true;
		for (ControlArea<?> ca : getControlAreas()) {
			if (ca instanceof ControlPoint) {
				ControlPoint cp = (ControlPoint) ca;
				Point cpInContainerView = convertLocalNormalizedPointToRemoteViewCoordinates(cp.getPoint(), getDrawing().getRoot(), scale);
				DianaPoint preciseCPInContainerView = new DianaPoint(cpInContainerView.x, cpInContainerView.y);
				if (!drawingViewBounds.containsPoint(preciseCPInContainerView)) {
					// System.out.println("Going outside: point="+preciseCPInContainerView+" bounds="+containerViewBounds);
					isFullyContained = false;
				}
			}
		}
		return isFullyContained;
	}

	@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		Rectangle bounds = getNormalizedBounds(scale);

		// return AffineTransform.getScaleInstance(bounds.width, bounds.height);

		AffineTransform returned = AffineTransform.getTranslateInstance(minX < 0 ? -minX * bounds.width : 0,
				minY < 0 ? -minY * bounds.height : 0);

		returned.concatenate(AffineTransform.getScaleInstance(bounds.width, bounds.height));

		return returned;

	}

	@Override
	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale) {
		Rectangle bounds = getNormalizedBounds(scale);

		// return AffineTransform.getScaleInstance(1.0/bounds.width, 1.0/bounds.height);

		AffineTransform returned = AffineTransform.getTranslateInstance(minX < 0 ? minX * bounds.width : 0,
				minY < 0 ? minY * bounds.height : 0);

		returned.preConcatenate(AffineTransform.getScaleInstance(1.0 / bounds.width, 1.0 / bounds.height));

		return returned;

	}

	@Override
	public Point getLabelLocation(double scale) {
		if (getConnector() != null) {
			Point labelLocation = convertNormalizedPointToViewCoordinates(getConnector().getLabelLocation(), scale);
			Point point = new Point((int) (labelLocation.x + getGraphicalRepresentation().getAbsoluteTextX() * scale + getViewX(scale)),
					(int) (labelLocation.y + getGraphicalRepresentation().getAbsoluteTextY() * scale + getViewY(scale)));

			Dimension d = getLabelDimension(scale);
			if (getGraphicalRepresentation().getHorizontalTextAlignment() != null) {
				switch (getGraphicalRepresentation().getHorizontalTextAlignment()) {
					case CENTER:
						point.x -= d.width / 2;
						break;
					case LEFT:
						break;
					case RIGHT:
						point.x -= d.width;
						break;

				}
			}
			if (getGraphicalRepresentation().getVerticalTextAlignment() != null) {
				switch (getGraphicalRepresentation().getVerticalTextAlignment()) {
					case TOP:
						point.y -= d.height;
						break;
					case MIDDLE:
						point.y -= d.height / 2;
						break;
					case BOTTOM:
						break;
				}
			}
			return point;
		}
		return null;
	}

	@Override
	public void setLabelLocation(Point point, double scale) {
		Point connectorCenter = convertNormalizedPointToViewCoordinates(getConnector().getMiddleSymbolLocation(), scale);

		Double oldAbsoluteTextX = getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X);
		Double oldAbsoluteTextY = getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y);
		Dimension d = getLabelDimension(scale);
		switch (getGraphicalRepresentation().getHorizontalTextAlignment()) {
			case CENTER:
				point.x += d.width / 2;
				break;
			case LEFT:
				break;
			case RIGHT:
				point.x += d.width;
				break;

		}
		switch (getGraphicalRepresentation().getVerticalTextAlignment()) {
			case BOTTOM:
				point.y += d.height;
				break;
			case MIDDLE:
				point.y += d.height / 2;
				break;
			case TOP:
				break;
		}

		DianaPoint p = new DianaPoint((point.x - connectorCenter.x - getViewX(scale)) / scale,
				(point.y - connectorCenter.y - getViewY(scale)) / scale);
		setPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X, p.x);
		setPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y, p.y);
		notifyAttributeChanged(GraphicalRepresentation.ABSOLUTE_TEXT_X, oldAbsoluteTextX,
				getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X));
		notifyAttributeChanged(GraphicalRepresentation.ABSOLUTE_TEXT_Y, oldAbsoluteTextY,
				getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (temporaryIgnoredObservables.contains(evt.getSource())) {
			// System.out.println("IGORE NOTIFICATION " + notification);
			return;
		}

		if (getConnector() == null) {
			return;
		}

		super.propertyChange(evt);

		if (evt.getPropertyName().equals(ObjectWillMove.EVENT_NAME) || evt.getPropertyName().equals(ObjectWillResize.EVENT_NAME)) {
			((ConnectorImpl<?>) getConnector()).connectorWillBeModified();
			// Propagate notification to views
			forward(evt);
		}
		if (evt.getPropertyName().equals(ObjectHasMoved.EVENT_NAME) || evt.getPropertyName().equals(ObjectHasResized.EVENT_NAME)) {
			((ConnectorImpl<?>) getConnector()).connectorHasBeenModified();
			// Propagate notification to views
			forward(evt);
		}
		if (evt.getPropertyName().equals(ObjectMove.PROPERTY_NAME) || evt.getPropertyName().equals(ObjectResized.PROPERTY_NAME)
				|| evt.getPropertyName().equals(ShapeChanged.EVENT_NAME)) {
			// if (observable == startObject || observable == endObject) {
			// !!! or any of ancestors
			refreshConnector();
			// }
		}

		if (evt.getPropertyName().equals(ConnectorGraphicalRepresentation.CONNECTOR.getName())
				|| evt.getPropertyName().equals(ConnectorGraphicalRepresentation.CONNECTOR_TYPE.getName())) {
			// Connector Specification has changed
			fireConnectorSpecificationChanged();
		}

		else if (evt.getPropertyName() == GraphicalRepresentation.HORIZONTAL_TEXT_ALIGNEMENT.getName()
				|| evt.getPropertyName() == GraphicalRepresentation.VERTICAL_TEXT_ALIGNEMENT.getName()) {
			// System.out.println("Hop, on change l'alignement du texte du connecteur");
		}

		/*if (notification instanceof ConnectorModified) {
			updateControlAreas();
		}*/

		if (evt.getSource() instanceof ForegroundStyle) {
			notifyAttributeChanged(ConnectorGraphicalRepresentation.FOREGROUND, null, getGraphicalRepresentation().getForeground());
		}

	}

	private void fireConnectorSpecificationChanged() {
		// logger.info("fireConnectorSpecificationChanged()");
		if (connector != null && getConnectorSpecification() != null) {
			getConnectorSpecification().getPropertyChangeSupport().removePropertyChangeListener(connector);
			connector.delete();
			connector = null;
		}
		// We clear and re-compute control areas
		clearControlAreas();
		getControlAreas();
		refreshConnector(true);
	}

	/**
	 * Return boolean indicating if this {@link DrawingTreeNode} is valid.<br>
	 * 
	 * A {@link DrawingTreeNode} is valid when it is correctely embedded inside {@link Drawing} tree (which means that parent and child are
	 * set and correct, and that start and end shapes are set for connectors)
	 * 
	 * @return
	 */
	@Override
	public boolean isValid() {

		if (!super.isValid()) {
			return false;
		}
		// System.out.println("startNode=" + getStartNode() + " deleted=" + getStartNode().isDeleted());
		// System.out.println("endNode=" + getEndNode() + " deleted=" + getEndNode().isDeleted());
		// System.out.println("connected=" + DianaUtils.areElementsConnectedInGraphicalHierarchy(getStartNode(), getEndNode()));
		return getStartNode() != null && getEndNode() != null && !getStartNode().isDeleted() && !getEndNode().isDeleted()
				&& DianaUtils.areElementsConnectedInGraphicalHierarchy(getStartNode(), getEndNode());
	}

	@Override
	public void refreshConnector() {
		refreshConnector(false);
	}

	protected void refreshConnector(boolean forceRefresh) {
		if (!isValid()) {
			// Dont' go further for connector that are inconsistent (this may happen
			// during big model restructurations (for example during a multiple delete)
			return;
		}
		try {
			if (forceRefresh || (getConnector() != null && ((ConnectorImpl<?>) getConnector()).needsRefresh())) {
				((ConnectorImpl<?>) getConnector()).refreshConnector(forceRefresh);
				checkViewBounds();
				notifyConnectorModified();
			}
		} catch (Exception e) {
			logger.warning("Unexpected exception: " + e);
			e.printStackTrace();
		}
	}

	private List<? extends ControlArea<?>> controlAreas = null;

	@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		if (controlAreas == null && getConnector() != null) {
			List<? extends ControlArea<?>> customControlAreas = getGRBinding().makeControlAreasFor(this);
			if (customControlAreas == null) {
				controlAreas = getConnector().getControlAreas();
			}
			else {
				ConcatenedList<ControlArea<?>> concatenedControlAreas = new ConcatenedList<>();
				concatenedControlAreas.addElementList(getConnector().getControlAreas());
				concatenedControlAreas.addElementList(customControlAreas);
				controlAreas = concatenedControlAreas;
			}
		}
		return controlAreas;
		// return getConnector().getControlAreas();
	}

	@Override
	public void clearControlAreas() {
		controlAreas = null;
	}

	@Override
	public boolean delete() {
		if (!isDeleted()) {
			// System.out.println("ConnectorNode deleted");
			if (connector != null) {
				connector.delete();
			}
			connector = null;
			stopDrawableObserving();
			disableStartObjectObserving();
			disableEndObjectObserving();
			super.delete();
			finalizeDeletion();
			return true;
		}
		return false;
	}

	/**
	 * Return distance from point to connector representation with a given scale
	 * 
	 * @param aPoint
	 *            expressed in local normalized coordinates system
	 * @param scale
	 * @return
	 */
	@Override
	public double distanceToConnector(DianaPoint aPoint, double scale) {
		if (connector == null) {
			return Double.MAX_VALUE;
		}
		return connector.distanceToConnector(aPoint, scale);
	}

	@Override
	public String toString() {
		return "ConnectorImpl-" + getIndex() + (getStartNode() != null ? "[Shape-" + getStartNode().getIndex() + "]" : "[???]")
				+ (getEndNode() != null ? "[Shape-" + getEndNode().getIndex() + "]" : "[???]") + ":" + getDrawable();
	}

	@Override
	public boolean hasContainedLabel() {
		return false;
	}

	@Override
	public boolean hasFloatingLabel() {
		return hasText();
	}

	@Override
	public DianaDimension getRequiredLabelSize() {
		return null;
	}

	@Override
	public void paint(DianaConnectorGraphics g) {

		if (isDeleted()) {
			logger.warning("paint connector called for a deleted ConnectorNode");
			Thread.dumpStack();
			return;
		}

		if (getStartNode() == null || getStartNode().isDeleted()) {
			logger.warning("Could not paint connector: start object is null or deleted");
			return;
		}

		if (getEndNode() == null || getEndNode().isDeleted()) {
			logger.warning("Could not paint connector: end object is null or deleted");
			return;
		}

		/*if (DianaConstants.DEBUG) {
			Graphics2D g2 = graphics.getGraphics();
			g2.setColor(Color.PINK);
			g2.drawRect(0, 0, getNode().getViewWidth(controller.getScale()) - 1, getNode().getViewHeight(controller.getScale()) - 1);
		}*/

		if (getConnectorSpecification() != null) {

			if (!isValid()) {
				logger.warning("paint connector requested for not validated connector graphical representation: " + this);
				return;
			}
			if (getStartNode() == null || getStartNode().isDeleted() || !getStartNode().isValid()) {
				logger.warning("paint connector requested for invalid start object (either null, deleted or not validated) : " + this
						+ " start=" + getStartNode());
				return;
			}
			if (getEndNode() == null || getEndNode().isDeleted() || !getEndNode().isValid()) {
				logger.warning("paint connector requested for invalid start object (either null, deleted or not validated) : " + this
						+ " end=" + getEndNode());
				return;
			}

			if (getConnector() == null) {
				logger.warning("null connector for connector specification " + getConnectorSpecification());
			}

			getConnector().paintConnector(g);
		}

	}

	@Override
	public ForegroundStyle getForegroundStyle() {
		return getPropertyValue(ConnectorGraphicalRepresentation.FOREGROUND);
	}

	@Override
	public void setForegroundStyle(ForegroundStyle aValue) {
		setPropertyValue(ConnectorGraphicalRepresentation.FOREGROUND, aValue);
	}

	/**
	 * Convenient method used to retrieve shape specification property value
	 */
	@Override
	public ConnectorSpecification getConnectorSpecification() {
		return getPropertyValue(ConnectorGraphicalRepresentation.CONNECTOR);
	}

	/**
	 * Convenient method used to set shape specification property value
	 */
	@Override
	public void setConnectorSpecification(ConnectorSpecification connectorSpecification) {
		if (connectorSpecification != getConnectorSpecification()) {
			setPropertyValue(ConnectorGraphicalRepresentation.CONNECTOR, connectorSpecification);
			fireConnectorSpecificationChanged();
		}
	}

}
