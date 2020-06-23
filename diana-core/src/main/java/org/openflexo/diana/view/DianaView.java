/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.diana.view;

import java.awt.dnd.DropTarget;
import java.beans.PropertyChangeListener;

import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.DianaEditor;
import org.openflexo.diana.graphics.DianaGraphics;

/**
 * Implemented by all views representing a DrawingTreeNode<br>
 * The implementation of this interface is technology specific, and is guaranteed to be an instance of (or s subclass of) C<br>
 * 
 * A {@link DianaView} manages a {@link DianaGraphics}.<br>
 * A {@link DianaView} is painted using {@link #paint()} method.
 * 
 * @author sylvain
 * 
 * @param <O>
 *            type of object being represented by this view
 * @param <C>
 *            type of component this view is being instance (technology-specific)
 */
public interface DianaView<O, C> extends PropertyChangeListener, DianaConstants {

	/**
	 * Return the {@link DianaEditor} in the context where this {@link DianaView} is defined
	 * 
	 * @return
	 */
	public AbstractDianaEditor<?, ?, ? super C> getController();

	/**
	 * Return the {@link DrawingTreeNode} this view represents
	 * 
	 * @return
	 */
	public DrawingTreeNode<O, ?> getNode();

	/**
	 * Return the root view of the context where this view is defined
	 * 
	 * @return
	 */
	public DrawingView<?, ?> getDrawingView();

	/**
	 * Return parent view for this view (containment semantics)
	 * 
	 * @return
	 */
	public DianaContainerView<?, ?> getParentView();

	/**
	 * Return object actually represented by this view
	 * 
	 * @return
	 */
	public O getDrawable();

	/**
	 * Return {@link DianaGraphics} for this view
	 * 
	 * @return
	 */
	public DianaGraphics getDianaGraphics();

	/**
	 * Return scale this view is beeing represented
	 * 
	 * @return
	 */
	public double getScale();

	/**
	 * Called to rescale the view
	 */
	public void rescale();

	/**
	 * Activate Drag&Drop for this {@link DianaView} if not already activated
	 * 
	 * @return
	 */
	public DropTarget activateDragAndDrop();

	/**
	 * Desactivate Drag&Drop for this {@link DianaView} when activated
	 * 
	 * @return
	 */
	public DropTarget disactivateDragAndDrop();

	/**
	 * Return boolean indicating if drag&drop has been activated for this view
	 * 
	 * @return
	 */
	public boolean isDragAndDropActivated();

	/**
	 * Delete this view
	 */
	public void delete();

	/**
	 * Indicates if the view is deleted
	 * 
	 * @return
	 */
	public boolean isDeleted();

	/**
	 * Called to stop label edition
	 */
	public void stopLabelEdition();

}
