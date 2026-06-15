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
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.DianaModelFactoryImpl;
import org.openflexo.diana.layout.WrapFlowLayoutManagerSpecification.LineAlignment;
import org.openflexo.diana.layout.WrapFlowLayoutManagerSpecification.Orientation;

/**
 * Headless validation that the {@link WrapFlowLayoutManager} PAMELA model is well-formed: the factory can be built (compiling the new
 * {@code WRAP_FLOW} entities incl. the enum defaults), a {@link WrapFlowLayoutManagerSpecification} can be instantiated and configured, and
 * the {@code WRAP_FLOW} enum value resolves. WrapFlow carries no per-child data, so there is nothing to assert on
 * {@code ShapeGraphicalRepresentation}.
 *
 * @author sylvain
 */
public class TestWrapFlowLayoutManagerModel {

	@Test
	public void testFactoryBuildsAndWrapFlowSpecRoundTrips() throws Exception {
		DianaModelFactory factory = new DianaModelFactoryImpl();

		WrapFlowLayoutManagerSpecification spec = factory.makeLayoutManagerSpecification("wrap-flow",
				WrapFlowLayoutManagerSpecification.class);
		assertEquals("wrap-flow", spec.getIdentifier());
		// Defaults
		assertEquals(Orientation.HORIZONTAL, spec.getOrientation());
		assertEquals(LineAlignment.LEADING, spec.getLineAlignment());
		assertEquals(5.0, spec.getHgap(), 0.0);
		assertEquals(5.0, spec.getVgap(), 0.0);

		spec.setOrientation(Orientation.VERTICAL);
		spec.setLineAlignment(LineAlignment.CENTER);
		spec.setHgap(8);
		spec.setInsetLeft(4);
		assertEquals(Orientation.VERTICAL, spec.getOrientation());
		assertEquals(LineAlignment.CENTER, spec.getLineAlignment());
		assertEquals(8.0, spec.getHgap(), 0.0);
		assertEquals(4.0, spec.getInsetLeft(), 0.0);
	}

	@Test
	public void testEnumRegistration() {
		assertEquals(WrapFlowLayoutManagerSpecification.class,
				org.openflexo.diana.DianaLayoutManagerSpecification.LayoutManagerSpecificationType.WRAP_FLOW
						.getLayoutManagerSpecificationClass());
		assertEquals("wrap-flow",
				org.openflexo.diana.DianaLayoutManagerSpecification.LayoutManagerSpecificationType.WRAP_FLOW.getDefaultLayoutManagerName());
		assertTrue(org.openflexo.diana.DianaLayoutManagerSpecification.LayoutManagerSpecificationType.WRAP_FLOW != org.openflexo.diana.DianaLayoutManagerSpecification.LayoutManagerSpecificationType.FLOW);
	}
}
