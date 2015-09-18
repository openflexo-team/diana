package org.openflexo.checkers;

import java.util.LinkedList;
import java.util.List;

import org.openflexo.checkers.CheckersPiece.Color;

public class CheckersBoard {
	private final List<CheckersPiece> pieces;

	public CheckersBoard() {
		pieces = new LinkedList<CheckersPiece>();
		init();
	}

	public void init() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 3; j++) {
				if ((i + j) % 2 == 0) {
					addPiece(i, j, Color.WHITE);
				}
			}

			for (int j = 5; j < 8; j++) {
				if ((i + j) % 2 == 0) {
					addPiece(i, j, Color.BLACK);
				}
			}
		}
	}

	public void addPiece(int x, int y, CheckersPiece.Color color) {
		if (x < 8 && y < 8) {
			pieces.add(new CheckersPiece(x, y, color, this));
		}
	}

	public void removePiece(CheckersPiece piece) {
		pieces.remove(piece);
	}

	public void movePiece(int xStart, int yStart, int xEnd, int yEnd) {

	}

	public List<CheckersPiece> getPieces() {
		return pieces;
	}

	public boolean isEmpty(int x, int y) {
		for (CheckersPiece piece : pieces) {
			if (piece.getX() == x && piece.getY() == y)
				return false;
		}

		return true;
	}
}
