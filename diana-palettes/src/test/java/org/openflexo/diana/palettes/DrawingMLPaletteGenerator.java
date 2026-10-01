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

import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.sl.draw.geom.Context;
import org.apache.poi.sl.draw.geom.CustomGeometry;
import org.apache.poi.sl.draw.geom.Path;
import org.apache.poi.sl.draw.geom.PresetGeometries;
import org.apache.poi.sl.usermodel.PaintStyle.PaintModifier;
import org.openflexo.diana.ColorBackgroundStyle;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.DianaModelFactoryImpl;
import org.openflexo.diana.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.diana.PaletteElementSpecification;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.diana.geom.DianaGeneralShape;
import org.openflexo.diana.geom.DianaGeneralShape.Closure;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.shapes.GeneralShape;
import org.openflexo.diana.shapes.ShapeSpecification;
import org.openflexo.diana.shapes.ShapeUnion;

/**
 * Generates DIANA palettes (one directory of <code>.pel</code> files per palette) from the DrawingML preset geometries (ECMA-376,
 * <code>presetShapeDefinitions.xml</code>) shipped with Apache POI.<br>
 *
 * Each preset is evaluated with its default adjust values in a reference box, then normalized to the unit square. Every sub-path becomes
 * a {@link GeneralShape}; a preset made of several sub-paths becomes a {@link ShapeUnion}.<br>
 * DrawingML path attributes are translated as follows:
 * <ul>
 * <li><code>fill="none"</code>: the sub-path is stroked only</li>
 * <li><code>fill="darken|darkenLess|lighten|lightenLess"</code>: the sub-path is painted with the shape background, then overlaid with a
 * translucent black (or white) fill</li>
 * <li><code>stroke="false"</code>: ignored, those sub-paths always follow the outline drawn by a stroked sub-path</li>
 * </ul>
 * Since a {@link ShapeUnion} sub-shape declaring a style keeps it as default for the next sub-shapes, sub-shapes are ordered as: plain
 * fills, then overlays, then stroke-only paths.<br>
 *
 * Adjust handles and text rectangles of DrawingML presets are not translated.<br>
 *
 * Run it through <code>gradle :diana-palettes:generatePalettes</code>; the generated files are committed.
 *
 * @author sylvain
 */
public class DrawingMLPaletteGenerator {

	/** Default reference box (DrawingML shapes depend on the aspect ratio through <code>ss</code>, the shortest side) */
	private static final double REF_WIDTH = 120;
	private static final double REF_HEIGHT = 80;

	private static final double PRECISION = 10000;

	private static final Color DEFAULT_BACKGROUND = new Color(254, 247, 217);

	/**
	 * A palette element to generate from a DrawingML preset
	 */
	static class PresetElement {
		final String preset;
		final String fileName;
		final String name;
		final double refWidth;
		final double refHeight;

		PresetElement(String preset, String fileName, String name, double refWidth, double refHeight) {
			this.preset = preset;
			this.fileName = fileName;
			this.name = name;
			this.refWidth = refWidth;
			this.refHeight = refHeight;
		}
	}

	private static PresetElement element(String preset, String fileName, String name) {
		return new PresetElement(preset, fileName, name, REF_WIDTH, REF_HEIGHT);
	}

	private static PresetElement element(String preset, String fileName, String name, double refWidth, double refHeight) {
		return new PresetElement(preset, fileName, name, refWidth, refHeight);
	}

