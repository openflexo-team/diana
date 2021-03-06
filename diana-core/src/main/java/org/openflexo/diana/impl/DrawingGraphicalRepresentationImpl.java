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

package org.openflexo.diana.impl;

import java.awt.Color;
import java.util.logging.Logger;

import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.graphics.DrawingDecorationPainter;
import org.openflexo.diana.notifications.DrawingNeedsToBeRedrawn;
import org.openflexo.diana.notifications.DianaAttributeNotification;
import org.openflexo.diana.notifications.ObjectHasResized;
import org.openflexo.diana.notifications.ObjectResized;
import org.openflexo.diana.notifications.ObjectWillResize;

public abstract class DrawingGraphicalRepresentationImpl extends ContainerGraphicalRepresentationImpl
		implements DrawingGraphicalRepresentation {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DrawingGraphicalRepresentation.class.getPackage().getName());

	private Color backgroundColor = Color.WHITE;

	private Color rectangleSelectingSelectionColor = Color.BLUE;
	private Color focusColor = Color.RED;
	private Color selectionColor = Color.BLUE;
	private boolean drawWorkingArea = false;
	private boolean isResizable = false;
	protected DrawingDecorationPainter decorationPainter;

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public DrawingGraphicalRepresentationImpl() {
		super();
		width = DEFAULT_DRAWING_WIDTH;
		height = DEFAULT_DRAWING_HEIGHT;
	}

	/*
	@Deprecated
	private DrawingGraphicalRepresentationImpl(Drawing<?> aDrawing) {
		this();
		// setDrawing(aDrawing);
		// setDrawable(aDrawing != null ? aDrawing.getModel() : null);
	}
	
	@SuppressWarnings("unused")
	@Deprecated
	private DrawingGraphicalRepresentationImpl(Drawing<?> aDrawing, boolean initBasicControls) {
		this(aDrawing);
		if (initBasicControls) {
			addToMouseClickControls(getFactory().makeMouseClickControl("Drawing selection", MouseButton.LEFT, 1,
					PredefinedMouseClickControlActionType.SELECTION));
			addToMouseDragControls(getFactory().makeMouseDragControl("Rectangle selection", MouseButton.LEFT,
					PredefinedMouseDragControlActionType.RECTANGLE_SELECTING));
			addToMouseDragControls(getFactory().makeMouseDragControl("Zoom", MouseButton.RIGHT, PredefinedMouseDragControlActionType.ZOOM));
		}
		// width = DianaConstants.DEFAULT_DRAWING_WIDTH;
		// height = DianaConstants.DEFAULT_DRAWING_HEIGHT;
		// bgStyle = getFactory().makeColoredBackground(getBackgroundColor());
	}
	*/
	@Override
	public boolean delete(Object... context) {
		boolean returned = super.delete();
		/*if (graphics != null) {
			graphics.delete();
		}
		graphics = null;
		decorationGraphics = null;*/
		decorationPainter = null;
		return returned;
	}

	/*@Override
	public Vector<GRProperty> getAllParameters() {
		Vector<GRProperty> returned = super.getAllParameters();
		Parameters[] allParams = Parameters.values();
		for (int i = 0; i < allParams.length; i++) {
			returned.add(allParams[i]);
		}
		return returned;
	}*/

	/**
	 * Override parent behaviour by always returning true<br>
	 * IMPORTANT: a drawing graphical representation MUST be always validated
	 */
	/*@Override
	public final boolean isValidated() {
		return true;
	}*/

	// ***************************************************************************
	// * Cloning *
	// ***************************************************************************

	/*@Override
	public final void setsWith(GraphicalRepresentation gr) {
		super.setsWith(gr);
		if (gr instanceof DrawingGraphicalRepresentation) {
			for (Parameters p : Parameters.values()) {
				_setParameterValueWith(p, gr);
			}
		}
	}*/

	/*@Override
	public final void setsWith(GraphicalRepresentation gr, GRProperty... exceptedParameters) {
		super.setsWith(gr, exceptedParameters);
		if (gr instanceof ConnectorGraphicalRepresentation) {
			for (Parameters p : Parameters.values()) {
				boolean excepted = false;
				for (GRProperty ep : exceptedParameters) {
					if (p == ep) {
						excepted = true;
					}
				}
				if (!excepted) {
					_setParameterValueWith(p, gr);
				}
			}
		}
	}*/

	// *******************************************************************************
	// * Accessors *
	// *******************************************************************************

	@Override
	public DianaRectangle getWorkingArea() {
		return new DianaRectangle(0, 0, getWidth(), getHeight(), Filling.FILLED);
	}

	@Override
	public int getLayer() {
		return 0;
	}

	@Override
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	@Override
	public void setBackgroundColor(Color backgroundColor) {
		// logger.info("For "+this+" Set bg color to "+backgroundColor);

		DianaAttributeNotification<?> notification = requireChange(BACKGROUND_COLOR, backgroundColor);
		if (notification != null) {
			this.backgroundColor = backgroundColor;
			// bgStyle = getFactory().makeColoredBackground(backgroundColor);
			hasChanged(notification);
		}
	}

	@Override
	public Color getFocusColor() {
		return focusColor;
	}

	@Override
	public void setFocusColor(Color focusColor) {
		DianaAttributeNotification<?> notification = requireChange(FOCUS_COLOR, focusColor);
		if (notification != null) {
			this.focusColor = focusColor;
			hasChanged(notification);
		}
	}

	@Override
	public Color getSelectionColor() {
		return selectionColor;
	}

	@Override
	public void setSelectionColor(Color selectionColor) {
		DianaAttributeNotification<?> notification = requireChange(SELECTION_COLOR, selectionColor);
		if (notification != null) {
			this.selectionColor = selectionColor;
			hasChanged(notification);
		}
	}

	@Override
	public Color getRectangleSelectingSelectionColor() {
		return rectangleSelectingSelectionColor;
	}

	@Override
	public void setRectangleSelectingSelectionColor(Color selectionColor) {
		DianaAttributeNotification<?> notification = requireChange(RECTANGLE_SELECTING_SELECTION_COLOR, selectionColor);
		if (notification != null) {
			this.rectangleSelectingSelectionColor = selectionColor;
			hasChanged(notification);
		}
	}

	@Override
	public DrawingDecorationPainter getDecorationPainter() {
		return decorationPainter;
	}

	@Override
	public void setDecorationPainter(DrawingDecorationPainter aPainter) {
		// decorationGraphics = new DianaDrawingDecorationGraphicsImpl(this);
		decorationPainter = aPainter;
	}

	@Override
	public final String getText() {
		return null;
	}

	@Override
	public boolean getIsVisible() {
		return true;
	}

	@Override
	public boolean getDrawWorkingArea() {
		return drawWorkingArea;
	}

	@Override
	public void setDrawWorkingArea(boolean drawWorkingArea) {
		// logger.info("setDrawWorkingArea with "+drawWorkingArea);

		DianaAttributeNotification<?> notification = requireChange(DRAW_WORKING_AREA, drawWorkingArea);
		if (notification != null) {
			this.drawWorkingArea = drawWorkingArea;
			hasChanged(notification);
		}
	}

	@Override
	public boolean isResizable() {
		return isResizable;
	}

	@Override
	public void setIsResizable(boolean isResizable) {
		DianaAttributeNotification<?> notification = requireChange(IS_RESIZABLE, isResizable);
		if (notification != null) {
			this.isResizable = isResizable;
			hasChanged(notification);
		}
	}

	@Override
	public DianaDimension getSize() {
		return new DianaDimension(getWidth(), getHeight());
	}

	/**
	 * Notify that the object just resized
	 */
	@Override
	public void notifyObjectResized(DianaDimension oldSize) {
		notifyObservers(new ObjectResized(oldSize, getSize()));
	}

	/**
	 * Notify that the object will be resized
	 */
	@Override
	public void notifyObjectWillResize() {
		notifyObservers(new ObjectWillResize());
	}

	/**
	 * Notify that the object resizing has finished (take care that this just notify END of resize, this should NOT be used to notify a
	 * resizing: use notifyObjectResize() instead)
	 */
	@Override
	public void notifyObjectHasResized() {
		notifyObservers(new ObjectHasResized());
	}

	@Override
	public void notifyDrawingNeedsToBeRedrawn() {
		notifyObservers(new DrawingNeedsToBeRedrawn());
	}

	// *******************************************************************************
	// * Layout *
	// *******************************************************************************

}
