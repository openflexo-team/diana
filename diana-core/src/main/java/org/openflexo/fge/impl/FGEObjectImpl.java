/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.fge.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEObject;
import org.openflexo.fge.GRProperty;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.model.ModelEntity;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.DeletableProxyObject;

public abstract class FGEObjectImpl implements FGEObject {
	private static final Logger logger = Logger.getLogger(FGEObjectImpl.class.getPackage().getName());

	private FGEModelFactory factory;

	private PropertyChangeSupport pcSupport;
	private boolean isDeleted = false;

	private static int INDEX = 0;
	private int index = 0;

	public FGEObjectImpl() {
		index = INDEX++;
		pcSupport = new PropertyChangeSupport(this);
	}

	@Override
	public FGEModelFactory getFactory() {
		return factory;
	}

	@Override
	public void setFactory(FGEModelFactory factory) {
		this.factory = factory;
	}

	@Override
	public boolean delete(Object... context) {
		if (!isDeleted()) {
			isDeleted = true;
			performSuperDelete();
			// TODO: remove all listeners of PropertyChangedSupport
			// deleteObservers();
			if (getPropertyChangeSupport() != null) {
				// Property change support can be null if noone is listening. I noone is listening,
				// it is not needed to fire a property change.
				getPropertyChangeSupport().firePropertyChange(getDeletedProperty(), false, true);
				// Fixed huge bug with graphical representation (which are in the model) deleted when the diagram view was closed
				// TODO: Now we can really set the pcSupport to null here
				// Until now, it still create big issues
				// pcSupport = null;
				pcSupport = null;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean undelete(boolean restoreProperties) {
		// System.out.println("Undeleting " + this);
		performSuperUndelete(restoreProperties);
		isDeleted = false;
		// pcSupport = new PropertyChangeSupport(this);
		return false;
	}

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public final PropertyChangeSupport getPropertyChangeSupport() {
		if (pcSupport == null && !isDeleted()) {
			pcSupport = new PropertyChangeSupport(this);
		}
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return DeletableProxyObject.DELETED;
	}

	// *******************************************************************************
	// * Utils *
	// *******************************************************************************

	@Override
	public <T> void notifyChange(GRProperty<T> parameter, T oldValue, T newValue) {
		// Never notify unchanged values
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}
		hasChanged(new FGEAttributeNotification<T>(parameter, oldValue, newValue));
		/*propagateConstraintsAfterModification(parameter);
		setChanged();
		notifyObservers(new FGENotification(parameter, oldValue, newValue));*/
	}

	@Override
	public <T> void notifyChange(GRProperty<T> parameter) {
		notifyChange(parameter, null, valueForParameter(parameter));
	}

	@Override
	public <T> void notifyAttributeChange(GRProperty<T> parameter) {
		notifyChange(parameter);
	}

	/**
	 * Build and return a new notification for a potential parameter change, given a new value. Change is required if values are different
	 * considering equals() method
	 * 
	 * @param parameter
	 * @param value
	 * @param useEquals
	 * @return
	 */
	protected <T> FGEAttributeNotification<T> requireChange(GRProperty<T> parameter, T value) {
		return requireChange(parameter, value, true);
	}

	/**
	 * Build and return a new notification for a potential parameter change, given a new value. Change is required if values are different
	 * considering:
	 * <ul>
	 * <li>If useEquals flag set to true, equals() method
	 * <li>
	 * <li>If useEquals flag set to false, same reference for objects, same value for primitives</li>
	 * </ul>
	 * 
	 * @param parameter
	 * @param value
	 * @param useEquals
	 * @return
	 */
	protected <T> FGEAttributeNotification<T> requireChange(GRProperty<T> parameter, T value, boolean useEquals) {
		T oldValue = valueForParameter(parameter);
		if (value == oldValue && value != null && !value.getClass().isEnum()) {
			// logger.warning(parameter.name() + ": require change called for same object: aren't you wrong ???");
		}
		// System.out.println("param: "+parameterKey.name()+" value="+oldValue+" value="+value);
		if (oldValue == null) {
			if (value == null) {
				return null; // No change
			}
			else {
				return new FGEAttributeNotification<T>(parameter, oldValue, value);
			}
		}
		else {
			if (useEquals) {
				if (oldValue.equals(value)) {
					return null; // No change
				}
				else {
					return new FGEAttributeNotification<T>(parameter, oldValue, value);
				}
			}
			else {
				if (oldValue == value) {
					return null; // No change
				}
				else {
					return new FGEAttributeNotification<T>(parameter, oldValue, value);
				}
			}
		}
	}

	/**
	 * Computes value for supplied parameter
	 * 
	 * @param parameter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T valueForParameter(GRProperty<T> parameter) {
		if (parameter == null) {
			return null;
		}

		try {
			if (getFactory() != null && getFactory().getHandler(this) != null) {
				return (T) getFactory().getHandler(this).invokeGetter(parameter.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

		/*Class<?> type = getTypeForKey(parameter.getName());
		T returned = null;
		if (type.isPrimitive()) {
			if (type == Boolean.TYPE) {
				returned = (T) (Boolean) booleanValueForKey(parameter.getName());
			}
			if (type == Integer.TYPE) {
				returned = (T) (Integer) integerValueForKey(parameter.getName());
			}
			if (type == Short.TYPE) {
				returned = (T) (Short) shortValueForKey(parameter.getName());
			}
			if (type == Long.TYPE) {
				returned = (T) (Long) longValueForKey(parameter.getName());
			}
			if (type == Float.TYPE) {
				returned = (T) (Float) floatValueForKey(parameter.getName());
			}
			if (type == Double.TYPE) {
				returned = (T) (Double) doubleValueForKey(parameter.getName());
			}
			if (type == Byte.TYPE) {
				returned = (T) (Byte) byteValueForKey(parameter.getName());
			}
			if (type == Character.TYPE) {
				returned = (T) (Character) characterForKey(parameter.getName());
			}
		} else {
			returned = (T) objectForKey(parameter.getName());
		}
		return returned;*/
	}

	@Override
	public void notify(FGEAttributeNotification notification) {
		hasChanged(notification);
	}

	/**
	 * This method is called whenever a notification is triggered from GR model
	 * 
	 * @param notification
	 */
	protected void hasChanged(FGEAttributeNotification<?> notification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Change attribute " + notification.parameter + " for object " + this + " was: " + notification.oldValue
					+ " is now: " + notification.newValue);
		}
		// propagateConstraintsAfterModification(notification.parameter);
		setChanged();
		notifyObservers(notification);
		// getPropertyChangeSupport().firePropertyChange(notification.propertyName(), notification.oldValue, notification.newValue);
	}

	@Override
	public final boolean equals(Object object) {
		if (object instanceof FGEObject) {
			return equalsObject(object);
		}
		return super.equals(object);
	}

	@Override
	public final Object clone() {
		if (getFactory() != null && getFactory().getEditingContext() != null && getFactory().getEditingContext().getUndoManager() != null) {
			getFactory().getEditingContext().getUndoManager().enableAnticipatedRecording();
		}
		FGEObject returned = (FGEObject) cloneObject();
		returned.setFactory(getFactory());
		if (getFactory() != null && getFactory().getEditingContext() != null && getFactory().getEditingContext().getUndoManager() != null) {
			getFactory().getEditingContext().getUndoManager().disableAnticipatedRecording();
		}
		return returned;
	}

	@Override
	public final String toString() {
		if (getFactory() != null) {
			ModelEntity<?> entity = getFactory().getModelEntityForInstance(this);
			if (entity != null) {
				try {
					String returned = entity.getImplementedInterface().getSimpleName() + index
							+ (entity.getImplementingClass() != null ? "[" + entity.getImplementingClass().getSimpleName() + "]" : "");
					return entity.getImplementedInterface().getSimpleName() + index
							+ (entity.getImplementingClass() != null ? "[" + entity.getImplementingClass().getSimpleName() + "]" : "");
				} catch (ModelDefinitionException e) {
				}
			}
		}
		return super.toString();
	}

	public void notifyObservers(FGENotification notification) {
		if (!isDeleted() && getPropertyChangeSupport() != null) {
			getPropertyChangeSupport().firePropertyChange(notification.propertyName(), notification.oldValue, notification.newValue);
		}
	}

	public void forward(PropertyChangeEvent evt) {
		if (getPropertyChangeSupport() == null) {
			logger.warning("Object " + this + " has no property change support !!!");
			return;
		}

		/*getPropertyChangeSupport().firePropertyChange(
				new PropertyChangeEvent(evt.getSource(), evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));*/

		getPropertyChangeSupport().firePropertyChange(evt);

	}

	@Deprecated
	public void setChanged() {
	}
}
