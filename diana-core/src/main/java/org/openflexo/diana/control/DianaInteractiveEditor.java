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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.diana.Drawing;
import org.openflexo.diana.FGEModelFactory;
import org.openflexo.diana.FGEUtils;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.diana.control.actions.DrawConnectorAction;
import org.openflexo.diana.control.actions.DrawShapeAction;
import org.openflexo.diana.control.notifications.SelectionCopied;
import org.openflexo.diana.control.notifications.ToolChanged;
import org.openflexo.diana.control.notifications.ToolOptionChanged;
import org.openflexo.diana.control.tools.DianaPalette;
import org.openflexo.diana.control.tools.DrawConnectorToolController;
import org.openflexo.diana.control.tools.DrawCustomShapeToolController;
import org.openflexo.diana.control.tools.DrawShapeToolController;
import org.openflexo.diana.control.tools.DrawTextToolController;
import org.openflexo.diana.control.tools.InspectedBackgroundStyle;
import org.openflexo.diana.control.tools.InspectedConnectorSpecification;
import org.openflexo.diana.control.tools.InspectedForegroundStyle;
import org.openflexo.diana.control.tools.InspectedLayoutManagerSpecifications;
import org.openflexo.diana.control.tools.InspectedLocationSizeProperties;
import org.openflexo.diana.control.tools.InspectedShadowStyle;
import org.openflexo.diana.control.tools.InspectedShapeSpecification;
import org.openflexo.diana.control.tools.InspectedTextProperties;
import org.openflexo.diana.control.tools.InspectedTextStyle;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.view.DianaViewFactory;
import org.openflexo.exceptions.CopyException;
import org.openflexo.exceptions.CutException;
import org.openflexo.exceptions.PasteException;
import org.openflexo.model.factory.Clipboard;

/**
 * Represents an editor of a {@link Drawing}<br>
 * 
 * The {@link Drawing} can be fully edited. A {@link DianaInteractiveEditor} generally declares some palettes.<br>
 * 
 * Additionnaly, a {@link DianaInteractiveEditor} manage clipboard operations (copy/cut/paste), and undo/redo operations
 * 
 * @author sylvain
 * 
 * @param <M>
 */
public abstract class DianaInteractiveEditor<M, F extends DianaViewFactory<F, C>, C> extends DianaInteractiveViewer<M, F, C> {

	private static final Logger logger = Logger.getLogger(DianaInteractiveEditor.class.getPackage().getName());

	public static enum EditorTool {
		SelectionTool {
			@Override
			public Collection<EditorToolOption> getOptions() {
				return null;
			}
		},
		DrawShapeTool {
			@Override
			public Collection<DrawShapeToolOption> getOptions() {
				return Arrays.asList(DrawShapeToolOption.values());
			}
		},
		DrawCustomShapeTool {
			@Override
			public Collection<DrawCustomShapeToolOption> getOptions() {
				return Arrays.asList(DrawCustomShapeToolOption.values());
			}
		},
		DrawConnectorTool {
			@Override
			public Collection<DrawConnectorToolOption> getOptions() {
				return Arrays.asList(DrawConnectorToolOption.values());
			}
		},
		DrawTextTool {
			@Override
			public Collection<EditorToolOption> getOptions() {
				return null;
			}
		};
		public abstract Collection<? extends EditorToolOption> getOptions();
	}

	public static interface EditorToolOption {
		public String name();
	}

	public enum DrawShapeToolOption implements EditorToolOption {
		DrawRectangle, DrawOval
	}

	public enum DrawCustomShapeToolOption implements EditorToolOption {
		DrawPolygon, DrawClosedCurve, DrawOpenedCurve, DrawComplexShape
	}

	public enum DrawConnectorToolOption implements EditorToolOption {
		DrawLine, DrawCurve, DrawRectPolylin, DrawCurvedPolylin;
		public ConnectorType getConnectorType() {
			switch (this) {
				case DrawLine:
					return ConnectorType.LINE;
				case DrawCurve:
					return ConnectorType.CURVE;
				case DrawRectPolylin:
					return ConnectorType.RECT_POLYLIN;
				case DrawCurvedPolylin:
					return ConnectorType.CURVED_POLYLIN;
				default:
					return null;
			}
		}
	}

