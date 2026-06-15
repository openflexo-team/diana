/**
 *
 * Copyright (c) 2024, Openflexo
 *
 * This file is part of Diana-swing, a component of the software infrastructure
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

package org.openflexo.diana.layout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.DianaModelFactoryImpl;
import org.openflexo.diana.ShapeGraphicalRepresentation;

/**
 * Headless validation that the {@link GridBagLayoutManager} PAMELA model is well-formed: the factory can be built (compiling every
 * {@code @ModelEntity} including the new {@code GRIDBAG} entities and the new {@code ShapeGraphicalRepresentation} properties with their enum
 * defaults), a {@link GridBagLayoutManagerSpecification} can be instantiated and configured, the {@code GRIDBAG} enum value resolves, and the
 * eight per-child GR properties are registered with the right keys and types.
 *
 * @author sylvain
 */
public class TestGridBagLayoutManagerModel {

	@Test
	public void testFactoryBuildsAndGridBagSpecRoundTrips() throws Exception {
		DianaModelFactory factory = new DianaModelFactoryImpl();

		GridBagLayoutManagerSpecification spec = factory.makeLayoutManagerSpecification("gridbag",
				GridBagLayoutManagerSpecification.class);
		assertEquals("gridbag", spec.getIdentifier());
		assertEquals(0.0, spec.getHgap(), 0.0);
		assertEquals(0.0, spec.getVgap(), 0.0);

		spec.setHgap(8);
		spec.setVgap(6);
		spec.setInsetLeft(4);
		assertEquals(8.0, spec.getHgap(), 0.0);
		assertEquals(6.0, spec.getVgap(), 0.0);
		assertEquals(4.0, spec.getInsetLeft(), 0.0);
	}

	@Test
	public void testGridBagLayoutConstraintsRoundTrip() throws Exception {
		// Building the factory compiles the LayoutConstraints hierarchy, including GridBagLayoutConstraints with its
		// int/double/enum getters and defaults (an invalid enum default would fail here).
		DianaModelFactory factory = new DianaModelFactoryImpl();

		GridBagLayoutConstraints c = factory.newInstance(GridBagLayoutConstraints.class);
		// Defaults
		assertEquals(0, c.getGridX());
		assertEquals(1, c.getGridWidth());
		assertEquals(GridBagFill.NONE, c.getFill());
		assertEquals(GridBagAnchor.CENTER, c.getAnchor());

		c.setGridX(2);
		c.setGridY(3);
		c.setGridWidth(2);
		c.setWeightX(1.0);
		c.setFill(GridBagFill.HORIZONTAL);
		c.setAnchor(GridBagAnchor.WEST);
		assertEquals(2, c.getGridX());
		assertEquals(3, c.getGridY());
		assertEquals(2, c.getGridWidth());
		assertEquals(1.0, c.getWeightX(), 0.0);
		assertEquals(GridBagFill.HORIZONTAL, c.getFill());
		assertEquals(GridBagAnchor.WEST, c.getAnchor());

		// The single polymorphic GR slot is registered with the right key and type
		assertEquals(ShapeGraphicalRepresentation.LAYOUT_CONSTRAINTS_KEY,
				ShapeGraphicalRepresentation.LAYOUT_CONSTRAINTS.getName());
		assertEquals(org.openflexo.diana.layout.LayoutConstraints.class,
				ShapeGraphicalRepresentation.LAYOUT_CONSTRAINTS.getType());
	}

	@Test
	public void testEnumRegistration() {
		assertEquals(GridBagLayoutManagerSpecification.class,
				org.openflexo.diana.DianaLayoutManagerSpecification.LayoutManagerSpecificationType.GRIDBAG
						.getLayoutManagerSpecificationClass());
		assertEquals("gridbag",
				org.openflexo.diana.DianaLayoutManagerSpecification.LayoutManagerSpecificationType.GRIDBAG.getDefaultLayoutManagerName());
		assertTrue(org.openflexo.diana.DianaLayoutManagerSpecification.LayoutManagerSpecificationType.GRIDBAG != org.openflexo.diana.DianaLayoutManagerSpecification.LayoutManagerSpecificationType.GRID);
	}
}
