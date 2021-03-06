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

package org.openflexo.diana.swing.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.logging.Logger;

import org.openflexo.diana.BackgroundImageBackgroundStyle;
import org.openflexo.diana.BackgroundStyle;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.graphics.DianaShapeGraphics;
import org.openflexo.diana.impl.DianaCachedModelFactory;
import org.openflexo.diana.swing.view.JShapeView;
import org.openflexo.pamela.exceptions.ModelDefinitionException;

public class JDianaShapeGraphics extends JDianaGraphics implements DianaShapeGraphics {

	private static final Logger logger = Logger.getLogger(JDianaShapeGraphics.class.getPackage().getName());

	private final JDianaShapeDecorationGraphics shapeDecorationGraphics;

	public <O> JDianaShapeGraphics(ShapeNode<O> node, JShapeView<O> view) {
		super(node, view);
		shapeDecorationGraphics = new JDianaShapeDecorationGraphics(node, view);
	}

	@Override
	public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return (ShapeGraphicalRepresentation) super.getGraphicalRepresentation();
	}

	@Override
	public JDianaShapeDecorationGraphics getShapeDecorationGraphics() {
		return shapeDecorationGraphics;
	}

	/**
	 * 
	 * @param graphics2D
	 * @param controller
	 */
	@Override
	public void createGraphics(Graphics2D graphics2D) {

		// System.out.println("******** createGraphics() with " + graphics2D + " for " + getNode().getText());

		super.createGraphics(graphics2D);
		shapeDecorationGraphics.createGraphics(graphics2D);
	}

	@Override
	public void releaseGraphics() {
		super.releaseGraphics();
		shapeDecorationGraphics.releaseGraphics();
	}

	@Override
	protected void applyCurrentBackgroundStyle() {
		super.applyCurrentBackgroundStyle();

		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			/*if (((BackgroundImageBackgroundStyle) getCurrentBackground()).getFitToShape()) {
				BackgroundImageBackgroundStyle bgImage = (BackgroundImageBackgroundStyle) getCurrentBackground();
				bgImage.setDeltaX(0);
				bgImage.setDeltaY(0);
				if (bgImage.getImage() != null) {
					// SGU: Big performance issue here
					// I add to declare new methods without notification because in case of
					// the inspector is shown, an instability is raising: the shape is
					// continuously switching between two values
					// Please investigate
					// bgImage.setScaleXNoNotification(getNode().getWidth() / bgImage.getImage().getWidth(null));
					// bgImage.setScaleYNoNotification(getNode().getHeight() / bgImage.getImage().getHeight(null));
					bgImage.setScaleX(getNode().getWidth() / bgImage.getImage().getWidth(null));
					bgImage.setScaleY(getNode().getHeight() / bgImage.getImage().getHeight(null));
				}
			}*/
		}

	}

	@Override
	public ShapeNode<?> getNode() {
		return (ShapeNode<?>) super.getNode();
	}

	private static DianaCachedModelFactory SHADOW_FACTORY = null;

	static {
		try {
			SHADOW_FACTORY = new DianaCachedModelFactory();
		} catch (ModelDefinitionException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	private boolean isPaintingShadow = false;

	@Override
	public void paintShadow() {

		isPaintingShadow = true;
		double deep = getGraphicalRepresentation().getShadowStyle().getShadowDepth();
		int blur = getGraphicalRepresentation().getShadowStyle().getShadowBlur();
		double viewWidth = getViewWidth(1.0);
		double viewHeight = getViewHeight(1.0);
		AffineTransform shadowTranslation = AffineTransform.getTranslateInstance(deep / viewWidth, deep / viewHeight);

		int darkness = getGraphicalRepresentation().getShadowStyle().getShadowDarkness();

		Graphics2D oldGraphics = cloneGraphics();

		Area clipArea = new Area(
				new java.awt.Rectangle(0, 0, getViewWidth(getController().getScale()), getViewHeight(getController().getScale())));
		Area a = new Area(getNode().getDianaShape());
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
			getNode().getDianaShape().transform(at).paint(this);
		}
		releaseClonedGraphics(oldGraphics);
		isPaintingShadow = false;

	}

	@Override
	public void setDefaultBackgroundStyle(DianaShape<?> shape) {
		if (!isPaintingShadow) {
			super.setDefaultBackgroundStyle(shape);
		}
	}

	@Override
	public void setDefaultForegroundStyle(DianaShape<?> shape) {
		if (!isPaintingShadow) {
			super.setDefaultForegroundStyle(shape);
		}
	}

}
