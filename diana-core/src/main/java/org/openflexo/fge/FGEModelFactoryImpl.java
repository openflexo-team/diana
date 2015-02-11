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

package org.openflexo.fge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.connectors.CurveConnectorSpecification;
import org.openflexo.fge.connectors.CurvedPolylinConnectorSpecification;
import org.openflexo.fge.connectors.LineConnectorSpecification;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification;
import org.openflexo.fge.connectors.impl.ConnectorSpecificationImpl;
import org.openflexo.fge.connectors.impl.CurveConnectorSpecificationImpl;
import org.openflexo.fge.connectors.impl.CurvedPolylinConnectorSpecificationImpl;
import org.openflexo.fge.connectors.impl.LineConnectorSpecificationImpl;
import org.openflexo.fge.connectors.impl.RectPolylinConnectorSpecificationImpl;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.control.MouseClickControl;
import org.openflexo.fge.control.MouseClickControlAction;
import org.openflexo.fge.control.MouseControl.MouseButton;
import org.openflexo.fge.control.MouseDragControl;
import org.openflexo.fge.control.MouseDragControlAction;
import org.openflexo.fge.control.PredefinedMouseClickControlActionType;
import org.openflexo.fge.control.PredefinedMouseDragControlActionType;
import org.openflexo.fge.control.actions.ContinuousSelectionAction;
import org.openflexo.fge.control.actions.MouseClickControlImpl;
import org.openflexo.fge.control.actions.MouseDragControlImpl;
import org.openflexo.fge.control.actions.MoveAction;
import org.openflexo.fge.control.actions.MultipleSelectionAction;
import org.openflexo.fge.control.actions.RectangleSelectingAction;
import org.openflexo.fge.control.actions.SelectionAction;
import org.openflexo.fge.control.actions.ZoomAction;
import org.openflexo.fge.impl.BackgroundImageBackgroundStyleImpl;
import org.openflexo.fge.impl.BackgroundStyleImpl;
import org.openflexo.fge.impl.ColorBackgroundStyleImpl;
import org.openflexo.fge.impl.ColorGradientBackgroundStyleImpl;
import org.openflexo.fge.impl.ConnectorGraphicalRepresentationImpl;
import org.openflexo.fge.impl.ContainerGraphicalRepresentationImpl;
import org.openflexo.fge.impl.DrawingGraphicalRepresentationImpl;
import org.openflexo.fge.impl.FGEStyleImpl;
import org.openflexo.fge.impl.ForegroundStyleImpl;
import org.openflexo.fge.impl.GeometricGraphicalRepresentationImpl;
import org.openflexo.fge.impl.GraphicalRepresentationImpl;
import org.openflexo.fge.impl.NoneBackgroundStyleImpl;
import org.openflexo.fge.impl.ShadowStyleImpl;
import org.openflexo.fge.impl.ShapeGraphicalRepresentationImpl;
import org.openflexo.fge.impl.ShapeGraphicalRepresentationImpl.ShapeBorderImpl;
import org.openflexo.fge.impl.TextStyleImpl;
import org.openflexo.fge.impl.TextureBackgroundStyleImpl;
import org.openflexo.fge.layout.GridLayoutManager;
import org.openflexo.fge.layout.GridLayoutManagerImpl;
import org.openflexo.fge.shapes.Arc;
import org.openflexo.fge.shapes.Chevron;
import org.openflexo.fge.shapes.Circle;
import org.openflexo.fge.shapes.ComplexCurve;
import org.openflexo.fge.shapes.Losange;
import org.openflexo.fge.shapes.Oval;
import org.openflexo.fge.shapes.Parallelogram;
import org.openflexo.fge.shapes.Plus;
import org.openflexo.fge.shapes.Polygon;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.RectangularOctogon;
import org.openflexo.fge.shapes.RegularPolygon;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.Square;
import org.openflexo.fge.shapes.Star;
import org.openflexo.fge.shapes.Triangle;
import org.openflexo.fge.shapes.impl.ArcImpl;
import org.openflexo.fge.shapes.impl.ChevronImpl;
import org.openflexo.fge.shapes.impl.CircleImpl;
import org.openflexo.fge.shapes.impl.ComplexCurveImpl;
import org.openflexo.fge.shapes.impl.LosangeImpl;
import org.openflexo.fge.shapes.impl.OvalImpl;
import org.openflexo.fge.shapes.impl.ParallelogramImpl;
import org.openflexo.fge.shapes.impl.PlusImpl;
import org.openflexo.fge.shapes.impl.PolygonImpl;
import org.openflexo.fge.shapes.impl.RectangleImpl;
import org.openflexo.fge.shapes.impl.RectangularOctogonImpl;
import org.openflexo.fge.shapes.impl.RegularPolygonImpl;
import org.openflexo.fge.shapes.impl.ShapeSpecificationImpl;
import org.openflexo.fge.shapes.impl.SquareImpl;
import org.openflexo.fge.shapes.impl.StarImpl;
import org.openflexo.fge.shapes.impl.TriangleImpl;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

