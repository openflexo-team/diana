package org.openflexo.checkers;

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.area.FGEFiniteGrid;

public class FGECheckersBoard extends FGEFiniteGrid {

	public FGECheckersBoard(FGEPoint origin, double hStep, double vStep, FGERectangle bounds) {
		super(origin, hStep, vStep, bounds);
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 8; j++) {
				if (((i + j) % 2) == 1) {
					fillCell(i, j);
				}
			}
		}
	}
}
