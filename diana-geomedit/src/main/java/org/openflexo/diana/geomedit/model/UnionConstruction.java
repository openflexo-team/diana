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

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaUnionArea;
import org.openflexo.diana.geomedit.model.UnionConstruction.UnionConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.ComputedAreaGraphicalRepresentation;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.annotations.Getter.Cardinality;

@ModelEntity
@ImplementationClass(UnionConstructionImpl.class)
@XMLElement
public interface UnionConstruction extends GeometricConstruction<DianaArea> {

	@PropertyIdentifier(type = ObjectReference.class, cardinality = Cardinality.LIST)
	public static final String OBJECT_CONSTRUCTIONS_KEY = "objectConstructions";
	@PropertyIdentifier(type = Boolean.class)
	public static final String MERGE_CONTENTS_KEY = "mergeContents";

	@Getter(value = OBJECT_CONSTRUCTIONS_KEY, cardinality = Cardinality.LIST)
	@XMLElement
	public List<ObjectReference<? extends DianaArea>> getObjectConstructions();

	@Adder(OBJECT_CONSTRUCTIONS_KEY)
	public void addToObjectConstructions(ObjectReference<? extends DianaArea> objectReference);

	@Remover(OBJECT_CONSTRUCTIONS_KEY)
	public void removeFromObjectConstructions(ObjectReference<? extends DianaArea> objectReference);

	@Getter(value = MERGE_CONTENTS_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getMergeContents();

	@Setter(MERGE_CONTENTS_KEY)
	public void setMergeContents(boolean mergeContents);

	public static abstract class UnionConstructionImpl extends GeometricConstructionImpl<DianaArea> implements UnionConstruction {

		private static final Logger logger = FlexoLogger.getLogger(UnionConstruction.class.getPackage().getName());

		@Override
		public String getBaseName() {
			return "Union";
		}

		@Override
		public ComputedAreaGraphicalRepresentation makeNewConstructionGR(GeometricConstructionFactory factory) {
			ComputedAreaGraphicalRepresentation returned = factory.newInstance(ComputedAreaGraphicalRepresentation.class);
			return returned;
		}

		@Override
		protected DianaArea computeData() {
			if (getObjectConstructions() != null) {
				DianaArea[] objects = new DianaArea[getObjectConstructions().size()];
				for (int i = 0; i < getObjectConstructions().size(); i++) {
					objects[i] = getObjectConstructions().get(i).getData();
				}
				DianaArea returned = DianaUnionArea.makeUnion(Arrays.asList(objects), getMergeContents());
				if (returned == null) {
					new Exception("Unexpected union").printStackTrace();
				}
				return returned;
			}
			return null;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("UnionConstruction[\n");
			for (GeometricConstruction<?> c : getObjectConstructions()) {
				sb.append("> " + c.toString() + "\n");
			}
			sb.append("-> " + getData() + "\n");
			sb.append("]");
			return sb.toString();
		}

		@Override
		public GeometricConstruction<?>[] getDepends() {
			return getObjectConstructions().toArray(new GeometricConstruction[getObjectConstructions().size()]);
		}

		@Override
		public void setMergeContents(boolean mergeContents) {
			performSuperSetter(MERGE_CONTENTS_KEY, mergeContents);
			refresh();
			notifyGeometryChanged();
		}
	}

}
