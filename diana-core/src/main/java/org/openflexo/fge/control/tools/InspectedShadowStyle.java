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

import java.util.List;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.control.DianaInteractiveViewer;

/**
 * Implementation of {@link ShadowStyle}, as a container of graphical properties synchronized with and reflecting a selection<br>
 * This is the object beeing represented in tool inspectors
 * 
 * @author sylvain
 * 
 */
public class InspectedShadowStyle extends InspectedStyle<ShadowStyle> implements ShadowStyle {

	public InspectedShadowStyle(DianaInteractiveViewer<?, ?, ?> controller) {
		super(controller, controller != null ? controller.getFactory().makeDefaultShadowStyle() : null);
	}

	@Override
	public List<ShapeNode<?>> getSelection() {
		return getController().getSelectedShapes();
	}

	@Override
	public ShadowStyle getStyle(DrawingTreeNode<?, ?> node) {
		if (node instanceof ShapeNode) {
			return ((ShapeNode<?>) node).getShadowStyle();
		}
		return null;
	}

	@Override
	public boolean getDrawShadow() {
		return getPropertyValue(ShadowStyle.DRAW_SHADOW);
	}

	@Override
	public void setDrawShadow(boolean aFlag) {
		setPropertyValue(ShadowStyle.DRAW_SHADOW, aFlag);
	}

	@Override
	public int getShadowDarkness() {
		return getPropertyValue(ShadowStyle.SHADOW_DARKNESS);
	}

	@Override
	public void setShadowDarkness(int aValue) {
		setPropertyValue(ShadowStyle.SHADOW_DARKNESS, aValue);
	}

	@Override
	public int getShadowDepth() {
		return getPropertyValue(ShadowStyle.SHADOW_DEPTH);
	}

	@Override
	public void setShadowDepth(int aValue) {
		setPropertyValue(ShadowStyle.SHADOW_DEPTH, aValue);
	}

	@Override
	public int getShadowBlur() {
		return getPropertyValue(ShadowStyle.SHADOW_BLUR);
	}

	@Override
	public void setShadowBlur(int aValue) {
		setPropertyValue(ShadowStyle.SHADOW_BLUR, aValue);
	}

}