	public static final int PASTE_DELTA = 10;

	private EditorTool currentTool;
	private DrawShapeToolOption drawShapeToolOption;
	private DrawCustomShapeToolOption drawCustomShapeToolOption;
	private DrawConnectorToolOption drawConnectorToolOption;

	private DrawShapeToolController<?> drawShapeToolController;
	private DrawCustomShapeToolController<?, ?> drawCustomShapeToolController;
	private DrawConnectorToolController<?> drawConnectorToolController;
	private DrawTextToolController<?> drawTextToolController;

	private DrawShapeAction drawShapeAction;
	private DrawShapeAction drawCustomShapeAction;
	private DrawConnectorAction drawConnectorAction;
	private DrawShapeAction drawTextAction;

	private final InspectedForegroundStyle inspectedForegroundStyle;
	private final InspectedBackgroundStyle inspectedBackgroundStyle;
	private final InspectedTextStyle inspectedTextStyle;
	private final InspectedTextProperties inspectedTextProperties;
	private final InspectedShadowStyle inspectedShadowStyle;
	private final InspectedShapeSpecification inspectedShapeSpecification;
	private final InspectedConnectorSpecification inspectedConnectorSpecification;
	private final InspectedLocationSizeProperties inspectedLocationSizeProperties;
	private final InspectedLayoutManagerSpecifications inspectedLayoutManagerSpecifications;

	/**
	 * The clipboard beeing managed by this editor
	 */
	private Clipboard clipboard = null;

	/**
	 * The drawing tree node hosting a potential paste
	 */
	private DrawingTreeNode<?, ?> pastingContext;

	/**
	 * The location where applying paste, relative to root
	 */
	private FGEPoint pastingLocation;

	private boolean isSelectingAfterPaste = false;

	public DianaInteractiveEditor(Drawing<M> aDrawing, FGEModelFactory factory, F dianaFactory, DianaToolFactory<C> toolFactory) {
		super(aDrawing, factory, dianaFactory, toolFactory, true, true, true, true, true);
		inspectedForegroundStyle = new InspectedForegroundStyle(this);
		inspectedBackgroundStyle = new InspectedBackgroundStyle(this);
		inspectedTextStyle = new InspectedTextStyle(this);
		inspectedTextProperties = new InspectedTextProperties(this);
		inspectedShadowStyle = new InspectedShadowStyle(this);
		inspectedShapeSpecification = new InspectedShapeSpecification(this);
		inspectedConnectorSpecification = new InspectedConnectorSpecification(this);
		inspectedLocationSizeProperties = new InspectedLocationSizeProperties(this);
		inspectedLayoutManagerSpecifications = new InspectedLayoutManagerSpecifications(this);

		setCurrentTool(EditorTool.SelectionTool);
		setDrawShapeToolOption(DrawShapeToolOption.DrawRectangle);
		setDrawCustomShapeToolOption(DrawCustomShapeToolOption.DrawPolygon);
		setDrawConnectorToolOption(DrawConnectorToolOption.DrawLine);
	}

	@Override
	public void delete() {
		super.delete();
		/*if (toolbox != null) {
			toolbox.delete();
		}
		toolbox = null;*/
		/*if (palettes != null) {
			for (DrawingPalette palette : palettes) {
				palette.delete();
			}
		}
		palettes = null;*/
	}

	@Override
	protected void fireSelectionUpdated() {
		inspectedForegroundStyle.fireSelectionUpdated();
		inspectedTextStyle.fireSelectionUpdated();
		inspectedTextProperties.fireSelectionUpdated();
		inspectedShadowStyle.fireSelectionUpdated();
		inspectedBackgroundStyle.fireSelectionUpdated();
		inspectedShapeSpecification.fireSelectionUpdated();
		inspectedConnectorSpecification.fireSelectionUpdated();
		inspectedLocationSizeProperties.fireSelectionUpdated();
		inspectedLayoutManagerSpecifications.fireSelectionUpdated();
	}

