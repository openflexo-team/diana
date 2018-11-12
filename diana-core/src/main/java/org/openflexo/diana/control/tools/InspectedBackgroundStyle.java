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

import java.util.List;

import org.openflexo.diana.BackgroundStyle;
import org.openflexo.diana.BackgroundStyle.BackgroundStyleType;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.GeometricGraphicalRepresentation;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.control.DianaInteractiveViewer;
import org.openflexo.pamela.undo.CompoundEdit;

/**
 * Implementation of {@link BackgroundStyle}, as a container of graphical properties synchronized with and reflecting a selection<br>
 * This is the object beeing represented in tool inspectors
 * 
 * @author sylvain
 * 
 */
public class InspectedBackgroundStyle extends InspectedStyleUsingFactory<BackgroundStyleFactory, BackgroundStyle, BackgroundStyleType> {

	public InspectedBackgroundStyle(DianaInteractiveViewer<?, ?, ?> controller) {
		super(controller, new BackgroundStyleFactory(controller));
	}

	@Override
	public List<DrawingTreeNode<?, ?>> getSelection() {
		return getController().getSelectedShapesAndGeometricObjects();
	}

	@Override
	public BackgroundStyle getStyle(DrawingTreeNode<?, ?> node) {
		if (node instanceof ShapeNode) {
			return ((ShapeNode<?>) node).getBackgroundStyle();
		}
		else if (node instanceof GeometricNode) {
			return ((GeometricNode<?>) node).getBackgroundStyle();
		}
		return null;
	}

	@Override
	protected BackgroundStyleType getStyleType(BackgroundStyle style) {
		if (style != null) {
			return style.getBackgroundStyleType();
		}
		return null;
	}

	@Override
	protected void applyNewStyle(BackgroundStyleType aStyleType, DrawingTreeNode<?, ?> node) {

		if (node instanceof ShapeNode) {
			ShapeNode<?> n = (ShapeNode<?>) node;
			BackgroundStyle oldStyle = n.getBackgroundStyle();
			CompoundEdit setValueEdit = startRecordEdit("Set BackgroundStyleType to " + aStyleType);
			n.setBackgroundStyle(getStyleFactory().makeNewStyle(oldStyle));
			if (oldStyle != null) {
				n.getPropertyChangeSupport().firePropertyChange(ShapeGraphicalRepresentation.BACKGROUND_STYLE_TYPE_KEY,
						oldStyle.getBackgroundStyleType(), aStyleType);
			}
			stopRecordEdit(setValueEdit);
		}
		else if (node instanceof GeometricNode) {
			GeometricNode<?> n = (GeometricNode<?>) node;
			BackgroundStyle oldStyle = n.getBackgroundStyle();
			CompoundEdit setValueEdit = startRecordEdit("Set BackgroundStyleType to " + aStyleType);
			BackgroundStyle newStyle = getStyleFactory().makeNewStyle(oldStyle);
			n.setBackgroundStyle(newStyle);

			if (oldStyle != null) {
				n.getPropertyChangeSupport().firePropertyChange(GeometricGraphicalRepresentation.BACKGROUND_STYLE_TYPE_KEY,
						oldStyle.getBackgroundStyleType(), aStyleType);
			}
			stopRecordEdit(setValueEdit);
		}
	}
}
