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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.DianaModelFactoryImpl;
import org.openflexo.diana.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.diana.GraphicalRepresentation.VerticalTextAlignment;
import org.openflexo.diana.PaletteElementSpecification;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.control.PaletteElement;
import org.openflexo.diana.geom.DianaGeneralShape;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.DianaShapeUnion;
import org.openflexo.diana.palettes.DianaPalettes.PaletteDefinition;
import org.openflexo.diana.palettes.DrawingMLPaletteGenerator.PresetElement;
import org.openflexo.diana.palettes.RenderPalettes.PreviewPalette;
import org.openflexo.diana.shapes.GeneralShape;
import org.openflexo.diana.shapes.ShapeSpecification;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

/**
 * Checks the palettes shipped by this module: each element is readable, builds a DIANA shape, and is up to date with
 * {@link DrawingMLPaletteGenerator}
 * 
 * @author sylvain
 */
public class TestDianaPalettes {

	private static DianaModelFactory factory;
	private static DrawingMLPaletteGenerator generator;

	@BeforeClass
	public static void setUp() throws Exception {
		factory = new DianaModelFactoryImpl();
		generator = new DrawingMLPaletteGenerator();
	}

	private static PaletteElementSpecification load(String paletteName, String fileName) throws Exception {
		Resource resource = ResourceLocator.locateResource("Palettes/" + paletteName + "/" + fileName + ".pel");
		assertNotNull("Palette element not found: " + paletteName + "/" + fileName, resource);
		return (PaletteElementSpecification) factory.deserialize(resource.openInputStream());
	}

	@Test
	public void testPaletteContents() throws Exception {
		for (Map.Entry<String, List<PresetElement>> palette : DrawingMLPaletteGenerator.palettes().entrySet()) {
			Resource directory = ResourceLocator.locateResource("Palettes/" + palette.getKey());
			assertNotNull("Palette not found: " + palette.getKey(), directory);
			List<String> files = new ArrayList<>();
			for (Resource r : directory.getContents()) {
				files.add(r.getURI());
			}
			assertEquals("Unexpected files in palette " + palette.getKey() + ": " + files, palette.getValue().size(), files.size());
		}
	}

	/**
	 * The Basic palette has a square and a circle: elements of a constrained shape, as wide as they are high
	 */
	@Test
	public void testBasicSquareAndCircle() throws Exception {
		PaletteElementSpecification square = load("Basic", "Square");
		assertEquals(ShapeType.SQUARE, square.getGraphicalRepresentation().getShapeSpecification().getShapeType());
		assertEquals(square.getGraphicalRepresentation().getWidth(), square.getGraphicalRepresentation().getHeight(), 0.001);
		PaletteElementSpecification circle = load("Basic", "Circle");
		assertEquals(ShapeType.CIRCLE, circle.getGraphicalRepresentation().getShapeSpecification().getShapeType());
		assertEquals(circle.getGraphicalRepresentation().getWidth(), circle.getGraphicalRepresentation().getHeight(), 0.001);
		// Right after the rounded rectangle, and right after the oval
		assertEquals((int) load("Basic", "RoundedRectangle").getIndex() + 1, (int) square.getIndex());
		assertEquals((int) load("Basic", "Oval").getIndex() + 1, (int) circle.getIndex());
	}

	@Test
	public void testElementsBuildShapes() throws Exception {
		for (Map.Entry<String, List<PresetElement>> palette : DrawingMLPaletteGenerator.palettes().entrySet()) {
			for (PresetElement element : palette.getValue()) {
				PaletteElementSpecification spec = load(palette.getKey(), element.fileName);
				assertEquals(element.name, spec.getName());
				assertFloatingLabel(element.fileName, spec.getGraphicalRepresentation(), element.floatingLabel);
				ShapeSpecification shape = spec.getGraphicalRepresentation().getShapeSpecification();
				assertNotNull(element.fileName, shape);
				DianaShape<?> dianaShape = shape.makeDianaShape(new DianaRectangle(0, 0, 1, 1));
				assertNotNull(element.fileName, dianaShape);
				Rectangle2D box = curveBounds(dianaShape);
				// Some DrawingML presets slightly overshoot their box (heart: 0.4%)
				double eps = 0.01;
				assertTrue(element.fileName + " is empty: " + box, box.getWidth() > 0.1 && box.getHeight() > 0.1);
				assertTrue(element.fileName + " exceeds unit square: " + box, box.getX() > -eps && box.getY() > -eps
						&& box.getX() + box.getWidth() < 1 + eps && box.getY() + box.getHeight() < 1 + eps);
			}
		}
	}

