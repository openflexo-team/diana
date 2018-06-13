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

import org.openflexo.fge.geom.area.FGEBand;
import org.openflexo.fge.geomedit.construction.BandWithTwoLinesConstruction.BandWithTwoLinesConstructionImpl;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(BandWithTwoLinesConstructionImpl.class)
@XMLElement
public interface BandWithTwoLinesConstruction extends BandConstruction {

	public LineConstruction getLineConstruction1();

	public void setLineConstruction1(LineConstruction lineConstruction1);

	public LineConstruction getLineConstruction2();

	public void setLineConstruction2(LineConstruction lineConstruction2);

	public static abstract class BandWithTwoLinesConstructionImpl extends BandConstructionImpl implements BandWithTwoLinesConstruction {

		private LineConstruction lineConstruction1;
		private LineConstruction lineConstruction2;

		@Override
		public LineConstruction getLineConstruction1() {
			return lineConstruction1;
		}

		@Override
		public void setLineConstruction1(LineConstruction lineConstruction1) {
			if ((lineConstruction1 == null && this.lineConstruction1 != null)
					|| (lineConstruction1 != null && !lineConstruction1.equals(this.lineConstruction1))) {
				LineConstruction oldValue = this.lineConstruction1;
				this.lineConstruction1 = lineConstruction1;
				getPropertyChangeSupport().firePropertyChange("lineConstruction1", oldValue, lineConstruction1);
			}
		}

		@Override
		public LineConstruction getLineConstruction2() {
			return lineConstruction2;
		}

		@Override
		public void setLineConstruction2(LineConstruction lineConstruction2) {
			if ((lineConstruction2 == null && this.lineConstruction2 != null)
					|| (lineConstruction2 != null && !lineConstruction2.equals(this.lineConstruction2))) {
				LineConstruction oldValue = this.lineConstruction2;
				this.lineConstruction2 = lineConstruction2;
				getPropertyChangeSupport().firePropertyChange("lineConstruction2", oldValue, lineConstruction2);
			}
		}

		@Override
		protected FGEBand computeData() {
			FGEBand returned = new FGEBand(lineConstruction1.getLine(), lineConstruction2.getLine());
			return returned;
		}

		@Override
		public String toString() {
			return "BandWithTwoLinesConstruction[\n" + "> " + lineConstruction1.toString() + "\n> " + lineConstruction2.toString() + "\n]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { lineConstruction1, lineConstruction2 };
			return returned;
		}

	}

}
