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

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.openflexo.diana.geomedit.edition.EditionInput;
import org.openflexo.diana.geomedit.edition.EditionInputMethod;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.GeometricObject;
import org.openflexo.fge.geomedit.construction.ObjectReference;

public class ObtainObject extends EditionInput<FGEArea> {
	public static int preferredMethodIndex = 0;

	private boolean endOnRightClick = false;

	private GeometricObject<? extends FGEGeometricObject> referencedObject;

	public ObtainObject(String anInputLabel, GeomEditController controller) {
		super(anInputLabel, controller);

		availableMethods.add(new MouseSelection());
		availableMethods.add(new ListSelection(controller));
	}

	public ObtainObject(String anInputLabel, GeomEditController controller, boolean appendEndSelection) {
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

	public class MouseSelection extends EditionInputMethod<FGEArea, ObtainObject> {

		private GeometricGraphicalRepresentation focusedObject;

		public MouseSelection() {
			super("With mouse", ObtainObject.this);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (focusedObject != null) {
				focusedObject.setIsFocused(false);
				GeometricObject<? extends FGEGeometricObject> reference = (GeometricObject<? extends FGEGeometricObject>) focusedObject
						.getDrawable();
				referencedObject = reference;
				setConstruction(new ObjectReference(reference.getConstruction()));
				done();
			}
			if (endOnRightClick && e.getButton() == MouseEvent.BUTTON3) {
				endEdition();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			GraphicalRepresentation focused = getFocusRetriever().getFocusedObject(e);

			if (focusedObject != null && focusedObject != focused) {
				focusedObject.setIsFocused(false);
			}

			if (focused instanceof GeometricGraphicalRepresentation
					&& ((GeometricGraphicalRepresentation) focused).getGeometricObject() != null) {
				focusedObject = (GeometricGraphicalRepresentation) focused;
				focusedObject.setIsFocused(true);
			} else {
				focusedObject = null;
			}

		}

		@Override
		public InputComponent getInputComponent() {
			return new InputComponentButton(MouseSelection.this);
		}

	}

	public class ListSelection extends EditionInputMethod<FGEArea, ObtainObject> {

		private DropDownSelection dropDown;

		public ListSelection(GeomEditController controller) {
			super("In list", ObtainObject.this);
			dropDown = new DropDownSelection(this, controller);
		}

		@Override
		public InputComponent getInputComponent() {
			return dropDown;
		}

		protected class DropDownSelection extends JPanel implements InputComponent {
			private JComboBox dropDown;
			private JButton activateButton;
			private JButton okButton;

			protected DropDownSelection(final ListSelection method, GeomEditController controller) {
				super(new FlowLayout(FlowLayout.CENTER, 5, 0));

				DefaultComboBoxModel model = new DefaultComboBoxModel(controller.getDrawing().getModel().getChilds());
				dropDown = new JComboBox(model);
				dropDown.setRenderer(new DefaultListCellRenderer() {
					@Override
					public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
							boolean cellHasFocus) {
						Component returned = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
						if (returned instanceof JLabel) {
							((JLabel) returned).setText(((GeometricObject<?>) value).name);
						}
						return returned;
					}
				});
				dropDown.setEnabled(false);
				activateButton = new JButton(method.getMethodLabel());
				activateButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						method.getEditionInput().setActiveMethod(method);
					}
				});
				okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						setConstruction(new ObjectReference(((GeometricObject<?>) dropDown.getSelectedItem()).getConstruction()));
						done();
					}
				});
				okButton.setEnabled(false);

				add(activateButton);
				add(dropDown);
				add(okButton);

			}

			@Override
			public void disableInputComponent() {
				dropDown.setEnabled(false);
				okButton.setEnabled(false);
			}

			@Override
			public void enableInputComponent() {
				dropDown.setEnabled(true);
				okButton.setEnabled(true);
			}

		}

	}

	@Override
	public ObjectReference<? extends FGEArea> getConstruction() {
		return (ObjectReference<? extends FGEArea>) super.getConstruction();
	}

	public GeometricObject<? extends FGEGeometricObject> getReferencedObject() {
		return referencedObject;
	}

	@Override
	public boolean endOnRightClick() {
		return endOnRightClick;
	}

}
