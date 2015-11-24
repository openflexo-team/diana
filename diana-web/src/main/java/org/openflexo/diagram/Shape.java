package org.openflexo.diagram;

public class Shape {
	public enum ShapeType {
		OVAL, RECTANGLE, TRIANGLE
	};

	private double x, y;
	private double width;
	private double height;
	private final ShapeType type;

	public Shape(ShapeType type, double x, double y, double width, double height) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.width = x;
		this.height = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public ShapeType getType() {
		return type;
	}
}
