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

import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.xmlcode.StringEncoder;

public class ObtainDouble extends EditionInput<Double> {
	public static int preferredMethodIndex = 0;

	private boolean endOnRightClick = false;

	public ObtainDouble(String anInputLabel, double defaultValue, GeomEditController controller) {
		super(anInputLabel, controller);

		availableMethods.add(new KeyboardSelection(anInputLabel, defaultValue));
	}

	public ObtainDouble(String anInputLabel, double defaultValue, GeomEditController controller, boolean appendEndSelection) {
		this(anInputLabel, defaultValue, controller);
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

	public class KeyboardSelection extends EditionInputMethod<Double, ObtainDouble> {

		private InputComponentTextField<Double> inputComponent;

		/*private JTextField tf;
		private JLabel label;
		private JPanel panel;*/

		public KeyboardSelection(String labelString, double defaultValue) {
			super("Typing", ObtainDouble.this);
			/*panel = new JPanel(new FlowLayout());
			label = new JLabel(labelString);
			tf = new JTextField();*/
			inputComponent = new InputComponentTextField<Double>(KeyboardSelection.this, defaultValue) {

				@Override
				public int getColumnSize() {
					return 8;
				}

				@Override
				public String convertDataToString(Double data) {
					if (data == null) {
						return "";
					}
					StringEncoder.getDefaultInstance();
					return StringEncoder.encodeDouble(data);
				}

				@Override
				public Double convertStringToData(String string) {
					if (string == null || string.trim().equals("")) {
						return null;
					}
					StringEncoder.getDefaultInstance();
					return StringEncoder.decodeAsDouble(string);
				}

				@Override
				public void dataEntered(Double data) {
					setInputData(data);
					done();
				}

			};
		}

		@Override
		public InputComponent getInputComponent() {
			return inputComponent;
		}

	}

	@Override
	public boolean endOnRightClick() {
		return endOnRightClick;
	}

}
