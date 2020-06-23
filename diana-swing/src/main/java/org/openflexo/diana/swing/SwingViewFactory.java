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

package org.openflexo.diana.swing;

import javax.swing.JComponent;

import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.ShadowStyle;
import org.openflexo.diana.TextStyle;
import org.openflexo.diana.connectors.ConnectorSpecification;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.DianaInteractiveViewer;
import org.openflexo.diana.control.MouseControlContext;
import org.openflexo.diana.control.actions.DNDInfo;
import org.openflexo.diana.control.actions.MoveAction;
import org.openflexo.diana.control.tools.BackgroundStyleFactory;
import org.openflexo.diana.control.tools.DianaPalette;
import org.openflexo.diana.control.tools.PaletteController;
import org.openflexo.diana.control.tools.ShapeSpecificationFactory;
import org.openflexo.diana.shapes.ShapeSpecification;
import org.openflexo.diana.swing.control.JDNDInfo;
import org.openflexo.diana.swing.control.JMouseControlContext;
import org.openflexo.diana.swing.control.tools.JDianaPalette;
import org.openflexo.diana.swing.view.DianaViewMouseListener;
import org.openflexo.diana.swing.view.JConnectorView;
import org.openflexo.diana.swing.view.JDianaView;
import org.openflexo.diana.swing.view.JDrawingView;
import org.openflexo.diana.swing.view.JShapeView;
import org.openflexo.diana.swing.widget.JConnectorPreviewPanel;
import org.openflexo.diana.swing.widget.JFIBBackgroundStyleSelector;
import org.openflexo.diana.swing.widget.JFIBForegroundStyleSelector;
import org.openflexo.diana.swing.widget.JFIBShadowStyleSelector;
import org.openflexo.diana.swing.widget.JFIBShapeSelector;
import org.openflexo.diana.swing.widget.JFIBTextStyleSelector;
import org.openflexo.diana.swing.widget.JShapePreviewPanel;
import org.openflexo.diana.view.DianaView;
import org.openflexo.diana.view.DianaViewFactory;

/**
 * Represent the view factory for Swing technology
 * 
 * @author sylvain
 * 
 */
public class SwingViewFactory implements DianaViewFactory<SwingViewFactory, JComponent> {

	public static SwingViewFactory INSTANCE = new SwingViewFactory();

	protected SwingViewFactory() {
	}

	/**
	 * Build and return a MouseListener for supplied node and view<br>
	 * Here return null as a simple viewer doesn't allow any editing facility
	 * 
	 * @param node
	 * @param view
	 * @return
	 */
	public <O> DianaViewMouseListener makeViewMouseListener(DrawingTreeNode<O, ?> node, DianaView<O, ? extends JComponent> view,
			AbstractDianaEditor<?, SwingViewFactory, JComponent> controller) {
		return new DianaViewMouseListener(node, (JDianaView<O, ? extends JComponent>) view);
	}

	/**
	 * Instantiate a new JDrawingView<br>
	 * You might override this method for a custom view managing
	 * 
	 * @return
	 */
	@Override
	public <M> JDrawingView<M> makeDrawingView(AbstractDianaEditor<M, SwingViewFactory, JComponent> controller) {
		return new JDrawingView<M>(controller);
	}

	/**
	 * Instantiate a new JShapeView for a shape node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	@Override
	public <O> JShapeView<O> makeShapeView(ShapeNode<O> shapeNode, AbstractDianaEditor<?, SwingViewFactory, JComponent> controller) {
		return new JShapeView<O>(shapeNode, controller);
	}

	/**
	 * Instantiate a new JConnectorView for a connector node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	@Override
	public <O> JConnectorView<O> makeConnectorView(ConnectorNode<O> connectorNode,
			AbstractDianaEditor<?, SwingViewFactory, JComponent> controller) {
		return new JConnectorView<O>(connectorNode, controller);
	}

	@Override
	public PaletteController<SwingViewFactory, JComponent> makePaletteController(DianaPalette<?, ?> palette) {
		return new JPaletteController((JDianaPalette) palette);
	}

	@Override
	public DNDInfo makeDNDInfo(MoveAction moveAction, ShapeNode<?> shapeNode, DianaInteractiveViewer<?, ?, ?> controller,
			MouseControlContext initialContext) {
		return new JDNDInfo(moveAction, shapeNode, controller, (JMouseControlContext) initialContext);
	}

	@Override
	public JFIBBackgroundStyleSelector makeFIBBackgroundStyleSelector(BackgroundStyleFactory backgroundStyleFactory) {
		return new JFIBBackgroundStyleSelector(backgroundStyleFactory);
	}

	@Override
	public JFIBForegroundStyleSelector makeFIBForegroundStyleSelector(ForegroundStyle foregroundStyle) {
		return new JFIBForegroundStyleSelector(foregroundStyle);
	}

	@Override
	public JFIBTextStyleSelector makeFIBTextStyleSelector(TextStyle textStyle) {
		return new JFIBTextStyleSelector(textStyle);
	}

	@Override
	public JFIBShadowStyleSelector makeFIBShadowStyleSelector(ShadowStyle shadowStyle) {
		return new JFIBShadowStyleSelector(shadowStyle);
	}

	@Override
	public JFIBShapeSelector makeFIBShapeSelector(ShapeSpecificationFactory shapeFactory) {
		return new JFIBShapeSelector(shapeFactory);
	}

	@Override
	public JShapePreviewPanel makeShapePreviewPanel(ShapeSpecification shapeSpecification) {
		return new JShapePreviewPanel(shapeSpecification);
	}

	@Override
	public JConnectorPreviewPanel makeConnectorPreviewPanel(ConnectorSpecification connectorSpecification) {
		return new JConnectorPreviewPanel(connectorSpecification);
	}

}
