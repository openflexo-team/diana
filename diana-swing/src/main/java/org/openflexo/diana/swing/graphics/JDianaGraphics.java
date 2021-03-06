/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.diana.swing.graphics;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.apache.batik.svggen.SVGGraphics2D;
import org.openflexo.diana.BackgroundImageBackgroundStyle;
import org.openflexo.diana.BackgroundStyle;
import org.openflexo.diana.ColorBackgroundStyle;
import org.openflexo.diana.ColorGradientBackgroundStyle;
import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.ForegroundStyle.DashStyle;
import org.openflexo.diana.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.diana.NoneBackgroundStyle;
import org.openflexo.diana.TextureBackgroundStyle;
import org.openflexo.diana.TextureBackgroundStyle.TextureType;
import org.openflexo.diana.geom.DianaCubicCurve;
import org.openflexo.diana.geom.DianaGeneralShape;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaQuadCurve;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.graphics.DianaGraphics;
import org.openflexo.diana.graphics.DianaGraphicsImpl;
import org.openflexo.diana.swing.view.JDianaView;

import sun.awt.image.ImageRepresentation;
import sun.awt.image.ToolkitImage;

/**
 * This is the SWING base implementation of a {@link DianaGraphics}.<br>
 * We mainly wrap a {@link Graphics2D} object and use those paint primitive
 * 
 * @author sylvain
 * 
 */
public abstract class JDianaGraphics extends DianaGraphicsImpl {

	private static final Logger logger = Logger.getLogger(JDianaGraphics.class.getPackage().getName());

	protected Graphics2D g2d;

	private static final int TRANSPARENT_COMPOSITE_RULE = AlphaComposite.SRC_OVER;

	protected JDianaGraphics(DrawingTreeNode<?, ?> dtn, JDianaView<?, ?> view) {
		super(dtn, view);
	}

	@Override
	public JDianaView<?, ?> getView() {
		return (JDianaView<?, ?>) super.getView();
	}

	@Override
	public void delete() {
		super.delete();
		g2d = null;
	}

	/**
	 * 
	 * @param graphics2D
	 * @param controller
	 */

	public void createGraphics(Graphics2D graphics2D/*, AbstractDianaEditor<?, ?, ?> controller*/) {
		g2d = (Graphics2D) graphics2D.create();
	}

	public void releaseGraphics() {
		g2d.dispose();
	}

	/**
	 * Creates a new <code>Graphics2D</code> object that is a copy of current <code>Graphics2D</code> object.
	 * 
	 * @return old <code>Graphics2D</code> object
	 */

	public Graphics2D cloneGraphics() {
		Graphics2D returned = g2d;
		g2d = (Graphics2D) g2d.create();
		return returned;
	}

	public void releaseClonedGraphics(Graphics2D oldGraphics) {
		g2d.dispose();
		g2d = oldGraphics;
	}

	public Graphics2D getGraphics() {
		return g2d;
	}

	@Override
	public void translate(double tx, double ty) {
		g2d.translate(tx, ty);
	}

	public void setStroke(Stroke aStroke) {
		g2d.setStroke(aStroke);
	}

