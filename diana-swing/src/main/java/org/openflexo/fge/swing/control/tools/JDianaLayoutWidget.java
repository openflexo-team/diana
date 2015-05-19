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

package org.openflexo.fge.swing.control.tools;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import org.openflexo.fge.FGEIconLibrary;
import org.openflexo.fge.control.tools.DianaLayoutWidget;
import org.openflexo.fge.swing.SwingViewFactory;

/**
 * SWING implementation of {@link DianaLayoutWidget}
 * 
 * @author sylvain
 * 
 */
public class JDianaLayoutWidget extends DianaLayoutWidget<JToolBar, SwingViewFactory> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(JDianaLayoutWidget.class.getPackage().getName());

	private final JToolBar component;

	public JDianaLayoutWidget() {
		super();

		component = new JToolBar();
		component.setRollover(true);

		component.add(new JLabel(FGEIconLibrary.TOOLBAR_LEFT_ICON));
		component.add(new LayoutButton(FGEIconLibrary.ALIGN_LEFT_ICON, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alignLeft();
			}
		}));
		component.add(new JLabel(FGEIconLibrary.TOOLBAR_SPACER_ICON));
		component.add(new LayoutButton(FGEIconLibrary.ALIGN_CENTER_ICON, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alignCenter();
			}
		}));
		component.add(new JLabel(FGEIconLibrary.TOOLBAR_SPACER_ICON));
		component.add(new LayoutButton(FGEIconLibrary.ALIGN_RIGHT_ICON, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alignRight();
			}
		}));
		component.add(new JLabel(FGEIconLibrary.TOOLBAR_RIGHT_ICON));

		component.addSeparator();

		component.add(new JLabel(FGEIconLibrary.TOOLBAR_LEFT_ICON));
		component.add(new LayoutButton(FGEIconLibrary.ALIGN_TOP_ICON, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alignTop();
			}
		}));
		component.add(new JLabel(FGEIconLibrary.TOOLBAR_SPACER_ICON));
		component.add(new LayoutButton(FGEIconLibrary.ALIGN_MIDDLE_ICON, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alignMiddle();
			}
		}));
		component.add(new JLabel(FGEIconLibrary.TOOLBAR_SPACER_ICON));
		component.add(new LayoutButton(FGEIconLibrary.ALIGN_BOTTOM_ICON, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alignBottom();
			}
		}));
		component.add(new JLabel(FGEIconLibrary.TOOLBAR_RIGHT_ICON));

		component.validate();
	}

	@SuppressWarnings("serial")
	public class LayoutButton extends JButton {
		public LayoutButton(ImageIcon icon, ActionListener al) {
			super(icon);
			setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.GRAY),
					BorderFactory.createEmptyBorder(2, 0, 2, 0)));
			addActionListener(al);
		}
	}

	@Override
	public JToolBar getComponent() {
		return component;
	}

	@Override
	public SwingViewFactory getDianaFactory() {
		return SwingViewFactory.INSTANCE;
	}

}
