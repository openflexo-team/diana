/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geomedit, a component of the software infrastructure 
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

package org.openflexo.fge.geomedit.construction;

import org.openflexo.diana.geomedit.model.GeometricConstructionFactory;
import org.openflexo.diana.geomedit.model.gr.RoundRectangleGraphicalRepresentation;
import org.openflexo.fge.geom.FGERoundRectangle;
import org.openflexo.fge.geomedit.construction.RoundRectangleConstruction.RoundRectangleConstructionImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;

@ModelEntity(isAbstract = true)
@ImplementationClass(RoundRectangleConstructionImpl.class)
@Imports({ @Import(RoundRectangleWithTwoPointsConstruction.class) })
public interface RoundRectangleConstruction extends GeometricConstruction<FGERoundRectangle> {

	@PropertyIdentifier(type = Double.class)
	public static final String X_KEY = "x";
	@PropertyIdentifier(type = Double.class)
	public static final String Y_KEY = "y";
	@PropertyIdentifier(type = Double.class)
	public static final String WIDTH_KEY = "width";
	@PropertyIdentifier(type = Double.class)
	public static final String HEIGHT_KEY = "height";
	@PropertyIdentifier(type = Double.class)
	public static final String ARC_WIDTH_KEY = "arcWidth";
	@PropertyIdentifier(type = Double.class)
	public static final String ARC_HEIGHT_KEY = "arcHeight";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_FILLED_KEY = "isFilled";

	@Getter(X_KEY)
	public double getX();

	@Setter(X_KEY)
	public void setX(double value);

	@Getter(Y_KEY)
	public double getY();

	@Setter(Y_KEY)
	public void setY(double value);

	@Getter(WIDTH_KEY)
	public double getWidth();

	@Setter(WIDTH_KEY)
	public void setWidth(double value);

	@Getter(HEIGHT_KEY)
	public double getHeight();

	@Setter(HEIGHT_KEY)
	public void setHeight(double value);

	@Getter(IS_FILLED_KEY)
	public boolean getIsFilled();

	@Setter(IS_FILLED_KEY)
	public void setIsFilled(boolean isFilled);

	@Getter(ARC_WIDTH_KEY)
	public double getArcWidth();

	@Setter(ARC_WIDTH_KEY)
	public void setArcWidth(double arcWidth);

	@Getter(ARC_HEIGHT_KEY)
	public double getArcHeight();

	@Setter(ARC_HEIGHT_KEY)
	public void setArcHeight(double arcHeight);

	public FGERoundRectangle getRectangle();

	public static abstract class RoundRectangleConstructionImpl extends GeometricConstructionImpl<FGERoundRectangle>
			implements RoundRectangleConstruction {

		@Override
		public final FGERoundRectangle getRectangle() {
			return getData();
		}

		@Override
		public RoundRectangleGraphicalRepresentation makeNewConstructionGR(GeometricConstructionFactory factory) {
			RoundRectangleGraphicalRepresentation returned = factory.newInstance(RoundRectangleGraphicalRepresentation.class);
			return returned;
		}

		@Override
		protected abstract FGERoundRectangle computeData();

		@Override
		public double getX() {
			return getRectangle().getX();
		}

		@Override
		public void setX(double x) {
			if (x != getX()) {
				double oldX = getX();
				getRectangle().x = x;
				getPropertyChangeSupport().firePropertyChange(X_KEY, oldX, x);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getWidth() {
			return getRectangle().getWidth();
		}

		@Override
		public void setWidth(double width) {
			if (width != getWidth()) {
				double oldWidth = getWidth();
				getRectangle().width = width;
				getPropertyChangeSupport().firePropertyChange(WIDTH_KEY, oldWidth, width);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getY() {
			return getRectangle().getY();
		}

		@Override
		public void setY(double y) {
			if (y != getY()) {
				double oldY = getY();
				getRectangle().y = y;
				getPropertyChangeSupport().firePropertyChange(Y_KEY, oldY, y);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getHeight() {
			return getRectangle().getHeight();
		}

		@Override
		public void setHeight(double height) {
			if (height != getHeight()) {
				double oldHeight = getHeight();
				getRectangle().height = height;
				getPropertyChangeSupport().firePropertyChange(HEIGHT_KEY, oldHeight, height);
				notifyGeometryChanged();
			}
		}

		@Override
		public boolean getIsFilled() {
			return getRectangle().getIsFilled();
		}

		@Override
		public void setIsFilled(boolean filled) {
			if (filled != getIsFilled()) {
				getRectangle().setIsFilled(filled);
				getPropertyChangeSupport().firePropertyChange(IS_FILLED_KEY, !filled, filled);
			}
		}

		@Override
		public double getArcHeight() {
			return getRectangle().getArcHeight();
		}

		@Override
		public void setArcHeight(double arcHeight) {
			if (arcHeight != getArcHeight()) {
				double oldHeight = getArcHeight();
				getRectangle().archeight = arcHeight;
				getPropertyChangeSupport().firePropertyChange(ARC_HEIGHT_KEY, oldHeight, arcHeight);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getArcWidth() {
			return getRectangle().getArcWidth();
		}

		@Override
		public void setArcWidth(double arcWidth) {
			if (arcWidth != getArcWidth()) {
				double oldWidth = getArcWidth();
				getRectangle().arcwidth = arcWidth;
				getPropertyChangeSupport().firePropertyChange(ARC_WIDTH_KEY, oldWidth, arcWidth);
				notifyGeometryChanged();
			}
		}
	}

}
