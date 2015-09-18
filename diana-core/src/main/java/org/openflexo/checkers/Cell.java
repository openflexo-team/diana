package org.openflexo.checkers;

public class Cell {
	public int x, y;

	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final Cell cell = (Cell) obj;
		return (x == cell.x && y == cell.y);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
