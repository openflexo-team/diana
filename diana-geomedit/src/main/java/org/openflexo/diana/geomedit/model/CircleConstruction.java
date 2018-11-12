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

import org.openflexo.diana.geom.DianaCircle;
import org.openflexo.diana.geomedit.model.CircleConstruction.CircleConstructionImpl;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;

@ModelEntity(isAbstract = true)
@ImplementationClass(CircleConstructionImpl.class)
@Imports({ @Import(CircleReference.class), @Import(CircleWithCenterAndPointConstruction.class) })
public interface CircleConstruction extends EllipsConstruction<DianaCircle> {

	@PropertyIdentifier(type = Double.class)
	public static final String CENTER_X_KEY = "centerX";
	@PropertyIdentifier(type = Double.class)
	public static final String CENTER_Y_KEY = "centerY";
	@PropertyIdentifier(type = Double.class)
	public static final String RADIUS_KEY = "radius";

	@Getter(value = CENTER_X_KEY, defaultValue = "0.0")
	public double getCenterX();

	@Setter(CENTER_X_KEY)
	public void setCenterX(double value);

	@Getter(value = CENTER_Y_KEY, defaultValue = "0.0")
	public double getCenterY();

	@Setter(CENTER_Y_KEY)
	public void setCenterY(double value);

	@Getter(value = RADIUS_KEY, defaultValue = "0.0")
	public double getRadius();

	@Setter(RADIUS_KEY)
	public void setRadius(double value);

	public DianaCircle getCircle();

	public static abstract class CircleConstructionImpl extends EllipsConstructionImpl<DianaCircle> implements CircleConstruction {

		@Override
		public String getBaseName() {
			return "Circle";
		}

		@Override
		public final DianaCircle getCircle() {
			return getData();
		}

		@Override
		protected abstract DianaCircle computeData();

		@Override
		public double getCenterX() {
			return getCircle().getCenterX();
		}

		@Override
		public void setCenterX(double centerX) {
			if (centerX != getCenterX()) {
				double oldCenterX = getCenterX();
				getCircle().x = centerX - getCircle().getRadius();
				getPropertyChangeSupport().firePropertyChange(CENTER_X_KEY, oldCenterX, centerX);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getCenterY() {
			return getCircle().getCenterY();
		}

		@Override
		public void setCenterY(double centerY) {
			if (centerY != getCenterY()) {
				double oldCenterY = getCenterY();
				getCircle().y = centerY - getCircle().getRadius();
				getPropertyChangeSupport().firePropertyChange(CENTER_Y_KEY, oldCenterY, centerY);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getRadius() {
			return getCircle().getRadius();
		}

		@Override
		public void setRadius(double aRadius) {
			if (aRadius != getRadius()) {
				double oldRadius = getRadius();
				getCircle().setRadius(aRadius);
				getPropertyChangeSupport().firePropertyChange(CENTER_X_KEY, oldRadius, aRadius);
				notifyGeometryChanged();
			}
		}

	}

}
