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

import org.openflexo.fge.geom.FGERoundRectangle;
import org.openflexo.fge.geomedit.construction.RoundRectangleConstruction.RoundRectangleConstructionImpl;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;

@ModelEntity(isAbstract = true)
@ImplementationClass(RoundRectangleConstructionImpl.class)
@Imports({ @Import(RoundRectangleWithTwoPointsConstruction.class) })
public interface RoundRectangleConstruction extends GeometricConstruction<FGERoundRectangle> {

	public FGERoundRectangle getRectangle();

	public boolean getIsFilled();

	public void setIsFilled(boolean isFilled);

	public double getArcHeight();

	public void setArcHeight(double arcHeight);

	public double getArcWidth();

	public void setArcWidth(double arcWidth);

	public static abstract class RoundRectangleConstructionImpl extends GeometricConstructionImpl<FGERoundRectangle>
			implements RoundRectangleConstruction {

		private boolean isFilled;

		private double arcWidth = 30;
		private double arcHeight = 30;

		@Override
		public final FGERoundRectangle getRectangle() {
			return getData();
		}

		@Override
		protected abstract FGERoundRectangle computeData();

		@Override
		public boolean getIsFilled() {
			return isFilled;
		}

		@Override
		public void setIsFilled(boolean isFilled) {
			this.isFilled = isFilled;
			setModified(true);
		}

		@Override
		public double getArcHeight() {
			return arcHeight;
		}

		@Override
		public void setArcHeight(double arcHeight) {
			this.arcHeight = arcHeight;
			setModified(true);
		}

		@Override
		public double getArcWidth() {
			return arcWidth;
		}

		@Override
		public void setArcWidth(double arcWidth) {
			this.arcWidth = arcWidth;
			setModified(true);
		}
	}

}
