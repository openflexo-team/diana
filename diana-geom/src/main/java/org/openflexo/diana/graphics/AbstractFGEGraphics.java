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

import org.openflexo.diana.geom.FGECubicCurve;
import org.openflexo.diana.geom.FGEDimension;
import org.openflexo.diana.geom.FGEGeneralShape;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.geom.FGEPolygon;
import org.openflexo.diana.geom.FGEQuadCurve;
import org.openflexo.diana.geom.FGERectangle;

public interface AbstractFGEGraphics {

	public FGERectangle getNodeNormalizedBounds();

	/*
	public abstract FGEModelFactory getFactory();
	
	public abstract DrawingTreeNode<?, ?> getDrawingTreeNode();
	
	public abstract DrawingTreeNode<?, ?> getNode();
	
	public abstract GraphicalRepresentation getGraphicalRepresentation();
	
	public abstract DrawingControllerImpl<?> getController();
	
	public abstract double getScale();
	
	public abstract void delete();
	
	public abstract void createGraphics(Graphics2D graphics2D, DrawingControllerImpl controller);
	
	public abstract void releaseGraphics();
	
	public abstract Graphics2D cloneGraphics();
	
	public abstract void releaseClonedGraphics(Graphics2D oldGraphics);
	
	public abstract Graphics2D getGraphics();
	public abstract ForegroundStyle getDefaultForeground();
	
	public abstract ForegroundStyle getCurrentForeground();
	
	public abstract void setDefaultForeground(ForegroundStyle aForegound);
	
	public abstract void setStroke(Stroke aStroke);
	*/

	public abstract void useDefaultForegroundStyle();

	/*	public abstract void useForegroundStyle(ForegroundStyle aStyle);
	
		public abstract TextStyle getCurrentTextStyle();
	
		public abstract BackgroundStyle getDefaultBackground();
	
		public abstract void setDefaultBackground(BackgroundStyle aBackground);
	*/
	public abstract void useDefaultBackgroundStyle();

	/*	public abstract void useBackgroundStyle(BackgroundStyle aStyle);
	
		public abstract void setDefaultTextStyle(TextStyle aTextStyle);
	*/
	public abstract void useDefaultTextStyle();

	/*	public abstract void useTextStyle(TextStyle aStyle);
	
		public abstract FGERectangle getNormalizedBounds();
	
		public abstract int getViewWidth();
	
		public abstract int getViewHeight();
	
		public abstract int getViewWidth(double scale);
	
		public abstract int getViewHeight(double scale);
	
		public abstract Point convertNormalizedPointToViewCoordinates(double x, double y);
	
		public abstract Point convertNormalizedPointToViewCoordinates(FGEPoint p);
	
		public abstract Rectangle convertNormalizedRectangleToViewCoordinates(FGERectangle r);
	
		public abstract Rectangle convertNormalizedRectangleToViewCoordinates(double x, double y, double width, double height);
	
		public abstract FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y);
	
		public abstract FGEPoint convertViewCoordinatesToNormalizedPoint(Point p);
	
		public abstract FGERectangle convertViewCoordinatesToNormalizedRectangle(Rectangle r);
	
		public abstract FGERectangle convertViewCoordinatesToNormalizedRectangle(int x, int y, int width, int height);
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
