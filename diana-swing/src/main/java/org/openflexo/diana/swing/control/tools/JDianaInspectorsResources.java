package org.openflexo.diana.swing.control.tools;

import org.openflexo.diana.DianaCoreUtils;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public class JDianaInspectorsResources {
	public static Resource FOREGROUND_STYLE_FIB_FILE = ResourceLocator.locateResource("Fib/ForegroundStylePanel.fib");
	public static String FOREGROUND_NAME = DianaCoreUtils.DIANA_LOCALIZATION.localizedForKey("foreground");

	public static Resource TEXT_PROPERTIES_FIB_FILE = ResourceLocator.locateResource("Fib/TextPropertiesPanel.fib");
	public static String TEXT_NAME = DianaCoreUtils.DIANA_LOCALIZATION.localizedForKey("text");

	public static Resource SHADOW_STYLE_FIB_FILE = ResourceLocator.locateResource("Fib/ShadowStylePanel.fib");
	public static String SHADOW_NAME = DianaCoreUtils.DIANA_LOCALIZATION.localizedForKey("shadow");

	public static Resource BACKGROUND_STYLE_FIB_FILE = ResourceLocator.locateResource("Fib/BackgroundStylePanel.fib");
	public static String BACKGROUND_NAME = DianaCoreUtils.DIANA_LOCALIZATION.localizedForKey("background");

	public static Resource SHAPE_SPECIFICATION_PANEL_FIB_FILE = ResourceLocator.locateResource("Fib/ShapeSelectorPanel.fib");
	public static String SHAPE_NAME = DianaCoreUtils.DIANA_LOCALIZATION.localizedForKey("shape");

	public static Resource CONNECTOR_SPECIFICATION_PANEL_FIB_FILE = ResourceLocator.locateResource("Fib/ConnectorSelectorPanel.fib");
	public static String CONNECTOR_NAME = DianaCoreUtils.DIANA_LOCALIZATION.localizedForKey("connector");

	public static Resource LOCATION_SIZE_FIB_FILE = ResourceLocator.locateResource("Fib/LocationSizePanel.fib");
	public static String LOCATION_NAME = DianaCoreUtils.DIANA_LOCALIZATION.localizedForKey("location_size");

	public static Resource CONTROL_FIB_FILE = ResourceLocator.locateResource("Fib/ControlPanel.fib");
	public static String CONTROL_NAME = DianaCoreUtils.DIANA_LOCALIZATION.localizedForKey("control");

	public static Resource LAYOUT_MANAGERS_FIB_FILE = ResourceLocator.locateResource("Fib/LayoutManagersPanel.fib");
	public static String LAYOUT_NAME = DianaCoreUtils.DIANA_LOCALIZATION.localizedForKey("layout_managers");
}
