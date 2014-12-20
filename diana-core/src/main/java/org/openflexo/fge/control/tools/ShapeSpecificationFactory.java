/*
 * (c) Copyright 2013-2014 Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.fge.control.tools;

import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.geom.FGEArc;
import org.openflexo.fge.geom.FGEArc.ArcType;
import org.openflexo.fge.geom.FGEComplexCurve;
import org.openflexo.fge.geom.FGEEllips;
import org.openflexo.fge.geom.FGEGeneralShape.Closure;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGERegularPolygon;
import org.openflexo.fge.geom.FGERoundRectangle;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.shapes.Arc;
import org.openflexo.fge.shapes.Chevron;
import org.openflexo.fge.shapes.Circle;
import org.openflexo.fge.shapes.ComplexCurve;
import org.openflexo.fge.shapes.Losange;
import org.openflexo.fge.shapes.Oval;
import org.openflexo.fge.shapes.Parallelogram;
import org.openflexo.fge.shapes.Plus;
import org.openflexo.fge.shapes.Polygon;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.RectangularOctogon;
import org.openflexo.fge.shapes.RegularPolygon;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fge.shapes.Square;
import org.openflexo.fge.shapes.Star;
import org.openflexo.fge.shapes.Triangle;
import org.openflexo.fge.shapes.impl.ShapeImpl;
import org.openflexo.model.undo.CompoundEdit;

/**
 * Convenient class used to manipulate ShapeSpecification instances over ShapeSpecification class hierarchy
 * 
 * @author sylvain
 * 
 */
public class ShapeSpecificationFactory implements StyleFactory<ShapeSpecification, ShapeType> {

	private static final Logger logger = Logger.getLogger(ShapeSpecificationFactory.class.getPackage().getName());

	private static final String DELETED = "deleted";

	private ShapeType shapeType = ShapeType.RECTANGLE;

	private final InspectedRectangle<Rectangle> rectangle;
	private final InspectedSquare square;
	private final InspectedPolygon<Polygon> polygon;
	private final InspectedRegularPolygon<RegularPolygon> regularPolygon;
	private final InspectedRectangularOctogon rectangularOctogon;
	private final InspectedLosange losange;
	private final InspectedTriangle triangle;
	private final InspectedOval<Oval> oval;
	private final InspectedCircle circle;
	private final InspectedArc arc;
	private final InspectedStar star;
	private final InspectedComplexCurve complexCurve;
	private final InspectedPlus plus;
	private final InspectedChevron chevron;
	private final InspectedParallelogram parallelogram;

	private PropertyChangeSupport pcSupport;
	private FGEModelFactory fgeFactory;
	private final DianaInteractiveViewer<?, ?, ?> controller;

	public ShapeSpecificationFactory(final DianaInteractiveViewer<?, ?, ?> controller) {
		this.pcSupport = new PropertyChangeSupport(this);
		this.controller = controller;
		this.fgeFactory = controller.getFactory();
		this.rectangle = new InspectedRectangle<Rectangle>(controller, (Rectangle) controller.getFactory().makeShape(ShapeType.RECTANGLE));
		this.square = new InspectedSquare(controller, (Square) controller.getFactory().makeShape(ShapeType.SQUARE));
		this.polygon = new InspectedPolygon<Polygon>(controller, (Polygon) controller.getFactory().makeShape(ShapeType.CUSTOM_POLYGON));
		this.regularPolygon = new InspectedRegularPolygon<RegularPolygon>(controller, (RegularPolygon) controller.getFactory().makeShape(
				ShapeType.POLYGON));
		this.rectangularOctogon = new InspectedRectangularOctogon(controller, (RectangularOctogon) controller.getFactory().makeShape(
				ShapeType.RECTANGULAROCTOGON));
		this.losange = new InspectedLosange(controller, (Losange) controller.getFactory().makeShape(ShapeType.LOSANGE));
		this.triangle = new InspectedTriangle(controller, (Triangle) controller.getFactory().makeShape(ShapeType.TRIANGLE));
		this.oval = new InspectedOval<Oval>(controller, (Oval) controller.getFactory().makeShape(ShapeType.OVAL));
		this.circle = new InspectedCircle(controller, (Circle) controller.getFactory().makeShape(ShapeType.CIRCLE));
		this.arc = new InspectedArc(controller, (Arc) controller.getFactory().makeShape(ShapeType.ARC));
		this.star = new InspectedStar(controller, (Star) controller.getFactory().makeShape(ShapeType.STAR));
		this.complexCurve = new InspectedComplexCurve(controller, (ComplexCurve) controller.getFactory().makeShape(ShapeType.COMPLEX_CURVE));
		this.plus = new InspectedPlus(controller, (Plus) controller.getFactory().makeShape(ShapeType.PLUS));
		this.chevron = new InspectedChevron(controller, (Chevron) controller.getFactory().makeShape(ShapeType.CHEVRON));
		this.parallelogram = new InspectedParallelogram(controller, (Parallelogram) controller.getFactory().makeShape(
				ShapeType.PARALLELOGRAM));
	}

