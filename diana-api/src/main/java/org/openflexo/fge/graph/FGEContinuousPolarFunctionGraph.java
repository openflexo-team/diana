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

package org.openflexo.fge.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openflexo.fge.graphics.FGEShapeGraphics;

/**
 * Represents a polar graph [R=f(A)] where A is a Double and takes its values from -180 to 180
 * 
 * @author sylvain
 * 
 */
public class FGEContinuousPolarFunctionGraph extends FGEPolarFunctionGraph<Double> {

	private static final int DEFAULT_STEPS_NUMBER = 20;

	private Double angleTickSpacing = null;
	private Integer stepsNumber = null;

	private boolean displayAngleTicks = true;
	private boolean displayLabels = true;
	private boolean displayGrid = false;

	public FGEContinuousPolarFunctionGraph() {
		super();
	}

	/**
	 * Return the number of steps beeing drawn for continuous function (sampling for float or double value)<br>
	 * 
	 * @return
	 */
	public int getStepsNumber() {
		if (stepsNumber == null) {
			return DEFAULT_STEPS_NUMBER;
		}
		return stepsNumber;
	}

	/**
	 * Sets the number of steps beeing drawn for continuous function (sampling for float or double value)<br>
	 * 
	 * @param stepNumber
	 */
	public void setStepsNumber(Integer stepsNumber) {
		if ((stepsNumber == null && this.stepsNumber != null) || (stepsNumber != null && !stepsNumber.equals(this.stepsNumber))) {
			Integer oldValue = this.stepsNumber;
			this.stepsNumber = stepsNumber;
			getPropertyChangeSupport().firePropertyChange("stepsNumber", oldValue, stepsNumber);
		}
	}

	@Override
	public Double getNormalizedAngle(Double angle) {
		return angle;
	}

	@Override
	public Double getNormalizedAngleExtent(Double parameterValue) {
		return 360.0 / getStepsNumber();
	}

	/**
	 * This method returns the minor tick spacing. The number that is returned represents the distance, measured in values, between each
	 * minor tick mark.
	 * 
	 * @return the number of values between minor ticks
	 */
	public Double getAngleTickSpacing() {
		return angleTickSpacing;
	}

	/**
	 * This method sets the minor tick spacing. The number that is passed in represents the distance, measured in values, between each minor
	 * tick mark.
	 * 
	 */
	public void setAngleTickSpacing(Double angleTickSpacing) {
		if ((angleTickSpacing == null && this.angleTickSpacing != null)
				|| (angleTickSpacing != null && !angleTickSpacing.equals(this.angleTickSpacing))) {
			Double oldValue = this.angleTickSpacing;
			this.angleTickSpacing = angleTickSpacing;
			getPropertyChangeSupport().firePropertyChange("angleTickSpacing", oldValue, angleTickSpacing);
		}
	}

	@Override
	protected Iterator<Double> iterateParameter() {

		List<Double> returned = new ArrayList<Double>();

		if (getStepsNumber() > -1) {
			for (int i = 0; i < getStepsNumber() + 1; i++) {
				returned.add(i * 360.0 / getStepsNumber());
			}
		}
		return returned.iterator();
	}

	@Override
	public void paintParameters(FGEShapeGraphics g) {

		// System.out.println("Major tick spacing = " + getParameterMajorTickSpacing());
		// System.out.println("Minor tick spacing = " + getParameterMinorTickSpacing());

		// g.setDefaultTextStyle(aTextStyle);

	}

	public boolean getDisplayAngleTicks() {
		return displayAngleTicks;
	}

	public void setDisplayAngleTicks(boolean displayAngleTicks) {
		if (displayAngleTicks != this.displayAngleTicks) {
			this.displayAngleTicks = displayAngleTicks;
			getPropertyChangeSupport().firePropertyChange("displayAngleTicks", !displayAngleTicks, displayAngleTicks);
		}
	}

	public boolean getDisplayLabels() {
		return displayLabels;
	}

	public void setDisplayLabels(boolean displayLabels) {
		if (displayLabels != this.displayLabels) {
			this.displayLabels = displayLabels;
			getPropertyChangeSupport().firePropertyChange("displayLabels", !displayLabels, displayLabels);
		}
	}

	public boolean getDisplayGrid() {
		return displayGrid;
	}

	public void setDisplayGrid(boolean displayGrid) {
		if (displayGrid != this.displayGrid) {
			this.displayGrid = displayGrid;
			getPropertyChangeSupport().firePropertyChange("displayGrid", !displayGrid, displayGrid);
		}
	}

}
