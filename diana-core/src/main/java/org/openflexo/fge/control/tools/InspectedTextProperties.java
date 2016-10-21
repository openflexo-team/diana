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

package org.openflexo.fge.control.tools;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.GraphicalRepresentation.ParagraphAlignment;
import org.openflexo.fge.GraphicalRepresentation.VerticalTextAlignment;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.DianaInteractiveViewer;

/**
 * Implementation of {@link TextStyle}, as a container of graphical properties synchronized with and reflecting a selection<br>
 * This is the object beeing represented in tool inspectors
 * 
 * @author sylvain
 * 
 */
public class InspectedTextProperties extends InspectedStyle<GraphicalRepresentation> {

	private final InspectedTextStyle inspectedTextStyle;

	public InspectedTextProperties(DianaInteractiveViewer<?, ?, ?> controller) {
		super(controller, null);
		inspectedTextStyle = new InspectedTextStyle(controller);
	}

	@Override
	public void fireSelectionUpdated() {
		super.fireSelectionUpdated();
		inspectedTextStyle.fireSelectionUpdated();
	}

	@Override
	public List<DrawingTreeNode<?, ?>> getSelection() {
		return getController().getSelectedObjects(ShapeNode.class, ConnectorNode.class, GeometricNode.class);
	}

	@Override
	public GraphicalRepresentation getStyle(DrawingTreeNode<?, ?> node) {
		return node.getGraphicalRepresentation();
	}

	public InspectedTextStyle getInspectedTextStyle() {
		return inspectedTextStyle;
	}

	public Color getColor() {
		return getInspectedTextStyle().getColor();
	}

	public void setColor(Color aColor) {
		getInspectedTextStyle().setColor(aColor);
	}

	public Color getBackgroundColor() {
		return getInspectedTextStyle().getBackgroundColor();
	}

	public void setBackgroundColor(Color aColor) {
		getInspectedTextStyle().setBackgroundColor(aColor);
	}

	public Font getFont() {
		return getInspectedTextStyle().getFont();
	}

	public void setFont(Font aFont) {
		getInspectedTextStyle().setFont(aFont);
	}

	public int getOrientation() {
		return getInspectedTextStyle().getOrientation();
	}

	public void setOrientation(int anOrientation) {
		getInspectedTextStyle().setOrientation(anOrientation);
	}

	public boolean getIsBackgroundColored() {
		return getInspectedTextStyle().getIsBackgroundColored();
	}

	public void setIsBackgroundColored(boolean aFlag) {
		getInspectedTextStyle().setIsBackgroundColored(aFlag);
	}

	public HorizontalTextAlignment getHorizontalTextAlignment() {
		return getPropertyValue(GraphicalRepresentation.HORIZONTAL_TEXT_ALIGNEMENT);
	}

	public void setHorizontalTextAlignment(HorizontalTextAlignment alignment) {
		setPropertyValue(GraphicalRepresentation.HORIZONTAL_TEXT_ALIGNEMENT, alignment);
	}

	public VerticalTextAlignment getVerticalTextAlignment() {
		return getPropertyValue(GraphicalRepresentation.VERTICAL_TEXT_ALIGNEMENT);
	}

	public void setVerticalTextAlignment(VerticalTextAlignment alignment) {
		setPropertyValue(GraphicalRepresentation.VERTICAL_TEXT_ALIGNEMENT, alignment);
	}

	public ParagraphAlignment getParagraphAlignment() {
		return getPropertyValue(GraphicalRepresentation.PARAGRAPH_ALIGNEMENT);
	}

	public void setParagraphAlignment(ParagraphAlignment alignment) {
		setPropertyValue(GraphicalRepresentation.PARAGRAPH_ALIGNEMENT, alignment);
	}

	public Boolean getIsFloatingLabel() {
		return getPropertyValue(ShapeGraphicalRepresentation.IS_FLOATING_LABEL);
	}

	public void setIsFloatingLabel(Boolean flag) {
		setPropertyValue(ShapeGraphicalRepresentation.IS_FLOATING_LABEL, flag);
	}

	public Double getAbsoluteTextX() {
		return getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X);
	}

	public void setAbsoluteTextX(Double x) {
		setPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X, x);
	}

	public Double getAbsoluteTextY() {
		return getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y);
	}

	public void setAbsoluteTextY(Double y) {
		setPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y, y);
	}

	public Double getRelativeTextX() {
		return getPropertyValue(ShapeGraphicalRepresentation.RELATIVE_TEXT_X);
	}

	public void setRelativeTextX(Double x) {
		setPropertyValue(ShapeGraphicalRepresentation.RELATIVE_TEXT_X, x);
	}

	public Double getRelativeTextY() {
		return getPropertyValue(ShapeGraphicalRepresentation.RELATIVE_TEXT_Y);
	}

	public void setRelativeTextY(Double y) {
		setPropertyValue(ShapeGraphicalRepresentation.RELATIVE_TEXT_Y, y);
	}

	public Boolean getIsMultilineAllowed() {
		return getPropertyValue(GraphicalRepresentation.IS_MULTILINE_ALLOWED);
	}

	public void setIsMultilineAllowed(Boolean flag) {
		setPropertyValue(GraphicalRepresentation.IS_MULTILINE_ALLOWED, flag);
	}

	public Boolean getLineWrap() {
		return getPropertyValue(GraphicalRepresentation.LINE_WRAP);
	}

	public void setLineWrap(Boolean flag) {
		setPropertyValue(GraphicalRepresentation.LINE_WRAP, flag);
	}

}
