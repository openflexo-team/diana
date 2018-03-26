/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.diana.graphics;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import org.openflexo.diana.BackgroundStyle;
import org.openflexo.diana.DianaCoreUtils;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.diana.TextStyle;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.geom.DianaCubicCurve;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaGeneralShape;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolygon;
import org.openflexo.diana.geom.DianaQuadCurve;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.view.DianaView;

/**
 * This is the generic base implementation of a {@link DianaGraphics}
 * 
 * @author sylvain
 * 
 */
public abstract class DianaGraphicsImpl implements DianaGraphics {

	private DrawingTreeNode<?, ?> dtn;
	private final DianaView<?, ?> view;

	private static final DianaModelFactory GRAPHICS_FACTORY = DianaCoreUtils.TOOLS_FACTORY;
	private static final ForegroundStyle DEFAULT_FG = GRAPHICS_FACTORY.makeDefaultForegroundStyle();
	private static final BackgroundStyle DEFAULT_BG = GRAPHICS_FACTORY.makeEmptyBackground();
	private static final TextStyle DEFAULT_TEXT = GRAPHICS_FACTORY.makeDefaultTextStyle();

	private ForegroundStyle defaultForeground = DEFAULT_FG;
	private BackgroundStyle defaultBackground = DEFAULT_BG;
	private TextStyle defaultTextStyle = DEFAULT_TEXT;

	private ForegroundStyle currentForeground = defaultForeground;
	private BackgroundStyle currentBackground = defaultBackground;
	private TextStyle currentTextStyle = defaultTextStyle;

	public DianaGraphicsImpl(DrawingTreeNode<?, ?> dtn, DianaView<?, ?> view) {
		super();
		this.dtn = dtn;
		this.view = view;
	}

	@Override
	public DianaModelFactory getFactory() {
		return GRAPHICS_FACTORY;
	}

	@Override
	public DrawingTreeNode<?, ?> getDrawingTreeNode() {
		return dtn;
	}

	@Override
	public DrawingTreeNode<?, ?> getNode() {
		return getDrawingTreeNode();
	}

	public DianaView<?, ?> getView() {
		return view;
	}

	@Override
	public GraphicalRepresentation getGraphicalRepresentation() {
		return dtn.getGraphicalRepresentation();
	}

	@Override
	public AbstractDianaEditor<?, ?, ?> getController() {
		return view.getController();
	}

	@Override
	public double getScale() {
		return getController().getScale();
	}

	@Override
	public void delete() {
		dtn = null;
	}

	@Override
	public ForegroundStyle getDefaultForeground() {
		return defaultForeground;
	}

	@Override
	public ForegroundStyle getCurrentForeground() {
		return currentForeground;
	}

	@Override
	public void setDefaultForeground(ForegroundStyle aForegound) {
		defaultForeground = aForegound;
	}

	@Override
	public void useDefaultForegroundStyle() {
		useForegroundStyle(defaultForeground);
	}

	@Override
	public void useForegroundStyle(ForegroundStyle aStyle) {
		currentForeground = aStyle;
		applyCurrentForegroundStyle();
	}

	protected abstract void applyCurrentForegroundStyle();

	@Override
	public TextStyle getCurrentTextStyle() {
		return currentTextStyle;
	}

	@Override
	public BackgroundStyle getDefaultBackground() {
		return defaultBackground;
	}

	@Override
	public void setDefaultBackground(BackgroundStyle aBackground) {
		defaultBackground = aBackground;
	}

	public BackgroundStyle getCurrentBackground() {
		return currentBackground;
	}

	@Override
	public void useDefaultBackgroundStyle() {
		useBackgroundStyle(defaultBackground);
	}

	@Override
	public void useBackgroundStyle(BackgroundStyle aStyle) {
		currentBackground = aStyle;
		applyCurrentBackgroundStyle();
	}

	protected abstract void applyCurrentBackgroundStyle();

	@Override
	public void setDefaultTextStyle(TextStyle aTextStyle) {
		defaultTextStyle = aTextStyle;
	}

