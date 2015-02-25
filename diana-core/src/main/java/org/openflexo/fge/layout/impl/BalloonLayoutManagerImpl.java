/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-api, a component of the software infrastructure 
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

package org.openflexo.fge.layout.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.geom.FGECircle;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.layout.BalloonLayoutManager;
import org.openflexo.fge.layout.BalloonLayoutManagerSpecification;
import org.openflexo.fib.annotation.FIBPanel;

import edu.uci.ics.jung.algorithms.layout.BalloonLayout;

/**
 * Default implementation for {@link BalloonLayoutManager}
 * 
 * @author sylvain
 * 
 */
@FIBPanel("Fib/Layout/BalloonLayoutManagerPanel.fib")
public abstract class BalloonLayoutManagerImpl<O> extends TreeBasedLayoutManagerImpl<BalloonLayoutManagerSpecification<O>, O> implements
		BalloonLayoutManager<O> {

	private BalloonLayout<ShapeNode<?>, ConnectorNode<?>> layout;

	private List<FGECircle> baseCircles;

	@Override
	protected BalloonLayout<ShapeNode<?>, ConnectorNode<?>> buildLayout() {
		if (baseCircles == null) {
			baseCircles = new ArrayList<FGECircle>();
		} else {
			baseCircles.clear();
		}
		layout = new BalloonLayout<ShapeNode<?>, ConnectorNode<?>>(getForest()) {
			@Override
			protected void setPolars(List<ShapeNode<?>> kids, Point2D parentLocation, double parentRadius) {
				int childCount = kids.size();
				if (childCount == 0)
					return;
				// handle the 1-child case with 0 limit on angle.
				double angle = Math.max(0, Math.PI / 2 * (1 - 2.0 / childCount));
				double childRadius = parentRadius * Math.cos(angle) / (1 + Math.cos(angle));
				double radius = parentRadius - childRadius;

				baseCircles.add(new FGECircle(new FGEPoint(parentLocation), radius, Filling.NOT_FILLED));

				super.setPolars(kids, parentLocation, parentRadius);
			}
		};
		layout.setSize(new Dimension((int) getContainerNode().getWidth(), (int) getContainerNode().getHeight()));
		return layout;
	}

	@Override
	public BalloonLayout<ShapeNode<?>, ConnectorNode<?>> getLayout() {
		return layout;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		/*if (evt.getPropertyName().equals(ForceDirectedGraphLayoutManagerSpecification.STRETCH_KEY)) {
			invalidate();
			doLayout(true);
		} else if (evt.getPropertyName().equals(ForceDirectedGraphLayoutManagerSpecification.REPULSION_RANGE_SQ_KEY)) {
			invalidate();
			doLayout(true);
		} else if (evt.getPropertyName().equals(ForceDirectedGraphLayoutManagerSpecification.FORCE_MULTIPLIER_KEY)) {
			invalidate();
			doLayout(true);
		}*/
	}

	/**
	 * Called to paint decoration
	 * 
	 * @param g
	 */
	@Override
	public void paintDecoration(FGEGraphics g) {

		g.setDefaultForeground(getFactory().makeForegroundStyle(Color.GRAY, 1, DashStyle.DOTS_DASHES));
		g.useDefaultForegroundStyle();

		for (FGECircle circle : baseCircles) {
			circle.paint(g);
		}

		/*	FGECircle circle1 = new FGECircle(getContainerNode().getBounds().getCenter(), 185 * 2 - 60, Filling.NOT_FILLED);

			circle1.paint(g);

			System.out.println("radii=" + getLayout().getRadii());

			for (ShapeNode<?> n : getLayout().getRadii().keySet()) {
				System.out.println("Node " + n.getText() + " radius=" + getLayout().getRadii().get(n));
			}*/

	}

}
