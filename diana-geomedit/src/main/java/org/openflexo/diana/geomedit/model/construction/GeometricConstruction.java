/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-drawing-editor, a component of the software infrastructure 
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

package org.openflexo.diana.geomedit.model.construction;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.diana.geomedit.model.GeometricConstructionFactory;
import org.openflexo.diana.geomedit.model.GeometricDiagram;
import org.openflexo.diana.geomedit.model.GeometricElement;
import org.openflexo.diana.geomedit.model.construction.GeometricConstruction.GeometricConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.GeometricObjectGraphicalRepresentation;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity(isAbstract = true)
@ImplementationClass(GeometricConstructionImpl.class)
public interface GeometricConstruction<A extends FGEArea> extends GeometricElement {

	@PropertyIdentifier(type = GeometricObjectGraphicalRepresentation.class)
	public static final String GRAPHICAL_REPRESENTATION = "graphicalRepresentation";
	@PropertyIdentifier(type = GeometricDiagram.class)
	public static final String GEOMETRIC_DRAWING = "geometricDrawing";

	@Override
	@Getter(value = GEOMETRIC_DRAWING)
	public GeometricDiagram getGeometricDrawing();

	@Setter(value = GEOMETRIC_DRAWING)
	public void setGeometricDrawing(GeometricDiagram geometricDiagram);

	@Getter(value = GRAPHICAL_REPRESENTATION)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	public GeometricObjectGraphicalRepresentation<A> getGraphicalRepresentation();

	@Setter(value = GRAPHICAL_REPRESENTATION)
	public void setGraphicalRepresentation(GeometricObjectGraphicalRepresentation<A> graphicalRepresentation);

	public void refresh();

	public GeometricConstruction<?>[] getDepends();

	public GeometricObjectGraphicalRepresentation<A> makeNewConstructionGR(GeometricConstructionFactory geometricConstructionFactory);

	public A getData();

	public List<ControlPoint> getControlPoints(GeometricNode<?> node);

	public static abstract class GeometricConstructionImpl<A extends FGEArea> extends GeometricElementImpl
			implements GeometricConstruction<A> {

		private A computedData;
		private List<GeometricConstruction<?>> _altered = new ArrayList<GeometricConstruction<?>>();

		protected abstract A computeData();

		@Override
		public final A getData() {
			// System.out.println("getData() for "+this.getClass().getSimpleName());

			ensureUpToDate();

			if (computedData == null) {
				computedData = computeData();
			}

			return computedData;
		}

		private void ensureUpToDate() {
			ensureUpToDate(new ArrayList<GeometricConstruction<?>>());
		}

		private void ensureUpToDate(List<GeometricConstruction<?>> updatedConstructions) {
			// Recursively called ensureUpToDate() on dependancies
			if (getDepends() != null) {
				for (GeometricConstruction<?> d : getDepends()) {
					GeometricConstructionImpl<?> c = (GeometricConstructionImpl<?>) d;
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

		@Override
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

		@Override
		public abstract GeometricConstruction<?>[] getDepends();

		private boolean modified = false;

		@Override
		public void setModified(boolean modified) {
			this.modified = modified;
			performSuperSetModified(modified);
			for (GeometricConstruction<?> c : _altered) {
				((GeometricConstructionImpl<?>) c).computedData = null;
				c.setModified(modified);
			}
		}

		@Override
		public List<ControlPoint> getControlPoints(GeometricNode<?> node) {
			return node.getControlPoints();
		}
	}

}
