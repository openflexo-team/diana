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

import java.awt.Color;

import org.openflexo.diana.geomedit.model.PointConstruction.PointConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.PointGraphicalRepresentation;
import org.openflexo.diana.TextureBackgroundStyle.TextureType;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;

@ModelEntity(isAbstract = true)
@ImplementationClass(PointConstructionImpl.class)
@Imports({ @Import(PointReference.class), @Import(ExplicitPointConstruction.class), @Import(ControlPointReference.class),
		@Import(LineIntersectionPointConstruction.class), @Import(PointMiddleOfTwoPointsConstruction.class),
		@Import(SymetricPointConstruction.class), @Import(NearestPointFromObjectConstruction.class) })
public interface PointConstruction extends GeometricConstruction<DianaPoint> {

	@PropertyIdentifier(type = Double.class)
	public static final String X_KEY = "x";
	@PropertyIdentifier(type = Double.class)
	public static final String Y_KEY = "y";

	@Getter(value = X_KEY, defaultValue = "0.0")
	public double getX();

	@Setter(value = X_KEY)
	public void setX(double value);

	@Getter(value = Y_KEY, defaultValue = "0.0")
	public double getY();

	@Setter(value = Y_KEY)
	public void setY(double value);

	public DianaPoint getPoint();

	public static abstract class PointConstructionImpl extends GeometricConstructionImpl<DianaPoint> implements PointConstruction {

		@Override
		public String getBaseName() {
			return "Point";
		}

		@Override
		public DianaPoint getPoint() {
			return getData();
		}

		@Override
		protected abstract DianaPoint computeData();

		@Override
		public PointGraphicalRepresentation makeNewConstructionGR(GeometricConstructionFactory factory) {
			PointGraphicalRepresentation returned = factory.newInstance(PointGraphicalRepresentation.class);
			returned.setBackground(factory.makeTexturedBackground(TextureType.TEXTURE1, Color.RED, Color.WHITE));
			return returned;
		}

		@Override
		public double getX() {
			return getPoint().getX();
		}

		@Override
		public void setX(double x) {
			if (x != getX()) {
				double oldX = getX();
				getPoint().x = x;
				getPropertyChangeSupport().firePropertyChange(X_KEY, oldX, x);
				// getGraphicalRepresentation().notify(new DianaAttributeNotification("x", oldX, x));
				notifyGeometryChanged();
			}
		}

		@Override
		public double getY() {
			return getPoint().getY();
		}

		@Override
		public void setY(double y) {
			if (y != getY()) {
				double oldY = getY();
				getPoint().y = y;
				getPropertyChangeSupport().firePropertyChange(Y_KEY, oldY, y);
				// getGraphicalRepresentation().notify(new DianaAttributeNotification("y", oldY, y));
				notifyGeometryChanged();
			}
		}

	}
}
