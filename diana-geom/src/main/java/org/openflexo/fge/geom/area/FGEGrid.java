/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geom, a component of the software infrastructure 
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

package org.openflexo.fge.geom.area;

import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.AbstractFGEGraphics;

public class FGEGrid implements FGEArea {

	private static final Logger logger = Logger.getLogger(FGEGrid.class.getPackage().getName());

	public FGEPoint origin;
	public double hStep;
	public double vStep;
	public List<int[]> filledCells;
	public Map<FGEGridCoordinates, FGEGridCell> cells;

	public FGEGrid() {
		this(new FGEPoint(0, 0), 1.0, 1.0);
	}

	public FGEGrid(FGEPoint origin, double hStep, double vStep) {
		this.origin = origin;
		this.hStep = hStep;
		this.vStep = vStep;
		this.filledCells = new LinkedList<>();
		this.cells = new HashMap<>();
	}

	public double getHorizontalStep() {
		return hStep;
	}

	public double getVerticalStep() {
		return vStep;
	}

	@Override
	public boolean containsArea(FGEArea a) {
		if (a instanceof FGEPoint) {
			return containsPoint((FGEPoint) a);
		}
		if (a instanceof FGEGrid) {
			FGEGrid grid = (FGEGrid) a;
			return grid.origin.equals(origin) && grid.hStep == hStep && grid.vStep == vStep;
		}
		return false;
	}

	@Override
	public boolean containsLine(FGEAbstractLine<?> l) {
		return false;
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		return Math.abs(Math.IEEEremainder(p.x - origin.x, hStep)) < FGEGeometricObject.EPSILON
				&& Math.abs(Math.IEEEremainder(p.y - origin.y, vStep)) < FGEGeometricObject.EPSILON;
	}

	@Override
	public FGEArea exclusiveOr(FGEArea area) {
		return new FGEExclusiveOrArea(this, area);
	}

