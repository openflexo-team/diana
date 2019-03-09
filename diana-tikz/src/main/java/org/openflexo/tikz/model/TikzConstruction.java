/**
 * 
 */
package org.openflexo.tikz.model;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;

/**
 * @author Quentin
 *
 */
//*
@ModelEntity
@XMLElement
public class TikzConstruction {
	
	protected Hashtable<ParameterType, ParameterValue<?>> parameters;
	protected Boolean deleted;
	
	public TikzConstruction() {
		parameters = new Hashtable<ParameterType, ParameterValue<?>>();
		deleted = false;
	}
	
	public String toString() {
		return "TikzConstruction";
	}
	
	public static enum ParameterType {
		NAME,
		COLOR,
		SHAPE,
		DRAW,
		RADIUS,
		AT_ABSOLUTE,
		AT_NODE,
		FROM_ABSOLUTE,
		FROM_NODE,
		TO_ABSOLUTE,
		TO_NODE;
		
		public Boolean isInParametersList() {
			if(this == NAME || 
					this == AT_ABSOLUTE || this == AT_NODE ||
					this == FROM_ABSOLUTE || this == FROM_NODE ||
					this == TO_ABSOLUTE || this == TO_NODE) {
				return false;
			}
			return true;
		}
		
		public String toString() {
			switch(this) {
			case DRAW:
				return "draw";
			case COLOR:
				return "color";
			case SHAPE:
				return "shape";
			case RADIUS:
				return "radius";
			default:
				return "";
			}
		}
		
		public static ParameterType fromString(String s) {
			switch(s) {
			case "draw":
				return DRAW;
			case "color":
				return COLOR;
			case "shape":
				return SHAPE;
			case "radius":
				return RADIUS;
			default:
				return null;
			}
		}
		
		public static List<ParameterType> allParameterTypes(){
			return new Vector<ParameterType>(Arrays.asList(ParameterType.class.getEnumConstants()));
		}
	}
	
	public Boolean parameterExists(ParameterType p) {
		return parameters.containsKey(p);
	}
	public ParameterValue<?> getParameterValue(ParameterType p) {
		return parameters.get(p);
	}
	public void setParameterValue(ParameterType p, ParameterValue<?> v) {
		parameters.put(p, v);
	}
	
	public Boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean newValue) {
		deleted = newValue;
	}
}
