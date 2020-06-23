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

package org.openflexo.diana.geom;

import org.openflexo.diana.geom.DianaArc.ArcType;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaUnionArea;

import junit.framework.TestCase;

public class TestArc extends TestCase {

	public void testEquals() {
		DianaArc arc1 = buildArc(0, 90);
		DianaArc arc2 = buildArc(0, 450);
		DianaArc arc3 = buildArc(360, 90);
		DianaArc arc4 = buildArc(360, 450);
		assertEquals(arc2, arc1);
		assertEquals(arc3, arc1);
		assertEquals(arc4, arc1);
	}

	public void testIntersect1() {
		DianaArc arc1 = buildArc(0, 90);
		DianaArc arc2 = buildArc(0, 45);
		assertEquals(arc2, arc1.intersect(arc2));
		assertEquals(arc2, arc2.intersect(arc1));
	}

	public void testIntersect2() {
		DianaArc arc1 = buildArc(-45, 45);
		DianaArc arc2 = buildArc(0, 90);
		assertEquals(buildArc(0, 45), arc1.intersect(arc2));
	}

	public void testIntersect3() {
		DianaArc arc1 = buildArc(20, 45);
		DianaArc arc2 = buildArc(0, 90);
		assertEquals(arc1, arc1.intersect(arc2));
	}

	public void testIntersect4() {
		DianaArc arc1 = buildArc(20, 90);
		DianaArc arc2 = buildArc(0, 45);
		assertEquals(buildArc(20, 45), arc1.intersect(arc2));
	}

	public void testIntersect5() {
		DianaArc arc1 = buildArc(20, 180);
		DianaArc arc2 = buildArc(0, 225);
		DianaArc arc3 = buildArc(-340, 180);
		DianaArc arc4 = buildArc(360, 225);
		assertEquals(buildArc(20, 180), arc1.intersect(arc2));
		assertEquals(buildArc(20, 180), arc4.intersect(arc3));
	}

	public void testIntersect6() {
		DianaArc arc1 = buildArc(0, 180);
		DianaArc arc2 = buildArc(20, 110);
		assertEquals(arc2, arc1.intersect(arc2));
		assertEquals(arc2, arc2.intersect(arc1));
	}

	public void testIntersect7() {
		DianaArc arc1 = buildArc(0, 180);
		DianaArc arc2 = buildArc(170, 200);
		assertEquals(buildArc(170, -180), arc1.intersect(arc2));
		assertEquals(buildArc(170, 180), arc2.intersect(arc1));
	}

	public void testIntersect8() {
		DianaArc arc1 = buildArc(0, 180);
		DianaArc arc2 = buildArc(170, 370);
		assertEquals(new DianaUnionArea(buildArc(170, 180), buildArc(0, 10)), arc1.intersect(arc2));
		assertEquals(new DianaUnionArea(buildArc(170, 180), buildArc(0, 10)), arc2.intersect(arc1));
	}

	public void testIntersect9() {
		DianaArc arc1 = buildArc(20, 30);
		DianaArc arc2 = buildArc(30, 210);
		assertEquals(arc1.getPointAtAngle(30), arc1.intersect(arc2));
	}

	public void testIntersect10() {
		DianaArc arc1 = buildArc(0, 180);
		DianaArc arc2 = buildArc(180, 0);
		assertEquals(new DianaUnionArea(new DianaPoint(0, 0.5), new DianaPoint(1, 0.5)), arc1.intersect(arc2));
	}

	public void testIntersect11() {
		DianaArc arc1 = buildArc(30, 80);
		DianaArc arc2 = buildArc(90, 130);
		assertEquals(new DianaEmptyArea(), arc1.intersect(arc2));
	}

	public void testIntersect12() {
		DianaArc arc1 = buildArc(30, 90);
		DianaArc arc2 = buildArc(-200, 80);
		assertEquals(new DianaEmptyArea(), arc1.intersect(arc2));
	}

	public void testIntersect13() {
		DianaArc arc1 = buildEllips();
		DianaArc arc2 = buildArc(0, 45);
		assertEquals(arc2, arc1.intersect(arc2));
		assertEquals(arc2, arc2.intersect(arc1));
	}

	// This test handle special cases of arcs starting or ending at 180°/-180°
	// Not a piece of cake
	public void testIntersect14() {
		DianaArc arc1 = buildEllips();
		DianaArc arc2 = buildArc(90, 270);
		DianaArc arc3 = buildArc(90, 180);
		assertEquals(true, arc2.containsArea(arc3));
		assertEquals(arc2, arc1.intersect(arc2));
		assertEquals(arc2, arc2.intersect(arc1));
	}

	public void testUnion1() {
		DianaArc arc1 = buildArc(0, 90);
		DianaArc arc2 = buildArc(10, 80);
		assertEquals(buildArc(0, 90), arc1.union(arc2));
	}

	public void testUnion2() {
		DianaArc arc1 = buildArc(0, 90);
		DianaArc arc2 = buildArc(10, 100);
		assertEquals(buildArc(0, 100), arc1.union(arc2));
	}

	public void testUnion3() {
		DianaArc arc1 = buildArc(0, 90);
		DianaArc arc2 = buildArc(90, 170);
		assertEquals(buildArc(0, 170), arc1.union(arc2));
	}

	public void testUnion4() {
		DianaArc arc1 = buildArc(0, 90);
		DianaArc arc2 = buildArc(90, 200);
		assertEquals(buildArc(0, 200), arc1.union(arc2));
	}

	public void testUnion5() {
		DianaArc arc1 = buildArc(0, 90);
		DianaArc arc2 = buildArc(90, 180);
		assertEquals(buildArc(0, 180), arc1.union(arc2));
	}

	public void testUnion6() {
		DianaArc arc1 = buildArc(-100, 100);
		DianaArc arc2 = buildArc(90, 270);
		assertEquals(buildEllips(), arc1.union(arc2));
	}

	public void testUnion7() {
		DianaArc arc1 = buildArc(0, 90);
		DianaArc arc2 = buildArc(90, 180);
		DianaArc arc3 = buildArc(-180, -90);
		DianaArc arc4 = buildArc(-90, 0);
		DianaArea result = DianaUnionArea.makeUnion(arc1, arc2, arc3, arc4);
		assertEquals(buildEllips(), result);
	}

	// This test handle special cases of arcs starting or ending at 180°/-180°
	// Not a piece of cake
	public void testUnion8() {
		DianaArc arc2 = buildArc(180, 0);
		DianaArc arc3 = buildArc(-180, 0);
		assertEquals(arc2, arc3);
		assertTrue(arc3.containsArea(arc2));
		assertTrue(arc2.containsArea(arc3));
	}

	public void testLineIntersection1() {
		DianaArc arc = buildArc(90, 270);
		DianaLine line = new DianaLine(new DianaPoint(0.5001, 0), new DianaPoint(0.5, 1));
		System.out.println("arc=" + arc);
		System.out.println("line=" + line);
		System.out.println("arc.intersect(line)=" + arc.intersect(line));
	}

	private static DianaArc buildArc(double angleStart, double angleEnd) {
		return new DianaArc(0, 0, 1, 1, angleStart, Math.toDegrees(DianaArc.angleExtent(Math.toRadians(angleStart), Math.toRadians(angleEnd))),
				ArcType.OPEN);
	}

	private static DianaArc buildEllips() {
		return new DianaEllips(0, 0, 1, 1, Filling.NOT_FILLED);
	}

}
