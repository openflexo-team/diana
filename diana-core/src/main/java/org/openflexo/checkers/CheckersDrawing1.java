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
import org.openflexo.fge.ColorBackgroundStyle;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;

public class CheckersDrawing1 extends DrawingImpl<CheckersBoard> {

	private DrawingGraphicalRepresentation boardRepresentation;
	private ShapeGraphicalRepresentation whiteCellRepresentation, blackCellRepresentation;
	private ShapeGraphicalRepresentation pieceRepresentation;

	public CheckersDrawing1(CheckersBoard board, FGEModelFactory factory) {
		super(board, factory, PersistenceMode.SharedGraphicalRepresentations);
	}

	@Override
	public void init() {
		boardRepresentation = getFactory().makeDrawingGraphicalRepresentation();

		whiteCellRepresentation = getFactory().makeShapeGraphicalRepresentation();
		whiteCellRepresentation.setShapeType(ShapeType.SQUARE);
		whiteCellRepresentation.setLocationConstraints(LocationConstraints.UNMOVABLE);
		whiteCellRepresentation.setWidth(40);
		whiteCellRepresentation.setHeight(40);

		blackCellRepresentation = getFactory().makeShapeGraphicalRepresentation(whiteCellRepresentation);
		ColorBackgroundStyle bbg = getFactory().makeColoredBackground(Color.BLACK);
		blackCellRepresentation.setBackground(bbg);

		pieceRepresentation = getFactory().makeShapeGraphicalRepresentation(ShapeType.CIRCLE);
		pieceRepresentation.setWidth(30);
		pieceRepresentation.setHeight(30);

		final DrawingGRBinding<CheckersBoard> boardBinding = bindDrawing(CheckersBoard.class, "board",
				new DrawingGRProvider<CheckersBoard>() {
					@Override
					public DrawingGraphicalRepresentation provideGR(CheckersBoard drawable, FGEModelFactory factory) {
						return boardRepresentation;
					}
				});

		final ShapeGRBinding<BoardCoordinates> cellBinding = bindShape(BoardCoordinates.class, "cell",
				new ShapeGRProvider<BoardCoordinates>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(BoardCoordinates drawable, FGEModelFactory factory) {
						if ((drawable.getX() + drawable.getY()) % 2 == 1)
							return whiteCellRepresentation;
						else
							return blackCellRepresentation;
					}
				});

		final ShapeGRBinding<PieceAndCoordinates> pieceBinding = bindShape(PieceAndCoordinates.class, "piece",
				new ShapeGRProvider<PieceAndCoordinates>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(PieceAndCoordinates drawable, FGEModelFactory factory) {
						return pieceRepresentation;
					}
				});

		/*boardBinding.addToWalkers(new GRStructureVisitor<CheckersBoard>() {
		
			@Override
			public void visit(CheckersBoard board) {
				for (CheckersPiece piece : board.getContent()) {
						BoardCoordinates coordinates = new BoardCoordinates(i, j);
						drawShape(cellBinding, coordinates);
		
						CheckersPiece piece = board.getContent()[i][j];
						if (piece != null) {
							PieceAndCoordinates pieceAndCoordinates = new PieceAndCoordinates(piece, i, j);
							drawShape(pieceBinding, pieceAndCoordinates);
						}
					}
				}
		
			}
		});*/

		cellBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.X, new DataBinding<Double>("drawable.drawingX"), false);
		cellBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.Y, new DataBinding<Double>("drawable.drawingY"), false);
		pieceBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.X, new DataBinding<Double>("drawable.drawingX"), true);
		pieceBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.Y, new DataBinding<Double>("drawable.drawingY"), true);
	}
}
