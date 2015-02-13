package org.openflexo.fge.jung;

import java.awt.Dimension;
import java.util.List;

import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class TestJung {
	public static void main(String[] args) {
		Graph<MyNode, MyLink> g = new DirectedSparseMultigraph<MyNode, MyLink>();
		// Create some MyNode objects to use as vertices
		MyNode n1 = new MyNode(1);
		MyNode n2 = new MyNode(2);
		MyNode n3 = new MyNode(3);
		MyNode n4 = new MyNode(4);
		MyNode n5 = new MyNode(5); // note n1-n5 declared elsewhere.
		// Add some directed edges along with the vertices to the graph
		g.addEdge(new MyLink(2.0, 48), n1, n2, EdgeType.DIRECTED); // This method
		g.addEdge(new MyLink(2.0, 48), n2, n3, EdgeType.DIRECTED);
		g.addEdge(new MyLink(3.0, 192), n3, n5, EdgeType.DIRECTED);
		g.addEdge(new MyLink(2.0, 48), n5, n4, EdgeType.DIRECTED); // or we can use
		g.addEdge(new MyLink(2.0, 48), n4, n2); // In a directed graph the
		g.addEdge(new MyLink(2.0, 48), n3, n1); // first node is the source
		g.addEdge(new MyLink(10.0, 48), n2, n5);// and the second the destination

		System.out.println(g.toString());

		DijkstraShortestPath<MyNode, MyLink> alg = new DijkstraShortestPath(g);
		List<MyLink> l = alg.getPath(n1, n4);
		System.out.println("The shortest unweighted path from " + n1 + " to " + n4 + " is:");
		System.out.println(l.toString());

		SpringLayout<MyNode, MyLink> layout = new SpringLayout<MyNode, MyLink>(g);
		layout.setSize(new Dimension(100, 100));

		printNodes(layout);

		for (int i = 0; i < 20; i++) {
			layout.step();
			printNodes(layout);
		}

	}

	private static void printNodes(SpringLayout<MyNode, MyLink> layout) {
		System.out.println("Iteration-------->");
		for (MyNode n : layout.getGraph().getVertices()) {
			System.out.println("Node:" + n + " x=" + layout.getX(n) + " y=" + layout.getY(n));
		}

	}
}
