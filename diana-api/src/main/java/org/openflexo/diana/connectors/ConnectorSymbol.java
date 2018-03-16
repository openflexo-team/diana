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

package org.openflexo.diana.connectors;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;

import org.openflexo.diana.BackgroundStyle;
import org.openflexo.diana.FGEIconLibrary;
import org.openflexo.diana.FGEModelFactory;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.FGEUtils.HasIcon;
import org.openflexo.diana.geom.FGEEllips;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.geom.FGEPolygon;
import org.openflexo.diana.geom.FGERectangle;
import org.openflexo.diana.geom.FGESegment;
import org.openflexo.diana.geom.FGEGeometricObject.Filling;
import org.openflexo.diana.geom.area.FGEArea;
import org.openflexo.diana.geom.area.FGEEmptyArea;
import org.openflexo.diana.geom.area.FGEUnionArea;

public interface ConnectorSymbol {

	public static enum StartSymbolType implements ConnectorSymbol, HasIcon {
		NONE,
		ARROW,
		PLAIN_ARROW,
		FILLED_ARROW,
		PLAIN_DOUBLE_ARROW,
		FILLED_DOUBLE_ARROW,
		PLAIN_CIRCLE,
		FILLED_CIRCLE,
		PLAIN_SQUARE,
		FILLED_SQUARE,
		PLAIN_DIAMOND,
		PLAIN_LONG_DIAMOND,
		FILLED_DIAMOND,
		DEFAULT_FLOW;

		@Override
		public ImageIcon getIcon() {
			if (this == NONE) {
				return FGEIconLibrary.START_NONE_ICON;
			} else if (this == ARROW) {
				return FGEIconLibrary.START_ARROW_ICON;
			} else if (this == PLAIN_ARROW) {
				return FGEIconLibrary.START_PLAIN_ARROW_ICON;
			} else if (this == FILLED_ARROW) {
				return FGEIconLibrary.START_FILLED_ARROW_ICON;
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return FGEIconLibrary.START_PLAIN_DOUBLE_ARROW_ICON;
			} else if (this == FILLED_DOUBLE_ARROW) {
				return FGEIconLibrary.START_FILLED_DOUBLE_ARROW_ICON;
			} else if (this == PLAIN_CIRCLE) {
				return FGEIconLibrary.START_PLAIN_CIRCLE_ICON;
			} else if (this == FILLED_CIRCLE) {
				return FGEIconLibrary.START_FILLED_CIRCLE_ICON;
			} else if (this == PLAIN_SQUARE) {
				return FGEIconLibrary.START_PLAIN_SQUARE_ICON;
			} else if (this == FILLED_SQUARE) {
				return FGEIconLibrary.START_FILLED_SQUARE_ICON;
			} else if (this == PLAIN_DIAMOND || this == PLAIN_LONG_DIAMOND) {
				return FGEIconLibrary.START_PLAIN_DIAMOND_ICON;
			} else if (this == FILLED_DIAMOND) {
				return FGEIconLibrary.START_FILLED_DIAMOND_ICON;
			} else if (this == DEFAULT_FLOW) {
				return FGEIconLibrary.DEFAULT_FLOW_ICON;
			}
			return null;
		}

		@Override
		public FGEArea getSymbol() {
			if (this == NONE) {
				return SymbolShapes.EMPTY_AREA;
			} else if (this == ARROW) {
				return SymbolShapes.BASIC_ARROW;
			} else if (this == PLAIN_ARROW) {
				return SymbolShapes.ARROW;
			} else if (this == FILLED_ARROW) {
				return SymbolShapes.ARROW;
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return SymbolShapes.DOUBLE_ARROW;
			} else if (this == FILLED_DOUBLE_ARROW) {
				return SymbolShapes.DOUBLE_ARROW;
			} else if (this == PLAIN_CIRCLE) {
				return SymbolShapes.CIRCLE;
			} else if (this == FILLED_CIRCLE) {
				return SymbolShapes.CIRCLE;
			} else if (this == PLAIN_SQUARE) {
				return SymbolShapes.SQUARE;
			} else if (this == FILLED_SQUARE) {
				return SymbolShapes.SQUARE;
			} else if (this == PLAIN_DIAMOND) {
				return SymbolShapes.DIAMOND;
			} else if (this == PLAIN_LONG_DIAMOND) {
				return SymbolShapes.LONG_DIAMOND;
			} else if (this == FILLED_DIAMOND) {
				return SymbolShapes.DIAMOND;
			} else if (this == DEFAULT_FLOW) {
				return SymbolShapes.SLASH;
			}
			return null;
		}

