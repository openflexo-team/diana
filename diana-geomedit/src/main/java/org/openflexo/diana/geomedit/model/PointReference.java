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
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geomedit.model.PointReference.PointReferenceImpl;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(PointReferenceImpl.class)
@XMLElement
public interface PointReference extends PointConstruction {

	@PropertyIdentifier(type = PointConstruction.class)
	public static final String REFERENCE_KEY = "reference";

	@Getter(value = REFERENCE_KEY)
	@XMLElement(context = "Ref_")
	public PointConstruction getReference();

	@Setter(value = REFERENCE_KEY)
	public void setReference(PointConstruction reference);

	public static abstract class PointReferenceImpl extends PointConstructionImpl implements PointReference {

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
		protected DianaPoint computeData() {
			if (getReference() != null) {
				return getReference().getData();
			}
			return null;
		}

		@Override
		public String toString() {
			return "PointReference[" + getReference().toString() + "]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { getReference() };
			return returned;
		}
	}
}
