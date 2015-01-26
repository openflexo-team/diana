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

package org.openflexo.fge.drawingeditor;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRBinding.ConnectorGRBinding;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.ConnectorGRProvider;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.drawingeditor.model.Connector;
import org.openflexo.fge.drawingeditor.model.Diagram;
import org.openflexo.fge.drawingeditor.model.DiagramFactory;
import org.openflexo.fge.drawingeditor.model.Shape;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.EditingContextImpl;

public class DiagramDrawing extends DrawingImpl<Diagram> {

	public DiagramDrawing(Diagram model, FGEModelFactory factory) {
		super(model, factory, PersistenceMode.UniqueGraphicalRepresentations);
	}

	@Override
	public void init() {

		final DrawingGRBinding<Diagram> drawingBinding = bindDrawing(Diagram.class, "drawing", new DrawingGRProvider<Diagram>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(Diagram drawable, FGEModelFactory factory) {
				if (drawable.getGraphicalRepresentation() != null) {
					drawable.getGraphicalRepresentation().setFactory(factory);
					return drawable.getGraphicalRepresentation();
				} else {
					DrawingGraphicalRepresentation returned = factory.makeDrawingGraphicalRepresentation();
					drawable.setGraphicalRepresentation(returned);
					return returned;
				}
			}
		});
		final ShapeGRBinding<Shape> shapeBinding = bindShape(Shape.class, "shape", new ShapeGRProvider<Shape>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(Shape drawable, FGEModelFactory factory) {
				if (drawable.getGraphicalRepresentation() != null) {
					drawable.getGraphicalRepresentation().setFactory(factory);
					return drawable.getGraphicalRepresentation();
				} else {
					ShapeGraphicalRepresentation returned = factory.makeShapeGraphicalRepresentation();
					drawable.setGraphicalRepresentation(returned);
					return returned;
				}
			}
		});
		final ConnectorGRBinding<Connector> connectorBinding = bindConnector(Connector.class, "connector", shapeBinding, shapeBinding,
				new ConnectorGRProvider<Connector>() {
					@Override
					public ConnectorGraphicalRepresentation provideGR(Connector drawable, FGEModelFactory factory) {
						if (drawable.getGraphicalRepresentation() != null) {
							drawable.getGraphicalRepresentation().setFactory(factory);
							return drawable.getGraphicalRepresentation();
						} else {
							ConnectorGraphicalRepresentation returned = factory.makeConnectorGraphicalRepresentation();
							drawable.setGraphicalRepresentation(returned);
							return returned;
						}
					}
				});

		// visitor plutot ?
		drawingBinding.addToWalkers(new GRStructureVisitor<Diagram>() {

			@Override
			public void visit(Diagram myDrawing) {
				for (Shape shape : myDrawing.getShapes()) {
					drawShape(shapeBinding, shape, myDrawing);
					// drawShape(shape).as(shapeBinding).in(myDrawing);
				}
			}
		});

		drawingBinding.addToWalkers(new GRStructureVisitor<Diagram>() {
			@Override
			public void visit(Diagram myDrawing) {
				for (Connector connector : myDrawing.getConnectors()) {
					drawConnector(connectorBinding, connector, connector.getStartShape(), connector.getEndShape());
				}
			}
		});

		shapeBinding.addToWalkers(new GRStructureVisitor<Shape>() {
			@Override
			public void visit(Shape myShape) {
				for (Shape shape : myShape.getShapes()) {
					drawShape(shapeBinding, shape, shapeBinding, myShape);
					// drawShape(shape).as(shapeBinding).in(myShape).as(shapeBinding);
				}
			}
		});
		shapeBinding.addToWalkers(new GRStructureVisitor<Shape>() {
			@Override
			public void visit(Shape myShape) {
				for (Connector connector : myShape.getConnectors()) {
					drawConnector(connectorBinding, connector, connector.getStartShape(), connector.getEndShape());
				}
			}
		});

		shapeBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);
		connectorBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);

	}

	public static void main(String[] args) throws ModelDefinitionException {
		EditingContextImpl editingContext = new EditingContextImpl();
		editingContext.createUndoManager();
		DiagramFactory factory = new DiagramFactory(editingContext);
		Diagram myDrawing = factory.makeNewDiagram();
		new DiagramDrawing(myDrawing, factory);
	}
}
