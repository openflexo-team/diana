package org.openflexo.checkers;

public class PieceAndCoordinates {
	CheckersPiece piece;
	BoardCoordinates coordinates;

	public PieceAndCoordinates(CheckersPiece piece, BoardCoordinates coordinates) {
		this.piece = piece;
		this.coordinates = coordinates;
	}

	public CheckersPiece getPiece() {
		return piece;
	}

	public BoardCoordinates getCoordinates() {
		return coordinates;
	}
}
