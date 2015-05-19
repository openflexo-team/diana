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

package org.openflexo.fge;

import java.awt.Dimension;
import java.awt.Stroke;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.DataBinding;
import org.openflexo.fge.control.MouseClickControl;
import org.openflexo.fge.control.MouseDragControl;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * This is the common super interfaces for all graphical representation object encoded in a diagram<br>
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * 
 * @param <O>
 *            the represented type
 */
@ModelEntity(isAbstract = true)
public interface GraphicalRepresentation extends FGEObject, Bindable, PropertyChangeListener {

	// Property keys

	// public static final String DRAWABLE_KEY = "drawable";
	// public static final String DRAWING_KEY = "drawing";

	@PropertyIdentifier(type = String.class)
	public static final String IDENTIFIER_KEY = "identifier";
	@PropertyIdentifier(type = Integer.class)
	public static final String LAYER_KEY = "layer";
	// public static final String HAS_TEXT = "hasText";
	@PropertyIdentifier(type = Double.class)
	public static final String TRANSPARENCY_KEY = "transparency";
	@PropertyIdentifier(type = String.class)
	public static final String TEXT_KEY = "text";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_MULTILINE_ALLOWED_KEY = "isMultilineAllowed";
	@PropertyIdentifier(type = Boolean.class)
	public static final String LINE_WRAP_KEY = "lineWrap";
	@PropertyIdentifier(type = Boolean.class)
	public static final String CONTINUOUS_TEXT_EDITING_KEY = "continuousTextEditing";
	@PropertyIdentifier(type = TextStyle.class)
	public static final String TEXT_STYLE_KEY = "textStyle";
	@PropertyIdentifier(type = Double.class)
	public static final String ABSOLUTE_TEXT_X_KEY = "absoluteTextX";
	@PropertyIdentifier(type = Double.class)
	public static final String ABSOLUTE_TEXT_Y_KEY = "absoluteTextY";
	@PropertyIdentifier(type = HorizontalTextAlignment.class)
	public static final String HORIZONTAL_TEXT_ALIGNEMENT_KEY = "horizontalTextAlignment";
	@PropertyIdentifier(type = VerticalTextAlignment.class)
	public static final String VERTICAL_TEXT_ALIGNEMENT_KEY = "verticalTextAlignment";
	@PropertyIdentifier(type = ParagraphAlignment.class)
	public static final String PARAGRAPH_ALIGNMENT_KEY = "paragraphAlignment";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_SELECTABLE_KEY = "isSelectable";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_FOCUSABLE_KEY = "isFocusable";
	// public static final String IS_SELECTED = "isSelected";
	// public static final String IS_FOCUSED = "isFocused";
	@PropertyIdentifier(type = Boolean.class)
	public static final String DRAW_CONTROL_POINTS_WHEN_FOCUSED_KEY = "drawControlPointsWhenFocused";
	@PropertyIdentifier(type = Boolean.class)
	public static final String DRAW_CONTROL_POINTS_WHEN_SELECTED_KEY = "drawControlPointsWhenSelected";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_READ_ONLY_KEY = "isReadOnly";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_LABEL_EDITABLE_KEY = "isLabelEditable";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_VISIBLE_KEY = "isVisible";
	public static final String MOUSE_CLICK_CONTROLS_KEY = "mouseClickControls";
	public static final String MOUSE_DRAG_CONTROLS_KEY = "mouseDragControls";
	@PropertyIdentifier(type = String.class)
	public static final String TOOLTIP_TEXT_KEY = "toolTipText";

	// public static final String VARIABLES = "variables";

	// *******************************************************************************
	// * Inner concepts
	// *******************************************************************************

	public static interface LabelMetricsProvider {
		public Dimension getScaledPreferredDimension(double scale);

	}

	/*public static interface GRProperty {
		public String name();
	}

	public static enum Parameters implements GRProperty {
		identifier, layer, hasText, text, isMultilineAllowed, lineWrap, continuousTextEditing, textStyle, absoluteTextX, // TODO: remove ?
		absoluteTextY, // TODO: remove ?
		horizontalTextAlignment,
		verticalTextAlignment,
		paragraphAlignment,
		isSelectable,
		isFocusable,
		// isSelected,
		// isFocused,
		drawControlPointsWhenFocused,
		drawControlPointsWhenSelected,
		isReadOnly,
		isLabelEditable,
		isVisible, // TODO: remove ?
		mouseClickControls,
		mouseDragControls,
		toolTipText,
		variables;

	}*/

