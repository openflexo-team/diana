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

package org.openflexo.checkers;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.GeometricGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.GeometricGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.MouseControl.MouseButton;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.control.actions.MouseDragControlImpl;
import org.openflexo.fge.control.actions.MoveAction;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;

public class CheckersDrawing extends DrawingImpl<CheckersGame> {

	private DrawingGraphicalRepresentation gameRepresentation;
	private GeometricGraphicalRepresentation gridRepresentation;
	private ShapeGraphicalRepresentation pieceRepresentation, blackPieceRepresentation;
	private ShapeGraphicalRepresentation stepRepresentation;

	private GRStructureVisitor<CheckersGame> moveVisitor;
	private final List<ShapeNode<Cell>> moveStepNodes;

	private FGECheckersBoard grid;

	public CheckersDrawing(CheckersGame board, FGEModelFactory factory) {
		super(board, factory, PersistenceMode.SharedGraphicalRepresentations);
		moveStepNodes = new LinkedList<ShapeNode<Cell>>();
	}

	@Override
	public void init() {
		gameRepresentation = getFactory().makeDrawingGraphicalRepresentation();

		grid = new FGECheckersBoard(new FGEPoint(0, 0), 40, 40, new FGERectangle(0, 0, 8 * 40, 8 * 40));
		gridRepresentation = getFactory().makeGeometricGraphicalRepresentation(grid);
		gridRepresentation.setBackground(getFactory().makeColoredBackground(Color.BLACK));

		/*
		GridLayoutManagerSpecification gridLayoutManager = getFactory().makeLayoutManagerSpecification("grid", GridLayoutManagerSpecification.class);
		gridLayoutManager.setPaintDecoration(false);
		gridLayoutManager.setDraggingMode(DraggingMode.FreeDiaggingAndLayout);
		gridLayoutManager.setGridX(20);
		gridLayoutManager.setGridY(20);
		gridLayoutManager.setHorizontalAlignment(HorizontalTextAlignment.CENTER);
		gridLayoutManager.setVerticalAlignment(VerticalTextAlignment.MIDDLE);
		boardRepresentation.addToLayoutManagerSpecifications(gridLayoutManager);
		*/

		pieceRepresentation = getFactory().makeShapeGraphicalRepresentation(ShapeType.CIRCLE);
		pieceRepresentation.setWidth(30);
		pieceRepresentation.setHeight(30);
		pieceRepresentation.setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
		pieceRepresentation.setBackground(getFactory().makeColoredBackground(Color.WHITE));

		blackPieceRepresentation = getFactory().makeShapeGraphicalRepresentation(pieceRepresentation);
		blackPieceRepresentation.setBackground(getFactory().makeColoredBackground(Color.BLACK));

		// moveRepresentation = getFactory().;

		stepRepresentation = getFactory().makeShapeGraphicalRepresentation(ShapeType.SQUARE);
		stepRepresentation.setWidth(40);
		stepRepresentation.setHeight(40);
		stepRepresentation.setIsSelectable(false);
		stepRepresentation.setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
		// stepRepresentation.setLocationConstraints(LocationConstraints.UNMOVABLE);
		stepRepresentation.setBackground(getFactory().makeColoredBackground(new Color(1.f, 1.f, 0.f, 0.2f)));
		stepRepresentation.setShadowStyle(getFactory().makeNoneShadowStyle());

		final DrawingGRBinding<CheckersGame> gameBinding = bindDrawing(CheckersGame.class, "game", new DrawingGRProvider<CheckersGame>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(CheckersGame drawable, FGEModelFactory factory) {
				return gameRepresentation;
			}
		});

		final GeometricGRBinding<FGECheckersBoard> gridBinding = bindGeometric(FGECheckersBoard.class, "grid",
				new GeometricGRProvider<FGECheckersBoard>() {
					@Override
					public GeometricGraphicalRepresentation provideGR(FGECheckersBoard drawable, FGEModelFactory factory) {
						return gridRepresentation;
					}
				});

