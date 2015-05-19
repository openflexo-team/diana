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

package org.openflexo.fge.geomedit.edition;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.ObjectIntersection;
import org.openflexo.fge.geomedit.construction.IntersectionConstruction;
import org.openflexo.fge.geomedit.construction.ObjectReference;
import org.openflexo.fge.swing.graphics.JFGEDrawingGraphics;
import org.openflexo.logging.FlexoLogger;

public class CreateIntersection extends Edition {

	private static final Logger logger = FlexoLogger.getLogger(GeomEditController.class.getPackage().getName());

	public CreateIntersection(GeomEditController controller) {
		super("Create intersection", controller);
		appendNewObjectEdition(controller, 1);
	}

	private void appendNewObjectEdition(final GeomEditController controller, int index) {
		ObtainObject newObtainObject = new ObtainObject("Select object " + index, controller, true) {
			@Override
			public void done() {
				appendNewObjectEdition(controller, currentStep + 2);
				super.done();
			}

			@Override
			public void endEdition() {
				System.out.println("End edition called");
				super.done();
			}
		};
		inputs.add(newObtainObject);
	}

	@Override
	public void performEdition() {
		Vector<ObjectReference<?>> lgc = new Vector<ObjectReference<?>>();
		for (EditionInput o : inputs) {
			ObtainObject oo = (ObtainObject) o;
			if (oo.getReferencedObject() != null) {
				oo.getReferencedObject().getGraphicalRepresentation().setIsSelected(false);
			}
			ObjectReference<?> or = ((ObtainObject) o).getConstruction();
			if (or != null) {
				lgc.add(or);
			}
		}

		addObject(new ObjectIntersection(getController().getDrawing().getModel(), new IntersectionConstruction(lgc)));

	}

	/*public void addObject(GeometricObject object)
	{
		getController().getDrawing().getModel().addToChilds(object);
	}*/

	@Override
	public void paintEdition(JFGEDrawingGraphics graphics, FGEPoint lastMouseLocation) {
		for (EditionInput o : inputs) {
			ObtainObject oo = (ObtainObject) o;
			if (oo.getReferencedObject() != null) {
				oo.getReferencedObject().getGraphicalRepresentation().setIsSelected(true);
			}
		}
	}
}