	public static enum ParagraphAlignment {
		LEFT, CENTER, RIGHT, JUSTIFY;
	}

	// TODO: rename to HorizontalAlignment
	public static enum HorizontalTextAlignment {
		LEFT, CENTER, RIGHT
	}

	// TODO: rename to VerticalAlignment
	public static enum VerticalTextAlignment {
		TOP, MIDDLE, BOTTOM;
	}

	public static GRProperty<String> IDENTIFIER = GRProperty.getGRParameter(GraphicalRepresentation.class, IDENTIFIER_KEY, String.class);
	public static GRProperty<Integer> LAYER = GRProperty.getGRParameter(GraphicalRepresentation.class, LAYER_KEY, Integer.class);
	public static GRProperty<Double> TRANSPARENCY = GRProperty
			.getGRParameter(GraphicalRepresentation.class, TRANSPARENCY_KEY, Double.class);
	public static GRProperty<String> TEXT = GRProperty.getGRParameter(GraphicalRepresentation.class, TEXT_KEY, String.class);
	public static GRProperty<TextStyle> TEXT_STYLE = GRProperty.getGRParameter(GraphicalRepresentation.class, TEXT_STYLE_KEY,
			TextStyle.class);
	public static GRProperty<HorizontalTextAlignment> HORIZONTAL_TEXT_ALIGNEMENT = GRProperty.getGRParameter(GraphicalRepresentation.class,
			HORIZONTAL_TEXT_ALIGNEMENT_KEY, HorizontalTextAlignment.class);
	public static GRProperty<VerticalTextAlignment> VERTICAL_TEXT_ALIGNEMENT = GRProperty.getGRParameter(GraphicalRepresentation.class,
			VERTICAL_TEXT_ALIGNEMENT_KEY, VerticalTextAlignment.class);
	public static GRProperty<Double> ABSOLUTE_TEXT_X = GRProperty.getGRParameter(GraphicalRepresentation.class, ABSOLUTE_TEXT_X_KEY,
			Double.class);
	public static GRProperty<Double> ABSOLUTE_TEXT_Y = GRProperty.getGRParameter(GraphicalRepresentation.class, ABSOLUTE_TEXT_Y_KEY,
			Double.class);
	public static GRProperty<Boolean> IS_MULTILINE_ALLOWED = GRProperty.getGRParameter(GraphicalRepresentation.class,
			IS_MULTILINE_ALLOWED_KEY, Boolean.TYPE);
	public static GRProperty<Boolean> LINE_WRAP = GRProperty.getGRParameter(GraphicalRepresentation.class, LINE_WRAP_KEY, Boolean.class);
	public static GRProperty<Boolean> CONTINUOUS_TEXT_EDITING = GRProperty.getGRParameter(GraphicalRepresentation.class,
			CONTINUOUS_TEXT_EDITING_KEY, Boolean.class);

