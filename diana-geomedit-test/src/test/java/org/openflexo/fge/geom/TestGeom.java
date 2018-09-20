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

package org.openflexo.fge.geom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.junit.Test;
import org.openflexo.diana.geomedit.DiagramEditingContext;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstructionFactory;
import org.openflexo.diana.geomedit.model.GeometricDiagram;
import org.openflexo.diana.geomedit.model.IntersectionConstruction;
import org.openflexo.fge.geom.FGEGeometricObject.CardinalDirection;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEGrid;
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
		FGEPoint testPoint1 = new FGEPoint(0, 0);
		FGEPoint testPoint2 = new FGEPoint(1, 1);
		FGEPoint testPoint3 = new FGEPoint(0.8, 0.8);
		FGEPoint testPoint4 = new FGEPoint(2.7, 1.3);
		FGEGrid grid = new FGEGrid();
		assertTrue(grid.containsPoint(new FGEPoint(0, 0)));
		assertTrue(grid.containsPoint(new FGEPoint(1, 0)));
		assertTrue(grid.containsPoint(new FGEPoint(0, 1)));
		assertTrue(grid.containsPoint(new FGEPoint(1, 1)));
		assertFalse(grid.containsPoint(new FGEPoint(0.5, 0)));
		assertFalse(grid.containsPoint(new FGEPoint(0, 0.5)));
		assertEquals(new FGEPoint(0, 0), grid.getNearestPoint(testPoint1));
		assertEquals(new FGEPoint(1, 1), grid.getNearestPoint(testPoint2));
		assertEquals(new FGEPoint(1, 1), grid.getNearestPoint(testPoint3));
		assertEquals(new FGEPoint(3, 1), grid.getNearestPoint(testPoint4));

		FGEGrid grid2 = new FGEGrid(new FGEPoint(0.5, 0.5), 0.25, 0.25);
		assertTrue(grid2.containsPoint(new FGEPoint(0, 0)));
		assertFalse(grid2.containsPoint(new FGEPoint(0.3, 0)));
		assertFalse(grid2.containsPoint(new FGEPoint(0, 0.3)));
		assertTrue(grid2.containsPoint(new FGEPoint(1, 0.5)));
		assertTrue(grid2.containsPoint(new FGEPoint(0.5, 1)));
		assertTrue(grid2.containsPoint(new FGEPoint(1, 1)));
		assertEquals(new FGEPoint(0, 0), grid2.getNearestPoint(testPoint1));
		assertEquals(new FGEPoint(1, 1), grid2.getNearestPoint(testPoint2));
		assertEquals(new FGEPoint(0.75, 0.75), grid2.getNearestPoint(testPoint3));
		assertEquals(new FGEPoint(2.75, 1.25), grid2.getNearestPoint(testPoint4));

		FGEGrid grid3 = new FGEGrid(new FGEPoint(0.3, 0.3), 0.4, 0.4);
		assertTrue(grid3.containsPoint(new FGEPoint(-0.1, -0.1)));
		assertTrue(grid3.containsPoint(new FGEPoint(0.3, 0.3)));
		assertTrue(grid3.containsPoint(new FGEPoint(0.7, 0.7)));
		assertTrue(grid3.containsPoint(new FGEPoint(0.3, 0.7)));
		assertTrue(grid3.containsPoint(new FGEPoint(0.3, 0.3)));
		assertEquals(new FGEPoint(-0.1, -0.1), grid3.getNearestPoint(testPoint1));
		assertEquals(new FGEPoint(1.1, 1.1), grid3.getNearestPoint(testPoint2));
		assertEquals(new FGEPoint(0.7, 0.7), grid3.getNearestPoint(testPoint3));
		assertEquals(new FGEPoint(2.7, 1.1), grid3.getNearestPoint(testPoint4));

	}

	@Test
	public void testPointOrientation() {
		FGEPoint topLeft = new FGEPoint(0, 0);
		FGEPoint topRight = new FGEPoint(1, 0);
		FGEPoint bottomLeft = new FGEPoint(0, 1);
		FGEPoint bottomRight = new FGEPoint(1, 1);
		FGEPoint top = new FGEPoint(0.5, 0);
		FGEPoint left = new FGEPoint(0, 0.5);
		FGEPoint bottom = new FGEPoint(0.5, 1);
		FGEPoint right = new FGEPoint(1, 0.5);
		FGEPoint center = new FGEPoint(0.5, 0.5);
		assertEquals(CardinalDirection.NORTH_WEST, FGEPoint.getOrientation(center, topLeft));
		assertEquals(CardinalDirection.NORTH_WEST, FGEPoint.getOrientation(center, new FGEPoint(topLeft.x + 0.125, topLeft.y)));
		assertEquals(CardinalDirection.NORTH_WEST, FGEPoint.getOrientation(center, new FGEPoint(topLeft.x, topLeft.y + 0.125)));
		assertEquals(CardinalDirection.NORTH, FGEPoint.getOrientation(center, top));
		assertEquals(CardinalDirection.NORTH, FGEPoint.getOrientation(center, new FGEPoint(top.x + 0.125, top.y)));
		assertEquals(CardinalDirection.NORTH, FGEPoint.getOrientation(center, new FGEPoint(top.x - 0.125, top.y)));
		assertEquals(CardinalDirection.NORTH_EAST, FGEPoint.getOrientation(center, topRight));
		assertEquals(CardinalDirection.NORTH_EAST, FGEPoint.getOrientation(center, new FGEPoint(topRight.x - 0.125, topRight.y)));
		assertEquals(CardinalDirection.NORTH_EAST, FGEPoint.getOrientation(center, new FGEPoint(topRight.x, topRight.y + 0.125)));
		assertEquals(CardinalDirection.EAST, FGEPoint.getOrientation(center, right));
		assertEquals(CardinalDirection.EAST, FGEPoint.getOrientation(center, new FGEPoint(right.x, right.y + 0.125)));
		assertEquals(CardinalDirection.EAST, FGEPoint.getOrientation(center, new FGEPoint(right.x, right.y - 0.125)));
		assertEquals(CardinalDirection.SOUTH_EAST, FGEPoint.getOrientation(center, bottomRight));
		assertEquals(CardinalDirection.SOUTH_EAST, FGEPoint.getOrientation(center, new FGEPoint(bottomRight.x - 0.125, bottomRight.y)));
		assertEquals(CardinalDirection.SOUTH_EAST, FGEPoint.getOrientation(center, new FGEPoint(bottomRight.x, bottomRight.y - 0.125)));
		assertEquals(CardinalDirection.SOUTH, FGEPoint.getOrientation(center, bottom));
		assertEquals(CardinalDirection.SOUTH, FGEPoint.getOrientation(center, new FGEPoint(bottom.x + 0.125, bottom.y)));
		assertEquals(CardinalDirection.SOUTH, FGEPoint.getOrientation(center, new FGEPoint(bottom.x - 0.125, bottom.y)));
		assertEquals(CardinalDirection.SOUTH_WEST, FGEPoint.getOrientation(center, bottomLeft));
		assertEquals(CardinalDirection.SOUTH_WEST, FGEPoint.getOrientation(center, new FGEPoint(bottomLeft.x - 0.125, bottomLeft.y)));
		assertEquals(CardinalDirection.SOUTH_WEST, FGEPoint.getOrientation(center, new FGEPoint(bottomLeft.x, bottomLeft.y + 0.125)));
		assertEquals(CardinalDirection.WEST, FGEPoint.getOrientation(center, left));
		assertEquals(CardinalDirection.WEST, FGEPoint.getOrientation(center, new FGEPoint(left.x, left.y + 0.125)));
		assertEquals(CardinalDirection.WEST, FGEPoint.getOrientation(center, new FGEPoint(left.x, left.y - 0.125)));

		/*assertEquals(CardinalDirection.EAST, FGEPoint.getOrientation(center, right));
		assertEquals(CardinalDirection.EAST, FGEPoint.getOrientation(center, right));
		assertEquals(CardinalDirection.EAST, FGEPoint.getOrientation(center, right));
		assertEquals(CardinalDirection.EAST, FGEPoint.getOrientation(center, right));*/
	}

	private void executeTest(Resource testResource) throws FileNotFoundException, Exception {

		System.out.println("geomedit test on " + testResource);

		if (testResource == null) {
			return;
		}

		DiagramEditingContext editingContext = new DiagramEditingContext();

		GeometricConstructionFactory factory = new GeometricConstructionFactory(editingContext);

		GeometricDiagram diagram = (GeometricDiagram) factory.deserialize(testResource.openInputStream());

		for (GeometricConstruction<?> geometricConstruction : diagram.getConstructions()) {
			if (geometricConstruction instanceof IntersectionConstruction) {
				IntersectionConstruction construction = (IntersectionConstruction) geometricConstruction;
				FGEArea o1 = construction.getObjectConstructions().get(0).getData();
				FGEArea o2 = construction.getObjectConstructions().get(1).getData();
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
				FGEArea o1 = construction.objectConstructions.get(0).getData();
				FGEArea o2 = construction.objectConstructions.get(1).getData();
				logger.fine("Checking intersection reversibility");
				assertEquals(o1.intersect(o2), o2.intersect(o1));
			}
		}*/
	}
}
