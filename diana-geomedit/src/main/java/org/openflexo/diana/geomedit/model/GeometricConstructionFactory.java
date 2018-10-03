/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-drawing-editor, a component of the software infrastructure 
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

import java.util.List;

import org.openflexo.diana.geomedit.controller.ShowGeneralContextualMenuControl;
import org.openflexo.diana.geomedit.model.gr.GeometricDrawingGraphicalRepresentation;
import org.openflexo.diana.geomedit.model.gr.GeometricObjectGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.EditingContext;
import org.openflexo.model.undo.CompoundEdit;
import org.openflexo.model.undo.UndoManager;

public class GeometricConstructionFactory extends FGEModelFactoryImpl {

	private int objectIndex = 0;

	public GeometricConstructionFactory(EditingContext editingContext) throws ModelDefinitionException {
		super(GeometricDiagram.class, GeometricObjectGraphicalRepresentation.class);
		setEditingContext(editingContext);
	}

	public UndoManager getUndoManager() {
		if (getEditingContext() != null) {
			return getEditingContext().getUndoManager();
		}
		return null;
	}

	public GeometricDiagram makeNewGeometricDiagram() {
		CompoundEdit edit = getUndoManager().startRecording("Create new geometric diagram");
		GeometricDiagram returned = newInstance(GeometricDiagram.class);
		getUndoManager().stopRecording(edit);
		return returned;
	}

	public <C extends GeometricConstruction<A>, A extends FGEArea> C makeNewConstruction(Class<C> type,
			GeometricObjectGraphicalRepresentation<A> gr, GeometricDiagram drawing) {
		C returned = newInstance(type);
		returned.setGraphicalRepresentation(gr);
		drawing.addToConstructions(returned);
		return returned;
	}

	public DrawingGraphicalRepresentation makeNewDrawingGR() {
		GeometricDrawingGraphicalRepresentation returned = newInstance(GeometricDrawingGraphicalRepresentation.class);
		returned.setWidth(10000);
		returned.setHeight(10000);
		return returned;
	}

	public <A extends FGEArea> GeometricObjectGraphicalRepresentation<A> makeNewConstructionGR(GeometricConstruction<A> construction) {
		GeometricObjectGraphicalRepresentation<A> returned = construction.makeNewConstructionGR(this);
		if (construction.getForeground() == null) {
			construction.setForeground(makeDefaultForegroundStyle());
		}
		if (construction.getBackground() == null) {
			construction.setBackground(makeDefaultBackgroundStyle());
		}
		if (returned.getTextStyle() == null) {
			returned.setTextStyle(makeDefaultTextStyle());
		}
		returned.setGeometricObject(construction.getData());
		return returned;
	}

	@Override
	public void applyBasicControls(DrawingGraphicalRepresentation drawingGraphicalRepresentation) {
		super.applyBasicControls(drawingGraphicalRepresentation);
		drawingGraphicalRepresentation.addToMouseClickControls(new ShowGeneralContextualMenuControl(getEditingContext()));
	}

	@Override
	public void applyBasicControls(ShapeGraphicalRepresentation shapeGraphicalRepresentation) {
		super.applyBasicControls(shapeGraphicalRepresentation);
		shapeGraphicalRepresentation.addToMouseClickControls(new ShowGeneralContextualMenuControl(getEditingContext()));
	}

	public ExplicitPointConstruction makeExplicitPointConstruction(FGEPoint pointLocation) {
		ExplicitPointConstruction returned = newInstance(ExplicitPointConstruction.class);
		returned.setPoint(pointLocation);
		return returned;
	}

	public PointReference makePointReference(PointConstruction pointConstruction) {
		PointReference returned = newInstance(PointReference.class);
		returned.setReference(pointConstruction);
		return returned;
	}