	public DianaInteractiveViewer<?, ?, ?> getController() {
		return controller;
	}

	@Override
	public FGEModelFactory getFGEFactory() {
		return this.fgeFactory;
	}

	@Override
	public void setFGEFactory(final FGEModelFactory fgeFactory) {
		this.fgeFactory = fgeFactory;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return this.pcSupport;
	}

	public void delete() {
		this.getPropertyChangeSupport().firePropertyChange(DELETED, false, true);
		this.pcSupport = null;
	}

	@Override
	public String getDeletedProperty() {
		return DELETED;
	}

	@Override
	public AbstractInspectedShapeSpecification<?> getCurrentStyle() {
		return this.getShapeSpecification();
	}

	public AbstractInspectedShapeSpecification<?> getShapeSpecification() {
		if (this.shapeType == null) {
			return null;
		}
		switch (this.shapeType) {
			case RECTANGLE:
				return this.rectangle;
			case SQUARE:
				return this.square;
			case CUSTOM_POLYGON:
				return this.polygon;
			case PARALLELOGRAM:
				return this.parallelogram;
			case RECTANGULAROCTOGON:
				return this.rectangularOctogon;
			case POLYGON:
				return this.regularPolygon;
			case LOSANGE:
				return this.losange;
			case TRIANGLE:
				return this.triangle;
			case OVAL:
				return this.oval;
			case CIRCLE:
				return this.circle;
			case ARC:
				return this.arc;
			case STAR:
				return this.star;
			case COMPLEX_CURVE:
				return this.complexCurve;
			case PLUS:
				return this.plus;
			case CHEVRON:
				return this.chevron;
			default:
				break;
		}
		logger.warning("Unexpected " + this.shapeType);
		return null;
	}

	/**
	 * Equals method allowing null values
	 * 
	 * @param oldObject
	 * @param newObject
	 * @return
	 */
	protected boolean requireChange(final Object oldObject, final Object newObject) {
		if (oldObject == null) {
			if (newObject == null) {
				return false;
			}
			else {
				return true;
			}
		}
		return !oldObject.equals(newObject);
	}

	@Override
	public ShapeType getStyleType() {
		return this.shapeType;
	}

	@Override
	public void setStyleType(final ShapeType shapeType) {
		final ShapeType oldShapeType = this.getStyleType();

		if (oldShapeType == shapeType) {
			return;
		}

		final ShapeSpecification oldSS = this.getShapeSpecification();

		this.shapeType = shapeType;
		this.pcSupport.firePropertyChange(STYLE_CLASS_CHANGED, oldShapeType, this.getStyleType());
		this.pcSupport.firePropertyChange("shapeSpecification", oldSS, this.getShapeSpecification());
		this.pcSupport.firePropertyChange("styleType", oldShapeType, this.getStyleType());
	}

	public boolean isSingleSelection() {
		return getController().getSelectedShapes().size() == 1;
	}

