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
import static org.junit.Assert.assertTrue;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.control.PaletteElement;
import org.openflexo.diana.control.PaletteModel;
import org.openflexo.diana.palettes.RenderPalettes.PreviewPalette;
import org.openflexo.test.UITest;

/**
 * Checks that shapes made of a union cast the same shadow as simple shapes: along their real outline (not their bounding box), and
 * without stacking one translucent layer per shape of the union.<br>
 * 
 * @author sylvain
 */
public class TestPaletteShadows {

	private static PreviewPalette flowchart;
	private static PreviewPalette shapes3D;
	private static BufferedImage flowchartImage;
	private static BufferedImage shapes3DImage;

	@BeforeClass
	public static void render() throws Exception {
		flowchart = new PreviewPalette("Flowchart");
		flowchartImage = RenderPalettes.render(flowchart);
		shapes3D = new PreviewPalette("Shapes3D");
		shapes3DImage = RenderPalettes.render(shapes3D);
	}

	/**
	 * Bounds of the element of supplied name, in palette (and image) coordinates
	 */
	private static Rectangle2D bounds(PaletteModel palette, String name) {
		for (PaletteElement element : palette.getElements()) {
			if (element.getName().equals(name)) {
				ShapeGraphicalRepresentation gr = element.getGraphicalRepresentation();
				return new Rectangle2D.Double(gr.getX(), gr.getY(), gr.getWidth(), gr.getHeight());
			}
		}
		throw new AssertionError("No element " + name);
	}

	private static int[] grayLevels(BufferedImage image, double x, double y, int dx, int dy) {
		int[] returned = new int[5];
		for (int i = 0; i < returned.length; i++) {
			int rgb = image.getRGB((int) Math.round(x) + i * dx, (int) Math.round(y) + i * dy);
			returned[i] = ((rgb >> 16 & 0xFF) + (rgb >> 8 & 0xFF) + (rgb & 0xFF)) / 3;
		}
		return returned;
	}

	/**
	 * Just outside the lower right edge of Sort, a diamond plus a line: the shadow is there (it used to be clipped by the bounding box
	 * of the union)
	 */
	@Test
	@Category(UITest.class)
	public void testShadowFollowsUnionOutline() {
		Rectangle2D sort = bounds(flowchart, "Sort");
		int[] levels = grayLevels(flowchartImage, sort.getX() + sort.getWidth() * 0.75 + 1, sort.getY() + sort.getHeight() * 0.75 + 1, 1,
				1);
		assertTrue("No shadow along Sort edge: " + Arrays.toString(levels), Arrays.stream(levels).min().getAsInt() < 230);
	}

	/**
	 * Under the bottom edge: Process is a single shape, Cube and Folded corner are unions of overlapping shapes
	 */
	@Test
	@Category(UITest.class)
	public void testUnionShadowIsNotDarker() {
		int[] process = underBottomEdge(flowchartImage, bounds(flowchart, "Process"));
		assertArrayEquals(process, underBottomEdge(shapes3DImage, bounds(shapes3D, "Cube")));
		assertArrayEquals(process, underBottomEdge(shapes3DImage, bounds(shapes3D, "Folded corner")));
	}

	private static int[] underBottomEdge(BufferedImage image, Rectangle2D bounds) {
		return grayLevels(image, bounds.getX() + bounds.getWidth() * 0.25, bounds.getY() + bounds.getHeight(), 0, 1);
	}
}
