package org.openflexo.checkers;

import java.util.LinkedList;
import java.util.List;

public class CheckersMove {
	private final CheckersPiece piece;
	private final List<CheckersStep> steps;
	private final List<CheckersPiece> capturedPieces;

	public CheckersMove(CheckersPiece piece) {
		this.piece = piece;
		steps = new LinkedList<CheckersStep>();
		addStep(new CheckersStep(null, new Cell(piece.getX(), piece.getY())));

		capturedPieces = new LinkedList<CheckersPiece>();
	}

	/*public CheckersMove(CheckersPiece piece, int destX, int destY) {
		this(piece);
		this.steps.add(new Cell(destX, destY));
	}*/

	public CheckersPiece getPiece() {
		return piece;
	}

	public void addStep(CheckersStep step) {
		steps.add(step);
	}

	public void removeStep(CheckersStep step) {
		steps.remove(step);
	}

	public void addCell(Cell cell) {
		this.steps.add(new CheckersStep(lastCell(), cell));
	}

	public void addCell(int x, int y) {
		addCell(new Cell(x, y));
	}

	public void removeLastStep() {
		if (!steps.isEmpty())
			steps.remove(steps.size() - 1);
	}

	public List<CheckersStep> getSteps() {
		return steps;
	}

	public CheckersStep last() {
		if (!steps.isEmpty())
			return steps.get(steps.size() - 1);

		return null;
	}

	public Cell lastCell() {
		if (last() != null)
			return last().getEndCell();

		return null;
	}

	public Cell secondToLastCell() {
		if (last() != null)
			return last().getStartCell();

		return null;
	}

	public int nbSteps() {
		return steps.size();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final CheckersMove move = (CheckersMove) obj;
		if (piece != move.piece)
			return false;
		if (steps.size() != move.steps.size())
			return false;
		for (int i = 0; i < steps.size(); i++)
			if (steps.get(i) != move.steps.get(i))
				return false;

		return true;
	}
}
