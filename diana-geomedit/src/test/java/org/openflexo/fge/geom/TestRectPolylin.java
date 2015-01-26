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

import java.util.logging.Logger;

import junit.framework.TestCase;

import org.openflexo.fge.geomedit.GeometricObject;
import org.openflexo.fge.geomedit.GeometricSet;
import org.openflexo.toolbox.FileResource;

public class TestRectPolylin extends TestCase {

	private static final Logger logger = Logger.getLogger(TestRectPolylin.class.getPackage().getName());

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testNorthNorth() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_NORTH_NORTH.drw"));
	}

	public void testEastEast() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_EAST_EAST.drw"));
	}

	public void testSouthSouth() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_SOUTH_SOUTH.drw"));
	}

	public void testWestWest() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_WEST_WEST.drw"));
	}

	public void testEastWest() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_EAST_WEST.drw"));
	}

	public void testNorthSouth() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_NORTH_SOUTH.drw"));
	}

	public void testNorthEast() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_NORTH_EAST.drw"));
	}

	public void testEastNorth() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_EAST_NORTH.drw"));
	}

	public void testSouthEast() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_SOUTH_EAST.drw"));
	}

	public void testEastSouth() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_EAST_SOUTH.drw"));
	}

	public void testNorthWest() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_NORTH_WEST.drw"));
	}

	public void testWestNorth() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_WEST_NORTH.drw"));
	}

	public void testSouthWest() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_SOUTH_WEST.drw"));
	}

	public void testWestSouth() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_WEST_SOUTH.drw"));
	}

	private void executeTest(FileResource testFile) {
		logger.info(">>>>>>> Test " + testFile.getName());
		GeometricSet geometricSet = GeometricSet.load(testFile);
		for (GeometricObject object : geometricSet.getChilds()) {
			logger.fine("Check equals: " + object.getResultingGeometricObject() + " / " + object.getGeometricObject());
			assertEquals(object.getResultingGeometricObject(), object.getGeometricObject());
		}
	}
}
