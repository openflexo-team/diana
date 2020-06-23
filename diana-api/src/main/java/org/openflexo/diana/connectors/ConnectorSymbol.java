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
import org.openflexo.diana.DianaIconLibrary;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.DianaUtils.HasIcon;
import org.openflexo.diana.geom.DianaEllips;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolygon;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaUnionArea;

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
				return DianaIconLibrary.START_NONE_ICON;
			} else if (this == ARROW) {
				return DianaIconLibrary.START_ARROW_ICON;
			} else if (this == PLAIN_ARROW) {
				return DianaIconLibrary.START_PLAIN_ARROW_ICON;
			} else if (this == FILLED_ARROW) {
				return DianaIconLibrary.START_FILLED_ARROW_ICON;
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return DianaIconLibrary.START_PLAIN_DOUBLE_ARROW_ICON;
			} else if (this == FILLED_DOUBLE_ARROW) {
				return DianaIconLibrary.START_FILLED_DOUBLE_ARROW_ICON;
			} else if (this == PLAIN_CIRCLE) {
				return DianaIconLibrary.START_PLAIN_CIRCLE_ICON;
			} else if (this == FILLED_CIRCLE) {
				return DianaIconLibrary.START_FILLED_CIRCLE_ICON;
			} else if (this == PLAIN_SQUARE) {
				return DianaIconLibrary.START_PLAIN_SQUARE_ICON;
			} else if (this == FILLED_SQUARE) {
				return DianaIconLibrary.START_FILLED_SQUARE_ICON;
			} else if (this == PLAIN_DIAMOND || this == PLAIN_LONG_DIAMOND) {
				return DianaIconLibrary.START_PLAIN_DIAMOND_ICON;
			} else if (this == FILLED_DIAMOND) {
				return DianaIconLibrary.START_FILLED_DIAMOND_ICON;
			} else if (this == DEFAULT_FLOW) {
				return DianaIconLibrary.DEFAULT_FLOW_ICON;
			}
			return null;
		}

		@Override
		public DianaArea getSymbol() {
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
		public BackgroundStyle getBackgroundStyle(Color fgColor, Color bgColor, DianaModelFactory factory) {
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
		public ForegroundStyle getForegroundStyle(ForegroundStyle fgStyle, DianaModelFactory factory) {
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
				return DianaIconLibrary.END_NONE_ICON;
			} else if (this == ARROW) {
				return DianaIconLibrary.END_ARROW_ICON;
			} else if (this == PLAIN_ARROW) {
				return DianaIconLibrary.END_PLAIN_ARROW_ICON;
			} else if (this == FILLED_ARROW) {
				return DianaIconLibrary.END_FILLED_ARROW_ICON;
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return DianaIconLibrary.END_PLAIN_DOUBLE_ARROW_ICON;
			} else if (this == FILLED_DOUBLE_ARROW) {
				return DianaIconLibrary.END_FILLED_DOUBLE_ARROW_ICON;
			} else if (this == PLAIN_CIRCLE) {
				return DianaIconLibrary.END_PLAIN_CIRCLE_ICON;
			} else if (this == FILLED_CIRCLE) {
				return DianaIconLibrary.END_FILLED_CIRCLE_ICON;
			} else if (this == PLAIN_SQUARE) {
				return DianaIconLibrary.END_PLAIN_SQUARE_ICON;
			} else if (this == FILLED_SQUARE) {
				return DianaIconLibrary.END_FILLED_SQUARE_ICON;
			} else if (this == PLAIN_DIAMOND) {
				return DianaIconLibrary.END_PLAIN_DIAMOND_ICON;
			} else if (this == FILLED_DIAMOND) {
				return DianaIconLibrary.END_FILLED_DIAMOND_ICON;
			}
			return null;
		}

		@Override
		public DianaArea getSymbol() {
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
		public BackgroundStyle getBackgroundStyle(Color fgColor, Color bgColor, DianaModelFactory factory) {
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
		public ForegroundStyle getForegroundStyle(ForegroundStyle fgStyle, DianaModelFactory factory) {
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
				return DianaIconLibrary.MIDDLE_NONE_ICON;
			} else if (this == ARROW) {
				return DianaIconLibrary.MIDDLE_ARROW_ICON;
			} else if (this == PLAIN_ARROW) {
				return DianaIconLibrary.MIDDLE_PLAIN_ARROW_ICON;
			} else if (this == FILLED_ARROW) {
				return DianaIconLibrary.MIDDLE_FILLED_ARROW_ICON;
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return DianaIconLibrary.MIDDLE_PLAIN_DOUBLE_ARROW_ICON;
			} else if (this == FILLED_DOUBLE_ARROW) {
				return DianaIconLibrary.MIDDLE_FILLED_DOUBLE_ARROW_ICON;
			} else if (this == PLAIN_CIRCLE) {
				return DianaIconLibrary.MIDDLE_PLAIN_CIRCLE_ICON;
			} else if (this == FILLED_CIRCLE) {
				return DianaIconLibrary.MIDDLE_FILLED_CIRCLE_ICON;
			} else if (this == PLAIN_SQUARE) {
				return DianaIconLibrary.MIDDLE_PLAIN_SQUARE_ICON;
			} else if (this == FILLED_SQUARE) {
				return DianaIconLibrary.MIDDLE_FILLED_SQUARE_ICON;
			} else if (this == PLAIN_DIAMOND) {
				return DianaIconLibrary.MIDDLE_PLAIN_DIAMOND_ICON;
			} else if (this == FILLED_DIAMOND) {
				return DianaIconLibrary.MIDDLE_FILLED_DIAMOND_ICON;
			}
			return null;
		}

		@Override
		public DianaArea getSymbol() {
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
		public BackgroundStyle getBackgroundStyle(Color fgColor, Color bgColor, DianaModelFactory factory) {
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
		public ForegroundStyle getForegroundStyle(ForegroundStyle fgStyle, DianaModelFactory factory) {
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
		private static DianaArea EMPTY_AREA = new DianaEmptyArea();
		static DianaArea BASIC_ARROW = new DianaUnionArea(new DianaSegment(new DianaPoint(0, 0), new DianaPoint(1, 0.5)), new DianaSegment(
				new DianaPoint(1, 0.5), new DianaPoint(0, 1)));
		static DianaArea ARROW = new DianaPolygon(Filling.FILLED, new DianaPoint(0, 0.1), new DianaPoint(1, 0.5), new DianaPoint(0, 0.9));
		/*private static DianaArea CENTERED_ARROW = new DianaPolygon(
				Filling.FILLED,
				new DianaPoint(0.5,0.1),
				new DianaPoint(1.5,0.5),
				new DianaPoint(0.5,0.9));*/
		static DianaArea DOUBLE_ARROW = new DianaUnionArea(new DianaPolygon(Filling.FILLED, new DianaPoint(0, 0.2), new DianaPoint(0.5, 0.5),
				new DianaPoint(0, 0.8)), new DianaPolygon(Filling.FILLED, new DianaPoint(0.5, 0.2), new DianaPoint(1.0, 0.5),
				new DianaPoint(0.5, 0.8)));
		static DianaArea CIRCLE = new DianaEllips(0, 0, 1, 1, Filling.FILLED);
		static DianaArea SQUARE = new DianaRectangle(0, 0, 1, 1, Filling.FILLED);
		static DianaArea DIAMOND = new DianaPolygon(Filling.FILLED, new DianaPoint(0.5, 0), new DianaPoint(1, 0.5), new DianaPoint(0.5, 1),
				new DianaPoint(0, 0.5));
		static DianaArea LONG_DIAMOND = new DianaPolygon(Filling.FILLED, new DianaPoint(0.5, 0.2), new DianaPoint(1, 0.5), new DianaPoint(0.5, 0.8),
				new DianaPoint(0, 0.5));
		static DianaArea SLASH = new DianaSegment(new DianaPoint(0.0, 0), new DianaPoint(0.2, 1));
	}

	public DianaArea getSymbol();

	public BackgroundStyle getBackgroundStyle(Color fgColor, Color bgColor, DianaModelFactory factory);

	public ForegroundStyle getForegroundStyle(ForegroundStyle fgStyle, DianaModelFactory factory);

}
