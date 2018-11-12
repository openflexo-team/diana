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

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

/**
 * Represents the specification of a ForceDirectedGraphLayoutManager in DIANA<br>
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement
@Imports({ @Import(ForceDirectedGraphLayoutManager.class) })
public interface ForceDirectedGraphLayoutManagerSpecification extends
		GraphBasedLayoutManagerSpecification<ForceDirectedGraphLayoutManager<?>> {

	@PropertyIdentifier(type = Double.class)
	public static final String STRETCH_KEY = "stretch";
	@PropertyIdentifier(type = Integer.class)
	public static final String REPULSION_RANGE_SQ_KEY = "repulsionRangeSq";
	@PropertyIdentifier(type = Double.class)
	public static final String FORCE_MULTIPLIER_KEY = "forceMultiplier";

	@Getter(value = STRETCH_KEY, defaultValue = "0.70")
	@XMLAttribute
	public double getStretch();

	@Setter(STRETCH_KEY)
	public void setStretch(double value);

	@Getter(value = REPULSION_RANGE_SQ_KEY, defaultValue = "10000")
	@XMLAttribute
	public int getRepulsionRangeSq();

	@Setter(REPULSION_RANGE_SQ_KEY)
	public void setRepulsionRangeSq(int value);

	@Getter(value = FORCE_MULTIPLIER_KEY, defaultValue = "0.3333333333333333333")
	@XMLAttribute
	public double getForceMultiplier();

	@Setter(FORCE_MULTIPLIER_KEY)
	public void setForceMultiplier(double value);
}
