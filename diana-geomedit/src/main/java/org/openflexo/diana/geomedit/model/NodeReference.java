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

package org.openflexo.diana.geomedit.model;

import org.openflexo.diana.BackgroundStyle;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geomedit.model.NodeReference.NodeReferenceImpl;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(NodeReferenceImpl.class)
@XMLElement
public interface NodeReference extends NodeConstruction {

	@PropertyIdentifier(type = LineConstruction.class)
	public static final String REFERENCE_KEY = "reference";

	@Getter(value = REFERENCE_KEY)
	@XMLElement
	public NodeConstruction getReference();

	@Setter(value = REFERENCE_KEY)
	public void setReference(NodeConstruction reference);

	public static abstract class NodeReferenceImpl extends NodeConstructionImpl implements NodeReference {

		@Override
		public double getX() {
			return getReference().getX();
		}

		@Override
		public double getY() {
			return getReference().getY();
		}

		@Override
		public double getWidth() {
			return getReference().getWidth();
		}

		@Override
		public double getHeight() {
			return getReference().getHeight();
		}

		@Override
		public BackgroundStyle getBackground() {
			if (getReference() != null) {
				return getReference().getBackground();
			}
			return null;
		}

		@Override
		public ForegroundStyle getForeground() {
			if (getReference() != null) {
				return getReference().getForeground();
			}
			return null;
		}

		@Override
		protected DianaShape<?> computeData() {
			if (getReference() != null) {
				return getReference().getData();
			}
			return null;
		}

		@Override
		public String toString() {
			return "NodeReference[" + getReference().toString() + "]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { getReference() };
			return returned;
		}
	}
}
