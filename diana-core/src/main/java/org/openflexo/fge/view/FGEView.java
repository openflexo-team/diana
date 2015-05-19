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

package org.openflexo.fge.view;

import java.beans.PropertyChangeListener;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.tools.DianaPalette;
import org.openflexo.fge.graphics.FGEGraphics;

/**
 * Implemented by all views representing a DrawingTreeNode<br>
 * The implementation of this interface is technology specific, and is guaranteed to be an instance of (or s subclass of) C<br>
 * 
 * A {@link FGEView} manages a {@link FGEGraphics}.<br>
 * A {@link FGEView} is painted using {@link #paint()} method.
 * 
 * @author sylvain
 * 
 * @param <O>
 *            type of object beeing represented by this view
 * @param <C>
 *            type of component this view is beeing instance (technology-specific)
 */
public interface FGEView<O, C> extends PropertyChangeListener, FGEConstants {

	public AbstractDianaEditor<?, ?, ? super C> getController();

	public DrawingTreeNode<O, ?> getNode();

	public DrawingView<?, ?> getDrawingView();

	public FGEContainerView<?, ?> getParentView();

	public O getDrawable();

	/**
	 * Return {@link FGEGraphics} for this view
	 * 
	 * @return
	 */
	public FGEGraphics getFGEGraphics();

	public double getScale();

	public void rescale();

	public void activatePalette(DianaPalette<?, ?> aPalette);

	public void delete();

	public boolean isDeleted();

	public void stopLabelEdition();

}
