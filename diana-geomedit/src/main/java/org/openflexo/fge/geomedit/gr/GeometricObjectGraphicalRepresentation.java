/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geomedit, a component of the software infrastructure 
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

package org.openflexo.fge.geomedit.gr;

import java.awt.Color;

import org.openflexo.fge.TextureBackgroundStyle.TextureType;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.GeometricObject;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.ShowContextualMenuControl;
import org.openflexo.fge.impl.GeometricGraphicalRepresentationImpl;
import org.openflexo.xmlcode.XMLSerializable;

public class GeometricObjectGraphicalRepresentation<A extends FGEArea, G extends GeometricObject<A>> extends
		GeometricGraphicalRepresentationImpl<G> implements XMLSerializable {
	// Called for LOAD
	public GeometricObjectGraphicalRepresentation(GeomEditBuilder builder) {
		super();
		setDiagram(builder.drawing);
		initializeDeserialization();
	}

	public GeometricObjectGraphicalRepresentation(G object, GeometricDrawing aDrawing) {
		super();
		setDiagram(aDrawing);
		setDrawable(object);
		setBackground(aDrawing.getController().getFactory().makeTexturedBackground(TextureType.TEXTURE1, Color.RED, Color.WHITE));
		addToMouseClickControls(new ShowContextualMenuControl());
	}

	@Override
	public String getInspectorName() {
		return getDrawable().getInspectorName();
	}

	/*@Override
	public G getDrawable() {
		// TODO Auto-generated method stub
		return super.getDrawable();
	}*/

	@Override
	public A getGeometricObject() {
		if (getDrawable() != null) {
			return getDrawable().getGeometricObject();
		}
		return null;
	}

	@Override
	public String getText() {
		if (!getDisplayLabel()) {
			return null;
		}
		if (getDrawable() != null) {
			return getDrawable().name;
		}
		return super.getText();
	}

	private boolean displayLabel;

	public boolean getDisplayLabel() {
		return displayLabel;
	}

	public void setDisplayLabel(boolean aFlag) {
		displayLabel = aFlag;
	}

}
