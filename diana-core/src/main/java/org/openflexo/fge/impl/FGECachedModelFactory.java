/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.fge.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.openflexo.fge.ColorBackgroundStyle;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.model.exceptions.ModelDefinitionException;

/**
 * A factory that implements cache to get objects used for rendering
 * 
 * @author sylvain
 * 
 */
public class FGECachedModelFactory extends FGEModelFactoryImpl {

	private final Map<Color, ForegroundStyle> foregroundStyles;
	private final Map<Color, ColorBackgroundStyle> colorBackgroundStyles;

	public FGECachedModelFactory() throws ModelDefinitionException {
		super();
		foregroundStyles = new HashMap<>();
		colorBackgroundStyles = new HashMap<>();
	}

	@Override
	public ForegroundStyle makeForegroundStyle(Color aColor) {
		ForegroundStyle returned = foregroundStyles.get(aColor);
		if (returned == null) {
			returned = super.makeForegroundStyle(aColor);
			foregroundStyles.put(aColor, returned);
		}
		return returned;
	}

	@Override
	public ColorBackgroundStyle makeColoredBackground(Color aColor) {
		ColorBackgroundStyle returned = colorBackgroundStyles.get(aColor);
		if (returned == null) {
			returned = super.makeColoredBackground(aColor);
			colorBackgroundStyles.put(aColor, returned);
		}
		return returned;
	}
}
