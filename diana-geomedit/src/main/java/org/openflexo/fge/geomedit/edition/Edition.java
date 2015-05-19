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

import java.awt.Color;
import java.util.Vector;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.TextureBackgroundStyle.TextureType;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.GeometricObject;
import org.openflexo.fge.swing.graphics.JFGEDrawingGraphics;

public abstract class Edition {
	public int currentStep;
	public Vector<EditionInput> inputs;
	private String label;
	private GeomEditController controller;

	protected ForegroundStyle focusedForegroundStyle;
	protected BackgroundStyle focusedBackgroundStyle;

	public Edition(String aLabel, GeomEditController aController) {
		super();
		controller = aController;
		focusedForegroundStyle = aController.getFactory().makeForegroundStyle(Color.RED);
		focusedBackgroundStyle = aController.getFactory().makeTexturedBackground(TextureType.TEXTURE1, Color.RED, Color.WHITE);
		focusedBackgroundStyle.setUseTransparency(true);
		label = aLabel;
		currentStep = 0;
		inputs = new Vector<EditionInput>();
	}

	public String getLabel() {
		return label;
	}

	public GeomEditController getController() {
		return controller;
	}

	public final void addObject(GeometricObject object) {
		getController().getDrawing().getModel().addToChilds(object);
	}

	public boolean next() {
		currentStep++;
		if (currentStep >= inputs.size()) {
			performEdition();
			return true;
		}
		return false;
	}

	public abstract void performEdition();

	public final void paint(JFGEDrawingGraphics graphics, FGEPoint lastMouseLocation) {
		paintEdition(graphics, lastMouseLocation);
		inputs.get(currentStep).paint(graphics);
	}

	public abstract void paintEdition(JFGEDrawingGraphics graphics, FGEPoint lastMouseLocation);

	public boolean requireRepaint(FGEPoint lastMouseLocation) {
		return true;
	}

}
