/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-ppt-editor, a component of the software infrastructure 
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

package org.openflexo.diana.ppteditor;

import java.util.logging.Logger;

import javax.swing.JPopupMenu;

import org.apache.poi.sl.usermodel.Slide;
import org.openflexo.diana.ConnectorGraphicalRepresentation;
import org.openflexo.diana.DianaCoreUtils;
import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.control.actions.DrawConnectorAction;
import org.openflexo.diana.control.actions.DrawShapeAction;
import org.openflexo.diana.swing.JDianaInteractiveEditor;
import org.openflexo.diana.swing.control.SwingToolFactory;

public class SlideEditor extends JDianaInteractiveEditor<Slide> {

	private static final Logger logger = Logger.getLogger(SlideEditor.class.getPackage().getName());

	private JPopupMenu contextualMenu;

	// private DrawingTreeNode<?, ?> contextualMenuInvoker;
	// private Point contextualMenuClickedPoint;

	// private Shape copiedShape;

	public SlideEditor(final SlideDrawing aDrawing) {
		super(aDrawing, DianaCoreUtils.TOOLS_FACTORY, SwingToolFactory.DEFAULT);

		DrawShapeAction drawShapeAction = new DrawShapeAction() {
			@Override
			public void performedDrawNewShape(ShapeGraphicalRepresentation graphicalRepresentation, ContainerNode<?, ?> parentNode) {
				System.out.println("OK, perform draw new shape with " + graphicalRepresentation + " and parent: " + parentNode);
			}
		};

		DrawConnectorAction drawConnectorAction = new DrawConnectorAction() {

			@Override
			public void performedDrawNewConnector(ConnectorGraphicalRepresentation graphicalRepresentation, ShapeNode<?> startNode,
					ShapeNode<?> endNode) {
				System.out.println(
						"OK, perform draw new connector with " + graphicalRepresentation + " start: " + startNode + " end: " + endNode);

			}
		};

		setDrawCustomShapeAction(drawShapeAction);
		setDrawShapeAction(drawShapeAction);
		setDrawConnectorAction(drawConnectorAction);
	}

	@Override
	public SlideDrawing getDrawing() {
		return (SlideDrawing) super.getDrawing();
	}

	public Slide getSlide() {
		return getDrawing().getModel();
	}
}
