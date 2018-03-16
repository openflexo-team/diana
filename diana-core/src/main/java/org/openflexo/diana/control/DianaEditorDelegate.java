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

package org.openflexo.diana.control;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Set;

import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.view.FGEView;

/**
 * This A.P.I defines an helper class used as a hook provider to implement technology-specific schemes<br>
 * A {@link DianaEditorDelegate} works with the {@link AbstractDianaEditor}
 * 
 * @author sylvain
 * 
 */
public interface DianaEditorDelegate {

	public void focusedObjectChanged(DrawingTreeNode<?, ?> oldFocusedObject, DrawingTreeNode<?, ?> newFocusedObject);

	public void objectStartMoving(DrawingTreeNode<?, ?> node);

	public void objectStopMoving(DrawingTreeNode<?, ?> node);

	public void objectsStartMoving(Set<? extends DrawingTreeNode<?, ?>> nodes);

	public void objectsStopMoving(Set<? extends DrawingTreeNode<?, ?>> nodes);

	public void repaintAll();

	public Point getPointInView(Object source, Point point, FGEView<?, ?> view);

	/**
	 * Makes a screenshot of supplied graphical node
	 * 
	 * @param node
	 * @return
	 */
	public BufferedImage makeScreenshot(DrawingTreeNode<?, ?> node);

}
