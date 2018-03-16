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

package org.openflexo.diana.control.tools;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import org.openflexo.diana.TextStyle;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.control.DianaInteractiveViewer;

/**
 * Implementation of {@link TextStyle}, as a container of graphical properties synchronized with and reflecting a selection<br>
 * This is the object beeing represented in tool inspectors
 * 
 * @author sylvain
 * 
 */
public class InspectedTextStyle extends InspectedStyle<TextStyle> implements TextStyle {

	public InspectedTextStyle(DianaInteractiveViewer<?, ?, ?> controller) {
		super(controller, controller != null ? controller.getFactory().makeDefaultTextStyle() : null);
	}

	@Override
	public List<DrawingTreeNode<?, ?>> getSelection() {
		return getController().getSelectedObjects(ShapeNode.class, ConnectorNode.class, GeometricNode.class);
	}

	@Override
	public TextStyle getStyle(DrawingTreeNode<?, ?> node) {
		return node.getTextStyle();
	}

	@Override
	public Color getColor() {
		return getPropertyValue(TextStyle.COLOR);
	}

	@Override
	public void setColor(Color aColor) {
		setPropertyValue(TextStyle.COLOR, aColor);
	}

	@Override
	public Color getBackgroundColor() {
		return getPropertyValue(TextStyle.BACKGROUND_COLOR);
	}

	@Override
	public void setBackgroundColor(Color aColor) {
		setPropertyValue(TextStyle.BACKGROUND_COLOR, aColor);
	}

	@Override
	public Font getFont() {
		return getPropertyValue(TextStyle.FONT);
	}

	@Override
	public void setFont(Font aFont) {
		setPropertyValue(TextStyle.FONT, aFont);
	}

	@Override
	public int getOrientation() {
		try {
			return getPropertyValue(TextStyle.ORIENTATION);
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println("OK, je vois que ca chie la");
			System.out.println("En pas a pas");
			return getPropertyValue(TextStyle.ORIENTATION);
		}
	}

	@Override
	public void setOrientation(int anOrientation) {
		setPropertyValue(TextStyle.ORIENTATION, anOrientation);
	}

	@Override
	public boolean getIsBackgroundColored() {
		return getPropertyValue(TextStyle.IS_BACKGROUND_COLORED);
	}

	@Override
	public void setIsBackgroundColored(boolean aFlag) {
		setPropertyValue(TextStyle.IS_BACKGROUND_COLORED, aFlag);
	}

}
