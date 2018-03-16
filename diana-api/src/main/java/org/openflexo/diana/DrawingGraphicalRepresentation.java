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

import org.openflexo.diana.geom.FGEDimension;
import org.openflexo.diana.geom.FGERectangle;
import org.openflexo.diana.graphics.DrawingDecorationPainter;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents the diagram as the top-level graphical representation <br>
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "DrawingGraphicalRepresentation")
public interface DrawingGraphicalRepresentation extends ContainerGraphicalRepresentation {

	// Property keys

	@PropertyIdentifier(type = Color.class)
	public static final String BACKGROUND_COLOR_KEY = "backgroundColor";
	@PropertyIdentifier(type = Color.class)
	public static final String RECTANGLE_SELECTING_SELECTION_COLOR_KEY = "rectangleSelectingSelectionColor";
	@PropertyIdentifier(type = Color.class)
	public static final String FOCUS_COLOR_KEY = "focusColor";
	@PropertyIdentifier(type = Color.class)
	public static final String SELECTION_COLOR_KEY = "selectionColor";
	@PropertyIdentifier(type = Boolean.class)
	public static final String DRAW_WORKING_AREA_KEY = "drawWorkingArea";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_RESIZABLE_KEY = "isResizable";

	public static GRProperty<Color> BACKGROUND_COLOR = GRProperty.getGRParameter(DrawingGraphicalRepresentation.class, BACKGROUND_COLOR_KEY,
			Color.class);
	public static GRProperty<Color> RECTANGLE_SELECTING_SELECTION_COLOR = GRProperty.getGRParameter(DrawingGraphicalRepresentation.class,
			RECTANGLE_SELECTING_SELECTION_COLOR_KEY, Color.class);
	public static GRProperty<Color> FOCUS_COLOR = GRProperty.getGRParameter(DrawingGraphicalRepresentation.class, FOCUS_COLOR_KEY,
			Color.class);
	public static GRProperty<Color> SELECTION_COLOR = GRProperty.getGRParameter(DrawingGraphicalRepresentation.class, SELECTION_COLOR_KEY,
			Color.class);
	public static GRProperty<Boolean> DRAW_WORKING_AREA = GRProperty.getGRParameter(DrawingGraphicalRepresentation.class,
			DRAW_WORKING_AREA_KEY, Boolean.TYPE);
	public static GRProperty<Boolean> IS_RESIZABLE = GRProperty.getGRParameter(DrawingGraphicalRepresentation.class, IS_RESIZABLE_KEY,
			Boolean.TYPE);

	/*public static enum DrawingParameters implements GRProperty {
		backgroundColor, width, height, rectangleSelectingSelectionColor, focusColor, selectionColor, drawWorkingArea, isResizable;
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = BACKGROUND_COLOR_KEY)
	@XMLAttribute
	public abstract Color getBackgroundColor();

	@Setter(value = BACKGROUND_COLOR_KEY)
	public abstract void setBackgroundColor(Color backgroundColor);

	@Override
	@Getter(value = WIDTH_KEY, defaultValue = "" + FGEConstants.DEFAULT_DRAWING_WIDTH)
	@XMLAttribute
	public abstract double getWidth();

	@Override
	@Getter(value = HEIGHT_KEY, defaultValue = "" + FGEConstants.DEFAULT_DRAWING_HEIGHT)
	@XMLAttribute
	public abstract double getHeight();

	@Getter(value = FOCUS_COLOR_KEY)
	@XMLAttribute
	public abstract Color getFocusColor();

	@Setter(value = FOCUS_COLOR_KEY)
	public abstract void setFocusColor(Color focusColor);

	@Getter(value = SELECTION_COLOR_KEY)
	@XMLAttribute
	public abstract Color getSelectionColor();

	@Setter(value = SELECTION_COLOR_KEY)
	public abstract void setSelectionColor(Color selectionColor);

	@Getter(value = RECTANGLE_SELECTING_SELECTION_COLOR_KEY)
	@XMLAttribute
	public abstract Color getRectangleSelectingSelectionColor();

	@Setter(value = RECTANGLE_SELECTING_SELECTION_COLOR_KEY)
	public abstract void setRectangleSelectingSelectionColor(Color selectionColor);

	@Getter(value = DRAW_WORKING_AREA_KEY, defaultValue = "true")
	@XMLAttribute
	public abstract boolean getDrawWorkingArea();

	@Setter(DRAW_WORKING_AREA_KEY)
	public abstract void setDrawWorkingArea(boolean drawWorkingArea);

	@Getter(value = IS_RESIZABLE_KEY, defaultValue = "false")
	@XMLAttribute
	public abstract boolean isResizable();

	@Setter(IS_RESIZABLE_KEY)
	public abstract void setIsResizable(boolean isResizable);

	public abstract FGERectangle getWorkingArea();

	// @Override
	// public abstract void paint(Graphics g, DianaEditor<?> controller);

	public abstract DrawingDecorationPainter getDecorationPainter();

	public abstract void setDecorationPainter(DrawingDecorationPainter aPainter);

	// public abstract Vector<GraphicalRepresentation> allGraphicalRepresentations();

	// public abstract void startConnectorObserving();

	@Override
	public abstract FGEDimension getSize();

	/**
	 * Notify that the object just resized
	 */
	public abstract void notifyObjectResized(FGEDimension oldSize);

	/**
	 * Notify that the object will be resized
	 */
	public abstract void notifyObjectWillResize();

	/**
	 * Notify that the object resizing has finished (take care that this just notify END of resize, this should NOT be used to notify a
	 * resizing: use notifyObjectResize() instead)
	 */
	public abstract void notifyObjectHasResized();

	public abstract void notifyDrawingNeedsToBeRedrawn();

	// public abstract FGEDrawingGraphics getGraphics();

	// public abstract ShapeGraphicalRepresentation getTopLevelShapeGraphicalRepresentation(FGEPoint p);

	// public abstract void performRandomLayout();

	// public abstract void performAutoLayout();

}
