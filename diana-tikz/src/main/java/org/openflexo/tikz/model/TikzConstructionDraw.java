/**
 * 
 */
package org.openflexo.tikz.model;

/**
 * @author Quentin
 *
 */
public class TikzConstructionDraw extends TikzConstruction {
	
	private ParameterValue<ParameterValuePosition> from_absolute;
	private ParameterValue<String> from_node;
	private ParameterValue<ParameterValuePosition> to_absolute;
	private ParameterValue<String> to_node;
	
	public TikzConstructionDraw() {
		super();
		
		from_absolute = new ParameterValue<ParameterValuePosition>(new ParameterValuePosition());
		from_node = new ParameterValue<String>("");
		to_absolute = new ParameterValue<ParameterValuePosition>(new ParameterValuePosition());
		to_node = new ParameterValue<String>("");
		
		parameters.put(ParameterType.FROM_ABSOLUTE, from_absolute);
		parameters.put(ParameterType.FROM_NODE, from_node);
		parameters.put(ParameterType.TO_ABSOLUTE, to_absolute);
		parameters.put(ParameterType.TO_NODE, to_node);
		
		from_absolute.addConcurentParameter(from_node);
		from_node.addConcurentParameter(from_absolute);
		to_absolute.addConcurentParameter(to_node);
		to_node.addConcurentParameter(to_absolute);
	}
	
	public String toString() {
		String returned = "TikzConstructionDraw{ ";
		
		if(has_from_absolute()) {
			returned += get_from_absolute().toString() + " -- ";
		}
		else if(has_from_node()) {
			returned += get_from_node() + " -- ";
		}

		if(has_to_absolute()) {
			returned += get_to_absolute().toString() + "}";
		}
		else if(has_to_node()) {
			returned += get_to_node() + "}";
		}
		
		return returned;
	}
	
	public ParameterValuePosition get_from_absolute() {
		return from_absolute.getValue();
	}
	public void set_from_absolute(ParameterValuePosition newValue) {
		from_absolute.setValue(newValue);
	}
	public Boolean has_from_absolute() {
		return from_absolute.hasValue();
	}
	
	public String get_from_node() {
		return from_node.getValue();
	}
	public void set_from_node(String newValue) {
		from_node.setValue(newValue);
	}
	public Boolean has_from_node() {
		return from_node.hasValue();
	}
	
	public ParameterValuePosition get_to_absolute() {
		return to_absolute.getValue();
	}
	public void set_to_absolute(ParameterValuePosition newValue) {
		to_absolute.setValue(newValue);
	}
	public Boolean has_to_absolute() {
		return to_absolute.hasValue();
	}
	
	public String get_to_node() {
		return to_node.getValue();
	}
	public void set_to_node(String newValue) {
		to_node.setValue(newValue);
	}
	public Boolean has_to_node() {
		return to_node.hasValue();
	}
	
}
