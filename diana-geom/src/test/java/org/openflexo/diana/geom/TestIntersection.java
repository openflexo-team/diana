/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.diana.geom;

import org.junit.Assert;
import org.junit.Test;
import org.openflexo.diana.geom.DianaArc;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaEllips;
import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolygon;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaRoundRectangle;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.area.DianaHalfPlane;
import org.openflexo.diana.geom.area.DianaIntersectionArea;

public class TestIntersection extends Assert {

	private static final DianaSegment line1 = new DianaSegment(new DianaPoint(0, 0), new DianaPoint(0, 1)); // Vertical line
	private static final DianaLine line2 = new DianaLine(new DianaPoint(0, 0), new DianaPoint(1, 1)); // Diagonal (top-left to bottom-right)
	private static final DianaRectangle rectangle = new DianaRectangle(new DianaPoint(0, 0), new DianaPoint(1, 1), Filling.FILLED);
	private static final DianaHalfPlane hp = new DianaHalfPlane(line1, new DianaPoint(-1, 1));
	private static final DianaPoint TOP_LEFT = new DianaPoint(0, 0);
	private static final DianaPoint TOP_RIGHT = new DianaPoint(1, 0);
	private static final DianaPoint BOTTOM_LEFT = new DianaPoint(0, 1);
	private static final DianaPoint BOTTOM_RIGHT = new DianaPoint(1, 1);
	private static final DianaPoint MIDDLE = new DianaPoint(0.5, 0.5);
	private static final DianaPolygon DIAMOND = new DianaPolygon(Filling.FILLED, new DianaPoint(0.5, 0), new DianaPoint(1, 0.5), new DianaPoint(0.5,
			1), new DianaPoint(0, 0.5));

	private static final DianaLine HORIZONTAL_LINE = new DianaLine(0, 1, 0);
	private static final DianaLine OFFSETED_HORIZONTAL_LINE = new DianaLine(0, 1, -1);
	private static final DianaLine VERTICAL_LINE = new DianaLine(1, 0, 0);
	private static final DianaLine OFFSETED_VERTICAL_LINE = new DianaLine(1, 0, -1);

	private static final DianaEllips ELLIPS = new DianaEllips(new DianaPoint(0, 0), new DianaDimension(1, 1), Filling.NOT_FILLED);
	private static final DianaArc HALF_ELLIPS = new DianaArc(ELLIPS.getCenter(), new DianaDimension(ELLIPS.getWidth(), ELLIPS.getHeight()), 90,
			180);

	private static final DianaRoundRectangle ROUND_RECTANGLE = new DianaRoundRectangle(0, 0, 1, 1, 0.01, 0.01);

	@Test
	public void testParallelism() {
		assertFalse(VERTICAL_LINE.isParallelTo(rectangle.getNorth()));
		assertFalse(VERTICAL_LINE.isParallelTo(rectangle.getSouth()));
		assertTrue(VERTICAL_LINE.isParallelTo(rectangle.getEast()));
		assertTrue(VERTICAL_LINE.isParallelTo(rectangle.getWest()));

		assertFalse(OFFSETED_VERTICAL_LINE.isParallelTo(rectangle.getNorth()));
		assertFalse(OFFSETED_VERTICAL_LINE.isParallelTo(rectangle.getSouth()));
		assertTrue(OFFSETED_VERTICAL_LINE.isParallelTo(rectangle.getEast()));
		assertTrue(OFFSETED_VERTICAL_LINE.isParallelTo(rectangle.getWest()));

		assertTrue(HORIZONTAL_LINE.isParallelTo(rectangle.getNorth()));
		assertTrue(HORIZONTAL_LINE.isParallelTo(rectangle.getSouth()));
		assertFalse(HORIZONTAL_LINE.isParallelTo(rectangle.getEast()));
		assertFalse(HORIZONTAL_LINE.isParallelTo(rectangle.getWest()));

		assertTrue(OFFSETED_HORIZONTAL_LINE.isParallelTo(rectangle.getNorth()));
		assertTrue(OFFSETED_HORIZONTAL_LINE.isParallelTo(rectangle.getSouth()));
		assertFalse(OFFSETED_HORIZONTAL_LINE.isParallelTo(rectangle.getEast()));
		assertFalse(OFFSETED_HORIZONTAL_LINE.isParallelTo(rectangle.getWest()));
	}

	@Test
	public void testOrthonalism() {
		assertTrue(VERTICAL_LINE.isOrthogonalTo(rectangle.getNorth()));
		assertTrue(VERTICAL_LINE.isOrthogonalTo(rectangle.getSouth()));
		assertFalse(VERTICAL_LINE.isOrthogonalTo(rectangle.getEast()));
		assertFalse(VERTICAL_LINE.isOrthogonalTo(rectangle.getWest()));

		assertTrue(OFFSETED_VERTICAL_LINE.isOrthogonalTo(rectangle.getNorth()));
		assertTrue(OFFSETED_VERTICAL_LINE.isOrthogonalTo(rectangle.getSouth()));
		assertFalse(OFFSETED_VERTICAL_LINE.isOrthogonalTo(rectangle.getEast()));
		assertFalse(OFFSETED_VERTICAL_LINE.isOrthogonalTo(rectangle.getWest()));

		assertFalse(HORIZONTAL_LINE.isOrthogonalTo(rectangle.getNorth()));
		assertFalse(HORIZONTAL_LINE.isOrthogonalTo(rectangle.getSouth()));
		assertTrue(HORIZONTAL_LINE.isOrthogonalTo(rectangle.getEast()));
		assertTrue(HORIZONTAL_LINE.isOrthogonalTo(rectangle.getWest()));

		assertFalse(OFFSETED_HORIZONTAL_LINE.isOrthogonalTo(rectangle.getNorth()));
		assertFalse(OFFSETED_HORIZONTAL_LINE.isOrthogonalTo(rectangle.getSouth()));
		assertTrue(OFFSETED_HORIZONTAL_LINE.isOrthogonalTo(rectangle.getEast()));
		assertTrue(OFFSETED_HORIZONTAL_LINE.isOrthogonalTo(rectangle.getWest()));
	}

