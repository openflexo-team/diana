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

package org.openflexo.diana.layout.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Set;

import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.ForegroundStyle.DashStyle;
import org.openflexo.diana.geom.FGECircle;
import org.openflexo.diana.geom.FGEGeometricObject.Filling;
import org.openflexo.diana.graphics.FGEGraphics;
import org.openflexo.diana.layout.BalloonLayoutManager;
import org.openflexo.diana.layout.RadialTreeLayoutManager;
import org.openflexo.diana.layout.RadialTreeLayoutManagerSpecification;
import org.openflexo.gina.annotation.FIBPanel;

import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;

/**
 * Default implementation for {@link BalloonLayoutManager}
 * 
 * @author sylvain
 * 
 */
@FIBPanel("Fib/Layout/RadialTreeLayoutManagerPanel.fib")
public abstract class RadialTreeLayoutManagerImpl<O> extends TreeBasedLayoutManagerImpl<RadialTreeLayoutManagerSpecification<O>, O>
		implements RadialTreeLayoutManager<O> {

	private RadialTreeLayout<ShapeNode<?>, ConnectorNode<?>> layout;

	@Override
	protected RadialTreeLayout<ShapeNode<?>, ConnectorNode<?>> buildLayout() {
		layout = new RadialTreeLayout<>(getForest());
		layout.setSize(new Dimension((int) getContainerNode().getWidth(), (int) getContainerNode().getHeight()));
		return layout;
	}

	@Override
	public RadialTreeLayout<ShapeNode<?>, ConnectorNode<?>> getLayout() {
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

		Set<Double> radiuses = new HashSet<>();

		for (ShapeNode<?> n : getGraph().getVertices()) {
			PolarPoint pp = getLayout().getPolarLocations().get(n);
			radiuses.add(pp.getRadius());
		}

		for (Double radius : radiuses) {
			FGECircle circle = new FGECircle(getContainerNode().getBounds().getCenter(), radius, Filling.NOT_FILLED);
			circle.paint(g);
		}
	}
}
