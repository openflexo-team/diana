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

package org.openflexo.fge.swing.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundImageBackgroundStyle;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.impl.FGECachedModelFactory;
import org.openflexo.fge.swing.view.JShapeView;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class JFGEShapeGraphics extends JFGEGraphics implements FGEShapeGraphics {

	private static final Logger logger = Logger.getLogger(JFGEShapeGraphics.class.getPackage().getName());

	private final JFGEShapeDecorationGraphics shapeDecorationGraphics;

	public <O> JFGEShapeGraphics(ShapeNode<O> node, JShapeView<O> view) {
		super(node, view);
		shapeDecorationGraphics = new JFGEShapeDecorationGraphics(node, view);
	}

	@Override
	public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return (ShapeGraphicalRepresentation) super.getGraphicalRepresentation();
	}

	@Override
	public JFGEShapeDecorationGraphics getShapeDecorationGraphics() {
		return shapeDecorationGraphics;
	}

	/**
	 * 
	 * @param graphics2D
	 * @param controller
	 */
	@Override
	public void createGraphics(Graphics2D graphics2D) {

		System.out.println("******** createGraphics() with " + graphics2D + " for " + getNode().getText());

		super.createGraphics(graphics2D);
		shapeDecorationGraphics.createGraphics(graphics2D);
		/*if (getNode().getParentNode() instanceof ShapeNode) {
			graphics2D.translate(((ShapeNode) getNode().getParentNode()).getBorderLeft(),
					((ShapeNode) getNode().getParentNode()).getBorderTop());
		}*/

		/*g2d.translate(getNode().getBorderLeft(), getNode().getBorderTop());
		
		if (getNode().getParentNode() instanceof ShapeNode) {
			g2d.translate(((ShapeNode) getNode().getParentNode()).getBorderLeft(), ((ShapeNode) getNode().getParentNode()).getBorderTop());
		}*/
	}

	@Override
	public Graphics2D cloneGraphics() {

		System.out.println("******** cloneGraphics() for " + getNode().getText());

		return super.cloneGraphics();
	}

	@Override
	public void releaseGraphics() {
		super.releaseGraphics();
		shapeDecorationGraphics.releaseGraphics();
	}

	@Override
	protected void applyCurrentBackgroundStyle() {
		super.applyCurrentBackgroundStyle();

		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle
				&& ((BackgroundImageBackgroundStyle) getCurrentBackground()).getFitToShape()) {
			BackgroundImageBackgroundStyle bgImage = (BackgroundImageBackgroundStyle) getCurrentBackground();
			bgImage.setDeltaX(0);
			bgImage.setDeltaY(0);
			if (bgImage.getImage() != null) {
				// SGU: Big performance issue here
				// I add to declare new methods without notification because in case of
				// the inspector is shown, an instability is raising: the shape is
				// continuously switching between two values
				// Please investigate
				bgImage.setScaleXNoNotification(getNode().getWidth() / bgImage.getImage().getWidth(null));
				bgImage.setScaleYNoNotification(getNode().getHeight() / bgImage.getImage().getHeight(null));
			}
		}

	}

	@Override
	public ShapeNode<?> getNode() {
		return (ShapeNode<?>) super.getNode();
	}

	private static FGECachedModelFactory SHADOW_FACTORY = null;

	static {
		try {
			SHADOW_FACTORY = new FGECachedModelFactory();
		} catch (ModelDefinitionException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void paintShadow() {

		double deep = getGraphicalRepresentation().getShadowStyle().getShadowDepth();
		int blur = getGraphicalRepresentation().getShadowStyle().getShadowBlur();
		double viewWidth = getViewWidth(1.0);
		double viewHeight = getViewHeight(1.0);
		AffineTransform shadowTranslation = AffineTransform.getTranslateInstance(deep / viewWidth, deep / viewHeight);

		int darkness = getGraphicalRepresentation().getShadowStyle().getShadowDarkness();

		Graphics2D oldGraphics = cloneGraphics();

		Area clipArea = new Area(
				new java.awt.Rectangle(0, 0, getViewWidth(getController().getScale()), getViewHeight(getController().getScale())));
		Area a = new Area(getNode().getFGEShape());
		a.transform(getNode().convertNormalizedPointToViewCoordinatesAT(getController().getScale()));
		clipArea.subtract(a);
		getGraphics().clip(clipArea);

		Color shadowColor = new Color(darkness, darkness, darkness);
		ForegroundStyle foreground = SHADOW_FACTORY.makeForegroundStyle(shadowColor);
		foreground.setUseTransparency(true);
		foreground.setTransparencyLevel(0.5f);
		BackgroundStyle background = SHADOW_FACTORY.makeColoredBackground(shadowColor);
		background.setUseTransparency(true);
		background.setTransparencyLevel(0.5f);
		setDefaultForeground(foreground);
		setDefaultBackground(background);

		for (int i = blur - 1; i >= 0; i--) {
			float transparency = 0.4f - i * 0.4f / blur;
			foreground.setTransparencyLevel(transparency);
			background.setTransparencyLevel(transparency);
			AffineTransform at = AffineTransform.getScaleInstance((i + 1 + viewWidth) / viewWidth, (i + 1 + viewHeight) / viewHeight);
			at.concatenate(shadowTranslation);
			getNode().getFGEShape().transform(at).paint(this);
		}
		releaseClonedGraphics(oldGraphics);

	}

	/*@Override
	public Point convertNormalizedPointToViewCoordinates(double x, double y, double scale) {
		Point returned = getNode().convertNormalizedPointToViewCoordinates(x, y, scale);
		System.out.println("1. return " + returned);
		returned.x += getNode().getBorderLeft();
		returned.y += getNode().getBorderTop();
		System.out.println("2. return " + returned);
	
		return returned;
	}*/

	/*@Override
	public FGEPoint convertViewCoordinatesToNormalizedPoint(Point p, double scale) {
		Point p2 = new Point(p);
		p2.x -= getNode().getBorderLeft();
		p2.y -= getNode().getBorderTop();
		return getNode().convertViewCoordinatesToNormalizedPoint(p2, scale);
	}*/

	/*@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		AffineTransform returned = getNode().convertNormalizedPointToViewCoordinatesAT(scale);
		returned.concatenate(AffineTransform.getTranslateInstance(getNode().getBorderLeft(), getNode().getBorderTop()));
		return returned;
	}
	
	@Override
	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale) {
		AffineTransform returned = getNode().convertViewCoordinatesToNormalizedPointAT(scale);
		returned.preConcatenate(AffineTransform.getTranslateInstance(-getNode().getBorderLeft(), -getNode().getBorderTop()));
		return returned;
	}*/

	/*@Override
	public FGEPoint convertViewCoordinatesToNormalizedPoint(Point p, double scale) {
		FGEPoint returned = getNode().convertViewCoordinatesToNormalizedPoint(p, scale);
		returned.x -= getNode().getBorderLeft();
		returned.y -= getNode().getBorderTop();
		return returned;
	}
	
	@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		AffineTransform returned = getNode().convertNormalizedPointToViewCoordinatesAT(scale);
		returned.concatenate(AffineTransform.getTranslateInstance(getNode().getBorderLeft(), getNode().getBorderTop()));
		return returned;
	}
	
	@Override
	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale) {
		AffineTransform returned = getNode().convertViewCoordinatesToNormalizedPointAT(scale);
		returned.preConcatenate(AffineTransform.getTranslateInstance(-getNode().getBorderLeft(), -getNode().getBorderTop()));
		return returned;
	}*/

	/*@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		AffineTransform returned = getNode().convertNormalizedPointToViewCoordinatesAT(scale);
		returned.concatenate(AffineTransform.getTranslateInstance(getNode().getBorderLeft(), getNode().getBorderTop()));
		return returned;
	}*/

	/*@Override
	public void drawRoundRect(FGEPoint p, FGEDimension d, double arcwidth, double archeight) {
		// g2d.translate(getNode().getBorderLeft(), getNode().getBorderTop());
		// System.out.println("1- On peint un round rect origin=" + p + " dimension=" + d);
		super.drawRoundRect(p, d, arcwidth, archeight);
	}*/

	/*@Override
	public void drawRect(double x, double y, double width, double height) {
		// g2d.translate(getNode().getBorderLeft(), getNode().getBorderTop());
		super.drawRect(x, y, width, height);
	}*/

	/*@Override
	public void fillRoundRect(FGEPoint p, FGEDimension d, double arcwidth, double archeight) {
		// g2d.translate(getNode().getBorderLeft(), getNode().getBorderTop());
		// System.out.println("1- On fill un round rect origin=" + p + " dimension=" + d);
		super.fillRoundRect(p, d, arcwidth, archeight);
	}*/

	/*@Override
	public void fillRoundRect(double x, double y, double width, double height, double arcwidth, double archeight) {
		// g2d.translate(getNode().getBorderLeft(), getNode().getBorderTop());
		if (!drawShadow) {
			System.out.println(
					"--------------------- > 2- On fill un round rect origin=" + x + "," + y + " dimension=" + width + "x" + height);
			System.out.println("size: " + getNode().getWidth() + "x" + getNode().getHeight());
			System.out.println("border left=" + getNode().getBorderLeft() + " top=" + getNode().getBorderTop());
			Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
			System.out.println("rectangle d'arrivee: " + r);
		}
		super.fillRoundRect(x, y, width, height, arcwidth, archeight);
	}*/

}
