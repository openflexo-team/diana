/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-swing, a component of the software infrastructure 
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

package org.openflexo.diana.swing.control.tools;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.tools.DianaScaleSelector;
import org.openflexo.diana.swing.SwingViewFactory;

/**
 * SWING implementation of the {@link DianaScaleSelector}
 * 
 * @author sylvain
 * 
 */
public class JDianaScaleSelector extends DianaScaleSelector<JToolBar, SwingViewFactory> {

	private static final int MAX_ZOOM_VALUE = 300;
	protected JTextField scaleTF;
	protected JSlider slider;

	protected ChangeListener sliderChangeListener;
	protected ActionListener actionListener;

	private final JToolBar component;

	private boolean isInitialized = false;

	public JDianaScaleSelector(AbstractDianaEditor<?, SwingViewFactory, ?> editor) {
		super(editor);
		component = new JToolBar();
		component.setOpaque(false);
		scaleTF = new JTextField(5);
		scaleTF.setFont(new Font("SansSerif", Font.PLAIN, 9));
		int currentScale = (getEditor() != null ? (int) (getEditor().getScale() * 100) : 100);
		scaleTF.setText(currentScale + "%");
		slider = new JSlider(SwingConstants.HORIZONTAL, 0, MAX_ZOOM_VALUE, currentScale);
		slider.setPreferredSize(new Dimension(200, 15));
		slider.setOpaque(false);
		slider.setMajorTickSpacing(100);
		slider.setMinorTickSpacing(20);
		slider.setPaintTicks(false/* true */);
		slider.setPaintLabels(false);
		slider.setBorder(BorderFactory.createEmptyBorder());
		sliderChangeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (slider.getValue() > 0) {
					getEditor().setScale((double) slider.getValue() / 100);
					scaleTF.removeActionListener(actionListener);
					scaleTF.setText("" + (int) (getEditor().getScale() * 100) + "%");
					scaleTF.addActionListener(actionListener);
				}
			}
		};
		actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// logger.info("On fait avec "+scaleTF.getText()+" ce qui donne: "+(((double)Integer.decode(scaleTF.getText()))/100));
					Integer newScale = null;
					if (scaleTF.getText().indexOf("%") > -1) {
						newScale = Integer.decode(scaleTF.getText().substring(0, scaleTF.getText().indexOf("%")));
					}
					else {
						newScale = Integer.decode(scaleTF.getText());
					}
					if (newScale > MAX_ZOOM_VALUE) {
						newScale = MAX_ZOOM_VALUE;
						SwingUtilities.invokeLater(() -> scaleTF.setText(MAX_ZOOM_VALUE + "%"));
					}
					getEditor().setScale((double) newScale / 100);
				} catch (NumberFormatException exception) {
					// Forget
				}
				scaleTF.removeActionListener(actionListener);
				slider.removeChangeListener(sliderChangeListener);
				scaleTF.setText("" + (int) (getEditor().getScale() * 100) + "%");
				slider.setValue((int) (getEditor().getScale() * 100));
				slider.addChangeListener(sliderChangeListener);
				scaleTF.addActionListener(actionListener);
			}
		};
		scaleTF.addActionListener(actionListener);
		slider.addChangeListener(sliderChangeListener);
		component.add(slider);
		component.add(scaleTF);
		isInitialized = true;
		// setBorder(BorderFactory.createEmptyBorder());
	}

	@Override
	public JToolBar getComponent() {
		return component;
	}

	@Override
	public void handleScaleChanged() {
		if (isInitialized) {
			slider.setValue((int) (getEditor().getScale() * 100));
		}
	}

	@Override
	public SwingViewFactory getDianaFactory() {
		return SwingViewFactory.INSTANCE;
	}

}
