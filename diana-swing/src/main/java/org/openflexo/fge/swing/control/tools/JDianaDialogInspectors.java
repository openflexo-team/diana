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
import org.openflexo.fge.swing.control.tools.JDianaDialogInspectors.JDialogInspector;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

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
	private JDialogInspector<TextStyle> textStyleInspector;
	private JDialogInspector<ShadowStyle> shadowInspector;
	private JDialogInspector<ShapeSpecificationFactory> shapeInspector;
	private JDialogInspector<ConnectorSpecificationFactory> connectorInspector;
	private JDialogInspector<InspectedLocationSizeProperties> locationSizeInspector;

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

	public static Resource FOREGROUND_STYLE_FIB_FILE = ResourceLocator.locateResource("Fib/ForegroundStylePanel.fib");
	public static Resource TEXT_STYLE_FIB_FILE = ResourceLocator.locateResource("Fib/TextStylePanel.fib");
	public static Resource SHADOW_STYLE_FIB_FILE = ResourceLocator.locateResource("Fib/ShadowStylePanel.fib");
	public static Resource BACKGROUND_STYLE_FIB_FILE = ResourceLocator.locateResource("Fib/BackgroundStylePanel.fib");
	public static Resource SHAPE_SPECIFICATION_PANEL_FIB_FILE = ResourceLocator.locateResource("Fib/ShapeSelectorPanel.fib");
	public static Resource CONNECTOR_SPECIFICATION_PANEL_FIB_FILE = ResourceLocator.locateResource("Fib/ConnectorSelectorPanel.fib");
	public static Resource LOCATION_SIZE_FIB_FILE = ResourceLocator.locateResource("Fib/LocationSizePanel.fib");

	@Override
	public JDialogInspector<ForegroundStyle> getForegroundStyleInspector() {
		if (foregroundStyleInspector == null) {
			foregroundStyleInspector = new JDialogInspector<ForegroundStyle>(FIBLibrary.instance().retrieveFIBComponent(
					FOREGROUND_STYLE_FIB_FILE, true), getInspectedForegroundStyle(), frame, "Foreground");
		}
		return foregroundStyleInspector;
	}

	@Override
	public JDialogInspector<BackgroundStyleFactory> getBackgroundStyleInspector() {
		if (backgroundStyleInspector == null) {
			// bsFactory = new BackgroundStyleFactory(getEditor().getCurrentBackgroundStyle());
			backgroundStyleInspector = new JDialogInspector<BackgroundStyleFactory>(FIBLibrary.instance().retrieveFIBComponent(
					BACKGROUND_STYLE_FIB_FILE, true), (getInspectedBackgroundStyle() != null ? getInspectedBackgroundStyle()
					.getStyleFactory() : null), frame, "Background");
		}
		return backgroundStyleInspector;
	}

	@Override
	public JDialogInspector<TextStyle> getTextStyleInspector() {
		if (textStyleInspector == null) {
			textStyleInspector = new JDialogInspector<TextStyle>(FIBLibrary.instance().retrieveFIBComponent(TEXT_STYLE_FIB_FILE, true),
					getInspectedTextStyle(), frame, "Text");
		}
		return textStyleInspector;
	}

	@Override
	public JDialogInspector<ShadowStyle> getShadowStyleInspector() {
		if (shadowInspector == null) {
			shadowInspector = new JDialogInspector<ShadowStyle>(FIBLibrary.instance().retrieveFIBComponent(SHADOW_STYLE_FIB_FILE, true),
					getInspectedShadowStyle(), frame, "Shadow");
		}
		return shadowInspector;
	}

	@Override
	public JDialogInspector<InspectedLocationSizeProperties> getLocationSizeInspector() {
		if (locationSizeInspector == null) {
			locationSizeInspector = new JDialogInspector<InspectedLocationSizeProperties>(FIBLibrary.instance().retrieveFIBComponent(
					LOCATION_SIZE_FIB_FILE, true), getInspectedLocationSizeProperties(), frame, "Location/Size");
		}
		return locationSizeInspector;
	}

	@Override
	public JDialogInspector<ShapeSpecificationFactory> getShapeInspector() {
		if (shapeInspector == null) {
			shapeInspector = new JDialogInspector<ShapeSpecificationFactory>(FIBLibrary.instance().retrieveFIBComponent(
					SHAPE_SPECIFICATION_PANEL_FIB_FILE, true), (getInspectedShapeSpecification() != null ? getInspectedShapeSpecification()
					.getStyleFactory() : null), frame, "Shape");
		}
		return shapeInspector;
	}

	@Override
	public JDialogInspector<ConnectorSpecificationFactory> getConnectorInspector() {
		if (connectorInspector == null) {
			connectorInspector = new JDialogInspector<ConnectorSpecificationFactory>(FIBLibrary.instance().retrieveFIBComponent(
					CONNECTOR_SPECIFICATION_PANEL_FIB_FILE, true),
					(getInspectedConnectorSpecification() != null ? getInspectedConnectorSpecification().getStyleFactory() : null), frame,
					"Connector");
		}
		return connectorInspector;
	}

	@SuppressWarnings("serial")
	public static class JDialogInspector<T> extends FIBDialog<T> implements DianaInspectors.Inspector<T> {

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
