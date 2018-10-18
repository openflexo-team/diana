/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-geom, a component of the software infrastructure 
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

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import org.openflexo.diana.geom.DianaCubicCurve;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaGeneralShape;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolygon;
import org.openflexo.diana.geom.DianaPolylin;
import org.openflexo.diana.geom.DianaQuadCurve;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaShape;

/**
 * General API for a graphics component responsible for painting
 * 
 * @author sylvain
 *
 */
public interface AbstractDianaGraphics {

	/**
	 * Return the normalized bounds of node beeing painted
	 * 
	 * @return
	 */
	public DianaRectangle getNodeNormalizedBounds();

	/**
	 * Directive used to choose foreground style declared as default
	 */
	public abstract void useDefaultForegroundStyle();

	/**
	 * Directive used to choose background style declared as default
	 */
	public abstract void useDefaultBackgroundStyle();

	/**
	 * Directive used to choose text style declared as default
	 */
	public abstract void useDefaultTextStyle();

	/**
	 * Sets default foreground style with the one specified bu supplied shape, if any (does nothing when null)
	 */
	public abstract void setDefaultForegroundStyle(DianaShape<?> shape);

	/**
	 * Sets default background style with the one specified bu supplied shape, if any (does nothing when null)
	 */
	public abstract void setDefaultBackgroundStyle(DianaShape<?> shape);

	/**
	 * Draw control point at specified location, and with specified size
	 * 
	 * @param x
	 * @param y
	 * @param size
	 * @return
	 */
	public abstract Rectangle drawControlPoint(double x, double y, int size);

	public abstract void drawPoint(DianaPoint p);

	public abstract void drawPoint(double x, double y);

	public abstract void drawRoundArroundPoint(DianaPoint p, int size);

	public abstract void drawRoundArroundPoint(double x, double y, int size);

	public abstract Rectangle drawControlPoint(DianaPoint p, int size);

	public abstract void drawRect(double x, double y, double width, double height);

	public abstract void drawRect(DianaPoint p, DianaDimension d);

	public abstract void fillRect(double x, double y, double width, double height);

	/**
	 * This method is used to paint an image or a portion of an image into a supplied shape. Background properties are used, and
	 * transparency managed here.
	 * 
	 * @param aShape
	 */
	public abstract void drawImage(Image image, DianaPoint p);

	public abstract void fillRect(DianaPoint p, DianaDimension d);

	public abstract void drawLine(double x1, double y1, double x2, double y2);

	public abstract void drawLine(DianaPoint p1, DianaPoint p2);

	public abstract void drawRoundRect(double x, double y, double width, double height, double arcwidth, double archeight);

	public abstract void drawRoundRect(DianaPoint p, DianaDimension d, double arcwidth, double archeight);

	public abstract void fillRoundRect(double x, double y, double width, double height, double arcwidth, double archeight);

	public abstract void fillRoundRect(DianaPoint p, DianaDimension d, double arcwidth, double archeight);

	public abstract void drawPolyline(DianaPolylin polylin);

	public abstract void drawPolyline(DianaPoint[] points);

	public abstract void drawPolygon(DianaPolygon polygon);

	public abstract void drawPolygon(DianaPoint[] points);

	public abstract void fillPolygon(DianaPolygon polygon);

	public abstract void fillPolygon(DianaPoint[] points);

	public abstract void drawCircle(double x, double y, double width, double height);

	public abstract void drawCircle(double x, double y, double width, double height, Stroke stroke);

	public abstract void drawCircle(DianaPoint p, DianaDimension d);

	public abstract void fillCircle(double x, double y, double width, double height);

	public abstract void fillCircle(DianaPoint p, DianaDimension d);

	public abstract void drawArc(double x, double y, double width, double height, double angleStart, double arcAngle);

	public abstract void drawArc(DianaPoint p, DianaDimension d, double angleStart, double arcAngle);

	public abstract void fillArc(double x, double y, double width, double height, double angleStart, double arcAngle);

	public abstract void fillArc(double x, double y, double width, double height, double angleStart, double arcAngle, boolean chord);

	public abstract void fillArc(DianaPoint p, DianaDimension d, double angleStart, double arcAngle);

	// public abstract DianaRectangle drawString(String text, DianaPoint location, int orientation, HorizontalTextAlignment alignment);

	public abstract void drawCurve(DianaQuadCurve curve);

	public abstract void drawCurve(DianaCubicCurve curve);

	public abstract void drawGeneralShape(DianaGeneralShape<?> shape);

	public abstract void fillGeneralShape(DianaGeneralShape<?> shape);

	/*public abstract DianaRectangle drawString(String text, double x, double y, int orientation, HorizontalTextAlignment alignment);
	
	public abstract DianaRectangle drawString(String text, DianaPoint location, HorizontalTextAlignment alignment);
	
	public abstract DianaRectangle drawString(String text, double x, double y, HorizontalTextAlignment alignment);*/

	public Point convertNormalizedPointToViewCoordinates(double x, double y, double scale);

	public DianaPoint convertViewCoordinatesToNormalizedPoint(Point p, double scale);

	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale);

	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale);

}
