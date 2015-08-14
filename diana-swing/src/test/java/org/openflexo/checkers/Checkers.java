package org.openflexo.checkers;

public class Checkers {

	public static void main(String[] args) {
		CheckersBoard board = new CheckersBoard();
		CheckersController controller = new CheckersController(board);
		controller.start();
	}

}
