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

package org.openflexo.diana;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.openflexo.diana.connectors.ConnectorSpecification;
import org.openflexo.diana.connectors.CurveConnectorSpecification;
import org.openflexo.diana.connectors.CurvedPolylinConnectorSpecification;
import org.openflexo.diana.connectors.LineConnectorSpecification;
import org.openflexo.diana.connectors.RectPolylinConnectorSpecification;
import org.openflexo.diana.connectors.impl.ConnectorSpecificationImpl;
import org.openflexo.diana.connectors.impl.CurveConnectorSpecificationImpl;
import org.openflexo.diana.connectors.impl.CurvedPolylinConnectorSpecificationImpl;
import org.openflexo.diana.connectors.impl.LineConnectorSpecificationImpl;
import org.openflexo.diana.connectors.impl.RectPolylinConnectorSpecificationImpl;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.DianaEditor;
import org.openflexo.diana.control.MouseClickControl;
import org.openflexo.diana.control.MouseClickControlAction;
import org.openflexo.diana.control.MouseControl.MouseButton;
import org.openflexo.diana.control.MouseDragControl;
import org.openflexo.diana.control.MouseDragControlAction;
import org.openflexo.diana.control.PredefinedMouseClickControlActionType;
import org.openflexo.diana.control.PredefinedMouseDragControlActionType;
import org.openflexo.diana.control.actions.ContinuousSelectionAction;
import org.openflexo.diana.control.actions.MouseClickControlImpl;
import org.openflexo.diana.control.actions.MouseDragControlImpl;
import org.openflexo.diana.control.actions.MoveAction;
import org.openflexo.diana.control.actions.MultipleSelectionAction;
import org.openflexo.diana.control.actions.RectangleSelectingAction;
import org.openflexo.diana.control.actions.SelectionAction;
import org.openflexo.diana.control.actions.ZoomAction;
import org.openflexo.diana.impl.BackgroundImageBackgroundStyleImpl;
import org.openflexo.diana.impl.BackgroundStyleImpl;
import org.openflexo.diana.impl.ColorBackgroundStyleImpl;
import org.openflexo.diana.impl.ColorGradientBackgroundStyleImpl;
import org.openflexo.diana.impl.ConnectorGraphicalRepresentationImpl;
import org.openflexo.diana.impl.ContainerGraphicalRepresentationImpl;
import org.openflexo.diana.impl.DianaStyleImpl;
import org.openflexo.diana.impl.DrawingGraphicalRepresentationImpl;
import org.openflexo.diana.impl.ForegroundStyleImpl;
import org.openflexo.diana.impl.GeometricGraphicalRepresentationImpl;
import org.openflexo.diana.impl.GraphicalRepresentationImpl;
import org.openflexo.diana.impl.NoneBackgroundStyleImpl;
import org.openflexo.diana.impl.PaletteElementSpecificationImpl;
import org.openflexo.diana.impl.ShadowStyleImpl;
import org.openflexo.diana.impl.ShapeGraphicalRepresentationImpl;
import org.openflexo.diana.impl.TextStyleImpl;
import org.openflexo.diana.impl.TextureBackgroundStyleImpl;
import org.openflexo.diana.layout.BalloonLayoutManager;
import org.openflexo.diana.layout.BalloonLayoutManagerSpecification;
import org.openflexo.diana.layout.FlowLayoutManager;
import org.openflexo.diana.layout.FlowLayoutManagerSpecification;
import org.openflexo.diana.layout.ForceDirectedGraphLayoutManager;
import org.openflexo.diana.layout.ForceDirectedGraphLayoutManagerSpecification;
import org.openflexo.diana.layout.GridLayoutManager;
import org.openflexo.diana.layout.GridLayoutManagerSpecification;
import org.openflexo.diana.layout.ISOMGraphLayoutManager;
import org.openflexo.diana.layout.ISOMGraphLayoutManagerSpecification;
import org.openflexo.diana.layout.OutlineLayoutManager;
import org.openflexo.diana.layout.OutlineLayoutManagerSpecification;
import org.openflexo.diana.layout.RadialTreeLayoutManager;
import org.openflexo.diana.layout.RadialTreeLayoutManagerSpecification;
import org.openflexo.diana.layout.TreeLayoutManager;
import org.openflexo.diana.layout.TreeLayoutManagerSpecification;
import org.openflexo.diana.layout.impl.BalloonLayoutManagerImpl;
import org.openflexo.diana.layout.impl.BalloonLayoutManagerSpecificationImpl;
import org.openflexo.diana.layout.impl.FlowLayoutManagerImpl;
import org.openflexo.diana.layout.impl.FlowLayoutManagerSpecificationImpl;
import org.openflexo.diana.layout.impl.ForceDirectedGraphLayoutManagerImpl;
import org.openflexo.diana.layout.impl.ForceDirectedGraphLayoutManagerSpecificationImpl;
import org.openflexo.diana.layout.impl.GridLayoutManagerImpl;
import org.openflexo.diana.layout.impl.GridLayoutManagerSpecificationImpl;
import org.openflexo.diana.layout.impl.ISOMGraphLayoutManagerImpl;
import org.openflexo.diana.layout.impl.ISOMGraphLayoutManagerSpecificationImpl;
import org.openflexo.diana.layout.impl.OutlineLayoutManagerImpl;
import org.openflexo.diana.layout.impl.OutlineLayoutManagerSpecificationImpl;
import org.openflexo.diana.layout.impl.RadialTreeLayoutManagerImpl;
import org.openflexo.diana.layout.impl.RadialTreeLayoutManagerSpecificationImpl;
import org.openflexo.diana.layout.impl.TreeLayoutManagerImpl;
import org.openflexo.diana.layout.impl.TreeLayoutManagerSpecificationImpl;
import org.openflexo.diana.shapes.Arc;
import org.openflexo.diana.shapes.Chevron;
import org.openflexo.diana.shapes.Circle;
import org.openflexo.diana.shapes.ComplexCurve;
import org.openflexo.diana.shapes.GeneralShape;
import org.openflexo.diana.shapes.GeneralShape.CubicCurvePathElement;
import org.openflexo.diana.shapes.GeneralShape.GeneralShapePathElement;
import org.openflexo.diana.shapes.GeneralShape.QuadCurvePathElement;
import org.openflexo.diana.shapes.GeneralShape.SegmentPathElement;
import org.openflexo.diana.shapes.Losange;
import org.openflexo.diana.shapes.Oval;
import org.openflexo.diana.shapes.Parallelogram;
import org.openflexo.diana.shapes.Plus;
import org.openflexo.diana.shapes.Polygon;
import org.openflexo.diana.shapes.Rectangle;
import org.openflexo.diana.shapes.RectangularOctogon;
import org.openflexo.diana.shapes.RegularPolygon;
import org.openflexo.diana.shapes.ShapeSpecification;
import org.openflexo.diana.shapes.ShapeUnion;
import org.openflexo.diana.shapes.Square;
import org.openflexo.diana.shapes.Star;
import org.openflexo.diana.shapes.Triangle;
import org.openflexo.diana.shapes.impl.ArcImpl;
import org.openflexo.diana.shapes.impl.ChevronImpl;
import org.openflexo.diana.shapes.impl.CircleImpl;
import org.openflexo.diana.shapes.impl.ComplexCurveImpl;
import org.openflexo.diana.shapes.impl.GeneralShapeImpl;
import org.openflexo.diana.shapes.impl.GeneralShapeImpl.CubicCurvePathElementImpl;
import org.openflexo.diana.shapes.impl.GeneralShapeImpl.GeneralShapePathElementImpl;
import org.openflexo.diana.shapes.impl.GeneralShapeImpl.QuadCurvePathElementImpl;
import org.openflexo.diana.shapes.impl.GeneralShapeImpl.SegmentPathElementImpl;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.PamelaModelFactory;
import org.openflexo.diana.shapes.impl.LosangeImpl;
import org.openflexo.diana.shapes.impl.OvalImpl;
import org.openflexo.diana.shapes.impl.ParallelogramImpl;
import org.openflexo.diana.shapes.impl.PlusImpl;
import org.openflexo.diana.shapes.impl.PolygonImpl;
import org.openflexo.diana.shapes.impl.RectangleImpl;
import org.openflexo.diana.shapes.impl.RectangularOctogonImpl;
import org.openflexo.diana.shapes.impl.RegularPolygonImpl;
import org.openflexo.diana.shapes.impl.ShapeSpecificationImpl;
import org.openflexo.diana.shapes.impl.ShapeUnionImpl;
import org.openflexo.diana.shapes.impl.SquareImpl;
import org.openflexo.diana.shapes.impl.StarImpl;
import org.openflexo.diana.shapes.impl.TriangleImpl;

