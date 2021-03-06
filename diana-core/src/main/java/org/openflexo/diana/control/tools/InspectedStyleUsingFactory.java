/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.diana.control.tools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.GRProperty;
import org.openflexo.diana.control.DianaInteractiveViewer;
import org.openflexo.pamela.factory.KeyValueCoding;

/**
 * 
 * @author sylvain
 * 
 * @param <F>
 * @param <S>
 * @param <ST>
 */
public abstract class InspectedStyleUsingFactory<F extends StyleFactory<S, ST>, S extends KeyValueCoding, ST> extends InspectedStyle<S> {

	private static final Logger logger = Logger.getLogger(InspectedStyleUsingFactory.class.getPackage().getName());

	private final F styleFactory;

	private final FactoryPropertyChangeListener factoryListener;

	public InspectedStyleUsingFactory(DianaInteractiveViewer<?, ?, ?> controller, F styleFactory) {
		super(controller, styleFactory.makeNewStyle(null));
		this.styleFactory = styleFactory;
		factoryListener = new FactoryPropertyChangeListener();
		styleFactory.getPropertyChangeSupport().addPropertyChangeListener(factoryListener);
	}

	@Override
	public boolean delete() {
		styleFactory.getPropertyChangeSupport().removePropertyChangeListener(factoryListener);
		return super.delete();
	}

	public F getStyleFactory() {
		return styleFactory;
	}

	@Override
	public S cloneStyle() {
		if (styleFactory != null && styleFactory.getDianaFactory() != null && styleFactory.getDianaFactory().getEditingContext() != null) {
			styleFactory.getDianaFactory().getEditingContext().getUndoManager().enableAnticipatedRecording();
		}
		S returned = styleFactory.makeNewStyle(null);
		if (styleFactory != null && styleFactory.getDianaFactory() != null && styleFactory.getDianaFactory().getEditingContext() != null) {
			styleFactory.getDianaFactory().getEditingContext().getUndoManager().disableAnticipatedRecording();
		}
		return returned;
	}

	@Override
	protected Class<? extends S> getInspectedStyleClass() {
		if (getSelection().size() == 0) {
			return (Class<? extends S>) getStyleFactory().getCurrentStyle().getClass();
		}
		S style = getStyle(getSelection().get(0));
		if (style != null) {
			return (Class<? extends S>) style.getClass();
		}
		return null;
	}

	@Override
	public <T> T getPropertyValue(GRProperty<T> parameter) {
		InspectedStyle<? extends S> currentlyInspected = styleFactory.getCurrentStyle();
		if (parameter.getDeclaringClass().isAssignableFrom(currentlyInspected.getClass())) {
			return currentlyInspected.getPropertyValue(parameter);
		}
		return null;
	}

	@Override
	public <T> void setPropertyValue(GRProperty<T> parameter, T value) {
		InspectedStyle<? extends S> currentlyInspected = styleFactory.getCurrentStyle();
		if (parameter.getDeclaringClass().isAssignableFrom(currentlyInspected.getClass())) {
			currentlyInspected.setPropertyValue(parameter, value);
		}
	}

	@Override
	protected <T> void fireChangedProperty(GRProperty<T> parameter) {
		InspectedStyle<? extends S> currentlyInspected = styleFactory.getCurrentStyle();
		if (parameter.getDeclaringClass().isAssignableFrom(currentlyInspected.getClass())) {
			currentlyInspected.fireChangedProperty(parameter);
		}
	}

	public ST getStyleType() {

		if (getSelection().size() == 0) {
			return getStyleType(getDefaultValue());
		}
		return getStyleType(getStyle(getSelection().get(0)));
	}

	protected abstract ST getStyleType(S style);

	@Override
	public void fireSelectionUpdated() {
		// System.out.println("Selection mise a jour, je veux un " + getStyleType() + " alors que je suis a "
		// + getStyleFactory().getStyleType());

		if (requireChange(getStyleFactory().getStyleType(), getStyleType())) {
			getStyleFactory().setStyleType(getStyleType());
		}
		super.fireSelectionUpdated();
	}

	protected void applyNewStyleTypeToSelection(ST newStyleType) {
		// System.out.println("Changing for " + newStyleType);
		for (DrawingTreeNode<?, ?> n : getSelection()) {
			S nodeStyle = getStyle(n);
			if (getStyleType(nodeStyle) != newStyleType) {
				applyNewStyle(newStyleType, n);
			}
		}
	}

	protected abstract void applyNewStyle(ST aStyleType, DrawingTreeNode<?, ?> node);

	protected class FactoryPropertyChangeListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// System.out.println("Tiens, on me previent que " + evt + " pour " + evt.getPropertyName());
			if (evt.getPropertyName().equals(StyleFactory.STYLE_CLASS_CHANGED)) {
				if (getSelection().size() == 0) {
					// In this case style type should be applied as default value, which should be recomputed
					setDefaultValue(cloneStyle());
				}
				else {
					applyNewStyleTypeToSelection((ST) evt.getNewValue());
				}
				// We should now force notify all properties related to new style
				if (getInspectedStyleClass() == null) {
					logger.warning("Could not retrieve style for selection: " + getSelection());
					return;
				}
				for (GRProperty<?> p : GRProperty.getGRParameters(getInspectedStyleClass())) {
					forceFireChangedProperty(p);
				}
			}

			/*Class<?> inspectedStyleClass = getStyleFactory().getCurrentStyle().getClass();
			GRProperty param = GRProperty.getGRParameter(inspectedStyleClass, evt.getPropertyName());
			System.out.println("Found param = " + param);
			if (param != null) {
				setPropertyValue(param, evt.getNewValue());
			}*/
		}
	}

}
