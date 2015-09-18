package org.openflexo.checkers;

public class CheckersPiece {
	public enum Color {
		BLACK, WHITE
	};

	private final CheckersBoard board; // necessaire ?
	private int x, y;
	private final Color color;
	private boolean king;

	public CheckersPiece(int x, int y, Color color, CheckersBoard board) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.board = board;
	}

	public void move(int x, int y) {
		setX(x);
		setY(y);
		System.out.println("piece moved " + getX() + ", " + getY());
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
		if (x >= 0 && x < 8)
			this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		if (y >= 0 && y < 8)
			this.y = y;
	}
}
