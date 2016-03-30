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

package org.openflexo.fge.graphics;

import java.awt.Point;
import java.awt.Rectangle;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;

/**
 * 
 * The <code>FGEGraphics</code> class is the abstract base class for all graphics contexts that allow to draw inside a view representing a
 * {@link DrawingTreeNode}.<br>
 * A <code>FGEGraphics</code> object encapsulates state information needed for the basic rendering operations that Diana supports.<br>
 * A <code>FGEGraphics</code> object is connected to its {@link DrawingTreeNode} and to its {@link DianaEditor}.
 * 
 * @author sylvain
 * 
 */
public interface FGEGraphics extends AbstractFGEGraphics {

	public abstract FGEModelFactory getFactory();

	public abstract DrawingTreeNode<?, ?> getDrawingTreeNode();

	public abstract DrawingTreeNode<?, ?> getNode();

	public abstract GraphicalRepresentation getGraphicalRepresentation();

	public abstract DianaEditor<?> getController();

	public abstract double getScale();

	public abstract void delete();

	public abstract void translate(double tx, double ty);

	// public abstract void createGraphics(Graphics2D graphics2D, DianaEditor<?> controller);

	// public abstract void releaseGraphics();

	// public abstract Graphics2D cloneGraphics();

	// public abstract void releaseClonedGraphics(Graphics2D oldGraphics);

	// public abstract Graphics2D getGraphics();

	public abstract ForegroundStyle getDefaultForeground();

	public abstract ForegroundStyle getCurrentForeground();

	public abstract void setDefaultForeground(ForegroundStyle aForegound);

	// public abstract void setStroke(Stroke aStroke);

	public abstract void useForegroundStyle(ForegroundStyle aStyle);

	public abstract TextStyle getCurrentTextStyle();

	public abstract BackgroundStyle getDefaultBackground();

	public abstract void setDefaultBackground(BackgroundStyle aBackground);

	public abstract void useBackgroundStyle(BackgroundStyle aStyle);

	public abstract void setDefaultTextStyle(TextStyle aTextStyle);

	public abstract void useTextStyle(TextStyle aStyle);

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

	public abstract FGERectangle drawString(String text, FGEPoint location, int orientation, HorizontalTextAlignment alignment);

	public abstract FGERectangle drawString(String text, double x, double y, int orientation, HorizontalTextAlignment alignment);

	public abstract FGERectangle drawString(String text, FGEPoint location, HorizontalTextAlignment alignment);

	public abstract FGERectangle drawString(String text, double x, double y, HorizontalTextAlignment alignment);

}
