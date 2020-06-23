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

import org.openflexo.diana.geom.area.DianaBand;
import org.openflexo.diana.geomedit.model.BandConstruction.BandConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.BandGraphicalRepresentation;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;

@ModelEntity(isAbstract = true)
@ImplementationClass(BandConstructionImpl.class)
@Imports({ @Import(BandWithTwoLinesConstruction.class) })
public interface BandConstruction extends GeometricConstruction<DianaBand> {

	public DianaBand getBand();

	public static abstract class BandConstructionImpl extends GeometricConstructionImpl<DianaBand> implements BandConstruction {

		@Override
		public String getBaseName() {
			return "Band";
		}

		@Override
		public final DianaBand getBand() {
			return getData();
		}

		@Override
		public BandGraphicalRepresentation makeNewConstructionGR(GeometricConstructionFactory factory) {
			BandGraphicalRepresentation returned = factory.newInstance(BandGraphicalRepresentation.class);
			return returned;
		}

		@Override
		protected abstract DianaBand computeData();

	}

}
