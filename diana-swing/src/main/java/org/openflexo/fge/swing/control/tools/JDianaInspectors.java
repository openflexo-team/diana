/*
 * (c) Copyright 2010-2011 AgileBirds
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
package org.openflexo.fge.swing.control.tools;

import java.util.logging.Logger;

import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.tools.BackgroundStyleFactory;
import org.openflexo.fge.control.tools.ConnectorSpecificationFactory;
import org.openflexo.fge.control.tools.DianaInspectors;
import org.openflexo.fge.control.tools.InspectedLocationSizeProperties;
import org.openflexo.fge.control.tools.ShapeSpecificationFactory;
import org.openflexo.fge.swing.SwingViewFactory;
import org.openflexo.fge.swing.control.tools.JDianaInspectors.JInspector;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.widget.DefaultFIBCustomComponent;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
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
	private JInspector<TextStyle> textStyleInspector;
	private JInspector<ShadowStyle> shadowInspector;
	private JInspector<ShapeSpecificationFactory> shapeInspector;
	private JInspector<ConnectorSpecificationFactory> connectorInspector;
	private JInspector<InspectedLocationSizeProperties> locationSizeInspector;

	private final FlexoCollabsiblePanelGroup panelGroup;

	public JDianaInspectors() {
		panelGroup = new FlexoCollabsiblePanelGroup();
		panelGroup.addContents(getForegroundStyleInspector().getTitle(), getForegroundStyleInspector());
		panelGroup.addContents(getBackgroundStyleInspector().getTitle(), getBackgroundStyleInspector());
		panelGroup.addContents(getTextStyleInspector().getTitle(), getTextStyleInspector());
		panelGroup.addContents(getShadowStyleInspector().getTitle(), getShadowStyleInspector());
		panelGroup.addContents(getLocationSizeInspector().getTitle(), getLocationSizeInspector());
		panelGroup.addContents(getShapeInspector().getTitle(), getShapeInspector());
		panelGroup.addContents(getConnectorInspector().getTitle(), getConnectorInspector());
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
		if (textStyleInspector != null) {
			textStyleInspector.setData(getInspectedTextStyle());
		}
		if (shadowInspector != null) {
			shadowInspector.setData(getInspectedShadowStyle());
		}
		if (backgroundStyleInspector != null) {
			backgroundStyleInspector.setData(getInspectedBackgroundStyle().getStyleFactory());
		}
		if (shapeInspector != null) {
			shapeInspector.setData(getInspectedShapeSpecification().getStyleFactory());
		}
		if (connectorInspector != null) {
			connectorInspector.setData(getInspectedConnectorSpecification().getStyleFactory());
		}
		if (locationSizeInspector != null) {
			locationSizeInspector.setData(getInspectedLocationSizeProperties());
		}
	}

	public static Resource FOREGROUND_STYLE_FIB_FILE = ResourceLocator.locateResource("Fib/ForegroundStylePanel.fib");
	public static Resource TEXT_STYLE_FIB_FILE = ResourceLocator.locateResource("Fib/TextStylePanel.fib");
	public static Resource SHADOW_STYLE_FIB_FILE = ResourceLocator.locateResource("Fib/ShadowStylePanel.fib");
	public static Resource BACKGROUND_STYLE_FIB_FILE = ResourceLocator.locateResource("Fib/BackgroundStylePanel.fib");
	public static Resource SHAPE_SPECIFICATION_PANEL_FIB_FILE = ResourceLocator.locateResource("Fib/ShapeSelectorPanel.fib");
	public static Resource CONNECTOR_SPECIFICATION_PANEL_FIB_FILE = ResourceLocator.locateResource("Fib/ConnectorSelectorPanel.fib");
	public static Resource LOCATION_SIZE_FIB_FILE = ResourceLocator.locateResource("Fib/LocationSizePanel.fib");

	@Override
	public JInspector<ForegroundStyle> getForegroundStyleInspector() {
		if (foregroundStyleInspector == null) {
			foregroundStyleInspector = new JInspector<ForegroundStyle>(FIBLibrary.instance().retrieveFIBComponent(
					FOREGROUND_STYLE_FIB_FILE, true), getInspectedForegroundStyle(), "Foreground", ForegroundStyle.class);
		}
		return foregroundStyleInspector;
	}

	@Override
	public JInspector<BackgroundStyleFactory> getBackgroundStyleInspector() {
		if (backgroundStyleInspector == null) {
			// bsFactory = new BackgroundStyleFactory(getEditor().getCurrentBackgroundStyle());
			backgroundStyleInspector = new JInspector<BackgroundStyleFactory>(FIBLibrary.instance().retrieveFIBComponent(
					BACKGROUND_STYLE_FIB_FILE, true), (getInspectedBackgroundStyle() != null ? getInspectedBackgroundStyle()
					.getStyleFactory() : null), "Background", BackgroundStyleFactory.class);
		}
		return backgroundStyleInspector;
	}

	@Override
	public JInspector<TextStyle> getTextStyleInspector() {
		if (textStyleInspector == null) {
			textStyleInspector = new JInspector<TextStyle>(FIBLibrary.instance().retrieveFIBComponent(TEXT_STYLE_FIB_FILE, true),
					getInspectedTextStyle(), "Text", TextStyle.class);
		}
		return textStyleInspector;
	}

	@Override
	public JInspector<ShadowStyle> getShadowStyleInspector() {
		if (shadowInspector == null) {
			shadowInspector = new JInspector<ShadowStyle>(FIBLibrary.instance().retrieveFIBComponent(SHADOW_STYLE_FIB_FILE, true),
					getInspectedShadowStyle(), "Shadow", ShadowStyle.class);
		}
		return shadowInspector;
	}

	@Override
	public JInspector<InspectedLocationSizeProperties> getLocationSizeInspector() {
		if (locationSizeInspector == null) {
			locationSizeInspector = new JInspector<InspectedLocationSizeProperties>(FIBLibrary.instance().retrieveFIBComponent(
					LOCATION_SIZE_FIB_FILE, true), getInspectedLocationSizeProperties(), "Location/Size",
					InspectedLocationSizeProperties.class);
		}
		return locationSizeInspector;
	}

	@Override
	public JInspector<ShapeSpecificationFactory> getShapeInspector() {
		if (shapeInspector == null) {
			shapeInspector = new JInspector<ShapeSpecificationFactory>(FIBLibrary.instance().retrieveFIBComponent(
					SHAPE_SPECIFICATION_PANEL_FIB_FILE, true), (getInspectedShapeSpecification() != null ? getInspectedShapeSpecification()
					.getStyleFactory() : null), "Shape", ShapeSpecificationFactory.class);
		}
		return shapeInspector;
	}

	@Override
	public JInspector<ConnectorSpecificationFactory> getConnectorInspector() {
		if (connectorInspector == null) {
			connectorInspector = new JInspector<ConnectorSpecificationFactory>(FIBLibrary.instance().retrieveFIBComponent(
					CONNECTOR_SPECIFICATION_PANEL_FIB_FILE, true),
					(getInspectedConnectorSpecification() != null ? getInspectedConnectorSpecification().getStyleFactory() : null),
					"Connector", ConnectorSpecificationFactory.class);
		}
		return connectorInspector;
	}

	@SuppressWarnings("serial")
	public static class JInspector<T> extends DefaultFIBCustomComponent<T> implements DianaInspectors.Inspector<T> {

		private final String title;
		private final Class<T> representedType;

		protected JInspector(FIBComponent fibComponent, T data, String title, final Class<T> representedType) {
			super(fibComponent, data, (LocalizedDelegate) null);
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
