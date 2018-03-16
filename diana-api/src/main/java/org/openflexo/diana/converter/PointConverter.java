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

package org.openflexo.diana.converter;

import java.util.StringTokenizer;

import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;

public class PointConverter extends Converter<FGEPoint> {
	public PointConverter(Class<? super FGEPoint> aClass) {
		super(aClass);
	}

	@Override
	public FGEPoint convertFromString(String value, ModelFactory factory) {
		try {
			FGEPoint returned = new FGEPoint();
			StringTokenizer st = new StringTokenizer(value, ",");
			if (st.hasMoreTokens()) {
				returned.x = Double.parseDouble(st.nextToken());
			}
			if (st.hasMoreTokens()) {
				returned.y = Double.parseDouble(st.nextToken());
			}
			return returned;
		} catch (NumberFormatException e) {
			// Warns about the exception
			System.err.println("Supplied value is not parsable as a FGEPoint:" + value);
			return null;
		}
	}

	@Override
	public String convertToString(FGEPoint aPoint) {
		if (aPoint != null) {
			return aPoint.x + "," + aPoint.y;
		} else {
			return null;
		}
	}
}
