package org.openflexo.checkers;

import org.openflexo.checkers.CheckersPiece.Color;

public class CheckersBoard {
	private final CheckersPiece[][] content;

	public CheckersBoard() {
		content = new CheckersPiece[8][8];
	}

	public void init() {
		for (int i = 0; i < content.length; i++) {
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
			if (content[x][y] == null) {
				content[x][y] = new CheckersPiece(color);
			}
		}
	}

	public void removePiece(int x, int y) {
		content[x][y] = null;
	}

	public void movePiece(int xStart, int yStart, int xEnd, int yEnd) {
		if (content[xStart][yStart] != null) {
			content[xEnd][yEnd] = content[xStart][yStart];
			content[xStart][yStart] = null;
		}
	}

	public CheckersPiece[][] getContent() {
		return content;
	}

	public CheckersPiece getContent(int x, int y) {
		return content[x][y];
	}

}