	public PointMiddleOfTwoPointsConstruction makePointMiddleOfTwoPointsConstruction(PointConstruction pointConstruction1,
			PointConstruction pointConstruction2) {
		PointMiddleOfTwoPointsConstruction returned = newInstance(PointMiddleOfTwoPointsConstruction.class);
		returned.setPointConstruction1(pointConstruction1);
		returned.setPointConstruction2(pointConstruction2);
		return returned;
	}

	public NearestPointFromObjectConstruction makePointNearestPointFromObject(PointConstruction pointConstruction,
			ObjectReference<?> objectReference) {
		NearestPointFromObjectConstruction returned = newInstance(NearestPointFromObjectConstruction.class);
		returned.setPointConstruction(pointConstruction);
		returned.setObjectReference(objectReference);
		return returned;
	}

	public SymetricPointConstruction makeSymetricPointConstruction(PointConstruction pointConstruction,
			PointConstruction pivotConstruction) {
		SymetricPointConstruction returned = newInstance(SymetricPointConstruction.class);
		returned.setPointConstruction(pointConstruction);
		returned.setPivotConstruction(pivotConstruction);
		return returned;
	}

	public LineReference makeLineReference(LineConstruction construction) {
		LineReference returned = newInstance(LineReference.class);
		returned.setReference(construction);
		return returned;
	}

	public ControlPointReference makeControlPointReference(GeometricConstruction<?> construction, String name) {
		ControlPointReference returned = newInstance(ControlPointReference.class);
		returned.setReference(construction);
		returned.setControlPointName(name);
		return returned;
	}

	public LineIntersectionPointConstruction makeLineIntersectionPointConstruction(LineConstruction lineConstruction1,
			LineConstruction lineConstruction2) {
		LineIntersectionPointConstruction returned = newInstance(LineIntersectionPointConstruction.class);
		returned.setLineConstruction1(lineConstruction1);
		returned.setLineConstruction2(lineConstruction2);
		return returned;
	}

	public LineWithTwoPointsConstruction makeLineWithTwoPointsConstruction(PointConstruction pointConstruction1,
			PointConstruction pointConstruction2) {
		LineWithTwoPointsConstruction returned = newInstance(LineWithTwoPointsConstruction.class);
		returned.setPointConstruction1(pointConstruction1);
		returned.setPointConstruction2(pointConstruction2);
		return returned;
	}

	public HorizontalLineWithPointConstruction makeHorizontalLineWithPointConstruction(PointConstruction pointConstruction) {
		HorizontalLineWithPointConstruction returned = newInstance(HorizontalLineWithPointConstruction.class);
		returned.setPointConstruction(pointConstruction);
		return returned;
	}

	public VerticalLineWithPointConstruction makeVerticalLineWithPointConstruction(PointConstruction pointConstruction) {
		VerticalLineWithPointConstruction returned = newInstance(VerticalLineWithPointConstruction.class);
		returned.setPointConstruction(pointConstruction);
		return returned;
	}

	public ParallelLineWithPointConstruction makeParallelLineWithPointConstruction(LineConstruction lineConstruction,
			PointConstruction pointConstruction) {
		ParallelLineWithPointConstruction returned = newInstance(ParallelLineWithPointConstruction.class);
		returned.setLineConstruction(lineConstruction);
		returned.setPointConstruction(pointConstruction);
		return returned;
	}

	public OrthogonalLineWithPointConstruction makeOrthogonalLineWithPointConstruction(LineConstruction lineConstruction,
			PointConstruction pointConstruction) {
		OrthogonalLineWithPointConstruction returned = newInstance(OrthogonalLineWithPointConstruction.class);
		returned.setLineConstruction(lineConstruction);
		returned.setPointConstruction(pointConstruction);
		return returned;
	}

	public RotatedLineWithPointConstruction makeRotatedLineWithPointConstruction(LineConstruction lineConstruction,
			PointConstruction pointConstruction, double angle) {
		RotatedLineWithPointConstruction returned = newInstance(RotatedLineWithPointConstruction.class);
		returned.setLineConstruction(lineConstruction);
		returned.setPointConstruction(pointConstruction);
		returned.setAngle(angle);
		return returned;
	}

