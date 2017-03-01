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

import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.FGEShapeGraphics;

/**
 * Represents a 2D-base graph, where a coordinates is defined on a continuous number range defined with min and max value<br>
 * Sampling is defined by stepNumber property
 * 
 * @author sylvain
 * 
 * @param <X>
 *            the represented type
 */
public class FGEContinuousSimpleFunctionGraph<X extends Number> extends FGESimpleFunctionGraph<X> {

	private final Class<X> numberClass;

	private X minValue = null;
	private X maxValue = null;
	private X minorTickSpacing = null;
	private X majorTickSpacing = null;
	private Integer stepsNumber = null;
	private boolean displayMajorTicks = true;
	private boolean displayMinorTicks = false;
	private boolean displayReferenceMarks = true;
	private boolean displayLabels = true;
	private boolean displayGrid = false;

	public FGEContinuousSimpleFunctionGraph(Class<X> numberClass) {
		super();
		this.numberClass = numberClass;
	}

	/**
	 * Return the number of steps beeing drawn for continuous function (sampling for float or double value)<br>
	 * Spacing between each sample is then obtained by (maxValue - minValue)/stepNumber<br>
	 * Default value is 20 for floating types, -1 for other numbers<br>
	 * Note that Integer/Long/Short/Byte types are iterated by unit increment, and do not use this parameter by default
	 * 
	 * @return
	 */
	public int getStepsNumber() {
		if (stepsNumber == null) {
			if (isFloatingType()) {
				return 20;
			}
			else {
				return -1;
			}
		}
		return stepsNumber;
	}

	/**
	 * Sets the number of steps beeing drawn for continuous function (sampling for float or double value)<br>
	 * Spacing between each sample is then obtained by (maxValue - minValue)/stepNumber<br>
	 * Default value is 20 for floating types, -1 for other numbers<br>
	 * Note that Integer/Long/Short/Byte types are iterated by unit increment, and do not use this parameter by default
	 * 
	 * @param stepNumber
	 */
	public void setStepsNumber(int stepsNumber) {
		this.stepsNumber = stepsNumber;
	}

	/**
	 * Sets range for parameter<br>
	 * This range configure the drawing of continuous function (sampling)
	 * 
	 * @param minValue
	 * @param maxValue
	 */
	public void setParameterRange(X minValue, X maxValue) {
		setParameterMinValue(minValue);
		setParameterMaxValue(maxValue);
	}

	/**
	 * Internal method used to retrieve min value of right type for each {@link Number} class
	 * 
	 * @return
	 */
	private X getDefaultMinValue() {
		if (getNumberClass().equals(Double.class)) {
			return (X) (Double) Double.MIN_VALUE;
		}
		else if (getNumberClass().equals(Float.class)) {
			return (X) (Float) Float.MIN_VALUE;
		}
		else if (getNumberClass().equals(Long.class)) {
			return (X) (Long) Long.MIN_VALUE;
		}
		else if (getNumberClass().equals(Integer.class)) {
			return (X) (Integer) Integer.MIN_VALUE;
		}
		else if (getNumberClass().equals(Short.class)) {
			return (X) (Short) Short.MIN_VALUE;
		}
		else if (getNumberClass().equals(Byte.class)) {
			return (X) (Byte) Byte.MIN_VALUE;
		}
		return null;
	}

	/**
	 * Internal method used to retrieve max value of right type for each {@link Number} class
	 * 
	 * @return
	 */
	private X getDefaultMaxValue() {
		if (getNumberClass().equals(Double.class)) {
			return (X) (Double) Double.MAX_VALUE;
		}
		else if (getNumberClass().equals(Float.class)) {
			return (X) (Float) Float.MAX_VALUE;
		}
		else if (getNumberClass().equals(Long.class)) {
			return (X) (Long) Long.MAX_VALUE;
		}
		else if (getNumberClass().equals(Integer.class)) {
			return (X) (Integer) Integer.MAX_VALUE;
		}
		else if (getNumberClass().equals(Short.class)) {
			return (X) (Short) Short.MAX_VALUE;
		}
		else if (getNumberClass().equals(Byte.class)) {
			return (X) (Byte) Byte.MAX_VALUE;
		}
		return null;
	}

