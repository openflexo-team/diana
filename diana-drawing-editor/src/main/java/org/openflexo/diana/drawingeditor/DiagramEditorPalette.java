/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.diana.drawingeditor;

import java.util.logging.Logger;

import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.PaletteElementSpecification;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.control.DianaInteractiveEditor.EditorTool;
import org.openflexo.diana.control.PaletteElement;
import org.openflexo.diana.control.PaletteModel;
import org.openflexo.diana.drawingeditor.model.DiagramElement;
import org.openflexo.diana.drawingeditor.model.Shape;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.converter.RelativePathResourceConverter;
import org.openflexo.model.undo.CompoundEdit;
import org.openflexo.rm.ResourceLocator;

public class DiagramEditorPalette extends PaletteModel {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(DiagramEditorPalette.class.getPackage().getName());

	private DianaDrawingEditor editor;

	public DiagramEditorPalette() {
		super("default", 250, 210, 50, 40, 10, 10);
		FACTORY.addConverter(
				new RelativePathResourceConverter(ResourceLocator.locateResource("Palettes/Basic").getContainer().getContainer()));
		readFromDirectory(ResourceLocator.locateResource("Palettes/Basic"));
	}

	public DianaDrawingEditor getEditor() {
		return editor;
	}

	public void setEditor(DianaDrawingEditor editor) {
		this.editor = editor;
	}

	@SuppressWarnings("serial")
	@Override
	protected PaletteElement buildPaletteElement(final PaletteElementSpecification paletteElement) {
		PaletteElement returned = new PaletteElement() {

			@Override
			public boolean acceptDragging(DrawingTreeNode<?, ?> target) {
				return getEditor() != null && target instanceof ContainerNode;
			}

			@Override
			public boolean elementDragged(DrawingTreeNode<?, ?> target, DianaPoint dropLocation) {

				if (getEditor() == null) {
					return false;
				}

				DiagramElement<?, ?> container = (DiagramElement<?, ?>) target.getDrawable();

				logger.info("dragging " + this + " in " + container);

				// getController().addNewShape(new Shape(getGraphicalRepresentation().getShapeType(), dropLocation,
				// getController().getDrawing()),container);

				CompoundEdit edit = getEditor().getFactory().getUndoManager().startRecording("Dragging new Element");

				System.out.println("dropLocation=" + dropLocation);

				Shape newShape = getEditor().getFactory().makeNewShape(getGraphicalRepresentation(), dropLocation, container.getDiagram());

				ShapeGraphicalRepresentation shapeGR = newShape.getGraphicalRepresentation();

				if (paletteElement.getApplyCurrentForeground()) {
					shapeGR.setForeground(getEditor().getInspectedForegroundStyle().cloneStyle());
				}
				if (paletteElement.getApplyCurrentBackground()) {
					shapeGR.setBackground(getEditor().getInspectedBackgroundStyle().cloneStyle());
				}
				if (paletteElement.getApplyCurrentTextStyle()) {
					shapeGR.setTextStyle(getEditor().getInspectedTextStyle().cloneStyle());
				}
				if (paletteElement.getApplyCurrentShadowStyle()) {
					shapeGR.setShadowStyle(getEditor().getInspectedShadowStyle().cloneStyle());
				}
				if (paletteElement.getAskForImage()) {
					// TODO: ask image ?
				}

				container.addToShapes(newShape);

				getEditor().getFactory().getUndoManager().stopRecording(edit);

				getEditor().setCurrentTool(EditorTool.SelectionTool);
				getEditor().setSelectedObject(getEditor().getDrawing().getDrawingTreeNode(newShape));

				return true;
			}

			@Override
			public ShapeGraphicalRepresentation getGraphicalRepresentation() {
				return paletteElement.getGraphicalRepresentation();
			}

			@Override
			public String getName() {
				return paletteElement.getName();
			}

			@Override
			public void delete(Object... context) {
				paletteElement.delete();
			}

		};
		return returned;
	}
}
