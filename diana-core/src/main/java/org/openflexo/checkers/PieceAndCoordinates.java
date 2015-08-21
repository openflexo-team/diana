package org.openflexo.checkers;

public class PieceAndCoordinates {
	CheckersPiece piece;
	private final double[] coordinates;

	public PieceAndCoordinates(CheckersPiece piece, int x, int y) {
		this.piece = piece;
		this.coordinates = new double[] { x, y };
	}

	public CheckersPiece getPiece() {
		return piece;
	}

	public double getX() {
		return coordinates[0];
	}

	public double getY() {
		return coordinates[1];
	}
}