	public DrawShapeToolController<?> getDrawShapeToolController() {
		if (drawShapeToolController == null) {
			prepareDrawShapeToolController();
		}
		return drawShapeToolController;
	}

	public DrawCustomShapeToolController<?, ?> getDrawCustomShapeToolController() {
		if (drawCustomShapeToolController == null) {
			prepareDrawCustomShapeToolController();
		}
		return drawCustomShapeToolController;
	}

	public DrawConnectorToolController<?> getDrawConnectorToolController() {
		if (drawConnectorToolController == null) {
			prepareDrawConnectorToolController();
		}
		return drawConnectorToolController;
	}

	public DrawTextToolController<?> getDrawTextToolController() {
		if (drawTextToolController == null) {
			prepareDrawTextToolController();
		}
		return drawTextToolController;
	}

	public EditorTool getCurrentTool() {
		return currentTool;
	}

	public void setCurrentTool(EditorTool aTool) {
		if (aTool != currentTool) {
			EditorTool oldTool = currentTool;
			logger.fine("Switch to tool " + aTool);
			switch (aTool) {
				case SelectionTool:
					/*if (currentTool == EditorTool.DrawShapeTool && drawShapeToolController != null) {
						drawShapeToolController.makeNewShape();
					}*/
					break;
				case DrawShapeTool:
					break;
				case DrawCustomShapeTool:
					break;
				case DrawConnectorTool:
					break;
				case DrawTextTool:
					break;
				default:
					break;
			}
			currentTool = aTool;
			notifyObservers(new ToolChanged(oldTool, currentTool));
		}
	}

	public DrawShapeToolOption getDrawShapeToolOption() {
		return drawShapeToolOption;
	}

	public void setDrawShapeToolOption(DrawShapeToolOption drawShapeToolOption) {
		if (this.drawShapeToolOption != drawShapeToolOption) {
			DrawShapeToolOption oldToolOption = this.drawShapeToolOption;
			this.drawShapeToolOption = drawShapeToolOption;
			prepareDrawShapeToolController();
			notifyObservers(new ToolOptionChanged(oldToolOption, drawShapeToolOption));
		}
	}

	public DrawCustomShapeToolOption getDrawCustomShapeToolOption() {
		return drawCustomShapeToolOption;
	}

	public void setDrawCustomShapeToolOption(DrawCustomShapeToolOption drawCustomShapeToolOption) {
		if (this.drawCustomShapeToolOption != drawCustomShapeToolOption) {
			DrawCustomShapeToolOption oldToolOption = this.drawCustomShapeToolOption;
			this.drawCustomShapeToolOption = drawCustomShapeToolOption;
			prepareDrawCustomShapeToolController();
			notifyObservers(new ToolOptionChanged(oldToolOption, drawCustomShapeToolOption));
		}
	}

	private void prepareDrawCustomShapeToolController() {
		if (drawCustomShapeToolController != null) {
			drawCustomShapeToolController.delete();
		}
		if (drawCustomShapeAction != null && getToolFactory() != null) {
			switch (getDrawCustomShapeToolOption()) {
				case DrawPolygon:
					drawCustomShapeToolController = getToolFactory().makeDrawPolygonToolController(this, drawCustomShapeAction);
					break;
				case DrawClosedCurve:
					drawCustomShapeToolController = getToolFactory().makeDrawClosedCurveToolController(this, drawCustomShapeAction, true);
					break;
				case DrawOpenedCurve:
					drawCustomShapeToolController = getToolFactory().makeDrawClosedCurveToolController(this, drawCustomShapeAction, false);
					break;
				default:
					logger.warning("Not implemented: " + getDrawCustomShapeToolOption());
			}
		}

	}

