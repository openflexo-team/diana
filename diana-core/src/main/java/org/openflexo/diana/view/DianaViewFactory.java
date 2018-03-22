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

package org.openflexo.diana.view;

import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.ShadowStyle;
import org.openflexo.diana.TextStyle;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.ShapeNode;
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
import org.openflexo.diana.view.widget.ConnectorPreviewPanel;
import org.openflexo.diana.view.widget.FIBBackgroundStyleSelector;
import org.openflexo.diana.view.widget.FIBForegroundStyleSelector;
import org.openflexo.diana.view.widget.FIBShadowStyleSelector;
import org.openflexo.diana.view.widget.FIBShapeSelector;
import org.openflexo.diana.view.widget.FIBTextStyleSelector;
import org.openflexo.diana.view.widget.ShapePreviewPanel;

/**
 * Represent the view factory for a given technology (eg. Swing)
 * 
 * @author sylvain
 * 
 * @param <C>
 *            base minimal class of components build by this tool factory (eg JComponent for Swing)
 */
public interface DianaViewFactory<F extends DianaViewFactory<F, C>, C> {

	/**
	 * Instantiate a new DrawingView<br>
	 * 
	 * @return
	 */
	public <M> DrawingView<M, ? extends C> makeDrawingView(AbstractDianaEditor<M, F, C> controller);

	/**
	 * Instantiate a new ShapeView for a shape node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> ShapeView<O, ? extends C> makeShapeView(ShapeNode<O> shapeNode, AbstractDianaEditor<?, F, C> controller);

	/**
	 * Instantiate a new ConnectorView for a connector node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> ConnectorView<O, ? extends C> makeConnectorView(ConnectorNode<O> connectorNode, AbstractDianaEditor<?, F, C> controller);

	/*
	public DianaDrawingGraphics makeDrawingGraphics(RootNode<?> rootNode);
	
	public DianaShapeGraphics makeShapeGraphics(ShapeNode<?> shapeNode);
	
	public DianaConnectorGraphics makeConnectorGraphics(ConnectorNode<?> connectorNode);
	
	public DianaGeometricGraphics makeGeometricGraphics(GeometricNode<?> geometricNode);
	
	public DianaDrawingDecorationGraphics makeDrawingDecorationGraphics(RootNode<?> rootNode);
	
	public DianaShapeDecorationGraphics makeShapeDecorationGraphics(ShapeNode<?> shapeNode);
	
	public DianaSymbolGraphics makeSymbolGraphics(ConnectorNode<?> connectorNode);
	*/
	public PaletteController<F, C> makePaletteController(DianaPalette<?, ?> palette);

	public FIBBackgroundStyleSelector makeFIBBackgroundStyleSelector(BackgroundStyleFactory backgroundStyleFactory);

	public FIBForegroundStyleSelector makeFIBForegroundStyleSelector(ForegroundStyle foregroundStyle);

	public FIBTextStyleSelector makeFIBTextStyleSelector(TextStyle textStyle);

	public FIBShadowStyleSelector makeFIBShadowStyleSelector(ShadowStyle shadowStyle);

	public FIBShapeSelector makeFIBShapeSelector(ShapeSpecificationFactory shapeFactory);

	public ShapePreviewPanel makeShapePreviewPanel(ShapeSpecification shapeSpecification);

	public ConnectorPreviewPanel makeConnectorPreviewPanel(ConnectorSpecification shapeSpecification);

	public DNDInfo makeDNDInfo(MoveAction moveAction, ShapeNode<?> shapeNode, DianaInteractiveViewer<?, ?, ?> controller,
			final MouseControlContext initialContext);
}