	/**
	 * Palettes to generate, by directory name
	 */
	static Map<String, List<PresetElement>> palettes() {
		Map<String, List<PresetElement>> returned = new LinkedHashMap<>();

		List<PresetElement> flowchart = new ArrayList<>();
		flowchart.add(element("flowChartProcess", "Process", "Process"));
		flowchart.add(element("flowChartAlternateProcess", "AlternateProcess", "Alternate process"));
		flowchart.add(element("flowChartDecision", "Decision", "Decision"));
		flowchart.add(element("flowChartInputOutput", "Data", "Data"));
		flowchart.add(element("flowChartPredefinedProcess", "PredefinedProcess", "Predefined process"));
		flowchart.add(element("flowChartInternalStorage", "InternalStorage", "Internal storage", 80, 80));
		flowchart.add(element("flowChartDocument", "Document", "Document"));
		flowchart.add(element("flowChartMultidocument", "Multidocument", "Multidocument"));
		flowchart.add(element("flowChartTerminator", "Terminator", "Terminator", 120, 50));
		flowchart.add(element("flowChartPreparation", "Preparation", "Preparation"));
		flowchart.add(element("flowChartManualInput", "ManualInput", "Manual input"));
		flowchart.add(element("flowChartManualOperation", "ManualOperation", "Manual operation"));
		flowchart.add(element("flowChartConnector", "Connector", "Connector", 80, 80));
		flowchart.add(element("flowChartOffpageConnector", "OffpageConnector", "Off-page connector", 80, 80));
		flowchart.add(element("flowChartPunchedCard", "Card", "Card"));
		flowchart.add(element("flowChartPunchedTape", "PunchedTape", "Punched tape"));
		flowchart.add(element("flowChartSummingJunction", "SummingJunction", "Summing junction", 80, 80));
		flowchart.add(element("flowChartOr", "Or", "Or", 80, 80));
		flowchart.add(element("flowChartCollate", "Collate", "Collate", 80, 80));
		flowchart.add(element("flowChartSort", "Sort", "Sort", 80, 80));
		flowchart.add(element("flowChartExtract", "Extract", "Extract", 80, 80));
		flowchart.add(element("flowChartMerge", "Merge", "Merge", 80, 80));
		flowchart.add(element("flowChartOnlineStorage", "StoredData", "Stored data"));
		flowchart.add(element("flowChartDelay", "Delay", "Delay"));
		flowchart.add(element("flowChartMagneticTape", "SequentialAccessStorage", "Sequential access storage", 80, 80));
		flowchart.add(element("flowChartMagneticDisk", "MagneticDisk", "Magnetic disk", 80, 100));
		flowchart.add(element("flowChartMagneticDrum", "DirectAccessStorage", "Direct access storage"));
		flowchart.add(element("flowChartDisplay", "Display", "Display"));
		flowchart.add(element("flowChartOfflineStorage", "OfflineStorage", "Offline storage", 80, 80));
		returned.put("Flowchart", flowchart);

		List<PresetElement> shapes3D = new ArrayList<>();
		shapes3D.add(element("cube", "Cube", "Cube", 100, 100));
		shapes3D.add(element("can", "Cylinder", "Cylinder", 80, 110));
		shapes3D.add(element("bevel", "Bevel", "Bevel", 100, 100));
		shapes3D.add(element("foldedCorner", "FoldedCorner", "Folded corner", 100, 100));
		returned.put("Shapes3D", shapes3D);

		return returned;
	}

	private final DianaModelFactory factory;

	public DrawingMLPaletteGenerator() throws Exception {
		factory = new DianaModelFactoryImpl();
	}

	public static void main(String[] args) throws Exception {
		File palettesDirectory = new File(args[0]);
		DrawingMLPaletteGenerator generator = new DrawingMLPaletteGenerator();
		for (Map.Entry<String, List<PresetElement>> palette : palettes().entrySet()) {
			File paletteDirectory = new File(palettesDirectory, palette.getKey());
			paletteDirectory.mkdirs();
			int index = 0;
			for (PresetElement element : palette.getValue()) {
				PaletteElementSpecification spec = generator.makePaletteElement(element, index++);
				File pelFile = new File(paletteDirectory, element.fileName + ".pel");
				try (OutputStream out = new FileOutputStream(pelFile)) {
					generator.factory.serialize(spec, out);
				}
				System.out.println("Generated " + pelFile);
			}
		}
	}

	PaletteElementSpecification makePaletteElement(PresetElement element, int index) {
		PaletteElementSpecification spec = factory.newInstance(PaletteElementSpecification.class);
		spec.setName(element.name);
		spec.setDescription("DrawingML preset geometry: " + element.preset);
		spec.setIndex(index);

		ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation(makeShapeSpecification(element));
		gr.setForeground(factory.makeDefaultForegroundStyle());
		gr.setBackground(factory.makeColoredBackground(DEFAULT_BACKGROUND));
		gr.setTextStyle(factory.makeDefaultTextStyle());
		gr.setIsFloatingLabel(false);
		gr.setRelativeTextX(0.5);
		gr.setRelativeTextY(0.5);
		gr.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
		gr.setDimensionConstraints(DimensionConstraints.FREELY_RESIZABLE);
		gr.setWidth(element.refWidth);
		gr.setHeight(element.refHeight);
		spec.setGraphicalRepresentation(gr);
		return spec;
	}

