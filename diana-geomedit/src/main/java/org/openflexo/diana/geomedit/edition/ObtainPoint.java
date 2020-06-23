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

import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.diana.geomedit.controller.ComputedControlPoint;
import org.openflexo.diana.geomedit.controller.DraggableControlPoint;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.PointConstruction;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.converter.PointConverter;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.swing.graphics.JDianaDrawingGraphics;

public class ObtainPoint extends EditionInput<DianaPoint> {
	public static int preferredMethodIndex = 0;

	private boolean endOnRightClick = false;

	public ObtainPoint(String anInputLabel, GeomEditDrawingController controller) {
		super(anInputLabel, controller);

		availableMethods.add(new CursorSelection());
		availableMethods.add(new ControlPointSelection());
		availableMethods.add(new IntersectionSelection());
		availableMethods.add(new KeyboardSelection());
	}

	public ObtainPoint(String anInputLabel, GeomEditDrawingController controller, boolean appendEndSelection) {
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

	public class CursorSelection extends EditionInputMethod<DianaPoint, ObtainPoint> {

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

	public class ControlPointSelection extends EditionInputMethod<DianaPoint, ObtainPoint> {

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

			if (focused == null && focusedObject != null) {
				// We exit a focused object
				focusedObject.setIsFocused(false);
				focusedObject = null;
				focusedControlPoint = null;
				setConstruction(null);
				setInputData(null);
			}

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
		public void paint(JDianaDrawingGraphics graphics) {
			if (focusedControlPoint != null) {
				graphics.useForegroundStyle(graphics.getFactory().makeForegroundStyle(Color.RED));
				graphics.drawControlPoint(focusedControlPoint.getPoint(), DianaConstants.CONTROL_POINT_SIZE);
				graphics.drawRoundArroundPoint(focusedControlPoint.getPoint(), 8);
			}
		}

		@Override
		public InputComponent getInputComponent() {
			return new InputComponentButton(ControlPointSelection.this);
		}

	}

	public class IntersectionSelection extends EditionInputMethod<DianaPoint, ObtainPoint> {

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
		public void paint(JDianaDrawingGraphics graphics) {
			if (currentChildInputStep == 0) {
				// Nothing to draw
			}
			else if (currentChildInputStep == 1 && ((ObtainLine) childInputs.get(0)).getConstruction() != null) {
				// TODO
				System.out.println("Faire un truc ici pour selectionner la ligne !");
				// ((ObtainLine) childInputs.get(0)).getReferencedLine().getGraphicalRepresentation().setIsSelected(true);
			}
		}

	}

	public class KeyboardSelection extends EditionInputMethod<DianaPoint, ObtainPoint> {

		private InputComponentTextField<DianaPoint> inputComponent;

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
				inputComponent = new InputComponentTextField<DianaPoint>(KeyboardSelection.this, new DianaPoint(0, 0)) {

					@Override
					public int getColumnSize() {
						return 8;
					}

					@Override
					public String convertDataToString(DianaPoint data) {
						if (data == null) {
							return "";
						}
						return (new PointConverter(DianaPoint.class)).convertToString(data);
					}

					@Override
					public DianaPoint convertStringToData(String string) {
						if (string == null || string.trim().equals("")) {
							return null;
						}
						return (new PointConverter(DianaPoint.class)).convertFromString(string, null);
					}

					@Override
					public void dataEntered(DianaPoint data) {
						setConstruction(getFactory().makeExplicitPointConstruction(data));
						done();
					}

				};
			}
			return inputComponent;
		}

	}

	@Override
	public void paint(JDianaDrawingGraphics graphics) {
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
