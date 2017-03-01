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

package org.openflexo.fge.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.factory.ModelFactory;

public class RectPolylinConverter extends Converter<FGERectPolylin> {
	public RectPolylinConverter(Class<? super FGERectPolylin> aClass) {
		super(aClass);
	}

	@Override
	public FGERectPolylin convertFromString(String value, ModelFactory factory) {
		try {
			List<FGEPoint> points = new ArrayList<>();
			StringTokenizer st = new StringTokenizer(value, ";");
			while (st.hasMoreTokens()) {
				String nextPoint = st.nextToken();
				try {
					points.add(FGEUtils.POINT_CONVERTER.convertFromString(nextPoint, factory));
				} catch (InvalidDataException e) {
					e.printStackTrace();
				}
			}
			return new FGERectPolylin(points);
		} catch (NumberFormatException e) {
			// Warns about the exception
			System.err.println("Supplied value is not parsable as a FGEPoint:" + value);
			return null;
		}
	}

	@Override
	public String convertToString(FGERectPolylin aPolylin) {
		if (aPolylin != null) {
			StringBuffer sb = new StringBuffer();
			boolean isFirst = true;
			for (FGEPoint pt : aPolylin.getPoints()) {
				if (!isFirst) {
					sb.append(";");
				}
				sb.append(FGEUtils.POINT_CONVERTER.convertToString(pt));
				isFirst = false;
			}
			return sb.toString();
		}
		else {
			return null;
		}
	}
}
