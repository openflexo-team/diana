/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.diana.swing.view;

import javax.swing.JComponent;

import org.openflexo.diana.swing.graphics.JDianaGraphics;
import org.openflexo.diana.swing.paint.DianaPaintManager;
import org.openflexo.diana.view.DianaView;

/**
 * Implemented by all Swing views
 * 
 * @author sylvain
 * 
 * @param <O>
 *            type of object beeing represented by this view
 * @param <C>
 *            type of component this view is beeing instance (at least a JComponent)
 */
public interface JDianaView<O, C extends JComponent> extends DianaView<O, C> {

	public JDrawingView<?> getDrawingView();

	public JLabelView<O> getLabelView();

	public JDianaGraphics getDianaGraphics();

	public DianaPaintManager getPaintManager();

}
