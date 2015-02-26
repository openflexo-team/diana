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

package org.openflexo.fge.swing.control.tools;

import java.util.logging.Logger;

import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.tools.BackgroundStyleFactory;
import org.openflexo.fge.control.tools.ConnectorSpecificationFactory;
import org.openflexo.fge.control.tools.DianaInspectors;
import org.openflexo.fge.control.tools.InspectedLayoutManagerSpecifications;
import org.openflexo.fge.control.tools.InspectedLocationSizeProperties;
import org.openflexo.fge.control.tools.ShapeSpecificationFactory;
import org.openflexo.fge.swing.SwingViewFactory;
import org.openflexo.fge.swing.control.tools.JDianaInspectors.JInspector;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.swing.FIBJPanel;
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
	private JInspector<InspectedLayoutManagerSpecifications> layoutManagersInspector;

	private final FlexoCollabsiblePanelGroup panelGroup;

	public JDianaInspectors() {
		panelGroup = new FlexoCollabsiblePanelGroup();
		panelGroup.addContents(getForegroundStyleInspector().getTitle(), getForegroundStyleInspector());
		panelGroup.addContents(getBackgroundStyleInspector().getTitle(), getBackgroundStyleInspector());
		panelGroup.addContents(getTextStyleInspector().getTitle(), getTextStyleInspector());
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
		if (layoutManagersInspector != null) {
			layoutManagersInspector.setData(getInspectedLayoutManagerSpecifications());
		}
	}

	public static Resource FOREGROUND_STYLE_FIB_FILE = ResourceLocator.locateResource("Fib/ForegroundStylePanel.fib");
	public static Resource TEXT_STYLE_FIB_FILE = ResourceLocator.locateResource("Fib/TextStylePanel.fib");
	public static Resource SHADOW_STYLE_FIB_FILE = ResourceLocator.locateResource("Fib/ShadowStylePanel.fib");
	public static Resource BACKGROUND_STYLE_FIB_FILE = ResourceLocator.locateResource("Fib/BackgroundStylePanel.fib");
	public static Resource SHAPE_SPECIFICATION_PANEL_FIB_FILE = ResourceLocator.locateResource("Fib/ShapeSelectorPanel.fib");
	public static Resource CONNECTOR_SPECIFICATION_PANEL_FIB_FILE = ResourceLocator.locateResource("Fib/ConnectorSelectorPanel.fib");
	public static Resource LOCATION_SIZE_FIB_FILE = ResourceLocator.locateResource("Fib/LocationSizePanel.fib");
	public static Resource LAYOUT_MANAGERS_FIB_FILE = ResourceLocator.locateResource("Fib/LayoutManagersPanel.fib");

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

	@Override
	public JInspector<InspectedLayoutManagerSpecifications> getLayoutManagersInspector() {
		if (layoutManagersInspector == null) {
			layoutManagersInspector = new JInspector<InspectedLayoutManagerSpecifications>(FIBLibrary.instance().retrieveFIBComponent(
					LAYOUT_MANAGERS_FIB_FILE, true), getInspectedLayoutManagerSpecifications(), "Layout Managers",
					InspectedLayoutManagerSpecifications.class);
		}
		return layoutManagersInspector;
	}

	@SuppressWarnings("serial")
	public static class JInspector<T> extends FIBJPanel<T> implements DianaInspectors.Inspector<T> {

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
