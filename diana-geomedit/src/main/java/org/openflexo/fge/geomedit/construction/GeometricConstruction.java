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

package org.openflexo.fge.geomedit.construction;

import java.util.Vector;

import org.openflexo.inspector.DefaultInspectableObject;
import org.openflexo.xmlcode.XMLSerializable;

public abstract class GeometricConstruction<O extends Object> extends DefaultInspectableObject implements XMLSerializable {

	private O computedData;

	protected abstract O computeData();

	public final O getData() {
		// System.out.println("getData() for "+this.getClass().getSimpleName());

		ensureUpToDate();

		if (computedData == null) {
			computedData = computeData();
		}

		return computedData;
	}

	private void ensureUpToDate() {
		ensureUpToDate(new Vector<GeometricConstruction>());
	}

	private void ensureUpToDate(Vector<GeometricConstruction> updatedConstructions) {
		// Recursively called ensureUpToDate() on dependancies
		if (getDepends() != null) {
			for (GeometricConstruction<?> c : getDepends()) {
				if (!c._altered.contains(this)) {
					c._altered.add(this);
				}
				c.ensureUpToDate(updatedConstructions);
			}
		}

		if (modified || updatedConstructions.size() > 0) {
			// System.out.println("Recompute data for "+this.getClass().getSimpleName());
			computedData = computeData();
			updatedConstructions.add(this);
			modified = false;
		}
	}

	public final void refresh() {
		// System.out.println("Refresh for "+this.getClass().getSimpleName());
		if (getDepends() != null) {
			for (GeometricConstruction c : getDepends()) {
				c.refresh();
			}
		}
		computedData = computeData();
	}

	@Override
	public abstract String toString();

	public abstract GeometricConstruction[] getDepends();

	private boolean modified = true;

	protected void setModified() {
		modified = true;
		for (GeometricConstruction c : _altered) {
			c.computedData = null;
			c.setModified();
		}
	}

	private Vector<GeometricConstruction> _altered = new Vector<GeometricConstruction>();

	@Override
	public String getInspectorName() {
		// Never inspected alone
		return null;
	}

}
