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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Logger;

import org.openflexo.diana.FGEModelFactory;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.control.DianaInteractiveEditor;
import org.openflexo.diana.control.actions.ToolAction;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.graphics.FGEGraphics;
import org.openflexo.diana.view.DrawingView;
import org.openflexo.model.undo.CompoundEdit;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Abstract implementation of a Tool controller
 * 
 * @author sylvain
 * 
 * @param <ME>
 *            technology-specific controlling events type
 */
public abstract class ToolController<ME> implements PropertyChangeListener, HasPropertyChangeSupport {

	private static final Logger logger = Logger.getLogger(ToolController.class.getPackage().getName());

	private final DianaInteractiveEditor<?, ?, ?> controller;

	private boolean editionHasBeenStarted = false;

	private final PropertyChangeSupport pcSupport;

	private final ToolAction toolAction;

	public ToolController(DianaInteractiveEditor<?, ?, ?> controller, ToolAction toolAction) {
		super();
		pcSupport = new PropertyChangeSupport(this);
		this.controller = controller;
		this.toolAction = toolAction;
		editionHasBeenStarted = false;

	}

	public abstract FGEGraphics getGraphics();

	/**
	 * Return the DrawingView of the controller this tool is associated to
	 * 
	 * @return
	 */
	public DrawingView<?, ?> getDrawingView() {
		if (getController() != null) {
			return getController().getDrawingView();
		}
		return null;
	}

	public ToolAction getToolAction() {
		return toolAction;
	}

	protected void startMouseEdition(ME e) {
		editionHasBeenStarted = true;
	}

	protected void stopMouseEdition() {
		editionHasBeenStarted = false;
	}

	public boolean editionHasBeenStarted() {
		return editionHasBeenStarted;
	}

	public DianaInteractiveEditor<?, ?, ?> getController() {
		return controller;
	}

	public FGEModelFactory getFactory() {
		return controller.getFactory();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		logger.info("propertyChange in DrawCustomShapeToolController with " + evt);
	}

	/**
	 * Return point where event occurs, relative to DrawingView
	 */
	public abstract FGEPoint getPoint(ME e);

	/**
	 * Process mouse cliked event for current controller
	 * 
	 * @param e
	 * @return true if the event has been consumed (means that the event processing should stop)
	 */
	public boolean mouseClicked(ME e) {
		// System.out.println("mouseClicked() on " + getPoint(e));
		return false;
	}

	/**
	 * Process mouse pressed event for current controller
	 * 
	 * @param e
	 * @return true if the event has been consumed (means that the event processing should stop)
	 */
	public boolean mousePressed(ME e) {
		// System.out.println("mousePressed() on " + getPoint(e));
		return false;
	}

	/**
	 * Process mouse released event for current controller
	 * 
	 * @param e
	 * @return true if the event has been consumed (means that the event processing should stop)
	 */
	public boolean mouseReleased(ME e) {
		// System.out.println("mouseReleased() on " + getPoint(e));
		return false;
	}

	/**
	 * Process mouse dragged event for current controller
	 * 
	 * @param e
	 * @return true if the event has been consumed (means that the event processing should stop)
	 */
	public boolean mouseDragged(ME e) {
		// System.out.println("mouseDragged() on " + getPoint(e));
		return false;
	}

	/**
	 * Process mouse moved event for current controller
	 * 
	 * @param e
	 * @return true if the event has been consumed (means that the event processing should stop)
	 */
	public boolean mouseMoved(ME e) {
		// System.out.println("mouseMoved() on " + getPoint(e));
		return false;
	}

	/**
	 * Process mouse entered event for current controller
	 * 
	 * @param e
	 * @return true if the event has been consumed (means that the event processing should stop)
	 */
	public boolean mouseEntered(ME e) {
		// System.out.println("mouseEntered() on " + getPoint(e));
		return false;
	}

	/**
	 * Process mouse exited event for current controller
	 * 
	 * @param e
	 * @return true if the event has been consumed (means that the event processing should stop)
	 */
	public boolean mouseExited(ME e) {
		// System.out.println("mouseEntered() on " + getPoint(e));
		return false;
	}

	public abstract DrawingTreeNode<?, ?> getFocusedObject(ME e);

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	protected CompoundEdit startRecordEdit(String editName) {
		if (controller.getUndoManager() != null && !controller.getUndoManager().isUndoInProgress()
				&& !controller.getUndoManager().isRedoInProgress()) {
			return controller.getUndoManager().startRecording(editName);
		}
		return null;
	}

	protected void stopRecordEdit(CompoundEdit edit) {
		if (edit != null && controller.getUndoManager() != null) {
			controller.getUndoManager().stopRecording(edit);
		}
	}

	public void delete() {
		// TODO
		logger.warning("Please implement deletion for ToolController");
	}

}
