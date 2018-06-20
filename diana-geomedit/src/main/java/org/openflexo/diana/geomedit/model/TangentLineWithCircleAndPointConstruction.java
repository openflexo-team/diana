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

import java.util.logging.Logger;

import org.openflexo.diana.geomedit.model.TangentLineWithCircleAndPointConstruction.TangentLineWithCircleAndPointConstructionImpl;
import org.openflexo.fge.geom.FGECircle;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(TangentLineWithCircleAndPointConstructionImpl.class)
@XMLElement
public interface TangentLineWithCircleAndPointConstruction extends LineConstruction {

	@PropertyIdentifier(type = CircleConstruction.class)
	public static final String CIRCLE_CONSTRUCTION_KEY = "circleConstruction";
	@PropertyIdentifier(type = PointConstruction.class)
	public static final String POINT_CONSTRUCTION_KEY = "pointConstruction";
	@PropertyIdentifier(type = PointConstruction.class)
	public static final String CHOOSING_POINT_CONSTRUCTION_KEY = "choosingPointConstruction";

	@Getter(value = CIRCLE_CONSTRUCTION_KEY)
	public CircleConstruction getCircleConstruction();

	@Setter(value = CIRCLE_CONSTRUCTION_KEY)
	public void setCircleConstruction(CircleConstruction pointConstruction);

	@Getter(value = POINT_CONSTRUCTION_KEY)
	public PointConstruction getPointConstruction();

	@Setter(value = POINT_CONSTRUCTION_KEY)
	public void setPointConstruction(PointConstruction pointConstruction);

	@Getter(value = CHOOSING_POINT_CONSTRUCTION_KEY)
	public PointConstruction getChoosingPointConstruction();

	@Setter(value = CHOOSING_POINT_CONSTRUCTION_KEY)
	public void setChoosingPointConstruction(PointConstruction pointConstruction);

	public static abstract class TangentLineWithCircleAndPointConstructionImpl extends LineConstructionImpl
			implements TangentLineWithCircleAndPointConstruction {

		private static final Logger logger = FlexoLogger.getLogger(TangentLineWithCircleAndPointConstruction.class.getPackage().getName());

		private CircleConstruction circleConstruction;
		private PointConstruction pointConstruction;
		private PointConstruction choosingPointConstruction;

		@Override
		protected FGELine computeData() {
			FGEUnionArea tangentPoints = FGECircle.getTangentsPointsToCircle(circleConstruction.getCircle(), pointConstruction.getPoint());
			if (tangentPoints.isUnionOfPoints()) {
				return new FGELine(tangentPoints.getNearestPoint(choosingPointConstruction.getPoint()), pointConstruction.getPoint());
			}
			logger.warning("Received strange result for FGEEllips.getTangentsPointsToCircle()");
			return null;
		}

		@Override
		public String toString() {
			return "TangentLineWithCircleAndPoint[\n" + "> " + circleConstruction.toString() + "\n> " + pointConstruction.toString()
					+ "\n> " + choosingPointConstruction.toString() + "\n]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { circleConstruction, pointConstruction, choosingPointConstruction };
			return returned;
		}

		@Override
		public CircleConstruction getCircleConstruction() {
			return circleConstruction;
		}

		@Override
		public void setCircleConstruction(CircleConstruction circleConstruction) {
			if ((circleConstruction == null && this.circleConstruction != null)
					|| (circleConstruction != null && !circleConstruction.equals(this.circleConstruction))) {
				CircleConstruction oldValue = this.circleConstruction;
				this.circleConstruction = circleConstruction;
				getPropertyChangeSupport().firePropertyChange("circleConstruction", oldValue, circleConstruction);
			}
		}

		@Override
		public PointConstruction getPointConstruction() {
			return pointConstruction;
		}

		@Override
		public void setPointConstruction(PointConstruction pointConstruction) {
			if ((pointConstruction == null && this.pointConstruction != null)
					|| (pointConstruction != null && !pointConstruction.equals(this.pointConstruction))) {
				PointConstruction oldValue = this.pointConstruction;
				this.pointConstruction = pointConstruction;
				getPropertyChangeSupport().firePropertyChange("pointConstruction", oldValue, pointConstruction);
			}
		}

		@Override
		public PointConstruction getChoosingPointConstruction() {
			return choosingPointConstruction;
		}

		@Override
		public void setChoosingPointConstruction(PointConstruction choosingPointConstruction) {
			if ((choosingPointConstruction == null && this.choosingPointConstruction != null)
					|| (choosingPointConstruction != null && !choosingPointConstruction.equals(this.choosingPointConstruction))) {
				PointConstruction oldValue = this.choosingPointConstruction;
				this.choosingPointConstruction = choosingPointConstruction;
				getPropertyChangeSupport().firePropertyChange("choosingPointConstruction", oldValue, choosingPointConstruction);
			}
		}

	}

}
