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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Each preset is evaluated with its default adjust values in a reference box, then normalized to the unit square. A DrawingML path may
 * hold several sub-paths, whereas a {@link GeneralShape} has a single one: each path is translated into the following shapes, gathered
 * in a {@link ShapeUnion} when there are several of them:
 * <ul>
 * <li>a filled shape, when the path is filled. Its sub-paths are joined by zero-width back and forth segments, so that holes (frame,
 * donut) are kept by the non-zero winding rule, as Apache POI draws them. It is not stroked when the path has several sub-paths (the
 * joins would show) or when the path is not stroked (<code>stroke="false"</code>)</li>
 * <li>a translucent black (or white) overlay of that shape, when the path fill is <code>darken|darkenLess|lighten|lightenLess</code></li>
 * <li>a stroked-only shape per sub-path, when the path is stroked and not already stroked by its filled shape</li>
 * </ul>
 * Shapes are painted in DrawingML path order, a shape of a {@link ShapeUnion} overriding default styles for itself only.<br>
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

		// roundRect is the rounded rectangle of the Basic palette
		List<PresetElement> rectangles = new ArrayList<>();
		rectangles.add(element("round1Rect", "OneRoundedCorner", "One rounded corner"));
		rectangles.add(element("round2SameRect", "TwoRoundedCornersSameSide", "Two rounded corners, same side"));
		rectangles.add(element("round2DiagRect", "TwoRoundedCornersDiagonal", "Two rounded corners, diagonal"));
		rectangles.add(element("snip1Rect", "OneSnippedCorner", "One snipped corner"));
		rectangles.add(element("snip2SameRect", "TwoSnippedCornersSameSide", "Two snipped corners, same side"));
		rectangles.add(element("snip2DiagRect", "TwoSnippedCornersDiagonal", "Two snipped corners, diagonal"));
		rectangles.add(element("snipRoundRect", "SnippedAndRoundedCorners", "Snipped and rounded corners"));
		rectangles.add(element("plaque", "Plaque", "Plaque"));
		returned.put("Rectangles", rectangles);

		// Presets matching a native shape of the Basic palette are left out: rect, ellipse, triangle, diamond, parallelogram,
		// pentagon, hexagon, octagon, plus, chevron, pie and arc
		List<PresetElement> basicShapes = new ArrayList<>();
		basicShapes.add(element("rtTriangle", "RightTriangle", "Right triangle", 100, 100));
		basicShapes.add(element("trapezoid", "Trapezoid", "Trapezoid"));
		basicShapes.add(element("nonIsoscelesTrapezoid", "NonIsoscelesTrapezoid", "Non-isosceles trapezoid"));
		basicShapes.add(element("heptagon", "Heptagon", "Heptagon", 100, 100));
		basicShapes.add(element("decagon", "Decagon", "Decagon", 100, 100));
		basicShapes.add(element("dodecagon", "Dodecagon", "Dodecagon", 100, 100));
		basicShapes.add(element("chord", "Chord", "Chord", 100, 100));
		basicShapes.add(element("pieWedge", "PieWedge", "Pie wedge", 100, 100));
		basicShapes.add(element("blockArc", "BlockArc", "Block arc", 100, 100));
		basicShapes.add(element("teardrop", "Teardrop", "Teardrop", 100, 100));
		basicShapes.add(element("frame", "Frame", "Frame"));
		basicShapes.add(element("halfFrame", "HalfFrame", "Half frame", 100, 100));
		basicShapes.add(element("corner", "LShape", "L-shape", 100, 100));
		basicShapes.add(element("diagStripe", "DiagonalStripe", "Diagonal stripe", 100, 100));
		basicShapes.add(element("donut", "Donut", "Donut", 100, 100));
		basicShapes.add(element("noSmoking", "NoSymbol", "\"No\" symbol", 100, 100));
		basicShapes.add(element("smileyFace", "SmileyFace", "Smiley face", 100, 100));
		basicShapes.add(element("heart", "Heart", "Heart", 100, 100));
		basicShapes.add(element("lightningBolt", "LightningBolt", "Lightning bolt", 100, 100));
		basicShapes.add(element("sun", "Sun", "Sun", 100, 100));
		basicShapes.add(element("moon", "Moon", "Moon", 60, 100));
		basicShapes.add(element("cloud", "Cloud", "Cloud"));
		basicShapes.add(element("funnel", "Funnel", "Funnel", 100, 100));
		basicShapes.add(element("gear6", "Gear6", "Gear (6 teeth)", 100, 100));
		basicShapes.add(element("gear9", "Gear9", "Gear (9 teeth)", 100, 100));
		basicShapes.add(element("bracketPair", "DoubleBracket", "Double bracket"));
		basicShapes.add(element("bracePair", "DoubleBrace", "Double brace"));
		basicShapes.add(element("leftBracket", "LeftBracket", "Left bracket", 30, 100));
		basicShapes.add(element("rightBracket", "RightBracket", "Right bracket", 30, 100));
		basicShapes.add(element("leftBrace", "LeftBrace", "Left brace", 30, 100));
		basicShapes.add(element("rightBrace", "RightBrace", "Right brace", 30, 100));
		returned.put("BasicShapes", basicShapes);

		// chevron is in the Basic palette
		List<PresetElement> arrows = new ArrayList<>();
		arrows.add(element("rightArrow", "RightArrow", "Right arrow", 120, 60));
		arrows.add(element("leftArrow", "LeftArrow", "Left arrow", 120, 60));
		arrows.add(element("upArrow", "UpArrow", "Up arrow", 60, 120));
		arrows.add(element("downArrow", "DownArrow", "Down arrow", 60, 120));
		arrows.add(element("leftRightArrow", "LeftRightArrow", "Left-right arrow", 140, 60));
		arrows.add(element("upDownArrow", "UpDownArrow", "Up-down arrow", 60, 140));
		arrows.add(element("quadArrow", "QuadArrow", "Quad arrow", 100, 100));
		arrows.add(element("leftRightUpArrow", "LeftRightUpArrow", "Left-right-up arrow", 120, 80));
		arrows.add(element("bentArrow", "BentArrow", "Bent arrow", 100, 100));
		arrows.add(element("uturnArrow", "UTurnArrow", "U-turn arrow", 100, 100));
		arrows.add(element("leftUpArrow", "LeftUpArrow", "Left-up arrow", 100, 100));
		arrows.add(element("bentUpArrow", "BentUpArrow", "Bent-up arrow", 100, 100));
		arrows.add(element("curvedRightArrow", "CurvedRightArrow", "Curved right arrow", 60, 120));
		arrows.add(element("curvedLeftArrow", "CurvedLeftArrow", "Curved left arrow", 60, 120));
		arrows.add(element("curvedUpArrow", "CurvedUpArrow", "Curved up arrow", 120, 60));
		arrows.add(element("curvedDownArrow", "CurvedDownArrow", "Curved down arrow", 120, 60));
		arrows.add(element("stripedRightArrow", "StripedRightArrow", "Striped right arrow", 120, 60));
		arrows.add(element("notchedRightArrow", "NotchedRightArrow", "Notched right arrow", 120, 60));
		arrows.add(element("homePlate", "Pentagon", "Pentagon", 120, 60));
		arrows.add(element("rightArrowCallout", "RightArrowCallout", "Right arrow callout"));
		arrows.add(element("leftArrowCallout", "LeftArrowCallout", "Left arrow callout"));
		arrows.add(element("upArrowCallout", "UpArrowCallout", "Up arrow callout", 80, 120));
		arrows.add(element("downArrowCallout", "DownArrowCallout", "Down arrow callout", 80, 120));
		arrows.add(element("leftRightArrowCallout", "LeftRightArrowCallout", "Left-right arrow callout", 140, 80));
		arrows.add(element("upDownArrowCallout", "UpDownArrowCallout", "Up-down arrow callout", 80, 140));
		arrows.add(element("quadArrowCallout", "QuadArrowCallout", "Quad arrow callout", 100, 100));
		arrows.add(element("circularArrow", "CircularArrow", "Circular arrow", 100, 100));
		arrows.add(element("leftCircularArrow", "LeftCircularArrow", "Left circular arrow", 100, 100));
		arrows.add(element("leftRightCircularArrow", "LeftRightCircularArrow", "Left-right circular arrow", 100, 100));
		arrows.add(element("swooshArrow", "SwooshArrow", "Swoosh arrow"));
		returned.put("Arrows", arrows);

		List<PresetElement> starsAndBanners = new ArrayList<>();
		starsAndBanners.add(element("irregularSeal1", "Explosion1", "Explosion 1", 100, 100));
		starsAndBanners.add(element("irregularSeal2", "Explosion2", "Explosion 2", 100, 100));
		for (int n : new int[] { 4, 5, 6, 7, 8, 10, 12, 16, 24, 32 }) {
			starsAndBanners.add(element("star" + n, "Star" + n, n + "-point star", 100, 100));
		}
		starsAndBanners.add(element("ribbon2", "RibbonUp", "Ribbon: tilted up", 140, 70));
		starsAndBanners.add(element("ribbon", "RibbonDown", "Ribbon: tilted down", 140, 70));
		starsAndBanners.add(element("ellipseRibbon2", "CurvedRibbonUp", "Ribbon: curved and tilted up", 140, 70));
		starsAndBanners.add(element("ellipseRibbon", "CurvedRibbonDown", "Ribbon: curved and tilted down", 140, 70));
		starsAndBanners.add(element("leftRightRibbon", "LeftRightRibbon", "Ribbon: left-right", 140, 70));
		starsAndBanners.add(element("verticalScroll", "VerticalScroll", "Vertical scroll", 80, 120));
		starsAndBanners.add(element("horizontalScroll", "HorizontalScroll", "Horizontal scroll"));
		starsAndBanners.add(element("wave", "Wave", "Wave", 120, 70));
		starsAndBanners.add(element("doubleWave", "DoubleWave", "Double wave", 120, 70));
		returned.put("StarsAndBanners", starsAndBanners);

		List<PresetElement> equations = new ArrayList<>();
		equations.add(element("mathPlus", "Plus", "Plus", 100, 100));
		equations.add(element("mathMinus", "Minus", "Minus", 100, 100));
		equations.add(element("mathMultiply", "Multiply", "Multiply", 100, 100));
		equations.add(element("mathDivide", "Divide", "Division", 100, 100));
		equations.add(element("mathEqual", "Equal", "Equal", 100, 100));
		equations.add(element("mathNotEqual", "NotEqual", "Not equal", 100, 100));
		returned.put("Equations", equations);

		// Left out: line callouts (accentCallout*, borderCallout*, callout*), speech callouts (wedge*Callout, cloudCallout),
		// connectors and lines, action buttons, chart placeholders (chartPlus, chartStar, chartX) and tab corners (cornerTabs,
		// plaqueTabs, squareTabs)

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
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				generator.factory.serialize(spec, out);
				Files.write(pelFile.toPath(), sortAttributes(out.toString("UTF-8")).getBytes(StandardCharsets.UTF_8));
				System.out.println("Generated " + pelFile);
			}
		}
	}

	private static final Pattern ELEMENT = Pattern.compile("^(\\s*<[\\w:]+)((?:\\s+[\\w:]+=\"[^\"]*\")+)(\\s*/?>)$");
	private static final Pattern ATTRIBUTE = Pattern.compile("[\\w:]+=\"[^\"]*\"");

	/**
	 * PAMELA writes attributes in an order which changes from one run to the other: sort them (id first), so that regenerating
	 * palettes does not change files whose content did not change
	 */
	static String sortAttributes(String xml) {
		StringBuilder returned = new StringBuilder();
		for (String line : xml.split("\n", -1)) {
			Matcher element = ELEMENT.matcher(line);
			if (element.matches()) {
				List<String> attributes = new ArrayList<>();
				Matcher attribute = ATTRIBUTE.matcher(element.group(2));
				while (attribute.find()) {
					attributes.add(attribute.group());
				}
				attributes.sort(Comparator.comparing((String a) -> !a.startsWith("id=")).thenComparing(Comparator.naturalOrder()));
				line = element.group(1) + " " + String.join(" ", attributes) + element.group(3);
			}
			returned.append(line).append('\n');
		}
		// split() keeps a last empty line for the final line separator
		return returned.substring(0, returned.length() - 1);
	}

	PaletteElementSpecification makePaletteElement(PresetElement element, int index) {
		PaletteElementSpecification spec = factory.newInstance(PaletteElementSpecification.class);
		spec.setName(element.name);
		spec.setDescription("DrawingML preset geometry: " + element.preset);
		spec.setIndex(index);

		ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation(makeShapeSpecification(element));
		// The default identifier is an identity hash code, changing whenever the generator changes
		gr.setIdentifier(element.preset);
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

		List<ShapeSpecification> shapes = new ArrayList<>();
		for (Path path : geometry) {
			// Paths declaring their own coordinate space (w/h) are scaled to the anchor
			double w = path.getW() > 0 ? path.getW() : element.refWidth;
			double h = path.getH() > 0 ? path.getH() : element.refHeight;
			Context ctx = new Context(geometry, new Rectangle2D.Double(0, 0, w, h), name -> null);
			List<SubPath> subPaths = splitAndNormalize(path.getPath(ctx), w, h);
			if (subPaths.isEmpty()) {
				continue;
			}
			boolean strokedByFill = false;
			if (path.isFilled()) {
				PaintModifier fill = path.getFill();
				strokedByFill = subPaths.size() == 1 && path.isStroked() && fill == PaintModifier.NORM;
				DianaGeneralShape<?> area = subPaths.size() == 1 ? subPaths.get(0).makeShape(true) : joinSubPaths(subPaths);
				GeneralShape filled = factory.makeGeneralShape(area);
				if (!strokedByFill) {
					filled.setForeground(factory.makeNoneForegroundStyle());
				}
				shapes.add(filled);
				if (fill != PaintModifier.NORM) {
					GeneralShape overlay = factory.makeGeneralShape(area);
					overlay.setBackground(makeOverlay(fill));
					overlay.setForeground(factory.makeNoneForegroundStyle());
					shapes.add(overlay);
				}
			}
			if (path.isStroked() && !strokedByFill) {
				for (SubPath subPath : subPaths) {
					shapes.add(factory.makeGeneralShape(subPath.makeShape(false)));
				}
			}
		}

		if (shapes.size() == 1) {
			return shapes.get(0);
		}
		ShapeUnion union = factory.newInstance(ShapeUnion.class);
		for (ShapeSpecification shape : shapes) {
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
	 * A sub-path of a DrawingML path, normalized to the unit square
	 */
	private static class SubPath {
		private final DianaPoint start;
		// Each element: the end point, preceded by control points for curves
		private final List<DianaPoint[]> elements = new ArrayList<>();
		private boolean closed = false;

		SubPath(DianaPoint start) {
			this.start = start;
		}

		void appendTo(ShapeBuilder builder) {
			for (DianaPoint[] element : elements) {
				builder.add(element);
			}
		}

		DianaGeneralShape<?> makeShape(boolean filled) {
			Closure closure = filled ? (closed ? Closure.CLOSED_FILLED : Closure.OPEN_FILLED)
					: (closed ? Closure.CLOSED_NOT_FILLED : Closure.OPEN_NOT_FILLED);
			ShapeBuilder builder = new ShapeBuilder(start, closure);
			appendTo(builder);
			return builder.shape;
		}
	}

	/**
	 * Builds a {@link DianaGeneralShape}, skipping degenerate segments
	 */
	private static class ShapeBuilder {
		private final DianaGeneralShape<?> shape;
		private DianaPoint current;

		ShapeBuilder(DianaPoint start, Closure closure) {
			shape = new DianaGeneralShape<>(closure);
			shape.beginAtPoint(start);
			current = start;
		}

		void lineTo(DianaPoint p) {
			if (!p.equals(current)) {
				shape.addSegment(p);
				current = p;
			}
		}

		void add(DianaPoint[] element) {
			switch (element.length) {
				case 1:
					lineTo(element[0]);
					return;
				case 2:
					shape.addQuadCurve(element[0], element[1]);
					break;
				default:
					shape.addCubicCurve(element[0], element[1], element[2]);
			}
			current = element[element.length - 1];
		}
	}

	/**
	 * Joins sub-paths into a single filled shape: from the start of the first sub-path, go to each other sub-path, draw it, then come back
	 * by the same segment. Back and forth segments enclose no area, and inner sub-paths drawn the other way round remain holes
	 */
	private static DianaGeneralShape<?> joinSubPaths(List<SubPath> subPaths) {
		SubPath first = subPaths.get(0);
		ShapeBuilder builder = new ShapeBuilder(first.start, Closure.CLOSED_FILLED);
		first.appendTo(builder);
		builder.lineTo(first.start);
		for (SubPath subPath : subPaths.subList(1, subPaths.size())) {
			builder.lineTo(subPath.start);
			subPath.appendTo(builder);
			builder.lineTo(subPath.start);
			builder.lineTo(first.start);
		}
		return builder.shape;
	}

	/**
	 * Split an AWT path into its sub-paths, normalized to the unit square
	 */
	private static List<SubPath> splitAndNormalize(Path2D path, double w, double h) {
		List<SubPath> returned = new ArrayList<>();
		SubPath current = null;
		double[] c = new double[6];
		for (PathIterator it = path.getPathIterator(null); !it.isDone(); it.next()) {
			int type = it.currentSegment(c);
			if (type != PathIterator.SEG_MOVETO && type != PathIterator.SEG_CLOSE && hasNaN(c, type)) {
				// POI draws an arc of null radius (round2SameRect, round2DiagRect) as NaN coordinates: it does not move
				continue;
			}
			switch (type) {
				case PathIterator.SEG_MOVETO:
					current = new SubPath(point(c[0] / w, c[1] / h));
					returned.add(current);
					break;
				case PathIterator.SEG_LINETO:
					current.elements.add(new DianaPoint[] { point(c[0] / w, c[1] / h) });
					break;
				case PathIterator.SEG_QUADTO:
					current.elements.add(new DianaPoint[] { point(c[0] / w, c[1] / h), point(c[2] / w, c[3] / h) });
					break;
				case PathIterator.SEG_CUBICTO:
					current.elements.add(
							new DianaPoint[] { point(c[0] / w, c[1] / h), point(c[2] / w, c[3] / h), point(c[4] / w, c[5] / h) });
					break;
				case PathIterator.SEG_CLOSE:
					current.closed = true;
					break;
				default:
					break;
			}
		}
		// Sub-paths made of a single point draw nothing
		returned.removeIf(subPath -> subPath.elements.isEmpty());
		return returned;
	}

	/**
	 * Tells if the coordinates of a segment of supplied type hold a NaN (other slots of the array are left over by previous segments)
	 */
	private static boolean hasNaN(double[] coords, int segmentType) {
		int count = segmentType == PathIterator.SEG_CUBICTO ? 6 : segmentType == PathIterator.SEG_QUADTO ? 4 : 2;
		for (int i = 0; i < count; i++) {
			if (Double.isNaN(coords[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Rounds coordinates, to get rid of floating point noise in the serialized palette elements
	 */
	private static DianaPoint point(double x, double y) {
		if (Double.isNaN(x) || Double.isNaN(y)) {
			// Math.round() would silently make it 0
			throw new IllegalStateException("NaN coordinate");
		}
		return new DianaPoint(round(x), round(y));
	}

	private static double round(double value) {
		double returned = Math.round(value * PRECISION) / PRECISION;
		// Avoid -0.0
		return returned == 0 ? 0 : returned;
	}
}
