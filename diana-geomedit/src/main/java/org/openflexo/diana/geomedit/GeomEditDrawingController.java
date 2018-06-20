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
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.openflexo.diana.geomedit.edition.CreateLineFromPoints;
import org.openflexo.diana.geomedit.edition.CreatePoint;
import org.openflexo.diana.geomedit.edition.Edition;
import org.openflexo.diana.geomedit.edition.EditionInput;
import org.openflexo.diana.geomedit.model.GeometricConstructionFactory;
import org.openflexo.diana.geomedit.model.GeometricDiagram;
import org.openflexo.diana.geomedit.view.GeomEditDrawingView;
import org.openflexo.exceptions.CopyException;
import org.openflexo.exceptions.CutException;
import org.openflexo.exceptions.PasteException;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.swing.JDianaInteractiveEditor;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.fge.view.FGEView;

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

	private JPopupMenu contextualMenu;

	// TODO: move this to its own view: GeomEditDrawingPanel
	private JPanel controlPanel;
	private JPanel availableMethodsPanel;
	private JLabel editionLabel;
	private JButton cancelButton;
	private JLabel positionLabel;

	private String NO_EDITION_STRING = "No edition";

	public GeomEditDrawingController(final GeometricDiagramDrawing aDrawing, GeometricConstructionFactory factory,
			SwingToolFactory toolFactory) {
		super(aDrawing, factory, toolFactory);

		// getPaintManager().disablePaintingCache();

		contextualMenu = new JPopupMenu();
		/*for (final ShapeType st : ShapeType.values()) {
			JMenuItem menuItem = new JMenuItem("Add " + st.name());
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Shape newShape = getFactory().makeNewShape(st, new FGEPoint(getLastClickedPoint()), getDrawing().getModel());
					getDrawing().getModel().addToShapes(newShape);
				}
			});
			contextualMenu.add(menuItem);
		}*/

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

		contextualMenu.add(makeCreatePointMenu());
		contextualMenu.add(makeCreateLineMenu());

		contextualMenu.addSeparator();
		JMenuItem copyItem = new JMenuItem("Copy");
		copyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					copy();
				} catch (CopyException e1) {
					e1.printStackTrace();
				}
			}
		});
		contextualMenu.add(copyItem);
		JMenuItem pasteItem = new JMenuItem("Paste");
		pasteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					paste();
				} catch (PasteException e1) {
					e1.printStackTrace();
				}
			}
		});
		contextualMenu.add(pasteItem);
		JMenuItem cutItem = new JMenuItem("Cut");
		cutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cut();
				} catch (CutException e1) {
					e1.printStackTrace();
				}
			}
		});
		contextualMenu.add(cutItem);

		contextualMenu.addSeparator();

		JMenuItem undoItem = new JMenuItem("Undo");
		undoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				undo();
			}
		});
		contextualMenu.add(undoItem);
		JMenuItem redoItem = new JMenuItem("Redo");
		redoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				redo();
			}
		});
		contextualMenu.add(redoItem);
		// initPalette();
	}

	private JMenu makeCreatePointMenu() {
		JMenu createPointItem = new JMenu("Create point");

		JMenuItem createExplicitPoint = new JMenuItem("As explicit position");
		createExplicitPoint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreatePoint(GeomEditDrawingController.this));
			}
		});
		createPointItem.add(createExplicitPoint);

		/*JMenuItem createPointAsMiddleFromPointsItem = new JMenuItem("As middle of two other points");
		createPointAsMiddleFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreatePointMiddleOfPoints(GeomEditDrawingController.this));
			}
		});
		createPointItem.add(createPointAsMiddleFromPointsItem);
		
		JMenuItem createPointSymetricOfPointItem = new JMenuItem("Symetric to an other point");
		createPointSymetricOfPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreatePointSymetricOfPoint(GeomEditDrawingController.this));
			}
		});
		createPointItem.add(createPointSymetricOfPointItem);*/

		return createPointItem;
	}

	private JMenu makeCreateLineMenu() {
		JMenu createLineItem = new JMenu("Create line");

		JMenuItem createLineFromPointsItem = new JMenuItem("From points");
		createLineFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateLineFromPoints(GeomEditDrawingController.this));
			}
		});
		createLineItem.add(createLineFromPointsItem);

		/*JMenuItem createHorizontalLineWithPointItem = new JMenuItem("Horizontal crossing point");
		createHorizontalLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateHorizontalLineWithPoint(GeomEditDrawingController.this));
			}
		});
		createLineItem.add(createHorizontalLineWithPointItem);
		
		JMenuItem createVerticalLineWithPointItem = new JMenuItem("Vertical crossing point");
		createVerticalLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateVerticalLineWithPoint(GeomEditDrawingController.this));
			}
		});
		createLineItem.add(createVerticalLineWithPointItem);
		
		JMenuItem createParallelLineWithPointItem = new JMenuItem("Parallel crossing point");
		createParallelLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateParallelLineWithPoint(GeomEditDrawingController.this));
			}
		});
		createLineItem.add(createParallelLineWithPointItem);
		
		JMenuItem createOrthogonalLineWithPointItem = new JMenuItem("Orthogonal crossing point");
		createOrthogonalLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateOrthogonalLineWithPoint(GeomEditDrawingController.this));
			}
		});
		createLineItem.add(createOrthogonalLineWithPointItem);
		
		JMenuItem createRotatedLineWithPointItem = new JMenuItem("Rotated line crossing point");
		createRotatedLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateRotatedLineWithPoint(GeomEditDrawingController.this));
			}
		});
		createLineItem.add(createRotatedLineWithPointItem);
		
		JMenuItem createTangentLineWithCircleAndPointItem = new JMenuItem("Tangent to a circle and crossing point");
		createTangentLineWithCircleAndPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateTangentLineWithCircleAndPoint(GeomEditDrawingController.this));
			}
		});
		createLineItem.add(createTangentLineWithCircleAndPointItem);*/

		return createLineItem;
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

	/*private void initPalette() {
		paletteModel = new DiagramEditorPalette(this);
		palette = (JDianaPalette) getToolFactory().makeDianaPalette(paletteModel);
		palette.setEditor(this);
		activatePalette(palette);
	}*/

	// private DiagramEditorPalette paletteModel;
	// private JDianaPalette palette;

	/*public DiagramEditorPalette getPaletteModel() {
		return paletteModel;
	}*/

	public void showContextualMenu(DrawingTreeNode<?, ?> dtn, FGEView view, Point p) {
		// contextualMenuInvoker = dtn;
		// contextualMenuClickedPoint = p;
		contextualMenu.show((Component) view, p.x, p.y);
	}

	/*@Override
	public JDrawingView<GeometricDiagramDrawing> makeDrawingView() {
		return new GeomEditDrawingView(drawing, this);
	}*/

	@Override
	public GeomEditDrawingView makeDrawingView() {
		return new GeomEditDrawingView(this);
	}

	@Override
	public GeomEditDrawingView getDrawingView() {
		return (GeomEditDrawingView) super.getDrawingView();
	}

	@Override
	protected void prepareClipboardForPasting(FGEPoint proposedPastingLocation) {
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
		if (contextualMenu.isShowing()) {
			contextualMenu.setVisible(false);
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

}
