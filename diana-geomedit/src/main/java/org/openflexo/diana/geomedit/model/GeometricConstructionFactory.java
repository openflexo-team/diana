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

import org.openflexo.diana.geomedit.ShowContextualMenuControl;
import org.openflexo.diana.geomedit.model.construction.ControlPointReference;
import org.openflexo.diana.geomedit.model.construction.ExplicitPointConstruction;
import org.openflexo.diana.geomedit.model.construction.GeometricConstruction;
import org.openflexo.diana.geomedit.model.construction.LineConstruction;
import org.openflexo.diana.geomedit.model.construction.LineIntersectionPointConstruction;
import org.openflexo.diana.geomedit.model.construction.LineReference;
import org.openflexo.diana.geomedit.model.construction.LineWithTwoPointsConstruction;
import org.openflexo.diana.geomedit.model.construction.PointConstruction;
import org.openflexo.diana.geomedit.model.construction.PointReference;
import org.openflexo.diana.geomedit.model.gr.GeometricDrawingGraphicalRepresentation;
import org.openflexo.diana.geomedit.model.gr.GeometricObjectGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.ShapeGraphicalRepresentation;
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
		return construction.makeNewConstructionGR(this);
	}

	@Override
	public void applyBasicControls(DrawingGraphicalRepresentation drawingGraphicalRepresentation) {
		super.applyBasicControls(drawingGraphicalRepresentation);
		drawingGraphicalRepresentation.addToMouseClickControls(new ShowContextualMenuControl(getEditingContext()));
	}

	@Override
	public void applyBasicControls(ShapeGraphicalRepresentation shapeGraphicalRepresentation) {
		super.applyBasicControls(shapeGraphicalRepresentation);
		shapeGraphicalRepresentation.addToMouseClickControls(new ShowContextualMenuControl(getEditingContext()));
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
}
