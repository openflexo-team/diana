package org.openflexo.checkers;

import java.util.List;

import org.openflexo.checkers.CheckersPiece.Color;

public class CheckersController {
	private final CheckersBoard board;

	private CheckersPiece.Color turn;

	private Cell lastCellEvaluated;
	private CheckersMove playerMove;
	private List<CheckersMove> compulsoryMoves;

	public CheckersController(CheckersBoard board) {
		this.board = board;
	}

	public void pickPiece(CheckersPiece piece) {
		if (piece.getColor() == turn) {

		}
	}

	public void trackDraggedPiece(CheckersPiece piece, int cellX, int cellY) {

		if ((lastCellEvaluated == null) || (lastCellEvaluated.x != cellX || lastCellEvaluated.y != cellY)) {
			Cell cell = new Cell(cellX, cellY);
			lastCellEvaluated = cell;
			System.out.println("'Valuhating");

			if (playerMove == null) // shouldn't be necessary, create the move at the pick/click
				playerMove = new CheckersMove(piece);

			System.out.println("SecondToLast" + playerMove.secondToLastCell());
			System.out.println("Last" + playerMove.last());
			System.out.println("Cell" + cell);
			if (cell.equals(playerMove.secondToLastCell())) {
				System.out.println("reomving last");
				playerMove.removeLastStep();
			}
			else if (stepIsValid(piece, cell, playerMove.lastCell())) {
				playerMove.addCell(cell);
			}
			else if ((playerMove.secondToLastCell() != null) && (stepIsValid(piece, playerMove.secondToLastCell(), cell))) {
				playerMove.removeLastStep();
				if (stepIsValid(piece, cell, playerMove.lastCell()))
					playerMove.addCell(cell);
			}
			lastCellEvaluated = null;
			System.out.println(playerMove.getSteps());
		}
		// if(stepIsValid(piece, end, playerMove.last()))
	}

	public void dropPiece(CheckersPiece piece, int cellX, int cellY) {
		if (playerMove != null) {
			if (playerMove.last() != null && playerMove.getPiece() == piece) {
				if (cellX == playerMove.lastCell().x && cellY == playerMove.lastCell().y)
					movePiece(playerMove.getPiece(), playerMove.lastCell().x, playerMove.lastCell().y);
				for (CheckersStep step : playerMove.getSteps()) {
					if (step.getCaptured() != null) {
						board.removePiece(step.getCaptured());
					}
					playerMove = null;
				}
			}
		}
	}

	public boolean stepIsValid(CheckersPiece piece, Cell start, Cell end) {
		return !playerMove.getSteps().contains(new CheckersStep(start, end));

		/*int dx = end.x - start.x;
		int dy = end.y - start.y;
		
		if (dx != dy) {
			return false;
		}
		
		if (dy == getForward()) {
			return (playerMove.nbSteps() == 1) && board.isEmpty(end.x, end.y);
		}
		
		return true;*/
	}

	public void movePiece(CheckersPiece piece, double x, double y) {
		System.out.println(x + ", " + y);
		int cellX = (int) Math.floor(x / 40);
		int cellY = (int) Math.floor(y / 40);

		piece.move(cellX, cellY);
		System.out.println("ctrl moved");
	}

	public CheckersMove getPlayerMove() {
		return playerMove;
	}

	public int getForward() {
		return (turn == Color.BLACK) ? -1 : 1;
	}

	public Color getOpponent() {
		return (turn == Color.BLACK) ? Color.WHITE : Color.BLACK;
	}
}
