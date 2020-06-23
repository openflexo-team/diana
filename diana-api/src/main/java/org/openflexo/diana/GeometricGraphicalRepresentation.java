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

package org.openflexo.diana;

import org.openflexo.diana.BackgroundStyle.BackgroundStyleType;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;

/**
 * Represents a geometric object in a diagram<br>
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "GeometricGraphicalRepresentation")
public interface GeometricGraphicalRepresentation extends GraphicalRepresentation {

	// Property keys

	@PropertyIdentifier(type = ForegroundStyle.class)
	public static final String FOREGROUND_KEY = "foreground";
	@PropertyIdentifier(type = BackgroundStyle.class)
	public static final String BACKGROUND_KEY = "background";
	@PropertyIdentifier(type = DianaArea.class)
	public static final String GEOMETRIC_OBJECT_KEY = "geometricObject";

	public static final String BACKGROUND_STYLE_TYPE_KEY = "BackgroundStyleType";

	public static GRProperty<BackgroundStyle> BACKGROUND = GRProperty.getGRParameter(GeometricGraphicalRepresentation.class, BACKGROUND_KEY,
			BackgroundStyle.class);
	public static GRProperty<ForegroundStyle> FOREGROUND = GRProperty.getGRParameter(GeometricGraphicalRepresentation.class, FOREGROUND_KEY,
			ForegroundStyle.class);
	public static GRProperty<DianaArea> GEOMETRIC_OBJECT = GRProperty.getGRParameter(GeometricGraphicalRepresentation.class,
			GEOMETRIC_OBJECT_KEY, DianaArea.class);

	/*public static enum GeometricParameters implements GRProperty {
		foreground, background, geometricObject
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = FOREGROUND_KEY)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	public ForegroundStyle getForeground();

	@Setter(value = FOREGROUND_KEY)
	public void setForeground(ForegroundStyle aForeground);

	@Getter(value = BACKGROUND_KEY)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	public BackgroundStyle getBackground();

	@Setter(value = BACKGROUND_KEY)
	public void setBackground(BackgroundStyle aBackground);

	@Getter(value = GEOMETRIC_OBJECT_KEY, isStringConvertable = true)
	// @XMLElement
	public DianaArea getGeometricObject();

	@Setter(value = GEOMETRIC_OBJECT_KEY)
	public void setGeometricObject(DianaArea geometricObject);

	// *******************************************************************************
	// * Utils
	// *******************************************************************************

	public boolean getNoStroke();

	public void setNoStroke(boolean noStroke);

	public BackgroundStyleType getBackgroundType();

	public void setBackgroundType(BackgroundStyleType backgroundType);

	/*public Rectangle getBounds(double scale);

	public void paintGeometricObject(DianaGeometricGraphics graphics);

	public List<ControlPoint> getControlPoints();

	public List<ControlPoint> rebuildControlPoints();

	public void notifyGeometryChanged();*/

}
