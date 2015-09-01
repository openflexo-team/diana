package org.openflexo.checkers;

public class CheckersPiece {
	public enum Color {
		BLACK, WHITE
	};

	private final CheckersBoard board;
	private int x, y;
	private final Color color;
	private boolean king;

	public CheckersPiece(int x, int y, Color color, CheckersBoard board) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.board = board;
	}

	public boolean isKing() {
		return king;
	}

	public Color getColor() {
		return color;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
