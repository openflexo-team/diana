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

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.openflexo.diana.GRBinding.ConnectorGRBinding;
import org.openflexo.diana.GRBinding.ContainerGRBinding;
import org.openflexo.diana.GRBinding.DrawingGRBinding;
import org.openflexo.diana.GRBinding.GeometricGRBinding;
import org.openflexo.diana.GRBinding.GraphGRBinding;
import org.openflexo.diana.GRBinding.ShapeGRBinding;
import org.openflexo.diana.GRProvider.ConnectorGRProvider;
import org.openflexo.diana.GRProvider.DrawingGRProvider;
import org.openflexo.diana.GRProvider.GeometricGRProvider;
import org.openflexo.diana.GRProvider.ShapeGRProvider;
import org.openflexo.diana.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.diana.GraphicalRepresentation.LabelMetricsProvider;
import org.openflexo.diana.GraphicalRepresentation.VerticalTextAlignment;
import org.openflexo.diana.animation.Animable;
import org.openflexo.diana.connectors.Connector;
import org.openflexo.diana.connectors.ConnectorSpecification;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.graph.DianaGraph;
import org.openflexo.diana.graphics.DianaConnectorGraphics;
import org.openflexo.diana.graphics.DianaDrawingGraphics;
import org.openflexo.diana.graphics.DianaGeometricGraphics;
import org.openflexo.diana.graphics.DianaGraphics;
import org.openflexo.diana.graphics.DianaShapeGraphics;
import org.openflexo.diana.graphics.ShapeDecorationPainter;
import org.openflexo.diana.graphics.ShapePainter;
import org.openflexo.diana.shapes.Shape;
import org.openflexo.diana.shapes.ShapeSpecification;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.factory.EditingContext;
import org.openflexo.pamela.undo.UndoManager;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * This interface is implemented by all objects representing a graphical drawing<br>
 * 
 * The internal structure of a {@link Drawing} rely on a tree whose nodes are {@link DrawingTreeNode}, which refer to a
 * {@link GraphicalRepresentation} and the represented drawable (an arbitrary java {@link Object}).
 * 
 * {@link DrawingImpl} is the default implementation
 * 
 * @see DrawingTreeNode
 * @see DrawingImpl
 * 
 * @author sylvain
 * 
 * @param <M>
 *            Type of object which is handled as root object: this is the model represented by the drawing
 */
public interface Drawing<M> extends HasPropertyChangeSupport, Animable {

	/**
	 * Encode the way the internal persistance of values for graphical properties is performed.<br>
	 * <ul>
	 * <li><b>SharedGraphicalRepresentation</b> mode indicates that {@link GraphicalRepresentation} instances are subject to be shared, and
	 * that the framework cannot rely on {@link GraphicalRepresentation} unicity, and thus, cannot assert that GraphicalRepresentation might
	 * carry property values. That means that the persistance encoding should be performed by the framework user using dynamic properties
	 * bound to original model</li>
	 * <li><b>UniqueGraphicalRepresentation</b> mode indicates that the framework user guarantees the providing of a unique
	 * {@link GraphicalRepresentation} instance for each {@link DrawingTreeNode}. Thus, the framework will use those
	 * {@link GraphicalRepresentation} to internally store graphical property values. Those {@link GraphicalRepresentation} may then be used
	 * to provide persistance of graphical properties.</li>
	 * 
	 * @author sylvain
	 * 
	 */
	public enum PersistenceMode {
		SharedGraphicalRepresentations, UniqueGraphicalRepresentations
	}

