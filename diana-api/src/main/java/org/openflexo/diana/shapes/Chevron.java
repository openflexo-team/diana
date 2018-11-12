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

package org.openflexo.diana.shapes;

import org.openflexo.diana.GRProperty;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

/**
 * Interface for a chevron. The sole property is the ratio of the arrow lengths. To have a correct shape, this ratio has to be strictly
 * between 0 and 0.5.
 * 
 * @author eloubout
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "ChevronShape")
public interface Chevron extends ShapeSpecification {

	// Property Keys
	@PropertyIdentifier(type = Double.class)
	public static final String			ARROW_LENGTH_KEY	= "arrowLength";

	public static GRProperty<Double>	ARROW_LENGTH		= GRProperty.getGRParameter(Chevron.class, ARROW_LENGTH_KEY, Double.class);

	// *******************************************************************************
	// * Properties
	// *******************************************************************************
	@Getter(value = ARROW_LENGTH_KEY, defaultValue = "0.2")
	@XMLAttribute
	public double getArrowLength();

	@Setter(value = ARROW_LENGTH_KEY)
	public void setArrowLength(final double anArrowLength);
}
