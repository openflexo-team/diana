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

/**
 * Convenient class used to manipulate ShapeSpecification instances over ShapeSpecification class hierarchy
 * 
 * @author sylvain
 * 
 */
public class ShapeSpecificationFactory implements StyleFactory<ShapeSpecification, ShapeType> {

	private static final Logger										logger		= Logger.getLogger(ShapeSpecificationFactory.class
																						.getPackage().getName());

	private static final String										DELETED		= "deleted";

	private ShapeType												shapeType	= ShapeType.RECTANGLE;

	private final InspectedRectangle<Rectangle>						rectangle;
	private final InspectedSquare									square;
	private final InspectedPolygon<Polygon>							polygon;
	private final InspectedRegularPolygon<RegularPolygon>			regularPolygon;
	private final InspectedRectangularOctogon<RectangularOctogon>	rectangularOctogon;
	private final InspectedLosange									losange;
	private final InspectedTriangle									triangle;
	private final InspectedOval<Oval>								oval;
	private final InspectedCircle									circle;
	private final InspectedArc										arc;
	private final InspectedStar										star;
	private final InspectedComplexCurve								complexCurve;
	private final InspectedPlus<Plus>								plus;
	private final InspectedChevron<Chevron>							chevron;

	private PropertyChangeSupport									pcSupport;
	private FGEModelFactory											fgeFactory;

	public ShapeSpecificationFactory(DianaInteractiveViewer<?, ?, ?> controller) {
		pcSupport = new PropertyChangeSupport(this);
		fgeFactory = controller.getFactory();
		rectangle = new InspectedRectangle<Rectangle>(controller, (Rectangle) controller.getFactory().makeShape(ShapeType.RECTANGLE));
		square = new InspectedSquare(controller, (Square) controller.getFactory().makeShape(ShapeType.SQUARE));
		polygon = new InspectedPolygon<Polygon>(controller, (Polygon) controller.getFactory().makeShape(ShapeType.CUSTOM_POLYGON));
		regularPolygon = new InspectedRegularPolygon<RegularPolygon>(controller, (RegularPolygon) controller.getFactory().makeShape(
				ShapeType.POLYGON));
		rectangularOctogon = new InspectedRectangularOctogon<RectangularOctogon>(controller, (RectangularOctogon) controller.getFactory()
				.makeShape(ShapeType.RECTANGULAROCTOGON));
		losange = new InspectedLosange(controller, (Losange) controller.getFactory().makeShape(ShapeType.LOSANGE));
		triangle = new InspectedTriangle(controller, (Triangle) controller.getFactory().makeShape(ShapeType.TRIANGLE));
		oval = new InspectedOval<Oval>(controller, (Oval) controller.getFactory().makeShape(ShapeType.OVAL));
		circle = new InspectedCircle(controller, (Circle) controller.getFactory().makeShape(ShapeType.CIRCLE));
		arc = new InspectedArc(controller, (Arc) controller.getFactory().makeShape(ShapeType.ARC));
		star = new InspectedStar(controller, (Star) controller.getFactory().makeShape(ShapeType.STAR));
		complexCurve = new InspectedComplexCurve(controller, (ComplexCurve) controller.getFactory().makeShape(ShapeType.COMPLEX_CURVE));
		plus = new InspectedPlus<Plus>(controller, (Plus) controller.getFactory().makeShape(ShapeType.PLUS));
		chevron = new InspectedChevron<Chevron>(controller, (Chevron) controller.getFactory().makeShape(ShapeType.CHEVRON));
	}

	@Override
	public FGEModelFactory getFGEFactory() {
		return fgeFactory;
	}