	@Override
	public void useDefaultTextStyle() {
		useTextStyle(defaultTextStyle);
	}

	@Override
	public void useTextStyle(TextStyle aStyle) {
		currentTextStyle = aStyle;
		applyCurrentTextStyle();
	}

	protected abstract void applyCurrentTextStyle();

	@Override
	public DianaRectangle getNodeNormalizedBounds() {
		return getNode().getNormalizedBounds();
	}

	@Override
	public DianaRectangle getNormalizedBounds() {
		return new DianaRectangle(0.0, 0.0, 1.0, 1.0);
	}

	@Override
	public int getViewWidth() {
		return getViewWidth(getScale());
	}

	@Override
	public int getViewHeight() {
		return getViewHeight(getScale());
	}

	@Override
	public int getViewWidth(double scale) {
		return dtn.getViewWidth(scale);
	}

	@Override
	public int getViewHeight(double scale) {
		return dtn.getViewHeight(scale);
	}

	@Override
	public Point convertNormalizedPointToViewCoordinates(double x, double y) {
		return dtn.convertNormalizedPointToViewCoordinates(x, y, getScale());
	}

	@Override
	public final Point convertNormalizedPointToViewCoordinates(DianaPoint p) {
		return convertNormalizedPointToViewCoordinates(p.x, p.y);
	}

	@Override
	public final Rectangle convertNormalizedRectangleToViewCoordinates(DianaRectangle r) {
		return convertNormalizedRectangleToViewCoordinates(r.x, r.y, r.width, r.height);
	}