	/**
	 * Bounds of the curve itself: on Java 8, {@link java.awt.geom.Path2D#getBounds2D()} (thus
	 * {@link DianaShape#getBoundingBox()}) also encloses the Bezier control points
	 */
	private static Rectangle2D curveBounds(DianaShape<?> shape) {
		if (shape instanceof DianaShapeUnion) {
			Rectangle2D returned = null;
			for (DianaShape<?> s : ((DianaShapeUnion) shape).getShapes()) {
				Rectangle2D r = curveBounds(s);
				returned = returned == null ? r : returned.createUnion(r);
			}
			return returned;
		}
		if (shape instanceof DianaGeneralShape) {
			Path2D flat = new Path2D.Double();
			flat.append(((DianaGeneralShape<?>) shape).getGeneralPath().getPathIterator(null, 0.0001), false);
			return flat.getBounds2D();
		}
		DianaRectangle box = shape.getBoundingBox();
		return new Rectangle2D.Double(box.getX(), box.getY(), box.getWidth(), box.getHeight());
	}

	/**
	 * Committed .pel files must be those the generator produces (regenerate with gradle :diana-palettes:generatePalettes)
	 */
	@Test
	public void testElementsAreUpToDate() throws Exception {
		for (Map.Entry<String, List<PresetElement>> palette : DrawingMLPaletteGenerator.palettes().entrySet()) {
			int index = 0;
			for (PresetElement element : palette.getValue()) {
				PaletteElementSpecification loaded = load(palette.getKey(), element.fileName);
				PaletteElementSpecification generated = generator.makePaletteElement(element, index++);
				assertTrue(element.fileName + " is not up to date with its generator",
						generated.getGraphicalRepresentation().getShapeSpecification()
								.equalsObject(loaded.getGraphicalRepresentation().getShapeSpecification()));
				assertEquals(element.fileName, generated.getIndex(), loaded.getIndex());
			}
		}
	}

	/**
	 * A label floating above its shape, centered, when the generator asks for it; in the middle of the shape otherwise
	 */
	static void assertFloatingLabel(String name, ShapeGraphicalRepresentation gr, boolean floating) {
		assertEquals(name, floating, gr.getIsFloatingLabel());
		if (floating) {
			assertEquals(name, gr.getWidth() / 2, gr.getAbsoluteTextX(), 0.001);
			assertEquals(name, -PaletteLabels.FLOATING_LABEL_GAP, gr.getAbsoluteTextY(), 0.001);
			assertEquals(name, HorizontalTextAlignment.CENTER, gr.getHorizontalTextAlignment());
			assertEquals(name, VerticalTextAlignment.BOTTOM, gr.getVerticalTextAlignment());
		}
	}

	/**
	 * Each palette of the catalog is on the classpath, with elements
	 */
	@Test
	public void testCatalog() {
		for (PaletteDefinition palette : DianaPalettes.PALETTES) {
			Resource directory = ResourceLocator.locateResource(palette.getPath());
			assertNotNull(palette.getPath(), directory);
			assertTrue(palette.getPath() + " is empty", directory.getContents().size() > 0);
		}
	}

	/**
	 * Fitting an element in its palette cell moves the anchor of its floating label: still centered above it
	 */
	@Test
	public void testFloatingLabelInPalette() {
		PreviewPalette equations = new PreviewPalette("Equations");
		for (PaletteElement element : equations.getElements()) {
			// Elements of 100x100, in cells of 120x80
			assertEquals(80, element.getGraphicalRepresentation().getWidth(), 0.001);
			assertFloatingLabel(element.getName(), element.getGraphicalRepresentation(), true);
		}
	}

	/**
	 * Collate passes twice through the center: both segments must survive serialization
	 */
	@Test
	public void testRepeatedPathElements() throws Exception {
		GeneralShape collate = (GeneralShape) load("Flowchart", "Collate").getGraphicalRepresentation().getShapeSpecification();
		assertEquals(5, collate.getPathElements().size());
	}
}
