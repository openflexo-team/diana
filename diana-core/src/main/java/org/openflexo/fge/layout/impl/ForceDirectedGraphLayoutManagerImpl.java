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

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.layout.ForceDirectedGraphLayoutManager;
import org.openflexo.fge.layout.ForceDirectedGraphLayoutManagerSpecification;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;

/**
 * Default implementation for {@link ForceDirectedGraphLayoutManager}
 * 
 * @author sylvain
 * 
 */
public abstract class ForceDirectedGraphLayoutManagerImpl<O> extends
		GraphBasedLayoutManagerImpl<ForceDirectedGraphLayoutManagerSpecification, O> implements ForceDirectedGraphLayoutManager<O> {

	@Override
	public double getStretch() {
		return getLayoutManagerSpecification().getStretch();
	}

	@Override
	public int getRepulsionRangeSq() {
		return getLayoutManagerSpecification().getRepulsionRangeSq();
	}

	@Override
	public double getForceMultiplier() {
		return getLayoutManagerSpecification().getForceMultiplier();
	}

	private SpringLayout<ShapeNode<?>, ConnectorNode<?>> layout;

	@Override
	protected AbstractLayout<ShapeNode<?>, ConnectorNode<?>> buildLayout() {
		layout = new SpringLayout<ShapeNode<?>, ConnectorNode<?>>(getGraph());
		layout.setSize(new Dimension((int) getContainerNode().getWidth(), (int) getContainerNode().getHeight()));
		layout.setForceMultiplier(getForceMultiplier());
		layout.setRepulsionRange(getRepulsionRangeSq());
		layout.setStretch(getStretch());
		return layout;
	}

	@Override
	public SpringLayout<ShapeNode<?>, ConnectorNode<?>> getLayout() {
		return layout;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getPropertyName().equals(ForceDirectedGraphLayoutManagerSpecification.STRETCH_KEY)) {
			invalidate();
			doLayout(true);
		} else if (evt.getPropertyName().equals(ForceDirectedGraphLayoutManagerSpecification.REPULSION_RANGE_SQ_KEY)) {
			invalidate();
			doLayout(true);
		} else if (evt.getPropertyName().equals(ForceDirectedGraphLayoutManagerSpecification.FORCE_MULTIPLIER_KEY)) {
			invalidate();
			doLayout(true);
		}
	}

}
