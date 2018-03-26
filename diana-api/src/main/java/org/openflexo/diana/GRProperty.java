/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-api, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.diana;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openflexo.model.annotations.PropertyIdentifier;

/**
 * A {@link GRProperty} encodes a typed property access associated to any class of Diana model<br>
 * This allows a reflexive access to all properties of Diana model
 * 
 * @author sylvain
 * 
 * @param <T>
 *            type of accessed data
 */
public class GRProperty<T> {
	private static Map<String, GRProperty<?>> retrieveParameters(Class<?> ownerClass) {
		Map<String, GRProperty<?>> returned = new HashMap<>();
		for (Field f : ownerClass.getFields()) {
			PropertyIdentifier parameter = f.getAnnotation(PropertyIdentifier.class);
			if (parameter != null) {
				GRProperty<?> p = new GRProperty<>(f, parameter);
				// System.out.println("Found " + p);
				returned.put(p.getName(), p);
			}
		}
		return returned;
	}

	private static Map<Class<?>, Map<String, GRProperty<?>>> cachedParameters = new HashMap<>();

	public static <T> GRProperty<T> getGRParameter(Class<?> declaringClass, String name, Class<T> type) {
		@SuppressWarnings("unchecked")
		GRProperty<T> returned = (GRProperty<T>) getGRParameter(declaringClass, name);
		if (returned != null) {
			returned.type = type;
		}
		return returned;
	}

	public static GRProperty<?> getGRParameter(Class<?> declaringClass, String name) {
		Map<String, GRProperty<?>> cacheForClass = cachedParameters.get(declaringClass);
		if (cacheForClass == null) {
			cacheForClass = retrieveParameters(declaringClass);
			cachedParameters.put(declaringClass, cacheForClass);
		}
		GRProperty<?> returned = cacheForClass.get(name);
		if (returned == null && declaringClass.getSuperclass() != null) {
			return getGRParameter(declaringClass.getSuperclass(), name);
		}
		/*if (returned == null) {
			logger.warning("Not found GRProperty " + name + " for " + declaringClass);
		}*/
		return returned;
	}

	private static Map<Class<?>, Collection<GRProperty<?>>> cache = new HashMap<>();

	public static Collection<GRProperty<?>> getGRParameters(Class<?> declaringClass) {
		Collection<GRProperty<?>> returned = cache.get(declaringClass);
		if (returned == null) {
			Map<String, GRProperty<?>> cacheForClass = cachedParameters.get(declaringClass);
			if (cacheForClass == null) {
				cacheForClass = retrieveParameters(declaringClass);
				cachedParameters.put(declaringClass, cacheForClass);
			}
			returned = new ArrayList<>();
			returned.addAll(cacheForClass.values());
			if (declaringClass.getSuperclass() != null) {
				returned.addAll(getGRParameters(declaringClass.getSuperclass()));
			}
			cache.put(declaringClass, returned);
		}
		return returned;
	}

	private final Field field;
	private String name;
	private Class<T> type;

	@SuppressWarnings("unchecked")
	private GRProperty(Field field, PropertyIdentifier p) {
		this.field = field;
		try {
			name = (String) field.get(field.getDeclaringClass());
		} catch (IllegalArgumentException e1) {
			name = field.getName();
		} catch (IllegalAccessException e1) {
			name = field.getName();
		}

		type = (Class<T>) p.type();
		if (p.isPrimitive()) {
			if (type.equals(Integer.class)) {
				type = (Class<T>) Integer.TYPE;
			}
			else if (type.equals(Short.class)) {
				type = (Class<T>) Short.TYPE;
			}
			else if (type.equals(Long.class)) {
				type = (Class<T>) Long.TYPE;
			}
			else if (type.equals(Byte.class)) {
				type = (Class<T>) Byte.TYPE;
			}
			else if (type.equals(Double.class)) {
				type = (Class<T>) Double.TYPE;
			}
			else if (type.equals(Float.class)) {
				type = (Class<T>) Float.TYPE;
			}
			else if (type.equals(Character.class)) {
				type = (Class<T>) Character.TYPE;
			}
			else if (type.equals(Boolean.class)) {
				type = (Class<T>) Boolean.TYPE;
			}
		}
	}

	public String getFieldName() {
		return field.getName();
	}

	public String getName() {
		return name;
	}

	public Class<T> getType() {
		return type;
	}

	public Class<?> getDeclaringClass() {
		return field.getDeclaringClass();
	}

	@Override
	public String toString() {
		return "GRProperty: " + getFieldName() + " " + getName() + " " + getType().getSimpleName();
	}

	@SuppressWarnings("unchecked")
	public T getDefaultValue() {
		if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
			return (T) Integer.valueOf(0);
		}
		if (type.equals(Short.TYPE) || type.equals(Short.class)) {
			return (T) Short.valueOf((short) 0);
		}
		if (type.equals(Long.TYPE) || type.equals(Long.class)) {
			return (T) Long.valueOf(0);
		}
		if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
			return (T) Byte.valueOf((byte) 0);
		}
		if (type.equals(Double.TYPE) || type.equals(Double.class)) {
			return (T) Double.valueOf(0);
		}
		if (type.equals(Float.TYPE) || type.equals(Float.class)) {
			return (T) Float.valueOf(0);
		}
		if (type.equals(Character.TYPE) || type.equals(Character.class)) {
			return (T) Character.valueOf('a');
		}
		if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
			return (T) Boolean.FALSE;
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GRProperty<?> other = (GRProperty<?>) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		}
		else if (!field.equals(other.field))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		}
		else if (!type.equals(other.type))
			return false;
		return true;
	}

}
