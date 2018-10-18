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

package org.openflexo.diana.geomedit.model;

import org.openflexo.diana.geom.DianaEllips;
import org.openflexo.diana.geomedit.model.EllipsConstruction.EllipsConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.EllipsGraphicalRepresentation;
import org.openflexo.diana.geomedit.model.gr.GeometricObjectGraphicalRepresentation;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;

@ModelEntity(isAbstract = true)
@ImplementationClass(EllipsConstructionImpl.class)
@Imports({ @Import(CircleConstruction.class), @Import(EllipsReference.class) })
public interface EllipsConstruction<E extends DianaEllips> extends GeometricConstruction<E> {

	@PropertyIdentifier(type = Double.class)
	public static final String X_KEY = "x";
	@PropertyIdentifier(type = Double.class)
	public static final String Y_KEY = "y";
	@PropertyIdentifier(type = Double.class)
	public static final String WIDTH_KEY = "width";
	@PropertyIdentifier(type = Double.class)
	public static final String HEIGHT_KEY = "height";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_FILLED_KEY = "isFilled";

	@Getter(value = X_KEY, defaultValue = "0.0")
	public double getX();

	@Setter(X_KEY)
	public void setX(double value);

	@Getter(value = Y_KEY, defaultValue = "0.0")
	public double getY();

	@Setter(Y_KEY)
	public void setY(double value);

	@Getter(value = WIDTH_KEY, defaultValue = "0.0")
	public double getWidth();

	@Setter(WIDTH_KEY)
	public void setWidth(double value);

	@Getter(value = HEIGHT_KEY, defaultValue = "0.0")
	public double getHeight();

	@Setter(HEIGHT_KEY)
	public void setHeight(double value);

	@Getter(value = IS_FILLED_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsFilled();

	@Setter(IS_FILLED_KEY)
	public void setIsFilled(boolean isFilled);

	public DianaEllips getEllips();

	public abstract class EllipsConstructionImpl<E extends DianaEllips> extends GeometricConstructionImpl<E>
			implements EllipsConstruction<E> {

		@Override
		public String getBaseName() {
			return "Ellips";
		}

		@Override
		public final DianaEllips getEllips() {
			return getData();
		}

		@Override
		public GeometricObjectGraphicalRepresentation<E> makeNewConstructionGR(GeometricConstructionFactory factory) {
			EllipsGraphicalRepresentation returned = factory.newInstance(EllipsGraphicalRepresentation.class);
			return (GeometricObjectGraphicalRepresentation<E>) returned;
		}

		@Override
		protected abstract E computeData();

		@Override
		public double getX() {
			return getEllips().getX();
		}

		@Override
		public void setX(double x) {
			if (x != getX()) {
				double oldX = getX();
				getEllips().x = x;
				getPropertyChangeSupport().firePropertyChange(X_KEY, oldX, x);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getWidth() {
			return getEllips().getWidth();
		}

		@Override
		public void setWidth(double width) {
			if (width != getWidth()) {
				double oldWidth = getWidth();
				getEllips().width = width;
				getPropertyChangeSupport().firePropertyChange(WIDTH_KEY, oldWidth, width);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getY() {
			return getEllips().getY();
		}

		@Override
		public void setY(double y) {
			if (y != getY()) {
				double oldY = getY();
				getEllips().y = y;
				getPropertyChangeSupport().firePropertyChange(Y_KEY, oldY, y);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getHeight() {
			return getEllips().getHeight();
		}

		@Override
		public void setHeight(double height) {
			if (height != getHeight()) {
				double oldHeight = getHeight();
				getEllips().height = height;
				getPropertyChangeSupport().firePropertyChange(HEIGHT_KEY, oldHeight, height);
				notifyGeometryChanged();
			}
		}

		@Override
		public void notifyGeometryChanged() {
			super.notifyGeometryChanged();
			getPropertyChangeSupport().firePropertyChange(X_KEY, null, getX());
			getPropertyChangeSupport().firePropertyChange(Y_KEY, null, getY());
			getPropertyChangeSupport().firePropertyChange(WIDTH_KEY, null, getWidth());
			getPropertyChangeSupport().firePropertyChange(HEIGHT_KEY, null, getHeight());
		}

		@Override
		public void setIsFilled(boolean isFilled) {
			performSuperSetter(IS_FILLED_KEY, isFilled);
			refresh();
			notifyGeometryChanged();
		}

	}
}
