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

package org.openflexo.fge.graph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.swing.JDianaInteractiveEditor;
import org.openflexo.fge.swing.SwingViewFactory;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.fge.swing.control.tools.JDianaScaleSelector;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.swing.utils.logging.FlexoLoggingViewer;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class LaunchFGEGraphExamples {

	private static final Logger logger = FlexoLogger.getLogger(LaunchFGEGraphExamples.class.getPackage().getName());

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

	public static class TestDrawingController extends JDianaInteractiveEditor<Object> {
		private final JDianaScaleSelector scaleSelector;

		public TestDrawingController(Drawing<Object> aDrawing) {
			super(aDrawing, aDrawing.getFactory(), SwingViewFactory.INSTANCE, SwingToolFactory.DEFAULT);
			scaleSelector = (JDianaScaleSelector) getToolFactory().makeDianaScaleSelector(this);
		}
	}

	public static void showPanel() {
		final JDialog dialog = new JDialog((Frame) null, false);

		JPanel contentPane = new JPanel(new BorderLayout());

		JTabbedPane tabbedPane = new JTabbedPane();

		JPanel example1Panel = new JPanel(new BorderLayout());

		final ExampleFGEContinuousFunctionGraphDrawing d = makeExampleDrawing1();
		final TestDrawingController dc = new TestDrawingController(d);
		example1Panel.add(new JScrollPane(dc.getDrawingView()), BorderLayout.CENTER);
		example1Panel.add(dc.scaleSelector.getComponent(), BorderLayout.NORTH);

		tabbedPane.add(example1Panel, "Example1");

		JPanel example2Panel = new JPanel(new BorderLayout());

		final ExampleFGEDiscreteFunctionGraphDrawing d2 = makeExampleDrawing2();
		final TestDrawingController dc2 = new TestDrawingController(d2);
		example2Panel.add(new JScrollPane(dc2.getDrawingView()), BorderLayout.CENTER);
		example2Panel.add(dc2.scaleSelector.getComponent(), BorderLayout.NORTH);

		tabbedPane.add(example2Panel, "Example2");

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				System.exit(0);
			}
		});

		JButton inspectButton = new JButton("Inspect");
		inspectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// inspector.getWindow().setVisible(true);
			}
		});

		JButton logButton = new JButton("Logs");
		logButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), ApplicationFIBLibraryImpl.instance(), dialog);
			}
		});

		contentPane.add(tabbedPane, BorderLayout.CENTER);

		JPanel controlPanel = new JPanel(new FlowLayout());
		controlPanel.add(closeButton);
		controlPanel.add(inspectButton);
		controlPanel.add(logButton);

		contentPane.add(controlPanel, BorderLayout.SOUTH);

		dialog.setPreferredSize(new Dimension(800, 700));
		dialog.getContentPane().add(contentPane);
		dialog.validate();
		dialog.pack();

		dialog.setVisible(true);

	}

	public static ExampleFGEContinuousFunctionGraphDrawing makeExampleDrawing1() {
		FGEModelFactory factory = null;
		try {
			factory = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		ExampleFGEContinuousFunctionGraphDrawing returned = new ExampleFGEContinuousFunctionGraphDrawing(new Object(), factory);
		return returned;
	}

	public static ExampleFGEDiscreteFunctionGraphDrawing makeExampleDrawing2() {
		FGEModelFactory factory = null;
		try {
			factory = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		ExampleFGEDiscreteFunctionGraphDrawing returned = new ExampleFGEDiscreteFunctionGraphDrawing(new Object(), factory);
		return returned;
	}

}
