/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-api, a component of the software infrastructure 
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

package org.openflexo.diana;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.connie.DataBinding;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.converter.PointConverter;
import org.openflexo.diana.converter.RectPolylinConverter;
import org.openflexo.diana.converter.SteppedDimensionConverter;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectPolylin;
import org.openflexo.diana.geom.DianaSteppedDimensionConstraint;
import org.openflexo.pamela.StringConverterLibrary.Converter;
import org.openflexo.pamela.annotations.StringConverter;
import org.openflexo.pamela.converter.DataBindingConverter;
import org.openflexo.uicst.ColorUtils;

public class DianaUtils {

	static final Logger LOGGER = Logger.getLogger(DianaUtils.class.getPackage().getName());

	public static final Color NICE_RED = new Color(255, 153, 153);
	public static final Color NICE_BLUE = new Color(153, 153, 255);
	public static final Color NICE_YELLOW = new Color(255, 255, 153);
	public static final Color NICE_PINK = new Color(255, 204, 255);
	public static final Color NICE_GREEN = new Color(153, 255, 153);
	public static final Color NICE_TURQUOISE = new Color(153, 255, 255);
	public static final Color NICE_ORANGE = new Color(255, 204, 102);

	public static final Color NICE_DARK_GREEN = new Color(0, 153, 51);
	public static final Color NICE_BROWN = new Color(186, 112, 0);
	public static final Color NICE_BORDEAU = new Color(153, 0, 51);

	/**
	 * Returns the color that has the best contrast ratio with the oppositeColor. The algorthm used is the one considered by the W3C for
	 * WCAG 2.0.
	 * 
	 * @param oppositeColor
	 *            - the opposite color to consider
	 * @param colors
	 *            - all colors amongst which to choose.
	 * @return the color the best contrast ratio compared to opposite color. See
	 *         http://www.w3.org/TR/2007/WD-WCAG20-TECHS-20070517/Overview.html#G18
	 */
	public static Color chooseBestColor(Color oppositeColor, Color... colors) {
		// int bestContrast = Integer.MIN_VALUE;
		double contrastRatio = 0;
		Color returned = null;
		for (Color c : colors) {
			/*
			 * int colorConstrastDiff = getColorConstrastDiff(oppositeColor,c); int colorBrightnessDiff =
			 * getColorBrightnessDiff(oppositeColor,c); int colorContrast = colorConstrastDiff+colorBrightnessDiff; if (colorContrast >
			 * bestContrast) { bestContrast = colorContrast; returned = c; }
			 */
			double d = ColorUtils.getContrastRatio(oppositeColor, c);
			if (d > contrastRatio) {
				contrastRatio = d;
				returned = c;
			}
		}
		return returned;
	}

	public static Color emphasizedColor(Color c) {
		if (c == null) {
			return c;
		}
		double l = ColorUtils.getRelativeLuminance(c);
		Color test = c, best = c;
		double ratio = 0, bestRatio = -1;
		int count = -1;
		if (l > 0.5) {
			do {
				test = test.darker();
				ratio = ColorUtils.getContrastRatio(test, c);
				if (ratio > bestRatio) {
					bestRatio = ratio;
					best = test;
				}
				count++;
			} while (ratio < 5 && count < 10);
		}
		else {
			test = new Color(test.getRed() == 0 ? 5 : test.getRed(), test.getGreen() == 0 ? 5 : test.getGreen(),
					test.getBlue() == 0 ? 5 : test.getBlue());
			do {
				test = test.brighter();
				ratio = ColorUtils.getContrastRatio(test, c);
				if (ratio > bestRatio) {
					bestRatio = ratio;
					best = test;
				}
				count++;
			} while (ratio < 5 && count < 10);
		}
		return best;
	}

	public static final Color mergeColors(Color color1, Color color2) {
		return new Color((color1.getRed() + color2.getRed()) / 2, (color1.getGreen() + color2.getGreen()) / 2,
				(color1.getBlue() + color2.getBlue()) / 2);
	}

	public static void main(String[] args) {
		Color yellow = new Color(255, 255, 204);
		Color green = new Color(153, 255, 204);
		Color grey = new Color(192, 192, 192);
		System.err.println(emphasizedColor(yellow));
		System.err.println(emphasizedColor(green));
		System.err.println(emphasizedColor(grey));
	}

	public static DrawingTreeNode<?, ?> getFirstCommonAncestor(List<DrawingTreeNode<?, ?>> nodes) {
		if (nodes.size() == 0) {
			return null;
		}
		if (nodes.size() == 1) {
			return nodes.get(0).getParentNode();
		}
		DrawingTreeNode<?, ?> returned = getFirstCommonAncestor(nodes.get(0), nodes.get(1));
		for (int i = 2; i < nodes.size(); i++) {
			returned = getFirstCommonAncestor(returned, nodes.get(i), true);
		}

		return returned;
	}

	public static DrawingTreeNode<?, ?> getFirstCommonAncestor(DrawingTreeNode<?, ?> child1, DrawingTreeNode<?, ?> child2) {
		if (!child1.isValid()) {
			return null;
		}
		if (!child2.isValid()) {
			return null;
		}
		return getFirstCommonAncestor(child1, child2, false);
	}

