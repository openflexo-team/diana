/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing-editor, a component of the software infrastructure 
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

package org.openflexo.diana.drawingeditor;

import org.openflexo.gina.test.FIBInspectorTestCase;
import org.openflexo.rm.ResourceLocator;

/**
 * Test all inspectors used in FIBEditor
 * 
 * @author sylvain
 * 
 */
public class TestDrawingEditorInspectors extends FIBInspectorTestCase {

	public static void main(String[] args) {
		ResourceLocator.getResourceLocator();
		System.out.println("Dir=" + ResourceLocator.locateResource("Inspectors"));
		System.out.println(generateInspectorTestCaseClass(ResourceLocator.locateResource("Inspectors"), "Inspectors/"));
	}

	// Those inspectors are no more used

	/*@Test
	public void testGraphicalRepresentationInspector() {
		validateFIB("Inspectors/GraphicalRepresentation.inspector");
	}
	
	@Test
	public void testShapeGraphicalRepresentationInspector() {
		validateFIB("Inspectors/ShapeGraphicalRepresentation.inspector");
	}
	
	@Test
	public void testConnectorGraphicalRepresentationInspector() {
		validateFIB("Inspectors/ConnectorGraphicalRepresentation.inspector");
	}
	
	@Test
	public void testDrawingGraphicalRepresentationInspector() {
		validateFIB("Inspectors/DrawingGraphicalRepresentation.inspector");
	}*/

}