	public TangentLineWithCircleAndPointConstruction makeTangentLineWithCircleAndPointConstruction(CircleConstruction circleConstruction,
			PointConstruction pointConstruction, PointConstruction choosingPointConstruction) {
		TangentLineWithCircleAndPointConstruction returned = newInstance(TangentLineWithCircleAndPointConstruction.class);
		returned.setCircleConstruction(circleConstruction);
		returned.setPointConstruction(pointConstruction);
		returned.setChoosingPointConstruction(choosingPointConstruction);
		return returned;
	}

	public HalfLineWithTwoPointsConstruction makeHalfLineWithTwoPointsConstruction(PointConstruction limitPointConstruction,
			PointConstruction oppositePointConstruction) {
		HalfLineWithTwoPointsConstruction returned = newInstance(HalfLineWithTwoPointsConstruction.class);
		returned.setLimitPointConstruction(limitPointConstruction);
		returned.setOppositePointConstruction(oppositePointConstruction);
		return returned;
	}

	public SegmentWithTwoPointsConstruction makeSegmentWithTwoPointsConstruction(PointConstruction pointConstruction1,
			PointConstruction pointConstruction2) {
		SegmentWithTwoPointsConstruction returned = newInstance(SegmentWithTwoPointsConstruction.class);
		returned.setPointConstruction1(pointConstruction1);
		returned.setPointConstruction2(pointConstruction2);
		return returned;
	}

	public PolylinWithNPointsConstruction makePolylinWithNPointsConstruction(List<PointConstruction> pointConstructions) {
		PolylinWithNPointsConstruction returned = newInstance(PolylinWithNPointsConstruction.class);
		for (PointConstruction pc : pointConstructions) {
			returned.addToPointConstructions(pc);
		}
		return returned;
	}

	public RectPolylinWithStartAndEndAreaConstruction makeRectPolylinWithStartAndEndAreaConstruction(
			ObjectReference<?> startAreaConstruction, SimplifiedCardinalDirection startOrientation, ObjectReference<?> endAreaConstruction,
			SimplifiedCardinalDirection endOrientation) {
		RectPolylinWithStartAndEndAreaConstruction returned = newInstance(RectPolylinWithStartAndEndAreaConstruction.class);
		returned.setStartAreaConstruction(startAreaConstruction);
		returned.setStartOrientation(startOrientation);
		returned.setEndAreaConstruction(endAreaConstruction);
		returned.setEndOrientation(endOrientation);
		return returned;
	}

	public RectangleWithTwoPointsConstruction makeRectangleWithTwoPointsConstruction(PointConstruction pointConstruction1,
			PointConstruction pointConstruction2) {
		RectangleWithTwoPointsConstruction returned = newInstance(RectangleWithTwoPointsConstruction.class);
		returned.setPointConstruction1(pointConstruction1);
		returned.setPointConstruction2(pointConstruction2);
		return returned;
	}

	public RoundRectangleWithTwoPointsConstruction makeRoundRectangleWithTwoPointsConstruction(PointConstruction pointConstruction1,
			PointConstruction pointConstruction2, double arcWidth, double arcHeight) {
		RoundRectangleWithTwoPointsConstruction returned = newInstance(RoundRectangleWithTwoPointsConstruction.class);
		returned.setPointConstruction1(pointConstruction1);
		returned.setPointConstruction2(pointConstruction2);
		returned.setArcWidth(arcWidth);
		returned.setArcHeight(arcHeight);
		return returned;
	}

	public CircleReference makeCircleReference(CircleConstruction construction) {
		CircleReference returned = newInstance(CircleReference.class);
		returned.setReference(construction);
		return returned;
	}

	public PolygonWithNPointsConstruction makePolygonWithNPointsConstruction(List<PointConstruction> pointConstructions) {
		PolygonWithNPointsConstruction returned = newInstance(PolygonWithNPointsConstruction.class);
		for (PointConstruction pc : pointConstructions) {
			returned.addToPointConstructions(pc);
		}
		return returned;
	}

