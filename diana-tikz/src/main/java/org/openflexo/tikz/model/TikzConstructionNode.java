/**
 * 
 */
package org.openflexo.tikz.model;

import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;

/**
 * @author Quentin
 *
 */

@ModelEntity
@XMLElement
public class TikzConstructionNode extends TikzConstruction {
	
	private ParameterValue<String> name;
	private ParameterValue<ParameterValuePosition> at_absolute;
	private ParameterValue<String> at_node;
	private ParameterValue<String> color;
	private ParameterValue<String> draw;
	private ParameterValue<String> shape;
	private ParameterValue<ParameterValueValueWithUnit> radius;
	
	public TikzConstructionNode() {
		super();
		
		name = new ParameterValue<String>("");
		at_absolute = new ParameterValue<ParameterValuePosition>(new ParameterValuePosition());
		at_node = new ParameterValue<String>("");
		color = new ParameterValue<String>("white");
		draw = new ParameterValue<String>("white");
		shape = new ParameterValue<String>("rectangle");
		radius = new ParameterValue<ParameterValueValueWithUnit>(new ParameterValueValueWithUnit());
		
		parameters.put(ParameterType.NAME, name);
		parameters.put(ParameterType.AT_ABSOLUTE, at_absolute);
		parameters.put(ParameterType.AT_NODE, at_node);
		parameters.put(ParameterType.COLOR, color);
		parameters.put(ParameterType.DRAW, draw);
		parameters.put(ParameterType.SHAPE, shape);
		parameters.put(ParameterType.RADIUS, radius);
		
		at_absolute.addConcurentParameter(at_node);
		at_node.addConcurentParameter(at_absolute);
	}
	
	public String toString() {
		String returned = "TikzConstructionNode{ ";

		if(has_name()) {
			returned += get_name() + " : ";
		}
		
		if(has_at_absolute()) {
			returned += get_at_absolute().toString() + " + ";
		}
		else if(has_at_node()) {
			returned += get_at_node() + " + ";
		}

		if(has_color()) {
			returned += "color: " + get_color() + ", ";
		}

		if(has_draw()) {
			returned += "draw: " + get_draw() + ", ";
		}

		if(has_shape()) {
			returned += "shape: " +  get_shape() + ", ";
		}

		if(has_radius()) {
			returned += "radius: " + get_radius().toString() + ", ";
		}
		
		returned += "}";
		
		return returned;
	}
	
	public String get_name() {
		return name.getValue();
	}
	public void set_name(String newValue) {
		name.setValue(newValue);
	}
	public Boolean has_name() {
		return name.hasValue();
	}
	
	public ParameterValuePosition get_at_absolute() {
		return at_absolute.getValue();
	}
	public void set_at_absolute(ParameterValuePosition newValue) {
		at_absolute.setValue(newValue);
	}
	public Boolean has_at_absolute() {
		return at_absolute.hasValue();
	}
	
	public String get_at_node() {
		return at_node.getValue();
	}
	public void set_at_node(String newValue) {
		at_node.setValue(newValue);
	}
	public Boolean has_at_node() {
		return at_node.hasValue();
	}
	
	public String get_color() {
		return color.getValue();
	}
	public void set_color(String newValue) {
		color.setValue(newValue);
	}
	public Boolean has_color() {
		return color.hasValue();
	}
	
	public String get_draw() {
		return draw.getValue();
	}
	public void set_draw(String newValue) {
		draw.setValue(newValue);
	}
	public Boolean has_draw() {
		return draw.hasValue();
	}
	
	public String get_shape() {
		return shape.getValue();
	}
	public void set_shape(String newValue) {
		shape.setValue(newValue);
	}
	public Boolean has_shape() {
		return shape.hasValue();
	}

	public ParameterValueValueWithUnit get_radius() {
		return radius.getValue();
	}
	public void set_radius(ParameterValueValueWithUnit newValue) {
		radius.setValue(newValue);
	}
	public Boolean has_radius() {
		return radius.hasValue();
	}
	
}