		final ShapeGRBinding<CheckersPiece> pieceBinding = bindShape(CheckersPiece.class, "piece", new ShapeGRProvider<CheckersPiece>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(CheckersPiece drawable, FGEModelFactory factory) {
				return (drawable.getColor() == CheckersPiece.Color.WHITE) ? pieceRepresentation : blackPieceRepresentation;
			}
		});

		/*final ConnectorGRBinding<CheckersMove> moveBinding = bindConnector(CheckersMove.class, "move",
				new ConnectorGRProvider<CheckersMove>() {
					@Override
					public ConnectorGraphicalRepresentation provideGR(CheckersMove drawable, FGEModelFactory factory) {
						return moveRepresentation;
					}
				});*/

		final ShapeGRBinding<Cell> stepBinding = bindShape(Cell.class, "step", new ShapeGRProvider<Cell>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(Cell drawable, FGEModelFactory factory) {
				return stepRepresentation;
			}
		});

		gameBinding.addToWalkers(new GRStructureVisitor<CheckersGame>() {

			@Override
			public void visit(CheckersGame game) {
				drawGeometricObject(gridBinding, grid);
				for (CheckersPiece piece : game.getBoard().getPieces()) {
					ShapeNode<CheckersPiece> node = drawShape(pieceBinding, piece);
					node.setX(piece.getX() * 40 + 2.5);
					node.setY(piece.getY() * 40 + 2.5);
				}
			}
		});

		moveVisitor = new GRStructureVisitor<CheckersGame>() {

			@Override
			public void visit(CheckersGame game) {
				CheckersMove move = getModel().getController().getPlayerMove();

				List<ShapeNode<Cell>> nodesToDelete = new LinkedList<>();

				if (move != null) {
					System.out.println(moveStepNodes);
					for (ShapeNode<Cell> stepNode : moveStepNodes) {
						boolean found = false;
						for (CheckersStep step : move.getSteps()) {
							if (step.getEndCell().equals(stepNode.getDrawable())) {
								found = true;
								break;
							}
						}

						if (!found) {
							nodesToDelete.add(stepNode);
						}
					}

					for (ShapeNode<Cell> node : nodesToDelete) {
						node.delete();
						moveStepNodes.remove(node);
					}

					for (CheckersStep step : move.getSteps()) {
						boolean found = false;
						for (ShapeNode<Cell> stepNode : moveStepNodes) {
							if (step.equals(stepNode.getDrawable())) {
								found = true;
								break;
							}
						}

						if (!found) {
							ShapeNode<Cell> node = drawShape(stepBinding, step.getEndCell());
							if (node != null)
								moveStepNodes.add(node);
						}
					}
				}
			}
		};
		gameBinding.addToWalkers(moveVisitor);

		/*moveBinding.addToWalkers(new GRStructureVisitor<CheckersMove>() {
		
			@Override
			public void visit(CheckersMove drawable) {
				for (Cell step : drawable.getSteps()) {
					drawShape(stepBinding, step);
				}
			}
		});*/

		/*gameBinding.addToWalkers(new GRStructureVisitor<CheckersGame>() {
		
			@Override
			public void visit(CheckersGame game) {
				for(Cell cell : game.getController().getPlayerMove().getSteps()) {
					
				}
			}
		});*/

		stepBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.X, new DataBinding<Double>("40.0 * drawable.x"), false);
		stepBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.Y, new DataBinding<Double>("40.0 * drawable.y"), false);

		pieceRepresentation.getMouseDragControls().clear();
		pieceRepresentation.addToMouseDragControls(new MouseDragControlImpl("dragPiece", MouseButton.LEFT, new MoveAction() {

			@Override
			public boolean handleMousePressed(org.openflexo.fge.Drawing.DrawingTreeNode<?, ?> node,
					org.openflexo.fge.control.DianaInteractiveViewer<?, ?, ?> editor, MouseControlContext context) {
				boolean returned = super.handleMousePressed(node, editor, context);
				CheckersPiece piece = (CheckersPiece) node.getDrawable();
				getModel().getController().pickPiece(piece);
				return returned;
			}

			@Override
			public boolean handleMouseDragged(org.openflexo.fge.Drawing.DrawingTreeNode<?, ?> node,
					org.openflexo.fge.control.DianaInteractiveViewer<?, ?, ?> editor, MouseControlContext context) {
				boolean returned = super.handleMouseDragged(node, editor, context);

				int cellX = (int) Math.floor(node.getViewX(1) / 40);
				int cellY = (int) Math.floor(node.getViewY(1) / 40);

				CheckersPiece piece = (CheckersPiece) node.getDrawable();
				getModel().getController().trackDraggedPiece(piece, cellX, cellY);
				moveVisitor.visit(getModel());
				return returned;
			}

			@Override
			public boolean handleMouseReleased(org.openflexo.fge.Drawing.DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> editor,
					MouseControlContext context, boolean isSignificativeDrag) {
				boolean returned = super.handleMouseReleased(node, editor, context, isSignificativeDrag);

				int cellX = (int) Math.floor(node.getViewX(1) / 40);
				int cellY = (int) Math.floor(node.getViewY(1) / 40);

				CheckersPiece piece = (CheckersPiece) node.getDrawable();
				getModel().getController().dropPiece(piece, cellX, cellY);
				return returned;
			}
		}, false, false, false, false, getFactory().getEditingContext()));

	}
	/*
		public synchronized void drawPlayerMove() {
			CheckersMove move = getModel().getController().getPlayerMove();
	
			for (ShapeNode<Cell> stepNode : moveStepNodes) {
				if (!move.getSteps().contains(stepNode.getDrawable()))
					stepNode.delete();
				moveStepNodes.remove(stepNode);
			}
	
			for (Cell step : move.getSteps()) {
				boolean found = false;
				for (ShapeNode<Cell> stepNode : moveStepNodes) {
					if (step.equals(stepNode.getDrawable())) {
						found = true;
						break;
					}
				}
	
				if (!found) {
					// moveStepNodes.add(drawShape(stepBinding, step));
				}
			}
		}*/
}
