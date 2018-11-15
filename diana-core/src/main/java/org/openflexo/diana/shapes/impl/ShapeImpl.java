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

package org.openflexo.diana.shapes.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.cp.ShapeResizingControlPoint;
import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaHalfBand;
import org.openflexo.diana.geom.area.DianaHalfLine;
import org.openflexo.diana.graphics.DianaShapeGraphics;
import org.openflexo.diana.shapes.Shape;
import org.openflexo.diana.shapes.ShapeSpecification;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * This class represents a shape as a geometric area in a ShapeNode. This is an instance of {@link ShapeSpecification}. As it, it is
 * attached to a {@link ShapeNode}.
 * 
 * This class is a wrapper of {@link DianaShape} which is the geometrical definition of the object as defined in geometrical framework.<br>
 * A {@link ShapeSpecification} has a geometrical definition inside a normalized rectangle as defined by (0.0,0.0,1.0,1.0)<br>
 * 
 * 
 * @author sylvain
 * 
 */
public class ShapeImpl<SS extends ShapeSpecification> implements PropertyChangeListener, Shape<SS>, Cloneable {
	// private static final DianaModelFactory SHADOW_FACTORY = DianaCoreUtils.TOOLS_FACTORY;

	protected ShapeNode<?> shapeNode;

	private DianaShape<?> shape;
	private DianaShape<?> outline;

	private List<ControlPoint> controlPoints;

	private final SS shapeSpecification;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public ShapeImpl(ShapeNode<?> shapeNode) {
		super();
		this.shapeNode = shapeNode;
		shapeSpecification = (SS) shapeNode.getShapeSpecification();
		controlPoints = new ArrayList<>();
	}

