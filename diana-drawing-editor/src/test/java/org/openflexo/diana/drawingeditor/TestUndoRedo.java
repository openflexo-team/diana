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

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.diana.drawingeditor.model.Connector;
import org.openflexo.diana.drawingeditor.model.Diagram;
import org.openflexo.diana.drawingeditor.model.DiagramFactory;
import org.openflexo.diana.drawingeditor.model.Shape;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.factory.EditingContextImpl;
import org.openflexo.model.undo.CompoundEdit;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

import junit.framework.TestCase;

/**
 * This test is actually testing PAMELA undo/redo features applied to Diana model
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestUndoRedo extends TestCase {

	private static DiagramFactory factory;

	private static Diagram diagram;

	private static CompoundEdit initDiagram;
	private static CompoundEdit addFirstShape;
	private static CompoundEdit addSecondShape;
	private static CompoundEdit addConnector;

	// private ModelContext modelContext;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new File("/tmp").mkdirs();
		EditingContextImpl editingContext = new EditingContextImpl();
		editingContext.createUndoManager();
		factory = new DiagramFactory(editingContext);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Override
	@Before
	public void setUp() throws Exception {
		/*if (factory == null) {
			factory = new DiagramFactory();
		}*/
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	@Test
	@TestOrder(1)
	public void test1Do() throws Exception {

		initDiagram = factory.getUndoManager().startRecording("Initialize new Diagram");
		diagram = factory.newInstance(Diagram.class);
		factory.getUndoManager().stopRecording(initDiagram);

		assertTrue(diagram != null);
		assertFalse(factory.getUndoManager().isBeeingRecording());
		System.out.println("PERFORMED: " + initDiagram.getPresentationName());
		System.out.println("edits nb=" + initDiagram.getEdits().size());
		assertEquals(1, initDiagram.getEdits().size());

		addFirstShape = factory.getUndoManager().startRecording("Create first shape");
		Shape shape1 = factory.makeNewShape(ShapeType.RECTANGLE, new DianaPoint(100, 100), diagram);
		diagram.addToShapes(shape1);
		factory.getUndoManager().stopRecording(addFirstShape);

		assertTrue(shape1 != null);
		assertFalse(factory.getUndoManager().isBeeingRecording());
		System.out.println("PERFORMED: " + addFirstShape.getPresentationName());
		System.out.println("edits nb=" + addFirstShape.getEdits().size());
		assertEquals(85, addFirstShape.getEdits().size());

		addSecondShape = factory.getUndoManager().startRecording("Create second shape");
		Shape shape2 = factory.makeNewShape(ShapeType.RECTANGLE, new DianaPoint(200, 100), diagram);
		diagram.addToShapes(shape2);
		factory.getUndoManager().stopRecording(addSecondShape);

		assertTrue(shape2 != null);
		assertFalse(factory.getUndoManager().isBeeingRecording());
		System.out.println("PERFORMED: " + addSecondShape.getPresentationName());
		System.out.println("edits nb=" + addSecondShape.getEdits().size());
		assertEquals(85, addSecondShape.getEdits().size());

		addConnector = factory.getUndoManager().startRecording("Add connector");
		Connector connector1 = factory.makeNewConnector(shape1, shape2, diagram);
		diagram.addToConnectors(connector1);
		factory.getUndoManager().stopRecording(addConnector);

		assertTrue(connector1 != null);
		assertFalse(factory.getUndoManager().isBeeingRecording());
		System.out.println("PERFORMED: " + addConnector.getPresentationName());
		System.out.println("edits nb=" + addConnector.getEdits().size());
		assertEquals(19, addConnector.getEdits().size());

	}

	// UNDO create connector
	@Test
	@TestOrder(2)
	public void test2Undo1() throws Exception {

		assertEquals(2, diagram.getShapes().size());
		assertEquals(1, diagram.getConnectors().size());
		Shape shape1 = diagram.getShapes().get(0);
		Shape shape2 = diagram.getShapes().get(1);
		Connector connector = diagram.getConnectors().get(0);

		assertNotNull(shape1);
		assertNotNull(shape2);
		assertNotNull(connector);

		assertTrue(factory.getUndoManager().canUndo());
		assertEquals(addConnector, factory.getUndoManager().editToBeUndone());

		factory.getUndoManager().undo();

		assertEquals(2, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		assertTrue(connector.isDeleted());
	}

	// UNDO create shape2
	@Test
	@TestOrder(3)
	public void test3Undo2() throws Exception {

		assertEquals(2, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		Shape shape1 = diagram.getShapes().get(0);
		Shape shape2 = diagram.getShapes().get(1);

		assertNotNull(shape1);
		assertNotNull(shape2);

		assertTrue(factory.getUndoManager().canUndo());
		assertEquals(addSecondShape, factory.getUndoManager().editToBeUndone());

		factory.getUndoManager().undo();

		assertEquals(1, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		assertTrue(shape2.isDeleted());
	}

	// UNDO create shape1
	@Test
	@TestOrder(4)
	public void test4Undo3() throws Exception {

		assertEquals(1, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		Shape shape1 = diagram.getShapes().get(0);

		assertNotNull(shape1);

		assertTrue(factory.getUndoManager().canUndo());
		assertEquals(addFirstShape, factory.getUndoManager().editToBeUndone());

		factory.getUndoManager().undo();

		assertEquals(0, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		assertTrue(shape1.isDeleted());
	}

	// REDO create shape1
	@Test
	@TestOrder(5)
	public void test5Redo1() throws Exception {

		assertEquals(0, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());

		assertTrue(factory.getUndoManager().canRedo());
		assertEquals(addFirstShape, factory.getUndoManager().editToBeRedone());

		factory.getUndoManager().redo();

		assertEquals(1, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		Shape shape1 = diagram.getShapes().get(0);

		assertNotNull(shape1);
		assertFalse(shape1.isDeleted());

	}

	// REDO create shape2
	@Test
	@TestOrder(6)
	public void test6Redo2() throws Exception {

		assertEquals(1, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		Shape shape1 = diagram.getShapes().get(0);

		assertNotNull(shape1);
		assertFalse(shape1.isDeleted());

		assertTrue(factory.getUndoManager().canRedo());
		assertEquals(addSecondShape, factory.getUndoManager().editToBeRedone());

		factory.getUndoManager().redo();

		assertEquals(2, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		Shape shape2 = diagram.getShapes().get(1);

		assertNotNull(shape2);
		assertFalse(shape2.isDeleted());

	}

	// REDO create connector
	@Test
	@TestOrder(7)
	public void test7Redo3() throws Exception {

		assertEquals(2, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		Shape shape1 = diagram.getShapes().get(0);
		Shape shape2 = diagram.getShapes().get(1);

		assertNotNull(shape1);
		assertNotNull(shape2);
		assertFalse(shape1.isDeleted());
		assertFalse(shape2.isDeleted());

		assertTrue(factory.getUndoManager().canRedo());
		assertEquals(addConnector, factory.getUndoManager().editToBeRedone());

		factory.getUndoManager().redo();

		assertEquals(2, diagram.getShapes().size());
		assertEquals(1, diagram.getConnectors().size());
		Connector connector = diagram.getConnectors().get(0);

		assertNotNull(connector);
		assertFalse(connector.isDeleted());

	}

}