	public X getParameterMinValue() {
		if (minValue == null) {
			return getDefaultMinValue();
		}
		return minValue;
	}

	public void setParameterMinValue(X minValue) {
		this.minValue = minValue;
	}

	public X getParameterMaxValue() {
		if (maxValue == null) {
			return getDefaultMaxValue();
		}
		return maxValue;
	}

	public void setParameterMaxValue(X maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * This method returns the major tick spacing. The number that is returned represents the distance, measured in values, between each
	 * major tick mark.
	 * 
	 * @return the number of values between minor ticks
	 */
	public X getParameterMajorTickSpacing() {

		/*if (majorTickSpacing == null) {
			System.out.println("min=" + getParameterMinValue());
			System.out.println("max=" + getParameterMaxValue());
		}*/
		return majorTickSpacing;
	}

	/**
	 * This method sets the minor tick spacing. The number that is passed in represents the distance, measured in values, between each major
	 * tick mark.
	 * 
	 */
	public void setParameterMajorTickSpacing(X majorTickSpacing) {
		this.majorTickSpacing = majorTickSpacing;
	}

	/**
	 * This method returns the minor tick spacing. The number that is returned represents the distance, measured in values, between each
	 * minor tick mark.
	 * 
	 * @return the number of values between minor ticks
	 */
	public X getParameterMinorTickSpacing() {
		return minorTickSpacing;
	}

	/**
	 * This method sets the minor tick spacing. The number that is passed in represents the distance, measured in values, between each minor
	 * tick mark.
	 * 
	 */
	public void setParameterMinorTickSpacing(X minorTickSpacing) {
		this.minorTickSpacing = minorTickSpacing;
	}

	protected Class<X> getNumberClass() {
		return numberClass;
	}

	protected boolean isFloatingType() {
		return getNumberClass().equals(Double.class) || getNumberClass().equals(Float.class);
	}

	@Override
	protected Iterator<X> iterateParameter() {

		List<X> returned = new ArrayList<>();

		// System.out.println("On itere pour FGEContinuousSimpleFunctionGraph");
		// System.out.println("stepsNumber=" + getStepsNumber());
		// System.out.println("numberClass=" + getNumberClass());

		if (getStepsNumber() > -1) {
			for (int i = 0; i < getStepsNumber() + 1; i++) {
				X value = null;
				if (getNumberClass().equals(Double.class)) {
					value = (X) (new Double(getParameterMinValue().doubleValue()
							+ ((getParameterMaxValue().doubleValue() - getParameterMinValue().doubleValue()) * i / getStepsNumber())));
				}
				else if (getNumberClass().equals(Float.class)) {
					value = (X) (new Float(getParameterMinValue().floatValue()
							+ ((getParameterMaxValue().floatValue() - getParameterMinValue().floatValue()) * i / getStepsNumber())));
				}
				else if (getNumberClass().equals(Long.class)) {
					value = (X) (new Long(getParameterMinValue().longValue()
							+ ((getParameterMaxValue().longValue() - getParameterMinValue().longValue()) * i / getStepsNumber())));
				}
				else if (getNumberClass().equals(Integer.class)) {
					value = (X) (new Integer(getParameterMinValue().intValue()
							+ ((getParameterMaxValue().intValue() - getParameterMinValue().intValue()) * i / getStepsNumber())));
				}
				else if (getNumberClass().equals(Short.class)) {
					value = (X) (new Short((short) (getParameterMinValue().shortValue()
							+ ((getParameterMaxValue().shortValue() - getParameterMinValue().shortValue()) * i / getStepsNumber()))));
				}
				else if (getNumberClass().equals(Byte.class)) {
					value = (X) (new Byte((byte) (getParameterMinValue().byteValue()
							+ ((getParameterMaxValue().byteValue() - getParameterMinValue().byteValue()) * i / getStepsNumber()))));
				}
				else {
					value = (X) (new Double(getParameterMinValue().doubleValue()
							+ ((getParameterMaxValue().doubleValue() - getParameterMinValue().doubleValue()) * i / getStepsNumber())));
				}
				returned.add(value);
			}
		}
		else {
			// Type can be only long/int/short/byte
			if (getNumberClass().equals(Long.class)) {
				for (long i = getParameterMinValue().longValue(); i <= getParameterMaxValue().longValue(); i++) {
					returned.add((X) (Long) i);
				}
			}
			else if (getNumberClass().equals(Integer.class)) {
				for (int i = getParameterMinValue().intValue(); i <= getParameterMaxValue().intValue(); i++) {
					returned.add((X) (Integer) i);
				}
			}
			else if (getNumberClass().equals(Short.class)) {
				for (short i = getParameterMinValue().shortValue(); i <= getParameterMaxValue().shortValue(); i++) {
					returned.add((X) (Short) i);
				}
			}
			else if (getNumberClass().equals(Byte.class)) {
				for (byte i = getParameterMinValue().byteValue(); i <= getParameterMaxValue().byteValue(); i++) {
					returned.add((X) (Byte) i);
				}
			}
		}
		return returned.iterator();
	}

	@Override
	protected Double getNormalizedPosition(X value) {
		/*if (getNumberClass().equals(Double.class)) {
			return (value.doubleValue() - getParameterMinValue().doubleValue())
					/ (getParameterMaxValue().doubleValue() - getParameterMinValue().doubleValue());
		}
		else if (getNumberClass().equals(Float.class)) {
			return (double) (value.floatValue() - getParameterMinValue().floatValue())
					/ (getParameterMaxValue().floatValue() - getParameterMinValue().floatValue());
		}
		else if (getNumberClass().equals(Long.class)) {
			return (double) (value.longValue() - getParameterMinValue().longValue())
					/ (getParameterMaxValue().longValue() - getParameterMinValue().longValue());
		}
		else if (getNumberClass().equals(Integer.class)) {
			return (double) (value.intValue() - getParameterMinValue().intValue())
					/ (getParameterMaxValue().intValue() - getParameterMinValue().intValue());
		}
		else if (getNumberClass().equals(Short.class)) {
			return (double) (value.shortValue() - getParameterMinValue().shortValue())
					/ (getParameterMaxValue().shortValue() - getParameterMinValue().shortValue());
		}
		else if (getNumberClass().equals(Byte.class)) {
			return (double) (value.byteValue() - getParameterMinValue().byteValue())
					/ (getParameterMaxValue().byteValue() - getParameterMinValue().byteValue());
		}*/
		return (value.doubleValue() - getParameterMinValue().doubleValue())
				/ (getParameterMaxValue().doubleValue() - getParameterMinValue().doubleValue());
	}

	@Override
	public void paintParameters(FGEShapeGraphics g) {

		if (getParameterOrientation() == Orientation.HORIZONTAL) {
			// System.out.println("Major tick spacing = " + getParameterMajorTickSpacing());
			// System.out.println("Minor tick spacing = " + getParameterMinorTickSpacing());

			double y0 = getNormalizedPosition((X) new Double(0));

			if (getDisplayMinorTicks()) {
				double minMTS = ((int) getParameterMinValue().doubleValue() / getParameterMinorTickSpacing().doubleValue())
						* getParameterMinorTickSpacing().doubleValue();
				double maxMTS = ((int) getParameterMaxValue().doubleValue() / getParameterMinorTickSpacing().doubleValue())
						* getParameterMinorTickSpacing().doubleValue();

				for (double ts = minMTS; ts <= maxMTS; ts += getParameterMinorTickSpacing().doubleValue()) {
					double nts = getNormalizedPosition((X) new Double(ts));
					// System.out.println("ts: on " + ts + " on nts=" + nts);
					g.drawLine(new FGEPoint(nts, 0.995), new FGEPoint(nts, 1.005));
				}
			}

			if (getDisplayMajorTicks() && getParameterMajorTickSpacing() != null) {
				double minMTS = ((int) getParameterMinValue().doubleValue() / getParameterMajorTickSpacing().doubleValue())
						* getParameterMajorTickSpacing().doubleValue();
				double maxMTS = ((int) getParameterMaxValue().doubleValue() / getParameterMajorTickSpacing().doubleValue())
						* getParameterMajorTickSpacing().doubleValue();

				for (double ts = minMTS; ts <= maxMTS; ts += getParameterMajorTickSpacing().doubleValue()) {
					double nts = getNormalizedPosition((X) new Double(ts));
					// System.out.println("ts: on " + ts + " on nts=" + nts);
					g.drawLine(new FGEPoint(nts, 0.99), new FGEPoint(nts, 1.01));
				}
			}

			if (getDisplayLabels() && getParameterMajorTickSpacing() != null) {
				double minMTS = ((int) getParameterMinValue().doubleValue() / getParameterMajorTickSpacing().doubleValue())
						* getParameterMajorTickSpacing().doubleValue();
				double maxMTS = ((int) getParameterMaxValue().doubleValue() / getParameterMajorTickSpacing().doubleValue())
						* getParameterMajorTickSpacing().doubleValue();
				g.useTextStyle(g.getFactory().makeTextStyle(g.getCurrentForeground().getColor().darker(),
						g.getCurrentTextStyle().getFont().deriveFont(8.0f)));
				for (double ts = minMTS; ts <= maxMTS; ts += getParameterMajorTickSpacing().doubleValue()) {
					double nts = getNormalizedPosition((X) new Double(ts));
					g.drawString("" + ts, new FGEPoint(nts, 0.99), HorizontalTextAlignment.CENTER);
				}

			}
		}

		// System.out.println("Major tick spacing = " + getParameterMajorTickSpacing());
		// System.out.println("Minor tick spacing = " + getParameterMinorTickSpacing());

		// g.setDefaultTextStyle(aTextStyle);

	}

	@Override
	public void paint(FGEShapeGraphics g) {

		/*FGELine horizontalCoordinates = new FGELine(0, 0.5, 1, 0.5);
		FGELine verticalCoordinates = new FGELine(0.5, 0, 0.5, 1);
		
		// g.setDefaultForeground(get);
		horizontalCoordinates.paint(g);
		verticalCoordinates.paint(g);*/

		super.paint(g);
	}

	public boolean getDisplayMajorTicks() {
		return displayMajorTicks;
	}

	public void setDisplayMajorTicks(boolean displayMajorTicks) {
		if (displayMajorTicks != this.displayMajorTicks) {
			this.displayMajorTicks = displayMajorTicks;
			getPropertyChangeSupport().firePropertyChange("displayMajorTicks", !displayMajorTicks, displayMajorTicks);
		}
	}

	public boolean getDisplayMinorTicks() {
		return displayMinorTicks;
	}

	public void setDisplayMinorTicks(boolean displayMinorTicks) {
		if (displayMinorTicks != this.displayMinorTicks) {
			this.displayMinorTicks = displayMinorTicks;
			getPropertyChangeSupport().firePropertyChange("displayMinorTicks", !displayMinorTicks, displayMinorTicks);
		}
	}

	public boolean getDisplayReferenceMarks() {
		return displayReferenceMarks;
	}

	public void setDisplayReferenceMarks(boolean displayReferenceMarks) {
		if (displayReferenceMarks != this.displayReferenceMarks) {
			this.displayReferenceMarks = displayReferenceMarks;
			getPropertyChangeSupport().firePropertyChange("displayReferenceMarks", !displayReferenceMarks, displayReferenceMarks);
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