public class DianaModelFactoryImpl extends DianaModelFactory {

	/**
	 * Creates a new model factory including all classes involved in Diana model
	 * 
	 * @throws ModelDefinitionException
	 */
	public DianaModelFactoryImpl() throws ModelDefinitionException {
		this(Collections.emptyList());
	}

	/**
	 * Creates a new model factory including all supplied classes and all classes involved in Diana model
	 * 
	 * @throws ModelDefinitionException
	 */
	public DianaModelFactoryImpl(Class<?>... classes) throws ModelDefinitionException {
		this(Arrays.asList(classes));
	}

	/**
	 * Creates a new model factory including all supplied classes and all classes involved in Diana model
	 * 
	 * @throws ModelDefinitionException
	 */
	public DianaModelFactoryImpl(Collection<Class<?>> classes) throws ModelDefinitionException {

		super(classes);
	}

	@Override
	public void installImplementingClasses() throws ModelDefinitionException {

		installImplementingClasses(this);
	}

	public static void installImplementingClasses(PamelaModelFactory pamelaModelFactory) throws ModelDefinitionException {

		pamelaModelFactory.setImplementingClassForInterface(GraphicalRepresentationImpl.class, GraphicalRepresentation.class);
		pamelaModelFactory.setImplementingClassForInterface(ShapeGraphicalRepresentationImpl.class, ShapeGraphicalRepresentation.class);
		pamelaModelFactory.setImplementingClassForInterface(ConnectorGraphicalRepresentationImpl.class, ConnectorGraphicalRepresentation.class);
		pamelaModelFactory.setImplementingClassForInterface(DrawingGraphicalRepresentationImpl.class, DrawingGraphicalRepresentation.class);
		pamelaModelFactory.setImplementingClassForInterface(ContainerGraphicalRepresentationImpl.class, ContainerGraphicalRepresentation.class);
		pamelaModelFactory.setImplementingClassForInterface(GeometricGraphicalRepresentationImpl.class, GeometricGraphicalRepresentation.class);

		// modelFactory.setImplementingClassForInterface(ShapeBorderImpl.class, ShapeBorder.class);

		pamelaModelFactory.setImplementingClassForInterface(DianaStyleImpl.class, DianaStyle.class);
		pamelaModelFactory.setImplementingClassForInterface(ForegroundStyleImpl.class, ForegroundStyle.class);
		pamelaModelFactory.setImplementingClassForInterface(ShadowStyleImpl.class, ShadowStyle.class);
		pamelaModelFactory.setImplementingClassForInterface(TextStyleImpl.class, TextStyle.class);
		pamelaModelFactory.setImplementingClassForInterface(BackgroundStyleImpl.class, BackgroundStyle.class);
		pamelaModelFactory.setImplementingClassForInterface(NoneBackgroundStyleImpl.class, NoneBackgroundStyle.class);
		pamelaModelFactory.setImplementingClassForInterface(ColorBackgroundStyleImpl.class, ColorBackgroundStyle.class);
		pamelaModelFactory.setImplementingClassForInterface(ColorGradientBackgroundStyleImpl.class, ColorGradientBackgroundStyle.class);
		pamelaModelFactory.setImplementingClassForInterface(TextureBackgroundStyleImpl.class, TextureBackgroundStyle.class);
		pamelaModelFactory.setImplementingClassForInterface(BackgroundImageBackgroundStyleImpl.class, BackgroundImageBackgroundStyle.class);

		pamelaModelFactory.setImplementingClassForInterface(ShapeSpecificationImpl.class, ShapeSpecification.class);
		pamelaModelFactory.setImplementingClassForInterface(ArcImpl.class, Arc.class);
		pamelaModelFactory.setImplementingClassForInterface(CircleImpl.class, Circle.class);
		pamelaModelFactory.setImplementingClassForInterface(LosangeImpl.class, Losange.class);
		pamelaModelFactory.setImplementingClassForInterface(OvalImpl.class, Oval.class);
		pamelaModelFactory.setImplementingClassForInterface(PolygonImpl.class, Polygon.class);
		pamelaModelFactory.setImplementingClassForInterface(RectangleImpl.class, Rectangle.class);
		pamelaModelFactory.setImplementingClassForInterface(RectangularOctogonImpl.class, RectangularOctogon.class);
		pamelaModelFactory.setImplementingClassForInterface(RegularPolygonImpl.class, RegularPolygon.class);
		pamelaModelFactory.setImplementingClassForInterface(SquareImpl.class, Square.class);
		pamelaModelFactory.setImplementingClassForInterface(StarImpl.class, Star.class);
		pamelaModelFactory.setImplementingClassForInterface(TriangleImpl.class, Triangle.class);
		pamelaModelFactory.setImplementingClassForInterface(ComplexCurveImpl.class, ComplexCurve.class);
		pamelaModelFactory.setImplementingClassForInterface(GeneralShapeImpl.class, GeneralShape.class);
		pamelaModelFactory.setImplementingClassForInterface(GeneralShapePathElementImpl.class, GeneralShapePathElement.class);
		pamelaModelFactory.setImplementingClassForInterface(SegmentPathElementImpl.class, SegmentPathElement.class);
		pamelaModelFactory.setImplementingClassForInterface(QuadCurvePathElementImpl.class, QuadCurvePathElement.class);
		pamelaModelFactory.setImplementingClassForInterface(CubicCurvePathElementImpl.class, CubicCurvePathElement.class);
		pamelaModelFactory.setImplementingClassForInterface(PlusImpl.class, Plus.class);
		pamelaModelFactory.setImplementingClassForInterface(ChevronImpl.class, Chevron.class);
		pamelaModelFactory.setImplementingClassForInterface(ParallelogramImpl.class, Parallelogram.class);
		pamelaModelFactory.setImplementingClassForInterface(ShapeUnionImpl.class, ShapeUnion.class);

		pamelaModelFactory.setImplementingClassForInterface(ConnectorSpecificationImpl.class, ConnectorSpecification.class);
		pamelaModelFactory.setImplementingClassForInterface(LineConnectorSpecificationImpl.class, LineConnectorSpecification.class);
		pamelaModelFactory.setImplementingClassForInterface(CurveConnectorSpecificationImpl.class, CurveConnectorSpecification.class);
		pamelaModelFactory.setImplementingClassForInterface(RectPolylinConnectorSpecificationImpl.class, RectPolylinConnectorSpecification.class);
		pamelaModelFactory.setImplementingClassForInterface(CurvedPolylinConnectorSpecificationImpl.class,
				CurvedPolylinConnectorSpecification.class);

		// Layout managers
		pamelaModelFactory.setImplementingClassForInterface(FlowLayoutManagerImpl.class, FlowLayoutManager.class);
		pamelaModelFactory.setImplementingClassForInterface(FlowLayoutManagerSpecificationImpl.class, FlowLayoutManagerSpecification.class);
		pamelaModelFactory.setImplementingClassForInterface(GridLayoutManagerImpl.class, GridLayoutManager.class);
		pamelaModelFactory.setImplementingClassForInterface(GridLayoutManagerSpecificationImpl.class, GridLayoutManagerSpecification.class);
		pamelaModelFactory.setImplementingClassForInterface(OutlineLayoutManagerImpl.class, OutlineLayoutManager.class);
		pamelaModelFactory.setImplementingClassForInterface(OutlineLayoutManagerSpecificationImpl.class, OutlineLayoutManagerSpecification.class);
		pamelaModelFactory.setImplementingClassForInterface(ForceDirectedGraphLayoutManagerImpl.class, ForceDirectedGraphLayoutManager.class);
		pamelaModelFactory.setImplementingClassForInterface(ForceDirectedGraphLayoutManagerSpecificationImpl.class,
				ForceDirectedGraphLayoutManagerSpecification.class);
		pamelaModelFactory.setImplementingClassForInterface(ISOMGraphLayoutManagerImpl.class, ISOMGraphLayoutManager.class);
		pamelaModelFactory.setImplementingClassForInterface(ISOMGraphLayoutManagerSpecificationImpl.class,
				ISOMGraphLayoutManagerSpecification.class);
		pamelaModelFactory.setImplementingClassForInterface(TreeLayoutManagerImpl.class, TreeLayoutManager.class);
		pamelaModelFactory.setImplementingClassForInterface(TreeLayoutManagerSpecificationImpl.class, TreeLayoutManagerSpecification.class);
		pamelaModelFactory.setImplementingClassForInterface(BalloonLayoutManagerImpl.class, BalloonLayoutManager.class);
		pamelaModelFactory.setImplementingClassForInterface(BalloonLayoutManagerSpecificationImpl.class, BalloonLayoutManagerSpecification.class);
		pamelaModelFactory.setImplementingClassForInterface(RadialTreeLayoutManagerImpl.class, RadialTreeLayoutManager.class);
		pamelaModelFactory.setImplementingClassForInterface(RadialTreeLayoutManagerSpecificationImpl.class,
				RadialTreeLayoutManagerSpecification.class);

		// PaletteElementSpecification
		pamelaModelFactory.setImplementingClassForInterface(PaletteElementSpecificationImpl.class, PaletteElementSpecification.class);

	}

