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

import java.awt.Color;
import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openflexo.diana.drawingeditor.model.Connector;
import org.openflexo.diana.drawingeditor.model.Diagram;
import org.openflexo.diana.drawingeditor.model.DiagramFactory;
import org.openflexo.diana.drawingeditor.model.Shape;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.pamela.factory.Clipboard;
import org.openflexo.pamela.factory.EditingContextImpl;

import junit.framework.TestCase;

/**
 * This test is actually testing PAMELA copy/paste features applied to Diana model
 * 
 * @author sylvain
 * 
 */
public class TestCopyPaste extends TestCase {

	private DiagramFactory factory;

	// private ModelContext modelContext;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Override
	@Before
	public void setUp() throws Exception {
		new File("/tmp").mkdirs();
		// modelContext = new ModelContext(FlexoProcess.class);
		EditingContextImpl editingContext = new EditingContextImpl();
		editingContext.createUndoManager();
		factory = new DiagramFactory(editingContext);
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	public void testCopyPaste() throws Exception {

		Diagram diagram = factory.newInstance(Diagram.class);
		assertTrue(diagram != null);

		Shape shape1 = factory.makeNewShape(ShapeType.RECTANGLE, new DianaPoint(100, 100), diagram);
		assertTrue(shape1 != null);
		shape1.getGraphicalRepresentation().setForeground(factory.makeForegroundStyle(Color.RED));
		shape1.getGraphicalRepresentation().setBackground(factory.makeColoredBackground(Color.BLUE));

		Shape shape2 = factory.makeNewShape(ShapeType.RECTANGLE, new DianaPoint(200, 100), diagram);
		assertTrue(shape2 != null);
		shape2.getGraphicalRepresentation().setForeground(factory.makeForegroundStyle(Color.BLUE));
		shape2.getGraphicalRepresentation().setBackground(factory.makeColoredBackground(Color.WHITE));

		Connector connector1 = factory.makeNewConnector(shape1, shape2, diagram);
		assertTrue(connector1 != null);

		diagram.addToShapes(shape1);
		diagram.addToShapes(shape2);
		diagram.addToConnectors(connector1);

		Clipboard clipboard = factory.copy(shape1, shape2, connector1);
		assertFalse(clipboard.isSingleObject());
		assertEquals(3, clipboard.getMultipleContents().size());
		Shape shape3 = (Shape) clipboard.getMultipleContents().get(0);
		Shape shape4 = (Shape) clipboard.getMultipleContents().get(1);
		Connector connector2 = (Connector) clipboard.getMultipleContents().get(2);

		assertNotSame(shape1, shape3);
		assertNotSame(shape2, shape4);
		assertNotSame(connector1, connector2);

		assertFalse(shape1.equals(shape3));
		assertFalse(shape2.equals(shape4));
		assertFalse(connector1.equals(connector2));

		assertEquals(connector2.getStartShape(), shape3);
		assertEquals(connector2.getEndShape(), shape4);

		Object pasted = factory.paste(clipboard, diagram);

		assertEquals(4, diagram.getShapes().size());
		assertEquals(2, diagram.getConnectors().size());

		assertTrue(diagram.getShapes().contains(shape1));
		assertTrue(diagram.getShapes().contains(shape2));
		assertTrue(diagram.getShapes().contains(shape3));
		assertTrue(diagram.getShapes().contains(shape4));
		assertTrue(diagram.getConnectors().contains(connector1));
		assertTrue(diagram.getConnectors().contains(connector2));

		assertEquals(((List<?>) pasted).get(0), shape3);
		assertEquals(((List<?>) pasted).get(1), shape4);
		assertEquals(((List<?>) pasted).get(2), connector2);

		assertEquals(connector1.getStartShape(), shape1);
		assertEquals(connector1.getEndShape(), shape2);

		assertEquals(connector2.getStartShape(), shape3);
		assertEquals(connector2.getEndShape(), shape4);

		assertFalse(clipboard.isSingleObject());
		assertEquals(3, clipboard.getMultipleContents().size());
		Shape shape5 = (Shape) clipboard.getMultipleContents().get(0);
		Shape shape6 = (Shape) clipboard.getMultipleContents().get(1);
		Connector connector3 = (Connector) clipboard.getMultipleContents().get(2);

		assertNotSame(shape3, shape5);
		assertNotSame(shape4, shape6);
		assertNotSame(connector2, connector3);

		assertFalse(shape3.equals(shape5));
		assertFalse(shape4.equals(shape6));
		assertFalse(connector2.equals(connector3));

		assertEquals(connector3.getStartShape(), shape5);
		assertEquals(connector3.getEndShape(), shape6);

		assertNotSame(shape1.getGraphicalRepresentation().getForeground(), shape3.getGraphicalRepresentation().getForeground());
		assertEquals(shape1.getGraphicalRepresentation().getForeground(), shape1.getGraphicalRepresentation().getForeground());
		assertNotSame(shape1.getGraphicalRepresentation().getSelectedForeground(),
				shape3.getGraphicalRepresentation().getSelectedForeground());
		assertEquals(shape1.getGraphicalRepresentation().getSelectedForeground(),
				shape1.getGraphicalRepresentation().getSelectedForeground());
		assertNotSame(shape1.getGraphicalRepresentation().getFocusedForeground(),
				shape3.getGraphicalRepresentation().getFocusedForeground());
		assertEquals(shape1.getGraphicalRepresentation().getFocusedForeground(),
				shape1.getGraphicalRepresentation().getFocusedForeground());

		assertNotSame(shape1.getGraphicalRepresentation().getBackground(), shape3.getGraphicalRepresentation().getBackground());
		assertEquals(shape1.getGraphicalRepresentation().getBackground(), shape1.getGraphicalRepresentation().getBackground());
		assertNotSame(shape1.getGraphicalRepresentation().getSelectedBackground(),
				shape3.getGraphicalRepresentation().getSelectedBackground());
		assertEquals(shape1.getGraphicalRepresentation().getSelectedBackground(),
				shape1.getGraphicalRepresentation().getSelectedBackground());
		assertNotSame(shape1.getGraphicalRepresentation().getFocusedBackground(),
				shape3.getGraphicalRepresentation().getFocusedBackground());
		assertEquals(shape1.getGraphicalRepresentation().getFocusedBackground(),
				shape1.getGraphicalRepresentation().getFocusedBackground());

	}

}
