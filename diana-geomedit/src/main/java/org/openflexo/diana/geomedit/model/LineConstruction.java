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

import org.openflexo.diana.geomedit.model.LineConstruction.LineConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.LineGraphicalRepresentation;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;

@ModelEntity(isAbstract = true)
@ImplementationClass(LineConstructionImpl.class)
@Imports({ @Import(HorizontalLineWithPointConstruction.class), @Import(LineReference.class), @Import(LineWithTwoPointsConstruction.class),
		@Import(OrthogonalLineWithPointConstruction.class), @Import(ParallelLineWithPointConstruction.class),
		@Import(RotatedLineWithPointConstruction.class), @Import(TangentLineWithCircleAndPointConstruction.class),
		@Import(VerticalLineWithPointConstruction.class) })
public interface LineConstruction extends GeometricConstruction<FGELine> {

	@PropertyIdentifier(type = Double.class)
	public static final String X1_KEY = "x1";
	@PropertyIdentifier(type = Double.class)
	public static final String Y1_KEY = "y1";
	@PropertyIdentifier(type = Double.class)
	public static final String X2_KEY = "x2";
	@PropertyIdentifier(type = Double.class)
	public static final String Y2_KEY = "y2";

	@Getter(value = X1_KEY, defaultValue = "0.0")
	public double getX1();

	@Setter(value = X1_KEY)
	public void setX1(double value);

	@Getter(value = Y1_KEY, defaultValue = "0.0")
	public double getY1();

	@Setter(value = Y1_KEY)
	public void setY1(double value);

	@Getter(value = X2_KEY, defaultValue = "0.0")
	public double getX2();

	@Setter(value = X2_KEY)
	public void setX2(double value);

	@Getter(value = Y2_KEY, defaultValue = "0.0")
	public double getY2();

	@Setter(value = Y2_KEY)
	public void setY2(double value);

	public FGELine getLine();

	public static abstract class LineConstructionImpl extends GeometricConstructionImpl<FGELine> implements LineConstruction {

		@Override
		public String getBaseName() {
			return "Line";
		}

		@Override
		public final FGELine getLine() {
			return getData();
		}

		@Override
		public LineGraphicalRepresentation makeNewConstructionGR(GeometricConstructionFactory factory) {
			LineGraphicalRepresentation returned = factory.newInstance(LineGraphicalRepresentation.class);
			return returned;
		}

		@Override
		protected abstract FGELine computeData();

		@Override
		public double getX1() {
			return getLine().getX1();
		}

		@Override
		public void setX1(double x1) {
			if (x1 != getX1()) {
				double oldX1 = getX1();
				getLine().setX1(x1);
				getPropertyChangeSupport().firePropertyChange(X1_KEY, oldX1, x1);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getX2() {
			return getLine().getX2();
		}

		@Override
		public void setX2(double x2) {
			if (x2 != getX2()) {
				double oldX2 = getX2();
				getLine().setX2(x2);
				getPropertyChangeSupport().firePropertyChange(X2_KEY, oldX2, x2);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getY1() {
			return getLine().getY1();
		}

		@Override
		public void setY1(double y1) {
			if (y1 != getY1()) {
				double oldY1 = getY1();
				getLine().setY1(y1);
				getPropertyChangeSupport().firePropertyChange(Y1_KEY, oldY1, y1);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getY2() {
			return getLine().getY2();
		}

		@Override
		public void setY2(double y2) {
			if (y2 != getY2()) {
				double oldY2 = getY2();
				getLine().setY2(y2);
				getPropertyChangeSupport().firePropertyChange(Y2_KEY, oldY2, y2);
				notifyGeometryChanged();
			}
		}

	}
}
