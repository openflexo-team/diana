package org.openflexo.fge.jung;

public class MyLink {

	static int edgeCount;

	double capacity; // should be private
	double weight; // should be private for good practice
	int id;

	public MyLink(double weight, double capacity) {
		this.id = edgeCount++; // This is defined in the outer class.
		this.weight = weight;
		this.capacity = capacity;
	}

	@Override
	public String toString() { // Always good for debugging
		return "E" + id;
	}
}
