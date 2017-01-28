/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.fge.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class TestGraphDrawing {

	private static FGEModelFactory FACTORY;
	private static TestGraph graph;
	private static TestGraphNode node1, node2, node3, node4;
	private static GraphDrawing1 graphDrawing1;
	private static GraphDrawing2 graphDrawing2;
	private static GraphDrawing3 graphDrawing3;

	@BeforeClass
	public static void beforeClass() throws ModelDefinitionException {
		FACTORY = new FGEModelFactoryImpl();
	}

	@AfterClass
	public static void afterClass() {
		// FACTORY.delete();
	}

	@Before
	public void before() {
	}

	@After
	public void after() {
	}

	@Test
	@TestOrder(1)
	public void test1InitGraph() {
		graph = new TestGraph();
		node1 = new TestGraphNode("node1", graph);
		node2 = new TestGraphNode("node2", graph);
		node3 = new TestGraphNode("node3", graph);
		node1.connectTo(node2);
		node1.connectTo(node3);
		node3.connectTo(node2);
		assertEquals(0, node1.getInputEdges().size());
		assertEquals(2, node1.getOutputEdges().size());
		assertEquals(2, node2.getInputEdges().size());
		assertEquals(0, node2.getOutputEdges().size());
		assertEquals(1, node3.getInputEdges().size());
		assertEquals(1, node3.getOutputEdges().size());
	}

	@Test
	@TestOrder(2)
	public void test2InitGraphDrawing1() {
		System.out.println("INIT graph drawing 1 ********************** ");
		graphDrawing1 = new GraphDrawing1(graph, FACTORY);
		assertNotNull(graphDrawing1.getRoot());
		System.out.println("Root = " + graphDrawing1.getRoot());
		System.out.println("GR = " + graphDrawing1.getRoot().getGraphicalRepresentation());
	}

	@Test
	@TestOrder(3)
	public void test3InitGraphicalObjectHierarchyForGraphDrawing1() {
		ShapeNode<TestGraphNode> graphNode1 = (ShapeNode<TestGraphNode>) graphDrawing1.getRoot().getChildNodes().get(0);
		assertNotNull(graphNode1);
		assertEquals(node1, graphNode1.getDrawable());
		ShapeNode<TestGraphNode> graphNode2 = (ShapeNode<TestGraphNode>) graphDrawing1.getRoot().getChildNodes().get(1);
		assertNotNull(graphNode2);
		assertEquals(node2, graphNode2.getDrawable());
		ShapeNode<TestGraphNode> graphNode3 = (ShapeNode<TestGraphNode>) graphDrawing1.getRoot().getChildNodes().get(2);
		assertNotNull(graphNode3);
		assertEquals(node3, graphNode3.getDrawable());
		ConnectorNode<TestEdge> connectorNode1 = (ConnectorNode<TestEdge>) graphDrawing1.getRoot().getChildNodes().get(3);
		assertNotNull(connectorNode1);
		assertEquals(node1.getOutputEdges().get(0), connectorNode1.getDrawable());
		assertEquals(node2.getInputEdges().get(0), connectorNode1.getDrawable());
		ConnectorNode<TestEdge> connectorNode2 = (ConnectorNode<TestEdge>) graphDrawing1.getRoot().getChildNodes().get(4);
		assertNotNull(connectorNode2);
		assertEquals(node1.getOutputEdges().get(1), connectorNode2.getDrawable());
		assertEquals(node3.getInputEdges().get(0), connectorNode2.getDrawable());
		ConnectorNode<TestEdge> connectorNode3 = (ConnectorNode<TestEdge>) graphDrawing1.getRoot().getChildNodes().get(5);
		assertNotNull(connectorNode3);
		assertEquals(node3.getOutputEdges().get(0), connectorNode3.getDrawable());
		assertEquals(node2.getInputEdges().get(1), connectorNode3.getDrawable());

	}

	@Test
	@TestOrder(4)
	public void test4UpdateGraphicalObjectHierarchyForGraphDrawing1() {
		ShapeNode<TestGraphNode> graphNode1 = (ShapeNode<TestGraphNode>) graphDrawing1.getRoot().getChildNodes().get(0);
		ShapeNode<TestGraphNode> graphNode2 = (ShapeNode<TestGraphNode>) graphDrawing1.getRoot().getChildNodes().get(1);
		ShapeNode<TestGraphNode> graphNode3 = (ShapeNode<TestGraphNode>) graphDrawing1.getRoot().getChildNodes().get(2);
		ConnectorNode<TestEdge> connectorNode1 = (ConnectorNode<TestEdge>) graphDrawing1.getRoot().getChildNodes().get(3);
		ConnectorNode<TestEdge> connectorNode2 = (ConnectorNode<TestEdge>) graphDrawing1.getRoot().getChildNodes().get(4);
		ConnectorNode<TestEdge> connectorNode3 = (ConnectorNode<TestEdge>) graphDrawing1.getRoot().getChildNodes().get(5);
		graphDrawing1.updateGraphicalObjectsHierarchy();
		ShapeNode<TestGraphNode> graphNode1bis = (ShapeNode<TestGraphNode>) graphDrawing1.getRoot().getChildNodes().get(0);
		ShapeNode<TestGraphNode> graphNode2bis = (ShapeNode<TestGraphNode>) graphDrawing1.getRoot().getChildNodes().get(1);
		ShapeNode<TestGraphNode> graphNode3bis = (ShapeNode<TestGraphNode>) graphDrawing1.getRoot().getChildNodes().get(2);
		ConnectorNode<TestEdge> connectorNode1bis = (ConnectorNode<TestEdge>) graphDrawing1.getRoot().getChildNodes().get(3);
		ConnectorNode<TestEdge> connectorNode2bis = (ConnectorNode<TestEdge>) graphDrawing1.getRoot().getChildNodes().get(4);
		ConnectorNode<TestEdge> connectorNode3bis = (ConnectorNode<TestEdge>) graphDrawing1.getRoot().getChildNodes().get(5);
		assertEquals(graphNode1, graphNode1bis);
		assertEquals(graphNode2, graphNode2bis);
		assertEquals(graphNode3, graphNode3bis);
		assertEquals(connectorNode1, connectorNode1bis);
		assertEquals(connectorNode2, connectorNode2bis);
		assertEquals(connectorNode3, connectorNode3bis);
	}

	@Test
	@TestOrder(5)
	public void test5InitGraphDrawing2() {
		System.out.println("INIT graph drawing 2 ********************** ");
		graphDrawing2 = new GraphDrawing2(graph, FACTORY);
		assertNotNull(graphDrawing2.getRoot());
		System.out.println("Root = " + graphDrawing2.getRoot());
		System.out.println("GR = " + graphDrawing2.getRoot().getGraphicalRepresentation());
	}

	@Test
	@TestOrder(6)
	public void test6InitGraphicalObjectHierarchyForGraphDrawing2() {
		ShapeNode<TestGraphNode> graphNode1 = (ShapeNode<TestGraphNode>) graphDrawing2.getRoot().getChildNodes().get(0);
		assertNotNull(graphNode1);
		assertEquals(node1, graphNode1.getDrawable());
		ShapeNode<TestGraphNode> graphNode2 = (ShapeNode<TestGraphNode>) graphDrawing2.getRoot().getChildNodes().get(1);
		assertNotNull(graphNode2);
		assertEquals(node2, graphNode2.getDrawable());
		ShapeNode<TestGraphNode> graphNode3 = (ShapeNode<TestGraphNode>) graphDrawing2.getRoot().getChildNodes().get(2);
		assertNotNull(graphNode3);
		assertEquals(node3, graphNode3.getDrawable());
		ConnectorNode<TestEdge> connectorNode1 = (ConnectorNode<TestEdge>) graphDrawing2.getRoot().getChildNodes().get(3);
		assertNotNull(connectorNode1);
		assertEquals(node1.getOutputEdges().get(0), connectorNode1.getDrawable());
		assertEquals(node2.getInputEdges().get(0), connectorNode1.getDrawable());
		ConnectorNode<TestEdge> connectorNode2 = (ConnectorNode<TestEdge>) graphDrawing2.getRoot().getChildNodes().get(4);
		assertNotNull(connectorNode2);
		assertEquals(node1.getOutputEdges().get(1), connectorNode2.getDrawable());
		assertEquals(node3.getInputEdges().get(0), connectorNode2.getDrawable());
		ConnectorNode<TestEdge> connectorNode3 = (ConnectorNode<TestEdge>) graphDrawing2.getRoot().getChildNodes().get(5);
		assertNotNull(connectorNode3);
		assertEquals(node3.getOutputEdges().get(0), connectorNode3.getDrawable());
		assertEquals(node2.getInputEdges().get(1), connectorNode3.getDrawable());

	}

	@Test
	@TestOrder(7)
	public void test7UpdateGraphicalObjectHierarchyForGraphDrawing2() {
		ShapeNode<TestGraphNode> graphNode1 = (ShapeNode<TestGraphNode>) graphDrawing2.getRoot().getChildNodes().get(0);
		ShapeNode<TestGraphNode> graphNode2 = (ShapeNode<TestGraphNode>) graphDrawing2.getRoot().getChildNodes().get(1);
		ShapeNode<TestGraphNode> graphNode3 = (ShapeNode<TestGraphNode>) graphDrawing2.getRoot().getChildNodes().get(2);
		ConnectorNode<TestEdge> connectorNode1 = (ConnectorNode<TestEdge>) graphDrawing2.getRoot().getChildNodes().get(3);
		ConnectorNode<TestEdge> connectorNode2 = (ConnectorNode<TestEdge>) graphDrawing2.getRoot().getChildNodes().get(4);
		ConnectorNode<TestEdge> connectorNode3 = (ConnectorNode<TestEdge>) graphDrawing2.getRoot().getChildNodes().get(5);
		graphDrawing2.updateGraphicalObjectsHierarchy();
		ShapeNode<TestGraphNode> graphNode1bis = (ShapeNode<TestGraphNode>) graphDrawing2.getRoot().getChildNodes().get(0);
		ShapeNode<TestGraphNode> graphNode2bis = (ShapeNode<TestGraphNode>) graphDrawing2.getRoot().getChildNodes().get(1);
		ShapeNode<TestGraphNode> graphNode3bis = (ShapeNode<TestGraphNode>) graphDrawing2.getRoot().getChildNodes().get(2);
		ConnectorNode<TestEdge> connectorNode1bis = (ConnectorNode<TestEdge>) graphDrawing2.getRoot().getChildNodes().get(3);
		ConnectorNode<TestEdge> connectorNode2bis = (ConnectorNode<TestEdge>) graphDrawing2.getRoot().getChildNodes().get(4);
		ConnectorNode<TestEdge> connectorNode3bis = (ConnectorNode<TestEdge>) graphDrawing2.getRoot().getChildNodes().get(5);
		assertEquals(graphNode1, graphNode1bis);
		assertEquals(graphNode2, graphNode2bis);
		assertEquals(graphNode3, graphNode3bis);
		assertEquals(connectorNode1, connectorNode1bis);
		assertEquals(connectorNode2, connectorNode2bis);
		assertEquals(connectorNode3, connectorNode3bis);
	}

	@Test
	@TestOrder(8)
	public void test8InitGraphDrawing3() {
		System.out.println("INIT graph drawing 3 ********************** ");
		graphDrawing3 = new GraphDrawing3(graph, FACTORY);
		assertNotNull(graphDrawing3.getRoot());
		System.out.println("Root = " + graphDrawing3.getRoot());
		System.out.println("GR = " + graphDrawing3.getRoot().getGraphicalRepresentation());
	}

	@Test
	@TestOrder(9)
	public void test9InitGraphicalObjectHierarchyForGraphDrawing3() {
		System.out.println("all nodes = " + graphDrawing3.getRoot().getChildNodes());
		ShapeNode<TestGraphNode> graphNode1 = (ShapeNode<TestGraphNode>) graphDrawing3.getRoot().getChildNodes().get(0);
		assertNotNull(graphNode1);
		assertEquals(node1, graphNode1.getDrawable());
		ShapeNode<TestGraphNode> graphNode2 = (ShapeNode<TestGraphNode>) graphDrawing3.getRoot().getChildNodes().get(1);
		assertNotNull(graphNode2);
		assertEquals(node2, graphNode2.getDrawable());
		ShapeNode<TestGraphNode> graphNode3 = (ShapeNode<TestGraphNode>) graphDrawing3.getRoot().getChildNodes().get(2);
		assertNotNull(graphNode3);
		assertEquals(node3, graphNode3.getDrawable());
		ConnectorNode<TestEdge> connectorNode1 = (ConnectorNode<TestEdge>) graphDrawing3.getRoot().getChildNodes().get(3);
		assertNotNull(connectorNode1);
		assertEquals(node1.getOutputEdges().get(0), connectorNode1.getDrawable());
		assertEquals(node2.getInputEdges().get(0), connectorNode1.getDrawable());
		ConnectorNode<TestEdge> connectorNode2 = (ConnectorNode<TestEdge>) graphDrawing3.getRoot().getChildNodes().get(4);
		assertNotNull(connectorNode2);
		assertEquals(node1.getOutputEdges().get(1), connectorNode2.getDrawable());
		assertEquals(node3.getInputEdges().get(0), connectorNode2.getDrawable());
		ConnectorNode<TestEdge> connectorNode3 = (ConnectorNode<TestEdge>) graphDrawing3.getRoot().getChildNodes().get(5);
		assertNotNull(connectorNode3);
		assertEquals(node3.getOutputEdges().get(0), connectorNode3.getDrawable());
		assertEquals(node2.getInputEdges().get(1), connectorNode3.getDrawable());
	}

	@Test
	@TestOrder(10)
	public void test10TUpdateGraphicalObjectHierarchyForGraphDrawing3() {
		ShapeNode<TestGraphNode> graphNode1 = (ShapeNode<TestGraphNode>) graphDrawing3.getRoot().getChildNodes().get(0);
		ShapeNode<TestGraphNode> graphNode2 = (ShapeNode<TestGraphNode>) graphDrawing3.getRoot().getChildNodes().get(1);
		ShapeNode<TestGraphNode> graphNode3 = (ShapeNode<TestGraphNode>) graphDrawing3.getRoot().getChildNodes().get(2);
		ConnectorNode<TestEdge> connectorNode1 = (ConnectorNode<TestEdge>) graphDrawing3.getRoot().getChildNodes().get(3);
		ConnectorNode<TestEdge> connectorNode2 = (ConnectorNode<TestEdge>) graphDrawing3.getRoot().getChildNodes().get(4);
		ConnectorNode<TestEdge> connectorNode3 = (ConnectorNode<TestEdge>) graphDrawing3.getRoot().getChildNodes().get(5);
		graphDrawing3.updateGraphicalObjectsHierarchy();
		ShapeNode<TestGraphNode> graphNode1bis = (ShapeNode<TestGraphNode>) graphDrawing3.getRoot().getChildNodes().get(0);
		ShapeNode<TestGraphNode> graphNode2bis = (ShapeNode<TestGraphNode>) graphDrawing3.getRoot().getChildNodes().get(1);
		ShapeNode<TestGraphNode> graphNode3bis = (ShapeNode<TestGraphNode>) graphDrawing3.getRoot().getChildNodes().get(2);
		ConnectorNode<TestEdge> connectorNode1bis = (ConnectorNode<TestEdge>) graphDrawing3.getRoot().getChildNodes().get(3);
		ConnectorNode<TestEdge> connectorNode2bis = (ConnectorNode<TestEdge>) graphDrawing3.getRoot().getChildNodes().get(4);
		ConnectorNode<TestEdge> connectorNode3bis = (ConnectorNode<TestEdge>) graphDrawing3.getRoot().getChildNodes().get(5);
		assertEquals(graphNode1, graphNode1bis);
		assertEquals(graphNode2, graphNode2bis);
		assertEquals(graphNode3, graphNode3bis);
		assertEquals(connectorNode1, connectorNode1bis);
		assertEquals(connectorNode2, connectorNode2bis);
		assertEquals(connectorNode3, connectorNode3bis);
	}

}
