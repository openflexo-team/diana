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
import org.openflexo.diana.geom.DianaEllips;
import org.openflexo.diana.geomedit.model.EllipsReference.EllipsReferenceImpl;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(EllipsReferenceImpl.class)
@XMLElement
public interface EllipsReference extends EllipsConstruction<DianaEllips> {

	public EllipsConstruction<DianaEllips> getReference();

	public void setReference(EllipsConstruction<DianaEllips> reference);

	public static abstract class EllipsReferenceImpl extends EllipsConstructionImpl<DianaEllips> implements EllipsReference {

		private EllipsConstruction<DianaEllips> reference;

		@Override
		public EllipsConstruction<DianaEllips> getReference() {
			return reference;
		}

		@Override
		public void setReference(EllipsConstruction<DianaEllips> reference) {
			if ((reference == null && this.reference != null) || (reference != null && !reference.equals(this.reference))) {
				EllipsConstruction<DianaEllips> oldValue = this.reference;
				this.reference = reference;
				getPropertyChangeSupport().firePropertyChange("reference", oldValue, reference);
			}
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
		protected DianaEllips computeData() {
			if (getReference() != null) {
				return getReference().getData();
			}
			return null;
		}

		@Override
		public String toString() {
			return "EllipsReference[" + reference.toString() + "]";
		}

		@Override
		public GeometricConstruction[] getDepends() {
			GeometricConstruction[] returned = { reference };
			return returned;
		}

	}

}
