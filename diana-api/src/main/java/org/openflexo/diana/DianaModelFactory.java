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

package org.openflexo.diana;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.openflexo.diana.BackgroundStyle.BackgroundStyleType;
import org.openflexo.diana.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.diana.ForegroundStyle.CapStyle;
import org.openflexo.diana.ForegroundStyle.DashStyle;
import org.openflexo.diana.ForegroundStyle.JoinStyle;
import org.openflexo.diana.TextureBackgroundStyle.TextureType;
import org.openflexo.diana.connectors.ConnectorSpecification;
import org.openflexo.diana.connectors.CurveConnectorSpecification;
import org.openflexo.diana.connectors.CurvedPolylinConnectorSpecification;
import org.openflexo.diana.connectors.LineConnectorSpecification;
import org.openflexo.diana.connectors.RectPolylinConnectorSpecification;
import org.openflexo.diana.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.diana.control.DianaEditor;
import org.openflexo.diana.control.MouseClickControl;
import org.openflexo.diana.control.MouseClickControlAction;
import org.openflexo.diana.control.MouseDragControl;
import org.openflexo.diana.control.MouseDragControlAction;
import org.openflexo.diana.control.PredefinedMouseClickControlActionType;
import org.openflexo.diana.control.PredefinedMouseDragControlActionType;
import org.openflexo.diana.control.MouseControl.MouseButton;
import org.openflexo.diana.geom.DianaComplexCurve;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolygon;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.shapes.Arc;
import org.openflexo.diana.shapes.Chevron;
import org.openflexo.diana.shapes.Circle;
import org.openflexo.diana.shapes.ComplexCurve;
import org.openflexo.diana.shapes.Losange;
import org.openflexo.diana.shapes.Oval;
import org.openflexo.diana.shapes.Parallelogram;
import org.openflexo.diana.shapes.Plus;
import org.openflexo.diana.shapes.Polygon;
import org.openflexo.diana.shapes.Rectangle;
import org.openflexo.diana.shapes.RectangularOctogon;
import org.openflexo.diana.shapes.RegularPolygon;
import org.openflexo.diana.shapes.ShapeSpecification;
import org.openflexo.diana.shapes.Square;
import org.openflexo.diana.shapes.Star;
import org.openflexo.diana.shapes.Triangle;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.ModelContextLibrary;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.rm.Resource;
import org.openflexo.toolbox.ToolBox;

/**
 * This is the default PAMELA model factory for class involved in FlexoGraphicalEngine model management<br>
 * All objects involved in a Diana model should be created using this factory in order to be well managed by PAMELA framework.<br>
 * In particular, please note that all objects belonging to the closure of an object graph managed using PAMELA must share the same factory.
 * 
 * This class also provides support for XML serializing/deserializing
 * 
 * Please feel free to override this class
 * 
 * @author sylvain
 * 
 */
public abstract class DianaModelFactory extends ModelFactory {

	static final Logger LOGGER = Logger.getLogger(DianaModelFactory.class.getPackage().getName());

	/**
	 * Creates a new model factory including all classes involved in Diana model
	 * 
	 * @throws ModelDefinitionException
	 */
	public DianaModelFactory() throws ModelDefinitionException {
		this(new ArrayList<Class<?>>());
	}

	/**
	 * Creates a new model factory including all supplied classes and all classes involved in Diana model
	 * 
	 * @throws ModelDefinitionException
	 */
	public DianaModelFactory(final Class<?>... classes) throws ModelDefinitionException {
		this(Arrays.asList(classes));
	}

	/**
	 * Creates a new model factory including all supplied classes and all classes involved in Diana model
	 * 
	 * @throws ModelDefinitionException
	 */
	public DianaModelFactory(final Collection<Class<?>> classes) throws ModelDefinitionException {

		super(ModelContextLibrary.getCompoundModelContext(appendGRClasses(classes)));

		this.addConverter(DianaUtils.DATA_BINDING_CONVERTER);
		this.getStringEncoder().addConverter(DianaUtils.POINT_CONVERTER);
		this.getStringEncoder().addConverter(DianaUtils.STEPPED_DIMENSION_CONVERTER);

		// logger.info("Created new DianaModelFactory...............................");

		this.installImplementingClasses();

	}

	public abstract void installImplementingClasses() throws ModelDefinitionException;

	private static Class<?>[] appendGRClasses(final Collection<Class<?>> classes) {
		final Set<Class<?>> returned = new HashSet<>(classes);
		returned.add(GraphicalRepresentation.class);
		returned.add(DrawingGraphicalRepresentation.class);
		returned.add(ShapeGraphicalRepresentation.class);
		returned.add(ConnectorGraphicalRepresentation.class);
		returned.add(GeometricGraphicalRepresentation.class);
		returned.add(DianaLayoutManager.class);
		returned.add(DianaLayoutManagerSpecification.class);
		return returned.toArray(new Class<?>[returned.size()]);
	}

	/**
	 * Deserialized object are always set with basic controls
	 */
	@Override
	public <I> void objectHasBeenDeserialized(final I newlyCreatedObject, final Class<I> implementedInterface) {
		super.objectHasBeenDeserialized(newlyCreatedObject, implementedInterface);
		if (newlyCreatedObject instanceof DrawingGraphicalRepresentation) {
			this.applyBasicControls((DrawingGraphicalRepresentation) newlyCreatedObject);
		}
		if (newlyCreatedObject instanceof ShapeGraphicalRepresentation) {
			this.applyBasicControls((ShapeGraphicalRepresentation) newlyCreatedObject);
		}
		if (newlyCreatedObject instanceof ConnectorGraphicalRepresentation) {
			this.applyBasicControls((ConnectorGraphicalRepresentation) newlyCreatedObject);
		}
		if (newlyCreatedObject instanceof GeometricGraphicalRepresentation) {
			this.applyBasicControls((GeometricGraphicalRepresentation) newlyCreatedObject);
		}
	}

