package org.openflexo.checkers;

public class CheckersStep {
	private final Cell startCell, endCell;
	private CheckersPiece captured;

	public CheckersStep(Cell startCell, Cell endCell) {
		this.startCell = startCell;
		this.endCell = endCell;
		captured = null;
	}

	public CheckersStep(Cell startCell, int endX, int endY) {
		this(startCell, new Cell(endX, endY));
	}

	public CheckersStep(int startX, int startY, int endX, int endY) {
		this(new Cell(startX, startY), new Cell(endX, endY));
	}

	public CheckersStep(Cell startCell, Cell endCell, CheckersPiece captured) {
		this(startCell, endCell);
		this.captured = captured;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final CheckersStep step = (CheckersStep) obj;

		return (startCell == step.startCell && endCell == step.endCell);
	}

	public Cell getStartCell() {
		return startCell;
	}

	public Cell getEndCell() {
		return endCell;
	}

	public CheckersPiece getCaptured() {
		return captured;
	}
}
