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

package org.openflexo.diana.impl;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.diana.BackgroundStyle;
import org.openflexo.diana.ContainerGraphicalRepresentation;
import org.openflexo.diana.DianaLayoutManager;
import org.openflexo.diana.DianaLayoutManagerSpecification;
import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.GRBinding;
import org.openflexo.diana.GRProperty;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.ShadowStyle;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.ConstraintDependency;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.GRBinding.ShapeGRBinding;
import org.openflexo.diana.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.diana.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaGeometricObject;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.GeomUtils;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaIntersectionArea;
import org.openflexo.diana.graphics.DianaShapeGraphics;
import org.openflexo.diana.graphics.ShapeDecorationPainter;
import org.openflexo.diana.graphics.ShapePainter;
import org.openflexo.diana.notifications.ObjectHasMoved;
import org.openflexo.diana.notifications.ObjectHasResized;
import org.openflexo.diana.notifications.ObjectMove;
import org.openflexo.diana.notifications.ObjectResized;
import org.openflexo.diana.notifications.ObjectWillMove;
import org.openflexo.diana.notifications.ObjectWillResize;
import org.openflexo.diana.notifications.ShapeChanged;
import org.openflexo.diana.notifications.ShapeNeedsToBeRedrawn;
import org.openflexo.diana.shapes.ShapeSpecification;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.diana.shapes.impl.ShapeImpl;
import org.openflexo.toolbox.ConcatenedList;
import org.openflexo.toolbox.StringUtils;

public class ShapeNodeImpl<O> extends ContainerNodeImpl<O, ShapeGraphicalRepresentation> implements ShapeNode<O> {

	private static final Logger logger = Logger.getLogger(ShapeNodeImpl.class.getPackage().getName());

	private boolean isMoving = false;

	private boolean observeParentGRBecauseMyLocationReferToIt = false;

	// private DianaShapeGraphicsImpl graphics;
	// private DianaShapeDecorationGraphicsImpl decorationGraphics;
	private ShapeDecorationPainter decorationPainter;
	private ShapePainter shapePainter;

	private ShapeImpl<?> shape;

	private DianaLayoutManager<?, ?> layoutManager;
	private boolean layoutValidated = false;

	private BindingValueChangeListener<Double> xConstraintsListener;
	private BindingValueChangeListener<Double> yConstraintsListener;
	private BindingValueChangeListener<Double> widthConstraintsListener;
	private BindingValueChangeListener<Double> heightConstraintsListener;

	public static final int DEFAULT_BORDER_TOP = 0;
	public static final int DEFAULT_BORDER_LEFT = 0;

	public ShapeNodeImpl(DrawingImpl<?> drawingImpl, O drawable, ShapeGRBinding<O> grBinding, ContainerNodeImpl<?, ?> parentNode) {
		super(drawingImpl, drawable, grBinding, parentNode);
		startDrawableObserving();
		// graphics = new DianaShapeGraphicsImpl(this);
		// width = getGraphicalRepresentation().getMinimalWidth();
		// height = getGraphicalRepresentation().getMinimalHeight();
		relayoutNode();
		listenConstraintsValuesChange();
	}

	private void listenConstraintsValuesChange() {

		listenXConstraintsValueChange();
		listenYConstraintsValueChange();
		listenWidthConstraintsValueChange();
		listenHeightConstraintsValueChange();
	}

	private void listenXConstraintsValueChange() {

		stopListenXConstraintsValueChange();

		if (getGraphicalRepresentation().getXConstraints() != null && getGraphicalRepresentation().getXConstraints().isSet()
				&& getGraphicalRepresentation().getXConstraints().isValid()) {
			xConstraintsListener = new BindingValueChangeListener<Double>(getGraphicalRepresentation().getXConstraints(),
					getBindingEvaluationContext(), true) {
				@Override
				public void bindingValueChanged(Object source, Double newValue) {
					// System.out.println(" bindingValueChanged() detected for xConstraints=" +
					// getGraphicalRepresentation().getXConstraints()
					// + " with newValue=" + newValue + " source=" + source);
					if (newValue != null) {
						setX(newValue);
					}
				}

				@Override
				protected Double getDefaultValue() {
					return 0.0;
				}
			};
		}

	}

	private void listenYConstraintsValueChange() {

		stopListenYConstraintsValueChange();

		if (getGraphicalRepresentation().getYConstraints() != null && getGraphicalRepresentation().getYConstraints().isSet()
				&& getGraphicalRepresentation().getYConstraints().isValid()) {
			yConstraintsListener = new BindingValueChangeListener<Double>(getGraphicalRepresentation().getYConstraints(),
					getBindingEvaluationContext(), true) {
				@Override
				public void bindingValueChanged(Object source, Double newValue) {
					// System.out.println(" bindingValueChanged() detected for yConstraints=" +
					// getGraphicalRepresentation().getYConstraints()
					// + " with newValue=" + newValue + " source=" + source);
					if (newValue != null) {
						setY(newValue);
					}
				}

				@Override
				protected Double getDefaultValue() {
					return 0.0;
				}
			};
		}
	}

	private void listenWidthConstraintsValueChange() {

		stopListenWidthConstraintsValueChange();

		if (getGraphicalRepresentation().getWidthConstraints() != null && getGraphicalRepresentation().getWidthConstraints().isSet()
				&& getGraphicalRepresentation().getWidthConstraints().isValid()) {
			widthConstraintsListener = new BindingValueChangeListener<Double>(getGraphicalRepresentation().getWidthConstraints(),
					getBindingEvaluationContext(), true) {
				@Override
				public void bindingValueChanged(Object source, Double newValue) {
					// System.out.println(" bindingValueChanged() detected for widthConstraints="
					// + getGraphicalRepresentation().getWidthConstraints() + " with newValue=" + newValue + " source=" + source);
					if (newValue != null) {
						setWidth(newValue);
					}
				}

				@Override
				protected Double getDefaultValue() {
					return 0.0;
				}
			};
		}
	}

	private void listenHeightConstraintsValueChange() {

		stopListenHeightConstraintsValueChange();

		if (getGraphicalRepresentation().getHeightConstraints() != null && getGraphicalRepresentation().getHeightConstraints().isSet()
				&& getGraphicalRepresentation().getHeightConstraints().isValid()) {
			heightConstraintsListener = new BindingValueChangeListener<Double>(getGraphicalRepresentation().getHeightConstraints(),
					getBindingEvaluationContext(), true) {
				@Override
				public void bindingValueChanged(Object source, Double newValue) {
					// System.out.println(" bindingValueChanged() detected for heightConstraints="
					// + getGraphicalRepresentation().getHeightConstraints() + " with newValue=" + newValue + " source=" + source);
					if (newValue != null) {
						setHeight(newValue);
					}
				}

				@Override
				protected Double getDefaultValue() {
					return 0.0;
				}
			};
		}
	}

	private void stopListenConstraintsValueChange() {
		stopListenXConstraintsValueChange();
		stopListenYConstraintsValueChange();
		stopListenWidthConstraintsValueChange();
		stopListenHeightConstraintsValueChange();
	}

	private void stopListenXConstraintsValueChange() {
		if (xConstraintsListener != null) {
			xConstraintsListener.stopObserving();
			xConstraintsListener.delete();
			xConstraintsListener = null;
		}
	}

	private void stopListenYConstraintsValueChange() {
		if (yConstraintsListener != null) {
			yConstraintsListener.stopObserving();
			yConstraintsListener.delete();
			yConstraintsListener = null;
		}
	}

	private void stopListenWidthConstraintsValueChange() {
		if (widthConstraintsListener != null) {
			widthConstraintsListener.stopObserving();
			widthConstraintsListener.delete();
			widthConstraintsListener = null;
		}
	}

	private void stopListenHeightConstraintsValueChange() {
		if (heightConstraintsListener != null) {
			heightConstraintsListener.stopObserving();
			heightConstraintsListener.delete();
			heightConstraintsListener = null;
		}
	}

	@Override
	public boolean delete() {
		// Object o = getDrawable();
		if (!isDeleted()) {
			// System.out.println("ShapeNode deleted");
			stopListenConstraintsValueChange();
			stopDrawableObserving();
			super.delete();
			finalizeDeletion();
			// logger.info("Deleted ShapeNodeImpl for drawable " + o);
			return true;
		}
		return false;
	}

	@Override
	public ShapeImpl<?> getShape() {
		if (getShapeSpecification() == null) {
			logger.warning("INVESTIGATE: ShapeSpecification is Null for Node; falling back to rectangle");
			setShapeSpecification(this.getFactory().makeShape(ShapeType.RECTANGLE));
		}
		if (shape == null && getShapeSpecification() != null) {
			shape = (ShapeImpl<?>) getShapeSpecification().makeShape(this);
		}

		return shape;
	}

	@Override
	public DianaShape<?> getDianaShape() {
		if (getShape() != null) {
			return getShape().getShape();
		}
		return null;
	}

	@Override
	public DianaShape<?> getDianaShapeOutline() {
		if (getShape() != null) {
			return getShape().getOutline();
		}
		return null;
	}

	/**
	 * Return bounds (including border) relative to parent container
	 * 
	 * @return
	 */
	@Override
	public DianaRectangle getBounds() {
		return new DianaRectangle(getX(), getY(), getWidth(), getHeight());
	}

	/**
	 * Return view bounds (excluding border) relative to parent container
	 * 
	 * @param scale
	 * @return
	 */
	@Override
	public Rectangle getBounds(double scale) {
		Rectangle bounds = new Rectangle();

		bounds.x = (int) getX();
		bounds.y = (int) getY();
		bounds.width = (int) (getWidth() * scale);
		bounds.height = (int) (getHeight() * scale);

		return bounds;
	}

