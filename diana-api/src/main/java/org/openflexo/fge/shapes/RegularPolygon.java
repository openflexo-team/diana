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

package org.openflexo.fge.shapes;

import org.openflexo.fge.GRProperty;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a regular polygon, with more than 3 points
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 */
@ModelEntity
@XMLElement(xmlTag = "PolygonShape")
public interface RegularPolygon extends Polygon {

	// Property keys

	@PropertyIdentifier(type = Integer.class)
	public static final String N_POINTS_KEY = "nPoints";
	@PropertyIdentifier(type = Integer.class)
	public static final String START_ANGLE_KEY = "startAngle";

	public static GRProperty<Integer> N_POINTS = GRProperty.getGRParameter(RegularPolygon.class, N_POINTS_KEY, Integer.class);
	public static GRProperty<Integer> START_ANGLE = GRProperty.getGRParameter(RegularPolygon.class, START_ANGLE_KEY, Integer.class);

	/*public static enum RegularPolygonParameters implements GRProperty {
		nPoints, startAngle;
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = N_POINTS_KEY, defaultValue = "5")
	@XMLAttribute
	public int getNPoints();

	@Setter(value = N_POINTS_KEY)
	public void setNPoints(int pointsNb);

	@Getter(value = START_ANGLE_KEY, defaultValue = "90")
	@XMLAttribute
	public int getStartAngle();

	@Setter(value = START_ANGLE_KEY)
	public void setStartAngle(int anAngle);

}