	@Test
	public void testContainment() {
		assertTrue(line2.contains(MIDDLE));
		assertFalse(line2.contains(TOP_RIGHT));
		assertFalse(line2.contains(BOTTOM_LEFT));

		assertTrue(HORIZONTAL_LINE.contains(TOP_LEFT));
		assertTrue(HORIZONTAL_LINE.contains(TOP_RIGHT));
		assertFalse(HORIZONTAL_LINE.contains(BOTTOM_LEFT));
		assertFalse(HORIZONTAL_LINE.contains(BOTTOM_RIGHT));
		assertEquals(HORIZONTAL_LINE.getPlaneLocation(BOTTOM_LEFT), HORIZONTAL_LINE.getPlaneLocation(MIDDLE));
		assertEquals(HORIZONTAL_LINE.getPlaneLocation(BOTTOM_RIGHT), HORIZONTAL_LINE.getPlaneLocation(MIDDLE));

		assertFalse(OFFSETED_HORIZONTAL_LINE.contains(TOP_LEFT));
		assertFalse(OFFSETED_HORIZONTAL_LINE.contains(TOP_RIGHT));
		assertTrue(OFFSETED_HORIZONTAL_LINE.contains(BOTTOM_LEFT));
		assertTrue(OFFSETED_HORIZONTAL_LINE.contains(BOTTOM_RIGHT));
		assertEquals(OFFSETED_HORIZONTAL_LINE.getPlaneLocation(TOP_LEFT), OFFSETED_HORIZONTAL_LINE.getPlaneLocation(MIDDLE));
		assertEquals(OFFSETED_HORIZONTAL_LINE.getPlaneLocation(TOP_RIGHT), OFFSETED_HORIZONTAL_LINE.getPlaneLocation(MIDDLE));

		assertTrue(VERTICAL_LINE.contains(TOP_LEFT));
		assertTrue(VERTICAL_LINE.contains(BOTTOM_LEFT));
		assertFalse(VERTICAL_LINE.contains(TOP_RIGHT));
		assertFalse(VERTICAL_LINE.contains(BOTTOM_RIGHT));
		assertEquals(VERTICAL_LINE.getPlaneLocation(TOP_RIGHT), VERTICAL_LINE.getPlaneLocation(MIDDLE));
		assertEquals(VERTICAL_LINE.getPlaneLocation(BOTTOM_RIGHT), VERTICAL_LINE.getPlaneLocation(MIDDLE));

		assertTrue(OFFSETED_VERTICAL_LINE.contains(BOTTOM_RIGHT));
		assertTrue(OFFSETED_VERTICAL_LINE.contains(TOP_RIGHT));
		assertFalse(OFFSETED_VERTICAL_LINE.contains(BOTTOM_LEFT));
		assertFalse(OFFSETED_VERTICAL_LINE.contains(TOP_LEFT));
		assertEquals(OFFSETED_VERTICAL_LINE.getPlaneLocation(TOP_LEFT), OFFSETED_VERTICAL_LINE.getPlaneLocation(MIDDLE));
		assertEquals(OFFSETED_VERTICAL_LINE.getPlaneLocation(BOTTOM_LEFT), OFFSETED_VERTICAL_LINE.getPlaneLocation(MIDDLE));

	}

	@Test
	public void testIntersection() {
		assertFalse(hp.containsArea(DIAMOND));
		assertFalse(hp.containsArea(rectangle));
		assertFalse(DIAMOND.containsArea(hp));
		assertFalse(rectangle.containsArea(hp));
		assertEquals(new DianaPoint(0, 0.5), DianaIntersectionArea.makeIntersection(hp, DIAMOND));
		assertEquals(line1, DianaIntersectionArea.makeIntersection(hp, rectangle));
		assertEquals(line1, DianaIntersectionArea.makeIntersection(line1, rectangle));
		assertEquals(line1, DianaIntersectionArea.makeIntersection(rectangle, line1));
		assertEquals(TOP_LEFT, DianaIntersectionArea.makeIntersection(line1, TOP_LEFT));
		assertEquals(TOP_LEFT, DianaIntersectionArea.makeIntersection(TOP_LEFT, line1));
		assertEquals(TOP_LEFT, DianaIntersectionArea.makeIntersection(TOP_LEFT, line2));
		assertEquals(ROUND_RECTANGLE.getArcExcludedWest(), ROUND_RECTANGLE.intersect(hp));
		assertEquals(HALF_ELLIPS, ELLIPS.intersect(hp));

		DianaEllips ellips = new DianaEllips(0.07092198581560283, 0.16666666666666666, 0.21985815602836878, 0.4696969696969698,
				Filling.NOT_FILLED);
		DianaLine line = new DianaLine(0.18085106382978722, 0.16666666666666669, 0.400709219858156, 0.16666666666666669);
		DianaPoint point = new DianaPoint(0.18085106382978722, -0.30303030303030304);
		DianaHalfPlane halfPlane = new DianaHalfPlane(line, point);
		assertEquals(ellips.getPointAtAngle(90), DianaIntersectionArea.makeIntersection(ellips, halfPlane));
	}
}
