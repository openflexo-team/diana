/**
 * 
 */
package org.openflexo.tikz.model;

import java.util.List;
import java.util.Vector;

import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;

/**
 * @author Quentin
 *
 */

@ModelEntity
@XMLElement
public class ParameterValue<T> {
	
	private T value;
	private T defaultValue;
	private List<ParameterValue<?>> concurentParameters;
	
	public ParameterValue(T defaultValue) {
		value = null;
		this.defaultValue = defaultValue;
		concurentParameters = new Vector<ParameterValue<?>>();
	}
	
	// SETTERS
	
	public void setValue(T newValue) {
		value = newValue;
		for(ParameterValue<?> pv : concurentParameters) {
			pv.resetValue();
		}
	}

	@SuppressWarnings("unchecked")
	public Boolean setValueAsObject(Object newValue) {
		Boolean returned = true;
		T newValueTyped = value;
		try {
			newValueTyped = (T) newValue;
			setValue(newValueTyped);
		}
		catch(ClassCastException e){
			returned = false;
		}
		return returned;
	}
	
	public void resetValue() {
		value = null;
	}
	
	public void setConcurentParameters(List<ParameterValue<?>> newConcurentParameters) {
		concurentParameters = newConcurentParameters;
	}
	
	public void addConcurentParameter(ParameterValue<?> pv){
		concurentParameters.add(pv);
	}
	
	// GETTERS
	
	public T getValue() {
		return value;
	}
	
	public String getValueAsString() {
		return value.toString();
	}
	
	public T getDefaultValue() {
		return defaultValue;
	}
	
	// CHECKERS
	
	public Boolean hasValue() {
		return value != null;
	}
	
	public Boolean hasDefaultValue() {
		return value == defaultValue;
	}
	
}
