package org.openflexo.tikz.model;

public class ParameterValuePosition {
	private double x;
	private double y;
	public ParameterValuePosition(double x_, double y_) {
		x = x_;
		y = y_;
	}
	
	public ParameterValuePosition() {
		this(0., 0.);
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX(double newValue) {
		x = newValue;
	}
	
	public void setY(double newValue) {
		y = newValue;
	}
	
	public String toString(int nbDecimal) {
		double coef = Math.pow(10, nbDecimal);
		return "(" + Double.toString(Math.round(x * coef) / coef) + ", " + Double.toString(Math.round(y * coef) / coef) + ")";
	}
	
	public String toString() {
		return "(" + Double.toString(x) + ", " + Double.toString(y) + ")";
	}
}
