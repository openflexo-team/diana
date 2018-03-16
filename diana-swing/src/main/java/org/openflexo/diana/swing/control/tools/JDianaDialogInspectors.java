/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-swing, a component of the software infrastructure 
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

package org.openflexo.diana.swing.control.tools;

import java.util.logging.Logger;

import javax.swing.JFrame;

import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.ShadowStyle;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.tools.BackgroundStyleFactory;
import org.openflexo.diana.control.tools.ConnectorSpecificationFactory;
import org.openflexo.diana.control.tools.DianaInspectors;
import org.openflexo.diana.control.tools.InspectedLayoutManagerSpecifications;
import org.openflexo.diana.control.tools.InspectedLocationSizeProperties;
import org.openflexo.diana.control.tools.InspectedTextProperties;
import org.openflexo.diana.control.tools.ShapeSpecificationFactory;
import org.openflexo.diana.swing.SwingViewFactory;
import org.openflexo.diana.swing.control.tools.JDianaDialogInspectors.JDialogInspector;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.swing.utils.JFIBDialog;
import org.openflexo.localization.LocalizedDelegate;

/**
 * SWING implementation of {@link DianaInspectors}
 * 
 * @author sylvain
 * 
 */
public class JDianaDialogInspectors extends DianaInspectors<JDialogInspector<?>, SwingViewFactory> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(JDianaDialogInspectors.class.getPackage().getName());

	private JDialogInspector<ForegroundStyle> foregroundStyleInspector;
	private JDialogInspector<BackgroundStyleFactory> backgroundStyleInspector;
	private JDialogInspector<InspectedTextProperties> textPropertiesInspector;
	private JDialogInspector<ShadowStyle> shadowInspector;
	private JDialogInspector<ShapeSpecificationFactory> shapeInspector;
	private JDialogInspector<ConnectorSpecificationFactory> connectorInspector;
	private JDialogInspector<InspectedLocationSizeProperties> locationSizeInspector;
	private JDialogInspector<InspectedLayoutManagerSpecifications> layoutManagersInspector;

	private final JFrame frame;

	public JDianaDialogInspectors(JFrame frame) {
		this.frame = frame;
	}

	@Override
	public void attachToEditor(AbstractDianaEditor<?, SwingViewFactory, ?> editor) {
		super.attachToEditor(editor);
		if (foregroundStyleInspector != null) {
			foregroundStyleInspector.setData(getInspectedForegroundStyle(), true);
		}
		if (textPropertiesInspector != null) {
			textPropertiesInspector.setData(getInspectedTextProperties(), true);
		}
		if (shadowInspector != null) {
			shadowInspector.setData(getInspectedShadowStyle(), true);
		}
		if (backgroundStyleInspector != null && getInspectedBackgroundStyle() != null) {
			backgroundStyleInspector.setData(getInspectedBackgroundStyle().getStyleFactory(), true);
		}
		if (shapeInspector != null && getInspectedShapeSpecification() != null) {
			shapeInspector.setData(getInspectedShapeSpecification().getStyleFactory(), true);
		}
		if (connectorInspector != null && getInspectedConnectorSpecification() != null) {
			connectorInspector.setData(getInspectedConnectorSpecification().getStyleFactory(), true);
		}
		if (locationSizeInspector != null) {
			locationSizeInspector.setData(getInspectedLocationSizeProperties(), true);
		}
		if (layoutManagersInspector != null) {
			layoutManagersInspector.setData(getInspectedLayoutManagerSpecifications(), true);
		}
	}

	@Override
	public JDialogInspector<ForegroundStyle> getForegroundStyleInspector() {
		if (foregroundStyleInspector == null) {
			foregroundStyleInspector = new JDialogInspector<ForegroundStyle>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(JDianaInspectorsResources.FOREGROUND_STYLE_FIB_FILE, true),
					getInspectedForegroundStyle(), frame, JDianaInspectorsResources.FOREGROUND_NAME);
		}
		return foregroundStyleInspector;
	}

	@Override
	public JDialogInspector<BackgroundStyleFactory> getBackgroundStyleInspector() {
		if (backgroundStyleInspector == null) {
			backgroundStyleInspector = new JDialogInspector<BackgroundStyleFactory>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(JDianaInspectorsResources.BACKGROUND_STYLE_FIB_FILE, true),
					(getInspectedBackgroundStyle() != null ? getInspectedBackgroundStyle().getStyleFactory() : null), frame,
					JDianaInspectorsResources.BACKGROUND_NAME);
		}
		return backgroundStyleInspector;
	}

	@Override
	public JDialogInspector<InspectedTextProperties> getTextPropertiesInspector() {
		if (textPropertiesInspector == null) {
			textPropertiesInspector = new JDialogInspector<InspectedTextProperties>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(JDianaInspectorsResources.TEXT_PROPERTIES_FIB_FILE, true),
					getInspectedTextProperties(), frame, JDianaInspectorsResources.TEXT_NAME);
		}
		return textPropertiesInspector;
	}

	@Override
	public JDialogInspector<ShadowStyle> getShadowStyleInspector() {
		if (shadowInspector == null) {
			shadowInspector = new JDialogInspector<ShadowStyle>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(JDianaInspectorsResources.SHADOW_STYLE_FIB_FILE, true),
					getInspectedShadowStyle(), frame, JDianaInspectorsResources.SHADOW_NAME);
		}
		return shadowInspector;
	}

	@Override
	public JDialogInspector<InspectedLocationSizeProperties> getLocationSizeInspector() {
		if (locationSizeInspector == null) {
			locationSizeInspector = new JDialogInspector<InspectedLocationSizeProperties>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(JDianaInspectorsResources.LOCATION_SIZE_FIB_FILE, true),
					getInspectedLocationSizeProperties(), frame, JDianaInspectorsResources.LOCATION_NAME);
		}
		return locationSizeInspector;
	}

	@Override
	public JDialogInspector<ShapeSpecificationFactory> getShapeInspector() {
		if (shapeInspector == null) {
			shapeInspector = new JDialogInspector<ShapeSpecificationFactory>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY
							.retrieveFIBComponent(JDianaInspectorsResources.SHAPE_SPECIFICATION_PANEL_FIB_FILE, true),
					(getInspectedShapeSpecification() != null ? getInspectedShapeSpecification().getStyleFactory() : null), frame,
					JDianaInspectorsResources.SHAPE_NAME);
		}
		return shapeInspector;
	}

	@Override
	public JDialogInspector<ConnectorSpecificationFactory> getConnectorInspector() {
		if (connectorInspector == null) {
			connectorInspector = new JDialogInspector<ConnectorSpecificationFactory>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY
							.retrieveFIBComponent(JDianaInspectorsResources.CONNECTOR_SPECIFICATION_PANEL_FIB_FILE, true),
					(getInspectedConnectorSpecification() != null ? getInspectedConnectorSpecification().getStyleFactory() : null), frame,
					JDianaInspectorsResources.CONNECTOR_NAME);
		}
		return connectorInspector;
	}

	@Override
	public JDialogInspector<InspectedLayoutManagerSpecifications> getLayoutManagersInspector() {
		if (layoutManagersInspector == null) {
			layoutManagersInspector = new JDialogInspector<InspectedLayoutManagerSpecifications>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(JDianaInspectorsResources.LAYOUT_MANAGERS_FIB_FILE, true),
					getInspectedLayoutManagerSpecifications(), frame, JDianaInspectorsResources.LAYOUT_NAME);
		}
		return layoutManagersInspector;
	}

	@SuppressWarnings("serial")
	public static class JDialogInspector<T> extends JFIBDialog<T> implements DianaInspectors.Inspector<T> {

		protected JDialogInspector(FIBComponent fibComponent, T data, JFrame frame, String title) {
			super(fibComponent, data, frame, false, (LocalizedDelegate) null);
			setTitle(title);
			setAlwaysOnTop(true);
		}

	}

	@Override
	public SwingViewFactory getDianaFactory() {
		return SwingViewFactory.INSTANCE;
	}

}
