/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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


package org.openflexo.fge.control;

import org.openflexo.fge.control.actions.DrawConnectorAction;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.control.tools.DianaInspectors;
import org.openflexo.fge.control.tools.DianaLayoutWidget;
import org.openflexo.fge.control.tools.DianaPalette;
import org.openflexo.fge.control.tools.DianaScaleSelector;
import org.openflexo.fge.control.tools.DianaStyles;
import org.openflexo.fge.control.tools.DianaToolSelector;
import org.openflexo.fge.control.tools.DrawComplexCurveToolController;
import org.openflexo.fge.control.tools.DrawConnectorToolController;
import org.openflexo.fge.control.tools.DrawPolygonToolController;
import org.openflexo.fge.control.tools.DrawShapeToolController;
import org.openflexo.fge.control.tools.DrawTextToolController;

/**
 * Represent the tool factory for a given technology (eg. Swing)
 * 
 * @author sylvain
 * 
 * @param <C>
 *            base minimal class of components build by this tool factory (eg JComponent for Swing)
 */
public interface DianaToolFactory<C> {

	public DianaToolSelector<? extends C, ?> makeDianaToolSelector(AbstractDianaEditor<?, ?, ?> editor);

	public DianaScaleSelector<? extends C, ?> makeDianaScaleSelector(AbstractDianaEditor<?, ?, ?> editor);

	public DianaStyles<? extends C, ?> makeDianaStyles();

	public DianaInspectors<?, ?> makeDianaDialogInspectors();

	public DianaInspectors<?, ?> makeDianaInspectors();

	public DianaLayoutWidget<? extends C, ?> makeDianaLayoutWidget();

	public DianaPalette<? extends C, ?> makeDianaPalette(DrawingPalette palette);

	public DrawPolygonToolController<?> makeDrawPolygonToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction control);

	public DrawComplexCurveToolController<?> makeDrawClosedCurveToolController(DianaInteractiveEditor<?, ?, ?> controller,
			DrawShapeAction control, boolean isClosedCurve);

	public DrawShapeToolController<?> makeDrawShapeToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction control);

	public DrawConnectorToolController<?> makeDrawConnectorToolController(DianaInteractiveEditor<?, ?, ?> controller,
			DrawConnectorAction control);

	public DrawTextToolController<?> makeDrawTextToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction control);

}
