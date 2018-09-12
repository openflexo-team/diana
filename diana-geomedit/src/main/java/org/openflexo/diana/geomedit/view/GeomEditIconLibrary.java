/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Openflexo-technology-adapters-ui, a component of the software infrastructure 
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

package org.openflexo.diana.geomedit.view;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.diana.geomedit.model.GeometricDiagram;
import org.openflexo.diana.geomedit.model.GeometricElement;
import org.openflexo.diana.geomedit.model.LineConstruction;
import org.openflexo.diana.geomedit.model.PointConstruction;
import org.openflexo.icon.ImageIconResource;
import org.openflexo.rm.ResourceLocator;

/**
 * Utility class containing all icons used in GeomEdit context
 * 
 * @author sylvain
 * 
 */
public class GeomEditIconLibrary {

	private static final Logger logger = Logger.getLogger(GeomEditIconLibrary.class.getPackage().getName());

	public static final ImageIconResource DIAGRAM_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/Model/GeometricDiagram.png"));

	public static final ImageIconResource POINT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Model/Point.png"));

	public static final ImageIconResource LINE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Model/Line.png"));

	public static final ImageIconResource UNKNOWN_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/Model/UnknownIcon.gif"));

	public static ImageIcon iconForObject(GeometricElement object) {
		if (object instanceof GeometricDiagram) {
			return DIAGRAM_ICON;
		}
		if (object instanceof PointConstruction) {
			return POINT_ICON;
		}
		if (object instanceof LineConstruction) {
			return LINE_ICON;
		}

		return UNKNOWN_ICON;
	}

}