	@Override
	public <I> void objectHasBeenCreated(final I newlyCreatedObject, final Class<I> implementedInterface) {
		if (newlyCreatedObject instanceof DianaObject) {
			((DianaObject) newlyCreatedObject).setFactory(this);
		}
		super.objectHasBeenCreated(newlyCreatedObject, implementedInterface);
		if (newlyCreatedObject instanceof DrawingGraphicalRepresentation) {
			this.applyBasicControls((DrawingGraphicalRepresentation) newlyCreatedObject);
		}
		if (newlyCreatedObject instanceof ShapeGraphicalRepresentation) {
			this.applyBasicControls((ShapeGraphicalRepresentation) newlyCreatedObject);
		}
		if (newlyCreatedObject instanceof ConnectorGraphicalRepresentation) {
			this.applyBasicControls((ConnectorGraphicalRepresentation) newlyCreatedObject);
		}
		if (newlyCreatedObject instanceof GeometricGraphicalRepresentation) {
			this.applyBasicControls((GeometricGraphicalRepresentation) newlyCreatedObject);
		}
	}

	/**
	 * Creates and return a new DrawingGraphicalRepresentation, given a Drawing instance, and initialized with default values and default
	 * controls (drawing selection, rectangle selection and zoom)
	 * 
	 * @param aDrawing
	 * @return a newly created DrawingGraphicalRepresentation
	 */
	public DrawingGraphicalRepresentation makeDrawingGraphicalRepresentation(/*Drawing<?> aDrawing*/) {
		return this.makeDrawingGraphicalRepresentation(/*aDrawing,*/true);
	}

	/**
	 * Creates and return a new DrawingGraphicalRepresentation, given a Drawing instance, and initialized with default values and a flag
	 * indicating if default controls should be created (drawing selection, rectangle selection and zoom)
	 * 
	 * @param aDrawing
	 * @return a newly created DrawingGraphicalRepresentation
	 */
	public DrawingGraphicalRepresentation makeDrawingGraphicalRepresentation(/*Drawing<?> aDrawing,*/final boolean initBasicControls) {
		final DrawingGraphicalRepresentation returned = this.newInstance(DrawingGraphicalRepresentation.class, true, initBasicControls);
		returned.setFactory(this);
		// returned.setDrawable(aDrawing.getModel());
		// returned.setDrawing(aDrawing);
		return returned;
	}

	public <LMS extends DianaLayoutManagerSpecification<?>> LMS makeLayoutManagerSpecification(String identifier,
			Class<? extends LMS> layoutManagerSpecClass) {
		LMS returned = newInstance(layoutManagerSpecClass);
		returned.setIdentifier(identifier);
		return returned;
	}

	/**
	 * Sets and apply default properties (text style, background and size) to supplied DrawingGraphicalRepresentation
	 * 
	 * @param drawingGraphicalRepresentation
	 */
	public void applyDefaultProperties(final DrawingGraphicalRepresentation drawingGraphicalRepresentation) {
		drawingGraphicalRepresentation.setTextStyle(this.makeDefaultTextStyle());
		drawingGraphicalRepresentation.setBackgroundColor(DianaConstants.DEFAULT_DRAWING_BACKGROUND_COLOR);
		drawingGraphicalRepresentation.setWidth(DianaConstants.DEFAULT_DRAWING_WIDTH);
		drawingGraphicalRepresentation.setHeight(DianaConstants.DEFAULT_DRAWING_HEIGHT);
	}

	/**
	 * Sets and apply default basic controls (drawing selection, rectangle selection and zoom) to supplied DrawingGraphicalRepresentation
	 * 
	 * @param drawingGraphicalRepresentation
	 */
	public void applyBasicControls(final DrawingGraphicalRepresentation drawingGraphicalRepresentation) {
		drawingGraphicalRepresentation.addToMouseClickControls(
				this.makeMouseClickControl("Drawing selection", MouseButton.LEFT, 1, PredefinedMouseClickControlActionType.SELECTION));
		drawingGraphicalRepresentation.addToMouseDragControls(this.makeMouseDragControl("Rectangle selection", MouseButton.LEFT,
				PredefinedMouseDragControlActionType.RECTANGLE_SELECTING));
		drawingGraphicalRepresentation
				.addToMouseDragControls(this.makeMouseDragControl("Zoom", MouseButton.RIGHT, PredefinedMouseDragControlActionType.ZOOM));
	}

	/**
	 * Creates and return a new ShapeGraphicalRepresentation, given a Drawable and a Drawing instance, initialized with default values as a
	 * rectangle shape
	 * 
	 * @param aDrawing
	 *            the drawing where the shape is defined
	 * @param aDrawable
	 *            object supposed to be represented by returned graphical representation
	 * 
	 * @return a newly created ShapeGraphicalRepresentation
	 */
	public <O> ShapeGraphicalRepresentation makeShapeGraphicalRepresentation() {

		final ShapeGraphicalRepresentation returned = this.newInstance(ShapeGraphicalRepresentation.class, true, true);
		returned.setFactory(this);
		// returned.setDrawable(aDrawable);
		// returned.setDrawing(aDrawing);
		return returned;
	}

