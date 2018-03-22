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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.Toolkit;

public interface DianaConstants {

	// GPO: this flag cannot be turned to false so far because there are many issues with the invalidation of the Graphical hierarchy
	// Mainly: when initiating a view or when invalidating the gr hierarchy, all gr's are rebuilt which forces them to apply their
	// constraints
	// to avoid this, we should rework code to cache GR's (avoir rebuilding them) and don't apply constraints on creation of GR's, only on
	// creation of model objects
	// public static final boolean APPLY_CONSTRAINTS_IMMEDIATELY = true;
	public static final boolean DEBUG = false;
	public static final int CONTROL_POINT_SIZE = 2;
	public static final int POINT_SIZE = 4;

	public static final Cursor MOVE_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(DianaIconLibrary.CURSOR_MOVE_ICON.getImage(),
			new Point(16, 16), "CustomMove");
	public static final Integer INITIAL_LAYER = 32;
	public static final Integer DEFAULT_CONNECTOR_LAYER = 64;
	public static final Integer DEFAULT_SHAPE_LAYER = 1;
	public static final Integer DEFAULT_OBJECT_LAYER = 10;

	public static final int DEFAULT_ROUNDED_RECTANGLE_ARC_SIZE = 30; // pixels

	public static final int DEFAULT_RECT_POLYLIN_PIXEL_OVERLAP = 20; // overlap expressed in pixels relative to 1.0 scale
	public static final int DEFAULT_ROUNDED_RECT_POLYLIN_ARC_SIZE = 5; // pixels

	public static final int DEFAULT_BORDER_SIZE = 2; // pixels

	public static final double SELECTION_DISTANCE = 5.0; // < 5 pixels
	public static final double DND_DISTANCE = 20.0; // < 20 pixels

	public static final int DEFAULT_SHADOW_DARKNESS = 150;
	public static final int DEFAULT_SHADOW_DEEP = 2;
	public static final int DEFAULT_SHADOW_BLUR = 4;

	public static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
	public static final Font DEFAULT_TEXT_FONT = new Font("Lucida Sans", Font.PLAIN, 11);

	public static final Font DEFAULT_SMALL_TEXT_FONT = new Font("Lucida Sans", Font.PLAIN, 9);

	public static final Color DEFAULT_BACKGROUND_COLOR = new Color(254, 247, 217);

	public static Stroke DASHED = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[] { 3.0f, 3.0f }, 1);

	public static final double DEFAULT_DRAWING_WIDTH = 1000;
	public static final double DEFAULT_DRAWING_HEIGHT = 1000;
	public static final Color DEFAULT_DRAWING_BACKGROUND_COLOR = Color.WHITE;

	public static final Image DEFAULT_IMAGE = null;

}
