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

import java.awt.Color;
import java.awt.Font;
import java.util.logging.Logger;

import org.openflexo.diana.FGEConstants;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.diana.control.DrawingPalette;
import org.openflexo.diana.control.PaletteElement;
import org.openflexo.diana.control.DianaInteractiveEditor.EditorTool;
import org.openflexo.diana.drawingeditor.model.DiagramElement;
import org.openflexo.diana.drawingeditor.model.Shape;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.undo.CompoundEdit;

public class DiagramEditorPalette extends DrawingPalette {

	private static final Logger logger = FlexoLogger.getLogger(DiagramEditorPalette.class.getPackage().getName());

	private static final int GRID_WIDTH = 80;
	private static final int GRID_HEIGHT = 60;
	public static final Font DEFAULT_TEXT_FONT = new Font("SansSerif", Font.PLAIN, 9);
	public static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 11);

	private DianaDrawingEditor editor;

	public DiagramEditorPalette() {
		super(360, 350, "default");
		int px = 0;
		int py = 0;
		for (ShapeType st : ShapeType.values()) {
			if (st != ShapeType.CUSTOM_POLYGON && st != ShapeType.COMPLEX_CURVE) {
				addElement(makePaletteElement(st, px, py));
				px = px + 1;
				if (px == 4) {
					px = 0;
					py++;
				}
			}
		}

		addElement(makeSingleLabel(0, 4));
		addElement(makeMultilineLabel(1, 4));
		addElement(makeBoundedMultilineLabel(2, 4));

	}

	public DianaDrawingEditor getEditor() {
		return editor;
	}

	public void setEditor(DianaDrawingEditor editor) {
		this.editor = editor;
	}

	private PaletteElement makePaletteElement(ShapeType st, int px, int py) {
		final ShapeGraphicalRepresentation gr = FACTORY.makeShapeGraphicalRepresentation(st);
		FACTORY.applyDefaultProperties(gr);
		if (gr.getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
			gr.setX(px * GRID_WIDTH + 15);
			gr.setY(py * GRID_HEIGHT + 10);
			gr.setWidth(50);
			gr.setHeight(50);
		}
		else {
			gr.setX(px * GRID_WIDTH + 10);
			gr.setY(py * GRID_HEIGHT + 10);
			gr.setWidth(60);
			gr.setHeight(50);
		}
		gr.setText(st.name());
		gr.setTextStyle(FACTORY.makeTextStyle(Color.DARK_GRAY, DEFAULT_TEXT_FONT));
		gr.setIsFloatingLabel(false);
		gr.setForeground(FACTORY.makeForegroundStyle(Color.BLACK));
		gr.setBackground(FACTORY.makeColoredBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR));
		gr.setIsVisible(true);
		gr.setAllowToLeaveBounds(false);

		return makePaletteElement(gr, true, true, true, true);

		/*PaletteElement returned = new PaletteElement() {
			public boolean acceptDragging(GraphicalRepresentation gr)
			{
				return (gr instanceof DrawingGraphicalRepresentation) || (gr instanceof ShapeGraphicalRepresentation);
			}
			public boolean elementDragged(GraphicalRepresentation gr, FGEPoint dropLocation)
			{
				DiagramElement container = (DiagramElement)gr.getDrawable();
				getController().addNewShape(new Shape(getGraphicalRepresentation().getShapeType(), dropLocation, getController().getDrawing()),container);
				return true;
			}
			public PaletteElementGraphicalRepresentation getGraphicalRepresentation()
			{
				return gr;
			}		
			public DrawingPalette getPalette()
			{
				return DiagramEditorPalette.this;
			}
		};
		gr.setDrawable(returned);
		return returned;*/
	}

	private PaletteElement makeSingleLabel(int px, int py) {
		final ShapeGraphicalRepresentation gr = FACTORY.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		gr.setX(px * GRID_WIDTH + 10);
		gr.setY(py * GRID_HEIGHT + 15);
		gr.setWidth(60);
		gr.setHeight(20);
		gr.setAdjustMinimalWidthToLabelWidth(true);
		gr.setAdjustMinimalHeightToLabelHeight(true);

		gr.setTextStyle(FACTORY.makeTextStyle(Color.BLACK, LABEL_FONT));
		gr.setText("Label");
		gr.setIsFloatingLabel(false);
		gr.setForeground(FACTORY.makeNoneForegroundStyle());
		gr.setBackground(FACTORY.makeEmptyBackground());
		gr.setShadowStyle(FACTORY.makeNoneShadowStyle());
		gr.setIsVisible(true);

		return makePaletteElement(gr, false, false, true, false);
	}

	private PaletteElement makeMultilineLabel(int px, int py) {
		final ShapeGraphicalRepresentation gr = FACTORY.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		gr.setX(px * GRID_WIDTH + 10);
		gr.setY(py * GRID_HEIGHT + 10);
		gr.setWidth(60);
		gr.setHeight(20);
		gr.setAdjustMinimalWidthToLabelWidth(true);
		gr.setAdjustMinimalHeightToLabelHeight(true);

		gr.setTextStyle(FACTORY.makeTextStyle(Color.BLACK, LABEL_FONT));
		gr.setIsMultilineAllowed(true);
		gr.setText("Multiple\nlines label");
		gr.setIsFloatingLabel(false);
		gr.setForeground(FACTORY.makeNoneForegroundStyle());
		gr.setBackground(FACTORY.makeEmptyBackground());
		gr.setShadowStyle(FACTORY.makeNoneShadowStyle());
		gr.setIsVisible(true);

		return makePaletteElement(gr, false, false, true, false);
	}

	private PaletteElement makeBoundedMultilineLabel(int px, int py) {
		final ShapeGraphicalRepresentation gr = FACTORY.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		gr.setX(px * GRID_WIDTH + 10);
		gr.setY(py * GRID_HEIGHT + 10);
		gr.setWidth(60);
		gr.setHeight(20);
		gr.setAdjustMinimalWidthToLabelWidth(true);
		gr.setAdjustMinimalHeightToLabelHeight(true);

		gr.setTextStyle(FACTORY.makeTextStyle(Color.BLACK, LABEL_FONT));
		gr.setIsMultilineAllowed(true);
		gr.setText("Multiple\nlines label");
		gr.setIsFloatingLabel(false);
		gr.setBackground(FACTORY.makeEmptyBackground());
		gr.setForeground(FACTORY.makeNoneForegroundStyle());
		gr.setShadowStyle(FACTORY.makeNoneShadowStyle());
		gr.setIsVisible(true);

		return makePaletteElement(gr, false, false, true, false);
	}

	private PaletteElement makePaletteElement(final ShapeGraphicalRepresentation gr, final boolean applyCurrentForeground,
			final boolean applyCurrentBackground, final boolean applyCurrentTextStyle, final boolean applyCurrentShadowStyle) {
		PaletteElement returned = new PaletteElement() {

			@Override
			public boolean acceptDragging(DrawingTreeNode<?, ?> target) {
				return getEditor() != null && target instanceof ContainerNode;
			}

			@Override
			public boolean elementDragged(DrawingTreeNode<?, ?> target, FGEPoint dropLocation) {

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

				if (applyCurrentForeground) {
					shapeGR.setForeground(getEditor().getInspectedForegroundStyle().cloneStyle());
				}
				if (applyCurrentBackground) {
					shapeGR.setBackground(getEditor().getInspectedBackgroundStyle().cloneStyle());
				}
				if (applyCurrentTextStyle) {
					shapeGR.setTextStyle(getEditor().getInspectedTextStyle().cloneStyle());
				}
				if (applyCurrentShadowStyle) {
					shapeGR.setShadowStyle(getEditor().getInspectedShadowStyle().cloneStyle());
				}

				container.addToShapes(newShape);

				getEditor().getFactory().getUndoManager().stopRecording(edit);

				getEditor().setCurrentTool(EditorTool.SelectionTool);
				getEditor().setSelectedObject(getEditor().getDrawing().getDrawingTreeNode(newShape));

				return true;
			}

			@Override
			public ShapeGraphicalRepresentation getGraphicalRepresentation() {
				return gr;
			}

			@Override
			public String getName() {
				return null;
			}

			@Override
			public void delete(Object... context) {
				gr.delete();
			}

			/*public DrawingPalette getPalette() {
				return DiagramEditorPalette.this;
			}*/
		};
		// gr.setDrawable(returned);
		return returned;
	}
}