	public CircleWithCenterAndPointConstruction makeCircleWithCenterAndPointConstruction(PointConstruction centerConstruction,
			PointConstruction pointConstruction) {
		CircleWithCenterAndPointConstruction returned = newInstance(CircleWithCenterAndPointConstruction.class);
		returned.setCenterConstruction(centerConstruction);
		returned.setPointConstruction(pointConstruction);
		return returned;
	}

	public HalfPlaneWithLineAndPointConstruction makeHalfPlaneWithLineAndPointConstruction(LineConstruction lineConstruction,
			PointConstruction pointConstruction) {
		HalfPlaneWithLineAndPointConstruction returned = newInstance(HalfPlaneWithLineAndPointConstruction.class);
		returned.setLineConstruction(lineConstruction);
		returned.setPointConstruction(pointConstruction);
		return returned;
	}

	public BandWithTwoLinesConstruction makeBandWithTwoLinesConstruction(LineConstruction lineConstruction1,
			LineConstruction lineConstruction2) {
		BandWithTwoLinesConstruction returned = newInstance(BandWithTwoLinesConstruction.class);
		returned.setLineConstruction1(lineConstruction1);
		returned.setLineConstruction2(lineConstruction2);
		return returned;
	}

	public HalfBandWithLinesConstruction makeHalfBandWithLinesConstruction(LineConstruction lineConstruction1,
			LineConstruction lineConstruction2, LineConstruction limitLineConstruction, PointConstruction pointConstruction) {
		HalfBandWithLinesConstruction returned = newInstance(HalfBandWithLinesConstruction.class);
		returned.setLineConstruction1(lineConstruction1);
		returned.setLineConstruction2(lineConstruction2);
		returned.setLimitLineConstruction(limitLineConstruction);
		returned.setPointConstruction(pointConstruction);
		return returned;
	}

	public QuadCurveWithThreePointsConstruction makeQuadCurveWithThreePointsConstruction(PointConstruction startPointConstruction,
			PointConstruction endPointConstruction, PointConstruction controlPointConstruction) {
		QuadCurveWithThreePointsConstruction returned = newInstance(QuadCurveWithThreePointsConstruction.class);
		returned.setStartPointConstruction(startPointConstruction);
		returned.setEndPointConstruction(endPointConstruction);
		returned.setControlPointConstruction(controlPointConstruction);
		return returned;
	}

	public CubicCurveWithFourPointsConstruction makeCubicCurveWithFourPointsConstruction(PointConstruction startPointConstruction,
			PointConstruction endPointConstruction, PointConstruction controlPointConstruction1,
			PointConstruction controlPointConstruction2) {
		CubicCurveWithFourPointsConstruction returned = newInstance(CubicCurveWithFourPointsConstruction.class);
		returned.setStartPointConstruction(startPointConstruction);
		returned.setEndPointConstruction(endPointConstruction);
		returned.setControlPointConstruction1(controlPointConstruction1);
		returned.setControlPointConstruction2(controlPointConstruction2);
		return returned;
	}

	public ComplexCurveWithNPointsConstruction makeCurveWithNPointsConstruction(List<PointConstruction> pointConstructions) {
		ComplexCurveWithNPointsConstruction returned = newInstance(ComplexCurveWithNPointsConstruction.class);
		for (PointConstruction pc : pointConstructions) {
			returned.addToPointConstructions(pc);
		}
		return returned;
	}

	public ObjectReference<?> makeObjectReference(GeometricConstruction construction) {
		ObjectReference returned = newInstance(ObjectReference.class);
		returned.setReference(construction);
		return returned;
	}

	public IntersectionConstruction makeIntersectionConstruction(List<ObjectReference<?>> objectReferences) {
		IntersectionConstruction returned = newInstance(IntersectionConstruction.class);
		for (ObjectReference<?> or : objectReferences) {
			returned.addToObjectConstructions(or);
		}
		return returned;
	}

}
