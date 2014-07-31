/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fge;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

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

	private static final Logger logger = Logger.getLogger(GRProperty.class.getPackage().getName());

	private static Map<String, GRProperty<?>> retrieveParameters(Class<?> ownerClass) {
		Map<String, GRProperty<?>> returned = new HashMap<String, GRProperty<?>>();
		for (Field f : ownerClass.getFields()) {
			PropertyIdentifier parameter = f.getAnnotation(PropertyIdentifier.class);
			if (parameter != null) {
				GRProperty p = new GRProperty(f, parameter);
				// System.out.println("Found " + p);
				returned.put(p.getName(), p);
			}
		}
		return returned;
	}

	private static Map<Class<?>, Map<String, GRProperty<?>>> cachedParameters = new HashMap<Class<?>, Map<String, GRProperty<?>>>();

	public static <T> GRProperty<T> getGRParameter(Class<?> declaringClass, String name, Class<T> type) {
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

	private static Map<Class<?>, Collection<GRProperty<?>>> cache = new HashMap<Class<?>, Collection<GRProperty<?>>>();

	public static Collection<GRProperty<?>> getGRParameters(Class<?> declaringClass) {
		Collection<GRProperty<?>> returned = cache.get(declaringClass);
		if (returned == null) {
			Map<String, GRProperty<?>> cacheForClass = cachedParameters.get(declaringClass);
			if (cacheForClass == null) {
				cacheForClass = retrieveParameters(declaringClass);
				cachedParameters.put(declaringClass, cacheForClass);
			}
			returned = new ArrayList<GRProperty<?>>();
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
			} else if (type.equals(Short.class)) {
				type = (Class<T>) Short.TYPE;
			} else if (type.equals(Long.class)) {
				type = (Class<T>) Long.TYPE;
			} else if (type.equals(Byte.class)) {
				type = (Class<T>) Byte.TYPE;
			} else if (type.equals(Double.class)) {
				type = (Class<T>) Double.TYPE;
			} else if (type.equals(Float.class)) {
				type = (Class<T>) Float.TYPE;
			} else if (type.equals(Character.class)) {
				type = (Class<T>) Character.TYPE;
			} else if (type.equals(Boolean.class)) {
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

	public T getDefaultValue() {
		if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
			return (T) new Integer(0);
		}
		if (type.equals(Short.TYPE) || type.equals(Short.class)) {
			return (T) new Short((short) 0);
		}
		if (type.equals(Long.TYPE) || type.equals(Long.class)) {
			return (T) new Long(0);
		}
		if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
			return (T) new Byte((byte) 0);
		}
		if (type.equals(Double.TYPE) || type.equals(Double.class)) {
			return (T) new Double(0);
		}
		if (type.equals(Float.TYPE) || type.equals(Float.class)) {
			return (T) new Float(0);
		}
		if (type.equals(Character.TYPE) || type.equals(Character.class)) {
			return (T) new Character('a');
		}
		if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
			return (T) new Boolean(false);
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
		GRProperty other = (GRProperty) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
