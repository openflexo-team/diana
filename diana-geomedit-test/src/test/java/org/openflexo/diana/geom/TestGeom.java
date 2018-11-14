/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geomedit, a component of the software infrastructure 
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.junit.Test;
import org.openflexo.diana.geom.DianaGeometricObject.CardinalDirection;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaGrid;
import org.openflexo.diana.geomedit.DiagramEditingContext;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstructionFactory;
import org.openflexo.diana.geomedit.model.GeometricDiagram;
import org.openflexo.diana.geomedit.model.IntersectionConstruction;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public class TestGeom {

	private static final Logger logger = Logger.getLogger(TestGeom.class.getPackage().getName());

	@Test
	public void testBands() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestBands.geom"));
	}

	@Test
	public void testCircles() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestCircles.geom"));
	}

	@Test
	public void testComplexCurves() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestComplexCurves.geom"));
	}

	@Test
	public void testCubicCurves() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestCubicCurves.geom"));
	}

	@Test
	public void testEmpty() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestEmpty.geom"));
	}

	@Test
	public void testHalfBands() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestHalfBands.geom"));
	}

	@Test
	public void testHalfLines() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestHalfLines.geom"));
	}

	@Test
	public void testHalfPlanes() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestHalfPlanes.geom"));
	}

	@Test
	public void testIntersections() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestIntersections.geom"));
	}

	@Test
	public void testLines() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestLines.geom"));
	}

	@Test
	public void testPoints() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestPoints.geom"));
	}

	@Test
	public void testPolygons() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestPolygons.geom"));
	}

	@Test
	public void testPolylines() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestPolylines.geom"));
	}

	@Test
	public void testQuadCurves() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestQuadCurves.geom"));
	}

	@Test
	public void testRectangles() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestRectangles.geom"));
	}

	@Test
	public void testRectPolylin() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestRectPolylin.geom"));
	}

	@Test
	public void testRoundRectangles() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestRoundRectangles.geom"));
	}

	@Test
	public void testSegments() throws FileNotFoundException, Exception {
		executeTest(ResourceLocator.locateResource("GeomJUnitTest/Basic/TestSegments.geom"));
	}

	@Test
	public void testGrid() {
		//
		DianaPoint testPoint1 = new DianaPoint(0, 0);
		DianaPoint testPoint2 = new DianaPoint(1, 1);
		DianaPoint testPoint3 = new DianaPoint(0.8, 0.8);
		DianaPoint testPoint4 = new DianaPoint(2.7, 1.3);
		DianaGrid grid = new DianaGrid();
		assertTrue(grid.containsPoint(new DianaPoint(0, 0)));
		assertTrue(grid.containsPoint(new DianaPoint(1, 0)));
		assertTrue(grid.containsPoint(new DianaPoint(0, 1)));
		assertTrue(grid.containsPoint(new DianaPoint(1, 1)));
		assertFalse(grid.containsPoint(new DianaPoint(0.5, 0)));
		assertFalse(grid.containsPoint(new DianaPoint(0, 0.5)));
		assertEquals(new DianaPoint(0, 0), grid.getNearestPoint(testPoint1));
		assertEquals(new DianaPoint(1, 1), grid.getNearestPoint(testPoint2));
		assertEquals(new DianaPoint(1, 1), grid.getNearestPoint(testPoint3));
		assertEquals(new DianaPoint(3, 1), grid.getNearestPoint(testPoint4));

		DianaGrid grid2 = new DianaGrid(new DianaPoint(0.5, 0.5), 0.25, 0.25);
		assertTrue(grid2.containsPoint(new DianaPoint(0, 0)));
		assertFalse(grid2.containsPoint(new DianaPoint(0.3, 0)));
		assertFalse(grid2.containsPoint(new DianaPoint(0, 0.3)));
		assertTrue(grid2.containsPoint(new DianaPoint(1, 0.5)));
		assertTrue(grid2.containsPoint(new DianaPoint(0.5, 1)));
		assertTrue(grid2.containsPoint(new DianaPoint(1, 1)));
		assertEquals(new DianaPoint(0, 0), grid2.getNearestPoint(testPoint1));
		assertEquals(new DianaPoint(1, 1), grid2.getNearestPoint(testPoint2));
		assertEquals(new DianaPoint(0.75, 0.75), grid2.getNearestPoint(testPoint3));
		assertEquals(new DianaPoint(2.75, 1.25), grid2.getNearestPoint(testPoint4));

		DianaGrid grid3 = new DianaGrid(new DianaPoint(0.3, 0.3), 0.4, 0.4);
		assertTrue(grid3.containsPoint(new DianaPoint(-0.1, -0.1)));
		assertTrue(grid3.containsPoint(new DianaPoint(0.3, 0.3)));
		assertTrue(grid3.containsPoint(new DianaPoint(0.7, 0.7)));
		assertTrue(grid3.containsPoint(new DianaPoint(0.3, 0.7)));
		assertTrue(grid3.containsPoint(new DianaPoint(0.3, 0.3)));
		assertEquals(new DianaPoint(-0.1, -0.1), grid3.getNearestPoint(testPoint1));
		assertEquals(new DianaPoint(1.1, 1.1), grid3.getNearestPoint(testPoint2));
		assertEquals(new DianaPoint(0.7, 0.7), grid3.getNearestPoint(testPoint3));
		assertEquals(new DianaPoint(2.7, 1.1), grid3.getNearestPoint(testPoint4));

	}

	@Test
	public void testPointOrientation() {
		DianaPoint topLeft = new DianaPoint(0, 0);
		DianaPoint topRight = new DianaPoint(1, 0);
		DianaPoint bottomLeft = new DianaPoint(0, 1);
		DianaPoint bottomRight = new DianaPoint(1, 1);
		DianaPoint top = new DianaPoint(0.5, 0);
		DianaPoint left = new DianaPoint(0, 0.5);
		DianaPoint bottom = new DianaPoint(0.5, 1);
		DianaPoint right = new DianaPoint(1, 0.5);
		DianaPoint center = new DianaPoint(0.5, 0.5);
		assertEquals(CardinalDirection.NORTH_WEST, DianaPoint.getOrientation(center, topLeft));
		assertEquals(CardinalDirection.NORTH_WEST, DianaPoint.getOrientation(center, new DianaPoint(topLeft.x + 0.125, topLeft.y)));
		assertEquals(CardinalDirection.NORTH_WEST, DianaPoint.getOrientation(center, new DianaPoint(topLeft.x, topLeft.y + 0.125)));
		assertEquals(CardinalDirection.NORTH, DianaPoint.getOrientation(center, top));
		assertEquals(CardinalDirection.NORTH, DianaPoint.getOrientation(center, new DianaPoint(top.x + 0.125, top.y)));
		assertEquals(CardinalDirection.NORTH, DianaPoint.getOrientation(center, new DianaPoint(top.x - 0.125, top.y)));
		assertEquals(CardinalDirection.NORTH_EAST, DianaPoint.getOrientation(center, topRight));
		assertEquals(CardinalDirection.NORTH_EAST, DianaPoint.getOrientation(center, new DianaPoint(topRight.x - 0.125, topRight.y)));
		assertEquals(CardinalDirection.NORTH_EAST, DianaPoint.getOrientation(center, new DianaPoint(topRight.x, topRight.y + 0.125)));
		assertEquals(CardinalDirection.EAST, DianaPoint.getOrientation(center, right));
		assertEquals(CardinalDirection.EAST, DianaPoint.getOrientation(center, new DianaPoint(right.x, right.y + 0.125)));
		assertEquals(CardinalDirection.EAST, DianaPoint.getOrientation(center, new DianaPoint(right.x, right.y - 0.125)));
		assertEquals(CardinalDirection.SOUTH_EAST, DianaPoint.getOrientation(center, bottomRight));
		assertEquals(CardinalDirection.SOUTH_EAST, DianaPoint.getOrientation(center, new DianaPoint(bottomRight.x - 0.125, bottomRight.y)));
		assertEquals(CardinalDirection.SOUTH_EAST, DianaPoint.getOrientation(center, new DianaPoint(bottomRight.x, bottomRight.y - 0.125)));
		assertEquals(CardinalDirection.SOUTH, DianaPoint.getOrientation(center, bottom));
		assertEquals(CardinalDirection.SOUTH, DianaPoint.getOrientation(center, new DianaPoint(bottom.x + 0.125, bottom.y)));
		assertEquals(CardinalDirection.SOUTH, DianaPoint.getOrientation(center, new DianaPoint(bottom.x - 0.125, bottom.y)));
		assertEquals(CardinalDirection.SOUTH_WEST, DianaPoint.getOrientation(center, bottomLeft));
		assertEquals(CardinalDirection.SOUTH_WEST, DianaPoint.getOrientation(center, new DianaPoint(bottomLeft.x - 0.125, bottomLeft.y)));
		assertEquals(CardinalDirection.SOUTH_WEST, DianaPoint.getOrientation(center, new DianaPoint(bottomLeft.x, bottomLeft.y + 0.125)));
		assertEquals(CardinalDirection.WEST, DianaPoint.getOrientation(center, left));
		assertEquals(CardinalDirection.WEST, DianaPoint.getOrientation(center, new DianaPoint(left.x, left.y + 0.125)));
		assertEquals(CardinalDirection.WEST, DianaPoint.getOrientation(center, new DianaPoint(left.x, left.y - 0.125)));

		/*assertEquals(CardinalDirection.EAST, DianaPoint.getOrientation(center, right));
		assertEquals(CardinalDirection.EAST, DianaPoint.getOrientation(center, right));
		assertEquals(CardinalDirection.EAST, DianaPoint.getOrientation(center, right));
		assertEquals(CardinalDirection.EAST, DianaPoint.getOrientation(center, right));*/
	}

	private void executeTest(Resource testResource) throws FileNotFoundException, Exception {

		System.out.println("geomedit test on " + testResource);

		if (testResource == null) {
			return;
		}

		DiagramEditingContext editingContext = new DiagramEditingContext();

		GeometricConstructionFactory factory = new GeometricConstructionFactory(editingContext);

		GeometricDiagram diagram = (GeometricDiagram) factory.deserialize(testResource.openInputStream());
		diagram.setFactory(factory);

		for (GeometricConstruction<?> geometricConstruction : diagram.getConstructions()) {
			if (geometricConstruction instanceof IntersectionConstruction) {
				IntersectionConstruction construction = (IntersectionConstruction) geometricConstruction;
				DianaArea o1 = construction.getObjectConstructions().get(0).getData();
				DianaArea o2 = construction.getObjectConstructions().get(1).getData();
				System.out.println("Checking intersection reversibility");
				assertEquals(o1.intersect(o2), o2.intersect(o1));
			}
		}

		/*logger.info(">>>>>>> Test " + testFile.getName());
		GeometricSet geometricSet = GeometricSet.load(testFile);
		for (GeometricObject object : geometricSet.getChilds()) {
			logger.fine("Check equals: " + object.getResultingGeometricObject() + " / " + object.getGeometricObject());
			assertEquals(object.getResultingGeometricObject(), object.getGeometricObject());
			if (object instanceof ObjectIntersection) {
				IntersectionConstruction construction = ((ObjectIntersection) object).getConstruction();
				DianaArea o1 = construction.objectConstructions.get(0).getData();
				DianaArea o2 = construction.objectConstructions.get(1).getData();
				logger.fine("Checking intersection reversibility");
				assertEquals(o1.intersect(o2), o2.intersect(o1));
			}
		}*/
	}
}
