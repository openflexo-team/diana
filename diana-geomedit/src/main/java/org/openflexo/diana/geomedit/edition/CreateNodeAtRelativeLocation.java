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

package org.openflexo.diana.geomedit.edition;

import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.diana.swing.graphics.JDianaDrawingGraphics;

public class CreateNodeAtRelativeLocation extends Edition {

	public CreateNodeAtRelativeLocation(GeomEditDrawingController controller) {
		super("Create node at relative location", controller);
		inputs.add(new ObtainNode("Select reference node", controller));
		inputs.add(new ObtainDouble("Select horizontal translation", 100, controller));
		inputs.add(new ObtainDouble("Select vertical translation", 100, controller));
	}

	@Override
	public void performEdition() {
		ObtainNode reference = (ObtainNode) inputs.get(0);
		double tx = ((ObtainDouble) inputs.get(1)).getInputData();
		double ty = ((ObtainDouble) inputs.get(2)).getInputData();

		addConstruction(getController().getFactory().makeNodeWithRelativePositionConstruction(reference.getConstruction(), tx, ty));

	}

	@Override
	public void paintEdition(JDianaDrawingGraphics graphics, DianaPoint lastMouseLocation) {
	}
}