	public static GRProperty<ParagraphAlignment> PARAGRAPH_ALIGNEMENT = GRProperty.getGRParameter(GraphicalRepresentation.class,
			PARAGRAPH_ALIGNMENT_KEY, ParagraphAlignment.class);
	public static GRProperty<Boolean> IS_SELECTABLE = GRProperty.getGRParameter(GraphicalRepresentation.class, IS_SELECTABLE_KEY,
			Boolean.class);
	public static GRProperty<Boolean> IS_FOCUSABLE = GRProperty.getGRParameter(GraphicalRepresentation.class, IS_FOCUSABLE_KEY,
			Boolean.class);
	public static GRProperty<Boolean> DRAW_CONTROL_POINTS_WHEN_FOCUSED = GRProperty.getGRParameter(GraphicalRepresentation.class,
			DRAW_CONTROL_POINTS_WHEN_FOCUSED_KEY, Boolean.class);
	public static GRProperty<Boolean> DRAW_CONTROL_POINTS_WHEN_SELECTED = GRProperty.getGRParameter(GraphicalRepresentation.class,
			DRAW_CONTROL_POINTS_WHEN_SELECTED_KEY, Boolean.class);
	public static GRProperty<Boolean> IS_READ_ONLY = GRProperty.getGRParameter(GraphicalRepresentation.class, IS_READ_ONLY_KEY,
			Boolean.class);
	public static GRProperty<Boolean> IS_LABEL_EDITABLE = GRProperty.getGRParameter(GraphicalRepresentation.class, IS_LABEL_EDITABLE_KEY,
			Boolean.class);
	public static GRProperty<Boolean> IS_VISIBLE = GRProperty.getGRParameter(GraphicalRepresentation.class, IS_VISIBLE_KEY, Boolean.class);
	public static GRProperty<List> MOUSE_CLICK_CONTROLS = GRProperty.getGRParameter(GraphicalRepresentation.class,
			MOUSE_CLICK_CONTROLS_KEY, List.class);
	public static GRProperty<List> MOUSE_DRAG_CONTROLS = GRProperty.getGRParameter(GraphicalRepresentation.class, MOUSE_DRAG_CONTROLS_KEY,
			List.class);
	public static GRProperty<String> TOOLTIP_TEXT = GRProperty
			.getGRParameter(GraphicalRepresentation.class, TOOLTIP_TEXT_KEY, String.class);

	// *******************************************************************************
	// * Model
	// *******************************************************************************

	/*@Getter(value = DRAWING_KEY, ignoreType = true)
	@CloningStrategy(CloningStrategy.StrategyType.REFERENCE)
	public Drawing<?> getDrawing();

	@Setter(value = DRAWING_KEY)
	public void setDrawing(Drawing<?> drawing);*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = IDENTIFIER_KEY)
	@XMLAttribute
	public String getIdentifier();

	@Setter(value = IDENTIFIER_KEY)
	public void setIdentifier(String identifier);

	@Getter(value = LAYER_KEY, defaultValue = "0")
	@XMLAttribute
	public int getLayer();

	@Setter(value = LAYER_KEY)
	public void setLayer(int layer);

	/*@Getter(value = HAS_TEXT, defaultValue = "true")
	@XMLAttribute
	public boolean getHasText();

	@Setter(value = HAS_TEXT)
	public void setHasText(boolean hasText);*/

	@Getter(value = TEXT_KEY)
	@XMLAttribute
	public String getText();

	@Setter(value = TEXT_KEY)
	public void setText(String text);

	public String getMultilineText();

	public void setMultilineText(String text);

