/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Fml-technologyadapter-ui, a component of the software infrastructure 
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

package org.openflexo.diana.layout;

import java.awt.Component;
import java.util.logging.Level;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.diana.FGEModelFactory;
import org.openflexo.diana.FGEModelFactoryImpl;
import org.openflexo.diana.test.TestGraph;
import org.openflexo.diana.test.layout.BalloonLayoutManagerDrawing;
import org.openflexo.diana.test.layout.ForceDirectedGraphLayoutManagerDrawing;
import org.openflexo.diana.test.layout.GridLayoutManagerDrawing;
import org.openflexo.diana.test.layout.ISOMGraphLayoutManagerDrawing;
import org.openflexo.diana.test.layout.RadialTreeLayoutManagerDrawing;
import org.openflexo.diana.test.layout.TreeLayoutManagerDrawing;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.swing.utils.JFIBDialogInspectorController;
import org.openflexo.gina.test.SwingGraphicalContextDelegate;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test FlexoConceptPanel fib
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestAllLayouts extends AbstractLaunchLayoutManagerExample {

	private static SwingGraphicalContextDelegate gcDelegate;

	private static JFIBDialogInspectorController inspector;

	static FGEModelFactory factory = null;

	@BeforeClass
	public static void setupClass() {
		// instanciateTestServiceManager();
		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		try {
			factory = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}

		inspector = new JFIBDialogInspectorController(null, ResourceLocator.locateResource("LayoutInspectors"),
				ApplicationFIBLibraryImpl.instance(), null);

		initGUI();
	}

	/*public static TestGraph makeTestGraph() {
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
					FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), gcDelegate.getFrame());
				}
			});
	
			JButton layoutButton = new JButton("Layout");
			layoutButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Layout");
					drawingController.getDrawing().getRoot().getDefaultLayoutManager().doLayout(true);
				}
			});
	
			JPanel controlPanel = new JPanel(new FlowLayout());
			controlPanel.add(inspectButton);
			controlPanel.add(logButton);
			controlPanel.add(layoutButton);
	
			add(controlPanel, BorderLayout.SOUTH);
	
		}
	
		public TestDrawingController getDrawingController() {
			return drawingController;
		}
	
		public FGELayoutManagerSpecification<?> getLayoutManagerSpecification() {
			return drawingController.getDrawing().getRoot().getDefaultLayoutManager().getLayoutManagerSpecification();
		}
	
	}
	
	public static LayoutDemoPanel makePanel(final Drawing d) {
		return new LayoutDemoPanel(d);
	}*/

	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(TestAllLayouts.class.getSimpleName()) {

			@Override
			public void selectedTab(int index, Component selectedComponent) {
				super.selectedTab(index, selectedComponent);
				LayoutDemoPanel demoPanel = (LayoutDemoPanel) selectedComponent;
				inspector.inspectObject(demoPanel.getLayoutManager());
			}

		};
	}

	@AfterClass
	public static void waitGUI() {
		gcDelegate.waitGUI();
	}

	@Before
	public void setUp() {
		gcDelegate.setUp();
	}

	@After
	public void tearDown() throws Exception {
		gcDelegate.tearDown();
	}

	@Test
	@TestOrder(1)
	public void testGridLayoutManager() {

		TestGraph graph = makeTestGraph();
		GridLayoutManagerDrawing returned = new GridLayoutManagerDrawing(graph, factory);

		gcDelegate.addTab("GridLayoutManager", makePanel(returned));
	}

	@Test
	@TestOrder(2)
	public void testForceDirectedGraphLayoutManager() {

		TestGraph graph = makeTestGraph();
		ForceDirectedGraphLayoutManagerDrawing returned = new ForceDirectedGraphLayoutManagerDrawing(graph, factory);

		gcDelegate.addTab("ForceDirectedGraphLayoutManager", makePanel(returned));
	}

	@Test
	@TestOrder(3)
	public void testISOMGraphLayoutManager() {

		TestGraph graph = makeTestGraph();
		ISOMGraphLayoutManagerDrawing returned = new ISOMGraphLayoutManagerDrawing(graph, factory);

		gcDelegate.addTab("ISOMGraphLayoutManager", makePanel(returned));
	}

	@Test
	@TestOrder(4)
	public void testTreeLayoutManager() {

		TestGraph graph = makeTestGraph();
		TreeLayoutManagerDrawing returned = new TreeLayoutManagerDrawing(graph, factory);

		gcDelegate.addTab("TreeLayoutManager", makePanel(returned));
	}

	@Test
	@TestOrder(5)
	public void testBalloonLayoutManager() {

		TestGraph graph = makeTestGraph();
		BalloonLayoutManagerDrawing returned = new BalloonLayoutManagerDrawing(graph, factory);

		gcDelegate.addTab("BalloonLayoutManager", makePanel(returned));
	}

	@Test
	@TestOrder(6)
	public void testRadialTreeLayoutManager() {

		TestGraph graph = makeTestGraph();
		RadialTreeLayoutManagerDrawing returned = new RadialTreeLayoutManagerDrawing(graph, factory);

		gcDelegate.addTab("RadialTreeLayoutManager", makePanel(returned));
	}

}
