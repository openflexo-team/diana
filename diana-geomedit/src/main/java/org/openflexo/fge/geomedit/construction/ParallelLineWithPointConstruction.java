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

import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;

public class ParallelLineWithPointConstruction extends LineConstruction {

	public LineConstruction lineConstruction;
	public PointConstruction pointConstruction;

	public ParallelLineWithPointConstruction() {
		super();
	}

	public ParallelLineWithPointConstruction(LineConstruction aLineConstruction, PointConstruction aPointConstruction) {
		this();
		this.lineConstruction = aLineConstruction;
		this.pointConstruction = aPointConstruction;
	}

	@Override
	protected FGELine computeData() {
		FGELine computedLine = lineConstruction.getLine().getParallelLine(pointConstruction.getPoint());
		FGEPoint pp1 = computedLine.getProjection(lineConstruction.getLine().getP1());
		FGEPoint pp2 = computedLine.getProjection(lineConstruction.getLine().getP2());
		computedLine.setP1(pp1);
		computedLine.setP2(pp2);
		return computedLine;
	}

	@Override
	public String toString() {
		return "ParallelLineWithPointConstruction[\n" + "> " + lineConstruction.toString() + "\n> " + pointConstruction.toString() + "\n]";
	}

	@Override
	public GeometricConstruction[] getDepends() {
		GeometricConstruction[] returned = { lineConstruction, pointConstruction };
		return returned;
	}

}