	@Override
	public final Rectangle convertNormalizedRectangleToViewCoordinates(double x, double y, double width, double height) {
		Point p1 = convertNormalizedPointToViewCoordinates(x, y);
		Point p2 = convertNormalizedPointToViewCoordinates(x + width, y + height);
		Dimension d = new Dimension(Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));
		return new Rectangle(p1, d);
	}

	@Override
	public DianaPoint convertViewCoordinatesToNormalizedPoint(int x, int y) {
		return dtn.convertViewCoordinatesToNormalizedPoint(x, y, getScale());
	}

	@Override
	public final DianaPoint convertViewCoordinatesToNormalizedPoint(Point p) {
		return convertViewCoordinatesToNormalizedPoint(p.x, p.y);
	}

	@Override
	public final DianaRectangle convertViewCoordinatesToNormalizedRectangle(Rectangle r) {
		return convertViewCoordinatesToNormalizedRectangle(r.x, r.y, r.width, r.height);
	}

	@Override
	public final DianaRectangle convertViewCoordinatesToNormalizedRectangle(int x, int y, int width, int height) {
		DianaPoint p1 = convertViewCoordinatesToNormalizedPoint(x, y);
		DianaPoint p2 = convertViewCoordinatesToNormalizedPoint(x + width, y + height);
		DianaDimension d = new DianaDimension(Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));
		return new DianaRectangle(p1, d, Filling.NOT_FILLED);
	}

	@Override
	public void drawPoint(DianaPoint p) {
		drawPoint(p.x, p.y);
	}

	@Override
	public void drawRoundArroundPoint(DianaPoint p, int size) {
		drawRoundArroundPoint(p.x, p.y, size);
	}

	@Override
	public Rectangle drawControlPoint(DianaPoint p, int size) {
		return drawControlPoint(p.x, p.y, size);
	}

	@Override
	public void drawRect(DianaPoint p, DianaDimension d) {
		drawRect(p.x, p.y, d.width, d.height);
	}

	@Override
	public void fillRect(DianaPoint p, DianaDimension d) {
		fillRect(p.x, p.y, d.width, d.height);
	}

	@Override
	public void drawLine(DianaPoint p1, DianaPoint p2) {
		drawLine(p1.x, p1.y, p2.x, p2.y);
	}

	@Override
	public void drawRoundRect(DianaPoint p, DianaDimension d, double arcwidth, double archeight) {
		drawRoundRect(p.x, p.y, d.width, d.height, arcwidth, archeight);
	}

	@Override
	public void fillRoundRect(DianaPoint p, DianaDimension d, double arcwidth, double archeight) {
		fillRoundRect(p.x, p.y, d.width, d.height, arcwidth, archeight);
	}

	@Override
	public void drawPolygon(DianaPolygon polygon) {
		drawPolygon(polygon.getPoints().toArray(new DianaPoint[polygon.getPointsNb()]));
	}

	@Override
	public void fillPolygon(DianaPolygon polygon) {
		fillPolygon(polygon.getPoints().toArray(new DianaPoint[polygon.getPointsNb()]));
	}

	@Override
	public void drawCircle(DianaPoint p, DianaDimension d) {
		drawCircle(p.x, p.y, d.width, d.height);
	}

	@Override
	public void fillCircle(DianaPoint p, DianaDimension d) {
		fillCircle(p.x, p.y, d.width, d.height);
	}

	@Override
	public void drawArc(DianaPoint p, DianaDimension d, double angleStart, double arcAngle) {
		drawArc(p.x, p.y, d.width, d.height, angleStart, arcAngle);
	}

	@Override
	public void fillArc(double x, double y, double width, double height, double angleStart, double arcAngle) {
		fillArc(x, y, width, height, angleStart, arcAngle, false);
	}

	@Override
	public void fillArc(DianaPoint p, DianaDimension d, double angleStart, double arcAngle) {
		fillArc(p.x, p.y, d.width, d.height, angleStart, arcAngle);
	}

	@Override
	public DianaRectangle drawString(String text, DianaPoint location, int orientation, HorizontalTextAlignment alignment) {
		return drawString(text, location.x, location.y, orientation, alignment);
	}

	@Override
	public void drawGeneralShape(DianaGeneralShape<?> shape) {
		if (currentForeground.getNoStroke()) {
			return;
		}

		PathIterator pi = shape.getPathIterator(null);
		DianaPoint current = new DianaPoint();
		DianaPoint first = null;
		while (!pi.isDone()) {
			double[] pts = new double[6];
			DianaPoint p2, cp, cp1, cp2;
			switch (pi.currentSegment(pts)) {
				case PathIterator.SEG_MOVETO:
					current.x = pts[0];
					current.y = pts[1];
					first = current.clone();
					break;
				case PathIterator.SEG_LINETO:
					p2 = new DianaPoint(pts[0], pts[1]);
					drawLine(current, p2);
					current = p2;
					break;
				case PathIterator.SEG_QUADTO:
					cp = new DianaPoint(pts[0], pts[1]);
					p2 = new DianaPoint(pts[2], pts[3]);
					drawCurve(new DianaQuadCurve(current, cp, p2));
					current = p2;
					break;
				case PathIterator.SEG_CUBICTO:
					cp1 = new DianaPoint(pts[0], pts[1]);
					cp2 = new DianaPoint(pts[2], pts[3]);
					p2 = new DianaPoint(pts[4], pts[5]);
					drawCurve(new DianaCubicCurve(current, cp1, cp2, p2));
					current = p2;
					break;
				case PathIterator.SEG_CLOSE:
					drawLine(current, first);
					current = first;
					break;
				default:
					break;
			}
			pi.next();
		}

	}

	@Override
	public DianaRectangle drawString(String text, DianaPoint location, HorizontalTextAlignment alignment) {
		return drawString(text, location.x, location.y, 0, alignment);
	}

	@Override
	public DianaRectangle drawString(String text, double x, double y, HorizontalTextAlignment alignment) {
		return drawString(text, x, y, 0, alignment);
	}

	@Override
	public Point convertNormalizedPointToViewCoordinates(double x, double y, double scale) {
		return getNode().convertNormalizedPointToViewCoordinates(x, y, scale);
	}

	@Override
	public DianaPoint convertViewCoordinatesToNormalizedPoint(Point p, double scale) {
		return getNode().convertViewCoordinatesToNormalizedPoint(p, scale);
	}

	@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		return getNode().convertNormalizedPointToViewCoordinatesAT(scale);
	}

	@Override
	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale) {
		return getNode().convertViewCoordinatesToNormalizedPointAT(scale);
	}

}
