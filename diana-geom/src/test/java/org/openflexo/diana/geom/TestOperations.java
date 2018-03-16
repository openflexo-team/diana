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

import org.openflexo.diana.geom.FGEEllips;
import org.openflexo.diana.geom.FGERectangle;
import org.openflexo.diana.geom.FGEGeometricObject.Filling;
import org.openflexo.diana.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.area.FGEArea;
import org.openflexo.diana.geom.area.FGEEmptyArea;
import org.openflexo.diana.geom.area.FGEIntersectionArea;
import org.openflexo.diana.geom.area.FGESubstractionArea;
import org.openflexo.diana.geom.area.FGEUnionArea;

import junit.framework.TestCase;

public class TestOperations extends TestCase {

	static FGERectangle r1 = new FGERectangle(0, 4, 8, 8, Filling.FILLED);
	static FGERectangle r2 = new FGERectangle(4, 1, 3, 5, Filling.FILLED);
	static FGERectangle r3 = new FGERectangle(6, 0, 3, 7, Filling.FILLED);
	static FGERectangle r4 = new FGERectangle(9, 0, 2, 7, Filling.FILLED);
	static FGERectangle r5 = new FGERectangle(5, 2, 10, 6, Filling.FILLED);
	static FGERectangle r6 = new FGERectangle(12, 4, 2, 2, Filling.FILLED);

	static FGEArea r7;
	static FGEArea r8;
	static FGEArea r9;
	static FGEArea r10;

	public void testSomeOperations() {
		r7 = FGEUnionArea.makeUnion(r3, r4);
		System.out.println("r7: " + r7);
		assertEquals(new FGERectangle(6, 0, 5, 7, Filling.FILLED), r7);

		r8 = FGEUnionArea.makeUnion(r2, FGEUnionArea.makeUnion(r3, r4));
		System.out.println("r8: " + r8);
		assertEquals(new FGEUnionArea(r2, r7), r8);

		assertEquals(new FGEEmptyArea(), FGESubstractionArea.makeSubstraction(r6, r5, false));
		// assertEquals(r1,FGESubstractionArea.makeSubstraction(r1,r6,false));
		r9 = FGESubstractionArea.makeSubstraction(r5, r6, false);
		System.out.println("r9: " + r9);
		assertEquals(new FGESubstractionArea(r5, r6, false), r9);

		r10 = FGEIntersectionArea.makeIntersection(r1, FGEUnionArea.makeUnion(r3, r4), FGESubstractionArea.makeSubstraction(r5, r6, false));
		System.out.println("r10: " + r10);
		assertEquals(new FGERectangle(6, 4, 2, 3, Filling.FILLED), r10);

		FGEEllips ellips1 = new FGEEllips(0, 0, 3, 3, Filling.FILLED);
		FGEEllips ellips2 = new FGEEllips(5, 1, 3, 3, Filling.FILLED);
		FGEArea area1 = ellips1.getOrthogonalPerspectiveArea(SimplifiedCardinalDirection.EAST);
		FGEArea area2 = ellips2.getOrthogonalPerspectiveArea(SimplifiedCardinalDirection.WEST);

		System.out.println("area1=" + area1);
		System.out.println("area2=" + area2);
		System.out.println("intersect=" + area1.intersect(area2));

	}

}
