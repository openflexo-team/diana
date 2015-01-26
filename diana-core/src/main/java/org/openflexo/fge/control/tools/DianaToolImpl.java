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

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.view.DianaViewFactory;
import org.openflexo.model.undo.CompoundEdit;

public abstract class DianaToolImpl<C, F extends DianaViewFactory<F, ?>> implements DianaTool<C, F>, PropertyChangeListener {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DianaToolImpl.class.getPackage().getName());

	private AbstractDianaEditor<?, F, ?> editor;

	/**
	 * Return the editor this tool is associated to
	 * 
	 * @return
	 */
	@Override
	public AbstractDianaEditor<?, F, ?> getEditor() {
		return editor;
	}

	/**
	 * Sets the editor this tool is associated to
	 * 
	 * @param editor
	 */
	@Override
	public void attachToEditor(AbstractDianaEditor<?, F, ?> editor) {
		if (this.editor != editor) {
			if (this.editor != null) {
				// This tool was associated to another editor, disconnect it
				this.editor.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			if (editor != null) {
				// Connect this tool to the new editor
				editor.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			this.editor = editor;
		}
	}

	public List<DrawingTreeNode<?, ?>> getSelection() {
		if (getEditor() instanceof DianaInteractiveViewer) {
			return ((DianaInteractiveViewer<?, F, ?>) getEditor()).getSelectedObjects();
		}
		return null;
	}

	public List<ShapeNode<?>> getSelectedShapes() {
		if (getEditor() instanceof DianaInteractiveViewer) {
			return ((DianaInteractiveViewer<?, F, ?>) getEditor()).getSelectedShapes();
		}
		return null;
	}

	public List<ConnectorNode<?>> getSelectedConnectors() {
		if (getEditor() instanceof DianaInteractiveViewer) {
			return ((DianaInteractiveViewer<?, F, ?>) getEditor()).getSelectedConnectors();
		}
		return null;
	}

	protected CompoundEdit startRecordEdit(String editName) {
		if (getEditor().getUndoManager() != null && !getEditor().getUndoManager().isUndoInProgress()
				&& !getEditor().getUndoManager().isRedoInProgress()) {
			return getEditor().getUndoManager().startRecording(editName);
		}
		return null;
	}

	protected void stopRecordEdit(CompoundEdit edit) {
		if (edit != null && getEditor().getUndoManager() != null) {
			getEditor().getUndoManager().stopRecording(edit);
		}
	}

}