	@Override
	protected void applyCurrentForegroundStyle() {
		if (g2d == null) {
			return; // Strange...
		}

		// logger.info("Apply "+currentForeground);

		if (getCurrentForeground() != null) {
			g2d.setColor(getCurrentForeground().getColor());
			Stroke stroke = getStroke(getCurrentForeground(), getScale());
			if (stroke != null) {
				g2d.setStroke(stroke);
			}

			// When exporting to SVG, do not set the Composite
			if (!(g2d instanceof SVGGraphics2D)) {
				if (getCurrentForeground().getUseTransparency()) {
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getCurrentForeground().getTransparencyLevel()));
				}
				else {
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
				}
			}

		}

	}

	protected String debugForegroundStyle() {
		BasicStroke stroke = (BasicStroke) g2d.getStroke();
		return "FS-(color=" + g2d.getColor() + ")-(with=" + stroke.getLineWidth() + ")-(transp=" + g2d.getComposite() + ")-(join="
				+ stroke.getLineJoin() + ")-(cap=" + stroke.getEndCap() + ")-(dash=" + stroke.getDashPhase() + ")";
	}

	@Override
	protected void applyCurrentBackgroundStyle() {
		if (g2d == null) {
			return; // Strange...
		}

		if (getCurrentBackground() != null) {
			Paint paint = getPaint(getCurrentBackground(), getScale());
			if (paint != null) {
				g2d.setPaint(paint);
				// When exporting to SVG, do not set the Composite
				if (!(g2d instanceof SVGGraphics2D)) {
					if (getCurrentBackground().getUseTransparency()) {
						g2d.setComposite(
								AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getCurrentBackground().getTransparencyLevel()));
					}
					else {
						g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
					}
				}
			}
			else {
				// paint was null, meaning that Paint could not been obtained yet (texture not ready yet)
				// the best is to paint it totally transparent
				// When exporting to SVG, do not set the Composite
				if (!(g2d instanceof SVGGraphics2D)) {
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0));
				}
			}
		}
	}

	@Override
	protected void applyCurrentTextStyle() {
		g2d.setColor(getCurrentTextStyle().getColor());
		g2d.setFont(getCurrentTextStyle().getFont());
	}

	@Override
	public Rectangle drawControlPoint(double x, double y, int size) {
		if (getCurrentForeground().getNoStroke()) {
			return null;
		}
		Point p = convertNormalizedPointToViewCoordinates(x, y);
		p.x -= size;
		p.y -= size;
		g2d.fillRect(p.x, p.y, size * 2, size * 2);
		return new Rectangle(p.x, p.y, size * 2, size * 2);
	}

	@Override
	public void drawPoint(double x, double y) {
		if (getCurrentForeground().getNoStroke()) {
			return;
		}
		Point p1 = convertNormalizedPointToViewCoordinates(x, y);
		p1.x -= DianaConstants.POINT_SIZE;
		p1.y -= DianaConstants.POINT_SIZE;
		Point p2 = convertNormalizedPointToViewCoordinates(x, y);
		p2.x += DianaConstants.POINT_SIZE;
		p2.y += DianaConstants.POINT_SIZE;
		Point p3 = convertNormalizedPointToViewCoordinates(x, y);
		p3.x -= DianaConstants.POINT_SIZE;
		p3.y += DianaConstants.POINT_SIZE;
		Point p4 = convertNormalizedPointToViewCoordinates(x, y);
		p4.x += DianaConstants.POINT_SIZE;
		p4.y -= DianaConstants.POINT_SIZE;
		g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
		g2d.drawLine(p3.x, p3.y, p4.x, p4.y);
	}

	@Override
	public void drawRoundArroundPoint(double x, double y, int size) {
		if (getCurrentForeground().getNoStroke()) {
			return;
		}
		Point p = convertNormalizedPointToViewCoordinates(x, y);
		p.x -= size;
		p.y -= size;
		g2d.drawOval(p.x, p.y, size * 2, size * 2);
	}

	@Override
	public void drawRect(double x, double y, double width, double height) {
		if (getCurrentForeground().getNoStroke()) {
			return;
		}
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		// logger.info("drawRect() with "+debugForegroundStyle());
		// SGU: I don't understand why, but is you use non-plain stroke,
		// Rendering using drawRect is not correct !!!
		// g2d.drawRect(r.x,r.y,r.width,r.height);
		if (r.height == 0 || r.width == 0) {
			g2d.drawLine(r.x, r.y, r.x + r.width, r.y + r.height);
		}
		else {
			g2d.drawLine(r.x, r.y, r.x + r.width - 1, r.y);
			g2d.drawLine(r.x + r.width, r.y, r.x + r.width, r.y + r.height - 1);
			g2d.drawLine(r.x + r.width, r.y + r.height, r.x + 1, r.y + r.height);
			g2d.drawLine(r.x, r.y + r.height, r.x, r.y + 1);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("drawRect(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	@Override
	public void fillRect(double x, double y, double width, double height) {
		if (getCurrentBackground() instanceof NoneBackgroundStyle) {
			return;
		}

		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);

		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			fillInShapeWithImage(r);
		}
		else {
			g2d.fillRect(r.x, r.y, r.width, r.height);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("fillRect(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	/**
	 * This method is used to paint an image or a portion of an image into a supplied shape. Background properties are used, and
	 * transparency managed here.
	 * 
	 * @param aShape
	 */
	private void fillInShapeWithImage(Shape aShape) {
		Graphics2D oldGraphics = cloneGraphics();
		g2d.clip(aShape);
		// g2d.setClip(aShape);

		AffineTransform at = AffineTransform.getScaleInstance(getScale(), getScale());
		/*if (getNode() instanceof ShapeNode) {
			ShapeNode<?> node = (ShapeNode<?>) getNode();
			at.concatenate(AffineTransform.getTranslateInstance(node.getBorderLeft(), node.getBorderTop()));
		}*/
		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			BackgroundImageBackgroundStyle imageBGStyle = (BackgroundImageBackgroundStyle) getCurrentBackground();

			if (imageBGStyle.getImage() != null) {
				if (!imageBGStyle.getFitToShape()) {
					at.concatenate(AffineTransform.getTranslateInstance(imageBGStyle.getDeltaX(), imageBGStyle.getDeltaY()));
				}
				if (imageBGStyle.getImageBackgroundType() == BackgroundImageBackgroundStyle.ImageBackgroundType.OPAQUE) {
					g2d.setColor(imageBGStyle.getImageBackgroundColor());
					g2d.fill(aShape);
				}
				if (imageBGStyle.getFitToShape() && getNode() instanceof ContainerNode) {
					// System.out.println("imageBGStyle.getImage()=" + imageBGStyle.getImage());
					at.concatenate(AffineTransform.getScaleInstance(
							((ContainerNode) getNode()).getWidth() / imageBGStyle.getImage().getWidth(null),
							((ContainerNode) getNode()).getHeight() / imageBGStyle.getImage().getHeight(null)));
				}
				else {
					at.concatenate(AffineTransform.getScaleInstance(imageBGStyle.getScaleX(), imageBGStyle.getScaleY()));
				}

				// When exporting to SVG, do not set the Composite
				if (!(g2d instanceof SVGGraphics2D)) {
					if (getCurrentBackground().getUseTransparency()) {
						g2d.setComposite(
								AlphaComposite.getInstance(TRANSPARENT_COMPOSITE_RULE, getCurrentBackground().getTransparencyLevel()));
					}
					else {
						g2d.setComposite(AlphaComposite.getInstance(TRANSPARENT_COMPOSITE_RULE));
					}
				}
				g2d.drawImage(((BackgroundImageBackgroundStyle) getCurrentBackground()).getImage(), at, null);
			}
		}

		releaseClonedGraphics(oldGraphics);
	}

	/**
	 * This method is used to paint an image or a portion of an image into a supplied shape. Background properties are used, and
	 * transparency managed here.
	 * 
	 * @param aShape
	 */

	@Override
	public void drawImage(Image image, DianaPoint p) {
		// When exporting to SVG, do not set the Composite
		if (!(g2d instanceof SVGGraphics2D)) {
			g2d.setComposite(AlphaComposite.getInstance(TRANSPARENT_COMPOSITE_RULE));
		}
		Point location = convertNormalizedPointToViewCoordinates(p.x, p.y);
		// System.err.println(location);
		AffineTransform at = AffineTransform.getScaleInstance(getScale(), getScale());
		at.preConcatenate(AffineTransform.getTranslateInstance(location.x, location.y));
		g2d.drawImage(image, at, null);
	}

	@Override
	public void fillRoundRect(double x, double y, double width, double height, double arcwidth, double archeight) {
		if (getCurrentBackground() instanceof NoneBackgroundStyle) {
			return;
		}

		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		Rectangle arcRect = convertNormalizedRectangleToViewCoordinates(0, 0, arcwidth, archeight);
		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			RoundRectangle2D.Double rr = new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, arcRect.width, arcRect.height);
			fillInShapeWithImage(rr);
		}
		else {
			g2d.fillRoundRect(r.x, r.y, r.width, r.height, arcRect.width, arcRect.height);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("fillRoundRect(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	@Override
	public void drawRoundRect(double x, double y, double width, double height, double arcwidth, double archeight) {
		if (getCurrentForeground().getNoStroke()) {
			return;
		}
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		Rectangle arcRect = convertNormalizedRectangleToViewCoordinates(0, 0, arcwidth, archeight);

		g2d.drawRoundRect(r.x, r.y, r.width, r.height, arcRect.width, arcRect.height);

		if (logger.isLoggable(Level.FINER)) {
			logger.finer("drawRoundRect(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2) {
		// logger.info("drawLine(" + x1 + "," + y1 + "," + x2 + "," + y2 + ")" + " with " + debugForegroundStyle());
		// logger.info("clipbounds=" + g2d.getClipBounds());

		if (getCurrentForeground() == null || getCurrentForeground().getNoStroke()) {
			return;
		}
		Point p1 = convertNormalizedPointToViewCoordinates(x1, y1);
		Point p2 = convertNormalizedPointToViewCoordinates(x2, y2);
		// logger.info("drawLine("+p1.x+","+p1.y+","+p2.x+","+p2.y+")");
		g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("drawLine(" + p1.x + "," + p1.y + "," + p2.x + "," + p2.y + ")");
		}
	}

	@Override
	public void drawPolyline(DianaPoint[] points) {
		if (getCurrentForeground().getNoStroke()) {
			return;
		}
		if (points == null || points.length == 0) {
			return;
		}
		int[] xpoints = new int[points.length];
		int[] ypoints = new int[points.length];
		for (int i = 0; i < points.length; i++) {
			Point p = convertNormalizedPointToViewCoordinates(points[i]);
			xpoints[i] = p.x;
			ypoints[i] = p.y;
		}
		g2d.drawPolyline(xpoints, ypoints, points.length);
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("drawPolyline(" + points + ")");
		}
	}

	@Override
	public void drawPolygon(DianaPoint[] points) {
		if (getCurrentForeground().getNoStroke()) {
			return;
		}
		if (points == null || points.length == 0) {
			return;
		}
		int[] xpoints = new int[points.length];
		int[] ypoints = new int[points.length];
		for (int i = 0; i < points.length; i++) {
			Point p = convertNormalizedPointToViewCoordinates(points[i]);
			xpoints[i] = p.x;
			ypoints[i] = p.y;
		}
		g2d.drawPolygon(xpoints, ypoints, points.length);
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("drawPolygon(" + points + ")");
		}
	}

	@Override
	public void fillPolygon(DianaPoint[] points) {
		if (getCurrentBackground() instanceof NoneBackgroundStyle) {
			return;
		}
		if (points == null || points.length == 0) {
			return;
		}

		int[] xpoints = new int[points.length];
		int[] ypoints = new int[points.length];
		for (int i = 0; i < points.length; i++) {
			Point p = convertNormalizedPointToViewCoordinates(points[i]);
			xpoints[i] = p.x;
			ypoints[i] = p.y;
		}
		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			Polygon p = new Polygon(xpoints, ypoints, points.length);
			fillInShapeWithImage(p);
		}
		else {
			g2d.fillPolygon(xpoints, ypoints, points.length);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("fillPolygon(" + points + ")");
		}
	}

	@Override
	public void drawCircle(double x, double y, double width, double height) {
		if (getCurrentForeground().getNoStroke()) {
			return;
		}
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		g2d.drawArc(r.x, r.y, r.width, r.height, 0, 360);
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("drawCircle(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	@Override
	public void drawCircle(double x, double y, double width, double height, Stroke stroke) {
		if (getCurrentForeground().getNoStroke()) {
			return;
		}
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		Stroke back = g2d.getStroke();
		g2d.setStroke(stroke);
		g2d.drawArc(r.x, r.y, r.width, r.height, 0, 360);
		g2d.setStroke(back);
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("drawCircle(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	@Override
	public void fillCircle(double x, double y, double width, double height) {
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			Arc2D.Double a = new Arc2D.Double(r.x, r.y, r.width, r.height, 0, 360, Arc2D.CHORD);
			fillInShapeWithImage(a);
		}
		else {
			g2d.fillArc(r.x, r.y, r.width, r.height, 0, 360);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("fillCircle(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	@Override
	public void drawArc(double x, double y, double width, double height, double angleStart, double arcAngle) {
		if (getCurrentForeground().getNoStroke()) {
			return;
		}
		// System.out.println("drawArc ("+x+","+y+","+width+","+height+")");
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		g2d.drawArc(r.x, r.y, r.width, r.height, (int) angleStart, (int) arcAngle);
		// System.out.println("drawArc("+r.x+","+r.y+","+r.width+","+r.height+")");
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("drawArc(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	@Override
	public void fillArc(double x, double y, double width, double height, double angleStart, double arcAngle, boolean chord) {
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			Arc2D.Double a = new Arc2D.Double(r.x, r.y, r.width, r.height, (int) angleStart, (int) arcAngle,
					chord ? Arc2D.CHORD : Arc2D.PIE);
			fillInShapeWithImage(a);
		}
		else {
			if (chord) {
				Arc2D.Double a = new Arc2D.Double(r.x, r.y, r.width, r.height, (int) angleStart, (int) arcAngle, Arc2D.CHORD);
				g2d.setClip(a);
			}
			g2d.fillArc(r.x, r.y, r.width, r.height, (int) angleStart, (int) arcAngle);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("fillArc(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	@Override
	public void drawCurve(DianaQuadCurve curve) {
		if (getCurrentForeground().getNoStroke()) {
			return;
		}
		Point p1 = convertNormalizedPointToViewCoordinates(curve.getX1(), curve.getY1());
		Point ctrl_p = convertNormalizedPointToViewCoordinates(curve.getCtrlX(), curve.getCtrlY());
		Point p2 = convertNormalizedPointToViewCoordinates(curve.getX2(), curve.getY2());
		QuadCurve2D awtCurve = new QuadCurve2D.Double(p1.x, p1.y, ctrl_p.x, ctrl_p.y, p2.x, p2.y);
		g2d.draw(awtCurve);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("drawCurve(" + p1.x + "," + p1.y + "," + ctrl_p.x + "," + ctrl_p.y + "," + p2.x + "," + p2.y + ")");
		}
	}

	@Override
	public void drawCurve(DianaCubicCurve curve) {
		if (getCurrentForeground().getNoStroke()) {
			return;
		}
		Point p1 = convertNormalizedPointToViewCoordinates(curve.getX1(), curve.getY1());
		Point ctrl_p1 = convertNormalizedPointToViewCoordinates(curve.getCtrlX1(), curve.getCtrlY1());
		Point ctrl_p2 = convertNormalizedPointToViewCoordinates(curve.getCtrlX2(), curve.getCtrlY2());
		Point p2 = convertNormalizedPointToViewCoordinates(curve.getX2(), curve.getY2());
		CubicCurve2D awtCurve = new CubicCurve2D.Double(p1.x, p1.y, ctrl_p1.x, ctrl_p1.y, ctrl_p2.x, ctrl_p2.y, p2.x, p2.y);
		g2d.draw(awtCurve);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("drawCurve(" + p1.x + "," + p1.y + "," + ctrl_p1.x + "," + ctrl_p1.y + "," + p2.x + ctrl_p2.x + "," + ctrl_p2.y
					+ "," + p2.x + "," + p2.y + ")");
		}
	}

	@Override
	public void fillGeneralShape(DianaGeneralShape shape) {
		AffineTransform at = getNode().convertNormalizedPointToViewCoordinatesAT(getScale());
		DianaGeneralShape<?> transformedShape = shape.transform(at);
		GeneralPath p = transformedShape.getGeneralPath();
		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			fillInShapeWithImage(p);
		}
		else {
			g2d.fill(p);
		}
	}

	@Override
	public DianaRectangle drawString(String text, double x, double y, int orientation, HorizontalTextAlignment alignment) {
		if (text == null || text.length() == 0) {
			return new DianaRectangle();
		}

		Point p = convertNormalizedPointToViewCoordinates(x, y);
		Font oldFont = g2d.getFont();
		AffineTransform at = AffineTransform.getScaleInstance(getScale(), getScale());
		if (orientation != 0) {
			at.concatenate(AffineTransform.getRotateInstance(Math.toRadians(orientation)));
		}
		Font font = oldFont.deriveFont(at);
		Rectangle2D b = g2d.getFontMetrics().getStringBounds(text, g2d);
		g2d.setFont(font);
		/*DianaRectangle returned = convertViewCoordinatesToNormalizedRectangle(
				(int)(bounds.getX()+p.x-bounds.getWidth()/2),
				(int)(bounds.getY()+p.y+bounds.getHeight()/2),
				(int)bounds.getWidth(),(int)bounds.getHeight());*/
		DianaRectangle bounds = new DianaRectangle(b.getX(), b.getY(), b.getWidth(), b.getHeight());
		if (orientation != 0) {
			bounds = bounds.transform(AffineTransform.getRotateInstance(Math.toRadians(orientation))).getEmbeddingBounds();
		}
		int x2 = (int) (p.x - bounds.getWidth() / 2);
		int y2 = (int) (p.y + bounds.getHeight() / 2);
		switch (alignment) {
			case LEFT:
				x2 = p.x;
				break;
			case RIGHT:
				x2 = (int) (p.x - bounds.getWidth());
				break;
			case CENTER:
			default:
				break;
		}
		// GPO: Je crois que c'est complètement foireux si le background est "colored"
		if (getCurrentTextStyle().getIsBackgroundColored()) {
			g2d.setColor(getCurrentTextStyle().getBackgroundColor());
			g2d.fillRect((int) (bounds.getX() + p.x - bounds.getWidth() / 2), (int) (bounds.getY() + p.y + bounds.getHeight() / 2),
					(int) bounds.getWidth(), (int) bounds.getHeight());
			g2d.setColor(getCurrentTextStyle().getColor());
		}
		g2d.drawString(text, x2, y2);
		g2d.setFont(oldFont);
		return convertViewCoordinatesToNormalizedRectangle((int) (bounds.getX() + p.x - bounds.getWidth() / 2),
				(int) (bounds.getY() + p.y + bounds.getHeight() / 2), (int) bounds.getWidth(), (int) bounds.getHeight());
	}

	// TODO: implements cache for stroke
	private Stroke cachedStroke = null;

	/**
	 * Computes and return stroke for supplied ForegroundStyle and scale<br>
	 * Stores a cached value when possible
	 * 
	 * @param foregroundStyle
	 * @param scale
	 * @return
	 */
	public Stroke getStroke(ForegroundStyle foregroundStyle, double scale) {
		// if (cachedStroke == null || cachedStrokeFS == null || !cachedStrokeFS.equalsObject(foregroundStyle) || scale != cachedStokeScale)
		// {
		if (foregroundStyle.getDashStyle() == null) {
			return null;
		}
		if (foregroundStyle.getDashStyle() == DashStyle.PLAIN_STROKE) {
			cachedStroke = new BasicStroke((float) (foregroundStyle.getLineWidth() * scale), foregroundStyle.getCapStyle().ordinal(),
					foregroundStyle.getJoinStyle().ordinal());
		}
		else {
			float[] scaledDashArray = new float[foregroundStyle.getDashStyle().getDashArray().length];
			for (int i = 0; i < foregroundStyle.getDashStyle().getDashArray().length; i++) {
				scaledDashArray[i] = (float) (foregroundStyle.getDashStyle().getDashArray()[i] * scale * foregroundStyle.getLineWidth());
			}
			float scaledDashedPhase = (float) (foregroundStyle.getDashStyle().getDashPhase() * scale * foregroundStyle.getLineWidth());
			cachedStroke = new BasicStroke((float) (foregroundStyle.getLineWidth() * scale), foregroundStyle.getCapStyle().ordinal(),
					foregroundStyle.getJoinStyle().ordinal(), 10, scaledDashArray, scaledDashedPhase);
		}
		// cachedStokeScale = scale;
		// }
		return cachedStroke;
	}

	/**
	 * Computes and return stroke for supplied ForegroundStyle and scale<br>
	 * Stores a cached value when possible
	 * 
	 * @param foregroundStyle
	 * @param scale
	 * @return
	 */
	public Paint getPaint(BackgroundStyle backgroundStyle, double scale) {
		if (backgroundStyle instanceof NoneBackgroundStyle) {
			return null;
		}
		else if (backgroundStyle instanceof ColorBackgroundStyle) {
			return ((ColorBackgroundStyle) backgroundStyle).getColor();
		}
		else if (getCurrentBackground() instanceof ColorGradientBackgroundStyle) {
			return getGradientPaint((ColorGradientBackgroundStyle) backgroundStyle, scale);
		}
		else if (getCurrentBackground() instanceof TextureBackgroundStyle) {
			return getTexturePaint((TextureBackgroundStyle) backgroundStyle, scale);
		}
		else if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			return Color.WHITE;
		}
		else {
			return null;
		}
	}

	private GradientPaint getGradientPaint(ColorGradientBackgroundStyle bs, double scale) {
		switch (bs.getDirection()) {
			case NORTH_WEST_SOUTH_EAST:
				return new GradientPaint(0, 0, bs.getColor1(), getNode().getViewWidth(scale), getNode().getViewHeight(scale),
						bs.getColor2());
			case SOUTH_WEST_NORTH_EAST:
				return new GradientPaint(0, getNode().getViewHeight(scale), bs.getColor1(), getNode().getViewWidth(scale), 0,
						bs.getColor2());
			case WEST_EAST:
				return new GradientPaint(0, 0.5f * getNode().getViewHeight(scale), bs.getColor1(), getNode().getViewWidth(scale),
						0.5f * getNode().getViewHeight(scale), bs.getColor2());
			case NORTH_SOUTH:
				return new GradientPaint(0.5f * getNode().getViewWidth(scale), 0, bs.getColor1(), 0.5f * getNode().getViewWidth(scale),
						getNode().getViewHeight(scale), bs.getColor2());
			default:
				return new GradientPaint(0, 0, bs.getColor1(), getNode().getViewWidth(scale), getNode().getViewHeight(scale),
						bs.getColor2());
		}
	}

	private synchronized TexturePaint getTexturePaint(TextureBackgroundStyle bs, double scale) {
		final BufferedImage coloredTexture = getColoredTexture(bs);
		if (coloredTexture != null) {
			return new TexturePaint(coloredTexture, new Rectangle(0, 0, coloredTexture.getWidth(), coloredTexture.getHeight()));
		}
		// Since image building take some time, colored texture might not be ready yet
		// In this case, invoke repaint later
		logger.warning("ColoredTexture not ready");
		repaintWhenColoredTextureHasBeenComputed();
		return null;
	}

	private synchronized void repaintWhenColoredTextureHasBeenComputed() {
		if (coloredTexture != null) {
			// Now it's ok, proceed repaint
			getView().getPaintManager().invalidate(getNode());
			getView().getPaintManager().repaint(getView());
		}
		else {
			SwingUtilities.invokeLater(() -> repaintWhenColoredTextureHasBeenComputed());
		}
	}

	private BufferedImage coloredTexture;
	private ToolkitImage coloredImage;
	private ToolkitImage requestedColoredImage;
	private TextureType coloredTextureMadeForThisTextureType = null;
	private Color coloredTextureMadeForThisColor1 = null;
	private Color coloredTextureMadeForThisColor2 = null;

	/**
	 * Internally called to rebuild colored texture if cached value is not up-to-date (parameters have changed)
	 * 
	 * @param bs
	 */
	private synchronized void rebuildColoredTextureWhenRequired(TextureBackgroundStyle bs) {
		if (coloredTexture == null || coloredTextureMadeForThisTextureType == null || coloredTextureMadeForThisColor1 == null
				|| coloredTextureMadeForThisColor2 == null || coloredTextureMadeForThisTextureType != bs.getTextureType()
				|| coloredTextureMadeForThisColor1 != bs.getColor1() || coloredTextureMadeForThisColor2 != bs.getColor2()) {
			// Texture needs to be rebuilt
			rebuildColoredTexture(bs);
		}
		else {
			logger.fine("Texture is still valid");
		}
	}

	/**
	 * Internally called to rebuild colored texture
	 * 
	 * @param bs
	 */
	private synchronized void rebuildColoredTexture(final TextureBackgroundStyle bs) {
		coloredTexture = null;
		coloredImage = null;
		if (bs.getTextureType() == null) {
			return;
		}
		final Image initialImage = bs.getTextureType().getImageIcon().getImage();
		ColorSwapFilter imgfilter = new ColorSwapFilter(java.awt.Color.BLACK, bs.getColor1(), java.awt.Color.WHITE, bs.getColor2()) {
			@Override
			public void imageComplete(int status) {
				super.imageComplete(status);
				synchronized (JDianaGraphics.this) {
					coloredTexture = new BufferedImage(coloredImage.getWidth(null), coloredImage.getHeight(null),
							BufferedImage.TYPE_INT_ARGB);
					Graphics gi = coloredTexture.getGraphics();
					if (coloredImage != null) {
						gi.drawImage(coloredImage, 0, 0, null);
					}
					coloredTextureMadeForThisTextureType = bs.getTextureType();
					coloredTextureMadeForThisColor1 = bs.getColor1();
					coloredTextureMadeForThisColor2 = bs.getColor2();
					logger.fine("Image has been computed, status=" + status);
				}
			}
		};

		// Launch a background job building a new image with specified two colors
		ImageProducer producer = new FilteredImageSource(initialImage.getSource(), imgfilter);
		coloredImage = (ToolkitImage) Toolkit.getDefaultToolkit().createImage(producer);
		ImageRepresentation consumer = new ImageRepresentation(coloredImage, null, true);
		producer.addConsumer(consumer);
		try {
			producer.startProduction(consumer);
		} catch (RuntimeException e) {
			logger.warning("Unexpected exception: " + e);
		}

	}

	private BufferedImage getColoredTexture(TextureBackgroundStyle bs) {
		rebuildColoredTextureWhenRequired(bs);
		return coloredTexture;

		/*if (coloredTexture == null) {
			rebuildColoredTexture(bs);
		} 
		return coloredTexture;*/

	}

	static class ColorSwapFilter extends RGBImageFilter {
		private final int target1;
		private final int replacement1;
		private final int target2;
		private final int replacement2;

		public ColorSwapFilter(java.awt.Color target1, java.awt.Color replacement1, java.awt.Color target2, java.awt.Color replacement2) {
			this.target1 = target1.getRGB();
			this.replacement1 = replacement1.getRGB();
			this.target2 = target2.getRGB();
			this.replacement2 = replacement2.getRGB();
		}

		@Override
		public int filterRGB(int x, int y, int rgb) {
			// if (x==0 && y==0) logger.info("Starting convert image");
			// if (x==15 && y==15) logger.info("Finished convert image");
			if (rgb == target1) {
				return replacement1;
			}
			else if (rgb == target2) {
				return replacement2;
			}
			return rgb;
		}

		@Override
		public void imageComplete(int status) {
			super.imageComplete(status);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("imageComplete status=" + status);
			}
		}

	}

}
