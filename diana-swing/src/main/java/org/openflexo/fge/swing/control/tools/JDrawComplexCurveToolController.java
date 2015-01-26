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

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.control.tools.DrawComplexCurveToolController;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.swing.graphics.JFGEGeometricGraphics;
import org.openflexo.fge.swing.view.JDrawingView;

/**
 * Swing implementation for the controller of the Complex Curve drawing tool<br>
 * As swing component, this controller is driven by {@link MouseEvent}
 * 
 * @author sylvain
 */
public class JDrawComplexCurveToolController extends DrawComplexCurveToolController<MouseEvent> {

	private static final Logger logger = Logger.getLogger(JDrawComplexCurveToolController.class.getPackage().getName());

	public JDrawComplexCurveToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction control, boolean isClosedCurve) {
		super(controller, control, isClosedCurve);
	}

	/**
	 * Return the DrawingView of the controller this tool is associated to
	 * 
	 * @return
	 */
	public JDrawingView<?> getDrawingView() {
		return (JDrawingView<?>) super.getDrawingView();
	}

	public boolean isFinalizationEvent(MouseEvent e) {
		return e.getClickCount() == 2 || e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3;
	}

	public DrawingTreeNode<?, ?> getFocusedObject(MouseEvent e) {
		return getDrawingView().getFocusRetriever().getFocusedObject(e);
	}

	/**
	 * Return point where event occurs, relative to DrawingView
	 */
	public FGEPoint getPoint(MouseEvent e) {
		Point pt = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), getDrawingView());
		return new FGEPoint(pt.getX(), pt.getY());
	}

	@Override
	public JFGEGeometricGraphics makeGraphics(ForegroundStyle foregroundStyle) {
		JFGEGeometricGraphics returned = new JFGEGeometricGraphics(getCurrentEditedShape(), getDrawingView());
		returned.setDefaultForeground(foregroundStyle);
		return returned;
	}

	@Override
	public JFGEGeometricGraphics getGraphics() {
		return (JFGEGeometricGraphics) super.getGraphics();
	}
}
