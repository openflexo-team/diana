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

import java.util.Vector;

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolylin;
import org.openflexo.fge.geom.FGERectPolylin;

public class PolylinWithNPointsConstruction extends PolylinConstruction {

	public Vector<PointConstruction> pointConstructions;

	public PolylinWithNPointsConstruction() {
		super();
		pointConstructions = new Vector<PointConstruction>();
	}

	public PolylinWithNPointsConstruction(Vector<PointConstruction> somePointConstructions) {
		this();
		this.pointConstructions.addAll(somePointConstructions);
	}

	@Override
	protected FGEPolylin computeData() {
		Vector<FGEPoint> pts = new Vector<FGEPoint>();
		for (PointConstruction pc : pointConstructions) {
			pts.add(pc.getPoint());
		}
		return new FGERectPolylin(pts);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("PolylinWithNPointsConstruction[\n");
		for (PointConstruction pc : pointConstructions) {
			sb.append("> " + pc.toString() + "\n");
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public GeometricConstruction[] getDepends() {
		return pointConstructions.toArray(new GeometricConstruction[pointConstructions.size()]);
	}

}