	@Override
	public MouseClickControl<AbstractDianaEditor<?, ?, ?>> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new MouseClickControlImpl<>(aName, button, clickCount, null, shiftPressed, ctrlPressed, metaPressed, altPressed,
				getEditingContext());
	}

	@SuppressWarnings("unchecked")
	@Override
	public MouseClickControl<AbstractDianaEditor<?, ?, ?>> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			PredefinedMouseClickControlActionType actionType, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed,
			boolean altPressed) {
		return new MouseClickControlImpl<>(aName, button, clickCount,
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
		return new MouseClickControlImpl<>(aName, button, clickCount, action, shiftPressed, ctrlPressed, metaPressed, altPressed,
				getEditingContext());
	}

	@Override
	public MouseDragControl<? extends AbstractDianaEditor<?, ?, ?>> makeMouseDragControl(String aName, MouseButton button,
			boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new MouseDragControlImpl<>(aName, button, null, shiftPressed, ctrlPressed, metaPressed, altPressed, getEditingContext());
	}

	@SuppressWarnings("unchecked")
	@Override
	public MouseDragControl<AbstractDianaEditor<?, ?, ?>> makeMouseDragControl(String aName, MouseButton button,
			PredefinedMouseDragControlActionType actionType, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed,
			boolean altPressed) {
		return new MouseDragControlImpl<>(aName, button,
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
		return new MouseDragControlImpl<>(aName, button, action, shiftPressed, ctrlPressed, metaPressed, altPressed, getEditingContext());
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