		@Override
		public BackgroundStyle getBackgroundStyle(Color fgColor, Color bgColor, FGEModelFactory factory) {
			if (this == NONE) {
				return factory.makeEmptyBackground();
			} else if (this == ARROW) {
				return factory.makeEmptyBackground();
			} else if (this == PLAIN_ARROW) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_ARROW) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_DOUBLE_ARROW) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_CIRCLE) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_CIRCLE) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_SQUARE) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_SQUARE) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_DIAMOND) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == PLAIN_LONG_DIAMOND) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_DIAMOND) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == DEFAULT_FLOW) {
				return factory.makeColoredBackground(fgColor);
			}
			return null;

		}

		@Override
		public ForegroundStyle getForegroundStyle(ForegroundStyle fgStyle, FGEModelFactory factory) {
			if (this == NONE) {
				return factory.makeNoneForegroundStyle();
			} else if (this == ARROW) {
				return fgStyle; // Use connector fg style
			} else {
				return factory.makeForegroundStyle(fgStyle.getColor());
			}
		}

	}

	public static enum EndSymbolType implements ConnectorSymbol, HasIcon {
		NONE,
		ARROW,
		PLAIN_ARROW,
		FILLED_ARROW,
		PLAIN_DOUBLE_ARROW,
		FILLED_DOUBLE_ARROW,
		PLAIN_CIRCLE,
		FILLED_CIRCLE,
		PLAIN_SQUARE,
		FILLED_SQUARE,
		PLAIN_DIAMOND,
		FILLED_DIAMOND;

		@Override
		public ImageIcon getIcon() {
			if (this == NONE) {
				return FGEIconLibrary.END_NONE_ICON;
			} else if (this == ARROW) {
				return FGEIconLibrary.END_ARROW_ICON;
			} else if (this == PLAIN_ARROW) {
				return FGEIconLibrary.END_PLAIN_ARROW_ICON;
			} else if (this == FILLED_ARROW) {
				return FGEIconLibrary.END_FILLED_ARROW_ICON;
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return FGEIconLibrary.END_PLAIN_DOUBLE_ARROW_ICON;
			} else if (this == FILLED_DOUBLE_ARROW) {
				return FGEIconLibrary.END_FILLED_DOUBLE_ARROW_ICON;
			} else if (this == PLAIN_CIRCLE) {
				return FGEIconLibrary.END_PLAIN_CIRCLE_ICON;
			} else if (this == FILLED_CIRCLE) {
				return FGEIconLibrary.END_FILLED_CIRCLE_ICON;
			} else if (this == PLAIN_SQUARE) {
				return FGEIconLibrary.END_PLAIN_SQUARE_ICON;
			} else if (this == FILLED_SQUARE) {
				return FGEIconLibrary.END_FILLED_SQUARE_ICON;
			} else if (this == PLAIN_DIAMOND) {
				return FGEIconLibrary.END_PLAIN_DIAMOND_ICON;
			} else if (this == FILLED_DIAMOND) {
				return FGEIconLibrary.END_FILLED_DIAMOND_ICON;
			}
			return null;
		}

		@Override
		public FGEArea getSymbol() {
			if (this == NONE) {
				return SymbolShapes.EMPTY_AREA;
			} else if (this == ARROW) {
				return SymbolShapes.BASIC_ARROW;
			} else if (this == PLAIN_ARROW) {
				return SymbolShapes.ARROW;
			} else if (this == FILLED_ARROW) {
				return SymbolShapes.ARROW;
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return SymbolShapes.DOUBLE_ARROW;
			} else if (this == FILLED_DOUBLE_ARROW) {
				return SymbolShapes.DOUBLE_ARROW;
			} else if (this == PLAIN_CIRCLE) {
				return SymbolShapes.CIRCLE;
			} else if (this == FILLED_CIRCLE) {
				return SymbolShapes.CIRCLE;
			} else if (this == PLAIN_SQUARE) {
				return SymbolShapes.SQUARE;
			} else if (this == FILLED_SQUARE) {
				return SymbolShapes.SQUARE;
			} else if (this == PLAIN_DIAMOND) {
				return SymbolShapes.DIAMOND;
			} else if (this == FILLED_DIAMOND) {
				return SymbolShapes.DIAMOND;
			}
			return null;
		}

		@Override
		public BackgroundStyle getBackgroundStyle(Color fgColor, Color bgColor, FGEModelFactory factory) {
			if (this == NONE) {
				return factory.makeEmptyBackground();
			} else if (this == ARROW) {
				return factory.makeEmptyBackground();
			} else if (this == PLAIN_ARROW) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_ARROW) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_DOUBLE_ARROW) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_CIRCLE) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_CIRCLE) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_SQUARE) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_SQUARE) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_DIAMOND) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_DIAMOND) {
				return factory.makeColoredBackground(fgColor);
			}
			return null;

		}

		@Override
		public ForegroundStyle getForegroundStyle(ForegroundStyle fgStyle, FGEModelFactory factory) {
			if (this == NONE) {
				return factory.makeNoneForegroundStyle();
			} else if (this == ARROW) {
				return fgStyle; // Use connector fg style
			} else {
				return factory.makeForegroundStyle(fgStyle.getColor());
			}
		}

	}

	public static enum MiddleSymbolType implements ConnectorSymbol, HasIcon {
		NONE,
		ARROW,
		PLAIN_ARROW,
		FILLED_ARROW,
		PLAIN_DOUBLE_ARROW,
		FILLED_DOUBLE_ARROW,
		PLAIN_CIRCLE,
		FILLED_CIRCLE,
		PLAIN_SQUARE,
		FILLED_SQUARE,
		PLAIN_DIAMOND,
		FILLED_DIAMOND;

		@Override
		public ImageIcon getIcon() {
			if (this == NONE) {
				return FGEIconLibrary.MIDDLE_NONE_ICON;
			} else if (this == ARROW) {
				return FGEIconLibrary.MIDDLE_ARROW_ICON;
			} else if (this == PLAIN_ARROW) {
				return FGEIconLibrary.MIDDLE_PLAIN_ARROW_ICON;
			} else if (this == FILLED_ARROW) {
				return FGEIconLibrary.MIDDLE_FILLED_ARROW_ICON;
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return FGEIconLibrary.MIDDLE_PLAIN_DOUBLE_ARROW_ICON;
			} else if (this == FILLED_DOUBLE_ARROW) {
				return FGEIconLibrary.MIDDLE_FILLED_DOUBLE_ARROW_ICON;
			} else if (this == PLAIN_CIRCLE) {
				return FGEIconLibrary.MIDDLE_PLAIN_CIRCLE_ICON;
			} else if (this == FILLED_CIRCLE) {
				return FGEIconLibrary.MIDDLE_FILLED_CIRCLE_ICON;
			} else if (this == PLAIN_SQUARE) {
				return FGEIconLibrary.MIDDLE_PLAIN_SQUARE_ICON;
			} else if (this == FILLED_SQUARE) {
				return FGEIconLibrary.MIDDLE_FILLED_SQUARE_ICON;
			} else if (this == PLAIN_DIAMOND) {
				return FGEIconLibrary.MIDDLE_PLAIN_DIAMOND_ICON;
			} else if (this == FILLED_DIAMOND) {
				return FGEIconLibrary.MIDDLE_FILLED_DIAMOND_ICON;
			}
			return null;
		}

		@Override
		public FGEArea getSymbol() {
			// Translate to put the middle of the symbol at required location
			AffineTransform translator = AffineTransform.getTranslateInstance(0.5, 0);
			if (this == NONE) {
				return SymbolShapes.EMPTY_AREA.transform(translator);
			} else if (this == ARROW) {
				return SymbolShapes.BASIC_ARROW.transform(translator);
			} else if (this == PLAIN_ARROW) {
				return SymbolShapes.ARROW.transform(translator);
			} else if (this == FILLED_ARROW) {
				return SymbolShapes.ARROW.transform(translator);
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return SymbolShapes.DOUBLE_ARROW.transform(translator);
			} else if (this == FILLED_DOUBLE_ARROW) {
				return SymbolShapes.DOUBLE_ARROW.transform(translator);
			} else if (this == PLAIN_CIRCLE) {
				return SymbolShapes.CIRCLE.transform(translator);
			} else if (this == FILLED_CIRCLE) {
				return SymbolShapes.CIRCLE.transform(translator);
			} else if (this == PLAIN_SQUARE) {
				return SymbolShapes.SQUARE.transform(translator);
			} else if (this == FILLED_SQUARE) {
				return SymbolShapes.SQUARE.transform(translator);
			} else if (this == PLAIN_DIAMOND) {
				return SymbolShapes.DIAMOND.transform(translator);
			} else if (this == FILLED_DIAMOND) {
				return SymbolShapes.DIAMOND.transform(translator);
			}
			return null;
		}

		@Override
		public BackgroundStyle getBackgroundStyle(Color fgColor, Color bgColor, FGEModelFactory factory) {
			if (this == NONE) {
				return factory.makeEmptyBackground();
			} else if (this == ARROW) {
				return factory.makeEmptyBackground();
			} else if (this == PLAIN_ARROW) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_ARROW) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_DOUBLE_ARROW) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_CIRCLE) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_CIRCLE) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_SQUARE) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_SQUARE) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_DIAMOND) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_DIAMOND) {
				return factory.makeColoredBackground(fgColor);
			}
			return null;

		}

		@Override
		public ForegroundStyle getForegroundStyle(ForegroundStyle fgStyle, FGEModelFactory factory) {
			if (this == NONE) {
				return factory.makeNoneForegroundStyle();
			} else if (this == ARROW) {
				return fgStyle; // Use connector fg style
			} else {
				return factory.makeForegroundStyle(fgStyle.getColor());
			}
		}

	}

	public static class SymbolShapes {
		private static FGEArea EMPTY_AREA = new FGEEmptyArea();
		static FGEArea BASIC_ARROW = new FGEUnionArea(new FGESegment(new FGEPoint(0, 0), new FGEPoint(1, 0.5)), new FGESegment(
				new FGEPoint(1, 0.5), new FGEPoint(0, 1)));
		static FGEArea ARROW = new FGEPolygon(Filling.FILLED, new FGEPoint(0, 0.1), new FGEPoint(1, 0.5), new FGEPoint(0, 0.9));
		/*private static FGEArea CENTERED_ARROW = new FGEPolygon(
				Filling.FILLED,
				new FGEPoint(0.5,0.1),
				new FGEPoint(1.5,0.5),
				new FGEPoint(0.5,0.9));*/
		static FGEArea DOUBLE_ARROW = new FGEUnionArea(new FGEPolygon(Filling.FILLED, new FGEPoint(0, 0.2), new FGEPoint(0.5, 0.5),
				new FGEPoint(0, 0.8)), new FGEPolygon(Filling.FILLED, new FGEPoint(0.5, 0.2), new FGEPoint(1.0, 0.5),
				new FGEPoint(0.5, 0.8)));
		static FGEArea CIRCLE = new FGEEllips(0, 0, 1, 1, Filling.FILLED);
		static FGEArea SQUARE = new FGERectangle(0, 0, 1, 1, Filling.FILLED);
		static FGEArea DIAMOND = new FGEPolygon(Filling.FILLED, new FGEPoint(0.5, 0), new FGEPoint(1, 0.5), new FGEPoint(0.5, 1),
				new FGEPoint(0, 0.5));
		static FGEArea LONG_DIAMOND = new FGEPolygon(Filling.FILLED, new FGEPoint(0.5, 0.2), new FGEPoint(1, 0.5), new FGEPoint(0.5, 0.8),
				new FGEPoint(0, 0.5));
		static FGEArea SLASH = new FGESegment(new FGEPoint(0.0, 0), new FGEPoint(0.2, 1));
	}

	public FGEArea getSymbol();

	public BackgroundStyle getBackgroundStyle(Color fgColor, Color bgColor, FGEModelFactory factory);

	public ForegroundStyle getForegroundStyle(ForegroundStyle fgStyle, FGEModelFactory factory);

}
