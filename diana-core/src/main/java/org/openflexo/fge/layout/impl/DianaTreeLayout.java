package org.openflexo.fge.layout.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.Forest;

public class DianaTreeLayout<V, E> extends TreeLayout<V, E> {

	private final double spacingX;
	private final double spacingY;
	private final double borderX;
	private final double borderY;

	public DianaTreeLayout(Forest<V, E> g, double spacingX, double spacingY, double borderX, double borderY) {
		super(g);
		this.spacingX = spacingX;
		this.spacingY = spacingY;
		this.borderX = borderX;
		this.borderY = borderY;
	}

	private Map<Integer, List<V>> verticesByRows;

	@Override
	protected void buildTree() {
		verticesByRows = new HashMap<>();
		super.buildTree();
		/*for (V n : getGraph().getVertices()) {
			int depth = getDepth(n);
			// System.out.println("Base position " + basePositions.get(n) + " depth=" + depth + " for node " + n);
			List<V> l = verticesByRows.get(depth);
			if (l == null) {
				l = new ArrayList<V>();
				verticesByRows.put(depth, l);
			}
			l.add(n);
		}*/
		/*for (Integer i : verticesByRows.keySet()) {
			System.out.println("Row " + i + " " + verticesByRows.get(i));
		}*/
	}

	@Override
	protected void buildTree(V n, int x) {
		super.buildTree(n, x);
		int depth = getDepth(n);
		// System.out.println("Base position " + basePositions.get(n) + " depth=" + depth + " for node " + n);
		List<V> l = verticesByRows.get(depth);
		if (l == null) {
			l = new ArrayList<>();
			verticesByRows.put(depth, l);
		}
		l.add(n);
	}

	protected int getDepth(V node) {
		if (getGraph().getPredecessorCount(node) > 0) {
			int returned = -1;
			List<V> predecessors = new ArrayList<>(getGraph().getPredecessors(node));
			for (int i = 0; i < predecessors.size(); i++) {
				int parentDepth = getDepth(predecessors.get(i));
				if (parentDepth + 1 > returned) {
					returned = parentDepth + 1;
				}
			}
			return returned;
		}
		return 0;
	}

	public Map<Integer, List<V>> getVerticesByRows() {
		return verticesByRows;
	}

	public int getBasePosition(V n) {
		return basePositions.get(n) + distX;
	}

	public double getSpacingX() {
		return spacingX;
	}

	public double getSpacingY() {
		return spacingY;
	}

	public double getBorderX() {
		return borderX;
	}

	public double getBorderY() {
		return borderY;
	}
}