	public static DrawingTreeNode<?, ?> getFirstCommonAncestor(DrawingTreeNode<?, ?> child1, DrawingTreeNode<?, ?> child2,
			boolean includeCurrent) {
		if (!child1.isValid()) {
			return null;
		}
		if (!child2.isValid()) {
			return null;
		}
		List<DrawingTreeNode<?, ?>> ancestors1 = child1.getAncestors();
		if (includeCurrent) {
			ancestors1.add(0, child1);
		}
		List<DrawingTreeNode<?, ?>> ancestors2 = child2.getAncestors();
		if (includeCurrent) {
			ancestors2.add(0, child2);
		}
		for (int i = 0; i < ancestors1.size(); i++) {
			DrawingTreeNode<?, ?> o1 = ancestors1.get(i);
			if (ancestors2.contains(o1)) {
				return o1;
			}
		}
		return null;
	}

	public static boolean areElementsConnectedInGraphicalHierarchy(DrawingTreeNode<?, ?> element1, DrawingTreeNode<?, ?> element2) {
		if (!element1.isValid()) {
			return false;
		}
		if (!element2.isValid()) {
			return false;
		}
		return getFirstCommonAncestor(element1, element2) != null;
	}

	@StringConverter
	public static final Converter<DataBinding<?>> DATA_BINDING_CONVERTER = new DataBindingConverter();

	@StringConverter
	public static final Converter<DianaPoint> POINT_CONVERTER = new PointConverter(DianaPoint.class);

	@StringConverter
	public static final Converter<DianaRectPolylin> RECT_POLYLIN_CONVERTER = new RectPolylinConverter(DianaRectPolylin.class);

	@StringConverter
	public static final Converter<DianaSteppedDimensionConstraint> STEPPED_DIMENSION_CONVERTER = new SteppedDimensionConverter(
			DianaSteppedDimensionConstraint.class);

	/**
	 * Convert a point relative to the view representing source drawable, with supplied scale, in a point relative to the view representing
	 * destination drawable
	 * 
	 * @param source
	 *            graphical representation of drawable represented in the source view
	 * @param point
	 *            point to convert
	 * @param destination
	 *            graphical representation of drawable represented in the destination view
	 * @param scale
	 *            the scale to be used to perform this conversion
	 * @return
	 */
	public static Point convertPoint(DrawingTreeNode<?, ?> source, Point point, DrawingTreeNode<?, ?> destination, double scale) {
		if (source != destination) {
			AffineTransform at = convertCoordinatesAT(source, destination, scale);
			return (Point) at.transform(point, new Point());
		}
		return new Point(point);
	}

	/**
	 * Convert a rectangle coordinates expressed in the view representing source drawable, with supplied scale, in coordinates expressed in
	 * the view representing destination drawable
	 * 
	 * @param source
	 *            graphical representation of drawable represented in the source view
	 * @param aRectangle
	 *            rectangle to convert
	 * @param destination
	 *            graphical representation of drawable represented in the destination view
	 * @param scale
	 *            the scale to be used to perform this conversion
	 * @return
	 */
	public static Rectangle convertRectangle(DrawingTreeNode<?, ?> source, Rectangle aRectangle, DrawingTreeNode<?, ?> destination,
			double scale) {
		Point point = new Point(aRectangle.x, aRectangle.y);
		if (source != destination) {
			point = convertPoint(source, point, destination, scale);
		}
		return new Rectangle(point.x, point.y, aRectangle.width, aRectangle.height);
	}

	/**
	 * Build and return a new AffineTransform allowing to perform coordinates conversion from the view representing source drawable, with
	 * supplied scale, to the view representing destination drawable
	 * 
	 * @param source
	 * @param destination
	 * @param scale
	 * @return
	 */
	public static AffineTransform convertCoordinatesAT(DrawingTreeNode<?, ?> source, DrawingTreeNode<?, ?> destination, double scale) {
		if (source != destination) {
			AffineTransform returned = convertFromDrawableToDrawingAT(source, scale);
			returned.preConcatenate(convertFromDrawingToDrawableAT(destination, scale));
			return returned;
		}
		return new AffineTransform();
	}

	/**
	 * Convert a point defined in coordinates system related to "source" graphical representation to related drawing graphical
	 * representation
	 * 
	 * @param destination
	 * @param point
	 * @param scale
	 * @return
	 */
	public static Point convertPointFromDrawableToDrawing(DrawingTreeNode<?, ?> source, Point point, double scale) {
		AffineTransform at = convertFromDrawableToDrawingAT(source, scale);
		return (Point) at.transform(point, new Point());
	}

