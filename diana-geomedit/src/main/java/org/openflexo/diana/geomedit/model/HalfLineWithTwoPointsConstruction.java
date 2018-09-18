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

import org.openflexo.diana.geomedit.model.HalfLineWithTwoPointsConstruction.HalfLineWithTwoPointsConstructionImpl;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(HalfLineWithTwoPointsConstructionImpl.class)
@XMLElement
public interface HalfLineWithTwoPointsConstruction extends HalfLineConstruction {

	@PropertyIdentifier(type = PointConstruction.class)
	public static final String LIMIT_POINT_CONSTRUCTION_KEY = "limitPointConstruction";
	@PropertyIdentifier(type = PointConstruction.class)
	public static final String OPPOSITE_POINT_CONSTRUCTION_KEY = "oppositePointConstruction";

	@Getter(value = LIMIT_POINT_CONSTRUCTION_KEY)
	@XMLElement(context = "Limit_")
	public PointConstruction getLimitPointConstruction();

	@Setter(value = LIMIT_POINT_CONSTRUCTION_KEY)
	public void setLimitPointConstruction(PointConstruction limitPointConstruction);

	@Getter(value = OPPOSITE_POINT_CONSTRUCTION_KEY)
	@XMLElement(context = "Opposite_")
	public PointConstruction getOppositePointConstruction();

	@Setter(value = OPPOSITE_POINT_CONSTRUCTION_KEY)
	public void setOppositePointConstruction(PointConstruction oppositePointConstruction);

	public static abstract class HalfLineWithTwoPointsConstructionImpl extends HalfLineConstructionImpl
			implements HalfLineWithTwoPointsConstruction {

		private PointConstruction limitPointConstruction;
		private PointConstruction oppositePointConstruction;

		@Override
		public PointConstruction getLimitPointConstruction() {
			return limitPointConstruction;
		}

		@Override
		public void setLimitPointConstruction(PointConstruction limitPointConstruction) {
			if ((limitPointConstruction == null && this.limitPointConstruction != null)
					|| (limitPointConstruction != null && !limitPointConstruction.equals(this.limitPointConstruction))) {
				PointConstruction oldValue = this.limitPointConstruction;
				this.limitPointConstruction = limitPointConstruction;
				getPropertyChangeSupport().firePropertyChange("limitPointConstruction", oldValue, limitPointConstruction);
			}
		}

		@Override
		public PointConstruction getOppositePointConstruction() {
			return oppositePointConstruction;
		}

		@Override
		public void setOppositePointConstruction(PointConstruction oppositePointConstruction) {
			if ((oppositePointConstruction == null && this.oppositePointConstruction != null)
					|| (oppositePointConstruction != null && !oppositePointConstruction.equals(this.oppositePointConstruction))) {
				PointConstruction oldValue = this.oppositePointConstruction;
				this.oppositePointConstruction = oppositePointConstruction;
				getPropertyChangeSupport().firePropertyChange("oppositePointConstruction", oldValue, oppositePointConstruction);
			}
		}

		@Override
		protected FGEHalfLine computeData() {
			return new FGEHalfLine(limitPointConstruction.getPoint(), oppositePointConstruction.getPoint());
		}

		@Override
		public String toString() {
			return "HalfLineWithTwoPointsConstruction[\n" + "> " + limitPointConstruction.toString() + "\n> "
					+ oppositePointConstruction.toString() + "\n]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { limitPointConstruction, oppositePointConstruction };
			return returned;
		}

	}

}
