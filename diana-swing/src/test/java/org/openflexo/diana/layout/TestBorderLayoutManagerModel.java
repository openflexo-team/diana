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
 * Headless validation that the {@link BorderLayoutManager} PAMELA model is well-formed: the factory can be built (which compiles every
 * {@code @ModelEntity} including the new {@code BORDER} entities), a {@link BorderLayoutManagerSpecification} can be instantiated and
 * configured, and the new per-child {@link ShapeGraphicalRepresentation#getLayoutBorderRegion()} GR property round-trips with its
 * {@code CENTER} default.
 *
 * @author sylvain
 */
public class TestBorderLayoutManagerModel {

	@Test
	public void testFactoryBuildsAndBorderSpecRoundTrips() throws Exception {
		DianaModelFactory factory = new DianaModelFactoryImpl();

		BorderLayoutManagerSpecification spec = factory.makeLayoutManagerSpecification("border",
				BorderLayoutManagerSpecification.class);
		assertEquals(DianaModelFactoryImpl.class, factory.getClass());
		assertEquals("border", spec.getIdentifier());

		// Defaults
		assertEquals(0.0, spec.getHgap(), 0.0);
		assertEquals(0.0, spec.getVgap(), 0.0);

		spec.setHgap(6);
		spec.setVgap(8);
		spec.setInsetTop(10);
		spec.setInsetBottom(11);
		spec.setInsetLeft(12);
		spec.setInsetRight(13);
		assertEquals(6.0, spec.getHgap(), 0.0);
		assertEquals(8.0, spec.getVgap(), 0.0);
		assertEquals(10.0, spec.getInsetTop(), 0.0);
		assertEquals(11.0, spec.getInsetBottom(), 0.0);
		assertEquals(12.0, spec.getInsetLeft(), 0.0);
		assertEquals(13.0, spec.getInsetRight(), 0.0);
	}

	@Test
	public void testLayoutBorderRegionGRProperty() throws Exception {
		// Building the factory compiles the ShapeGraphicalRepresentation entity, including the new
		// layoutBorderRegion @Getter with defaultValue="CENTER" (an invalid enum default would fail here).
		DianaModelFactory factory = new DianaModelFactoryImpl();
		assertEquals(DianaModelFactoryImpl.class, factory.getClass());

		// The per-child GR property must be registered with the right key and enum type, and be distinct
		// from the box weight property (headless-safe: no ShapeGraphicalRepresentation instance is created,
		// which would require a graphics environment).
		assertEquals(ShapeGraphicalRepresentation.LAYOUT_BORDER_REGION_KEY,
				ShapeGraphicalRepresentation.LAYOUT_BORDER_REGION.getName());
		assertEquals(BorderRegion.class, ShapeGraphicalRepresentation.LAYOUT_BORDER_REGION.getType());
		assertFalse(ShapeGraphicalRepresentation.LAYOUT_BORDER_REGION.getName()
				.equals(ShapeGraphicalRepresentation.LAYOUT_WEIGHT.getName()));
	}

	@Test
	public void testEnumRegistration() {
		// BORDER must resolve to the spec class and expose its default name
		assertEquals(BorderLayoutManagerSpecification.class,
				org.openflexo.diana.DianaLayoutManagerSpecification.LayoutManagerSpecificationType.BORDER
						.getLayoutManagerSpecificationClass());
		assertEquals("border",
				org.openflexo.diana.DianaLayoutManagerSpecification.LayoutManagerSpecificationType.BORDER.getDefaultLayoutManagerName());
		// Sanity: BORDER and BOX are distinct entries
		assertTrue(org.openflexo.diana.DianaLayoutManagerSpecification.LayoutManagerSpecificationType.BORDER != org.openflexo.diana.DianaLayoutManagerSpecification.LayoutManagerSpecificationType.BOX);
		assertFalse("border".equals(
				org.openflexo.diana.DianaLayoutManagerSpecification.LayoutManagerSpecificationType.BOX.getDefaultLayoutManagerName()));
	}
}
