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

package org.openflexo.diana.geomedit.model.gr;

import java.util.List;

import org.openflexo.diana.geomedit.model.construction.GeometricConstruction;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.impl.GeometricGraphicalRepresentationImpl;
import org.openflexo.model.annotations.ModelEntity;

@ModelEntity(isAbstract = true)
public interface GeometricObjectGraphicalRepresentation<A extends FGEArea> extends GeometricGraphicalRepresentation {

	public boolean getDisplayLabel();

	public void setDisplayLabel(boolean aFlag);

	public List<? extends ControlArea<?>> makeControlAreasFor(
			org.openflexo.fge.Drawing.DrawingTreeNode<GeometricConstruction<A>, GeometricGraphicalRepresentation> dtn);

	public static abstract class GeometricObjectGraphicalRepresentationImpl<A extends FGEArea> extends GeometricGraphicalRepresentationImpl
			implements GeometricObjectGraphicalRepresentation<A> {

		@Override
		public String getText() {
			if (!getDisplayLabel()) {
				return null;
			}
			return super.getText();
		}

		private boolean displayLabel;

		@Override
		public boolean getDisplayLabel() {
			return displayLabel;
		}

		@Override
		public void setDisplayLabel(boolean aFlag) {
			displayLabel = aFlag;
		}

		@Override
		public A getGeometricObject() {
			return (A) super.getGeometricObject();
		}
	}

}