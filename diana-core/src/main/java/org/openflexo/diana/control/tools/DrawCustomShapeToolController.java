/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.diana.control.tools;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.connie.type.TypeUtils;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.GeometricGraphicalRepresentation;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.GRBinding.GeometricGRBinding;
import org.openflexo.diana.GRProvider.GeometricGRProvider;
import org.openflexo.diana.control.DianaInteractiveEditor;
import org.openflexo.diana.control.actions.DrawShapeAction;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.graphics.DianaGeometricGraphics;
import org.openflexo.diana.impl.ContainerNodeImpl;
import org.openflexo.diana.impl.DrawingImpl;
import org.openflexo.diana.impl.GeometricNodeImpl;
import org.openflexo.diana.notifications.GeometryModified;
import org.openflexo.diana.view.DrawingView;

/**
 * Abstract implementation for the controller of a DrawCustomShape tool
 * 
 * @author sylvain
 * 
 * @param <ME>
 *            technology-specific controlling events type
 */
public abstract class DrawCustomShapeToolController<S extends DianaShape<S>, ME> extends ToolController<ME> {

	private static final Logger logger = Logger.getLogger(DrawCustomShapeToolController.class.getPackage().getName());

	protected DrawingTreeNode<?, ?> parentNode = null;

	private S shape;
	private GeometricNode<S> currentEditedShapeGeometricNode;
	private GeometricGraphicalRepresentation geomGR;

	private DianaGeometricGraphics graphics;

	public DrawCustomShapeToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction toolAction) {
		super(controller, toolAction);
	}

	@Override
	public DrawShapeAction getToolAction() {
		return (DrawShapeAction) super.getToolAction();
	}

	public abstract DianaGeometricGraphics makeGraphics(ForegroundStyle foregroundStyle);

	@Override
	public DianaGeometricGraphics getGraphics() {
		return graphics;
	}

	/**
	 * Return the DrawingView of the controller this tool is associated to
	 * 
	 * @return
	 */
	@Override
	public DrawingView<?, ?> getDrawingView() {
		if (getController() != null) {
			return getController().getDrawingView();
		}
		return null;
	}

	@Override
	protected void startMouseEdition(ME e) {
		super.startMouseEdition(e);

		parentNode = getFocusedObject(e);

		shape = makeDefaultShape(e);
		Class<S> shapeClass = (Class<S>) TypeUtils.getTypeArgument(getClass(), DrawCustomShapeToolController.class, 0);
		geomGR = getFactory().makeGeometricGraphicalRepresentation(shape);
		GeometricGRBinding<S> editedGeometricObjectBinding = getController().getDrawing().bindGeometric(shapeClass, "editedGeometricObject",
				new GeometricGRProvider<S>() {
					@Override
					public GeometricGraphicalRepresentation provideGR(S drawable, DianaModelFactory factory) {
						return geomGR;
					}
				});
		currentEditedShapeGeometricNode = new GeometricNodeImpl<>((DrawingImpl<?>) getController().getDrawing(), shape,
				editedGeometricObjectBinding, (ContainerNodeImpl<?, ?>) getController().getDrawing().getRoot());
		currentEditedShapeGeometricNode.getPropertyChangeSupport().addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(GeometryModified.EVENT_NAME)) {
					geometryChanged();
				}
			}
		});

		graphics = makeGraphics(getFactory().makeForegroundStyle(Color.GREEN));

		// TODO Check this / fge_under_pamela
		/*currentEditedShapeGeometricNode = new GeometricGraphicalRepresentationImpl(shape, shape, controller.getDrawing()) {
			@Override
			public void notifyGeometryChanged() {
				super.notifyGeometryChanged();
				geometryChanged();
			}
		};*/
		currentEditedShapeGeometricNode.getGraphicalRepresentation()
				.setBackground(getController().getInspectedBackgroundStyle().cloneStyle());
		currentEditedShapeGeometricNode.getGraphicalRepresentation()
				.setForeground(getController().getInspectedForegroundStyle().cloneStyle());
		geometryChanged();
	}

	public abstract S makeDefaultShape(ME e);

	/**
	 * Returns shape currently being edited (using DrawShape tool)
	 * 
	 * @return
	 */
	public S getShape() {
		return shape;
	}

	public void setShape(DianaShape<?> shape) {
		this.shape = (S) shape.clone();
		currentEditedShapeGeometricNode.getGraphicalRepresentation().setGeometricObject(this.shape);
		geometryChanged();
	}

	/**
	 * Returns graphical representation for shape currently being edited (using DrawShape tool)
	 * 
	 * @return
	 */
	public GeometricNode<S> getCurrentEditedShape() {
		return currentEditedShapeGeometricNode;
	}

	protected void geometryChanged() {
		getController().getDelegate().repaintAll();
	}

	@Override
	public abstract DrawingTreeNode<?, ?> getFocusedObject(ME e);

	public void paintCurrentEditedShape() {

		if (!editionHasBeenStarted()) {
			return;
		}

		currentEditedShapeGeometricNode.paint(graphics);
	}

	public List<? extends ControlArea<?>> getControlAreas() {
		return currentEditedShapeGeometricNode.getControlPoints();
	}

	public abstract ShapeGraphicalRepresentation buildShapeGraphicalRepresentation();

	public void makeNewShape() {
		if (getToolAction() != null && parentNode instanceof ContainerNode) {
			ShapeGraphicalRepresentation newShapeGraphicalRepresentation = buildShapeGraphicalRepresentation();
			getToolAction().performedDrawNewShape(newShapeGraphicalRepresentation, (ContainerNode<?, ?>) parentNode);
		}
		else {
			System.out.println("toolAction=" + getToolAction());
			System.out.println("parentNode=" + parentNode);
			logger.warning("No DrawShapeAction defined !");
		}
	}

	@Override
	public void delete() {
		logger.warning("Please implement deletion for DrawCustomShapeToolController");
		super.delete();
	}

}