	/**
	 * Sets and apply default properties (border, layer, text style, foreground, background and shadow) to supplied
	 * ShapeGraphicalRepresentation
	 * 
	 * @param shapeGraphicalRepresentation
	 */
	public void applyDefaultProperties(final ShapeGraphicalRepresentation shapeGraphicalRepresentation) {
		// shapeGraphicalRepresentation.setBorder(this.makeShapeBorder());
		shapeGraphicalRepresentation.setLayer(DianaConstants.DEFAULT_SHAPE_LAYER);
		shapeGraphicalRepresentation.setTextStyle(this.makeDefaultTextStyle());
		shapeGraphicalRepresentation.setForeground(this.makeDefaultForegroundStyle());
		shapeGraphicalRepresentation.setBackground(this.makeColoredBackground(Color.WHITE));
		shapeGraphicalRepresentation.setShadowStyle(this.makeDefaultShadowStyle());
	}

	/**
	 * Sets and apply default basic controls (drawing selection, rectangle selection and zoom) to supplied ShapeGraphicalRepresentation
	 * 
	 * @param shapeGraphicalRepresentation
	 */
	public void applyBasicControls(final ShapeGraphicalRepresentation shapeGraphicalRepresentation) {
		if (shapeGraphicalRepresentation.getMouseClickControl("Selection") == null) {
			shapeGraphicalRepresentation.addToMouseClickControls(
					this.makeMouseClickControl("Selection", MouseButton.LEFT, 1, PredefinedMouseClickControlActionType.SELECTION));
		}
		if (shapeGraphicalRepresentation.getMouseClickControl("Multiple selection") == null) {
			if (ToolBox.isMacOS()) {
				shapeGraphicalRepresentation.addToMouseClickControls(this.makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT,
						1, PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
			}
			else {
				shapeGraphicalRepresentation.addToMouseClickControls(this.makeMouseControlClickControl("Multiple selection",
						MouseButton.LEFT, 1, PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
			}
		}
		if (shapeGraphicalRepresentation.getMouseDragControl("Move") == null) {
			shapeGraphicalRepresentation
					.addToMouseDragControls(this.makeMouseDragControl("Move", MouseButton.LEFT, PredefinedMouseDragControlActionType.MOVE));
		}
		if (shapeGraphicalRepresentation.getMouseDragControl("Zoom") == null) {
			shapeGraphicalRepresentation.addToMouseDragControls(
					this.makeMouseDragControl("Zoom", MouseButton.RIGHT, PredefinedMouseDragControlActionType.ZOOM));
		}
		if (shapeGraphicalRepresentation.getMouseDragControl("Rectangle selection") == null) {
			shapeGraphicalRepresentation.addToMouseDragControls(this.makeMouseShiftDragControl("Rectangle selection", MouseButton.LEFT,
					PredefinedMouseDragControlActionType.RECTANGLE_SELECTING));
		}
	}

	/**
	 * Creates and return a new ShapeGraphicalRepresentation, initialized with default values and given a shape type
	 * 
	 * @return a newly created ShapeGraphicalRepresentation
	 */
	public <O> ShapeGraphicalRepresentation makeShapeGraphicalRepresentation(final ShapeType shapeType) {
		final ShapeGraphicalRepresentation returned = this.makeShapeGraphicalRepresentation();
		returned.setShapeType(shapeType);
		return returned;
	}

	/**
	 * Creates and return a new ShapeGraphicalRepresentation, given a ShapeSpecification
	 * 
	 * @return a newly created ShapeGraphicalRepresentation
	 */
	public <O> ShapeGraphicalRepresentation makeShapeGraphicalRepresentation(final ShapeSpecification shapeSpecification) {
		final ShapeGraphicalRepresentation returned = this.makeShapeGraphicalRepresentation();
		returned.setShapeSpecification(shapeSpecification);
		return returned;
	}

	/**
	 * Creates and return a new ShapeGraphicalRepresentation, initialized with all properties of passed GR given a shape type
	 * 
	 * @return a newly created ShapeGraphicalRepresentation
	 */
	public <O> ShapeGraphicalRepresentation makeShapeGraphicalRepresentation(final ShapeGraphicalRepresentation aGR) {
		final ShapeGraphicalRepresentation returned = this.makeShapeGraphicalRepresentation();
		returned.setsWith(aGR);
		return returned;
	}

	/**
	 * Creates and return a new ConnectorGraphicalRepresentation
	 * 
	 * @return a newly created ConnectorGraphicalRepresentation
	 */
	public <O> ConnectorGraphicalRepresentation makeConnectorGraphicalRepresentation() {

		final ConnectorGraphicalRepresentation returned = this.newInstance(ConnectorGraphicalRepresentation.class, true, true);
		returned.setFactory(this);
		// returned.setDrawable(aDrawable);
		// returned.setDrawing(aDrawing);
		return returned;
	}

	/**
	 * Creates and return a new ConnectorGraphicalRepresentation, given a Drawable and a Drawing instance, initialized with default values.
	 * <br>
	 * The newly created connector is also initialized with connector type, and bound to supplied start and end shapes
	 * 
	 * @param aDrawing
	 *            the drawing where the shape is defined
	 * @param aDrawable
	 *            object supposed to be represented by returned graphical representation
	 * 
	 * @return a newly created ConnectorGraphicalRepresentation
	 */
	public <O> ConnectorGraphicalRepresentation makeConnectorGraphicalRepresentation(final ConnectorType aConnectorType) {

		final ConnectorGraphicalRepresentation returned = this.makeConnectorGraphicalRepresentation();
		// returned.setStartObject(aStartObject);
		// returned.setEndObject(anEndObject);
		returned.setConnectorType(aConnectorType);

		return returned;
	}

	/**
	 * Sets and apply default properties (layer, text style, foreground) to supplied ConnectorGraphicalRepresentation
	 * 
	 * @param connectorGraphicalRepresentation
	 */
	public void applyDefaultProperties(final ConnectorGraphicalRepresentation connectorGraphicalRepresentation) {
		connectorGraphicalRepresentation.setLayer(DianaConstants.DEFAULT_CONNECTOR_LAYER);
		connectorGraphicalRepresentation.setForeground(this.makeDefaultForegroundStyle());
		connectorGraphicalRepresentation.setTextStyle(this.makeDefaultTextStyle());
	}

	/**
	 * Sets and apply default basic controls (drawing selection, rectangle selection and zoom) to supplied ConnectorGraphicalRepresentation
	 * 
	 * @param connectorGraphicalRepresentation
	 */
	public void applyBasicControls(final ConnectorGraphicalRepresentation connectorGraphicalRepresentation) {
		connectorGraphicalRepresentation.addToMouseClickControls(
				this.makeMouseClickControl("Selection", MouseButton.LEFT, 1, PredefinedMouseClickControlActionType.SELECTION));
		if (ToolBox.isMacOS()) {
			connectorGraphicalRepresentation.addToMouseClickControls(this.makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT,
					1, PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
		}
		else {
			connectorGraphicalRepresentation.addToMouseClickControls(this.makeMouseControlClickControl("Multiple selection",
					MouseButton.LEFT, 1, PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
		}
	}

	/**
	 * Creates and return a new GeometricGraphicalRepresentation, given a Drawable and a Drawing instance, initialized with default values
	 * 
	 * @param aDrawing
	 *            the drawing where the shape is defined
	 * @param aDrawable
	 *            object supposed to be represented by returned graphical representation
	 * 
	 * @return a newly created GeometricGraphicalRepresentation
	 */
	public <O> GeometricGraphicalRepresentation makeGeometricGraphicalRepresentation() {

		final GeometricGraphicalRepresentation returned = this.newInstance(GeometricGraphicalRepresentation.class, true, true);
		returned.setFactory(this);
		// returned.setDrawable(aDrawable);
		// returned.setDrawing(aDrawing);

		this.applyDefaultProperties(returned);
		this.applyBasicControls(returned);

		return returned;
	}

	/**
	 * Creates and return a new GeometricGraphicalRepresentation, given a Drawable and a Drawing instance, initialized with default values
	 * 
	 * @param aDrawing
	 *            the drawing where the shape is defined
	 * @param aDrawable
	 *            object supposed to be represented by returned graphical representation
	 * 
	 * @return a newly created GeometricGraphicalRepresentation
	 */
	public <O> GeometricGraphicalRepresentation makeGeometricGraphicalRepresentation(final DianaArea geometricObject) {

		final GeometricGraphicalRepresentation returned = this.makeGeometricGraphicalRepresentation();
		returned.setGeometricObject(geometricObject);
		return returned;
	}

	/**
	 * Sets and apply default properties (layer, text style, foreground) to supplied ConnectorGraphicalRepresentation
	 * 
	 * @param connectorGraphicalRepresentation
	 */
	public void applyDefaultProperties(final GeometricGraphicalRepresentation geometricGraphicalRepresentation) {
		geometricGraphicalRepresentation.setLayer(DianaConstants.DEFAULT_OBJECT_LAYER);
		geometricGraphicalRepresentation.setForeground(this.makeDefaultForegroundStyle());
		geometricGraphicalRepresentation.setBackground(this.makeColoredBackground(Color.WHITE));
		geometricGraphicalRepresentation.setTextStyle(this.makeDefaultTextStyle());
	}

	/**
	 * Sets and apply default basic controls (drawing selection, rectangle selection and zoom) to supplied ConnectorGraphicalRepresentation
	 * 
	 * @param connectorGraphicalRepresentation
	 */
	public void applyBasicControls(final GeometricGraphicalRepresentation geometricGraphicalRepresentation) {
		geometricGraphicalRepresentation.addToMouseClickControls(
				this.makeMouseClickControl("Selection", MouseButton.LEFT, 1, PredefinedMouseClickControlActionType.SELECTION));
		if (ToolBox.isMacOS()) {
			geometricGraphicalRepresentation.addToMouseClickControls(this.makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT,
					1, PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
		}
		else {
			geometricGraphicalRepresentation.addToMouseClickControls(this.makeMouseControlClickControl("Multiple selection",
					MouseButton.LEFT, 1, PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
		}
	}

	public ConnectorSpecification makeConnector(final ConnectorType type) {

		if (type == ConnectorType.LINE) {
			return this.makeLineConnector();
		}
		else if (type == ConnectorType.RECT_POLYLIN) {
			return this.makeRectPolylinConnector();
		}
		else if (type == ConnectorType.CURVE) {
			return this.makeCurveConnector();
		}
		else if (type == ConnectorType.CURVED_POLYLIN) {
			return this.makeCurvedPolylinConnector();
		}
		LOGGER.warning("Unexpected type: " + type);
		return null;

	}

	/**
	 * Creates and return a new LineConnectorSpecification, given a connector graphical representation
	 * 
	 * @param aGR
	 * @return a newly created LineConnectorSpecification
	 */
	public LineConnectorSpecification makeLineConnector() {
		final LineConnectorSpecification returned = this.newInstance(LineConnectorSpecification.class);
		returned.setFactory(this);
		// aGR.setConnectorSpecification(returned);
		// returned.setGraphicalRepresentation(aGR);
		return returned;
	}

	/**
	 * Creates and return a new CurveConnectorSpecification, given a connector graphical representation
	 * 
	 * @param aGR
	 * @return a newly created CurveConnectorSpecification
	 */
	public CurveConnectorSpecification makeCurveConnector() {
		final CurveConnectorSpecification returned = this.newInstance(CurveConnectorSpecification.class);
		returned.setFactory(this);
		// aGR.setConnectorSpecification(returned);
		// returned.setGraphicalRepresentation(aGR);
		return returned;
	}

	/**
	 * Creates and return a new RectPolylinConnectorSpecification, given a connector graphical representation
	 * 
	 * @param aGR
	 * @return a newly created RectPolylinConnectorSpecification
	 */
	public RectPolylinConnectorSpecification makeRectPolylinConnector() {
		final RectPolylinConnectorSpecification returned = this.newInstance(RectPolylinConnectorSpecification.class);
		returned.setFactory(this);
		// aGR.setConnectorSpecification(returned);
		// returned.setGraphicalRepresentation(aGR);
		return returned;
	}

	/**
	 * Creates and return a new CurvedPolylinConnectorSpecification, given a connector graphical representation
	 * 
	 * @param aGR
	 * @return a newly created CurvedPolylinConnectorSpecification
	 */
	public CurvedPolylinConnectorSpecification makeCurvedPolylinConnector() {
		final CurvedPolylinConnectorSpecification returned = this.newInstance(CurvedPolylinConnectorSpecification.class);
		returned.setFactory(this);
		// aGR.setConnectorSpecification(returned);
		// returned.setGraphicalRepresentation(aGR);
		return returned;
	}

	/**
	 * Make a new foreground style (stroke style)
	 * 
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeNewForegroundStyle() {
		final ForegroundStyle returned = this.newInstance(ForegroundStyle.class);
		returned.setFactory(this);
		return returned;
	}

	/**
	 * Make a new foreground style (stroke style), initialized with default values as declared in DianaConstants
	 * 
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeDefaultForegroundStyle() {
		final ForegroundStyle returned = this.newInstance(ForegroundStyle.class);
		returned.setFactory(this);
		returned.setNoStroke(false);
		returned.setColor(Color.BLACK);
		returned.setLineWidth(1.0);
		returned.setJoinStyle(JoinStyle.JOIN_MITER);
		returned.setCapStyle(CapStyle.CAP_SQUARE);
		returned.setDashStyle(DashStyle.PLAIN_STROKE);
		return returned;
	}

	/**
	 * Make a new foreground style (stroke style), with no stroke (invisible stroke)
	 * 
	 * @param aColor
	 *            the color to assign to line
	 * 
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeNoneForegroundStyle() {
		final ForegroundStyle returned = this.makeDefaultForegroundStyle();
		returned.setNoStroke(true);
		return returned;
	}

	/**
	 * Make a new foreground style (stroke style), initialized with default values as declared in DianaConstants and a specific color
	 * 
	 * @param aColor
	 *            the color to assign to line
	 * 
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeForegroundStyle(final Color aColor) {
		final ForegroundStyle returned = this.makeDefaultForegroundStyle();
		returned.setColor(aColor);
		return returned;
	}

	/**
	 * Make a new foreground style (stroke style), initialized with default values as declared in DianaConstants, with a specific color and a
	 * specific line width
	 * 
	 * @param aColor
	 *            the color to assign to line
	 * @param aLineWidth
	 *            the width of line
	 * 
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeForegroundStyle(final Color aColor, final float aLineWidth) {
		final ForegroundStyle returned = this.makeForegroundStyle(aColor);
		returned.setLineWidth(aLineWidth);
		return returned;
	}

	/**
	 * Make a new foreground style (stroke style), initialized with values supplied as parameters
	 * 
	 * @param aColor
	 *            the color to assign to line
	 * @param aLineWidth
	 *            the width of line
	 * @param joinStyle
	 * @param capStyle
	 * @param dashStyle
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeForegroundStyle(final Color aColor, final float aLineWidth, final JoinStyle joinStyle,
			final CapStyle capStyle, final DashStyle dashStyle) {
		final ForegroundStyle returned = this.makeForegroundStyle(aColor);
		returned.setLineWidth(aLineWidth);
		returned.setJoinStyle(joinStyle);
		returned.setCapStyle(capStyle);
		returned.setDashStyle(dashStyle);
		return returned;
	}

	/**
	 * Make a new foreground style (stroke style), initialized with values supplied as parameters
	 * 
	 * @param aColor
	 *            the color to assign to line
	 * @param aLineWidth
	 *            the width of line
	 * @param dashStyle
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeForegroundStyle(final Color aColor, final float aLineWidth, final DashStyle dashStyle) {
		final ForegroundStyle returned = this.makeForegroundStyle(aColor);
		returned.setLineWidth(aLineWidth);
		returned.setDashStyle(dashStyle);
		return returned;
	}

	/**
	 * Make a new colored background style initialized with default values as declared in DianaConstants
	 * 
	 * @return a newly created ForegroundStyle
	 */
	public BackgroundStyle makeDefaultBackgroundStyle() {
		return this.makeColoredBackground(DianaConstants.DEFAULT_BACKGROUND_COLOR);
	}

	/**
	 * Make a new background style as empty background (invisible)
	 * 
	 * @return a newly created BackgroundStyle
	 */
	public NoneBackgroundStyle makeEmptyBackground() {
		final NoneBackgroundStyle returned = this.newInstance(NoneBackgroundStyle.class);
		returned.setFactory(this);
		return returned;
	}

	/**
	 * Make a new background style as plain colored background (invisible)
	 * 
	 * @param aColor
	 *            color to be used as plain colored background
	 * @return a newly created BackgroundStyle
	 */
	public ColorBackgroundStyle makeColoredBackground(final Color aColor) {
		final ColorBackgroundStyle returned = this.newInstance(ColorBackgroundStyle.class);
		returned.setFactory(this);
		returned.setColor(aColor);
		return returned;
	}

	/**
	 * Make a new background style as color gradient with two colors
	 * 
	 * @param color1
	 * @param color2
	 * @param direction
	 * 
	 * @return a newly created BackgroundStyle
	 */
	public ColorGradientBackgroundStyle makeColorGradientBackground(final Color color1, final Color color2,
			final ColorGradientDirection direction) {
		final ColorGradientBackgroundStyle returned = this.newInstance(ColorGradientBackgroundStyle.class);
		returned.setFactory(this);
		returned.setColor1(color1);
		returned.setColor2(color2);
		returned.setDirection(direction);
		return returned;
	}

	/**
	 * Make a new background style as textured background with two colors
	 * 
	 * @param color1
	 * @param color2
	 * @param textureType
	 * 
	 * @return a newly created BackgroundStyle
	 */
	public TextureBackgroundStyle makeTexturedBackground(final TextureType textureType, final Color color1, final Color color2) {
		final TextureBackgroundStyle returned = this.newInstance(TextureBackgroundStyle.class);
		returned.setFactory(this);
		returned.setColor1(color1);
		returned.setColor2(color2);
		returned.setTextureType(textureType);
		return returned;
	}

	/**
	 * Make a new background style as image background, given a file encoding image
	 * 
	 * @param imageResource
	 *            the file where image is located (most image format allowed)
	 * 
	 * @return a newly created BackgroundStyle
	 */
	public BackgroundImageBackgroundStyle makeImageBackground(final Resource imageResource) {
		final BackgroundImageBackgroundStyle returned = this.newInstance(BackgroundImageBackgroundStyle.class);
		returned.setFactory(this);
		returned.setImageResource(imageResource);
		return returned;
	}

	/**
	 * Make a new background style as image background, given an image
	 * 
	 * @param image
	 * 
	 * @return a newly created BackgroundStyle
	 */
	public BackgroundImageBackgroundStyle makeImageBackground(final Image image) {
		final BackgroundImageBackgroundStyle returned = this.newInstance(BackgroundImageBackgroundStyle.class);
		returned.setFactory(this);
		returned.setImage(image);
		return returned;
	}

	/**
	 * Make a new background style given type of background to create
	 * 
	 * @param type
	 * @return a newly created BackgroundStyle
	 */
	public BackgroundStyle makeBackground(final BackgroundStyleType type) {
		if (type == BackgroundStyleType.NONE) {
			return this.makeEmptyBackground();
		}
		else if (type == BackgroundStyleType.COLOR) {
			return this.makeColoredBackground(java.awt.Color.WHITE);
		}
		else if (type == BackgroundStyleType.COLOR_GRADIENT) {
			return this.makeColorGradientBackground(java.awt.Color.WHITE, java.awt.Color.BLACK,
					ColorGradientDirection.NORTH_WEST_SOUTH_EAST);
		}
		else if (type == BackgroundStyleType.TEXTURE) {
			return this.makeTexturedBackground(TextureType.TEXTURE1, java.awt.Color.RED, java.awt.Color.WHITE);
		}
		else if (type == BackgroundStyleType.IMAGE) {
			return this.makeImageBackground((Resource) null);
		}
		return null;
	}

	/**
	 * Make a shadow style without any shadow
	 * 
	 * @return a newly created ShadowStyle
	 */
	public ShadowStyle makeNoneShadowStyle() {
		final ShadowStyle returned = this.newInstance(ShadowStyle.class);
		returned.setFactory(this);
		returned.setDrawShadow(false);
		returned.setShadowDepth(0);
		return returned;
	}

	/**
	 * Make default shadow style (initialized with default values as declared in DianaConstants)
	 * 
	 * @return a newly created ShadowStyle
	 */
	public ShadowStyle makeDefaultShadowStyle() {
		final ShadowStyle returned = this.newInstance(ShadowStyle.class);
		returned.setFactory(this);
		returned.setDrawShadow(true);
		returned.setShadowDarkness(DianaConstants.DEFAULT_SHADOW_DARKNESS);
		returned.setShadowDepth(DianaConstants.DEFAULT_SHADOW_DEEP);
		returned.setShadowBlur(DianaConstants.DEFAULT_SHADOW_BLUR);
		return returned;
	}

	/**
	 * Make a new text style, initialized with default values as declared in DianaConstants
	 * 
	 * @return a newly created TextStyle
	 */
	public TextStyle makeDefaultTextStyle() {
		return this.makeTextStyle(DianaConstants.DEFAULT_TEXT_COLOR, DianaConstants.DEFAULT_TEXT_FONT);
	}

	/**
	 * Make a new text style, initialized with supplied font and color
	 * 
	 * @param aColor
	 * @param aFont
	 * 
	 * @return a newly created TextStyle
	 */
	public TextStyle makeTextStyle(final Color aColor, final Font aFont) {
		final TextStyle returned = this.newInstance(TextStyle.class);
		returned.setFactory(this);
		returned.setFont(aFont);
		returned.setColor(aColor);
		return returned;
	}

	/**
	 * Make a new border, initialized with default values as in DianaConstants
	 * 
	 * @return a newly created ShapeBorder
	 */
	/*public ShapeBorder makeShapeBorder() {
		final ShapeBorder returned = this.newInstance(ShapeBorder.class);
		returned.setFactory(this);
		returned.setTop(DianaConstants.DEFAULT_BORDER_SIZE);
		returned.setBottom(DianaConstants.DEFAULT_BORDER_SIZE);
		returned.setLeft(DianaConstants.DEFAULT_BORDER_SIZE);
		returned.setRight(DianaConstants.DEFAULT_BORDER_SIZE);
		return returned;
	}*/

	/**
	 * Make a new border, initialized with supplied values
	 * 
	 * @return a newly created ShapeBorder
	 */
	/*public ShapeBorder makeShapeBorder(final int top, final int bottom, final int left, final int right) {
		final ShapeBorder returned = this.newInstance(ShapeBorder.class);
		returned.setFactory(this);
		returned.setTop(top);
		returned.setBottom(bottom);
		returned.setLeft(left);
		returned.setRight(right);
		return returned;
	}*/

	/**
	 * Make a new border, initialized with an other border
	 * 
	 * @return a newly created ShapeBorder
	 */
	/*public ShapeBorder makeShapeBorder(final ShapeBorder border) {
		final ShapeBorder returned = this.newInstance(ShapeBorder.class);
		returned.setFactory(this);
		returned.setTop(border.getTop());
		returned.setBottom(border.getBottom());
		returned.setLeft(border.getLeft());
		returned.setRight(border.getRight());
		return returned;
	}*/

	/**
	 * Make a new ShapeSpecification from corresponding ShapeType
	 * 
	 * @param type
	 * @return a newly created ShapeSpecification
	 */
	public ShapeSpecification makeShape(final ShapeType type) {
		ShapeSpecification returned = null;
		switch (type) {
			case SQUARE:
				returned = this.newInstance(Square.class);
				break;
			case RECTANGLE:
				returned = this.newInstance(Rectangle.class);
				break;
			case PARALLELOGRAM:
				returned = this.newInstance(Parallelogram.class);
				break;
			case TRIANGLE:
				returned = this.newInstance(Triangle.class);
				break;
			case ARC:
				returned = this.newInstance(Arc.class);
				break;
			case CHEVRON:
				returned = this.newInstance(Chevron.class);
				break;
			case CIRCLE:
				returned = this.newInstance(Circle.class);
				break;
			case COMPLEX_CURVE:
				returned = this.newInstance(ComplexCurve.class);
				break;
			case CUSTOM_POLYGON:
				returned = this.newInstance(Polygon.class);
				break;
			case LOSANGE:
				returned = this.newInstance(Losange.class);
				break;
			case OVAL:
				returned = this.newInstance(Oval.class);
				break;
			case PLUS:
				returned = this.newInstance(Plus.class);
				break;
			case POLYGON:
				returned = this.newInstance(RegularPolygon.class);
				break;
			case RECTANGULAROCTOGON:
				returned = this.newInstance(RectangularOctogon.class);
				break;
			case STAR:
				returned = this.newInstance(Star.class);
				break;
			default:
				LOGGER.warning("Unexpected ShapeType: " + type);
				break;
		}

		if (returned != null) {
			returned.setFactory(this);
		}

		return returned;
	}

	/**
	 * Make a new Polygon with supplied polygon
	 * 
	 * @param aGraphicalRepresentation
	 * @param aPolygon
	 * @return a newly created Polygon
	 */
	public Polygon makePolygon(final DianaPolygon aPolygon) {
		final Polygon polygon = this.newInstance(Polygon.class);
		for (final DianaPoint pt : aPolygon.getPoints()) {
			polygon.addToPoints(pt);
		}
		return polygon;
	}

	/**
	 * Make a new Polygon with supplied points
	 * 
	 * @param aGraphicalRepresentation
	 * @param aPolygon
	 * @return a newly created Polygon
	 */
	public Polygon makePolygon(final DianaPoint... points) {
		final Polygon polygon = this.newInstance(Polygon.class);
		for (final DianaPoint pt : points) {
			polygon.addToPoints(pt);
		}
		return polygon;
	}

	/**
	 * Make a new ComplexCurve with supplied curve
	 * 
	 * @param aCurve
	 * 
	 * @return a newly created ComplexCurve
	 */
	public ComplexCurve makeComplexCurve(final DianaComplexCurve aCurve) {
		final ComplexCurve curve = this.newInstance(ComplexCurve.class);
		for (final DianaPoint pt : aCurve.getPoints()) {
			curve.addToPoints(pt);
		}
		curve.setClosure(aCurve.getClosure());
		return curve;
	}

	/**
	 * Make a new ComplexCurve with supplied points
	 * 
	 * @param points
	 * 
	 * @return a newly created ComplexCurve
	 */
	public ComplexCurve makeComplexCurve(final DianaPoint... points) {
		final ComplexCurve curve = this.newInstance(ComplexCurve.class);
		for (final DianaPoint pt : points) {
			curve.addToPoints(pt);
		}
		return curve;
	}

	/**
	 * Instanciate a new instance with parameters defining whether default properties and/or defaut basic controls should be assigned to
	 * newly created instances for GraphicalRepresentation instances
	 * 
	 * 
	 * @param implementedInterface
	 * @param initWithDefaultProperties
	 * @param initWithBasicControls
	 * @return
	 */
	public <I extends GraphicalRepresentation> I newInstance(final Class<I> implementedInterface, final boolean initWithDefaultProperties,
			final boolean initWithBasicControls) {
		final I returned = super.newInstance(implementedInterface);
		if (returned instanceof ShapeGraphicalRepresentation) {
			if (initWithDefaultProperties) {
				this.applyDefaultProperties((ShapeGraphicalRepresentation) returned);
			}
			if (initWithBasicControls) {
				this.applyBasicControls((ShapeGraphicalRepresentation) returned);
			}
		}
		else if (returned instanceof ConnectorGraphicalRepresentation) {
			if (initWithDefaultProperties) {
				this.applyDefaultProperties((ConnectorGraphicalRepresentation) returned);
			}
			if (initWithBasicControls) {
				this.applyBasicControls((ConnectorGraphicalRepresentation) returned);
			}
		}
		else if (returned instanceof DrawingGraphicalRepresentation) {
			if (initWithDefaultProperties) {
				this.applyDefaultProperties((DrawingGraphicalRepresentation) returned);
			}
			if (initWithBasicControls) {
				this.applyBasicControls((DrawingGraphicalRepresentation) returned);
			}
		}
		else if (returned instanceof GeometricGraphicalRepresentation) {
			if (initWithDefaultProperties) {
				this.applyDefaultProperties((GeometricGraphicalRepresentation) returned);
			}
			if (initWithBasicControls) {
				this.applyBasicControls((GeometricGraphicalRepresentation) returned);
			}
		}
		return returned;
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseClickControl(final String aName, final MouseButton button,
			final int clickCount) {
		return this.makeMouseClickControl(aName, button, clickCount, false, false, false, false);
	}

	public <E extends DianaEditor<?>> MouseClickControl<E> makeMouseClickControl(final String aName, final MouseButton button,
			final int clickCount, final MouseClickControlAction<E> action) {
		return this.makeMouseClickControl(aName, button, clickCount, action, false, false, false, false);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseShiftClickControl(final String aName, final MouseButton button,
			final int clickCount) {
		return this.makeMouseClickControl(aName, button, clickCount, true, false, false, false);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseControlClickControl(final String aName, final MouseButton button,
			final int clickCount) {
		return this.makeMouseClickControl(aName, button, clickCount, false, true, false, false);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseMetaClickControl(final String aName, final MouseButton button,
			final int clickCount) {
		return this.makeMouseClickControl(aName, button, clickCount, false, false, true, false);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseAltClickControl(final String aName, final MouseButton button,
			final int clickCount) {
		return this.makeMouseClickControl(aName, button, clickCount, false, false, false, true);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseClickControl(final String aName, final MouseButton button,
			final int clickCount, final PredefinedMouseClickControlActionType actionType) {
		return this.makeMouseClickControl(aName, button, clickCount, actionType, false, false, false, false);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseShiftClickControl(final String aName, final MouseButton button,
			final int clickCount, final PredefinedMouseClickControlActionType actionType) {
		return this.makeMouseClickControl(aName, button, clickCount, actionType, true, false, false, false);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseControlClickControl(final String aName, final MouseButton button,
			final int clickCount, final PredefinedMouseClickControlActionType actionType) {
		return this.makeMouseClickControl(aName, button, clickCount, actionType, false, true, false, false);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseMetaClickControl(final String aName, final MouseButton button,
			final int clickCount, final PredefinedMouseClickControlActionType actionType) {
		return this.makeMouseClickControl(aName, button, clickCount, actionType, false, false, true, false);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseAltClickControl(final String aName, final MouseButton button,
			final int clickCount, final PredefinedMouseClickControlActionType actionType) {
		return this.makeMouseClickControl(aName, button, clickCount, actionType, false, false, false, true);
	}

	public abstract MouseClickControl<? extends DianaEditor<?>> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed);

	public abstract MouseClickControl<? extends DianaEditor<?>> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			PredefinedMouseClickControlActionType actionType, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed,
			boolean altPressed);

	public abstract <E extends DianaEditor<?>> MouseClickControl<E> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			MouseClickControlAction<E> action, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed);

	public MouseDragControl<? extends DianaEditor<?>> makeMouseDragControl(final String aName, final MouseButton button) {
		return this.makeMouseDragControl(aName, button, false, false, false, false);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseShiftDragControl(final String aName, final MouseButton button) {
		return this.makeMouseDragControl(aName, button, true, false, false, false);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseControlDragControl(final String aName, final MouseButton button) {
		return this.makeMouseDragControl(aName, button, false, true, false, false);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseMetaDragControl(final String aName, final MouseButton button) {
		return this.makeMouseDragControl(aName, button, false, false, true, false);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseAltDragControl(final String aName, final MouseButton button) {
		return this.makeMouseDragControl(aName, button, false, false, false, true);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseDragControl(final String aName, final MouseButton button,
			final PredefinedMouseDragControlActionType actionType) {
		return this.makeMouseDragControl(aName, button, actionType, false, false, false, false);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseShiftDragControl(final String aName, final MouseButton button,
			final PredefinedMouseDragControlActionType actionType) {
		return this.makeMouseDragControl(aName, button, actionType, true, false, false, false);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseControlDragControl(final String aName, final MouseButton button,
			final PredefinedMouseDragControlActionType actionType) {
		return this.makeMouseDragControl(aName, button, actionType, false, true, false, false);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseMetaDragControl(final String aName, final MouseButton button,
			final PredefinedMouseDragControlActionType actionType) {
		return this.makeMouseDragControl(aName, button, actionType, false, false, true, false);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseAltDragControl(final String aName, final MouseButton button,
			final PredefinedMouseDragControlActionType actionType) {
		return this.makeMouseDragControl(aName, button, actionType, false, false, false, true);
	}

	public abstract MouseDragControl<? extends DianaEditor<?>> makeMouseDragControl(String aName, MouseButton button, boolean shiftPressed,
			boolean ctrlPressed, boolean metaPressed, boolean altPressed);

	public abstract MouseDragControl<? extends DianaEditor<?>> makeMouseDragControl(String aName, MouseButton button,
			PredefinedMouseDragControlActionType actionType, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed,
			boolean altPressed);

	public abstract <E extends DianaEditor<?>> MouseDragControl<E> makeMouseDragControl(String aName, MouseButton button,
			MouseDragControlAction<E> action, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed);

	public abstract MouseDragControlAction<? extends DianaEditor<?>> makeMouseDragControlAction(
			PredefinedMouseDragControlActionType actionType);

	public abstract MouseClickControlAction<? extends DianaEditor<?>> makeMouseClickControlAction(
			PredefinedMouseClickControlActionType actionType);

}
