/**
 *
 * Copyright (c) 2026, Openflexo
 *
 * This file is part of Diana-palettes, a component of the software infrastructure
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

package org.openflexo.diana.palettes;

import static org.junit.Assert.assertArrayEquals;

import java.awt.image.BufferedImage;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openflexo.test.UITest;

/**
 * Checks that shapes made of a union cast the same shadow as simple shapes: along their real outline (not their bounding box), and
 * without stacking one translucent layer per shape of the union.<br>
 * Palette elements are rendered at 120x80, every 140 pixels horizontally and 110 pixels vertically, from (20,30).
 * 
 * @author sylvain
 */
public class TestPaletteShadows {

	private static BufferedImage flowchart;
	private static BufferedImage shapes3D;

	@BeforeClass
	public static void render() throws Exception {
		flowchart = RenderPalettes.render("Flowchart");
		shapes3D = RenderPalettes.render("Shapes3D");
	}

	private static int[] grayLevels(BufferedImage image, int x, int y, int dx, int dy) {
		int[] returned = new int[5];
		for (int i = 0; i < returned.length; i++) {
			int rgb = image.getRGB(x + i * dx, y + i * dy);
			returned[i] = ((rgb >> 16 & 0xFF) + (rgb >> 8 & 0xFF) + (rgb & 0xFF)) / 3;
		}
		return returned;
	}

	/**
	 * Along the lower right edge of a diamond: Decision is a single shape, Sort the same diamond plus a line
	 */
	@Test
	@Category(UITest.class)
	public void testShadowFollowsUnionOutline() {
		int[] decision = grayLevels(flowchart, 300 + 91, 30 + 61, 1, 1);
		int[] sort = grayLevels(flowchart, 580 + 91, 360 + 61, 1, 1);
		assertArrayEquals(decision, sort);
	}

	/**
	 * Under the bottom edge: Process is a single shape, Cube and Folded corner are unions of overlapping shapes
	 */
	@Test
	@Category(UITest.class)
	public void testUnionShadowIsNotDarker() {
		int[] process = grayLevels(flowchart, 20 + 40, 30 + 80, 0, 1);
		assertArrayEquals(process, grayLevels(shapes3D, 20 + 40, 30 + 80, 0, 1));
		assertArrayEquals(process, grayLevels(shapes3D, 440 + 40, 30 + 80, 0, 1));
	}
}
