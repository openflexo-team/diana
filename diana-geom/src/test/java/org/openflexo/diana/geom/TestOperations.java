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

import org.openflexo.diana.geom.DianaEllips;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaIntersectionArea;
import org.openflexo.diana.geom.area.DianaSubstractionArea;
import org.openflexo.diana.geom.area.DianaUnionArea;

import junit.framework.TestCase;

public class TestOperations extends TestCase {

	static DianaRectangle r1 = new DianaRectangle(0, 4, 8, 8, Filling.FILLED);
	static DianaRectangle r2 = new DianaRectangle(4, 1, 3, 5, Filling.FILLED);
	static DianaRectangle r3 = new DianaRectangle(6, 0, 3, 7, Filling.FILLED);
	static DianaRectangle r4 = new DianaRectangle(9, 0, 2, 7, Filling.FILLED);
	static DianaRectangle r5 = new DianaRectangle(5, 2, 10, 6, Filling.FILLED);
	static DianaRectangle r6 = new DianaRectangle(12, 4, 2, 2, Filling.FILLED);

	static DianaArea r7;
	static DianaArea r8;
	static DianaArea r9;
	static DianaArea r10;

	public void testSomeOperations() {
		r7 = DianaUnionArea.makeUnion(r3, r4);
		System.out.println("r7: " + r7);
		assertEquals(new DianaRectangle(6, 0, 5, 7, Filling.FILLED), r7);

		r8 = DianaUnionArea.makeUnion(r2, DianaUnionArea.makeUnion(r3, r4));
		System.out.println("r8: " + r8);
		assertEquals(new DianaUnionArea(r2, r7), r8);

		assertEquals(new DianaEmptyArea(), DianaSubstractionArea.makeSubstraction(r6, r5, false));
		// assertEquals(r1,DianaSubstractionArea.makeSubstraction(r1,r6,false));
		r9 = DianaSubstractionArea.makeSubstraction(r5, r6, false);
		System.out.println("r9: " + r9);
		assertEquals(new DianaSubstractionArea(r5, r6, false), r9);

		r10 = DianaIntersectionArea.makeIntersection(r1, DianaUnionArea.makeUnion(r3, r4), DianaSubstractionArea.makeSubstraction(r5, r6, false));
		System.out.println("r10: " + r10);
		assertEquals(new DianaRectangle(6, 4, 2, 3, Filling.FILLED), r10);

		DianaEllips ellips1 = new DianaEllips(0, 0, 3, 3, Filling.FILLED);
		DianaEllips ellips2 = new DianaEllips(5, 1, 3, 3, Filling.FILLED);
		DianaArea area1 = ellips1.getOrthogonalPerspectiveArea(SimplifiedCardinalDirection.EAST);
		DianaArea area2 = ellips2.getOrthogonalPerspectiveArea(SimplifiedCardinalDirection.WEST);

		System.out.println("area1=" + area1);
		System.out.println("area2=" + area2);
		System.out.println("intersect=" + area1.intersect(area2));

	}

}
