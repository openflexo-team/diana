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
import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import org.openflexo.diana.BackgroundStyle;
import org.openflexo.diana.BackgroundStyle.BackgroundStyleType;
import org.openflexo.diana.Drawing;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.GeometricGraphicalRepresentation;
import org.openflexo.diana.control.MouseControl.MouseButton;
import org.openflexo.diana.control.PredefinedMouseClickControlActionType;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.notifications.DianaAttributeNotification;
import org.openflexo.toolbox.ToolBox;

public abstract class GeometricGraphicalRepresentationImpl extends GraphicalRepresentationImpl implements GeometricGraphicalRepresentation {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GeometricGraphicalRepresentation.class.getPackage().getName());

	// *******************************************************************************
	// * Inner classes *
	// *******************************************************************************

	// *******************************************************************************
	// * Fields *
	// *******************************************************************************

	// private int layer = DianaConstants.DEFAULT_OBJECT_LAYER;
	private ForegroundStyle foreground;
	private BackgroundStyle background;

	private DianaArea geometricObject;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public GeometricGraphicalRepresentationImpl() {
		super();
	}

	@SuppressWarnings("unused")
	@Deprecated
	private GeometricGraphicalRepresentationImpl(DianaArea anObject, Object aDrawable, Drawing<?> aDrawing) {
		this();
		// setDrawable(aDrawable);
		// setDrawing(aDrawing);

		foreground = getFactory().makeForegroundStyle(Color.BLACK);
		// foreground.setGraphicalRepresentation(this);
		foreground.getPropertyChangeSupport().addPropertyChangeListener(this);

		background = getFactory().makeColoredBackground(Color.WHITE);
		// background.setGraphicalRepresentation(this);
		background.getPropertyChangeSupport().addPropertyChangeListener(this);

		setGeometricObject(anObject);

		addToMouseClickControls(
				getFactory().makeMouseClickControl("Selection", MouseButton.LEFT, 1, PredefinedMouseClickControlActionType.SELECTION));
		if (ToolBox.isMacOS()) {
			addToMouseClickControls(getFactory().makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT, 1,
					PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
		}
		else {
			addToMouseClickControls(getFactory().makeMouseControlClickControl("Multiple selection", MouseButton.LEFT, 1,
					PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
		}
	}

	/*@Override
	public Vector<GRProperty> getAllParameters() {
		Vector<GRProperty> returned = super.getAllParameters();
		GeometricParameters[] allParams = GeometricParameters.values();
		for (int i = 0; i < allParams.length; i++) {
			returned.add(allParams[i]);
		}
		return returned;
	}*/

	// ***************************************************************************
	// * Deletion *
	// ***************************************************************************

	@Override
	public boolean delete(Object... context) {
		if (background != null) {
			background.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		if (foreground != null) {
			foreground.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		return super.delete();
	}

	// *******************************************************************************
	// * Accessors *
	// *******************************************************************************

	@Override
	public ForegroundStyle getForeground() {
		return foreground;
	}

	@Override
	public void setForeground(ForegroundStyle aForeground) {
		DianaAttributeNotification<?> notification = requireChange(FOREGROUND, aForeground, false);
		if (notification != null) {
			if (foreground != null) {
				foreground.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			foreground = aForeground;
			if (aForeground != null) {
				aForeground.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			hasChanged(notification);
		}
	}

	@Override
	public boolean getNoStroke() {
		return foreground.getNoStroke();
	}

	@Override
	public void setNoStroke(boolean noStroke) {
		foreground.setNoStroke(noStroke);
	}

	@Override
	public BackgroundStyle getBackground() {
		return background;
	}

	@Override
	public void setBackground(BackgroundStyle aBackground) {
		DianaAttributeNotification<?> notification = requireChange(BACKGROUND, aBackground, false);
		if (notification != null) {
			// background = aBackground.clone();
			if (background != null) {
				background.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			background = aBackground;
			// background.setGraphicalRepresentation(this);
			if (aBackground != null) {
				aBackground.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			hasChanged(notification);
		}
	}

	@Override
	public BackgroundStyleType getBackgroundType() {
		return background.getBackgroundStyleType();
	}

	@Override
	public void setBackgroundType(BackgroundStyleType backgroundType) {
		if (backgroundType != getBackgroundType()) {
			setBackground(getFactory().makeBackground(backgroundType));
		}
	}

	/*@Override
	public int getLayer() {
		return layer;
	}
	
	@Override
	public void setLayer(int layer) {
		DianaNotification notification = requireChange(Parameters.layer, layer);
		if (notification != null) {
			this.layer = layer;
			hasChanged(notification);
		}
	}*/

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);

		if (evt.getSource() instanceof BackgroundStyle) {
			notifyAttributeChange(BACKGROUND);
		}
		if (evt.getSource() instanceof ForegroundStyle) {
			notifyAttributeChange(FOREGROUND);
		}
	}

	@Override
	public DianaArea getGeometricObject() {
		return geometricObject;
	}

	@Override
	public void setGeometricObject(DianaArea geometricObject) {
		DianaAttributeNotification<?> notification = requireChange(GEOMETRIC_OBJECT, geometricObject);
		if (notification != null) {
			this.geometricObject = geometricObject;
			hasChanged(notification);
		}
	}

	/*protected final void notifyCPDragged(String name, DianaPoint newLocation)
	{
		notifyGeometryChanged();
		rebuildControlPoints();
	}*/

}
