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
import java.util.logging.Logger;

import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.ShadowStyle;
import org.openflexo.diana.control.DianaInteractiveEditor;
import org.openflexo.diana.control.tools.DianaInspectors.Inspector;
import org.openflexo.diana.view.DianaViewFactory;

/**
 * Represents a tool allowing to manage style inspectors
 * 
 * @author sylvain
 * 
 * @param <C>
 * @param <F>
 * @param <ME>
 */
public abstract class DianaInspectors<C extends Inspector<?>, F extends DianaViewFactory<F, ?>> extends DianaToolImpl<C, F> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DianaInspectors.class.getPackage().getName());

	public DianaInspectors() {
	}

	@Override
	public DianaInteractiveEditor<?, F, ?> getEditor() {
		return (DianaInteractiveEditor<?, F, ?>) super.getEditor();
	}

	public InspectedForegroundStyle getInspectedForegroundStyle() {
		if (getEditor() != null) {
			return getEditor().getInspectedForegroundStyle();
		}
		return null;
	}

	/*public InspectedTextStyle getInspectedTextStyle() {
		if (getEditor() != null) {
			return getEditor().getInspectedTextStyle();
		}
		return null;
	}*/

	public InspectedTextProperties getInspectedTextProperties() {
		if (getEditor() != null) {
			return getEditor().getInspectedTextProperties();
		}
		return null;
	}

	public InspectedShadowStyle getInspectedShadowStyle() {
		if (getEditor() != null) {
			return getEditor().getInspectedShadowStyle();
		}
		return null;
	}

	public InspectedBackgroundStyle getInspectedBackgroundStyle() {
		if (getEditor() != null) {
			return getEditor().getInspectedBackgroundStyle();
		}
		return null;
	}

	public InspectedShapeSpecification getInspectedShapeSpecification() {
		if (getEditor() != null) {
			return getEditor().getInspectedShapeSpecification();
		}
		return null;
	}

	public InspectedConnectorSpecification getInspectedConnectorSpecification() {
		if (getEditor() != null) {
			return getEditor().getInspectedConnectorSpecification();
		}
		return null;
	}

	public InspectedLocationSizeProperties getInspectedLocationSizeProperties() {
		if (getEditor() != null) {
			return getEditor().getInspectedLocationSizeProperties();
		}
		return null;
	}

	public InspectedLayoutManagerSpecifications getInspectedLayoutManagerSpecifications() {
		if (getEditor() != null) {
			return getEditor().getInspectedLayoutManagerSpecifications();
		}
		return null;
	}

	public abstract Inspector<ForegroundStyle> getForegroundStyleInspector();

	public abstract Inspector<BackgroundStyleFactory> getBackgroundStyleInspector();

	public abstract Inspector<InspectedTextProperties> getTextPropertiesInspector();

	public abstract Inspector<ShadowStyle> getShadowStyleInspector();

	public abstract Inspector<ShapeSpecificationFactory> getShapeInspector();

	public abstract Inspector<ConnectorSpecificationFactory> getConnectorInspector();

	public abstract Inspector<InspectedLocationSizeProperties> getLocationSizeInspector();

	public abstract Inspector<InspectedLayoutManagerSpecifications> getLayoutManagersInspector();

	public static interface Inspector<D> {
		public void setData(D data);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}

}
