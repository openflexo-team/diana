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

package org.openflexo.fge.geomedit;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.GeometricConstruction;
import org.openflexo.fge.geomedit.gr.GeometricObjectGraphicalRepresentation;
import org.openflexo.inspector.DefaultInspectableObject;
import org.openflexo.xmlcode.XMLSerializable;

public abstract class GeometricObject<A extends FGEArea> extends DefaultInspectableObject implements XMLSerializable, Cloneable, TreeNode {
	public String name;
	public String description;
	private GeometricSet geometricSet;

	private GeometricConstruction<? extends A> construction;

	private static int index = 0;

	// Called for LOAD
	public GeometricObject(GeomEditBuilder builder) {
		super();
		initializeDeserialization();
	}

	// Called for NEW
	public GeometricObject(GeometricSet set, GeometricConstruction<A> aConstruction) {
		super();
		geometricSet = set;
		_setConstruction(aConstruction);
		index++;
		name = getClass().getSimpleName() + index;
	}

	public abstract GeometricObjectGraphicalRepresentation<A, ? extends GeometricObject<A>> getGraphicalRepresentation();

	// public abstract void setGraphicalRepresentation(GeometricObjectGraphicalRepresentation<O,? extends GeometricObject<O>> aGR);

	@Override
	public String toString() {
		return "GeometricObject[" + name + ":" + construction.getData() + "]";
	}

	private boolean isDeserializing = false;

	public void initializeDeserialization() {
		isDeserializing = true;
	}

	public void finalizeDeserialization() {
		isDeserializing = false;
	}

	public boolean isDeserializing() {
		return isDeserializing;
	}

	@Override
	public GeometricObject clone() {
		try {
			return (GeometricObject) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			// cannot happen since we are clonable
			return null;
		}
	}

	public A getGeometricObject() {
		return getConstruction().getData();
	}

	@Override
	public abstract String getInspectorName();

	public GeometricConstruction<? extends A> getConstruction() {
		return construction;
	}

	protected void _setConstruction(GeometricConstruction<? extends A> aConstruction) {
		construction = aConstruction;
	}

	@Override
	public Enumeration children() {
		return null;
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return null;
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	public int getIndex(TreeNode node) {
		return 0;
	}

	@Override
	public TreeNode getParent() {
		return geometricSet;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	private A _resultingGeometricObject;

	public void resetResultingGeometricObject() {
		_resultingGeometricObject = null;
		getConstruction().refresh();
		getResultingGeometricObject();
	}

	public A getResultingGeometricObject() {
		if (_resultingGeometricObject == null) {
			_resultingGeometricObject = getGeometricObject();
		}
		return _resultingGeometricObject;
	}

	public void setResultingGeometricObject(A object) {
		_resultingGeometricObject = object;
	}

}
