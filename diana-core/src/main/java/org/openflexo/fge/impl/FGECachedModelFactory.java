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
		foregroundStyles = new HashMap<Color, ForegroundStyle>();
		colorBackgroundStyles = new HashMap<Color, ColorBackgroundStyle>();
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
