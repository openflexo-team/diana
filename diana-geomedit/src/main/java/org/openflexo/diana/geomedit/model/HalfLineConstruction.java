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

import org.openflexo.diana.geomedit.model.HalfLineConstruction.HalfLineConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.HalfLineGraphicalRepresentation;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;

@ModelEntity(isAbstract = true)
@ImplementationClass(HalfLineConstructionImpl.class)
@Imports({ @Import(HalfLineWithTwoPointsConstruction.class) })
public interface HalfLineConstruction extends GeometricConstruction<FGEHalfLine> {

	@PropertyIdentifier(type = Double.class)
	public static final String LIMIT_X_KEY = "limitX";
	@PropertyIdentifier(type = Double.class)
	public static final String LIMIT_Y_KEY = "limitY";
	@PropertyIdentifier(type = Double.class)
	public static final String OPPOSITE_X_KEY = "oppositeX";
	@PropertyIdentifier(type = Double.class)
	public static final String OPPOSITE_Y_KEY = "oppositeY";

	@Getter(value = LIMIT_X_KEY, defaultValue = "0.0")
	public double getLimitX();

	@Setter(LIMIT_X_KEY)
	public void setLimitX(double value);

	@Getter(value = LIMIT_Y_KEY, defaultValue = "0.0")
	public double getLimitY();

	@Setter(LIMIT_Y_KEY)
	public void setLimitY(double value);

	@Getter(value = OPPOSITE_X_KEY, defaultValue = "0.0")
	public double getOppositeX();

	@Setter(OPPOSITE_X_KEY)
	public void setOppositeX(double value);

	@Getter(value = OPPOSITE_Y_KEY, defaultValue = "0.0")
	public double getOppositeY();

	@Setter(OPPOSITE_Y_KEY)
	public void setOppositeY(double value);

	public FGEHalfLine getHalfLine();

	public static abstract class HalfLineConstructionImpl extends GeometricConstructionImpl<FGEHalfLine> implements HalfLineConstruction {

		@Override
		public String getBaseName() {
			return "HalfLine";
		}

		@Override
		public final FGEHalfLine getHalfLine() {
			return getData();
		}

		@Override
		public HalfLineGraphicalRepresentation makeNewConstructionGR(GeometricConstructionFactory factory) {
			HalfLineGraphicalRepresentation returned = factory.newInstance(HalfLineGraphicalRepresentation.class);
			return returned;
		}

		@Override
		protected abstract FGEHalfLine computeData();

		@Override
		public double getLimitX() {
			return getHalfLine().getX1();
		}

		@Override
		public void setLimitX(double limitX) {
			if (limitX != getLimitX()) {
				double oldLimitX = getLimitX();
				getHalfLine().setX1(limitX);
				getPropertyChangeSupport().firePropertyChange(LIMIT_X_KEY, oldLimitX, limitX);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getLimitY() {
			return getHalfLine().getY1();
		}

		@Override
		public void setLimitY(double limitY) {
			if (limitY != getLimitY()) {
				double oldLimitY = getLimitY();
				getHalfLine().setY1(limitY);
				getPropertyChangeSupport().firePropertyChange(LIMIT_Y_KEY, oldLimitY, limitY);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getOppositeX() {
			return getHalfLine().getX2();
		}

		@Override
		public void setOppositeX(double oppositeX) {
			if (oppositeX != getOppositeX()) {
				double oldOppositeX = getOppositeX();
				getHalfLine().setX2(oppositeX);
				getPropertyChangeSupport().firePropertyChange(OPPOSITE_X_KEY, oldOppositeX, oppositeX);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getOppositeY() {
			return getHalfLine().getY2();
		}

		@Override
		public void setOppositeY(double oppositeY) {
			if (oppositeY != getOppositeY()) {
				double oldOppositeY = getOppositeY();
				getHalfLine().setY2(oppositeY);
				getPropertyChangeSupport().firePropertyChange(OPPOSITE_Y_KEY, oldOppositeY, oppositeY);
				notifyGeometryChanged();
			}
		}

	}

}
