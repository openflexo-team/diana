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

import org.openflexo.diana.geomedit.model.SegmentConstruction.SegmentConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.SegmentGraphicalRepresentation;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.diana.geom.DianaSegment;

@ModelEntity(isAbstract = true)
@ImplementationClass(SegmentConstructionImpl.class)
@Imports({ @Import(SegmentReference.class), @Import(SegmentWithTwoPointsConstruction.class) })
public interface SegmentConstruction extends GeometricConstruction<DianaSegment> {

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

	public DianaSegment getSegment();

	public abstract class SegmentConstructionImpl extends GeometricConstructionImpl<DianaSegment> implements SegmentConstruction {

		@Override
		public String getBaseName() {
			return "Segment";
		}

		@Override
		public final DianaSegment getSegment() {
			return getData();
		}

		@Override
		public SegmentGraphicalRepresentation makeNewConstructionGR(GeometricConstructionFactory factory) {
			SegmentGraphicalRepresentation returned = factory.newInstance(SegmentGraphicalRepresentation.class);
			return returned;
		}

		@Override
		protected abstract DianaSegment computeData();

		@Override
		public double getX1() {
			return getSegment().getX1();
		}

		@Override
		public void setX1(double x1) {
			if (x1 != getX1()) {
				double oldX1 = getX1();
				getSegment().setX1(x1);
				getPropertyChangeSupport().firePropertyChange(X1_KEY, oldX1, x1);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getX2() {
			return getSegment().getX2();
		}

		@Override
		public void setX2(double x2) {
			if (x2 != getX2()) {
				double oldX2 = getX2();
				getSegment().setX2(x2);
				getPropertyChangeSupport().firePropertyChange(X2_KEY, oldX2, x2);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getY1() {
			return getSegment().getY1();
		}

		@Override
		public void setY1(double y1) {
			if (y1 != getY1()) {
				double oldY1 = getY1();
				getSegment().setY1(y1);
				getPropertyChangeSupport().firePropertyChange(Y1_KEY, oldY1, y1);
				notifyGeometryChanged();
			}
		}

		@Override
		public double getY2() {
			return getSegment().getY2();
		}

		@Override
		public void setY2(double y2) {
			if (y2 != getY2()) {
				double oldY2 = getY2();
				getSegment().setY2(y2);
				getPropertyChangeSupport().firePropertyChange(Y2_KEY, oldY2, y2);
				notifyGeometryChanged();
			}
		}

	}

}
