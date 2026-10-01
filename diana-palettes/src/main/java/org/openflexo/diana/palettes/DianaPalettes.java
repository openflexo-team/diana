/**
 *
 * Copyright (c) 2026, Openflexo
 *
 * This file is part of Diana-palettes, a component of the software infrastructure
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

package org.openflexo.diana.palettes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The palettes shipped with DIANA, in the order tools should show them: Basic (from diana-core) and the palettes of this module.<br>
 * Each palette is a directory of <code>.pel</code> files, located on the classpath at {@link PaletteDefinition#getPath()}
 * 
 * @author sylvain
 */
public class DianaPalettes {

	/**
	 * A palette: the directory of its elements, and its title
	 */
	public static class PaletteDefinition {
		private final String directory;
		private final String title;

		PaletteDefinition(String directory, String title) {
			this.directory = directory;
			this.title = title;
		}

		/**
		 * Name of the directory holding the elements, under <code>Palettes/</code>
		 */
		public String getDirectory() {
			return directory;
		}

		public String getTitle() {
			return title;
		}

		/**
		 * Classpath path of the directory holding the elements
		 */
		public String getPath() {
			return "Palettes/" + directory;
		}
	}

	public static final PaletteDefinition BASIC = new PaletteDefinition("Basic", "Basic");

	public static final List<PaletteDefinition> PALETTES = Collections.unmodifiableList(Arrays.asList(BASIC,
			new PaletteDefinition("Rectangles", "Rectangles"), new PaletteDefinition("BasicShapes", "Basic shapes"),
			new PaletteDefinition("Arrows", "Arrows"), new PaletteDefinition("Flowchart", "Flowchart"),
			new PaletteDefinition("StarsAndBanners", "Stars and banners"), new PaletteDefinition("Equations", "Equations"),
			new PaletteDefinition("Shapes3D", "3D shapes"), new PaletteDefinition("Emoji", "Emoji")));

	private DianaPalettes() {
	}
}
