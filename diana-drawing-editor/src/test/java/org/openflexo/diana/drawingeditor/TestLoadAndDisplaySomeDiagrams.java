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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openflexo.diana.drawingeditor.model.DiagramFactory;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.EditingContext;
import org.openflexo.pamela.factory.EditingContextImpl;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.test.UITest;

//@Ignore
public class TestLoadAndDisplaySomeDiagrams {

	public static EventProcessor eventProcessor;
	public static DiagramFactory FACTORY;
	public static EditingContext EDITING_CONTEXT;

	public static boolean dontDestroyMe = false;

	// private static JTabbedPane tabbedPane;

	private static DiagramEditorApplication application;

	@BeforeClass
	public static void initGUI() {

		application = new DiagramEditorApplication();

		eventProcessor = new EventProcessor();

		try {
			SwingUtilities.invokeAndWait(() -> {
				try {
					EDITING_CONTEXT = new EditingContextImpl();
					((EditingContextImpl) EDITING_CONTEXT).createUndoManager();
					FACTORY = new DiagramFactory(EDITING_CONTEXT);
				} catch (ModelDefinitionException e) {
					e.printStackTrace();
				}
				/*tabbedPane = new JTabbedPane();
				JFrame frame = new JFrame("TestLoadAndDisplaySomeDiagrams");
				frame.setLayout(new BorderLayout());
				frame.setSize(new Dimension(1024, 768));
				frame.setLocationRelativeTo(null);*/
				JButton myButton;
				myButton = new JButton("I take the control");
				myButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dontDestroyMe = true;
					}
				});
				application.getMainPanel().add(myButton, BorderLayout.SOUTH);
				application.showMainPanel();
			});
		} catch (

		InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static DiagramEditor deserializeAndDisplay(File file) {
		System.out.println("Loading " + file.getAbsolutePath() + " exists=" + file.exists());
		final DiagramEditor diagramEditor = DiagramEditor.loadDiagramEditor(file, FACTORY, application);
		System.out.println("Deserialized:");
		System.out.println(FACTORY.stringRepresentation(diagramEditor.getDiagram()));
		try {
			SwingUtilities.invokeAndWait(() -> application.addDiagramEditor(diagramEditor));
		} catch (

		InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		try {
			SwingUtilities.invokeAndWait(() -> {
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return diagramEditor;
	}

	@Test
	@Category(UITest.class)
	public void testBasicExample() {

		deserializeAndDisplay(ResourceLocator.retrieveResourceAsFile(ResourceLocator.locateResource(("DrawingExamples/BasicExample.drw"))));
	}

	/*@Test
	public void testExampleEOModel() {
		deserialize(new FileResource("DrawingExamples/ExampleEOModel.drw"));
	}
	
	@Test
	public void testTestFocusSelection() {
		deserialize(new FileResource("DrawingExamples/TestFocusSelection.drw"));
	}
	
	@Test
	public void testTestInspector() {
		deserialize(new FileResource("DrawingExamples/TestInspector.drw"));
	}
	
	@Test
	public void testExampleEOModel2() {
		deserialize(new FileResource("DrawingExamples/ExampleEOModel2.drw"));
	}
	
	@Test
	public void testTestInspector2() {
		deserialize(new FileResource("DrawingExamples/TestInspector2.drw"));
	}
	
	@Test
	public void testNewPolylinDragging() {
		deserialize(new FileResource("DrawingExamples/NewPolylinDragging.drw"));
	}
	
	@Test
	public void testTestLabel() {
		deserialize(new FileResource("DrawingExamples/TestLabel.drw"));
	}
	
	@Test
	public void testShapeGraphicalRepresentation() {
		deserialize(new FileResource("DrawingExamples/ShapeGraphicalRepresentation.drw"));
	}
	
	@Test
	public void testTestLayout1() {
		deserialize(new FileResource("DrawingExamples/TestLayout1.drw"));
	}
	
	@Test
	public void testShapes() {
		deserialize(new FileResource("DrawingExamples/Shapes.drw"));
	}
	
	@Test
	public void testTestLayout2() {
		deserialize(new FileResource("DrawingExamples/TestLayout2.drw"));
	}
	
	@Test
	public void testTestAdjustableRectPolylinConnector() {
		deserialize(new FileResource("DrawingExamples/TestAdjustableRectPolylinConnector.drw"));
	}
	
	@Test
	public void testTestLayout3() {
		deserialize(new FileResource("DrawingExamples/TestLayout3.drw"));
	}
	
	@Test
	public void testTestConnectorSymbols() {
		deserialize(new FileResource("DrawingExamples/TestConnectorSymbols.drw"));
	}
	
	@Test
	public void testTestOrthogonalLayout() {
		deserialize(new FileResource("DrawingExamples/TestOrthogonalLayout.drw"));
	}
	
	@Test
	public void testTestConnectors() {
		deserialize(new FileResource("DrawingExamples/TestConnectors.drw"));
	}
	
	@Test
	public void testTestConnectors2() {
		deserialize(new FileResource("DrawingExamples/TestConnectors2.drw"));
	}
	
	@Test
	public void testTestRectPolylinConnector() {
		deserialize(new FileResource("DrawingExamples/TestRectPolylinConnector.drw"));
	}
	
	@Test
	public void testTestCurve() {
		deserialize(new FileResource("DrawingExamples/TestCurve.drw"));
	}
	
	@Test
	public void testTestRepaint() {
		deserialize(new FileResource("DrawingExamples/TestRepaint.drw"));
	}
	
	@Test
	public void testTestCurvedConnector() {
		deserialize(new FileResource("DrawingExamples/TestCurvedConnector.drw"));
	}
	
	@Test
	public void testTestShape1() {
		deserialize(new FileResource("DrawingExamples/TestShape1.drw"));
	}
	
	@Test
	public void testTestDrag() {
		deserialize(new FileResource("DrawingExamples/TestDrag.drw"));
	}
	
	@Test
	public void testTestDragMiddleSymbol() {
		deserialize(new FileResource("DrawingExamples/TestDragMiddleSymbol.drw"));
	}
	
	@Test
	public void testVoitureDeLouise() {
		deserialize(new FileResource("DrawingExamples/VoitureDeLouise.drw"));
	}
	*/

	@AfterClass
	public static void waitGUI() {
		if (dontDestroyMe) {
			while (true) {
				try {
					synchronized (TestLoadAndDisplaySomeDiagrams.class) {
						TestLoadAndDisplaySomeDiagrams.class.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void runInEDT(final Runnable runnable) {
		class WrappingRunnable implements Runnable {
			private Throwable exception;

			@Override
			public void run() {
				try {
					runnable.run();
				} catch (Throwable t) {
					this.exception = t;
				}
			}

			public Throwable getException() {
				return exception;
			}
		}
		WrappingRunnable doRun = new WrappingRunnable();
		// 1. Execute the runnable in EDT
		try {
			SwingUtilities.invokeAndWait(doRun);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (doRun.getException() != null) {
			throw new RuntimeException(doRun.getException());
		}

	}

	@Before
	public void setUp() {
		eventProcessor.reset();
	}

	@After
	public void tearDown() throws Throwable {
		if (eventProcessor.getException() != null) {
			throw eventProcessor.getException();
		}
	}

	public static class EventProcessor extends java.awt.EventQueue {

		private Throwable exception = null;

		public EventProcessor() {
			Toolkit.getDefaultToolkit().getSystemEventQueue().push(this);
		}

		public void reset() {
			exception = null;
		}

		@Override
		protected void dispatchEvent(AWTEvent e) {
			try {
				super.dispatchEvent(e);
			} catch (Throwable exception) {
				System.err.println("Caught " + exception);
				this.exception = exception;
			}
		}

		public Throwable getException() {
			return exception;
		}
	}

}
