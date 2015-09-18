package org.openflexo.checkers;

public class CheckersGame {

	private final CheckersBoard board;
	private final CheckersController controller;

	public CheckersGame(CheckersBoard board, CheckersController controller) {
		this.board = board;
		this.controller = controller;
	}

	public CheckersBoard getBoard() {
		return board;
	}

	public CheckersController getController() {
		return controller;
	}

}
