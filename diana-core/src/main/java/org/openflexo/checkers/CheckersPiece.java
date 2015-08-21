package org.openflexo.checkers;

public class CheckersPiece {
	public enum Color {
		BLACK, WHITE
	};

	private final Color color;
	private boolean king;

	public CheckersPiece(Color color) {
		this.color = color;
	}

	public boolean isKing() {
		return king;
	}
}