public class FGEModelFactoryImpl extends FGEModelFactory {

	/**
	 * Creates a new model factory including all classes involved in FGE model
	 * 
	 * @throws ModelDefinitionException
	 */
	public FGEModelFactoryImpl() throws ModelDefinitionException {
		this(new ArrayList<Class<?>>());
	}

	/**
	 * Creates a new model factory including all supplied classes and all classes involved in FGE model
	 * 
	 * @throws ModelDefinitionException
	 */
	public FGEModelFactoryImpl(Class<?>... classes) throws ModelDefinitionException {
		this(Arrays.asList(classes));
	}

	/**
	 * Creates a new model factory including all supplied classes and all classes involved in FGE model
	 * 
	 * @throws ModelDefinitionException
	 */
	public FGEModelFactoryImpl(Collection<Class<?>> classes) throws ModelDefinitionException {

		super(classes);
	}

	@Override
	public void installImplementingClasses() throws ModelDefinitionException {

		installImplementingClasses(this);
	}

	public static void installImplementingClasses(ModelFactory modelFactory) throws ModelDefinitionException {

		modelFactory.setImplementingClassForInterface(GraphicalRepresentationImpl.class, GraphicalRepresentation.class);
		modelFactory.setImplementingClassForInterface(ShapeGraphicalRepresentationImpl.class, ShapeGraphicalRepresentation.class);
		modelFactory.setImplementingClassForInterface(ConnectorGraphicalRepresentationImpl.class, ConnectorGraphicalRepresentation.class);
		modelFactory.setImplementingClassForInterface(DrawingGraphicalRepresentationImpl.class, DrawingGraphicalRepresentation.class);
		modelFactory.setImplementingClassForInterface(ContainerGraphicalRepresentationImpl.class, ContainerGraphicalRepresentation.class);
		modelFactory.setImplementingClassForInterface(GeometricGraphicalRepresentationImpl.class, GeometricGraphicalRepresentation.class);

		modelFactory.setImplementingClassForInterface(ShapeBorderImpl.class, ShapeBorder.class);

		modelFactory.setImplementingClassForInterface(FGEStyleImpl.class, FGEStyle.class);
		modelFactory.setImplementingClassForInterface(ForegroundStyleImpl.class, ForegroundStyle.class);
		modelFactory.setImplementingClassForInterface(ShadowStyleImpl.class, ShadowStyle.class);
		modelFactory.setImplementingClassForInterface(TextStyleImpl.class, TextStyle.class);
		modelFactory.setImplementingClassForInterface(BackgroundStyleImpl.class, BackgroundStyle.class);
		modelFactory.setImplementingClassForInterface(NoneBackgroundStyleImpl.class, NoneBackgroundStyle.class);
		modelFactory.setImplementingClassForInterface(ColorBackgroundStyleImpl.class, ColorBackgroundStyle.class);
		modelFactory.setImplementingClassForInterface(ColorGradientBackgroundStyleImpl.class, ColorGradientBackgroundStyle.class);
		modelFactory.setImplementingClassForInterface(TextureBackgroundStyleImpl.class, TextureBackgroundStyle.class);
		modelFactory.setImplementingClassForInterface(BackgroundImageBackgroundStyleImpl.class, BackgroundImageBackgroundStyle.class);

		modelFactory.setImplementingClassForInterface(ShapeSpecificationImpl.class, ShapeSpecification.class);
		modelFactory.setImplementingClassForInterface(ArcImpl.class, Arc.class);
		modelFactory.setImplementingClassForInterface(CircleImpl.class, Circle.class);
		modelFactory.setImplementingClassForInterface(LosangeImpl.class, Losange.class);
		modelFactory.setImplementingClassForInterface(OvalImpl.class, Oval.class);
		modelFactory.setImplementingClassForInterface(PolygonImpl.class, Polygon.class);
		modelFactory.setImplementingClassForInterface(RectangleImpl.class, Rectangle.class);
		modelFactory.setImplementingClassForInterface(RectangularOctogonImpl.class, RectangularOctogon.class);
		modelFactory.setImplementingClassForInterface(RegularPolygonImpl.class, RegularPolygon.class);
		modelFactory.setImplementingClassForInterface(SquareImpl.class, Square.class);
		modelFactory.setImplementingClassForInterface(StarImpl.class, Star.class);
		modelFactory.setImplementingClassForInterface(TriangleImpl.class, Triangle.class);
		modelFactory.setImplementingClassForInterface(ComplexCurveImpl.class, ComplexCurve.class);
		modelFactory.setImplementingClassForInterface(PlusImpl.class, Plus.class);
		modelFactory.setImplementingClassForInterface(ChevronImpl.class, Chevron.class);
		modelFactory.setImplementingClassForInterface(ParallelogramImpl.class, Parallelogram.class);

		modelFactory.setImplementingClassForInterface(ConnectorSpecificationImpl.class, ConnectorSpecification.class);
		modelFactory.setImplementingClassForInterface(LineConnectorSpecificationImpl.class, LineConnectorSpecification.class);
		modelFactory.setImplementingClassForInterface(CurveConnectorSpecificationImpl.class, CurveConnectorSpecification.class);
		modelFactory.setImplementingClassForInterface(RectPolylinConnectorSpecificationImpl.class, RectPolylinConnectorSpecification.class);
		modelFactory.setImplementingClassForInterface(CurvedPolylinConnectorSpecificationImpl.class,
				CurvedPolylinConnectorSpecification.class);

		// Layout managers
		modelFactory.setImplementingClassForInterface(GridLayoutManagerImpl.class, GridLayoutManager.class);

	}

