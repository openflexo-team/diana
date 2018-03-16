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
package org.openflexo.diana.animation.impl;

import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.animation.Transition;
import org.openflexo.diana.geom.FGEPoint;

public class TranslationTransition implements Transition {

	private final ShapeNode<?> shapeNode;
	private final FGEPoint oldLocation;
	private final FGEPoint newLocation;

	public TranslationTransition(ShapeNode<?> shapeNode, FGEPoint oldLocation, FGEPoint newLocation) {
		super();
		this.shapeNode = shapeNode;
		this.oldLocation = oldLocation;
		this.newLocation = newLocation;
	}

	@Override
	public void performStep(int step, int totalSteps) {
		shapeNode.setRelayouting(true);
		shapeNode.setLocation(new FGEPoint(oldLocation.x - (oldLocation.x - newLocation.x) * step / totalSteps,
				oldLocation.y - (oldLocation.y - newLocation.y) * step / totalSteps));
		shapeNode.setRelayouting(false);
	}

	@Override
	public String toString() {
		return "TranslationTransition node=" + shapeNode + " from " + oldLocation + " to " + newLocation;
	}
}
