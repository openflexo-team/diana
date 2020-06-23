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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.diana.geomedit.edition.EditionInputMethod.InputComponent;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstructionFactory;
import org.openflexo.diana.swing.graphics.JDianaDrawingGraphics;

public abstract class EditionInput<O> {
	private String inputLabel;

	public Vector<EditionInputMethod> availableMethods;
	public EditionInputMethod activeMethod;

	private GeomEditDrawingController controller;

	private GeometricConstruction<?> contruction;

	public EditionInput(String anInputLabel, GeomEditDrawingController aController) {
		super();
		controller = aController;
		inputLabel = anInputLabel;
		availableMethods = new Vector<EditionInputMethod>();
		activeMethod = null;
	}

	protected abstract int getPreferredMethodIndex();

	private JPanel subPanel;

	public void resetControlPanel(JPanel controlPanel) {
		if (subPanel != null) {
			controlPanel.remove(subPanel);
			subPanel = null;
		}
	}

	public void updateControlPanel(JPanel controlPanel, JPanel availableMethodsPanel) {
		availableMethodsPanel.removeAll();
		if (activeMethod == null && getPreferredMethodIndex() < availableMethods.size()) {
			activeMethod = availableMethods.get(getPreferredMethodIndex());
		}
		availableMethodsPanel.add(new JLabel(inputLabel));
		for (final EditionInputMethod method : availableMethods) {
			InputComponent inputComponent = method.getInputComponent();
			availableMethodsPanel.add((JComponent) inputComponent);
			if (method == getActiveMethod()) {
				inputComponent.enableInputComponent();
			}
			else {
				inputComponent.disableInputComponent();
			}
		}
		if (getActiveMethod().hasChildInputs()) {
			subPanel = new JPanel(new BorderLayout());
			JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
			subPanel.add(flowPanel, BorderLayout.WEST);
			controlPanel.add(subPanel, BorderLayout.SOUTH);
			getActiveMethod().getCurrentInput().updateControlPanel(subPanel, flowPanel);
		}
		else if (subPanel != null) {
			controlPanel.remove(subPanel);
			subPanel = null;
		}
		availableMethodsPanel.revalidate();
		availableMethodsPanel.repaint();
	}

	public String getInputLabel() {
		return inputLabel;
	}

	public String getActiveMethodLabel() {
		if (getActiveMethod() != null) {
			return getActiveMethod().getMethodLabel();
		}
		return "No active selection method";
	}

	public EditionInputMethod getActiveMethod() {
		return activeMethod;
	}

	public void setActiveMethod(EditionInputMethod aMethod) {
		if (activeMethod != aMethod) {
			activeMethod = aMethod;
			controller.updateCurrentInput();
		}
	}

	public EditionInputMethod getDerivedActiveMethod() {
		if (activeMethod.hasChildInputs()) {
			return activeMethod.getCurrentInput().getDerivedActiveMethod();
		}
		else {
			return activeMethod;
		}
	}

	public GeomEditDrawingController getController() {
		return controller;
	}

	public GeometricConstructionFactory getFactory() {
		return getController().getFactory();
	}

	private O inputData;

	public O getInputData() {
		if (contruction != null) {
			return (O) contruction.getData();
		}
		return inputData;
	}

	public void setInputData(O data) {
		inputData = data;
	}

	public void setConstruction(GeometricConstruction<?> aContruction) {
		contruction = aContruction;
	}

	public GeometricConstruction<?> getConstruction() {
		return contruction;
	}

	public void done() {
		if (getParentInputMethod() != null) {
			getParentInputMethod().nextChildInput();
		}
		else {
			getController().currentInputGiven();
		}
	}

	public void endEdition() {
	}

	public void paint(JDianaDrawingGraphics graphics) {
	}

	private EditionInputMethod parentInputMethod = null;

	public EditionInputMethod getParentInputMethod() {
		return parentInputMethod;
	}

	public void setParentInputMethod(EditionInputMethod aMethod) {
		this.parentInputMethod = aMethod;
	}

	public class EndEditionSelection extends EditionInputMethod<O, EditionInput<O>> {

		public EndEditionSelection() {
			super("Done", EditionInput.this);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			endEdition();
		}

		@Override
		public InputComponent getInputComponent() {
			return new EndEditionButton(this);
		}

	}

	public class EndEditionButton extends JButton implements InputComponent {
		public EndEditionButton(final EndEditionSelection method) {
			super(method.getMethodLabel());
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					method.getEditionInput().endEdition();
				}
			});
		}

		@Override
		public void enableInputComponent() {
			setSelected(true);
		}

		@Override
		public void disableInputComponent() {
			setSelected(false);
		}
	}

	public abstract boolean endOnRightClick();

}