	@Override
	public MouseClickControl<AbstractDianaEditor<?, ?, ?>> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new MouseClickControlImpl<AbstractDianaEditor<?, ?, ?>>(aName, button, clickCount, null, shiftPressed, ctrlPressed,
				metaPressed, altPressed, getEditingContext());
	}

	@SuppressWarnings("unchecked")
	@Override
	public MouseClickControl<AbstractDianaEditor<?, ?, ?>> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			PredefinedMouseClickControlActionType actionType, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed,
			boolean altPressed) {
		return new MouseClickControlImpl<AbstractDianaEditor<?, ?, ?>>(aName, button, clickCount,
				(MouseClickControlAction<AbstractDianaEditor<?, ?, ?>>) makeMouseClickControlAction(actionType), shiftPressed, ctrlPressed,
				metaPressed, altPressed, getEditingContext());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends DianaEditor<?>> MouseClickControl<E> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			MouseClickControlAction<E> action, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		// Little compiling hack to "cast" generics E (the goal is to change upper bounds of E from DianaEditor<?> to AbstractDianaEditor<?,
		// ?, ?>)
		return (MouseClickControl<E>) makeMouseClickControl2(aName, button, clickCount,
				(MouseClickControlAction<AbstractDianaEditor<?, ?, ?>>) action, shiftPressed, ctrlPressed, metaPressed, altPressed);
	}

	// Little compiling hack to "cast" generics E (the goal is to change upper bounds of E from DianaEditor<?> to AbstractDianaEditor<?, ?,
	// ?>)
	private <E2 extends AbstractDianaEditor<?, ?, ?>> MouseClickControl<E2> makeMouseClickControl2(String aName, MouseButton button,
			int clickCount, MouseClickControlAction<E2> action, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed,
			boolean altPressed) {
		return new MouseClickControlImpl<E2>(aName, button, clickCount, action, shiftPressed, ctrlPressed, metaPressed, altPressed,
				getEditingContext());
	}

	@Override
	public MouseDragControl<? extends AbstractDianaEditor<?, ?, ?>> makeMouseDragControl(String aName, MouseButton button,
			boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new MouseDragControlImpl<AbstractDianaEditor<?, ?, ?>>(aName, button, null, shiftPressed, ctrlPressed, metaPressed,
				altPressed, getEditingContext());
	}

	@SuppressWarnings("unchecked")
	@Override
	public MouseDragControl<AbstractDianaEditor<?, ?, ?>> makeMouseDragControl(String aName, MouseButton button,
			PredefinedMouseDragControlActionType actionType, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed,
			boolean altPressed) {
		return new MouseDragControlImpl<AbstractDianaEditor<?, ?, ?>>(aName, button,
				(MouseDragControlAction<AbstractDianaEditor<?, ?, ?>>) makeMouseDragControlAction(actionType), shiftPressed, ctrlPressed,
				metaPressed, altPressed, getEditingContext());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends DianaEditor<?>> MouseDragControl<E> makeMouseDragControl(String aName, MouseButton button,
			MouseDragControlAction<E> action, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		// Little compiling hack to "cast" generics E (the goal is to change upper bounds of E from DianaEditor<?> to AbstractDianaEditor<?,
		// ?, ?>)
		return (MouseDragControl<E>) makeMouseDragControl2(aName, button, (MouseDragControlAction<AbstractDianaEditor<?, ?, ?>>) action,
				shiftPressed, ctrlPressed, metaPressed, altPressed);
	}

	// Little compiling hack to "cast" generics E (the goal is to change upper bounds of E from DianaEditor<?> to AbstractDianaEditor<?, ?,
	// ?>)
	private <E2 extends AbstractDianaEditor<?, ?, ?>> MouseDragControl<E2> makeMouseDragControl2(String aName, MouseButton button,
			MouseDragControlAction<E2> action, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new MouseDragControlImpl<E2>(aName, button, action, shiftPressed, ctrlPressed, metaPressed, altPressed, getEditingContext());
	}

	@Override
	public MouseDragControlAction<? extends AbstractDianaEditor<?, ?, ?>> makeMouseDragControlAction(
			PredefinedMouseDragControlActionType actionType) {
		switch (actionType) {
		case MOVE:
			return new MoveAction();
		case RECTANGLE_SELECTING:
			return new RectangleSelectingAction();
		case ZOOM:
			return new ZoomAction();
		default:
			LOGGER.warning("Unexpected actionType " + actionType);
			return null;
		}
	}

	@Override
	public MouseClickControlAction<? extends AbstractDianaEditor<?, ?, ?>> makeMouseClickControlAction(
			PredefinedMouseClickControlActionType actionType) {
		switch (actionType) {
		case SELECTION:
			return new SelectionAction();
		case CONTINUOUS_SELECTION:
			return new ContinuousSelectionAction();
		case MULTIPLE_SELECTION:
			return new MultipleSelectionAction();
		default:
			LOGGER.warning("Unexpected actionType " + actionType);
			return null;
		}
	}

}
