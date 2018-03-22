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

import org.openflexo.diana.DianaCoreUtils;
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
import org.openflexo.diana.swing.control.tools.JDianaInspectors.JInspector;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.swing.utils.FIBJPanel;
import org.openflexo.swing.FlexoCollabsiblePanelGroup;

/**
 * SWING implementation of {@link DianaInspectors}
 * 
 * @author sylvain
 * 
 */
public class JDianaInspectors extends DianaInspectors<JInspector<?>, SwingViewFactory> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(JDianaInspectors.class.getPackage().getName());

	private JInspector<ForegroundStyle> foregroundStyleInspector;
	private JInspector<BackgroundStyleFactory> backgroundStyleInspector;
	private JInspector<InspectedTextProperties> textPropertiesInspector;
	private JInspector<ShadowStyle> shadowInspector;
	private JInspector<ShapeSpecificationFactory> shapeInspector;
	private JInspector<ConnectorSpecificationFactory> connectorInspector;
	private JInspector<InspectedLocationSizeProperties> locationSizeInspector;
	private JInspector<InspectedLayoutManagerSpecifications> layoutManagersInspector;

	private final FlexoCollabsiblePanelGroup panelGroup;

	public JDianaInspectors() {
		panelGroup = new FlexoCollabsiblePanelGroup();
		panelGroup.addContents(getForegroundStyleInspector().getTitle(), getForegroundStyleInspector());
		panelGroup.addContents(getBackgroundStyleInspector().getTitle(), getBackgroundStyleInspector());
		panelGroup.addContents(getTextPropertiesInspector().getTitle(), getTextPropertiesInspector());
		panelGroup.addContents(getShadowStyleInspector().getTitle(), getShadowStyleInspector());
		panelGroup.addContents(getLocationSizeInspector().getTitle(), getLocationSizeInspector());
		panelGroup.addContents(getLayoutManagersInspector().getTitle(), getLayoutManagersInspector());
		panelGroup.addContents(getShapeInspector().getTitle(), getShapeInspector());
		panelGroup.addContents(getConnectorInspector().getTitle(), getConnectorInspector());
		panelGroup.setOpenedPanel(0); // Open foreground style inspector
	}

	public FlexoCollabsiblePanelGroup getPanelGroup() {
		return panelGroup;
	}

	@Override
	public void attachToEditor(AbstractDianaEditor<?, SwingViewFactory, ?> editor) {
		super.attachToEditor(editor);
		if (foregroundStyleInspector != null) {
			foregroundStyleInspector.setData(getInspectedForegroundStyle());
		}
		if (textPropertiesInspector != null) {
			textPropertiesInspector.setData(getInspectedTextProperties());
		}
		if (shadowInspector != null) {
			shadowInspector.setData(getInspectedShadowStyle());
		}
		if (backgroundStyleInspector != null && getInspectedBackgroundStyle() != null) {
			backgroundStyleInspector.setData(getInspectedBackgroundStyle().getStyleFactory());
		}
		if (shapeInspector != null && getInspectedShapeSpecification() != null) {
			shapeInspector.setData(getInspectedShapeSpecification().getStyleFactory());
		}
		if (connectorInspector != null && getInspectedConnectorSpecification() != null) {
			connectorInspector.setData(getInspectedConnectorSpecification().getStyleFactory());
		}
		if (locationSizeInspector != null) {
			locationSizeInspector.setData(getInspectedLocationSizeProperties());
		}
		if (layoutManagersInspector != null) {
			layoutManagersInspector.setData(getInspectedLayoutManagerSpecifications());
		}
	}

	@Override
	public JInspector<ForegroundStyle> getForegroundStyleInspector() {
		if (foregroundStyleInspector == null) {
			foregroundStyleInspector = new JInspector<ForegroundStyle>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(JDianaInspectorsResources.FOREGROUND_STYLE_FIB_FILE, true),
					getInspectedForegroundStyle(), JDianaInspectorsResources.FOREGROUND_NAME, ForegroundStyle.class);
		}
		return foregroundStyleInspector;
	}

	@Override
	public JInspector<BackgroundStyleFactory> getBackgroundStyleInspector() {
		if (backgroundStyleInspector == null) {
			// bsFactory = new BackgroundStyleFactory(getEditor().getCurrentBackgroundStyle());
			backgroundStyleInspector = new JInspector<BackgroundStyleFactory>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(JDianaInspectorsResources.BACKGROUND_STYLE_FIB_FILE, true),
					(getInspectedBackgroundStyle() != null ? getInspectedBackgroundStyle().getStyleFactory() : null),
					JDianaInspectorsResources.BACKGROUND_NAME, BackgroundStyleFactory.class);
		}
		return backgroundStyleInspector;
	}

	@Override
	public JInspector<InspectedTextProperties> getTextPropertiesInspector() {
		if (textPropertiesInspector == null) {
			textPropertiesInspector = new JInspector<InspectedTextProperties>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(JDianaInspectorsResources.TEXT_PROPERTIES_FIB_FILE, true),
					getInspectedTextProperties(), JDianaInspectorsResources.TEXT_NAME, InspectedTextProperties.class);
		}
		return textPropertiesInspector;
	}

	@Override
	public JInspector<ShadowStyle> getShadowStyleInspector() {
		if (shadowInspector == null) {
			shadowInspector = new JInspector<ShadowStyle>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(JDianaInspectorsResources.SHADOW_STYLE_FIB_FILE, true),
					getInspectedShadowStyle(), JDianaInspectorsResources.SHADOW_NAME, ShadowStyle.class);
		}
		return shadowInspector;
	}

	@Override
	public JInspector<InspectedLocationSizeProperties> getLocationSizeInspector() {
		if (locationSizeInspector == null) {
			locationSizeInspector = new JInspector<InspectedLocationSizeProperties>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(JDianaInspectorsResources.LOCATION_SIZE_FIB_FILE, true),
					getInspectedLocationSizeProperties(), JDianaInspectorsResources.LOCATION_NAME, InspectedLocationSizeProperties.class);
		}
		return locationSizeInspector;
	}

	@Override
	public JInspector<ShapeSpecificationFactory> getShapeInspector() {
		if (shapeInspector == null) {
			shapeInspector = new JInspector<ShapeSpecificationFactory>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY
							.retrieveFIBComponent(JDianaInspectorsResources.SHAPE_SPECIFICATION_PANEL_FIB_FILE, true),
					(getInspectedShapeSpecification() != null ? getInspectedShapeSpecification().getStyleFactory() : null),
					JDianaInspectorsResources.SHAPE_NAME, ShapeSpecificationFactory.class);
		}
		return shapeInspector;
	}

	@Override
	public JInspector<ConnectorSpecificationFactory> getConnectorInspector() {
		if (connectorInspector == null) {
			connectorInspector = new JInspector<ConnectorSpecificationFactory>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY
							.retrieveFIBComponent(JDianaInspectorsResources.CONNECTOR_SPECIFICATION_PANEL_FIB_FILE, true),
					(getInspectedConnectorSpecification() != null ? getInspectedConnectorSpecification().getStyleFactory() : null),
					JDianaInspectorsResources.CONNECTOR_NAME, ConnectorSpecificationFactory.class);
		}
		return connectorInspector;
	}

	@Override
	public JInspector<InspectedLayoutManagerSpecifications> getLayoutManagersInspector() {
		if (layoutManagersInspector == null) {
			layoutManagersInspector = new JInspector<InspectedLayoutManagerSpecifications>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(JDianaInspectorsResources.LAYOUT_MANAGERS_FIB_FILE, true),
					getInspectedLayoutManagerSpecifications(), JDianaInspectorsResources.LAYOUT_NAME,
					InspectedLayoutManagerSpecifications.class);
		}
		return layoutManagersInspector;
	}

	@SuppressWarnings("serial")
	public static class JInspector<T> extends FIBJPanel<T> implements DianaInspectors.Inspector<T> {

		private final String title;
		private final Class<T> representedType;

		protected JInspector(FIBComponent fibComponent, T data, String title, final Class<T> representedType) {
			super(fibComponent, data, DianaCoreUtils.DIANA_LOCALIZATION);
			this.representedType = representedType;
			this.title = title;
		}

		@Override
		public void setData(T data) {
			setEditedObject(data);
		}

		@Override
		public Class<T> getRepresentedType() {
			return representedType;
		}

		@Override
		public void delete() {
		}

		public String getTitle() {
			return title;
		}
	}

	@Override
	public SwingViewFactory getDianaFactory() {
		return SwingViewFactory.INSTANCE;
	}

}
