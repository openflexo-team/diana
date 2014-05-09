package org.openflexo.fge.drawingeditor;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;

public class DiagramEditorVisualMock {
	private static final Logger	logger	= FlexoLogger.getLogger(LaunchDiagramEditor.class.getPackage().getName());

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				init();
			}
		});
	}

	private static void init() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
		} catch (Exception e) {
			final String msg = "Error during initialization";
			logger.log(Level.SEVERE, msg, e);
		}
		FlexoLocalization.initWith(DiagramEditorApplication.LOCALIZATION);

		// StringEncoder.getDefaultInstance()._addConverter(DataBinding.CONVERTER);

		DiagramEditorApplication application = new DiagramEditorApplication();
		application.setExitOnClose();
		application.showMainPanel();
		application.newDiagramEditor();
	}
}