	private void prepareDrawShapeToolController() {
		if (drawShapeToolController != null) {
			drawShapeToolController.delete();
		}
		if (drawShapeAction != null && getToolFactory() != null) {
			System.out.println("Preparing the prepareDrawShapeToolController");
			drawShapeToolController = getToolFactory().makeDrawShapeToolController(this, drawShapeAction);
			switch (getDrawShapeToolOption()) {
				default:
					logger.warning("Not implemented: " + getDrawShapeToolOption());
			}
		}

	}

	private void prepareDrawConnectorToolController() {
		if (drawConnectorToolController != null) {
			drawConnectorToolController.delete();
		}
		if (drawConnectorAction != null) {
			System.out.println("Preparing the DrawConnectorToolController");
			drawConnectorToolController = getToolFactory().makeDrawConnectorToolController(this, drawConnectorAction);
			switch (getDrawConnectorToolOption()) {
				default:
					logger.warning("Not implemented: " + getDrawConnectorToolOption());
			}
		}

	}

	private void prepareDrawTextToolController() {
		if (drawTextToolController != null) {
			drawTextToolController.delete();
		}
		if (drawTextAction != null) {
			System.out.println("Preparing the prepareDrawTextToolController");
			drawTextToolController = getToolFactory().makeDrawTextToolController(this, drawTextAction);
		}

	}

	public DrawConnectorToolOption getDrawConnectorToolOption() {
		return drawConnectorToolOption;
	}

	public void setDrawConnectorToolOption(DrawConnectorToolOption drawConnectorToolOption) {
		if (this.drawConnectorToolOption != drawConnectorToolOption) {
			DrawConnectorToolOption oldToolOption = this.drawConnectorToolOption;
			this.drawConnectorToolOption = drawConnectorToolOption;
			getInspectedConnectorSpecification().getStyleFactory().setStyleType(drawConnectorToolOption.getConnectorType());
			prepareDrawConnectorToolController();
			notifyObservers(new ToolOptionChanged(oldToolOption, drawConnectorToolOption));
		}
	}

	public InspectedForegroundStyle getInspectedForegroundStyle() {
		return inspectedForegroundStyle;
	}

	public InspectedBackgroundStyle getInspectedBackgroundStyle() {
		return inspectedBackgroundStyle;
	}

	public InspectedTextStyle getInspectedTextStyle() {
		return inspectedTextStyle;
	}

	public InspectedTextProperties getInspectedTextProperties() {
		return inspectedTextProperties;
	}

	public InspectedShadowStyle getInspectedShadowStyle() {
		return inspectedShadowStyle;
	}

	public InspectedShapeSpecification getInspectedShapeSpecification() {
		return inspectedShapeSpecification;
	}

	public InspectedConnectorSpecification getInspectedConnectorSpecification() {
		return inspectedConnectorSpecification;
	}

	public InspectedLocationSizeProperties getInspectedLocationSizeProperties() {
		return inspectedLocationSizeProperties;
	}

	public InspectedLayoutManagerSpecifications getInspectedLayoutManagerSpecifications() {
		return inspectedLayoutManagerSpecifications;
	}

	public DrawShapeAction getDrawShapeAction() {
		return drawShapeAction;
	}

	public void setDrawShapeAction(DrawShapeAction drawShapeAction) {
		this.drawShapeAction = drawShapeAction;
		prepareDrawShapeToolController();
	}

	public DrawShapeAction getDrawCustomShapeAction() {
		return drawCustomShapeAction;
	}

	public void setDrawCustomShapeAction(DrawShapeAction drawCustomShapeAction) {
		this.drawCustomShapeAction = drawCustomShapeAction;
		prepareDrawCustomShapeToolController();
	}

	public DrawConnectorAction getDrawConnectorAction() {
		return drawConnectorAction;
	}

	public void setDrawConnectorAction(DrawConnectorAction drawConnectorAction) {
		this.drawConnectorAction = drawConnectorAction;
		prepareDrawConnectorToolController();
	}

	public DrawShapeAction getDrawTextAction() {
		return drawTextAction;
	}

