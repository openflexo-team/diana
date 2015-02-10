/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.fge.impl;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ColorBackgroundStyle;
import org.openflexo.fge.Drawing.RootNode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEDrawingGraphics;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class RootNodeImpl<M> extends ContainerNodeImpl<M, DrawingGraphicalRepresentation> implements RootNode<M> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(RootNodeImpl.class.getPackage().getName());

	private BackgroundStyle bgStyle;

	private static FGECachedModelFactory BACKGROUND_FACTORY = null;

	static {
		try {
			BACKGROUND_FACTORY = new FGECachedModelFactory();
		} catch (ModelDefinitionException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	protected RootNodeImpl(DrawingImpl<M> drawing, M drawable, DrawingGRBinding<M> grBinding) {
		super(drawing, drawable, grBinding, null);
		startDrawableObserving();
	}

	@Override
	public boolean delete() {
		if (!isDeleted()) {
			stopDrawableObserving();
			super.delete();
			finalizeDeletion();
			return true;
		}
		return false;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// System.out.println("******************************** Received " + evt.getPropertyName() + " " + evt);
		super.propertyChange(evt);
	}

	private BackgroundStyle getBGStyle() {
		if (bgStyle == null && getGraphicalRepresentation() != null) {
			bgStyle = BACKGROUND_FACTORY.makeColoredBackground(getGraphicalRepresentation().getBackgroundColor());
		}
		return bgStyle;
	}

	/**
	 * Return bounds relative to parent container
	 * 
	 * @return
	 */
	@Override
	public FGERectangle getBounds() {
		return new FGERectangle(0, 0, getWidth(), getHeight());
	}

	@Override
	public int getViewX(double scale) {
		return 0;
	}

	@Override
	public int getViewY(double scale) {
		return 0;
	}

	@Override
	public int getViewWidth(double scale) {
		return (int) (getWidth() * scale);
	}

	@Override
	public int getViewHeight(double scale) {
		return (int) (getHeight() * scale);
	}

	@Override
	public FGERectangle getNormalizedBounds() {
		return new FGERectangle(0, 0, getWidth(), getHeight(), Filling.FILLED);
	}

	@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		return AffineTransform.getScaleInstance(scale, scale);

		/*AffineTransform returned = AffineTransform.getScaleInstance(getWidth(), getHeight());
		if (scale != 1) {
			returned.preConcatenate(AffineTransform.getScaleInstance(scale,scale));
		}
		return returned;*/
	}

	@Override
	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale) {
		return AffineTransform.getScaleInstance(1 / scale, 1 / scale);

		/*AffineTransform returned = new AffineTransform();
		if (scale != 1) returned = AffineTransform.getScaleInstance(1/scale, 1/scale);
		returned.preConcatenate(AffineTransform.getScaleInstance(1/getWidth(),1/getHeight()));
		return returned;*/
	}

	@Override
	public boolean isContainedInSelection(Rectangle drawingViewSelection, double scale) {
		return false;
	}

	@Override
	public List<ControlArea<?>> getControlAreas() {
		// No control areas are declared for the root node
		return Collections.emptyList();
	}

	/**
	 * Return flag indicating if this node should be displayed, relatively to the value returned by visible feature in
	 * {@link GraphicalRepresentation}, and the structure of the tree (the parent should be visible too)<br>
	 * Here, as a root node, no parent node is expected to be set and visible
	 */
	@Override
	public boolean shouldBeDisplayed() {
		if (!isValidated()) {
			return false;
		}
		return getGraphicalRepresentation().getIsVisible();
	}

	@Override
	public String toString() {
		return "[" + Integer.toHexString(hashCode()) + "]Root[" + getWidth() + "x" + getHeight() + "]:" + getDrawable();
	}

	@Override
	public final boolean hasText() {
		return false;
	}

	@Override
	public boolean hasContainedLabel() {
		return false;
	}

	@Override
	public boolean hasFloatingLabel() {
		return false;
	}

	@Override
	public FGEDimension getRequiredLabelSize() {
		return null;
	}

	@Override
	public void paint(FGEDrawingGraphics g) {

		// If there is a decoration painter and decoration should be painted BEFORE shape, fo it now
		if (getGraphicalRepresentation().getDecorationPainter() != null
				&& getGraphicalRepresentation().getDecorationPainter().paintBeforeDrawing()) {
			getGraphicalRepresentation().getDecorationPainter().paintDecoration(g.getDrawingDecorationGraphics());
		}

		if (!(getBGStyle() instanceof ColorBackgroundStyle)
				|| !((ColorBackgroundStyle) getBGStyle()).getColor().equals(getGraphicalRepresentation().getBackgroundColor())) {
			bgStyle = BACKGROUND_FACTORY.makeColoredBackground(getGraphicalRepresentation().getBackgroundColor());
		}

		ForegroundStyle fgStyle = BACKGROUND_FACTORY.makeForegroundStyle(Color.DARK_GRAY);

		g.setDefaultForeground(fgStyle);
		g.setDefaultBackground(getBGStyle());
		if (getGraphicalRepresentation().getDrawWorkingArea()) {
			getGraphicalRepresentation().getWorkingArea().paint(g);
		}
		// If there is a decoration painter and decoration should be painted BEFORE shape, fo it now
		if (getGraphicalRepresentation().getDecorationPainter() != null
				&& !getGraphicalRepresentation().getDecorationPainter().paintBeforeDrawing()) {
			getGraphicalRepresentation().getDecorationPainter().paintDecoration(g.getDrawingDecorationGraphics());
		}
	}

	/**
	 * Convenient method used to retrieve 'drawWorkingArea' property value
	 */
	@Override
	public boolean getDrawWorkingArea() {
		return getPropertyValue(DrawingGraphicalRepresentation.DRAW_WORKING_AREA);
	}

	/**
	 * Convenient method used to set 'drawWorkingArea' property value
	 */
	@Override
	public void setDrawWorkingArea(boolean drawWorkingArea) {
		setPropertyValue(DrawingGraphicalRepresentation.DRAW_WORKING_AREA, drawWorkingArea);
	}

}