	/**
	 * This interfaces encodes a node in the drawing tree.<br>
	 * A node essentially references {@link GraphicalRepresentation} and the represented drawable (an arbitrary java {@link Object}).<br>
	 * The {@link GraphicalRepresentation} is observed using {@link PropertyChangeSupport} scheme<br>
	 * The drawable object may be observed both ways:
	 * <ul>
	 * <li>(preferably)using {@link PropertyChangeSupport} scheme, if drawable implements {@link HasPropertyChangeSupport} mechanism</li>
	 * <li>(preferably)using classical {@link Observer}/{@link Observable} scheme, if drawable extends {@link Observable}</li>
	 * </ul>
	 * 
	 * Also referenceq the {@link GRBinding} which is the specification of a node in the drawing tree
	 * 
	 * @author sylvain
	 * 
	 * @param <O>
	 *            Type of (model) object represented by this graphical node
	 * @param <GR>
	 *            Type of GraphicalRepresentation represented by this node
	 */
	public interface DrawingTreeNode<O, GR extends GraphicalRepresentation>
			extends PropertyChangeListener, Observer, HasPropertyChangeSupport /*, KeyValueCoding*/ {

		public static final String THIS_KEY = "this";
		public static final String PARENT_KEY = "parent";
		public static final String DRAWABLE_KEY = "drawable";
		public static final String GR_KEY = "gr";

		public static GRProperty<Boolean> IS_FOCUSED = GRProperty.getGRParameter(DrawingTreeNode.class, DrawingTreeNode.IS_FOCUSED_KEY,
				Boolean.class);
		public static GRProperty<Boolean> IS_SELECTED = GRProperty.getGRParameter(DrawingTreeNode.class, DrawingTreeNode.IS_SELECTED_KEY,
				Boolean.class);
		public static GRProperty<Boolean> IS_LONG_TIME_FOCUSED = GRProperty.getGRParameter(DrawingTreeNode.class,
				DrawingTreeNode.IS_LONG_TIME_FOCUSED_KEY, Boolean.class);

		@PropertyIdentifier(type = Boolean.class)
		public static final String IS_SELECTED_KEY = "isSelected";
		@PropertyIdentifier(type = Boolean.class)
		public static final String IS_FOCUSED_KEY = "isFocused";
		@PropertyIdentifier(type = Boolean.class)
		public static final String IS_LONG_TIME_FOCUSED_KEY = "isLongTimeFocused";

		public static final String GRAPHICAL_REPRESENTATION_KEY = "graphicalRepresentation";

		/*public static enum DrawingTreeNodeParameter implements GRProperty {
			isSelected, isFocused;
		}*/

		/**
		 * Return the drawing
		 * 
		 * @return
		 */
		public Drawing<?> getDrawing();

		/**
		 * Return the node specification (the formal binding between a {@link GraphicalRepresentation} and an arbitrary java {@link Object}
		 * of the represented model
		 * 
		 * @return
		 */
		public GRBinding<O, GR> getGRBinding();

		/**
		 * Return the DianaModelFactory (the factory used to build graphical representation objects)
		 * 
		 * @return
		 */
		public DianaModelFactory getFactory();

		/**
		 * Return the EditingContext when any (might be null)
		 * 
		 * @return
		 */
		public EditingContext getEditingContext();

		/**
		 * Return the UndoManager when any (might be null)
		 * 
		 * @return
		 */
		public UndoManager getUndoManager();

		/**
		 * Return the represented java {@link Object} (the object of the model represented by this graphical node)
		 * 
		 * @return
		 */
		public O getDrawable();

		/**
		 * Return the graphical representation which represents the drawable (the object of the model represented by this graphical node)
		 * 
		 * @return
		 */
		public GR getGraphicalRepresentation();

		public void retrieveGraphicalRepresentation();

		/**
		 * Return parent node (a container node which contains this node)
		 * 
		 * @return
		 */
		public ContainerNode<?, ?> getParentNode();

		/**
		 * Return a list of all ancestors of this node
		 * 
		 * @return
		 */
		public List<DrawingTreeNode<?, ?>> getAncestors();

		/**
		 * Return the index of this node relatively to all children declared in parent node
		 * 
		 * @return
		 */
		public int getIndex();

		/**
		 * Return flag indicating if this node should be displayed, relatively to the value returned by visible feature in
		 * {@link GraphicalRepresentation}, and the structure of the tree (the parent should be visible too)
		 */
		public boolean shouldBeDisplayed();

		/**
		 * Return depth of this node in the whole hierarchy
		 * 
		 * @return
		 */
		public int getDepth();

		/**
		 * Return boolean indicating if this {@link DrawingTreeNode} is valid.<br>
		 * 
		 * A {@link DrawingTreeNode} is valid when it is correctely embedded inside {@link Drawing} tree (which means that parent and child
		 * are set and correct, and that start and end shapes are set for connectors)
		 * 
		 * @return
		 */
		public boolean isValid();

		/**
		 * Called this to invalidate this {@link DrawingTreeNode} regarding to the Drawing tree hierarchy
		 */
		public void invalidate();

		/**
		 * Return boolean indicating if this {@link DrawingTreeNode} was invalidated regarding to the Drawing tree hierarchy
		 * 
		 * @return
		 */
		public boolean isInvalidated();

		/**
		 * Return the list of all control areas declared for this graphical node
		 * 
		 * @return
		 */
		public List<? extends ControlArea<?>> getControlAreas();

		/**
		 * Clear control area: allows to recompute control areas
		 */
		public void clearControlAreas();

		/**
		 * Recursively delete this DrawingTreeNode and all its descendants
		 */
		public boolean delete();

		/**
		 * Return a flag indicating if this node has been deleted
		 * 
		 * @return
		 */
		public boolean isDeleted();

		/**
		 * Returns the property value for supplied parameter<br>
		 * If a dynamic property was set, compute and return this value, according to binding declared as dynamic property value<br>
		 * If many {@link DrawingTreeNode} share same {@link GraphicalRepresentation} (as indicated by {@link Drawing#getPersistenceMode()),
		 * do not store value in GraphicalRepresentation, but store it in the {@link DrawingTreeNode} itself.<br>
		 * This implies that this value is not persistent (not serializable) Otherwise, use the {@link GraphicalRepresentation} as a support
		 * for this value.
		 * 
		 * @param parameter
		 *            parameter which is to be set
		 * @return
		 */
		public <T> T getPropertyValue(GRProperty<T> parameter);

		/**
		 * Sets the property value for supplied parameter<br>
		 * If a dynamic property was set, sets this value according to binding declared as dynamic property value<br>
		 * If many {@link DrawingTreeNode} share same {@link GraphicalRepresentation} (as indicated by {@link Drawing#getPersistenceMode()),
		 * do not store value in GraphicalRepresentation, but store it in the {@link DrawingTreeNode} itself.<br>
		 * This implies that this value is not persistent (not serializable) Otherwise, use the {@link GraphicalRepresentation} as a support
		 * for this value.
		 * 
		 * @param parameter
		 *            parameter which is to be set
		 * @param value
		 *            value to be set
		 * @return
		 */
		public <T> void setPropertyValue(GRProperty<T> parameter, T value);

		// public boolean isConnectedToDrawing();

		public boolean isAncestorOf(DrawingTreeNode<?, ?> child);

		public DianaPoint convertRemoteViewCoordinatesToLocalNormalizedPoint(Point p, DrawingTreeNode<?, ?> source, double scale);

		public DianaPoint convertLocalViewCoordinatesToRemoteNormalizedPoint(Point p, DrawingTreeNode<?, ?> destination, double scale);

		public Point convertLocalNormalizedPointToRemoteViewCoordinates(DianaPoint p, DrawingTreeNode<?, ?> destination, double scale);

		public Rectangle convertLocalNormalizedRectangleToRemoteViewCoordinates(DianaRectangle r, DrawingTreeNode<?, ?> destination,
				double scale);

		public Point convertRemoteNormalizedPointToLocalViewCoordinates(DianaPoint p, DrawingTreeNode<?, ?> source, double scale);

		public Point convertNormalizedPointToViewCoordinates(double x, double y, double scale);

		public Rectangle convertNormalizedRectangleToViewCoordinates(DianaRectangle r, double scale);

		public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale);

