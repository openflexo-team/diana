/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.diana.geomedit.view;

import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.GeometricElement;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBMouseEvent;
import org.openflexo.gina.view.GinaViewFactory;

public class BrowserFIBController extends FIBController {

	private static final Logger logger = Logger.getLogger(BrowserFIBController.class.getPackage().getName());

	private GeomEditDrawingController editorController;

	public BrowserFIBController(FIBComponent rootComponent, GinaViewFactory<?> viewFactory) {
		super(rootComponent, viewFactory);
	}

	public GeomEditDrawingController getEditorController() {
		return editorController;
	}

	public void setEditorController(GeomEditDrawingController editorController) {
		if ((editorController == null && this.editorController != null)
				|| (editorController != null && !editorController.equals(this.editorController))) {
			GeomEditDrawingController oldValue = this.editorController;
			this.editorController = editorController;
			getPropertyChangeSupport().firePropertyChange("editorController", oldValue, editorController);
		}
	}

	public Icon iconForObject(Object object) {
		if (object instanceof GeometricElement) {
			return GeomEditIconLibrary.iconForObject((GeometricElement) object);
		}
		return null;
	}

	public void rightClick(GeometricConstruction<?> construction, FIBMouseEvent event) {
		System.out.println("rightClick with " + construction + " event=" + event);
		editorController.getContextualMenu().displayPopupMenu(construction, (Component) event.getSource(), event.getPoint());
	}

}
