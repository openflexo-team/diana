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

import java.awt.event.MouseEvent;

import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.diana.geomedit.model.LineConstruction;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.geom.DianaLine;

public class ObtainLine extends EditionInput<DianaLine> {
	public static int preferredMethodIndex = 0;

	public ObtainLine(String anInputLabel, GeomEditDrawingController controller) {
		super(anInputLabel, controller);

		availableMethods.add(new LineSelection());
	}

	@Override
	protected int getPreferredMethodIndex() {
		return preferredMethodIndex;
	}

	@Override
	public void setActiveMethod(EditionInputMethod aMethod) {
		super.setActiveMethod(aMethod);
		preferredMethodIndex = availableMethods.indexOf(aMethod);
	}

	public class LineSelection extends EditionInputMethod<DianaLine, ObtainLine> {

		private GeometricNode<?> focusedObject;

		public LineSelection() {
			super("With mouse", ObtainLine.this);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (focusedObject != null) {
				focusedObject.setIsFocused(false);
				if (focusedObject.getDrawable() instanceof LineConstruction) {
					setConstruction(getFactory().makeLineReference((LineConstruction) focusedObject.getDrawable()));
				}
				done();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			DrawingTreeNode<?, ?> focused = getFocusRetriever().getFocusedObject(e);

			if (focusedObject != null && focusedObject != focused) {
				focusedObject.setIsFocused(false);
			}

			if (focused instanceof GeometricNode) {
				focusedObject = (GeometricNode<?>) focused;
				focusedObject.setIsFocused(true);
			}

		}

		@Override
		public InputComponent getInputComponent() {
			return new InputComponentButton(LineSelection.this);
		}

	}

	@Override
	public LineConstruction getConstruction() {
		return (LineConstruction) super.getConstruction();
	}

	@Override
	public boolean endOnRightClick() {
		return false;
	}

}
