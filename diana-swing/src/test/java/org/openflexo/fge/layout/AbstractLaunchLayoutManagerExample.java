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

package org.openflexo.fge.layout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.FGELayoutManager;
import org.openflexo.fge.TestGraph;
import org.openflexo.fge.TestGraphNode;
import org.openflexo.fge.swing.JDianaInteractiveEditor;
import org.openflexo.fge.swing.SwingViewFactory;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.fge.swing.control.tools.JDianaScaleSelector;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.swing.utils.JFIBInspectorController;
import org.openflexo.gina.swing.utils.logging.FlexoLoggingViewer;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.rm.ResourceLocator;

/**
 * Demonstrates how to use GridLayoutManagerImpl
 * 
 * @author sylvain
 * 
 */
public class AbstractLaunchLayoutManagerExample {

	private static final Logger LOGGER = FlexoLogger.getLogger(AbstractLaunchLayoutManagerExample.class.getPackage().getName());

	private static JFIBInspectorController inspector;

	public static TestGraph makeTestGraph() {
		TestGraph graph = new TestGraph();
		TestGraphNode node0 = new TestGraphNode("node0", graph);
		TestGraphNode node1 = new TestGraphNode("node1", graph);
		TestGraphNode node2 = new TestGraphNode("node2", graph);
		TestGraphNode node3 = new TestGraphNode("node3", graph);
		TestGraphNode node4 = new TestGraphNode("node4", graph);
		TestGraphNode node5 = new TestGraphNode("node5", graph);
		TestGraphNode node6 = new TestGraphNode("node6", graph);
		TestGraphNode node7 = new TestGraphNode("node7", graph);
		TestGraphNode node8 = new TestGraphNode("node8", graph);
		TestGraphNode node9 = new TestGraphNode("node9", graph);
		TestGraphNode node10 = new TestGraphNode("node10", graph);
		TestGraphNode node11 = new TestGraphNode("node11", graph);
		TestGraphNode node12 = new TestGraphNode("node12", graph);
		TestGraphNode node13 = new TestGraphNode("node13", graph);
		node0.connectTo(node1);
		node0.connectTo(node2);
		node0.connectTo(node3);
		node0.connectTo(node4);
		node0.connectTo(node5);
		node2.connectTo(node6);
		node2.connectTo(node7);
		node2.connectTo(node8);
		node4.connectTo(node9);
		node4.connectTo(node10);
		node7.connectTo(node11);
		node7.connectTo(node12);
		node7.connectTo(node13);
		return graph;
	}

	public static class TestDrawingController extends JDianaInteractiveEditor<TestGraph> {
		// private final JPopupMenu contextualMenu;
		private final JDianaScaleSelector scaleSelector;

		public TestDrawingController(Drawing aDrawing) {
			super(aDrawing, aDrawing.getFactory(), SwingViewFactory.INSTANCE, SwingToolFactory.DEFAULT);
			scaleSelector = (JDianaScaleSelector) getToolFactory().makeDianaScaleSelector(this);
			// contextualMenu = new JPopupMenu();
			// contextualMenu.add(new JMenuItem("Item"));
		}

	}

	static class LayoutDemoPanel extends JPanel {

		final TestDrawingController drawingController;

		public LayoutDemoPanel(Drawing drawing) {
			super(new BorderLayout());

			drawingController = new TestDrawingController(drawing);

			drawingController.getDrawingView().setName("[NO_CACHE]");
			add(new JScrollPane(drawingController.getDrawingView()), BorderLayout.CENTER);
			add(drawingController.scaleSelector.getComponent(), BorderLayout.NORTH);

			JButton inspectButton = new JButton("Inspect");
			inspectButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					inspector.setVisible(true);
				}
			});

			JButton logButton = new JButton("Logs");
			logButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), ApplicationFIBLibraryImpl.instance(), null);
				}
			});

			JButton layoutButton = new JButton("Layout");
			layoutButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO: remove invalidate
					drawingController.getDrawing().getRoot().getDefaultLayoutManager().invalidate();
					drawingController.getDrawing().getRoot().getDefaultLayoutManager().doLayout(true);
				}
			});

			JButton randomButton = new JButton("Random");
			randomButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					drawingController.getDrawing().getRoot().getDefaultLayoutManager().invalidate();
					drawingController.getDrawing().getRoot().getDefaultLayoutManager().randomLayout(true);
				}
			});

			JPanel controlPanel = new JPanel(new FlowLayout());
			controlPanel.add(inspectButton);
			controlPanel.add(logButton);
			controlPanel.add(layoutButton);
			controlPanel.add(randomButton);

			add(controlPanel, BorderLayout.SOUTH);

		}

		public TestDrawingController getDrawingController() {
			return drawingController;
		}

		public FGELayoutManager<?, ?> getLayoutManager() {
			return drawingController.getDrawing().getRoot().getDefaultLayoutManager();
		}

	}

	public static LayoutDemoPanel makePanel(final Drawing d) {
		return new LayoutDemoPanel(d);
	}

	public static void showPanel(final Drawing d) {
		final JDialog dialog = new JDialog((Frame) null, false);

		LayoutDemoPanel panel = new LayoutDemoPanel(d);

		dialog.setPreferredSize(new Dimension(550, 600));
		dialog.getContentPane().add(panel);
		dialog.validate();
		dialog.pack();

		inspector = new JFIBInspectorController(null, ResourceLocator.locateResource("LayoutInspectors"),
				ApplicationFIBLibraryImpl.instance(), null);
		inspector.inspectObject(panel.getLayoutManager());

		dialog.setVisible(true);

	}

}
