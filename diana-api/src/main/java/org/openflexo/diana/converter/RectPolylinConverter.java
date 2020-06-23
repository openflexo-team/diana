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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectPolylin;
import org.openflexo.pamela.StringConverterLibrary.Converter;
import org.openflexo.pamela.exceptions.InvalidDataException;
import org.openflexo.pamela.factory.ModelFactory;

public class RectPolylinConverter extends Converter<DianaRectPolylin> {
	public RectPolylinConverter(Class<? super DianaRectPolylin> aClass) {
		super(aClass);
	}

	@Override
	public DianaRectPolylin convertFromString(String value, ModelFactory factory) {
		try {
			List<DianaPoint> points = new ArrayList<>();
			StringTokenizer st = new StringTokenizer(value, ";");
			while (st.hasMoreTokens()) {
				String nextPoint = st.nextToken();
				try {
					points.add(DianaUtils.POINT_CONVERTER.convertFromString(nextPoint, factory));
				} catch (InvalidDataException e) {
					e.printStackTrace();
				}
			}
			return new DianaRectPolylin(points);
		} catch (NumberFormatException e) {
			// Warns about the exception
			System.err.println("Supplied value is not parsable as a DianaPoint:" + value);
			return null;
		}
	}

	@Override
	public String convertToString(DianaRectPolylin aPolylin) {
		if (aPolylin != null) {
			StringBuffer sb = new StringBuffer();
			boolean isFirst = true;
			for (DianaPoint pt : aPolylin.getPoints()) {
				if (!isFirst) {
					sb.append(";");
				}
				sb.append(DianaUtils.POINT_CONVERTER.convertToString(pt));
				isFirst = false;
			}
			return sb.toString();
		}
		return null;
	}
}
