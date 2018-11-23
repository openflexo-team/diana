/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-swing, a component of the software infrastructure 
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

package org.openflexo.diana.swing;

import javax.swing.JComponent;

import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.Drawing;
import org.openflexo.diana.control.DianaInteractiveEditor;
import org.openflexo.diana.swing.control.SwingToolFactory;
import org.openflexo.diana.swing.view.JDrawingView;

/**
 * This is the SWING implementation of a basic read-only viewer of a {@link Drawing}<br>
 * 
 * The {@link Drawing} can only be viewed, without any editing possibility (shapes are all non-movable)
 * 
 * @author sylvain
 * 
 * @param <M>
 */
public class JDianaInteractiveEditor<M> extends DianaInteractiveEditor<M, SwingViewFactory, JComponent> {

	public JDianaInteractiveEditor(Drawing<M> aDrawing, DianaModelFactory factory, SwingToolFactory toolFactory) {
		super(aDrawing, factory, SwingViewFactory.INSTANCE, toolFactory);
		setDelegate(new SwingEditorDelegate(this));
	}

	public JDianaInteractiveEditor(Drawing<M> aDrawing, DianaModelFactory factory, SwingViewFactory viewFactory,
			SwingToolFactory toolFactory) {
		super(aDrawing, factory, viewFactory, toolFactory);
		setDelegate(new SwingEditorDelegate(this));
	}

	@SuppressWarnings("unchecked")
	@Override
	public JDrawingView<M> getDrawingView() {
		return (JDrawingView<M>) super.getDrawingView();
	}

	@Override
	public SwingEditorDelegate getDelegate() {
		return (SwingEditorDelegate) super.getDelegate();
	}

	@Override
	public void setCurrentTool(org.openflexo.diana.control.DianaInteractiveEditor.EditorTool aTool) {
		super.setCurrentTool(aTool);
		if (getDrawingView() != null && getDrawingView().getPaintManager() != null) {
			getDrawingView().getPaintManager().repaint(getDrawingView());
		}
	}
}
