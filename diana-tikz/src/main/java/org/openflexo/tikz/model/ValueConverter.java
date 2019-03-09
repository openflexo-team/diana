/**
 * 
 */
package org.openflexo.tikz.model;

import java.util.Hashtable;

/**
 * @author Quentin
 *
 */
public class ValueConverter<T,S> {
	protected Hashtable<T,S> correspondance = new Hashtable<T,S>();
	protected Hashtable<S,T> backCorrespondance = new Hashtable<S,T>();
	protected T defaultTValue;
	protected S defaultSValue;
	
	public ValueConverter(T tValue, S sValue) {
		defaultTValue = tValue;
		defaultSValue = sValue;
	}
	
	public void addValueCorrespondance(T tValue, S sValue) {
		correspondance.put(tValue, sValue);
		backCorrespondance.put(sValue, tValue);
	}
	
	public S convert(T tValue) {
		S returned = correspondance.get(tValue);
		if(returned == null){
			returned = defaultSValue;
		}
		return returned;
	}
	
	public T convertBack(S sValue) {
		T returned = backCorrespondance.get(sValue);
		if(returned == null){
			returned = defaultTValue;
		}
		return returned;
	}
	
}