	/**
	 * Return view bounds (excluding border) relative to given container
	 * 
	 * @param scale
	 * @return
	 */
	@Override
	public Rectangle getBounds(DrawingTreeNode<?, ?> aContainer, double scale) {
		Rectangle bounds = getBounds(scale);
		bounds = DianaUtils.convertRectangle(getParentNode(), bounds, aContainer, scale);
		return bounds;
	}

	/**
	 * Return logical bounds (including border) relative to given container
	 * 
	 * @param scale
	 * @return
	 */
	@Override
	public Rectangle getViewBounds(DrawingTreeNode<?, ?> aContainer, double scale) {
		Rectangle bounds = getViewBounds(scale);
		if (getParentNode() == null) {
			logger.warning("Container is null for " + this + " valid=" + isValid());
		}
		if (aContainer == null) {
			logger.warning("Container is null for " + this + " valid=" + isValid());
		}
		bounds = DianaUtils.convertRectangle(getParentNode(), bounds, aContainer, scale);
		return bounds;
	}

	@Override
	public Rectangle getViewBounds(double scale) {
		Rectangle bounds = new Rectangle();

		bounds.x = getViewX(scale) - (int) (getBorderLeft() * scale);
		bounds.y = getViewY(scale) - (int) (getBorderTop() * scale);
		bounds.width = getViewWidth(scale);
		bounds.height = getViewHeight(scale);

		return bounds;
	}

	@Override
	public boolean isPointInsideShape(DianaPoint aPoint) {
		if (getShape() == null) {
			return false;
		}
		return getShape().isPointInsideShape(aPoint);
	}

	/**
	 * Computes and return required border on top, while taking under account:
	 * <ul>
	 * <li>the eventual shadow to paint</li>
	 * <li>the control areas to display</li>
	 * <li>all contained elements which may be located outside of original bounds</li>
	 * </ul>
	 */
	@Override
	public int getBorderTop() {
		int returned = DEFAULT_BORDER_TOP;

		// Handle control areas
		if (getControlAreas() != null) {
			for (ControlArea<?> ca : new ArrayList<>(getControlAreas())) {
				if (ca != null) {
					DianaArea a = ca.getArea();
					if (a instanceof DianaShape) {
						DianaRectangle bb = ((DianaShape<?>) a).getBoundingBox();
						if (bb.getY() < 0) {
							returned = Math.max(returned, (int) (-getHeight() * bb.getY() + 2));
						}
					}
				}
			}
		}

		// Handle child nodes
		if (getChildNodes() != null) {
			for (DrawingTreeNode<?, ?> childNode : getChildNodes()) {
				if (childNode instanceof ShapeNode) {
					ShapeNode<?> child = (ShapeNode<?>) childNode;
					if (child.getY() - child.getBorderTop() < -returned) {
						returned = -(int) child.getY() + child.getBorderTop();
					}
				}
			}
		}
		// System.out.println("top= " + returned);
		return returned;
	}

	/**
	 * Computes and return required border on left, while taking under account:
	 * <ul>
	 * <li>the eventual shadow to paint</li>
	 * <li>the control areas to display</li>
	 * <li>all contained elements which may be located outside of original bounds</li>
	 * </ul>
	 */
	@Override
	public int getBorderLeft() {
		int returned = DEFAULT_BORDER_LEFT;

		// Handle control areas
		if (getControlAreas() != null) {
			for (ControlArea<?> ca : new ArrayList<>(getControlAreas())) {
				if (ca != null) {
					DianaArea a = ca.getArea();
					if (a instanceof DianaShape) {
						DianaRectangle bb = ((DianaShape<?>) a).getBoundingBox();
						if (bb.getX() < 0) {
							returned = Math.max(returned, (int) (-getWidth() * bb.getX() + 2));
						}
					}
				}
			}
		}

		// Handle child nodes
		if (getChildNodes() != null) {
			for (DrawingTreeNode<?, ?> childNode : getChildNodes()) {
				if (childNode instanceof ShapeNode) {
					ShapeNode<?> child = (ShapeNode<?>) childNode;
					if (child.getX() - child.getBorderLeft() < -returned) {
						returned = -(int) child.getX() + child.getBorderLeft();
					}
				}
			}
		}
		// System.out.println("left= " + returned);
		return returned;
	}

	/**
	 * Computes and return required border on bottom, while taking under account:
	 * <ul>
	 * <li>the eventual shadow to paint</li>
	 * <li>the control areas to display</li>
	 * <li>all contained elements which may be located outside of original bounds</li>
	 * </ul>
	 */
	@Override
	public int getBorderBottom() {
		int returned = 0;

		// What about shadow ?
		if (getShadowStyle() != null) {
			returned = 15;
		}

		// Handle control areas
		if (getControlAreas() != null) {
			for (ControlArea<?> ca : new ArrayList<>(getControlAreas())) {
				if (ca != null) {
					DianaArea a = ca.getArea();
					if (a instanceof DianaShape) {
						DianaRectangle bb = ((DianaShape<?>) a).getBoundingBox();
						if (bb.getY() + bb.getHeight() > 1) {
							returned = Math.max(returned, (int) (getWidth() * (bb.getY() + bb.getHeight() - 1.0) + 2));
						}
					}
				}
			}
		}

		// Handle child nodes
		if (getChildNodes() != null) {
			for (DrawingTreeNode<?, ?> childNode : getChildNodes()) {
				if (childNode instanceof ShapeNode) {
					ShapeNode<?> child = (ShapeNode<?>) childNode;
					int requiredBorder = (int) (child.getY() + child.getHeight() + child.getBorderBottom() - getHeight());
					if (requiredBorder > returned) {
						returned = requiredBorder;
					}
				}
			}
		}
		return returned;
	}

	/**
	 * Computes and return required border on right, while taking under account:
	 * <ul>
	 * <li>the eventual shadow to paint</li>
	 * <li>the control areas to display</li>
	 * <li>all contained elements which may be located outside of original bounds</li>
	 * </ul>
	 */
	@Override
	public int getBorderRight() {
		int returned = 0;

		// What about shadow ?
		if (getShadowStyle() != null) {
			returned = 15;
		}

		// Handle control areas
		if (getControlAreas() != null) {
			for (ControlArea<?> ca : new ArrayList<>(getControlAreas())) {
				if (ca != null) {
					DianaArea a = ca.getArea();
					if (a instanceof DianaShape) {
						DianaRectangle bb = ((DianaShape<?>) a).getBoundingBox();
						if (bb.getX() + bb.getWidth() > 1) {
							returned = Math.max(returned, (int) (getWidth() * (bb.getX() + bb.getWidth() - 1.0) + 2));
						}
					}
				}
			}
		}

		// Handle child nodes
		if (getChildNodes() != null) {
			for (DrawingTreeNode<?, ?> childNode : getChildNodes()) {
				if (childNode instanceof ShapeNode) {
					ShapeNode<?> child = (ShapeNode<?>) childNode;
					int requiredBorder = (int) (child.getX() + child.getWidth() + child.getBorderRight() - getWidth());
					if (requiredBorder > returned) {
						returned = requiredBorder;
					}
				}
			}
		}
		return returned;
	}

	/**
	 * Note that we don't take border under account here, because computing are done relatively to local coordinates system.<br>
	 * The border management is a technical artefact which must be handled at view level (in the rendering engine)
	 */
	@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		AffineTransform returned = AffineTransform.getScaleInstance(getPropertyValue(ShapeGraphicalRepresentation.WIDTH),
				getPropertyValue(ShapeGraphicalRepresentation.HEIGHT));

		// returned.preConcatenate(AffineTransform.getTranslateInstance(getBorderLeft(), getBorderTop()));

		if (scale != 1) {
			returned.preConcatenate(AffineTransform.getScaleInstance(scale, scale));
		}

		/*DianaPoint p = new DianaPoint(0, 0);
		DianaPoint p2 = new DianaPoint();
		returned.transform(p, p2);
		
		System.out.println("Pour width=" + getPropertyValue(ShapeGraphicalRepresentation.WIDTH));
		System.out.println("Pour height=" + getPropertyValue(ShapeGraphicalRepresentation.HEIGHT));
		System.out.println("Cette AT permet de transformer " + p + " en " + p2);
		
		p = new DianaPoint(1.0, 1.0);
		p2 = new DianaPoint();
		returned.transform(p, p2);
		System.out.println("Cette AT permet de transformer " + p + " en " + p2);*/

