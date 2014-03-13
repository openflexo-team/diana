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

import javax.swing.JFrame;

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
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.toolbox.ResourceLocation;
import org.openflexo.toolbox.ResourceLocator;

/**
 * SWING implementation of {@link DianaInspectors}
 * 
 * @author sylvain
 * 
 */
public class JDianaInspectors extends DianaInspectors<JInspector<?>, SwingViewFactory> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(JDianaInspectors.class.getPackage().getName());
	private static final ResourceLocator rl = ResourceLocator.getResourceLocator();

	private JInspector<ForegroundStyle> foregroundStyleInspector;
	private JInspector<BackgroundStyleFactory> backgroundStyleInspector;
	private JInspector<TextStyle> textStyleInspector;
	private JInspector<ShadowStyle> shadowInspector;
	private JInspector<ShapeSpecificationFactory> shapeInspector;
	private JInspector<ConnectorSpecificationFactory> connectorInspector;
	private JInspector<InspectedLocationSizeProperties> locationSizeInspector;

	private JFrame frame;

	public JDianaInspectors(JFrame frame) {
		this.frame = frame;
	}

	@Override
	public void attachToEditor(AbstractDianaEditor<?, SwingViewFactory, ?> editor) {
		super.attachToEditor(editor);
		if (foregroundStyleInspector != null) {
			foregroundStyleInspector.setData(getInspectedForegroundStyle(), true);
		}
		if (textStyleInspector != null) {
			textStyleInspector.setData(getInspectedTextStyle(), true);
		}
		if (shadowInspector != null) {
			shadowInspector.setData(getInspectedShadowStyle(), true);
		}
		if (backgroundStyleInspector != null) {
			backgroundStyleInspector.setData(getInspectedBackgroundStyle().getStyleFactory(), true);
		}
		if (shapeInspector != null) {
			shapeInspector.setData(getInspectedShapeSpecification().getStyleFactory(), true);
		}
		if (connectorInspector != null) {
			connectorInspector.setData(getInspectedConnectorSpecification().getStyleFactory(), true);
		}
		if (locationSizeInspector != null) {
			locationSizeInspector.setData(getInspectedLocationSizeProperties(), true);
		}
	}

	public static ResourceLocation FOREGROUND_STYLE_FIB_FILE = rl.locateResource("Fib/ForegroundStylePanel.fib");
	public static ResourceLocation TEXT_STYLE_FIB_FILE = rl.locateResource("Fib/TextStylePanel.fib");
	public static ResourceLocation SHADOW_STYLE_FIB_FILE = rl.locateResource("Fib/ShadowStylePanel.fib");
	public static ResourceLocation BACKGROUND_STYLE_FIB_FILE = rl.locateResource("Fib/BackgroundStylePanel.fib");
	public static ResourceLocation SHAPE_SPECIFICATION_PANEL_FIB_FILE = rl.locateResource("Fib/ShapeSelectorPanel.fib");
	public static ResourceLocation CONNECTOR_SPECIFICATION_PANEL_FIB_FILE = rl.locateResource("Fib/ConnectorSelectorPanel.fib");
	public static ResourceLocation LOCATION_SIZE_FIB_FILE = rl.locateResource("Fib/LocationSizePanel.fib");

	public JInspector<ForegroundStyle> getForegroundStyleInspector() {
		if (foregroundStyleInspector == null) {
			foregroundStyleInspector = new JInspector<ForegroundStyle>(FIBLibrary.instance()
					.retrieveFIBComponent(FOREGROUND_STYLE_FIB_FILE,true), getInspectedForegroundStyle(), frame, "Foreground");
		}
		return foregroundStyleInspector;
	}

	public JInspector<BackgroundStyleFactory> getBackgroundStyleInspector() {
		if (backgroundStyleInspector == null) {
			// bsFactory = new BackgroundStyleFactory(getEditor().getCurrentBackgroundStyle());
			backgroundStyleInspector = new JInspector<BackgroundStyleFactory>(FIBLibrary.instance().retrieveFIBComponent(
					BACKGROUND_STYLE_FIB_FILE,true), (getInspectedBackgroundStyle() != null ? getInspectedBackgroundStyle().getStyleFactory()
					: null), frame, "Background");
		}
		return backgroundStyleInspector;
	}

	public JInspector<TextStyle> getTextStyleInspector() {
		if (textStyleInspector == null) {
			textStyleInspector = new JInspector<TextStyle>(FIBLibrary.instance().retrieveFIBComponent(TEXT_STYLE_FIB_FILE,true),
					getInspectedTextStyle(), frame, "Text");
		}
		return textStyleInspector;
	}

	public JInspector<ShadowStyle> getShadowStyleInspector() {
		if (shadowInspector == null) {
			shadowInspector = new JInspector<ShadowStyle>(FIBLibrary.instance().retrieveFIBComponent(SHADOW_STYLE_FIB_FILE,true),
					getInspectedShadowStyle(), frame, "Shadow");
		}
		return shadowInspector;
	}

	@Override
	public JInspector<InspectedLocationSizeProperties> getLocationSizeInspector() {
		if (locationSizeInspector == null) {
			locationSizeInspector = new JInspector<InspectedLocationSizeProperties>(FIBLibrary.instance().retrieveFIBComponent(
					LOCATION_SIZE_FIB_FILE,true), getInspectedLocationSizeProperties(), frame, "Location/Size");
		}
		return locationSizeInspector;
	}

	public JInspector<ShapeSpecificationFactory> getShapeInspector() {
		if (shapeInspector == null) {
			shapeInspector = new JInspector<ShapeSpecificationFactory>(FIBLibrary.instance().retrieveFIBComponent(
					SHAPE_SPECIFICATION_PANEL_FIB_FILE,true), (getInspectedShapeSpecification() != null ? getInspectedShapeSpecification()
					.getStyleFactory() : null), frame, "Shape");
		}
		return shapeInspector;
	}

	public JInspector<ConnectorSpecificationFactory> getConnectorInspector() {
		if (connectorInspector == null) {
			connectorInspector = new JInspector<ConnectorSpecificationFactory>(FIBLibrary.instance().retrieveFIBComponent(
					CONNECTOR_SPECIFICATION_PANEL_FIB_FILE,true),
					(getInspectedConnectorSpecification() != null ? getInspectedConnectorSpecification().getStyleFactory() : null), frame,
					"Connector");
		}
		return connectorInspector;
	}

	@SuppressWarnings("serial")
	public static class JInspector<T> extends FIBDialog<T> implements DianaInspectors.Inspector<T> {

		protected JInspector(FIBComponent fibComponent, T data, JFrame frame, String title) {
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
