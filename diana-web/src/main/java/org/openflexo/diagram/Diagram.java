package org.openflexo.diagram;

import java.util.ArrayList;
import java.util.List;

public class Diagram {
	public List<Shape> shapes;

	public Diagram() {
		shapes = new ArrayList<Shape>();
	}

	public List<Shape> getShapes() {
		return shapes;
	}

	public void add(Shape shape) {
		shapes.add(shape);
	}
}