		return returned;
	}

	/**
	 * Note that we don't take border under account here, because computing are done relatively to local coordinates system.<br>
	 * The border management is a technical artefact which must be handled at view level (in the rendering engine)
	 */
	@Override
	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale) {
		AffineTransform returned = new AffineTransform();
		if (scale != 1) {
			returned = AffineTransform.getScaleInstance(1 / scale, 1 / scale);
		}

		// returned.preConcatenate(AffineTransform.getTranslateInstance(-getBorderLeft(), -getBorderTop()));

		returned.concatenate(AffineTransform.getScaleInstance(1 / getWidth(), 1 / getHeight()));

		/*DianaPoint p = new DianaPoint(getBorderLeft(), getBorderTop());
		DianaPoint p2 = new DianaPoint();
		returned.transform(p, p2);
		
		System.out.println("Pour width=" + getPropertyValue(ShapeGraphicalRepresentation.WIDTH));
		System.out.println("Pour height=" + getPropertyValue(ShapeGraphicalRepresentation.HEIGHT));
		System.out.println("Cette AT permet de transformer " + p + " en " + p2);
		
		p = new DianaPoint(getPropertyValue(ShapeGraphicalRepresentation.WIDTH) + getBorderLeft(),
				getPropertyValue(ShapeGraphicalRepresentation.HEIGHT) + getBorderTop());
		p2 = new DianaPoint();
		returned.transform(p, p2);
		System.out.println("Cette AT permet de transformer " + p + " en " + p2);*/

		return returned;
	}

	@Override
	public int getViewX(double scale) {
		return (int) ((getX() /*- getBorderLeft()*/) * scale);
	}

	@Override
	public int getViewY(double scale) {
		return (int) ((getY() /*- getBorderTop()*/) * scale);
	}

	@Override
	public int getViewWidth(double scale) {
		return (int) ((getWidth() + getBorderLeft() + getBorderRight()) * scale) + 1;
	}

	@Override
	public int getViewHeight(double scale) {
		return (int) ((getHeight() + getBorderTop() + getBorderBottom()) * scale) + 1;
	}

	/**
	 * This method is called whenever it was detected that the value of a property declared as dynamic (specified by a {@link DataBinding}
	 * in {@link GRBinding}) has changed
	 * 
	 * @param parameter
	 * @param oldValue
	 * @param newValue
	 */
	@Override
	public <T> void fireDynamicPropertyChanged(GRProperty<T> parameter, T oldValue, T newValue) {
		super.fireDynamicPropertyChanged(parameter, oldValue, newValue);
		if (parameter == ShapeGraphicalRepresentation.X || parameter == ShapeGraphicalRepresentation.Y) {
			notifyObjectMoved();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (temporaryIgnoredObservables.contains(evt.getSource())) {
			// System.out.println("IGORE NOTIFICATION " + notification);
			return;
		}

		super.propertyChange(evt);

		// logger.info("Received for " + getDrawable() + " in ShapeNodeImpl: " + evt.getPropertyName() + " evt=" + evt);

		if (evt.getSource() == getGraphicalRepresentation()) {
			// Those notifications are forwarded by my graphical representation

			if (evt.getPropertyName() == GraphicalRepresentation.TEXT.getName()) {
				checkAndUpdateDimensionIfRequired();
			}
			else if (evt.getPropertyName() == GraphicalRepresentation.TEXT_STYLE.getName()) {
				checkAndUpdateDimensionIfRequired();
			}
			else if (evt.getPropertyName() == ShapeGraphicalRepresentation.ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT.getName()
					|| evt.getPropertyName() == ShapeGraphicalRepresentation.ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH.getName()
					|| evt.getPropertyName() == ShapeGraphicalRepresentation.ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT.getName()
					|| evt.getPropertyName() == ShapeGraphicalRepresentation.ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH.getName()) {
				checkAndUpdateDimensionIfRequired();
			}
			else if (evt.getPropertyName() == ShapeGraphicalRepresentation.X.getName()
					|| evt.getPropertyName() == ShapeGraphicalRepresentation.Y.getName()) {
				forward(evt);
				notifyObjectMoved(null);
			}
			else if (evt.getPropertyName() == ContainerGraphicalRepresentation.WIDTH.getName()
					|| evt.getPropertyName() == ContainerGraphicalRepresentation.HEIGHT.getName()
					|| evt.getPropertyName() == ShapeGraphicalRepresentation.MINIMAL_HEIGHT.getName()
					|| evt.getPropertyName() == ShapeGraphicalRepresentation.MINIMAL_WIDTH.getName()
					|| evt.getPropertyName() == ShapeGraphicalRepresentation.MAXIMAL_HEIGHT.getName()
					|| evt.getPropertyName() == ShapeGraphicalRepresentation.MAXIMAL_WIDTH.getName()) {
				checkAndUpdateDimensionIfRequired();
				// We forward then the event to the view
				forward(evt);
			}
			else if (evt.getPropertyName() == GraphicalRepresentation.HORIZONTAL_TEXT_ALIGNEMENT.getName()
					|| evt.getPropertyName() == GraphicalRepresentation.VERTICAL_TEXT_ALIGNEMENT.getName()) {
				checkAndUpdateDimensionIfRequired();
			}
			else if (evt.getPropertyName() == GraphicalRepresentation.ABSOLUTE_TEXT_X.getName()
					|| evt.getPropertyName() == GraphicalRepresentation.ABSOLUTE_TEXT_Y.getName()) {
				checkAndUpdateDimensionIfRequired();
			}
			else if (evt.getPropertyName() == ShapeGraphicalRepresentation.LOCATION_CONSTRAINTS.getName()
					|| evt.getPropertyName() == ShapeGraphicalRepresentation.LOCATION_CONSTRAINED_AREA.getName()
					|| evt.getPropertyName() == ShapeGraphicalRepresentation.DIMENSION_CONSTRAINT_STEP.getName()
					|| evt.getPropertyName() == ShapeGraphicalRepresentation.DIMENSION_CONSTRAINTS.getName()) {
				checkAndUpdateLocationIfRequired();
				getShape().updateControlPoints();
			}
			else if (evt.getPropertyName() == ShapeGraphicalRepresentation.ADAPT_BOUNDS_TO_CONTENTS.getName()) {
				extendBoundsToHostContents();
			}
			/*else if (evt.getPropertyName() == ShapeGraphicalRepresentation.BORDER.getName()) {
				forward(evt);
				notifyObjectMoved(null);
				checkAndUpdateDimensionIfRequired();
			}*/
			else if (evt.getPropertyName() == ShapeGraphicalRepresentation.SHAPE.getName()
					|| evt.getPropertyName() == ShapeGraphicalRepresentation.SHAPE_TYPE.getName()) {
				fireShapeSpecificationChanged();
			}
			else if (evt.getPropertyName().equals(ShapeGraphicalRepresentation.LAYOUT_MANAGER_IDENTIFIER_KEY)) {
				relayoutNode();
			}

			else if (evt.getPropertyName().equals(ShapeGraphicalRepresentation.X_CONSTRAINTS_KEY)) {
				listenXConstraintsValueChange();
			}
			else if (evt.getPropertyName().equals(ShapeGraphicalRepresentation.Y_CONSTRAINTS_KEY)) {
				listenYConstraintsValueChange();
			}
			else if (evt.getPropertyName().equals(ShapeGraphicalRepresentation.WIDTH_CONSTRAINTS_KEY)) {
				listenWidthConstraintsValueChange();
			}
			else if (evt.getPropertyName().equals(ShapeGraphicalRepresentation.HEIGHT_CONSTRAINTS_KEY)) {
				listenHeightConstraintsValueChange();
			}

			/*if (notif instanceof BindingChanged) {
				DataBinding<?> dataBinding = ((BindingChanged) notif).getBinding();
				if (dataBinding == getGraphicalRepresentation().getXConstraints() && dataBinding.isValid()) {
					updateXPosition();
				} else if (dataBinding == getGraphicalRepresentation().getYConstraints() && dataBinding.isValid()) {
					updateYPosition();
				} else if (dataBinding == getGraphicalRepresentation().getWidthConstraints() && dataBinding.isValid()) {
					updateWidthPosition();
				} else if (dataBinding == getGraphicalRepresentation().getHeightConstraints() && dataBinding.isValid()) {
					updateHeightPosition();
				}
			
			}*/

		}

		if (observeParentGRBecauseMyLocationReferToIt /*&& observable == getContainerGraphicalRepresentation()*/) {
			if (evt.getPropertyName().equals(ObjectWillMove.EVENT_NAME) || evt.getPropertyName().equals(ObjectWillResize.EVENT_NAME)
					|| evt.getPropertyName().equals(ObjectHasMoved.EVENT_NAME) || evt.getPropertyName().equals(ObjectHasResized.EVENT_NAME)
					|| evt.getPropertyName().equals(ObjectMove.PROPERTY_NAME) || evt.getPropertyName().equals(ObjectResized.PROPERTY_NAME)
					|| evt.getPropertyName().equals(ShapeChanged.EVENT_NAME)) {
				checkAndUpdateLocationIfRequired();
			}
		}

		if (evt.getSource() instanceof BackgroundStyle || evt.getPropertyName() == ShapeGraphicalRepresentation.BACKGROUND_STYLE_TYPE_KEY) {
			notifyAttributeChanged(ShapeGraphicalRepresentation.BACKGROUND, null, getBackgroundStyle());
		}
		if (evt.getSource() instanceof ForegroundStyle) {
			notifyAttributeChanged(ShapeGraphicalRepresentation.FOREGROUND, null, getForegroundStyle());
		}
		if (evt.getSource() instanceof ShadowStyle) {
			notifyAttributeChanged(ShapeGraphicalRepresentation.SHADOW_STYLE, null, getShadowStyle());
		}
	}

	private void fireShapeSpecificationChanged() {

		// logger.info("fireShapeSpecificationChanged()");
		if (shape != null && getShapeSpecification() != null) {
			getShapeSpecification().getPropertyChangeSupport().removePropertyChangeListener(shape);
			shape.delete();
			shape = null;
		}

		getShape().updateShape();
		notifyShapeChanged();
	}

	@Override
	public void extendParentBoundsToHostThisShape() {
		if (getParentNode() instanceof ShapeNode) {
			ShapeNode<?> parent = (ShapeNode<?>) getParentNode();
			parent.extendBoundsToHostContents();
		}
	}

	/**
	 * Check and eventually relocate and resize current graphical representation in order to all all contained shape graphical
	 * representations. Contained graphical representations may substantically be relocated.
	 */
	@Override
	public void extendBoundsToHostContents() {

		boolean needsResize = false;
		DianaDimension newDimension = new DianaDimension(getWidth(), getHeight());
		boolean needsRelocate = false;
		DianaPoint newPosition = new DianaPoint(getX(), getY());
		double deltaX = 0;
		double deltaY = 0;

		// First compute the delta to be applied (max of all required delta)
		for (DrawingTreeNode<?, ?> child : getChildNodes()) {
			if (child instanceof ShapeNode) {
				ShapeNode<?> gr = (ShapeNode<?>) child;
				if (gr.getX() < -deltaX) {
					deltaX = -gr.getX();
					needsRelocate = true;
				}
				if (gr.getY() < -deltaY) {
					deltaY = -gr.getY();
					needsRelocate = true;
				}
			}
		}

		// Relocate
		if (needsRelocate) {
			// System.out.println("Relocate with deltaX=" + deltaX + " deltaY=" + deltaY);
			newPosition.x = newPosition.x - deltaX;
			newPosition.y = newPosition.y - deltaY;
			setLocation(newPosition);
			needsResize = true;
			newDimension = new DianaDimension(getWidth() + deltaX, getHeight() + deltaY);
			for (DrawingTreeNode<?, ?> child : getChildNodes()) {
				if (child instanceof ShapeNode && child != this) {
					ShapeNode<?> c = (ShapeNode<?>) child;
					c.setLocation(new DianaPoint(c.getX() + deltaX, c.getY() + deltaY));
				}
			}
		}

		// First compute the resize to be applied
		for (DrawingTreeNode<?, ?> child : getChildNodes()) {
			if (child instanceof ShapeNode) {
				ShapeNode<?> gr = (ShapeNode<?>) child;
				if (gr.getX() + gr.getWidth() > getWidth()) {
					newDimension.width = gr.getX() + gr.getWidth();
					needsResize = true;
				}
				if (gr.getY() + gr.getHeight() > getHeight()) {
					newDimension.height = gr.getY() + gr.getHeight();
					needsResize = true;
				}
			}
		}

		if (needsResize) {
			System.out.println("Resize to " + newDimension);
			setSize(newDimension);
		}

		/*if (needsRelocate || needsResize) {
			for (GraphicalRepresentation child : getContainedGraphicalRepresentations()) {
				if (child instanceof ShapeGraphicalRepresentation) {
					ShapeGraphicalRepresentation c = (ShapeGraphicalRepresentation) child;
					DianaPoint oldLocation = new DianaPoint(c.getX() - deltaX, c.getY() - deltaY);
					c.notifyObjectMoved(oldLocation);
					c.notifyChange(Parameters.x, oldLocation.x, c.getX());
					c.notifyChange(Parameters.y, oldLocation.y, c.getY());
				}
			}
		}*/

	}

	// ********************************************
	// Location/size management
	// ********************************************

	@Override
	public double getX() {
		return getPropertyValue(ShapeGraphicalRepresentation.X);
	}

	@Override
	public final void setX(double aValue) {
		if (aValue != getX()) {
			DianaPoint newLocation = new DianaPoint(aValue, getY());
			updateLocation(newLocation);
		}
	}

	protected void setXNoNotification(double aValue) {
		setPropertyValue(ShapeGraphicalRepresentation.X, aValue);
	}

	@Override
	public double getY() {
		return getPropertyValue(ShapeGraphicalRepresentation.Y);
	}

	@Override
	public final void setY(double aValue) {
		if (aValue != getY()) {

			DianaPoint newLocation = new DianaPoint(getX(), aValue);
			updateLocation(newLocation);
		}
	}

	protected void setYNoNotification(double aValue) {
		setPropertyValue(ShapeGraphicalRepresentation.Y, aValue);
	}

	/**
	 * General method called to update location of a ShapeNode
	 * 
	 * @param requestedLocation
	 */
	private void updateLocation(DianaPoint requestedLocation) {

		try {

			// If no value supplied, just ignore
			if (requestedLocation == null) {
				return;
			}

			// If value is same, also ignore
			if (requestedLocation.equals(getLocation())) {
				return;
			}

			// Prelude of update, first select new location respecting contextual constraints
			DianaPoint newLocation = getConstrainedLocation(requestedLocation);

			// Now the newLocation respect required constraints, we might apply it
			DianaPoint oldLocation = getLocation();
			if (!newLocation.equals(oldLocation)) {
				double oldX = getX();
				double oldY = getY();
				if (isParentLayoutedAsContainer()) {
					setLocationForContainerLayout(newLocation);
				}
				else {
					setXNoNotification(newLocation.x);
					setYNoNotification(newLocation.y);
				}

				if (!isRelayouting && getLayoutManager() != null && getLayoutManager().supportAutolayout()
						&& !getLayoutManager().isLayoutInProgress()) {
					boolean performLayout;
					if (isMoving() || isResizing()) {
						// We are inside a drag operation
						performLayout = getLayoutManager().getDraggingMode().relayoutOnDrag();
					}
					else {
						performLayout = getLayoutManager().getDraggingMode().relayoutAfterDrag();
					}
					if (performLayout) {
						performLayout();
					}
				}

				notifyObjectMoved(oldLocation);
				notifyAttributeChanged(ShapeGraphicalRepresentation.X, oldX, getX());
				notifyAttributeChanged(ShapeGraphicalRepresentation.Y, oldY, getY());
				if (!isFullyContainedInContainer()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("setLocation() lead shape going outside it's parent view");
					}
					if (getParentNode() instanceof ShapeNodeImpl) {
						((ShapeNodeImpl<?>) getParentNode()).notifyObjectMoved(null);
						((ShapeNodeImpl<?>) getParentNode()).notifyObjectResized(null);
					}
				}

			}
		} catch (IllegalArgumentException e) {
			logger.warning("Unexpected exception " + e.getMessage());
		}
	}

	private void performLayout() {
		setRelayouting(true);

		getLayoutManager().attemptToPlaceNodeManually(this);
		if (getLayoutManager().isFullyLayouted()) {
			getLayoutManager().computeLayout();
		}
		getLayoutManager().doLayout(this, true);
		setRelayouting(false);
	}

	/**
	 * Flag indicating if we are about to relayout current node<br>
	 * This means that the relocation request was initiated from the layout manager
	 */
	private boolean isRelayouting = false;

	/**
	 * Return flag indicating if we are about to relayout current node<br>
	 * This means that the relocation request was initiated from the layout manager
	 */
	@Override
	public boolean isRelayouting() {
		return isRelayouting;
	}

	/**
	 * Sets flag indicating if we are about to relayout current node<br>
	 * This means that the relocation request was initiated from the layout manager
	 */
	@Override
	public void setRelayouting(boolean relayouting) {
		isRelayouting = relayouting;
	}

	/**
	 * Compute and return a constrained location, according to contextual constraints
	 * 
	 * @param requestedLocation
	 * @return a new location respecting all contextual constraints
	 */
	private DianaPoint getConstrainedLocation(DianaPoint requestedLocation) {

		if (isParentLayoutedAsContainer()) {
			return requestedLocation;
		}

		if (getGraphicalRepresentation() == null) {
			return requestedLocation;
		}

		if (!getGraphicalRepresentation().getAllowToLeaveBounds()) {
			requestedLocation = requestedLocation.clone();
			if (requestedLocation.x < 0) {
				requestedLocation.setX(0);
			}
			double maxX = getParentNode().getWidth();
			if (maxX > 0 && requestedLocation.x > maxX - getWidth()) {
				// logger.info("Relocate x from "+x+" to "+(maxX-getWidth())+" maxX="+maxX+" width="+getWidth());
				requestedLocation.setX(maxX - getWidth());
			}
			if (requestedLocation.y < 0) {
				requestedLocation.setY(0);
			}
			double maxY = getParentNode().getHeight();
			if (maxY > 0 && requestedLocation.y > maxY - getHeight()) {
				// logger.info("Relocate x from "+x+" to "+(maxX-getWidth())+" maxX="+maxX+" width="+getWidth());
				requestedLocation.setY(maxY - getHeight());
			}
		}

		if (getGraphicalRepresentation().getLocationConstraints() == LocationConstraints.FREELY_MOVABLE) {
			return requestedLocation.clone();
		}
		if (getGraphicalRepresentation().getLocationConstraints() == LocationConstraints.CONTAINED_IN_SHAPE) {
			DrawingTreeNode<?, ?> parent = getParentNode();
			if (parent instanceof ShapeNode) {
				ShapeNode<?> container = (ShapeNode<?>) parent;
				DianaPoint center = new DianaPoint(container.getWidth() / 2, container.getHeight() / 2);
				double authorizedRatio = getMoveAuthorizedRatio(requestedLocation, center);
				return new DianaPoint(center.x + (requestedLocation.x - center.x) * authorizedRatio,
						center.y + (requestedLocation.y - center.y) * authorizedRatio);
			}
		}
		if (getGraphicalRepresentation().getLocationConstraints() == LocationConstraints.AREA_CONSTRAINED) {
			if (getGraphicalRepresentation().getLocationConstrainedArea() == null) {
				// logger.warning("No location constrained are defined");
				return requestedLocation;
			}
			else {
				return getGraphicalRepresentation().getLocationConstrainedArea().getNearestPoint(requestedLocation);
			}
		}
		return requestedLocation;
	}

	@Override
	public DianaPoint getLocation() {
		return new DianaPoint(getX(), getY());
	}

	@Override
	public void setLocation(DianaPoint newLocation) {
		updateLocation(newLocation);
	}

	@Override
	public DianaPoint getLocationInDrawing() {
		return DianaUtils.convertNormalizedPoint(this, new DianaPoint(0, 0), getDrawing().getRoot());
	}

	/* Unused
	private void setLocationNoCheckNorNotification(DianaPoint newLocation) {
		setXNoNotification(newLocation.x);
		setYNoNotification(newLocation.y);
	}
	*/

	private void setLocationForContainerLayout(DianaPoint newLocation) {
		if (getParentNode() instanceof ShapeNodeImpl) {
			ShapeNodeImpl<?> container = (ShapeNodeImpl<?>) getParentNode();
			container.updateRequiredBoundsForChildGRLocation(this, newLocation);
		}
	}

	/**
	 * Calling this method forces Diana to check (and eventually update) location of current graphical representation according defined
	 * location constraints
	 */
	protected void checkAndUpdateLocationIfRequired() {
		try {
			setLocation(getLocation());
		} catch (IllegalArgumentException e) {
			// May happen if object hierarchy inconsistent (or not consistent yet)
			logger.fine("Ignore IllegalArgumentException: " + e.getMessage());
		}
		if (!observeParentGRBecauseMyLocationReferToIt) {
			if (getGraphicalRepresentation().getLocationConstraints() == LocationConstraints.AREA_CONSTRAINED && getParentNode() != null) {
				getParentNode().getPropertyChangeSupport().addPropertyChangeListener(this);
				observeParentGRBecauseMyLocationReferToIt = true;
				// logger.info("Start observe my father");
			}
		}
	}

	@Override
	public boolean isFullyContainedInContainer() {
		if (getParentNode() == null || getDrawing() == null) {
			return true;
		}
		boolean isFullyContained = true;
		DianaRectangle containerViewBounds = new DianaRectangle(0, 0, getParentNode().getWidth(), getParentNode().getHeight(), Filling.FILLED);
		for (ControlPoint cp : getShape().getControlPoints()) {
			if (cp != null) {
				Point cpInContainerView = convertLocalNormalizedPointToRemoteViewCoordinates(cp.getPoint(), getParentNode(), 1);
				DianaPoint preciseCPInContainerView = new DianaPoint(cpInContainerView.x, cpInContainerView.y);
				if (!containerViewBounds.containsPoint(preciseCPInContainerView)) {
					// System.out.println("Going outside: point=" + preciseCPInContainerView + " bounds=" + containerViewBounds);
					isFullyContained = false;
				}
			}
		}
		return isFullyContained;
	}

	@Override
	public boolean isContainedInSelection(Rectangle drawingViewSelection, double scale) {
		if (getShape() == null) {
			return false;
		}
		DianaRectangle drawingViewBounds = new DianaRectangle(drawingViewSelection.getX(), drawingViewSelection.getY(),
				drawingViewSelection.getWidth(), drawingViewSelection.getHeight(), Filling.FILLED);
		boolean isFullyContained = true;
		for (ControlPoint cp : getShape().getControlPoints()) {
			if (cp != null) {
				Point cpInContainerView = convertLocalNormalizedPointToRemoteViewCoordinates(cp.getPoint(), getParentNode(), scale);
				DianaPoint preciseCPInContainerView = new DianaPoint(cpInContainerView.x, cpInContainerView.y);
				if (!drawingViewBounds.containsPoint(preciseCPInContainerView)) {
					// System.out.println("Going outside: point="+preciseCPInContainerView+" bounds="+containerViewBounds);
					isFullyContained = false;
				}
			}
		}
		return isFullyContained;
	}

	@Override
	public double getMoveAuthorizedRatio(DianaPoint desiredLocation, DianaPoint initialLocation) {
		if (isParentLayoutedAsContainer()) {
			// This object is contained in a ShapeSpecification acting as container: all locations are valid thus,
			// container will adapt
			return 1;
		}

		double returnedAuthorizedRatio = 1;
		DianaRectangle containerViewArea = new DianaRectangle(0, 0, getParentNode().getViewWidth(1), getParentNode().getViewHeight(1),
				Filling.FILLED);
		DianaRectangle containerViewBounds = new DianaRectangle(0, 0, getParentNode().getViewWidth(1), getParentNode().getViewHeight(1),
				Filling.NOT_FILLED);

		for (ControlPoint cp : getShape().getControlPoints()) {
			if (cp != null) {
				Point currentCPInContainerView = convertLocalNormalizedPointToRemoteViewCoordinates(cp.getPoint(), getParentNode(), 1);
				DianaPoint initialCPInContainerView = new DianaPoint((int) (currentCPInContainerView.x + initialLocation.x - getX()),
						(int) (currentCPInContainerView.y + initialLocation.y - getY()));
				DianaPoint desiredCPInContainerView = new DianaPoint((int) (currentCPInContainerView.x + desiredLocation.x - getX()),
						(int) (currentCPInContainerView.y + desiredLocation.y - getY()));
				if (!containerViewArea.containsPoint(initialCPInContainerView)) {
					logger.warning("getMoveAuthorizedRatio() called for a shape whose initial location wasn't in container shape");
					return 1;
				}
				if (!containerViewArea.containsPoint(desiredCPInContainerView)) {
					// We are now sure that desired move will make the shape
					// go outside parent bounds
					DianaSegment segment = new DianaSegment(initialCPInContainerView, desiredCPInContainerView);
					DianaArea intersection = DianaIntersectionArea.makeIntersection(segment, containerViewBounds);
					if (intersection instanceof DianaPoint) {
						// Intersection is normally a point
						DianaPoint intersect = (DianaPoint) intersection;
						double currentRatio = 1;
						if (Math.abs(desiredCPInContainerView.x - initialCPInContainerView.x) > DianaGeometricObject.EPSILON) {
							currentRatio = (intersect.x - initialCPInContainerView.x)
									/ (desiredCPInContainerView.x - initialCPInContainerView.x) - DianaGeometricObject.EPSILON;
						}
						else if (Math.abs(desiredCPInContainerView.y - initialCPInContainerView.y) > DianaGeometricObject.EPSILON) {
							currentRatio = (intersect.y - initialCPInContainerView.y)
									/ (desiredCPInContainerView.y - initialCPInContainerView.y) - DianaGeometricObject.EPSILON;
						}
						else {
							logger.warning(
									"Unexpected unsignifiant move from " + initialCPInContainerView + " to " + desiredCPInContainerView);
						}
						if (currentRatio < returnedAuthorizedRatio) {
							returnedAuthorizedRatio = currentRatio;
						}
					}
					else {
						logger.warning("Unexpected intersection: " + intersection);
					}
				}
			}
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getMoveAuthorizedRatio() initial=" + initialLocation + " desired=" + desiredLocation + " return "
					+ returnedAuthorizedRatio);
		}
		if (returnedAuthorizedRatio < 0) {
			returnedAuthorizedRatio = 0;
		}
		return returnedAuthorizedRatio;
	}

	@Override
	public void finalizeConstraints() {
		if (getGraphicalRepresentation() != null) {
			if (getGraphicalRepresentation().getXConstraints() != null && getGraphicalRepresentation().getXConstraints().isValid()) {
				getGraphicalRepresentation().getXConstraints().decode();
				try {
					setX((Double) TypeUtils.castTo(
							getGraphicalRepresentation().getXConstraints().getBindingValue(getBindingEvaluationContext()), Double.class));
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				getGraphicalRepresentation().setLocationConstraints(LocationConstraints.UNMOVABLE);
			}
			if (getGraphicalRepresentation().getYConstraints() != null && getGraphicalRepresentation().getYConstraints().isValid()) {
				getGraphicalRepresentation().getYConstraints().decode();
				try {
					setY((Double) TypeUtils.castTo(
							getGraphicalRepresentation().getYConstraints().getBindingValue(getBindingEvaluationContext()), Double.class));
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				getGraphicalRepresentation().setLocationConstraints(LocationConstraints.UNMOVABLE);
			}
			if (getGraphicalRepresentation().getWidthConstraints() != null
					&& getGraphicalRepresentation().getWidthConstraints().isValid()) {
				getGraphicalRepresentation().getWidthConstraints().decode();
				try {
					Object bdv = getGraphicalRepresentation().getWidthConstraints().getBindingValue(getBindingEvaluationContext());
					if (bdv != null) {
						setWidth((Double) TypeUtils.castTo(bdv, Double.class));
					}
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				getGraphicalRepresentation().setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
			}
			if (getGraphicalRepresentation().getHeightConstraints() != null
					&& getGraphicalRepresentation().getHeightConstraints().isValid()) {
				getGraphicalRepresentation().getHeightConstraints().decode();
				try {
					Object bdv = getGraphicalRepresentation().getHeightConstraints().getBindingValue(getBindingEvaluationContext());
					if (bdv != null) {
						setHeight((Double) TypeUtils.castTo(bdv, Double.class));
					}
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				getGraphicalRepresentation().setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
			}
		}
	}

	protected void computeNewConstraint(ConstraintDependency dependancy) {
		if (dependancy.requiringParameter == ShapeGraphicalRepresentation.X_CONSTRAINTS
				&& getGraphicalRepresentation().getXConstraints() != null && getGraphicalRepresentation().getXConstraints().isValid()) {
			updateXPosition();
		}
		else if (dependancy.requiringParameter == ShapeGraphicalRepresentation.Y_CONSTRAINTS
				&& getGraphicalRepresentation().getYConstraints() != null && getGraphicalRepresentation().getYConstraints().isValid()) {
			updateYPosition();
		}
		else if (dependancy.requiringParameter == ShapeGraphicalRepresentation.WIDTH_CONSTRAINTS
				&& getGraphicalRepresentation().getWidthConstraints() != null
				&& getGraphicalRepresentation().getWidthConstraints().isValid()) {
			updateWidthPosition();
		}
		else if (dependancy.requiringParameter == ShapeGraphicalRepresentation.HEIGHT_CONSTRAINTS
				&& getGraphicalRepresentation().getHeightConstraints() != null
				&& getGraphicalRepresentation().getHeightConstraints().isValid()) {
			updateHeightPosition();
		}
	}

	public void updateConstraints() {
		// System.out.println("updateConstraints() called, valid=" + xConstraints.isValid() + "," + yConstraints.isValid() + ","
		// + widthConstraints.isValid() + "," + heightConstraints.isValid());
		logger.fine("Called updateConstraints(), drawable=" + getDrawable() + " class=" + getClass());
		if (getGraphicalRepresentation().getXConstraints() != null && getGraphicalRepresentation().getXConstraints().isValid()) {
			// System.out.println("x was " + getX() + " constraint=" + xConstraints);
			updateXPosition();
			// System.out.println("x is now " + getX());
		}
		if (getGraphicalRepresentation().getYConstraints() != null && getGraphicalRepresentation().getYConstraints().isValid()) {
			// System.out.println("y was " + getY() + " constraint=" + yConstraints);
			updateYPosition();
			// System.out.println("y is now " + getY());
		}
		if (getGraphicalRepresentation().getWidthConstraints() != null && getGraphicalRepresentation().getWidthConstraints().isValid()) {
			// System.out.println("width was " + getWidth() + " constraint=" + widthConstraints);
			updateWidthPosition();
			// System.out.println("width is now " + getWidth());
		}
		if (getGraphicalRepresentation().getHeightConstraints() != null && getGraphicalRepresentation().getHeightConstraints().isValid()) {
			// System.out.println("height was " + getHeight() + " constraint=" + heightConstraints);
			updateHeightPosition();
			// System.out.println("height is now " + getHeight());
		}

	}

	private void updateXPosition() {
		try {
			Double n = getGraphicalRepresentation().getXConstraints().getBindingValue(getBindingEvaluationContext());
			if (n != null) {
				// System.out.println("New value for x is now: " + newValue);
				setX(n.doubleValue());
			}
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void updateYPosition() {
		try {
			Double n = getGraphicalRepresentation().getYConstraints().getBindingValue(getBindingEvaluationContext());
			if (n != null) {
				// System.out.println("New value for y is now: " + newValue);
				setY(n.doubleValue());
			}
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void updateWidthPosition() {
		try {
			Double n = getGraphicalRepresentation().getWidthConstraints().getBindingValue(getBindingEvaluationContext());
			if (n != null) {
				// System.out.println("New value for width is now: " + newValue);
				setWidth(n.doubleValue());
			}
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void updateHeightPosition() {
		try {
			Double n = getGraphicalRepresentation().getHeightConstraints().getBindingValue(getBindingEvaluationContext());
			if (n != null) {
				// System.out.println("New value for height is now: " + newValue);
				setHeight(n.doubleValue());
			}
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ShapeDecorationPainter getDecorationPainter() {
		return decorationPainter;
	}

	@Override
	public void setDecorationPainter(ShapeDecorationPainter aPainter) {
		decorationPainter = aPainter;
	}

	@Override
	public ShapePainter getShapePainter() {
		return shapePainter;
	}

	@Override
	public void setShapePainter(ShapePainter aPainter) {
		shapePainter = aPainter;
	}

	@Override
	public Point getLabelLocation(double scale) {
		Point point;
		if (getGraphicalRepresentation().getIsFloatingLabel()) {
			point = new Point((int) (getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X) * scale + getViewX(scale)),
					(int) (getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y) * scale + getViewY(scale)));
		}
		else {
			DianaPoint relativePosition = new DianaPoint(getPropertyValue(ShapeGraphicalRepresentation.RELATIVE_TEXT_X),
					getPropertyValue(ShapeGraphicalRepresentation.RELATIVE_TEXT_Y));
			point = convertLocalNormalizedPointToRemoteViewCoordinates(relativePosition, getParentNode(), scale);
		}
		Dimension d = getLabelDimension(scale);
		if (getGraphicalRepresentation().getHorizontalTextAlignment() != null) {
			switch (getGraphicalRepresentation().getHorizontalTextAlignment()) {
				case CENTER:
					point.x -= d.width / 2;
					break;
				case LEFT:
					point.x = (int) (point.x - getWidth() / 2);
					break;
				case RIGHT:
					point.x = (int) (point.x + getWidth() / 2) - d.width;
					break;

			}
		}
		if (getGraphicalRepresentation().getVerticalTextAlignment() != null) {
			switch (getGraphicalRepresentation().getVerticalTextAlignment()) {
				case BOTTOM:

					point.y = (int) (point.y + getHeight() / 2) - d.height;
					// point.y -= d.height;
					break;
				case MIDDLE:
					point.y -= d.height / 2;
					break;
				case TOP:
					point.y = (int) (point.y - getHeight() / 2);
					break;
			}
		}

		// We have here to translate result to take borders into account
		if (getParentNode() instanceof ShapeNode) {
			point.x += ((ShapeNode<?>) getParentNode()).getBorderLeft() * scale /*- (int) (getBorderLeft() * scale)*/;
			point.y += ((ShapeNode<?>) getParentNode()).getBorderTop() * scale /*- (int) (getBorderTop() * scale)*/;
		}
		return point;
	}

	@Override
	public void setLabelLocation(Point point, double scale) {

		// First take the borders under account
		if (getParentNode() instanceof ShapeNode) {
			point.x -= ((ShapeNode<?>) getParentNode()).getBorderLeft() * scale /*- (int) (getBorderLeft() * scale)*/;
			point.y -= ((ShapeNode<?>) getParentNode()).getBorderTop() * scale /*- (int) (getBorderTop() * scale)*/;
		}
		// point.x -= (DianaUtils.getCumulativeLeftBorders(getParentNode()) * scale /*- (int) (getBorderLeft() * scale)*/);
		// point.y -= (DianaUtils.getCumulativeTopBorders(getParentNode()) * scale /*- (int) (getBorderTop() * scale)*/);

		if (getGraphicalRepresentation().getIsFloatingLabel()) {
			Double oldAbsoluteTextX = getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X);
			Double oldAbsoluteTextY = getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y);
			Dimension d = getLabelDimension(scale);
			switch (getGraphicalRepresentation().getHorizontalTextAlignment()) {
				case CENTER:
					point.x += d.width / 2;
					break;
				case LEFT:
					break;
				case RIGHT:
					point.x += d.width;
					break;

			}
			switch (getGraphicalRepresentation().getVerticalTextAlignment()) {
				case BOTTOM:
					point.y += d.height;
					break;
				case MIDDLE:
					point.y += d.height / 2;
					break;
				case TOP:
					break;
			}
			DianaPoint p = new DianaPoint((point.x - getViewX(scale)) / scale, (point.y - getViewY(scale)) / scale);
			setPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X, p.x);
			setPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y, p.y);
			notifyAttributeChanged(GraphicalRepresentation.ABSOLUTE_TEXT_X, oldAbsoluteTextX,
					getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X));
			notifyAttributeChanged(GraphicalRepresentation.ABSOLUTE_TEXT_Y, oldAbsoluteTextY,
					getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y));
		}
	}

	@Override
	public int getAvailableLabelWidth(double scale) {
		if (getGraphicalRepresentation().getLineWrap()) {
			double rpx = getGraphicalRepresentation().getRelativeTextX();
			switch (getGraphicalRepresentation().getHorizontalTextAlignment()) {
				case RIGHT:
					if (GeomUtils.doubleEquals(rpx, 0.0)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Impossible to handle RIGHT alignement with relative x position set to 0!");
						}
					}
					else {
						return (int) (getWidth() * rpx * scale);
					}
				case CENTER:
					if (GeomUtils.doubleEquals(rpx, 0.0)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Impossible to handle CENTER alignement with relative x position set to 0");
						}
					}
					else if (GeomUtils.doubleEquals(rpx, 1.0)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Impossible to handle CENTER alignement with relative x position set to 1");
						}
					}
					else {
						if (rpx > 0.5) {
							return (int) (getWidth() * 2 * (1 - rpx) * scale);
						}
						else {
							return (int) (getWidth() * 2 * rpx * scale);
						}
					}
					break;
				case LEFT:
					if (GeomUtils.doubleEquals(rpx, 1.0)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Impossible to handle LEFT alignement with relative x position set to 1");
						}
					}
					else {
						return (int) (getWidth() * (1 - rpx) * scale);
					}
					break;
			}
		}
		return super.getAvailableLabelWidth(scale);
	}

	@Override
	public void addChild(DrawingTreeNode<?, ?> aChildNode) {
		super.addChild(aChildNode);
		if (getGraphicalRepresentation().getAdaptBoundsToContents()) {
			extendBoundsToHostContents();
		}
	}

	@Override
	public void notifyObjectMoved() {
		boolean mustNotify = !isMoving();
		if (mustNotify) {
			notifyObjectWillMove();
		}
		notifyObjectMoved(null);
		if (mustNotify) {
			notifyObjectHasMoved();
		}
	}

	@Override
	public void notifyObjectMoved(DianaPoint oldLocation) {

		if (getLayoutManager() != null) {
			getLayoutManager().shapeMoved(oldLocation, getLocation());
		}

		notifyObservers(new ObjectMove(oldLocation, getLocation()));
	}

	@Override
	public void notifyObjectWillMove() {
		isMoving = true;
		notifyObservers(new ObjectWillMove());
	}

	@Override
	public void notifyObjectHasMoved() {
		isMoving = false;

		if (getLayoutManager() != null) {
			if (getLayoutManager().getDraggingMode().relayoutAfterDrag()) {
				performLayout();
			}
		}

		notifyObservers(new ObjectHasMoved());
	}

	@Override
	public boolean isMoving() {
		return isMoving;
	}

	/**
	 * Notify that the object just resized
	 */
	@Override
	public void notifyObjectResized(DianaDimension oldSize) {
		getShape().notifyObjectResized();
		super.notifyObjectResized(oldSize);
	}

	@Override
	public void notifyObjectHasResized() {
		super.notifyObjectHasResized();

		if (getLayoutManager() != null) {
			if (getLayoutManager().getDraggingMode().relayoutAfterDrag()) {
				performLayout();
			}
		}

	}

	@Override
	public void notifyShapeChanged() {
		notifyObservers(new ShapeChanged());
	}

	@Override
	public void notifyShapeNeedsToBeRedrawn() {
		notifyObservers(new ShapeNeedsToBeRedrawn());
	}

	private List<? extends ControlArea<?>> controlAreas = null;

	@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		if (controlAreas == null) {

			controlAreas = super.getControlAreas();

			List<ControlPoint> shapeControlAreas = getShape().getControlAreas();

			if (shapeControlAreas != null && shapeControlAreas.size() > 0) {
				if (controlAreas == null) {
					controlAreas = shapeControlAreas;
				}
				else if (controlAreas instanceof ConcatenedList) {
					((ConcatenedList<ControlArea<?>>) controlAreas).addElementList(shapeControlAreas);
				}
				else {
					controlAreas = new ConcatenedList<>(shapeControlAreas, controlAreas);
				}
			}

		}
		return controlAreas;
	}

	/**
	 * Returns the area on which the given connector can start. The area is expressed in this normalized coordinates
	 * 
	 * @param connectorGR
	 *            the connector asking where to start
	 * @return the area on which the given connector can start
	 */
	@Override
	public DianaArea getAllowedStartAreaForConnector(ConnectorNode<?> connector) {
		return getShape().getOutline();
	}

	/**
	 * Returns the area on which the given connector can end. The area is expressed in this normalized coordinates
	 * 
	 * @param connectorGR
	 *            the connector asking where to end
	 * @return the area on which the given connector can end
	 */
	@Override
	public DianaArea getAllowedEndAreaForConnector(ConnectorNode<?> connector) {
		return getShape().getOutline();
	}

	/**
	 * Returns the area on which the given connector can start. The area is expressed in this normalized coordinates
	 * 
	 * @param connectorGR
	 *            the connector asking where to start
	 * @return the area on which the given connector can start
	 */
	@Override
	public DianaArea getAllowedStartAreaForConnectorForDirection(ConnectorNode<?> connector, DianaArea area,
			SimplifiedCardinalDirection direction) {
		return area;
	}

	/**
	 * Returns the area on which the given connector can end. The area is expressed in this normalized coordinates
	 * 
	 * @param connectorGR
	 *            the connector asking where to end
	 * @return the area on which the given connector can end
	 */
	@Override
	public DianaArea getAllowedEndAreaForConnectorForDirection(ConnectorNode<?> connector, DianaArea area,
			SimplifiedCardinalDirection direction) {
		return area;
	}

	@Override
	public String toString() {

		/*	if (isDeleted()) {
				return "[" + Integer.toHexString(hashCode()) + "]Shape-" + getIndex() + " [DELETED] ";
			}
			return "[" + Integer.toHexString(hashCode()) + "]Shape-" + getIndex() + "[" + getX() + ";" + getY() + "][" + getWidth() + "x"
					+ getHeight() + "][" + getDianaShape() + "]:" + getDrawable();*/
		return "ShapeNodeImpl[" + getText() + "/" + Integer.toHexString(hashCode()) + "/" + getDrawable() + "]";
	}

	@Override
	public boolean hasContainedLabel() {
		return hasText() && !getGraphicalRepresentation().getIsFloatingLabel();
	}

	@Override
	public boolean hasFloatingLabel() {
		return hasText() && getGraphicalRepresentation().getIsFloatingLabel();
	}

	@Override
	public DianaDimension getRequiredLabelSize() {
		Dimension normalizedLabelSize = getNormalizedLabelSize();
		int labelWidth = normalizedLabelSize.width;
		int labelHeight = normalizedLabelSize.height;
		double rh = 0, rw = 0;
		DianaPoint rp = new DianaPoint(getGraphicalRepresentation().getRelativeTextX(), getGraphicalRepresentation().getRelativeTextY());
		switch (getGraphicalRepresentation().getVerticalTextAlignment()) {
			case BOTTOM:
				if (GeomUtils.doubleEquals(rp.y, 0.0)) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Impossible to handle BOTTOM alignement with relative y position set to 0!");
					}
				}
				else {
					rh = labelHeight / rp.y;
				}
				break;
			case MIDDLE:
				if (GeomUtils.doubleEquals(rp.y, 0.0)) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Impossible to handle MIDDLE alignement with relative y position set to 0");
					}
				}
				else if (GeomUtils.doubleEquals(rp.y, 1.0)) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Impossible to handle MIDDLE alignement with relative y position set to 1");
					}
				}
				else {
					if (rp.y > 0.5) {
						rh = labelHeight / (2 * (1 - rp.y));
					}
					else {
						rh = labelHeight / (2 * rp.y);
					}
				}
				break;
			case TOP:
				if (GeomUtils.doubleEquals(rp.x, 1.0)) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Impossible to handle TOP alignement with relative y position set to 1!");
					}
				}
				else {
					rh = labelHeight / (1 - rp.y);
				}
				break;

		}

		switch (getGraphicalRepresentation().getHorizontalTextAlignment()) {
			case RIGHT:
				if (GeomUtils.doubleEquals(rp.x, 0.0)) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Impossible to handle RIGHT alignement with relative x position set to 0!");
					}
				}
				else {
					rw = labelWidth / rp.x;
				}
			case CENTER:
				if (GeomUtils.doubleEquals(rp.x, 0.0)) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Impossible to handle CENTER alignement with relative x position set to 0");
					}
				}
				else if (GeomUtils.doubleEquals(rp.x, 1.0)) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Impossible to handle CENTER alignement with relative x position set to 1");
					}
				}
				else {
					if (rp.x > 0.5) {
						rw = labelWidth / (2 * (1 - rp.x));
					}
					else {
						rw = labelWidth / (2 * rp.x);
					}
				}
				break;
			case LEFT:
				if (GeomUtils.doubleEquals(rp.x, 1.0)) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Impossible to handle LEFT alignement with relative x position set to 1!");
					}
				}
				else {
					rw = labelWidth / (1 - rp.x);
				}
				break;
		}

		return new DianaDimension(rw, rh);
	}

	protected void updateRequiredBoundsForChildGRLocation(ShapeNode<?> child, DianaPoint newChildLocation) {
		DianaRectangle oldBounds = getBounds();
		DianaRectangle newBounds = getRequiredBoundsForChildGRLocation(child, newChildLocation);
		// System.out.println("oldBounds= "+oldBounds+" newBounds="+newBounds);
		double deltaX = -newBounds.x + oldBounds.x;
		double deltaY = -newBounds.y + oldBounds.y;
		AffineTransform translation = AffineTransform.getTranslateInstance(deltaX, deltaY);
		for (DrawingTreeNode<?, ?> childNode : getChildNodes()) {
			if (childNode instanceof ShapeNode) {
				ShapeNodeImpl<?> shapeNode = (ShapeNodeImpl<?>) childNode;
				if (shapeNode == child) {
					DianaPoint newPoint = newChildLocation.transform(translation);
					shapeNode.setXNoNotification(newPoint.x);
					shapeNode.setYNoNotification(newPoint.y);
				}
				else {
					DianaPoint newPoint = shapeNode.getLocation().transform(translation);
					shapeNode.setXNoNotification(newPoint.x);
					shapeNode.setYNoNotification(newPoint.y);
				}
				shapeNode.notifyObjectMoved();
			}
		}
		setLocation(new DianaPoint(newBounds.x, newBounds.y));
		setSize(new DianaDimension(newBounds.width, newBounds.height));
	}

	protected DianaRectangle getRequiredBoundsForChildGRLocation(DrawingTreeNode<?, ?> child, DianaPoint newChildLocation) {
		DianaRectangle requiredBounds = null;
		for (DrawingTreeNode<?, ?> gr : getChildNodes()) {
			if (gr instanceof ShapeNode) {
				ShapeNode<?> shapeGR = (ShapeNode<?>) gr;
				DianaRectangle bounds = shapeGR.getBounds();
				if (shapeGR == child) {
					bounds.x = newChildLocation.x;
					bounds.y = newChildLocation.y;
				}
				if (shapeGR.hasText()) {
					Rectangle labelBounds = shapeGR.getNormalizedLabelBounds(); // getLabelBounds((new JLabel()), 1.0);
					DianaRectangle labelBounds2 = new DianaRectangle(labelBounds.x, labelBounds.y, labelBounds.width, labelBounds.height);
					bounds = bounds.rectangleUnion(labelBounds2);
				}

				if (requiredBounds == null) {
					requiredBounds = bounds;
				}
				else {
					requiredBounds = requiredBounds.rectangleUnion(bounds);
				}
			}
		}
		if (requiredBounds == null) {
			requiredBounds = new DianaRectangle(getX(), getY(), getGraphicalRepresentation().getMinimalWidth(),
					getGraphicalRepresentation().getMinimalHeight());
		}
		else {
			requiredBounds.x = requiredBounds.x + getX();
			requiredBounds.y = requiredBounds.y + getY();
			if (requiredBounds.width < getGraphicalRepresentation().getMinimalWidth()) {
				requiredBounds.x = requiredBounds.x - (int) ((getGraphicalRepresentation().getMinimalWidth() - requiredBounds.width) / 2.0);
				requiredBounds.width = getGraphicalRepresentation().getMinimalWidth();
			}
			if (requiredBounds.height < getGraphicalRepresentation().getMinimalHeight()) {
				requiredBounds.y = requiredBounds.y
						- (int) ((getGraphicalRepresentation().getMinimalHeight() - requiredBounds.height) / 2.0);
				requiredBounds.height = getGraphicalRepresentation().getMinimalHeight();
			}
		}
		return requiredBounds;
	}

	@Override
	public DianaRectangle getRequiredBoundsForContents() {
		DianaRectangle requiredBounds = super.getRequiredBoundsForContents();

		// requiredBounds.x = requiredBounds.x - getGraphicalRepresentation().getBorder().getLeft();
		// requiredBounds.y = requiredBounds.y - getGraphicalRepresentation().getBorder().getTop();

		return requiredBounds;
	}

	/**
	 * Calling this method forces Diana to check (and eventually update) dimension of current graphical representation according defined
	 * dimension constraints
	 */
	@Override
	protected void checkAndUpdateDimensionIfRequired() {
		if (getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.CONTAINER) {
			List<DrawingTreeNodeImpl<?, ?>> childs = getChildNodes();
			if (childs != null && childs.size() > 0) {
				ShapeNode<?> first = (ShapeNode<?>) childs.get(0);
				updateRequiredBoundsForChildGRLocation(first, first.getLocation());
			}
		}
		else {
			setSize(getSize());
		}
	}

	@Override
	public void paint(DianaShapeGraphics g) {

		g.translate(getBorderLeft() * g.getScale() /*+ DianaUtils.getCumulativeLeftBorders(getParentNode())*/,
				getBorderTop() * g.getScale() /*+ DianaUtils.getCumulativeTopBorders(getParentNode())*/);

		// Paint container properties (layout managers)
		super.paint(g);

		// If there is a decoration painter and decoration should be painted BEFORE shape, do it now
		if (decorationPainter != null && decorationPainter.paintBeforeShape()) {
			decorationPainter.paintDecoration(g.getShapeDecorationGraphics());
		}

		/*if (DianaConstants.DEBUG) {
			if (getGraphicalRepresentation().getBorder() != null) {
				g2.setColor(Color.RED);
				g2.drawRect(0, 0, getViewWidth(controller.getScale()) - 1, getViewHeight(controller.getScale()) - 1);
				g2.setColor(Color.BLUE);
				g2.drawRect((int) (getGraphicalRepresentation().getBorder().getLeft() * controller.getScale()),
						(int) (getGraphicalRepresentation().getBorder().getTop() * controller.getScale()),
						(int) (getWidth() * controller.getScale()) - 1, (int) (getHeight() * controller.getScale()) - 1);
			} else {
				g2.setColor(Color.BLUE);
				g2.drawRect(0, 0, getViewWidth(controller.getScale()) - 1, getViewHeight(controller.getScale()) - 1);
			}
		}*/

		if (getGraphicalRepresentation() != null && getGraphicalRepresentation().getShapeSpecification() != null
				&& getGraphicalRepresentation().getShadowStyle() != null) {

			if (getGraphicalRepresentation().getShadowStyle().getDrawShadow()) {
				g.paintShadow();
				// getShape().paintShadow(g);
			}
			// System.out.println("Painting shape: " + getShape());
			getShape().paintShape(g);

		}

		if (shapePainter != null) {
			shapePainter.paintShape(g);
		}

		// If there is a decoration painter and decoration should be painted AFTER shape, do it now
		if (decorationPainter != null && !decorationPainter.paintBeforeShape()) {
			decorationPainter.paintDecoration(g.getShapeDecorationGraphics());
		}

		g.translate(-getBorderLeft() * g.getScale(), -getBorderTop() * g.getScale());

	}

	/**
	 * Convenient method used to retrieve foreground style property value
	 */
	@Override
	public ForegroundStyle getForegroundStyle() {
		return getPropertyValue(ShapeGraphicalRepresentation.FOREGROUND);
	}

	/**
	 * Convenient method used to set foreground style property value
	 */
	@Override
	public void setForegroundStyle(ForegroundStyle aValue) {
		setPropertyValue(ShapeGraphicalRepresentation.FOREGROUND, aValue);
	}

	@Override
	public boolean getHasFocusedForegroundStyle() {
		return getGraphicalRepresentation().getHasFocusedForeground();
	}

	/**
	 * Convenient method used to retrieve focused foreground style property value
	 */
	@Override
	public ForegroundStyle getFocusedForegroundStyle() {
		return getPropertyValue(ShapeGraphicalRepresentation.FOCUSED_FOREGROUND);
	}

	/**
	 * Convenient method used to set focused foreground style property value
	 */
	@Override
	public void setFocusedForegroundStyle(ForegroundStyle aValue) {
		setPropertyValue(ShapeGraphicalRepresentation.FOCUSED_FOREGROUND, aValue);
	}

	@Override
	public boolean getHasSelectedForegroundStyle() {
		return getGraphicalRepresentation().getHasSelectedForeground();
	}

	/**
	 * Convenient method used to retrieve selected foreground style property value
	 */
	@Override
	public ForegroundStyle getSelectedForegroundStyle() {
		return getPropertyValue(ShapeGraphicalRepresentation.SELECTED_FOREGROUND);
	}

	/**
	 * Convenient method used to set selected foreground style property value
	 */
	@Override
	public void setSelectedForegroundStyle(ForegroundStyle aValue) {
		setPropertyValue(ShapeGraphicalRepresentation.SELECTED_FOREGROUND, aValue);
	}

	/**
	 * Convenient method used to retrieve background style property value
	 */
	@Override
	public BackgroundStyle getBackgroundStyle() {
		return getPropertyValue(ShapeGraphicalRepresentation.BACKGROUND);
	}

	/**
	 * Convenient method used to set background style property value
	 */
	@Override
	public void setBackgroundStyle(BackgroundStyle style) {
		setPropertyValue(ShapeGraphicalRepresentation.BACKGROUND, style);
	}

	@Override
	public boolean getHasSelectedBackgroundStyle() {
		return getGraphicalRepresentation().getHasSelectedBackground();
	}

	/**
	 * Convenient method used to retrieve selected background style property value
	 */
	@Override
	public BackgroundStyle getSelectedBackgroundStyle() {
		return getPropertyValue(ShapeGraphicalRepresentation.SELECTED_BACKGROUND);
	}

	/**
	 * Convenient method used to set selected background style property value
	 */
	@Override
	public void setSelectedBackgroundStyle(BackgroundStyle style) {
		setPropertyValue(ShapeGraphicalRepresentation.SELECTED_BACKGROUND, style);
	}

	@Override
	public boolean getHasFocusedBackgroundStyle() {
		return getGraphicalRepresentation().getHasFocusedBackground();
	}

	/**
	 * Convenient method used to retrieve focused background style property value
	 */
	@Override
	public BackgroundStyle getFocusedBackgroundStyle() {
		return getPropertyValue(ShapeGraphicalRepresentation.FOCUSED_BACKGROUND);
	}

	/**
	 * Convenient method used to set focused background style property value
	 */
	@Override
	public void setFocusedBackgroundStyle(BackgroundStyle style) {
		setPropertyValue(ShapeGraphicalRepresentation.FOCUSED_BACKGROUND, style);
	}

	/**
	 * Convenient method used to retrieve shadow style property value
	 */
	@Override
	public ShadowStyle getShadowStyle() {
		return getPropertyValue(ShapeGraphicalRepresentation.SHADOW_STYLE);
	}

	/**
	 * Convenient method used to set shadow style property value
	 */
	@Override
	public void setShadowStyle(ShadowStyle style) {
		setPropertyValue(ShapeGraphicalRepresentation.SHADOW_STYLE, style);
	}

	/**
	 * Convenient method used to retrieve border property value
	 */
	/*@Override
	public ShapeBorder getBorder() {
		return getPropertyValue(ShapeGraphicalRepresentation.BORDER);
	}*/

	/**
	 * Convenient method used to set border property value
	 */
	/*@Override
	public void setBorder(ShapeBorder border) {
		setPropertyValue(ShapeGraphicalRepresentation.BORDER, border);
	}*/

	/**
	 * Convenient method used to retrieve shape specification property value
	 */
	@Override
	public ShapeSpecification getShapeSpecification() {
		return getPropertyValue(ShapeGraphicalRepresentation.SHAPE);
	}

	/**
	 * Convenient method used to set shape specification property value
	 */
	@Override
	public void setShapeSpecification(ShapeSpecification shapeSpecification) {
		if (shapeSpecification != getShapeSpecification()) {
			setPropertyValue(ShapeGraphicalRepresentation.SHAPE, shapeSpecification);
			fireShapeSpecificationChanged();
		}
	}

	/**
	 * Called to define DianaLayoutManager for this node<br>
	 * The layout manager should be already declared in the parent. It is identified by supplied layoutManagerIdentifier.
	 * 
	 * @param layoutManagerIdentifier
	 */
	public void layoutedWith(String layoutManagerIdentifier) {
		setLayoutManager(getParentNode().getLayoutManager(layoutManagerIdentifier));
	}

	/**
	 * Return the layout manager responsible for the layout of this node (relating to its container)
	 * 
	 * @return
	 */
	@Override
	public DianaLayoutManager<?, ?> getLayoutManager() {
		return layoutManager;
	}

	private void setLayoutManager(DianaLayoutManager<?, ?> layoutManager) {
		if ((layoutManager != this.layoutManager)) {
			DianaLayoutManager<?, ?> oldValue = this.layoutManager;
			this.layoutManager = layoutManager;
			getPropertyChangeSupport().firePropertyChange("layoutManager", oldValue, layoutManager);
		}
	}

	/**
	 * Called when changed DianaLayoutManager for this node<br>
	 * The layout manager is retrieved from layout identifier defined in {@link ShapeGraphicalRepresentation}, asserting that related
	 * {@link DianaLayoutManagerSpecification} is defined in {@link ContainerGraphicalRepresentation}
	 */
	public void relayoutNode() {

		// System.out.println("************* relayoutNode called for " + this);

		DianaLayoutManager<?, ?> layoutManager = null;
		if (StringUtils.isNotEmpty(getGraphicalRepresentation().getLayoutManagerIdentifier())) {
			layoutManager = getParentNode().getLayoutManager(getGraphicalRepresentation().getLayoutManagerIdentifier());
		}
		setLayoutManager(layoutManager);
		if (layoutManager != null) {
			layoutManager.invalidate(this);
			layoutManager.doLayout(this, true);
		}
	}

	@Override
	public boolean isLayoutValidated() {
		return layoutValidated;
	}

	@Override
	public void invalidateLayout() {
		layoutValidated = false;
	}

	public void validateLayout() {
		layoutValidated = true;
	}

	/**
	 * Convenient method used to retrieve 'allowsToLeaveBounds property value
	 */
	@Override
	public Boolean getAllowsToLeaveBounds() {
		return getPropertyValue(ShapeGraphicalRepresentation.ALLOW_TO_LEAVE_BOUNDS);
	}

	/**
	 * Convenient method used to set 'allowsToLeaveBounds' property value
	 */
	@Override
	public void setAllowsToLeaveBounds(Boolean aValue) {
		setPropertyValue(ShapeGraphicalRepresentation.ALLOW_TO_LEAVE_BOUNDS, aValue);
	}

}
