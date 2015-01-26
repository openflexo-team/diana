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

import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import org.openflexo.fge.control.tools.DianaStyles;
import org.openflexo.fge.swing.SwingViewFactory;

/**
 * SWING implementation of {@link DianaStyles} toolbar
 * 
 * @author sylvain
 * 
 */
public class JDianaStyles extends DianaStyles<JToolBar, SwingViewFactory> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(JDianaStyles.class.getPackage().getName());

	private JToolBar component;

	public JToolBar getComponent() {
		if (component == null) {
			component = new JToolBar();
			component.setRollover(true);
			component.add((JComponent) getForegroundSelector());
			component.add((JComponent) getBackgroundSelector());
			component.add((JComponent) getShapeSelector());
			component.add((JComponent) getShadowStyleSelector());
			component.add((JComponent) getTextStyleSelector());
			component.add(Box.createHorizontalGlue());
			component.validate();
		}
		return component;
	}

	@Override
	public SwingViewFactory getDianaFactory() {
		return SwingViewFactory.INSTANCE;
	}

}
