package org.openflexo.checkers;

public class BoardCoordinates {
	private double x;
	private double y;

	public BoardCoordinates(double x, double y) {
		setX(x);
		setY(y);
	}

	public double getX() {
		return x;
	}

	public void setX(double value) {
		x = value;
	}

	public double getY() {
		return y;
	}

	public void setY(double value) {
		y = value;
	}
}
