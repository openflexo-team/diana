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

package org.openflexo.fge.graphics;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import org.openflexo.fge.geom.FGECubicCurve;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGEPolylin;
import org.openflexo.fge.geom.FGEQuadCurve;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGEShape;

/**
 * General API for a graphics component responsible for painting
 * 
 * @author sylvain
 *
 */
public interface AbstractFGEGraphics {

	/**
	 * Return the normalized bounds of node beeing painted
	 * 
	 * @return
	 */
	public FGERectangle getNodeNormalizedBounds();

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
	public abstract void setDefaultForegroundStyle(FGEShape<?> shape);

	/**
	 * Sets default background style with the one specified bu supplied shape, if any (does nothing when null)
	 */
	public abstract void setDefaultBackgroundStyle(FGEShape<?> shape);

	/**
	 * Draw control point at specified location, and with specified size
	 * 
	 * @param x
	 * @param y
	 * @param size
	 * @return
	 */
	public abstract Rectangle drawControlPoint(double x, double y, int size);

	public abstract void drawPoint(FGEPoint p);

	public abstract void drawPoint(double x, double y);

	public abstract void drawRoundArroundPoint(FGEPoint p, int size);

	public abstract void drawRoundArroundPoint(double x, double y, int size);

	public abstract Rectangle drawControlPoint(FGEPoint p, int size);

	public abstract void drawRect(double x, double y, double width, double height);

	public abstract void drawRect(FGEPoint p, FGEDimension d);

	public abstract void fillRect(double x, double y, double width, double height);

	/**
	 * This method is used to paint an image or a portion of an image into a supplied shape. Background properties are used, and
	 * transparency managed here.
	 * 
	 * @param aShape
	 */
	public abstract void drawImage(Image image, FGEPoint p);

	public abstract void fillRect(FGEPoint p, FGEDimension d);

	public abstract void drawLine(double x1, double y1, double x2, double y2);

	public abstract void drawLine(FGEPoint p1, FGEPoint p2);

	public abstract void drawRoundRect(double x, double y, double width, double height, double arcwidth, double archeight);

	public abstract void drawRoundRect(FGEPoint p, FGEDimension d, double arcwidth, double archeight);

	public abstract void fillRoundRect(double x, double y, double width, double height, double arcwidth, double archeight);

	public abstract void fillRoundRect(FGEPoint p, FGEDimension d, double arcwidth, double archeight);

	public abstract void drawPolyline(FGEPolylin polylin);

	public abstract void drawPolyline(FGEPoint[] points);

	public abstract void drawPolygon(FGEPolygon polygon);

	public abstract void drawPolygon(FGEPoint[] points);

	public abstract void fillPolygon(FGEPolygon polygon);

	public abstract void fillPolygon(FGEPoint[] points);

	public abstract void drawCircle(double x, double y, double width, double height);

	public abstract void drawCircle(double x, double y, double width, double height, Stroke stroke);

	public abstract void drawCircle(FGEPoint p, FGEDimension d);

	public abstract void fillCircle(double x, double y, double width, double height);

	public abstract void fillCircle(FGEPoint p, FGEDimension d);

	public abstract void drawArc(double x, double y, double width, double height, double angleStart, double arcAngle);

	public abstract void drawArc(FGEPoint p, FGEDimension d, double angleStart, double arcAngle);

	public abstract void fillArc(double x, double y, double width, double height, double angleStart, double arcAngle);

	public abstract void fillArc(double x, double y, double width, double height, double angleStart, double arcAngle, boolean chord);

	public abstract void fillArc(FGEPoint p, FGEDimension d, double angleStart, double arcAngle);

	// public abstract FGERectangle drawString(String text, FGEPoint location, int orientation, HorizontalTextAlignment alignment);

	public abstract void drawCurve(FGEQuadCurve curve);

	public abstract void drawCurve(FGECubicCurve curve);

	public abstract void drawGeneralShape(FGEGeneralShape<?> shape);

	public abstract void fillGeneralShape(FGEGeneralShape<?> shape);

	/*public abstract FGERectangle drawString(String text, double x, double y, int orientation, HorizontalTextAlignment alignment);
	
	public abstract FGERectangle drawString(String text, FGEPoint location, HorizontalTextAlignment alignment);
	
	public abstract FGERectangle drawString(String text, double x, double y, HorizontalTextAlignment alignment);*/

	public Point convertNormalizedPointToViewCoordinates(double x, double y, double scale);

	public FGEPoint convertViewCoordinatesToNormalizedPoint(Point p, double scale);

	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale);

	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale);

}