	public void delete() {
		if (getShapeSpecification() != null && getShapeSpecification().getPropertyChangeSupport() != null) {
			getShapeSpecification().getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		shapeNode = null;
		controlPoints.clear();
		controlPoints = null;
	}

	@Override
	public SS getShapeSpecification() {
		/*if (shapeNode != null && shapeNode.getGraphicalRepresentation() != null) {
			return (SS) shapeNode.getGraphicalRepresentation().getShapeSpecification();
		}
		return null;*/
		return shapeSpecification;
	}

	/**
	 * Retrieve all control area used to manage this connector
	 * 
	 * @return
	 */
	@Override
	public List<ControlPoint> getControlAreas() {
		if (shape == null) {
			shape = makeShape();
			updateControlPoints();
		}
		return controlPoints;
	}

	/**
	 * Retrieve all control points used to manage this connector
	 * 
	 * @return
	 */
	@Override
	public List<ControlPoint> getControlPoints() {
		return controlPoints;
	}

	@Override
	public DianaShape<?> getShape() {
		if (shape == null) {
			shape = makeShape();
			updateControlPoints();
		}
		return shape;
	}

	@Override
	public DianaShape<?> getOutline() {
		if (outline == null) {
			outline = makeOutline();
		}
		return outline;
	}

	public void updateShape() {

		if (getShapeSpecification() != null) {
			shape = makeShape();
			outline = makeOutline();
			updateControlPoints();
		}
		shapeNode.notifyShapeChanged();
	}

	/**
	 * Recompute all control points for supplied shape node<br>
	 * Return a newly created list of required control points
	 * 
	 * @param shapeNode
	 * @return
	 */
	public List<ControlPoint> updateControlPoints() {

		controlPoints.clear();

		if (shape != null && shape.getControlPoints() != null) {
			for (DianaPoint pt : shape.getControlPoints()) {
				controlPoints.add(new ShapeResizingControlPoint(shapeNode, pt, null));
			}
		}
		return controlPoints;
	}

	@Override
	public ShapeType getShapeType() {
		return getShapeSpecification().getShapeType();
	}

	protected DianaShape<?> makeShape() {
		if (getShapeSpecification() != null) {
			return getShapeSpecification().makeDianaShape(shapeNode.getBounds());
		}
		return null;
	}

	/**
	 * Return outline for geometric shape of this shape (this is the shape itself, but NOT filled)
	 * 
	 * @return
	 */
	protected final DianaShape<?> makeOutline() {
		DianaShape<?> plainShape = makeShape();
		DianaShape<?> outline = (DianaShape<?>) plainShape.clone();
		outline.setIsFilled(false);
		return outline;
	}

	/**
	 * Return nearest point located on outline, asserting aPoint is related to shape coordinates, and normalized to shape
	 * 
	 * @param aPoint
	 * @return
	 */
	@Override
	public DianaPoint nearestOutlinePoint(DianaPoint aPoint) {
		return getShape().nearestOutlinePoint(aPoint);
	}

	/**
	 * Return flag indicating if position represented is located inside shape, asserting aPoint is related to shape coordinates, and
	 * normalized to shape
	 * 
	 * @param aPoint
	 * @return
	 */
	@Override
	public boolean isPointInsideShape(DianaPoint aPoint) {
		return getShape().containsPoint(aPoint);
	}

	/**
	 * Compute point where supplied line intersects with shape outline trying to minimize distance from "from" point
	 * 
	 * Returns null if no intersection was found
	 * 
	 * @param aLine
	 * @param from
	 * @return
	 */
	@Override
	public final DianaPoint outlineIntersect(DianaLine line, DianaPoint from) {
		DianaArea intersection = getShape().intersect(line);
		return intersection.getNearestPoint(from);
	}

	/**
	 * Compute point where a line formed by current shape's center and "from" point intersects with shape outline trying to minimize
	 * distance from "from" point This implementation provide simplified computation with outer bounds (relative coordinates (0,0)-(1,1))
	 * and must be overriden when required
	 * 
	 * Returns null if no intersection was found
	 * 
	 * @param aLine
	 * @param from
	 * @return
	 */
	@Override
	public final DianaPoint outlineIntersect(DianaPoint from) {
		DianaLine line = new DianaLine(new DianaPoint(0.5, 0.5), from);
		return outlineIntersect(line, from);
	}

	@Override
	public DianaArea getAllowedHorizontalConnectorLocationFromEast() {
		DianaHalfLine north = new DianaHalfLine(1, 0, 2, 0);
		DianaHalfLine south = new DianaHalfLine(1, 1, 2, 1);
		return new DianaHalfBand(north, south);
	}

	/*@Override
	public DianaArea getAllowedHorizontalConnectorLocationFromWest2() {
		double maxY = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		for (ControlPoint cp : getControlPoints()) {
			DianaPoint p = cp.getPoint();
			DianaHalfLine hl = new DianaHalfLine(p.x, p.y, p.x - 1, p.y);
			DianaArea inters = getShape().intersect(hl);
			System.out.println("inters=" + inters);
			if (inters instanceof DianaPoint || inters instanceof DianaEmptyArea) {
				// Consider this point
				if (p.y > maxY) {
					maxY = p.y;
				}
				if (p.y < minY) {
					minY = p.y;
				}
			}
		}
		DianaHalfLine north = new DianaHalfLine(0, minY, -1, minY);
		DianaHalfLine south = new DianaHalfLine(0, maxY, -1, maxY);
		if (north.overlap(south)) {
			System.out.println("Return a " + north.intersect(south));
			return north.intersect(south);
		}
		return new DianaHalfBand(north, south);
	}*/

	@Override
	public DianaArea getAllowedHorizontalConnectorLocationFromWest() {
		DianaHalfLine north = new DianaHalfLine(0, 0, -1, 0);
		DianaHalfLine south = new DianaHalfLine(0, 1, -1, 1);
		return new DianaHalfBand(north, south);
	}

	@Override
	public DianaArea getAllowedVerticalConnectorLocationFromNorth() {
		DianaHalfLine east = new DianaHalfLine(1, 0, 1, -1);
		DianaHalfLine west = new DianaHalfLine(0, 0, 0, -1);
		return new DianaHalfBand(east, west);
	}

	@Override
	public DianaArea getAllowedVerticalConnectorLocationFromSouth() {
		DianaHalfLine east = new DianaHalfLine(1, 1, 1, 2);
		DianaHalfLine west = new DianaHalfLine(0, 1, 0, 2);
		return new DianaHalfBand(east, west);
	}

	// *******************************************************************************
	// * Painting methods *
	// *******************************************************************************

	public void setPaintAttributes(DianaShapeGraphics g) {

		// Background
		if (shapeNode.getIsSelected()) {
			if (shapeNode.getHasSelectedBackgroundStyle()) {
				g.setDefaultBackground(shapeNode.getSelectedBackgroundStyle());
			}
			else if (shapeNode.getHasFocusedBackgroundStyle()) {
				g.setDefaultBackground(shapeNode.getFocusedBackgroundStyle());
			}
			else {
				g.setDefaultBackground(shapeNode.getBackgroundStyle());
			}
		}
		else if (shapeNode.getIsFocused() && shapeNode.getHasFocusedBackgroundStyle()) {
			g.setDefaultBackground(shapeNode.getFocusedBackgroundStyle());
		}
		else {
			g.setDefaultBackground(shapeNode.getBackgroundStyle());
		}

		// Foreground
		if (shapeNode.getIsSelected()) {
			if (shapeNode.getHasSelectedForegroundStyle()) {
				g.setDefaultForeground(shapeNode.getSelectedForegroundStyle());
			}
			else if (shapeNode.getHasFocusedForegroundStyle()) {
				g.setDefaultForeground(shapeNode.getFocusedForegroundStyle());
			}
			else {
				g.setDefaultForeground(shapeNode.getForegroundStyle());
			}
		}
		else if (shapeNode.getIsFocused() && shapeNode.getHasFocusedForegroundStyle()) {
			g.setDefaultForeground(shapeNode.getFocusedForegroundStyle());
		}
		else {
			g.setDefaultForeground(shapeNode.getForegroundStyle());
		}

		// Text
		g.setDefaultTextStyle(shapeNode.getTextStyle());
	}

	/*	@Override
		public final void paintShadow(DianaShapeGraphics g) {
	
			if (g instanceof DianaShapeGraphicsImpl) {
	
				double deep = shapeNode.getGraphicalRepresentation().getShadowStyle().getShadowDepth();
				int blur = shapeNode.getGraphicalRepresentation().getShadowStyle().getShadowBlur();
				double viewWidth = shapeNode.getViewWidth(1.0);
				double viewHeight = shapeNode.getViewHeight(1.0);
				AffineTransform shadowTranslation = AffineTransform.getTranslateInstance(deep / viewWidth, deep / viewHeight);
	
				int darkness = shapeNode.getGraphicalRepresentation().getShadowStyle().getShadowDarkness();
	
				Graphics2D oldGraphics = ((DianaShapeGraphicsImpl) g).cloneGraphics();
	
				Area clipArea = new Area(new java.awt.Rectangle(0, 0, shapeNode.getViewWidth(g.getScale()), shapeNode.getViewHeight(g
						.getScale())));
				Area a = new Area(getShape());
				a.transform(shapeNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
				clipArea.subtract(a);
				((DianaShapeGraphicsImpl) g).getGraphics().clip(clipArea);
	
				Color shadowColor = new Color(darkness, darkness, darkness);
				ForegroundStyle foreground = SHADOW_FACTORY.makeForegroundStyle(shadowColor);
				foreground.setUseTransparency(true);
				foreground.setTransparencyLevel(0.5f);
				BackgroundStyle background = SHADOW_FACTORY.makeColoredBackground(shadowColor);
				background.setUseTransparency(true);
				background.setTransparencyLevel(0.5f);
				g.setDefaultForeground(foreground);
				g.setDefaultBackground(background);
	
				for (int i = blur - 1; i >= 0; i--) {
					float transparency = 0.4f - i * 0.4f / blur;
					foreground.setTransparencyLevel(transparency);
					background.setTransparencyLevel(transparency);
					AffineTransform at = AffineTransform.getScaleInstance((i + 1 + viewWidth) / viewWidth, (i + 1 + viewHeight) / viewHeight);
					at.concatenate(shadowTranslation);
					getShape().transform(at).paint(g);
				}
	
				((DianaShapeGraphicsImpl) g).releaseClonedGraphics(oldGraphics);
			} else {
				logger.warning("Not support DianaGraphics: " + g);
			}
		}*/

	@Override
	public final void paintShape(DianaShapeGraphics g) {
		setPaintAttributes(g);
		getShape().paint(g);
		// drawLabel(g);
	}

	@Override
	public ShapeImpl<SS> clone() {
		try {
			@SuppressWarnings("unchecked")
			ShapeImpl<SS> returned = (ShapeImpl<SS>) super.clone();
			// returned._controlPoints = null;
			// returned.graphicalRepresentation = null;
			// returned.updateShape();
			// returned.rebuildControlPoints();
			return returned;
		} catch (CloneNotSupportedException e) {
			// cannot happen since we are clonable
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean equals(Object object) {
		// TODO
		/*if (object instanceof ShapeSpecificationImpl && getShape() != null) {
			return getShape().equals(((ShapeSpecificationImpl) object).getShape())
					&& areDimensionConstrained() == ((ShapeSpecificationImpl) object).areDimensionConstrained();
		}*/
		return super.equals(object);
	}

	@Override
	public int hashCode() {
		// TODO
		/*if (getShape() != null) {
			return getShape().toString().hashCode();
		}*/
		return super.hashCode();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getShapeSpecification()
				&& !evt.getPropertyName().equals(((HasPropertyChangeSupport) evt.getSource()).getDeletedProperty())) {
			// System.out.println("Received " + evt + " in ShapeImpl propertyName=" + evt.getPropertyName());
			// Those notifications are forwarded by the shape specification
			updateShape();
		}
	}

	public void notifyObjectResized() {
		updateShape();
	}
}
