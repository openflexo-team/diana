/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-drawing-editor, a component of the software infrastructure 
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

package org.openflexo.diana.drawingeditor;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openflexo.diana.ConnectorGraphicalRepresentation;
import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.control.actions.DrawConnectorAction;
import org.openflexo.diana.control.actions.DrawShapeAction;
import org.openflexo.diana.drawingeditor.model.Connector;
import org.openflexo.diana.drawingeditor.model.Diagram;
import org.openflexo.diana.drawingeditor.model.DiagramElement;
import org.openflexo.diana.drawingeditor.model.DiagramFactory;
import org.openflexo.diana.drawingeditor.model.Shape;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.diana.swing.JDianaInteractiveEditor;
import org.openflexo.diana.swing.control.SwingToolFactory;
import org.openflexo.diana.view.DianaView;
import org.openflexo.exceptions.CopyException;
import org.openflexo.exceptions.CutException;
import org.openflexo.exceptions.PasteException;

public class DianaDrawingEditor extends JDianaInteractiveEditor<Diagram> {

	private static final Logger logger = Logger.getLogger(DianaDrawingEditor.class.getPackage().getName());

	private JPopupMenu contextualMenu;

	public DianaDrawingEditor(final DiagramDrawing aDrawing, DiagramFactory factory, SwingToolFactory toolFactory) {
		super(aDrawing, factory, toolFactory);

		DrawShapeAction drawShapeAction = new DrawShapeAction() {
			@Override
			public void performedDrawNewShape(ShapeGraphicalRepresentation graphicalRepresentation, ContainerNode<?, ?> parentNode) {
				System.out.println("OK, perform draw new shape with " + graphicalRepresentation + " and parent: " + parentNode);
				Shape newShape = getFactory().makeNewShape(graphicalRepresentation, getDrawing().getModel());
				getDrawing().getModel().addToShapes(newShape);
			}
		};

		DrawConnectorAction drawConnectorAction = new DrawConnectorAction() {

			@Override
			public void performedDrawNewConnector(ConnectorGraphicalRepresentation graphicalRepresentation, ShapeNode<?> startNode,
					ShapeNode<?> endNode) {
				System.out.println(
						"OK, perform draw new connector with " + graphicalRepresentation + " start: " + startNode + " end: " + endNode);
				Connector newConnector = getFactory().makeNewConnector(graphicalRepresentation, (Shape) startNode.getDrawable(),
						(Shape) endNode.getDrawable(), getDrawing().getModel());
				DrawingTreeNode<?, ?> fatherNode = DianaUtils.getFirstCommonAncestor(startNode, endNode);
				((DiagramElement<?, ?>) fatherNode.getDrawable()).addToConnectors(newConnector);

			}
		};

		setDrawCustomShapeAction(drawShapeAction);
		setDrawShapeAction(drawShapeAction);
		setDrawConnectorAction(drawConnectorAction);

		contextualMenu = new JPopupMenu();
		for (final ShapeType st : ShapeType.values()) {
			JMenuItem menuItem = new JMenuItem("Add " + st.name());
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Shape newShape = getFactory().makeNewShape(st, new DianaPoint(getLastClickedPoint()), getDrawing().getModel());
					getDrawing().getModel().addToShapes(newShape);
				}
			});
			contextualMenu.add(menuItem);
		}
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

	@Override
	public DiagramDrawing getDrawing() {
		return (DiagramDrawing) super.getDrawing();
	}

	@Override
	public DiagramFactory getFactory() {
		return (DiagramFactory) super.getFactory();
	}

	public void showContextualMenu(DrawingTreeNode<?, ?> dtn, DianaView<?, ?> view, Point p) {
		// contextualMenuInvoker = dtn;
		// contextualMenuClickedPoint = p;
		contextualMenu.show((Component) view, p.x, p.y);
	}

	@Override
	public DiagramEditorView makeDrawingView() {
		return new DiagramEditorView(this);
	}

	@Override
	public DiagramEditorView getDrawingView() {
		return (DiagramEditorView) super.getDrawingView();
	}

	@Override
	protected void prepareClipboardForPasting(DianaPoint proposedPastingLocation) {
		logger.info("Pasting in " + getPastingContext().getDrawable() + " at " + proposedPastingLocation);
		if (getClipboard().isSingleObject()) {
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
		}
	}

}
