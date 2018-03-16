/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geom, a component of the software infrastructure 
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

package org.openflexo.diana.geom;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FGESteppedDimensionConstraint implements Cloneable {

	private static final Logger LOGGER = Logger.getLogger(FGESteppedDimensionConstraint.class.getPackage().getName());

	private double hStep = 1.0;
	private double vStep = 1.0;

	public FGESteppedDimensionConstraint(double hStep, double vStep) {
		this.hStep = hStep;
		this.vStep = vStep;
	}

	public FGEDimension getNearestDimension(FGEDimension dimension, double minWidth, double maxWidth, double minHeight, double maxHeight) {
		FGEDimension d = dimension.clone();
		if (minWidth > maxWidth) {
			if (LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.warning("Cannot proceed with minWidth>maxWidth: " + minWidth + " " + maxWidth);
			}
			return d;
		}
		if (minHeight > maxHeight) {
			if (LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.warning("Cannot proceed with minHeight>maxHeight: " + minHeight + " " + maxHeight);
			}
			return d;
		}
		if (d.width > maxWidth) {
			d.width = maxWidth;
		}
		if (d.height > maxHeight) {
			d.height = maxHeight;
		}
		if (d.width < minWidth) {
			d.width = minWidth;
		}
		if (d.height < minHeight) {
			d.height = minWidth;
		}
		double lWidth = Math.floor(d.width / hStep) * hStep;
		double uWidth = lWidth + hStep;
		double width = 0.0;
		if (Math.abs(lWidth - d.width) > Math.abs(uWidth - d.width) && uWidth < maxWidth || lWidth < minWidth) {
			width = uWidth;
		} else {
			width = lWidth;
		}
		d.width = width;

		double lHeight = Math.floor(d.height / vStep) * vStep;
		double uHeight = lHeight + vStep;
		double height = 0.0;
		if (Math.abs(lHeight - d.height) > Math.abs(uHeight - d.height) && uHeight < maxHeight || lHeight < minHeight) {
			height = uHeight;
		} else {
			height = lHeight;
		}
		d.height = height;
		return d;
	}

	public double getHorizontalStep() {
		return hStep;
	}

	public double getVerticalStep() {
		return vStep;
	}

	@Override
	public int hashCode() {
		return (Double.valueOf(hStep)).hashCode() + (Double.valueOf(vStep)).hashCode();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof FGESteppedDimensionConstraint) {
			return ((FGESteppedDimensionConstraint) object).hStep == hStep && ((FGESteppedDimensionConstraint) object).vStep == vStep;
		}
		return false;
	}

	@Override
	public FGESteppedDimensionConstraint clone() {
		return new FGESteppedDimensionConstraint(hStep, vStep);
	}

}
