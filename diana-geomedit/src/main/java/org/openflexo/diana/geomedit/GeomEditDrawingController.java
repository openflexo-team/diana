/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geomedit, a component of the software infrastructure 
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

package org.openflexo.diana.geomedit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geomedit.controller.ContextualMenu;
import org.openflexo.diana.geomedit.controller.GeneralContextualMenu;
import org.openflexo.diana.geomedit.controller.InspectedNodeShapeSpecification;
import org.openflexo.diana.geomedit.edition.Edition;
import org.openflexo.diana.geomedit.edition.EditionInput;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstructionFactory;
import org.openflexo.diana.geomedit.model.GeometricDiagram;
import org.openflexo.diana.geomedit.model.NodeConstruction;
import org.openflexo.diana.geomedit.view.GeomEditDrawingView;
import org.openflexo.diana.swing.JDianaInteractiveEditor;
import org.openflexo.diana.swing.control.SwingToolFactory;
import org.openflexo.diana.view.DianaView;
import org.openflexo.exceptions.CopyException;
import org.openflexo.exceptions.CutException;
import org.openflexo.exceptions.PasteException;
import org.openflexo.pamela.factory.Clipboard;

/**
 * A controller managing the drawing edition.
 * 
 * This is a {@link JDianaInteractiveEditor}, and it works with a {@link GeomEditEditor}
 * 
 * @author sylvain
 *
 */
public class GeomEditDrawingController extends JDianaInteractiveEditor<GeometricDiagram> {

	private static final Logger logger = Logger.getLogger(GeomEditDrawingController.class.getPackage().getName());

	private GeneralContextualMenu generalContextualMenu;
	private ContextualMenu contextualMenu;

	// TODO: move this to its own view: GeomEditDrawingPanel
	private JPanel controlPanel;
	private JPanel availableMethodsPanel;
	private JLabel editionLabel;
	private JButton cancelButton;
	private JLabel positionLabel;

	private final GeomEditEditor editor;

	private String NO_EDITION_STRING = "No edition";

	private InspectedNodeShapeSpecification inspectedNodeShapeSpecification;