	ShapeSpecification makeShapeSpecification(PresetElement element) {
		CustomGeometry geometry = PresetGeometries.getInstance().get(element.preset);
		if (geometry == null) {
			throw new IllegalArgumentException("Unknown DrawingML preset: " + element.preset);
		}

		List<ShapeSpecification> fills = new ArrayList<>();
		List<ShapeSpecification> overlays = new ArrayList<>();
		List<ShapeSpecification> strokes = new ArrayList<>();

		for (Path path : geometry) {
			// Paths declaring their own coordinate space (w/h) are scaled to the anchor
			double w = path.getW() > 0 ? path.getW() : element.refWidth;
			double h = path.getH() > 0 ? path.getH() : element.refHeight;
			Context ctx = new Context(geometry, new Rectangle2D.Double(0, 0, w, h), name -> null);
			Path2D.Double awtPath = path.getPath(ctx);
			for (DianaGeneralShape<?> subPath : splitAndNormalize(awtPath, w, h, path.isFilled())) {
				PaintModifier fill = path.getFill();
				if (!path.isFilled()) {
					strokes.add(factory.makeGeneralShape(subPath));
				}
				else if (fill == PaintModifier.NORM) {
					fills.add(factory.makeGeneralShape(subPath));
				}
				else {
					fills.add(factory.makeGeneralShape(subPath));
					GeneralShape overlay = factory.makeGeneralShape(subPath);
					overlay.setBackground(makeOverlay(fill));
					overlays.add(overlay);
				}
			}
		}

		List<ShapeSpecification> all = new ArrayList<>();
		all.addAll(fills);
		all.addAll(overlays);
		all.addAll(strokes);
		if (all.size() == 1) {
			return all.get(0);
		}
		ShapeUnion union = factory.newInstance(ShapeUnion.class);
		for (ShapeSpecification shape : all) {
			union.addToShapes(shape);
		}
		return union;
	}

	private ColorBackgroundStyle makeOverlay(PaintModifier modifier) {
		ColorBackgroundStyle returned;
		switch (modifier) {
			case DARKEN:
				returned = factory.makeColoredBackground(Color.BLACK);
				returned.setTransparencyLevel(0.4f);
				break;
			case DARKEN_LESS:
				returned = factory.makeColoredBackground(Color.BLACK);
				returned.setTransparencyLevel(0.2f);
				break;
			case LIGHTEN:
				returned = factory.makeColoredBackground(Color.WHITE);
				returned.setTransparencyLevel(0.4f);
				break;
			case LIGHTEN_LESS:
				returned = factory.makeColoredBackground(Color.WHITE);
				returned.setTransparencyLevel(0.2f);
				break;
			default:
				throw new IllegalArgumentException("Unexpected fill modifier " + modifier);
		}
		returned.setUseTransparency(true);
		return returned;
	}

	/**
	 * Split an AWT path into its sub-paths (a {@link DianaGeneralShape} has a single start point), normalized to the unit square
	 */
	private static List<DianaGeneralShape<?>> splitAndNormalize(Path2D path, double w, double h, boolean filled) {
		List<DianaGeneralShape<?>> returned = new ArrayList<>();
		DianaGeneralShape<?> current = null;
		DianaPoint currentPoint = null;
		double[] c = new double[6];
		for (PathIterator it = path.getPathIterator(null); !it.isDone(); it.next()) {
			int type = it.currentSegment(c);
			switch (type) {
				case PathIterator.SEG_MOVETO:
					if (current != null && !current.getPathElements().isEmpty()) {
						returned.add(current);
					}
					current = new DianaGeneralShape<>(filled ? Closure.OPEN_FILLED : Closure.OPEN_NOT_FILLED);
					currentPoint = point(c[0] / w, c[1] / h);
					current.beginAtPoint(currentPoint);
					break;
				case PathIterator.SEG_LINETO:
					DianaPoint p = point(c[0] / w, c[1] / h);
					// Skip degenerate segments
					if (!p.equals(currentPoint)) {
						current.addSegment(p);
						currentPoint = p;
					}
					break;
				case PathIterator.SEG_QUADTO:
					currentPoint = point(c[2] / w, c[3] / h);
					current.addQuadCurve(point(c[0] / w, c[1] / h), currentPoint);
					break;
				case PathIterator.SEG_CUBICTO:
					currentPoint = point(c[4] / w, c[5] / h);
					current.addCubicCurve(point(c[0] / w, c[1] / h), point(c[2] / w, c[3] / h), currentPoint);
					break;
				case PathIterator.SEG_CLOSE:
					current.setClosure(filled ? Closure.CLOSED_FILLED : Closure.CLOSED_NOT_FILLED);
					break;
				default:
					break;
			}
		}
		if (current != null && !current.getPathElements().isEmpty()) {
			returned.add(current);
		}
		return returned;
	}

	/**
	 * Rounds coordinates, to get rid of floating point noise in the serialized palette elements
	 */
	private static DianaPoint point(double x, double y) {
		return new DianaPoint(round(x), round(y));
	}

	private static double round(double value) {
		double returned = Math.round(value * PRECISION) / PRECISION;
		// Avoid -0.0
		return returned == 0 ? 0 : returned;
	}
}
