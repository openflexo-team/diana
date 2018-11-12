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

package org.openflexo.diana.layout;

import org.openflexo.diana.DianaLayoutManager;
import org.openflexo.diana.DianaLayoutManagerSpecification;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;

/**
 * Abstract specification of a {@link DianaLayoutManager} generally handling graphs with an iterative process<br>
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@Imports({ @Import(ForceDirectedGraphLayoutManagerSpecification.class), @Import(ISOMGraphLayoutManagerSpecification.class) })
public interface GraphBasedLayoutManagerSpecification<LM extends GraphBasedLayoutManager<?, ?>> extends DianaLayoutManagerSpecification<LM> {

	@PropertyIdentifier(type = Integer.class)
	public static final String STEPS_NUMBER_KEY = "stepsNumber";
	@PropertyIdentifier(type = Double.class)
	public static final String LAYOUT_WIDTH_KEY = "layoutWidth";
	@PropertyIdentifier(type = Double.class)
	public static final String LAYOUT_HEIGHT_KEY = "layoutHeight";

	@Getter(value = STEPS_NUMBER_KEY, defaultValue = "30")
	@XMLAttribute
	public int getStepsNumber();

	@Setter(STEPS_NUMBER_KEY)
	public void setStepsNumber(int stepsNumber);

	@Getter(value = LAYOUT_WIDTH_KEY)
	@XMLAttribute
	public abstract Double getLayoutWidth();

	@Setter(value = LAYOUT_WIDTH_KEY)
	public abstract void setLayoutWidth(Double aValue);

	@Getter(value = LAYOUT_HEIGHT_KEY)
	@XMLAttribute
	public abstract Double getLayoutHeight();

	@Setter(value = LAYOUT_HEIGHT_KEY)
	public abstract void setLayoutHeight(Double aValue);

}
