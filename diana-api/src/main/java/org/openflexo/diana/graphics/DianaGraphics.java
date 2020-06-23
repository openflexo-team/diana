/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.diana.graphics;

import java.awt.Point;
import java.awt.Rectangle;

import org.openflexo.diana.BackgroundStyle;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.TextStyle;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.diana.control.DianaEditor;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

/**
 * 
 * The <code>DianaGraphics</code> class is the abstract base class for all graphics contexts that allow to draw inside a view representing a
 * {@link DrawingTreeNode}.<br>
 * A <code>DianaGraphics</code> object encapsulates state information needed for the basic rendering operations that Diana supports.<br>
 * A <code>DianaGraphics</code> object is connected to its {@link DrawingTreeNode} and to its {@link DianaEditor}.
 * 
 * @author sylvain
 * 
 */
public interface DianaGraphics extends AbstractDianaGraphics {

	public abstract DianaModelFactory getFactory();

	public abstract DrawingTreeNode<?, ?> getDrawingTreeNode();

	public abstract DrawingTreeNode<?, ?> getNode();

	public abstract GraphicalRepresentation getGraphicalRepresentation();

	public abstract DianaEditor<?> getController();

	public abstract double getScale();

	public abstract void delete();

	public abstract void translate(double tx, double ty);

	public abstract ForegroundStyle getDefaultForeground();

	public abstract ForegroundStyle getCurrentForeground();

	public abstract void setDefaultForeground(ForegroundStyle aForegound);

	public abstract void useForegroundStyle(ForegroundStyle aStyle);

	public abstract TextStyle getCurrentTextStyle();

	public abstract BackgroundStyle getDefaultBackground();

	public abstract void setDefaultBackground(BackgroundStyle aBackground);

	public abstract void useBackgroundStyle(BackgroundStyle aStyle);

	public abstract void setDefaultTextStyle(TextStyle aTextStyle);

	public abstract void useTextStyle(TextStyle aStyle);

	public abstract DianaRectangle getNormalizedBounds();

	public abstract int getViewWidth();

	public abstract int getViewHeight();

	public abstract int getViewWidth(double scale);

	public abstract int getViewHeight(double scale);

	public abstract Point convertNormalizedPointToViewCoordinates(double x, double y);

	public abstract Point convertNormalizedPointToViewCoordinates(DianaPoint p);

	public abstract Rectangle convertNormalizedRectangleToViewCoordinates(DianaRectangle r);

	public abstract Rectangle convertNormalizedRectangleToViewCoordinates(double x, double y, double width, double height);

	public abstract DianaPoint convertViewCoordinatesToNormalizedPoint(int x, int y);

	public abstract DianaPoint convertViewCoordinatesToNormalizedPoint(Point p);

	public abstract DianaRectangle convertViewCoordinatesToNormalizedRectangle(Rectangle r);

	public abstract DianaRectangle convertViewCoordinatesToNormalizedRectangle(int x, int y, int width, int height);

	public abstract DianaRectangle drawString(String text, DianaPoint location, int orientation, HorizontalTextAlignment alignment);

	public abstract DianaRectangle drawString(String text, double x, double y, int orientation, HorizontalTextAlignment alignment);

	public abstract DianaRectangle drawString(String text, DianaPoint location, HorizontalTextAlignment alignment);

	public abstract DianaRectangle drawString(String text, double x, double y, HorizontalTextAlignment alignment);

}
