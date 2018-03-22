/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.diana.cp;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.graphics.DianaGraphics;

/**
 * A {@link ControlArea} encodes an interactive area, attached to a DrawingTreeNode, and represented by a single point<br>
 * 
 * @author sylvain
 */
public abstract class ControlPoint extends ControlArea<DianaPoint> {

	private static final Logger logger = Logger.getLogger(ControlPoint.class.getPackage().getName());

	public ControlPoint(DrawingTreeNode<?, ?> node, DianaPoint pt) {
		super(node, pt);
	}

	public DianaPoint getPoint() {
		return getArea();
	}

	public void setPoint(DianaPoint point) {
		setArea(point);
	}

	// @SuppressWarnings("unchecked")
	@Override
	public Rectangle paint(DianaGraphics graphics) {
		if (getNode() == null) {
			logger.warning("Unexpected null node");
			return null;
		}
		// logger.info("paintControlPoint " + getPoint() + "style=" + graphics.getDefaultForeground() + " for " +
		// getGraphicalRepresentation());
		graphics.useDefaultForegroundStyle();
		if (isEmbeddedInComponentHierarchy(graphics)) {
			AffineTransform at = DianaUtils.convertNormalizedCoordinatesAT(getNode(), graphics.getNode());
			return graphics.drawControlPoint(getPoint().transform(at), DianaConstants.CONTROL_POINT_SIZE);
		} else {
			return graphics.drawControlPoint(getPoint(), DianaConstants.CONTROL_POINT_SIZE);
		}

	}

	public boolean isEmbeddedInComponentHierarchy(DianaGraphics graphics) {
		return getNode().isValid();
	}

}