	@Override
	public void setFGEFactory(FGEModelFactory fgeFactory) {
		this.fgeFactory = fgeFactory;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	public void delete() {
		getPropertyChangeSupport().firePropertyChange(DELETED, false, true);
		pcSupport = null;
	}

	@Override
	public String getDeletedProperty() {
		return DELETED;
	}

	@Override
	public AbstractInspectedShapeSpecification<?> getCurrentStyle() {
		return getShapeSpecification();
	}

	public AbstractInspectedShapeSpecification<?> getShapeSpecification() {
		switch (shapeType) {
		case RECTANGLE:
			return rectangle;
		case SQUARE:
			return square;
		case CUSTOM_POLYGON:
			return polygon;
		case RECTANGULAROCTOGON:
			return rectangularOctogon;
		case POLYGON:
			return regularPolygon;
		case LOSANGE:
			return losange;
		case TRIANGLE:
			return triangle;
		case OVAL:
			return oval;
		case CIRCLE:
			return circle;
		case ARC:
			return arc;
		case STAR:
			return star;
		case COMPLEX_CURVE:
			return complexCurve;
		case PLUS:
			return plus;
		case CHEVRON:
			return chevron;
		default:
			break;
		}
		logger.warning("Unexpected " + shapeType);
		return null;
	}

	/**
	 * Equals method allowing null values
	 * 
	 * @param oldObject
	 * @param newObject
	 * @return
	 */
	protected boolean requireChange(Object oldObject, Object newObject) {
		if (oldObject == null) {
			if (newObject == null) {
				return false;
			} else {
				return true;
			}
		}
		return !oldObject.equals(newObject);
	}

	@Override
	public ShapeType getStyleType() {
		return shapeType;
	}

	@Override
	public void setStyleType(ShapeType shapeType) {
		ShapeType oldShapeType = getStyleType();

		if (oldShapeType == shapeType) {
			return;
		}

		ShapeSpecification oldSS = getShapeSpecification();

		this.shapeType = shapeType;
		pcSupport.firePropertyChange(STYLE_CLASS_CHANGED, oldShapeType, getStyleType());
		pcSupport.firePropertyChange("shapeSpecification", oldSS, getShapeSpecification());
		pcSupport.firePropertyChange("styleType", oldShapeType, getStyleType());
	}

	@Override
	public ShapeSpecification makeNewStyle(ShapeSpecification oldShapeSpecification) {
		switch (shapeType) {
		case RECTANGLE:
			return rectangle.cloneStyle();
		case SQUARE:
			return square.cloneStyle();
		case CUSTOM_POLYGON:
			return polygon.cloneStyle();
		case RECTANGULAROCTOGON:
			return rectangularOctogon.cloneStyle();
		case POLYGON:
			return regularPolygon.cloneStyle();
		case LOSANGE:
			return losange.cloneStyle();
		case TRIANGLE:
			return triangle.cloneStyle();
		case OVAL:
			return oval.cloneStyle();
		case CIRCLE:
			return circle.cloneStyle();
		case ARC:
			return arc.cloneStyle();
		case COMPLEX_CURVE:
			return complexCurve.cloneStyle();
		case STAR:
			return star.cloneStyle();
		case PLUS:
			return plus.cloneStyle();
		case CHEVRON:
			return chevron.cloneStyle();
		default:
			return null;
		}
	}

	protected abstract class AbstractInspectedShapeSpecification<SS extends ShapeSpecification> extends InspectedStyle<SS> implements
			ShapeSpecification {

		protected AbstractInspectedShapeSpecification(DianaInteractiveViewer<?, ?, ?> controller, SS defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public List<ShapeNode<?>> getSelection() {
			return getController().getSelectedShapes();
		}

		@Override
		public ShapeImpl<?> makeShape(ShapeNode<?> node) {
			ShapeImpl returned = new ShapeImpl(node);
			getPropertyChangeSupport().addPropertyChangeListener(returned);
			return returned;
		}

	}

	protected class InspectedRectangle<SS extends Rectangle> extends AbstractInspectedShapeSpecification<SS> implements Rectangle {

		protected InspectedRectangle(DianaInteractiveViewer<?, ?, ?> controller, SS defaultValue) {
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
		public FGEShape<?> makeFGEShape(ShapeNode<?> node) {
			if (node != null && getIsRounded()) {
				double arcwidth = getArcSize() / node.getWidth();
				double archeight = getArcSize() / node.getHeight();
				return new FGERoundRectangle(0, 0, 1, 1, arcwidth, archeight, Filling.FILLED);
			}
			return new FGERectangle(0, 0, 1, 1, Filling.FILLED);
		}

		@Override
		public double getArcSize() {
			return getPropertyValue(Rectangle.ARC_SIZE);
		}

		@Override
		public void setArcSize(double anArcSize) {
			setPropertyValue(Rectangle.ARC_SIZE, anArcSize);
		}

		@Override
		public boolean getIsRounded() {
			return getPropertyValue(Rectangle.IS_ROUNDED);
		}

		@Override
		public void setIsRounded(boolean aFlag) {
			setPropertyValue(Rectangle.IS_ROUNDED, aFlag);
		}

		@Override
		public SS getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Rectangle) {
					return (SS) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedSquare extends InspectedRectangle<Square> implements Square {

		protected InspectedSquare(DianaInteractiveViewer<?, ?, ?> controller, Square defaultValue) {
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
		public Square getStyle(DrawingTreeNode<?, ?> node) {
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

		protected InspectedPolygon(DianaInteractiveViewer<?, ?, ?> controller, SS defaultValue) {
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
			return getPropertyValue(ComplexCurve.POINTS);
		}

		@Override
		public void setPoints(List<FGEPoint> points) {
			// Not applicable in this context (ambigous semantics, preferably disabled)
			/*if (points != null) {
				this.points = new ArrayList<FGEPoint>(points);
			} else {
				this.points = null;
			}
			notifyChange(POINTS);*/
			setPropertyValue(ComplexCurve.POINTS, points);
		}

		@Override
		public void addToPoints(FGEPoint aPoint) {
			// Not applicable in this context (ambigous semantics, preferably disabled)
			// points.add(aPoint);
			// notifyChange(POINTS);
		}

		@Override
		public void removeFromPoints(FGEPoint aPoint) {
			// Not applicable in this context (ambigous semantics, preferably disabled)
			// points.remove(aPoint);
			// notifyChange(POINTS);
		}

		@Override
		public FGEShape<?> makeFGEShape(ShapeNode<?> node) {
			return new FGEPolygon(Filling.FILLED, getPoints());
		}

		@Override
		public SS getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Polygon) {
					return (SS) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedComplexCurve extends AbstractInspectedShapeSpecification<ComplexCurve> implements ComplexCurve {

		// private List<FGEPoint> points;

		protected InspectedComplexCurve(DianaInteractiveViewer<?, ?, ?> controller, ComplexCurve defaultValue) {
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
			return getPropertyValue(ComplexCurve.POINTS);
		}

		@Override
		public void setPoints(List<FGEPoint> points) {
			// Not applicable in this context (ambigous semantics, preferably disabled)
			/*if (points != null) {
				this.points = new ArrayList<FGEPoint>(points);
			} else {
				this.points = null;
			}
			notifyChange(POINTS);*/
			setPropertyValue(ComplexCurve.POINTS, points);
		}

		@Override
		public void addToPoints(FGEPoint aPoint) {
			// Not applicable in this context (ambigous semantics, preferably disabled)
			// points.add(aPoint);
			// notifyChange(POINTS);
		}

		@Override
		public void removeFromPoints(FGEPoint aPoint) {
			// Not applicable in this context (ambigous semantics, preferably disabled)
			// points.remove(aPoint);
			// notifyChange(POINTS);
		}

		@Override
		public Closure getClosure() {
			return getPropertyValue(ComplexCurve.CLOSURE);
		}

		@Override
		public void setClosure(Closure aClosure) {
			setPropertyValue(ComplexCurve.CLOSURE, aClosure);
		}

		@Override
		public FGEShape<?> makeFGEShape(ShapeNode<?> node) {
			return new FGEComplexCurve(getClosure(), getPoints());
		}

		@Override
		public ComplexCurve getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof ComplexCurve) {
					return (ComplexCurve) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedRegularPolygon<SS extends RegularPolygon> extends InspectedPolygon<SS> implements RegularPolygon {

		protected InspectedRegularPolygon(DianaInteractiveViewer<?, ?, ?> controller, SS defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public int getNPoints() {
			return getPropertyValue(RegularPolygon.N_POINTS);
		}

		@Override
		public void setNPoints(int pointsNb) {
			setPropertyValue(RegularPolygon.N_POINTS, pointsNb);
		}

		@Override
		public int getStartAngle() {
			return getPropertyValue(RegularPolygon.START_ANGLE);
		}

		@Override
		public void setStartAngle(int anAngle) {
			setPropertyValue(RegularPolygon.START_ANGLE, anAngle);
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.POLYGON;
		}

		@Override
		public SS getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof RegularPolygon) {
					return (SS) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

		@Override
		public FGEShape<?> makeFGEShape(ShapeNode<?> node) {
			if (getNPoints() > 2) {
				return new FGERegularPolygon(0, 0, 1, 1, Filling.FILLED, getNPoints(), getStartAngle());
			}
			return new FGERectangle(0, 0, 1, 1, Filling.FILLED);
		}

	}

	protected class InspectedRectangularOctogon<SS extends RectangularOctogon> extends InspectedPolygon<SS> implements RectangularOctogon {

		protected InspectedRectangularOctogon(DianaInteractiveViewer<?, ?, ?> controller, SS defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.POLYGON;
		}

		@Override
		public SS getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof RectangularOctogon) {
					return (SS) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedLosange extends InspectedRegularPolygon<Losange> implements Losange {

		protected InspectedLosange(DianaInteractiveViewer<?, ?, ?> controller, Losange defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.LOSANGE;
		}

		@Override
		public Losange getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Losange) {
					return (Losange) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedTriangle extends InspectedRegularPolygon<Triangle> implements Triangle {

		protected InspectedTriangle(DianaInteractiveViewer<?, ?, ?> controller, Triangle defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.TRIANGLE;
		}

		@Override
		public Triangle getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Triangle) {
					return (Triangle) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedOval<SS extends Oval> extends AbstractInspectedShapeSpecification<SS> implements Oval {

		protected InspectedOval(DianaInteractiveViewer<?, ?, ?> controller, SS defaultValue) {
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
		public FGEShape<?> makeFGEShape(ShapeNode<?> node) {
			return new FGEEllips(0, 0, 1, 1, Filling.FILLED);
		}

		@Override
		public SS getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Oval) {
					return (SS) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedCircle extends InspectedOval<Circle> implements Circle {

		protected InspectedCircle(DianaInteractiveViewer<?, ?, ?> controller, Circle defaultValue) {
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
		public Circle getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Circle) {
					return (Circle) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedArc extends AbstractInspectedShapeSpecification<Arc> implements Arc {

		protected InspectedArc(DianaInteractiveViewer<?, ?, ?> controller, Arc defaultValue) {
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
		public FGEShape<?> makeFGEShape(ShapeNode<?> node) {
			return new FGEArc(0, 0, 1, 1, getAngleStart(), getAngleExtent(), getArcType());
		}

		@Override
		public int getAngleStart() {
			return getPropertyValue(Arc.ANGLE_START);
		}

		@Override
		public void setAngleStart(int anAngle) {
			setPropertyValue(Arc.ANGLE_START, anAngle);
		}

		@Override
		public int getAngleExtent() {
			return getPropertyValue(Arc.ANGLE_EXTENT);
		}

		@Override
		public void setAngleExtent(int anAngle) {
			setPropertyValue(Arc.ANGLE_EXTENT, anAngle);
		}

		@Override
		public ArcType getArcType() {
			return getPropertyValue(Arc.ARC_TYPE);
		}

		@Override
		public void setArcType(ArcType anArcType) {
			setPropertyValue(Arc.ARC_TYPE, anArcType);
		}

		@Override
		public Arc getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Arc) {
					return (Arc) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

	}

	protected class InspectedStar extends AbstractInspectedShapeSpecification<Star> implements Star {

		protected InspectedStar(DianaInteractiveViewer<?, ?, ?> controller, Star defaultValue) {
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
		public FGEShape<?> makeFGEShape(ShapeNode<?> node) {
			FGEPolygon returned = new FGEPolygon(Filling.FILLED);
			double startA = getStartAngle() * Math.PI / 180;
			double angleInterval = Math.PI * 2 / getNPoints();
			for (int i = 0; i < getNPoints(); i++) {
				double angle = i * angleInterval + startA;
				double angle1 = (i - 0.5) * angleInterval + startA;
				returned.addToPoints(new FGEPoint(Math.cos(angle1) * 0.5 * getRatio() + 0.5, Math.sin(angle1) * 0.5 * getRatio() + 0.5));
				returned.addToPoints(new FGEPoint(Math.cos(angle) * 0.5 + 0.5, Math.sin(angle) * 0.5 + 0.5));
			}
			return returned;
		}

		@Override
		public int getNPoints() {
			return getPropertyValue(Star.N_POINTS);
		}

		@Override
		public void setNPoints(int pointsNb) {
			setPropertyValue(Star.N_POINTS, pointsNb);
		}

		@Override
		public int getStartAngle() {
			return getPropertyValue(Star.START_ANGLE);
		}

		@Override
		public void setStartAngle(int anAngle) {
			setPropertyValue(Star.START_ANGLE, anAngle);
		}

		@Override
		public double getRatio() {
			return getPropertyValue(Star.RATIO);
		}

		@Override
		public void setRatio(double aRatio) {
			setPropertyValue(Star.RATIO, aRatio);
		}

		@Override
		public Star getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Star) {
					return (Star) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}
	}

	protected class InspectedPlus<SS extends Plus> extends InspectedPolygon<SS> implements Plus {

		protected InspectedPlus(DianaInteractiveViewer<?, ?, ?> controller, SS defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.PLUS;
		}

		@Override
		public SS getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Plus) {
					return (SS) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

		@Override
		public double getRatio() {
			return getPropertyValue(Plus.RATIO);
		}

		@Override
		public void setRatio(double aRatio) {
			setPropertyValue(Plus.RATIO, aRatio);
		}
	}

	protected class InspectedChevron<SS extends Chevron> extends InspectedPolygon<SS> implements Chevron {

		protected InspectedChevron(DianaInteractiveViewer<?, ?, ?> controller, SS defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public ShapeType getShapeType() {
			return ShapeType.CHEVRON;
		}

		@Override
		public SS getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getShapeSpecification() instanceof Chevron) {
					return (SS) ((ShapeNode<?>) node).getShapeSpecification();
				}
			}
			return null;
		}

		@Override
		public double getArrowLength() {
			return getPropertyValue(Chevron.ARROW_LENGTH);
		}

		@Override
		public void setArrowLength(double anArrowLength) {
			setPropertyValue(Chevron.ARROW_LENGTH, anArrowLength);
		}
	}

}