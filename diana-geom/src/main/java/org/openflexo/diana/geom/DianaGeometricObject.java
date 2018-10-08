/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geom, a component of the software infrastructure 
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

package org.openflexo.diana.geom;

import java.util.List;
import java.util.Vector;

import org.openflexo.diana.geom.area.DianaArea;

/**
 * API describing a {@link DianaArea} as the well-defined object (and not the result of an algebric computation)
 * 
 * @author sylvain
 *
 * @param <O>
 *            type of represented {@link DianaGeometricObject}
 */
public interface DianaGeometricObject<O extends DianaGeometricObject<O>> extends DianaArea {

	public static final double EPSILON = 1e-10;

	public static enum Filling {
		NOT_FILLED, FILLED
	}

	@Override
	public DianaGeometricObject<? extends O> clone();

	public List<DianaPoint> getControlPoints();

	public static enum SimplifiedCardinalDirection {
		NORTH, EAST, SOUTH, WEST;

		public boolean isHorizontal() {
			return this == EAST || this == WEST;
		}

		public boolean isVertical() {
			return this == NORTH || this == SOUTH;
		}

		public double getRadians() {
			switch (this) {
				case NORTH:
					return Math.PI / 2;
				case SOUTH:
					return -Math.PI / 2;
				case EAST:
					return 0;
				case WEST:
					return Math.PI;
				default:
					return 0;
			}
		}

		public DianaPoint getNormalizedRepresentativePoint() {
			switch (this) {
				case NORTH:
					return new DianaPoint(0.5, 0.0);
				case SOUTH:
					return new DianaPoint(0.5, 1.0);
				case EAST:
					return new DianaPoint(1.0, 0.5);
				case WEST:
					return new DianaPoint(0.0, 0.5);
				default:
					return new DianaPoint();
			}
		}

		public SimplifiedCardinalDirection getOpposite() {
			if (this == EAST) {
				return WEST;
			}
			if (this == WEST) {
				return EAST;
			}
			if (this == SOUTH) {
				return NORTH;
			}
			if (this == NORTH) {
				return SOUTH;
			}
			return null;
		}

		public CardinalDirection getCardinalDirectionEquivalent() {
			switch (this) {
				case EAST:
					return CardinalDirection.EAST;
				case NORTH:
					return CardinalDirection.NORTH;
				case SOUTH:
					return CardinalDirection.SOUTH;
				case WEST:
					return CardinalDirection.WEST;
			}
			return null;
		}

		public static Vector<SimplifiedCardinalDirection> allDirections() {
			Vector<SimplifiedCardinalDirection> returned = new Vector<>();
			for (SimplifiedCardinalDirection o : values()) {
				returned.add(o);
			}
			return returned;
		}

		public static Vector<SimplifiedCardinalDirection> uniqueDirection(SimplifiedCardinalDirection aDirection) {
			Vector<SimplifiedCardinalDirection> returned = new Vector<>();
			returned.add(aDirection);
			return returned;
		}

		public static Vector<SimplifiedCardinalDirection> someDirections(SimplifiedCardinalDirection... someDirections) {
			Vector<SimplifiedCardinalDirection> returned = new Vector<>();
			for (SimplifiedCardinalDirection o : someDirections) {
				returned.add(o);
			}
			return returned;
		}

		public static Vector<SimplifiedCardinalDirection> allDirectionsExcept(SimplifiedCardinalDirection aDirection) {
			Vector<SimplifiedCardinalDirection> returned = new Vector<>();
			for (SimplifiedCardinalDirection o : values()) {
				if (o != aDirection) {
					returned.add(o);
				}
			}
			return returned;
		}

		public static Vector<SimplifiedCardinalDirection> allDirectionsExcept(SimplifiedCardinalDirection... someDirections) {
			Vector<SimplifiedCardinalDirection> returned = new Vector<>();
			for (SimplifiedCardinalDirection o : values()) {
				boolean isToBeExcepted = false;
				for (int i = 0; i < someDirections.length; i++) {
					if (o == someDirections[i]) {
						isToBeExcepted = true;
					}
				}
				if (!isToBeExcepted) {
					returned.add(o);
				}
			}
			return returned;
		}

		public static Vector<SimplifiedCardinalDirection> allDirectionsExcept(Vector<SimplifiedCardinalDirection> someDirections) {
			Vector<SimplifiedCardinalDirection> returned = new Vector<>();
			for (SimplifiedCardinalDirection o : values()) {
				boolean isToBeExcepted = false;
				for (SimplifiedCardinalDirection o2 : someDirections) {
					if (o == o2) {
						isToBeExcepted = true;
					}
				}
				if (!isToBeExcepted) {
					returned.add(o);
				}
			}
			return returned;
		}

		public static Vector<SimplifiedCardinalDirection> intersection(Vector<SimplifiedCardinalDirection> someDirections,
				Vector<SimplifiedCardinalDirection> someOtherDirections) {
			Vector<SimplifiedCardinalDirection> returned = new Vector<>();
			for (SimplifiedCardinalDirection o : someDirections) {
				if (someOtherDirections.contains(o)) {
					returned.add(o);
				}
			}
			return returned;
		}

	}

	public static enum CardinalQuadrant {
		NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST;

		public SimplifiedCardinalDirection getVerticalComponent() {
			if (this == NORTH_EAST || this == NORTH_WEST) {
				return SimplifiedCardinalDirection.NORTH;
			}
			if (this == SOUTH_EAST || this == SOUTH_WEST) {
				return SimplifiedCardinalDirection.SOUTH;
			}
			return null;
		}

		public SimplifiedCardinalDirection getHorizonalComponent() {
			if (this == NORTH_WEST || this == SOUTH_WEST) {
				return SimplifiedCardinalDirection.WEST;
			}
			if (this == NORTH_EAST || this == SOUTH_EAST) {
				return SimplifiedCardinalDirection.EAST;
			}
			return null;
		}
	}

	public static enum CardinalDirection {
		NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST;
	}

	public abstract String getStringRepresentation();

}