	/**
	 * 
	 * Build a new AffineTransform allowing to convert coordinates from coordinate system defined by "source" graphical representation to
	 * related drawing graphical representation
	 * 
	 * @param source
	 * @param scale
	 * @return
	 */
	public static AffineTransform convertFromDrawableToDrawingAT(DrawingTreeNode<?, ?> source, double scale) {
		double tx = 0;
		double ty = 0;
		if (source == null) {
			LOGGER.warning("Called convertFromDrawableToDrawingAT() for null graphical representation (source)");
			return new AffineTransform();
		}

		DrawingTreeNode<?, ?> current = source;

		while (current != source.getDrawing().getRoot()) {
			if (current.getGraphicalRepresentation() == null) {
				throw new IllegalArgumentException("DrawingTreeNode " + current + " has no graphical representation. (source=" + source
						+ ")\nDevelopper note: Use DianaUtils.areElementsConnectedInGraphicalHierarchy(DrawingTreeNode<?,?>,DrawingTreeNode<?,?>) to prevent such cases.");
			}
			if (current.getParentNode() == null) {
				throw new IllegalArgumentException("DrawingTreeNode " + current
						+ " has no container.\nDevelopper note: Use DianaUtils.areElementsConnectedInGraphicalHierarchy(DrawingTreeNode<?,?>,DrawingTreeNode<?,?>) to prevent such cases.");
			}
			tx += current.getViewX(scale);
			ty += current.getViewY(scale);
			current = current.getParentNode();
		}
		return AffineTransform.getTranslateInstance(tx, ty);
	}

	/**
	 * Convert a point defined in related drawing graphical representation coordinates system to the one defined by "destination" graphical
	 * representation
	 * 
	 * @param destination
	 * @param point
	 * @param scale
	 * @return
	 */
	public static Point convertPointFromDrawingToDrawable(DrawingTreeNode<?, ?> destination, Point point, double scale) {
		AffineTransform at = convertFromDrawingToDrawableAT(destination, scale);
		return (Point) at.transform(point, new Point());
	}

	/**
	 * 
	 * Build a new AffineTransform allowing to convert coordinates from coordinate system defined by related drawing graphical
	 * representation to the one defined by "destination" graphical representation
	 * 
	 * @param destination
	 * @param scale
	 * @return
	 */
	public static AffineTransform convertFromDrawingToDrawableAT(DrawingTreeNode<?, ?> destination, double scale) {
		double tx = 0;
		double ty = 0;
		if (destination == null) {
			LOGGER.warning("Called convertFromDrawingToDrawableAT() for null graphical representation (destination)");
			return new AffineTransform();
		}
		DrawingTreeNode<?, ?> current = destination;
		while (current != destination.getDrawing().getRoot()) {
			if (current.getParentNode() == null) {
				throw new IllegalArgumentException("DrawingTreeNode " + current
						+ " has no container.\nDevelopper note: Use GraphicalRepresentation.areElementsConnectedInGraphicalHierarchy(GraphicalRepresentation,GraphicalRepresentation) to prevent such cases.");
			}
			tx -= current.getViewX(scale);
			ty -= current.getViewY(scale);
			current = current.getParentNode();
		}

		return AffineTransform.getTranslateInstance(tx, ty);
	}

	/**
	 * Convert a point relative to the normalized coordinates system from source drawable to the normalized coordinates system from
	 * destination drawable
	 * 
	 * @param source
	 * @param point
	 * @param destination
	 * @return
	 */
	public static DianaPoint convertNormalizedPoint(DrawingTreeNode<?, ?> source, DianaPoint point, DrawingTreeNode<?, ?> destination) {
		if (point == null) {
			return null;
		}
		AffineTransform at = convertNormalizedCoordinatesAT(source, destination);
		return (DianaPoint) at.transform(point, new DianaPoint());
	}

	/**
	 * Build and return an AffineTransform allowing to convert locations relative to the normalized coordinates system from source drawable
	 * to the normalized coordinates system from destination drawable
	 * 
	 * @param source
	 * @param point
	 * @param destination
	 * @return
	 */
	public static AffineTransform convertNormalizedCoordinatesAT(DrawingTreeNode<?, ?> source, DrawingTreeNode<?, ?> destination) {
		if (source == null) {
			LOGGER.warning("null source !");
		}
		AffineTransform returned = source.convertNormalizedPointToViewCoordinatesAT(1.0);
		returned.preConcatenate(convertCoordinatesAT(source, destination, 1.0));
		returned.preConcatenate(destination.convertViewCoordinatesToNormalizedPointAT(1.0));
		return returned;
	}

	/*public static int getCumulativeLeftBorders(ContainerNode<?, ?> node) {
		if (node instanceof ShapeNode) {
			int returned = ((ShapeNode<?>) node).getBorderLeft();
			if (node.getParentNode() instanceof ShapeNode) {
				return returned + getCumulativeLeftBorders(node.getParentNode());
			}
			return returned;
		}
		return 0;
	}
	
	public static int getCumulativeTopBorders(ContainerNode<?, ?> node) {
		if (node instanceof ShapeNode) {
			int returned = ((ShapeNode<?>) node).getBorderTop();
			if (node.getParentNode() instanceof ShapeNode) {
				return returned + getCumulativeTopBorders(node.getParentNode());
			}
			return returned;
		}
		return 0;
	}*/

	public static interface HasIcon {

		public ImageIcon getIcon();
	}

}
