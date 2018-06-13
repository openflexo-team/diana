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

import java.awt.Color;
import java.awt.event.MouseEvent;

import org.openflexo.diana.geomedit.GeomEditDrawingEditor;
import org.openflexo.diana.geomedit.controller.ComputedControlPoint;
import org.openflexo.diana.geomedit.controller.DraggableControlPoint;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.converter.PointConverter;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.construction.GeometricConstruction;
import org.openflexo.fge.geomedit.construction.PointConstruction;
import org.openflexo.fge.swing.graphics.JFGEDrawingGraphics;

public class ObtainPoint extends EditionInput<FGEPoint> {
	public static int preferredMethodIndex = 0;

	private boolean endOnRightClick = false;

	public ObtainPoint(String anInputLabel, GeomEditDrawingEditor controller) {
		super(anInputLabel, controller);

		availableMethods.add(new CursorSelection());
		availableMethods.add(new ControlPointSelection());
		availableMethods.add(new IntersectionSelection());
		availableMethods.add(new KeyboardSelection());
	}

	public ObtainPoint(String anInputLabel, GeomEditDrawingEditor controller, boolean appendEndSelection) {
		this(anInputLabel, controller);
		if (appendEndSelection) {
			availableMethods.add(new EndEditionSelection());
		}
		endOnRightClick = appendEndSelection;
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

	public class CursorSelection extends EditionInputMethod<FGEPoint, ObtainPoint> {

		public CursorSelection() {
			super("From cursor", ObtainPoint.this);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			setConstruction(getFactory().makeExplicitPointConstruction(getPointLocation(e)));
			done();
			if (endOnRightClick && e.getButton() == MouseEvent.BUTTON3) {
				endEdition();
			}
		}

		@Override
		public InputComponent getInputComponent() {
			return new InputComponentButton(CursorSelection.this);
		}

	}

	public class ControlPointSelection extends EditionInputMethod<FGEPoint, ObtainPoint> {

		private ControlPoint focusedControlPoint;
		private GeometricNode<?> focusedObject;

		public ControlPointSelection() {
			super("As control point", ObtainPoint.this);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (focusedControlPoint != null) {
				if (focusedObject != null) {
					focusedObject.setIsFocused(false);
				}
				if (focusedControlPoint instanceof DraggableControlPoint) {
					setConstruction(
							getFactory().makePointReference(((DraggableControlPoint) focusedControlPoint).getExplicitPointConstruction()));
					done();
				}
				else if (focusedControlPoint instanceof ComputedControlPoint) {
					setConstruction(getFactory().makeControlPointReference((GeometricConstruction<?>) focusedObject.getDrawable(),
							((ComputedControlPoint) focusedControlPoint).getName()));
					done();
				}
				else {
					System.err.println("Don't know what to do with a " + focusedControlPoint);
				}
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

			ControlArea<?> controlArea = (focused != null ? getFocusRetriever().getFocusedControlAreaForDrawable(focused, e) : null);
			if (controlArea instanceof ControlPoint) {
				focusedControlPoint = (ControlPoint) controlArea;
			}

			if (!(focusedControlPoint instanceof DraggableControlPoint || focusedControlPoint instanceof ComputedControlPoint)) {
				focusedControlPoint = null;
			}
		}

		@Override
		public void paint(JFGEDrawingGraphics graphics) {
			if (focusedControlPoint != null) {
				graphics.useForegroundStyle(graphics.getFactory().makeForegroundStyle(Color.RED));
				graphics.drawControlPoint(focusedControlPoint.getPoint(), FGEConstants.CONTROL_POINT_SIZE);
				graphics.drawRoundArroundPoint(focusedControlPoint.getPoint(), 8);
			}
		}

		@Override
		public InputComponent getInputComponent() {
			return new InputComponentButton(ControlPointSelection.this);
		}

	}

	public class IntersectionSelection extends EditionInputMethod<FGEPoint, ObtainPoint> {

		public IntersectionSelection() {
			super("As intersection", ObtainPoint.this);
			addChildInput(new ObtainLine("Select first line", ObtainPoint.this.getController()));
			addChildInput(new ObtainLine("Select second line", ObtainPoint.this.getController()));
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			System.out.println("Mouse clicked for intersection selection");
		}

		@Override
		public InputComponent getInputComponent() {
			return new InputComponentButton(IntersectionSelection.this);
		}

		@Override
		public PointConstruction retrieveInputDataFromChildInputs() {
			// TODO
			System.out.println("Faire un truc ici pour deselectionner la ligne !");
			// ((ObtainLine) childInputs.get(0)).getConstruction().getGraphicalRepresentation().setIsSelected(false);
			return getFactory().makeLineIntersectionPointConstruction(((ObtainLine) childInputs.get(0)).getConstruction(),
					((ObtainLine) childInputs.get(1)).getConstruction());
		}

		@Override
		public void paint(JFGEDrawingGraphics graphics) {
			if (currentChildInputStep == 0) {
				// Nothing to draw
			}
			else if (currentChildInputStep == 1 && ((ObtainLine) childInputs.get(0)).getReferencedLine() != null) {
				// TODO
				System.out.println("Faire un truc ici pour selectionner la ligne !");
				// ((ObtainLine) childInputs.get(0)).getReferencedLine().getGraphicalRepresentation().setIsSelected(true);
			}
		}

	}

	public class KeyboardSelection extends EditionInputMethod<FGEPoint, ObtainPoint> {

		private InputComponentTextField<FGEPoint> inputComponent;

		public KeyboardSelection() {
			super("As", ObtainPoint.this);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			inputComponent.setData(getPointLocation(e));
		}

		@Override
		public InputComponent getInputComponent() {
			if (inputComponent == null) {
				inputComponent = new InputComponentTextField<FGEPoint>(KeyboardSelection.this, new FGEPoint(0, 0)) {

					@Override
					public int getColumnSize() {
						return 8;
					}

					@Override
					public String convertDataToString(FGEPoint data) {
						if (data == null) {
							return "";
						}
						return (new PointConverter(FGEPoint.class)).convertToString(data);
					}

					@Override
					public FGEPoint convertStringToData(String string) {
						if (string == null || string.trim().equals("")) {
							return null;
						}
						return (new PointConverter(FGEPoint.class)).convertFromString(string, null);
					}

					@Override
					public void dataEntered(FGEPoint data) {
						setConstruction(getFactory().makeExplicitPointConstruction(data));
						done();
					}

				};
			}
			return inputComponent;
		}

	}

	@Override
	public void paint(JFGEDrawingGraphics graphics) {
		super.paint(graphics);
		if ((getActiveMethod() instanceof ControlPointSelection) || (getActiveMethod() instanceof IntersectionSelection)) {
			getActiveMethod().paint(graphics);
		}
	}

	@Override
	public PointConstruction getConstruction() {
		return (PointConstruction) super.getConstruction();
	}

	@Override
	public boolean endOnRightClick() {
		return endOnRightClick;
	}

}
