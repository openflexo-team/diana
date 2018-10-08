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


package org.openflexo.fge.swing.control;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.DianaToolFactory;
import org.openflexo.fge.control.PaletteModel;
import org.openflexo.fge.control.actions.DrawConnectorAction;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.swing.JDianaInteractiveEditor;
import org.openflexo.fge.swing.SwingViewFactory;
import org.openflexo.fge.swing.control.tools.JDianaDialogInspectors;
import org.openflexo.fge.swing.control.tools.JDianaInspectors;
import org.openflexo.fge.swing.control.tools.JDianaLayoutWidget;
import org.openflexo.fge.swing.control.tools.JDianaPalette;
import org.openflexo.fge.swing.control.tools.JDianaScaleSelector;
import org.openflexo.fge.swing.control.tools.JDianaStyles;
import org.openflexo.fge.swing.control.tools.JDianaToolSelector;
import org.openflexo.fge.swing.control.tools.JDrawComplexCurveToolController;
import org.openflexo.fge.swing.control.tools.JDrawConnectorToolController;
import org.openflexo.fge.swing.control.tools.JDrawPolygonToolController;
import org.openflexo.fge.swing.control.tools.JDrawShapeToolController;
import org.openflexo.fge.swing.control.tools.JDrawTextToolController;

/**
 * Represent the view factory for Swing technology
 * 
 * @author sylvain
 * 
 */
public class SwingToolFactory implements DianaToolFactory<JComponent> {

	public static SwingToolFactory DEFAULT = new SwingToolFactory(null);

	private final JFrame frame;

	public SwingToolFactory(JFrame frame) {
		this.frame = frame;
	}

	@Override
	public JDianaToolSelector makeDianaToolSelector(AbstractDianaEditor<?, ?, ?> editor) {
		return new JDianaToolSelector((JDianaInteractiveEditor<?>) editor);
	}

	@Override
	public JDianaScaleSelector makeDianaScaleSelector(AbstractDianaEditor<?, ?, ?> editor) {
		return new JDianaScaleSelector((AbstractDianaEditor<?, SwingViewFactory, ?>) editor);
	}

	@Override
	public JDianaStyles makeDianaStyles() {
		return new JDianaStyles();
	}

	@Override
	public JDianaDialogInspectors makeDianaDialogInspectors() {
		return new JDianaDialogInspectors(frame);
	}

	@Override
	public JDianaInspectors makeDianaInspectors() {
		return new JDianaInspectors();
	}

	@Override
	public JDianaLayoutWidget makeDianaLayoutWidget() {
		return new JDianaLayoutWidget();
	}

	@Override
	public JDianaPalette makeDianaPalette(PaletteModel palette) {
		return new JDianaPalette(palette);
	}

	@Override
	public JDrawPolygonToolController makeDrawPolygonToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction control) {
		return new JDrawPolygonToolController(controller, control);
	}

	@Override
	public JDrawComplexCurveToolController makeDrawClosedCurveToolController(DianaInteractiveEditor<?, ?, ?> controller,
			DrawShapeAction control, boolean isClosedCurve) {
		return new JDrawComplexCurveToolController(controller, control, isClosedCurve);
	}

	@Override
	public JDrawShapeToolController makeDrawShapeToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction control) {
		return new JDrawShapeToolController(controller, control);
	}

	@Override
	public JDrawConnectorToolController makeDrawConnectorToolController(DianaInteractiveEditor<?, ?, ?> controller,
			DrawConnectorAction control) {
		return new JDrawConnectorToolController(controller, control);
	}

	@Override
	public JDrawTextToolController makeDrawTextToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction control) {
		return new JDrawTextToolController(controller, control);
	}
}
