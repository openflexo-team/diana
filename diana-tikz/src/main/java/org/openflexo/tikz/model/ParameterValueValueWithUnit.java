/**
 * 
 */
package org.openflexo.tikz.model;

/**
 * @author Quentin
 *
 */
public class ParameterValueValueWithUnit {
	private double value;
	private Unit unit;
	
	public ParameterValueValueWithUnit(double v, Unit u) {
		value = v;
		unit = u;
	}
	
	public ParameterValueValueWithUnit() {
		this(0.0, Unit.MM);
	}
	
	public ParameterValueValueWithUnit(double v, String unitAsAString) {
		value = v;
		unit = Unit.fromString(unitAsAString);
	}
	
	public double getValue() {
		return value;
	}
	
	public Unit getUnit() {
		return unit;
	}
	
	public void setValue(double newValue) {
		value = newValue;
	}
	
	public void setUnit(Unit newUnit) {
		unit = newUnit;
	}
	
	public String toString() {
		return Double.toString(value) + unit.toString();
	}
	
	enum Unit{
		MM,
		CM;
		
		public String toString() {
			switch(this) {
			case MM:
				return "mm";
			case CM:
				return "cm";
			default:
				return "mm";
			}
		}
		
		public static Unit fromString(String s) {
			switch(s) {
			case "mm":
				return MM;
			case "cm":
				return CM;
			default:
				return MM;
			}
		}
	}
}