	public GeomEditDrawingController(final GeomEditEditor editor, final GeometricDiagramDrawing aDrawing,
			GeometricConstructionFactory factory, SwingToolFactory toolFactory) {
		super(aDrawing, factory, toolFactory);
		this.editor = editor;

		generalContextualMenu = new GeneralContextualMenu(this);

		contextualMenu = new ContextualMenu(this, editor.getApplication().getFrame());

		controlPanel = new JPanel(new BorderLayout());
		availableMethodsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		controlPanel.add(availableMethodsPanel, BorderLayout.WEST);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetCurrentInput();
			}
		});
		controlPanel.add(cancelButton, BorderLayout.EAST);

		controlPanel.add(availableMethodsPanel, BorderLayout.WEST);

		editionLabel = new JLabel(NO_EDITION_STRING);

		positionLabel = new JLabel("                       ");

		resetCurrentInput();

		inspectedNodeShapeSpecification = new InspectedNodeShapeSpecification(this);
	}

	@Override
	public InspectedNodeShapeSpecification getInspectedShapeSpecification() {
		return inspectedNodeShapeSpecification;
	}

	public GeomEditEditor getEditor() {
		return editor;
	}

	public ContextualMenu getContextualMenu() {
		return contextualMenu;
	}

	public GeometricDiagram getDiagram() {
		return getDrawing().getModel();
	}

	@Override
	public GeometricDiagramDrawing getDrawing() {
		return (GeometricDiagramDrawing) super.getDrawing();
	}

	@Override
	public GeometricConstructionFactory getFactory() {
		return (GeometricConstructionFactory) super.getFactory();
	}

	public void showGeneralContextualMenu(DianaView<?, ?> view, Point p) {
		// contextualMenuInvoker = dtn;
		// contextualMenuClickedPoint = p;
		generalContextualMenu.show((Component) view, p.x, p.y);
	}

	public void showContextualMenu(DrawingTreeNode<?, ?> dtn, DianaView view, Point p) {
		getContextualMenu().displayPopupMenu((GeometricConstruction<?>) dtn.getDrawable(), getDrawingView(), p);
	}

	@Override
	public GeomEditDrawingView makeDrawingView() {
		return new GeomEditDrawingView(this);
	}

	@Override
	public GeomEditDrawingView getDrawingView() {
		return (GeomEditDrawingView) super.getDrawingView();
	}

	private Edition currentEdition = null;

	public Edition getCurrentEdition() {
		return currentEdition;
	}

	public void setCurrentEdition(Edition anEdition) {
		currentEdition = anEdition;
		if (anEdition != null) {
			updateCurrentInput();
		}
		else {
			editionLabel.setText(NO_EDITION_STRING);
			editionLabel.revalidate();
			editionLabel.repaint();
			resetCurrentInput();
		}
	}

	private EditionInput currentInput;

	public void updateCurrentInput() {
		cancelButton.setEnabled(true);
		// controlPanel.setVisible(true);
		currentInput = currentEdition.inputs.get(currentEdition.currentStep);
		currentInput.updateControlPanel(controlPanel, availableMethodsPanel);
		editionLabel.setText(currentEdition.getLabel() + ", " + currentInput.getInputLabel() + ", " + currentInput.getActiveMethodLabel()
				+ (currentInput.endOnRightClick() ? " (right-click to finish)" : ""));
		editionLabel.revalidate();
		editionLabel.repaint();
		if (generalContextualMenu.isShowing()) {
			generalContextualMenu.setVisible(false);
		}
		getDrawingView().enableEditionInputMethod(currentInput.getDerivedActiveMethod());
	}

	private void resetCurrentInput() {
		if (currentInput != null) {
			currentInput.resetControlPanel(controlPanel);
		}
		availableMethodsPanel.removeAll();
		availableMethodsPanel.revalidate();
		availableMethodsPanel.repaint();
		// controlPanel.setVisible(false);
		cancelButton.setEnabled(false);
		editionLabel.setText(NO_EDITION_STRING);
		editionLabel.revalidate();
		editionLabel.repaint();
		getDrawingView().disableEditionInputMethod();
		currentEdition = null;
		currentInput = null;
		getDrawingView().revalidate();
		getDrawingView().repaint();
	}

	public void currentInputGiven() {
		if (currentEdition.next()) {
			// Done
			resetCurrentInput();
		}
		else {
			// Switch to next input
			updateCurrentInput();
		}
	}

	public JPanel getControlPanel() {
		return controlPanel;
	}

	public JLabel getEditionLabel() {
		return editionLabel;
	}

	public JLabel getPositionLabel() {
		return positionLabel;
	}

	private GeometricConstruction<?> selectedConstruction;

	public GeometricConstruction<?> getSelectedConstruction() {

		return selectedConstruction;
	}

	public void setSelectedConstruction(GeometricConstruction<?> aConstruction) {
		if (aConstruction != selectedConstruction) {
			GeometricConstruction<?> oldValue = selectedConstruction;
			selectedConstruction = aConstruction;
			DrawingTreeNode<?, GraphicalRepresentation> drawingTreeNode = getDrawing().getDrawingTreeNode(aConstruction);
			clearSelection();
			addToSelectedObjects(drawingTreeNode);
			getPropertyChangeSupport().firePropertyChange("selectedConstruction", oldValue, selectedConstruction);
		}
	}

	private GeometricConstruction<?> _getComputedSelectedConstruction() {
		if (getSelectedObjects().size() >= 1) {
			DrawingTreeNode<?, ?> drawingTreeNode = getSelectedObjects().get(0);
			if (drawingTreeNode.getDrawable() instanceof GeometricConstruction) {
				return (GeometricConstruction<?>) drawingTreeNode.getDrawable();
			}
		}
		return null;
	}

	@Override
	public List<DrawingTreeNode<?, ?>> getSelectedObjects() {
		return super.getSelectedObjects();
	}

	@Override
	public void setSelectedObjects(List<? extends DrawingTreeNode<?, ?>> someSelectedObjects) {
		GeometricConstruction<?> oldSelectedConstruction = getSelectedConstruction();
		super.setSelectedObjects(someSelectedObjects);
		selectedConstruction = _getComputedSelectedConstruction();
		getPropertyChangeSupport().firePropertyChange("selectedConstruction", oldSelectedConstruction, getSelectedConstruction());
	}

	@Override
	public void addToSelectedObjects(DrawingTreeNode<?, ?> aNode) {
		GeometricConstruction<?> oldSelectedConstruction = getSelectedConstruction();
		super.addToSelectedObjects(aNode);
		selectedConstruction = _getComputedSelectedConstruction();
		getPropertyChangeSupport().firePropertyChange("selectedConstruction", oldSelectedConstruction, getSelectedConstruction());
	}

	@Override
	public void removeFromSelectedObjects(DrawingTreeNode<?, ?> aNode) {
		GeometricConstruction<?> oldSelectedConstruction = getSelectedConstruction();
		super.removeFromSelectedObjects(aNode);
		selectedConstruction = _getComputedSelectedConstruction();
		getPropertyChangeSupport().firePropertyChange("selectedConstruction", oldSelectedConstruction, getSelectedConstruction());
	}

	@Override
	protected void prepareClipboardForPasting(DianaPoint proposedPastingLocation) {
		logger.info("Pasting in " + getPastingContext().getDrawable() + " at " + proposedPastingLocation);
		/*if (getClipboard().isSingleObject()) {
			if (getClipboard().getSingleContents() instanceof Shape) {
				Shape shapeBeingPasted = (Shape) getClipboard().getSingleContents();
				shapeBeingPasted.setName(shapeBeingPasted.getName() + "-new");
				shapeBeingPasted.getGraphicalRepresentation().setX(proposedPastingLocation.x);
				shapeBeingPasted.getGraphicalRepresentation().setY(proposedPastingLocation.y);
			}
			else if (getClipboard().getSingleContents() instanceof Connector) {
				Connector connectorBeingPasted = (Connector) getClipboard().getSingleContents();
				connectorBeingPasted.setName(connectorBeingPasted.getName() + "-new");
			}
		}
		else {
			for (Object o : getClipboard().getMultipleContents()) {
				if (o instanceof Shape) {
					((Shape) o).getGraphicalRepresentation().setX(((Shape) o).getGraphicalRepresentation().getX() + PASTE_DELTA);
					((Shape) o).getGraphicalRepresentation().setY(((Shape) o).getGraphicalRepresentation().getY() + PASTE_DELTA);
					((Shape) o).setName(((Shape) o).getName() + "-new");
				}
				else if (o instanceof Connector) {
					((Connector) o).setName(((Connector) o).getName() + "-new");
				}
			}
		}*/
	}

	@Override
	public Clipboard copy() throws CopyException {
		// TODO Auto-generated method stub
		return super.copy();
	}

	@Override
	public Clipboard cut() throws CutException {
		// TODO Auto-generated method stub
		return super.cut();
	}

	@Override
	public void paste() throws PasteException {
		// TODO Auto-generated method stub
		super.paste();
	}

	@Override
	protected void clearSelectedNodesComputation() {
		super.clearSelectedNodesComputation();
		selectedNodes = null;
	}

	private List<GeometricNode<?>> selectedNodes;

	public List<GeometricNode<?>> getSelectedNodes() {
		if (selectedNodes == null) {
			selectedNodes = new ArrayList<>();
			for (DrawingTreeNode<?, ?> node : getSelectedObjects()) {
				if (node instanceof GeometricNode && node.getDrawable() instanceof NodeConstruction) {
					System.out.println("Tiens, j'ai trouve " + node + " drawable=" + node.getDrawable());
					selectedNodes.add((GeometricNode<?>) node);
				}
			}
		}
		return selectedNodes;
	}

}
