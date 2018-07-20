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

package org.openflexo.diana.geomedit.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.diana.geomedit.model.GeometricConstruction.GeometricConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.GeometricObjectGraphicalRepresentation;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.notifications.GeometryModified;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity(isAbstract = true)
@ImplementationClass(GeometricConstructionImpl.class)
@Imports({ @Import(LineConstruction.class), @Import(PointConstruction.class), @Import(EllipsConstruction.class),
		@Import(RectangleConstruction.class), @Import(RoundRectangleConstruction.class), @Import(SegmentConstruction.class),
		@Import(HalfLineConstruction.class), @Import(CubicCurveConstruction.class), @Import(QuadCurveConstruction.class),
		@Import(ComplexCurveConstruction.class), @Import(BandConstruction.class), @Import(HalfBandConstruction.class),
		@Import(HalfPlaneConstruction.class), @Import(IntersectionConstruction.class), @Import(ObjectReference.class),
		@Import(PolygonConstruction.class), @Import(QuarterPlaneConstruction.class) })
public interface GeometricConstruction<A extends FGEArea> extends GeometricElement {

	@PropertyIdentifier(type = GeometricObjectGraphicalRepresentation.class)
	public static final String GRAPHICAL_REPRESENTATION = "graphicalRepresentation";
	@PropertyIdentifier(type = GeometricDiagram.class)
	public static final String GEOMETRIC_DIAGRAM = "geometricDiagram";

	@Override
	@Getter(value = GEOMETRIC_DIAGRAM)
	public GeometricDiagram getGeometricDiagram();

	@Setter(value = GEOMETRIC_DIAGRAM)
	public void setGeometricDiagram(GeometricDiagram geometricDiagram);

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

	public List<ControlPoint> getControlPoints();

	public void setControlPoints(List<ControlPoint> controlPoints);

	public void notifyGeometryChanged();

	public static abstract class GeometricConstructionImpl<A extends FGEArea> extends GeometricElementImpl
			implements GeometricConstruction<A>, PropertyChangeListener {

		private A computedData;
		private List<GeometricConstruction<?>> listened = new ArrayList<GeometricConstruction<?>>();

		protected abstract A computeData();

		@Override
		public A getData() {
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
					// GeometricConstructionImpl<?> c = (GeometricConstructionImpl<?>) d;
					if (!listened.contains(d)) {
						listened.add(d);
						d.getPropertyChangeSupport().addPropertyChangeListener(this);
						System.out.println("Je suis " + this + " et j'ecoute " + d);
					}

					/*if (!c._altered.contains(this)) {
						c._altered.add(this);
					}*/
					((GeometricConstructionImpl<?>) d).ensureUpToDate(updatedConstructions);
				}
			}

			/*if (modified || updatedConstructions.size() > 0) {
				// System.out.println("Recompute data for "+this.getClass().getSimpleName());
				computedData = computeData();
				updatedConstructions.add(this);
				modified = false;
			}*/
		}

		@Override
		public boolean delete(Object... arg0) {
			for (GeometricConstruction<?> c : listened) {
				c.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			listened.clear();
			return true;
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

		// private boolean modified = false;

		/*@Override
		public void setModified(boolean modified) {
			this.modified = modified;
			performSuperSetModified(modified);
			for (GeometricConstruction<?> c : _altered) {
				((GeometricConstructionImpl<?>) c).computedData = null;
				c.setModified(modified);
			}
		}*/

		@Override
		public String getName() {
			String returned = (String) performSuperGetter(NAME_KEY);
			if (returned == null) {
				return getDefaultName();
			}
			return returned;
		}

		protected String getDefaultName() {
			return "Construction" + getIndex();
		}

		private static int totalIndex = 0;
		private int index = -1;

		public int getIndex() {
			if (index == -1) {
				index = totalIndex;
				totalIndex++;
			}
			return index;
		}

		private List<ControlPoint> controlPoints;

		@Override
		public List<ControlPoint> getControlPoints() {
			return controlPoints;
		}

		@Override
		public void setControlPoints(List<ControlPoint> controlPoints) {
			this.controlPoints = controlPoints;
		}

		@Override
		public void notifyGeometryChanged() {
			System.out.println("notifyGeometryChanged called in " + this);
			System.out.println("Qui m'ecoute ? " + listened);
			getPropertyChangeSupport().firePropertyChange(GeometryModified.EVENT_NAME, false, true);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			System.out.println("Tiens, je suis " + this);
			System.out.println("La geometrie de " + evt.getSource() + " a change");
			refresh();
			notifyGeometryChanged();
		}
	}

}