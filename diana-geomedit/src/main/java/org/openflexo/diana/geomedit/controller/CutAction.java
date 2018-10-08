/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-swing-editor, a component of the software infrastructure 
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

package org.openflexo.diana.geomedit.controller;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.openflexo.diana.geomedit.GeomEditApplication;
import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.view.GeomEditIconLibrary;
import org.openflexo.exceptions.CutException;

public class CutAction extends AbstractEditorActionImpl {

	public static KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_X, GeomEditApplication.META_MASK);

	public CutAction(GeomEditDrawingController anEditorController) {
		super("cut", GeomEditIconLibrary.CUT_ICON, anEditorController);
	}

	@Override
	public KeyStroke getShortcut() {
		return ACCELERATOR;
	}

	@Override
	public boolean isEnabledFor(GeometricConstruction<?> object) {
		return object != null;
	}

	@Override
	public boolean isVisibleFor(GeometricConstruction<?> object) {
		return true;
	}

	@Override
	public GeometricConstruction<?> performAction(GeometricConstruction<?> object) {
		System.out.println("Cut");
		try {
			getEditorController().cut();
		} catch (CutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*try {
			getEditorController().getEditor().setClipboard(getEditorController().getEditor().getApplication().getFactory().cut(object));
		} catch (ModelExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		return object;
	}

}
