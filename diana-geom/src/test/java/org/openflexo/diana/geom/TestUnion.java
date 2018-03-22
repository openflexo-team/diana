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

import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolygon;
import org.openflexo.diana.geom.DianaPolylin;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaHalfPlane;
import org.openflexo.diana.geom.area.DianaUnionArea;

import junit.framework.TestCase;

public class TestUnion extends TestCase {

	static DianaLine line1 = new DianaLine(new DianaPoint(0, 0), new DianaPoint(1, 0));
	static DianaLine line2 = new DianaLine(new DianaPoint(0, 0), new DianaPoint(1, 1));
	static DianaRectangle rectangle = new DianaRectangle(new DianaPoint(0, 0), new DianaPoint(1, 1), Filling.FILLED);
	static DianaHalfPlane hp = new DianaHalfPlane(line1, new DianaPoint(1, 1));
	static DianaPoint p1 = new DianaPoint(0, 0);
	static DianaPoint p2 = new DianaPoint(4, 0);
	static DianaPoint p3 = new DianaPoint(1, 0);
	static DianaPoint p4 = new DianaPoint(3, 0);
	static DianaPoint p5 = new DianaPoint(-2, 0);
	static DianaPoint p6 = new DianaPoint(6, 0);
	static DianaPoint p7 = new DianaPoint(2, 1);
	static DianaPoint p8 = new DianaPoint(4, 9);
	static DianaPoint p9 = new DianaPoint(6, 6);
	static DianaPoint p10 = new DianaPoint(6, 89);
	static DianaPoint p11 = new DianaPoint(632, 23);
	static DianaPoint p12 = new DianaPoint(2, 44);
	static DianaSegment s1 = new DianaSegment(p1, p2);
	static DianaSegment s2 = new DianaSegment(p3, p4);
	static DianaSegment s3 = new DianaSegment(p2, p3);
	static DianaSegment s4 = new DianaSegment(p3, p5);
	static DianaSegment s5 = new DianaSegment(p4, p6);
	static DianaSegment s6 = new DianaSegment(p7, p8);
	static DianaSegment s7 = new DianaSegment(p9, p10);
	static DianaSegment s8 = new DianaSegment(p9, p8);
	static DianaSegment s9 = new DianaSegment(p11, p10);
	static DianaSegment s10 = new DianaSegment(p11, p12);
	static DianaSegment s11 = new DianaSegment(p12, p1);

	public void testUnion1() {
		System.out.println("Union1: " + DianaUnionArea.makeUnion(line1, line2));
		assertEquals(new DianaUnionArea(line1, line2), DianaUnionArea.makeUnion(line1, line2));
	}

	public void testUnion2() {
		System.out.println("Union2: " + DianaUnionArea.makeUnion(rectangle, line1, p1));

		assertEquals(new DianaUnionArea(rectangle, line1), DianaUnionArea.makeUnion(rectangle, line1, p1));
	}

	public void testUnion3() {
		System.out.println("Union3: " + DianaUnionArea.makeUnion(p1, line1, rectangle, hp));

		assertEquals(hp, DianaUnionArea.makeUnion(p1, line1, rectangle, hp));
	}

	public void testUnion4() {
		System.out.println("Union4: " + DianaUnionArea.makeUnion(p1, line1, rectangle, hp, line2));

		assertEquals(new DianaUnionArea(hp, line2), DianaUnionArea.makeUnion(p1, line1, rectangle, hp, line2));
	}

	public void testSegments1() {
		assertEquals(s1, DianaUnionArea.makeUnion(s1, s2));
		assertEquals(s1, DianaUnionArea.makeUnion(s1, s3));

		assertEquals(new DianaSegment(p5, p2), DianaUnionArea.makeUnion(s4, s3));
		assertEquals(new DianaSegment(p2, p5), DianaUnionArea.makeUnion(new DianaSegment(p5, p3), s3));

		assertEquals(new DianaSegment(p5, p6), DianaUnionArea.makeUnion(s1, s2, s3, s4, s5));

	}

	public void testSegments2() {
		assertEquals(new DianaPolylin(p7, p8, p9), DianaUnionArea.makeUnion(s6, s8));
		assertEquals(new DianaPolylin(p9, p8, p7), DianaUnionArea.makeUnion(s6, s8));
		assertEquals(new DianaPolylin(p7, p8, p9, p10), DianaUnionArea.makeUnion(s6, s8, s7));
		assertEquals(new DianaPolylin(p10, p9, p8, p7), DianaUnionArea.makeUnion(s7, s6, s8));

		assertEquals(new DianaPolygon(Filling.NOT_FILLED, p7, p8, p9),
				DianaUnionArea.makeUnion(new DianaSegment(p7, p9), DianaUnionArea.makeUnion(s6, s8)));
		assertEquals(new DianaPolygon(Filling.NOT_FILLED, p8, p7, p9),
				DianaUnionArea.makeUnion(new DianaSegment(p7, p9), DianaUnionArea.makeUnion(s6, s8)));

		assertEquals(new DianaPolylin(p7, p8, p9, p10, p11, p12, p1), DianaUnionArea.makeUnion(s7, s6, s8, s9, s11, s10));
		assertEquals(new DianaUnionArea(new DianaPolylin(p7, p8, p9, p10), new DianaPolylin(p12, p1, p2)),
				DianaUnionArea.makeUnion(s6, s8, s11, s1, s7));
	}

	public void testSegments3() {
		DianaSegment a = new DianaSegment(p5, p6);
		DianaSegment b = new DianaSegment(p6, p7);
		DianaSegment c = new DianaSegment(p7, p8);
		DianaSegment d = new DianaSegment(p8, p5);

		DianaArea poly1 = DianaUnionArea.makeUnion(a, b);
		DianaArea poly2 = DianaUnionArea.makeUnion(b, c);
		DianaArea poly3 = DianaUnionArea.makeUnion(c, d);
		DianaArea poly4 = DianaUnionArea.makeUnion(d, a);

		System.out.println("poly1=" + poly1);
		System.out.println("poly2=" + poly2);
		System.out.println("poly3=" + poly3);
		System.out.println("poly4=" + poly4);

		System.out.println("Union=" + DianaUnionArea.makeUnion(poly1, poly2, poly3, poly4));

		/*DianaArea pp1 = DianaUnionArea.makeUnion(a,b,c);
		DianaArea pp2 = DianaUnionArea.makeUnion(c,d,a);
		
		System.out.println("pp1="+pp1);
		System.out.println("pp2="+pp2);
		
		System.out.println("2-Union="+DianaUnionArea.makeUnion(pp1,pp2));
		System.out.println("2-Union2="+pp1.union(pp2));*/

	}

}
