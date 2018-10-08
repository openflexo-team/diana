/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-geomedit, a component of the software infrastructure 
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

package org.openflexo.diana.geomedit;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.diana.BackgroundStyle;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.GRBinding.DrawingGRBinding;
import org.openflexo.diana.GRBinding.GeometricGRBinding;
import org.openflexo.diana.GRProvider.DrawingGRProvider;
import org.openflexo.diana.GRProvider.GeometricGRProvider;
import org.openflexo.diana.GRStructureVisitor;
import org.openflexo.diana.GeometricGraphicalRepresentation;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geomedit.controller.ShowContextualMenuControl;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstructionFactory;
import org.openflexo.diana.geomedit.model.GeometricDiagram;
import org.openflexo.diana.geomedit.model.gr.GeometricObjectGraphicalRepresentation;
import org.openflexo.diana.impl.DrawingImpl;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.EditingContextImpl;

public class GeometricDiagramDrawing extends DrawingImpl<GeometricDiagram> {

	public GeometricDiagramDrawing(GeometricDiagram model, DianaModelFactory factory) {
		super(model, factory, PersistenceMode.UniqueGraphicalRepresentations);
	}

	@Override
	public void init() {

		final DrawingGRBinding<GeometricDiagram> drawingBinding = bindDrawing(GeometricDiagram.class, "drawing",
				new DrawingGRProvider<GeometricDiagram>() {
					@Override
					public DrawingGraphicalRepresentation provideGR(GeometricDiagram drawable, DianaModelFactory factory) {
						if (drawable.getGraphicalRepresentation() != null) {
							drawable.getGraphicalRepresentation().setFactory(factory);
							return drawable.getGraphicalRepresentation();
						}
						else {
							DrawingGraphicalRepresentation returned = factory.makeDrawingGraphicalRepresentation();
							drawable.setGraphicalRepresentation(returned);
							return returned;
						}
					}
				});
		final GeometricGRBinding<GeometricConstruction> constructionBinding = bindGeometric(GeometricConstruction.class, "construction",
				new GeometricGRProvider<GeometricConstruction>() {
					@Override
					public GeometricGraphicalRepresentation provideGR(GeometricConstruction drawable, DianaModelFactory factory) {
						if (drawable.getGraphicalRepresentation() != null) {
							drawable.getGraphicalRepresentation().setFactory(factory);
							drawable.getGraphicalRepresentation().setGeometricObject(drawable.getData());
							if (drawable.getForeground() == null) {
								drawable.setForeground(factory.makeDefaultForegroundStyle());
							}
							if (drawable.getBackground() == null) {
								drawable.setBackground(factory.makeDefaultBackgroundStyle());
							}
							drawable.getGraphicalRepresentation()
									.addToMouseClickControls(new ShowContextualMenuControl(factory.getEditingContext()));
							return drawable.getGraphicalRepresentation();
						}
						else {
							GeometricObjectGraphicalRepresentation returned = ((GeometricConstructionFactory) factory)
									.makeNewConstructionGR(drawable);
							returned.addToMouseClickControls(new ShowContextualMenuControl(factory.getEditingContext()));
							drawable.setGraphicalRepresentation(returned);
							return returned;
						}
					}

					@Override
					public List<? extends ControlArea<?>> makeControlAreasFor(
							DrawingTreeNode<GeometricConstruction, GeometricGraphicalRepresentation> dtn) {
						List<? extends ControlArea<?>> returned = dtn.getDrawable().getGraphicalRepresentation().makeControlAreasFor(dtn);
						List<ControlPoint> controlPoints = new ArrayList<ControlPoint>();
						for (ControlArea<?> ca : returned) {
							if (ca instanceof ControlPoint) {
								controlPoints.add((ControlPoint) ca);
							}
						}

						dtn.getDrawable().setControlPoints(controlPoints);

						return returned;
					}
				});
		drawingBinding.addToWalkers(new GRStructureVisitor<GeometricDiagram>() {

			@Override
			public void visit(GeometricDiagram diagram) {
				for (GeometricConstruction construction : diagram.getConstructions()) {
					drawGeometricObject(constructionBinding, construction);
				}
			}
		});

		// We bind the text of the graphical representation to the name of GeometricConstruction as read/write binding
		constructionBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);

		// We bind the geometric object to be displayed (in graphical representation) to the data given by GeometricConstruction as a read
		// binding
		constructionBinding.setDynamicPropertyValue(GeometricGraphicalRepresentation.GEOMETRIC_OBJECT,
				new DataBinding<DianaArea>("drawable.data"), false);
		constructionBinding.setDynamicPropertyValue(GeometricGraphicalRepresentation.FOREGROUND,
				new DataBinding<ForegroundStyle>("drawable.foreground"), true);
		constructionBinding.setDynamicPropertyValue(GeometricGraphicalRepresentation.BACKGROUND,
				new DataBinding<BackgroundStyle>("drawable.background"), true);

	}

	public static void main(String[] args) throws ModelDefinitionException {
		EditingContextImpl editingContext = new EditingContextImpl();
		editingContext.createUndoManager();
		GeometricConstructionFactory factory = new GeometricConstructionFactory(editingContext);
		GeometricDiagram myDrawing = factory.makeNewGeometricDiagram();
		new GeometricDiagramDrawing(myDrawing, factory);
	}

}
