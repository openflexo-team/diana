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

package org.openflexo.fge.geomedit;

import org.openflexo.diana.geomedit.model.gr.HalfPlaneGraphicalRepresentation;
import org.openflexo.fge.geom.area.FGEHalfPlane;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.HalfPlaneConstruction;

public class HalfPlane extends GeometricObject<FGEHalfPlane> {

	private HalfPlaneGraphicalRepresentation graphicalRepresentation;

	// Called for LOAD
	public HalfPlane(GeomEditBuilder builder) {
		super(builder);
	}

	public HalfPlane(GeometricSet set, HalfPlaneConstruction construction) {
		super(set, construction);
		graphicalRepresentation = new HalfPlaneGraphicalRepresentation(this, set.getEditedDrawing());
	}

	@Override
	public HalfPlaneGraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(HalfPlaneGraphicalRepresentation aGR) {
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}

	@Override
	public HalfPlaneConstruction getConstruction() {
		return (HalfPlaneConstruction) super.getConstruction();
	}

	public void setConstruction(HalfPlaneConstruction lineConstruction) {
		_setConstruction(lineConstruction);
	}

	@Override
	public String getInspectorName() {
		return "HalfPlane.inspector";
	}

}