	@Override
	public FGEArea getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		return clone();
	}

	@Override
	public FGERectangle getEmbeddingBounds() {
		// Infinite--> null
		return null;
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint point) {
		FGEPoint translatedPoint = new FGEPoint(point.x - origin.x, point.y - origin.y);
		FGEPoint remainderPoint = new FGEPoint(Math.IEEEremainder(translatedPoint.x, hStep), Math.IEEEremainder(translatedPoint.y, vStep));
		FGEPoint ulPoint = new FGEPoint(point.x - remainderPoint.x, point.y - remainderPoint.y);

		double distanceUL = point.distance(ulPoint);
		double distanceUR = point.distance(ulPoint.x + hStep, ulPoint.y);
		double distanceLL = point.distance(ulPoint.x, ulPoint.y + vStep);
		double distanceLR = point.distance(ulPoint.x + hStep, ulPoint.y + vStep);
		if (distanceUL <= distanceUR && distanceUL <= distanceLL && distanceUL <= distanceLR) {
			// Upper left is closest
			return ulPoint;
		}
		else if (distanceUR <= distanceUL && distanceUR <= distanceLL && distanceUR <= distanceLR) {
			// Upper right is closest
			ulPoint.x += hStep;
			return ulPoint;
		}
		else if (distanceLL <= distanceUR && distanceLL <= distanceUL && distanceLL <= distanceLR) {
			// Lower left is closest
			ulPoint.y += vStep;
			return ulPoint;
		}
		else {
			// Lower right is closest
			ulPoint.x += hStep;
			ulPoint.y += vStep;
			return ulPoint;
		}
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FGEArea intersect(FGEArea area) {
		return null;
	}

	@Override
	public boolean isFinite() {
		return false;
	}

	@Override
	public FGEPoint nearestPointFrom(FGEPoint from, SimplifiedCardinalDirection orientation) {
		return getNearestPoint(from).nearestPointFrom(from, orientation);
	}

	@Override
	public void paint(AbstractFGEGraphics g) {

		FGERectangle bounds = g.getNodeNormalizedBounds();
		bounds.setIsFilled(true);

		int iStart = (int) Math.ceil((bounds.getMinX() - origin.x) / hStep);
		int iEnd = (int) Math.floor((bounds.getMaxX() - origin.x) / hStep);
		int jStart = (int) Math.ceil((bounds.getMinY() - origin.y) / hStep);
		int jEnd = (int) Math.floor((bounds.getMaxY() - origin.y) / hStep);
		int nx = (int) (bounds.getWidth() / hStep);
		int ny = (int) (bounds.getHeight() / vStep);

		for (int i = iStart; i < iEnd + 1; i++) {
			FGELine l = new FGELine(new FGEPoint(origin.x + i * hStep, 0), new FGEPoint(origin.x + i * hStep, 1));
			bounds.intersect(l).paint(g);
		}
		for (int j = jStart; j < jEnd + 1; j++) {
			FGELine l = new FGELine(new FGEPoint(0, origin.y + j * vStep), new FGEPoint(1, origin.y + j * vStep));
			bounds.intersect(l).paint(g);
		}

		for (FGEGridCell cell : cells.values()) {
			bounds.intersect(cell).paint(g);
		}

	}

	@Override
	public FGEArea substract(FGEArea area, boolean isStrict) {
		return new FGESubstractionArea(this, area, isStrict);
	}

	@Override
	public FGEArea transform(AffineTransform t) {
		return clone();
	}

	@Override
	public FGEArea union(FGEArea area) {
		return new FGEUnionArea(this, area);
	}

	@Override
	public FGEGrid clone() {
		return new FGEGrid(new FGEPoint(origin), hStep, vStep);
	}

	public void fillCell(FGEGridCoordinates coordinates) {
		getCell(coordinates).setIsFilled(true);
	}

	public void fillCell(int x, int y) {
		getCell(x, y).setIsFilled(true);

		int[] cell = { x, y };
		filledCells.add(cell);
	}

	public FGEGridCell getCell(FGEGridCoordinates coordinates) {
		if (cells.containsKey(coordinates)) {
			return cells.get(coordinates);
		}
		else {
			FGEGridCell cell = new FGEGridCell(coordinates, this);
			cells.put(coordinates, cell);
			return cell;
		}
	}

	public FGEGridCell getCell(int x, int y) {
		FGEGridCoordinates coordinates = new FGEGridCoordinates(x, y);

		return getCell(coordinates);
	}

	public FGEGridCell getCell(FGEPoint point) {
		int x = (int) Math.floor(point.getX() / hStep);
		int y = (int) Math.floor(point.getY() / vStep);

		return getCell(x, y);
	}

	public class FGEGridCell extends FGERectangle {
		private final FGEGridCoordinates coordinates;
		private final FGEGrid grid;

		protected FGEGridCell(FGEGridCoordinates coord, FGEGrid grid) {
			super(coord.x * hStep, coord.y * vStep, hStep, vStep);
			this.coordinates = coord;
			this.grid = grid;
		}

		protected FGEGridCell(int xInGrid, int yInGrid, FGEGrid grid) {
			this(new FGEGridCoordinates(xInGrid, yInGrid), grid);
		}

		public int getXInGrid() {
			return coordinates.x;
		}

		public int getYInGrid() {
			return coordinates.y;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof FGEGridCoordinates))
				return false;
			if (obj == this)
				return true;

			FGEGridCell cell = (FGEGridCell) obj;
			return (cell.coordinates == this.coordinates) && (cell.grid == this.grid);
		}
	}

	public class FGEGridCoordinates {
		public final int x, y;

		public FGEGridCoordinates(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof FGEGridCoordinates))
				return false;
			if (obj == this)
				return true;

			FGEGridCoordinates coordinates = (FGEGridCoordinates) obj;
			return (coordinates.x == this.x) && (coordinates.y == this.y);
		}

		@Override
		public int hashCode() {
			int result = x;
			result = 421 * result + y;
			return result;
		}
	}
}
