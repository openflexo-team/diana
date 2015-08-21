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
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.control.MouseClickControl;
import org.openflexo.fge.control.MouseClickControlAction;
import org.openflexo.fge.control.MouseControl.MouseButton;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.control.MouseDragControl;
import org.openflexo.fge.control.MouseDragControlAction;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.area.FGEFiniteGrid;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;

public class CheckersDrawing extends DrawingImpl<CheckersBoard> {

	private DrawingGraphicalRepresentation boardRepresentation;
	private GeometricGraphicalRepresentation gridRepresentation;
	private ShapeGraphicalRepresentation pieceRepresentation;

	private FGECheckersBoard grid;

	public CheckersDrawing(CheckersBoard board, FGEModelFactory factory) {
		super(board, factory, PersistenceMode.SharedGraphicalRepresentations);
	}

	@Override
	public void init() {
		boardRepresentation = getFactory().makeDrawingGraphicalRepresentation();

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

		MouseClickControl<DianaEditor<CheckersBoard>> click = (MouseClickControl<DianaEditor<CheckersBoard>>) getFactory()
				.makeMouseClickControl("click_piece", MouseButton.LEFT, 1);
		MouseDragControl<DianaEditor<CheckersBoard>> drag = (MouseDragControl<DianaEditor<CheckersBoard>>) getFactory()
				.makeMouseDragControl("drag_piece", MouseButton.LEFT);
		drag.setControlAction(new MouseDragControlAction<DianaEditor<CheckersBoard>>() {

			@Override
			public boolean isApplicable(DrawingTreeNode<?, ?> node, DianaEditor<CheckersBoard> controller, MouseControlContext context) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean handleMouseReleased(DrawingTreeNode<?, ?> node, DianaEditor<CheckersBoard> controller,
					MouseControlContext context, boolean isSignificativeDrag) {

				return false;
			}

			@Override
			public boolean handleMousePressed(DrawingTreeNode<?, ?> node, DianaEditor<CheckersBoard> controller,
					MouseControlContext context) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean handleMouseDragged(DrawingTreeNode<?, ?> node, DianaEditor<CheckersBoard> controller,
					MouseControlContext context) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		click.setControlAction(new MouseClickControlAction<DianaEditor<CheckersBoard>>() {

			@Override
			public boolean handleClick(org.openflexo.fge.Drawing.DrawingTreeNode<?, ?> node, DianaEditor<CheckersBoard> controller,
					MouseControlContext context) {
				System.out.println("hello");
				return true;
			}

			@Override
			public boolean isApplicable(org.openflexo.fge.Drawing.DrawingTreeNode<?, ?> node, DianaEditor<CheckersBoard> controller,
					MouseControlContext context) {
				return true;
			}
		});
		pieceRepresentation.addToMouseClickControls(click);

		/*MouseClickControl<DianaEditor<CheckersBoard>> click = (MouseClickControl<DianaEditor<CheckersBoard>>) getFactory()
				.makeMouseClickControl("click_piece", MouseButton.LEFT, 1);
		click.setControlAction(new MouseClickControlActionImpl<CheckersBoard>() {
			@Override
			public boolean handleClick(DrawingTreeNode<?, ?> node, DianaEditor<CheckersBoard> controller, MouseControlContext context) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		pieceRepresentation.getMouseClickControls().clear();
		pieceRepresentation.addToMouseClickControls(click);*/

		final DrawingGRBinding<CheckersBoard> boardBinding = bindDrawing(CheckersBoard.class, "board",
				new DrawingGRProvider<CheckersBoard>() {
					@Override
					public DrawingGraphicalRepresentation provideGR(CheckersBoard drawable, FGEModelFactory factory) {
						return boardRepresentation;
					}
				});

		final GeometricGRBinding<FGECheckersBoard> gridBinding = bindGeometric(FGECheckersBoard.class, "grid",
				new GeometricGRProvider<FGECheckersBoard>() {
					@Override
					public GeometricGraphicalRepresentation provideGR(FGECheckersBoard drawable, FGEModelFactory factory) {
						return gridRepresentation;
					}
				});

		final ShapeGRBinding<PieceAndCoordinates> pieceBinding = bindShape(PieceAndCoordinates.class, "piece",
				new ShapeGRProvider<PieceAndCoordinates>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(PieceAndCoordinates drawable, FGEModelFactory factory) {
						return pieceRepresentation;
					}
				});

		boardBinding.addToWalkers(new GRStructureVisitor<CheckersBoard>() {

			@Override
			public void visit(CheckersBoard board) {
				drawGeometricObject(gridBinding, grid);
				for (int i = 0; i < board.getContent().length; i++) {
					for (int j = 0; j < board.getContent()[i].length; j++) {
						BoardCoordinates coordinates = new BoardCoordinates(i * 40 + 2.5, j * 40 + 2.5);

						CheckersPiece piece = board.getContent()[i][j];
						if (piece != null) {
							PieceAndCoordinates pieceAndCoordinates = new PieceAndCoordinates(piece, i, j);
							drawShape(pieceBinding, pieceAndCoordinates);
						}
					}
				}

			}
		});

		pieceBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.X, new DataBinding<Double>("drawable.coordinates.x"), true);
		pieceBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.Y, new DataBinding<Double>("drawable.coordinates.y"), true);
		// pieceBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.BACKGROUND, new DataBinding<ColorBackgroundStyle>(""), false);
	}

	public class FGECheckersBoard extends FGEFiniteGrid {

		public FGECheckersBoard(FGEPoint origin, double hStep, double vStep, FGERectangle bounds) {
			super(origin, hStep, vStep, bounds);
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 8; j++) {
					if (((i + j) % 2) == 0) {
						fillCell(i, j);
					}
				}
			}
		}
	}
}