	public void setDrawTextAction(DrawShapeAction drawTextAction) {
		this.drawTextAction = drawTextAction;
		prepareDrawTextToolController();
	}

	public void activatePalette(DianaPalette<?, ?> aPalette) {
		if (getDrawingView() != null) {
			getDrawingView().activatePalette(aPalette);
		}
	}

	public Clipboard getClipboard() {
		return clipboard;
	}

	/**
	 * Return boolean indicating if the current selection is suitable for a COPY action
	 * 
	 * @return
	 */
	public boolean isCopiable() {
		if (getSelectedObjects().size() == 1) {
			if (getSelectedObjects().get(0) == getDrawing().getRoot()) {
				return false;
			}
			return true;
		}
		return getSelectedObjects().size() > 0;
	}

	/**
	 * Copy current selection in the clipboard
	 * 
	 * @throws CopyException
	 */
	public Clipboard copy() throws CopyException {
		if (getSelectedObjects().size() == 0) {
			System.out.println("Nothing to copy");
			return null;
		}

		Object[] objectsToBeCopied = makeArrayOfObjectsToBeCopied(getSelectedObjects());

		try {
			clipboard = getFactory().copy(objectsToBeCopied);
		} catch (Throwable e) {
			throw new CopyException(e);
		}

		// System.out.println(clipboard.debug());

		pastingContext = FGEUtils.getFirstCommonAncestor(getSelectedObjects());
		// System.out.println("Pasting context = " + pastingContext);

		notifyObservers(new SelectionCopied(clipboard));

		return clipboard;
	}

	/**
	 * Return boolean indicating if the current selection is suitable for a PASTE action in supplied context and position
	 * 
	 * @return
	 */
	public boolean isPastable() {
		return clipboard != null && pastingContext != null;
	}

	/**
	 * Paste current Clipboard in supplied context and position
	 * 
	 * @throws PasteException
	 * 
	 */
	public void paste() throws PasteException {

		if (clipboard != null) {

			// System.out.println("Pasting in " + pastingContext + " at "+pastingLocation);
			FGEPoint p = FGEUtils.convertNormalizedPoint(getDrawing().getRoot(), pastingLocation, pastingContext);

			// This point is valid for RootNode, but need to be translated in a ShapeNode
			if (pastingContext instanceof ShapeNode) {
				p.x = p.x * ((ShapeNode<?>) pastingContext).getWidth();
				p.y = p.y * ((ShapeNode<?>) pastingContext).getHeight();
			}

			prepareClipboardForPasting(p);

			// Prevent pastingContext to be changed
			isSelectingAfterPaste = true;

			// Do the paste
			try {
				Object pasted = getFactory().paste(clipboard, pastingContext.getDrawable());

				// Try to select newly created objects
				clearSelection();
				if (clipboard.isSingleObject()) {
					addToSelectedObjects(getDrawing().getDrawingTreeNode(pasted));
				}
				else {
					for (Object o : (List<?>) pasted) {
						addToSelectedObjects(getDrawing().getDrawingTreeNode(o));
					}
				}
			} catch (Throwable e) {
				throw new PasteException(e);
			}

			// OK, now we can track again new selection to set pastingContext
			isSelectingAfterPaste = false;

			pastingLocation.x = pastingLocation.x + PASTE_DELTA;
			pastingLocation.y = pastingLocation.y + PASTE_DELTA;

		}
	}

	/**
	 * Return boolean indicating if the current selection is suitable for a CUT action
	 * 
	 * @return
	 */
	public boolean isCutable() {
		return isCopiable();
	}

	/**
	 * Cut current selection, by deleting selecting contents while copying it in the clipboard for a future use
	 * 
	 * @throws CutException
	 */
	public Clipboard cut() throws CutException {
		if (getSelectedObjects().size() == 0) {
			System.out.println("Nothing to cut");
			return null;
		}

		Object[] objectsToBeCopied = makeArrayOfObjectsToBeCopied(getSelectedObjects());

		try {
			clipboard = getFactory().cut(objectsToBeCopied);
		} catch (Throwable e) {
			throw new CutException(e);
		}

		// System.out.println(clipboard.debug());

		return clipboard;

	}

