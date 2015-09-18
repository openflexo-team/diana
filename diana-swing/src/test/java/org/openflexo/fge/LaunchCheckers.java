/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-swing, a component of the software infrastructure developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either version 1.1 of the License, or any later version ),
 * which is available at https://joinup.ec.europa.eu/software/page/eupl/licence-eupl and the GNU General Public License (GPL, either version
 * 3 of the License, or any later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you must include the following additional permission.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or combining it with software containing parts covered by the terms of EPL
 * 1.0, the licensors of this Program grant you additional permission to convey the resulting work. *
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org) or visit www.openflexo.org if you need additional information.
 * 
 *//*
	
	package org.openflexo.fge;
	
	import java.awt.BorderLayout;
	import java.awt.Dimension;
	import java.awt.Frame;
	import java.io.IOException;
	import java.util.logging.Level;
	import java.util.logging.Logger;
	
	import javax.swing.JDialog;
	import javax.swing.JScrollPane;
	
	import org.openflexo.checkers.CheckersBoard;
	import org.openflexo.checkers.CheckersDrawing;
	import org.openflexo.fge.swing.JDianaInteractiveViewer;
	import org.openflexo.fge.swing.SwingViewFactory;
	import org.openflexo.fge.swing.control.SwingToolFactory;
	import org.openflexo.logging.FlexoLogger;
	import org.openflexo.logging.FlexoLoggingManager;
	import org.openflexo.model.exceptions.ModelDefinitionException;
	
	public class LaunchCheckers {
	
	private static final Logger LOGGER = FlexoLogger.getLogger(LaunchCheckers.class.getPackage().getName());
	
	public static void main(String[] args) {
		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		showPanel();
	}
	
	public static void showPanel() {
		final JDialog dialog = new JDialog((Frame) null, false);
	
		// final TestInspector inspector = new TestInspector();
	
		final CheckersDrawing d = makeDrawing();
		final JDianaInteractiveViewer<CheckersBoard> dc = new JDianaInteractiveViewer<>(d, d.getFactory(), SwingViewFactory.INSTANCE,
				SwingToolFactory.DEFAULT);
		// dc.disablePaintingCache();
		dc.getDrawingView().setName("[NO_CACHE]");
		dialog.getContentPane().add(new JScrollPane(dc.getDrawingView()), BorderLayout.CENTER);
	
		dialog.setPreferredSize(new Dimension(550, 600));
		dialog.validate();
		dialog.pack();
	
		dialog.setVisible(true);
	}
	
	public static CheckersDrawing makeDrawing() {
		FGEModelFactory factory = null;
		try {
			factory = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		CheckersBoard board = new CheckersBoard();
		CheckersDrawing returned = new CheckersDrawing(board, factory);
		returned.printGraphicalObjectHierarchy();
		return returned;
	}
	
	}*/
