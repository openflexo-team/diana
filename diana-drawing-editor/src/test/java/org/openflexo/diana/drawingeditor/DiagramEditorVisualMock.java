/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-drawing-editor, a component of the software infrastructure 
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

package org.openflexo.diana.drawingeditor;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.openflexo.diana.drawingeditor.DiagramEditorApplication;
import org.openflexo.diana.drawingeditor.LaunchDiagramEditor;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;

public class DiagramEditorVisualMock {
	private static final Logger logger = FlexoLogger.getLogger(LaunchDiagramEditor.class.getPackage().getName());

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> init());
	}

	private static void init() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
		} catch (Exception e) {
			final String msg = "Error during initialization";
			logger.log(Level.SEVERE, msg, e);
		}
		FlexoLocalization.initWith(DiagramEditorApplication.DIAGRAM_EDITOR_LOCALIZATION);

		// StringEncoder.getDefaultInstance()._addConverter(DataBinding.CONVERTER);

		DiagramEditorApplication application = new DiagramEditorApplication();
		application.setExitOnClose();
		application.showMainPanel();
		application.newDiagramEditor();
	}
}