	/**
	 * Returns true if edits may be undone.<br>
	 * If en edition is in progress, return true if stopping this edition will cause UndoManager to be able to undo
	 * 
	 * 
	 * @return true if there are edits to be undone
	 */
	public boolean canUndo() {
		return getUndoManager() != null && getUndoManager().canUndoIfStoppingCurrentEdition();
	}

	/**
	 * Undoes appropriate edit
	 */
	public void undo() {
		logger.info("UNDO called");
		getUndoManager().debug();
		if (getUndoManager().canUndo()) {
			logger.info("Undoing: " + getUndoManager().editToBeUndone().getPresentationName());
			getUndoManager().undo();
		}
		else {
			if (getUndoManager().canUndoIfStoppingCurrentEdition()) {
				getUndoManager().stopRecording(getUndoManager().getCurrentEdition());
				if (getUndoManager().canUndo()) {
					logger.info("Undoing: " + getUndoManager().editToBeUndone().getPresentationName());
					getUndoManager().undo();
				}
			}
		}
	}

	/**
	 * Returns true if edits may be redone.<br>
	 * 
	 * @return true if there are edits to be undone
	 */
	public boolean canRedo() {
		return getUndoManager() != null && getUndoManager().canRedo();
	}

	/**
	 * Redoes appropriate edit
	 */
	public void redo() {
		logger.info("REDO called");
		if (getUndoManager().canRedo()) {
			logger.info("Redoing: " + getUndoManager().editToBeRedone().getPresentationName());
			getUndoManager().redo();
		}
	}

	/**
	 * Internal method used to build an array with drawable objects
	 * 
	 * @param aSelection
	 * @return
	 */
	private Object[] makeArrayOfObjectsToBeCopied(List<DrawingTreeNode<?, ?>> aSelection) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Making copying selection with " + getSelectedObjects());
		}
		Object[] objectsToBeCopied = new Object[aSelection.size()];
		int i = 0;
		for (DrawingTreeNode<?, ?> dtn : aSelection) {
			objectsToBeCopied[i] = dtn.getDrawable();
			// System.out.println("object: " + objectsToBeCopied[i] + " gr=" + getSelectedObjects().get(i));
			// System.out.println("Copied: " + getFactory().stringRepresentation(objectsToBeCopied[i]));
			i++;
		}
		return objectsToBeCopied;
	}

	/**
	 * Return the drawing tree node hosting a potential paste
	 * 
	 * @return
	 */
	public DrawingTreeNode<?, ?> getPastingContext() {
		return pastingContext;
	}

	/**
	 * Return the location where applying paste, relative to root
	 * 
	 * @return
	 */
	public FGEPoint getPastingLocation() {
		return pastingLocation;
	}

	@Override
	public void setLastClickedPoint(FGEPoint lastClickedPoint, DrawingTreeNode<?, ?> node) {

		super.setLastClickedPoint(lastClickedPoint, node);
		pastingLocation = FGEUtils.convertNormalizedPoint(node, lastClickedPoint, getDrawing().getRoot());

	};

	@Override
	public void addToSelectedObjects(DrawingTreeNode<?, ?> aNode) {
		super.addToSelectedObjects(aNode);
		if (!isSelectingAfterPaste) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Pasting context set to " + pastingContext);
			}
			pastingContext = aNode;
		}
	}

	@Override
	public void clearSelection() {
		super.clearSelection();
		if (!isSelectingAfterPaste) {
			pastingContext = getDrawing().getRoot();
		}
	}

	/**
	 * This is a hook to set and/or translate some properties of clipboard beeing pasted<br>
	 * This is model-specific, and thus, default implementation does nothing. Please override this
	 * 
	 * @param proposedPastingLocation
	 */
	protected void prepareClipboardForPasting(FGEPoint proposedPastingLocation) {
	}

}
