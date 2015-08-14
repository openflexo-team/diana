package org.openflexo.checkers;

public class CheckersController {
	private CheckersPiece.Color turn;
	private CheckersPiece selected;

	private final CheckersBoard board;
	private CheckersUI UI;

	public CheckersController(CheckersBoard board) {
		this.board = board;
	}

	public void start() {
		board.init();
		UI = new CheckersUI(board);
	}

	public PieceAndCoordinates getPieceAndCoordinates() {
		return null;
	}
}
