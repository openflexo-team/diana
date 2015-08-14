package org.openflexo.checkers;

public class CheckersPiece {
	public enum Color {
		BLACK, WHITE
	};

	private final Color color;

	public CheckersPiece(Color color) {
		this.color = color;
	}
}