		public DianaPoint convertViewCoordinatesToNormalizedPoint(int x, int y, double scale);

		public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale);

		public Point convertNormalizedPointToViewCoordinates(DianaPoint p, double scale);

		public DianaPoint convertViewCoordinatesToNormalizedPoint(Point p, double scale);

		public int getViewX(double scale);

		public int getViewY(double scale);

		public int getViewWidth(double scale);

		public int getViewHeight(double scale);

		public Rectangle getViewBounds(double scale);

		public DianaRectangle getNormalizedBounds();

		public LabelMetricsProvider getLabelMetricsProvider();

		public void setLabelMetricsProvider(LabelMetricsProvider labelMetricsProvider);

		/**
		 * Returns the number of pixels available for the label considering its positioning. This method is used in case of line wrapping.
		 * 
		 * @param scale
		 * @return
		 */
		public int getAvailableLabelWidth(double scale);

		public Point getLabelLocation(double scale);

		public Dimension getLabelDimension(double scale);

		public void setLabelLocation(Point point, double scale);

		public Rectangle getLabelBounds(double scale);

		public boolean isContainedInSelection(Rectangle drawingViewSelection, double scale);

		public void notifyLabelWillBeEdited();

		public void notifyLabelHasBeenEdited();

		public void notifyLabelWillMove();

		public void notifyLabelHasMoved();

		public void notifyObjectHierarchyWillBeUpdated();

		public void notifyObjectHierarchyHasBeenUpdated();

		public Boolean getIsFocusable();

		public void setIsFocusable(Boolean focusable);

		public boolean getIsSelected();

		public void setIsSelected(boolean aFlag);

		public boolean getIsFocused();

		public void setIsFocused(boolean aFlag);

		public boolean getIsLongTimeFocused();

		public Boolean getIsVisible();

		public void setIsVisible(Boolean visible);

		public boolean hasText();

		public String getText();

		public void setText(String text);

		public HorizontalTextAlignment getHorizontalTextAlignment();

		public void setHorizontalTextAlignment(HorizontalTextAlignment align);

		public VerticalTextAlignment getVerticalTextAlignment();

		public void setVerticalTextAlignment(VerticalTextAlignment align);

		/**
		 * Return flag indicating is this node has a floating label
		 * 
		 * @return
		 */
		public boolean hasFloatingLabel();

		/**
		 * Return flag indicating is this node should display an inside label
		 * 
		 * @return
		 */
		public boolean hasContainedLabel();

		/**
		 * Returned required dimension for label (null when hasContainedLabel() return false)
		 * 
		 * @return
		 */
		public DianaDimension getRequiredLabelSize();

		public boolean isParentLayoutedAsContainer();

		public TextStyle getTextStyle();

		public void setTextStyle(TextStyle style);

		public boolean getContinuousTextEditing();

		public void setContinuousTextEditing(boolean continuousTextEditing);

	}

	public interface ContainerNode<O, GR extends ContainerGraphicalRepresentation> extends DrawingTreeNode<O, GR> {

		public static final String LAYOUT_DECORATION_KEY = "layoutDecoration";

		/**
		 * Return default DianaLayoutManager which are managed by this {@link ContainerNode} (the first one found)
		 * 
		 * @return
		 */
		public DianaLayoutManager<?, O> getDefaultLayoutManager();

		/**
		 * Return all {@link DianaLayoutManager} which are managed by this {@link ContainerNode} (those are not the {@link LayoutManager}
		 * used to layout this node)
		 * 
		 * @return
		 */
		public List<DianaLayoutManager<?, O>> getLayoutManagers();

		/**
		 * Return {@link DianaLayoutManager} managed by this {@link ContainerNode} and identified by identifier, null when not found
		 * 
		 * @param identifier
		 * @return
		 */
		public DianaLayoutManager<?, O> getLayoutManager(String identifier);

		public List<DianaLayoutManagerSpecification<?>> getLayoutManagerSpecifications();

		public double getWidth();

		public void setWidth(double aValue);

		public double getHeight();

		public void setHeight(double aValue);

		public DianaDimension getSize();

		public void setSize(DianaDimension newSize);

		public List<? extends ShapeNode<?>> getShapeNodes();

		public List<? extends DrawingTreeNode<?, ?>> getChildNodes();

		public void addChild(DrawingTreeNode<?, ?> aChildNode);

		public void removeChild(DrawingTreeNode<?, ?> aChildNode);

		public int getOrder(DrawingTreeNode<?, ?> child1, DrawingTreeNode<?, ?> child2);

		public ShapeNode<?> getTopLevelShapeGraphicalRepresentation(DianaPoint p);

		public void notifyNodeLayoutDecorationChanged(DianaLayoutManager<?, O> layoutManager);

		public void notifyNodeAdded(DrawingTreeNode<?, ?> addedNode);

		public void notifyNodeRemoved(DrawingTreeNode<?, ?> removedNode);

		public <O2> boolean hasShapeFor(ShapeGRBinding<O2> binding, O2 aDrawable);

		public <O2> ShapeNode<O2> getShapeFor(ShapeGRBinding<O2> binding, O2 aDrawable);

		public <O2> boolean hasConnectorFor(ConnectorGRBinding<O2> binding, O2 aDrawable);

		public <O2> ConnectorNode<O2> getConnectorFor(ConnectorGRBinding<O2> binding, O2 aDrawable);

		public <O2> boolean hasGeometricObjectFor(GeometricGRBinding<O2> binding, O2 aDrawable);

		public <O2> GeometricNode<O2> getGeometricObjectFor(GeometricGRBinding<O2> binding, O2 aDrawable);

		public <G extends DianaGraph> boolean hasGraphFor(GraphGRBinding<G> binding, G aDrawable);

		public <G extends DianaGraph> GraphNode<G> getGraphFor(GraphGRBinding<G> binding, G aDrawable);

		/**
		 * Notify that the object just resized
		 */
		public void notifyObjectResized();

		/**
		 * Notify that the object just resized
		 */
		public void notifyObjectResized(DianaDimension oldSize);

		/**
		 * Notify that the object will be resized
		 */
		public void notifyObjectWillResize();

		/**
		 * Notify that the object resizing has finished (take care that this just notify END of resize, this should NOT be used to notify a
		 * resizing: use notifyObjectResize() instead)
		 */
		public void notifyObjectHasResized();

		public boolean isResizing();

		public Dimension getNormalizedLabelSize();

		public Rectangle getNormalizedLabelBounds();

		public DianaRectangle getRequiredBoundsForContents();

		public DianaRectangle getBounds();

		public void paint(DianaGraphics g);

	}

	public interface RootNode<M> extends ContainerNode<M, DrawingGraphicalRepresentation> {

		/**
		 * Paint this {@link RootNode} using supplied DianaDrawingGraphics
		 * 
		 * @param g
		 */
		public void paint(DianaDrawingGraphics g);

		/**
		 * Convenient method used to retrieve 'drawWorkingArea' property value
		 */
		public boolean getDrawWorkingArea();

		/**
		 * Convenient method used to set 'drawWorkingArea' property value
		 */
		public void setDrawWorkingArea(boolean drawWorkingArea);

	}

	public interface ShapeNode<O> extends ContainerNode<O, ShapeGraphicalRepresentation> {

		public Shape<?> getShape();

		// public double getUnscaledViewWidth();

		// public double getUnscaledViewHeight();

		/**
		 * Return bounds (including border) relative to parent container
		 * 
		 * @return
		 */
		@Override
		public DianaRectangle getBounds();

		/**
		 * Return view bounds (excluding border) relative to parent container
		 * 
		 * @param scale
		 * @return
		 */
		public Rectangle getBounds(double scale);

		/**
		 * Return view bounds (excluding border) relative to given container
		 * 
		 * @param scale
		 * @return
		 */
		public Rectangle getBounds(DrawingTreeNode<?, ?> container, double scale);

		/**
		 * Return logical bounds (including border) relative to given container
		 * 
		 * @param scale
		 * @return
		 */
		public Rectangle getViewBounds(DrawingTreeNode<?, ?> container, double scale);

		public boolean isPointInsideShape(DianaPoint aPoint);

		public DianaShape<?> getDianaShape();

		public DianaShape<?> getDianaShapeOutline();

		public void notifyShapeChanged();

		public void notifyShapeNeedsToBeRedrawn();

		public void notifyObjectMoved();

		public void notifyObjectMoved(DianaPoint oldLocation);

		public void notifyObjectWillMove();

		public void notifyObjectHasMoved();

		public boolean isMoving();

		public void extendParentBoundsToHostThisShape();

		/**
		 * Check and eventually relocate and resize current graphical representation in order to all all contained shape graphical
		 * representations. Contained graphical representations may substantically be relocated.
		 */
		public void extendBoundsToHostContents();

		public double getX();

		public void setX(double aValue);

		public double getY();

		public void setY(double aValue);

		public DianaPoint getLocation();

		public void setLocation(DianaPoint newLocation);

		public DianaPoint getLocationInDrawing();

		/**
		 * Computes and return required border on top, while taking under account:
		 * <ul>
		 * <li>the eventual shadow to paint</li>
		 * <li>the control areas to display</li>
		 * <li>all contained elements which may be located outside of original bounds</li>
		 * </ul>
		 */
		public int getBorderTop();

		/**
		 * Computes and return required border on left, while taking under account:
		 * <ul>
		 * <li>the eventual shadow to paint</li>
		 * <li>the control areas to display</li>
		 * <li>all contained elements which may be located outside of original bounds</li>
		 * </ul>
		 */
		public int getBorderLeft();

		/**
		 * Computes and return required border on bottom, while taking under account:
		 * <ul>
		 * <li>the eventual shadow to paint</li>
		 * <li>the control areas to display</li>
		 * <li>all contained elements which may be located outside of original bounds</li>
		 * </ul>
		 */
		public int getBorderBottom();

		/**
		 * Computes and return required border on right, while taking under account:
		 * <ul>
		 * <li>the eventual shadow to paint</li>
		 * <li>the control areas to display</li>
		 * <li>all contained elements which may be located outside of original bounds</li>
		 * </ul>
		 */
		public int getBorderRight();

		public boolean isFullyContainedInContainer();

		public double getMoveAuthorizedRatio(DianaPoint desiredLocation, DianaPoint initialLocation);

		@Override
		public int getAvailableLabelWidth(double scale);

		public ShapeDecorationPainter getDecorationPainter();

		public void setDecorationPainter(ShapeDecorationPainter aPainter);

		public ShapePainter getShapePainter();

		public void setShapePainter(ShapePainter aPainter);

		public void finalizeConstraints();

		/**
		 * Returns the area on which the given connector can start. The area is expressed in this normalized coordinates
		 * 
		 * @param connectorGR
		 *            the connector asking where to start
		 * @return the area on which the given connector can start
		 */
		public DianaArea getAllowedStartAreaForConnector(ConnectorNode<?> connector);

		/**
		 * Returns the area on which the given connector can end. The area is expressed in this normalized coordinates
		 * 
		 * @param connectorGR
		 *            the connector asking where to end
		 * @return the area on which the given connector can end
		 */
		public DianaArea getAllowedEndAreaForConnector(ConnectorNode<?> connector);

		/**
		 * Returns the area on which the given connector can start. The area is expressed in this normalized coordinates
		 * 
		 * @param connectorGR
		 *            the connector asking where to start
		 * 
		 * @return the area on which the given connector can start
		 */
		public DianaArea getAllowedStartAreaForConnectorForDirection(ConnectorNode<?> connector, DianaArea area,
				SimplifiedCardinalDirection direction);

		/**
		 * Returns the area on which the given connector can end. The area is expressed in this normalized coordinates
		 * 
		 * @param connectorGR
		 *            the connector asking where to end
		 * @return the area on which the given connector can end
		 */
		public DianaArea getAllowedEndAreaForConnectorForDirection(ConnectorNode<?> connector, DianaArea area,
				SimplifiedCardinalDirection direction);

		/**
		 * Paint this {@link ShapeNode} using supplied DianaShapeGraphics
		 * 
		 * @param g
		 */
		public void paint(DianaShapeGraphics g);

		public ForegroundStyle getForegroundStyle();

		public void setForegroundStyle(ForegroundStyle aValue);

		public boolean getHasSelectedForegroundStyle();

		public ForegroundStyle getSelectedForegroundStyle();

		public void setSelectedForegroundStyle(ForegroundStyle aValue);

		public boolean getHasFocusedForegroundStyle();

		public ForegroundStyle getFocusedForegroundStyle();

		public void setFocusedForegroundStyle(ForegroundStyle aValue);

		public ShadowStyle getShadowStyle();

		public void setShadowStyle(ShadowStyle style);

		public BackgroundStyle getBackgroundStyle();

		public void setBackgroundStyle(BackgroundStyle style);

		public boolean getHasSelectedBackgroundStyle();

		public BackgroundStyle getSelectedBackgroundStyle();

		public void setSelectedBackgroundStyle(BackgroundStyle style);

		public boolean getHasFocusedBackgroundStyle();

		public BackgroundStyle getFocusedBackgroundStyle();

		public void setFocusedBackgroundStyle(BackgroundStyle style);

		public ShapeSpecification getShapeSpecification();

		public void setShapeSpecification(ShapeSpecification shapeSpecification);

		/**
		 * Called to define DianaLayoutManager for this node<br>
		 * The layout manager should be already declared in the parent. It is identified by supplied layoutManagerIdentifier.
		 * 
		 * @param layoutManagerIdentifier
		 */
		// public void layoutedWith(String layoutManagerIdentifier);

		/**
		 * Return the layout manager responsible for the layout of this node (relating to its container)
		 * 
		 * @return
		 */
		public DianaLayoutManager<?, ?> getActiveLayoutManager();

		/**
		 * Sets the layout manager responsible for the layout of this node (relating to its container)
		 * 
		 * @param layoutManager
		 */
		// public void setActiveLayoutManager(DianaLayoutManager<?, ?> layoutManager);

		/**
		 * Invalidate this graphical node regarding to its layout manager<br>
		 * This method might be used to relayout the node
		 */
		public void invalidateLayout();

		/**
		 * Return boolean indicating if this {@link DrawingTreeNode} is validated regarding to its layout manager<br>
		 * 
		 * @return
		 */
		public boolean isLayoutValidated();

		/**
		 * Return flag indicating if we are about to relayout current node<br>
		 * This means that the relocation request was initiated from the layout manager
		 */
		public boolean isRelayouting();

		/**
		 * Sets flag indicating if we are about to relayout current node<br>
		 * This means that the relocation request was initiated from the layout manager
		 */
		public void setRelayouting(boolean relayouting);

		/**
		 * Convenient method used to retrieve 'allowsToLeaveBounds property value
		 */
		public Boolean getAllowsToLeaveBounds();

		/**
		 * Convenient method used to set 'allowsToLeaveBounds' property value
		 */
		public void setAllowsToLeaveBounds(Boolean aValue);

	}

	public interface GraphNode<G extends DianaGraph> extends ShapeNode<G> {

		public void notifyGraphNeedsToBeRedrawn();

	}

	public interface ConnectorNode<O> extends DrawingTreeNode<O, ConnectorGraphicalRepresentation> {

		public ShapeNode<?> getStartNode();

		public ShapeNode<?> getEndNode();

		public Connector<?> getConnector();

		public void notifyConnectorModified();

		public int getExtendedX(double scale);

		public int getExtendedY(double scale);

		/**
		 * Return normalized bounds Those bounds corresponds to the normalized area defined as (0.0,0.0)-(1.0,1.0) enclosing EXACTELY the
		 * two related shape bounds. Those bounds should eventually be extended to contain connector contained outside this area.
		 */
		public Rectangle getNormalizedBounds(double scale);

		// public boolean isConnectorConsistent();

		public void refreshConnector();

		public double distanceToConnector(DianaPoint aPoint, double scale);

		/**
		 * Paint this {@link ConnectorNode} using supplied DianaConnectorGraphics
		 * 
		 * @param g
		 */
		public void paint(DianaConnectorGraphics g);

		public ForegroundStyle getForegroundStyle();

		public void setForegroundStyle(ForegroundStyle aValue);

		public ConnectorSpecification getConnectorSpecification();

		public void setConnectorSpecification(ConnectorSpecification connectorSpecification);
	}

	public interface GeometricNode<O> extends DrawingTreeNode<O, GeometricGraphicalRepresentation> {

		public Rectangle getBounds(double scale);

		public void paintGeometricObject(DianaGeometricGraphics graphics);

		public List<ControlPoint> makeDefaultControlPoints();

		public void notifyGeometryChanged();

		/**
		 * Paint this {@link GeometricNode} using supplied DianaGeometricGraphics
		 * 
		 * @param g
		 */
		public void paint(DianaGeometricGraphics g);

		public ForegroundStyle getForegroundStyle();

		public void setForegroundStyle(ForegroundStyle aValue);

		public BackgroundStyle getBackgroundStyle();

		public void setBackgroundStyle(BackgroundStyle style);

	}

	public PersistenceMode getPersistenceMode();

	public boolean isEditable();

	public void setEditable(boolean editable);

	public M getModel();

	public DianaModelFactory getFactory();

	public RootNode<M> getRoot();

	/**
	 * Delete this {@link Drawing} implementation, by deleting all {@link DrawingTreeNode}
	 */
	public void delete();

	/**
	 * Update the whole tree of graphical object hierarchy<br>
	 * Recursively navigate in the tree to find invalidated nodes. Only invalidated nodes are recomputed (and eventually rebuild if the
	 * graphical object hierarchy structure has changed)
	 * 
	 */
	public void updateGraphicalObjectsHierarchy();

	/**
	 * Update of all DrawingTreeNode representing supplied drawable<br>
	 * Recursively navigate in the tree to find invalidated nodes. Only invalidated nodes are recomputed (and eventually rebuild if the
	 * graphical object hierarchy structure has changed)
	 * 
	 * @param drawable
	 */
	public <O> void updateGraphicalObjectsHierarchy(O drawable);

	/**
	 * Invalidate the whole hierarchy.<br>
	 * All nodes of drawing tree are invalidated, which means that a complete recomputing of the whole tree will be performed during next
	 * updateGraphicalHierarchy() call<br>
	 * Existing graphical representation are kept.
	 */
	public void invalidateGraphicalObjectsHierarchy();

	/**
	 * Invalidate the graphical object hierarchy under all nodes where supplied object is represented.<br>
	 * All nodes of drawing tree under supplied node are invalidated, which means that a recomputing of the whole tree under one (or many)
	 * node (the nodes representing supplied drawable) will be performed during next updateGraphicalObjectHierarchy() call.<br>
	 * 
	 */
	public <O> void invalidateGraphicalObjectsHierarchy(O drawable);

	/**
	 * Return flag indicating if graphical objects hierarchy is beeing running
	 * 
	 * @return
	 */
	public boolean isUpdatingGraphicalObjectsHierarchy();

	/**
	 * Notify that supplied layout manager must be run after end of graphical object hierarchy updating
	 * 
	 * @param layoutManager
	 */
	public void invokeLayoutAfterGraphicalObjectsHierarchyUpdating(DianaLayoutManager<?, ?> layoutManager);

	/**
	 * Return boolean indicating if this {@link Drawing} is about to be deleted
	 * 
	 * @return
	 */
	public boolean isDeleting();

	public DrawingGRBinding<M> bindDrawing(Class<M> drawingClass, String name, DrawingGRProvider<M> grProvider);

	public <R> ShapeGRBinding<R> bindShape(Class<R> shapeObjectClass, String name, ShapeGRProvider<R> grProvider);

	public <R> ShapeGRBinding<R> bindShape(Class<R> shapeObjectClass, String name, ContainerGRBinding<?, ?> parentBinding,
			ShapeGRProvider<R> grProvider);

	public <R> GeometricGRBinding<R> bindGeometric(Class<R> geometricObjectClass, String name, GeometricGRProvider<R> grProvider);

	public <R> GeometricGRBinding<R> bindGeometric(Class<R> geometricObjectClass, String name, ContainerGRBinding<?, ?> parentBinding,
			GeometricGRProvider<R> grProvider);

	public <R> ConnectorGRBinding<R> bindConnector(Class<R> connectorObjectClass, String name, ConnectorGRProvider<R> grProvider);

	public <R> ConnectorGRBinding<R> bindConnector(Class<R> connectorObjectClass, String name, ShapeGRBinding<?> fromBinding,
			ShapeGRBinding<?> toBinding, ConnectorGRProvider<R> grProvider);

	public <R> ConnectorGRBinding<R> bindConnector(Class<R> connectorObjectClass, String name, ShapeGRBinding<?> fromBinding,
			ShapeGRBinding<?> toBinding, ContainerGRBinding<?, ?> parentBinding, ConnectorGRProvider<R> grProvider);

	public <G extends DianaGraph> GraphGRBinding<G> bindGraph(Class<G> graphClass, String name, ShapeGRProvider<G> grProvider);

	public <O> ShapeNode<O> createNewShapeNode(ContainerNode<?, ?> parent, ShapeGRBinding<O> binding, O representable);

	public <O> ConnectorNode<O> createNewConnectorNode(ContainerNode<?, ?> parent, ConnectorGRBinding<O> binding, O representable,
			ShapeNode<?> fromNode, ShapeNode<?> toNode);

	public <O> GeometricNode<O> createNewGeometricNode(ContainerNode<?, ?> parent, GeometricGRBinding<O> binding, O representable);

	public <G extends DianaGraph> GraphNode<G> createNewGraphNode(ContainerNode<?, ?> parent, GraphGRBinding<G> binding, G representable);

	public <O> boolean hasPendingConnector(ConnectorGRBinding<O> binding, O drawable, DrawingTreeNodeIdentifier<?> parentNodeIdentifier,
			DrawingTreeNodeIdentifier<?> startNodeIdentifier, DrawingTreeNodeIdentifier<?> endNodeIdentifier);

	public <O> PendingConnector<O> getPendingConnector(ConnectorGRBinding<O> binding, O drawable,
			DrawingTreeNodeIdentifier<?> parentNodeIdentifier, DrawingTreeNodeIdentifier<?> startNodeIdentifier,
			DrawingTreeNodeIdentifier<?> endNodeIdentifier);

	public <O> PendingConnector<O> createPendingConnector(ConnectorGRBinding<O> binding, O drawable,
			DrawingTreeNodeIdentifier<?> parentNodeIdentifier, DrawingTreeNodeIdentifier<?> startNodeIdentifier,
			DrawingTreeNodeIdentifier<?> endNodeIdentifier);

	// public <O> ShapeNode<O> drawShape(ShapeNode<?> parent, ShapeGRBinding<O> binding, O representable);

	/*public DrawingTreeNode<?> getContainer(DrawingTreeNode<?> node);
	
	public List<DrawingTreeNode<?>> getContainedNodes(DrawingTreeNode<?> parentNode);*/

	/**
	 * Retrieve first drawing tree node matching supplied drawable<br>
	 * Note that GRBinding is not specified here, so if a given drawable is represented through multiple GRBinding, there is no guarantee
	 * that you receive the right object. Use {@link #getDrawingTreeNode(Object, GRBinding)} instead
	 * 
	 * @param aDrawable
	 * @return
	 */
	public <O, GR extends GraphicalRepresentation> DrawingTreeNode<O, GR> getDrawingTreeNode(O aDrawable);

	/**
	 * Retrieve first shape node matching supplied drawable<br>
	 * Note that GRBinding is not specified here, so if a given drawable is represented through multiple GRBinding, there is no guarantee
	 * that you receive the right object. Use {@link #getDrawingTreeNode(Object, GRBinding)} instead
	 * 
	 * @param aDrawable
	 * @return
	 */
	public <O> ShapeNode<O> getShapeNode(O drawable);

	/**
	 * Retrieve first connector node matching supplied drawable<br>
	 * Note that GRBinding is not specified here, so if a given drawable is represented through multiple GRBinding, there is no guarantee
	 * that you receive the right object. Use {@link #getDrawingTreeNode(Object, GRBinding)} instead
	 * 
	 * @param aDrawable
	 * @return
	 */
	public <O> ConnectorNode<O> getConnectorNode(O drawable);

	/**
	 * Retrieve drawing tree node matching supplied drawable and grBinding
	 * 
	 * @param aDrawable
	 * @return
	 */
	public <O, GR extends GraphicalRepresentation> DrawingTreeNode<O, GR> getDrawingTreeNode(O aDrawable, GRBinding<O, GR> grBinding);

	/**
	 * Retrieve shape node matching supplied drawable and grBinding
	 * 
	 * @param aDrawable
	 * @return
	 */
	public <O> ShapeNode<O> getShapeNode(O drawable, ShapeGRBinding<O> binding);

	/**
	 * Retrieve connector node matching supplied drawable and grBinding
	 * 
	 * @param aDrawable
	 * @return
	 */
	public <O> ConnectorNode<O> getConnectorNode(O drawable, ConnectorGRBinding<O> binding);

	/**
	 * Retrieve drawing tree node matching supplied identifier
	 * 
	 * @param identifier
	 * @return
	 */
	public <O> DrawingTreeNode<O, ?> getDrawingTreeNode(DrawingTreeNodeIdentifier<O> identifier);

	/**
	 * Retrieve shape node matching supplied identifier
	 * 
	 * @param aDrawable
	 * @return
	 */
	public <O> ShapeNode<O> getShapeNode(DrawingTreeNodeIdentifier<O> identifier);

	/**
	 * Retrieve connector node matching supplied identifier
	 * 
	 * @param aDrawable
	 * @return
	 */
	public <O> ConnectorNode<O> getConnectorNode(DrawingTreeNodeIdentifier<O> identifier);

	/**
	 * Throw when a dependancy raises a loop
	 * 
	 * @author sylvain
	 * 
	 */
	@SuppressWarnings("serial")
	public static class DependencyLoopException extends Exception {
		private final List<DrawingTreeNode<?, ?>> dependencies;

		public DependencyLoopException(List<DrawingTreeNode<?, ?>> dependancies) {
			this.dependencies = dependancies;
		}

		@Override
		public String getMessage() {
			return "DependencyLoopException: " + dependencies;
		}
	}

	/**
	 * Encodes an identifier for a DrawingTreeNode<br>
	 * This couple of data always uniquely identifies an occurrence of a given object in a particular context in a Drawing.<br>
	 * Use of this identifier allows to guarantee a conceptual persistence over life cycle of {@link Drawing}.
	 * 
	 * @author sylvain
	 * 
	 * @param <O>
	 */
	public static class DrawingTreeNodeIdentifier<O> {
		private final O drawable;
		private final GRBinding<O, ?> grBinding;

		public DrawingTreeNodeIdentifier(O drawable, GRBinding<O, ?> grBinding) {
			super();
			this.drawable = drawable;
			this.grBinding = grBinding;
		}

		@Override
		public String toString() {
			return "DrawingTreeNodeIdentifier[drawable=" + drawable + ",grBinding=" + grBinding + "]";
		}

		public O getDrawable() {
			return drawable;
		}

		public GRBinding<O, ?> getGRBinding() {
			return grBinding;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (drawable == null ? 0 : drawable.hashCode());
			result = prime * result + (grBinding == null ? 0 : grBinding.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			DrawingTreeNodeIdentifier<?> other = (DrawingTreeNodeIdentifier<?>) obj;
			if (drawable == null) {
				if (other.drawable != null) {
					return false;
				}
			}
			else if (!drawable.equals(other.drawable)) {
				return false;
			}
			if (grBinding == null) {
				if (other.grBinding != null) {
					return false;
				}
			}
			else if (!grBinding.equals(other.grBinding)) {
				return false;
			}
			return true;
		}

	}

	public static interface PendingConnector<O> {
		public ConnectorNode<O> getConnectorNode();

		public DrawingTreeNodeIdentifier<?> getParentNodeIdentifier();

		public DrawingTreeNodeIdentifier<?> getStartNodeIdentifier();

		public DrawingTreeNodeIdentifier<?> getEndNodeIdentifier();

		public boolean tryToResolve(Drawing<?> drawing);

	}

}