	@Getter(value = IS_MULTILINE_ALLOWED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsMultilineAllowed();

	@Setter(value = IS_MULTILINE_ALLOWED_KEY)
	public void setIsMultilineAllowed(boolean multilineAllowed);

	@Getter(value = LINE_WRAP_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getLineWrap();

	@Setter(value = LINE_WRAP_KEY)
	public void setLineWrap(boolean lineWrap);

	@Getter(value = CONTINUOUS_TEXT_EDITING_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getContinuousTextEditing();

	@Setter(value = CONTINUOUS_TEXT_EDITING_KEY)
	public void setContinuousTextEditing(boolean continuousTextEditing);

	@Getter(value = TEXT_STYLE_KEY)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	public TextStyle getTextStyle();

	@Setter(value = TEXT_STYLE_KEY)
	public void setTextStyle(TextStyle aTextStyle);

	@Getter(value = ABSOLUTE_TEXT_X_KEY, defaultValue = "0")
	@XMLAttribute
	public double getAbsoluteTextX();

	@Setter(value = ABSOLUTE_TEXT_X_KEY)
	public void setAbsoluteTextX(double absoluteTextX);

	@Getter(value = ABSOLUTE_TEXT_Y_KEY, defaultValue = "0")
	@XMLAttribute
	public double getAbsoluteTextY();

	@Setter(value = ABSOLUTE_TEXT_Y_KEY)
	public void setAbsoluteTextY(double absoluteTextY);

	@Getter(value = HORIZONTAL_TEXT_ALIGNEMENT_KEY, defaultValue = "CENTER")
	@XMLAttribute
	public HorizontalTextAlignment getHorizontalTextAlignment();

	@Setter(value = HORIZONTAL_TEXT_ALIGNEMENT_KEY)
	public void setHorizontalTextAlignment(HorizontalTextAlignment horizontalTextAlignment);

	@Getter(value = VERTICAL_TEXT_ALIGNEMENT_KEY, defaultValue = "MIDDLE")
	@XMLAttribute
	public VerticalTextAlignment getVerticalTextAlignment();

	@Setter(value = VERTICAL_TEXT_ALIGNEMENT_KEY)
	public void setVerticalTextAlignment(VerticalTextAlignment verticalTextAlignment);

	@Getter(value = PARAGRAPH_ALIGNMENT_KEY, defaultValue = "CENTER")
	@XMLAttribute
	public ParagraphAlignment getParagraphAlignment();

	@Setter(value = PARAGRAPH_ALIGNMENT_KEY)
	public void setParagraphAlignment(ParagraphAlignment paragraphAlignment);

	@Getter(value = IS_SELECTABLE_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsSelectable();

	@Setter(value = IS_SELECTABLE_KEY)
	public void setIsSelectable(boolean isSelectable);

	@Getter(value = IS_FOCUSABLE_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsFocusable();

	@Setter(value = IS_FOCUSABLE_KEY)
	public void setIsFocusable(boolean isFocusable);

	/*@Getter(value = IS_SELECTED, defaultValue = "false")
	@XMLAttribute
	public boolean getIsSelected();

	@Setter(value = IS_SELECTED)
	public void setIsSelected(boolean aFlag);

	@Getter(value = IS_FOCUSED, defaultValue = "false")
	@XMLAttribute
	public boolean getIsFocused();

	@Setter(value = IS_FOCUSED)
	public void setIsFocused(boolean aFlag);*/

	@Getter(value = DRAW_CONTROL_POINTS_WHEN_FOCUSED_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getDrawControlPointsWhenFocused();

	@Setter(value = DRAW_CONTROL_POINTS_WHEN_FOCUSED_KEY)
	public void setDrawControlPointsWhenFocused(boolean aFlag);

	@Getter(value = DRAW_CONTROL_POINTS_WHEN_SELECTED_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getDrawControlPointsWhenSelected();

	@Setter(value = DRAW_CONTROL_POINTS_WHEN_SELECTED_KEY)
	public void setDrawControlPointsWhenSelected(boolean aFlag);

	@Getter(value = IS_READ_ONLY_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsReadOnly();

	@Setter(value = IS_READ_ONLY_KEY)
	public void setIsReadOnly(boolean readOnly);

	@Getter(value = IS_LABEL_EDITABLE_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsLabelEditable();

	@Setter(value = IS_LABEL_EDITABLE_KEY)
	public void setIsLabelEditable(boolean labelEditable);

	@Getter(value = IS_VISIBLE_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsVisible();

	@Setter(value = IS_VISIBLE_KEY)
	public void setIsVisible(boolean isVisible);

	@Getter(value = TRANSPARENCY_KEY, defaultValue = "1.0")
	@XMLAttribute
	public double getTransparency();

	@Setter(value = TRANSPARENCY_KEY)
	public void setTransparency(double transparency);

	@Getter(value = MOUSE_CLICK_CONTROLS_KEY, cardinality = Cardinality.LIST, ignoreType = true)
	public List<MouseClickControl<?>> getMouseClickControls();

	@Setter(value = MOUSE_CLICK_CONTROLS_KEY)
	public void setMouseClickControls(List<MouseClickControl<?>> mouseClickControls);

	@Adder(value = MOUSE_CLICK_CONTROLS_KEY)
	public void addToMouseClickControls(MouseClickControl<?> mouseClickControl);

	public void addToMouseClickControls(MouseClickControl<?> mouseClickControl, boolean isPrioritar);

	@Remover(value = MOUSE_CLICK_CONTROLS_KEY)
	public void removeFromMouseClickControls(MouseClickControl<?> mouseClickControl);

	@Getter(value = MOUSE_DRAG_CONTROLS_KEY, cardinality = Cardinality.LIST, ignoreType = true)
	public List<MouseDragControl<?>> getMouseDragControls();

	@Setter(value = MOUSE_DRAG_CONTROLS_KEY)
	public void setMouseDragControls(List<MouseDragControl<?>> mouseDragControls);

	@Adder(value = MOUSE_DRAG_CONTROLS_KEY)
	public void addToMouseDragControls(MouseDragControl<?> mouseDragControl);

	public void addToMouseDragControls(MouseDragControl<?> mouseDragControl, boolean isPrioritar);

	@Remover(value = MOUSE_DRAG_CONTROLS_KEY)
	public void removeFromMouseDragControls(MouseDragControl<?> mouseDragControl);

	@Getter(value = TOOLTIP_TEXT_KEY)
	@XMLAttribute
	public String getToolTipText();

	@Setter(value = TOOLTIP_TEXT_KEY)
	public void setToolTipText(String tooltipText);

	// @Getter(value = VARIABLES, cardinality = Cardinality.LIST)
	// @XMLElement
	// public Vector<GRVariable> getVariables();

	// @Setter(value = VARIABLES)
	// public void setVariables(Vector<GRVariable> variables);

	// @Adder(value = VARIABLES)
	// public void addToVariables(GRVariable v);

	// @Remover(value = VARIABLES)
	// public void removeFromVariables(GRVariable v);

	// *******************************************************************************
	// * Utils
	// *******************************************************************************

	// public GRProperty<?> parameterWithName(String parameterName);

	// public Collection<GRProperty<?>> getAllParameters();

	public void setsWith(GraphicalRepresentation gr);

	public void setsWith(GraphicalRepresentation gr, GRProperty<?>... exceptedParameters);

	public void initializeDeserialization();

	public void finalizeDeserialization();

	@Override
	public boolean isDeserializing();

	// *******************************************************************************
	// * Graphical Utils
	// *******************************************************************************

	// public boolean hasFloatingLabel();

	// public boolean shouldBeDisplayed();

	/*public DrawingGraphicalRepresentation getDrawingGraphicalRepresentation();

	public GraphicalRepresentation getGraphicalRepresentation(Object drawable);

	public List<? extends Object> getContainedObjects(Object drawable);

	public Object getContainer(Object drawable);

	public List<? extends Object> getContainedObjects();

	public List<GraphicalRepresentation> getContainedGraphicalRepresentations();

	public List<GraphicalRepresentation> getOrderedContainedGraphicalRepresentations();*/

	public void moveToTop(GraphicalRepresentation gr);

	// public int getOrder(GraphicalRepresentation child1, GraphicalRepresentation child2);

	// public int getLayerOrder();

	// public int getIndex();

	/*public Object getContainer();

	public GraphicalRepresentation getContainerGraphicalRepresentation();

	public GraphicalRepresentation getParentGraphicalRepresentation();

	public boolean contains(GraphicalRepresentation gr);

	public boolean contains(Object drawable);

	public List<Object> getAncestors();

	public List<Object> getAncestors(boolean forceRecompute);

	public boolean isConnectedToDrawing();

	public boolean isAncestorOf(GraphicalRepresentation child);*/

	// public boolean isPointVisible(FGEPoint p);

	// public ShapeGraphicalRepresentation shapeHiding(FGEPoint p);

	// public boolean hasText();

	/*public int getViewX(double scale);

	public int getViewY(double scale);

	public int getViewWidth(double scale);

	public int getViewHeight(double scale);

	public Rectangle getViewBounds(double scale);

	public FGERectangle getNormalizedBounds();*/

	/*public Point getLabelLocation(double scale);

	public Dimension getLabelDimension(double scale);

	public void setLabelLocation(Point point, double scale);

	public Rectangle getLabelBounds(double scale);

	public void paint(Graphics g, DianaEditor controller);*/

	/*public void notifyChange(GRProperty parameter, Object oldValue, Object newValue);

	public void notifyChange(GRProperty parameter);

	public void notifyAttributeChange(GRProperty parameter);

	public void notify(FGENotification notification);*/

	// @Override
	// public String getInspectorName();

	public boolean isShape();

	public boolean isConnector();

	public boolean isDrawing();

	// public void notifyDrawableAdded(GraphicalRepresentation addedGR);

	// public void notifyDrawableRemoved(GraphicalRepresentation removedGR);

	// @Override
	// public void update(Observable observable, Object notification);

	/*public Point convertNormalizedPointToViewCoordinates(double x, double y, double scale);

	public Rectangle convertNormalizedRectangleToViewCoordinates(FGERectangle r, double scale);

	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale);

	public FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y, double scale);

	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale);

	public Point convertNormalizedPointToViewCoordinates(FGEPoint p, double scale);

	public FGEPoint convertViewCoordinatesToNormalizedPoint(Point p, double scale);

	public FGEPoint convertRemoteViewCoordinatesToLocalNormalizedPoint(Point p, GraphicalRepresentation source, double scale);

	public FGEPoint convertLocalViewCoordinatesToRemoteNormalizedPoint(Point p, GraphicalRepresentation destination, double scale);

	public Point convertLocalNormalizedPointToRemoteViewCoordinates(FGEPoint p, GraphicalRepresentation destination, double scale);

	public Rectangle convertLocalNormalizedRectangleToRemoteViewCoordinates(FGERectangle r, GraphicalRepresentation destination,
			double scale);

	public Point convertRemoteNormalizedPointToLocalViewCoordinates(FGEPoint p, GraphicalRepresentation source, double scale);
	*/

	// public boolean isRegistered();

	// public void setRegistered(boolean aFlag);

	public MouseClickControl<?> createMouseClickControl();

	public void deleteMouseClickControl(MouseClickControl<?> mouseClickControl);

	public boolean isMouseClickControlDeletable(MouseClickControl<?> mouseClickControl);

	public MouseDragControl<?> createMouseDragControl();

	public void deleteMouseDragControl(MouseDragControl<?> mouseDragControl);

	public boolean isMouseDragControlDeletable(MouseDragControl<?> mouseDragControl);

	// public boolean isContainedInSelection(Rectangle drawingViewSelection, double scale);

	/*public void notifyLabelWillBeEdited();

	public void notifyLabelHasBeenEdited();

	public void notifyLabelWillMove();

	public void notifyLabelHasMoved();*/

	// Override when required
	// public void notifyObjectHierarchyWillBeUpdated();

	// Override when required
	// public void notifyObjectHierarchyHasBeenUpdated();

	// public void performRandomLayout(double width, double height);

	// public void performAutoLayout(double width, double height);

	public Stroke getSpecificStroke();

	public void setSpecificStroke(Stroke aStroke);

	/*public boolean isRootGraphicalRepresentation();

	public GraphicalRepresentation getRootGraphicalRepresentation();*/

	public void createBindingModel();

	@Override
	public BindingModel getBindingModel();

	@Override
	public BindingFactory getBindingFactory();

	public void updateBindingModel();

	/*@Override
	public Object getValue(BindingVariable variable);*/

	public void notifiedBindingModelRecreated();

	public void notifyBindingChanged(DataBinding<?> binding);

	// public List<GraphicalRepresentation> retrieveAllContainedGR();

	// public Iterator<GraphicalRepresentation> allGRIterator();

	// public Iterator<GraphicalRepresentation> allContainedGRIterator();

	/*public Vector<ConstraintDependency> getDependancies();

	public Vector<ConstraintDependency> getAlterings();

	public void declareDependantOf(GraphicalRepresentation aComponent, GRProperty requiringParameter, GRProperty requiredParameter)
			throws DependencyLoopException;
	*/
	// public GRVariable createStringVariable();

	// public GRVariable createIntegerVariable();

	// public void deleteVariable(GRVariable v);

	@Override
	public PropertyChangeSupport getPropertyChangeSupport();

	@Override
	public String getDeletedProperty();

	/*public boolean isValidated();

	public void setValidated(boolean validated);

	public LabelMetricsProvider getLabelMetricsProvider();

	public void setLabelMetricsProvider(LabelMetricsProvider labelMetricsProvider);

	public int getAvailableLabelWidth(double scale);*/

}
