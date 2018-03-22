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

package org.openflexo.diana.shapes;

import org.openflexo.diana.GRProperty;
import org.openflexo.diana.geom.DianaArc.ArcType;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents an Arc, as defined by an arc type, an angle start, and an angle extent
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 */
@ModelEntity
@XMLElement(xmlTag = "ArcShape")
public interface Arc extends ShapeSpecification {

	// Property keys

	@PropertyIdentifier(type = Integer.class)
	public static final String ANGLE_EXTENT_KEY = "angleExtent";
	@PropertyIdentifier(type = Integer.class)
	public static final String ANGLE_START_KEY = "angleStart";
	@PropertyIdentifier(type = ArcType.class)
	public static final String ARC_TYPE_KEY = "arcType";

	public static GRProperty<Integer> ANGLE_EXTENT = GRProperty.getGRParameter(Arc.class, ANGLE_EXTENT_KEY, Integer.class);
	public static GRProperty<Integer> ANGLE_START = GRProperty.getGRParameter(Arc.class, ANGLE_START_KEY, Integer.class);
	public static GRProperty<ArcType> ARC_TYPE = GRProperty.getGRParameter(Arc.class, ARC_TYPE_KEY, ArcType.class);

	/*public static enum ArcParameters implements GRProperty {
		angleExtent, angleStart, arcType;
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = ANGLE_START_KEY, defaultValue = "0")
	@XMLAttribute
	public int getAngleStart();

	@Setter(value = ANGLE_START_KEY)
	public void setAngleStart(int anAngle);

	@Getter(value = ANGLE_EXTENT_KEY, defaultValue = "90")
	@XMLAttribute
	public int getAngleExtent();

	@Setter(value = ANGLE_EXTENT_KEY)
	public void setAngleExtent(int anAngle);

	@Getter(value = ARC_TYPE_KEY)
	@XMLAttribute
	public ArcType getArcType();

	@Setter(value = ARC_TYPE_KEY)
	public void setArcType(ArcType anArcType);

}
