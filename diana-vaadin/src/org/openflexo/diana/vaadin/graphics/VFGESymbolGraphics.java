package org.openflexo.diana.vaadin.graphics;

import java.awt.Point;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.FGESymbolGraphics;
import org.openflexo.fge.view.FGEView;

public class VFGESymbolGraphics extends VFGEGraphics implements FGESymbolGraphics{

	private static final Logger logger = Logger.getLogger(VFGESymbolGraphics.class.getPackage().getName());
	
	public VFGESymbolGraphics(DrawingTreeNode<?, ?> dtn, FGEView<?, ?> view) {
		super(dtn, view);
		// TODO Auto-generated constructor stub
	}
	
	// Symbol graphics doesn't used normalized coordinates system
	@Override
	public Point convertNormalizedPointToViewCoordinates(double x, double y) {
		return new Point((int) x, (int) y);
	}

	// Symbol graphics doesn't used normalized coordinates system
	@Override
	public FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y) {
		return new FGEPoint(x, y);
	}

}