	@Override
	public ShapeSpecification makeNewStyle(final ShapeSpecification oldShapeSpecification) {
		switch (this.shapeType) {
			case RECTANGLE:
				return this.rectangle.cloneStyle();
			case SQUARE:
				return this.square.cloneStyle();
			case CUSTOM_POLYGON:
				return this.polygon.cloneStyle();
			case RECTANGULAROCTOGON:
				return this.rectangularOctogon.cloneStyle();
			case POLYGON:
				return this.regularPolygon.cloneStyle();
			case LOSANGE:
				return this.losange.cloneStyle();
			case TRIANGLE:
				return this.triangle.cloneStyle();
			case OVAL:
				return this.oval.cloneStyle();
			case CIRCLE:
				return this.circle.cloneStyle();
			case ARC:
				return this.arc.cloneStyle();
			case COMPLEX_CURVE:
				return this.complexCurve.cloneStyle();
			case STAR:
				return this.star.cloneStyle();
			case PLUS:
				return this.plus.cloneStyle();
			case CHEVRON:
				return this.chevron.cloneStyle();
			case PARALLELOGRAM:
				return this.parallelogram.cloneStyle();
			default:
				return null;
		}
	}

	protected abstract class AbstractInspectedShapeSpecification<SS extends ShapeSpecification> extends InspectedStyle<SS> implements
			ShapeSpecification {

		protected AbstractInspectedShapeSpecification(final DianaInteractiveViewer<?, ?, ?> controller, final SS defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public List<ShapeNode<?>> getSelection() {
			return this.getController().getSelectedShapes();
		}

		@Override
		public ShapeImpl<?> makeShape(final ShapeNode<?> node) {
			final ShapeImpl returned = new ShapeImpl(node);
			this.getPropertyChangeSupport().addPropertyChangeListener(returned);
			return returned;
		}

	}

	protected class InspectedRectangle<SS extends Rectangle> extends AbstractInspectedShapeSpecification<SS> implements Rectangle {

		protected InspectedRectangle(final DianaInteractiveViewer<?, ?, ?> controller, final SS defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public boolean areDimensionConstrained() {
			return false;
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.RECTANGLE;
		}

		@Override
		public FGEShape<?> makeFGEShape(final ShapeNode<?> node) {
			if (node != null && this.getIsRounded()) {
				final double arcwidth = this.getArcSize() / node.getWidth();
				final double archeight = this.getArcSize() / node.getHeight();
				return new FGERoundRectangle(0, 0, 1, 1, arcwidth, archeight, Filling.FILLED);
			}
			return new FGERectangle(0, 0, 1, 1, Filling.FILLED);
		}

		@Override
		public double getArcSize() {
			return this.getPropertyValue(Rectangle.ARC_SIZE);
		}

		@Override
		public void setArcSize(final double anArcSize) {
			this.setPropertyValue(Rectangle.ARC_SIZE, anArcSize);
		}

		@Override
		public boolean getIsRounded() {
			return this.getPropertyValue(Rectangle.IS_ROUNDED);
		}

		@Override
		public void setIsRounded(final boolean aFlag) {
			this.setPropertyValue(Rectangle.IS_ROUNDED, aFlag);
		}

		@Override
		public SS getStyle(final DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Rectangle) {
					return (SS) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedSquare extends InspectedRectangle<Square> implements Square {

		protected InspectedSquare(final DianaInteractiveViewer<?, ?, ?> controller, final Square defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public boolean areDimensionConstrained() {
			return true;
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.SQUARE;
		}

		@Override
		public Square getStyle(final DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Square) {
					return (Square) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedPolygon<SS extends Polygon> extends AbstractInspectedShapeSpecification<SS> implements Polygon {

		// private List<FGEPoint> points;

		protected InspectedPolygon(final DianaInteractiveViewer<?, ?, ?> controller, final SS defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public boolean areDimensionConstrained() {
			return false;
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.CUSTOM_POLYGON;
		}

		@Override
		public List<FGEPoint> getPoints() {
			return this.getPropertyValue(Polygon.POINTS);
		}

		@Override
		public void setPoints(final List<FGEPoint> points) {
			// Not applicable in this context (ambigous semantics, preferably disabled)
			this.setPropertyValue(Polygon.POINTS, points);
		}

		@Override
		public void addToPoints(final FGEPoint aPoint) {
			// Not applicable in this context (ambigous semantics, preferably disabled)
			// points.add(aPoint);
			// notifyChange(POINTS);
			getPoints().add(new FGEPoint(1.0, 1.0));
			notifyChange(POINTS);
		}

		@Override
		public void removeFromPoints(final FGEPoint aPoint) {
			// Not applicable in this context (ambigous semantics, preferably disabled)
			// points.remove(aPoint);
			// notifyChange(POINTS);

			getPoints().remove(aPoint);
			notifyChange(POINTS);
		}

		public void addCustomPolygonPoint(FGEPoint current) {
			CompoundEdit addPointEdit = startRecordEdit("Add point");
			int index = (current != null ? getPoints().indexOf(current) : -1);
			FGEPoint newPoint;
			if (!getPoints().isEmpty()) {
				FGEPoint previousPoint = (index > -1 ? getPoints().get(index) : getPoints().get(getPoints().size() - 1));
				FGEPoint nextPoint = (index + 1 < getPoints().size() ? getPoints().get(index + 1) : getPoints().get(0));
				newPoint = FGEPoint.middleOf(previousPoint, nextPoint);
			}
			else {
				newPoint = new FGEPoint(0.5, 0.5);
			}
			if (index == -1) {
				getPoints().add(newPoint);
			}
			else {
				getPoints().add(index + 1, newPoint);
			}
			notifyChange(POINTS);
			stopRecordEdit(addPointEdit);

		}

		public void deleteCustomPolygonPoint(FGEPoint current) {
			if (current == null) {
				return;
			}
			int index = getPoints().indexOf(current);
			if (index > -1) {
				CompoundEdit removePointEdit = startRecordEdit("Remove point");
				getPoints().remove(current);
				notifyChange(POINTS);
				stopRecordEdit(removePointEdit);
			}
		}

		@Override
		public FGEShape<?> makeFGEShape(final ShapeNode<?> node) {
			return new FGEPolygon(Filling.FILLED, this.getPoints());
		}

		@Override
		public SS getStyle(final DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Polygon) {
					return (SS) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

		public void geometryChanged() {
			notifyChange(POINTS);
		}
	}

	protected class InspectedComplexCurve extends AbstractInspectedShapeSpecification<ComplexCurve> implements ComplexCurve {

		// private List<FGEPoint> points;

		protected InspectedComplexCurve(final DianaInteractiveViewer<?, ?, ?> controller, final ComplexCurve defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public boolean areDimensionConstrained() {
			return false;
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.COMPLEX_CURVE;
		}

		@Override
		public List<FGEPoint> getPoints() {
			return this.getPropertyValue(ComplexCurve.POINTS);
		}

		@Override
		public void setPoints(final List<FGEPoint> points) {
			// Not applicable in this context (ambigous semantics, preferably disabled)
			/*if (points != null) {
				this.points = new ArrayList<FGEPoint>(points);
			} else {
				this.points = null;
			}
			notifyChange(POINTS);*/
			this.setPropertyValue(ComplexCurve.POINTS, points);
		}

		@Override
		public void addToPoints(final FGEPoint aPoint) {
			// Not applicable in this context (ambigous semantics, preferably disabled)
			// points.add(aPoint);
			// notifyChange(POINTS);
		}

		@Override
		public void removeFromPoints(final FGEPoint aPoint) {
			// Not applicable in this context (ambigous semantics, preferably disabled)
			// points.remove(aPoint);
			// notifyChange(POINTS);
		}

		@Override
		public Closure getClosure() {
			return this.getPropertyValue(ComplexCurve.CLOSURE);
		}

		@Override
		public void setClosure(final Closure aClosure) {
			this.setPropertyValue(ComplexCurve.CLOSURE, aClosure);
		}

		@Override
		public FGEShape<?> makeFGEShape(final ShapeNode<?> node) {
			return new FGEComplexCurve(this.getClosure(), this.getPoints());
		}

		@Override
		public ComplexCurve getStyle(final DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof ComplexCurve) {
					return (ComplexCurve) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedRegularPolygon<SS extends RegularPolygon> extends InspectedPolygon<SS> implements RegularPolygon {

		protected InspectedRegularPolygon(final DianaInteractiveViewer<?, ?, ?> controller, final SS defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public int getNPoints() {
			return this.getPropertyValue(RegularPolygon.N_POINTS);
		}

		@Override
		public void setNPoints(final int pointsNb) {
			this.setPropertyValue(RegularPolygon.N_POINTS, pointsNb);
		}

		@Override
		public int getStartAngle() {
			return this.getPropertyValue(RegularPolygon.START_ANGLE);
		}

		@Override
		public void setStartAngle(final int anAngle) {
			this.setPropertyValue(RegularPolygon.START_ANGLE, anAngle);
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.POLYGON;
		}

		@Override
		public SS getStyle(final DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof RegularPolygon) {
					return (SS) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

		@Override
		public FGEShape<?> makeFGEShape(final ShapeNode<?> node) {
			if (this.getNPoints() > 2) {
				return new FGERegularPolygon(0, 0, 1, 1, Filling.FILLED, this.getNPoints(), this.getStartAngle());
			}
			return new FGERectangle(0, 0, 1, 1, Filling.FILLED);
		}

	}

	protected class InspectedRectangularOctogon extends AbstractInspectedShapeSpecification<RectangularOctogon> implements
			RectangularOctogon {

		protected InspectedRectangularOctogon(final DianaInteractiveViewer<?, ?, ?> controller, final RectangularOctogon defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.RECTANGULAROCTOGON;
		}

		@Override
		public RectangularOctogon getStyle(final DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof RectangularOctogon) {
					return (RectangularOctogon) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

		@Override
		public double getRatio() {
			return this.getPropertyValue(Plus.RATIO);
		}

		@Override
		public void setRatio(final double aRatio) {
			this.setPropertyValue(Plus.RATIO, aRatio);
		}

		@Override
		public boolean areDimensionConstrained() {
			return false;
		}

		@Override
		public FGEShape<?> makeFGEShape(final ShapeNode<?> node) {
			final FGEPolygon returned = new FGEPolygon(Filling.FILLED);
			returned.addToPoints(new FGEPoint(0, this.getRatio()));
			returned.addToPoints(new FGEPoint(0, 1 - this.getRatio()));
			returned.addToPoints(new FGEPoint(this.getRatio() / 2, 1));
			returned.addToPoints(new FGEPoint(1 - this.getRatio() / 2, 1));
			returned.addToPoints(new FGEPoint(1, 1 - this.getRatio()));
			returned.addToPoints(new FGEPoint(1, this.getRatio()));
			returned.addToPoints(new FGEPoint(1 - this.getRatio() / 2, 0));
			returned.addToPoints(new FGEPoint(this.getRatio() / 2, 0));

			return returned;
		}

	}

	protected class InspectedLosange extends InspectedRegularPolygon<Losange> implements Losange {

		protected InspectedLosange(final DianaInteractiveViewer<?, ?, ?> controller, final Losange defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.LOSANGE;
		}

		@Override
		public Losange getStyle(final DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Losange) {
					return (Losange) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedTriangle extends InspectedRegularPolygon<Triangle> implements Triangle {

		protected InspectedTriangle(final DianaInteractiveViewer<?, ?, ?> controller, final Triangle defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.TRIANGLE;
		}

		@Override
		public Triangle getStyle(final DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Triangle) {
					return (Triangle) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedOval<SS extends Oval> extends AbstractInspectedShapeSpecification<SS> implements Oval {

		protected InspectedOval(final DianaInteractiveViewer<?, ?, ?> controller, final SS defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public boolean areDimensionConstrained() {
			return false;
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.OVAL;
		}

		@Override
		public FGEShape<?> makeFGEShape(final ShapeNode<?> node) {
			return new FGEEllips(0, 0, 1, 1, Filling.FILLED);
		}

		@Override
		public SS getStyle(final DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Oval) {
					return (SS) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedCircle extends InspectedOval<Circle> implements Circle {

		protected InspectedCircle(final DianaInteractiveViewer<?, ?, ?> controller, final Circle defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public boolean areDimensionConstrained() {
			return true;
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.CIRCLE;
		}

		@Override
		public Circle getStyle(final DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Circle) {
					return (Circle) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedArc extends AbstractInspectedShapeSpecification<Arc> implements Arc {

		protected InspectedArc(final DianaInteractiveViewer<?, ?, ?> controller, final Arc defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public boolean areDimensionConstrained() {
			return false;
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.ARC;
		}

		@Override
		public FGEShape<?> makeFGEShape(final ShapeNode<?> node) {
			return new FGEArc(0, 0, 1, 1, this.getAngleStart(), this.getAngleExtent(), this.getArcType());
		}

		@Override
		public int getAngleStart() {
			return this.getPropertyValue(Arc.ANGLE_START);
		}

		@Override
		public void setAngleStart(final int anAngle) {
			this.setPropertyValue(Arc.ANGLE_START, anAngle);
		}

		@Override
		public int getAngleExtent() {
			return this.getPropertyValue(Arc.ANGLE_EXTENT);
		}

		@Override
		public void setAngleExtent(final int anAngle) {
			this.setPropertyValue(Arc.ANGLE_EXTENT, anAngle);
		}

		@Override
		public ArcType getArcType() {
			return this.getPropertyValue(Arc.ARC_TYPE);
		}

		@Override
		public void setArcType(final ArcType anArcType) {
			this.setPropertyValue(Arc.ARC_TYPE, anArcType);
		}

		@Override
		public Arc getStyle(final DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Arc) {
					return (Arc) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedStar extends AbstractInspectedShapeSpecification<Star> implements Star {

		protected InspectedStar(final DianaInteractiveViewer<?, ?, ?> controller, final Star defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public boolean areDimensionConstrained() {
			return false;
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.STAR;
		}

		@Override
		public FGEShape<?> makeFGEShape(final ShapeNode<?> node) {
			final FGEPolygon returned = new FGEPolygon(Filling.FILLED);
			final double startA = this.getStartAngle() * Math.PI / 180;
			final double angleInterval = Math.PI * 2 / this.getNPoints();
			for (int i = 0; i < this.getNPoints(); i++) {
				final double angle = i * angleInterval + startA;
				final double angle1 = (i - 0.5) * angleInterval + startA;
				returned.addToPoints(new FGEPoint(Math.cos(angle1) * 0.5 * this.getRatio() + 0.5, Math.sin(angle1) * 0.5 * this.getRatio()
						+ 0.5));
				returned.addToPoints(new FGEPoint(Math.cos(angle) * 0.5 + 0.5, Math.sin(angle) * 0.5 + 0.5));
			}
			return returned;
		}

		@Override
		public int getNPoints() {
			return this.getPropertyValue(Star.N_POINTS);
		}

		@Override
		public void setNPoints(final int pointsNb) {
			this.setPropertyValue(Star.N_POINTS, pointsNb);
		}

		@Override
		public int getStartAngle() {
			return this.getPropertyValue(Star.START_ANGLE);
		}

		@Override
		public void setStartAngle(final int anAngle) {
			this.setPropertyValue(Star.START_ANGLE, anAngle);
		}

		@Override
		public double getRatio() {
			return this.getPropertyValue(Star.RATIO);
		}

		@Override
		public void setRatio(final double aRatio) {
			this.setPropertyValue(Star.RATIO, aRatio);
		}

		@Override
		public Star getStyle(final DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Star) {
					return (Star) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}
	}

	protected class InspectedPlus extends AbstractInspectedShapeSpecification<Plus> implements Plus {

		protected InspectedPlus(final DianaInteractiveViewer<?, ?, ?> controller, final Plus defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.PLUS;
		}

		@Override
		public Plus getStyle(final DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Plus) {
					return (Plus) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

		@Override
		public double getRatio() {
			return this.getPropertyValue(Plus.RATIO);
		}

		@Override
		public void setRatio(final double aRatio) {
			this.setPropertyValue(Plus.RATIO, aRatio);
		}

		@Override
		public boolean areDimensionConstrained() {
			return true;
		}

		@Override
		public FGEShape<?> makeFGEShape(final ShapeNode<?> node) {
			final FGEPolygon returned = new FGEPolygon(Filling.FILLED);
			returned.addToPoints(new FGEPoint(0, this.getRatio()));
			returned.addToPoints(new FGEPoint(0, 1 - this.getRatio()));
			returned.addToPoints(new FGEPoint(this.getRatio(), 1 - this.getRatio()));
			returned.addToPoints(new FGEPoint(this.getRatio(), 1));
			returned.addToPoints(new FGEPoint(1 - this.getRatio(), 1));
			returned.addToPoints(new FGEPoint(1 - this.getRatio(), 1 - this.getRatio()));
			returned.addToPoints(new FGEPoint(1, 1 - this.getRatio()));
			returned.addToPoints(new FGEPoint(1, this.getRatio()));
			returned.addToPoints(new FGEPoint(1 - this.getRatio(), this.getRatio()));
			returned.addToPoints(new FGEPoint(1 - this.getRatio(), 0));
			returned.addToPoints(new FGEPoint(this.getRatio(), 0));
			returned.addToPoints(new FGEPoint(this.getRatio(), this.getRatio()));
			return returned;
		}
	}

	protected class InspectedChevron extends AbstractInspectedShapeSpecification<Chevron> implements Chevron {

		protected InspectedChevron(final DianaInteractiveViewer<?, ?, ?> controller, final Chevron defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.CHEVRON;
		}

		@Override
		public Chevron getStyle(final DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Chevron) {
					return (Chevron) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

		@Override
		public double getArrowLength() {
			return this.getPropertyValue(Chevron.ARROW_LENGTH);
		}

		@Override
		public void setArrowLength(final double anArrowLength) {
			this.setPropertyValue(Chevron.ARROW_LENGTH, anArrowLength);
		}

		@Override
		public boolean areDimensionConstrained() {
			return false;
		}

		@Override
		public FGEShape<?> makeFGEShape(final ShapeNode<?> node) {
			final FGEPolygon returned = new FGEPolygon(Filling.FILLED);
			returned.addToPoints(new FGEPoint(0, 0));
			returned.addToPoints(new FGEPoint(this.getArrowLength(), 0.5));
			returned.addToPoints(new FGEPoint(0, 1));
			returned.addToPoints(new FGEPoint(1 - this.getArrowLength(), 1));
			returned.addToPoints(new FGEPoint(1, 0.5));
			returned.addToPoints(new FGEPoint(1 - this.getArrowLength(), 0));
			return returned;
		}
	}

	protected class InspectedParallelogram extends AbstractInspectedShapeSpecification<Parallelogram> implements Parallelogram {

		protected InspectedParallelogram(final DianaInteractiveViewer<?, ?, ?> controller, final Parallelogram defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.PARALLELOGRAM;
		}

		@Override
		public Parallelogram getStyle(final DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Parallelogram) {
					return (Parallelogram) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

		@Override
		public double getShiftRatio() {
			return this.getPropertyValue(Parallelogram.SHIFT_RATIO);
		}

		@Override
		public void setShiftRatio(final double aShiftRatio) {
			this.setPropertyValue(Parallelogram.SHIFT_RATIO, aShiftRatio);
		}

		@Override
		public boolean areDimensionConstrained() {
			return false;
		}

		@Override
		public FGEShape<?> makeFGEShape(final ShapeNode<?> node) {
			final FGEPolygon returned = new FGEPolygon(Filling.FILLED);
			double shift_ratio = getShiftRatio();
			if (shift_ratio >= 0) {
				returned.addToPoints(new FGEPoint(shift_ratio, 0));
				returned.addToPoints(new FGEPoint(1, 0));
				returned.addToPoints(new FGEPoint(1 - shift_ratio, 1));
				returned.addToPoints(new FGEPoint(0, 1));
			}
			else {
				returned.addToPoints(new FGEPoint(0, 0));
				returned.addToPoints(new FGEPoint(1 + shift_ratio, 0));
				returned.addToPoints(new FGEPoint(1, 1));
				returned.addToPoints(new FGEPoint(-shift_ratio, 1));
			}
			return returned;
		}
	}

	// This method is used in Fib!!
	public InspectedPolygon<Polygon> getInspectedPolygon() {
		return polygon;
	}

}
