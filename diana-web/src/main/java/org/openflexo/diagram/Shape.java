package org.openflexo.diagram;

import java.util.ArrayList;
import java.util.List;

public class Shape {
	public enum ShapeType {
		OVAL, RECTANGLE, TRIANGLE
	};

	private double x, y;
	private double width;
	private double height;
	private final ShapeType type;
	private final List<Shape> children;

	public Shape(ShapeType type, double x, double y, double width, double height) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.children = new ArrayList<>();
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

	public List<Shape> getChildren() {
		return children;
	}

	public void addChild(Shape shape) {
		children.add(shape);
	}
}
