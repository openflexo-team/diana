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

package org.openflexo.diana.drawingeditor.model;

import org.openflexo.diana.ConnectorGraphicalRepresentation;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.FGEModelFactoryImpl;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.diana.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.diana.drawingeditor.DrawEdgeControl;
import org.openflexo.diana.drawingeditor.ShowContextualMenuControl;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.converter.RelativePathResourceConverter;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.EditingContext;
import org.openflexo.model.undo.CompoundEdit;
import org.openflexo.model.undo.UndoManager;

public class DiagramFactory extends FGEModelFactoryImpl {

	private int shapeIndex = 0;
	private int connectorIndex = 0;

	private RelativePathResourceConverter resourceConverter;

	public DiagramFactory(EditingContext editingContext) throws ModelDefinitionException {
		super(Diagram.class, Shape.class, Connector.class);
		setEditingContext(editingContext);
		addConverter(resourceConverter = new RelativePathResourceConverter(null));

	}

	public RelativePathResourceConverter getResourceConverter() {
		return resourceConverter;
	}

	public UndoManager getUndoManager() {
		if (getEditingContext() != null) {
			return getEditingContext().getUndoManager();
		}
		return null;
	}

	// Called for NEW
	public Diagram makeNewDiagram() {
		CompoundEdit edit = getUndoManager().startRecording("Create empty diagram");
		Diagram returned = newInstance(Diagram.class);
		// returned.setFactory(this);
		// returned.setIndex(totalOccurences);
		// returned.getEditedDrawing().init();
		getUndoManager().stopRecording(edit);
		return returned;
	}

	public Connector makeNewConnector(ConnectorGraphicalRepresentation aGR, Shape from, Shape to, Diagram diagram) {
		Connector returned = newInstance(Connector.class);
		returned.setName("Connector" + connectorIndex);
		connectorIndex++;
		returned.setDiagram(diagram);
		returned.setGraphicalRepresentation(makeNewConnectorGR(aGR));
		returned.setStartShape(from);
		returned.setEndShape(to);
		return returned;
	}

	public Connector makeNewConnector(Shape from, Shape to, Diagram diagram) {
		Connector returned = newInstance(Connector.class);
		returned.setName("Connector" + connectorIndex);
		connectorIndex++;
		returned.setDiagram(diagram);
		returned.setGraphicalRepresentation(makeNewConnectorGR(ConnectorType.LINE));
		returned.setStartShape(from);
		returned.setEndShape(to);
		return returned;
	}

	public Shape makeNewShape(ShapeType shape, FGEPoint p, Diagram diagram) {
		ShapeGraphicalRepresentation gr = makeNewShapeGR(shape);
		gr.setWidth(100);
		gr.setHeight(80);
		return makeNewShape(gr, p, diagram);
	}

	public Shape makeNewShape(ShapeGraphicalRepresentation aGR, FGEPoint p, Diagram diagram) {
		Shape returned = newInstance(Shape.class);
		returned.setDiagram(diagram);
		returned.setName("Shape" + shapeIndex);
		System.out.println("New name: " + returned.getName());
		shapeIndex++;
		ShapeGraphicalRepresentation gr = makeNewShapeGR(aGR);
		gr.setX(p.x);
		gr.setY(p.y);
		returned.setGraphicalRepresentation(gr);
		return returned;
	}

	public Shape makeNewShape(ShapeGraphicalRepresentation aGR, Diagram diagram) {
		return makeNewShape(aGR, aGR.getLocation(), diagram);
	}

	public DrawingGraphicalRepresentation makeNewDrawingGR() {
		return makeDrawingGraphicalRepresentation(true);
	}

	public ShapeGraphicalRepresentation makeNewShapeGR(ShapeType shapeType) {
		ShapeGraphicalRepresentation returned = newInstance(ShapeGraphicalRepresentation.class, true, true);
		returned.setFactory(this);
		returned.setShapeType(shapeType);
		returned.setIsFocusable(true);
		returned.setIsSelectable(true);
		returned.setIsReadOnly(false);
		returned.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
		return returned;

	}

	public ShapeGraphicalRepresentation makeNewShapeGR(ShapeGraphicalRepresentation aGR) {
		ShapeGraphicalRepresentation returned = newInstance(ShapeGraphicalRepresentation.class, true, true);
		returned.setFactory(this);
		returned.setsWith(aGR);
		returned.setIsFocusable(true);
		returned.setIsSelectable(true);
		returned.setIsReadOnly(false);
		returned.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
		return returned;
	}

	public ConnectorGraphicalRepresentation makeNewConnectorGR(ConnectorType aConnectorType) {
		ConnectorGraphicalRepresentation returned = newInstance(ConnectorGraphicalRepresentation.class);
		returned.setFactory(this);
		returned.setConnectorType(aConnectorType);
		applyDefaultProperties(returned);
		applyBasicControls(returned);
		return returned;
	}

	public ConnectorGraphicalRepresentation makeNewConnectorGR(ConnectorGraphicalRepresentation aGR) {
		ConnectorGraphicalRepresentation returned = newInstance(ConnectorGraphicalRepresentation.class);
		returned.setFactory(this);
		returned.setsWith(aGR);
		return returned;
	}

	/*@Override
	public <I> I newInstance(Class<I> implementedInterface) {
		if (implementedInterface == ShapeGraphicalRepresentation.class) {
			return (I) newInstance(ShapeGraphicalRepresentation.class, true, true);
		} else if (implementedInterface == ConnectorGraphicalRepresentation.class) {
			return (I) newInstance(ConnectorGraphicalRepresentation.class, true, true);
		} else if (implementedInterface == DrawingGraphicalRepresentation.class) {
			return (I) newInstance(DrawingGraphicalRepresentation.class, true, true);
		}
		return super.newInstance(implementedInterface);
	}*/

	@Override
	public void applyBasicControls(ConnectorGraphicalRepresentation connectorGraphicalRepresentation) {
		super.applyBasicControls(connectorGraphicalRepresentation);
	}

	@Override
	public void applyBasicControls(DrawingGraphicalRepresentation drawingGraphicalRepresentation) {
		super.applyBasicControls(drawingGraphicalRepresentation);
		drawingGraphicalRepresentation.addToMouseClickControls(new ShowContextualMenuControl(getEditingContext()));
		drawingGraphicalRepresentation.addToMouseDragControls(new DrawEdgeControl(this));
	}

	@Override
	public void applyBasicControls(ShapeGraphicalRepresentation shapeGraphicalRepresentation) {
		super.applyBasicControls(shapeGraphicalRepresentation);
		shapeGraphicalRepresentation.addToMouseClickControls(new ShowContextualMenuControl(getEditingContext()));
		shapeGraphicalRepresentation.addToMouseDragControls(new DrawEdgeControl(this));
	}

}
